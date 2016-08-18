package com;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class Test9 {
    public static void getCountryCount() {
        Map<String, Object> mapAll = new HashMap<String, Object>();
        mapAll.put("count", "1000000");

        List<Map<String, String>> list = new ArrayList<Map<String, String>>();

    }

    public static void main(String[] args) {
        /*
		 * String jsonStr=JsonUtil.getJson("test.json"); JSONObject j=
		 * JSONObject.fromObject(jsonStr); JSONArray
		 * list=j.getJSONObject("returndata").getJSONArray("countryCount"); int
		 * total=0; for(int i=0;i<list.size();i++){
		 * System.out.println(list.get(i)); JSONObject
		 * json=list.getJSONObject(i); total=total+json.getInt("count"); }
		 * System.out.println("count="+j.getJSONObject("returndata").getString(
		 * "count")); System.out.println("国家累计count="+total);
		 */

        // JSONObject j=new JSONObject();
        // j.put("productName", "我的大学");
        // j.put("eles", "挺好");
        // j.put("website", "亚马逊");
        // String param=URLEncoder.encode(j.toString());
        // System.out.println(param);
        // System.out.println(new
        // Test9().getClass().getResource("/htmlcode.txt"));
        // Map<String,Object> rMap=new HashMap<String,Object>();
        // rMap.put("resultCount", 0);
        // rMap.put("resultList", new String[]{});
        // String result = JSONObject.fromObject(rMap).toString();
        // System.out.println(result);

        JSONObject j = new JSONObject();
        j.put("keyword", "\"手机行业\" or \"手机数据\" or \"通讯市场\" or \"通讯条例\" or \"通讯监管\" or \"mobile industry\"");
        j.put("endDate", "2016-7-29 0:19:00");
        j.put("beginDate", "2016-04-01 00:16:00");
        j.put("countryNameZh", new String[]{"美国"});
        String param = URLEncoder.encode(j.toString());
        System.out.println(param);
    }
}
