package com.deleidos.dp.calculations;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.math.NumberUtils;

import com.deleidos.dp.enums.DetailType;
import com.deleidos.dp.enums.MainType;
import com.deleidos.dp.enums.Tolerance;
import com.deleidos.dp.exceptions.MainTypeException;
import com.deleidos.dp.profiler.DefaultProfilerRecord;

/**
 * Methods that determine the type of an arbitrary value or field.
 * @author leegc
 *
 */
public class TypeDetermination {


	/**
	 * Determine the data types that a value <i>probably</i> is.
	 * @param value The object being evaluated.
	 * @param binaryPercentageCutoff The percentage cutoff for when a string should be considered binary.
	 * @return a list of possible types, either binary or number and string.
	 */
	public static List<MainType> determineProbableDataTypes(Object value, float binaryPercentageCutoff) {
		if(value == null) {
			return Arrays.asList(MainType.NULL);
		}
		ArrayList<MainType> typeList = new ArrayList<MainType>();
		String stringValue = value.toString();
		if(value instanceof ByteBuffer) {
			typeList.add(MainType.BINARY);
			return typeList;
		}

		if(value instanceof Number) {
			typeList.add(MainType.NUMBER);
		} else if(value instanceof Boolean) {
			typeList.add(MainType.STRING);
		} else if(value instanceof String) {
			if(NumberUtils.isNumber(stringValue)) {
				typeList.add(MainType.NUMBER);
			}
		} else if(value instanceof List) {
			typeList.add(MainType.ARRAY);
			return typeList;
		} else if(value instanceof Map) {
			typeList.add(MainType.OBJECT);
			return typeList;
		}
		typeList.add(MainType.STRING);
		return typeList;
	}

	public static DetailType determineStringDetailType(Object object) {
		String stringValue = object.toString();
		String dateRegex = "(19|20)\\d\\d[- /.](0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])";
		Pattern datePattern = Pattern.compile(dateRegex);
		Matcher dateMatcher = datePattern.matcher(stringValue);
		if(dateMatcher.matches()) {
			return DetailType.DATE_TIME;
		} 
		if(stringValue.equals(DefaultProfilerRecord.EMPTY_FIELD_VALUE_INDICATOR)) {
			return DetailType.TERM;
		} else if(stringValue.contains(" ")) {
			// TODO Adjust if necessary
			if (stringValue.split(" ").length > 5) {
				return DetailType.TEXT;
			} else {
				return DetailType.PHRASE;
			}
		} else {
			String booleanRegex = "(TRUE|FALSE|true|false|T|F|t|f|yes|no|YES|NO|Yes|No|True|False)";
			Pattern bPattern = Pattern.compile(booleanRegex);
			Matcher bMatcher = bPattern.matcher(stringValue);
			if(bMatcher.matches()) return DetailType.BOOLEAN;
			return DetailType.TERM;
		}
	}

	public static DetailType determineNumberDetailType(Object object) {
		DetailType type = DetailType.DECIMAL;
		String stringValue = object.toString();
		if(stringValue.contains("e") || stringValue.contains("E") || stringValue.contains("^")) {
			type = DetailType.EXPONENT;
		} else {
			int dotIndex = stringValue.indexOf(".");
			if (dotIndex > -1) {
				boolean allZeros = true;
				for (int i = dotIndex + 1;i < stringValue.length(); i++) {
					if (stringValue.charAt(i) != '0') {
						allZeros = false;
						break;
					}
				}
				if (allZeros) {
					type = DetailType.INTEGER;
				} 
			} else {
				type = DetailType.INTEGER;
			}
		}
		return type;
	}


	public static DetailType determineBinaryDetailType(Object object) {
		DetailType type = null;
		return type;
	}

	/**
	 * Determine the detail type of the given record.
	 */
	public static DetailType determineDetailType(MainType mainType, Object object) {
		DetailType detailType = null;
		switch(mainType) {
		case STRING: {
			return determineStringDetailType(object);
		}
		case NUMBER: {
			return determineNumberDetailType(object);
		}
		case BINARY: {
			return determineBinaryDetailType(object);
		}
		default:
			break;
		}
		return detailType;
	}
	
	public static DetailType getDetailTypeFromDistribution(MainType mainType, int[] distribution) throws MainTypeException {
		int m = -1;
		for(int i = 0; i < distribution.length; i++) {
			if(m > -1) {
				if(distribution[i] > distribution[m] && DetailType.getTypeByIndex(i).getMainType().equals(mainType)) {
					m = i;
				}
			} else {
				if(distribution[i] > 0 && DetailType.getTypeByIndex(i).getMainType().equals(mainType)) {
					m = i;
				}
			}
		}
		if(m > -1) {
			if(m == DetailType.INTEGER.getIndex() && distribution[DetailType.DECIMAL.getIndex()] > 0) {
				m = DetailType.DECIMAL.getIndex();
			}
			return DetailType.getTypeByIndex(m);
		} else {
			throw new MainTypeException(
					"Distribution could not be used to determine detail type for " + mainType.toString() + ".");
		}
	}

	public static MainType getDataTypeFromDistribution(int[] distribution, Tolerance errorLevel) {
		MainType type = null;
		int m = -1;
		for(int i = 0; i < distribution.length; i++) {
			if(distribution[i] > 0) {
				m = i;
			}
		}
		if(m > -1) {
			type = MainType.getTypeByIndex(m);
			if(type.equals(MainType.BINARY)) {
				return type;
			}
			if(distribution[MainType.NUMBER.getIndex()] == distribution[MainType.STRING.getIndex()]) {
				type = MainType.NUMBER;
			}
			if(type == MainType.NUMBER) {
				switch(errorLevel) {
				case STRICT: {
					if(distribution[MainType.STRING.getIndex()] > 0) {
						return MainType.STRING;
					}
				}
				case MODERATE: {
					float percentageString = (float)(distribution[MainType.STRING.getIndex()] 
							/ (float)(distribution[MainType.STRING.getIndex()] + distribution[MainType.BINARY.getIndex()] + distribution[MainType.NUMBER.getIndex()]));
					if(distribution[MainType.STRING.getIndex()] > 0 && percentageString > Tolerance.MODERATE.getAcceptableErrorsPercentage()) {
						return MainType.STRING;
					}
				}
				case RELAXED : {
					float percentageString = (float)(distribution[MainType.STRING.getIndex()] 
							/ (float)(distribution[MainType.STRING.getIndex()] + distribution[MainType.BINARY.getIndex()] + distribution[MainType.NUMBER.getIndex()]));
					if(distribution[MainType.STRING.getIndex()] > 0 && percentageString > Tolerance.RELAXED.getAcceptableErrorsPercentage()) {
						return MainType.STRING;
					}
				}
				}
			}
		}
		return type;
	}
	
}
