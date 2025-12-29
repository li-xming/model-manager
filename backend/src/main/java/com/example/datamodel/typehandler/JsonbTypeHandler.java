package com.example.datamodel.typehandler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * JSONB类型处理器
 * 用于PostgreSQL JSONB类型与Java String类型的转换
 *
 * @author DataModel Team
 */
@MappedTypes(String.class)
@MappedJdbcTypes(JdbcType.OTHER)
public class JsonbTypeHandler extends BaseTypeHandler<String> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        // 使用PostgreSQL的PGobject来明确指定jsonb类型
        PGobject jsonObject = new PGobject();
        jsonObject.setType("jsonb");
        jsonObject.setValue(parameter != null ? parameter : "{}");
        ps.setObject(i, jsonObject);
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object obj = rs.getObject(columnName);
        if (obj == null) {
            return null;
        }
        if (obj instanceof PGobject) {
            return ((PGobject) obj).getValue();
        }
        return obj.toString();
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object obj = rs.getObject(columnIndex);
        if (obj == null) {
            return null;
        }
        if (obj instanceof PGobject) {
            return ((PGobject) obj).getValue();
        }
        return obj.toString();
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object obj = cs.getObject(columnIndex);
        if (obj == null) {
            return null;
        }
        if (obj instanceof PGobject) {
            return ((PGobject) obj).getValue();
        }
        return obj.toString();
    }
}

