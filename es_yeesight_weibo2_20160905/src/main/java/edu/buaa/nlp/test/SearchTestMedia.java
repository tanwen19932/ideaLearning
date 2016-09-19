package edu.buaa.nlp.test;

import org.apache.log4j.PropertyConfigurator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import edu.buaa.nlp.es.common.MediaSearchBuilder;
import edu.buaa.nlp.es.util.Constant;
import edu.buaa.nlp.es.weibo.Mapper;

public class SearchTestMedia {
	
	public static void testAdvanced(){
		MediaSearchBuilder sb=new MediaSearchBuilder();
		JSONObject jsonQuery=new JSONObject();
		jsonQuery.put(Mapper.Query.KEYWORD, "网易");
		
		jsonQuery.put(Mapper.Query.PAGE_NO, 1);
		jsonQuery.put(Mapper.Query.PAGE_SIZE, 10);
		jsonQuery.put(Mapper.Sort.FIELD_NAME, Mapper.Sort.RELEVANCE);
		jsonQuery.put(Mapper.Sort.FIELD_NAME, Mapper.FieldWeibo.LANGUAGECODE);
		jsonQuery.put(Mapper.Sort.ORDER, Constant.QUERY_SORT_ORDER_ASC);
		//####################高级检索部分#######  begin  ##################33
		//语言
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_LANGUAGE, new String[]{"zh"});//语言名称数组 
		//地区范围
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_PROVINCE, new String[]{"北京","天津"});//省份名称数组
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_PROVINCE_ID, new String[]{"01"});//省份名称数组
		//情感
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_SENTIMENT, new int[]{-1});//情感标识ID数组


		long s1=System.currentTimeMillis();
		String result=sb.keywordSearch(jsonQuery.toString());
//		String result=sb.filterSearch(jsonQuery.toString());
		long e1=System.currentTimeMillis();
//		System.out.println("time:"+(e1-s1)/1000);
		JSONArray array=JSONObject.fromObject(result).getJSONArray(Mapper.Query.RESULT_LIST);
		System.out.println("result size:"+array.size());
		System.out.println(JSONObject.fromObject(result).get(Mapper.Query.RESULT_COUNT));
		for(int i=0; i<array.size(); i++){
			JSONObject obj=array.getJSONObject(i);
			System.out.println(obj);
		}
		sb.close();
	}
	
	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.properties");
		testAdvanced();
	}
}
