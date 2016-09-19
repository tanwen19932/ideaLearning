package edu.buaa.nlp.test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;

import edu.buaa.nlp.es.client.ESClient;
import edu.buaa.nlp.es.client.IndexBuilder;
import edu.buaa.nlp.es.constant.Configuration;
import edu.buaa.nlp.es.exception.QueryFormatException;
import edu.buaa.nlp.es.news.Mapper;
import edu.buaa.nlp.es.news.SearchBuilder;
import edu.buaa.nlp.es.util.Constant;
import edu.buaa.nlp.es.util.DateUtil;
import edu.buaa.wordsegment.PreProcessor;

/**
 * Index维护类
 * @author Administrator
 *
 */
public class IndexMaint {
	public static List<String> getIdsFromFile(String filePath)
	{
		try
		{
			List<String> lstIds = new ArrayList<String>();			
			
			String content = PreProcessor.readFile(filePath);
			String[] ids = content.split("[\r\n]+");
			for(String id : ids)
			{
				if(id.trim().isEmpty()) continue;
				if(id.trim().matches("[0-9a-zA-Z]+"))
				{
					lstIds.add(id.trim());
				}
			}
			System.out.println(lstIds.size());
			return lstIds;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 删除指定文件中包含的id
	 * @param filePath
	 * @return
	 */
	public static boolean delelteIndexByIdsFile(String filePath)
	{
		try
		{
			List<String> lstIds = getIdsFromFile(filePath);
			if(lstIds == null)
			{
				return true;
			}
			
			for(String id : lstIds)
			{
				System.out.println(id);
			    DeleteResponse response = ESClient.getClient().prepareDelete(Configuration.INDEX_NAME,
			    		Configuration.INDEX_TYPE_ARTICLE, id)   
			            .execute()   
			            .actionGet();  
			    //System.out.println(response);
			}
			System.out.println("OK");
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean delelteIndexByIdsFile(String filePath,String indexName,String indexType)
	{
		try
		{
			List<String> lstIds = getIdsFromFile(filePath);
			if(lstIds == null)
			{
				return true;
			}
			
			Client client = ESClient.getClient();
			
			for(String id : lstIds)
			{
				System.out.println(id);
			    DeleteResponse response = client.prepareDelete(indexName,
			    		indexType, id)   
			            .execute()   
			            .actionGet();  
			    //System.out.println(response);
			}
			client.close();
			System.out.println("OK");
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * 删除指定文件中包含的id
	 * @param filePath
	 * @return
	 */
	public static boolean delelteIndexByIds(List<String> lstUUIDs)
	{
		try
		{

			StringBuffer sb = new StringBuffer();
			System.out.println(lstUUIDs.size());
			
			Client client = ESClient.getClient();
			
			for(String id : lstUUIDs)
			{
				try
				{
				System.out.println(id);
				sb.append(id + "\r\n");
			    DeleteResponse response = client.prepareDelete(Configuration.INDEX_NAME,
			    		Configuration.INDEX_TYPE_ARTICLE, id)   
			            .execute()   
			            .actionGet();  
			    //System.out.println(response);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		
			PreProcessor.writeFile("test/deleteBigDateIds.txt", "utf-8", sb.toString());
			System.out.println("OK");
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * 根据Ids文件提取Json对象
	 * @param filePath
	 * @return
	 */
	public static JSONArray getIndexByIdsFile(String filePath,String destPath)
	{
		try
		{
			List<String> lstIds = getIdsFromFile(filePath);
			if(lstIds == null)
			{
				return null;
			}

			JSONArray arr = new JSONArray();
			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(destPath,false), "utf-8");
			
			for(String id : lstIds)
			{
				try
				{
				    GetResponse responseGet = ESClient.getClient().prepareGet(Configuration.INDEX_NAME,
				    		Configuration.INDEX_TYPE_ARTICLE, id).execute().actionGet();  
				    arr.add(responseGet.getSource());
				    
				    System.out.println(id + ":" + responseGet.getSourceAsString());
				    osw.append(responseGet.getSourceAsString() + "\r\n");
				}
				catch(Exception e2)
				{
					e2.printStackTrace();
				}
			}
			osw.close();
			return arr;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean getAllIndex()
	{
		try
		{
		    Client esClient = ESClient.getClient();
		    SearchResponse searchResponse = esClient.prepareSearch(Configuration.INDEX_NAME)
		    			.setTypes(Configuration.INDEX_TYPE_ARTICLE)
		    			.setSearchType(SearchType.SCAN)		    //加上这个据说可以提高性能，但第一次却不返回结果
		    			.setSize(5)							//实际返回的数量为5*index的主分片格式
		    			.setScroll(TimeValue.timeValueMinutes(8))	 //这个游标维持多长时间
		    			.execute().actionGet();
		    //第一次查询，只返回数量和一个scrollId
		    System.out.println(searchResponse.getHits().getTotalHits());
		    System.out.println(searchResponse.getHits().hits().length);
		    //第一次运行没有结果
		    
		    for (SearchHit hit : searchResponse.getHits()) {
		        System.out.println(hit.getSourceAsString());
		    }
		    System.out.println("------------------------------");
		    //使用上次的scrollId继续访问
		    searchResponse = esClient.prepareSearchScroll(searchResponse.getScrollId())
		        .setScroll(TimeValue.timeValueMinutes(8))
		        .execute().actionGet();
		    System.out.println(searchResponse.getHits().getTotalHits());
		    System.out.println(searchResponse.getHits().hits().length);
		    int count =0;
		    for (SearchHit hit : searchResponse.getHits()) {
		    	System.out.println(count ++);
		        System.out.println(hit.getSourceAsString());
		    }
		    return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	
    // 获取大量数据
    public List<Long> getSearchDataByScrolls(QueryBuilder queryBuilder) {
        List<Long> ids = new ArrayList<Long>();
        // 一次获取100000数据
         
		try {
			Client	client = ESClient.getClient();
	
	        SearchResponse scrollResp = client.prepareSearch(Configuration.INDEX_NAME)
	        		.setTypes(Configuration.INDEX_TYPE_ARTICLE)
	                .setSearchType(SearchType.SCAN)
	                .setScroll(new TimeValue(60000))
	                .setQuery(queryBuilder)
	                .setSize(100000)
	                .execute().actionGet();
	        
	        while (true) {
	            for (SearchHit searchHit : scrollResp.getHits().getHits()) {
	                Long id = (Long) searchHit.getSource().get(edu.buaa.nlp.es.news.Mapper.FieldArticle.ID);
	                ids.add(id);
	            }
	            scrollResp = client.prepareSearchScroll(scrollResp.getScrollId())
	                    .setScroll(new TimeValue(600000)).execute().actionGet();
	            if (scrollResp.getHits().getHits().length == 0) {
	                break;
	            }
	        }
	        return ids;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }
	
    public static List<String> getAllDataByScrolls() {
        List<String> ids = new ArrayList<String>();
        // 一次获取100000数据
        
		try {
			Client client = ESClient.getClient();

	        SearchResponse scrollResp = client.prepareSearch(Configuration.INDEX_NAME)
	        		.setTypes(Configuration.INDEX_TYPE_ARTICLE)
	                .setSearchType(SearchType.SCAN)
	                .setScroll(new TimeValue(60000))
	                .setSize(1000)
	                .execute().actionGet();
	        
	        
	        while (true) {
	            for (SearchHit searchHit : scrollResp.getHits().getHits()) {
	            	String id = (String) searchHit.getSource().get(edu.buaa.nlp.es.news.Mapper.FieldArticle.ID);
	            	String textSrc = (String) searchHit.getSource().get(edu.buaa.nlp.es.news.Mapper.FieldArticle.TEXT_SRC);
	            	System.out.println(textSrc);
	                ids.add(id);
	            }
	            System.out.println(ids.size());
	            scrollResp = client.prepareSearchScroll(scrollResp.getScrollId())
	                    .setScroll(new TimeValue(600000)).execute().actionGet();
	            if (scrollResp.getHits().getHits().length == 0) {
	                break;
	            }
	        }
	        return ids;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }
    
	public static List<String> getQueryIds(){
		try
		{
			SearchBuilder sb=new SearchBuilder();
			JSONObject jsonQuery=new JSONObject();
	//		jsonQuery.put(Mapper.Query.KEYWORD,  "大纪元");
			jsonQuery.put(Mapper.Query.RESULT_TYPE, Constant.QUERY_RESULT_DETAIL); //.QUERY_RESULT_ANALYSIS);//.
			jsonQuery.put(Mapper.Query.PAGE_NO, 1);
			jsonQuery.put(Mapper.Query.PAGE_SIZE, 10);
			jsonQuery.put(Mapper.Query.HIGHLIGHT, false);
			jsonQuery.put(Mapper.Sort.FIELD_NAME, Mapper.Sort.RELEVANCE);
	//		jsonQuery.put(Mapper.Sort.FIELD_NAME, Mapper.FieldArticle.PUBDATE_SORT);
	//		jsonQuery.put(Mapper.Sort.FIELD_NAME, Mapper.FieldArticle.LANGUAGE_CODE);
	
			jsonQuery.put(Mapper.Sort.ORDER, Constant.QUERY_SORT_ORDER_DESC);
			//####################高级检索部分#######  begin  ##################33
			//媒体名称
			jsonQuery.put(Mapper.AdvancedQuery.FIELD_MEDIA_NAME_ZH, new String[]{"Beijingspring","大纪元新闻网","Minghui.org","Epoch Times","澳大利亚时报","New Tang Dynasty Television","Epoch Times Australia","Epoch Times China"});//,"有吧"});//
			//语言
	//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_LANGUAGE_TNAME, new String[]{"中文"});//语言名称数组
	//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_LANGUAGE, new String[]{"zh"});//语言名称数组 
			//日期
	//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_BEGIN_DATE, "2016-07-27 00:00:00");//开始日期
	//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_END_DATE, "9999-05-16 00:00:00");//结束日期
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
	//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_CATEGORY, new int[]{1,2,3,5,6});//领域ID数组
	
			//指定去掉websit ID
	//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_MEDIA_NOT, new int[]{166450, 166449, 166448, 166447, 166446, 166445, 166444, 166443, 166442, 166441, 165198, 165197, 165196, 165195, 165194, 165193, 165192, 165191, 165190, 165189, 165188, 165187, 165186, 165185, 165184, 165183, 165182, 165181, 165013, 164735, 164734, 164733, 164732, 164731, 164730, 164729, 164728, 164727, 164163, 164162, 164161, 164160, 164159, 164158, 164157, 164156, 164155, 164154, 164153, 164152, 164151, 163997, 163996, 163995, 163994, 163993, 163992, 163991, 163990, 163989, 163988, 163987, 163986, 163985, 163984, 163983, 163982, 163981, 163980, 163979, 163978, 163977, 163976, 163975, 163974, 163973, 163972, 163971, 163970, 163969, 163968, 163967, 163966, 163965, 163964, 163963, 163962, 163961, 163960, 163959, 163958, 163957, 163956, 163955, 163954, 163953, 163952, 163951, 163950, 163949, 163948, 163947, 163946, 163945, 163944, 163943, 163942, 163941, 163940, 163939, 163938, 163937, 163936, 163935, 163934, 163933, 163932, 163931, 163930, 163929, 163928, 163927, 163926, 163925, 163924, 163923, 163922, 163921, 163920, 163919, 163918, 163917, 163916, 163915, 16151, 168237, 168235, 168230, 168228, 168227, 168226, 168225, 168224, 168223, 168220, 15785, 15784, 15783, 15782, 15781, 15780, 15779, 15778, 15777, 15776, 15775, 15774, 15773, 15772, 15771, 15770, 15769, 15768, 15767, 15766, 15765, 15764, 15763, 15762, 15761, 15760, 15759, 15758, 15757, 15756, 15755, 15754, 15753, 15752, 15751, 15750, 15749, 15748, 15747, 15746, 15745, 15744, 15743, 15742, 15741, 15740, 15739, 15738, 15737, 15736, 15735, 15734, 15733, 15732, 15731, 15730, 15729, 15728, 15727, 15726, 15725, 15724, 15723, 15722, 15721, 15720, 15719, 15718, 15717, 15716, 15715, 15714, 15713, 15712, 15711, 15710, 15709, 15708, 15707, 15706, 15705, 15704, 15703, 15702, 15701, 15700, 15699, 15698, 15697, 15696, 15695, 183899, 168884, 168879, 166873, 166851, 166850, 166849, 166848, 166847, 166846, 41658, 15188, 15187, 15186, 15185, 15184, 15183, 15182, 15181, 15180, 15179, 15178, 15177, 15176, 15175, 15174, 15173, 15172, 15171, 15170, 15169, 15168, 15167, 13327, 13326, 13325, 13324, 13323, 13322, 13321, 13320, 13319, 13318, 13317, 176664, 176662, 176659, 176657, 176649, 176642, 171329, 173130, 172946, 171351, 166618, 168120, 168119, 168118, 168117, 168113, 168112, 168110, 168108, 168107, 168106, 168104, 135824, 122876, 28934, 28933, 28932, 28931, 28930, 28929, 28928, 28927, 28926, 28925, 28924, 28923, 28922, 164009, 164008, 164007, 164006, 164005, 164004, 164003, 164002, 164001, 164000, 163999, 163998, 163489, 15999, 15998, 15997, 15996, 15995, 15994, 15993, 15992, 15991, 15990, 15989, 15988, 15987, 15986, 13431, 13430, 13429, 13428, 13427, 13426, 13425, 13424, 13423, 13422, 13421, 13420, 13419, 13418, 13417, 13416, 13387, 13386, 13385, 13384, 13383, 13382, 13381, 13380, 13379, 13377, 13376});//媒体类型ID数组
			
			List<String> lstUUIDs = new ArrayList<String>();
			int pageNo = 1;
			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream("test/delids_" + DateUtil.getTimeLong(new Date()) + ".txt",false), "utf-8");
	
			while(true){
				long s1=System.currentTimeMillis();
				jsonQuery.put(Mapper.Query.PAGE_NO, pageNo ++ );
	//			String result=sb.crossSearch(jsonQuery.toString());
	//			String result=sb.specialSearch(jsonQuery.toString());
				String result=sb.filterSearch(jsonQuery.toString());
				//long e1=System.currentTimeMillis();
				
				//*
				JSONArray array=JSONObject.fromObject(result).getJSONArray(Mapper.Query.RESULT_LIST);
				System.out.println("result size:"+array.size());
				//System.out.println(JSONObject.fromObject(result).get(Mapper.Query.RESULT_COUNT));
				if(array.size() == 0) break;
				for(int i=0; i<array.size(); i++){
					JSONObject obj=array.getJSONObject(i);
					System.out.println(obj);
					String uuid = obj.getString("uuid");
					//osw.append(obj.toString() + "\r\n");
					osw.append(obj.getString("titleSrc") + "\t|||\t"+ obj.getString("categoryId") +  "\r\n");
					lstUUIDs.add(uuid);
				}
				//*/
				//System.out.println(e1-s1);
			}
			sb.close();
			osw.close();
			return lstUUIDs;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}

	}    
    
    
    
    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//IndexMaint.getIndexByIdsFile("test/getIndex.txt","test/getIndex_out.txt");
		//IndexMaint.getAllIndex();
/*		List<String> ids = IndexMaint.getAllDataByScrolls();
		for(String id : ids)
		{
			System.out.println(id);
		}
*/
		/*
	    GetResponse responseGet = ESClient.getClient().prepareGet(Configuration.INDEX_NAME,
	    		Configuration.INDEX_TYPE_ARTICLE, "821463423773882B0935BC7F39B29C7").execute().actionGet();  
	    System.out.println(responseGet.getSourceAsString());
		System.out.println("OK");
		*/
		List<String> lstUUIDs = getQueryIds();
		//List<String> lstUUIDs = new ArrayList<String>();
		//lstUUIDs.add("161466708360016A9FDC354EC3422CA");
		System.out.println(lstUUIDs.size());
		delelteIndexByIds(lstUUIDs);
		System.out.println("OK");
		
		
	}

}
