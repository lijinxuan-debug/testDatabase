package org.example;

import org.example.utils.JdbcUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Sql {
    public static void main(String[] args) {
        String sql = "SELECT g.course_id, c.course_name, ROUND(AVG(g.score), 2) as average " +
                "FROM grades g " +
                "JOIN courses c ON c.course_id = g.course_id " +
                "GROUP BY g.course_id, c.course_name " +
                "HAVING AVG(g.score) > 80";

        // 直接通过工具类获取连接
        try (Connection con = JdbcUtil.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                System.out.printf("课程ID：%s，名称：%s，平均分：%.2f%n",
                        rs.getString("course_id"),
                        rs.getString("course_name"),
                        rs.getDouble("average"));
            }

            if (!hasData) {
                System.out.println("查询成功，但当前没有平均分超过80的课程。");
            }

        } catch (SQLException e) {
            // 打印详细错误，方便调试
            System.err.println("数据库操作失败：" + e.getMessage());
        }
    }
}
