package com.deleidos.dp.calculations;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.apache.lucene.search.spell.JaroWinklerDistance;

import com.deleidos.dp.beans.DataSample;
import com.deleidos.dp.beans.Interpretation;
import com.deleidos.dp.beans.NumberDetail;
import com.deleidos.dp.beans.Profile;
import com.deleidos.dp.beans.Schema;
import com.deleidos.dp.beans.StringDetail;
import com.deleidos.dp.exceptions.MainTypeRuntimeException;
import com.deleidos.dp.profiler.DefaultProfilerRecord;
import com.deleidos.hd.enums.MainType;

/**
 * Matching algorithm logic and methods.
 * @author leegc
 *
 */
public class MatchingAlgorithm {
	private static final Logger logger = Logger.getLogger(MatchingAlgorithm.class);
	public static final MathContext SIMILARITY_CONTEXT = MathContext.DECIMAL32;

	private final Schema schema;
	private final Map<String, DataSample> dataSamples;
	private final Map<String, Set<String>> structureContainmentMapping;

	public MatchingAlgorithm(Schema schema, List<DataSample> dataSampleList) {
		this.schema = schema;
		this.dataSamples = dataSampleList.stream().collect(
				Collectors.toMap(
						sample->sample.getDsGuid(), 
						sample->sample));
		this.structureContainmentMapping = buildStructureContainmentMapping(schema, dataSamples);
	}

	private String mergedKey(String guid1, String guid2) {
		return guid1.compareTo(guid2) >= 0 ? guid1 + guid2 : guid2 + 1;
	}

	private Map<String, Set<String>> buildStructureContainmentMapping(
			Schema schema, Map<String, DataSample> samples) {
		for (String schemaField : schema.getsProfile().keySet()) {
			for (String sampleGuid : samples.keySet()) {

			}
		}
		return null;
	}

	private double matchWithSchema(String dataSampleGuid, String fieldKey) {
		return 0;
	}

	private double matchWithSample(String baseGuid, String baseFieldKey, String sampleGuid, String fieldKey) {
		return 0;
	}

	public static double jaroWinklerComparison(String s1, String s2) {
		String lowerCaseS1 = s1.toLowerCase();
		String lowerCaseS2 = s2.toLowerCase();
		JaroWinklerDistance d = new JaroWinklerDistance();
		return d.getDistance(lowerCaseS1, lowerCaseS2);
	}

	private static double cosineSimilarity(Profile profile1, Profile profile2) {
		final int MIN = 0;
		final int MAX = 1;
		final int AVG = 2;
		final int STD = 3;
		final int DST = 4;
		final int VECTOR_SIZE = 5;
		if(profile1.getDetail().getDetailType().equals(profile2.getDetail().getDetailType()) 
				&& profile1.getMainType().equals(profile2.getMainType())) {

			String mainType = profile1.getMainType();
			// TODO need if above high cardinality threshold 
			double cosSim = 0;
			double[] v1 = new double[5];
			double[] v2 = new double[5];
			double numerator = 0;
			double denominator = 0;
			if(mainType.equals(MainType.NUMBER.toString())) {
				NumberDetail nm1 = (NumberDetail)profile1.getDetail();
				NumberDetail nm2 = (NumberDetail)profile2.getDetail();
				v1[MIN] = nm1.getMin().doubleValue();
				v2[MIN] = nm2.getMin().doubleValue();
				v1[MAX] = nm1.getMax().doubleValue();
				v2[MAX] = nm2.getMax().doubleValue();
				v1[AVG] = nm1.getAverage().doubleValue();
				v2[AVG] = nm2.getAverage().doubleValue();
				v1[STD] = nm1.getStdDev();
				v2[STD] = nm2.getStdDev();
				v1[DST] = stripNumDistinctValuesChars((nm1.getNumDistinctValues()));
				v2[DST] = stripNumDistinctValuesChars(nm2.getNumDistinctValues());
			} else if(mainType.equals(MainType.STRING.toString())) {
				StringDetail sm1 = (StringDetail)profile1.getDetail();
				StringDetail sm2 = (StringDetail)profile2.getDetail();
				v1[MIN] = sm1.getMinLength();
				v2[MIN] = sm2.getMinLength();
				v1[MAX] = sm1.getMaxLength();
				v2[MAX] = sm2.getMaxLength();
				v1[AVG] = sm1.getAverageLength();
				v2[AVG] = sm2.getAverageLength();
				v1[STD] = sm1.getStdDevLength();
				v2[STD] = sm2.getStdDevLength();
				v1[DST] = stripNumDistinctValuesChars(sm1.getNumDistinctValues());
				v2[DST] = stripNumDistinctValuesChars(sm2.getNumDistinctValues());
			} else {
				logger.error("Got binary for matching.");
				return 0.0;
			}
			for(int i = 0; i < VECTOR_SIZE; i++) {
				numerator += (v1[i] * v2[i]);
			}
			double sumOfSquaresA = 0;
			double sumOfSquaresB = 0;
			for(int i = 0; i < VECTOR_SIZE; i++) {
				sumOfSquaresA += Math.pow(v1[i], 2);
				sumOfSquaresB += Math.pow(v2[i], 2);
			}
			denominator = Math.sqrt(sumOfSquaresA) * Math.sqrt(sumOfSquaresB);
			cosSim = numerator/denominator;
			return cosSim;
		} else {
			return 0.0;
		}
	}

