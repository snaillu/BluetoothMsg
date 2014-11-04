package com.keda.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DbHelper {
	public static Connection getConnection(){
		//String dbPath = "D:/Code/QtWorkSpace/King_1014/PVASMOBILE_VOB/10-common/version/KING/searchrecfile.db";
		String dbPath = "C:/KING/searchrecfile.db";
		Connection conn = null;
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:"+dbPath);
			System.out.println("get connection success.");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception: can't get connection.");
		}
		return conn;
	}
	
	public static boolean execSql(String sql){
		Connection conn = getConnection();
		boolean result = false;
		
		try {
			Statement stmt = conn.createStatement();
			result = stmt.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static void main(String[] args) {
		String sql = "update configinfo set loginName = 'snail' where configid=1";
		execSql(sql);
	}
}
