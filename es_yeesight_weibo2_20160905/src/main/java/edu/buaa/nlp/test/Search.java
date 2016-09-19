package edu.buaa.nlp.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.joda.time.DateTime;

import edu.buaa.nlp.es.constant.Configuration;
import edu.buaa.nlp.es.exception.QueryFormatException;
import edu.buaa.nlp.es.news.Mapper;
import edu.buaa.nlp.es.news.SearchBuilder;
import edu.buaa.nlp.es.util.Constant;
import net.sf.json.JSONObject;

public class Search {
	public static void main(String[] args) {

		JSONObject jsonQuery = new JSONObject();
		jsonQuery.put(Mapper.Query.KEYWORD, "中国");
		jsonQuery.put(Mapper.AdvancedQuery.FIELD_BEGIN_DATE,"2015-01-01 00:00:00");
		jsonQuery.put(Mapper.AdvancedQuery.FIELD_END_DATE, "2016-01-01 00:00:00");
		jsonQuery.put(Mapper.Query.RESULT_TYPE, Constant.QUERY_RESULT_FRONT);
		jsonQuery.put(Mapper.Query.PAGE_NO, 1);
		jsonQuery.put(Mapper.Query.PAGE_SIZE, 1);
		jsonQuery.put(Mapper.Query.HIGHLIGHT, false);
		jsonQuery.put(Mapper.Sort.FIELD_NAME, Mapper.FieldArticle.PUBDATE_SORT);
		jsonQuery.put(Mapper.Sort.ORDER, Constant.QUERY_SORT_ORDER_DESC);
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

		System.out.println(jsonQuery);
//		List<NewsTrend> listNT = new ArrayList<NewsTrend>();
		/* 构建查询 */
		JSONObject obj = null;
		SearchBuilder sb = new SearchBuilder();
		try {
			obj = sb.initAdvancedQuery(jsonQuery.toString());
		} catch (QueryFormatException e) {
			sb.close();
//			return listNT;
		}
		
		SearchRequestBuilder srb = sb.buildQuery(obj);
		if(srb == null) {
			sb.close();
//			return listNT;
		}
		
		/* 总体情感走势 listNST */
		DateHistogramBuilder dateBuilder = AggregationBuilders.dateHistogram("dateAgg").field("pubTime")
				.interval(DateHistogramInterval.DAY).minDocCount(0);
		Map<String, Aggregation> aggMap;
		try {
			srb.setTypes(Configuration.INDEX_TYPE_ARTICLE)
//			.setSearchType(SearchType.COUNT)
//			.setPostFilter(FilterBuilders.matchAllFilter())
			.setMinScore(Configuration.QUERY_RESULT_MIN_SCORE)
			.addAggregation(dateBuilder);
			System.out.println(srb.toString());
			
			SearchResponse sr1 = srb.
//					.setSearchType(SearchType.COUNT)
//					.setPostFilter(FilterBuilders.matchAllFilter())
//					.setMinScore(Configuration.QUERY_RESULT_MIN_SCORE)
//					.addAggregation(dateBuilder)
					execute()
					.actionGet();
			
			aggMap = sr1.getAggregations().asMap();
			System.out.println("++++++++++++++++++++++++ "+aggMap);
			/* 解析总体情感走势 */
			aggMap = sr1.getAggregations().asMap();
			Histogram dateTerms = (Histogram) aggMap.get("dateAgg");
//			logger.info("dateTerms: " + dateTerms.getBuckets().size());
			for (Histogram.Bucket entry : dateTerms.getBuckets()) {
//				System.out.println("->date: " + ((DateTime) entry.getKey()).toString("yyyy-MM-dd") + ", count: " + entry.getDocCount());
//				NewsTrend t = new NewsTrend();
//				t.setDate(((DateTime) entry.getKey()).toString("yyyy-MM-dd"));
//				t.setCount((int) entry.getDocCount());
//				listNT.add(t);
//				t.show();
			}
			dateTerms = null;
			sr1 = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		sb.close();
		aggMap = null;
		srb = null;
		jsonQuery = null;
		obj = null;
//		return listNT;
	
	}
}
