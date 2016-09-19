package edu.buaa.nlp.es.news;

import java.util.Properties;

import edu.buaa.nlp.es.util.FileUtil;

public class Mapper {

/*	public static String INDEX_SERVER_ADDRESS="";//索引服务器地址
	public static String CLUSTER_NAME;//节点名称
	public static String INDEX_NAME;//索引名称
	public static String INDEX_TYPE_ARTICLE;
	public static float QUERY_RESULT_MIN_SCORE=1f;
	private static Properties prop;
	
	static{
		//读配置文件
		prop=FileUtil.getPropertyFile("es_config.properties");
		INDEX_SERVER_ADDRESS=prop.getProperty("INDEX_SERVER_ADDRESS");
		INDEX_NAME=prop.getProperty("INDEX_NAME");
		CLUSTER_NAME=prop.getProperty("CLUSTER_NAME");
		INDEX_TYPE_ARTICLE=prop.getProperty("INDEX_TYPE_ARTICLE");
		String score=prop.getProperty("MIN_SCORE", "1");
		QUERY_RESULT_MIN_SCORE=Float.parseFloat(score);
	}*/
	
	/**
	 * 知识单元字段名，与数据库表字段名对应
	 * @author Vincent
	 *
	 */
	public class FieldArticle{
		public static final String ID="uuid";
		
		public static final String TITLE_SRC="titleSrc";
		public static final String TITLE_ZH="titleZh";//只用于显示
		public static final String TITLE_EN="titleEn";
		public static final String ABSTRACT_EN="abstractEn";
		public static final String ABSTRACT_ZH="abstractZh";//只用于显示
		public static final String TEXT_SRC="textSrc";
		public static final String TEXT_EN="textEn";
		public static final String TEXT_ZH="textZh";
		
		public static final String MEDIA_TYPE="mediaType";
		public static final String MEDIA_TNAME="mediaTname";
//		public static final String INDUSTRY_ID="industryId";
//		public static final String INDUSTRY_NAME="industryName";
//		public static final String REGION_ID="regionId";
//		public static final String REGION_NAME="regionName";
//		public static final String LANGUAGE_TYPE="languageType";
		public static final String LANGUAGE_CODE="languageCode";
		public static final String LANGUAGE_TNAME="languageTname";
		public static final String URL="url";
		public static final String VIEW="view";
//		public static final String REPLY="reply";
		public static final String AUTHOR="author";
//		public static final String SUBNAME="subname";
		public static final String IS_ORIGINAL="isOriginal";
		public static final String ORIGINAL="original";
		public static final String PUBDATE="pubdate";
		public static final String PUBDATE_SORT="pubTime";
//		public static final String SNATCH_TIME="snatchTime";
		public static final String CREATE_TIME="created";
		public static final String UPDATE_TIME="updated";
		public static final String CATEGORY_ID="categoryId";
		public static final String CATEGORY_NAME="categoryName";
		public static final String KEYWORDS_EN="keywordsEn";//用于跨语言检索
		public static final String KEYWORDS_ZH="keywordsZh";//单独存储中文文本关键词，不参与检索
		public static final String SENTIMENT_ID="sentimentId";
		public static final String SENTIMENT_NAME="sentimentName";
		public static final String MEDIA_LEVEL="mediaLevel";
//		public static final String LEVEL_NAME="levelName";
//		public static final String COUNTRY_ID="countryId";
//		public static final String COUNTRY_NAME="countryName";
		public static final String COUNTRY_NAME_ZH="countryNameZh";
		public static final String COUNTRY_NAME_EN="countryNameEn";
		public static final String PROVINCE_NAME_ZH = "provinceNameZh";
		public static final String PROVINCE_NAME_EN = "provinceNameEn";
		public static final String DISTRICT_NAME_ZH = "districtNameZh";
		public static final String DISTRICT_NAME_EN = "districtNameEn";
		
//		public static final String MEDIA_ID="mediaId";
		public static final String MEDIA_NAME_ZH="mediaNameZh";
		public static final String MEDIA_NAME_EN="mediaNameEn";
		public static final String MEDIA_NAME_SRC="mediaNameSrc";
		public static final String DOC_LENGTH="docLength";
		public static final String WEBSITE_ID="websiteId";
		public static final String TRANSFER="transfer";
		public static final String SIMILARITY_ID="similarityId";
		public static final String PRODUCTS="products";
		public static final String COMPANIES="companies";
		public static final String TRANSFROMM="transFromM";
		public static final String PV="pv";
		public static final String ISHOME="isHome";
		public static final String ISPICTURE="isPicture";
		public static final String COME_FROM="comeFrom";
		public static final String COME_FROM_DB="comeFromDb";
		public static final String USER_TAG="userTag";
		
