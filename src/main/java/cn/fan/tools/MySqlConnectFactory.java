package cn.fan.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
 * 数据库连接工厂
 */
public class MySqlConnectFactory {
	private static final String driver = "com.mysql.jdbc.Driver";
	// URL指向要访问的数据库名mydata
	private static final String url = "jdbc:mysql://10.141.221.85:3306/code_graph";
	// MySQL配置时的用户名
	private static final String user = "root";
	// MySQL配置时的密码
	private static final String password = "root";

	public static Connection getConnection() {
		Connection con = null;
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return con;
	}

	public static void main(String[] args) {
		Connection connection = getConnection();
		System.out.println(connection);
	}
}
