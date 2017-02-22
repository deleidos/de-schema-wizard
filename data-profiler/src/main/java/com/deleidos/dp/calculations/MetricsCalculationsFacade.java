package com.deleidos.dp.calculations;

import java.math.MathContext;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import com.deleidos.dp.beans.DataSample;
import com.deleidos.dp.beans.MatchingField;
import com.deleidos.dp.beans.Profile;
import com.deleidos.dp.beans.Schema;
import com.deleidos.dp.beans.StructuredNode;
import com.deleidos.dp.beans.StructuredProfile;
import com.deleidos.dp.deserializors.ConversionUtility;
import com.deleidos.dp.enums.Tolerance;
import com.deleidos.dp.exceptions.MainTypeException;
import com.deleidos.dp.profiler.DefaultProfilerRecord;
import com.deleidos.dp.profiler.api.ProfilingProgressUpdateHandler;
import com.deleidos.hd.enums.DetailType;
import com.deleidos.hd.enums.MainType;
import com.deleidos.hd.h2.H2Database;

/**
 * Utility class to drive most of the calculations that need to be done for metrics.  Uses the MatchingAlgorithm
 * and TypeDetermination classes.  Provides default values for method calls.
 * @author leegc
 *
 */
public class MetricsCalculationsFacade {
	public static final MathContext DEFAULT_CONTEXT = MathContext.DECIMAL128;
	public static String GREATER_THAN_EQUAL_TO = ">=";
	private static final Logger logger = Logger.getLogger(MetricsCalculationsFacade.class);
	
	public static List<MainType> determineProbableDataTypes(Object value, float binaryPercentageCutoff) {
		return TypeDetermination.determineProbableDataTypes(value, binaryPercentageCutoff);
	}

	public static List<MainType> determineProbableDataTypes(Object value) {
		return TypeDetermination.determineProbableDataTypes(value, .3f);
	}
	
	public static DetailType determineDetailType(MainType mainType, Object value) throws MainTypeException {
		switch (mainType) {
		case NUMBER: return TypeDetermination.determineNumberDetailType(value);
		case STRING: return TypeDetermination.determineStringDetailType(value);
		case BINARY: return TypeDetermination.determineBinaryDetailType(value);
		default: throw new MainTypeException("Could not determination detail type of "
				+ value + " as a " + mainType + ".");
		}
	}
		
	public static DetailType getDetailTypeFromDistribution(String mainType, int[] distribution) throws MainTypeException {
		return TypeDetermination.getDetailTypeFromDistribution(MainType.fromString(mainType), distribution);
	}
	
	public static MainType getDataTypeFromDistribution(int[] dataTypeTracker, Tolerance toleranceLevel) {
		return TypeDetermination.getDataTypeFromDistribution(dataTypeTracker, toleranceLevel);
	}

	public static List<DataSample> matchFieldsAcrossSamplesAndSchema(Schema schema, List<DataSample> samples, 
			double matchCutoff, ProfilingProgressUpdateHandler progressCallback) {
		Set<String> failedGuids = H2Database.getFailedAnalysisMapping().keySet();
		return matchFieldsAcrossSamplesAndSchema(schema, samples, 
				failedGuids, .8, progressCallback);
	}

	public static List<DataSample> matchFieldsAcrossSamplesAndSchema(Schema schema, List<DataSample> samples, 
			ProfilingProgressUpdateHandler progressCallback) {
		return matchFieldsAcrossSamplesAndSchema(schema, samples, .8, progressCallback);
	}

