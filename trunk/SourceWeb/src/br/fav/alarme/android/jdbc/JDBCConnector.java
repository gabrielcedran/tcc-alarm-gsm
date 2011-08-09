package br.fav.alarme.android.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCConnector {

	private static Connection conn = null;
	
	public Connection getConnection() {
		if(conn == null) {
			try {
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/alarmeandroiddb?user=root&password=root");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		}
		return conn;
    	//Statement stm = conn.createStatement();  
    	//ResultSet rs = stm.executeQuery("SELECT * FROM cliente");  
	}
	
	public void closeConnection() {
		if(conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}  
		}
	}
}