	@Deprecated
	public static double originalMatch(String name1, Profile profile1, String name2, Profile profile2, double nameWeight) {
		return similarityAlgorithm1(name1, profile1, name2, profile2, nameWeight);
	}

	@Deprecated
	private static double similarityAlgorithm1(String name1, Profile profile1, String name2, Profile profile2, double nameWeight) {
		final double statisticsWeight = 1 - nameWeight;
		double nameSimilarity = jaroWinklerComparison(name1, name2);
		double cosineSimilarity = cosineSimilarity(profile1, profile2);
		return (nameWeight * nameSimilarity) + (statisticsWeight * cosineSimilarity);
	}

	private static double similarityAlgorithm2(String name1, Profile profile1, String name2, Profile profile2,
			double disimilarityRate, double similarityArc, double nameWeight, double sameInterpretationWeight) {
		final double statisticsWeight = 1 - nameWeight;
		double nameSimilarity = jaroWinklerComparison(name1, name2);
		double newSimilarity = newSimilarity(name1, profile1, name2, profile2, disimilarityRate, similarityArc);
		double normalMatch = (nameWeight * nameSimilarity) + (statisticsWeight * newSimilarity);
		if (sameInterpretationWeight < 0) {
			return normalMatch;
		} else {
			if (profile1.getInterpretations().getAvailableOptions().size() < 1) {
				throw new MainTypeRuntimeException("No interpretations in interpretation list for " + name1);
			} else if (profile2.getInterpretations().getAvailableOptions().size() < 1) {
				throw new MainTypeRuntimeException("No interpretations in interpretation list for " + name2);
			}
			Interpretation profile1Interpretation = profile1.getInterpretations().getSelectedOption();
			Interpretation profile2Interpretation = profile2.getInterpretations().getSelectedOption();
			if (!Interpretation.isUnknown(profile1Interpretation)) {
				// if the interpretations match and are not unknown
				// make the minimum match 80
				if (profile1Interpretation.getiName()
						.equals(profile2Interpretation.getiName())) {
					return sameInterpretationWeight + ((1 - sameInterpretationWeight) * normalMatch);
				} 
			} 
			return normalMatch;
		}
	}

