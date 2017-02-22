package com.deleidos.hd.enums;

/**
 * Enum for all the main types in Schema Wizard.
 * @author leegc
 *
 */
public enum MainType {
	STRING, NUMBER, BINARY, OBJECT, ARRAY, NULL;
	
	public int getIndex() {
		switch (this) {
		case STRING: return 0;
		case NUMBER: return 1;
		case BINARY: return 2;
		case OBJECT: return 3;
		case ARRAY: return 4;
		case NULL: return 5;
		default: return -1;
		}
	}
	
	public static MainType getTypeByIndex(int index) {
		return MainType.values()[index];
	}
	
	public void incrementCount(int[] typeTracker) {
		typeTracker[getIndex()]++;
	}
	
	@Override
	public String toString() {
		String s = super.toString();
		return s.toLowerCase();
	}
	
	public static MainType fromString(String string) {
		string = string.toLowerCase();
		for (MainType type : values()) {
			if (string.equals(type.toString())) return type;
		}
		return null;
	}

}
