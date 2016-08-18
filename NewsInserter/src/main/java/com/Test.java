/**
 *
 */
package com;

import java.net.URLEncoder;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import net.sf.json.JSONObject;

/**
 * @author Administrator
 */
public class Test {

    /**
     * @param args
     */

    /**
     * 3.1.3 [相关资讯接口] [OK] 入参：标题、正文【可选】、keyword 出参：相关资讯列表 输入资讯信息获取检索结果
     */
    public static void getRelationNew(boolean isExecu) {
        JSONObject j = new JSONObject();
        j.put("title",
                "国军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军");
        String param = URLEncoder.encode(j.toString());
        if (isExecu) {
            System.out.println(param);
            return;
        }
        String s = HttpRequest.sendGet("http://192.168.55.20:8088/webservice/getRelationNew/39006660?param=" + param);
        System.out.println(s);
    }

    /**
     * 3.2.1 数据源列表【OK】
     */
    public static void getSourceList(boolean isExecu) {
        JSONObject j = new JSONObject();
        j.put("pageNo", "2");
        j.put("pageSize", "10");
        j.put("keyword", "中国");
        String param = URLEncoder.encode(j.toString());
        if (isExecu) {
            System.out.println(param);
            return;
        }
        String s = HttpRequest.sendGet("http://192.168.55.20:8088/webservice/getSourceList/39006660?param=" + param);
        System.out.println(s);
    }

    /**
     * 3.3.1 热点列表【OK】
     */
    public static void getHotList(boolean isExecu) {
        JSONObject j = new JSONObject();
        j.put("token", "111111111111111");
        j.put("beginDate", "2016-04");
        j.put("endDate", "10");
        String param = URLEncoder.encode(j.toString());
        if (isExecu) {
            System.out.println(param);
            return;
        }
        String s = HttpRequest.sendGet("http://192.168.55.20:8088/webservice/getHotList/39006660?param=" + param);
        System.out.println(s);
    }

    public static void getHotResultByID(boolean isExecu) {
        JSONObject j = new JSONObject();
        j.put("id", "9");
        j.put("pageSize", "10");
        j.put("pageNo", "4");
        String param = URLEncoder.encode(j.toString());
        if (isExecu) {
            System.out.println(param);
            return;
        }
        String s = HttpRequest.sendGet("http://192.168.55.20:8088/webservice/getHotResultByID/39006660?param=" + param);
        System.out.println(s);
    }

    public static void getSearchAnalyse(boolean isExecu) {
        JSONObject j = new JSONObject();
        j.put("keyword", "美女");
        j.put("models", new String[]{"listNMSD", "listNKDZh"});
        String param = URLEncoder.encode(j.toString());
        if (isExecu) {
            System.out.println(param);
            return;
        }
        String s = HttpRequest.sendGet("http://192.168.55.20:8088/webservice/getSearchAnalyse/39006660?param=" + param);
        System.out.println(s);
    }

    public static void getSearchFastAnalyse(boolean isExecu) {
        JSONObject j = new JSONObject();
        j.put("keyword", "(msi ) and (\"\") and () and not ()");
        j.put("beginDate", "2016-01-01 00:00:00");
        j.put("endDate", "2016-04-01 23:59:59");
        String param = URLEncoder.encode(j.toString());
        if (isExecu) {
            System.out.println(param);
            return;
        }
        String s = HttpRequest
                .sendGet("http://192.168.55.20:8088/webservice/getSearchFastAnalyse/39006660?param=" + param);
        System.out.println(s);
    }

