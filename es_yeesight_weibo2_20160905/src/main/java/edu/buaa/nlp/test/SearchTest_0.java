package edu.buaa.nlp.test;

import javax.naming.directory.SearchResult;

import org.apache.log4j.PropertyConfigurator;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.ESLoggerFactory;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import edu.buaa.nlp.es.client.ESClient;
import edu.buaa.nlp.es.client.IndexBuilder;
import edu.buaa.nlp.es.constant.Configuration;
import edu.buaa.nlp.es.exception.ExceptionUtil;
import edu.buaa.nlp.es.exception.QueryFormatException;
import edu.buaa.nlp.es.news.Mapper;
import edu.buaa.nlp.es.news.SearchBuilder;
import edu.buaa.nlp.es.util.Constant;
import edu.buaa.nlp.es.util.DateUtil;

public class SearchTest_0 {
	
	public static void test(){
		SearchBuilder sb=new SearchBuilder();
		JSONObject jsonQuery=new JSONObject();
		jsonQuery.put(Mapper.Query.KEYWORD, "长江");
		jsonQuery.put(Mapper.Query.RESULT_TYPE, Constant.QUERY_RESULT_ANALYSIS);
		jsonQuery.put(Mapper.Query.PAGE_NO, 1);
		jsonQuery.put(Mapper.Query.PAGE_SIZE, 20);
		jsonQuery.put(Mapper.Query.HIGHLIGHT, false);
		jsonQuery.put(Mapper.Sort.FIELD_NAME, Mapper.Sort.RELEVANCE);
		jsonQuery.put(Mapper.Sort.ORDER, Constant.QUERY_SORT_ORDER_DESC);
		//####################高级检索部分#######  begin  ##################33
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_BEGIN_DATE, "2008-01-01 00:00:00");//开始日期
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_END_DATE, "2016-12-31 00:00:00");//结束日期
		//语言
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_LANGUAGE, new int[]{200});//语言id数组
		//日期
		/*
		//地区范围
		jsonQuery.put(Mapper.AdvancedQuery.FIELD_REGION, new int[]{1,321,311});//地区范围id数组
		//媒体级别
		jsonQuery.put(Mapper.AdvancedQuery.FIELD_MEDIA_LEVEL, new int[]{1,4,3});//媒体级别ID数组
		//情感
		//领域分类
*/		//####################高级检索部分#######  end  ##################33
		//媒体
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_CATEGORY, new int[]{5});//媒体ID数组
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_SENTIMENT, new int[]{-1});//情感标识ID数组

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
//		jsonQuery.put(Mapper.AdvancedQuery.QUERY_BODY, adArr.toString());
		long s1=System.currentTimeMillis();
		String result=sb.crossSearch(jsonQuery.toString());
		long e1=System.currentTimeMillis();
		System.out.println("time:"+(e1-s1)/1000);
//		String result=sb.filterSearch(jsonQuery.toString());
		JSONArray array=JSONObject.fromObject(result).getJSONArray(Mapper.Query.RESULT_LIST);
		System.out.println(JSONObject.fromObject(result).get(Mapper.Query.RESULT_COUNT));
//		System.out.println(result);
		int count=0;
		for(int i=0; i<array.size(); i++){
			JSONObject obj=array.getJSONObject(i);
			if("en".equals(obj.getString(Mapper.FieldArticle.LANGUAGE_CODE))){
				System.out.println(obj);
				count++;
			}
			System.out.println(obj);
		}
		System.out.println(count);
		sb.close();
	}
	
	public static void testSearch(String keyword){
		System.out.println("keyword:"+keyword);
		SearchBuilder sb=new SearchBuilder();
		JSONObject jsonQuery=new JSONObject();
		jsonQuery.put(Mapper.Query.KEYWORD, keyword);
		jsonQuery.put(Mapper.Query.RESULT_TYPE, Constant.QUERY_RESULT_ANALYSIS);
		jsonQuery.put(Mapper.Query.PAGE_NO, 1);
		jsonQuery.put(Mapper.Query.PAGE_SIZE, 20);
		jsonQuery.put(Mapper.Query.HIGHLIGHT, false);
//		jsonQuery.put(Mapper.Sort.FIELD_NAME, Mapper.FieldArticle.PUBDATE_SORT);
		jsonQuery.put(Mapper.Sort.ORDER, Constant.QUERY_SORT_ORDER_ASC);
		long s1=System.currentTimeMillis();
		String result=sb.crossSearch(jsonQuery.toString());
		long e1=System.currentTimeMillis();
		System.out.println("time:"+(e1-s1)/1000);
		JSONArray array=JSONObject.fromObject(result).getJSONArray(Mapper.Query.RESULT_LIST);
		System.out.println(JSONObject.fromObject(result).get(Mapper.Query.RESULT_COUNT));
		int count=0;
		for(int i=0; i<array.size(); i++){
			JSONObject obj=array.getJSONObject(i);
			if("en".equals(obj.getString(Mapper.FieldArticle.LANGUAGE_CODE))){
				System.out.println(obj);
				count++;
			}
			System.out.println(obj);
		}
		System.out.println(count);
		sb.close();
	}
	
	public static void main(String[] args) throws QueryFormatException, InterruptedException {
		PropertyConfigurator.configure("log4j.properties");
			test();
//		testSearch(args[0]);
	}
}
