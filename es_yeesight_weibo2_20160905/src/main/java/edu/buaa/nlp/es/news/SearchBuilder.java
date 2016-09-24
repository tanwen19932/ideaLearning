package edu.buaa.nlp.es.news;


import edu.buaa.nlp.es.client.ESClient;
import edu.buaa.nlp.es.client.IndexBuilder;
import edu.buaa.nlp.es.constant.Configuration;
import edu.buaa.nlp.es.exception.ExceptionUtil;
import edu.buaa.nlp.es.exception.QueryFormatException;
import edu.buaa.nlp.es.util.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchBuilder {

    private Client client;
    private IndexBuilder builder = null;
    private static Logger logger = Logger.getLogger(SearchBuilder.class);

    //for sensitive
    private static Map<String, Integer> hashLeaders = null;
    private static Map<String, Integer> hashSensiWords = null;
    private static Map<String, Integer> hashLeadersPingyin = null;
    private static Map<String, Integer> hashSensiWordsPingyin = null;
    private boolean handledSensitiveWords = false;
    private static PingyinTool pingyinTool = null;

    //for field搜索
    private static Map<String, String> hashFieldKeywords = null;    //存储可用于域搜索的关键词
    private static Pattern patYinHao = Pattern.compile("(\"[^\"]+\")");
    private static String yinhaoTag = "YH_"; //引号标签

    static {
        pingyinTool = new PingyinTool();

        hashFieldKeywords = new HashMap<String, String>();
        hashFieldKeywords.put("title", "titleZh:<value> or titleEn:<value> or titleSrc:<value>");
        hashFieldKeywords.put("titlezh", "titleZh");
        hashFieldKeywords.put("titleen", "titleEn");
        hashFieldKeywords.put("titlesrc", "titleSrc");
        hashFieldKeywords.put("text", "textSrc:<value> or textEn:<value> or textZh:<value>");
        hashFieldKeywords.put("textsrc", "textSrc");
        hashFieldKeywords.put("texten", "textEn");
        hashFieldKeywords.put("textzh", "textZh");
    }


    public SearchBuilder() {
        try {
            this.client = ESClient.getClient();
        } catch (UnknownHostException e) {
            logger.error(ExceptionUtil.getExceptionTrace(e));
        }
    }

    public SearchBuilder(String clusterName, String serverAddress) {
        try {
            this.client = ESClient.getClient(clusterName, serverAddress);
        } catch (UnknownHostException e) {
            logger.error(ExceptionUtil.getExceptionTrace(e));
        }
    }


    // for sensitive
    public static boolean initSensitiveModels(String leadersFile, String sensiWordsFile) {
        try {
            hashLeaders = new HashMap<String, Integer>();
            hashLeadersPingyin = new HashMap<String, Integer>();
            InputStreamReader isR = new InputStreamReader(new FileInputStream(leadersFile), "utf-8");
            BufferedReader br = new BufferedReader(isR);

            String line = "";
            String clearLine = "";
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                clearLine = CharUtil.ToDBC(line);
                clearLine = CharUtil.removeUnChar(clearLine);
                if (clearLine.length() > 1) {
                    clearLine = pingyinTool.toPinYin(clearLine, "", PingyinTool.Type.LOWERCASE).toLowerCase();
                    hashLeadersPingyin.put(clearLine, 1);
                }
                hashLeaders.put(line.trim().toLowerCase(), 1);

            }
            br.close();
            isR.close();


            hashSensiWords = new HashMap<String, Integer>();
            hashSensiWordsPingyin = new HashMap<String, Integer>();
            isR = new InputStreamReader(new FileInputStream(sensiWordsFile), "utf-8");
            br = new BufferedReader(isR);

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                clearLine = CharUtil.ToDBC(line);
                clearLine = CharUtil.removeUnChar(clearLine);

                if (clearLine.length() > 1) {
                    clearLine = pingyinTool.toPinYin(clearLine, "", PingyinTool.Type.LOWERCASE).toLowerCase();
                    hashSensiWordsPingyin.put(clearLine, 1);
                }
                hashSensiWords.put(line.trim().toLowerCase(), 1);
            }
            br.close();
            isR.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean addLeader(String word) {
        try {
            if (hashLeaders == null) {
                hashLeaders = new HashMap<String, Integer>();
                hashLeadersPingyin = new HashMap<String, Integer>();
            }
            hashLeaders.put(word.trim().toLowerCase(), 1);
            word = CharUtil.ToDBC(word);
            word = CharUtil.removeUnChar(word);

            if (word.length() > 1) {
                word = pingyinTool.toPinYin(word, "", PingyinTool.Type.LOWERCASE).toLowerCase();
                hashLeadersPingyin.put(word, 1);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean addSensiWords(String word) {
        try {
            if (hashSensiWords == null) {
                hashSensiWords = new HashMap<String, Integer>();
                hashSensiWordsPingyin = new HashMap<String, Integer>();
            }
            hashSensiWords.put(word.trim().toLowerCase(), 1);
            word = CharUtil.ToDBC(word);
            word = CharUtil.removeUnChar(word);

            if (word.length() > 1) {
                word = pingyinTool.toPinYin(word, "", PingyinTool.Type.LOWERCASE).toLowerCase();
                hashSensiWordsPingyin.put(word, 1);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private int handleSensitiveWords(String word) {
        try {
            String clearword = CharUtil.ToDBC(word);
            clearword = CharUtil.removeUnChar(clearword).toLowerCase();


            if (clearword.length() > 1) {

                String wordPingyin = pingyinTool.toPinYin(clearword, "", PingyinTool.Type.LOWERCASE);


                if (hashLeadersPingyin != null) {
                    for (String key : hashLeadersPingyin.keySet()) {
                        if (wordPingyin.contains(key)) {
                            return 1;
                        }
                    }
                }

                if (hashSensiWordsPingyin != null) {
                    for (String key : hashSensiWordsPingyin.keySet()) {
                        if (wordPingyin.contains(key)) {
                            return 1;
                        }
                    }
                }
            }

            if (hashLeaders != null) {
                for (String key : hashLeaders.keySet()) {
                    if (word.contains(key)) {
                        return 1;
                    }
                }
            }

            if (hashSensiWords != null) {
                for (String key : hashSensiWords.keySet()) {
                    if (word.contains(key)) {
                        return 1;
                    }
                }
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    /**
     * 验证query是否合法，对不合法部分进行默认初始化
     *
     * @param jsonQuery
     *
     * @return
     */
    private JSONObject initQuery(String jsonQuery) {
        JSONObject obj = JSONObject.fromObject(jsonQuery);
        if (!obj.containsKey(Mapper.Query.KEYWORD)) {
            obj.put(Mapper.Query.KEYWORD, "");
        } else {
            obj.put(Mapper.Query.KEYWORD, obj.getString(Mapper.Query.KEYWORD).replaceAll("/", "//"));
        }
        if (!obj.containsKey(Mapper.Query.PAGE_NO)) {
            obj.put(Mapper.Query.PAGE_NO, Constant.QUERY_PAGE_NO_DEFAULT);
        }
        if (!obj.containsKey(Mapper.Query.PAGE_SIZE)) {
            obj.put(Mapper.Query.PAGE_SIZE, Constant.QUERY_PAGE_SIZE_DEFAULT);
        }
        if (!obj.containsKey(Mapper.Query.INDEX_TYPE)) {
            obj.put(Mapper.Query.INDEX_TYPE, Constant.QUERY_INDEX_TYPE_ALL);
        }
        return obj;
    }

    /**
     * 过滤器
     * 目前只支持日期、地区、语言、媒体级别、媒体、情感、领域分类条件过滤
     *
     * @param jsonFilter
     *
     * @return
     */
    public String filterSearch(String jsonFilter) {
        JSONObject obj = JSONObject.fromObject(jsonFilter);
        if (!obj.containsKey(Mapper.Query.PAGE_NO)) {
            obj.put(Mapper.Query.PAGE_NO, Constant.QUERY_PAGE_NO_DEFAULT);
        }
        if (!obj.containsKey(Mapper.Query.PAGE_SIZE)) {
            obj.put(Mapper.Query.PAGE_SIZE, Constant.QUERY_PAGE_SIZE_DEFAULT);
        }
        if (!obj.containsKey(Mapper.Query.INDEX_TYPE)) {
            obj.put(Mapper.Query.INDEX_TYPE, Constant.QUERY_INDEX_TYPE_ALL);
        }
        //获取过滤器设置
        QueryBuilder filter = filterQuery(obj);

        SearchRequestBuilder srb = client.prepareSearch(
                "news201501", "news201502", "news201503", "news201504", "news201505", "news201506",
                "news201507", "news201508", "news201509", "news201510", "news201511", "news201512",
                "news201601", "news201602", "news201603", "news201604", "news201605", "news201606",
                "news201607", "news201508", "news201609", "news201610", "news201611", "news201612"
        );

        String type = obj.getString(Mapper.Query.INDEX_TYPE);
        if (type != null && Constant.QUERY_INDEX_TYPE_ALL.equals(type)) {
            //TODO: add multiple index types
            srb.setTypes(Configuration.INDEX_TYPE_ARTICLE);
        } else {
            srb.setTypes(type);
        }
        FilteredQueryBuilder fqb = QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(), filter);
        SortBuilder sorter = getSort(obj);
        //设置排序
        //		setSort(srb, obj);
        logger.info("[es-query]-" + fqb.toString());
        logger.info("[es-sort]-" + sorter.toString());
        SearchResponse sr = srb.setQuery(fqb)
                .setMinScore(Configuration.QUERY_RESULT_MIN_SCORE)
                .addSort(sorter)
                .setFrom((obj.getInt(Mapper.Query.PAGE_NO) - 1) * obj.getInt(Mapper.Query.PAGE_SIZE)).setSize(obj.getInt(Mapper.Query.PAGE_SIZE))
                //				.addAggregation(termAggregation(Mapper.FieldArticle.SECONDE_LEVEL))
                .execute().actionGet();
        SearchHits hits = sr.getHits();
        //封装结果
        SearchHit sh = null;
        List<JSONObject> list = new ArrayList<JSONObject>();
        for (int i = 0; i < hits.hits().length; i++) {
            sh = hits.getAt(i);
            //			arr.add(getResultByType(sh));
            list.add(getResultByType(sh, obj.getString(Mapper.Query.RESULT_TYPE)));
        }
        JSONArray arr = JSONArray.fromObject(list);
        JSONObject result = new JSONObject();
        result.put(Mapper.Query.RESULT_LIST, arr);
        result.put(Mapper.Query.RESULT_COUNT, hits.getTotalHits());
        //		result.put(Mapper.Query.RESULT_GROUP, parseAggregationBucket((Terms) sr.getAggregations().get(Mapper.FieldArticle.SECONDE_LEVEL)));
        return result.toString();
    }


    public SearchRequestBuilder buildQuery(JSONObject obj) {

        String key = obj.getString(Mapper.Query.KEYWORD);

        //for sensitive
        if (handledSensitiveWords == false) {
            int sensiResult = handleSensitiveWords(key);
            if (sensiResult == 1) {
                return null;
            }
        }
        handledSensitiveWords = false;


        //cross
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
/*
        String oldKey=JSONObject.fromObject(jsonQuery).getString(Mapper.Query.KEYWORD);
  		boolean isEn=LanguageUtil.isEnglish(oldKey);
		if(!isEn){//检索源语言,非英文
			int k=3;
			QueryBuilder crossQb=searchSrc(obj, k);
			qb.should(crossQb);
		}*/
		/*if(isEn){
//			srcQb=QueryBuilders.multiMatchQuery(key, new String[]{Mapper.FieldArticle.TITLE_SRC, Mapper.FieldArticle.TEXT_SRC, Mapper.FieldArticle.TEXT_EN, Mapper.FieldArticle.TITLE_EN}).type("most_fields").boost(1);
			QueryBuilder initQb=QueryBuilders.queryStringQuery(key).field(Mapper.FieldArticle.TITLE_SRC, 5).field(Mapper.FieldArticle.TEXT_SRC, 3).field(Mapper.FieldArticle.TEXT_EN, 2).field(Mapper.FieldArticle.TITLE_EN,3).field(Mapper.FieldArticle.TITLE_ZH, 3).field(Mapper.FieldArticle.ABSTRACT_ZH, 2);
			qb.must(initQb);
		}else{
//			srcQb=QueryBuilders.multiMatchQuery(key, new String[]{Mapper.FieldArticle.TITLE_SRC, Mapper.FieldArticle.TEXT_SRC, Mapper.FieldArticle.TITLE_ZH}).type("most_fields").boost(1);
			QueryBuilder initQb=QueryBuilders.queryStringQuery(key).field(Mapper.FieldArticle.TITLE_SRC, 5).field(Mapper.FieldArticle.TEXT_SRC, 3).field(Mapper.FieldArticle.TITLE_ZH, 2);
			qb.must(initQb);
		}*/

        QueryBuilder initQb = QueryBuilders.queryStringQuery(key)
                .field(Mapper.FieldArticle.TITLE_SRC, Configuration.TITLE_SRC_WEIGHT) // 100)
                .field(Mapper.FieldArticle.TITLE_EN, Configuration.TITLE_WEIGHT) //10)
                .field(Mapper.FieldArticle.TITLE_ZH, Configuration.TITLE_WEIGHT) //10)
                .field(Mapper.FieldArticle.TEXT_SRC, Configuration.TEXT_WEIGHT) //2)
                .field(Mapper.FieldArticle.TEXT_EN, Configuration.TEXT_WEIGHT) //2)
                .field(Mapper.FieldArticle.TEXT_ZH, Configuration.TEXT_WEIGHT) // 2)

                ;

        QueryBuilder fsqb = initQb;

        //*方案零：写decay函数

        if (Configuration.SEARCH_TYPE == 0) {
            fsqb = QueryBuilders.functionScoreQuery(initQb)
                    //.add(ScoreFunctionBuilders.linearDecayFunction(Mapper.FieldArticle.PUBDATE, "5d").setDecay(0.98).setWeight(20F))
                    .add(ScoreFunctionBuilders.linearDecayFunction(Mapper.FieldArticle.PUBDATE, "" + Configuration.TIME_SCALE + "d").setDecay(Configuration.TIME_DECAY).setWeight(Configuration.TIME_SCALE_WEIGHT))  //.setWeight(20F))
                    .add(ScoreFunctionBuilders.gaussDecayFunction(Mapper.FieldArticle.MEDIA_LEVEL, "" + Configuration.MEDIA_SCALE + "").setDecay(Configuration.MEDIA_DECAY).setWeight(Configuration.MEDIA_SCALE_WEIGHT)) //.setWeight(20F))
                    .scoreMode(Configuration.SCORE_MODE)
            ;
        }

        if (Configuration.SEARCH_TYPE == 3) {
            fsqb = QueryBuilders.functionScoreQuery(initQb)
                    //.add(ScoreFunctionBuilders.linearDecayFunction(Mapper.FieldArticle.PUBDATE, "5d").setDecay(0.98).setWeight(20F))
                    .add(ScoreFunctionBuilders.gaussDecayFunction(Mapper.FieldArticle.PUBDATE, "2016-08-11", "" + Configuration.TIME_SCALE + "d").setDecay(Configuration.TIME_DECAY).setWeight(Configuration.TIME_SCALE_WEIGHT))  //.setWeight(20F))
                    .add(ScoreFunctionBuilders.gaussDecayFunction(Mapper.FieldArticle.MEDIA_LEVEL, "" + Configuration.MEDIA_SCALE + "").setDecay(Configuration.MEDIA_DECAY).setWeight(Configuration.MEDIA_SCALE_WEIGHT)) //.setWeight(20F))
                    .scoreMode(Configuration.SCORE_MODE)
            ;
        }

        if (Configuration.SEARCH_TYPE == 4) {
            fsqb = QueryBuilders.functionScoreQuery(initQb)
                    //.add(ScoreFunctionBuilders.linearDecayFunction(Mapper.FieldArticle.PUBDATE, "5d").setDecay(0.98).setWeight(20F))
                    .add(ScoreFunctionBuilders.linearDecayFunction(Mapper.FieldArticle.PUBDATE, "" + Configuration.TIME_SCALE + "d").setDecay(Configuration.TIME_DECAY).setWeight(Configuration.TIME_SCALE_WEIGHT))  //.setWeight(20F))
                    .add(ScoreFunctionBuilders.gaussDecayFunction(Mapper.FieldArticle.MEDIA_LEVEL, 1).setDecay(Configuration.MEDIA_DECAY).setWeight(Configuration.MEDIA_SCALE_WEIGHT)) //.setWeight(20F))
                    .scoreMode(Configuration.SCORE_MODE)
            ;
        }

        if (Configuration.SEARCH_TYPE == 5) {
            fsqb = QueryBuilders.functionScoreQuery(initQb)
                    //.add(ScoreFunctionBuilders.linearDecayFunction(Mapper.FieldArticle.PUBDATE, "5d").setDecay(0.98).setWeight(20F))
                    .add(ScoreFunctionBuilders.gaussDecayFunction(Mapper.FieldArticle.PUBDATE, "2016-08-11", "" + Configuration.TIME_SCALE + "d").setDecay(Configuration.TIME_DECAY).setWeight(Configuration.TIME_SCALE_WEIGHT))  //.setWeight(20F))
                    .add(ScoreFunctionBuilders.gaussDecayFunction(Mapper.FieldArticle.MEDIA_LEVEL, Configuration.MEDIA_SCALE).setDecay(Configuration.MEDIA_DECAY).setWeight(Configuration.MEDIA_SCALE_WEIGHT)) //.setWeight(20F))
                    .scoreMode(Configuration.SCORE_MODE)
            ;
        }

        if (Configuration.SEARCH_TYPE == 6) {
            fsqb = QueryBuilders.functionScoreQuery(initQb)
                    //.add(ScoreFunctionBuilders.linearDecayFunction(Mapper.FieldArticle.PUBDATE, "5d").setDecay(0.98).setWeight(20F))
                    .add(ScoreFunctionBuilders.linearDecayFunction(Mapper.FieldArticle.PUBDATE, "" + Configuration.TIME_SCALE + "d").setDecay(Configuration.TIME_DECAY).setWeight(Configuration.TIME_SCALE_WEIGHT))  //.setWeight(20F))
                    //.add(ScoreFunctionBuilders.gaussDecayFunction(Mapper.FieldArticle.MEDIA_LEVEL, 1).setDecay(Configuration.MEDIA_DECAY).setWeight(Configuration.MEDIA_SCALE_WEIGHT)) //.setWeight(20F))
                    .scoreMode(Configuration.SCORE_MODE)
            ;
        }

        if (Configuration.SEARCH_TYPE == 7) {
            fsqb = QueryBuilders.functionScoreQuery(initQb)
                    //.add(ScoreFunctionBuilders.linearDecayFunction(Mapper.FieldArticle.PUBDATE, "5d").setDecay(0.98).setWeight(20F))
                    .add(ScoreFunctionBuilders.gaussDecayFunction(Mapper.FieldArticle.PUBDATE, "" + Configuration.TIME_SCALE + "d").setDecay(Configuration.TIME_DECAY).setWeight(Configuration.TIME_SCALE_WEIGHT))  //.setWeight(20F))
                    //.add(ScoreFunctionBuilders.gaussDecayFunction(Mapper.FieldArticle.MEDIA_LEVEL,  Configuration.MEDIA_SCALE ).setDecay(Configuration.MEDIA_DECAY).setWeight(Configuration.MEDIA_SCALE_WEIGHT)) //.setWeight(20F))
                    .scoreMode(Configuration.SCORE_MODE)
            ;
        }

        //*/

        //*方案一：写Script


        if (Configuration.SEARCH_TYPE == 1) {
            //String script = "_score+doc['pubTime'].value*0.000000000001";
            //String script = "doc['pubTime'].value*0.0000000001";

            //String script1 = "" + Constant.TODAY_WEIGHT + ""; //1-2天内的权重偏置
            //String script2 = "exp(doc['pubTime'].value*0.00000000001)";
            //String script3 = "doc['mediaLevel'].value * 10";
            String script2 = "exp(doc['pubTime'].value*" + Configuration.TIME_WEIGHT + ")";
            String script3 = "doc['mediaLevel'].value *" + Configuration.MEDIA_WEIGHT;
            fsqb = QueryBuilders.functionScoreQuery(initQb)
                    .add(ScoreFunctionBuilders.scriptFunction(script2))
                    .add(ScoreFunctionBuilders.scriptFunction(script3))
                    .scoreMode(Configuration.SCORE_MODE)
            ;
        }


        if (Configuration.SEARCH_TYPE == 8) {
            //String script = "_score+doc['pubTime'].value*0.000000000001";
            //String script = "doc['pubTime'].value*0.0000000001";
            long currTime = System.currentTimeMillis() - 86400000;
            //String script1 = "1 + floor(doc['pubTime'].value/" + currTime + ") *" + Configuration.TIME_SCALE_WEIGHT + ""; //1-2天内的权重偏置
            String script1 = "min(max(1,doc['pubTime'].value - " + currTime + ") ," + Configuration.TIME_TODAY_WEIGHT + ")"; //1-2天内的权重偏置
            //String script2 = "exp(doc['pubTime'].value*0.00000000001)";
            //String script3 = "doc['mediaLevel'].value * 10";
            String script2 = "exp(doc['pubTime'].value*" + Configuration.TIME_WEIGHT + ")";
            String script3 = "doc['mediaLevel'].value *" + Configuration.MEDIA_WEIGHT;
            fsqb = QueryBuilders.functionScoreQuery(initQb)
                    .add(ScoreFunctionBuilders.scriptFunction(script1))
                    .add(ScoreFunctionBuilders.scriptFunction(script2))
                    .add(ScoreFunctionBuilders.scriptFunction(script3))
                    .scoreMode(Configuration.SCORE_MODE)
            ;
        }

        if (Configuration.SEARCH_TYPE == 9) {
            //String script = "_score+doc['pubTime'].value*0.000000000001";
            //String script = "doc['pubTime'].value*0.0000000001";
            long currTime = System.currentTimeMillis() - 86400000;
            //String script1 = "1 + floor(doc['pubTime'].value/" + currTime + ") *" + Configuration.TIME_SCALE_WEIGHT + ""; //1-2天内的权重偏置
            String script1 = "min(max(1,doc['pubTime'].value - " + currTime + ") ," + Configuration.TIME_TODAY_WEIGHT + ")"; //1-2天内的权重偏置
            //String script2 = "exp(doc['pubTime'].value*0.00000000001)";
            //String script3 = "doc['mediaLevel'].value * 10";
            //String script2 = "exp(doc['pubTime'].value*" + Configuration.TIME_WEIGHT + ")";
            String script2 = "(doc['pubTime'].value*" + Configuration.TIME_WEIGHT + ")";
            String script3 = "doc['mediaLevel'].value";
            fsqb = QueryBuilders.functionScoreQuery(initQb)
                    .add(ScoreFunctionBuilders.scriptFunction(script1))
                    .add(ScoreFunctionBuilders.scriptFunction(script2))
                    .add(ScoreFunctionBuilders.scriptFunction(script3))
                    .scoreMode(Configuration.SCORE_MODE)
            ;
        }

        if (Configuration.SEARCH_TYPE == 10) {
            //String script = "_score+doc['pubTime'].value*0.000000000001";
            //String script = "doc['pubTime'].value*0.0000000001";
            long currTime = System.currentTimeMillis() - 86400000;
            //String script1 = "1 + floor(doc['pubTime'].value/" + currTime + ") *" + Configuration.TIME_SCALE_WEIGHT + ""; //1-2天内的权重偏置
            String script1 = "min(max(1,doc['pubTime'].value - " + currTime + ") ," + Configuration.TIME_TODAY_WEIGHT + ")"; //1-2天内的权重偏置
            //String script2 = "exp(doc['pubTime'].value*0.00000000001)";
            //String script3 = "doc['mediaLevel'].value * 10";
            //String script2 = "exp(doc['pubTime'].value*" + Configuration.TIME_WEIGHT + ")";
            String script2 = "(doc['pubTime'].value*" + Configuration.TIME_WEIGHT + ")";
            String script3 = "doc['mediaLevel'].value";
            fsqb = QueryBuilders.functionScoreQuery(initQb)
                    .add(ScoreFunctionBuilders.scriptFunction(script1))
                    //.add(ScoreFunctionBuilders.scriptFunction(script2))
                    .add(ScoreFunctionBuilders.scriptFunction(script3))
                    .scoreMode(Configuration.SCORE_MODE)
            ;
        }

        if (Configuration.SEARCH_TYPE == 11) {
            //String script = "_score+doc['pubTime'].value*0.000000000001";
            //String script = "doc['pubTime'].value*0.0000000001";
            long currTime = System.currentTimeMillis() - 86400000;
            //String script1 = "1 + floor(doc['pubTime'].value/" + currTime + ") *" + Configuration.TIME_SCALE_WEIGHT + ""; //1-2天内的权重偏置
            String script1 = "min(max(1,doc['pubTime'].value - " + currTime + ") ," + Configuration.TIME_TODAY_WEIGHT + ")"; //1-2天内的权重偏置
            //String script2 = "exp(doc['pubTime'].value*0.00000000001)";
            //String script3 = "doc['mediaLevel'].value * 10";
            //String script2 = "exp(doc['pubTime'].value*" + Configuration.TIME_WEIGHT + ")";
            String script2 = "(doc['pubTime'].value*" + Configuration.TIME_WEIGHT + ")";
            String script3 = "doc['mediaLevel'].value*" + Configuration.MEDIA_WEIGHT;
            fsqb = QueryBuilders.functionScoreQuery(initQb)
                    .add(ScoreFunctionBuilders.scriptFunction(script1))
                    //.add(ScoreFunctionBuilders.scriptFunction(script2))
                    .add(ScoreFunctionBuilders.scriptFunction(script3))
                    .scoreMode(Configuration.SCORE_MODE)
            ;
        }

        if (Configuration.SEARCH_TYPE == 12) {
            //String script = "_score+doc['pubTime'].value*0.000000000001";
            //String script = "doc['pubTime'].value*0.0000000001";
            long currTime = System.currentTimeMillis() - 86400000;

            //String script1 = "1 + floor(doc['pubTime'].value/" + currTime + ") *" + Configuration.TIME_SCALE_WEIGHT + ""; //1-2天内的权重偏置
            String script1 = "min(max(1,doc['pubTime'].value - " + currTime + ") ," + Configuration.TIME_TODAY_WEIGHT + ")"; //1-2天内的权重偏置
            //String script2 = "exp(doc['pubTime'].value*0.00000000001)";
            //String script3 = "doc['mediaLevel'].value * 10";
            //String script2 = "exp(doc['pubTime'].value*" + Configuration.TIME_WEIGHT + ")";
            String script2 = "exp((doc['pubTime'].value - " + Configuration.BASE_TIME + ") * " + Configuration.TIME_WEIGHT + ")";
            String script3 = "doc['mediaLevel'].value";
            fsqb = QueryBuilders.functionScoreQuery(initQb)
                    .add(ScoreFunctionBuilders.scriptFunction(script1))
                    .add(ScoreFunctionBuilders.scriptFunction(script2))
                    .add(ScoreFunctionBuilders.scriptFunction(script3))
                    .scoreMode(Configuration.SCORE_MODE)
            ;
        }
        //*/

		/*方案二：写插件*/
		/*
		QueryBuilder fsqb=QueryBuilders.functionScoreQuery(initQb)
				.add(ScoreFunctionBuilders.scriptFunction("custom_score", "native"))
				;

		//*/
        //		qb.must(initQb);
        FilteredQueryBuilder fqb = QueryBuilders.filteredQuery(fsqb, filterQuery(obj));
        SearchRequestBuilder srb = client.prepareSearch("news201501", "news201502", "news201503", "news201504", "news201505", "news201506",
                "news201507", "news201508", "news201509", "news201510", "news201511", "news201512",
                "news201601", "news201602", "news201603", "news201604", "news201605", "news201606",
                "news201607", "news201508", "news201609", "news201610", "news201611", "news201612"
        );//Configuration.INDEX_NAME);	//
        srb.setQuery(fqb);
        String type = obj.getString(Mapper.Query.INDEX_TYPE);
        if (type != null && Constant.QUERY_INDEX_TYPE_ALL.equals(type)) {
            srb.setTypes(Configuration.INDEX_TYPE_ARTICLE);
        } else {
            srb.setTypes(type);
        }
        //是否高亮
        boolean highlight = obj.getBoolean(Mapper.Query.HIGHLIGHT);
        if (highlight) {
            srb
                    .addHighlightedField(Mapper.FieldArticle.TITLE_SRC)
                    .addHighlightedField(Mapper.FieldArticle.TITLE_ZH)
                    .addHighlightedField(Mapper.FieldArticle.TITLE_EN)
                    .addHighlightedField(Mapper.FieldArticle.ABSTRACT_EN)
                    .addHighlightedField(Mapper.FieldArticle.ABSTRACT_ZH, 1000)
            ;
        }
        return srb;
    }


    public SearchRequestBuilder buildQueryTotal(JSONObject obj) {

        String key = obj.getString(Mapper.Query.KEYWORD);

        //for sensitive
        if (handledSensitiveWords == false) {
            int sensiResult = handleSensitiveWords(key);
            if (sensiResult == 1) {
                return null;
            }
        }
        handledSensitiveWords = false;


        //cross
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
/*
		String oldKey=JSONObject.fromObject(jsonQuery).getString(Mapper.Query.KEYWORD);
  		boolean isEn=LanguageUtil.isEnglish(oldKey);
		if(!isEn){//检索源语言,非英文
			int k=3;
			QueryBuilder crossQb=searchSrc(obj, k);
			qb.should(crossQb);
		}*/
		/*if(isEn){
//			srcQb=QueryBuilders.multiMatchQuery(key, new String[]{Mapper.FieldArticle.TITLE_SRC, Mapper.FieldArticle.TEXT_SRC, Mapper.FieldArticle.TEXT_EN, Mapper.FieldArticle.TITLE_EN}).type("most_fields").boost(1);
			QueryBuilder initQb=QueryBuilders.queryStringQuery(key).field(Mapper.FieldArticle.TITLE_SRC, 5).field(Mapper.FieldArticle.TEXT_SRC, 3).field(Mapper.FieldArticle.TEXT_EN, 2).field(Mapper.FieldArticle.TITLE_EN,3).field(Mapper.FieldArticle.TITLE_ZH, 3).field(Mapper.FieldArticle.ABSTRACT_ZH, 2);
			qb.must(initQb);
		}else{
//			srcQb=QueryBuilders.multiMatchQuery(key, new String[]{Mapper.FieldArticle.TITLE_SRC, Mapper.FieldArticle.TEXT_SRC, Mapper.FieldArticle.TITLE_ZH}).type("most_fields").boost(1);
			QueryBuilder initQb=QueryBuilders.queryStringQuery(key).field(Mapper.FieldArticle.TITLE_SRC, 5).field(Mapper.FieldArticle.TEXT_SRC, 3).field(Mapper.FieldArticle.TITLE_ZH, 2);
			qb.must(initQb);
		}*/

        QueryBuilder initQb = QueryBuilders.queryStringQuery(key)
                .field(Mapper.FieldArticle.TITLE_SRC, Configuration.TITLE_SRC_WEIGHT) // 100)
                .field(Mapper.FieldArticle.TITLE_EN, Configuration.TITLE_WEIGHT) //10)
                .field(Mapper.FieldArticle.TITLE_ZH, Configuration.TITLE_WEIGHT) //10)
                .field(Mapper.FieldArticle.TEXT_SRC, Configuration.TEXT_WEIGHT) //2)
                .field(Mapper.FieldArticle.TEXT_EN, Configuration.TEXT_WEIGHT) //2)
                .field(Mapper.FieldArticle.TEXT_ZH, Configuration.TEXT_WEIGHT) // 2)

                ;

        QueryBuilder fsqb = initQb;

        //*方案零：写decay函数

        if (Configuration.SEARCH_TYPE == 0) {
            fsqb = QueryBuilders.functionScoreQuery(initQb)
                    //.add(ScoreFunctionBuilders.linearDecayFunction(Mapper.FieldArticle.PUBDATE, "5d").setDecay(0.98).setWeight(20F))
                    .add(ScoreFunctionBuilders.linearDecayFunction(Mapper.FieldArticle.PUBDATE, "" + Configuration.TIME_SCALE + "d").setDecay(Configuration.TIME_DECAY).setWeight(Configuration.TIME_SCALE_WEIGHT))  //.setWeight(20F))
                    .add(ScoreFunctionBuilders.gaussDecayFunction(Mapper.FieldArticle.MEDIA_LEVEL, "" + Configuration.MEDIA_SCALE + "").setDecay(Configuration.MEDIA_DECAY).setWeight(Configuration.MEDIA_SCALE_WEIGHT)) //.setWeight(20F))
                    .scoreMode(Configuration.SCORE_MODE)
            ;
        }

        if (Configuration.SEARCH_TYPE == 3) {
            fsqb = QueryBuilders.functionScoreQuery(initQb)
                    //.add(ScoreFunctionBuilders.linearDecayFunction(Mapper.FieldArticle.PUBDATE, "5d").setDecay(0.98).setWeight(20F))
                    .add(ScoreFunctionBuilders.gaussDecayFunction(Mapper.FieldArticle.PUBDATE, "2016-08-11", "" + Configuration.TIME_SCALE + "d").setDecay(Configuration.TIME_DECAY).setWeight(Configuration.TIME_SCALE_WEIGHT))  //.setWeight(20F))
                    .add(ScoreFunctionBuilders.gaussDecayFunction(Mapper.FieldArticle.MEDIA_LEVEL, "" + Configuration.MEDIA_SCALE + "").setDecay(Configuration.MEDIA_DECAY).setWeight(Configuration.MEDIA_SCALE_WEIGHT)) //.setWeight(20F))
                    .scoreMode(Configuration.SCORE_MODE)
            ;
        }

        if (Configuration.SEARCH_TYPE == 4) {
            fsqb = QueryBuilders.functionScoreQuery(initQb)
                    //.add(ScoreFunctionBuilders.linearDecayFunction(Mapper.FieldArticle.PUBDATE, "5d").setDecay(0.98).setWeight(20F))
                    .add(ScoreFunctionBuilders.linearDecayFunction(Mapper.FieldArticle.PUBDATE, "" + Configuration.TIME_SCALE + "d").setDecay(Configuration.TIME_DECAY).setWeight(Configuration.TIME_SCALE_WEIGHT))  //.setWeight(20F))
                    .add(ScoreFunctionBuilders.gaussDecayFunction(Mapper.FieldArticle.MEDIA_LEVEL, 1).setDecay(Configuration.MEDIA_DECAY).setWeight(Configuration.MEDIA_SCALE_WEIGHT)) //.setWeight(20F))
                    .scoreMode(Configuration.SCORE_MODE)
            ;
        }

        if (Configuration.SEARCH_TYPE == 5) {
            fsqb = QueryBuilders.functionScoreQuery(initQb)
                    //.add(ScoreFunctionBuilders.linearDecayFunction(Mapper.FieldArticle.PUBDATE, "5d").setDecay(0.98).setWeight(20F))
                    .add(ScoreFunctionBuilders.gaussDecayFunction(Mapper.FieldArticle.PUBDATE, "2016-08-11", "" + Configuration.TIME_SCALE + "d").setDecay(Configuration.TIME_DECAY).setWeight(Configuration.TIME_SCALE_WEIGHT))  //.setWeight(20F))
                    .add(ScoreFunctionBuilders.gaussDecayFunction(Mapper.FieldArticle.MEDIA_LEVEL, Configuration.MEDIA_SCALE).setDecay(Configuration.MEDIA_DECAY).setWeight(Configuration.MEDIA_SCALE_WEIGHT)) //.setWeight(20F))
                    .scoreMode(Configuration.SCORE_MODE)
            ;
        }

        if (Configuration.SEARCH_TYPE == 6) {
            fsqb = QueryBuilders.functionScoreQuery(initQb)
                    //.add(ScoreFunctionBuilders.linearDecayFunction(Mapper.FieldArticle.PUBDATE, "5d").setDecay(0.98).setWeight(20F))
                    .add(ScoreFunctionBuilders.linearDecayFunction(Mapper.FieldArticle.PUBDATE, "" + Configuration.TIME_SCALE + "d").setDecay(Configuration.TIME_DECAY).setWeight(Configuration.TIME_SCALE_WEIGHT))  //.setWeight(20F))
                    //.add(ScoreFunctionBuilders.gaussDecayFunction(Mapper.FieldArticle.MEDIA_LEVEL, 1).setDecay(Configuration.MEDIA_DECAY).setWeight(Configuration.MEDIA_SCALE_WEIGHT)) //.setWeight(20F))
                    .scoreMode(Configuration.SCORE_MODE)
            ;
        }

        if (Configuration.SEARCH_TYPE == 7) {
            fsqb = QueryBuilders.functionScoreQuery(initQb)
                    //.add(ScoreFunctionBuilders.linearDecayFunction(Mapper.FieldArticle.PUBDATE, "5d").setDecay(0.98).setWeight(20F))
                    .add(ScoreFunctionBuilders.gaussDecayFunction(Mapper.FieldArticle.PUBDATE, "" + Configuration.TIME_SCALE + "d").setDecay(Configuration.TIME_DECAY).setWeight(Configuration.TIME_SCALE_WEIGHT))  //.setWeight(20F))
                    //.add(ScoreFunctionBuilders.gaussDecayFunction(Mapper.FieldArticle.MEDIA_LEVEL,  Configuration.MEDIA_SCALE ).setDecay(Configuration.MEDIA_DECAY).setWeight(Configuration.MEDIA_SCALE_WEIGHT)) //.setWeight(20F))
                    .scoreMode(Configuration.SCORE_MODE)
            ;
        }

        //*/

        //*方案一：写Script


        if (Configuration.SEARCH_TYPE == 1) {
            //String script = "_score+doc['pubTime'].value*0.000000000001";
            //String script = "doc['pubTime'].value*0.0000000001";

            //String script1 = "" + Constant.TODAY_WEIGHT + ""; //1-2天内的权重偏置
            //String script2 = "exp(doc['pubTime'].value*0.00000000001)";
            //String script3 = "doc['mediaLevel'].value * 10";
            String script2 = "exp(doc['pubTime'].value*" + Configuration.TIME_WEIGHT + ")";
            String script3 = "doc['mediaLevel'].value *" + Configuration.MEDIA_WEIGHT;
            fsqb = QueryBuilders.functionScoreQuery(initQb)
                    .add(ScoreFunctionBuilders.scriptFunction(script2))
                    .add(ScoreFunctionBuilders.scriptFunction(script3))
                    .scoreMode(Configuration.SCORE_MODE)
            ;
        }


        if (Configuration.SEARCH_TYPE == 8) {
            //String script = "_score+doc['pubTime'].value*0.000000000001";
            //String script = "doc['pubTime'].value*0.0000000001";
            long currTime = System.currentTimeMillis() - 86400000;
            //String script1 = "1 + floor(doc['pubTime'].value/" + currTime + ") *" + Configuration.TIME_SCALE_WEIGHT + ""; //1-2天内的权重偏置
            String script1 = "min(max(1,doc['pubTime'].value - " + currTime + ") ," + Configuration.TIME_TODAY_WEIGHT + ")"; //1-2天内的权重偏置
            //String script2 = "exp(doc['pubTime'].value*0.00000000001)";
            //String script3 = "doc['mediaLevel'].value * 10";
            String script2 = "exp(doc['pubTime'].value*" + Configuration.TIME_WEIGHT + ")";
            String script3 = "doc['mediaLevel'].value *" + Configuration.MEDIA_WEIGHT;
            fsqb = QueryBuilders.functionScoreQuery(initQb)
                    .add(ScoreFunctionBuilders.scriptFunction(script1))
                    .add(ScoreFunctionBuilders.scriptFunction(script2))
                    .add(ScoreFunctionBuilders.scriptFunction(script3))
                    .scoreMode(Configuration.SCORE_MODE)
            ;
        }

        if (Configuration.SEARCH_TYPE == 9) {
            //String script = "_score+doc['pubTime'].value*0.000000000001";
            //String script = "doc['pubTime'].value*0.0000000001";
            long currTime = System.currentTimeMillis() - 86400000;
            //String script1 = "1 + floor(doc['pubTime'].value/" + currTime + ") *" + Configuration.TIME_SCALE_WEIGHT + ""; //1-2天内的权重偏置
            String script1 = "min(max(1,doc['pubTime'].value - " + currTime + ") ," + Configuration.TIME_TODAY_WEIGHT + ")"; //1-2天内的权重偏置
            //String script2 = "exp(doc['pubTime'].value*0.00000000001)";
            //String script3 = "doc['mediaLevel'].value * 10";
            //String script2 = "exp(doc['pubTime'].value*" + Configuration.TIME_WEIGHT + ")";
            String script2 = "(doc['pubTime'].value*" + Configuration.TIME_WEIGHT + ")";
            String script3 = "doc['mediaLevel'].value";
            fsqb = QueryBuilders.functionScoreQuery(initQb)
                    .add(ScoreFunctionBuilders.scriptFunction(script1))
                    .add(ScoreFunctionBuilders.scriptFunction(script2))
                    .add(ScoreFunctionBuilders.scriptFunction(script3))
                    .scoreMode(Configuration.SCORE_MODE)
            ;
        }

        if (Configuration.SEARCH_TYPE == 10) {
            //String script = "_score+doc['pubTime'].value*0.000000000001";
            //String script = "doc['pubTime'].value*0.0000000001";
            long currTime = System.currentTimeMillis() - 86400000;
            //String script1 = "1 + floor(doc['pubTime'].value/" + currTime + ") *" + Configuration.TIME_SCALE_WEIGHT + ""; //1-2天内的权重偏置
            String script1 = "min(max(1,doc['pubTime'].value - " + currTime + ") ," + Configuration.TIME_TODAY_WEIGHT + ")"; //1-2天内的权重偏置
            //String script2 = "exp(doc['pubTime'].value*0.00000000001)";
            //String script3 = "doc['mediaLevel'].value * 10";
            //String script2 = "exp(doc['pubTime'].value*" + Configuration.TIME_WEIGHT + ")";
            String script2 = "(doc['pubTime'].value*" + Configuration.TIME_WEIGHT + ")";
            String script3 = "doc['mediaLevel'].value";
            fsqb = QueryBuilders.functionScoreQuery(initQb)
                    .add(ScoreFunctionBuilders.scriptFunction(script1))
                    //.add(ScoreFunctionBuilders.scriptFunction(script2))
                    .add(ScoreFunctionBuilders.scriptFunction(script3))
                    .scoreMode(Configuration.SCORE_MODE)
            ;
        }

        if (Configuration.SEARCH_TYPE == 11) {
            //String script = "_score+doc['pubTime'].value*0.000000000001";
            //String script = "doc['pubTime'].value*0.0000000001";
            long currTime = System.currentTimeMillis() - 86400000;
            //String script1 = "1 + floor(doc['pubTime'].value/" + currTime + ") *" + Configuration.TIME_SCALE_WEIGHT + ""; //1-2天内的权重偏置
            String script1 = "min(max(1,doc['pubTime'].value - " + currTime + ") ," + Configuration.TIME_TODAY_WEIGHT + ")"; //1-2天内的权重偏置
            //String script2 = "exp(doc['pubTime'].value*0.00000000001)";
            //String script3 = "doc['mediaLevel'].value * 10";
            //String script2 = "exp(doc['pubTime'].value*" + Configuration.TIME_WEIGHT + ")";
            String script2 = "(doc['pubTime'].value*" + Configuration.TIME_WEIGHT + ")";
            String script3 = "doc['mediaLevel'].value*" + Configuration.MEDIA_WEIGHT;
            fsqb = QueryBuilders.functionScoreQuery(initQb)
                    .add(ScoreFunctionBuilders.scriptFunction(script1))
                    //.add(ScoreFunctionBuilders.scriptFunction(script2))
                    .add(ScoreFunctionBuilders.scriptFunction(script3))
                    .scoreMode(Configuration.SCORE_MODE)
            ;
        }

        if (Configuration.SEARCH_TYPE == 12) {
            //String script = "_score+doc['pubTime'].value*0.000000000001";
            //String script = "doc['pubTime'].value*0.0000000001";
            long currTime = System.currentTimeMillis() - 86400000;

            //String script1 = "1 + floor(doc['pubTime'].value/" + currTime + ") *" + Configuration.TIME_SCALE_WEIGHT + ""; //1-2天内的权重偏置
            String script1 = "min(max(1,doc['pubTime'].value - " + currTime + ") ," + Configuration.TIME_TODAY_WEIGHT + ")"; //1-2天内的权重偏置
            //String script2 = "exp(doc['pubTime'].value*0.00000000001)";
            //String script3 = "doc['mediaLevel'].value * 10";
            //String script2 = "exp(doc['pubTime'].value*" + Configuration.TIME_WEIGHT + ")";
            String script2 = "exp((doc['pubTime'].value - " + Configuration.BASE_TIME + ") * " + Configuration.TIME_WEIGHT + ")";
            String script3 = "doc['mediaLevel'].value";
            fsqb = QueryBuilders.functionScoreQuery(initQb)
                    .add(ScoreFunctionBuilders.scriptFunction(script1))
                    .add(ScoreFunctionBuilders.scriptFunction(script2))
                    .add(ScoreFunctionBuilders.scriptFunction(script3))
                    .scoreMode(Configuration.SCORE_MODE)
            ;
        }
        //*/

		/*方案二：写插件*/
		/*
		QueryBuilder fsqb=QueryBuilders.functionScoreQuery(initQb)
				.add(ScoreFunctionBuilders.scriptFunction("custom_score", "native"))
				;

		//*/
        //		qb.must(initQb);
        FilteredQueryBuilder fqb = QueryBuilders.filteredQuery(fsqb, filterQuery(obj));
        SearchRequestBuilder srb = client.prepareSearch(
                "news201601", "news201602", "news201603", "news201604", "news201605", "news201606",
                "news201607", "news201508", "news201609", "news201610", "news201611", "news201612",
                "news201501", "news201502", "news201503", "news201504", "news201505", "news201506",
                "news201507", "news201508", "news201509", "news201510", "news201511", "news201512",
                "news201401", "news201402", "news201403", "news201404", "news201405", "news201406",
                "news201407", "news201408", "news201409", "news201410", "news201411", "news201412",
                "news201301", "news201302", "news201303", "news201304", "news201305", "news201306",
                "news201307", "news201308", "news201309", "news201310", "news201311", "news201312",
                "news201201", "news201202", "news201203", "news201204", "news201205", "news201206",
                "news201207", "news201208", "news201209", "news201210", "news201211", "news201212",
                "news201101", "news201102", "news201103", "news201104", "news201105", "news201106",
                "news201107", "news201108", "news201109", "news201110", "news201111", "news201112",
                "news201001", "news201002", "news201003", "news201004", "news201005", "news201006",
                "news201007", "news201008", "news201009", "news201010", "news201011", "news201012"
        );//Configuration.INDEX_NAME);	//
        srb.setQuery(fqb);
        String type = obj.getString(Mapper.Query.INDEX_TYPE);
        if (type != null && Constant.QUERY_INDEX_TYPE_ALL.equals(type)) {
            srb.setTypes(Configuration.INDEX_TYPE_ARTICLE);
        } else {
            srb.setTypes(type);
        }
        //是否高亮
        boolean highlight = obj.getBoolean(Mapper.Query.HIGHLIGHT);
        if (highlight) {
            srb
                    .addHighlightedField(Mapper.FieldArticle.TITLE_SRC)
                    .addHighlightedField(Mapper.FieldArticle.TITLE_ZH)
                    .addHighlightedField(Mapper.FieldArticle.TITLE_EN)
                    .addHighlightedField(Mapper.FieldArticle.ABSTRACT_EN)
                    .addHighlightedField(Mapper.FieldArticle.ABSTRACT_ZH, 1000)
            ;
        }
        return srb;
    }


    /**
     * 高级检索
     * 1.替换逻辑符号，空格替换为||
     * 2.判断查询语种，做跨语言扩展
     * 3.检索
     *
     * @param jsonQuery
     *
     * @return
     */
    public String crossSearch(String jsonQuery) {

        JSONObject obj = null;
        try {
            obj = JSONObject.fromObject(jsonQuery);
            String keyword = obj.getString(Mapper.Query.KEYWORD);
            if (this.handleSensitiveWords(keyword) == 1) {
                return null;
            }
            handledSensitiveWords = true;
            obj = initAdvancedQuery(jsonQuery);
        } catch (QueryFormatException e) {
            logger.error(ExceptionUtil.getExceptionTrace(e));
            return null;
        }

        try {
            SearchRequestBuilder srb = buildQuery(obj);
            if (srb == null) return null;
            //			setSort(srb, obj);
            srb .addSort(getSort(obj))
                .setMinScore(Configuration.QUERY_RESULT_MIN_SCORE) //最低分值
                .setFrom((obj.getInt(Mapper.Query.PAGE_NO) - 1) * obj.getInt(Mapper.Query.PAGE_SIZE))
                .setSize(obj.getInt(Mapper.Query.PAGE_SIZE));
            if(!ValidateQuery.check(Configuration.INDEX_NAME,Configuration.INDEX_TYPE_ARTICLE,srb.toString())){
                return  null;
            };
            SearchResponse sr = srb.execute().actionGet();
            SearchHits hits = sr.getHits();
            logger.info("[es-query]-" + srb.toString());
            //封装结果
            SearchHit sh = null;
            List<JSONObject> list = new ArrayList<JSONObject>();
            for (int i = 0; i < hits.hits().length; i++) {
                try {
                    sh = hits.getAt(i);
                    //System.out.println(sh.getScore());
                    list.add(getResultByType(sh, obj.getString(Mapper.Query.RESULT_TYPE)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            JSONArray arr = JSONArray.fromObject(list);
            JSONObject result = new JSONObject();
            result.put(Mapper.Query.RESULT_LIST, arr);
            result.put(Mapper.Query.RESULT_COUNT, hits.getTotalHits());
            //			long e1=System.currentTimeMillis();
            //			System.out.println("time:"+(e1-s1)/1000);
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"resultList\":[],\"resultCount\":0}";
        }
    }

    /**
     * 为专题分析提供，采用scroll
     *
     * @param jsonQuery
     *
     * @return
     */
    public String specialSearch(String jsonQuery) {
        long s1 = System.currentTimeMillis();
        JSONObject obj = null;
        try {
            obj = initAdvancedQuery(jsonQuery);
        } catch (QueryFormatException e) {
            logger.error(ExceptionUtil.getExceptionTrace(e));
            return "";
        }
        SearchRequestBuilder srb = buildQuery(obj);
        if (srb == null) return "";
        int shards = greenShards(obj);
        if (shards == 0) return "";
        int goal = obj.getInt(Mapper.Query.PAGE_SIZE);
        if (goal > shards) {
            goal = (int) Math.ceil(1.0 * goal / shards);
        }
        SearchResponse sr1 = srb
                .addSort(getSort(obj))
                //				.setMinScore(Mapper.QUERY_RESULT_MIN_SCORE) //最低分值
                .setSearchType(SearchType.SCAN)
                .setSize(goal)
                .setScroll(new TimeValue(6000))
                .execute().actionGet();
        logger.info("[es-query]-" + srb.toString());
        List<JSONObject> list = new ArrayList<JSONObject>();
        while (true) {
            SearchResponse sr2 = client.prepareSearchScroll(sr1.getScrollId())
                    .setScroll(new TimeValue(10))
                    .execute().actionGet();
            //封装结果
            SearchHits hits = sr2.getHits();
            if (hits.getHits().length == 0) break;
            SearchHit sh = null;
            for (int i = 0; i < hits.hits().length; i++) {
                sh = hits.getAt(i);
                list.add(getResultByType(sh, Constant.QUERY_RESULT_ANALYSIS));
            }
        }
        JSONArray arr = JSONArray.fromObject(list);
        JSONObject result = new JSONObject();
        result.put(Mapper.Query.RESULT_LIST, arr);
        result.put(Mapper.Query.RESULT_COUNT, sr1.getHits().totalHits());
        long e1 = System.currentTimeMillis();
        System.out.println("time:" + (e1 - s1) / 1000);
        return result.toString();
    }

    //状态为green的shard数量
    private int greenShards(JSONObject obj) {
        ClusterHealthResponse chr = client.admin().cluster()
                .prepareHealth(Configuration.INDEX_NAME)
                .setWaitForGreenStatus()
                .get();
        ClusterHealthStatus status = chr.getStatus();
        if (status.equals(ClusterHealthStatus.GREEN)) {
            return chr.getActiveShards();
        }
        return 0;
    }

    public JSONObject initAdvancedQuery(String jsonQuery)
            throws QueryFormatException {
        //保证基本信息完整

        JSONObject obj = initQuery(jsonQuery);
        String keyword = obj.getString(Mapper.Query.KEYWORD);
        keyword = initKeyword(keyword);
        System.out.println(keyword);
        obj.put(Mapper.Query.KEYWORD, keyword.trim());

		/*
		//for test the following code
		JSONObject qb3=new JSONObject();
		qb3.put(Mapper.AdvancedQuery.FIELD, Mapper.FieldArticle.ABSTRACT_ZH);
		qb3.put(Mapper.AdvancedQuery.KEYWORD, "降水");
		qb3.put(Mapper.AdvancedQuery.OPERATOR, Constant.QUERY_OPERATOR_AND);

		JSONObject qb4=new JSONObject();
		qb4.put(Mapper.AdvancedQuery.FIELD, Mapper.FieldArticle.TITLE_ZH);
		qb4.put(Mapper.AdvancedQuery.KEYWORD, "长江");
		qb4.put(Mapper.AdvancedQuery.OPERATOR, Constant.QUERY_OPERATOR_OR);
		JSONArray adArr=new JSONArray();
		adArr.add(qb3);
		adArr.add(qb4);
		obj.put(Mapper.AdvancedQuery.QUERY_BODY, adArr.toString());
		*/


        //
		/*
		JSONArray arr=JSONArray.fromObject(obj.getString(Mapper.AdvancedQuery.QUERY_BODY));
		StringBuffer sb=new StringBuffer();
		Iterator<JSONObject> it=arr.iterator();
		//解析查询主体部分
		while(it.hasNext()){
			JSONObject ele=it.next();
			if(Constant.isNullKey(ele, Mapper.AdvancedQuery.KEYWORD)) continue;
			if(Constant.isNullKey(ele, Mapper.AdvancedQuery.FIELD)) throw new QueryFormatException("高级检索异常: 必须指定关键词 ["+ele.getString(Mapper.AdvancedQuery.KEYWORD)+"] 所在的查询域。");
			if(sb.length()!=0){
				if(!Constant.isNullKey(ele, Mapper.AdvancedQuery.OPERATOR)){
					if(Constant.QUERY_OPERATOR_NOT.equals(ele.getString(Mapper.AdvancedQuery.OPERATOR))){
						//非逻辑需要在关键字之前添加AND操作符
						sb.append(Constant.QUERY_OPERATOR_AND+" ");
						ele.put(Mapper.AdvancedQuery.KEYWORD, Constant.QUERY_OPERATOR_NOT+" "+ele.getString(Mapper.AdvancedQuery.KEYWORD));
					}else{
						sb.append(ele.getString(Mapper.AdvancedQuery.OPERATOR) +" ");
					}
				}else{
					sb.append(Constant.QUERY_OPERATOR_AND +" ");
				}
			}
			sb.append(ele.getString(Mapper.AdvancedQuery.FIELD)+":(");
			sb.append(ele.getString(Mapper.AdvancedQuery.KEYWORD)+") ");
		}
		obj.put(Mapper.AdvancedQuery.KEYWORD, sb.toString().replaceAll("/", "//"));
		*/

		/*//for test
		//String query = "(titleZh:(手机) and (titleZh:(行业) or titleZh:(发布) or  titleZh:(政策))";
		//String query = "titleZh:\"手机\" and (titleZh:\"行业\"  or titleZh:\"发布\" or  titleZh:\"政策\")";
		//obj.put(Mapper.AdvancedQuery.KEYWORD, query); */

        return obj;
    }


    public String initKeyword(String keyword)
            throws QueryFormatException {
        //keyword = "titleZh:\"中国\"  or titleEn:\"中国\" or titleSrc:\"中国\"";
        //keyword = "TITLE:冰棍";
        //keyword = "\"冰棍\"";

        //TODO 需进一步提高容错性

        //把 引号的独立出来作为一个词
        Matcher matYinhao = patYinHao.matcher(keyword);
        int index = 0;
        Map<String, String> hashYinhao = new HashMap<String, String>();
        while (matYinhao.find()) {
            ++index;
            String yinhaoGroup = matYinhao.group(1);
            hashYinhao.put(yinhaoTag + index, yinhaoGroup);
        }
        for (String key : hashYinhao.keySet()) {
            keyword = keyword.replace(hashYinhao.get(key), key);
        }
        keyword = checkBracketByRegexList(keyword);
        keyword = keyword.trim();
        keyword = keyword.replaceAll("((and|not|or)\\s*)+\\s*\\(\\s*\\)", "");
        keyword = keyword.replaceAll("((and|not|or)\\s*)+\\s*\\(\"\\s*\"\\)", "");
        keyword = keyword.replaceAll("and\\s*\\(\"\\s*\"\\)", "");
        keyword = keyword.replaceAll("and\\s+not\\s*\\(\\s*\\)", "");
        keyword = keyword.replaceAll("and\\s*\\(\\s*\\)", "");
        keyword = keyword.replaceAll("\\(\\s*\\)", "");
        keyword = keyword.replaceAll("(（|\\()", " ( ")
                .replaceAll("(）|\\))", " ) ")
                .replaceAll("“", " \" ")
                .replaceAll("”", " \" ")
                .trim();


        keyword = keyword.replaceAll("\\s+", " ");
        keyword = keyword.replaceAll("(and|AND)\\s+(not|NOT)", "_AND_NOT_");
        keyword = keyword.replaceAll("and not", " ");
        keyword = keyword.replaceAll("\\s+(or|OR)\\s+", Constant.QUERY_OPERATOR_OR2);
        keyword = keyword.replaceAll("\\s+(and|AND)\\s+", Constant.QUERY_OPERATOR_AND2);
        keyword = keyword.replaceAll("\\s+(not|NOT)\\s+", Constant.QUERY_OPERATOR_NOT2);

        keyword = keyword.replaceAll("\\(", " ( ")
                .replaceAll("\\s*\\(\\s*", "_(_")
                .replaceAll("\\s*\\)\\s*", "_)_")
                .trim();

        keyword = keyword.replace(" ", Constant.QUERY_OPERATOR_AND)
                .replace("&", Constant.QUERY_OPERATOR_AND)
                .replace("|", Constant.QUERY_OPERATOR_OR)
                .replace("~", Constant.QUERY_OPERATOR_NOT)
        ;

        keyword = keyword.replaceAll("_AND_NOT_", " and not ");
        keyword = keyword.replaceAll(Constant.QUERY_OPERATOR_OR2, " " + Constant.QUERY_OPERATOR_OR + " ");
        keyword = keyword.replaceAll(Constant.QUERY_OPERATOR_AND2, " " + Constant.QUERY_OPERATOR_AND + " ");
        keyword = keyword.replaceAll(Constant.QUERY_OPERATOR_NOT2, " " + Constant.QUERY_OPERATOR_NOT + " ");


        keyword = keyword.replace("_(_", " ( ");
        keyword = keyword.replace("_)_", " ) ");

        String[] items = keyword.split("\\s+");
        keyword = "";

        for (String item : items) {
            if (item.trim().isEmpty()) continue;
            if (item.trim().equalsIgnoreCase("and")
                    || item.equalsIgnoreCase("not")
                    || item.equalsIgnoreCase("or")
                    || item.equalsIgnoreCase("(")
                    || item.equalsIgnoreCase(")")
                    ) {
                keyword += item + " ";
            } else if (item.startsWith("\"") && item.endsWith("\"")) {
                keyword += item + " ";
            } else {
                //对于每个单元，都是用 引号 强括号
                if (item.contains(":")) {
                    String[] subItem = item.split(":");
                    if (subItem.length == 2) {
                        if (hashFieldKeywords.containsKey(subItem[0].toLowerCase())) {
                            String value = subItem[1];
                            if (value.startsWith("\"") && item.endsWith("\"")) {
                                ;
                            } else {
                                value = "\"" + value + "\"";
                            }
                            String field = hashFieldKeywords.get(subItem[0].toLowerCase());
                            if (field.contains("or")) {
                                keyword = " " + field.replaceAll("<value>", value) + " ";
                            } else {
                                keyword = " " + field + ":" + value + " ";
                            }

                        } else {
                            keyword += " \"" + item + "\" ";
                        }
                    } else {
                        keyword += " \"" + item + "\" ";
                    }
                } else {

                    keyword += " \"" + item + "\" ";
                }
            }

        }

        keyword = keyword.replaceAll("(AND\\s+OR\\s+)+", " OR ");
        keyword = keyword.replaceAll("(OR\\s+AND\\s+)+", " OR ");
        keyword = keyword.replaceAll("(AND\\s+NOT\\s+)+", " NOT ");
        keyword = keyword.replaceAll("(NOT\\s+AND\\s+)+", " NOT ");
        keyword = keyword.replaceAll("(OR\\s+)+", " OR ");
        keyword = keyword.replaceAll("(NOT\\s+)+", " NOT ");
        keyword = keyword.replaceAll("(AND\\s+)+", " AND ");
        keyword = keyword.replaceAll("\\s+", " ");

        keyword = keyword.trim();

        for (String key : hashYinhao.keySet()) {
            keyword = keyword.replace("\"" + key + "\"", hashYinhao.get(key));
            keyword = keyword.replace(key + "(?=^\\d)", hashYinhao.get(key));
        }
        return keyword;
    }


    private String checkBracketByRegexList(String keyword) {
        List<String> regexes = new ArrayList<>();
        regexes.add("(and|not|or)\\s*\\(\\s*\\)");
        regexes.add("(and|not|or)\\s*\\s*\\(\"\\s*\"\\)");
        regexes.add("and\\s*\\(\"\\s*\\)");
        regexes.add("and\\s+not\\s*\\(\\s*\\)");
        regexes.add("and\\s*\\(\\s*\\)");
        regexes.add("\\(\\s*\\)");
        while (true) {
            String temp = keyword;
            for (String tempRegex : regexes) {
                try {
                    keyword = checkBracket(tempRegex, keyword);
                } catch (Exception e) {
                    System.out.println(tempRegex);
                    e.printStackTrace();

                }
            }
            if (keyword.equals(temp)) {
                break;
            }
        }
        return keyword;
    }

    private String checkBracket(String regex, String keyword) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(keyword);
        if (matcher.find()) {
            keyword = matcher.replaceAll("");
        }
        return keyword;
    }


    /**
     * 封装查询过滤器
     * 过滤内容包括：
     * 日期、地区、语言、媒体级别、情感、领域
     *
     * @param jsonQuery
     *
     * @return
     */
    private QueryBuilder filterQuery(JSONObject jsonQuery) {
        List<QueryBuilder> filters = new ArrayList<QueryBuilder>();

        //日期过滤
        QueryBuilder fbDate = null;
        if (Constant.isNullKey(jsonQuery, Mapper.AdvancedQuery.FIELD_BEGIN_DATE)) {
            if (!Constant.isNullKey(jsonQuery, Mapper.AdvancedQuery.FIELD_END_DATE)) {
                fbDate = QueryBuilders.rangeQuery(Mapper.FieldArticle.PUBDATE_SORT)
                        .gte(DateUtil.time2Unix("1970-01-01 00:00:00"))
                        .lte(DateUtil.time2Unix(jsonQuery.getString(Mapper.AdvancedQuery.FIELD_END_DATE)));
                filters.add(fbDate);
            }
        } else {
            if (Constant.isNullKey(jsonQuery, Mapper.AdvancedQuery.FIELD_END_DATE)) {
                fbDate = QueryBuilders.rangeQuery(Mapper.FieldArticle.PUBDATE_SORT)
                        .gte(DateUtil.time2Unix(jsonQuery.getString(Mapper.AdvancedQuery.FIELD_BEGIN_DATE)))
                        .lte(DateUtil.time2Unix(DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss")));
            } else {
                fbDate = QueryBuilders.rangeQuery(Mapper.FieldArticle.PUBDATE_SORT)
                        .gte(DateUtil.time2Unix(jsonQuery.getString(Mapper.AdvancedQuery.FIELD_BEGIN_DATE)))
                        .lte(DateUtil.time2Unix(jsonQuery.getString(Mapper.AdvancedQuery.FIELD_END_DATE)));
            }
            filters.add(fbDate);
        }
        //		//地区
        //		FilterBuilder fbRegion=null;
        //		if(!Constant.isNullKey(jsonQuery, Mapper.AdvancedQuery.FIELD_REGION)){
        //			JSONArray arr=jsonQuery.getJSONArray(Mapper.AdvancedQuery.FIELD_REGION);
        //			int[] regionIds=new int[arr.size()];
        //			for(int i=0; i<arr.size(); i++){
        //				regionIds[i]=arr.getInt(i);
        //			}
        //			fbRegion=FilterBuilders.inFilter(Mapper.FieldArticle.REGION_ID, regionIds);
        //			filters.add(fbRegion);
        //		}
        QueryBuilder fbCountryNameZh = null;
        if (!Constant.isNullKey(jsonQuery, Mapper.AdvancedQuery.FIELD_COUNTRY_NAME_ZH)) {
            JSONArray arr = jsonQuery.getJSONArray(Mapper.AdvancedQuery.FIELD_COUNTRY_NAME_ZH);
            String[] regionIds = new String[arr.size()];
            for (int i = 0; i < arr.size(); i++) {
                regionIds[i] = arr.getString(i);
            }
            fbCountryNameZh = QueryBuilders.termsQuery(Mapper.FieldArticle.COUNTRY_NAME_ZH, regionIds);
            filters.add(fbCountryNameZh);
        }


        //语言
        QueryBuilder fbLang = null;
        if (!Constant.isNullKey(jsonQuery, Mapper.AdvancedQuery.FIELD_LANGUAGE)) {
            JSONArray arr = jsonQuery.getJSONArray(Mapper.AdvancedQuery.FIELD_LANGUAGE);
            String[] langIds = new String[arr.size()];
            for (int i = 0; i < arr.size(); i++) {
                langIds[i] = arr.getString(i);
            }
            fbLang = QueryBuilders.termsQuery(Mapper.FieldArticle.LANGUAGE_CODE, langIds);
            filters.add(fbLang);
        }

        QueryBuilder fbLangTName = null;
        if (!Constant.isNullKey(jsonQuery, Mapper.AdvancedQuery.FIELD_LANGUAGE_TNAME)) {
            JSONArray arr = jsonQuery.getJSONArray(Mapper.AdvancedQuery.FIELD_LANGUAGE_TNAME);
            String[] langIds = new String[arr.size()];
            for (int i = 0; i < arr.size(); i++) {
                langIds[i] = arr.getString(i);
            }
            fbLangTName = QueryBuilders.termsQuery(Mapper.FieldArticle.LANGUAGE_TNAME, langIds);
            filters.add(fbLangTName);
        }


        //媒体级别
        QueryBuilder fbMediaLevel = null;
        if (!Constant.isNullKey(jsonQuery, Mapper.AdvancedQuery.FIELD_MEDIA_LEVEL)) {
            JSONArray arr = jsonQuery.getJSONArray(Mapper.AdvancedQuery.FIELD_MEDIA_LEVEL);
            int[] levelIds = new int[arr.size()];
            for (int i = 0; i < arr.size(); i++) {
                levelIds[i] = arr.getInt(i);
            }
            fbMediaLevel = QueryBuilders.termsQuery(Mapper.FieldArticle.MEDIA_LEVEL, levelIds);
            filters.add(fbMediaLevel);
        }
        //情感
        QueryBuilder fbSentiment = null;
        if (!Constant.isNullKey(jsonQuery, Mapper.AdvancedQuery.FIELD_SENTIMENT)) {
            JSONArray arr = jsonQuery.getJSONArray(Mapper.AdvancedQuery.FIELD_SENTIMENT);
            int[] sentimentIds = new int[arr.size()];
            for (int i = 0; i < arr.size(); i++) {
                sentimentIds[i] = arr.getInt(i);
            }
            fbSentiment = QueryBuilders.termsQuery(Mapper.FieldArticle.SENTIMENT_ID, sentimentIds);
            filters.add(fbSentiment);
        }
        //领域
        QueryBuilder fbCategory = null;
        if (!Constant.isNullKey(jsonQuery, Mapper.AdvancedQuery.FIELD_CATEGORY)) {
            JSONArray arr = jsonQuery.getJSONArray(Mapper.AdvancedQuery.FIELD_CATEGORY);
            int[] categoryIds = new int[arr.size()];
            for (int i = 0; i < arr.size(); i++) {
                categoryIds[i] = arr.getInt(i);
            }
            fbCategory = QueryBuilders.termsQuery(Mapper.FieldArticle.CATEGORY_ID, categoryIds);
            filters.add(fbCategory);
        }


        //敏感
        //*
        QueryBuilder fbSensitive = null;
        if (!Constant.isNullKey(jsonQuery, Mapper.AdvancedQuery.FIELD_SENSITIVE)) {
            JSONArray arr = jsonQuery.getJSONArray(Mapper.AdvancedQuery.FIELD_SENSITIVE);
            int[] sensitiveIds = new int[arr.size()];
            for (int i = 0; i < arr.size(); i++) {
                sensitiveIds[i] = arr.getInt(i);
            }
            fbSensitive = QueryBuilders.termsQuery(Mapper.FieldArticle.IS_SENSITIVE, sensitiveIds);
            filters.add(fbSensitive);
        } else {

            int[] sensitiveIds = new int[1];
            sensitiveIds[0] = 0;
            fbSensitive = QueryBuilders.termsQuery(Mapper.FieldArticle.IS_SENSITIVE, sensitiveIds);
            filters.add(fbSensitive);
        }
        //*/


        //		//媒体
        //		FilterBuilder fbMedia=null;
        //		if(!Constant.isNullKey(jsonQuery, Mapper.AdvancedQuery.FIELD_MEDIA)){
        //			JSONArray arr=jsonQuery.getJSONArray(Mapper.AdvancedQuery.FIELD_MEDIA);
        //			int[] mediaIds=new int[arr.size()];
        //			for(int i=0; i<arr.size(); i++){
        //				mediaIds[i]=arr.getInt(i);
        //			}
        //			fbMedia=FilterBuilders.inFilter(Mapper.FieldArticle.MEDIA_ID, mediaIds);
        //			filters.add(fbMedia);
        //		}

        //媒体类型
        QueryBuilder fbMediaType = null;
        if (!Constant.isNullKey(jsonQuery, Mapper.AdvancedQuery.FIELD_MEDIA_TYPE)) {
            JSONArray arr = jsonQuery.getJSONArray(Mapper.AdvancedQuery.FIELD_MEDIA_TYPE);
            int[] mediaTypes = new int[arr.size()];
            for (int i = 0; i < arr.size(); i++) {
                mediaTypes[i] = arr.getInt(i);
            }
            fbMediaType = QueryBuilders.termsQuery(Mapper.FieldArticle.MEDIA_TYPE, mediaTypes);
            filters.add(fbMediaType);
        }

        //媒体名称
        QueryBuilder fbMediaNameZh = null;
        if (!Constant.isNullKey(jsonQuery, Mapper.AdvancedQuery.FIELD_MEDIA_NAME_ZH)) {
            JSONArray arr = jsonQuery.getJSONArray(Mapper.AdvancedQuery.FIELD_MEDIA_NAME_ZH);
            String[] mediaTypes = new String[arr.size()];
            for (int i = 0; i < arr.size(); i++) {
                mediaTypes[i] = arr.getString(i);
            }
            fbMediaNameZh = QueryBuilders.termsQuery(Mapper.FieldArticle.MEDIA_NAME_ZH, mediaTypes);
            filters.add(fbMediaNameZh);
        }
        //排除媒体
        QueryBuilder fbMediaNot = null;
        if (!Constant.isNullKey(jsonQuery, Mapper.AdvancedQuery.FIELD_MEDIA_NOT)) {
            JSONArray arr = jsonQuery.getJSONArray(Mapper.AdvancedQuery.FIELD_MEDIA_NOT);
            int[] mediaIds = new int[arr.size()];
            for (int i = 0; i < arr.size(); i++) {
                mediaIds[i] = arr.getInt(i);
            }
            fbMediaNot = QueryBuilders.boolQuery().mustNot((QueryBuilders.termsQuery(Mapper.FieldArticle.WEBSITE_ID, mediaIds)));
            filters.add(fbMediaNot);
        }
        //uuid
        QueryBuilder fbUuid = null;
        if (!Constant.isNullKey(jsonQuery, Mapper.AdvancedQuery.FIELD_ID)) {
            //fbUuid=FilterBuilders.termFilter(Mapper.FieldArticle.ID, jsonQuery.getString(Mapper.AdvancedQuery.FIELD_ID));
            //uuid 没有store，所以用_id
            JSONArray arr = jsonQuery.getJSONArray(Mapper.AdvancedQuery.FIELD_ID);
            String[] ids = new String[arr.size()];
            for (int i = 0; i < arr.size(); i++) {
                ids[i] = arr.getString(i);
            }
            //fbUuid=FilterBuilders.termFilter(Mapper.AdvancedQuery.FIELD_ID, jsonQuery.getString(Mapper.AdvancedQuery.FIELD_ID));
            fbUuid = QueryBuilders.termsQuery(Mapper.AdvancedQuery.FIELD_ID, ids);
            filters.add(fbUuid);
        }

        //similarityID
        QueryBuilder fbSimilarityID = null;
        if (!Constant.isNullKey(jsonQuery, Mapper.AdvancedQuery.FIELD_SIMILARITY_ID)) {
            String simId = jsonQuery.getString(Mapper.AdvancedQuery.FIELD_SIMILARITY_ID);
            //TODO 可能有错，需要调试
            fbSimilarityID = QueryBuilders.termQuery(Mapper.FieldArticle.SIMILARITY_ID, simId);
            filters.add(fbSimilarityID);
        }

        //comeFrom
        QueryBuilder fbComeFrom = null;
        if (!Constant.isNullKey(jsonQuery, Mapper.AdvancedQuery.FIELD_COMEFROM)) {
            //fbUuid=FilterBuilders.termFilter(Mapper.FieldArticle.ID, jsonQuery.getString(Mapper.AdvancedQuery.FIELD_ID));
            //uuid 没有store，所以用_id
            JSONArray arr = jsonQuery.getJSONArray(Mapper.AdvancedQuery.FIELD_COMEFROM);
            String[] ids = new String[arr.size()];
            for (int i = 0; i < arr.size(); i++) {
                ids[i] = arr.getString(i);
            }
            //fbUuid=FilterBuilders.termFilter(Mapper.AdvancedQuery.FIELD_ID, jsonQuery.getString(Mapper.AdvancedQuery.FIELD_ID));
            fbComeFrom = QueryBuilders.termsQuery(Mapper.AdvancedQuery.FIELD_COMEFROM, ids);
            filters.add(fbComeFrom);
        }


        //集成
        QueryBuilder[] fbs = new QueryBuilder[filters.size()];
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        for (int i = 0; i < fbs.length; i++) {
            qb.must(filters.get(i));
        }
        return qb;
    }

    //分组统计
    private AbstractAggregationBuilder termAggregation(String field) {
        return AggregationBuilders.terms(field).field(field).size(0);
    }

    private JSONObject getResultByType(SearchHit sh, String type) {
        if (type == null || "".equalsIgnoreCase(type)) return null;
        if (type.equalsIgnoreCase(Constant.QUERY_RESULT_FRONT)) return getResult4Front(sh);
        if (type.equalsIgnoreCase(Constant.QUERY_RESULT_ANALYSIS)) return getResult4Analysis(sh);
        if (type.equalsIgnoreCase(Constant.QUERY_RESULT_DETAIL)) return getResult4Detail(sh);
        return null;
    }

    /**
     * 返回前端页面检索所需的字段
     *
     * @param sh
     *
     * @return
     */
    private JSONObject getResult4Front(SearchHit sh) {
        Map<String, Object> map = sh.getSource();
        Map<String, HighlightField> fieldMap = sh.getHighlightFields();
		/*
		for(String key:fieldMap.keySet()){
			System.out.println(fieldMap.get(key).fragments()[0].string());
		}*/
        JSONObject obj = new JSONObject();
        obj.put(Mapper.FieldArticle.ID, map.get(Mapper.FieldArticle.ID));
        obj.put(Mapper.FieldArticle.LANGUAGE_CODE, map.get(Mapper.FieldArticle.LANGUAGE_CODE));

        //*************************titleSrc**************************
        HighlightField titleSrcHigh = fieldMap.get(Mapper.FieldArticle.TITLE_SRC);
        String titleSrc = "";
        if (titleSrcHigh == null) {
            Object o = map.get(Mapper.FieldArticle.TITLE_SRC);
            if (o == null) {
                titleSrc = "";
            } else {
                titleSrc = o.toString();
            }
        } else {
            titleSrc = titleSrcHigh.fragments()[0].string().replaceAll("<\\\\/em>(.)<em>", "$1");
        }
        if (titleSrc != null) {
            titleSrc = CharUtil.removeSpaceInChinese(titleSrc);
        }
        obj.put(Mapper.FieldArticle.TITLE_SRC, titleSrc);
        //*************************titleSrc**************************

        HighlightField titleEn = fieldMap.get(Mapper.FieldArticle.TITLE_EN);
        if (titleEn == null) {
            Object o = map.get(Mapper.FieldArticle.TITLE_EN);
            if (o == null) {
                obj.put(Mapper.FieldArticle.TITLE_EN, "");
            } else {
                obj.put(Mapper.FieldArticle.TITLE_EN, o);
            }
        } else {
            obj.put(Mapper.FieldArticle.TITLE_EN, titleEn.fragments()[0].string());
        }
        HighlightField titleZhHigh = fieldMap.get(Mapper.FieldArticle.TITLE_ZH);
        String titleZh = "";
        if (titleZhHigh == null) {
            Object o = map.get(Mapper.FieldArticle.TITLE_ZH);
            if (o == null) {
                titleZh = "";
            } else {
                titleZh = o.toString();
            }
        } else {
            titleZh = titleZhHigh.fragments()[0].string().replaceAll("<\\\\/em>(.)<em>", "$1");
        }
        if (titleZh != null) {
            titleZh = CharUtil.removeSpaceInChinese(titleZh);
        }
        obj.put(Mapper.FieldArticle.TITLE_ZH, titleZh);

        HighlightField absZhHigh = fieldMap.get(Mapper.FieldArticle.ABSTRACT_ZH);
        String absZh = "";
        if (absZhHigh == null) {
            Object o = map.get(Mapper.FieldArticle.ABSTRACT_ZH);
            if (o == null) {
                absZh = "";
            } else {
                absZh = o.toString();
                absZh = absZh.replaceAll("<\\s*br\\s*/\\s*>", " ")
                        .replaceAll("。。。。。。", " ")
                        .replaceAll("<[^>]*>", " ").trim();
            }
        } else {
            absZh = absZhHigh.fragments()[0].string().replaceAll("<\\\\/em>(.)<em>", "$1");
            if (absZh != null) {
                absZh = absZh.replaceAll("<\\s*br\\s*/\\s*>", " ")
                        .replaceAll("。。。。。。", " ");
            }
            //			obj.put(Mapper.FieldArticle.ABSTRACT_ZH, absZhHigh.fragments()[0].string());
        }

        /**
         * 临时处理摘要，现在应该已经处理完了
         */
        //		absZh = absZh.replaceAll("<\\s*br\\s*/\\s*>", " ");
        //		absZh = absZh.replaceAll("。。。。。。", " ");
        //		absZh = absZh.replaceAll("<[^>]*>", " ").trim();
        //		if(absZh.length() < 20)
        //		{
        //			absZh =((String)map.get(Mapper.FieldArticle.TEXT_ZH)).substring(0,50).replaceAll("<[^>]*>", " ").trim();
        //		}

        obj.put(Mapper.FieldArticle.ABSTRACT_ZH, absZh);

        HighlightField absEn = fieldMap.get(Mapper.FieldArticle.ABSTRACT_EN);
        if (absEn == null) {
            String absEnStr = (String) map.get(Mapper.FieldArticle.ABSTRACT_EN);
            if (absEnStr != null) {
                obj.put(Mapper.FieldArticle.ABSTRACT_EN, absEnStr.replaceAll("<\\s*BR\\s*/\\s*>", " "));//((String)map.get(Mapper.FieldArticle.ABSTRACT_EN)).replaceAll("<BR/>", " "));
            } else {
                obj.put(Mapper.FieldArticle.ABSTRACT_EN, null);
            }
        } else {
            String absEnStr = (String) absEn.fragments()[0].string();
            if (absEnStr != null) {
                obj.put(Mapper.FieldArticle.ABSTRACT_EN, absEnStr.replaceAll("<\\s*BR\\s*/\\s*>", " "));
            } else {
                obj.put(Mapper.FieldArticle.ABSTRACT_EN, null);
            }
        }
/*		obj.put(Mapper.FieldArticle.MEDIA_NAME_ZH, map.get(Mapper.FieldArticle.MEDIA_NAME_ZH));
		obj.put(Mapper.FieldArticle.MEDIA_NAME_EN, map.get(Mapper.FieldArticle.MEDIA_NAME_EN));
//		obj.put(Mapper.FieldArticle.MEDIA_ID, map.get(Mapper.FieldArticle.MEDIA_ID));
		obj.put(Mapper.FieldArticle.PUBDATE, map.get(Mapper.FieldArticle.PUBDATE));
		obj.put(Mapper.FieldArticle.MEDIA_TYPE, map.get(Mapper.FieldArticle.MEDIA_TYPE));
		obj.put(Mapper.FieldArticle.MEDIA_TNAME, map.get(Mapper.FieldArticle.MEDIA_TNAME));
//		obj.put(Mapper.FieldArticle.LANGUAGE_TYPE, map.get(Mapper.FieldArticle.LANGUAGE_TYPE));
		obj.put(Mapper.FieldArticle.LANGUAGE_TNAME, map.get(Mapper.FieldArticle.LANGUAGE_TNAME));
//		obj.put(Mapper.FieldArticle.COUNTRY_ID, map.get(Mapper.FieldArticle.COUNTRY_ID));
		obj.put(Mapper.FieldArticle.COUNTRY_NAME_ZH, map.get(Mapper.FieldArticle.COUNTRY_NAME_ZH));
		obj.put(Mapper.FieldArticle.COUNTRY_NAME_EN, map.get(Mapper.FieldArticle.COUNTRY_NAME_EN));
		obj.put(Mapper.FieldArticle.KEYWORDS_ZH, map.get(Mapper.FieldArticle.KEYWORDS_ZH));
		obj.put(Mapper.FieldArticle.KEYWORDS_EN, map.get(Mapper.FieldArticle.KEYWORDS_EN));
		obj.put(Mapper.FieldArticle.DOC_LENGTH, map.get(Mapper.FieldArticle.DOC_LENGTH));
		obj.put(Mapper.FieldArticle.URL, map.get(Mapper.FieldArticle.URL));
		*/


        obj.put(Mapper.FieldArticle.SENTIMENT_ID, map.get(Mapper.FieldArticle.SENTIMENT_ID));
        obj.put(Mapper.FieldArticle.SENTIMENT_NAME, map.get(Mapper.FieldArticle.SENTIMENT_NAME));
        if (((String) map.get(Mapper.FieldArticle.LANGUAGE_TNAME)).equalsIgnoreCase("英文")
                ||
                ((String) map.get(Mapper.FieldArticle.LANGUAGE_TNAME)).equalsIgnoreCase("en")
                ) {
            obj.put(Mapper.FieldArticle.LANGUAGE_TNAME, "英语");
        } else {
            obj.put(Mapper.FieldArticle.LANGUAGE_TNAME, map.get(Mapper.FieldArticle.LANGUAGE_TNAME));
        }

        //		obj.put(Mapper.FieldArticle.LANGUAGE_TYPE, map.get(Mapper.FieldArticle.LANGUAGE_TYPE));
        obj.put(Mapper.FieldArticle.LANGUAGE_CODE, map.get(Mapper.FieldArticle.LANGUAGE_CODE));
        obj.put(Mapper.FieldArticle.KEYWORDS_EN, map.get(Mapper.FieldArticle.KEYWORDS_EN));
        obj.put(Mapper.FieldArticle.KEYWORDS_ZH, map.get(Mapper.FieldArticle.KEYWORDS_ZH));
        obj.put(Mapper.FieldArticle.CATEGORY_ID, map.get(Mapper.FieldArticle.CATEGORY_ID));
        obj.put(Mapper.FieldArticle.CATEGORY_NAME, map.get(Mapper.FieldArticle.CATEGORY_NAME));
        //		obj.put(Mapper.FieldArticle.REGION_ID, map.get(Mapper.FieldArticle.REGION_ID));
        //		obj.put(Mapper.FieldArticle.REGION_NAME, map.get(Mapper.FieldArticle.REGION_NAME));
        //		obj.put(Mapper.FieldArticle.COUNTRY_ID, map.get(Mapper.FieldArticle.COUNTRY_ID));


        obj.put(Mapper.FieldArticle.COUNTRY_NAME_ZH, map.get(Mapper.FieldArticle.COUNTRY_NAME_ZH));
        obj.put(Mapper.FieldArticle.COUNTRY_NAME_EN, map.get(Mapper.FieldArticle.COUNTRY_NAME_EN));
        if ((map.get(Mapper.FieldArticle.COUNTRY_NAME_ZH) == null) && (map.get(Mapper.FieldArticle.COUNTRY_NAME_EN) != null)) {
            obj.put(Mapper.FieldArticle.COUNTRY_NAME_ZH, Mapper.FieldArticle.COUNTRY_NAME_EN);
        } else if ((map.get(Mapper.FieldArticle.COUNTRY_NAME_EN) == null) && (map.get(Mapper.FieldArticle.COUNTRY_NAME_ZH) != null)) {
            obj.put(Mapper.FieldArticle.COUNTRY_NAME_EN, Mapper.FieldArticle.COUNTRY_NAME_ZH);
        } else if ((map.get(Mapper.FieldArticle.COUNTRY_NAME_ZH) == null) && (map.get(Mapper.FieldArticle.COUNTRY_NAME_EN) == null)) {
            obj.put(Mapper.FieldArticle.COUNTRY_NAME_ZH, "other");
            obj.put(Mapper.FieldArticle.COUNTRY_NAME_EN, "other");
        }

        obj.put(Mapper.FieldArticle.PROVINCE_NAME_ZH, map.get(Mapper.FieldArticle.PROVINCE_NAME_ZH));
        obj.put(Mapper.FieldArticle.PROVINCE_NAME_EN, map.get(Mapper.FieldArticle.PROVINCE_NAME_EN));
        obj.put(Mapper.FieldArticle.DISTRICT_NAME_ZH, map.get(Mapper.FieldArticle.DISTRICT_NAME_ZH));
        obj.put(Mapper.FieldArticle.DISTRICT_NAME_EN, map.get(Mapper.FieldArticle.DISTRICT_NAME_EN));

        obj.put(Mapper.FieldArticle.PUBDATE, map.get(Mapper.FieldArticle.PUBDATE));
        obj.put(Mapper.FieldArticle.CREATE_TIME, map.get(Mapper.FieldArticle.CREATE_TIME));
        obj.put(Mapper.FieldArticle.UPDATE_TIME, map.get(Mapper.FieldArticle.UPDATE_TIME));
        //obj.put(Mapper.FieldArticle.AUTHOR, map.get(Mapper.FieldArticle.AUTHOR));
        obj.put(Mapper.FieldArticle.IS_ORIGINAL, map.get(Mapper.FieldArticle.IS_ORIGINAL));

        //		obj.put(Mapper.FieldArticle.MEDIA_ID, map.get(Mapper.FieldArticle.MEDIA_ID));
        obj.put(Mapper.FieldArticle.MEDIA_NAME_ZH, map.get(Mapper.FieldArticle.MEDIA_NAME_ZH));
        obj.put(Mapper.FieldArticle.MEDIA_NAME_EN, map.get(Mapper.FieldArticle.MEDIA_NAME_EN));
        obj.put(Mapper.FieldArticle.MEDIA_NAME_SRC, map.get(Mapper.FieldArticle.MEDIA_NAME_SRC));
        obj.put(Mapper.FieldArticle.MEDIA_TYPE, map.get(Mapper.FieldArticle.MEDIA_TYPE));
        obj.put(Mapper.FieldArticle.MEDIA_TNAME, map.get(Mapper.FieldArticle.MEDIA_TNAME));
        obj.put(Mapper.FieldArticle.MEDIA_LEVEL, map.get(Mapper.FieldArticle.MEDIA_LEVEL));
        obj.put(Mapper.FieldArticle.WEBSITE_ID, map.get(Mapper.FieldArticle.WEBSITE_ID));
        //		obj.put(Mapper.FieldArticle.LEVEL_NAME, map.get(Mapper.FieldArticle.LEVEL_NAME));
        obj.put(Mapper.FieldArticle.DOC_LENGTH, map.get(Mapper.FieldArticle.DOC_LENGTH));
        obj.put(Mapper.FieldArticle.URL, map.get(Mapper.FieldArticle.URL));

        obj.put(Mapper.FieldArticle.TRANSFER, map.get(Mapper.FieldArticle.TRANSFER));
        obj.put(Mapper.FieldArticle.SIMILARITY_ID, map.get(Mapper.FieldArticle.SIMILARITY_ID));
        obj.put(Mapper.FieldArticle.TRANSFROMM, map.get(Mapper.FieldArticle.TRANSFROMM));
        obj.put(Mapper.FieldArticle.ISPICTURE, map.get(Mapper.FieldArticle.ISPICTURE));
        obj.put(Mapper.FieldArticle.PV, map.get(Mapper.FieldArticle.PV));
        obj.put(Mapper.FieldArticle.ISHOME, map.get(Mapper.FieldArticle.ISHOME));

        obj.put(Mapper.FieldArticle.PUBDATE_SORT, map.get(Mapper.FieldArticle.PUBDATE_SORT));
        obj.put(Mapper.FieldArticle.IS_SENSITIVE, map.get(Mapper.FieldArticle.IS_SENSITIVE));

        return obj;
    }

    private JSONObject getResult4Analysis(SearchHit sh) {

        Map<String, Object> map = sh.getSource();
        JSONObject obj = new JSONObject();
        obj.put(Mapper.FieldArticle.ID, map.get(Mapper.FieldArticle.ID));
        obj.put(Mapper.FieldArticle.TITLE_SRC, map.get(Mapper.FieldArticle.TITLE_SRC));
        obj.put(Mapper.FieldArticle.TITLE_EN, map.get(Mapper.FieldArticle.TITLE_EN));
        obj.put(Mapper.FieldArticle.TITLE_ZH, map.get(Mapper.FieldArticle.TITLE_ZH));
        obj.put(Mapper.FieldArticle.ABSTRACT_EN, map.get(Mapper.FieldArticle.ABSTRACT_EN));
        obj.put(Mapper.FieldArticle.ABSTRACT_ZH, map.get(Mapper.FieldArticle.ABSTRACT_ZH));
        obj.put(Mapper.FieldArticle.SENTIMENT_ID, map.get(Mapper.FieldArticle.SENTIMENT_ID));
        obj.put(Mapper.FieldArticle.SENTIMENT_NAME, map.get(Mapper.FieldArticle.SENTIMENT_NAME));
        obj.put(Mapper.FieldArticle.LANGUAGE_TNAME, map.get(Mapper.FieldArticle.LANGUAGE_TNAME));
        if (((String) map.get(Mapper.FieldArticle.LANGUAGE_TNAME)).equalsIgnoreCase("英文")
                ||
                ((String) map.get(Mapper.FieldArticle.LANGUAGE_TNAME)).equalsIgnoreCase("en")
                ) {
            obj.put(Mapper.FieldArticle.LANGUAGE_TNAME, "英语");
        } else {
            obj.put(Mapper.FieldArticle.LANGUAGE_TNAME, map.get(Mapper.FieldArticle.LANGUAGE_TNAME));
        }


        //		obj.put(Mapper.FieldArticle.LANGUAGE_TYPE, map.get(Mapper.FieldArticle.LANGUAGE_TYPE));
        obj.put(Mapper.FieldArticle.LANGUAGE_CODE, map.get(Mapper.FieldArticle.LANGUAGE_CODE));
        obj.put(Mapper.FieldArticle.KEYWORDS_EN, map.get(Mapper.FieldArticle.KEYWORDS_EN));
        obj.put(Mapper.FieldArticle.KEYWORDS_ZH, map.get(Mapper.FieldArticle.KEYWORDS_ZH));
        obj.put(Mapper.FieldArticle.CATEGORY_ID, map.get(Mapper.FieldArticle.CATEGORY_ID));
        obj.put(Mapper.FieldArticle.CATEGORY_NAME, map.get(Mapper.FieldArticle.CATEGORY_NAME));
        //		obj.put(Mapper.FieldArticle.REGION_ID, map.get(Mapper.FieldArticle.REGION_ID));
        //		obj.put(Mapper.FieldArticle.REGION_NAME, map.get(Mapper.FieldArticle.REGION_NAME));
        //		obj.put(Mapper.FieldArticle.COUNTRY_ID, map.get(Mapper.FieldArticle.COUNTRY_ID));
        obj.put(Mapper.FieldArticle.COUNTRY_NAME_ZH, map.get(Mapper.FieldArticle.COUNTRY_NAME_ZH));
        obj.put(Mapper.FieldArticle.COUNTRY_NAME_EN, map.get(Mapper.FieldArticle.COUNTRY_NAME_EN));
        if ((map.get(Mapper.FieldArticle.COUNTRY_NAME_ZH) == null) && (map.get(Mapper.FieldArticle.COUNTRY_NAME_EN) != null)) {
            obj.put(Mapper.FieldArticle.COUNTRY_NAME_ZH, Mapper.FieldArticle.COUNTRY_NAME_EN);
        } else if ((map.get(Mapper.FieldArticle.COUNTRY_NAME_EN) == null) && (map.get(Mapper.FieldArticle.COUNTRY_NAME_ZH) != null)) {
            obj.put(Mapper.FieldArticle.COUNTRY_NAME_EN, Mapper.FieldArticle.COUNTRY_NAME_ZH);
        } else if ((map.get(Mapper.FieldArticle.COUNTRY_NAME_ZH) == null) && (map.get(Mapper.FieldArticle.COUNTRY_NAME_EN) == null)) {
            obj.put(Mapper.FieldArticle.COUNTRY_NAME_ZH, "other");
            obj.put(Mapper.FieldArticle.COUNTRY_NAME_EN, "other");
        }

        obj.put(Mapper.FieldArticle.PROVINCE_NAME_ZH, map.get(Mapper.FieldArticle.PROVINCE_NAME_ZH));
        obj.put(Mapper.FieldArticle.PROVINCE_NAME_EN, map.get(Mapper.FieldArticle.PROVINCE_NAME_EN));
        obj.put(Mapper.FieldArticle.DISTRICT_NAME_ZH, map.get(Mapper.FieldArticle.DISTRICT_NAME_ZH));
        obj.put(Mapper.FieldArticle.DISTRICT_NAME_EN, map.get(Mapper.FieldArticle.DISTRICT_NAME_EN));

        obj.put(Mapper.FieldArticle.PUBDATE, map.get(Mapper.FieldArticle.PUBDATE));
        obj.put(Mapper.FieldArticle.CREATE_TIME, map.get(Mapper.FieldArticle.CREATE_TIME));
        obj.put(Mapper.FieldArticle.UPDATE_TIME, map.get(Mapper.FieldArticle.UPDATE_TIME));
        //obj.put(Mapper.FieldArticle.AUTHOR, map.get(Mapper.FieldArticle.AUTHOR));
        obj.put(Mapper.FieldArticle.IS_ORIGINAL, map.get(Mapper.FieldArticle.IS_ORIGINAL));

        //		obj.put(Mapper.FieldArticle.MEDIA_ID, map.get(Mapper.FieldArticle.MEDIA_ID));
        obj.put(Mapper.FieldArticle.MEDIA_NAME_ZH, map.get(Mapper.FieldArticle.MEDIA_NAME_ZH));
        obj.put(Mapper.FieldArticle.MEDIA_NAME_EN, map.get(Mapper.FieldArticle.MEDIA_NAME_EN));
        obj.put(Mapper.FieldArticle.MEDIA_NAME_SRC, map.get(Mapper.FieldArticle.MEDIA_NAME_SRC));

        obj.put(Mapper.FieldArticle.MEDIA_TYPE, map.get(Mapper.FieldArticle.MEDIA_TYPE));
        obj.put(Mapper.FieldArticle.MEDIA_TNAME, map.get(Mapper.FieldArticle.MEDIA_TNAME));
        obj.put(Mapper.FieldArticle.MEDIA_LEVEL, map.get(Mapper.FieldArticle.MEDIA_LEVEL));
        obj.put(Mapper.FieldArticle.WEBSITE_ID, map.get(Mapper.FieldArticle.WEBSITE_ID));
        //		obj.put(Mapper.FieldArticle.LEVEL_NAME, map.get(Mapper.FieldArticle.LEVEL_NAME));
        obj.put(Mapper.FieldArticle.DOC_LENGTH, map.get(Mapper.FieldArticle.DOC_LENGTH));
        obj.put(Mapper.FieldArticle.URL, map.get(Mapper.FieldArticle.URL));


        obj.put(Mapper.FieldArticle.TRANSFER, map.get(Mapper.FieldArticle.TRANSFER));
        obj.put(Mapper.FieldArticle.SIMILARITY_ID, map.get(Mapper.FieldArticle.SIMILARITY_ID));
        obj.put(Mapper.FieldArticle.TRANSFROMM, map.get(Mapper.FieldArticle.TRANSFROMM));
        obj.put(Mapper.FieldArticle.ISPICTURE, map.get(Mapper.FieldArticle.ISPICTURE));
        obj.put(Mapper.FieldArticle.PV, map.get(Mapper.FieldArticle.PV));
        obj.put(Mapper.FieldArticle.ISHOME, map.get(Mapper.FieldArticle.ISHOME));

        obj.put(Mapper.FieldArticle.COME_FROM, map.get(Mapper.FieldArticle.COME_FROM));
        obj.put(Mapper.FieldArticle.IS_SENSITIVE, map.get(Mapper.FieldArticle.IS_SENSITIVE));

        return obj;
    }


    private JSONObject getResult4Analysis_bak(SearchHit sh) {
        Map<String, Object> map = sh.getSource();
        JSONObject obj = new JSONObject();
        obj.put(Mapper.FieldArticle.ID, map.get(Mapper.FieldArticle.ID));
        String langCode = map.get(Mapper.FieldArticle.LANGUAGE_CODE).toString();
        String titleSrc = map.get(Mapper.FieldArticle.TITLE_SRC).toString();
        if ("zh".equalsIgnoreCase(langCode)) {
            titleSrc = CharUtil.removeSpaceInChinese(titleSrc);
        }
        obj.put(Mapper.FieldArticle.TITLE_SRC, titleSrc);
        obj.put(Mapper.FieldArticle.TITLE_EN, map.get(Mapper.FieldArticle.TITLE_EN));
        Object o = map.get(Mapper.FieldArticle.TITLE_ZH);
        String titleZh = "";
        if (o != null) {
            titleZh = o.toString();
        }
        titleZh = CharUtil.removeSpaceInChinese(titleZh);
        obj.put(Mapper.FieldArticle.TITLE_ZH, titleZh);

        String absEnStr = (String) map.get(Mapper.FieldArticle.ABSTRACT_EN);
        if (absEnStr != null) {
            obj.put(Mapper.FieldArticle.ABSTRACT_EN, absEnStr.replaceAll("<\\s*BR\\s*/\\s*>", " "));//((String)map.get(Mapper.FieldArticle.ABSTRACT_EN)).replaceAll("<BR/>", " "));
        } else {
            obj.put(Mapper.FieldArticle.ABSTRACT_EN, null);
        }
        //obj.put(Mapper.FieldArticle.ABSTRACT_EN, map.get(Mapper.FieldArticle.ABSTRACT_EN).toString().replaceAll("<BR/>", " "));
        Object oa = map.get(Mapper.FieldArticle.ABSTRACT_ZH);
        String absZh = "";
        if (oa != null) {
            absZh = oa.toString();
        }
        absZh = CharUtil.removeSpaceInChinese(absZh)
                .replaceAll("<\\s*br\\s*/\\s*>", " ")
                .replaceAll("。。。。。。", " ")
                .replaceAll("<[^>]*>", " ").trim();


        /**
         * 临时加的，现在应该不用了
         */
        //		absZh = absZh.replaceAll("<\\s*br\\s*/\\s*>", " ");
        //		absZh = absZh.replaceAll("。。。。。。", " ");
        //		absZh = absZh.replaceAll("<[^>]*>", " ").trim();
        //		if(absZh.length() < 20)
        //		{
        //			absZh =((String)map.get(Mapper.FieldArticle.TEXT_ZH)).substring(0,50).replaceAll("<[^>]*>", " ").trim();
        //		}
        obj.put(Mapper.FieldArticle.ABSTRACT_ZH, absZh);


        obj.put(Mapper.FieldArticle.SENTIMENT_ID, map.get(Mapper.FieldArticle.SENTIMENT_ID));
        obj.put(Mapper.FieldArticle.SENTIMENT_NAME, map.get(Mapper.FieldArticle.SENTIMENT_NAME));
        obj.put(Mapper.FieldArticle.LANGUAGE_TNAME, map.get(Mapper.FieldArticle.LANGUAGE_TNAME));
        if (((String) map.get(Mapper.FieldArticle.LANGUAGE_TNAME)).equalsIgnoreCase("英文")
                ||
                ((String) map.get(Mapper.FieldArticle.LANGUAGE_TNAME)).equalsIgnoreCase("en")
                ) {
            obj.put(Mapper.FieldArticle.LANGUAGE_TNAME, "英语");
        } else {
            obj.put(Mapper.FieldArticle.LANGUAGE_TNAME, map.get(Mapper.FieldArticle.LANGUAGE_TNAME));
        }

        //		obj.put(Mapper.FieldArticle.LANGUAGE_TYPE, map.get(Mapper.FieldArticle.LANGUAGE_TYPE));
        obj.put(Mapper.FieldArticle.LANGUAGE_CODE, map.get(Mapper.FieldArticle.LANGUAGE_CODE));
        obj.put(Mapper.FieldArticle.KEYWORDS_EN, map.get(Mapper.FieldArticle.KEYWORDS_EN));
        obj.put(Mapper.FieldArticle.KEYWORDS_ZH, map.get(Mapper.FieldArticle.KEYWORDS_ZH));
        obj.put(Mapper.FieldArticle.CATEGORY_ID, map.get(Mapper.FieldArticle.CATEGORY_ID));
        obj.put(Mapper.FieldArticle.CATEGORY_NAME, map.get(Mapper.FieldArticle.CATEGORY_NAME));
        //		obj.put(Mapper.FieldArticle.REGION_ID, map.get(Mapper.FieldArticle.REGION_ID));
        //		obj.put(Mapper.FieldArticle.REGION_NAME, map.get(Mapper.FieldArticle.REGION_NAME));
        //		obj.put(Mapper.FieldArticle.COUNTRY_ID, map.get(Mapper.FieldArticle.COUNTRY_ID));
        obj.put(Mapper.FieldArticle.COUNTRY_NAME_ZH, map.get(Mapper.FieldArticle.COUNTRY_NAME_ZH));
        obj.put(Mapper.FieldArticle.COUNTRY_NAME_EN, map.get(Mapper.FieldArticle.COUNTRY_NAME_EN));
        if ((map.get(Mapper.FieldArticle.COUNTRY_NAME_ZH) == null) && (map.get(Mapper.FieldArticle.COUNTRY_NAME_EN) != null)) {
            obj.put(Mapper.FieldArticle.COUNTRY_NAME_ZH, Mapper.FieldArticle.COUNTRY_NAME_EN);
        } else if ((map.get(Mapper.FieldArticle.COUNTRY_NAME_EN) == null) && (map.get(Mapper.FieldArticle.COUNTRY_NAME_ZH) != null)) {
            obj.put(Mapper.FieldArticle.COUNTRY_NAME_EN, Mapper.FieldArticle.COUNTRY_NAME_ZH);
        } else if ((map.get(Mapper.FieldArticle.COUNTRY_NAME_ZH) == null) && (map.get(Mapper.FieldArticle.COUNTRY_NAME_EN) == null)) {
            obj.put(Mapper.FieldArticle.COUNTRY_NAME_ZH, "other");
            obj.put(Mapper.FieldArticle.COUNTRY_NAME_EN, "other");
        }
        obj.put(Mapper.FieldArticle.PROVINCE_NAME_ZH, map.get(Mapper.FieldArticle.PROVINCE_NAME_ZH));
        obj.put(Mapper.FieldArticle.PROVINCE_NAME_EN, map.get(Mapper.FieldArticle.PROVINCE_NAME_EN));
        obj.put(Mapper.FieldArticle.DISTRICT_NAME_ZH, map.get(Mapper.FieldArticle.DISTRICT_NAME_ZH));
        obj.put(Mapper.FieldArticle.DISTRICT_NAME_EN, map.get(Mapper.FieldArticle.DISTRICT_NAME_EN));

        obj.put(Mapper.FieldArticle.PUBDATE, map.get(Mapper.FieldArticle.PUBDATE));
        obj.put(Mapper.FieldArticle.CREATE_TIME, map.get(Mapper.FieldArticle.CREATE_TIME));
        obj.put(Mapper.FieldArticle.UPDATE_TIME, map.get(Mapper.FieldArticle.UPDATE_TIME));
        //obj.put(Mapper.FieldArticle.AUTHOR, map.get(Mapper.FieldArticle.AUTHOR));
        obj.put(Mapper.FieldArticle.IS_ORIGINAL, map.get(Mapper.FieldArticle.IS_ORIGINAL));

        //		obj.put(Mapper.FieldArticle.MEDIA_ID, map.get(Mapper.FieldArticle.MEDIA_ID));
        obj.put(Mapper.FieldArticle.MEDIA_NAME_ZH, map.get(Mapper.FieldArticle.MEDIA_NAME_ZH));
        obj.put(Mapper.FieldArticle.MEDIA_NAME_EN, map.get(Mapper.FieldArticle.MEDIA_NAME_EN));
        obj.put(Mapper.FieldArticle.MEDIA_NAME_SRC, map.get(Mapper.FieldArticle.MEDIA_NAME_SRC));

        obj.put(Mapper.FieldArticle.MEDIA_TYPE, map.get(Mapper.FieldArticle.MEDIA_TYPE));
        obj.put(Mapper.FieldArticle.MEDIA_TNAME, map.get(Mapper.FieldArticle.MEDIA_TNAME));
        obj.put(Mapper.FieldArticle.MEDIA_LEVEL, map.get(Mapper.FieldArticle.MEDIA_LEVEL));
        obj.put(Mapper.FieldArticle.WEBSITE_ID, map.get(Mapper.FieldArticle.WEBSITE_ID));
        //		obj.put(Mapper.FieldArticle.LEVEL_NAME, map.get(Mapper.FieldArticle.LEVEL_NAME));
        obj.put(Mapper.FieldArticle.DOC_LENGTH, map.get(Mapper.FieldArticle.DOC_LENGTH));
        obj.put(Mapper.FieldArticle.URL, map.get(Mapper.FieldArticle.URL));


        obj.put(Mapper.FieldArticle.TRANSFER, map.get(Mapper.FieldArticle.TRANSFER));
        obj.put(Mapper.FieldArticle.SIMILARITY_ID, map.get(Mapper.FieldArticle.SIMILARITY_ID));
        obj.put(Mapper.FieldArticle.TRANSFROMM, map.get(Mapper.FieldArticle.TRANSFROMM));
        obj.put(Mapper.FieldArticle.ISPICTURE, map.get(Mapper.FieldArticle.ISPICTURE));
        obj.put(Mapper.FieldArticle.PV, map.get(Mapper.FieldArticle.PV));
        obj.put(Mapper.FieldArticle.ISHOME, map.get(Mapper.FieldArticle.ISHOME));

        return obj;
    }

    private JSONObject getResult4Detail(SearchHit sh) {
        Map<String, Object> map = sh.getSource();
        JSONObject obj = new JSONObject();
        obj.put(Mapper.FieldArticle.ID, map.get(Mapper.FieldArticle.ID));
        obj.put(Mapper.FieldArticle.TITLE_SRC, map.get(Mapper.FieldArticle.TITLE_SRC));
        obj.put(Mapper.FieldArticle.TITLE_EN, map.get(Mapper.FieldArticle.TITLE_EN));
        Object o = map.get(Mapper.FieldArticle.TITLE_ZH);
        String titleZh = "";
        if (o != null) {
            titleZh = o.toString();
        }
        titleZh = CharUtil.removeSpaceInChinese(titleZh);
        obj.put(Mapper.FieldArticle.TITLE_ZH, titleZh);

        String absEnStr = (String) map.get(Mapper.FieldArticle.ABSTRACT_EN);
        if (absEnStr != null) {
            obj.put(Mapper.FieldArticle.ABSTRACT_EN, absEnStr.replaceAll("<\\s*BR\\s*/\\s*>", " "));//((String)map.get(Mapper.FieldArticle.ABSTRACT_EN)).replaceAll("<BR/>", " "));
        } else {
            obj.put(Mapper.FieldArticle.ABSTRACT_EN, null);
        }
        //obj.put(Mapper.FieldArticle.ABSTRACT_EN, map.get(Mapper.FieldArticle.ABSTRACT_EN).toString().replaceAll("<BR/>", " "));
        Object oa = map.get(Mapper.FieldArticle.ABSTRACT_ZH);
        String absZh = "";
        if (oa != null) {
            absZh = oa.toString();
        }
        absZh = CharUtil.removeSpaceInChinese(absZh)
                .replaceAll("<\\s*br\\s*/\\s*>", " ")
                .replaceAll("。。。。。。", " ")
                .replaceAll("<[^>]*>", " ").trim();

        obj.put(Mapper.FieldArticle.ABSTRACT_ZH, absZh);
        obj.put(Mapper.FieldArticle.SENTIMENT_ID, map.get(Mapper.FieldArticle.SENTIMENT_ID));
        obj.put(Mapper.FieldArticle.SENTIMENT_NAME, map.get(Mapper.FieldArticle.SENTIMENT_NAME));
        obj.put(Mapper.FieldArticle.LANGUAGE_TNAME, map.get(Mapper.FieldArticle.LANGUAGE_TNAME));
        if (((String) map.get(Mapper.FieldArticle.LANGUAGE_TNAME)).equalsIgnoreCase("英文")
                ||
                ((String) map.get(Mapper.FieldArticle.LANGUAGE_TNAME)).equalsIgnoreCase("en")
                ) {
            obj.put(Mapper.FieldArticle.LANGUAGE_TNAME, "英语");
        } else {
            obj.put(Mapper.FieldArticle.LANGUAGE_TNAME, map.get(Mapper.FieldArticle.LANGUAGE_TNAME));
        }

        //		obj.put(Mapper.FieldArticle.LANGUAGE_TYPE, map.get(Mapper.FieldArticle.LANGUAGE_TYPE));
        obj.put(Mapper.FieldArticle.LANGUAGE_CODE, map.get(Mapper.FieldArticle.LANGUAGE_CODE));
        obj.put(Mapper.FieldArticle.KEYWORDS_EN, map.get(Mapper.FieldArticle.KEYWORDS_EN));
        obj.put(Mapper.FieldArticle.KEYWORDS_ZH, map.get(Mapper.FieldArticle.KEYWORDS_ZH));
        obj.put(Mapper.FieldArticle.CATEGORY_ID, map.get(Mapper.FieldArticle.CATEGORY_ID));
        obj.put(Mapper.FieldArticle.CATEGORY_NAME, map.get(Mapper.FieldArticle.CATEGORY_NAME));
        //		obj.put(Mapper.FieldArticle.REGION_ID, map.get(Mapper.FieldArticle.REGION_ID));
        //		obj.put(Mapper.FieldArticle.REGION_NAME, map.get(Mapper.FieldArticle.REGION_NAME));
        //		obj.put(Mapper.FieldArticle.COUNTRY_ID, map.get(Mapper.FieldArticle.COUNTRY_ID));
        obj.put(Mapper.FieldArticle.COUNTRY_NAME_ZH, map.get(Mapper.FieldArticle.COUNTRY_NAME_ZH));
        obj.put(Mapper.FieldArticle.COUNTRY_NAME_EN, map.get(Mapper.FieldArticle.COUNTRY_NAME_EN));

        if ((map.get(Mapper.FieldArticle.COUNTRY_NAME_ZH) == null) && (map.get(Mapper.FieldArticle.COUNTRY_NAME_EN) != null)) {
            obj.put(Mapper.FieldArticle.COUNTRY_NAME_ZH, Mapper.FieldArticle.COUNTRY_NAME_EN);
        } else if ((map.get(Mapper.FieldArticle.COUNTRY_NAME_EN) == null) && (map.get(Mapper.FieldArticle.COUNTRY_NAME_ZH) != null)) {
            obj.put(Mapper.FieldArticle.COUNTRY_NAME_EN, Mapper.FieldArticle.COUNTRY_NAME_ZH);
        } else if ((map.get(Mapper.FieldArticle.COUNTRY_NAME_ZH) == null) && (map.get(Mapper.FieldArticle.COUNTRY_NAME_EN) == null)) {
            obj.put(Mapper.FieldArticle.COUNTRY_NAME_ZH, "other");
            obj.put(Mapper.FieldArticle.COUNTRY_NAME_EN, "other");
        }

        obj.put(Mapper.FieldArticle.PROVINCE_NAME_ZH, map.get(Mapper.FieldArticle.PROVINCE_NAME_ZH));
        obj.put(Mapper.FieldArticle.PROVINCE_NAME_EN, map.get(Mapper.FieldArticle.PROVINCE_NAME_EN));
        obj.put(Mapper.FieldArticle.DISTRICT_NAME_ZH, map.get(Mapper.FieldArticle.DISTRICT_NAME_ZH));
        obj.put(Mapper.FieldArticle.DISTRICT_NAME_EN, map.get(Mapper.FieldArticle.DISTRICT_NAME_EN));

        obj.put(Mapper.FieldArticle.PUBDATE, map.get(Mapper.FieldArticle.PUBDATE));
        obj.put(Mapper.FieldArticle.CREATE_TIME, map.get(Mapper.FieldArticle.CREATE_TIME));
        obj.put(Mapper.FieldArticle.UPDATE_TIME, map.get(Mapper.FieldArticle.UPDATE_TIME));
        //obj.put(Mapper.FieldArticle.AUTHOR, map.get(Mapper.FieldArticle.AUTHOR));
        obj.put(Mapper.FieldArticle.IS_ORIGINAL, map.get(Mapper.FieldArticle.IS_ORIGINAL));

        //		obj.put(Mapper.FieldArticle.MEDIA_ID, map.get(Mapper.FieldArticle.MEDIA_ID));
        obj.put(Mapper.FieldArticle.MEDIA_NAME_ZH, map.get(Mapper.FieldArticle.MEDIA_NAME_ZH));
        obj.put(Mapper.FieldArticle.MEDIA_NAME_EN, map.get(Mapper.FieldArticle.MEDIA_NAME_EN));
        obj.put(Mapper.FieldArticle.MEDIA_NAME_SRC, map.get(Mapper.FieldArticle.MEDIA_NAME_SRC));

        obj.put(Mapper.FieldArticle.MEDIA_TYPE, map.get(Mapper.FieldArticle.MEDIA_TYPE));
        obj.put(Mapper.FieldArticle.MEDIA_TNAME, map.get(Mapper.FieldArticle.MEDIA_TNAME));
        obj.put(Mapper.FieldArticle.MEDIA_LEVEL, map.get(Mapper.FieldArticle.MEDIA_LEVEL));
        obj.put(Mapper.FieldArticle.WEBSITE_ID, map.get(Mapper.FieldArticle.WEBSITE_ID));
        //		obj.put(Mapper.FieldArticle.LEVEL_NAME, map.get(Mapper.FieldArticle.LEVEL_NAME));
        obj.put(Mapper.FieldArticle.DOC_LENGTH, map.get(Mapper.FieldArticle.DOC_LENGTH));
        obj.put(Mapper.FieldArticle.URL, map.get(Mapper.FieldArticle.URL));


        obj.put(Mapper.FieldArticle.TRANSFER, map.get(Mapper.FieldArticle.TRANSFER));
        obj.put(Mapper.FieldArticle.SIMILARITY_ID, map.get(Mapper.FieldArticle.SIMILARITY_ID));
        obj.put(Mapper.FieldArticle.TRANSFROMM, map.get(Mapper.FieldArticle.TRANSFROMM));
        obj.put(Mapper.FieldArticle.ISPICTURE, map.get(Mapper.FieldArticle.ISPICTURE));
        obj.put(Mapper.FieldArticle.PV, map.get(Mapper.FieldArticle.PV));
        obj.put(Mapper.FieldArticle.ISHOME, map.get(Mapper.FieldArticle.ISHOME));


        obj.put(Mapper.FieldArticle.TEXT_SRC, map.get(Mapper.FieldArticle.TEXT_SRC));
        obj.put(Mapper.FieldArticle.TEXT_ZH, map.get(Mapper.FieldArticle.TEXT_ZH));
        obj.put(Mapper.FieldArticle.TEXT_EN, map.get(Mapper.FieldArticle.TEXT_EN));

        obj.put(Mapper.FieldArticle.IS_SENSITIVE, map.get(Mapper.FieldArticle.IS_SENSITIVE));


        return obj;
    }


    //排序
    private SortBuilder getSort(JSONObject obj) {
        if (!obj.containsKey(Mapper.Sort.ORDER)) return SortBuilders.scoreSort();
        String orderStr = obj.getString(Mapper.Sort.ORDER);
        if (orderStr == null || "".equals(orderStr)) {
            return SortBuilders.scoreSort();
        }
        SortOrder order = Constant.QUERY_SORT_ORDER_ASC.equals(orderStr) ? SortOrder.ASC : SortOrder.DESC;
        //		return SortBuilders.scriptSort("new java.text.SimpleDateFormat('yyyy-MM-dd HH:mm:dd').format(new Date(doc['"+Mapper.FieldArticle.PUBDATE+"'].value))", "string").order(order);
        return SortBuilders.fieldSort(obj.getString(Mapper.Sort.FIELD_NAME)).order(order);
    }

    private void setSort(SearchRequestBuilder srb, JSONObject obj) {
        if (!obj.containsKey(Mapper.Sort.FIELD_NAME) || !obj.containsKey(Mapper.Sort.ORDER)) {
            //默认相关度降序
            //			srb.addSort(SortBuilders.scoreSort());
            defaultSort(srb, obj);
            return;
        }
        SortOrder order = Constant.QUERY_SORT_ORDER_ASC.equals(obj.getString(Mapper.Sort.ORDER)) ? SortOrder.ASC : SortOrder.DESC;
        if (!obj.getString(Mapper.Sort.FIELD_NAME).equals(Mapper.Sort.RELEVANCE)) {
            srb.addSort(SortBuilders.fieldSort(obj.getString(Mapper.Sort.FIELD_NAME)).order(order));
            srb.addSort(SortBuilders.fieldSort(Mapper.Sort.RELEVANCE).order(SortOrder.DESC));
        } else {
            srb.addSort(SortBuilders.fieldSort(obj.getString(Mapper.Sort.FIELD_NAME)).order(order));
        }
    }

    private void defaultSort(SearchRequestBuilder srb, JSONObject obj) {
        srb.addSort(SortBuilders.fieldSort(Mapper.FieldArticle.PUBDATE_SORT).order(SortOrder.DESC));
        srb.addSort(SortBuilders.fieldSort(Mapper.Sort.RELEVANCE).order(SortOrder.DESC));
        srb.addSort(SortBuilders.fieldSort(Mapper.FieldArticle.MEDIA_LEVEL).order(SortOrder.ASC));
        srb.addSort(SortBuilders.fieldSort(Mapper.FieldArticle.TRANSFER).order(SortOrder.DESC));
    }

/*	private ChineseSorter getCustomSort(JSONObject obj){
		if(!obj.containsKey(Mapper.Sort.ORDER)) return null;
		String orderStr=obj.getString(Mapper.Sort.ORDER);
		if(orderStr==null || "".equals(orderStr)){
			return null;
		}
		String field=obj.getString(Mapper.Sort.FIELD_NAME);
		if(!field.equalsIgnoreCase(Mapper.FieldArticle.NAME)) return null;
		return new ChineseSorter(field, orderStr);
	}*/

    public void close() {
        try {
            if (builder != null) {
                builder.close();
            }
        } catch (Exception e) {
            logger.error(ExceptionUtil.getExceptionTrace(e));
        }
        if (client != null)
            this.client.close();
    }

    public boolean updateUnit(JSONObject jsonObject) {
        try {
            if (builder == null) {
                builder = new IndexBuilder();
            }
            return builder.updateUnit(jsonObject.toString(), Configuration.INDEX_NAME, Configuration.INDEX_TYPE_ARTICLE, Mapper.FieldArticle.ID);

        } catch (Exception e) {
            logger.error(ExceptionUtil.getExceptionTrace(e));
            return false;
        }
    }

    public boolean insertUnit(JSONObject jsonObject) {
        try {
            if (builder == null) {
                builder = new IndexBuilder();
            }
            return builder.addUnit(jsonObject.toString(), Configuration.INDEX_NAME, Configuration.INDEX_TYPE_ARTICLE, Mapper.FieldArticle.ID);

        } catch (Exception e) {
            logger.error(ExceptionUtil.getExceptionTrace(e));
            return false;
        }
    }


    public boolean deleteUnit(JSONObject jsonObject) {
        try {
            if (builder == null) {
                builder = new IndexBuilder();
            }
            return builder.deleteUnit(jsonObject.toString(), Configuration.INDEX_NAME, Configuration.INDEX_TYPE_ARTICLE, Mapper.FieldArticle.ID);

        } catch (Exception e) {
            logger.error(ExceptionUtil.getExceptionTrace(e));
            return false;
        }
    }

    public static int addUnitBatch(String jsonUnitArray) {
        //根据日期来判断真正的indexName，对array进行分组
        JSONObject obj = null;
        int sum = 0;
        try {
            JSONArray array = JSONArray.fromObject(jsonUnitArray);

            Map<String, JSONArray> hashJSONArray = new HashMap<String, JSONArray>();
            for (int i = 0; i < array.size(); i++) {
                obj = array.getJSONObject(i);
                String yearMonth = obj.getString(Mapper.FieldArticle.PUBDATE).substring(0, 7).replace("-", "");
                if (hashJSONArray.containsKey(yearMonth)) {
                    hashJSONArray.get(yearMonth).add(obj);
                } else {
                    JSONArray nArray = new JSONArray();
                    nArray.add(obj);
                    hashJSONArray.put(yearMonth, nArray);
                }
            }

            //
            IndexBuilder recentBuilder = new IndexBuilder(Configuration.CLUSTER_NAME, Configuration.INDEX_SERVER_ADDRESS);
            IndexBuilder totalBuilder = new IndexBuilder(Configuration.TOTAL_CLUSTER_NAME, Configuration.TOTAL_INDEX_SERVER_ADDRESS);

            for (String yearMonth : hashJSONArray.keySet()) {
                //写buck
                String year = yearMonth.substring(0, 4);
                if (year.equalsIgnoreCase("2015") || year.equalsIgnoreCase("2016")) {
                    recentBuilder.addUnitBatch(hashJSONArray.get(yearMonth).toString(), "news" + yearMonth, Configuration.INDEX_TYPE_ARTICLE, Mapper.FieldArticle.ID);
                }
                totalBuilder.addUnitBatch(hashJSONArray.get(yearMonth).toString(), "news" + yearMonth, Configuration.INDEX_TYPE_ARTICLE, Mapper.FieldArticle.ID);
            }

            recentBuilder.close();
            totalBuilder.close();


        } catch (Exception e) {
            logger.error(ExceptionUtil.getExceptionTrace(e));
        }
        return sum;

    }


    class KeywordEn
            implements Comparable<KeywordEn> {
        String name;
        int count;

        public KeywordEn(String name, int count) {
            this.name = name;
            this.count = count;
        }


        @Override
        public int compareTo(KeywordEn o) {
            return o.count - count;
        }
    }
}
