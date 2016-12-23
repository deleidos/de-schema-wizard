package com.deleidos.sw.realms;

import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.SimpleByteSource;

import com.deleidos.dmf.accessor.ServiceLayerAccessor;
import com.deleidos.dp.beans.User;
import com.deleidos.dp.exceptions.H2DataAccessException;

/**
 * An extension of Shiro's default JDBC realm. This class was customized to use
 * the Schema Wizard standard data access protocols.
 * 
 * @author yoonj1
 *
 */
public class H2Realm extends JdbcRealm {
	ServiceLayerAccessor sla = new ServiceLayerAccessor();
	private static Logger logger = Logger.getLogger(H2Realm.class);

	public H2Realm() {
		setSaltStyle(SaltStyle.COLUMN);
	}

	/**
	 * Authenticates a user with a given token. Passwords are encrypted with
	 * SHA-256 hashing.
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		UsernamePasswordToken upToken = (UsernamePasswordToken) token;
		String username = upToken.getUsername();
		SimpleAuthenticationInfo info = null;

		try {
			User user= sla.getPasswordForUser(username);

			// Null username is invalid
			if (username == null) {
				logger.error("Null usernames are not allowed.");
				throw new AccountException("Null usernames are not allowed by this realm.");
			}

			if (user.getPassword() == null) {
				logger.error("No account found for user: " + username + ".'");
				throw new UnknownAccountException("No account found for user [" + username + "]");
			}

			info = new SimpleAuthenticationInfo(username, user.getPassword(), getName());

			if (user.getSalt() != null) {
				info.setCredentialsSalt(new SimpleByteSource(user.getSalt()));
			}

			return info;
		} catch (H2DataAccessException e) { 
			logger.error(e.getMessage());
			throw new AuthorizationException("Error querying H2 for the user's password.");
		}
	}

	@Override
	/**
	 * Responsible for checking roles and permissions of a user (principal).
	 */
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		// null usernames are invalid
		if (principals == null) {
			throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
		}

		String username = (String) getAvailablePrincipal(principals);

		try {
			Set<String> roleNames = sla.getRoleNamesForUser(username);
			Set<String> permissions = sla.getPermissions(username, roleNames);

			SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(roleNames);
			info.setStringPermissions(permissions);

			return info;
		} catch (H2DataAccessException e) {
			logger.error(e.getMessage());
			throw new AuthorizationException("Error querying H2 for role names and permissions.");
		}
	}
}