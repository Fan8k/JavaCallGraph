package cn.fan.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import cn.fan.interfaces.CGInfoDaoI;
import cn.fan.model.CG_Info;

public class CGDao implements CGInfoDaoI {

	private Connection connection;

	public CGDao(Connection connection) {
		super();
		this.connection = connection;
	}

	@Override
	public boolean insertOne(CG_Info cg_Info) {
		// TODO Auto-generated method stub
		String sql = "insert into call_graph_info(source_method,source_method_dot_name,target_method,target_method_dot_name,line_number,project_name) values (?,?,?,?,?,?)";
		PreparedStatement prepareStatement = null;
		boolean flag = false;
		try {
			prepareStatement = connection.prepareStatement(sql);
			prepareStatement.setString(1, cg_Info.getSource_method().replaceAll("\\s+", "").trim());
			prepareStatement.setString(2, cg_Info.getSource_method_dot_name().replaceAll("\\s+", "").trim());
			prepareStatement.setString(3, cg_Info.getTarget_method().replaceAll("\\s+", "").trim());
			prepareStatement.setString(4, cg_Info.getTarget_method_dot_name().replaceAll("\\s+", "").trim());
			prepareStatement.setString(5, cg_Info.getLine_number().replaceAll("\\s+", "").trim());
			prepareStatement.setString(6, cg_Info.getProject_name().replaceAll("\\s+", "").trim());
			prepareStatement.execute();
			flag = true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				prepareStatement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return flag;
	}

}