	private static double newSimilarity(String name1, Profile profile1, String name2, Profile profile2,
			double disimilarityRate, double similarityArc) {
		double newSimilarityCalc = 0;
		final int MIN = 0;
		final int MAX = 1;
		final int AVG = 2;
		final int STD = 3;
		final int DST = 4;
		if (!profile1.getMainType().equals(profile2.getMainType())) {
			return 0;
		} else {
			MainType mainType = profile1.getMainTypeClass();
			if (!mainType.equals(MainType.STRING)) {
				// for Main Types other than string, don't allow different detail types to match
				if (!profile1.getDetail().getDetailType().equals(profile2.getDetail().getDetailType())) {
					return 0.0;
				}
			}
			Vector<BigDecimal> v1 = new Vector<BigDecimal>();
			Vector<BigDecimal> v2 = new Vector<BigDecimal>();
			for(int i = 0; i < 5; i++) {
				v1.add(new BigDecimal(0, SIMILARITY_CONTEXT));
				v2.add(new BigDecimal(0, SIMILARITY_CONTEXT));
			}
			switch(mainType) {
			case NUMBER: {
				NumberDetail numberDetail1 = Profile.getNumberDetail(profile1);
				NumberDetail numberDetail2 = Profile.getNumberDetail(profile2);
				v1.set(MIN, numberDetail1.getMin());
				v1.set(MAX, numberDetail1.getMax());
				v1.set(AVG, numberDetail1.getAverage());
				v1.set(STD, BigDecimal.valueOf(numberDetail1.getStdDev()));
				v1.set(DST, new BigDecimal(stripNumDistinctValuesChars(numberDetail1.getNumDistinctValues())));

				v2.set(MIN, numberDetail2.getMin());
				v2.set(MAX, numberDetail2.getMax());
				v2.set(AVG, numberDetail2.getAverage());
				v2.set(STD, BigDecimal.valueOf(numberDetail2.getStdDev()));
				v2.set(DST, new BigDecimal(stripNumDistinctValuesChars(numberDetail2.getNumDistinctValues())));
				break;
			}
			case STRING: {
				StringDetail stringDetail1 = Profile.getStringDetail(profile1);
				StringDetail stringDetail2 = Profile.getStringDetail(profile2);

				v1.set(MIN, BigDecimal.valueOf(stringDetail1.getMinLength()));
				v1.set(MAX, BigDecimal.valueOf(stringDetail1.getMaxLength()));
				v1.set(AVG, BigDecimal.valueOf(stringDetail1.getAverageLength()));
				v1.set(STD, BigDecimal.valueOf(stringDetail1.getStdDevLength()));
				v1.set(DST, new BigDecimal(stripNumDistinctValuesChars(stringDetail1.getNumDistinctValues())));

				v2.set(MIN, BigDecimal.valueOf(stringDetail2.getMinLength()));
				v2.set(MAX, BigDecimal.valueOf(stringDetail2.getMaxLength()));
				v2.set(AVG, BigDecimal.valueOf(stringDetail2.getAverageLength()));
				v2.set(STD, BigDecimal.valueOf(stringDetail2.getStdDevLength()));
				v2.set(DST, new BigDecimal(stripNumDistinctValuesChars(stringDetail2.getNumDistinctValues())));

				break;
			}
			case BINARY: {
				logger.error("Got binary for similarity calculation.  Defaulting to zero.");
				return 0;
			}
			default: {
				logger.error("Not found as number, string, or binary.");
				return 0;
			}
			}

			BigDecimal normalizedMax = v1.get(MIN);
			BigDecimal normalizedMin = v1.get(MIN);

			for(BigDecimal value : v1) {
				normalizedMax = value.compareTo(normalizedMax) > 0 ? value : normalizedMax;
				normalizedMin = value.compareTo(normalizedMin) < 0 ? value : normalizedMin;
			}

			for(BigDecimal value : v2) {
				normalizedMax = value.compareTo(normalizedMax) > 0 ? value : normalizedMax;
				normalizedMin = value.compareTo(normalizedMin) < 0 ? value : normalizedMin;
			}

			normalizedMax = normalizedMax.divide(BigDecimal.valueOf(disimilarityRate), SIMILARITY_CONTEXT);
			normalizedMin = normalizedMin.divide(BigDecimal.valueOf(disimilarityRate), SIMILARITY_CONTEXT);

			for(int i = 0; i < v1.size(); i++) {
				BigDecimal normalizingDenominator = normalizedMax.subtract(normalizedMin).add(BigDecimal.ONE);

				BigDecimal updatedElement1;
				updatedElement1 = v1.get(i).subtract(normalizedMin);
				updatedElement1 = updatedElement1.divide(normalizingDenominator, SIMILARITY_CONTEXT);
				v1.set(i, updatedElement1);

				BigDecimal updatedElement2;
				updatedElement2 = v2.get(i).subtract(normalizedMin);
				updatedElement2 = updatedElement2.divide(normalizingDenominator, SIMILARITY_CONTEXT);
				v2.set(i, updatedElement2);

			}

			BigDecimal errorValueDenominator = BigDecimal.valueOf(v1.size());
			BigDecimal errorValueNumerator = BigDecimal.ZERO;
			for(int i = 0; i < v1.size(); i++) {
				// for readability
				//BigDecimal sub = v1.get(i).subtract(v2.get(i));
				//BigDecimal abs = sub.abs();
				//BigDecimal pow = abs.pow((int)similarityArc);
				errorValueNumerator = errorValueNumerator.add((v1.get(i).subtract(v2.get(i))).abs().pow((int)similarityArc));
			}
			BigDecimal error = errorValueNumerator.divide(errorValueDenominator);
			newSimilarityCalc = 1 / (1 + error.doubleValue());
			return newSimilarityCalc;
		}

	}

