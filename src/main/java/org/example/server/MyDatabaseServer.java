package org.example.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.example.utils.JdbcUtil;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.nio.charset.StandardCharsets;

public class MyDatabaseServer {

    public static void main(String[] args) throws Exception {
        // 1. 创建服务器，监听 8080 端口
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // 2. 为三个不同的请求路径设置处理器
        server.createContext("/updateStudent", new UpdateHandler("student"));
        server.createContext("/updateCourse", new UpdateHandler("course"));
        server.createContext("/updateGrade", new UpdateHandler("grade"));

        server.setExecutor(null); // 使用默认执行器
        System.out.println(">>> 服务端已启动，监听端口: 8080");
        System.out.println(">>> 等待客户端发送 JSON 请求...");
        server.start();
    }

    // 统一的处理器类
    static class UpdateHandler implements HttpHandler {
        private final String type;

        public UpdateHandler(String type) {
            this.type = type;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // 只处理 POST 请求
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, "请使用 POST 方法", 405);
                return;
            }

            // 1. 读取客户端发来的 JSON 字符串
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            String jsonStr = sb.toString();
            System.out.println("收到 " + type + " 更新请求: " + jsonStr);

            // 2. 解析 JSON 并操作数据库
            boolean success = false;
            try {
                JSONObject json = new JSONObject(jsonStr);
                success = executeDatabaseUpdate(json);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 3. 返回处理结果
            if (success) {
                sendResponse(exchange, "Update Success!", 200);
            } else {
                sendResponse(exchange, "Update Failed!", 500);
            }
        }

        // 核心：根据不同类型执行不同的 SQL
        private boolean executeDatabaseUpdate(JSONObject json) throws Exception {
            // 注意：这里调用你之前封装的 JdbcUtils
            try (Connection conn = JdbcUtil.getConnection()) {
                String sql;
                PreparedStatement pstmt = null;

                switch (type) {
                    case "student":
                        sql = "UPDATE students SET name=?, class_name=? WHERE student_id=?";
                        pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1, json.getString("name"));
                        pstmt.setString(2, json.getString("class_name"));
                        pstmt.setInt(3, json.getInt("student_id"));
                        break;
                    case "course":
                        sql = "UPDATE courses SET course_name=?, teacher=? WHERE course_id=?";
                        pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1, json.getString("course_name"));
                        pstmt.setString(2, json.getString("teacher"));
                        pstmt.setString(3, json.getString("course_id"));
                        break;
                    case "grade":
                        // 成绩更新通常基于 学生ID 和 课程ID
                        sql = "UPDATE grades SET score=? WHERE student_id=? AND course_id=?";
                        pstmt = conn.prepareStatement(sql);
                        pstmt.setBigDecimal(1, json.getBigDecimal("score"));
                        pstmt.setInt(2, json.getInt("student_id"));
                        pstmt.setString(3, json.getString("course_id"));
                        break;
                }

                if (pstmt != null) {
                    int rows = pstmt.executeUpdate();
                    return rows > 0;
                }
            }
            return false;
        }

        private void sendResponse(HttpExchange exchange, String resp, int code) throws IOException {
            byte[] bytes = resp.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(code, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }
}