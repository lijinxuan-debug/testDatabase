package org.example.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JsonUtil {

    /**
     * 获取单个json对象
     */
    public static JSONObject readObjectFromFile(String filePath) {
        String content = readFileAsString(filePath);
        return new JSONObject(content);
    }

    /**
     * 获取json数组
     */
    public static JSONArray readArrayFromFile(String filePath) {
        String content = readFileAsString(filePath);
        return new JSONArray(content);
    }

    private static String readFileAsString(String filePath) {
        if (filePath == null || !Files.exists(Paths.get(filePath))) {
            System.err.println("文件路径为空或文件不存在:" + filePath);
            return "{}";
        }

        StringBuilder builder = new StringBuilder();
        Path path = Paths.get(filePath);
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            System.err.println("IO异常");
        }
        return builder.toString();
    }

}