    public static void getSpecialRegsiter(boolean isExecu) {
        JSONObject j = new JSONObject();
        j.put("title", "苹果公司业绩现拐点");
        j.put("register", "1");
        j.put("mediaLevel", new String[]{"1", "2", "3", "4"});
        j.put("type", "1");
        j.put("describe", "专\"题 描述测试");
        j.put("keyword", "苹果业绩");
        j.put("mediaType", new String[]{"1", "2"});
        j.put("categoryId", new String[]{"1", "2", "3", "4", "5", "6", "7", "999"}); // 领域分类ID数组
        j.put("endDate", "2016-12-31 00:00:00"); // 开始时间
        j.put("beginDate", "2016-01-01 00:00:00"); // 开始时间
        // j.put("countryNameZh",new String[]{
        // "1","2","3","4","5","6","7","8","9","10","11","12","13" });//语言id数组
        j.put("languageCode",
                new String[]{"ro", "th", "fa", "es", "zh", "da", "he", "sv", "pt", "ko", "pl", "nl", "sq", "ar", "ru",
                        "no", "mn", "zh-tw", "uz", "de", "cs", "vi", "ka", "it", "en", "hu", "el", "et", "ms", "tr",
                        "fi", "si", "hi", "ml", "fr"});// 语言id数组
        j.put("mediaNameZh", new String[]{});// 语言id数组
        // j.put("countryId","1001");//如右图中的数据源区域

        String param = URLEncoder.encode(j.toString());
        if (isExecu) {
            System.out.println(param);
            return;
        }
        String s = HttpRequest.sendGet(
                "http://192.168.55.20:8088/webservice/getSpecialRegsiter/dfa95dbe9d657116c5613d6b6c05abcd?param="
                        + param);
        System.out.println(s);
    }

    public static void getSpecialStatus(boolean isExecu) {
        JSONObject j = new JSONObject();
        j.put("token", "00114610488398410CBBF040BCBFA7BC");
        String param = URLEncoder.encode(j.toString());
        if (isExecu) {
            System.out.println(param);
            return;
        }
        String s = HttpRequest.sendGet("http://192.168.55.20:8088/webservice/getSpecialStatus/39006660?param=" + param);
        System.out.println(s);
    }

    public static void getSpecialSchedule(boolean isExecu) {
        for (int i = 0; i < 1000; i++) {
            System.out.println("第" + (i + 1) + "次开始");
            JSONObject j = new JSONObject();
            j.put("token", "0001461665090200C1AB234161DDD53C");
            j.put("testcount", i);
            String param = URLEncoder.encode(j.toString());
            if (isExecu) {
                System.out.println(param);
                return;
            }
            String s = HttpRequest
                    .sendGet("http://192.168.55.20:8088/webservice/getSpecialSchedule/39006660?param=" + param);
            System.out.println("第" + (i + 1) + "次结果：");
            System.out.println(s);
            System.out.println();
        }
    }

    public static void getWarnRegsiter(boolean isExecu) {
        JSONObject j = new JSONObject();
        j.put("isCountry", 1);// 关闭 1开启
        j.put("wCountryName", "中国");// ,//国别id，非数组
        j.put("isNewsChange", 0);// 0关闭 1开启
        j.put("newsCUp", 40);// 较上周均值增加40%
        j.put("newsCDown", 40);// 较上周均值增加40%
        j.put("isSensWord", 0);// 0关闭 1开启
        j.put("sensWords", "朝鲜");// ，多个词用空格隔开
        j.put("isNegative", 0);// 关闭 1开启
        j.put("token", "0011461048933761A319B36A3FBAFD4B");

        String param = URLEncoder.encode(j.toString());
        if (isExecu) {
            System.out.println(param);
            return;
        }
        String s = HttpRequest.sendGet("http://192.168.55.20:8088/webservice/getWarnRegsiter/39006660?param=" + param);
        System.out.println(s);
    }

    public static void getWarnUpdate(boolean isExecu) {
        JSONObject j = new JSONObject();
        j.put("isCountry", 0);// 关闭 1开启
        j.put("wCountryName", "中国修改后");// ,//国别id，非数组
        j.put("ifNewsChange", 0);// 0关闭 1开启
        j.put("newsCUp", 40);// 较上周均值增加40%
        j.put("newsCDown", 40);// 较上周均值增加40%
        j.put("ifSensWord", 0);// 0关闭 1开启
        j.put("sensWords", "伊朗修改后");// ，多个词用空格隔开
        j.put("negative", 0);// 关闭 1开启
        j.put("token", "2451c809-bc97-4b70-9dc0-d52f53e8bbdc");// 关闭 1开启

        String param = URLEncoder.encode(j.toString());
        if (isExecu) {
            System.out.println(param);
            return;
        }
        String s = HttpRequest.sendGet("http://192.168.55.20:8088/webservice/getWarnUpdate/39006660?param=" + param);
        System.out.println(s);
    }

