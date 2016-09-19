//package edu.buaa.nlp.test;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.log4j.Logger;
//import org.elasticsearch.action.search.SearchRequestBuilder;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.search.aggregations.Aggregation;
//import org.elasticsearch.search.aggregations.AggregationBuilders;
//import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramBuilder;
//import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
//import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
//import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
//import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
//import org.joda.time.DateTime;
//import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
//
//import net.sf.json.JSONObject;
//import edu.buaa.nlp.es.util.Constant;
//import edu.buaa.nlp.util.Constants;
//import edu.buaa.nlp.entity.analyse.NewsTrend;
//import edu.buaa.nlp.entity.analyse.TGeisAnalyseNCountryDis;
//import edu.buaa.nlp.es.constant.Configuration;
//import edu.buaa.nlp.es.exception.QueryFormatException;
//import edu.buaa.nlp.es.news.Mapper;
//import edu.buaa.nlp.es.news.SearchBuilder;
//
//public class NewsSearch {
//	
//	private static Logger logger;
//	static {
//		logger = Logger.getLogger(NewsSearch.class);
//	}
//	
//	public List<TGeisAnalyseNCountryDis> searchNews(Search search, String beginDate, String endDate) {
//		List<TGeisAnalyseNCountryDis> listNCD = new ArrayList<TGeisAnalyseNCountryDis>();
//		JSONObject jsonQuery=new JSONObject();
//		jsonQuery.put(Mapper.Query.KEYWORD, search.getKeyword());
//		jsonQuery.put(Mapper.Query.RESULT_TYPE, Constant.QUERY_RESULT_FRONT);
//		jsonQuery.put(Mapper.Query.PAGE_NO, 1);
//		jsonQuery.put(Mapper.Query.PAGE_SIZE, 1);
//		jsonQuery.put(Mapper.Query.HIGHLIGHT, false);
//		jsonQuery.put(Mapper.Sort.FIELD_NAME, Mapper.FieldArticle.PUBDATE_SORT);
////		jsonQuery.put(Mapper.Sort.FIELD_NAME, Mapper.Sort.RELEVANCE);
//		jsonQuery.put(Mapper.Sort.ORDER, Constant.QUERY_SORT_ORDER_DESC);
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_BEGIN_DATE, beginDate);
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_END_DATE, endDate);
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_COUNTRY_NAME_ZH, search.getCountryNameZh());
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_LANGUAGE, search.getLanguageCode());
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_MEDIA_LEVEL, search.getMediaLevel());
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_SENTIMENT, search.getSentimentId());
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_CATEGORY, search.getCateGoryId());
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_MEDIA_TYPE, search.getMediaType());
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_MEDIA_NAME_ZH, search.getMediaNameZh());
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_SIMILARITY_ID,"");
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_SENSITIVE, new int[]{0});
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_MEDIA_NOT, Constants.mediaNotObj);
//		
//		logger.info(jsonQuery);
//		/* 统计国家数 */
//		JSONObject obj = null;
//		SearchBuilder sb = new SearchBuilder();
//		try {
//			obj = sb.initAdvancedQuery(jsonQuery.toString());
//		} catch (QueryFormatException e) {
//			sb.close();
//			logger.info("initAdvancedQuery encounter an error.");
//			return listNCD;
//		}
//		logger.info("[initAdvancedQuery] -> " + obj.toString());
//		SearchRequestBuilder srb = sb.buildQuery(obj);
//		logger.info("[sb.buildQuery] -> " + obj.toString());
//		if(srb == null) {
//			sb.close();
//			logger.info("sb.buildQuery return null.");
//			return listNCD;
//		}
//		logger.info("[es-query]-"+srb.toString());
//		TermsBuilder countryTB = AggregationBuilders.terms("countryAgg").field("countryNameZh");
//		Map<String, Aggregation> aggMap;
//		
//        try {
//        	SearchResponse sr1 = srb
//            		.setTypes(Configuration.INDEX_TYPE_ARTICLE)
////            		.setSearchType(SearchType.COUNT)
////            		.setPostFilter(FilterBuilders.matchAllFilter())
//            		.setMinScore(Configuration.QUERY_RESULT_MIN_SCORE)
//            		.addAggregation(countryTB)
//            		.execute()
//            		.actionGet();
//        	logger.info("search NCD end");
//            aggMap = sr1.getAggregations().asMap();
//            logger.info(aggMap.keySet());
//            /* 解析国家分布 */
//            StringTerms countryTerms = (StringTerms) aggMap.get("countryAgg");
//            List<Bucket> buckets = countryTerms.getBuckets();
//           
//            for(Bucket bucket : buckets)
//            {
//            	System.out.print(bucket.getKey() + "  ");
//            	System.out.println(bucket.getDocCount());
//            	TGeisAnalyseNCountryDis t = new TGeisAnalyseNCountryDis();
//				t.setCountryName(bucket.getKey() + "");
//				t.setCount((int) bucket.getDocCount());
//				listNCD.add(t);
//            }
//            buckets = null;
//            sr1 = null;
//            countryTerms = null;
//		} catch (Exception e) {
//			e.printStackTrace();
//		} 
//        sb.close();
//        srb = null;
//        aggMap = null;
//        jsonQuery = null;
//        obj = null;	
//        return listNCD;
//	}
//	
//	@SuppressWarnings("unchecked")
//	public List<NewsTrend> searchDate(Search search, String beginDate, String endDate) {
//		JSONObject jsonQuery = new JSONObject();
//		jsonQuery.put(Mapper.Query.KEYWORD, search.getKeyword());
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_BEGIN_DATE, beginDate);
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_END_DATE, endDate);
//		jsonQuery.put(Mapper.Query.RESULT_TYPE, Constant.QUERY_RESULT_FRONT);
//		jsonQuery.put(Mapper.Query.PAGE_NO, 1);
//		jsonQuery.put(Mapper.Query.PAGE_SIZE, 1);
//		jsonQuery.put(Mapper.Query.HIGHLIGHT, false);
//		jsonQuery.put(Mapper.Sort.FIELD_NAME, Mapper.FieldArticle.PUBDATE_SORT);
//		jsonQuery.put(Mapper.Sort.ORDER, Constant.QUERY_SORT_ORDER_DESC);
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_COUNTRY_NAME_ZH, search.getCountryNameZh());
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_LANGUAGE, search.getLanguageCode());
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_MEDIA_LEVEL, search.getMediaLevel());
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_SENTIMENT, search.getSentimentId());
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_CATEGORY, search.getCateGoryId());
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_MEDIA_TYPE, search.getMediaType());
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_MEDIA_NAME_ZH, search.getMediaNameZh());
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_SIMILARITY_ID,"");
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_SENSITIVE, new int[]{0});
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_MEDIA_NOT, Constants.mediaNotObj);
//
//		logger.info(jsonQuery);
//		List<NewsTrend> listNT = new ArrayList<NewsTrend>();
//		/* 构建查询 */
//		JSONObject obj = null;
//		SearchBuilder sb = new SearchBuilder();
//		try {
//			obj = sb.initAdvancedQuery(jsonQuery.toString());
//		} catch (QueryFormatException e) {
//			sb.close();
//			return listNT;
//		}
//		
//		SearchRequestBuilder srb = sb.buildQuery(obj);
//		if(srb == null) {
//			sb.close();
//			return listNT;
//		}
//		
//		/* 总体情感走势 listNST */
//		DateHistogramBuilder dateBuilder = AggregationBuilders.dateHistogram("dateAgg").field("pubTime")
//				.interval(DateHistogramInterval.DAY).minDocCount(0);
//		Map<String, Aggregation> aggMap;
//		try {
//			SearchResponse sr1 = srb.setTypes(Configuration.INDEX_TYPE_ARTICLE)
////					.setSearchType(SearchType.COUNT)
////					.setPostFilter(FilterBuilders.matchAllFilter())
//					.setMinScore(Configuration.QUERY_RESULT_MIN_SCORE)
//					.addAggregation(dateBuilder)
//					.execute()
//					.actionGet();
//			
//			logger.info("search NT end");
//			aggMap = sr1.getAggregations().asMap();
//			logger.info(aggMap.keySet());
//			/* 解析总体情感走势 */
//			aggMap = sr1.getAggregations().asMap();
//			Histogram dateTerms = (Histogram) aggMap.get("dateAgg");
//			logger.info("dateTerms: " + dateTerms.getBuckets().size());
//			for (Histogram.Bucket entry : dateTerms.getBuckets()) {
////				System.out.println("->date: " + ((DateTime) entry.getKey()).toString("yyyy-MM-dd") + ", count: " + entry.getDocCount());
//				NewsTrend t = new NewsTrend();
//				t.setDate(((DateTime) entry.getKey()).toString("yyyy-MM-dd"));
//				t.setCount((int) entry.getDocCount());
//				listNT.add(t);
////				t.show();
//			}
//			dateTerms = null;
//			sr1 = null;
//		} catch (Exception e) {
//		}
//		sb.close();
//		aggMap = null;
//		srb = null;
//		jsonQuery = null;
//		obj = null;
//		return listNT;
//	}
//	
//	public static void main(String[] args) {
//		
//		NewsSearch ss = new NewsSearch();
//		Search search = new Search();
//		search.setKeyword("中国");
////		ss.searchNews(search, null, null);
//		ss.searchDate(search, null, null);
//		System.out.println("------------");
//	}
//	
//}
