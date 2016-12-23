package com.deleidos.dp.h2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.h2.jdbc.JdbcSQLException;

import com.deleidos.dp.beans.User;
import com.deleidos.dp.exceptions.DataAccessException;
import com.deleidos.dp.exceptions.H2DataAccessException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class H2ShiroDataAccessObject {
	/*--------------------------------------------
	|    I N S T A N C E   V A R I A B L E S    |
	============================================*/
	private static final Logger logger = H2DataAccessObject.logger;

	/*--------------------------------------------
	|             C O N S T A N T S             |
	============================================*/
	private static final String AUTH_QUERY = "SELECT password, salt FROM users WHERE user_name = ?";
	private static final String ROLES_QUERY = "SELECT user_role FROM user_roles WHERE user_name = ?";
	private static final String PERMISSIONS_QUERY = "SELECT permission FROM roles_permissions WHERE role_name = ?";
	private static final String CREATE_USER = "INSERT INTO users(user_name, password, salt, first_name, last_name) VALUES (?, ?, ?, ?, ?);";
	private static final String GET_USER = "SELECT * FROM users where user_name = ?;";
	private static final String GET_USER_FROM_FIRST_NAME = "SELECT * FROM users where first_name = ?;";
	private static final String GET_ALL_USERS = "SELECT * FROM users;";
	private static final String UPDATE_USER = "UPDATE users SET password = ?, salt = ?, first_name = ?, last_name = ? WHERE user_name = ?;";
	private static final String UPDATE_USER_NAMES = "UPDATE users SET first_name = ?, last_name = ? WHERE user_name = ?;";
	private static final String UPDATE_USER_ROLE = "UPDATE user_roles SET user_role = ? WHERE user_name = ?;";
	private static final String DELETE_USER = "DELETE FROM users WHERE user_name = ?;";
	private static final String DELETE_USER_ROLE = "DELETE FROM user_roles WHERE user_name = ?;";
	private static final String ADD_ROLES = "INSERT INTO user_roles(user_role_mapping_id, user_role, user_name) VALUES (NULL, ?, ?);";
	private static final String SECURITY_QUESTIONS_QUERY = "SELECT * FROM user_security_questions WHERE user_name = ?;";
	private static final String CREATE_SECURITY_QUESTIONS = "INSERT INTO user_security_questions(question_1, answer_1, question_2, answer_2, question_3, answer_3, user_name) VALUES (?, ?, ?, ?, ?, ?, ?)";
	private static final String CREATE_DEFAULT_SECURITY_QUESTIONS = "INSERT INTO security_questions(question) VALUES (?);";
	private static final String GET_ALL_SECURITY_QUESTIONS = "SELECT * FROM security_questions;";
	private static final int H2_REFERENTIAL_INTEGRITY_ERROR_CODE = 23505;
	private static final String DUPLICATE_USERNAME = "DUPLICATE_USERNAME";
	private static final String DUPLICATE_FIRST_NAME = "DUPLICATE_FIRST_NAME";

	/*--------------------------------------------
	|               M E T H O D S                |
	============================================*/

	public String createUser(Connection conn, User user) throws SQLException, H2DataAccessException {
		try {
			conn.setAutoCommit(false);
			createUserInDB(conn, user);

			logger.debug("Adding user " + user.getUserName() + " with the role of " + user.getUserRole());
			addUserRoles(conn, user.getUserRole(), user.getUserName());

			conn.commit();
			return user.getUserName();
		} catch (SQLException e) {
			if (e.getErrorCode() == H2_REFERENTIAL_INTEGRITY_ERROR_CODE) {
				throw new H2DataAccessException(DUPLICATE_USERNAME, e);
			} else {
				throw e;
			}
		}
	}

	/**
	 * 
	 * 
	 * @param conn
	 * @param username
	 * @return User
	 * @throws SQLException
	 * @throws H2DataAccessException
	 */
	public User getUser(Connection conn, String username) throws SQLException, H2DataAccessException {
		User user = new User();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(GET_USER);
			ps.setString(1, username);

			// Execute query
			rs = ps.executeQuery();

			// Loop over results - although we are only expecting one result,
			// since usernames should be unique
			boolean foundMultipleResults = false;
			while (rs.next()) {
				// Check to ensure only one row is processed
				if (foundMultipleResults) {
					logger.error("More than one user row found for user [" + username + "]. Usernames must be unique.");
					throw new H2DataAccessException(
							"More than one user row found for user [" + username + "]. Usernames must be unique.");
				}

				user = populateUser(conn, rs);

				foundMultipleResults = true;
			}
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
		}
		return user;
	}

	public List<User> getAllUsers(Connection conn) throws SQLException, H2DataAccessException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<User> userList = new ArrayList<User>();

		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(GET_ALL_USERS);

			// Execute query
			rs = ps.executeQuery();

			while (rs.next()) {
				User user = new User();
				user = populateUser(conn, rs);
				userList.add(user);
			}
			conn.commit();
		} catch (JdbcSQLException e) {
			logger.error("There was an error retrieving users from the database.");
			logger.error("It may be that the resultset cannot find a column");
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
		}
		return userList;
	}

	public boolean updateUser(Connection conn, User newUser) throws SQLException, H2DataAccessException {
		PreparedStatement ps = null;
		int rowsAffected = 0;
		User mergedUser = null;

		String username = newUser.getUserName();

		try {
			conn.setAutoCommit(false);

			User existingUser = getUser(conn, username);

			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> mergedProps = new HashMap<String, Object>();
			Map<String, Object> newUserProps = mapper.convertValue(newUser, Map.class);
			Map<String, Object> oldUserProps = mapper.convertValue(existingUser, Map.class);

			logger.debug("User object from H2: " + oldUserProps.toString());
			logger.debug("New user object from webapp: " + newUserProps.toString());

			oldUserProps.forEach((k, v) -> {
				if (v != null)
					mergedProps.put(k, v);
			});
			newUserProps.forEach((k, v) -> {
				if (v != null)
					mergedProps.put(k, v);
			});

			logger.debug("Merged user object: " + mergedProps.toString());

			mergedUser = mapper.convertValue(mergedProps, User.class);

			if (mergedUser.getPassword() == null) {
				ps = conn.prepareStatement(UPDATE_USER_NAMES);
				ps.setString(1, mergedUser.getFirstName());
				ps.setString(2, mergedUser.getLastName());
				ps.setString(3, mergedUser.getUserName());
			} else {
				ps = conn.prepareStatement(UPDATE_USER);
				ps.setString(1, mergedUser.getPassword());
				ps.setString(2, mergedUser.getSalt());
				ps.setString(3, mergedUser.getFirstName());
				ps.setString(4, mergedUser.getLastName());
				ps.setString(5, mergedUser.getUserName());
			}

			// Execute query
			rowsAffected = ps.executeUpdate();

			updateUserRole(conn, mergedUser);
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ps != null)
				ps.close();
		}

		if (rowsAffected == 1) {
			return true;
		} else {
			if (rowsAffected == 0) {
				logger.error("The user: " + username + " was not updated because it was not found");
			} else {
				logger.error("The updating of username: " + username + " may or may not have been successful\n"
						+ Integer.toString(rowsAffected) + " rows were affected by this operation.");
			}
			return false;
		}
	}

	public boolean updateUserRole(Connection conn, User user) throws SQLException {
		PreparedStatement ps = null;
		int rowsAffected = 0;

		String username = user.getUserName();
		String role = user.getUserRole();

		logger.debug("Updating user: " + username + " to the role of " + role);

		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(UPDATE_USER_ROLE);
			ps.setString(1, role);
			ps.setString(2, username);

			// Execute query
			rowsAffected = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ps != null)
				ps.close();
		}

		if (rowsAffected == 1) {
			return true;
		} else {
			if (rowsAffected == 0) {
				logger.error("The user: " + username + "'s role was not updated because it was not found");
			} else {
				logger.error("The updating of user: " + username + "'s role may or may not have been successful\n"
						+ Integer.toString(rowsAffected) + " rows were affected by this operation.");
			}
			return false;
		}
	}

	public boolean deleteUser(Connection conn, String username) throws SQLException {
		PreparedStatement ps = null;
		int rowsAffected = 0;

		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(DELETE_USER);
			ps.setString(1, username);

			// Execute query
			rowsAffected = ps.executeUpdate();
		} finally {
			if (ps != null)
				ps.close();
		}

		if (rowsAffected == 1) {
			return true;
		} else {
			if (rowsAffected == 0) {
				logger.error("The username: " + username + " was not deleted because it was not found");
			} else {
				logger.error("The deletion of username: " + username + " may or may not have been successful\n"
						+ Integer.toString(rowsAffected) + " rows were affected by this operation.");
			}
			return false;
		}
	}

	public boolean deleteUserRole(Connection conn, String username) throws SQLException {
		PreparedStatement ps = null;
		int rowsAffected = 0;

		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(DELETE_USER_ROLE);
			ps.setString(1, username);

			// Execute query
			rowsAffected = ps.executeUpdate();
			conn.commit();
		} finally {
			if (ps != null)
				ps.close();
		}

		if (rowsAffected == 1) {
			return true;
		} else {
			if (rowsAffected == 0) {
				logger.error("The username: " + username + "'s role was not deleted because it was not found");
			} else {
				logger.error("The deletion of username: " + username + "'s role may or may not have been successful\n"
						+ Integer.toString(rowsAffected) + " rows were affected by this operation.");
			}
			return false;
		}
	}

	// These methods are for the H2Realm
	public User getPasswordForUser(Connection conn, String username) throws H2DataAccessException, SQLException {
		User user = new User();
		boolean returningSeparatedSalt = true;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(AUTH_QUERY);
			ps.setString(1, username);

			// Execute query
			rs = ps.executeQuery();

			// Loop over results - although we are only expecting one result,
			// since usernames should be unique
			boolean foundMultipleResults = false;
			while (rs.next()) {
				// Check to ensure only one row is processed
				if (foundMultipleResults) {
					throw new H2DataAccessException(
							"More than one user row found for user [" + username + "]. Usernames must be unique.");
				}

				user.setPassword(rs.getString(1));
				if (returningSeparatedSalt) {
					user.setSalt(rs.getString(2));
				}

				foundMultipleResults = true;
			}
			conn.commit();
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
		}
		return user;
	}

	public Set<String> getRoleNamesForUser(Connection conn, String username) throws SQLException {
		Set<String> roleNames = new LinkedHashSet<String>();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(ROLES_QUERY);
			ps.setString(1, username);

			// Execute query
			rs = ps.executeQuery();

			// Loop over results and add each returned role to a set
			while (rs.next()) {
				String roleName = rs.getString(1);

				// Add the role to the list of names if it isn't null
				if (roleName != null) {
					roleNames.add(roleName);
				} else {
					logger.warn("Null role name found while retrieving role names for user [" + username + "]");
				}
			}
			conn.commit();
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
		}
		return roleNames;
	}

	public Set<String> getPermissions(Connection conn, String username, Collection<String> roleNames)
			throws SQLException {
		Set<String> permissions = new LinkedHashSet<String>();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(PERMISSIONS_QUERY);

			for (String roleName : roleNames) {

				ps.setString(1, roleName);
				try {
					// Execute query
					rs = ps.executeQuery();

					// Loop over results and add each returned role to a set
					while (rs.next()) {
						String permissionString = rs.getString(1);

						// Add the permission to the set of permissions
						permissions.add(permissionString);
					}
				} finally {
					rs.close();
				}
			}
			conn.commit();
		} finally {
			if (ps != null)
				ps.close();
		}
		return permissions;
	}

	public User getUsernameFromFirstName(Connection conn, String firstName) throws SQLException, H2DataAccessException {
		User user = new User();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(GET_USER_FROM_FIRST_NAME);
			ps.setString(1, firstName);

			// Execute query
			rs = ps.executeQuery();

			// Loop over results - although we are only expecting one result,
			// since names should be unique
			boolean foundMultipleResults = false;
			while (rs.next()) {
				// Check to ensure only one row is processed
				if (foundMultipleResults) {
					logger.error("More than one user row found for name [" + firstName + "]. Names must be unique.");
					throw new H2DataAccessException(
							DUPLICATE_FIRST_NAME);
				}

				user = populateUser(conn, rs);

				foundMultipleResults = true;
			}
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
		}
		return user;
	}

	public boolean addSecurityQuestionsForUser(Connection conn, User user) throws SQLException {
		PreparedStatement ps = null;

		logger.debug("Adding security questions for user: " + user.getUserName());
		logger.debug("Question 1: " + user.getSecurityQuestion1());
		logger.debug("Question 2: " + user.getSecurityQuestion2());
		logger.debug("Question 3: " + user.getSecurityQuestion3());

		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(CREATE_SECURITY_QUESTIONS);
			ps.setString(1, user.getSecurityQuestion1());
			ps.setString(2, user.getSecurityQuestion1Answer());
			ps.setString(3, user.getSecurityQuestion2());
			ps.setString(4, user.getSecurityQuestion2Answer());
			ps.setString(5, user.getSecurityQuestion3());
			ps.setString(6, user.getSecurityQuestion3Answer());
			ps.setString(7, user.getUserName());

			// Execute query
			ps.execute();
		} finally {
			if (ps != null)
				ps.close();
		}

		return true;
	}

	public User getSecurityQuestionsForUser(Connection conn, String username)
			throws H2DataAccessException, SQLException {
		User user = getUser(conn, username);
		user = populateSecurityQuestions(conn, user.getUserName());
		return user;

	}

	public boolean verifySecurityAnswersForUser(Connection conn, User submittedUser) throws SQLException {
		User storedUser = populateSecurityQuestions(conn, submittedUser.getUserName());

		if (storedUser.getSecurityQuestion1() == null)
			return false;
		if (storedUser.getSecurityQuestion2() == null)
			return false;
		if (storedUser.getSecurityQuestion3() == null)
			return false;

		if (storedUser.getSecurityQuestion1Answer() == null)
			return false;
		if (storedUser.getSecurityQuestion2Answer() == null)
			return false;
		if (storedUser.getSecurityQuestion3Answer() == null)
			return false;

		logger.debug("Comparing security questions and answers.");
		logger.debug("Existing question 1: " + storedUser.getSecurityQuestion1());
		logger.debug("Existing question 2: " + storedUser.getSecurityQuestion2());
		logger.debug("Existing question 3: " + storedUser.getSecurityQuestion3());

		logger.debug("Submitted question 1: " + submittedUser.getSecurityQuestion1());
		logger.debug("Submitted question 2: " + submittedUser.getSecurityQuestion2());
		logger.debug("Submitted question 3: " + submittedUser.getSecurityQuestion3());

		if (!isEqual(storedUser.getSecurityQuestion1(), submittedUser.getSecurityQuestion1()))
			return false;
		if (!isEqual(storedUser.getSecurityQuestion2(), submittedUser.getSecurityQuestion2()))
			return false;
		if (!isEqual(storedUser.getSecurityQuestion3(), submittedUser.getSecurityQuestion3()))
			return false;

		if (!isEqual(storedUser.getSecurityQuestion1Answer(), submittedUser.getSecurityQuestion1Answer()))
			return false;
		if (!isEqual(storedUser.getSecurityQuestion2Answer(), submittedUser.getSecurityQuestion2Answer()))
			return false;
		if (!isEqual(storedUser.getSecurityQuestion3Answer(), submittedUser.getSecurityQuestion3Answer()))
			return false;

		// return true if all of the questions and all of the answers are equal
		return true;
	}

	public List<String> getAllSecurityQuestionsFromBank(Connection conn) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String> questions = new ArrayList<String>();

		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(GET_ALL_SECURITY_QUESTIONS);

			// Execute query
			rs = ps.executeQuery();

			while (rs.next()) {
				String question = rs.getString("question");
				questions.add(question);
				logger.debug("Getting security question: " + question);
			}
			return questions;
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
		}
	}

	// Initialization
	/**
	 * Adds a default admin role if the users table is empty
	 * 
	 * @return true if a default user is added
	 * @throws SQLException
	 * @throws DataAccessException 
	 */
	public boolean initializeDefaultUser(Connection conn, User user) throws SQLException, H2DataAccessException {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(GET_ALL_USERS);

			// Execute query
			rs = ps.executeQuery();

			if (!rs.next()) {
				createUser(conn, user);
				return true;
			}
			return false;
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
		}
	}

	public boolean initializeDefaultSecurityQuestions(Connection conn) throws SQLException, H2DataAccessException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean ran = false;

		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(GET_ALL_SECURITY_QUESTIONS);

			// Execute query
			rs = ps.executeQuery();

			if (!rs.next()) {
				// twelve questions means 4 questions for every group of
				// security questions
				// (3 groups)
				List<String> questions = new ArrayList<String>();
				questions.add("What was the name of your elementary school?");
				questions.add("What is the first and last name of your first boyfriend/girlfriend?");
				questions.add("In what city were you born?");
				questions.add("What was the make of your first car?");
				questions.add("What is your father's middle name?");
				questions.add("What is your mother's maiden name?");
				questions.add("What was your high school mascot?");
				questions.add("What is your favorite color?");
				questions.add("What is your paternal grandfather's first name?");
				questions.add("Where did you travel to for the first time?");
				questions.add("Who was your first manager?");
				questions.add("What was the first company you worked for?");

				for (String question : questions) {
					addQuestionToBank(conn, question);
				}
				ran = true;
			}
			return ran;
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
		}
	}

	// Private methods
	private boolean addQuestionToBank(Connection conn, String question) throws SQLException, H2DataAccessException {
		PreparedStatement ps = null;
		boolean success = false;

		try {
			ps = conn.prepareStatement(CREATE_DEFAULT_SECURITY_QUESTIONS);
			ps.setString(1, question);

			// Execute query
			success = ps.execute();
		} finally {
			if (ps != null)
				ps.close();
		}

		return success;
	}

	private boolean isEqual(String str1, String str2) {
		if (str1.equals(str2))
			return true;
		return false;
	}

	private void addUserRoles(Connection conn, String user_role, String user_name)
			throws SQLException, H2DataAccessException {
		PreparedStatement ps = null;

		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(ADD_ROLES);
			ps.setString(1, user_role);
			ps.setString(2, user_name);
			ps.execute();
		} catch (Exception e) {
			logger.error("Prepared Statement: " + ADD_ROLES);
			logger.error("Role: " + user_role);
			logger.error("Name: " + user_name);
			logger.error(e.getMessage());
		} finally {
			if (ps != null)
				ps.close();
		}
	}

	private String createUserInDB(Connection conn, User user) throws SQLException {
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(CREATE_USER);
			ps.setString(1, user.getUserName());
			ps.setString(2, user.getPassword());
			ps.setString(3, user.getSalt());
			ps.setString(4, user.getFirstName());
			ps.setString(5, user.getLastName());

			// Execute query
			ps.execute();
		} finally {
			if (ps != null)
				ps.close();
		}

		return user.getUserName();
	}

	private User populateUser(Connection conn, ResultSet rs) throws SQLException {
		User user = new User();
		String username = rs.getString("user_name");

		user.setUserName(username);
		user.setPassword(rs.getString("password"));
		user.setSalt(rs.getString("salt"));
		user.setFirstName(rs.getString("first_name"));
		user.setLastName(rs.getString("last_name"));

		Set<String> roles = getRoleNamesForUser(conn, username);

		if (roles.size() > 1) {
			logger.error("User: " + username + " has more than one role when there should only be one.");
		}

		for (String role : roles) {
			user.setUserRole(role);
		}

		return user;
	}

	private User populateSecurityQuestions(Connection conn, String username) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		User user = new User();
		user.setUserName(username);

		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(SECURITY_QUESTIONS_QUERY);
			ps.setString(1, username);

			// Execute query
			rs = ps.executeQuery();

			while (rs.next()) {
				user.setSecurityQuestion1(rs.getString("question_1"));
				user.setSecurityQuestion1Answer(rs.getString("answer_1"));
				user.setSecurityQuestion2(rs.getString("question_2"));
				user.setSecurityQuestion2Answer(rs.getString("answer_2"));
				user.setSecurityQuestion3(rs.getString("question_3"));
				user.setSecurityQuestion3Answer(rs.getString("answer_3"));
			}
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
		}

		return user;
	}
}
