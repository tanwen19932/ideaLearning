package edu.buaa.nlp.es.weibo;

import java.util.Properties;

import edu.buaa.nlp.es.util.FileUtil;

public class Mapper {


	/**
	 * 知识单元字段名，与数据库表字段名对应
	 * @author Vincent
	 *
	 */
	public class FieldWeibo{
		public static final String UUID="myId";
		public static final String ID="id";
		public static final String TIME="time";
		public static final String TIMESTR="timeStr";
		public static final String TEXT="text";
		public static final String TEXTLEN="textLen";
		public static final String ISORI="isOri";
		public static final String SOURCEID="sourceWeiboId";
		public static final String USERID="userId";
		public static final String NAME="name";
		public static final String GENDER="gender";
		public static final String PROVINCEID="provinceId";
		public static final String PROVINCE="province";
		public static final String CITYID="cityId";
		public static final String CITY="city";
		public static final String VERIFIED="verified";
		public static final String VERIFIEDREASON="verifiedReason";
		public static final String USERTYPE="userType";
		public static final String FLWCNT="flwCnt";
		public static final String FRDCNT="frdCnt";
		public static final String STACNT="staCnt";
		public static final String USERAVATAR="userAvatar";
		public static final String RPSCNT="rpsCnt";
		public static final String CMTCNT="cmtCnt";
		public static final String ATDCNT="atdCnt";
		public static final String COMMENTSINCE="commentSince";
		public static final String REPOSTSINCE="repostSince";
		public static final String UPDATETIME="updateTime";
		public static final String UPDATETIMESTR="updateTimeStr";
		
		public static final String SENTIMENT="sentiment";
		public static final String SENTIMENTORIENT="sentimentOrient";
		public static final String PRODUCTS="products";
		public static final String COMPANIES="companies";
		public static final String LANGUAGECODE="languageCode";
		public static final String TEXTZH="textZh";
		public static final String TEXTEN="textEn";
		
		public static final String SOURCETYPE="sourceType";
		public static final String COUNTRY="country";
		public static final String USERTAG="userTag";
		
		public static final String URL="url";
	}
	
	public class FieldWeiboComment{
		public static final String UUID="myId";
		public static final String WEIBOUUID="weiboMyId";
		public static final String ID="id";
		public static final String WEIBOID="weiboId";
		public static final String WEIBOTIME="weiboTime";		
		public static final String TIME="time";
		public static final String TIMESTR="timeStr";
		public static final String TEXT="text";
		public static final String ISORI="isOri";
		public static final String SOURCEID="sourceId";
		public static final String USERID="userId";
		public static final String NAME="name";
		public static final String GENDER="gender";
		public static final String PROVINCEID="provinceId";
		public static final String PROVINCE="province";
		public static final String CITYID="cityId";
		public static final String CITY="city";
		public static final String VERIFIED="verified";
		public static final String VERIFIEDREASON="verifiedReason";
		public static final String USERTYPE="userType";
		public static final String FLWCNT="flwCnt";
		public static final String FRDCNT="frdCnt";
		public static final String STACNT="staCnt";
		public static final String USERAVATAR="userAvatar";
		
		public static final String SENTIMENT="sentiment";
		public static final String SENTIMENTORIENT="sentimentOrient";
		public static final String PRODUCTS="products";
		public static final String COMPANIES="companies";
		public static final String LANGUAGECODE="languageCode";
		public static final String TEXTZH="textZh";
		public static final String TEXTEN="textEn";
		
		public static final String SOURCETYPE="sourceType";
		public static final String USERTAG="userTag";
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
		public static final String FIELD_WEIBO_UUID="weiboMyId";
		public static final String FIELD_BEGIN_DATE="beginDate";
		public static final String FIELD_END_DATE="endDate";
		public static final String FIELD_PROVINCE="province";
		public static final String FIELD_PROVINCE_ID="provinceId";
		public static final String FIELD_LANGUAGE="languageCode";
		public static final String FIELD_SENTIMENT="sentimentOrient";
		public static final String FIELD_ISORI="isOri";
		public static final String FIELD_ID="myId";
		public static final String FIELD_COUNTRY="country";
		public static final String FIELD_SOURCETYPE="sourceType";
		public static final String FIELD_SOURCEWEIBOID="sourceWeiboId";
		public static final String FIELD_NAME="name";
		
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
