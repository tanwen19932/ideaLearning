package edu.buaa.nlp.es.product;

import java.util.Properties;

import edu.buaa.nlp.es.util.FileUtil;

public class Mapper {


	/**
	 * 知识单元字段名，与数据库表字段名对应
	 * @author Vincent
	 *
	 */
	public class FieldProduct{
		public static final String UUID="id";
		public static final String PRODUCT_NAME="productName";
		public static final String INDUSTRY_NAME="industryName";
		public static final String COMPANY_NAME="companyName";
	}
	
	public class FieldCompany{
		public static final String UUID="id";
		public static final String INDUSTRY_NAME="industryName";
		public static final String COMPANY_NAME="companyName";
		public static final String COMPANY_ALIAS="companyAlias";
		public static final String COMPANY_INTRO="companyIntro";
		public static final String COMPANY_LOGO="companyLogo";
		
		
	}
	
	
	
	public class FieldComment{
		public static final String UUID="commentId";
		public static final String PRODUCT_ID="productId";
		public static final String PRODUCT_NAME="productName";
		public static final String INDUSTRY_NAME="industryName ";
		public static final String COMPANY_NAME="companyName";
		public static final String CREATE_DATE="createdDate";
		public static final String CREATE_TIME="createTime";	
		public static final String CONTENT="contentSrc";
		public static final String CONTENT_ZH="contentZH";
		public static final String CONTENT_EN="contentEn";	
		public static final String USER_ID="userId";
		public static final String USER_NAME="userName";
		public static final String USER_LOC="userLoc";
		public static final String USER_REGIST_TIME="userRegistTime";
		public static final String REFERENCE_NAME="referenceName";
		public static final String WEBSITE="website";
		public static final String LANGUAGE_CODE="languageCode";
		public static final String OPINION_TARGET="opinionTarget";
		public static final String OPINION_WORD="opinionWord";
		public static final String ELES_SENTI="elesSenti";
		public static final String COUNT="count";
		public static final String SENTIMENT="sentiment";
		public static final String SCORE_BYUSER="cmtRateByUser";
		public static final String SCORE_BYMACHINE="cmtRateByAlgo";
		
		public static final String COME_FROM="comeFrom";
		public static final String USER_TAG="userTag";
		
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
		public static final String FIELD_COMMENT_UUID="commentId";
		public static final String FIELD_BEGIN_DATE="beginDate";
		public static final String FIELD_END_DATE="endDate";
		public static final String FIELD_PRODUCT_NAME="productName";
		public static final String FIELD_INDUSTRY_NAME="industryName";
		public static final String FIELD_COMPANY_NAME="companyName";
		public static final String FIELD_SENTIMENT="sentiment";
		public static final String FIELD_LANGUAGE="languageCode";
		public static final String FIELD_WEBSITE="website";
		
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
