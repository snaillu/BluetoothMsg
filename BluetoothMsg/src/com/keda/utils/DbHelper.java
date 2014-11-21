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
		int count = 0;
		
		try {
			Statement stmt = conn.createStatement();
			count = stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count>0;
	}
	
	public static void main(String[] args) {
		String sql = "update configinfo set loginName = '' where configid=1";
		boolean result = execSql(sql);
		System.out.println("result="+result);
		
	}
}