	/**
	 * Perform a default match.  This will <b>not</b> take interpretations into account.
	 * To boost matching confidence with interpretations, use the same method with an additional
	 * decimal parameter.
	 * @param name1
	 * @param p1
	 * @param name2
	 * @param p2
	 * @return
	 */
	public static double match(String name1, Profile p1, String name2, Profile p2) {
		return match(name1, p1, name2, p2, -1);
	}

	/**
	 * Get the match confidence of the two profiles.  If the sameInterpretationWeight parameter is
	 * above 0, an interpretation match will boost the confidence.  The confidence is affected by an
	 * interpretation match like so:
	 * 
	 * normalConfidence = match(name1, p1, name2, p2)
	 * boostedConfidence = sameInterpretationWeight + ((1 sameInterpretationWeight) * normalConfidence)
	 * 
	 * 
	 * @param name1
	 * @param p1
	 * @param name2
	 * @param p2
	 * @param sameInterpretationWeight the decimal boost (0-1) that an interpretation match should provide
	 * @return
	 */
	public static double match(String name1, Profile p1, 
			String name2, Profile p2, double sameInterpretationWeight) {
		if (sameInterpretationWeight > 1) {
			throw new ArithmeticException("Cannot provide an interpretation boost above 1");
		}
		String algId = System.getenv("SCHWIZ_MATCHING_ALG");
		// temporarily allow environment to specify matching algorithm
		if(p1.getPresence() < 0.0f || p2.getPresence() < 0.0f) {
			// could add some manually created matching logic, but we don't currently have any
			return 0.0;
		}

		if(name1.equals(DefaultProfilerRecord.EMPTY_FIELD_NAME_INDICATOR) 
				|| name2.equals(DefaultProfilerRecord.EMPTY_FIELD_NAME_INDICATOR)) {
			if(!name1.equals(name2)) {
				return 0.0; 
			}
		}
		if(name1.contains(String.valueOf(DefaultProfilerRecord.STRUCTURED_OBJECT_APPENDER))) {
			name1 = name1.substring(name1.lastIndexOf(DefaultProfilerRecord.STRUCTURED_OBJECT_APPENDER)+1, name1.length());
		}
		if(name2.contains(String.valueOf(DefaultProfilerRecord.STRUCTURED_OBJECT_APPENDER))) {
			name2 = name2.substring(name2.lastIndexOf(DefaultProfilerRecord.STRUCTURED_OBJECT_APPENDER)+1, name2.length());
		}


		final double nameWeight = .20;
		final double defaultDisimilarityRate = 1.25;
		final double defaultSimilarityArc = 6.0;
		if("1".equals(algId)) {
			return similarityAlgorithm1(name1, p1, name2, p2, nameWeight);
		} else if("2".equals(algId)) {
			// default is algorithm 2
			return similarityAlgorithm2(name1, p1, name2, p2, 
					defaultDisimilarityRate, defaultSimilarityArc, nameWeight, sameInterpretationWeight);
		} else {
			return similarityAlgorithm2(name1, p1, name2, p2, 
					defaultDisimilarityRate, defaultSimilarityArc, nameWeight, sameInterpretationWeight);
		}
	}

