package com;

import java.net.URLEncoder;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Test8 {
    public static void getSocialSearchResult(boolean isExecu) {
        /*
		 * "mediaLevel": [1,4,3], //媒体级别ID数组 "keyword": "apple",//关键字 "endDate":
		 * "2016-12-31 00:00:00", //结束时间 "highlight": true, //是否高亮 "beginDate":
		 * "2016-01-01 00:00:00", //开始时间 "mediaType":[4,5] //数据源类型新闻或社交
		 * "categoryId": [4,2,1,5], //数据源领域：行业 政府官网 "countryNameZh":
		 * "中国",//国家名数组 "languageTname": "中文",//语言名数组 "mediaNameZh":
		 * "人民日报",//媒体名数组 "sentimentId": [ -1 ],//情感标识ID数组 "order": "asc",//
		 * 指定排序字段 "pageNo": 1, //获取第几页 "pageSize": 10, //返回结果数 "fieldName":
		 * "_score",//排序字段 _score：相关度,pubdate：时间
		 */
        for (int i = 1; i < 100; i++) {
            System.out.println("第" + i + "次");
            JSONObject j = new JSONObject();
            // j.put("mediaLevel", new int[]{4,3});// //媒体级别ID数组
            j.put("keyword", "中国"); // 关键字
            // j.put("endDate", "2016-12-31 00:00:00");// //结束时间
            // j.put("highlight", "true");// //是否高亮
            // j.put("beginDate", "2016-01-01 00:00:00");// //开始时间
            // j.put("mediaType", new int[]{1});//数据源类型新闻或社交,数组
            // j.put("categoryId", new int[]{1,5}); //数据源领域：行业 政府官网
            // j.put("countryNameZh",new String[]{"中国"});//国家名数组
            // j.put("languageCode",new String[]{"en"});////语言名数组
            //// j.put("mediaNameZh",new String[]{"中网资讯中心-科技"});////媒体名数组
            // j.put("sentimentId",new int[]{ -1 });////情感标识ID数组
            j.put("order", "asc");//// 指定排序字段
            j.put("pageNo", 1);// //获取第几页
            j.put("pageSize", 1);// //返回结果数
            j.put("fieldName", "rpsCnt");//// 排序字段 _score：相关度,pubdate：时间
            String param = URLEncoder.encode(j.toString());
            if (isExecu) {
                System.out.println(param);
                return;
            }
            // String
            // s=HttpRequest.sendGet("http://localhost:8081/webservice/getSocialSearchResult/dfa95dbe9d657116c5613d6b6c05abcd?param="+param);
            String s = HttpRequest.sendGet("http://localhost:8088/?param=" + param);
            try {
                Thread.sleep(1000l);
            } catch (Exception e) {
                e.printStackTrace();
                // TODO: handle exception
            }
        }
    }

    public static void main(String[] args) {
        getSocialSearchResult(false);
        // Object keyArr[]=new Object[]{"a","b","c"};
    }
}
