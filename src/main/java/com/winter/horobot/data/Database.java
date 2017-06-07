package com.winter.horobot.data;

import com.winter.horobot.Main;
import org.postgresql.ds.PGPoolingDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.HashMap;

public class Database {

	public static final Logger LOGGER = LoggerFactory.getLogger(Database.class);

	/**
	 * The connection pool
	 */
	private static PGPoolingDataSource poolingDataSource;

	/**
	 * Executes an update to the database
	 * @param sql String containing an SQL statement to be executed.
	 * @return true on success, false on failure
	 */
	public static boolean set(String sql, Object... params) {
		try (
				Connection con = poolingDataSource.getConnection();
				PreparedStatement statement = setStatementParams(con.prepareStatement(sql), params)
		) {
			statement.executeUpdate();
			return true;
		} catch (SQLException e) {
			LOGGER.error("Caught an SQL exception!", e);
		}
		return false;
	}

	/**
	 * Execute a query to the database
	 * @param sql The SQL query string following a prepared statement format
	 * @param params The parameters to fill the query with
	 * @return The resulting ResultSet
	 */
	public static HashMap<String, Object> get(String sql, Object... params) {
		HashMap<String, Object> results = new HashMap<>();
		try (
				Connection con = poolingDataSource.getConnection();
				PreparedStatement statement = setStatementParams(con.prepareStatement(sql), params);
				ResultSet set = statement.executeQuery()
		) {
			ResultSetMetaData md = set.getMetaData();
			int columns = md.getColumnCount();
			if (set.next())
				for (int i = 1; i <= columns; i++)
					results.put(md.getColumnName(i), set.getObject(i));
		} catch (SQLException e) {
			LOGGER.error("Caught an SQL exception!", e);
		}
		return results;
	}

	/**
	 * Sets the statement parameters for a prepared statement
	 * @param statement PreparedStatement with parameters that need to be set
	 * @param params Array of parameters that need to be set
	 * @throws SQLException Upon failing to set a parameter
	 */
	public static PreparedStatement setStatementParams(PreparedStatement statement, Object[] params) throws SQLException {
		for (int i = 0; i < params.length; i++) {
			if (params[i] instanceof String)
				statement.setString(i + 1, (String) params[i]);
			else if (params[i] instanceof Integer)
				statement.setInt(i + 1, (int) params[i]);
			else if (params[i] instanceof Boolean)
				statement.setBoolean(i + 1, (boolean) params[i]);
			else if (params[i] instanceof Long)
				statement.setLong(i + 1, (long) params[i]);
			else if (params[i] instanceof Double)
				statement.setDouble(i + 1, (double) params[i]);
		}
		return statement;
	}

	/**
	 * Fills up the connection pool with valid connections to the database
	 */
	public static void connect() {
		poolingDataSource = new PGPoolingDataSource();
		poolingDataSource.setDataSourceName("DataSource");
		poolingDataSource.setServerName("localhost");
		poolingDataSource.setDatabaseName("postgres");
		poolingDataSource.setUser("postgres");
		poolingDataSource.setPassword(Main.config.get(Main.ConfigValue.DB_PASS));
		poolingDataSource.setMaxConnections(1000);
	}

	/**
	 * Sets up the database for bot use, creating all schemas and tables required
	 * @return true upon successful setup and false on failure
	 */
	public static boolean setup() {
		// TODO: Create all schemas and tables that are used by the bot here
		return true;
	}
}