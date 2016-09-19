package news;

import org.apache.hadoop.hbase.util.Bytes;
import org.json.JSONObject;
import tw.utils.ReflectUtil;

import java.lang.reflect.Field;

/**
 * @author TW
 * @date TW on 2016/8/17.
 */


public class News {
    private String id = null; // byte[] id ;
    private int mediaType = 1;// 媒体类型id
    private String mediaTname = null;// 新闻

    private String titleSrc = null;
    private String pubdate = null; // 发布时间
    private String textSrc = null;

    private String websiteId = null; // 站点id
    private String mediaNameSrc = null;// 数据源名称
    private String mediaNameZh = null;
    private String mediaNameEn = null;
    private int mediaLevel; // 级别

    private String countryNameZh = null;
    private String countryNameEn = null;
    private String provinceNameZh = null;
    private String provinceNameEn = null;
    private String districtNameZh = null;
    private String districtNameEn = null;

    private String languageCode = null;// en zh
    private String languageTname = null;

    private String author = null;

    private String created = null; // 爬取时间
    private String updated = null; // 更新时间

    private boolean isOriginal;// 是否原创

    private int view;
    private String url = null;
    private int docLength;

    private String transFromM = null;
    private int pv;
    private boolean isHome;
    private boolean isPicture;

    private String comeFrom = null;
    private String comeFromDb = null;
    private String userTag = "";

    public News(byte[] id, byte[] mediaType, byte[] mediaTname, byte[] titleSrc, byte[] pubdate, byte[] textSrc,
                byte[] websiteId, byte[] mediaNameSrc, byte[] mediaNameZh, byte[] mediaNameEn, byte[] mediaLevel,
                byte[] countryNameZh, byte[] countryNameEn, byte[] provinceNameZh, byte[] provinceNameEn,
                byte[] districtNameZh, byte[] districtNameEn, byte[] languageCode, byte[] languageTname, byte[] author,
                byte[] created, byte[] updated, byte[] isOriginal, byte[] view, byte[] url, byte[] docLength,
                byte[] transFromM, byte[] pv, byte[] isHome, byte[] isPicture, byte[] comeFrom, byte[] comeFromDb,
                byte[] userTag) {

        if (id != null)
            this.id = Bytes.toString(id);
        if (mediaType != null)
            this.mediaType = Integer.parseInt(Bytes.toString(mediaType));
        if (mediaTname != null)
            this.mediaTname = Bytes.toString(mediaTname);

        if (titleSrc != null)
            this.titleSrc = Bytes.toString(titleSrc);
        if (pubdate != null)
            this.pubdate = Bytes.toString(pubdate);
        if (textSrc != null)
            this.textSrc = Bytes.toString(textSrc);

        if (websiteId != null)
            this.websiteId = Bytes.toString(websiteId);
        if (mediaNameSrc != null)
            this.mediaNameSrc = Bytes.toString(mediaNameSrc);
        if (mediaNameZh != null)
            this.mediaNameZh = Bytes.toString(mediaNameZh);
        if (mediaNameEn != null)
            this.mediaNameEn = Bytes.toString(mediaNameEn);
        if (mediaLevel != null)
            this.mediaLevel = Integer.parseInt(Bytes.toString(mediaLevel));

        if (countryNameZh != null)
            this.countryNameZh = Bytes.toString(countryNameZh);
        if (countryNameEn != null)
            this.countryNameEn = Bytes.toString(countryNameEn);
        if (provinceNameZh != null)
            this.provinceNameZh = Bytes.toString(provinceNameZh);
        if (provinceNameEn != null)
            this.provinceNameEn = Bytes.toString(provinceNameEn);
        if (districtNameZh != null)
            this.districtNameZh = Bytes.toString(districtNameZh);
        if (districtNameEn != null)
            this.districtNameEn = Bytes.toString(districtNameEn);

        if (languageCode != null)
            this.languageCode = Bytes.toString(languageCode);
        if (languageTname != null)
            this.languageTname = Bytes.toString(languageTname);

        if (author != null)
            this.author = Bytes.toString(author);
        if (created != null)
            this.created = Bytes.toString(created);
        if (updated != null)
            this.updated = Bytes.toString(updated);
        if (isOriginal != null)
            this.isOriginal = Boolean.parseBoolean(Bytes.toString(isOriginal));

        if (view != null)
            this.view = Integer.parseInt(Bytes.toString(view));
        if (url != null)
            this.url = Bytes.toString(url);
        if (docLength != null)
            this.docLength = Integer.parseInt(Bytes.toString(docLength));

        if (transFromM != null)
            this.transFromM = Bytes.toString(transFromM);
        if (pv != null)
            this.pv = Integer.parseInt(Bytes.toString(pv));
        if (isHome != null)
            this.isHome = Boolean.parseBoolean(Bytes.toString(isHome));
        if (isPicture != null)
            this.isPicture = Boolean.parseBoolean(Bytes.toString(isPicture));
        if (comeFrom != null)
            this.comeFrom = Bytes.toString(comeFrom);
        if (comeFromDb != null)
            this.comeFromDb = Bytes.toString(comeFromDb);
        if (userTag != null)
            this.userTag = Bytes.toString(userTag);

    }

