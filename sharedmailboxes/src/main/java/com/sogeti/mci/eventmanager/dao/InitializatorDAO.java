package com.sogeti.mci.eventmanager.dao;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.dbcp2.BasicDataSource;


/**
 * This class initializes the datasource for MySQL and allocates some connections
 * @author
 *
 */


public class InitializatorDAO {
	
	
	/**
	 * The datasource onto MYSQL Database
	 */
	private BasicDataSource ds;
	
	/**
	 * The URL to connect to MySQL Database
	 */
	private String url;
		
	/**
	 * The username for connecting to MySQL Database
	 */
	private String username;
	
	/**
	 * The password for connecting to MySQL Database
	 */
	private String password;
	
	/**
	 * The driver for connecting to MySQL Database
	 */
	private String driver;
	
	
	private static InitializatorDAO instance = null;
	
	
	public static InitializatorDAO getInstance() {
		if(instance == null) {
			synchronized (InitializatorDAO.class){
				if(instance == null) {
					instance = new InitializatorDAO();
				}
			}
	    }
	    return instance;
	}
	
	protected InitializatorDAO()  {	
		try {
			Properties prop = new Properties();
			prop.load(this.getClass().getResourceAsStream("/config-db.properties"));
			driver 		= prop.getProperty("jdbc.driver");
			username 	= prop.getProperty("jdbc.username");
			password 	= prop.getProperty("jdbc.password");
			url  		= prop.getProperty("jdbc.url");
		} catch (Exception e){
			System.out.println("Unable to initialize database. The file config-db.properties is incorrect or it cannot be accessed");
			System.out.println(LoggerDAO.getStackTrace(e));
		}
		ds = new BasicDataSource();
		ds.setDriverClassName(driver);
		ds.setUsername(username);
		ds.setPassword(password);
		ds.setUrl(url);
		System.out.println("DB settings: \ndriver: " + driver + "\nusername: " + username + "\npassword: " + password + "\nurl: " + url);
	}
	
	
	/**
	 * Get the datasource
	 * @return one connection onto MySQL
	 * @throws ApplicationException
	 */
	public Connection getConnection() {
		Connection connection = null;
		try {
			connection = ds.getConnection();
		} catch (SQLException e) {
			System.out.println("Unable to initialize the connection to Database");
			System.out.println(LoggerDAO.getStackTrace(e));
		}
		return connection;
	}
	
}