	/**
	 * Return true if every key and nested key in an object match.
	 * @param name1
	 * @param name2
	 * @param profilesWithObjects
	 * @return
	 */
	public static double addStructureWeight(double originalConfidence, String name1, String name2, 
			Map<String, Profile> profiles1, Map<String, Profile> profiles2) {
		if (originalConfidence < .81f) {
			return originalConfidence;
		}
		int structureSeparatorIndex1 = name1.lastIndexOf(DefaultProfilerRecord.STRUCTURED_OBJECT_APPENDER);
		int structureSeparatorIndex2 = name2.lastIndexOf(DefaultProfilerRecord.STRUCTURED_OBJECT_APPENDER);
		if (structureSeparatorIndex1 < 0 && structureSeparatorIndex2 < 0) {
			return originalConfidence;
		} 
		String parentKey1 = structureSeparatorIndex1 < 0 ? "" : name1.substring(0, structureSeparatorIndex1);
		String parentKey2 = structureSeparatorIndex2 < 0 ? "" : name2.substring(0, structureSeparatorIndex2);
		boolean doStructuresMatch = doStructuresMatch(parentKey1, parentKey2, profiles1, profiles2);
		return (((5 * originalConfidence) - 4) // take last 20 % of confidence into account (x - .8) * 5
				* .1f) // scale this confidence in a .0 - .1 window
				+ (doStructuresMatch ? .9f : .8f); // add the base amount based on structure match
	}
	
	public static boolean doStructuresMatch(String parentKey1, String parentKey2,
			Map<String, Profile> profiles1, Map<String, Profile> profiles2) {
		// need to handle root matches differently
		List<String> childrenKeys = profiles1.keySet().parallelStream()
				.filter(getChildrenKeysFilter(parentKey1))
				.map(mapFullKeyToChildName(parentKey1))
				.collect(Collectors.toList());
		return profiles2.keySet().parallelStream()
				.filter(getChildrenKeysFilter(parentKey2))
				.map(mapFullKeyToChildName(parentKey2))
				.allMatch(childrenKey2 -> childrenKeys.contains(childrenKey2));
	}
	
	private static Predicate<String> getChildrenKeysFilter(String parent) {
		return parent.isEmpty() ? key -> true :
			key -> key.startsWith(parent + DefaultProfilerRecord.STRUCTURED_OBJECT_APPENDER);
	}
	
	private static Function<String, String> mapFullKeyToChildName(String parent) {
		return parent.isEmpty() ? key -> key : key -> key.substring(parent.length() + 1);
	}

	public static double structureMatch(String name1, String name2, 
			Map<String, Profile> profiles1, Map<String, Profile> profiles2) {
		double confidence = match(name1, profiles1.get(name1), name2, profiles2.get(name2), .8);
		return addStructureWeight(confidence, name1, name2, profiles1, profiles2);
	}

	private static Integer stripNumDistinctValuesChars(String numDistinctValues) {
		return MetricsCalculationsFacade.stripNumDistinctValuesChars(numDistinctValues);
	}

}