		public static final String IS_SENSITIVE="isSensitive";
		public static final String SENSITIVE_TYPE="sensitiveType";
		public static final String SENSITIVE_CLS="sensitiveCls";
	}
	
	/**
	 * 查询字段名
	 * @author Vincent
	 *
	 */
	public class Query{
		public static final String INDEX_TYPE="type";
		public static final String PAGE_NO="pageNo";
		public static final String PAGE_SIZE="pageSize";
		public static final String KEYWORD="keyword";
		/**
		 * 结果集类型，包括前端页面检索（front）、分析检索（analysis）
		 */
		public static final String RESULT_TYPE="resultType";
		/**
		 * 搜索结果集
		 */
		public static final String RESULT_LIST="resultList";
		/**
		 * 搜索结果总条数
		 */
		public static final String RESULT_COUNT="resultCount";
		/**
		 * 高亮
		 */
		public static final String HIGHLIGHT="highlight";
		
		
	}
	
	/**
	 * 高级检索查询字段定义
	 * 查询对象包含三个部分：field、keyword、operator
	 * @author Vincent
	 *
	 */
	public class AdvancedQuery{
		public static final String FIELD_BEGIN_DATE="beginDate";
		public static final String FIELD_END_DATE="endDate";
		public static final String FIELD_REGION="regionId";
		public static final String FIELD_COUNTRY_NAME_ZH="countryNameZh";
		
		
		public static final String FIELD_LANGUAGE="languageCode";//"languageId";
		public static final String FIELD_LANGUAGE_TNAME="languageTname";//"languageId";
		
		public static final String FIELD_MEDIA_LEVEL="mediaLevel";
		public static final String FIELD_SENTIMENT="sentimentId";
		public static final String FIELD_CATEGORY="categoryId";
		public static final String FIELD_MEDIA="mediaId";
		public static final String FIELD_MEDIA_TYPE="mediaType";
		public static final String FIELD_MEDIA_NAME_ZH="mediaNameZh";
		public static final String FIELD_MEDIA_NAME_EN="mediaNameEn";
		public static final String FIELD_MEDIA_NOT="mediaIdNot";

		public static final String FIELD_ID="_id";
		
		public static final String FIELD_SIMILARITY_ID="similarityId";
	
		public static final String FIELD_COMEFROM = "comeFrom";
		public static final String FIELD_SENSITIVE = "isSensitive";
		
		
		
		/**
		 * 高级检索查询对象：查询域的名称
		 */
		public static final String FIELD="fieldName";
		/**
		 * 高级检索查询对象：关键词
		 */
		public static final String KEYWORD="keyword";
		/**
		 * 高级检索查询对象：逻辑操作符
		 */
		public static final String OPERATOR="operator";
		/**
		 * 高级检索查询体
		 */
		public static final String QUERY_BODY="queryBody";
	}
	
	/**
	 * 排序相关字段名
	 * @author Vincent
	 *
	 */
	public class Sort{
		public static final String FIELD_NAME="fieldName";
		public static final String ORDER="order";
		public static final String RELEVANCE="_score";
		
	}
	
	private Mapper(){}
}