    public News(String id, int mediaType, String mediaTname, String titleSrc, String pubdate, String textSrc,
                String websiteId, String mediaNameSrc, String mediaNameZh, String mediaNameEn, int mediaLevel,
                String countryNameZh, String countryNameEn, String provinceNameZh, String provinceNameEn,
                String districtNameZh, String districtNameEn, String languageCode, String languageTname, String author,
                String created, String updated, boolean isOriginal, int view, String url, int docLength, String transFromM,
                int pv, boolean isHome, boolean isPicture, String comeFrom, String comeFromDb, String userTag) {
        super();
        this.id = id;
        this.mediaType = mediaType;
        this.mediaTname = mediaTname;
        this.titleSrc = titleSrc;
        this.pubdate = pubdate;
        this.textSrc = textSrc;
        this.websiteId = websiteId;
        this.mediaNameSrc = mediaNameSrc;
        this.mediaNameZh = mediaNameZh;
        this.mediaNameEn = mediaNameEn;
        this.mediaLevel = mediaLevel;
        this.countryNameZh = countryNameZh;
        this.countryNameEn = countryNameEn;
        this.provinceNameZh = provinceNameZh;
        this.provinceNameEn = provinceNameEn;
        this.districtNameZh = districtNameZh;
        this.districtNameEn = districtNameEn;
        this.languageCode = languageCode;
        this.languageTname = languageTname;
        this.author = author;
        this.created = created;
        this.updated = updated;
        this.isOriginal = isOriginal;
        this.view = view;
        this.url = url;
        this.docLength = docLength;
        this.transFromM = transFromM;
        this.pv = pv;
        this.isHome = isHome;
        this.isPicture = isPicture;
        this.comeFrom = comeFrom;
        this.comeFromDb = comeFromDb;
        this.userTag = userTag;
    }

    public News(String id, int mediaType, String mediaTname, String titleSrc, String pubdate, String textSrc,
                String websiteId, String mediaNameSrc, String mediaNameZh, String mediaNameEn, int mediaLevel,
                String countryNameZh, String countryNameEn, String provinceNameZh, String provinceNameEn,
                String districtNameZh, String districtNameEn, String languageCode, String languageTname, String author,
                String created, String updated, boolean isOriginal, int view, String url, int docLength, String transFromM,
                int pv, boolean isHome, boolean isPicture, String comeFrom) {
        super();
        this.id = id;
        this.mediaType = mediaType;
        this.mediaTname = mediaTname;
        this.titleSrc = titleSrc;
        this.pubdate = pubdate;
        this.textSrc = textSrc;
        this.websiteId = websiteId;
        this.mediaNameSrc = mediaNameSrc;
        this.mediaNameZh = mediaNameZh;
        this.mediaNameEn = mediaNameEn;
        this.mediaLevel = mediaLevel;
        this.countryNameZh = countryNameZh;
        this.countryNameEn = countryNameEn;
        this.provinceNameZh = provinceNameZh;
        this.provinceNameEn = provinceNameEn;
        this.districtNameZh = districtNameZh;
        this.districtNameEn = districtNameEn;
        this.languageCode = languageCode;
        this.languageTname = languageTname;
        this.author = author;
        this.created = created;
        this.updated = updated;
        this.isOriginal = isOriginal;
        this.view = view;
        this.url = url;
        this.docLength = docLength;
        this.transFromM = transFromM;
        this.pv = pv;
        this.isHome = isHome;
        this.isPicture = isPicture;
        this.comeFrom = comeFrom;
    }

