package com.deleidos.dp.beans;

import java.util.Set;

import com.deleidos.dp.enums.Roles;

public class User {
	private String firstName;
	private String lastName;
	private String userName;
	private String password;
	private String salt;
//	private Set<Roles> userRoles; Unused for now .. can be implemented for more powerful controls
	private String userRole; // for deserializing single roles
	private Set<String> permissions;
	private String securityQuestion1;
	private String securityQuestion1Answer;
	private String securityQuestion2;
	private String securityQuestion2Answer;
	private String securityQuestion3;
	private String securityQuestion3Answer;
	
	
	/*--------------------------------------------
    |  A C C E S S O R S / M O D I F I E R S    |
    ============================================*/
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

//	public Set<Roles> getUserRoles() {
//		return userRoles;
//	}
//
//	public void setUserRoles(Set<Roles> userRoles) {
//		this.userRoles = userRoles;
//	}

	public Set<String> getPermissions() {
		return permissions;
	}

	public void setPermissions(Set<String> permissions) {
		this.permissions = permissions;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	public String getSecurityQuestion1() {
		return securityQuestion1;
	}

	public void setSecurityQuestion1(String securityQuestion1) {
		this.securityQuestion1 = securityQuestion1;
	}

	public String getSecurityQuestion1Answer() {
		return securityQuestion1Answer;
	}

	public void setSecurityQuestion1Answer(String securityQuestion1Answer) {
		this.securityQuestion1Answer = securityQuestion1Answer;
	}

	public String getSecurityQuestion2() {
		return securityQuestion2;
	}

	public void setSecurityQuestion2(String securityQuestion2) {
		this.securityQuestion2 = securityQuestion2;
	}

	public String getSecurityQuestion2Answer() {
		return securityQuestion2Answer;
	}

	public void setSecurityQuestion2Answer(String securityQuestion2Answer) {
		this.securityQuestion2Answer = securityQuestion2Answer;
	}

	public String getSecurityQuestion3() {
		return securityQuestion3;
	}

	public void setSecurityQuestion3(String securityQuestion3) {
		this.securityQuestion3 = securityQuestion3;
	}

	public String getSecurityQuestion3Answer() {
		return securityQuestion3Answer;
	}

	public void setSecurityQuestion3Answer(String securityQuestion3Answer) {
		this.securityQuestion3Answer = securityQuestion3Answer;
	}
}