	public static List<DataSample> matchFieldsAcrossSamplesAndSchema(Schema schema, List<DataSample> samples, 
			Set<String> ignoreGuids, double matchCutoff, ProfilingProgressUpdateHandler progressCallback) {
		if(progressCallback == null) {
			progressCallback = new ProfilingProgressUpdateHandler() {
				@Override
				public void handleProgressUpdate(long progress) {
					return;
				}
			};
		}
		int progress = 0;
		List<String> usedFieldNames = new ArrayList<String>();

		if(schema != null) {
			for(String schemaKey : schema.getsProfile().keySet()) {
				usedFieldNames.add(schemaKey);
				for(DataSample otherSample : samples) {
					if(!ignoreGuids.contains(otherSample.getDsGuid())) {
						for(String otherKey : otherSample.getDsProfile().keySet()) {
							String p1Name = schemaKey;
							Profile p1 = schema.getsProfile().get(p1Name);
							String p2Name = otherKey;
							Profile p2 = otherSample.getDsProfile().get(otherKey);

							//double similarity = MatchingAlgorithm.match(p1Name, p1, p2Name, p2, matchCutoff);
							double similarity = MatchingAlgorithm.structureMatch(
									p1Name, p2Name, 
									schema.getsProfile(), 
									otherSample.getDsProfile());
							
							if(similarity > matchCutoff) {
								logger.debug("Match detected between " + p1Name + " in " + otherSample.getDsFileName()
									+ " and " + p2Name + " in schema " + schema.getsName()
									+ " with " + similarity + " confidence.");
								MatchingField altName = new MatchingField();
								List<MatchingField> altNames = p2.getMatchingFields();
								altName.setMatchingField(p1Name);
								altName.setConfidence((int)(similarity*100));
								altNames.add(altName);
								altNames.sort((MatchingField a1, MatchingField a2)->a2.getConfidence()-a1.getConfidence());
								p2.setMatchingFields(altNames);
							}
						}
					}
					progress++;
					progressCallback.handleProgressUpdate(progress);
				}
			}
		}

		for(DataSample sample : samples) {
			// dont analyze the error sample guids
			if(!ignoreGuids.contains(sample.getDsGuid())) {
				for(String key : sample.getDsProfile().keySet()) {
					if(!usedFieldNames.contains(key)) {
						sample.getDsProfile().get(key).setUsedInSchema(true);
						usedFieldNames.add(key);
					} else {
						progress++;
						continue; // seed value is already defined, skip analysis of this key
					}
					for(DataSample otherSample : samples) {
						if(ignoreGuids.contains(otherSample.getDsGuid()) || sample.equals(otherSample)) {
							continue; // skip same sample
						} else {
							for(String otherKey : otherSample.getDsProfile().keySet()) {
								String p1Name = key;
								Profile p1 = sample.getDsProfile().get(p1Name);
								String p2Name = otherKey;
								Profile p2 = otherSample.getDsProfile().get(otherKey);

								//double similarity = MatchingAlgorithm.match(p1Name, p1, p2Name, p2, matchCutoff);
								double similarity = MatchingAlgorithm.structureMatch(
										p1Name, p2Name, 
										sample.getDsProfile(), 
										otherSample.getDsProfile());
								
								if(similarity > matchCutoff) {
									logger.debug("Match detected between " + p1Name + " in " + sample.getDsFileName() + " and " + p2Name + " in " + otherSample.getDsFileName() + " with " + similarity + " confidence.");
									MatchingField altName = new MatchingField();
									List<MatchingField> altNames = p2.getMatchingFields();
									altName.setMatchingField(p1Name);
									altName.setConfidence((int)(similarity*100));
									altNames.add(altName);
									altNames.sort((MatchingField a1, MatchingField a2)->a2.getConfidence()-a1.getConfidence());
									p2.setMatchingFields(altNames);
								}
								progress++;
								progressCallback.handleProgressUpdate(progress);
							}
						}
					}
				}
			}
		}
		
		for (DataSample dataSample : samples) {
			if (dataSample.getDsProfile() != null) {
				dataSample.setDsProfile(ConversionUtility.addObjectProfiles(dataSample.getDsProfile()));
			}
		}
		
		return samples;
	}

	public static Number createNumberWithDoublePrecisionOrLower(Object value) throws MainTypeException {
		String stringValue = value.toString();
		if(NumberUtils.isNumber(stringValue)) {
			try {
				Double d = NumberUtils.createDouble(stringValue);
				if(d == null || d.isInfinite()) {
					throw new MainTypeException("Value " + stringValue + " is too precise for metrics accumulation.");
				} else if(d.longValue() == d.doubleValue()) {
					return d.longValue();
				} else {
					return d;
				}

			} catch (NumberFormatException e) {
				throw new MainTypeException("Value " + stringValue + " is too precise for metrics accumulation.");
			}
		} else {
			throw new MainTypeException("Value "+stringValue+" is not numeric.");
		}
	}

	public static float percentagePrintableCharacters(String stringValue) {
		int charCount = stringValue.length();
		if(charCount < 100) {
			return 1.0f;
		}
		int printableCharCount = 0;
		int nextInt = 0;

		for(int i = 0; i < charCount; i++) {
			nextInt = stringValue.charAt(i);
			if(isPrintableCharacter(nextInt)) {
				printableCharCount++;
			}
		}
		float printablePercentage = ((float)printableCharCount/(float)charCount);
		return printablePercentage;
	}

	public static boolean isPrintableCharacter(int c) {
		if(c > 32 && c < 127) return true;
		return false;
	}

	public static Integer stripNumDistinctValuesChars(String numDistinctValues) {
		try {
			if(numDistinctValues.startsWith(GREATER_THAN_EQUAL_TO)) {
				return Integer.valueOf(numDistinctValues.substring(GREATER_THAN_EQUAL_TO.length())).intValue();
			} else {
				return Integer.valueOf(numDistinctValues).intValue();
			}
		} catch (NumberFormatException e) {
			logger.error(e);
			logger.error("Could not get number of distinct values.");
			return 0;
		}
	}
	
	public static String stringifyNumDistinctValues(Integer numDistinctValues, boolean exceedsMax) {
		return exceedsMax ? GREATER_THAN_EQUAL_TO + String.valueOf(numDistinctValues) : String.valueOf(numDistinctValues);
	}
	
	public static String stringifyNumDistinctValues(Integer numDistinctValues, Integer maxNumDistinctValues) {
		return stringifyNumDistinctValues(numDistinctValues,  numDistinctValues >= maxNumDistinctValues);
	}
	
	public static Number createNumber(Object object) throws MainTypeException {
		if (object == null) {
			return null;
		}
		return MetricsCalculationsFacade.createNumberWithDoublePrecisionOrLower(object);
	}
	
	public static String createString(Object object) throws MainTypeException {
		if (object == null) {
			return null;
		}
		return object.toString();
	}
	
	public static ByteBuffer createBinary(Object object) throws MainTypeException {
		if (object == null) {
			return null;
		}
		if(!(object instanceof ByteBuffer)) {
			throw new MainTypeException("Value "+object.getClass().getName()+" is not a byte buffer.");
		} else {
			return (ByteBuffer)object;
		}
	}
}