    public News() {
    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public String getMediaTname() {
        return mediaTname;
    }

    public void setMediaTname(String mediaTname) {
        this.mediaTname = mediaTname;
    }

    public String getTitleSrc() {
        return titleSrc;
    }

    public void setTitleSrc(String titleSrc) {
        this.titleSrc = titleSrc;
    }

    public String getPubdate() {
        return pubdate;
    }

    public void setPubdate(String pubdate) {
        this.pubdate = pubdate;
    }

    public String getTextSrc() {
        return textSrc;
    }

    public void setTextSrc(String textSrc) {
        this.textSrc = textSrc;
    }

    public String getWebsiteId() {
        return websiteId;
    }

    public void setWebsiteId(String websiteId) {
        this.websiteId = websiteId;
    }

    public String getMediaNameSrc() {
        return mediaNameSrc;
    }

    public void setMediaNameSrc(String mediaNameSrc) {
        this.mediaNameSrc = mediaNameSrc;
    }

    public String getCountryNameZh() {
        return countryNameZh;
    }

    public void setCountryNameZh(String countryNameZh) {
        this.countryNameZh = countryNameZh;
    }

    public String getCountryNameEn() {
        return countryNameEn;
    }

    public void setCountryNameEn(String countryNameEn) {
        this.countryNameEn = countryNameEn;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean getIsOriginal() {
        return isOriginal;
    }

    public void setIsOriginal(boolean isOriginal) {
        this.isOriginal = isOriginal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getMediaLevel() {
        return mediaLevel;
    }

    public void setMediaLevel(int mediaLevel) {
        this.mediaLevel = mediaLevel;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getDocLength() {
        return docLength;
    }

    public void setDocLength(int docLength) {
        this.docLength = docLength;
    }

    public String getTransFromM() {
        return transFromM;
    }

    public void setTransFromM(String transFromM) {
        this.transFromM = transFromM;
    }

    public int getPv() {
        return pv;
    }

    public void setPv(int pv) {
        this.pv = pv;
    }

    public boolean getIsHome() {
        return isHome;
    }

    public void setIsHome(boolean isHome) {
        this.isHome = isHome;
    }

    public boolean getIsPicture() {
        return isPicture;
    }

    public void setIsPicture(boolean isPicture) {
        this.isPicture = isPicture;
    }

    public String getMediaNameEn() {
        return mediaNameEn;
    }

    public void setMediaNameEn(String mediaNameEn) {
        this.mediaNameEn = mediaNameEn;
    }

    public String getMediaNameZh() {
        return mediaNameZh;
    }

    public void setMediaNameZh(String mediaNameZh) {
        this.mediaNameZh = mediaNameZh;
    }

    public String getLanguageTname() {
        return languageTname;
    }

    public void setLanguageTname(String languageTname) {
        this.languageTname = languageTname;
    }

    public String getProvinceNameZh() {
        return provinceNameZh;
    }

    public void setProvinceNameZh(String provinceNameZh) {
        this.provinceNameZh = provinceNameZh;
    }

    public String getProvinceNameEn() {
        return provinceNameEn;
    }

    public void setProvinceNameEn(String provinceNameEn) {
        this.provinceNameEn = provinceNameEn;
    }

    public String getDistrictNameZh() {
        return districtNameZh;
    }

    public void setDistrictNameZh(String districtNameZh) {
        this.districtNameZh = districtNameZh;
    }

    public String getDistrictNameEn() {
        return districtNameEn;
    }

    public void setDistrictNameEn(String districtNameEn) {
        this.districtNameEn = districtNameEn;
    }

    public String getComeFrom() {
        return comeFrom;
    }

    public void setComeFrom(String comeFrom) {
        this.comeFrom = comeFrom;
    }

    public String getComeFromDb() {
        return comeFromDb;
    }

    public void setComeFromDb(String comeFromDb) {
        this.comeFromDb = comeFromDb;
    }

    public String getUserTag() {
        return userTag;
    }

    public void setUserTag(String userTag) {
        this.userTag = userTag;
    }

   public  static News getFromJSONObject(JSONObject newsJO){
        News news= new News();
        Field[] fields = news.getClass().getDeclaredFields();
        for (Field field : fields) {
            String value = field.getName();
            if (newsJO.has(value)) {
                ReflectUtil.invokeObjSetMethod(news, field, newsJO.get(value));
            }
        }
        return news;
    }

    @Override
    public String toString() {
        return "News{" +
                "author='" + author + '\'' +
                ", id='" + id + '\'' +
                ", mediaType=" + mediaType +
                ", mediaTname='" + mediaTname + '\'' +
                ", titleSrc='" + titleSrc + '\'' +
                ", pubdate='" + pubdate + '\'' +
                ", textSrc='" + textSrc + '\'' +
                ", websiteId='" + websiteId + '\'' +
                ", mediaNameSrc='" + mediaNameSrc + '\'' +
                ", mediaNameZh='" + mediaNameZh + '\'' +
                ", mediaNameEn='" + mediaNameEn + '\'' +
                ", mediaLevel=" + mediaLevel +
                ", countryNameZh='" + countryNameZh + '\'' +
                ", countryNameEn='" + countryNameEn + '\'' +
                ", provinceNameZh='" + provinceNameZh + '\'' +
                ", provinceNameEn='" + provinceNameEn + '\'' +
                ", districtNameZh='" + districtNameZh + '\'' +
                ", districtNameEn='" + districtNameEn + '\'' +
                ", languageCode='" + languageCode + '\'' +
                ", languageTname='" + languageTname + '\'' +
                ", created='" + created + '\'' +
                ", updated='" + updated + '\'' +
                ", isOriginal=" + isOriginal +
                ", view=" + view +
                ", url='" + url + '\'' +
                ", docLength=" + docLength +
                ", transFromM='" + transFromM + '\'' +
                ", pv=" + pv +
                ", isHome=" + isHome +
                ", isPicture=" + isPicture +
                ", comeFrom='" + comeFrom + '\'' +
                ", comeFromDb='" + comeFromDb + '\'' +
                ", userTag='" + userTag + '\'' +
                '}';
    }
}

