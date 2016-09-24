/**
 * 
 */
package edu.buaa.nlp.es.client;

import edu.buaa.nlp.es.exception.ExceptionUtil;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;



/**
 * @author Administrator
 * SearchBuilder的基类，后续其他类都继承自它
 */
public class SearchBuilder {
	private Client client = null;;
	private Logger logger=Logger.getLogger(getClass());	
	private IndexBuilder builder = null;
	//for get all index
	 private SearchResponse scrollResp = null;
	 private int indexSize = 10000;		//每次获得的索引大小
	 private long scrollTime = 60000;	//每次光标的时间
	
	public SearchBuilder() {
		try {
			this.client = ESClient.getClient();
		} catch (UnknownHostException e) {
			logger.error(ExceptionUtil.getExceptionTrace(e));
		}
	}
	
	public SearchBuilder(String clusterName,String serverAddress) {
		try {
			this.client = ESClient.getClient(clusterName,serverAddress);
		} catch (UnknownHostException e) {
			logger.error(ExceptionUtil.getExceptionTrace(e));
		}
	}	
	
	public void close(){
		try
		{
			if(builder != null)
			{
				builder.close();
			}
		}
		catch(Exception e)
		{
			logger.error(ExceptionUtil.getExceptionTrace(e));
		}		
		if(client!=null)
			this.client.close();
	}	
	
	/**
	 * 初始化依次获得所有index
	 * @param indexSize	每次获得多少index
	 * @param scrollTime 每次的时间
	 * @return
	 */
	public boolean initGetAllIndex(String indexName,String indexType,int indexSize,long scrollTime)
	{
		try
		{
			this.indexSize = indexSize;
			this.scrollTime = scrollTime;
	       
	        // 一次获取indexSize数据
			if(client == null)
			{
				client = ESClient.getClient();
			}
	        this.scrollResp = client.prepareSearch(indexName)
	        		.setTypes(indexType)
	                .setSearchType(SearchType.SCAN)
	                .setScroll(new TimeValue(scrollTime))
	                .setSize(indexSize)
	                .execute().actionGet();
	        for (SearchHit searchHit : scrollResp.getHits().getHits()) {
	        	
	        }
	        return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public List<String> nextGetAllIndex()
	{
		try
		{
			List<String> ids = new ArrayList<String>();
			if(scrollResp == null)
			{
				return null;
			}
	        scrollResp = client.prepareSearchScroll(scrollResp.getScrollId())
	              .setScroll(new TimeValue(scrollTime)).execute().actionGet();
	        if (scrollResp.getHits().getHits().length == 0) {
	                return null;
	        }
	        for (SearchHit searchHit : scrollResp.getHits().getHits()) {
	        	
	        	//String id = (String) searchHit.getSource().get(Mapper.FieldComment.UUID).toString();
	            //ids.add(id);
	        	
	        	ids.add(searchHit.getSourceAsString());
	        }
	        return ids;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
		
	
	public boolean updateUnit(JSONObject jsonObject,String indexName,String indexType,String idName)
	{
		try
		{
			if(builder == null)
			{
				builder=new IndexBuilder();
			}
			return builder.updateUnit(jsonObject.toString(), indexName, indexType,idName);
			
		}
		catch(Exception e)
		{
			logger.error(ExceptionUtil.getExceptionTrace(e));
			return false;
		}
	}
	
	public boolean insertUnit(JSONObject jsonObject,String indexName,String indexType,String idName)
	{
		try
		{
			if(builder == null)
			{
				builder=new IndexBuilder();
			}
			return builder.addUnit(jsonObject.toString(), indexName,indexType,idName);
			
		}
		catch(Exception e)
		{
			logger.error(ExceptionUtil.getExceptionTrace(e));
			return false;
		}
	}
	
	
	public boolean deleteUnit(JSONObject jsonObject,String indexName,String indexType,String idName)
	{
		try
		{
			if(builder == null)
			{
				builder=new IndexBuilder();
			}
			return builder.deleteUnit(jsonObject.toString(), indexName,indexType,idName);
			
		}
		catch(Exception e)
		{
			logger.error(ExceptionUtil.getExceptionTrace(e));
			return false;
		}
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
