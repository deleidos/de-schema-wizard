package com.deleidos.dp.h2;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.deleidos.dp.exceptions.H2DataAccessException;
import com.deleidos.hd.h2.H2Database;

public class H2FunctionRunner {
	private static final Logger logger = Logger.getLogger(H2FunctionRunner.class);
	private H2Database h2Database;

	public H2FunctionRunner(H2Database h2Database) {
		this.h2Database = h2Database;
	}

	/**
	 * Remove all files in the database directory with the database name.
	 */
	public void purge() {
		h2Database.purge();
	}

	public <T> T runConnectionFunction(FunctionWithConnection<T> connFunc, String errorMessage) throws H2DataAccessException {
		try (Connection conn = h2Database.getNewConnection()) {
			try {
				conn.setAutoCommit(false);
				T result = connFunc.runFunctionWithConnection(conn);
				conn.commit();
				return result;
			} catch (Exception e) {
				// probably a query/database error, but catch all
				conn.rollback();
				logger.error(errorMessage);
				logger.error(e.getMessage().toString());
				logger.debug(errorMessage, e);
				throw new H2DataAccessException(e.getMessage(), e);
			} 
		} catch (H2DataAccessException h2Exception) { 
			// rethrow the exception from above
			throw h2Exception;
		} catch (Exception e) {
			// could also be a more high level error (i.e. connection management), convert to H2DAOE
			logger.error(errorMessage);
			logger.error(e.getMessage().toString());
			logger.debug(errorMessage, e);
			throw new H2DataAccessException(e.getMessage(), e);
		}
	}

	public interface FunctionWithConnection<T> {
		public T runFunctionWithConnection(Connection conn) throws H2DataAccessException, SQLException;
	}

}
