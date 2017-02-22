package com.deleidos.hd.enums;

import com.deleidos.hd.enums.MainType;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum of possible detail types.
 * @author leegc, yoonj
 *
 */
public enum DetailType {
	INTEGER, DECIMAL, EXPONENT, BOOLEAN, TERM, PHRASE, IMAGE, VIDEO_FRAME, AUDIO_SEGMENT, TEXT;

	private static final Map<DetailType, MainType> associationMapping = new HashMap<DetailType, MainType>() {
		private static final long serialVersionUID = 1L;
		{
			put(INTEGER, MainType.NUMBER);
			put(DECIMAL, MainType.NUMBER);
			put(EXPONENT, MainType.NUMBER);
			put(BOOLEAN, MainType.STRING);
			put(TERM, MainType.STRING);
			put(PHRASE, MainType.STRING);
			put(TEXT, MainType.STRING);
			put(IMAGE, MainType.BINARY);
			put(VIDEO_FRAME, MainType.BINARY);
			put(AUDIO_SEGMENT, MainType.BINARY);
		}
	};

	public void incrementCount(int[] detailTypeTracker) {
		detailTypeTracker[getIndex()]++;
	}

	public static DetailType getTypeByIndex(int index) {
		switch(index) {
		case 0: return INTEGER;
		case 1: return DECIMAL;
		case 2: return EXPONENT;
		case 3: return BOOLEAN;
		case 4: return TERM;
		case 5: return PHRASE;
		case 6: return TEXT;
		case 7: return IMAGE;
		case 8: return VIDEO_FRAME;
		case 9: return AUDIO_SEGMENT;
		default : return null;
		}
	}
	public MainType getMainType() {
		return associationMapping.get(this);
	}
	public int getIndex() {
		switch(this) {
		case INTEGER: return 0; case DECIMAL: return 1; case EXPONENT: return 2;
		case BOOLEAN: return 3; case TERM: return 4; 
		case PHRASE: return 5; case TEXT: return 6;
		case IMAGE: return 7; case VIDEO_FRAME: return 8; case AUDIO_SEGMENT: return 9;
		default: return -1;
		}
	}
	@Override
	public String toString() {
		String s = super.toString();
		return s.toLowerCase();
	}
	public static DetailType fromString(String string) {
		string = string.toLowerCase();
		for (DetailType detailType : values()) {
			if (string.equals(detailType.toString())) return detailType;
		}
		return null;
	}
}
