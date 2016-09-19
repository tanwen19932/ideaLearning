package edu.buaa.nlp.es.common;

import edu.buaa.nlp.es.client.ESClient;
import edu.buaa.nlp.es.constant.Configuration;
import edu.buaa.nlp.es.exception.ExceptionUtil;
import edu.buaa.nlp.es.util.Constant;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@SuppressWarnings("deprecation")
public class MediaSearchBuilder {

	private Client client;
	private Logger logger=Logger.getLogger(getClass());

	public MediaSearchBuilder() {
		try {
			this.client = ESClient.getClient();
		} catch (UnknownHostException e) {
			logger.error(ExceptionUtil.getExceptionTrace(e));
		}
	}
	
	/**
	 * 验证query是否合法，对不合法部分进行默认初始化
	 * @param jsonQuery
	 * @return
	 */
	private JSONObject initQuery(String jsonQuery){
		JSONObject obj=JSONObject.fromObject(jsonQuery);
		if(!obj.containsKey(MediaMapper.Query.KEYWORD)){
			obj.put(MediaMapper.Query.KEYWORD, "");
		}else{
			obj.put(MediaMapper.Query.KEYWORD, obj.getString(MediaMapper.Query.KEYWORD).replaceAll("/", "//"));
		}
		if(!obj.containsKey(MediaMapper.Query.PAGE_NO)){
			obj.put(MediaMapper.Query.PAGE_NO, Constant.QUERY_PAGE_NO_DEFAULT);
		}
		if(!obj.containsKey(MediaMapper.Query.PAGE_SIZE)){
			obj.put(MediaMapper.Query.PAGE_SIZE, Constant.QUERY_PAGE_SIZE_DEFAULT);
		}
		if(!obj.containsKey(MediaMapper.Query.INDEX_TYPE)){
			obj.put(MediaMapper.Query.INDEX_TYPE, Constant.QUERY_INDEX_TYPE_ALL);
		}
		return obj;
	}
	
