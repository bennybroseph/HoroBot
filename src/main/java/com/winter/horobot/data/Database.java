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
				PreparedStatement statement = setStatementParams(con, con.prepareStatement(sql), params)
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
				PreparedStatement statement = setStatementParams(con, con.prepareStatement(sql), params);
				ResultSet set = statement.executeQuery()
		) {
			ResultSetMetaData md = set.getMetaData();
			int columns = md.getColumnCount();
			if (set.next()) {
				for (int i = 1; i <= columns; i++) {
					if (set.getObject(i) instanceof Array) {
						results.put(md.getColumnName(i), set.getArray(i).getArray());
					} else {
						results.put(md.getColumnName(i), set.getObject(i));
					}
				}
			}
		} catch (SQLException e) {
			LOGGER.error("Caught an SQL exception!", e);
		}
		return results;
	}

	/**
	 * This method should not be used during normal operation
	 * @param sql The sql to execute
	 * @return True on success false on failure
	 */
	private static boolean executeUnsafe(String sql) {
		try (
				Connection con = poolingDataSource.getConnection();
				Statement statement = con.createStatement()
		) {
			statement.executeUpdate(sql);
			return true;
		} catch (SQLException e) {
			LOGGER.error("Caught an SQL exception!", e);
		}
		return false;
	}

	/**
	 * Sets the statement parameters for a prepared statement
	 * @param statement PreparedStatement with parameters that need to be set
	 * @param params Array of parameters that need to be set
	 * @throws SQLException Upon failing to set a parameter
	 */
	private static PreparedStatement setStatementParams(Connection con, PreparedStatement statement, Object[] params) throws SQLException {
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
			else if (params[i] instanceof String[])
				statement.setArray(i + 1, con.createArrayOf("text", (String[]) params[i]));
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
		return (executeUnsafe("CREATE SCHEMA IF NOT EXISTS guilds;")) &&
				(executeUnsafe("CREATE TABLE IF NOT EXISTS guilds.guild(" +
				"id TEXT PRIMARY KEY NOT NULL," +
				"language TEXT DEFAULT 'en'," +
				"prefixes TEXT[10]," +
				"autoroles TEXT[3]," +
				"welcome TEXT DEFAULT 'none'," +
				"role TEXT DEFAULT 'none'," +
				"pm TEXT DEFAULT 'none'," +
				"lvlup BOOLEAN DEFAULT true," +
				"bpresentban BOOLEAN DEFAULT true," +
				"bignore BOOLEAN DEFAULT false);")) &&

				(executeUnsafe("CREATE SCHEMA IF NOT EXISTS wolves;")) &&
				(executeUnsafe("CREATE TABLE IF NOT EXISTS wolves.wolf(" +
				"id TEXT PRIMARY KEY NOT NULL," +
				"name TEXT NOT NULL DEFAULT 'Wolf'," +
				"level INTEGER NOT NULL DEFAULT 1," +
				"hunger INTEGER NOT NULL DEFAULT 0," +
				"maxHunger INTEGER NOT NULL DEFAULT 2," +
				"fedTimes INTEGER NOT NULL DEFAULT 0," +
				"background TEXT DEFAULT NULL," +
				"hat TEXT DEFAULT NULL," +
				"body TEXT DEFAULT NULL," +
				"paws TEXT DEFAULT NULL," +
				"tail TEXT DEFAULT NULL," +
				"shirt TEXT DEFAULT NULL," +
				"nose TEXT DEFAULT NULL," +
				"eye TEXT DEFAULT NULL," +
				"neck TEXT DEFAULT NULL);")) &&

				(executeUnsafe("CREATE SCHEMA IF NOT EXISTS blacklists;")) &&
				(executeUnsafe("CREATE TABLE IF NOT EXISTS blacklists.blacklist (" +
				"id TEXT PRIMARY KEY NOT NULL," +
				"userID TEXT NOT NULL);")) &&

				(executeUnsafe("CREATE SCHEMA IF NOT EXISTS tags;")) &&
				(executeUnsafe("CREATE TABLE IF NOT EXISTS tags.tag(" +
				"id TEXT NOT NULL," +
				"tag TEXT NOT NULL," +
				"content TEXT NOT NULL," +
				"PRIMARY KEY (id, tag));")) &&

				(executeUnsafe("CREATE SCHEMA IF NOT EXISTS users;")) &&
				(executeUnsafe("CREATE TABLE IF NOT EXISTS users.user(" +
				"id TEXT PRIMARY KEY NOT NULL," +
				"description TEXT NOT NULL DEFAULT 'I like trains'," +
				"level INTEGER NOT NULL DEFAULT 1," +
				"xp INTEGER NOT NULL DEFAULT 0," +
				"maxXp INTEGER NOT NULL DEFAULT 300," +
				"foxCoins INTEGER NOT NULL DEFAULT 0," +
				"background TEXT NOT NULL DEFAULT 'NONE0'," +
				"notifications BOOLEAN NOT NULL DEFAULT false);")) &&
				(executeUnsafe("CREATE TABLE IF NOT EXISTS users.item(" +
				"id TEXT NOT NULL," +
				"item TEXT NOT NULL," +
				"PRIMARY KEY (id, item));"));
	}
}