package edu.buaa.nlp.es.weibo;

import edu.buaa.nlp.es.client.ESClient;
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


@SuppressWarnings("deprecation")
public class SearchBuilder {

	private Client client;
	private Logger logger=Logger.getLogger(getClass());
	
	private static Map<String,Integer> hashLeaders = null;
	private static Map<String,Integer> hashSensiWords = null;
	private static Map<String,Integer> hashLeadersPingyin = null;
	private static Map<String,Integer> hashSensiWordsPingyin = null;
	
	private static PingyinTool pingyinTool = null ;

	private boolean handledSensitiveWords = false;
	
	private static Pattern patYinHao = Pattern.compile("(\"[^\"]+\")");
	private static String yinhaoTag = "YH_"; //引号标签
	
	
	static {
		pingyinTool = new PingyinTool();
	}

	public static SearchBuilder getTotal(){
		return new SearchBuilder( Configuration.CLUSTER_NAME,Configuration.TOTAL_INDEX_SERVER_ADDRESS );
	}
	public static SearchBuilder getRecent(){
		return new SearchBuilder();
	}

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
	
	public static boolean initSensitiveModels(String leadersFile,String sensiWordsFile)
	{
		try
		{
			hashLeaders = new HashMap<String,Integer>();
			hashLeadersPingyin = new HashMap<String,Integer>();
			InputStreamReader isR = new InputStreamReader(new FileInputStream(leadersFile),"utf-8");
			BufferedReader br = new BufferedReader(isR);
			
			String line = "";
			String clearLine = "";
			while ((line = br.readLine()) != null) {
				if(line.trim().isEmpty()) continue;
				
				clearLine = CharUtil.ToDBC(line);
				clearLine = CharUtil.removeUnChar(clearLine);
				if(clearLine.length()>1)
				{
					clearLine = pingyinTool.toPinYin(clearLine,"", PingyinTool.Type.LOWERCASE).toLowerCase();
					hashLeadersPingyin.put(clearLine, 1);
				}
				hashLeaders.put(line.trim().toLowerCase(), 1);
					
			}
			br.close();
			isR.close();

					
			hashSensiWords = new HashMap<String,Integer>();
			hashSensiWordsPingyin = new HashMap<String,Integer>();
			isR = new InputStreamReader(new FileInputStream(sensiWordsFile),"utf-8");
			br = new BufferedReader(isR);
		
			while ((line = br.readLine()) != null) {
				if(line.trim().isEmpty()) continue;
				
				clearLine = CharUtil.ToDBC(line);
				clearLine = CharUtil.removeUnChar(clearLine);
				
				if(clearLine.length()>1)
				{
					clearLine = pingyinTool.toPinYin(clearLine,"", PingyinTool.Type.LOWERCASE).toLowerCase();
					hashSensiWordsPingyin.put(clearLine, 1);
				}
				hashSensiWords.put(line.trim().toLowerCase(), 1);
			}
			br.close();
			isR.close();

			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean addLeader(String word)
	{
		try
		{
			if(hashLeaders == null)
			{
				hashLeaders = new HashMap<String,Integer>();
				hashLeadersPingyin = new HashMap<String,Integer>();
			}
			hashLeaders.put(word.trim().toLowerCase(), 1);
			word = CharUtil.ToDBC(word);
			word = CharUtil.removeUnChar(word);
			
			if(word.length()>1)
			{
				word = pingyinTool.toPinYin(word,"", PingyinTool.Type.LOWERCASE).toLowerCase();
				hashLeadersPingyin.put(word, 1);
			}
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean addSensiWords(String word)
	{
		try
		{
			if(hashSensiWords == null)
			{
				hashSensiWords = new HashMap<String,Integer>();
				hashSensiWordsPingyin = new HashMap<String,Integer>();
			}
			hashSensiWords.put(word.trim().toLowerCase(), 1);
			word = CharUtil.ToDBC(word);
			word = CharUtil.removeUnChar(word);
			
			if(word.length()>1)
			{
				word = pingyinTool.toPinYin(word,"", PingyinTool.Type.LOWERCASE).toLowerCase();
				hashSensiWordsPingyin.put(word, 1);
			}
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
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
		SearchRequestBuilder srb=client.prepareSearch(Configuration.SOCIALITY_INDEX_NAME);
		String type=obj.getString(Mapper.Query.INDEX_TYPE);
		if(type!=null && Constant.QUERY_INDEX_TYPE_ALL.equals(type)){
			srb.setTypes(Configuration.SOCIALITY_INDEX_TYPE_WEIBO);
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
	
	
	public QueryBuilder genQuery(JSONObject obj){
		String key=obj.getString(Mapper.Query.KEYWORD);

		if(handledSensitiveWords == false)
		{
			int sensiResult = handleSensitiveWords(key);
			if(sensiResult == 1)
			{
				return null;
			}
		}

		handledSensitiveWords = false;

		//cross
		QueryBuilder initQb=QueryBuilders.queryStringQuery(key)
				.field(Mapper.FieldWeibo.TEXT,5)
				.field(Mapper.FieldWeibo.TEXTZH,2)			//
				.field(Mapper.FieldWeibo.TEXTEN,2);

		//		qb.must(initQb);
		return QueryBuilders.filteredQuery(initQb, filterQuery(obj));
	}

	public SearchRequestBuilder buildQuery(JSONObject obj){
		SearchRequestBuilder srb=client.prepareSearch(Configuration.SOCIALITY_INDEX_NAME);
		srb.setQuery(genQuery( obj ));
		logger.info("[es-query]-"+srb);
		String type=obj.getString(Mapper.Query.INDEX_TYPE);
		//同时从Weibo和微博评论中查询？
		if(type!=null && Constant.QUERY_INDEX_TYPE_ALL.equals(type)){
			srb.setTypes(Configuration.SOCIALITY_INDEX_TYPE_WEIBO);
		}else{
			srb.setTypes(type);
		}
		//是否高亮
		boolean highlight=obj.getBoolean(Mapper.Query.HIGHLIGHT);
		if(highlight){
			srb
			.addHighlightedField(Mapper.FieldWeibo.TEXT)
			.addHighlightedField(Mapper.FieldWeibo.TEXTLEN)
			.addHighlightedField(Mapper.FieldWeibo.TEXTZH)
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
			obj=JSONObject.fromObject(jsonQuery);
			String keyword = obj.getString(Mapper.Query.KEYWORD);
			if(this.handleSensitiveWords(keyword)==1)
			{
				return null;
			}
			handledSensitiveWords = true;
			
			obj = initAdvancedQuery(jsonQuery);
		} catch (QueryFormatException e) {
			logger.error(ExceptionUtil.getExceptionTrace(e));
			return null;
		}
		SearchRequestBuilder srb=buildQuery(obj);
		if(srb==null) return "";
		if (!ValidateQuery.check(Configuration.SOCIALITY_INDEX_NAME, Configuration.SOCIALITY_INDEX_TYPE_WEIBO, srb.toString())) {
			return "";
		}
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
		List<JSONObject> list=new ArrayList<>();
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
		srb .addSort(getSort(obj))
				//				.setMinScore(Mapper.QUERY_RESULT_MIN_SCORE) //最低分值
			.setSearchType(SearchType.SCAN)
			.setSize(goal)
			.setScroll(new TimeValue(6000));
		if (!ValidateQuery.check(Configuration.SOCIALITY_INDEX_NAME, Configuration.SOCIALITY_INDEX_TYPE_WEIBO, srb.toString())) {
			return "";
		}
		SearchResponse sr1=srb
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
				.prepareHealth(Configuration.INDEX_NAME)
				.setWaitForGreenStatus()
				.get();
		ClusterHealthStatus status=chr.getStatus();
		if(status.equals(ClusterHealthStatus.GREEN)){
			return chr.getActiveShards();
		}
		return 0;
	}
	
	private int handleSensitiveWords(String word)
	{
		try
		{
			String clearword = CharUtil.ToDBC(word); 
			clearword = CharUtil.removeUnChar(clearword).toLowerCase();
			
			
			if(clearword.length() > 1)
			{
			
				String wordPingyin = pingyinTool.toPinYin(clearword, "", PingyinTool.Type.LOWERCASE);
				
				
				if(hashLeadersPingyin != null)
				{
					for(String key : hashLeadersPingyin.keySet())
					{
						if(wordPingyin.contains(key))
						{
							return 1;
						}
					}
				}
				
				if(hashSensiWordsPingyin != null)
				{
					for(String key : hashSensiWordsPingyin.keySet())
					{
						if(wordPingyin.contains(key))
						{
							return 1;
						}
					}
				}
			}
			
			if(hashLeaders != null)
			{
				for(String key : hashLeaders.keySet())
				{
					if(word.contains(key))
					{
						return 1;
					}
				}
			}
			
			if(hashSensiWords != null)
			{
				for(String key : hashSensiWords.keySet())
				{
					if(word.contains(key))
					{
						return 1;
					}
				}
			}
			return 0;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return 1;
		}
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
		
	
	private JSONObject initAdvancedQuery(String jsonQuery) throws QueryFormatException{
		//保证基本信息完整
		JSONObject obj=initQuery(jsonQuery);
		String keyword=obj.getString(Mapper.Query.KEYWORD);
		keyword = this.initKeyword(keyword);		
		obj.put(Mapper.Query.KEYWORD, keyword.trim());
		return obj;
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
				fbDate=QueryBuilders.rangeQuery(Mapper.FieldWeibo.TIME)
						.gte(DateUtil.time2Unix("1970-01-01 00:00:00"))
						.lte(DateUtil.time2Unix(jsonQuery.getString(Mapper.AdvancedQuery.FIELD_END_DATE)));
				filters.add(fbDate);
			}
		}else {
			if(Constant.isNullKey(jsonQuery, Mapper.AdvancedQuery.FIELD_END_DATE)){
				fbDate=QueryBuilders.rangeQuery(Mapper.FieldWeibo.TIME)
						.gte(DateUtil.time2Unix(jsonQuery.getString(Mapper.AdvancedQuery.FIELD_BEGIN_DATE)))
						.lte(DateUtil.time2Unix(DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss")));
			}else{
				fbDate=QueryBuilders.rangeQuery(Mapper.FieldWeibo.TIME)
						.gte(DateUtil.time2Unix(jsonQuery.getString(Mapper.AdvancedQuery.FIELD_BEGIN_DATE)))
						.lte(DateUtil.time2Unix(jsonQuery.getString(Mapper.AdvancedQuery.FIELD_END_DATE)));
			}
			filters.add(fbDate);
		}
		
		//weiboUUID
		QueryBuilder fbWeiboUUID=null;
		if(!Constant.isNullKey(jsonQuery, Mapper.AdvancedQuery.FIELD_ID)){
			JSONArray arr=jsonQuery.getJSONArray(Mapper.AdvancedQuery.FIELD_ID);
			String[] weiboIds=new String[arr.size()]; 
			for(int i=0; i<arr.size(); i++){
				weiboIds[i]=arr.getString(i);
			}
			fbWeiboUUID=QueryBuilders.termsQuery(Mapper.FieldWeibo.UUID, weiboIds);
			filters.add(fbWeiboUUID);
		}	
		
//		//国家
		QueryBuilder fbCountryName=null;
		if(!Constant.isNullKey(jsonQuery, Mapper.AdvancedQuery.FIELD_COUNTRY)){
			JSONArray arr=jsonQuery.getJSONArray(Mapper.AdvancedQuery.FIELD_COUNTRY);
			String[] regionIds=new String[arr.size()]; 
			for(int i=0; i<arr.size(); i++){
				regionIds[i]=arr.getString(i);
			}
			fbCountryName=QueryBuilders.termsQuery(Mapper.FieldWeibo.COUNTRY, regionIds);
			filters.add(fbCountryName);
		}	
		
//		//sourceType
		QueryBuilder fbSourceType=null;
		if(!Constant.isNullKey(jsonQuery, Mapper.AdvancedQuery.FIELD_SOURCETYPE)){
			JSONArray arr=jsonQuery.getJSONArray(Mapper.AdvancedQuery.FIELD_SOURCETYPE);
			String[] regionIds=new String[arr.size()]; 
			for(int i=0; i<arr.size(); i++){
				regionIds[i]=arr.getString(i);
			}
			fbSourceType=QueryBuilders.termsQuery(Mapper.FieldWeibo.SOURCETYPE, regionIds);
			filters.add(fbSourceType);
		}	
		
		
//		//地区
		QueryBuilder fbProvinceName=null;
		if(!Constant.isNullKey(jsonQuery, Mapper.AdvancedQuery.FIELD_PROVINCE)){
			JSONArray arr=jsonQuery.getJSONArray(Mapper.AdvancedQuery.FIELD_PROVINCE);
			String[] regionIds=new String[arr.size()]; 
			for(int i=0; i<arr.size(); i++){
				regionIds[i]=arr.getString(i);
			}
			fbProvinceName=QueryBuilders.termsQuery(Mapper.FieldWeibo.PROVINCE, regionIds);
			filters.add(fbProvinceName);
		}	
		
		QueryBuilder fbProvinceId=null;
		if(!Constant.isNullKey(jsonQuery, Mapper.AdvancedQuery.FIELD_PROVINCE_ID)){
			JSONArray arr=jsonQuery.getJSONArray(Mapper.AdvancedQuery.FIELD_PROVINCE_ID);
			int[] regionIds=new int[arr.size()]; 
			for(int i=0; i<arr.size(); i++){
				regionIds[i]=arr.getInt(i);
			}
			fbProvinceId=QueryBuilders.termsQuery(Mapper.FieldWeibo.PROVINCEID, regionIds);
			filters.add(fbProvinceId);
		}	
		
		//语言
		QueryBuilder fbLang=null;
		if(!Constant.isNullKey(jsonQuery, Mapper.AdvancedQuery.FIELD_LANGUAGE)){
			JSONArray arr=jsonQuery.getJSONArray(Mapper.AdvancedQuery.FIELD_LANGUAGE);
			String[] langIds=new String[arr.size()]; 
			for(int i=0; i<arr.size(); i++){
				langIds[i]=arr.getString(i);
			}
			fbLang=QueryBuilders.termsQuery(Mapper.FieldWeibo.LANGUAGECODE, langIds);
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
			fbSentiment=QueryBuilders.termsQuery(Mapper.FieldWeibo.SENTIMENTORIENT, sentimentIds);
			filters.add(fbSentiment);
		}
		
		//是否原创
		QueryBuilder fbIsOri=null;
		if(!Constant.isNullKey(jsonQuery, Mapper.AdvancedQuery.FIELD_ISORI)){
			JSONArray arr=jsonQuery.getJSONArray(Mapper.AdvancedQuery.FIELD_ISORI);
			int[] oriIds=new int[arr.size()]; 
			for(int i=0; i<arr.size(); i++){
				oriIds[i]=arr.getInt(i);
			}
			fbIsOri=QueryBuilders.termsQuery(Mapper.FieldWeibo.ISORI, oriIds);
			filters.add(fbIsOri);
		}
		
		
		//weiboSourceId
		QueryBuilder fbWeiboSourceId=null;
		if(!Constant.isNullKey(jsonQuery, Mapper.AdvancedQuery.FIELD_SOURCEWEIBOID)){
			JSONArray arr=jsonQuery.getJSONArray(Mapper.AdvancedQuery.FIELD_SOURCEWEIBOID);
			String[] sourceIds=new String[arr.size()]; 
			for(int i=0; i<arr.size(); i++){
				sourceIds[i]=arr.getString(i);
			}
			fbWeiboSourceId=QueryBuilders.termsQuery(Mapper.FieldWeibo.SOURCEID, sourceIds);
			filters.add(fbWeiboSourceId);
		}
		
		//name
		QueryBuilder fbName=null;
		if(!Constant.isNullKey(jsonQuery, Mapper.AdvancedQuery.FIELD_NAME)){
			JSONArray arr=jsonQuery.getJSONArray(Mapper.AdvancedQuery.FIELD_NAME);
			String[] names=new String[arr.size()]; 
			for(int i=0; i<arr.size(); i++){
				names[i]=arr.getString(i);
			}
			fbName=QueryBuilders.termsQuery(Mapper.FieldWeibo.NAME, names);
			filters.add(fbName);
		}
		

		//集成
		QueryBuilder[] fbs=new QueryBuilder[filters.size()];
		BoolQueryBuilder qb=QueryBuilders.boolQuery();
		for(int i=0; i<fbs.length; i++){
			qb.must(filters.get(i));
		}
		return qb;
	}
	
	//分组统计
	private AbstractAggregationBuilder termAggregation(String field){
		return AggregationBuilders.terms(field).field(field).size(0);
	}
	
	
	private JSONObject getResultByType(SearchHit sh, String type){
		if(type==null || "".equalsIgnoreCase(type)) return null;
		if(type.equalsIgnoreCase(Constant.QUERY_RESULT_FRONT)) return getResult4Front(sh);
		if(type.equalsIgnoreCase(Constant.QUERY_RESULT_ANALYSIS)) return getResult4Analysis(sh);
		if(type.equalsIgnoreCase(Constant.QUERY_RESULT_DETAIL)) return getResult4Detail(sh);
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
		
		obj.put(Mapper.FieldWeibo.UUID, map.get(Mapper.FieldWeibo.UUID));
		obj.put(Mapper.FieldWeibo.ID, map.get(Mapper.FieldWeibo.ID));
		obj.put(Mapper.FieldWeibo.TIME, map.get(Mapper.FieldWeibo.TIME));
		obj.put(Mapper.FieldWeibo.TIMESTR, map.get(Mapper.FieldWeibo.TIMESTR));

		obj.put(Mapper.FieldWeibo.TEXTLEN, map.get(Mapper.FieldWeibo.TEXTLEN));
		obj.put(Mapper.FieldWeibo.ISORI, map.get(Mapper.FieldWeibo.ISORI));
		obj.put(Mapper.FieldWeibo.SOURCEID, map.get(Mapper.FieldWeibo.SOURCEID));
		obj.put(Mapper.FieldWeibo.USERID, map.get(Mapper.FieldWeibo.USERID));
		obj.put(Mapper.FieldWeibo.NAME, map.get(Mapper.FieldWeibo.NAME));
		obj.put(Mapper.FieldWeibo.GENDER, map.get(Mapper.FieldWeibo.GENDER));
		obj.put(Mapper.FieldWeibo.PROVINCEID, map.get(Mapper.FieldWeibo.PROVINCEID));
		obj.put(Mapper.FieldWeibo.PROVINCE, map.get(Mapper.FieldWeibo.PROVINCE));
		obj.put(Mapper.FieldWeibo.CITYID, map.get(Mapper.FieldWeibo.CITYID));
		obj.put(Mapper.FieldWeibo.CITY, map.get(Mapper.FieldWeibo.CITY));
		obj.put(Mapper.FieldWeibo.VERIFIED, map.get(Mapper.FieldWeibo.VERIFIED));
		obj.put(Mapper.FieldWeibo.VERIFIEDREASON, map.get(Mapper.FieldWeibo.VERIFIEDREASON));
		obj.put(Mapper.FieldWeibo.USERTYPE, map.get(Mapper.FieldWeibo.USERTYPE));
		obj.put(Mapper.FieldWeibo.FLWCNT, map.get(Mapper.FieldWeibo.FLWCNT));
		obj.put(Mapper.FieldWeibo.FRDCNT, map.get(Mapper.FieldWeibo.FRDCNT));
		obj.put(Mapper.FieldWeibo.STACNT, map.get(Mapper.FieldWeibo.STACNT));
		obj.put(Mapper.FieldWeibo.USERAVATAR, map.get(Mapper.FieldWeibo.USERAVATAR));
		obj.put(Mapper.FieldWeibo.RPSCNT, map.get(Mapper.FieldWeibo.RPSCNT));
		obj.put(Mapper.FieldWeibo.CMTCNT, map.get(Mapper.FieldWeibo.CMTCNT));
		obj.put(Mapper.FieldWeibo.ATDCNT, map.get(Mapper.FieldWeibo.ATDCNT));
		obj.put(Mapper.FieldWeibo.COMMENTSINCE, map.get(Mapper.FieldWeibo.COMMENTSINCE));
		obj.put(Mapper.FieldWeibo.REPOSTSINCE, map.get(Mapper.FieldWeibo.REPOSTSINCE));
		obj.put(Mapper.FieldWeibo.UPDATETIME, map.get(Mapper.FieldWeibo.UPDATETIME));
		obj.put(Mapper.FieldWeibo.UPDATETIMESTR, map.get(Mapper.FieldWeibo.UPDATETIMESTR));

		
		
		obj.put(Mapper.FieldWeibo.SENTIMENT, map.get(Mapper.FieldWeibo.SENTIMENT));
		obj.put(Mapper.FieldWeibo.SENTIMENTORIENT, map.get(Mapper.FieldWeibo.SENTIMENTORIENT));
		obj.put(Mapper.FieldWeibo.PRODUCTS, map.get(Mapper.FieldWeibo.PRODUCTS));
		obj.put(Mapper.FieldWeibo.COMPANIES, map.get(Mapper.FieldWeibo.COMPANIES));
		obj.put(Mapper.FieldWeibo.LANGUAGECODE, map.get(Mapper.FieldWeibo.LANGUAGECODE));

		obj.put(Mapper.FieldWeibo.SOURCETYPE, map.get(Mapper.FieldWeibo.SOURCETYPE));
		obj.put(Mapper.FieldWeibo.COUNTRY, map.get(Mapper.FieldWeibo.COUNTRY));
		obj.put(Mapper.FieldWeibo.USERTAG, map.get(Mapper.FieldWeibo.USERTAG));

		obj.put(Mapper.FieldWeibo.URL, map.get(Mapper.FieldWeibo.URL));

		//*************************TEXT**************************
		HighlightField textHigh=fieldMap.get(Mapper.FieldWeibo.TEXT);
		String text="";
		if(textHigh==null){
			obj.put(Mapper.FieldWeibo.TEXT, map.get(Mapper.FieldWeibo.TEXT));
		}else{
			text=textHigh.fragments()[0].string().replaceAll("<\\\\/em>(.)<em>", "$1");
			obj.put(Mapper.FieldWeibo.TEXT, text);
		}
		//*************************TEXT**************************
		
		//*************************TEXTEN**************************
		HighlightField textEnHigh=fieldMap.get(Mapper.FieldWeibo.TEXTEN);
		String textEn="";
		if(textEnHigh==null){
			obj.put(Mapper.FieldWeibo.TEXTEN, map.get(Mapper.FieldWeibo.TEXTEN));
		}else{
			textEn=textEnHigh.fragments()[0].string();
			obj.put(Mapper.FieldWeibo.TEXTEN, textEn);
		}
		//*************************TEXT**************************
		
		//*************************TEXTZH**************************
		HighlightField textZhHigh=fieldMap.get(Mapper.FieldWeibo.TEXTZH);
		String textZh="";
		if(textZhHigh==null){
			obj.put(Mapper.FieldWeibo.TEXTZH, map.get(Mapper.FieldWeibo.TEXTZH));
		}else{
			textZh=textZhHigh.fragments()[0].string().replaceAll("<\\\\/em>(.)<em>", "$1");
			obj.put(Mapper.FieldWeibo.TEXTZH, textZh);
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
		
		obj.put(Mapper.FieldWeibo.UUID, map.get(Mapper.FieldWeibo.UUID));
		obj.put(Mapper.FieldWeibo.ID, map.get(Mapper.FieldWeibo.ID));
		obj.put(Mapper.FieldWeibo.TIME, map.get(Mapper.FieldWeibo.TIME));
		obj.put(Mapper.FieldWeibo.TIMESTR, map.get(Mapper.FieldWeibo.TIMESTR));

		obj.put(Mapper.FieldWeibo.TEXTLEN, map.get(Mapper.FieldWeibo.TEXTLEN));
		obj.put(Mapper.FieldWeibo.ISORI, map.get(Mapper.FieldWeibo.ISORI));
		obj.put(Mapper.FieldWeibo.SOURCEID, map.get(Mapper.FieldWeibo.SOURCEID));
		obj.put(Mapper.FieldWeibo.USERID, map.get(Mapper.FieldWeibo.USERID));
		obj.put(Mapper.FieldWeibo.NAME, map.get(Mapper.FieldWeibo.NAME));
		obj.put(Mapper.FieldWeibo.GENDER, map.get(Mapper.FieldWeibo.GENDER));
		obj.put(Mapper.FieldWeibo.PROVINCEID, map.get(Mapper.FieldWeibo.PROVINCEID));
		obj.put(Mapper.FieldWeibo.PROVINCE, map.get(Mapper.FieldWeibo.PROVINCE));
		obj.put(Mapper.FieldWeibo.CITYID, map.get(Mapper.FieldWeibo.CITYID));
		obj.put(Mapper.FieldWeibo.CITY, map.get(Mapper.FieldWeibo.CITY));
		obj.put(Mapper.FieldWeibo.VERIFIED, map.get(Mapper.FieldWeibo.VERIFIED));
		obj.put(Mapper.FieldWeibo.VERIFIEDREASON, map.get(Mapper.FieldWeibo.VERIFIEDREASON));
		obj.put(Mapper.FieldWeibo.USERTYPE, map.get(Mapper.FieldWeibo.USERTYPE));
		obj.put(Mapper.FieldWeibo.FLWCNT, map.get(Mapper.FieldWeibo.FLWCNT));
		obj.put(Mapper.FieldWeibo.FRDCNT, map.get(Mapper.FieldWeibo.FRDCNT));
		obj.put(Mapper.FieldWeibo.STACNT, map.get(Mapper.FieldWeibo.STACNT));
		obj.put(Mapper.FieldWeibo.USERAVATAR, map.get(Mapper.FieldWeibo.USERAVATAR));
		obj.put(Mapper.FieldWeibo.RPSCNT, map.get(Mapper.FieldWeibo.RPSCNT));
		obj.put(Mapper.FieldWeibo.CMTCNT, map.get(Mapper.FieldWeibo.CMTCNT));
		obj.put(Mapper.FieldWeibo.ATDCNT, map.get(Mapper.FieldWeibo.ATDCNT));
		obj.put(Mapper.FieldWeibo.COMMENTSINCE, map.get(Mapper.FieldWeibo.COMMENTSINCE));
		obj.put(Mapper.FieldWeibo.REPOSTSINCE, map.get(Mapper.FieldWeibo.REPOSTSINCE));
		obj.put(Mapper.FieldWeibo.UPDATETIME, map.get(Mapper.FieldWeibo.UPDATETIME));
		obj.put(Mapper.FieldWeibo.UPDATETIMESTR, map.get(Mapper.FieldWeibo.UPDATETIMESTR));
	
		obj.put(Mapper.FieldWeibo.SENTIMENT, map.get(Mapper.FieldWeibo.SENTIMENT));
		obj.put(Mapper.FieldWeibo.SENTIMENTORIENT, map.get(Mapper.FieldWeibo.SENTIMENTORIENT));
		obj.put(Mapper.FieldWeibo.PRODUCTS, map.get(Mapper.FieldWeibo.PRODUCTS));
		obj.put(Mapper.FieldWeibo.COMPANIES, map.get(Mapper.FieldWeibo.COMPANIES));
		obj.put(Mapper.FieldWeibo.LANGUAGECODE, map.get(Mapper.FieldWeibo.LANGUAGECODE));

		obj.put(Mapper.FieldWeibo.TEXT, map.get(Mapper.FieldWeibo.TEXT));
		obj.put(Mapper.FieldWeibo.TEXTEN, map.get(Mapper.FieldWeibo.TEXTEN));
		obj.put(Mapper.FieldWeibo.TEXTZH, map.get(Mapper.FieldWeibo.TEXTZH));
		
		obj.put(Mapper.FieldWeibo.SOURCETYPE, map.get(Mapper.FieldWeibo.SOURCETYPE));
		obj.put(Mapper.FieldWeibo.COUNTRY, map.get(Mapper.FieldWeibo.COUNTRY));
		obj.put(Mapper.FieldWeibo.USERTAG, map.get(Mapper.FieldWeibo.USERTAG));
		
		obj.put(Mapper.FieldWeibo.URL, map.get(Mapper.FieldWeibo.URL));
		
		return obj;
		

	}
	
	/***
	 * 详情，需要获取Comment
	 * @param sh
	 * @return
	 */
	private JSONObject getResult4Detail(SearchHit sh){
		Map<String, Object> map=sh.getSource();

		JSONObject obj=new JSONObject();
		
		obj.put(Mapper.FieldWeibo.UUID, map.get(Mapper.FieldWeibo.UUID));
		obj.put(Mapper.FieldWeibo.ID, map.get(Mapper.FieldWeibo.ID));
		obj.put(Mapper.FieldWeibo.TIME, map.get(Mapper.FieldWeibo.TIME));
		obj.put(Mapper.FieldWeibo.TIMESTR, map.get(Mapper.FieldWeibo.TIMESTR));

		obj.put(Mapper.FieldWeibo.TEXTLEN, map.get(Mapper.FieldWeibo.TEXTLEN));
		obj.put(Mapper.FieldWeibo.ISORI, map.get(Mapper.FieldWeibo.ISORI));
		obj.put(Mapper.FieldWeibo.SOURCEID, map.get(Mapper.FieldWeibo.SOURCEID));
		obj.put(Mapper.FieldWeibo.USERID, map.get(Mapper.FieldWeibo.USERID));
		obj.put(Mapper.FieldWeibo.NAME, map.get(Mapper.FieldWeibo.NAME));
		obj.put(Mapper.FieldWeibo.GENDER, map.get(Mapper.FieldWeibo.GENDER));
		obj.put(Mapper.FieldWeibo.PROVINCEID, map.get(Mapper.FieldWeibo.PROVINCEID));
		obj.put(Mapper.FieldWeibo.PROVINCE, map.get(Mapper.FieldWeibo.PROVINCE));
		obj.put(Mapper.FieldWeibo.CITYID, map.get(Mapper.FieldWeibo.CITYID));
		obj.put(Mapper.FieldWeibo.CITY, map.get(Mapper.FieldWeibo.CITY));
		obj.put(Mapper.FieldWeibo.VERIFIED, map.get(Mapper.FieldWeibo.VERIFIED));
		obj.put(Mapper.FieldWeibo.VERIFIEDREASON, map.get(Mapper.FieldWeibo.VERIFIEDREASON));
		obj.put(Mapper.FieldWeibo.USERTYPE, map.get(Mapper.FieldWeibo.USERTYPE));
		obj.put(Mapper.FieldWeibo.FLWCNT, map.get(Mapper.FieldWeibo.FLWCNT));
		obj.put(Mapper.FieldWeibo.FRDCNT, map.get(Mapper.FieldWeibo.FRDCNT));
		obj.put(Mapper.FieldWeibo.STACNT, map.get(Mapper.FieldWeibo.STACNT));
		obj.put(Mapper.FieldWeibo.USERAVATAR, map.get(Mapper.FieldWeibo.USERAVATAR));
		obj.put(Mapper.FieldWeibo.RPSCNT, map.get(Mapper.FieldWeibo.RPSCNT));
		obj.put(Mapper.FieldWeibo.CMTCNT, map.get(Mapper.FieldWeibo.CMTCNT));
		obj.put(Mapper.FieldWeibo.ATDCNT, map.get(Mapper.FieldWeibo.ATDCNT));
		obj.put(Mapper.FieldWeibo.COMMENTSINCE, map.get(Mapper.FieldWeibo.COMMENTSINCE));
		obj.put(Mapper.FieldWeibo.REPOSTSINCE, map.get(Mapper.FieldWeibo.REPOSTSINCE));
		obj.put(Mapper.FieldWeibo.UPDATETIME, map.get(Mapper.FieldWeibo.UPDATETIME));
		obj.put(Mapper.FieldWeibo.UPDATETIMESTR, map.get(Mapper.FieldWeibo.UPDATETIMESTR));
	
		obj.put(Mapper.FieldWeibo.SENTIMENT, map.get(Mapper.FieldWeibo.SENTIMENT));
		obj.put(Mapper.FieldWeibo.SENTIMENTORIENT, map.get(Mapper.FieldWeibo.SENTIMENTORIENT));
		obj.put(Mapper.FieldWeibo.PRODUCTS, map.get(Mapper.FieldWeibo.PRODUCTS));
		obj.put(Mapper.FieldWeibo.COMPANIES, map.get(Mapper.FieldWeibo.COMPANIES));
		obj.put(Mapper.FieldWeibo.LANGUAGECODE, map.get(Mapper.FieldWeibo.LANGUAGECODE));

		obj.put(Mapper.FieldWeibo.TEXT, map.get(Mapper.FieldWeibo.TEXT));
		obj.put(Mapper.FieldWeibo.TEXTEN, map.get(Mapper.FieldWeibo.TEXTEN));
		obj.put(Mapper.FieldWeibo.TEXTZH, map.get(Mapper.FieldWeibo.TEXTZH));
		
		obj.put(Mapper.FieldWeibo.SOURCETYPE, map.get(Mapper.FieldWeibo.SOURCETYPE));
		obj.put(Mapper.FieldWeibo.COUNTRY, map.get(Mapper.FieldWeibo.COUNTRY));
		obj.put(Mapper.FieldWeibo.USERTAG, map.get(Mapper.FieldWeibo.USERTAG));
		
		obj.put(Mapper.FieldWeibo.URL, map.get(Mapper.FieldWeibo.URL));
		
		
		return obj;
	}
	
	/***
	 * 详情，需要获取Comment
	 * @param sh
	 * @return
	 */
	private JSONObject getCommentResult(SearchHit sh){
		Map<String, Object> map=sh.getSource();

		JSONObject obj=new JSONObject();
		
		obj.put(Mapper.FieldWeiboComment.UUID, map.get(Mapper.FieldWeiboComment.UUID));
		obj.put(Mapper.FieldWeiboComment.WEIBOUUID, map.get(Mapper.FieldWeiboComment.WEIBOUUID));
		obj.put(Mapper.FieldWeiboComment.ID, map.get(Mapper.FieldWeiboComment.ID));
		obj.put(Mapper.FieldWeiboComment.WEIBOID, map.get(Mapper.FieldWeiboComment.WEIBOID));
		obj.put(Mapper.FieldWeiboComment.WEIBOTIME, map.get(Mapper.FieldWeiboComment.WEIBOTIME));		
		obj.put(Mapper.FieldWeiboComment.TIME, map.get(Mapper.FieldWeiboComment.TIME));
		obj.put(Mapper.FieldWeiboComment.TIMESTR, map.get(Mapper.FieldWeiboComment.TIMESTR));

		obj.put(Mapper.FieldWeiboComment.ISORI, map.get(Mapper.FieldWeiboComment.ISORI));
		obj.put(Mapper.FieldWeiboComment.SOURCEID, map.get(Mapper.FieldWeiboComment.SOURCEID));
		obj.put(Mapper.FieldWeiboComment.USERID, map.get(Mapper.FieldWeiboComment.USERID));
		obj.put(Mapper.FieldWeiboComment.NAME, map.get(Mapper.FieldWeiboComment.NAME));
		obj.put(Mapper.FieldWeiboComment.GENDER, map.get(Mapper.FieldWeiboComment.GENDER));
		obj.put(Mapper.FieldWeiboComment.PROVINCEID, map.get(Mapper.FieldWeiboComment.PROVINCEID));
		obj.put(Mapper.FieldWeiboComment.PROVINCE, map.get(Mapper.FieldWeiboComment.PROVINCE));
		obj.put(Mapper.FieldWeiboComment.CITYID, map.get(Mapper.FieldWeiboComment.CITYID));
		obj.put(Mapper.FieldWeiboComment.CITY, map.get(Mapper.FieldWeiboComment.CITY));
		obj.put(Mapper.FieldWeiboComment.VERIFIED, map.get(Mapper.FieldWeiboComment.VERIFIED));
		obj.put(Mapper.FieldWeiboComment.VERIFIEDREASON, map.get(Mapper.FieldWeiboComment.VERIFIEDREASON));
		obj.put(Mapper.FieldWeiboComment.USERTYPE, map.get(Mapper.FieldWeiboComment.USERTYPE));
		obj.put(Mapper.FieldWeiboComment.FLWCNT, map.get(Mapper.FieldWeiboComment.FLWCNT));
		obj.put(Mapper.FieldWeiboComment.FRDCNT, map.get(Mapper.FieldWeiboComment.FRDCNT));
		obj.put(Mapper.FieldWeiboComment.STACNT, map.get(Mapper.FieldWeiboComment.STACNT));
		obj.put(Mapper.FieldWeiboComment.USERAVATAR, map.get(Mapper.FieldWeiboComment.USERAVATAR));
	
		obj.put(Mapper.FieldWeiboComment.SENTIMENT, map.get(Mapper.FieldWeiboComment.SENTIMENT));
		obj.put(Mapper.FieldWeiboComment.SENTIMENTORIENT, map.get(Mapper.FieldWeiboComment.SENTIMENTORIENT));
		obj.put(Mapper.FieldWeiboComment.PRODUCTS, map.get(Mapper.FieldWeiboComment.PRODUCTS));
		obj.put(Mapper.FieldWeiboComment.COMPANIES, map.get(Mapper.FieldWeiboComment.COMPANIES));
		obj.put(Mapper.FieldWeiboComment.LANGUAGECODE, map.get(Mapper.FieldWeiboComment.LANGUAGECODE));

		obj.put(Mapper.FieldWeiboComment.TEXT, map.get(Mapper.FieldWeiboComment.TEXT));
		obj.put(Mapper.FieldWeiboComment.TEXTEN, map.get(Mapper.FieldWeiboComment.TEXTEN));
		obj.put(Mapper.FieldWeiboComment.TEXTZH, map.get(Mapper.FieldWeiboComment.TEXTZH));
		
		obj.put(Mapper.FieldWeiboComment.SOURCETYPE, map.get(Mapper.FieldWeiboComment.SOURCETYPE));
		obj.put(Mapper.FieldWeiboComment.USERTAG, map.get(Mapper.FieldWeiboComment.USERTAG));
		return obj;
	}
	
	
	
	
	public String filterWeiboComment(String jsonFilter)
	{
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
		
		QueryBuilder fbWeiboUUID=null;
		if(!Constant.isNullKey(obj, Mapper.AdvancedQuery.FIELD_WEIBO_UUID)){
			fbWeiboUUID=QueryBuilders.termQuery(Mapper.FieldWeiboComment.WEIBOUUID, obj.getString(Mapper.AdvancedQuery.FIELD_WEIBO_UUID));
		}	
		//
		SearchRequestBuilder srb=client.prepareSearch(Configuration.SOCIALITY_INDEX_NAME);
		String type=obj.getString(Mapper.Query.INDEX_TYPE);
		if(type!=null && Constant.QUERY_INDEX_TYPE_ALL.equals(type)){
			srb.setTypes(Configuration.SOCIALITY_INDEX_TYPE_WEIBO_COMMENT);
		}else{
			srb.setTypes(type);
		}
		FilteredQueryBuilder fqb=QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(), fbWeiboUUID);
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
			list.add(getCommentResult(sh));
		}
		JSONArray arr=JSONArray.fromObject(list);
		JSONObject result=new JSONObject();
		result.put(Mapper.Query.RESULT_LIST, arr);
		result.put(Mapper.Query.RESULT_COUNT, hits.getTotalHits());
//		result.put(Mapper.Query.RESULT_GROUP, parseAggregationBucket((Terms) sr.getAggregations().get(Mapper.FieldArticle.SECONDE_LEVEL)));
		return result.toString();
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
		if(client!=null)
			this.client.close();
	}
}
