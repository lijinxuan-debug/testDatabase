package org.example.utils;

import org.example.entity.Course;
import org.example.entity.Grade;
import org.example.entity.Student;
import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpClientUtils {

    /**
     * 发送 POST 请求
     * @param urlString 目标 URL
     * @param jsonBody JSON 格式的请求体
     * @return 响应码
     */
    public static int sendJsonRequest(String urlString, String jsonBody) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            
            // 设置请求属性
            conn.setRequestMethod("POST"); // 或者用 "PUT" 表示更新
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            // 写入请求体
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = conn.getResponseCode();
            System.out.println("请求 URL: " + urlString + " | 响应码: " + code);
            return code;
        } catch (Exception e) {
            return -1;
        }
    }

    public void updateStudent(Student student) {
        String url = "http://localhost:8080/updateStudent";

        JSONObject json = new JSONObject();
        json.put("student_id", student.getStudentId());
        json.put("name", student.getName());
        json.put("class_name", student.getClassName());
        // 注意：Date 需要转为字符串发送
        json.put("birth_date", student.getBirthDate().toString());

        HttpClientUtils.sendJsonRequest(url, json.toString());
    }

    public void updateCourse(Course course) {
        String url = "http://localhost:8080/updateCourse";

        JSONObject json = new JSONObject();
        json.put("course_id", course.getCourseId());
        json.put("course_name", course.getCourseName());
        json.put("credits", course.getCredits());
        json.put("teacher", course.getTeacher());

        HttpClientUtils.sendJsonRequest(url, json.toString());
    }

    public void updateGrade(Grade grade) {
        String url = "http://localhost:8080/updateGrade";

        JSONObject json = new JSONObject();
        json.put("student_id", grade.getStudentId());
        json.put("course_id", grade.getCourseId());
        json.put("score", grade.getScore()); // BigDecimal 会被 org.json 自动处理

        HttpClientUtils.sendJsonRequest(url, json.toString());
    }

}