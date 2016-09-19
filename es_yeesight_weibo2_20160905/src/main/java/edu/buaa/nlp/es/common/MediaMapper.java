package edu.buaa.nlp.es.common;

import java.util.Properties;

import edu.buaa.nlp.es.util.FileUtil;

public class MediaMapper {

	/**
	 * 知识单元字段名，与数据库表字段名对应
	 * @author Vincent
	 *
	 */
	public class FieldMedia{
		public static final String ID="mediaSrcId";


		public static final String MEDIA_NAME_ZH="mediaNameZh";
		public static final String MEDIA_NAME_EN="mediaNameEn";
		public static final String MEDIA_NAME_SRC="mediaNameSrc";	

		public static final String COUNTRY_NAME_ZH="countryNameZh";
		public static final String COUNTRY_NAME_EN="countryNameEn";
		
		public static final String PROVINCE_NAME_ZH = "mediaProvinceZh";
		public static final String PROVINCE_NAME_EN = "mediaProvinceEn";
		public static final String DISTRICT_NAME_ZH = "districtNameZh";
		public static final String DISTRICT_NAME_EN = "districtNameEn";

		
		public static final String MEDIA_LEVEL="mediaLevel";
		public static final String URL="domainName";
//		public static final String MEDIA_INDUSTRY_ID="mediaIndustryId";
		
//		public static final String MEDIA_TYPE="mediaType";
//		public static final String MEDIA_TNAME="mediaTname";
		
		public static final String LANGUAGE_CODE="languageCode";
		public static final String LANGUAGE_TNAME="languageTname";


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
		public static final String FIELD_URL="url";
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
	
	private MediaMapper(){}
}