    public static void getSpecialUpdate(boolean isExecu) {
        JSONObject j = new JSONObject();
        j.put("token", "3db11799-bd1b-4f44-9867-ef75dacf6d10");
        j.put("title", "111111111111");
        // j.put("mediaLevel","2");
        // j.put("type","1");
        // j.put("describe","专题描述");
        j.put("keyword", "只修改关键词");
        // j.put("mediaId ","数据源类型");
        // j.put("mediaType ",new int[]{4,2,1,5});
        // j.put("categoryId",new int[]{4,2,1,5}); //领域分类ID数组
        j.put("endDate", "2016-12-31 23:59:50"); // 开始时间
        j.put("beginDate", "2016-01-01 00:00:00"); // 开始时间
        // j.put("countryNameZh",new String[]{ "中国","美国" });//语言id数组
        // j.put("languageTname",new String[]{ "中文","英文" });//语言id数组
        // j.put("mediaNameZh",new String[]{ "人民日报","环球时报" });//语言id数组
        String param = URLEncoder.encode(j.toString());
        if (isExecu) {
            System.out.println(param);
            return;
        }
        String s = HttpRequest.sendGet("http://192.168.55.20:8088/webservice/getSpecialUpdate/39006660?param=" + param);
        System.out.println(s);
    }

    public static void getSpecialResultByID(boolean isExecu) {
        JSONObject j = new JSONObject();
        j.put("token", "000146223121454006E7CB4709FE844C");
        j.put("srcMediaType", "news");
        j.put("pageSize", "10");
        j.put("pageNo", "1");

        String param = URLEncoder.encode(j.toString());
        if (isExecu) {
            System.out.println(param);
            return;
        }
        String s = HttpRequest
                .sendGet("http://192.168.55.20:8088/webservice/getSpecialResultByID/39006660?param=" + param);
        System.out.println(s);
    }

    public static void getHotspotRegsiter(boolean isExecu) {
        JSONObject j = new JSONObject();
        j.put("title", "标题");
        j.put("categoryId", new int[]{4, 2, 1, 5});
        j.put("keywords", "关键词");

        String param = URLEncoder.encode(j.toString());
        if (isExecu) {
            System.out.println(param);
            return;
        }
        String s = HttpRequest
                .sendGet("http://192.168.55.20:8088/webservice/getHotspotRegsiter/39006660?param=" + param);
        System.out.println(s);
    }

    public static void getWarnStatus(boolean isExecu) {
        JSONObject j = new JSONObject();
        j.put("token", "0191461145274799CFA25E6B91CC861E");

        String param = URLEncoder.encode(j.toString());
        if (isExecu) {
            System.out.println(param);
            return;
        }
        String s = HttpRequest.sendGet("http://192.168.55.20:8088/webservice/getWarnStatus/39006660?param=" + param);
        System.out.println(s);
    }

    public static void getWarnResult(boolean isExecu) {
        JSONObject j = new JSONObject();
        j.put("token", "1222222222222222");

        String param = URLEncoder.encode(j.toString());
        if (isExecu) {
            System.out.println(param);
            return;
        }
        String s = HttpRequest.sendGet("http://192.168.55.20:8088/webservice/getWarnResult/39006660?param=" + param);
        System.out.println(s);
    }

    public static void getWarnNewsList(boolean isExecu) {
        JSONObject j = new JSONObject();
        j.put("token", "0061461324726926396E47DB279FC6CC");
        j.put("pageSize", "10");
        j.put("pageNo", "1");

        String param = URLEncoder.encode(j.toString());
        if (isExecu) {
            System.out.println(param);
            return;
        }
        String s = HttpRequest.sendGet("http://192.168.55.20:8088/webservice/getWarnNewsList/39006660?param=" + param);
        System.out.println(s);
    }

    public static void getMediaTypeList(boolean isExecu) {
        JSONObject j = new JSONObject();
        String param = URLEncoder.encode(j.toString());
        if (isExecu) {
            System.out.println(param);
            return;
        }
        String s = HttpRequest.sendGet("http://192.168.55.20:8088/webservice/getMediaTypeList/39006660?param=" + param);
        System.out.println(s);
    }

    public static void getIndustryTypeList(boolean isExecu) {
        JSONObject j = new JSONObject();
        String param = URLEncoder.encode(j.toString());
        if (isExecu) {
            System.out.println(param);
            return;
        }
        String s = HttpRequest
                .sendGet("http://192.168.55.20:8088/webservice/getIndustryTypeList/39006660?param=" + param);
        System.out.println(s);
    }

