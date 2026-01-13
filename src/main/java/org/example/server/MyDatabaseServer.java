package org.example.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.example.utils.JdbcUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MyDatabaseServer {

    public static void main(String[] args) throws IOException {
        // 1. 创建服务器，监听 8080 端口
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // 2. 注册路由，分别处理学生、课程、成绩
        server.createContext("/api/students", new StudentHandler());
        server.createContext("/api/courses", new CourseHandler());
        server.createContext("/api/grades", new GradeHandler());

        // 3. 关键：设置线程池，处理来自客户端的并发 HTTP 请求
        ThreadPoolExecutor executor = new ThreadPoolExecutor(5,10,30, TimeUnit.SECONDS,new ArrayBlockingQueue<>(100));
        server.setExecutor(executor);

        System.out.println("[Server] 数据库后端已启动，正在监听 8080 端口...");
        server.start();
    }

    // --- 处理器 1：处理学生数据 ---
    private static class StudentHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            processRequest(exchange, "INSERT INTO students (student_id, name, gender, birth_date, class_name) VALUES (?, ?, ?, ?, ?)", (json, ps) -> {
                ps.setInt(1, json.getInt("student_id"));
                ps.setString(2, json.getString("name"));
                ps.setString(3, json.getString("gender"));
                ps.setString(4, json.getString("birth_date"));
                ps.setString(5, json.getString("class_name"));
            });
        }
    }

    // --- 处理器 2：处理课程数据 ---
    private static class CourseHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            processRequest(exchange, "INSERT INTO courses (course_id, course_name, credits, teacher) VALUES (?, ?, ?, ?)", (json, ps) -> {
                ps.setString(1, json.getString("course_id"));
                ps.setString(2, json.getString("course_name"));
                ps.setInt(3, json.getInt("credits"));
                ps.setString(4, json.getString("teacher"));
            });
        }
    }

    // --- 处理器 3：处理成绩数据 ---
    private static class GradeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            processRequest(exchange, "INSERT INTO grades (student_id, course_id, score, exam_date) VALUES (?, ?, ?, ?)", (json, ps) -> {
                ps.setInt(1, json.getInt("student_id"));
                ps.setString(2, json.getString("course_id"));
                ps.setDouble(3, json.getDouble("score"));
                ps.setString(4, json.getString("exam_date"));
            });
        }
    }

    private static void processRequest(HttpExchange exchange, String sql, JdbcTask task) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");

        // 模拟：如果 Token 不是 "my-secret-token"，就认为是非法访问
        if (authHeader == null || !authHeader.equals("Bearer my-secret-token")) {
            System.err.println("[Auth] 拦截到非法请求：Token 缺失或错误");
            sendResponse(exchange, 401, "Unauthorized: Invalid or missing token");
            return;
        }


        try (InputStream is = exchange.getRequestBody();
             Connection conn = JdbcUtil.getConnection()) {

            String body = readStream(is);

            JSONArray jsonArray = new JSONArray(body);

            // 开启事务（保证这一批数据要么全成，要么全败）
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);

                    task.map(item, ps);
                    // 执行插入
                    ps.executeUpdate();
                }
                conn.commit(); // 提交事务

                sendResponse(exchange, 200, "Bulk Insert Success: " + jsonArray.length());
            } catch (Exception e) {
                conn.rollback(); // 出错回滚
                throw e;
            }

        } catch (Exception e) {
            System.err.println("处理失败: " + e.getMessage());
            sendResponse(exchange, 500, "Error: " + e.getMessage());
        }
    }

    public static String readStream(InputStream is) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            System.err.println("IO异常");
        }
        return sb.toString().trim();
    }

    private static void sendResponse(HttpExchange exchange, int code, String text) throws IOException {
        byte[] resp = text.getBytes();
        exchange.sendResponseHeaders(code, resp.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(resp);
        }
    }
}