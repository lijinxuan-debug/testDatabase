package org.example;

import org.example.utils.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.math.BigDecimal;

public class Main {

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
                JSONObject jsonObject = coursesArray.optJSONObject(i);
                System.out.println("课程ID：" + jsonObject.optString("course_id","") + " 课程名称：" + jsonObject.optString("course_name","") + " 学分：" + jsonObject.optInt("credits",0) + " 老师：" + jsonObject.optString("teacher",""));
            }

            System.out.println();

            for (int i = 0; i < gradesArray.length(); i++) {
                JSONObject jsonObject = gradesArray.optJSONObject(i);
                System.out.println("成绩ID：" + jsonObject.optInt("grade_id",0) + " 学生ID：" + jsonObject.optInt("student_id",0) + " 课程ID：" + jsonObject.optString("course_id","") + " 成绩：" + jsonObject.optBigDecimal("score",new BigDecimal(0)) + " 考试日期：" + jsonObject.optString("exam_date",""));
            }

            System.out.println();

            for (int i = 0; i < studentsArray.length(); i++) {
                JSONObject jsonObject = studentsArray.optJSONObject(i);
                System.out.println("学生ID：" + jsonObject.optInt("student_id",0) + " 姓名：" + jsonObject.optString("name","").trim() + " 性别：" + jsonObject.optString("gender","") + " 出生日期：" + jsonObject.optString("birth_date","") + " 班级名称：" + jsonObject.optString("class_name",""));
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