    public static void getWeiboSearchAnalyse(boolean isExecu) {
        JSONObject j = new JSONObject();
        j.put("word", "关键词");
        String param = URLEncoder.encode(j.toString());
        if (isExecu) {
            System.out.println(param);
            return;
        }
        String s = HttpRequest
                .sendGet("http://192.168.55.20:8088/webservice/getWeiboSearchAnalyse/39006660?param=" + param);
        System.out.println(s);
    }

    public static void getWeiXinSearchAnalyse(boolean isExecu) {
        JSONObject j = new JSONObject();
        j.put("word", "关键词");
        String param = URLEncoder.encode(j.toString());
        if (isExecu) {
            System.out.println(param);
            return;
        }
        String s = HttpRequest
                .sendGet("http://192.168.55.20:8088/webservice/getWeiXinSearchAnalyse/39006660?param=" + param);
        System.out.println(s);
    }

    public static void getNewsSearchAnalyse(boolean isExecu) {
        JSONObject j = new JSONObject();
        j.put("word", "关键词");
        String param = URLEncoder.encode(j.toString());
        if (isExecu) {
            System.out.println(param);
            return;
        }
        String s = HttpRequest
                .sendGet("http://192.168.55.20:8088/webservice/getNewsSearchAnalyse/39006660?param=" + param);
        System.out.println(s);
    }

    public static void getNewsSpreadAnalyse(boolean isExecu) {
        JSONObject j = new JSONObject();
        j.put("word", "关键词");
        String param = URLEncoder.encode(j.toString());
        if (isExecu) {
            System.out.println(param);
            return;
        }
        String s = HttpRequest
                .sendGet("http://192.168.55.20:8088/webservice/getNewsSpreadAnalyse/39006660?param=" + param);
        System.out.println(s);
    }

    public static void getWeiboByIDAnalyse(boolean isExecu) {
        JSONObject j = new JSONObject();
        j.put("myid", "微博ID");
        String param = URLEncoder.encode(j.toString());
        if (isExecu) {
            System.out.println(param);
            return;
        }
        String s = HttpRequest
                .sendGet("http://192.168.55.20:8088/webservice/getWeiboByIDAnalyse/39006660?param=" + param);
        System.out.println(s);
    }

    public static void getWeiXinByIDAnalyse(boolean isExecu) {
        JSONObject j = new JSONObject();
        j.put("id", "公众号ID");
        String param = URLEncoder.encode(j.toString());
        if (isExecu) {
            System.out.println(param);
            return;
        }
        String s = HttpRequest
                .sendGet("http://192.168.55.20:8088/webservice/getWeiXinByIDAnalyse/39006660?param=" + param);
        System.out.println(s);
    }

    public static void getSpecialPortraitByIDAnalyse(boolean isExecu) {
        JSONObject j = new JSONObject();
        j.put("token", "222222222222222222");
        String param = URLEncoder.encode(j.toString());
        if (isExecu) {
            System.out.println(param);
            return;
        }
        String s = HttpRequest
                .sendGet("http://192.168.55.20:8088/webservice/getSpecialPortraitByIDAnalyse/39006660?param=" + param);
        System.out.println(s);
    }

    public static void getSpecialSentimentByIDAnalyse(boolean isExecu) {
        JSONObject j = new JSONObject();
        j.put("token", "921462029254192C3566EDBC6C905D7");
        String param = URLEncoder.encode(j.toString());
        if (isExecu) {
            System.out.println(param);
            return;
        }
        String s = HttpRequest
                .sendGet("http://192.168.55.20:8088/webservice/getSpecialSentimentByIDAnalyse/39006660?param=" + param);
        System.out.println(s);
    }

    public static void getNewsByID(boolean isExecu) {
        JSONObject j = new JSONObject();
        j.put("nid", "pubdate");
        String param = URLEncoder.encode(j.toString());
        if (isExecu) {
            System.out.println(param);
            return;
        }
        String s = HttpRequest.sendGet("http://192.168.55.20:8088/webservice/getNewsByID/39006660?param=" + param);
        System.out.println(s);
    }

