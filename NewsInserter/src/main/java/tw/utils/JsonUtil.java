package tw.utils;

import java.lang.reflect.Field;

import org.json.*;

public class JsonUtil {

    private JsonUtil() {
    }

    public static String getJson(Object obj) {
        return (getJson(obj, true));
    }

    public static JSONObject getJsonObj(Object obj) {
        return (getJsonObj(obj, true));
    }

    public static JSONObject getJsonObj(Object obj, boolean isNullCheck) {
        Field[] fileds = obj.getClass().getDeclaredFields();
        JSONObject jo = new JSONObject();
        for (int i = 0; i < fileds.length; i++) {
            String value = (String) ReflectUtil.getObjFValue(obj, fileds[i]);
            if (value == null || value.toLowerCase().equals("null")) {
                continue;
            }
            jo.put(fileds[i].getName(), value);
        }
        return jo;
    }

    public static String getJson(Object obj, boolean isNullCheck) {
        return (getJsonObj(obj, isNullCheck).toString());
    }
}