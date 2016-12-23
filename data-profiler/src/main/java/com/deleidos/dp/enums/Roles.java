package com.deleidos.dp.enums;

public enum Roles {
	// This should match up with what is in the 'users' table of the H2 database
	admin("admin"), engineer("engineer"), analyst("analyst"), user("user"), guest("guest");

	// Used only for annotations - which must be a String constant in order to
	// be valid
	public final static String ADMIN = "admin";
	public final static String ENGINEER = "engineer";
	public final static String ANALYST = "analyst";
	public final static String USER = "user";
	public final static String GUEST = "guest";

	private String role;

	Roles(final String role) {
		this.role = role;
	}
	public String getRole() {
		return role;
	}

	@Override
	public String toString() {
		return this.getRole();
	}
}
