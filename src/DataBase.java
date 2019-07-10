import com.google.gson.Gson;
import kong.unirest.HttpResponse;

import java.util.HashMap;

import kong.unirest.Unirest;
import kong.unirest.UnirestException;

public class DataBase {
    private static String address = "http://127.0.0.1:8080/";
    private static Gson gson = new Gson();

    public static void initialize(String dataBaseName) {
        HttpResponse<String> answer = null;
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", dataBaseName);
        try {
            answer = Unirest.post(address + "init_DB").fields(map).asString();
            System.out.println(answer.getStatus());
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    public static void addValue(String dataBaseName, Object key, Object value) {
        HttpResponse<String> answer = null;
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", dataBaseName);
        map.put("key", gson.toJson(key));
        map.put("value", gson.toJson(value));
        try {
            answer = Unirest.post(address + "put").fields(map).asString();
            System.out.println(answer.getStatus());
        } catch (UnirestException e) {
            e.printStackTrace();
        }

    }

    public static <T> T getValues(String dataBaseName, Class<T> tClass) {
        HttpResponse<String> answer = null;
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", dataBaseName);
        try {
            answer = Unirest.post(address + "get_all_values").fields(map).asString();
            return gson.fromJson(answer.getBody(), tClass);
        } catch (UnirestException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T getKeys(String dataBaseName, Class<T> tClass) {
        HttpResponse<String> answer = null;
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", dataBaseName);
        try {
            answer = Unirest.post(address + "get_all_keys").fields(map).asString();
            return gson.fromJson(answer.getBody(), tClass);
        } catch (UnirestException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T getSingleValue(String dataBaseName, Object key, Class<T> tClass) {
        HttpResponse<String> answer = null;
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", dataBaseName);
        map.put("key", gson.toJson(key));
        try {
            answer = Unirest.post(address + "get").fields(map).asString();
            System.out.println(answer.getBody());
            return gson.fromJson(answer.getBody(), tClass);
        } catch (UnirestException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void deleteValue(String dataBaseName, Object key) {
        HttpResponse<String> answer = null;
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", dataBaseName);
        map.put("key", key);
        try {
            answer = Unirest.post(address + "del_from_DB").fields(map).asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }
}