    public static void updateNewsByIDForPost(boolean isExecu) {
        JSONObject j = new JSONObject();
        j.put("uuid", "0041464914971305CFADFD8C997071A3");
        j.put("languageCode", "en");
        j.put("textEn",
                "The Hollywood Reporter says HBO is teaming up with China Movie Channel to co-produce TV movies for Chinese TV audiences.<BR/><BR/>It marks HBO's first major attempt in the massive Chinese television market.<BR/><BR/>The China Movie Channel, better known as CCTV-6, is the flagship entertainment channel under the banner of China Central Television.<BR/><BR/>In an interview with The Hollywood Reporter, Zhang Ling, vice president of CCTV-6, said more details will be revealed at the upcoming Shanghai International Film Festival which runs from June 11 to 18.<BR/><BR/>The channel has invested in several Hollywood films such as Transformers: Age of Extinction and Mission Impossible - Rogue Nation%.<BR/><BR/>The most recent project is the distribution of Angry Birds in China on May 20.<BR/><BR/>HBO has not given any comment as yet.");
        // System.out.println(j.toString());
        // String param=URLEncoder.encode(j.toString());
        // if(isExecu){
        // System.out.println(param);
        // return;
        // }
        try {
            System.out.println(URLEncoder.encode(j.toString(), "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
        // try{
        // String
        // s=HttpRequest.sendPost("http://192.168.55.20:8088/webservice/updateNewsByIDForPost/dfa95dbe9d657116c5613d6b6c05abcd",
        // URLEncoder.encode(j.toString(),"UTF-8"));
        // System.out.println(s);}catch (Exception e) {
        // // TODO: handle exception
        // e.printStackTrace();
        // }
    }

    public static void getSearchResult(boolean isExecu) {
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
            j.put("pageNo", i);// //获取第几页
            j.put("pageSize", 1);// //返回结果数
            j.put("fieldName", "_score");//// 排序字段 _score：相关度,pubdate：时间
            // System.out.println(j.toString());
            String param = URLEncoder.encode(j.toString());
            if (isExecu) {
                System.out.println(param);
                return;
            }
            String s = HttpRequest.sendGet(
                    "http://192.168.55.20:8088/webservice/getSearchResult/dfa95dbe9d657116c5613d6b6c05abcd?param="
                            + param);
            System.out.println(s);
            try {
                Thread.sleep(1000l);
            } catch (Exception e) {
                e.printStackTrace();
                // TODO: handle exception
            }
        }

    }

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
            j.put("pageSize", 10);// //返回结果数
            j.put("fieldName", "rpsCnt");//// 排序字段 _score：相关度,pubdate：时间
            String param = URLEncoder.encode(j.toString());
            if (isExecu) {
                System.out.println(param);
                return;
            }
            String s = HttpRequest.sendGet(
                    "http://192.168.55.20:8088/webservice/getSocialSearchResult/dfa95dbe9d657116c5613d6b6c05abcd?param="
                            + param);
            try {
                Thread.sleep(1000l);
            } catch (Exception e) {
                e.printStackTrace();
                // TODO: handle exception
            }
        }
    }

    public static void getResultByCondition(boolean isExecu) {
        JSONObject j = new JSONObject();
        j.put("token", "0091461081815829F143C21835BB29FA");
        j.put("mediaLevel", new int[]{1, 4, 3});// //媒体级别ID数组
        j.put("keyword", "apple");//// 关键字
        j.put("endDate", "2016-12-31 00:00:00");// //结束时间
        j.put("highlight", true);// //是否高亮
        j.put("beginDate", "2016-01-01 00:00:00");// //开始时间
        j.put("sentimentId", new int[]{-1});//// 情感标识ID数组
        j.put("categoryId", new int[]{4, 2, 1, 5});// //数据源领域：行业 政府官网
        j.put("languageTname", new String[]{"日文"});
        j.put("FIELD_COUNTRY_NAME_ZH", new String[]{"日本"});
        j.put("order", "asc");//// 指定排序字段
        j.put("pageNo", 1);// //获取第几页
        j.put("pageSize", 10);// //返回结果数
        j.put("fieldName", "_score");//// 排序字段 _score：相关度,pubdate：时间

        String param = URLEncoder.encode(j.toString());
        if (isExecu) {
            System.out.println(param);
            return;
        }
        String s = HttpRequest
                .sendGet("http://192.168.55.20:8088/webservice/getResultByCondition/39006660?param=" + param);
        System.out.println(s);
    }

    public static void getLanguagesList(boolean isExecu) {
        JSONObject j = new JSONObject();
        String param = URLEncoder.encode(j.toString());
        if (isExecu) {
            System.out.println(param);
            return;
        }
        String s = HttpRequest.sendGet("http://192.168.55.20:8088/webservice/getLanguagesList/39006660?param=" + param);
        System.out.println(s);
    }

