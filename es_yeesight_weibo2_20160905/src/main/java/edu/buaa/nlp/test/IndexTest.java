package edu.buaa.nlp.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import edu.buaa.nlp.es.client.ESClient;
import edu.buaa.nlp.es.client.IndexBuilder;
import edu.buaa.nlp.es.constant.Configuration;
import edu.buaa.nlp.es.news.Mapper;
import edu.buaa.nlp.es.util.DateUtil;

public class IndexTest {

	public static void main(String[] args) throws IOException {
//		indexTest(new String[]{"", ""});
		index(19, "uriwq");
//		testUpdateField();
	}
	
	public static void testUpdateField(){
		IndexBuilder ib=new IndexBuilder();
		JSONObject json=new JSONObject();
		json.put(Mapper.FieldArticle.ID, "2jfkdau319");
		ib.updateField(json.toString(), "test", "article", "uuid");
	}

	public static void indexTest(String... args){
		if(args.length<2) {
			System.out.println("Usage: path type");
			return;
		}
		String path=args[0];
		String type=args[1];
		readDir(path, type);
	}
	
	public static void readDir(String path, String type){
		File file=new File(path);
		File[] files=file.listFiles();
		List<JSONObject> list=new ArrayList<JSONObject>();
		for(int i=0; i<files.length; i++ ){
			if(list.size()==2000){
				createIndex(list, type);
				System.out.println("finish "+list.size()+", type ="+type);
				list.clear();
				continue;
			}
			list.add(readFile(files[i]));
		}
		if(!list.isEmpty()){
			createIndex(list, type);
		}
	}
	
	public static void createIndex(List<JSONObject> list, String type){
		IndexBuilder builder=new IndexBuilder();
		JSONArray arr=new JSONArray();
		for(int i=0; i<list.size(); i++){
			JSONObject object=new JSONObject();
			JSONObject old=list.get(i);
			object.put(Mapper.FieldArticle.ID, old.getString("id"));
			object.put(Mapper.FieldArticle.TITLE_SRC, old.getString("titleSrc"));
			object.put(Mapper.FieldArticle.TITLE_EN, old.getString("titleSrc"));
			object.put(Mapper.FieldArticle.TITLE_ZH, old.getString("titleSrc"));
			int len=Math.min(old.getString("textSrc").length(), 150);
			String abs=old.getString("textSrc").substring(0, len);
			object.put(Mapper.FieldArticle.ABSTRACT_ZH, abs);
			object.put(Mapper.FieldArticle.ABSTRACT_EN, abs);
			object.put(Mapper.FieldArticle.TEXT_SRC, old.getString("textSrc"));
			object.put(Mapper.FieldArticle.TEXT_ZH, old.getString("textSrc"));
			object.put(Mapper.FieldArticle.MEDIA_TYPE, 1);
			String mediaName="";
			if(old.containsKey("")){
				mediaName=old.getString("mediaNameSrc");
			}else{
				mediaName="Dothaneagle";
			}
			object.put(Mapper.FieldArticle.MEDIA_TNAME, mediaName);
			object.put(Mapper.FieldArticle.MEDIA_NAME_EN, mediaName);
			object.put(Mapper.FieldArticle.MEDIA_NAME_ZH, mediaName);
			object.put(Mapper.FieldArticle.MEDIA_NAME_SRC, mediaName);
			String lname="";
			if(old.containsKey("languageTname")){
				lname=old.getString("languageTname");
			}else{
				lname="英语";
			}
			object.put(Mapper.FieldArticle.LANGUAGE_TNAME, lname);
			object.put(Mapper.FieldArticle.URL, old.getString("url"));
			object.put(Mapper.FieldArticle.VIEW, old.getInt("view"));
			object.put(Mapper.FieldArticle.IS_ORIGINAL, 1);
			object.put(Mapper.FieldArticle.PUBDATE, old.getString("pubdate"));
			object.put(Mapper.FieldArticle.CREATE_TIME, old.getString("created"));
			object.put(Mapper.FieldArticle.CATEGORY_ID, 1);
			object.put(Mapper.FieldArticle.CATEGORY_NAME, "政治");
			object.put(Mapper.FieldArticle.KEYWORDS_EN, new String[]{"live streaming platform","Panda","pornography","live content","system"});
			object.put(Mapper.FieldArticle.KEYWORDS_ZH, new String[]{"习近平", "上海合作组织", "峰会"});
			object.put(Mapper.FieldArticle.SENTIMENT_ID, 0);
			object.put(Mapper.FieldArticle.SENTIMENT_NAME, "中性");
			object.put(Mapper.FieldArticle.MEDIA_LEVEL, 1);
			String cname="";
			if(old.containsKey("countryNameZh")){
				cname=old.getString("countryNameZh");
			}else{
				cname="美国";
			}
			object.put(Mapper.FieldArticle.COUNTRY_NAME_ZH, cname);
			object.put(Mapper.FieldArticle.COUNTRY_NAME_EN, cname);
			String lan="";
			if(old.containsKey("languageCode")){
				lan=old.getString("languageCode");
			}else{
				lan="zh";
			}
			object.put(Mapper.FieldArticle.LANGUAGE_CODE, lan);
			arr.add(object);
		}
		builder.addUnitBatch(arr.toString(), type, Configuration.INDEX_TYPE_ARTICLE,Mapper.FieldArticle.ID);
		builder.close();
	}
	
