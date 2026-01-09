package org.example.server;

import org.json.JSONObject;
import java.sql.PreparedStatement;

// 函数式接口，简化 JDBC 映射操作
@FunctionalInterface
interface JdbcTask {
    void map(JSONObject json, PreparedStatement ps) throws Exception;
}