    public static void getRegionList(boolean isExecu) {
        JSONObject j = new JSONObject();
        String param = URLEncoder.encode(j.toString());
        if (isExecu) {
            System.out.println(param);
            return;
        }
        String s = HttpRequest.sendGet("http://192.168.55.20:8088/webservice/getRegionList/39006660?param=" + param);
        System.out.println(s);
    }

    public static void getCountryList(boolean isExecu) {
        JSONObject j = new JSONObject();
        String param = URLEncoder.encode(j.toString());
        if (isExecu) {
            System.out.println(param);
            return;
        }
        String s = HttpRequest.sendGet("http://192.168.55.20:8088/webservice/getCountryList/39006660?param=" + param);
        System.out.println(s);
    }

    public static void getAllInfoByTableName(boolean isExecu) {
        JSONObject j = new JSONObject();
        j.put("tableName", "Special");
        j.put("count", "20");
        String param = URLEncoder.encode(j.toString());
        if (isExecu) {
            System.out.println(param);
            return;
        }
        String s = HttpRequest
                .sendGet("http://192.168.55.20:8088/webservice/getAllInfoByTableName/39006660?param=" + param);
        System.out.println(s);
    }

    public static void updateNewsByID(boolean isExecu) {
        JSONObject j = new JSONObject();
        j.put("nid", "01514609953140353346ABAB1FC55C5F");
        j.put("titleZh", "\"中国之莺\"周小燕凌晨去世 追忆奇女子的家春秋-1");
        j.put("author", "未知");
        String param = URLEncoder.encode(j.toString());
        if (isExecu) {
            System.out.println(param);
            return;
        }
        String s = HttpRequest.sendGet("http://192.168.55.20:8088/webservice/updateNewsByID/39006660?param=" + param);
        System.out.println(s);
    }

    public static void getSocialSearchAnalyse(boolean isExecu) {
        JSONObject j = new JSONObject();
        j.put("endDate", "2016-12-01 00:00:00");
        j.put("beginDate", "2016-01-01 00:00:00");
        j.put("keyword", "中国");
        String param = URLEncoder.encode(j.toString());
        if (isExecu) {
            System.out.println(param);
            return;
        }
        String s = HttpRequest
                .sendGet("http://192.168.55.20:8088/webservice/getSocialSearchAnalyse/39006660?param=" + param);
        System.out.println(s);
    }

    public static void getSocialByID(boolean isExecu) {
        JSONObject j = new JSONObject();
        j.put("myId", "45579047339314613758210003967360102765526");
        String param = URLEncoder.encode(j.toString());
        if (isExecu) {
            System.out.println(param);
            return;
        }
        String s = HttpRequest.sendGet("http://192.168.55.20:8088/webservice/getSocialByID/39006660?param=" + param);
        System.out.println(s);
    }

    public static void getAllInfoByRowkey(boolean isExecu) {
        JSONObject j = new JSONObject();
        j.put("rowKey", "0001462361771840ADBC120FD3DC02A9");
        j.put("tableName", "SocialSpecial");
        String param = URLEncoder.encode(j.toString());
        if (isExecu) {
            System.out.println(param);
            return;
        }
        String s = HttpRequest
                .sendGet("http://192.168.55.20:8088/webservice/getAllInfoByRowkey/39006660?param=" + param);
        System.out.println(s);
    }

    public static void getProductsComparison(boolean isExecu) {
        JSONObject j = new JSONObject();
        j.put("productNames", new String[]{"大屏幕", "待机短"});
        String param = URLEncoder.encode(j.toString());
        if (isExecu) {
            System.out.println(param);
            return;
        }
        String s = HttpRequest
                .sendGet("http://192.168.55.20:8088/webservice/getAllInfoByRowkey/39006660?param=" + param);
        System.out.println(s);
    }

    public static void getIndustryInfo(boolean isExecu) {
        JSONObject j = new JSONObject();
        j.put("industryName", "手机");
        String param = URLEncoder.encode(j.toString());
        if (isExecu) {
            System.out.println(
                    "http://192.168.55.20:8088/webservice/getIndustryInfo/dfa95dbe9d657116c5613d6b6c05abcd?param="
                            + param);
            return;
        }
        String s = HttpRequest.sendGet(
                "http://192.168.55.20:8088/webservice/getIndustryInfo/dfa95dbe9d657116c5613d6b6c05abcd?param=" + param);
        System.out.println(s);
    }

