package edu.buaa.nlp.test;

import org.apache.log4j.PropertyConfigurator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import edu.buaa.nlp.es.util.Constant;
import edu.buaa.nlp.es.weibo.Mapper;
import edu.buaa.nlp.es.weibo.SearchBuilder;

public class SearchTestWeibo {
	
	public static void testAdvanced(String keyword){
		SearchBuilder.keywordGen.initSensitiveModels("data/sensitive/leaders.txt", "data/sensitive/sensiwords.txt");
		SearchBuilder sb=new SearchBuilder();
		JSONObject jsonQuery=new JSONObject();
		jsonQuery.put(Mapper.Query.KEYWORD, "中国");
//		jsonQuery.put(Mapper.Query.KEYWORD, "(中国  and 中国)and(美国 or 大选)and not(希拉里 or 天津)");
		jsonQuery.put(Mapper.Query.KEYWORD, keyword);
		
		
		jsonQuery.put(Mapper.Query.RESULT_TYPE, Constant.QUERY_RESULT_FRONT); //.QUERY_RESULT_ANALYSIS);
		jsonQuery.put(Mapper.Query.PAGE_NO, 1);
		jsonQuery.put(Mapper.Query.PAGE_SIZE, 10);
		jsonQuery.put(Mapper.Query.HIGHLIGHT, false);
		jsonQuery.put(Mapper.Sort.FIELD_NAME, Mapper.FieldWeibo.CMTCNT  ); //Mapper.FieldWeibo.RPSCNT);//Mapper.Sort.RELEVANCE);
//		jsonQuery.put(Mapper.Sort.FIELD_NAME, Mapper.FieldWeibo.LANGUAGECODE);
		jsonQuery.put(Mapper.Sort.ORDER, Constant.QUERY_SORT_ORDER_DESC);
		//####################高级检索部分#######  begin  ##################33
		//语言
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_LANGUAGE, new String[]{"zh"});//语言名称数组 
		//日期
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_BEGIN_DATE, "2016-04-01 00:00:00");//开始日期
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_END_DATE, "2016-05-01 00:00:00");//结束日期
		//地区范围
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_PROVINCE, new String[]{"北京","天津"});//省份名称数组
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_PROVINCE_ID, new String[]{"01"});//省份名称数组
		//情感
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_SENTIMENT, new int[]{-1});//情感标识ID数组
		//是否原创
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_ISORI, new int[]{0,1});//是否原创数据，1-原创；0-非原创
		
		//国家
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_COUNTRY, new String[]{"中国"});//中国，美国
		
		//来源类型
		//jsonQuery.put(Mapper.AdvancedQuery.FIELD_SOURCETYPE, new String[]{"weibo"});//weibo，weixin，twitter，facebook
		
		//weiboID
		//jsonQuery.put(Mapper.AdvancedQuery.FIELD_SOURCEWEIBOID, new String[]{"271086590313415691710003464854591515456"});
		//name
		//jsonQuery.put(Mapper.AdvancedQuery.FIELD_NAME, new String[]{"T542c"});
		long s1=System.currentTimeMillis();
		String result=sb.crossSearch(jsonQuery.toString());
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
	
	
	public static void getUUID()
	{
		SearchBuilder sb=new SearchBuilder();
		JSONObject jsonQuery=new JSONObject();
		jsonQuery.put(Mapper.Query.RESULT_TYPE, Constant.QUERY_RESULT_DETAIL); //.QUERY_RESULT_ANALYSIS);
		jsonQuery.put(Mapper.AdvancedQuery.FIELD_ID, new String[]{"279576381113910616290003672441005682265"});
		String result = sb.filterSearch(jsonQuery.toString());
		JSONArray array=JSONObject.fromObject(result).getJSONArray(Mapper.Query.RESULT_LIST);
		System.out.println("result size:"+array.size());
		System.out.println(JSONObject.fromObject(result).get(Mapper.Query.RESULT_COUNT));
		//System.out.println(result);
		for(int i=0; i<array.size(); i++){
			JSONObject obj=array.getJSONObject(i);
			System.out.println(obj);
			
			//get comments
			
			JSONObject jsonQueryComment = new JSONObject();
			jsonQueryComment.put(Mapper.AdvancedQuery.FIELD_WEIBO_UUID, obj.getString(Mapper.FieldWeibo.UUID));
			jsonQueryComment.put(Mapper.Query.PAGE_NO, 1);
			jsonQueryComment.put(Mapper.Query.PAGE_SIZE, 10);
			String resultComment = sb.filterWeiboComment(jsonQueryComment.toString());
			JSONArray arrayComment=JSONObject.fromObject(resultComment).getJSONArray(Mapper.Query.RESULT_LIST);
			System.out.println("comment size:"+arrayComment.size());
			System.out.println(JSONObject.fromObject(resultComment).get(Mapper.Query.RESULT_COUNT));
			for(int j=0; j<arrayComment.size(); j++){
				JSONObject objComment=arrayComment.getJSONObject(j);
				System.out.println(objComment);
			}
		}
		sb.close();
	}
	
	
	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.properties");
		testAdvanced("(幸福) and () and () and ()");
		//getUUID();
	}
}
