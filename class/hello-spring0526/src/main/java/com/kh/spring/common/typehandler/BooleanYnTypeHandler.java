package com.kh.spring.common.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(Boolean.class)
@MappedJdbcTypes(JdbcType.CHAR)
public class BooleanYnTypeHandler extends BaseTypeHandler<Boolean> {

	/*
	 * boolean ------> YN 이렇게 바꾸는 작없공간 
	 */
	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Boolean parameter, JdbcType jdbcType)
			throws SQLException {
		ps.setString(i, parameter ? "Y" : "N"); // parameter에 불린이 넘어온다
	}
	
	/*
	 * YN------> boolean 이렇게 바꾸는 작없공간 
	 */
	@Override
	public Boolean getNullableResult(ResultSet rs, String columnName) throws SQLException {
		//return rs.getString(columnName).equals("Y"); //이건 널뜰수도 있어서 별로//Y라면 true n이라면 true로 바꿔주는곳
		return "Y".equals(rs.getString(columnName));
	}

	/*
	 * YN------> boolean 이렇게 바꾸는 작없공간 
	 */
	@Override
	public Boolean getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return "Y".equals(rs.getString(columnIndex));
	}

	/*
	 * YN------> boolean 이렇게 바꾸는 작없공간 
	 */
	@Override
	public Boolean getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return "Y".equals(cs.getString(columnIndex));
	}

}