    public static void getIndustrySave(boolean isExecu) {
        JSONObject j = new JSONObject();
        j.put("industryName", "手机");
        j.put("keywords", new String[]{"大屏", "IOS"});
        j.put("cateGoryIds", new String[]{"1", "2"});
        j.put("cateGoryNames", new String[]{"aaa", "bbb"});
        String param = URLEncoder.encode(j.toString());
        if (isExecu) {
            System.out.println(
                    "http://192.168.55.20:8088/webservice/getIndustrySave/dfa95dbe9d657116c5613d6b6c05abcd?param="
                            + param);
            return;
        }
        String s = HttpRequest.sendGet(
                "http://192.168.55.20:8088/webservice/getIndustrySave/dfa95dbe9d657116c5613d6b6c05abcd?param=" + param);
        System.out.println(s);
    }

    public static void getIndustryDelete(boolean isExecu) {
        JSONObject j = new JSONObject();
        j.put("industryName", "手机");
        String param = URLEncoder.encode(j.toString());
        if (isExecu) {
            System.out.println(
                    "http://192.168.55.20:8088/webservice/getIndustryDelete/dfa95dbe9d657116c5613d6b6c05abcd?param="
                            + param);
            return;
        }
        String s = HttpRequest.sendGet(
                "http://192.168.55.20:8088/webservice/getIndustryDelete/dfa95dbe9d657116c5613d6b6c05abcd?param="
                        + param);
        System.out.println(s);
    }

    public static void getIndustryUpdate(boolean isExecu) {
        JSONObject j = new JSONObject();
        j.put("industryName", "手机");
        j.put("keywords", new String[]{"大屏", "IOS"});
        j.put("cateGoryIds", new String[]{"1", "2"});
        j.put("cateGoryNames", new String[]{"ccc", "eee"});
        String param = URLEncoder.encode(j.toString());
        if (isExecu) {
            System.out.println(
                    "http://192.168.55.20:8088/webservice/getIndustryUpdate/dfa95dbe9d657116c5613d6b6c05abcd?param="
                            + param);
            return;
        }
        String s = HttpRequest.sendGet(
                "http://192.168.55.20:8088/webservice/getIndustryUpdate/dfa95dbe9d657116c5613d6b6c05abcd?param="
                        + param);
        System.out.println(s);
    }

