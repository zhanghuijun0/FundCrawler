package com.evwan.zhj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

/**
 * 用于完成最基本的数据库操作
 * 
 * @author ZhangHuijun
 *
 */
public class DataBaseUnit {
	private String driver;
	private String url;
	private String user;
	private String pass;

	public static void main(String[] args) {
		DataBaseUnit mBaseUnit = new DataBaseUnit();
		mBaseUnit.execCall("CALL check_institution('shilili','院系',?);");
	}

	/**
	 * 初始化数据库配置文件
	 * 
	 * @param paramFile
	 *            文件的路径
	 */
	public void initParam(String paramFile) {
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(paramFile));
			driver = props.getProperty("driver");
			url = props.getProperty("url");
			user = props.getProperty("user");
			pass = props.getProperty("pass");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 调用存储过程
	 * 
	 * @param proceduce
	 *            "CALL check_institution('张慧俊','学校',?);"
	 * @return id
	 */
	public int execCall(String proceduce) {
		initParam("mysql.conf");
		int id = -1;
		new DataBaseUnit();
		CallableStatement proc = null;
		Connection conn = null;
		try {
			Class.forName(driver);// 加载数据库驱动
			// 与数据库建立连接
			conn = DriverManager.getConnection(url, user, pass);
			proc = conn.prepareCall(proceduce);
			proc.registerOutParameter(1, Types.DISTINCT);
			proc.execute();
			id = proc.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		return id;
	}

	/**
	 * 调用存储过程查询各个表中的数据条数
	 * 
	 * @param sql
	 * @return fund、fund_dailyprice、fund_type、institution的数据的条数
	 */
	public int[] callProceduceToShowCount(String sql) {
		int[] result = new int[4];
		initParam("mysql.conf");
		try {
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, user, pass);
			CallableStatement cstmt = conn.prepareCall(sql);
			for (int i = 1; i <= result.length; i++) {
				cstmt.registerOutParameter(i, Types.INTEGER);// 注册输出参数的类型
			}
			cstmt.execute();// 执行存储过程
			for (int i = 1; i <= result.length; i++) {
				result[i - 1] = cstmt.getInt(i);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

}
