package com.deleidos.sw.enums;

public enum Roles {
	admin("admin"), engineer("engineer"), analyst("analyst"), guest("guest");

	private String value;

	Roles (final String value) {
        this.value = value;
    }

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return this.getValue();
	}
}