	/**
	 * 过滤器
	 * 目前只支持url分类条件过滤
	 * @param jsonFilter
	 * @return
	 */
	public String filterSearch(String jsonFilter){
		JSONObject obj=JSONObject.fromObject(jsonFilter);
		if(!obj.containsKey(MediaMapper.Query.PAGE_NO)){
			obj.put(MediaMapper.Query.PAGE_NO, Constant.QUERY_PAGE_NO_DEFAULT);
		}
		if(!obj.containsKey(MediaMapper.Query.PAGE_SIZE)){
			obj.put(MediaMapper.Query.PAGE_SIZE, Constant.QUERY_PAGE_SIZE_DEFAULT);
		}
		if(!obj.containsKey(MediaMapper.Query.INDEX_TYPE)){
			obj.put(MediaMapper.Query.INDEX_TYPE, Constant.QUERY_INDEX_TYPE_ALL);
		}
		//获取过滤器设置
		QueryBuilder filter=filterQuery(obj);
		SearchRequestBuilder srb=client.prepareSearch(Configuration.INDEX_NAME);
		String type=obj.getString(MediaMapper.Query.INDEX_TYPE);
		if(type!=null && Constant.QUERY_INDEX_TYPE_ALL.equals(type)){
			srb.setTypes(Configuration.INDEX_TYPE_ARTICLE);
		}else{
			srb.setTypes(type);
		}
		FilteredQueryBuilder fqb=QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(), filter);
		SortBuilder sorter=getSort(obj);
		//设置排序
//		setSort(srb, obj);
		logger.info("[es-query]-"+fqb.toString());
		logger.info("[es-sort]-"+sorter.toString());
		SearchResponse sr=srb.setQuery(fqb)
				.setMinScore(Configuration.QUERY_RESULT_MIN_SCORE)
				.addSort(sorter)
				.setFrom((obj.getInt(MediaMapper.Query.PAGE_NO)-1)*obj.getInt(MediaMapper.Query.PAGE_SIZE)).setSize(obj.getInt(MediaMapper.Query.PAGE_SIZE))
//				.addAggregation(termAggregation(Mapper.FieldArticle.SECONDE_LEVEL))
				.execute().actionGet();
		SearchHits hits=sr.getHits();
		//封装结果
		SearchHit sh=null;
		List<JSONObject> list=new ArrayList<JSONObject>();
		for(int i=0; i<hits.hits().length; i++){
			sh=hits.getAt(i);
			list.add(getResult(sh));
		}
		JSONArray arr=JSONArray.fromObject(list);
		JSONObject result=new JSONObject();
		result.put(MediaMapper.Query.RESULT_LIST, arr);
		result.put(MediaMapper.Query.RESULT_COUNT, hits.getTotalHits());
		return result.toString();
	}
	
	
	public SearchRequestBuilder buildQuery(JSONObject obj){
		String key=obj.getString(MediaMapper.Query.KEYWORD);
		QueryBuilder initQb=QueryBuilders.queryStringQuery(key)
				.field(MediaMapper.FieldMedia.MEDIA_NAME_EN, 3)
				.field(MediaMapper.FieldMedia.MEDIA_NAME_ZH,3)
				.field(MediaMapper.FieldMedia.MEDIA_NAME_SRC, 3)
				.field(MediaMapper.FieldMedia.URL, 3);
		FilteredQueryBuilder fqb=QueryBuilders.filteredQuery(initQb, filterQuery(obj));
		SearchRequestBuilder srb=client.prepareSearch(Configuration.COMMON_INDEX_NAME);
		srb.setQuery(fqb);
		String type=obj.getString(MediaMapper.Query.INDEX_TYPE);
		if(type!=null && Constant.QUERY_INDEX_TYPE_ALL.equals(type)){
			srb.setTypes(Configuration.INDEX_TYPE_MEDIA);
		}else{
			srb.setTypes(type);
		}

		return srb;
	}
	
	/**
	 * 高级检索
	 * 1.替换逻辑符号，空格替换为||
	 * 2.判断查询语种，做跨语言扩展
	 * 3.检索
	 * @param jsonQuery
	 * @return
	 */
	public String keywordSearch(String jsonQuery){
		
		long s1=System.currentTimeMillis();
		JSONObject obj = initQuery(jsonQuery);

		try
		{
			SearchRequestBuilder srb=buildQuery(obj);
			if(srb==null) return "";
			SearchResponse sr=srb
					.addSort(getSort(obj))
					.setMinScore(Configuration.QUERY_RESULT_MIN_SCORE) //最低分值
					.setFrom((obj.getInt(MediaMapper.Query.PAGE_NO)-1)*obj.getInt(MediaMapper.Query.PAGE_SIZE))
					.setSize(obj.getInt(MediaMapper.Query.PAGE_SIZE))
					.execute().actionGet();
			SearchHits hits=sr.getHits();
			logger.info("[es-query]-"+srb.toString());
			//封装结果
			SearchHit sh=null;
			List<JSONObject> list=new ArrayList<JSONObject>();
			for(int i=0; i<hits.hits().length; i++){
				try
				{
					sh=hits.getAt(i);
					//	System.out.println(sh.getScore());
					list.add(getResult(sh));
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			JSONArray arr=JSONArray.fromObject(list);
			JSONObject result=new JSONObject();
			result.put(MediaMapper.Query.RESULT_LIST, arr);
			result.put(MediaMapper.Query.RESULT_COUNT, hits.getTotalHits());
			long e1=System.currentTimeMillis();
			System.out.println("time:"+(e1-s1)/1000);
			return result.toString();
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			return "{\"resultList\":[],\"resultCount\":0}";
		}
	}
	


	/**
	 * 封装查询过滤器
	 * 过滤内容包括：
	 * 日期、地区、语言、媒体级别、情感、领域
	 * @param jsonQuery
	 * @return
	 */
	private QueryBuilder filterQuery(JSONObject jsonQuery){
		List<QueryBuilder> filters=new ArrayList<QueryBuilder>();
		
		//url
		QueryBuilder fbURL=null;
		if(!Constant.isNullKey(jsonQuery, MediaMapper.AdvancedQuery.FIELD_URL)){
			fbURL=QueryBuilders.termQuery(MediaMapper.FieldMedia.URL, jsonQuery.getString(MediaMapper.AdvancedQuery.FIELD_URL));
			filters.add(fbURL);
		}		
		
		//集成
		QueryBuilder[] fbs=new QueryBuilder[filters.size()];
		BoolQueryBuilder qb=QueryBuilders.boolQuery();
		for(int i=0; i<fbs.length; i++){
			qb.must(filters.get(i));
		}
		return qb;
	}
	
	private JSONObject getResult(SearchHit sh){
		Map<String, Object> map=sh.getSource();
		JSONObject obj=new JSONObject();
		obj.put(MediaMapper.FieldMedia.ID, map.get(MediaMapper.FieldMedia.ID));
		
		obj.put(MediaMapper.FieldMedia.MEDIA_NAME_ZH, map.get(MediaMapper.FieldMedia.MEDIA_NAME_ZH));
		obj.put(MediaMapper.FieldMedia.MEDIA_NAME_EN, map.get(MediaMapper.FieldMedia.MEDIA_NAME_EN));
		obj.put(MediaMapper.FieldMedia.MEDIA_NAME_SRC, map.get(MediaMapper.FieldMedia.MEDIA_NAME_SRC));
		
		obj.put(MediaMapper.FieldMedia.COUNTRY_NAME_ZH, map.get(MediaMapper.FieldMedia.COUNTRY_NAME_ZH));
		obj.put(MediaMapper.FieldMedia.COUNTRY_NAME_EN, map.get(MediaMapper.FieldMedia.COUNTRY_NAME_EN));
		obj.put(MediaMapper.FieldMedia.PROVINCE_NAME_ZH, map.get(MediaMapper.FieldMedia.PROVINCE_NAME_ZH));
		obj.put(MediaMapper.FieldMedia.PROVINCE_NAME_EN, map.get(MediaMapper.FieldMedia.PROVINCE_NAME_EN));
		obj.put(MediaMapper.FieldMedia.DISTRICT_NAME_ZH, map.get(MediaMapper.FieldMedia.DISTRICT_NAME_ZH));
		obj.put(MediaMapper.FieldMedia.DISTRICT_NAME_EN, map.get(MediaMapper.FieldMedia.DISTRICT_NAME_EN));
		
		obj.put(MediaMapper.FieldMedia.MEDIA_LEVEL, map.get(MediaMapper.FieldMedia.MEDIA_LEVEL))
		;
		obj.put(MediaMapper.FieldMedia.URL, map.get(MediaMapper.FieldMedia.URL));
//		obj.put(MediaMapper.FieldMedia.MEDIA_TYPE, map.get(MediaMapper.FieldMedia.MEDIA_TYPE));
//		obj.put(MediaMapper.FieldMedia.MEDIA_TNAME, map.get(MediaMapper.FieldMedia.MEDIA_TNAME));
		
		obj.put(MediaMapper.FieldMedia.LANGUAGE_TNAME, map.get(MediaMapper.FieldMedia.LANGUAGE_TNAME));
		obj.put(MediaMapper.FieldMedia.LANGUAGE_CODE, map.get(MediaMapper.FieldMedia.LANGUAGE_CODE));
		
		return obj;
	}
	
	
	
	//排序
	private SortBuilder getSort(JSONObject obj){
		if(!obj.containsKey(MediaMapper.Sort.ORDER)) return SortBuilders.scoreSort(); 
		String orderStr=obj.getString(MediaMapper.Sort.ORDER);
		if(orderStr==null || "".equals(orderStr)){
			return SortBuilders.scoreSort();
		}
		SortOrder order=Constant.QUERY_SORT_ORDER_ASC.equals(orderStr) ? SortOrder.ASC : SortOrder.DESC;
//		return SortBuilders.scriptSort("new java.text.SimpleDateFormat('yyyy-MM-dd HH:mm:dd').format(new Date(doc['"+Mapper.FieldArticle.PUBDATE+"'].value))", "string").order(order);
		return SortBuilders.fieldSort(obj.getString(MediaMapper.Sort.FIELD_NAME)).order(order);
	}
	
	public void close(){
		if(client!=null)
			this.client.close();
	}
}
