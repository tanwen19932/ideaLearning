package edu.buaa.nlp.es.product;

import edu.buaa.nlp.es.client.ESClient;
import edu.buaa.nlp.es.client.IndexBuilder;
import edu.buaa.nlp.es.constant.Configuration;
import edu.buaa.nlp.es.exception.ExceptionUtil;
import edu.buaa.nlp.es.exception.QueryFormatException;
import edu.buaa.nlp.es.util.Constant;
import edu.buaa.nlp.es.util.DateUtil;
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
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import java.net.UnknownHostException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("deprecation")
public class SearchBuilder {

	private Client client;
	private Logger logger=Logger.getLogger(getClass());
	private IndexBuilder builder=null;

	private static Pattern patYinHao = Pattern.compile("(\"[^\"]+\")");
	private static String yinhaoTag = "YH_"; //引号标签
	
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
	
	/**
	 * 验证query是否合法，对不合法部分进行默认初始化
	 * @param jsonQuery
	 * @return
	 */
	private JSONObject initQuery(String jsonQuery){
		JSONObject obj=JSONObject.fromObject(jsonQuery);
		if(!obj.containsKey(Mapper.Query.KEYWORD)){
			obj.put(Mapper.Query.KEYWORD, "");
		}else{
			obj.put(Mapper.Query.KEYWORD, obj.getString(Mapper.Query.KEYWORD).replaceAll("/", "//"));
		}
		if(!obj.containsKey(Mapper.Query.PAGE_NO)){
			obj.put(Mapper.Query.PAGE_NO, Constant.QUERY_PAGE_NO_DEFAULT);
		}
		if(!obj.containsKey(Mapper.Query.PAGE_SIZE)){
			obj.put(Mapper.Query.PAGE_SIZE, Constant.QUERY_PAGE_SIZE_DEFAULT);
		}
		if(!obj.containsKey(Mapper.Query.INDEX_TYPE)){
			obj.put(Mapper.Query.INDEX_TYPE, Constant.QUERY_INDEX_TYPE_ALL);
		}
		return obj;
	}

