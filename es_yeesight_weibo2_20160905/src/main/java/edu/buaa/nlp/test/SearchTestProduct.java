package edu.buaa.nlp.test;

import java.util.List;

import org.apache.log4j.PropertyConfigurator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import edu.buaa.nlp.es.product.Mapper;
import edu.buaa.nlp.es.product.Mapper.FieldCompany;
import edu.buaa.nlp.es.product.Mapper.FieldProduct;
import edu.buaa.nlp.es.product.SearchBuilder;
import edu.buaa.nlp.es.util.Constant;


public class SearchTestProduct {
	
	public static void testAdvanced(String keyword){
		SearchBuilder sb=new SearchBuilder();
		JSONObject jsonQuery=new JSONObject();
		jsonQuery.put(Mapper.Query.KEYWORD, "电子工业出版社");
		
		jsonQuery.put(Mapper.Query.KEYWORD, keyword);
		
		jsonQuery.put(Mapper.Query.RESULT_TYPE, Constant.QUERY_RESULT_FRONT); //.QUERY_RESULT_ANALYSIS);
		
		jsonQuery.put(Mapper.Query.PAGE_NO, 1);
		jsonQuery.put(Mapper.Query.PAGE_SIZE, 10);
		jsonQuery.put(Mapper.Query.HIGHLIGHT, false);
		jsonQuery.put(Mapper.Sort.FIELD_NAME, Mapper.Sort.RELEVANCE);
		//jsonQuery.put(Mapper.Sort.FIELD_NAME, Mapper.FieldComment.COME_FROM);
		jsonQuery.put(Mapper.Sort.ORDER, Constant.QUERY_SORT_ORDER_DESC);
		//####################高级检索部分#######  begin  ##################33
		//语言
//		jsonQuery.put(Mapper.AdvancedQuery.FIELD_LANGUAGE, new String[]{"zh"});//语言名称数组 
		//日期
		//jsonQuery.put(Mapper.AdvancedQuery.FIELD_BEGIN_DATE, "2016-04-01 00:00:00");//开始日期
		//jsonQuery.put(Mapper.AdvancedQuery.FIELD_END_DATE, "2016-05-30 00:00:00");//结束日期
		//UUID
		//jsonQuery.put(Mapper.AdvancedQuery.FIELD_COMMENT_UUID, new String[]{"1000005432","1000005440"});
		//行业名	
		//jsonQuery.put(Mapper.AdvancedQuery.FIELD_INDUSTRY_NAME, new String[]{"手机"});
		//情感
		//jsonQuery.put(Mapper.AdvancedQuery.FIELD_SENTIMENT, new int[]{1});//情感标识ID数组
		//产品名
		//jsonQuery.put(Mapper.AdvancedQuery.FIELD_PRODUCT_NAME, new String[]{ "三星 Galaxy S7 edge（G9350）32G版 铂光金移动联通电信4G手机 双卡双待 骁龙820手机"});//产品名
		//jsonQuery.put(Mapper.AdvancedQuery.FIELD_PRODUCT_NAME, new String[]{ "jd.com"});
		//站点名
		//jsonQuery.put(Mapper.AdvancedQuery.FIELD_WEBSITE, new String[]{ "jd.com"});//
				
		

		long s1=System.currentTimeMillis();
		String result=sb.crossSearch(jsonQuery.toString());
		//String result=sb.crossSearchBySentiElement(jsonQuery.toString());
		//String result=sb.filterSearch(jsonQuery.toString());
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
		jsonQuery.put(Mapper.Query.RESULT_TYPE, Constant.QUERY_RESULT_FRONT); //.QUERY_RESULT_ANALYSIS);
		jsonQuery.put(Mapper.AdvancedQuery.FIELD_COMMENT_UUID, new String[]{"1000005432","1000005440"});
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
	

	public static void insertProduct() throws InterruptedException
	{
		JSONObject jsonQuery=new JSONObject();
		String uuid = "1234567890";
		jsonQuery.put(FieldProduct.UUID, uuid);
		jsonQuery.put(FieldProduct.PRODUCT_NAME, "test update44444");
		jsonQuery.put(FieldProduct.INDUSTRY_NAME, "hahahaha hahaha test update344444");
		jsonQuery.put(FieldProduct.COMPANY_NAME, "1234567890");
		
		SearchBuilder sb=new SearchBuilder();
		sb.insertProduct(jsonQuery);
		sb.close();
		Thread.sleep(1000);
	}
	
	public static void insertCompany() throws InterruptedException
	{
		JSONObject jsonQuery=new JSONObject();
		String uuid = "1234567890";
		jsonQuery.put(FieldCompany.UUID, uuid);
		jsonQuery.put(FieldCompany.COMPANY_NAME, "1234567890");
		jsonQuery.put(FieldCompany.COMPANY_ALIAS, "1234567890");
		jsonQuery.put(FieldCompany.INDUSTRY_NAME, "hahahaha hahaha test update344444");
		jsonQuery.put(FieldCompany.COMPANY_INTRO, "1234567890");
		jsonQuery.put(FieldCompany.COMPANY_LOGO, "data.....");
		
		SearchBuilder sb=new SearchBuilder();
		sb.insertCompany(jsonQuery);
		sb.close();
		Thread.sleep(1000);
	}
	
	public static void deleteProduct() throws InterruptedException
	{
		JSONObject jsonQuery=new JSONObject();
		String uuid = "1234567890";
		jsonQuery.put(FieldProduct.UUID, uuid);
	
		SearchBuilder sb=new SearchBuilder();
		sb.deleteProduct(jsonQuery);
		sb.close();
		Thread.sleep(1000);
	}
	
	public static void deleteCompany() throws InterruptedException
	{
		JSONObject jsonQuery=new JSONObject();
		String uuid = "1234567890";
		jsonQuery.put(FieldCompany.UUID, uuid);
	
		SearchBuilder sb=new SearchBuilder();
		sb.deleteCompany(jsonQuery);
		sb.close();
		Thread.sleep(1000);
	}
	
	public static void searchProduct() throws InterruptedException
	{
		SearchBuilder sb=new SearchBuilder();
		JSONObject jsonQuery=new JSONObject();
		jsonQuery.put(Mapper.Query.KEYWORD, "三星");

		jsonQuery.put(Mapper.Query.RESULT_TYPE, Constant.QUERY_RESULT_FRONT); //.QUERY_RESULT_ANALYSIS);
		
		jsonQuery.put(Mapper.Query.PAGE_NO, 1);
		jsonQuery.put(Mapper.Query.PAGE_SIZE, 10);
		jsonQuery.put(Mapper.Query.HIGHLIGHT, false);
		jsonQuery.put(Mapper.Sort.FIELD_NAME, Mapper.Sort.RELEVANCE);
		//jsonQuery.put(Mapper.Sort.FIELD_NAME, Mapper.FieldComment.COME_FROM);
		jsonQuery.put(Mapper.Sort.ORDER, Constant.QUERY_SORT_ORDER_DESC);


		long s1=System.currentTimeMillis();
		String result=sb.crossSearchProduct(jsonQuery.toString());
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
	
	public static void searchCompany() throws InterruptedException
	{
		SearchBuilder sb=new SearchBuilder();
		JSONObject jsonQuery=new JSONObject();
		jsonQuery.put(Mapper.Query.KEYWORD, "1234567890");

		jsonQuery.put(Mapper.Query.RESULT_TYPE, Constant.QUERY_RESULT_FRONT); //.QUERY_RESULT_ANALYSIS);
		
		jsonQuery.put(Mapper.Query.PAGE_NO, 1);
		jsonQuery.put(Mapper.Query.PAGE_SIZE, 10);
		jsonQuery.put(Mapper.Query.HIGHLIGHT, false);
		jsonQuery.put(Mapper.Sort.FIELD_NAME, Mapper.Sort.RELEVANCE);
		//jsonQuery.put(Mapper.Sort.FIELD_NAME, Mapper.FieldComment.COME_FROM);
		jsonQuery.put(Mapper.Sort.ORDER, Constant.QUERY_SORT_ORDER_DESC);


		long s1=System.currentTimeMillis();
		String result=sb.crossSearchCompany(jsonQuery.toString());
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
	
	
	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.properties");
		testAdvanced("幻城");
		//getUUID();
		//getAllIndex();
		
		try {
	//		insertProduct();
	//		searchProduct();
	//		insertCompany();
	//		searchCompany();
	//		deleteCompany();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
