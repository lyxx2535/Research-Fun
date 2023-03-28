package nju.researchfun.handler;

import nju.researchfun.constant.ScheduleState;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedJdbcTypes(JdbcType.INTEGER)
@MappedTypes(ScheduleState.class)
public class EnumHandler extends BaseTypeHandler<ScheduleState> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, ScheduleState scheduleState, JdbcType jdbcType) throws SQLException {
        preparedStatement.setInt(i, scheduleState.getValue());
    }

    @Override
    public ScheduleState getNullableResult(ResultSet resultSet, String s) throws SQLException {
        //根据字段名获取枚举值
        int value = resultSet.getInt(s);
        return ScheduleState.parse(value);
    }

    @Override
    public ScheduleState getNullableResult(ResultSet resultSet, int i) throws SQLException {
        //根据字段名获取枚举值
        int value = resultSet.getInt(i);
        return ScheduleState.parse(value);
    }

    @Override
    public ScheduleState getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return null;
    }
}
