package edu.buaa.nlp.test;

import edu.buaa.nlp.es.exception.QueryFormatException;
import edu.buaa.nlp.es.news.Mapper;
import edu.buaa.nlp.es.news.SearchBuilder;
import edu.buaa.nlp.es.util.Constant;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.PropertyConfigurator;

public class SearchTest {
	
	public static void test(){
		SearchBuilder sb=new SearchBuilder();
		JSONObject jsonQuery=new JSONObject();
		jsonQuery.put(Mapper.Query.KEYWORD, "中国");
		jsonQuery.put(Mapper.Query.RESULT_TYPE, Constant.QUERY_RESULT_FRONT);
		jsonQuery.put(Mapper.Query.PAGE_NO, 1);
		jsonQuery.put(Mapper.Query.PAGE_SIZE, 20);
		jsonQuery.put(Mapper.Query.HIGHLIGHT, true);
//		jsonQuery.put(Mapper.Sort.FIELD_NAME, Mapper.FieldArticle.PUBDATE_SORT);
//		jsonQuery.put(Mapper.Sort.ORDER, Constant.QUERY_SORT_ORDER_ASC);
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
	
	public static void testAdvanced2(String keyword) throws QueryFormatException{
		SearchBuilder sb=new SearchBuilder();
		JSONObject jsonQuery=new JSONObject();
//		jsonQuery.put(Mapper.Query.KEYWORD, "（美国&特朗普）");
		jsonQuery.put(Mapper.Query.KEYWORD, "\"美国大选\"");
//		jsonQuery.put(Mapper.Query.KEYWORD, "美国大选");
//		jsonQuery.put(Mapper.Query.KEYWORD, "\"诺贝尔\"");
//		jsonQuery.put(Mapper.Query.KEYWORD, "诺贝尔");
		jsonQuery.put(Mapper.Query.KEYWORD, "\"美国大选\"");
//		jsonQuery.put(Mapper.Query.KEYWORD, sb.initKeyword("panama leak"));
//		jsonQuery.put(Mapper.Query.KEYWORD, sb.initKeyword("一带一路"));
//		jsonQuery.put(Mapper.Query.KEYWORD, "阿里巴巴");
//		jsonQuery.put(Mapper.Query.KEYWORD, "alphago");
//		jsonQuery.put(Mapper.Query.KEYWORD, "A Breakdown of Breitbart&#39;s Big, Misogynistic Implosion");
//		jsonQuery.put(Mapper.Query.KEYWORD, "特朗普");
//		jsonQuery.put(Mapper.Query.KEYWORD, "山东：按保费总额30%标准给予补贴");//山东打击“网络售假”");//中关村对于北京意味着什么？");
//		jsonQuery.put(Mapper.Query.KEYWORD, "美国大选");
		
		
		jsonQuery.put(Mapper.Query.KEYWORD, "习近平 捷克总理 索博特卡");//特朗普");
		
		
//		jsonQuery.put(Mapper.Query.KEYWORD, "海通证券");//特朗普");
//		jsonQuery.put(Mapper.Query.KEYWORD, "\"魏则西\" or(百度的恶 or 吊打百度)");
		
//		jsonQuery.put(Mapper.Query.KEYWORD, "(中国  and 中国)and(\"特朗普\")and(美国 or 大选)and not(希拉里 or 天津)");
//		jsonQuery.put(Mapper.Query.KEYWORD, "(中国 and 北京)and(\"\") and() and not()");
		jsonQuery.put(Mapper.Query.KEYWORD,  "(中国 and 北京)and or not ( )");
		jsonQuery.put(Mapper.Query.KEYWORD,  "大数据");
		jsonQuery.put(Mapper.Query.KEYWORD,  "中国出版集团");
		jsonQuery.put(Mapper.Query.KEYWORD,  "天涯海角");
//		jsonQuery.put(Mapper.Query.KEYWORD,  "大数据");
//		jsonQuery.put(Mapper.Query.KEYWORD,  "TITLE:" + keyword);
		jsonQuery.put(Mapper.Query.KEYWORD,  keyword);
//		jsonQuery.put(Mapper.Query.KEYWORD, "魏则西");
		jsonQuery.put(Mapper.Query.RESULT_TYPE, Constant.QUERY_RESULT_FRONT);//QUERY_RESULT_DETAIL); //.QUERY_RESULT_ANALYSIS);//.QUERY_RESULT_FRONT); //
		jsonQuery.put(Mapper.Query.PAGE_NO, 1);
		jsonQuery.put(Mapper.Query.PAGE_SIZE, 10);
		jsonQuery.put(Mapper.Query.HIGHLIGHT, true);
		jsonQuery.put(Mapper.Sort.FIELD_NAME, Mapper.Sort.RELEVANCE);
//		jsonQuery.put(Mapper.Sort.FIELD_NAME, Mapper.FieldArticle.MEDIA_LEVEL);
//		jsonQuery.put(Mapper.Sort.FIELD_NAME, Mapper.FieldArticle.TRANSFER);
		jsonQuery.put(Mapper.Sort.ORDER, Constant.QUERY_SORT_ORDER_DESC);
		//####################高级检索部分#######  begin  ##################33
		//媒体名称
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_MEDIA_NAME_ZH, new String[]{"Minghui.org"});//有吧
		//语言
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_LANGUAGE_TNAME, new String[]{"中文"});//语言名称数组
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_LANGUAGE, new String[]{"en"});//语言名称数组 
		//日期
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_BEGIN_DATE, "2016-07-26 00:00:00");//开始日期
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_END_DATE, "2030-05-01 00:00:00");//结束日期
		//地区范围
		//jsonQuery.put(Mapper.AdvancedQuery.FIELD_REGION, new int[]{1,321,311});//地区范围id数组，暂时不用
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_COUNTRY_NAME_ZH, new String[]{"中国"});//国家名称数组
		
		//媒体级别
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_MEDIA_LEVEL, new int[]{0,1,2,3,4,5});//媒体级别ID数组
		//媒体类型
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_MEDIA_TYPE, new int[]{1});//媒体类型ID数组
		//情感
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_SENTIMENT, new int[]{1});//情感标识ID数组
		//领域分类
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_CATEGORY, new int[]{1,2});//领域ID数组

		//指定去掉websit ID
		//jsonQuery.put(Mapper.AdvancedQuery.FIELD_MEDIA_NOT, new int[]{166450, 166449, 166448, 166447, 166446, 166445, 166444, 166443, 166442, 166441, 165198, 165197, 165196, 165195, 165194, 165193, 165192, 165191, 165190, 165189, 165188, 165187, 165186, 165185, 165184, 165183, 165182, 165181, 165013, 164735, 164734, 164733, 164732, 164731, 164730, 164729, 164728, 164727, 164163, 164162, 164161, 164160, 164159, 164158, 164157, 164156, 164155, 164154, 164153, 164152, 164151, 163997, 163996, 163995, 163994, 163993, 163992, 163991, 163990, 163989, 163988, 163987, 163986, 163985, 163984, 163983, 163982, 163981, 163980, 163979, 163978, 163977, 163976, 163975, 163974, 163973, 163972, 163971, 163970, 163969, 163968, 163967, 163966, 163965, 163964, 163963, 163962, 163961, 163960, 163959, 163958, 163957, 163956, 163955, 163954, 163953, 163952, 163951, 163950, 163949, 163948, 163947, 163946, 163945, 163944, 163943, 163942, 163941, 163940, 163939, 163938, 163937, 163936, 163935, 163934, 163933, 163932, 163931, 163930, 163929, 163928, 163927, 163926, 163925, 163924, 163923, 163922, 163921, 163920, 163919, 163918, 163917, 163916, 163915, 16151, 168237, 168235, 168230, 168228, 168227, 168226, 168225, 168224, 168223, 168220, 15785, 15784, 15783, 15782, 15781, 15780, 15779, 15778, 15777, 15776, 15775, 15774, 15773, 15772, 15771, 15770, 15769, 15768, 15767, 15766, 15765, 15764, 15763, 15762, 15761, 15760, 15759, 15758, 15757, 15756, 15755, 15754, 15753, 15752, 15751, 15750, 15749, 15748, 15747, 15746, 15745, 15744, 15743, 15742, 15741, 15740, 15739, 15738, 15737, 15736, 15735, 15734, 15733, 15732, 15731, 15730, 15729, 15728, 15727, 15726, 15725, 15724, 15723, 15722, 15721, 15720, 15719, 15718, 15717, 15716, 15715, 15714, 15713, 15712, 15711, 15710, 15709, 15708, 15707, 15706, 15705, 15704, 15703, 15702, 15701, 15700, 15699, 15698, 15697, 15696, 15695, 183899, 168884, 168879, 166873, 166851, 166850, 166849, 166848, 166847, 166846, 41658, 15188, 15187, 15186, 15185, 15184, 15183, 15182, 15181, 15180, 15179, 15178, 15177, 15176, 15175, 15174, 15173, 15172, 15171, 15170, 15169, 15168, 15167, 13327, 13326, 13325, 13324, 13323, 13322, 13321, 13320, 13319, 13318, 13317, 176664, 176662, 176659, 176657, 176649, 176642, 171329, 173130, 172946, 171351, 166618, 168120, 168119, 168118, 168117, 168113, 168112, 168110, 168108, 168107, 168106, 168104, 135824, 122876, 28934, 28933, 28932, 28931, 28930, 28929, 28928, 28927, 28926, 28925, 28924, 28923, 28922, 164009, 164008, 164007, 164006, 164005, 164004, 164003, 164002, 164001, 164000, 163999, 163998, 163489, 15999, 15998, 15997, 15996, 15995, 15994, 15993, 15992, 15991, 15990, 15989, 15988, 15987, 15986, 13431, 13430, 13429, 13428, 13427, 13426, 13425, 13424, 13423, 13422, 13421, 13420, 13419, 13418, 13417, 13416, 13387, 13386, 13385, 13384, 13383, 13382, 13381, 13380, 13379, 13377, 13376});//媒体类型ID数组
		
		//SimilarityID
		//jsonQuery.put(Mapper.AdvancedQuery.FIELD_SIMILARITY_ID,"001463453317000D11B8D3754915D2F"); 
		
		//uuid
		//jsonQuery.put(Mapper.AdvancedQuery.FIELD_ID, new String[]{"161463419583216D666E166A737511E"});
		
		
		//来源
		//jsonQuery.put(Mapper.AdvancedQuery.FIELD_COMEFROM, new String[]{"Cision"});//领域ID数组
		
		//敏感
		//jsonQuery.put(Mapper.AdvancedQuery.FIELD_SENSITIVE, new int[]{1});//领域ID数组
		
		for(int k=0;k<1;k++){
			long s1=System.currentTimeMillis();
			String result=sb.crossSearch(jsonQuery.toString());
//			String result=sb.specialSearch(jsonQuery.toString());
//			String result=sb.filterSearch(jsonQuery.toString());
			long e1=System.currentTimeMillis();
			System.out.println("time:"+(e1-s1));
			//*
			JSONArray array=JSONObject.fromObject(result).getJSONArray(Mapper.Query.RESULT_LIST);
			System.out.println("result size:"+array.size());
			System.out.println(JSONObject.fromObject(result).get(Mapper.Query.RESULT_COUNT));
			for(int i=0; i<array.size(); i++){
				JSONObject obj=array.getJSONObject(i);
				System.out.println(obj);
			}
			//*/
			sb.close();
			long e2=System.currentTimeMillis();
			System.out.println(e2-e1);
			System.out.println(e2-s1);
		}
	}
	
	
	public static void testAdvanced4() throws QueryFormatException{
		SearchBuilder sb=new SearchBuilder();
		JSONObject jsonQuery=new JSONObject();
		
		long s0=System.currentTimeMillis();
		jsonQuery.put(Mapper.Query.KEYWORD,  "特朗普");
		jsonQuery.put(Mapper.Query.RESULT_TYPE, Constant.QUERY_RESULT_FRONT); //.QUERY_RESULT_ANALYSIS);
		jsonQuery.put(Mapper.Query.HIGHLIGHT, true);
		jsonQuery.put(Mapper.Sort.FIELD_NAME, Mapper.Sort.RELEVANCE);
		//jsonQuery.put(Mapper.Sort.FIELD_NAME, Mapper.FieldArticle.LANGUAGE_CODE);
		jsonQuery.put(Mapper.Sort.ORDER, Constant.QUERY_SORT_ORDER_DESC);
		
		for(int k=1;k<=100;k++)
		{
			jsonQuery.put(Mapper.Query.PAGE_NO, k);
			jsonQuery.put(Mapper.Query.PAGE_SIZE, 10);

			//####################高级检索部分#######  begin  ##################33
			//媒体名称
			//jsonQuery.put(Mapper.AdvancedQuery.FIELD_MEDIA_NAME_ZH, new String[]{"上海热线"});//
			//语言
	//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_LANGUAGE_TNAME, new String[]{"中文"});//语言名称数组
	//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_LANGUAGE, new String[]{"en"});//语言名称数组 
			//日期
	//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_BEGIN_DATE, "2016-05-09 00:00:00");//开始日期
	//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_END_DATE, "2016-05-16 00:00:00");//结束日期
			//地区范围
			//jsonQuery.put(Mapper.AdvancedQuery.FIELD_REGION, new int[]{1,321,311});//地区范围id数组，暂时不用
	//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_COUNTRY_NAME_ZH, new String[]{"中国"});//国家名称数组
			
			//媒体级别
	//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_MEDIA_LEVEL, new int[]{4,5});//媒体级别ID数组
			//媒体类型
	//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_MEDIA_TYPE, new int[]{1});//媒体类型ID数组
			//情感
	//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_SENTIMENT, new int[]{1});//情感标识ID数组
			//领域分类
	//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_CATEGORY, new int[]{1,2});//领域ID数组
	
			long s1=System.currentTimeMillis();
			String result=sb.crossSearch(jsonQuery.toString());
	//		String result=sb.filterSearch(jsonQuery.toString());
			long e1=System.currentTimeMillis();
			System.out.println("time:"+(e1-s1)/1000);
			JSONArray array=JSONObject.fromObject(result).getJSONArray(Mapper.Query.RESULT_LIST);
			System.out.println("result size:"+array.size());
			System.out.println(JSONObject.fromObject(result).get(Mapper.Query.RESULT_COUNT));
			for(int i=0; i<array.size(); i++){
				//JSONObject obj=array.getJSONObject(i);
				//System.out.println(obj);
			}
		
		}
		sb.close();
		long e0=System.currentTimeMillis();
		System.out.println("time:"+(e0-s0)/1000);
	}
	
	
	public static void testAdvanced(){
		SearchBuilder sb=new SearchBuilder();
		JSONObject jsonQuery=new JSONObject();
//		jsonQuery.put(Mapper.Query.KEYWORD, "（美国&特朗普）");
		jsonQuery.put(Mapper.Query.KEYWORD, "特朗普");
		
		jsonQuery.put(Mapper.Query.RESULT_TYPE, Constant.QUERY_RESULT_FRONT); //.QUERY_RESULT_ANALYSIS);
/*	
		jsonQuery.put(Mapper.Query.PAGE_NO, 1);
		jsonQuery.put(Mapper.Query.PAGE_SIZE, 10);
		jsonQuery.put(Mapper.Query.HIGHLIGHT, true);
		jsonQuery.put(Mapper.Sort.FIELD_NAME, Mapper.Sort.RELEVANCE);
		jsonQuery.put(Mapper.Sort.ORDER, Constant.QUERY_SORT_ORDER_DESC);
		jsonQuery.put(Mapper.AdvancedQuery.FIELD_LANGUAGE, new String[]{"en"});//语言名称数组 
*/
		
		
		jsonQuery.put(Mapper.Query.PAGE_NO, 1);
		jsonQuery.put(Mapper.Query.PAGE_SIZE, 10);
		jsonQuery.put(Mapper.Query.HIGHLIGHT, true);
		jsonQuery.put(Mapper.Sort.FIELD_NAME, Mapper.Sort.RELEVANCE);
//		jsonQuery.put(Mapper.Sort.FIELD_NAME, Mapper.FieldArticle.LANGUAGE_CODE);
		jsonQuery.put(Mapper.Sort.ORDER, Constant.QUERY_SORT_ORDER_DESC);
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_LANGUAGE, new String[]{"en"});//语言名称数组 
		
		
		
		System.out.println(jsonQuery.toString());
		long s1=System.currentTimeMillis();
