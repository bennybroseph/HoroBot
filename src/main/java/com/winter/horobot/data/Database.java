package com.winter.horobot.data;

import com.winter.horobot.Main;
import com.winter.horobot.exceptions.NoResultsException;
import org.postgresql.ds.PGPoolingDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Database {

	public static final Logger LOGGER = LoggerFactory.getLogger(Database.class);

	/**
	 * The connection pool
	 */
	private static PGPoolingDataSource poolingDataSource;

	/**
	 * Executes an update to the database
	 * @param sql String containing an SQL statement to be executed.
	 */
	public static void set(String sql, Object... params) {
		try (Connection con = poolingDataSource.getConnection(); PreparedStatement statement = con.prepareStatement(sql)) {
			setStatementParams(statement, params);
			statement.executeUpdate();
		} catch (SQLException e) {
			LOGGER.error("Caught an SQLException!", e);
		}
	}

	/**
	 * Execute a query to the database
	 * @param sql The SQL query string following a prepared statement format
	 * @param params The parameters to fill the query with
	 * @return The resulting ResultSet
	 */
	public static HashMap<String, Object> get(String sql, Object... params) {
		HashMap<String, Object> results = null;
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet set = null;
		try (Connection con = poolingDataSource.getConnection(); PreparedStatement statement = con.prepareStatement(sql)) {
			setStatementParams(statement, params);
			set = statement.executeQuery();
			ResultSetMetaData md = set.getMetaData();
			int columns = md.getColumnCount();
			if (set.next())
				for (int i = 1; i <= columns; i++)
					results.put(md.getColumnName(i), set.getObject(i));
		} catch (SQLException e) {
			LOGGER.error("Caught an SQLException!", e);
		} finally {
			if (set != null) {
				try {
					set.close();
				} catch (SQLException ignored) {
				}
			}
		}
		return results;
	}

	// TODO GetOrElse

	public static void setStatementParams(PreparedStatement statement, Object[] params) throws SQLException {
		for (int i = 1; i <= params.length; i++) {
			if (params[i] instanceof String)
				statement.setString(i, (String) params[i]);
			else if (params[i] instanceof Integer)
				statement.setInt(i, (int) params[i]);
			else if (params[i] instanceof Boolean)
				statement.setBoolean(i, (boolean) params[i]);
			else if (params[i] instanceof Long)
				statement.setLong(i, (long) params[i]);
			else if (params[i] instanceof Double)
				statement.setDouble(i, (double) params[i]);
		}
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
}