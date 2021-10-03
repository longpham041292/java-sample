package asia.cmg.f8.profile.partner.leep_web;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.beans.factory.annotation.Autowired;

import asia.cmg.f8.profile.config.LeepWebConfiguration;


public class LeepWebConnection {

	private static String DBDriver = "org.gjt.mm.mysql.Driver";
	private Connection connection = null;

	public LeepWebConnection(LeepWebConfiguration config) {
		try {
			Class.forName(DBDriver);
			connection = DriverManager.getConnection(config.getDatabaseUrl(), config.getDatabaseUser(), config.getDatabasePassword());
			connection.setAutoCommit(false);
		} catch (Exception ex) {
			// Logging
		}
	}
	
	public ResultSet executeQuery(final String query) throws Exception {
		try {
			if(connection == null || connection.isClosed()) {
				throw new Exception("DB Connection is null or closed");
			}
			Statement statement = connection.createStatement();
			return statement.executeQuery(query);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public ResultSet executeQuery(final String query, final int page, final int size) throws Exception {
		try {
			if(connection == null || connection.isClosed()) {
				throw new Exception("DB Connection is null or closed");
			}
			
			String limitQuery = query.concat(" ").concat(String.format("LIMIT %s, %s", page*size, size));
			
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(limitQuery);
			
			return resultSet;
			
		} catch (Exception e) {
			throw e;
		}
	}
	
	public int executeCountQuery(final String query) throws Exception {
		try {
			if(connection == null || connection.isClosed()) {
				throw new Exception("DB Connection is null or closed");
			}
			
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			int count = resultSet.getInt(0);
			
			return count;
		} catch (Exception e) {
			throw e;
		}
	}

	public void close() {	
		try {
			if(connection != null) {
				connection.close();
			}
		} catch (Exception ex) {
		}
	}

	public void commit() throws Exception {
		try {
			connection.commit();
		} catch (Exception ex) {
			throw ex;
		}
	}

	public void rollback() throws Exception {
		try {
			connection.rollback();
		} catch (Exception ex) {
			throw ex;
		}
	}
}
