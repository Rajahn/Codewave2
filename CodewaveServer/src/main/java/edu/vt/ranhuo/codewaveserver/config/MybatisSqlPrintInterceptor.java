package edu.vt.ranhuo.codewaveserver.config;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;

import java.sql.Connection;
import java.util.Properties;

@Intercepts({@Signature(
        type = StatementHandler.class,
        method = "prepare",
        args = {Connection.class, Integer.class})})
public class MybatisSqlPrintInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler handler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = handler.getBoundSql();
        String sql = boundSql.getSql();
        System.out.println("Executing SQL: " + sql);
        return invocation.proceed();
    }

    @Override
    public void setProperties(Properties properties) {
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }
}
