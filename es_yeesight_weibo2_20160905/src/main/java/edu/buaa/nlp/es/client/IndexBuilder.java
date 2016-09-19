package edu.buaa.nlp.es.client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;

import edu.buaa.nlp.es.constant.Configuration;
import edu.buaa.nlp.es.exception.ExceptionUtil;
import edu.buaa.wordsegment.PreProcessor;

/**
 * 索引工具类
 * @author Vincent
 *
 */
public class IndexBuilder {

	private Client client;
	private Logger logger=Logger.getLogger(getClass());
	
	public IndexBuilder() {
		try{
			client=ESClient.getClient();
		}catch(UnknownHostException uhe){
			logger.error(ExceptionUtil.getExceptionTrace(uhe));
		}
	}

	
	public IndexBuilder(String clusterName,String serverAddress) {
		try{
			this.client = ESClient.getClient(clusterName,serverAddress);
		}catch(UnknownHostException uhe){
			logger.error(ExceptionUtil.getExceptionTrace(uhe));
		}
	}
	
	/**
	 * 索引单个新闻
	 * @param jsonUnit 内容单元json串，key与IndexMapper.FieldXX中定义的保持一致
	 * @param indexName 指定特定的index名称
	 * @param type 指定索引中的类型
	 * @param idKey 指定的id键名
	 * @return
	 */
	public boolean addUnit(String jsonUnit, String indexName,String type,String idKey){
		XContentBuilder jsonBuilder=null;
		JSONObject json=JSONObject.fromObject(jsonUnit);
		IndexResponse response=null;
		try {
			jsonBuilder=XContentFactory.jsonBuilder().startObject();
			Iterator<String> it=json.keys();
			String key="";
			while(it.hasNext()){
				key=it.next();
				jsonBuilder.field(key, json.get(key));
			}
			jsonBuilder.endObject();
		} catch (IOException e) {
			logger.error(ExceptionUtil.getExceptionTrace(e));
		}finally{
			response=client.prepareIndex(indexName, type, json.getString(idKey)).setSource(jsonBuilder).get();
		}
		return response.isCreated();
	}
	
	/**
	 * 批量索引内容单元
	 * @param jsonUnitArray 内容单元json数组
	 * @param type 索引类型
	 * @return 返回成功添加文档的数量
	 */
	public int addUnitBatch(String jsonUnitArray, String indexName,String type,String idKey){
		BulkRequestBuilder brb=client.prepareBulk();
		JSONArray array=JSONArray.fromObject(jsonUnitArray);
		JSONObject obj=null;
		String key="";
		BulkResponse response=null;
		int sum=0;
		try {
			for(int i=0; i<array.size(); i++){
				XContentBuilder jsonBuilder=XContentFactory.jsonBuilder().startObject();
				obj=array.getJSONObject(i);
				Iterator<String> it=obj.keys();
				while(it.hasNext()){
					key=it.next();
					jsonBuilder.field(key, obj.get(key));
				}
				jsonBuilder.endObject();
				brb.add(client.prepareIndex(indexName, type, obj.getString(idKey)).setSource(jsonBuilder));
				sum++;
			}
		} catch (IOException e) {
			logger.error(ExceptionUtil.getExceptionTrace(e));
		}finally{
			response=brb.get();
			if(response.hasFailures()){
				logger.error(response.buildFailureMessage());
			}
		}
		return sum;
	}

	/**
	 * 修改内容单元索引
	 * @param jsonUnit 新的内容单元，json串，必须包含CONTENT_ID，否则修改失败。
	 * @param type 索引类型
	 * @return false 修改失败，否则返回true
	 */
	public boolean updateUnit(String jsonUnit, String indexName,String type,String idKey){
		JSONObject json=JSONObject.fromObject(jsonUnit);
		if(!json.containsKey(idKey) || json.getString(idKey)==null){
			return false;
		}
		UpdateRequest ur=new UpdateRequest();
		ur.index(indexName).type(type).id(json.getString(idKey));
		try {
			XContentBuilder xcb=XContentFactory.jsonBuilder().startObject();
			Iterator<String> it=json.keys();
			String key="";
			while(it.hasNext()){
				key=it.next();
				if(key.equals(idKey)) continue;
				xcb.field(key, json.get(key));
			}
			xcb.endObject();
			ur.doc(xcb);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			client.update(ur);
		}
		return ur.docAsUpsert();
	}
	
	/**
	 * 
	 * @param jsonUnit
	 * @param indexName
	 * @param type
	 * @param idKey
	 * @return
	 */
	public boolean updateField(String jsonUnit, String indexName,String type,String idKey){
		JSONObject json=JSONObject.fromObject(jsonUnit);
		if(!json.containsKey(idKey) || json.getString(idKey)==null){
			return false;
		}
		UpdateRequest ur=new UpdateRequest();
		ur.index(indexName).type(type).id(json.getString(idKey));
		ur.script(new Script("ctx._source.view +=1"));
		try {
			client.update(ur).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * 根据ID删除文档
	 * @param jsonUnit
	 * @return
	 */
	public boolean deleteUnit(String id,String indexName,String indexType){
		if(id==null || "".equals(id)) return false;
		DeleteResponse dr= client.prepareDelete(indexName,indexType, id).execute().actionGet();
		return true;
	}
	
	/**
	 * 根据ID删除文档
	 * @param jsonUnit
	 * @return
	 */
	public boolean deleteUnit(String jsonUnit,String indexName,String indexType,String idKey){
		JSONObject json=JSONObject.fromObject(jsonUnit);
		if(!json.containsKey(idKey) || json.getString(idKey)==null){
			return false;
		}
		DeleteResponse dr= client.prepareDelete(indexName,indexType, json.getString(idKey)).execute().actionGet();
		return true;
	}
	
	
	public void close(){
		if(client!=null)
			client.close();
	}

}
