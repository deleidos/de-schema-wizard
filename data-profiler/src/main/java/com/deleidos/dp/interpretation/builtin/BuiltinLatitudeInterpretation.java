package com.deleidos.dp.interpretation.builtin;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.deleidos.dp.beans.BinaryDetail;
import com.deleidos.dp.beans.NumberDetail;
import com.deleidos.dp.beans.Profile;
import com.deleidos.dp.beans.StringDetail;
import com.deleidos.dp.calculations.MatchingAlgorithm;
import com.deleidos.dp.calculations.MetricsCalculationsFacade;
import com.deleidos.hd.enums.DetailType;
import com.deleidos.hd.enums.MainType;

/**
 * Latitude interpretation
 * @author leegc
 *
 */
public class BuiltinLatitudeInterpretation extends AbstractBuiltinInterpretation {

	public BuiltinLatitudeInterpretation() {
		super("Coordinate - Latitude");
	}

	@Override
	public boolean fitsNumberMetrics(Number value) {
		double doubleValue = Double.valueOf(value.toString());
		if(doubleValue < -90 || doubleValue > 90) return false;
		return true;
	}

	public double matchesNumberProfileConfidence(String numberProfileName, Profile finalNumberMetrics) {
		NumberDetail finalNumberDetails = Profile.getNumberDetail(finalNumberMetrics);
		double nameWeight = 0.95f;
		Profile baseProfile = new Profile();
		baseProfile.setMainType(MainType.NUMBER.toString());
		NumberDetail baseComparisonMetrics = new NumberDetail();
		baseComparisonMetrics.setDetailType(DetailType.DECIMAL.toString());
		baseComparisonMetrics.setNumDistinctValues(finalNumberDetails.getNumDistinctValues());
		baseComparisonMetrics.setAverage(finalNumberDetails.getAverage());
		baseComparisonMetrics.setStdDev(finalNumberDetails.getStdDev());
		baseComparisonMetrics.setMin(new BigDecimal(-90));
		baseComparisonMetrics.setMax(new BigDecimal(90));
		baseProfile.setDetail(baseComparisonMetrics);
		return MatchingAlgorithm.originalMatch(numberProfileName, finalNumberMetrics, "Latitude", baseProfile, nameWeight);
	}

	@Override
	public boolean fitsStringMetrics(String value) {
		Pattern latPattern = Pattern.compile(
				"\\s*(([+-]?\\d{1,3}\\*?\\s+\\d{1,2}'?\\s+\\d{1,2}\"?[NSEW]?|\\d{1,3}(:\\d{2}){2}\\.\\d[NSEW]\\s*){1,2})");//|(\\d+(\\.\\d{1,9}))");
		Matcher matcher = latPattern.matcher(value);
		if(matcher.matches()) {
			if(value.contains(" ")) {
				String[] splits = value.split(" ");
				if(splits.length != 3) return false;
				for(int i = 0; i < splits.length; i++) {
					splits[i] = splits[i].replaceAll("[^0-9-]", "");
				}
				double degreesNumberValue;
				double minutesNumberValue;
				double secondsNumberValue;
				degreesNumberValue = Double.valueOf(splits[0]);
				if(degreesNumberValue > -90) {
					if (degreesNumberValue > 90) {
						return false;
					} else if(Double.doubleToLongBits(degreesNumberValue) == 90) {
						minutesNumberValue = Double.valueOf(splits[1]);
						if(Double.doubleToLongBits(minutesNumberValue) != 0) return false;
						secondsNumberValue = Double.valueOf(splits[2]);
						if(Double.doubleToLongBits(secondsNumberValue) != 0) return false;
					} else {
						minutesNumberValue = Double.valueOf(splits[1]);
						if(minutesNumberValue < 0 || minutesNumberValue > 60) return false;
						secondsNumberValue = Double.valueOf(splits[2]);
						if(secondsNumberValue < 0 || secondsNumberValue > 60) return false;
					}
				} else {
					return false;
				}
			} else if(value.contains(":")) {
				String[] splits = value.split(":");
				if(splits.length != 3) return false;
				for(int i = 0; i < splits.length; i++) {
					splits[i] = splits[i].replaceAll("[^0-9-]", "");
				}
				double degreesNumberValue;
				double minutesNumberValue;
				double secondsNumberValue;
				degreesNumberValue = Double.valueOf(splits[0]);
				if(degreesNumberValue > -90) {
					if (degreesNumberValue > 90) {
						return false;
					} else if(Double.doubleToLongBits(degreesNumberValue) == 90) {
						minutesNumberValue = Double.valueOf(splits[1]);
						if(Double.doubleToLongBits(minutesNumberValue) != 0) return false;
						secondsNumberValue = Double.valueOf(splits[2]);
						if(Double.doubleToLongBits(secondsNumberValue) != 0) return false;
					} else {
						minutesNumberValue = Double.valueOf(splits[1]);
						if(minutesNumberValue < 0 || minutesNumberValue > 60) return false;
						secondsNumberValue = Double.valueOf(splits[2]);
						if(secondsNumberValue < 0 || secondsNumberValue > 60) return false;
					}
				} else {
					return false;
				}
			} else if(value.contains(".")) {
				double numberValue;
				numberValue = Double.valueOf(value);
				if(numberValue < -90 || numberValue > 90) return false;
			}
		} else {
			try {
				BigDecimal decimalConversion = new BigDecimal(value);
				if(fitsNumberMetrics(decimalConversion)) {
					return true;
				}
			} catch(NumberFormatException e) {
				logger.debug("Dropped a bad number format: " + value);
				return false;
			}
		}
		return true;
	}

	public double matchesStringProfileConfidence(String stringProfileName, Profile finalStringMetrics) {
		StringDetail finalStringDetails = Profile.getStringDetail(finalStringMetrics);
		double nameWeight = .95f;
		Profile baseProfile = new Profile();
		baseProfile.setMainType(MainType.STRING.toString());
		StringDetail baseComparisonMetrics = new StringDetail();
		if(finalStringMetrics.getDetail().getDetailType().equals(DetailType.BOOLEAN.toString())) return 0.0f;
		else baseComparisonMetrics.setDetailType(finalStringDetails.getDetailType().toString());
		baseComparisonMetrics.setAverageLength(finalStringDetails.getAverageLength());
		baseComparisonMetrics.setMinLength(finalStringDetails.getMinLength());
		baseComparisonMetrics.setMaxLength(finalStringDetails.getMaxLength());
		baseComparisonMetrics.setStdDevLength(.1);
		baseComparisonMetrics.setNumDistinctValues(finalStringDetails.getNumDistinctValues());
		baseProfile.setDetail(baseComparisonMetrics);
		return MatchingAlgorithm.originalMatch(stringProfileName, finalStringMetrics, "Latitude", baseProfile, nameWeight);
	}

	@Override
	public boolean fitsBinaryMetrics(Object value) {
		return false;
	}

	public double matchesBinaryProfileConfidence(BinaryDetail finalBinaryMetrics) {
		return 0;
	}

	@Override
	public double matches(String name, Profile profile) {
		if(profile.getMainType().equals(MainType.NUMBER.toString())) {
			return matchesNumberProfileConfidence(name, profile);
		} else if(profile.getMainType().equals(MainType.STRING.toString())) {
			return matchesStringProfileConfidence(name, profile);
		} else if(profile.getMainType().equals(MainType.BINARY.toString())) {
			return matchesBinaryProfileConfidence(Profile.getBinaryDetail(profile));
		} else {
			return 0;
		}
	}

}
