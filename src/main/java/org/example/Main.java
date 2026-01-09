package org.example;

import org.example.utils.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.format.DateTimeFormatter;

public class Main {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
//        printJsonObject();
        createAndInsertArray();
    }

    // 解析json字符串并提取数据打印到控制台
    public static void printJsonObject() {
        try {
            JSONArray coursesArray = JsonUtil.readArrayFromFile("src/main/resources/data/courses.json");
            JSONArray gradesArray = JsonUtil.readArrayFromFile("src/main/resources/data/grades.json");
            JSONArray studentsArray = JsonUtil.readArrayFromFile("src/main/resources/data/students.json");

            for (int i = 0; i < coursesArray.length(); i++) {
                JSONObject jsonObject = coursesArray.getJSONObject(i);
                System.out.println("课程ID：" + jsonObject.getString("course_id") + " 课程名称：" + jsonObject.getString("course_name") + " 学分：" + jsonObject.getInt("credits") + " 老师：" + jsonObject.getString("teacher"));
            }

            System.out.println();

            for (int i = 0; i < gradesArray.length(); i++) {
                JSONObject jsonObject = gradesArray.getJSONObject(i);
                System.out.println("成绩ID：" + jsonObject.getInt("grade_id") + " 学生ID：" + jsonObject.getInt("student_id") + " 课程ID：" + jsonObject.getString("course_id") + " 成绩：" + jsonObject.getBigDecimal("score") + " 考试日期：" + jsonObject.getString("exam_date"));
            }

            System.out.println();

            for (int i = 0; i < studentsArray.length(); i++) {
                JSONObject jsonObject = studentsArray.getJSONObject(i);
                System.out.println("学生ID：" + jsonObject.getInt("student_id") + " 姓名：" + jsonObject.getString("name").trim() + " 性别：" + jsonObject.getString("gender") + " 出生日期：" + jsonObject.getString("birth_date") + " 班级名称：" + jsonObject.getString("class_name"));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    // 构建Java对象并插入到json数组中
    public static void createAndInsertArray() {
        String filePath = "src/main/resources/data/students.json";

        try {
            JSONArray array = JsonUtil.readArrayFromFile(filePath);

            JSONObject newStudent = new JSONObject();
            newStudent.put("student_id", 2024014);
            newStudent.put("name", "罗翔老师");
            newStudent.put("gender", "男");
            newStudent.put("birth_date", "1977-01-01");
            newStudent.put("class_name", "法律实验班");

            array.put(newStudent);
            // 写入必须放到读之后。
            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));

            bw.write(array.toString(4));
            bw.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}