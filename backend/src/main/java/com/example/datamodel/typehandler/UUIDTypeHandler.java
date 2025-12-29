package com.example.datamodel.typehandler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * UUID类型处理器
 * 用于PostgreSQL UUID类型与Java UUID类型的转换
 * 支持带或不带连字符的UUID字符串格式
 *
 * @author DataModel Team
 */
@MappedTypes(UUID.class)
@MappedJdbcTypes({JdbcType.OTHER, JdbcType.VARCHAR, JdbcType.CHAR})
public class UUIDTypeHandler extends BaseTypeHandler<UUID> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, UUID parameter, JdbcType jdbcType) throws SQLException {
        if (jdbcType == null || jdbcType == JdbcType.OTHER) {
            // PostgreSQL UUID类型
            ps.setObject(i, parameter, java.sql.Types.OTHER);
        } else {
            // 字符串类型
            ps.setString(i, parameter.toString());
        }
    }

    @Override
    public UUID getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return convertToUUID(rs.getObject(columnName));
    }

    @Override
    public UUID getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return convertToUUID(rs.getObject(columnIndex));
    }

    @Override
    public UUID getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return convertToUUID(cs.getObject(columnIndex));
    }

    /**
     * 将对象转换为UUID
     * 支持UUID对象、字符串（带或不带连字符）的转换
     * 
     * @param obj 待转换的对象
     * @return UUID对象，如果obj为null则返回null
     */
    private UUID convertToUUID(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof UUID) {
            return (UUID) obj;
        }
        String str = obj.toString().trim();
        // 处理不带连字符的32位十六进制字符串（如：7a443f8decb62db82b9f937be73848a5）
        if (str.length() == 32 && str.matches("[0-9a-fA-F]{32}")) {
            str = str.substring(0, 8) + "-" + str.substring(8, 12) + "-" + str.substring(12, 16) + "-" + str.substring(16, 20) + "-" + str.substring(20);
        }
        try {
            return UUID.fromString(str);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("无法将字符串转换为UUID: " + str, e);
        }
    }
}