	public static JSONObject readFile(File file){
		BufferedReader br=null;
		StringBuffer sb=new StringBuffer();
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
			String line="";
			while((line=br.readLine())!=null){
				sb.append(line.trim());
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(br!=null)
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return JSONObject.fromObject(sb.toString());
	}
	
	
	public static void index(int num, String idbase){
		IndexBuilder builder=new IndexBuilder();
		JSONArray arr=new JSONArray();
		for(int i=0; i<1; i++){
			JSONObject object=new JSONObject();
			object.put(Mapper.FieldArticle.ID, num+idbase+i);
			object.put(Mapper.FieldArticle.TITLE_SRC, "长江中下游水位全线超警 今年降水量仅次于98年");
			object.put(Mapper.FieldArticle.TITLE_EN, "The new points system of betta, stop them fans booing and desires?");
			object.put(Mapper.FieldArticle.TITLE_ZH, "长江中下游水位全线超警 今年降水量仅次于98年");
			object.put(Mapper.FieldArticle.ABSTRACT_ZH, "此次曝光的概念版海报 ，也正体现了影片的这一特性 。  当然 ，这个神秘的存在是否真如我们所料 ，还得看片方的后续动作 。  据悉 ，由北京紫禁城影业有限责任公司 、睦谷富文化传媒 (北京 )有限公司 、上海鼎堃影视投资有限公司 、浙江星河文化经纪有限公司联合出品的青春公路喜剧 《我大学室友的追爱囧途 》，习近平即将于今年 8月登陆全国院线 ，届时必将给大银幕前的观众 ，带来不一样的惊喜与欢乐");
			object.put(Mapper.FieldArticle.ABSTRACT_EN, "Listed companies in total, and panda living broadcast live coverage of this, home game.... the panda and the late betta several anchors live racing into the game on the TV news, live events, creating a betta in the early part of the year is direct to the public security organ at Weibo to the possibility of warning or be ordered to suspend business and make rectification.... for different violations have the detailed deduction rules, when the score is below a certain level, this studio will be the focus of attention of 超管 object, score buckle weighed with the permanent closure of the studio....");
			object.put(Mapper.FieldArticle.TEXT_SRC, "7月5日，湖北某预备役高炮师官兵在传递袋装石料，用于管涌处置围堰修筑。新华社发。。　　灾情。。　　长江中下游降雨已致170人死亡失踪。。　　记者5日从民政部获悉，6月30日以来，持续强降雨导致的长江中下游等地洪涝灾害，已致170人死亡或失踪。。。　　据统计，截至5日9时，灾害造成江苏、安徽、江西、河南、湖北、湖南、广西、重庆、四川、贵州、云南11省（自治区、直辖市）67市（自治州）331个县（市、区）2333.5万人受灾，128人死亡，42人失踪；4.1万间房屋倒塌，24.8万间不同程度损坏；295.2千公顷农作物绝收；直接经济损失381.6亿元。。。　　汛情。。　　长江中下游干流水位全线超警。。　　截至5日14时，除了黄石港江段外，长江监利至南京干流水位全线超警戒，最严重的江段超过警戒水位1米多。这意味长江干堤开始挡洪水。长江防总表示，目前暂时未接到干堤险情的报告。。。　　据长江水文局介绍，降雨还导致多条支流发生超警戒、超保证、超历史大洪水。洞庭湖流域资水4日已经全线超警戒，预计柘溪水库最大入库可达2万立方米每秒，接近“千年一遇”标准；鄱阳湖区域同样全线超警戒。。。　　气象部门预测，5日和6日中下游降雨整体形势将趋于减弱结束，但是上游岷江、沱江、嘉陵江等区域将开始强降雨过程，并将会持续一段时间。据新华社。。　　雨情。。　　评估 此次降雨过程强度为特强。。　　中国气象局昨日通报今年入汛以来全国天气特征。今年初到7月4日，我国气温偏高、降水偏多，全国平均降水量较常年同期偏多21.2%，为1951年以来第二多，仅少于重涝年份1998年。。。　　国家气候中心昨天发布数据称，6月30至7月4日，长江中下游降水量普遍有100到250毫米，其中安徽南部、湖北东部超过250毫米。过程雨量50毫米以上和100毫米以上范围分别为108.8万平方公里和40.1万平方公里，均为今年以来最大；过程雨量250毫米以上范围达6.4万平方公里。共有10县市突破3日雨量极值，6县市日雨量突破历史极值。国家气候中心称，综合评估，此次降雨过程强度为特强，此次暴雨过程为入汛以来我国最强降雨过程。。。　　气象专家提醒，未来三天，受长江上游持续强降雨影响，四川东部的中小河流洪水气象风险较高，长江中下游、江淮地区中小河流洪水气象风险高，其中，江苏中部局地中小河流洪水气象风险很高。。。　　比较 与1998年既相似也有不同。。　　与1998年一样，今年同是厄尔尼诺次年，我国的气候背景和气候条件与1998年有许多类似之处，所以，今年的雨情和汛情与1998年有一定可比性。。。　　国家气候中心气象灾害风险管理室首席专家叶殿秀表示，研究表明，厄尔尼诺事件对我国气候将产生明显影响；与1998年相似，今年长江流域降水明显偏多也是受超强厄尔尼诺事件影响的结果。1998年6月至8月，长江流域发生了继1954年之后又一次全流域性大洪水，嫩江、松花江发生了百年不遇的特大洪水，是新中国成立以来我国少有的重涝年份。。。　　今年入汛以来，长江流域平均降水量和平均暴雨日数均为1961年以来历史同期最多；松花江流域降水量为历史同期第二多，辽河流域平均降水量为历史同期第五多，而且以上两个流域的平均暴雨日数均为1961年以来历史同期第二多。不过与1998年不同的是，今年雨带位置南北摆动大，暴雨过程比1998年偏多6次，但最长持续时间不如1998年。");
			object.put(Mapper.FieldArticle.TEXT_ZH, "湖北某预备役高炮师官兵在传递袋装石料，");
			object.put(Mapper.FieldArticle.TEXT_EN, "Listed companies in total, and panda living broadcast live coverage of this, home game.... the panda and the late betta several anchors live racing into the game on the TV news, live events, creating a betta in the early part of the year is direct to the public security organ at Weibo to the possibility of warning or be ordered to suspend business and make rectification.... for different violations have the detailed deduction rules, when the score is below a certain level, this studio will be the focus of attention of 超管 object, score buckle weighed with the permanent closure of the studio....");
			object.put(Mapper.FieldArticle.MEDIA_TYPE, 1);
			object.put(Mapper.FieldArticle.MEDIA_TNAME, "新闻");
			object.put(Mapper.FieldArticle.MEDIA_NAME_EN, "新浪乐居-新闻列表-活动-楼市新闻");
			object.put(Mapper.FieldArticle.MEDIA_NAME_ZH, "新浪乐居-新闻列表-活动-楼市新闻");
			object.put(Mapper.FieldArticle.MEDIA_NAME_SRC, "新浪乐居-新闻列表-活动-楼市新闻");
			object.put(Mapper.FieldArticle.LANGUAGE_TNAME, "英文");
			object.put(Mapper.FieldArticle.URL, "http://www.dawn.com/news/1229626/man-city-not-up-to-the-mark-on-year-end-hart");
			object.put(Mapper.FieldArticle.VIEW, 110);
			object.put(Mapper.FieldArticle.IS_ORIGINAL, 1);
			String pubdate="2016-07-19 08:45:18";
			object.put(Mapper.FieldArticle.PUBDATE, pubdate);
			object.put(Mapper.FieldArticle.PUBDATE_SORT, DateUtil.time2Unix(pubdate));
			object.put(Mapper.FieldArticle.CREATE_TIME, "2015-01-14 15:31:12");
			object.put(Mapper.FieldArticle.CATEGORY_ID, 1);
			object.put(Mapper.FieldArticle.CATEGORY_NAME, "政治");
			object.put(Mapper.FieldArticle.KEYWORDS_EN, new String[]{"live streaming platform","Panda","pornography","live content","system"});
			object.put(Mapper.FieldArticle.KEYWORDS_ZH, new String[]{"习近平", "上海合作组织", "峰会"});
			object.put(Mapper.FieldArticle.SENTIMENT_ID, 0);
			object.put(Mapper.FieldArticle.SENTIMENT_NAME, "中性");
			object.put(Mapper.FieldArticle.MEDIA_LEVEL, 1);
			object.put(Mapper.FieldArticle.COUNTRY_NAME_ZH, "中国");
			object.put(Mapper.FieldArticle.COUNTRY_NAME_EN, "China");
			object.put(Mapper.FieldArticle.LANGUAGE_CODE, "zh");
			arr.add(object);
		}
		
//		builder.addUnit(object.toString(), Configuration.INDEX_NAME, Configuration.INDEX_TYPE_ARTICLE,Mapper.FieldArticle.ID);
		builder.addUnitBatch(arr.toString(), Configuration.INDEX_NAME, Configuration.INDEX_TYPE_ARTICLE,Mapper.FieldArticle.ID);
//		builder.deleteByType(Mapper.INDEX_TYPE_ARTICLE);
		builder.close();
	}
	
	public static void index2(int num, String idbase){
		IndexBuilder builder=new IndexBuilder();
		JSONArray arr=new JSONArray();
		for(int i=0; i<10000; i++){
			JSONObject object=new JSONObject();
			object.put(Mapper.FieldArticle.ID, num+idbase+i);
			object.put(Mapper.FieldArticle.TITLE_SRC, "温州回应“土地证续期缴费数十万”：系误读");
			object.put(Mapper.FieldArticle.TITLE_EN, "The new points system of betta, stop them fans booing and desires?");
			object.put(Mapper.FieldArticle.TITLE_ZH, "温州回应“土地证续期缴费数十万”：系误读");
//			object.put(Mapper.FieldArticle.ABSTRACT_ZH, "上市公司共、熊猫等直播平台看直播的这，家球赛。...末熊猫和斗鱼的几个主播直播飙车撞人上了电视新闻，今年年初的斗鱼直播造人事件则直接让公安机关在微博上给了可能停业整顿的警告。...分，对于不同的违规行为有详细的扣分准则，当分数低于某一水平时，这个直播间就会是超管重点关注的对象，分数扣完则永久关闭直播间。...");
//			object.put(Mapper.FieldArticle.ABSTRACT_EN, "Listed companies in total, and panda living broadcast live coverage of this, home game.... the panda and the late betta several anchors live racing into the game on the TV news, live events, creating a betta in the early part of the year is direct to the public security organ at Weibo to the possibility of warning or be ordered to suspend business and make rectification.... for different violations have the detailed deduction rules, when the score is below a certain level, this studio will be the focus of attention of 超管 object, score buckle weighed with the permanent closure of the studio....");
			object.put(Mapper.FieldArticle.TEXT_SRC, "<BR/>刚买了房子，却发现土地证已过期；想续期，又被告知需要缴纳数十万元的出让金，这笔费用占总房价的三分之一甚至更多。近段时间，温州频频有市民遭遇这样的难题。 <BR/>  新京报讯 （记者赵实）刚买了房子，却发现土地证已过期；想续期，又被告知需要缴纳数十万元的出让金，这笔费用占总房价的三分之一甚至更多。近段时间，温州频频有市民遭遇这样的难题。 <BR/>  对此，昨日，温州市国土局相关负责人在接受新京报记者采访时回应称，“土地证续期需缴数十万元”系误读，“我们始终都没有这个说法，因为政策还没出。” <BR/>   缴土地出让金遇不同计算方法 <BR/>  据媒体报道，温州一位市民三年前购买了一套75平米的二手房，近日发现土地证过期了，这套房子的土地使用权只有20年。土地管理部门回复她，如果想续期，需要按照现在的基准地价乘以用地面积，缴纳相应的土地出让金，重新购买土地使用权。测算后她得知，需缴纳近20万元的出让金，约为房价的三分之一。 <BR/>  同样也在温州，市民王先生刚买的一套50多平米的二手房也因为土地使用权过期，无法完成最终的交易。“房产证更名已经完成了，但是土地证过期了，我全额交的60万元购房款，全部冻结在银行；原来的房主只收到10万元的订金，房子现在却不属于他了，想拿到剩下的房款，就要把土地出让金补齐。” <BR/>  王先生告诉新京报记者，他们要缴纳的土地出让金，是另一个计算方法，“我们找到一家土地价格评估事务所评估，将这套房的楼面评估价与房产证上的建筑面积相乘，得出的土地出让金是30多万元，相当于总房价的一半。”16日，当他们进一步向土地管理部门和评估机构咨询时，之前的计算方法却又被推翻了。 <BR/>  温州国土局称已着手研究解决方案 <BR/>  同样的问题，为何计算方法不同？土地使用权到期后如何续期？土地出让金应如何计算？一系列问题在网上引发热议。 <BR/>  “之前媒体的报道，有一些内容存在偏差，关于要缴纳的土地出让金相当于房价的三分之一，或者是一半房价，我们始终都没有这个说法，因为政策还没出。”昨日，温州市国土资源局土地利用管理处处长张少清接受新京报记者采访时表示，目前被大家普遍诟病的几十万元土地出让金，只是媒体和市民个人按照所认为的地价、面积等因素算出来的钱，以此与交易房价对比，是不准确的。 <BR/>  “我们工作人员的一些说法，只是一种讨论性的看法，并不是结论。因为我们到目前为止，还没有处理过一起类似的土地续期事件，所以我们无法证实到底该怎么计算，也没有确切的政策可循。”张少清说。 <BR/>  不过，他证实，近几年温州有一批住宅的土地证将要到期，其总数可能比媒体报道的600户还要多。 <BR/>  依照《城镇国有土地使用权出让和转让暂行条例》及《城市房地产管理法》的规定，住宅土地使用权经批准准予续期的，应当重新签订土地使用权出让合同，依照规定支付土地使用权出让金。 <BR/>  此外，《物权法》规定“住宅建设用地使用权期间届满的，自动续期”，但“自动续期”该如何续，张少清介绍，目前国家尚未出台相关实施细则。 <BR/>  温州市国土部门表示，对目前出现的情况，已着手研究相关方案，近期将报上级研究决定。 <BR/>   ■ 追问 <BR/>  涉事房屋土地使用权为何仅20年？ <BR/>  新京报记者从温州国土局拟定的一份声明中了解到，1990年《中华人民共和国城镇国有土地使用权出让和转让暂行条例》发布后，温州市区在办理划拨国有土地使用权转让交易手续时，将划拨性质的国有土地使用权转为出让性质的国有土地使用权，并收取土地出让金。 <BR/>  在该政策实行初期，为了顺利推进国有土地使用权出让工作，居住用地在不超过最高年限的前提下，温州市区按20年到70年分档，由受让方自行选择的方式给予办理出让手续，并交纳相应的土地出让金额。 <BR/>  声明称，这种做法是基于当时国有土地使用权出让工作刚刚起步，且考虑到市民经济承受能力而施行的，符合相关的法律法规，也符合当时的社情民意。也正是由此出现了一批住宅用地的使用权期限为20年的情况，有其特殊性。 <BR/>   土地使用权从何时开始计算？ <BR/>  “很多人认为，我们买的是房子，不是土地，所以对于交纳土地税不认可。”中国房地产及住宅研究会副会长顾云昌说，大家需要厘清中国土地制度的前提，“土地国有，每年需要交纳土地使用费。” <BR/>  北京市朝阳区律协房地产委员会委员张志同律师也表示，很多人在购房时存在认识误区，只注意到房产证上写明房屋产权70年，而忽略了土地使用权的期限。“虽然期限都被设定在70年，但是房屋为所有权，是永久的，只要还没有毁坏，就归产权人所有；而土地只是使用权，期限到了之后，就必须要续期。” <BR/>  尽管房屋产权和土地使用权的最高年限均为70年，但从开发商拿地到房屋出售给个人办理产权证，中间可能会有一定的时间差，这就导致一种房、地年限脱节的情况出现，即土地使用权到期了，房屋所有权年限还没到。 <BR/>  那么房屋产权的年限从何时开始计算？张志同认为，从法律角度来讲，国土资源部门与用地单位或个人依法签订的土地使用权出让合同的日期，才可以作为土地使用权取得的开始时间。 <BR/>   土地使用权到期不续还能住吗？ <BR/>  有专家表示，根据《物权法》规定，住宅建设用地使用权期间届满的，自动续期。政府不能无偿、强制收回这块土地。但如果居民不缴纳土地出让金，其相当于是在无偿使用这块土地。这种情况下，业主在进行房产转让、抵押等方面的权利会受限，需要补缴土地出让金等才可重新拥有这些权利。 <BR/>  张志同也表示，如果土地使用权到期后没有及时续期，按照现在的法律规定，没有强制措施，居民依然可以居住，但若接下来想要进行房产转让，会比较麻烦。 <BR/>   ■ 专家观点 <BR/>  “续期不应按目前地价作收费标准” <BR/>  温州的土地使用权续期事件，在国内并非首例。据媒体报道，青岛阿里山小区的土地使用权续期，在业主交纳出让金时，以区域过去1年的平均地价为计算基准，折算到建筑面积大约是60%；深圳罗湖国际商业大厦的业主在续期时，政府按照公告基准地价的35%要求其补缴。 <BR/>  土地使用权到期后应该如何续期？土地出让金应如何计算？ <BR/>  顾云昌认为，《物权法》规定住宅建设用地使用权期间届满的，自动续期。就此，续期应该从“自动”的角度去施行，“自动就意味着，不用个人去申请。” <BR/>  就土地出让金计算，他认为，应和房地产税收政策结合起来，在购买房屋之初，就一次性缴纳。而对于使用权到期后自动续期的收费，也要和新买房屋价格有所区别，“既有住宅老百姓继续交土地出让金，应该考虑减免，不应按现在的土地价格作为续期收费标准。” <BR/>  顾云昌建议，可以按照基准地价来选择一个合理的中位数，“不能影响现在的收入结构。” <BR/>  张志同则认为，《物权法》在《城镇国有土地使用权出让和转让暂行条例》之后出台，属于高位法，因此对于土地使用权应主要参照《物权法》的规定，其中只规定届满自动续期，并未明确是否需缴纳土地出让金，“在没有明确的法律规定情况下，居民的住宅土地使用权应该免费续期。” <BR/>   ■ 北京落地 <BR/>  北京尚未现住宅土地证到期情况 <BR/>  北京市房屋权属登记部门相关负责人透露，近年来在工作中，尚未遇到过住宅土地使用权到期的情况。 <BR/>  市国土资源局的一位相关工作人员说，目前还没有听说北京市区的住宅土地使用权到期或即将到期的情况，如有必要，接下来将会进行相应调查。 ");
			object.put(Mapper.FieldArticle.TEXT_ZH, "<BR/>刚买了房子，却发现土地证已过期；想续期，又被告知需要缴纳数十万元的出让金，这笔费用占总房价的三分之一甚至更多。近段时间，温州频频有市民遭遇这样的难题。 <BR/>  新京报讯 （记者赵实）刚买了房子，却发现土地证已过期；想续期，又被告知需要缴纳数十万元的出让金，这笔费用占总房价的三分之一甚至更多。近段时间，温州频频有市民遭遇这样的难题。 <BR/>  对此，昨日，温州市国土局相关负责人在接受新京报记者采访时回应称，“土地证续期需缴数十万元”系误读，“我们始终都没有这个说法，因为政策还没出。” <BR/>   缴土地出让金遇不同计算方法 <BR/>  据媒体报道，温州一位市民三年前购买了一套75平米的二手房，近日发现土地证过期了，这套房子的土地使用权只有20年。土地管理部门回复她，如果想续期，需要按照现在的基准地价乘以用地面积，缴纳相应的土地出让金，重新购买土地使用权。测算后她得知，需缴纳近20万元的出让金，约为房价的三分之一。 <BR/>  同样也在温州，市民王先生刚买的一套50多平米的二手房也因为土地使用权过期，无法完成最终的交易。“房产证更名已经完成了，但是土地证过期了，我全额交的60万元购房款，全部冻结在银行；原来的房主只收到10万元的订金，房子现在却不属于他了，想拿到剩下的房款，就要把土地出让金补齐。” <BR/>  王先生告诉新京报记者，他们要缴纳的土地出让金，是另一个计算方法，“我们找到一家土地价格评估事务所评估，将这套房的楼面评估价与房产证上的建筑面积相乘，得出的土地出让金是30多万元，相当于总房价的一半。”16日，当他们进一步向土地管理部门和评估机构咨询时，之前的计算方法却又被推翻了。 <BR/>  温州国土局称已着手研究解决方案 <BR/>  同样的问题，为何计算方法不同？土地使用权到期后如何续期？土地出让金应如何计算？一系列问题在网上引发热议。 <BR/>  “之前媒体的报道，有一些内容存在偏差，关于要缴纳的土地出让金相当于房价的三分之一，或者是一半房价，我们始终都没有这个说法，因为政策还没出。”昨日，温州市国土资源局土地利用管理处处长张少清接受新京报记者采访时表示，目前被大家普遍诟病的几十万元土地出让金，只是媒体和市民个人按照所认为的地价、面积等因素算出来的钱，以此与交易房价对比，是不准确的。 <BR/>  “我们工作人员的一些说法，只是一种讨论性的看法，并不是结论。因为我们到目前为止，还没有处理过一起类似的土地续期事件，所以我们无法证实到底该怎么计算，也没有确切的政策可循。”张少清说。 <BR/>  不过，他证实，近几年温州有一批住宅的土地证将要到期，其总数可能比媒体报道的600户还要多。 <BR/>  依照《城镇国有土地使用权出让和转让暂行条例》及《城市房地产管理法》的规定，住宅土地使用权经批准准予续期的，应当重新签订土地使用权出让合同，依照规定支付土地使用权出让金。 <BR/>  此外，《物权法》规定“住宅建设用地使用权期间届满的，自动续期”，但“自动续期”该如何续，张少清介绍，目前国家尚未出台相关实施细则。 <BR/>  温州市国土部门表示，对目前出现的情况，已着手研究相关方案，近期将报上级研究决定。 <BR/>   ■ 追问 <BR/>  涉事房屋土地使用权为何仅20年？ <BR/>  新京报记者从温州国土局拟定的一份声明中了解到，1990年《中华人民共和国城镇国有土地使用权出让和转让暂行条例》发布后，温州市区在办理划拨国有土地使用权转让交易手续时，将划拨性质的国有土地使用权转为出让性质的国有土地使用权，并收取土地出让金。 <BR/>  在该政策实行初期，为了顺利推进国有土地使用权出让工作，居住用地在不超过最高年限的前提下，温州市区按20年到70年分档，由受让方自行选择的方式给予办理出让手续，并交纳相应的土地出让金额。 <BR/>  声明称，这种做法是基于当时国有土地使用权出让工作刚刚起步，且考虑到市民经济承受能力而施行的，符合相关的法律法规，也符合当时的社情民意。也正是由此出现了一批住宅用地的使用权期限为20年的情况，有其特殊性。 <BR/>   土地使用权从何时开始计算？ <BR/>  “很多人认为，我们买的是房子，不是土地，所以对于交纳土地税不认可。”中国房地产及住宅研究会副会长顾云昌说，大家需要厘清中国土地制度的前提，“土地国有，每年需要交纳土地使用费。” <BR/>  北京市朝阳区律协房地产委员会委员张志同律师也表示，很多人在购房时存在认识误区，只注意到房产证上写明房屋产权70年，而忽略了土地使用权的期限。“虽然期限都被设定在70年，但是房屋为所有权，是永久的，只要还没有毁坏，就归产权人所有；而土地只是使用权，期限到了之后，就必须要续期。” <BR/>  尽管房屋产权和土地使用权的最高年限均为70年，但从开发商拿地到房屋出售给个人办理产权证，中间可能会有一定的时间差，这就导致一种房、地年限脱节的情况出现，即土地使用权到期了，房屋所有权年限还没到。 <BR/>  那么房屋产权的年限从何时开始计算？张志同认为，从法律角度来讲，国土资源部门与用地单位或个人依法签订的土地使用权出让合同的日期，才可以作为土地使用权取得的开始时间。 <BR/>   土地使用权到期不续还能住吗？ <BR/>  有专家表示，根据《物权法》规定，住宅建设用地使用权期间届满的，自动续期。政府不能无偿、强制收回这块土地。但如果居民不缴纳土地出让金，其相当于是在无偿使用这块土地。这种情况下，业主在进行房产转让、抵押等方面的权利会受限，需要补缴土地出让金等才可重新拥有这些权利。 <BR/>  张志同也表示，如果土地使用权到期后没有及时续期，按照现在的法律规定，没有强制措施，居民依然可以居住，但若接下来想要进行房产转让，会比较麻烦。 <BR/>   ■ 专家观点 <BR/>  “续期不应按目前地价作收费标准” <BR/>  温州的土地使用权续期事件，在国内并非首例。据媒体报道，青岛阿里山小区的土地使用权续期，在业主交纳出让金时，以区域过去1年的平均地价为计算基准，折算到建筑面积大约是60%；深圳罗湖国际商业大厦的业主在续期时，政府按照公告基准地价的35%要求其补缴。 <BR/>  土地使用权到期后应该如何续期？土地出让金应如何计算？ <BR/>  顾云昌认为，《物权法》规定住宅建设用地使用权期间届满的，自动续期。就此，续期应该从“自动”的角度去施行，“自动就意味着，不用个人去申请。” <BR/>  就土地出让金计算，他认为，应和房地产税收政策结合起来，在购买房屋之初，就一次性缴纳。而对于使用权到期后自动续期的收费，也要和新买房屋价格有所区别，“既有住宅老百姓继续交土地出让金，应该考虑减免，不应按现在的土地价格作为续期收费标准。” <BR/>  顾云昌建议，可以按照基准地价来选择一个合理的中位数，“不能影响现在的收入结构。” <BR/>  张志同则认为，《物权法》在《城镇国有土地使用权出让和转让暂行条例》之后出台，属于高位法，因此对于土地使用权应主要参照《物权法》的规定，其中只规定届满自动续期，并未明确是否需缴纳土地出让金，“在没有明确的法律规定情况下，居民的住宅土地使用权应该免费续期。” <BR/>   ■ 北京落地 <BR/>  北京尚未现住宅土地证到期情况 <BR/>  北京市房屋权属登记部门相关负责人透露，近年来在工作中，尚未遇到过住宅土地使用权到期的情况。 <BR/>  市国土资源局的一位相关工作人员说，目前还没有听说北京市区的住宅土地使用权到期或即将到期的情况，如有必要，接下来将会进行相应调查。 ");
			object.put(Mapper.FieldArticle.MEDIA_TYPE, 1);
			object.put(Mapper.FieldArticle.MEDIA_TNAME, "新闻");
			object.put(Mapper.FieldArticle.MEDIA_NAME_EN, "新浪乐居-新闻列表-活动-楼市新闻");
			object.put(Mapper.FieldArticle.MEDIA_NAME_ZH, "新浪乐居-新闻列表-活动-楼市新闻");
			object.put(Mapper.FieldArticle.MEDIA_NAME_SRC, "新浪乐居-新闻列表-活动-楼市新闻");
			object.put(Mapper.FieldArticle.LANGUAGE_TNAME, "英文");
			object.put(Mapper.FieldArticle.URL, "http://www.dawn.com/news/1229626/man-city-not-up-to-the-mark-on-year-end-hart");
			object.put(Mapper.FieldArticle.VIEW, 110);
			object.put(Mapper.FieldArticle.IS_ORIGINAL, 1);
			object.put(Mapper.FieldArticle.PUBDATE, "2015-01-13 09:18:21");
			object.put(Mapper.FieldArticle.CREATE_TIME, "2015-01-14 15:31:12");
			object.put(Mapper.FieldArticle.CATEGORY_ID, 1);
			object.put(Mapper.FieldArticle.CATEGORY_NAME, "政治");
			object.put(Mapper.FieldArticle.KEYWORDS_EN, new String[]{"live streaming platform","Panda","pornography","live content","system"});
			object.put(Mapper.FieldArticle.KEYWORDS_ZH, new String[]{"土地使用权", "土地出让金", "住宅", "温州", "使用权到期"});
			object.put(Mapper.FieldArticle.SENTIMENT_ID, 0);
			object.put(Mapper.FieldArticle.SENTIMENT_NAME, "中性");
			object.put(Mapper.FieldArticle.MEDIA_LEVEL, 1);
			object.put(Mapper.FieldArticle.COUNTRY_NAME_ZH, "中国");
			object.put(Mapper.FieldArticle.COUNTRY_NAME_EN, "China");
			object.put(Mapper.FieldArticle.LANGUAGE_CODE, "zh");
			arr.add(object);
		}
		
//		builder.addUnit(object.toString(), Configuration.INDEX_NAME, Configuration.INDEX_TYPE_ARTICLE,Mapper.FieldArticle.ID);
		builder.addUnitBatch(arr.toString(), "test2", Configuration.INDEX_TYPE_ARTICLE,Mapper.FieldArticle.ID);
//		builder.deleteByType(Mapper.INDEX_TYPE_ARTICLE);
		builder.close();
	}
	private static void delete(){
		IndexBuilder builder=new IndexBuilder();
		builder.deleteUnit("101214620336253295D4A4D64119", Configuration.INDEX_NAME,Configuration.INDEX_TYPE_ARTICLE);
		builder.close();
	}
}
