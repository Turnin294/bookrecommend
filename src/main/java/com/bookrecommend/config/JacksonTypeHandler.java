package com.bookrecommend.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JacksonTypeHandler<T> extends BaseTypeHandler<T> {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final Class<T> type;

    public JacksonTypeHandler(Class<T> type) {
        if (type == null) throw new IllegalArgumentException("Type argument cannot be null");
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        try {
            ps.setString(i, objectMapper.writeValueAsString(parameter));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parse(rs.getString(columnName));
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parse(rs.getString(columnIndex));
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parse(cs.getString(columnIndex));
    }

    private T parse(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            // 这里为了简单，处理 List<String> 或 List<Long> 等常见情况
            // 如果有更复杂的嵌套，可能需要更精细的 TypeReference 处理
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
