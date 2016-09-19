package com.services.service.impl;

import com.services.service.PlatFormServiceI;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @author wuxu
 * @ClassName: HealthBusiServiceImpl
 * @Description: 接口处理服务类 接口中的所有逻辑均在此类中进行。待完善后，此类需要根据业务进行拆
 * @date 2015-8-4 下午03:27:54
 */
@Component("platFormService")
@Scope("singleton")
public class PlatFormServiceImpl
        implements PlatFormServiceI {
    protected static Logger log = LoggerFactory.getLogger(PlatFormServiceImpl.class);

    public static XMLGregorianCalendar convertToXMLGregorianCalendar(Date date) {

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        XMLGregorianCalendar gc = null;
        try {
            gc = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gc;
    }

    public JSONObject insertNewsForPost(String param)
            throws Exception {
        String result = NewsInsertServiceImpl.getInstance().insert(param);
        return new JSONObject(result);
    }

    public static String getJsonValueByKey(JSONObject jsonParam, String key) {
        String val = "";
        try {
            if (jsonParam == null) {
                val = "";
            } else {
                if (jsonParam.has(key)) {
                    val = jsonParam.getString(key);
                } else {
                    val = "";
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            val = "";
        }
        return val;
    }

    // public static Object getJsonBoolByKey(JSONObject jsonParam, String key) {
    // Object val = null;
    // try {
    // if (jsonParam == null) {
    // val = null;
    // } else {
    // if (jsonParam.containsKey(key)) {
    // val = jsonParam.get(key);
    // } else {
    // val = null;
    // }
    // }
    // } catch (Exception e) {
    // // TODO: handle exception
    // val = null;
    // }
    // return val;
    // }
    public static String arrToString(Object objArr[]) {
        String temp = "";
        if (objArr != null) {
            for (Object obj : objArr) {
                temp += obj + ",";
            }
            if (temp.endsWith(",")) {
                temp = temp.substring(0, temp.length() - 1);
            }
        }
        return temp;
    }

    public static String[] stringToArr(String str) {
        String temp[] = new String[]{};
        if (str != null) {
            if (str.indexOf(",") > 0) {
                return str.split(",");
            } else {
                if (str != null && !"".equals(str) && !"null".equals(str)) {
                    temp = new String[]{str};
                }
            }
        }
        return temp;
    }

    public static String[] convertStringArr(String arr[]) {
        if (arr == null || arr.length == 0) {
            return new String[]{};
        } else {
            List<String> list = new ArrayList<String>();
            for (String t : arr) {
                if (t != null && !"".equals(t) && !"null".equals(t)) {
                    list.add(t);
                }
            }
            if (list.size() == 0) {
                return new String[]{};
            } else {
                String tempArr[] = new String[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    tempArr[i] = list.get(i);
                }
                return tempArr;
            }
        }
    }

    private static String unix2TimeV2(long timestamp) {
        try {
            timestamp *= 1;
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timestamp));
            return date;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return "";
        }
    }

    public static void main(String[] args) {
        System.out.println(unix2TimeV2(1465212173000l));
    }
}