	/**
	 * 过滤器
	 * 目前只支持日期、地区、语言、媒体级别、媒体、情感、领域分类条件过滤
	 * @param jsonFilter
	 * @return
	 */
	public String filterSearch(String jsonFilter){
		JSONObject obj=JSONObject.fromObject(jsonFilter);
		if(!obj.containsKey(Mapper.Query.PAGE_NO)){
			obj.put(Mapper.Query.PAGE_NO, Constant.QUERY_PAGE_NO_DEFAULT);
		}
		if(!obj.containsKey(Mapper.Query.PAGE_SIZE)){
			obj.put(Mapper.Query.PAGE_SIZE, Constant.QUERY_PAGE_SIZE_DEFAULT);
		}
		if(!obj.containsKey(Mapper.Query.INDEX_TYPE)){
			obj.put(Mapper.Query.INDEX_TYPE, Constant.QUERY_INDEX_TYPE_ALL);
		}
		//获取过滤器设置
		QueryBuilder filter=filterQuery(obj);
		SearchRequestBuilder srb=client.prepareSearch(Configuration.PRODUCT_INDEX_NAME);
		String type=obj.getString(Mapper.Query.INDEX_TYPE);
		if(type!=null && Constant.QUERY_INDEX_TYPE_ALL.equals(type)){
			srb.setTypes(Configuration.PRODUCT_INDEX_TYPE_COMMENT);
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
				.setFrom((obj.getInt(Mapper.Query.PAGE_NO)-1)*obj.getInt(Mapper.Query.PAGE_SIZE)).setSize(obj.getInt(Mapper.Query.PAGE_SIZE))
//				.addAggregation(termAggregation(Mapper.FieldArticle.SECONDE_LEVEL))
				.execute().actionGet();
		SearchHits hits=sr.getHits();
		//封装结果
		SearchHit sh=null;
		List<JSONObject> list=new ArrayList<JSONObject>();
		for(int i=0; i<hits.hits().length; i++){
			sh=hits.getAt(i);
//			arr.add(getResultByType(sh));
			list.add(getResultByType(sh, obj.getString(Mapper.Query.RESULT_TYPE)));
		}
		JSONArray arr=JSONArray.fromObject(list);
		JSONObject result=new JSONObject();
		result.put(Mapper.Query.RESULT_LIST, arr);
		result.put(Mapper.Query.RESULT_COUNT, hits.getTotalHits());
//		result.put(Mapper.Query.RESULT_GROUP, parseAggregationBucket((Terms) sr.getAggregations().get(Mapper.FieldArticle.SECONDE_LEVEL)));
		return result.toString();
	}
	
	
	private SearchRequestBuilder buildQuery(JSONObject obj){
		String key=obj.getString(Mapper.Query.KEYWORD);
		//cross
		BoolQueryBuilder qb=QueryBuilders.boolQuery();

		QueryBuilder initQb=QueryBuilders.queryStringQuery(key)
				.field(Mapper.FieldComment.PRODUCT_NAME,5)
				.field(Mapper.FieldComment.COMPANY_NAME,5)
				.field(Mapper.FieldComment.REFERENCE_NAME,5)
				.field(Mapper.FieldComment.CONTENT,3)
				.field(Mapper.FieldComment.CONTENT_ZH,3)
				.field(Mapper.FieldComment.CONTENT_EN,3)
				;
		
		


//		qb.must(initQb);
		FilteredQueryBuilder fqb=QueryBuilders.filteredQuery(initQb, filterQuery(obj));
		SearchRequestBuilder srb=client.prepareSearch(Configuration.PRODUCT_INDEX_NAME);
		srb.setQuery(fqb);
		String type=obj.getString(Mapper.Query.INDEX_TYPE);
		//同时从Weibo和微博评论中查询？
		if(type!=null && Constant.QUERY_INDEX_TYPE_ALL.equals(type)){
			srb.setTypes(Configuration.PRODUCT_INDEX_TYPE_COMMENT);
		}else{
			srb.setTypes(type);
		}
		//是否高亮
		boolean highlight=obj.getBoolean(Mapper.Query.HIGHLIGHT);
		if(highlight){
			srb
			.addHighlightedField(Mapper.FieldComment.CONTENT)
			.addHighlightedField(Mapper.FieldComment.CONTENT_ZH)
			.addHighlightedField(Mapper.FieldComment.CONTENT_EN)
			;
		}
		return srb;
	}
	

	private SearchRequestBuilder buildQueryBySentiElements(JSONObject obj){
		String key=obj.getString(Mapper.Query.KEYWORD);
		//cross
		BoolQueryBuilder qb=QueryBuilders.boolQuery();
		
		QueryBuilder initQb=QueryBuilders.queryStringQuery(key)
				.field(Mapper.FieldComment.CONTENT,3)
				.field(Mapper.FieldComment.CONTENT_ZH,3)
				.field(Mapper.FieldComment.CONTENT_EN,3)
				;
//		qb.must(initQb);
		FilteredQueryBuilder fqb=QueryBuilders.filteredQuery(initQb, filterQuery(obj));
		SearchRequestBuilder srb=client.prepareSearch(Configuration.PRODUCT_INDEX_NAME);
		srb.setQuery(fqb);
		String type=obj.getString(Mapper.Query.INDEX_TYPE);
		//同时从Weibo和微博评论中查询？
		if(type!=null && Constant.QUERY_INDEX_TYPE_ALL.equals(type)){
			srb.setTypes(Configuration.PRODUCT_INDEX_TYPE_COMMENT);
		}else{
			srb.setTypes(type);
		}
		//是否高亮
		boolean highlight=obj.getBoolean(Mapper.Query.HIGHLIGHT);
		if(highlight){
			srb
			.addHighlightedField(Mapper.FieldComment.CONTENT)
			.addHighlightedField(Mapper.FieldComment.CONTENT_ZH)
			.addHighlightedField(Mapper.FieldComment.CONTENT_EN)
			;
		}
		return srb;
	}
	
	
	
	
	private SearchRequestBuilder buildQueryOther(JSONObject obj){
		String key=obj.getString(Mapper.Query.KEYWORD);
		//cross
		BoolQueryBuilder qb=QueryBuilders.boolQuery();
		
		QueryBuilder initQb=QueryBuilders.queryStringQuery(key)
				.field(Mapper.FieldComment.PRODUCT_NAME,5)
				.field(Mapper.FieldComment.COMPANY_NAME,5)
				.field(Mapper.FieldComment.REFERENCE_NAME,5)
				.field(Mapper.FieldComment.CONTENT,3)
				.field(Mapper.FieldComment.CONTENT_ZH,3)
				.field(Mapper.FieldComment.CONTENT_EN,3)
				;

//		qb.must(initQb);
		FilteredQueryBuilder fqb=QueryBuilders.filteredQuery(initQb, filterQuery(obj));
		SearchRequestBuilder srb=client.prepareSearch(Configuration.PRODUCT_INDEX_NAME);
		srb.setQuery(fqb);
		String type=obj.getString(Mapper.Query.INDEX_TYPE);
		//同时从Weibo和微博评论中查询？
		if(type!=null && Constant.QUERY_INDEX_TYPE_ALL.equals(type)){
			srb.setTypes(Configuration.PRODUCT_INDEX_TYPE_COMMENT);
		}else{
			srb.setTypes(type);
		}
		//是否高亮
		boolean highlight=obj.getBoolean(Mapper.Query.HIGHLIGHT);
		if(highlight){
			srb
			.addHighlightedField(Mapper.FieldComment.CONTENT)
			.addHighlightedField(Mapper.FieldComment.CONTENT_ZH)
			.addHighlightedField(Mapper.FieldComment.CONTENT_EN)
			;
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
	public String crossSearch(String jsonQuery){
		long s1=System.currentTimeMillis();
		JSONObject obj=null;
		try {
			obj = initAdvancedQuery(jsonQuery);
		} catch (QueryFormatException e) {
			logger.error(ExceptionUtil.getExceptionTrace(e));
			return null;
		}
		SearchRequestBuilder srb=buildQuery(obj);
		if(srb==null) return "";
		SearchResponse sr=srb
				.addSort(getSort(obj))
				.setMinScore(Configuration.QUERY_RESULT_MIN_SCORE) //最低分值
				.setFrom((obj.getInt(Mapper.Query.PAGE_NO)-1)*obj.getInt(Mapper.Query.PAGE_SIZE))
				.setSize(obj.getInt(Mapper.Query.PAGE_SIZE))
				.execute().actionGet();
		SearchHits hits=sr.getHits();
		logger.info("[es-query]-"+srb.toString());
		//封装结果
		SearchHit sh=null;
		List<JSONObject> list=new ArrayList<JSONObject>();
		for(int i=0; i<hits.hits().length; i++){
			sh=hits.getAt(i);
			System.out.println(sh.getScore());
			list.add(getResultByType(sh, obj.getString(Mapper.Query.RESULT_TYPE)));
		}
		JSONArray arr=JSONArray.fromObject(list);
		JSONObject result=new JSONObject();
		result.put(Mapper.Query.RESULT_LIST, arr);
		result.put(Mapper.Query.RESULT_COUNT, hits.getTotalHits());
		long e1=System.currentTimeMillis();
		System.out.println("time:"+(e1-s1)/1000);
		return result.toString();
	}
	
	
	public String crossSearchBySentiElement(String jsonQuery){
		long s1=System.currentTimeMillis();
		JSONObject obj=null;
		try {
			obj = initAdvancedQuery(jsonQuery);
		} catch (QueryFormatException e) {
			logger.error(ExceptionUtil.getExceptionTrace(e));
			return null;
		}
		SearchRequestBuilder srb=buildQueryBySentiElements(obj);
		if(srb==null) return "";
		SearchResponse sr=srb
				.addSort(getSort(obj))
				.setMinScore(Configuration.QUERY_RESULT_MIN_SCORE) //最低分值
				.setFrom((obj.getInt(Mapper.Query.PAGE_NO)-1)*obj.getInt(Mapper.Query.PAGE_SIZE))
				.setSize(obj.getInt(Mapper.Query.PAGE_SIZE))
				.execute().actionGet();
		SearchHits hits=sr.getHits();
		logger.info("[es-query]-"+srb.toString());
		//封装结果
		SearchHit sh=null;
		List<JSONObject> list=new ArrayList<JSONObject>();
		for(int i=0; i<hits.hits().length; i++){
			sh=hits.getAt(i);
			list.add(getResultByType(sh, obj.getString(Mapper.Query.RESULT_TYPE)));
		}
		JSONArray arr=JSONArray.fromObject(list);
		JSONObject result=new JSONObject();
		result.put(Mapper.Query.RESULT_LIST, arr);
		result.put(Mapper.Query.RESULT_COUNT, hits.getTotalHits());
		long e1=System.currentTimeMillis();
		System.out.println("time:"+(e1-s1)/1000);
		return result.toString();
	}
	
	
	
	/**
	 * 为专题分析提供，采用scroll
	 * @param jsonQuery
	 * @return
	 */
	public String specialSearch(String jsonQuery){
		long s1=System.currentTimeMillis();
		JSONObject obj=null;
		try {
			obj = initAdvancedQuery(jsonQuery);
		} catch (QueryFormatException e) {
			logger.error(ExceptionUtil.getExceptionTrace(e));
			return "";
		}
		SearchRequestBuilder srb=buildQuery(obj);
		if(srb==null) return "";
		int shards=greenShards(obj);
		if(shards==0) return "";
		int goal=obj.getInt(Mapper.Query.PAGE_SIZE);
		/*if(goal>shards) {
			goal=(int) Math.ceil(1.0*goal/shards);
		}*/
		SearchResponse sr1=srb
				.addSort(getSort(obj))
//				.setMinScore(Mapper.QUERY_RESULT_MIN_SCORE) //最低分值
				.setSearchType(SearchType.SCAN)
				.setSize(goal)
				.setScroll(new TimeValue(6000))
				.execute().actionGet();
		logger.info("[es-query]-"+srb.toString());
		List<JSONObject> list=new ArrayList<JSONObject>();
		while(true){
			SearchResponse sr2= client.prepareSearchScroll(sr1.getScrollId())
					.setScroll(new TimeValue(10))
					.execute().actionGet();
			//封装结果
			SearchHits hits=sr2.getHits();
			if(hits.getHits().length==0) break;
			SearchHit sh=null;
			for(int i=0; i<hits.hits().length; i++){
				sh=hits.getAt(i);
				list.add(getResultByType(sh, Constant.QUERY_RESULT_ANALYSIS));
			}
		}
		JSONArray arr=JSONArray.fromObject(list);
		JSONObject result=new JSONObject();
		result.put(Mapper.Query.RESULT_LIST, arr);
		result.put(Mapper.Query.RESULT_COUNT, sr1.getHits().totalHits());
		long e1=System.currentTimeMillis();
		System.out.println("time:"+(e1-s1)/1000);
		return result.toString();
	}
	
	//状态为green的shard数量
	private int greenShards(JSONObject obj){
		ClusterHealthResponse chr=client.admin().cluster()
				.prepareHealth(Configuration.SOCIALITY_INDEX_NAME)
				.setWaitForGreenStatus()
				.get();
		ClusterHealthStatus status=chr.getStatus();
		if(status.equals(ClusterHealthStatus.GREEN)){
			return chr.getActiveShards();
		}
		return 0;
	}
	
	private JSONObject initAdvancedQuery(String jsonQuery) throws QueryFormatException{
		//保证基本信息完整
		
		JSONObject obj=initQuery(jsonQuery);
		String keyword=obj.getString(Mapper.Query.KEYWORD);
		
		keyword = initKeyword(keyword);
		
		obj.put(Mapper.Query.KEYWORD, keyword.trim());
		return obj;
	}
	
	public String initKeyword(String keyword) throws QueryFormatException{
		//keyword = "titleZh:\"中国\"  or titleEn:\"中国\" or titleSrc:\"中国\"";
		//keyword = "TITLE:冰棍";
		//keyword = "\"冰棍\"";
		
		//把 引号的独立出来作为一个词
		Matcher matYinhao = patYinHao.matcher(keyword);
		int index = 0;
		Map<String,String> hashYinhao = new HashMap<String,String>();
		while(matYinhao.find())
		{
			++index ;
			String yinhaoGroup = matYinhao.group(1);
			hashYinhao.put(yinhaoTag + index, yinhaoGroup);
		}
		for(String key : hashYinhao.keySet())
		{
			keyword = keyword.replace(hashYinhao.get(key), key);
		}
		
		
		
		keyword = keyword.trim();
		keyword= keyword.replaceAll("((and|not|or)\\s*)+\\s*\\(\\s*\\)","");
		keyword= keyword.replaceAll("((and|not|or)\\s*)+\\s*\\(\"\\s*\"\\)","");
		keyword= keyword.replaceAll("and\\s*\\(\"\\s*\"\\)","");
		keyword= keyword.replaceAll("and\\s+not\\s*\\(\\s*\\)", "");
		keyword= keyword.replaceAll("and\\s*\\(\\s*\\)", "");
		keyword= keyword.replaceAll("\\(\\s*\\)", "");
		keyword=keyword.replaceAll("(（|\\()", " ( ")
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
		
		keyword=keyword.replaceAll("\\(", " ( ")
				.replaceAll("\\s*\\(\\s*", "_(_")
				.replaceAll("\\s*\\)\\s*", "_)_")
				.trim();		
		
		keyword=keyword.replace(" ", Constant.QUERY_OPERATOR_AND)
				.replace("&", Constant.QUERY_OPERATOR_AND)
				.replace("|", Constant.QUERY_OPERATOR_OR)
				.replace("~", Constant.QUERY_OPERATOR_NOT)
				;
		
		keyword = keyword.replaceAll( "_AND_NOT_" ," and not ");
		keyword = keyword.replaceAll( Constant.QUERY_OPERATOR_OR2," " + Constant.QUERY_OPERATOR_OR + " ");
		keyword = keyword.replaceAll( Constant.QUERY_OPERATOR_AND2," " + Constant.QUERY_OPERATOR_AND + " ");
		keyword = keyword.replaceAll( Constant.QUERY_OPERATOR_NOT2," " + Constant.QUERY_OPERATOR_NOT + " ");
		
		
		keyword = keyword.replace( "_(_" ," ( ");
		keyword = keyword.replace( "_)_" ," ) ");
		//TODO: chaowenhan 先分词再重新提交，比如 美国大选 => 美国 & 大选

		String[] items = keyword.split("\\s+");
		keyword = "";

		for(String item : items)
		{
			if(item.trim().isEmpty()) continue;
			if(item.trim().equalsIgnoreCase("and") 
					|| item.equalsIgnoreCase("not")
					|| item.equalsIgnoreCase("or") 
					|| item.equalsIgnoreCase("(")
					|| item.equalsIgnoreCase(")")
					)
			{
				keyword += item + " ";
			}
			else if(item.startsWith("\"") && item.endsWith("\""))
			{
				keyword += item + " ";
			}
			else		
			{	
				//对于每个单元，都是用 引号 强括号
				keyword += " \"" + item + "\" ";
			}

		}

		keyword = keyword.replaceAll("(AND\\s+OR\\s+)+"," OR ");
		keyword = keyword.replaceAll("(OR\\s+AND\\s+)+"," OR ");
		keyword = keyword.replaceAll("(AND\\s+NOT\\s+)+"," NOT ");
		keyword = keyword.replaceAll("(NOT\\s+AND\\s+)+"," NOT ");
		keyword = keyword.replaceAll("(OR\\s+)+"," OR ");
		keyword = keyword.replaceAll("(NOT\\s+)+"," NOT ");
		keyword = keyword.replaceAll("(AND\\s+)+"," AND ");
		keyword = keyword.replaceAll("\\s+"," ");
		
		keyword = keyword.trim();
		
		for(String key : hashYinhao.keySet())
		{
			keyword = keyword.replace("\"" + key + "\"", hashYinhao.get(key));
			keyword = keyword.replace(key, hashYinhao.get(key));
		}
		return keyword;
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
		//日期过滤
		QueryBuilder fbDate=null;
		if(Constant.isNullKey(jsonQuery, Mapper.AdvancedQuery.FIELD_BEGIN_DATE) ){
			if(!Constant.isNullKey(jsonQuery, Mapper.AdvancedQuery.FIELD_END_DATE)){
				fbDate=QueryBuilders.rangeQuery(Mapper.FieldComment.CREATE_TIME)
						.gte(DateUtil.time2Unix("1970-01-01 00:00:00"))
						.lte(DateUtil.time2Unix(jsonQuery.getString(Mapper.AdvancedQuery.FIELD_END_DATE)));
				filters.add(fbDate);
			}
		}else {
			if(Constant.isNullKey(jsonQuery, Mapper.AdvancedQuery.FIELD_END_DATE)){
				fbDate=QueryBuilders.rangeQuery(Mapper.FieldComment.CREATE_TIME)
						.gte(DateUtil.time2Unix(jsonQuery.getString(Mapper.AdvancedQuery.FIELD_BEGIN_DATE)))
						.lte(DateUtil.time2Unix(DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss")));
			}else{
				fbDate=QueryBuilders.rangeQuery(Mapper.FieldComment.CREATE_TIME)
						.gte(DateUtil.time2Unix(jsonQuery.getString(Mapper.AdvancedQuery.FIELD_BEGIN_DATE)))
						.lte(DateUtil.time2Unix(jsonQuery.getString(Mapper.AdvancedQuery.FIELD_END_DATE)));
			}
			filters.add(fbDate);
		}
		
		//UUID
		QueryBuilder fbUUID=null;
		if(!Constant.isNullKey(jsonQuery, Mapper.AdvancedQuery.FIELD_COMMENT_UUID)){
			JSONArray arr=jsonQuery.getJSONArray(Mapper.AdvancedQuery.FIELD_COMMENT_UUID);
			String[] weiboIds=new String[arr.size()]; 
			for(int i=0; i<arr.size(); i++){
				weiboIds[i]=arr.getString(i);
			}
			fbUUID=QueryBuilders.termsQuery(Mapper.FieldComment.UUID, weiboIds);
			filters.add(fbUUID);
		}	
		
		//*
//		//产品名
		QueryBuilder fbProductName=null;
		if(!Constant.isNullKey(jsonQuery, Mapper.AdvancedQuery.FIELD_PRODUCT_NAME)){
			JSONArray arr=jsonQuery.getJSONArray(Mapper.AdvancedQuery.FIELD_PRODUCT_NAME);
			String[] productNames=new String[arr.size()]; 
			for(int i=0; i<arr.size(); i++){
				productNames[i]="\"" + arr.getString(i) + "\"";
			}
			//fbProductName=FilterBuilders.inFilter(Mapper.FieldComment.PRODUCT_NAME, productNames);			
			//fbProductName = FilterBuilders.queryFilter(QueryBuilders.queryStringQuery(productNames[0])
					//.field(Mapper.FieldComment.PRODUCT_NAME,5)
			//		); //Mapper.FieldComment.PRODUCT_NAME,productNames[0]);
			
			//fbProductName = FilterBuilders.queryFilter(QueryBuilders.matchPhraseQuery(Mapper.FieldComment.WEBSITE, productNames[0]));  
			//fbProductName = FilterBuilders.regexpFilter(Mapper.FieldComment.PRODUCT_NAME, productNames[0]));
			fbProductName = QueryBuilders.termsQuery(Mapper.FieldComment.PRODUCT_NAME, productNames);
			filters.add(fbProductName);
		}	
		
		
		//*//
		/*
		//公司名
		QueryBuilder fbCompanyName=null;
		if(!Constant.isNullKey(jsonQuery, Mapper.AdvancedQuery.FIELD_COMPANY_NAME)){
			try
			{
				String companyName = initQueryKeyword(jsonQuery.getString(Mapper.AdvancedQuery.FIELD_COMPANY_NAME));
				fbCompanyName=FilterBuilders.queryFilter(QueryBuilders.queryStringQuery(companyName)
						   .field(Mapper.FieldComment.COMPANY_NAME,5));
				filters.add(fbCompanyName);
			} catch (QueryFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		*/
		
		
		//行业名
		QueryBuilder fbIndustryName=null;
		if(!Constant.isNullKey(jsonQuery, Mapper.AdvancedQuery.FIELD_INDUSTRY_NAME)){
			JSONArray arr=jsonQuery.getJSONArray(Mapper.AdvancedQuery.FIELD_INDUSTRY_NAME);
			String[] regionIds=new String[arr.size()]; 
			for(int i=0; i<arr.size(); i++){
				regionIds[i]=arr.getString(i);
			}
			fbIndustryName=QueryBuilders.termsQuery(Mapper.FieldComment.INDUSTRY_NAME, regionIds);
			filters.add(fbIndustryName);
		}	
		
		//站点名
		QueryBuilder fbWebsite=null;
		if(!Constant.isNullKey(jsonQuery, Mapper.AdvancedQuery.FIELD_WEBSITE)){
			JSONArray arr=jsonQuery.getJSONArray(Mapper.AdvancedQuery.FIELD_WEBSITE);
			String[] websites=new String[arr.size()]; 
			for(int i=0; i<arr.size(); i++){
				websites[i]=arr.getString(i);
			}
			fbWebsite=QueryBuilders.termsQuery(Mapper.FieldComment.WEBSITE, websites);
			filters.add(fbWebsite);
		}	
		
		
		//语言
		QueryBuilder fbLang=null;
		if(!Constant.isNullKey(jsonQuery, Mapper.AdvancedQuery.FIELD_LANGUAGE)){
			JSONArray arr=jsonQuery.getJSONArray(Mapper.AdvancedQuery.FIELD_LANGUAGE);
			String[] langIds=new String[arr.size()]; 
			for(int i=0; i<arr.size(); i++){
				langIds[i]=arr.getString(i);
			}
			fbLang=QueryBuilders.termsQuery(Mapper.FieldComment.LANGUAGE_CODE, langIds);
			filters.add(fbLang);
		}
		
		//情感
		QueryBuilder fbSentiment=null;
		if(!Constant.isNullKey(jsonQuery, Mapper.AdvancedQuery.FIELD_SENTIMENT)){
			JSONArray arr=jsonQuery.getJSONArray(Mapper.AdvancedQuery.FIELD_SENTIMENT);
			int[] sentimentIds=new int[arr.size()]; 
			for(int i=0; i<arr.size(); i++){
				sentimentIds[i]=arr.getInt(i);
			}
			fbSentiment=QueryBuilders.termsQuery(Mapper.FieldComment.SENTIMENT, sentimentIds);
			filters.add(fbSentiment);
		}
		
		//集成
		QueryBuilder[] fbs=new QueryBuilder[filters.size()];
		BoolQueryBuilder qb=QueryBuilders.boolQuery();
		for(int i=0; i<fbs.length; i++){
			qb.must(filters.get(i));
		}
		return qb;
	}
	
	private JSONObject getResultByType(SearchHit sh, String type){
		if(type==null || "".equalsIgnoreCase(type)) return null;
		if(type.equalsIgnoreCase(Constant.QUERY_RESULT_FRONT)) return getResult4Front(sh);
		if(type.equalsIgnoreCase(Constant.QUERY_RESULT_ANALYSIS)) return getResult4Analysis(sh);
		return null;
	}
	
	/**
	 * 返回前端页面检索所需的字段
	 * @param sh
	 * @return
	 */
	private JSONObject getResult4Front(SearchHit sh){
		Map<String, Object> map=sh.getSource();
		Map<String, HighlightField> fieldMap=sh.getHighlightFields();
		/*
		for(String key:fieldMap.keySet()){
			System.out.println(fieldMap.get(key).fragments()[0].string());
		}*/
		JSONObject obj=new JSONObject();
		
		obj.put(Mapper.FieldComment.UUID,map.get(Mapper.FieldComment.UUID));
		obj.put(Mapper.FieldComment.PRODUCT_ID,map.get(Mapper.FieldComment.PRODUCT_ID));
		obj.put(Mapper.FieldComment.PRODUCT_NAME,map.get(Mapper.FieldComment.PRODUCT_NAME));
		obj.put(Mapper.FieldComment.INDUSTRY_NAME,map.get(Mapper.FieldComment.INDUSTRY_NAME));
		obj.put(Mapper.FieldComment.COMPANY_NAME,map.get(Mapper.FieldComment.COMPANY_NAME));
		
		obj.put(Mapper.FieldComment.CREATE_DATE,map.get(Mapper.FieldComment.CREATE_DATE));
		obj.put(Mapper.FieldComment.CREATE_TIME,map.get(Mapper.FieldComment.CREATE_TIME));

		obj.put(Mapper.FieldComment.USER_ID,map.get(Mapper.FieldComment.USER_ID));
		obj.put(Mapper.FieldComment.USER_NAME,map.get(Mapper.FieldComment.USER_NAME));
		obj.put(Mapper.FieldComment.USER_LOC,map.get(Mapper.FieldComment.USER_LOC));
		obj.put(Mapper.FieldComment.USER_REGIST_TIME,map.get(Mapper.FieldComment.USER_REGIST_TIME));
		obj.put(Mapper.FieldComment.REFERENCE_NAME,map.get(Mapper.FieldComment.REFERENCE_NAME));
		obj.put(Mapper.FieldComment.WEBSITE,map.get(Mapper.FieldComment.WEBSITE));
		obj.put(Mapper.FieldComment.LANGUAGE_CODE,map.get(Mapper.FieldComment.LANGUAGE_CODE));
		obj.put(Mapper.FieldComment.OPINION_TARGET,map.get(Mapper.FieldComment.OPINION_TARGET));
		obj.put(Mapper.FieldComment.OPINION_WORD,map.get(Mapper.FieldComment.OPINION_WORD));
		obj.put(Mapper.FieldComment.ELES_SENTI,map.get(Mapper.FieldComment.ELES_SENTI));
		obj.put(Mapper.FieldComment.COUNT,map.get(Mapper.FieldComment.COUNT));
		obj.put(Mapper.FieldComment.SENTIMENT,map.get(Mapper.FieldComment.SENTIMENT));
		obj.put(Mapper.FieldComment.SCORE_BYUSER,map.get(Mapper.FieldComment.SCORE_BYUSER));
		obj.put(Mapper.FieldComment.SCORE_BYMACHINE,map.get(Mapper.FieldComment.SCORE_BYMACHINE));
		obj.put(Mapper.FieldComment.COME_FROM,map.get(Mapper.FieldComment.COME_FROM));
		obj.put(Mapper.FieldComment.USER_TAG,map.get(Mapper.FieldComment.USER_TAG));
		
		//*************************TEXT**************************
		HighlightField textHigh=fieldMap.get(Mapper.FieldComment.CONTENT);
		String text="";
		if(textHigh==null){
			obj.put(Mapper.FieldComment.CONTENT, map.get(Mapper.FieldComment.CONTENT));
		}else{
			text=textHigh.fragments()[0].string();
			obj.put(Mapper.FieldComment.CONTENT, textHigh);
		}
		//*************************TEXT**************************
		
		//*************************TEXTEN**************************
		HighlightField textEnHigh=fieldMap.get(Mapper.FieldComment.CONTENT_EN);
		String textEn="";
		if(textEnHigh==null){
			obj.put(Mapper.FieldComment.CONTENT_EN, map.get(Mapper.FieldComment.CONTENT_EN));
		}else{
			text=textEnHigh.fragments()[0].string();
			obj.put(Mapper.FieldComment.CONTENT_EN, textEn);
		}
		//*************************TEXT**************************
		
		//*************************TEXTZH**************************
		HighlightField textZhHigh=fieldMap.get(Mapper.FieldComment.CONTENT_ZH);
		String textZh="";
		if(textZhHigh==null){
			obj.put(Mapper.FieldComment.CONTENT_ZH, map.get(Mapper.FieldComment.CONTENT_ZH));
		}else{
			text=textZhHigh.fragments()[0].string();
			obj.put(Mapper.FieldComment.CONTENT_ZH, textZh);
		}
		//*************************TEXTEN**************************
		return obj;
	}
	
	private JSONObject getResult4Analysis(SearchHit sh){
		Map<String, Object> map=sh.getSource();
		Map<String, HighlightField> fieldMap=sh.getHighlightFields();
		/*
		for(String key:fieldMap.keySet()){
			System.out.println(fieldMap.get(key).fragments()[0].string());
		}*/
		JSONObject obj=new JSONObject();
		
		obj.put(Mapper.FieldComment.UUID,map.get(Mapper.FieldComment.UUID));
		obj.put(Mapper.FieldComment.PRODUCT_ID,map.get(Mapper.FieldComment.PRODUCT_ID));
		obj.put(Mapper.FieldComment.PRODUCT_NAME,map.get(Mapper.FieldComment.PRODUCT_NAME));
		obj.put(Mapper.FieldComment.INDUSTRY_NAME,map.get(Mapper.FieldComment.INDUSTRY_NAME));
		obj.put(Mapper.FieldComment.COMPANY_NAME,map.get(Mapper.FieldComment.COMPANY_NAME));
		
		obj.put(Mapper.FieldComment.CREATE_DATE,map.get(Mapper.FieldComment.CREATE_DATE));
		obj.put(Mapper.FieldComment.CREATE_TIME,map.get(Mapper.FieldComment.CREATE_TIME));
		obj.put(Mapper.FieldComment.CONTENT,map.get(Mapper.FieldComment.CONTENT));
		obj.put(Mapper.FieldComment.CONTENT_EN,map.get(Mapper.FieldComment.CONTENT_EN));
		obj.put(Mapper.FieldComment.CONTENT_ZH,map.get(Mapper.FieldComment.CONTENT_ZH));
		obj.put(Mapper.FieldComment.USER_ID,map.get(Mapper.FieldComment.USER_ID));
		obj.put(Mapper.FieldComment.USER_NAME,map.get(Mapper.FieldComment.USER_NAME));
		obj.put(Mapper.FieldComment.USER_LOC,map.get(Mapper.FieldComment.USER_LOC));
		obj.put(Mapper.FieldComment.USER_REGIST_TIME,map.get(Mapper.FieldComment.USER_REGIST_TIME));
		obj.put(Mapper.FieldComment.REFERENCE_NAME,map.get(Mapper.FieldComment.REFERENCE_NAME));
		obj.put(Mapper.FieldComment.WEBSITE,map.get(Mapper.FieldComment.WEBSITE));
		obj.put(Mapper.FieldComment.LANGUAGE_CODE,map.get(Mapper.FieldComment.LANGUAGE_CODE));
		obj.put(Mapper.FieldComment.OPINION_TARGET,map.get(Mapper.FieldComment.OPINION_TARGET));
		obj.put(Mapper.FieldComment.OPINION_WORD,map.get(Mapper.FieldComment.OPINION_WORD));
		obj.put(Mapper.FieldComment.ELES_SENTI,map.get(Mapper.FieldComment.ELES_SENTI));
		obj.put(Mapper.FieldComment.COUNT,map.get(Mapper.FieldComment.COUNT));
		obj.put(Mapper.FieldComment.SENTIMENT,map.get(Mapper.FieldComment.SENTIMENT));
		obj.put(Mapper.FieldComment.SCORE_BYUSER,map.get(Mapper.FieldComment.SCORE_BYUSER));
		obj.put(Mapper.FieldComment.SCORE_BYMACHINE,map.get(Mapper.FieldComment.SCORE_BYMACHINE));
		obj.put(Mapper.FieldComment.COME_FROM,map.get(Mapper.FieldComment.COME_FROM));
		obj.put(Mapper.FieldComment.USER_TAG,map.get(Mapper.FieldComment.USER_TAG));
		
		return obj;
		

	}
	
	
	private JSONObject getResultByTypeProduct(SearchHit sh, String type){
		if(type==null || "".equalsIgnoreCase(type)) return null;
		if(type.equalsIgnoreCase(Constant.QUERY_RESULT_FRONT)) return getResult4FrontProduct(sh);
		if(type.equalsIgnoreCase(Constant.QUERY_RESULT_ANALYSIS)) return getResult4AnalysisProduct(sh);
		return null;
	}
	
	/**
	 * 返回前端页面检索所需的字段
	 * @param sh
	 * @return
	 */
	private JSONObject getResult4FrontProduct(SearchHit sh){
		Map<String, Object> map=sh.getSource();
		Map<String, HighlightField> fieldMap=sh.getHighlightFields();

		JSONObject obj=new JSONObject();
		
		obj.put(Mapper.FieldProduct.UUID,map.get(Mapper.FieldProduct.UUID));
		obj.put(Mapper.FieldProduct.PRODUCT_NAME,map.get(Mapper.FieldProduct.PRODUCT_NAME));
		obj.put(Mapper.FieldProduct.INDUSTRY_NAME,map.get(Mapper.FieldProduct.INDUSTRY_NAME));
		obj.put(Mapper.FieldProduct.COMPANY_NAME,map.get(Mapper.FieldProduct.COMPANY_NAME));
		

		return obj;
	}
	
	private JSONObject getResult4AnalysisProduct(SearchHit sh){
		Map<String, Object> map=sh.getSource();
		JSONObject obj=new JSONObject();
	
		obj.put(Mapper.FieldProduct.UUID,map.get(Mapper.FieldProduct.UUID));
		obj.put(Mapper.FieldProduct.PRODUCT_NAME,map.get(Mapper.FieldProduct.PRODUCT_NAME));
		obj.put(Mapper.FieldProduct.INDUSTRY_NAME,map.get(Mapper.FieldProduct.INDUSTRY_NAME));
		obj.put(Mapper.FieldProduct.COMPANY_NAME,map.get(Mapper.FieldProduct.COMPANY_NAME));
		
		return obj;	
	}
		
	
	
	private SearchRequestBuilder buildQueryProduct(JSONObject obj){
		String key=obj.getString(Mapper.Query.KEYWORD);
		//cross
		BoolQueryBuilder qb=QueryBuilders.boolQuery();
			
		QueryBuilder initQb=QueryBuilders.queryStringQuery(key)
				.field(Mapper.FieldProduct.PRODUCT_NAME,5)
				;
		
		FilteredQueryBuilder fqb=QueryBuilders.filteredQuery(initQb, filterQuery(obj));
		SearchRequestBuilder srb=client.prepareSearch(Configuration.PRODUCT_INDEX_NAME);
		srb.setQuery(fqb);
		String type=obj.getString(Mapper.Query.INDEX_TYPE);
		//同时从Weibo和微博评论中查询？
		if(type!=null && Constant.QUERY_INDEX_TYPE_ALL.equals(type)){
			srb.setTypes(Configuration.PRODUCT_INDEX_TYPE_PRODUCT);
		}else{
			srb.setTypes(type);
		}
		//是否高亮
		boolean highlight=obj.getBoolean(Mapper.Query.HIGHLIGHT);
		if(highlight){
			srb.addHighlightedField(Mapper.FieldProduct.PRODUCT_NAME)
			;
		}
		return srb;
	}	
	
	public String crossSearchProduct(String jsonQuery){
		long s1=System.currentTimeMillis();
		JSONObject obj=null;
		try {
			obj = initAdvancedQuery(jsonQuery);
		} catch (QueryFormatException e) {
			logger.error(ExceptionUtil.getExceptionTrace(e));
			return null;
		}
		SearchRequestBuilder srb=buildQueryProduct(obj);
		if(srb==null) return "";
		SearchResponse sr=srb
				.addSort(getSort(obj))
				.setMinScore(Configuration.QUERY_RESULT_MIN_SCORE) //最低分值
				.setFrom((obj.getInt(Mapper.Query.PAGE_NO)-1)*obj.getInt(Mapper.Query.PAGE_SIZE))
				.setSize(obj.getInt(Mapper.Query.PAGE_SIZE))
				.execute().actionGet();
		SearchHits hits=sr.getHits();
		logger.info("[es-query]-"+srb.toString());
		//封装结果
		SearchHit sh=null;
		List<JSONObject> list=new ArrayList<JSONObject>();
		for(int i=0; i<hits.hits().length; i++){
			sh=hits.getAt(i);
			//System.out.println(sh.getScore());
			list.add(getResultByTypeProduct(sh, obj.getString(Mapper.Query.RESULT_TYPE)));
		}
		JSONArray arr=JSONArray.fromObject(list);
		JSONObject result=new JSONObject();
		result.put(Mapper.Query.RESULT_LIST, arr);
		result.put(Mapper.Query.RESULT_COUNT, hits.getTotalHits());
		long e1=System.currentTimeMillis();
		System.out.println("time:"+(e1-s1)/1000);
		return result.toString();
	}
	
	
	
	public boolean insertProduct(JSONObject jsonObject)
	{
		try
		{
			if(builder == null)
			{
				builder=new IndexBuilder();
			}
			return builder.addUnit(jsonObject.toString(), Configuration.PRODUCT_INDEX_NAME, Configuration.PRODUCT_INDEX_TYPE_PRODUCT, Mapper.FieldProduct.UUID);
			
		}
		catch(Exception e)
		{
			logger.error(ExceptionUtil.getExceptionTrace(e));
			return false;
		}
	}
	
	
	public boolean deleteProduct(JSONObject jsonObject)
	{
		try
		{
			if(builder == null)
			{
				builder=new IndexBuilder();
			}
			return builder.deleteUnit(jsonObject.toString(), Configuration.PRODUCT_INDEX_NAME, Configuration.PRODUCT_INDEX_TYPE_PRODUCT, Mapper.FieldProduct.UUID);
			
		}
		catch(Exception e)
		{
			logger.error(ExceptionUtil.getExceptionTrace(e));
			return false;
		}
	}	
	
	public boolean deleteCompany(JSONObject jsonObject)
	{
		try
		{
			if(builder == null)
			{
				builder=new IndexBuilder();
			}
			return builder.deleteUnit(jsonObject.toString(), Configuration.PRODUCT_INDEX_NAME, Configuration.PRODUCT_INDEX_TYPE_COMPANY, Mapper.FieldCompany.UUID);
			
		}
		catch(Exception e)
		{
			logger.error(ExceptionUtil.getExceptionTrace(e));
			return false;
		}
	}	
	
	
	private JSONObject getResultByTypeCompany(SearchHit sh, String type){
		if(type==null || "".equalsIgnoreCase(type)) return null;
		if(type.equalsIgnoreCase(Constant.QUERY_RESULT_FRONT)) return getResult4FrontCompany(sh);
		if(type.equalsIgnoreCase(Constant.QUERY_RESULT_ANALYSIS)) return getResult4AnalysisCompany(sh);
		return null;
	}
	
	/**
	 * 返回前端页面检索所需的字段
	 * @param sh
	 * @return
	 */
	private JSONObject getResult4FrontCompany(SearchHit sh){
		Map<String, Object> map=sh.getSource();
		Map<String, HighlightField> fieldMap=sh.getHighlightFields();

		JSONObject obj=new JSONObject();
		
		obj.put(Mapper.FieldCompany.UUID,map.get(Mapper.FieldCompany.UUID));
		obj.put(Mapper.FieldCompany.INDUSTRY_NAME,map.get(Mapper.FieldCompany.INDUSTRY_NAME));
		obj.put(Mapper.FieldCompany.COMPANY_NAME,map.get(Mapper.FieldCompany.COMPANY_NAME));
		obj.put(Mapper.FieldCompany.COMPANY_ALIAS,map.get(Mapper.FieldCompany.COMPANY_ALIAS));
		obj.put(Mapper.FieldCompany.COMPANY_INTRO,map.get(Mapper.FieldCompany.COMPANY_INTRO));
		obj.put(Mapper.FieldCompany.COMPANY_LOGO,map.get(Mapper.FieldCompany.COMPANY_LOGO));

		
		

		return obj;
	}
	
	private JSONObject getResult4AnalysisCompany(SearchHit sh){
		Map<String, Object> map=sh.getSource();
		JSONObject obj=new JSONObject();
	
		obj.put(Mapper.FieldCompany.UUID,map.get(Mapper.FieldCompany.UUID));
		obj.put(Mapper.FieldCompany.INDUSTRY_NAME,map.get(Mapper.FieldCompany.INDUSTRY_NAME));
		obj.put(Mapper.FieldCompany.COMPANY_NAME,map.get(Mapper.FieldCompany.COMPANY_NAME));
		obj.put(Mapper.FieldCompany.COMPANY_ALIAS,map.get(Mapper.FieldCompany.COMPANY_ALIAS));
		obj.put(Mapper.FieldCompany.COMPANY_INTRO,map.get(Mapper.FieldCompany.COMPANY_INTRO));
		obj.put(Mapper.FieldCompany.COMPANY_LOGO,map.get(Mapper.FieldCompany.COMPANY_LOGO));
		
		return obj;	
	}
		
	
	
	private SearchRequestBuilder buildQueryCompany(JSONObject obj){
		String key=obj.getString(Mapper.Query.KEYWORD);
		//cross
		BoolQueryBuilder qb=QueryBuilders.boolQuery();
			
		QueryBuilder initQb=QueryBuilders.queryStringQuery(key)
				.field(Mapper.FieldCompany.COMPANY_NAME,5)
				.field(Mapper.FieldCompany.COMPANY_ALIAS,5)
				;
		
		FilteredQueryBuilder fqb=QueryBuilders.filteredQuery(initQb, filterQuery(obj));
		SearchRequestBuilder srb=client.prepareSearch(Configuration.PRODUCT_INDEX_NAME);
		srb.setQuery(fqb);
		String type=obj.getString(Mapper.Query.INDEX_TYPE);
		//同时从Weibo和微博评论中查询？
		if(type!=null && Constant.QUERY_INDEX_TYPE_ALL.equals(type)){
			srb.setTypes(Configuration.PRODUCT_INDEX_TYPE_COMPANY);
		}else{
			srb.setTypes(type);
		}
		//是否高亮
		boolean highlight=obj.getBoolean(Mapper.Query.HIGHLIGHT);
		if(highlight){
			srb.addHighlightedField(Mapper.FieldCompany.COMPANY_NAME)
			;
		}
		return srb;
	}	
	
	public String crossSearchCompany(String jsonQuery){
		long s1=System.currentTimeMillis();
		JSONObject obj=null;
		try {
			obj = initAdvancedQuery(jsonQuery);
		} catch (QueryFormatException e) {
			logger.error(ExceptionUtil.getExceptionTrace(e));
			return null;
		}
		SearchRequestBuilder srb=buildQueryCompany(obj);
		if(srb==null) return "";
		SearchResponse sr=srb
				.addSort(getSort(obj))
				.setMinScore(Configuration.QUERY_RESULT_MIN_SCORE) //最低分值
				.setFrom((obj.getInt(Mapper.Query.PAGE_NO)-1)*obj.getInt(Mapper.Query.PAGE_SIZE))
				.setSize(obj.getInt(Mapper.Query.PAGE_SIZE))
				.execute().actionGet();
		SearchHits hits=sr.getHits();
		logger.info("[es-query]-"+srb.toString());
		//封装结果
		SearchHit sh=null;
		List<JSONObject> list=new ArrayList<JSONObject>();
		for(int i=0; i<hits.hits().length; i++){
			sh=hits.getAt(i);
			list.add(getResultByTypeCompany(sh, obj.getString(Mapper.Query.RESULT_TYPE)));
		}
		JSONArray arr=JSONArray.fromObject(list);
		JSONObject result=new JSONObject();
		result.put(Mapper.Query.RESULT_LIST, arr);
		result.put(Mapper.Query.RESULT_COUNT, hits.getTotalHits());
		long e1=System.currentTimeMillis();
		System.out.println("time:"+(e1-s1)/1000);
		return result.toString();
	}
	
	public boolean insertCompany(JSONObject jsonObject)
	{
		try
		{
			if(builder == null)
			{
				builder=new IndexBuilder();
			}
			return builder.addUnit(jsonObject.toString(), Configuration.PRODUCT_INDEX_NAME, Configuration.PRODUCT_INDEX_TYPE_COMPANY, Mapper.FieldCompany.UUID);
			
		}
		catch(Exception e)
		{
			logger.error(ExceptionUtil.getExceptionTrace(e));
			return false;
		}
	}	

	
	//排序
	private SortBuilder getSort(JSONObject obj){
		if(!obj.containsKey(Mapper.Sort.ORDER)) return SortBuilders.scoreSort(); 
		String orderStr=obj.getString(Mapper.Sort.ORDER);
		if(orderStr==null || "".equals(orderStr)){
			return SortBuilders.scoreSort();
		}
		SortOrder order=Constant.QUERY_SORT_ORDER_ASC.equals(orderStr)?SortOrder.ASC:SortOrder.DESC;
//		return SortBuilders.scriptSort("new java.text.SimpleDateFormat('yyyy-MM-dd HH:mm:dd').format(new Date(doc['"+Mapper.FieldArticle.PUBDATE+"'].value))", "string").order(order);
		return SortBuilders.fieldSort(obj.getString(Mapper.Sort.FIELD_NAME)).order(order);
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

}
