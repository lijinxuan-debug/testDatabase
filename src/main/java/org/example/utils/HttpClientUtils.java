package org.example.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class HttpClientUtils {

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(5,10,30, TimeUnit.SECONDS,new ArrayBlockingQueue<>(100));
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n======= 学生管理系统 (客户端) =======");
            System.out.println("1. 批量同步学生数据到服务器");
            System.out.println("2. 批量同步课程数据到服务器");
            System.out.println("3. 批量同步成绩数据到服务器");
            System.out.println("4. 查看本地学生JSON信息");
            System.out.println("5. 查看本地课程JSON信息");
            System.out.println("6. 查看本地成绩JSON信息");
            System.out.println("0. 退出系统");
            System.out.print("请选择操作: ");

            String choice = scanner.nextLine();
            switch (choice) {
                // 同步学生
                case "1":
                    syncDataGeneric("学生", "src/main/resources/data/students.json", "http://localhost:8080/api/students");
                    break;
                case "2":
                    syncDataGeneric("课程", "src/main/resources/data/courses.json", "http://localhost:8080/api/courses");
                    break;
                case "3":
                    syncDataGeneric("成绩", "src/main/resources/data/grades.json", "http://localhost:8080/api/grades");
                    break;
                case "4":
                    System.out.println("\n[ 正在加载本地学生信息... ]");
                    showStudents();
                    break;
                case "5":
                    System.out.println("\n[ 正在加载本地课程信息... ]");
                    showCourses();
                    break;
                case "6":
                    System.out.println("\n[ 正在加载本地成绩信息... ]");
                    showGrades();
                    break;
                case "0":
                    System.out.println("退出中...");
                    executor.shutdown();
                    return;
                default:
                    System.out.println("无效选择，请重新输入。");
            }
        }
    }

    private static void showStudents() {
        try {
            JSONArray array = JsonUtil.readArrayFromFile("src/main/resources/data/students.json");
            String divider = "----------------------------------------------------------------------";
            System.out.println("\n" + divider);
            System.out.printf("%-10s | %-12s | %-4s | %-10s | %-12s\n", "学号", "姓名", "性别", "出生日期", "班级");
            System.out.println(divider);

            for (int i = 0; i < array.length(); i++) {
                JSONObject s = array.getJSONObject(i);
                System.out.printf("%-10d | %-12s | %-4s | %-10s | %-12s\n",
                        s.getInt("student_id"),
                        s.getString("name"),
                        s.getString("gender"),
                        s.getString("birth_date"),
                        s.getString("class_name"));
            }
            System.out.println(divider);
        } catch (Exception e) {
            System.err.println("学生数据解析失败，请检查字段名: " + e.getMessage());
        }
    }

    private static void showCourses() {
        try {
            JSONArray array = JsonUtil.readArrayFromFile("src/main/resources/data/courses.json");
            String divider = "------------------------------------------------------------";
            System.out.println("\n" + divider);
            System.out.printf("%-10s | %-12s | %-6s | %-10s\n", "课程号", "课程名称", "学分", "授课老师");
            System.out.println(divider);

            for (int i = 0; i < array.length(); i++) {
                JSONObject c = array.getJSONObject(i);
                System.out.printf("%-10s | %-12s | %-6d | %-10s\n",
                        c.getString("course_id"),
                        c.getString("course_name"),
                        c.getInt("credits"),
                        c.getString("teacher"));
            }
            System.out.println(divider);
        } catch (Exception e) {
            System.err.println("课程数据解析失败: " + e.getMessage());
        }
    }

    private static void showGrades() {
        try {
            JSONArray array = JsonUtil.readArrayFromFile("src/main/resources/data/grades.json");
            String divider = "------------------------------------------------------------";
            System.out.println("\n" + divider);
            System.out.printf("%-8s | %-10s | %-10s | %-8s | %-10s\n", "ID", "学号", "课程号", "得分", "考试日期");
            System.out.println(divider);

            for (int i = 0; i < array.length(); i++) {
                JSONObject g = array.getJSONObject(i);
                System.out.printf("%-8d | %-10d | %-10s | %-8.2f | %-10s\n",
                        g.getInt("grade_id"),
                        g.getInt("student_id"),
                        g.getString("course_id"),
                        g.getDouble("score"),
                        g.getString("exam_date"));
            }
            System.out.println(divider);
        } catch (Exception e) {
            System.err.println("成绩数据解析失败: " + e.getMessage());
        }
    }

    /**
     * 通用同步方法
     * @param dataName 数据名称（如：学生、课程、成绩）
     * @param filePath 本地文件路径
     * @param apiUrl   后端接口地址
     */
    private static void syncDataGeneric(String dataName, String filePath, String apiUrl) {
        try {
            JSONArray dataArray = JsonUtil.readArrayFromFile(filePath);
            int total = dataArray.length();

            AtomicInteger successCount = new AtomicInteger(0);
            long startTime = System.currentTimeMillis();

            System.out.println("\n[系统] 开始同步 " + dataName + " 数据...");

            int batchSize = 50; // 每 50 条打包一次

            int totalBatches = (total + batchSize - 1) / batchSize;
            CountDownLatch latch = new CountDownLatch(totalBatches);

            for (int i = 0; i < total; i += batchSize) {
                // 截取当前批次的数据
                JSONArray batch = new JSONArray();
                for (int j = i; j < Math.min(i + batchSize, total); j++) {
                    batch.put(dataArray.getJSONObject(j));
                }

                // 将这一包数据交给线程池
                executor.execute(() -> {
                    int code = sendJsonRequest(apiUrl, batch.toString());
                    if (code == 200) {
                        successCount.addAndGet(batch.length());
                    }
                    latch.countDown(); // 注意：这里的 latch 计数逻辑需要按批次调整
                });
            }

            latch.await();
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("======= " + dataName + " 同步报告 =======");
            System.out.println("成功: " + successCount.get() + " / 总计: " + total + " | 耗时: " + duration + "ms");
            System.out.println("================================");

        } catch (Exception e) {
            System.err.println(dataName + " 同步过程中发生致命错误: " + e.getMessage());
        }
    }

    /**
     * 发送 POST 请求（带简单重试机制）
     */
    public static int sendJsonRequest(String urlString, String jsonBody) {
        int attempts = 0;
        int lastCode = -1;
        int maxRetries = 2;

        while (attempts < maxRetries) {
            try {
                HttpURLConnection conn = getHttpURLConnection(urlString, jsonBody);

                // 获取响应码
                lastCode = conn.getResponseCode();

                // 如果成功（200 或 201），直接返回
                if (lastCode == HttpURLConnection.HTTP_OK || lastCode == HttpURLConnection.HTTP_CREATED) {
                    return lastCode;
                }

                if (lastCode == HttpURLConnection.HTTP_BAD_REQUEST) { // 400
                    System.err.println("错误 400: 请求格式不规范，请检查 JSON 语法。");
                    break;
                }
                else if (lastCode == HttpURLConnection.HTTP_UNAUTHORIZED) { // 401
                    System.err.println("错误 401: 未授权，请检查登录状态或 Token。");
                    break;
                }
                else if (lastCode == HttpURLConnection.HTTP_FORBIDDEN) { // 403
                    System.err.println("错误 402: 服务器拒绝访问，你可能没有操作权限。");
                    break;
                }
                else if (lastCode == HttpURLConnection.HTTP_NOT_FOUND) { // 404
                    System.err.println("错误 404: 接口路径不存在，请检查 URL。");
                    break;
                }
                else if (lastCode == HttpURLConnection.HTTP_BAD_METHOD) { // 405
                    System.err.println("错误 405: 请求方法错误，后端可能不支持 POST。");
                    break;
                } else {
                    // 5xx 系列（如500）或其他未知错误
                    System.out.println("收到响应码: " + lastCode + "，准备进行下一次重试...");
                }

            } catch (Exception e) {
                System.err.println("第 " + (attempts + 1) + " 次尝试失败: " + e.getMessage());
            }

            attempts++;
            if (attempts < maxRetries) {
                try {
                    // 退避策略：重试前等 2 秒，避免频繁冲击服务器
                    Thread.sleep(2000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return lastCode;
    }

    private static HttpURLConnection getHttpURLConnection(String urlString, String jsonBody) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        // 设置超时（重试机制中必须设置，防止某个请求永久卡死）
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");
//        conn.setRequestProperty("Authorization", "Bearer my-secret-token");
        conn.setDoOutput(true);

        // 写入请求体
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        return conn;
    }

}