    public static void main(String[] args) {
        // getSpecialSchedule(false);

        boolean isExecu = true;
        getProductsComparison(true);
        // getIndustryUpdate(true);
        // getIndustrySave(true);
        // getIndustryDelete(true);
        // getIndustryInfo(true);
        // getProductsComparison(isExecu);
        // getSearchResult(false);
        // getNewsByID(isExecu);//3.1.1 正文详情
        // getRelationNew(isExecu);//3.1.3 [相关资讯接口]
        // getSourceList(isExecu);//3.2.1 数据源列表
        // getHotspotRegsiter(isExecu);//3.3.1 热点注册
        // getHotList(isExecu);//3.3.2 获得指定行业指定月份的热点列表
        // getHotResultByID(isExecu);//3.3.3 获得热点资讯列表
        // getSearchResult(false);//3.4.1 检索结果
        // getSearchResult(false);//3.4.1 检索结果
        // getSearchAnalyse(isExecu);//3.4.2 检索分析
        // getSpecialRegsiter(isExecu);//3.5.1 [专题提交(注册)
        // getAllInfoByRowkey(isExecu);
        // updateNewsByIDForPost(false);
        // getSpecialSchedule(isExecu);//3.5.2 [专题进度]
        // getSpecialUpdate(isExecu);//3.5.3 [专题修改]
        // getSpecialResultByID(isExecu);//3.5.4 专题资讯列表
        // getResultByCondition(isExecu);//3.5.5 获取条件资讯
        // getWarnRegsiter(isExecu);//3.6.1 [预警提交(注册)]
        // getWarnUpdate(isExecu);//3.6.2 [预警修改]
        // getWarnStatus(isExecu);//3.6.3 [预警结果]
        // getWarnResult();//3.6.4 [预警结果列表]-
        // getWarnNewsList(isExecu);//3.6.5 [预警结果资讯列表]-
        // getMediaTypeList();//3.7.1 [媒体分类接口]
        // getIndustryTypeList();//3.7.2 [行业分类接口]
        // getWeiboSearchAnalyse();//3.8.1 搜索关键词
        // getWeiboByIDAnalyse();//3.8.2 分析一条微博
        // getSpecialPortraitByIDAnalyse();//3.8.3 专题分析——用户画像
        // getSpecialSentimentByIDAnalyse();//3.8.4 专题分析——情感分析
        // getWeiXinSearchAnalyse();//微信关键词分析
        // getWeiXinByIDAnalyse();//分析某个公众号
        // getLanguagesList();//语言
        // getRegionList();//地区
        // getCountryList();//国家
        // getSearchFastAnalyse(isExecu);
        // getSocialSearchResult(isExecu);
        // getSpecialStatus(isExecu);
        // getAllInfoByTableName(isExecu);
        // getSocialSearchAnalyse(isExecu);
        // getSocialByID(isExecu);
        // updateNewsByID(isExecu);
        // Object obj[]=new Object[]{"1","2"};
        // String a[]=Utils.objArrToStringArr(obj);
        // for(String i:a){
        // System.out.println(i);
        // }
        // int a2[]=Utils.objArrToIntArr(obj);
        // for(int i:a2){
        // System.out.println(i);
        // }
        // Map<String,String> m=new HashMap<String,String>();
        // m.put("key", "values");
        //
        // JSONObject j=new JSONObject();
        // j.put("test",m);
        // System.out.println(j.getString("test"));
        // System.out.println(JSONObject.fromObject(m).toString());
        // String register="3";
        // if(!"1".equals(register)&&!"2".equals(register)){
        // System.out.println("if");
        // }else{
        // System.out.println("else");
        // }
        // String a="[aaaaaaa,b]";
        // a=a.replaceAll("\\[","").replaceAll("\\]","");
        // System.out.println(a);
        // AnalyseServiceI asImpl = new
        // AnalyseServiceImplService().getAnalyseServiceImplPort();
        // asImpl.setInfo(0, "1'");

        // Map<String,String> map=new HashMap<String,String>();
        // map.put("1111", "1");
        // map.put("2222", "2");
        // map.put("3333", "3");
        // map.put("4444", "4");
        // System.out.println(map.containsKey("4444"));
        // Long t=new Long(222);
        // System.out.println(t.longValue());
        // System.out.println(t);
        // Map<String,Object> map=new HashMap<String,Object>();
        // map.put("1111", "1");
        // map.put("2222", new ArrayList());
        // System.out.println(JSONObject.fromObject(map).toString());

        // System.out.println(convertToXMLGregorianCalendar(null));
        // JSONObject obj=new JSONObject();
        // obj.put("test", "");
        // System.out.println(obj);
        // String s="国";
        // for(int i=0;i<=150;i++){
        // s=s+"军";
        // }
        // System.out.println(s);
        // List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
        // Map<String,Object> map=new HashMap<String,Object>();
        // map.put("productName", "iPhone6s");
        // map.put("score", "888");
        // map.put("companyName", "apple");
        // map.put("sentiEles", new String[]{"大屏幕","待机短"});
        // list.add(map);
        //
        // map=new HashMap<String,Object>();
        // map.put("productName", "荣耀");
        // map.put("score", "800");
        // map.put("companyName", "华为");
        // map.put("sentiEles", new String[]{"大屏幕","待机短"});
        // list.add(map);
        // System.out.println(JSONArray.fromObject(list).toString());

        // String jsonStr=JsonUtil.getJson("MEDIA_NOT.json");
        // JSONObject arr=JSONObject.fromObject(jsonStr);
        // Object obj[]=arr.getJSONArray("list").toArray();
        // System.out.println(obj);
        // System.out.println(DateUtil.getDay());
        // getSourceList(isExecu);
        // for(int i=0;i<100000;i++){
        // String jsonStr=ConstantUtil.MEDIA_NOT_STR;
        // JSONObject arr=JSONObject.fromObject(jsonStr);
        // Object mediaNotObj[]=arr.getJSONArray("list").toArray();
        // System.out.println("第"+i+"次"+mediaNotObj.length);
        // }
        // JSONObject map=new JSONObject();
        // map.put("productNames", new String[]{"大屏幕","待机短"});
        // System.out.println(URLEncoder.encode((new
        // String[]{"大屏幕","待机短"}).toString()));
    }

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
}
