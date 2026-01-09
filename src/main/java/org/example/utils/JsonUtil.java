package org.example.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonUtil {

    /**
     * 获取单个json对象
     * @param filePath
     * @return
     * @throws Exception
     */
    public static JSONObject readObjectFromFile(String filePath) throws Exception {
        String content = readFileAsString(filePath);
        return new JSONObject(content);
    }

    /**
     * 获取json数组
     * @param filePath
     * @return
     * @throws Exception
     */
    public static JSONArray readArrayFromFile(String filePath) throws Exception {
        String content = readFileAsString(filePath);
        return new JSONArray(content);
    }

    private static String readFileAsString(String filePath) throws Exception {
        byte[] encoded = Files.readAllBytes(Paths.get(filePath));
        return new String(encoded, StandardCharsets.UTF_8);
    }

}