//		String result=sb.crossSearch(jsonQuery.toString());
		String result=sb.filterSearch(jsonQuery.toString());
		long e1=System.currentTimeMillis();
//		System.out.println("time:"+(e1-s1)/1000);
		JSONArray array=JSONObject.fromObject(result).getJSONArray(Mapper.Query.RESULT_LIST);
		System.out.println("result size:"+array.size());
		System.out.println(JSONObject.fromObject(result).get(Mapper.Query.RESULT_COUNT));
		System.out.println(result);
//		for(int i=0; i<array.size(); i++){
//			JSONObject obj=array.getJSONObject(i);
//			System.out.println(obj.get("languageCode")+" "+obj.get("countryNameZh"));
//		}
		sb.close();
	}
	public static void getUUID()
	{
		long start = System.currentTimeMillis();
		SearchBuilder sb=new SearchBuilder();
		JSONObject jsonQuery=new JSONObject();
		jsonQuery.put(Mapper.Query.RESULT_TYPE, Constant.QUERY_RESULT_DETAIL); //.QUERY_RESULT_ANALYSIS);
		jsonQuery.put(Mapper.AdvancedQuery.FIELD_ID, new String[]{"8214634237657829793422E0FEF0167"});
		String result = sb.filterSearch(jsonQuery.toString());
		JSONArray array=JSONObject.fromObject(result).getJSONArray(Mapper.Query.RESULT_LIST);
		System.out.println("result size:"+array.size());
		System.out.println(JSONObject.fromObject(result).get(Mapper.Query.RESULT_COUNT));
		//System.out.println(result);
		
		
		
		for(int i=0; i<array.size(); i++){
			JSONObject obj=array.getJSONObject(i);
			System.out.println(obj);
		}
		sb.close();
		
		System.out.println(System.currentTimeMillis() - start);
		
	}
	
	
	public static void getUUID(String uuid)
	{
		SearchBuilder sb=new SearchBuilder();
		JSONObject jsonQuery=new JSONObject();
		jsonQuery.put(Mapper.Query.RESULT_TYPE, Constant.QUERY_RESULT_DETAIL); //.QUERY_RESULT_ANALYSIS);
		jsonQuery.put(Mapper.AdvancedQuery.FIELD_ID, new String[]{uuid});
		String result = sb.filterSearch(jsonQuery.toString());
		JSONArray array=JSONObject.fromObject(result).getJSONArray(Mapper.Query.RESULT_LIST);
		System.out.println("result size:"+array.size());
		System.out.println(JSONObject.fromObject(result).get(Mapper.Query.RESULT_COUNT));
		//System.out.println(result);
		for(int i=0; i<array.size(); i++){
			JSONObject obj=array.getJSONObject(i);
			System.out.println(obj);
		}
		sb.close();
	}
	
	public static void Weiping20160330_test()
	{
		JSONObject jsonQuery = new JSONObject();
		// #################### 一般检索部分 ####### begin ##################
		jsonQuery.put(Mapper.Query.KEYWORD, "特朗普");
		jsonQuery.put(Mapper.Query.KEYWORD, "apple");
		jsonQuery.put(Mapper.Query.RESULT_TYPE, Constant.QUERY_RESULT_ANALYSIS);// Constant.QUERY_RESULT_ANALYSIS
		jsonQuery.put(Mapper.Query.HIGHLIGHT, false);
		jsonQuery.put(Mapper.Query.PAGE_SIZE, 500);
		jsonQuery.put(Mapper.Sort.FIELD_NAME, Mapper.Sort.RELEVANCE);//.FieldArticle.PUBDATE);//.Sort.RELEVANCE);//.FieldArticle.PUBDATE);
		jsonQuery.put(Mapper.Sort.ORDER, Constant.QUERY_SORT_ORDER_ASC);
		SearchBuilder sb = new SearchBuilder();
		long s = System.currentTimeMillis();
		String result=sb.crossSearch(jsonQuery.toString());
		//String result = sb.specialSearch(jsonQuery.toString());
		JSONArray array=JSONObject.fromObject(result).getJSONArray(Mapper.Query.RESULT_LIST);
		System.out.println("result count:"+array.size());
		System.out.println("total count:"+JSONObject.fromObject(result).get(Mapper.Query.RESULT_COUNT));
/*		for(int i=0; i<array.size(); i++){
			JSONObject obj=array.getJSONObject(i);
			System.out.println(obj);
		}*/
		sb.close();
//		System.out.println((System.currentTimeMillis() - s)/1000);

	}
	
	public static void groupSearch(String keyword)
	{
		/*
		SearchBuilder sb=new SearchBuilder();
		JSONObject jsonQuery=new JSONObject();
//		jsonQuery.put(Mapper.Query.KEYWORD, "（美国&特朗普）");
		jsonQuery.put(Mapper.Query.KEYWORD, "\"美国大选\"");
//		jsonQuery.put(Mapper.Query.KEYWORD, "美国大选");
//		jsonQuery.put(Mapper.Query.KEYWORD, "\"诺贝尔\"");
//		jsonQuery.put(Mapper.Query.KEYWORD, "诺贝尔");
		jsonQuery.put(Mapper.Query.KEYWORD, "\"美国大选\"");
		jsonQuery.put(Mapper.Query.KEYWORD, "panama  leak");
		jsonQuery.put(Mapper.Query.KEYWORD, "一带一路");
		jsonQuery.put(Mapper.Query.KEYWORD, "A Breakdown of Breitbart&#39;s Big, Misogynistic Implosion");
		jsonQuery.put(Mapper.Query.KEYWORD, keyword);

		jsonQuery.put(Mapper.Query.RESULT_TYPE, Constant.QUERY_RESULT_FRONT); //.QUERY_RESULT_ANALYSIS);
		jsonQuery.put(Mapper.Query.PAGE_NO, 1);
		jsonQuery.put(Mapper.Query.PAGE_SIZE, 10);
		jsonQuery.put(Mapper.Query.HIGHLIGHT, false);
		jsonQuery.put(Mapper.Sort.FIELD_NAME, Mapper.Sort.RELEVANCE);
//		jsonQuery.put(Mapper.Sort.FIELD_NAME, Mapper.FieldArticle.LANGUAGE_CODE);
		jsonQuery.put(Mapper.Sort.ORDER, Constant.QUERY_SORT_ORDER_DESC);
		//####################高级检索部分#######  begin  ##################33
		//媒体名称
		//jsonQuery.put(Mapper.AdvancedQuery.FIELD_MEDIA_NAME_ZH, new String[]{"上海热线"});//
		//语言
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_LANGUAGE_TNAME, new String[]{"中文"});//语言名称数组
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_LANGUAGE, new String[]{"en"});//语言名称数组 
		//日期
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_BEGIN_DATE, "2016-04-24 00:00:00");//开始日期
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_END_DATE, "2016-04-26 00:00:00");//结束日期
		//地区范围
		//jsonQuery.put(Mapper.AdvancedQuery.FIELD_REGION, new int[]{1,321,311});//地区范围id数组，暂时不用
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_COUNTRY_NAME_ZH, new String[]{"中国"});//国家名称数组
		
		//媒体级别
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_MEDIA_LEVEL, new int[]{4,5});//媒体级别ID数组
		//媒体类型
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_MEDIA_TYPE, new int[]{1});//媒体类型ID数组
		//情感
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_SENTIMENT, new int[]{1});//情感标识ID数组
		//领域分类
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_CATEGORY, new int[]{1,2});//领域ID数组
		
		JSONObject obj=null;
		try {
			obj = sb.initAdvancedQuery(jsonQuery.toString());
		} catch (QueryFormatException e) {
			return ;
		}
		SearchBuilder.initSensitiveModels("data/sensitive/leaders.txt", "data/sensitive/sensiwords.txt");

		SearchRequestBuilder srb=sb.buildQuery(obj);
		long start = System.currentTimeMillis();
	    Client client = ESClient.getClient();
	    TermsFacetBuilder facetBuilder = FacetBuilders.termsFacet("typeFacetName");
	    //facetBuilder.field("languageCode").size(Integer.MAX_VALUE);
	    //facetBuilder.field("comeFrom").size(Integer.MAX_VALUE);
	    facetBuilder.field("countryNameZh").size(Integer.MAX_VALUE);
	    //facetBuilder.field("sentimentId").size(Integer.MAX_VALUE);	    
	    facetBuilder.facetFilter(FilterBuilders.matchAllFilter());
//    	SearchResponse response = //srb
//	    		client.prepareSearch(Configuration.INDEX_NAME)
//	            .setTypes(Configuration.INDEX_TYPE_ARTICLE)
//	        .addFacet(facetBuilder)
//	        .setPostFilter(FilterBuilders.matchAllFilter())
//	            .execute()
//	            .actionGet();
	    SearchResponse response = srb.addFacet(facetBuilder)
	        .setPostFilter(FilterBuilders.matchAllFilter())
	            .execute()
	            .actionGet();	    
	    Facets f = response.getFacets();
	    //跟上面的名称一样
	    TermsFacet facet = (TermsFacet)f.getFacets().get("typeFacetName");
	    for(TermsFacet.Entry tf :facet.getEntries()){
	      System.out.println(tf.getTerm()+"\t:\t" + tf.getCount());
	    }
	    client.close();
	    long end = System.currentTimeMillis();
		
	    System.out.println(end - start);
	    */
		
	}
	
	public static void updateUnit() throws InterruptedException
	{
		JSONObject jsonQuery=new JSONObject();
		String uuid = "1234567890";
		jsonQuery.put("uuid", uuid);
/*		jsonQuery.put("titleZh", "test update44444");
		jsonQuery.put("abstractZh", "hahahaha hahaha test update344444");	*/	
		jsonQuery.put("similarityId", "66666");
		getUUID(uuid);
		SearchBuilder sb=new SearchBuilder();
		sb.updateUnit(jsonQuery);
		sb.close();
		Thread.sleep(1000);
		getUUID(uuid);
	}
	

	
	
	public static void insertUnit() throws InterruptedException
	{
		JSONObject jsonQuery=new JSONObject();
		String uuid = "1234567890";
		jsonQuery.put("uuid", uuid);
		jsonQuery.put("abstractEn", "test update44444");
		jsonQuery.put("textEn", "hahahaha hahaha test update344444");
		jsonQuery.put("similarityId", "1234567890");
		jsonQuery.put("mediaLevel", 0);
		
		SearchBuilder sb=new SearchBuilder();
		sb.insertUnit(jsonQuery);
		sb.close();
		Thread.sleep(1000);
		getUUID(uuid);
	}
	
	public static void deleteUnit() throws InterruptedException
	{
		JSONObject jsonQuery=new JSONObject();
		String uuid = "1234567890";
		jsonQuery.put("uuid", uuid);
	
		SearchBuilder sb=new SearchBuilder();
		sb.deleteUnit(jsonQuery);
		sb.close();
		Thread.sleep(1000);
		getUUID(uuid);
	}
	
	public static void testAdvanced3(){
		/*
		SearchBuilder.initSensitiveModels("data/sensitive/leaders.txt", "data/sensitive/sensiwords.txt");
		SearchBuilder sb=new SearchBuilder();
		JSONObject jsonQuery=new JSONObject();
		jsonQuery.put(Mapper.Query.KEYWORD, "\"达赖\"");
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_BEGIN_DATE, beginDate);
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_END_DATE, endDate);
		jsonQuery.put(Mapper.Query.RESULT_TYPE, Constant.QUERY_RESULT_FRONT);
		jsonQuery.put(Mapper.Query.PAGE_NO, 1);
		jsonQuery.put(Mapper.Query.PAGE_SIZE, 1);
		jsonQuery.put(Mapper.Query.HIGHLIGHT, false);
		jsonQuery.put(Mapper.Sort.FIELD_NAME, Mapper.FieldArticle.PUBDATE_SORT);
		jsonQuery.put(Mapper.Sort.ORDER, Constant.QUERY_SORT_ORDER_ASC);
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_BEGIN_DATE, beginDate);
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_END_DATE, endDate);
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_COUNTRY_NAME_ZH, search.getCountryNameZh());
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_LANGUAGE, search.getLanguageCode());
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_MEDIA_LEVEL, search.getMediaLevel());
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_SENTIMENT, search.getSentimentId());
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_CATEGORY, search.getCateGoryId());
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_MEDIA_TYPE, search.getMediaType());
		jsonQuery.put(Mapper.AdvancedQuery.FIELD_SIMILARITY_ID,"");
	//	jsonQuery.put(Mapper.AdvancedQuery.FIELD_MEDIA_NOT, Constants.mediaNotObj);//敏感站点
		
		JSONObject obj=null;
		try {
			obj = sb.initAdvancedQuery(jsonQuery.toString());
		} catch (QueryFormatException e) {
			return ;
		}
		SearchRequestBuilder srb=sb.buildQuery(obj);
		
	    TermsFacetBuilder facetBuilder = FacetBuilders.termsFacet("typeFacetName");
	    facetBuilder.field("countryNameZh").size(Integer.MAX_VALUE);
//	    TermsFacetBuilder facetBuilder2 = FacetBuilders.termsFacet("typeFacetName2");
//	    facetBuilder2.field("languageCode").size(Integer.MAX_VALUE);
	    facetBuilder.facetFilter(FilterBuilders.matchAllFilter());
	    SearchResponse response = srb
	        .addFacet(facetBuilder)
	        .setPostFilter(FilterBuilders.matchAllFilter())
	        .execute()
	        .actionGet();
	    Facets f = response.getFacets();
	    //跟上面的名称一样
	    TermsFacet facet = (TermsFacet)f.getFacets().get("typeFacetName");
	    for(TermsFacet.Entry tf :facet.getEntries()){
	      System.out.println(tf.getTerm()+"\t:\t" + tf.getCount());
	    }
	    sb.close();
	    */
	}

	
	public static void main(String[] args) throws QueryFormatException, InterruptedException {
		PropertyConfigurator.configure("log4j.properties");
		
/*		String response0 = PreProcessor.readFile("test/world-all.txt");
		System.out.println(response0);
		
		JSONObject jsObjAll = JSONObject.fromObject(response0);		
		JSONObject jsObjData = JSONObject.fromObject(jsObjAll.get("returndata").toString());
		
		String response00 = jsObjData.get("resultList").toString();
		System.out.println(response00);
		JSONArray jsArray = JSONArray.fromObject(response00);
		for(int i=0;i<jsArray.size();i++)
		{
			System.out.println(jsArray.get(i));
		}*/

		/*
		java.util.Scanner input=new java.util.Scanner(System.in);
		while(true)
		{
			System.out.println("1.keyword：关键词;2. stop：退出");
			
			String inputStr = input.nextLine();
			inputStr = inputStr.trim();
			testAdvanced2(inputStr);
		}
		//*/

//		testAdvanced2("\"香港\"");
		
		
//		String text = "wei哈哈<em>魏<\\/em>=<em>西<\\/em>哈啊哈";
//		
//		text = text.replaceAll("<\\\\/em>(.)<em>", "$1");
//		System.out.println(text);
		
		
		
		//getUUID("1234567890");

//		Weiping20160330_test();
		
//		groupSearch("达赖");
		
//		updateUnit();
//		updateUnitBatch();
//		getUUID("8914694986826897648006E7AFC9A1C");
		
//		insertUnit();
//		deleteUnit();
		//testAdvanced2("(中国  and 中国)and(\"特朗普\")and(美国 or 大选)and not(希拉里 or 天津)");
		//testAdvanced2("(中国 and 北京)and or not ( )");
		
		
		testAdvanced2("中国");
//		testAdvanced2("手机 AND PHONE");
//		groupSearch("大数据");
//		testAdvanced3();
	}
}
