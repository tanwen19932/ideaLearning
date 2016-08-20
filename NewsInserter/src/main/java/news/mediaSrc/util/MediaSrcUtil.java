package news.mediaSrc.util;

import news.DicMap;
import news.News;
import tw.utils.HtmlUtil;
import tw.utils.PropertiesUtil;
import tw.utils.StringUtil;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static tw.utils.StringUtil.isNull;

public class MediaSrcUtil {

    static Properties properties = PropertiesUtil
            .getProp(MediaSrcUtil.class.getResource("/").getPath() + "mediaSrc.properties");
    static Map<String, Map<String, String>> mediaSrcMap = null;

    private MediaSrcUtil() {
    }

    public static Map<String, Map<String, String>> getInstance() {
        try {
            if (mediaSrcMap == null) {
                getMediaMap();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mediaSrcMap;
    }

    public static boolean fixNews(News news) {
        getInstance();
        String url = news.getUrl();
        if (StringUtil.isNull(url)) {
            return false;
        }
        Map<String, String> valueMap = mediaSrcMap.get(HtmlUtil.getDomain(news.getUrl()));
        if (valueMap == null) {
            System.err.println("数据源不在 数据源库!!");
            return false;
        } else {
            news.setMediaNameZh(valueMap.get("mediaNameZh"));
            news.setMediaNameSrc(valueMap.get("mediaNameSrc"));
            news.setMediaNameEn(valueMap.get("mediaNameEn"));
            news.setCountryNameZh(valueMap.get("countryNameZh"));
            news.setCountryNameEn(valueMap.get("countryNameEn"));
            news.setDistrictNameZh(valueMap.get("districtNameZh"));
            news.setDistrictNameEn(valueMap.get("districtNameEn"));
            String levelString = valueMap.get("mediaLevel");
            news.setMediaLevel(Integer.valueOf(levelString));
            return true;
        }
    }

    private synchronized static void getMediaMap() throws SQLException, ClassNotFoundException {
        if (mediaSrcMap == null) {
            mediaSrcMap = new HashMap<>();
            Class.forName(properties.getProperty("JDBC_DRIVER"));
            Connection mysqlCon = DriverManager.getConnection(properties.getProperty("JDBC_URL"),
                    properties.getProperty("JDBC_USER"), properties.getProperty("JDBC_PWD"));
            PreparedStatement preparedStatement2 = null;
            ResultSet mediaSrcRs = null;
            try {
                String sql2 = "select  * from  " + properties.getProperty("TABLE");
                try {
                    preparedStatement2 = mysqlCon.prepareStatement(sql2);
                    mediaSrcRs = preparedStatement2.executeQuery();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                while (mediaSrcRs.next()) {
                    MediaSrc mediaSrc = new MediaSrc();
                    String id = mediaSrcRs.getString("id");
                    mediaSrc.setMediaNameZh(mediaSrcRs.getString("mediaNameZh"));
                    mediaSrc.setMediaNameEn(mediaSrcRs.getString("mediaNameEn"));
                    mediaSrc.setMediaNameSrc(mediaSrcRs.getString("mediaNameSrc"));
                    mediaSrc.setCountryNameZh(mediaSrcRs.getString("countryNameZh"));
                    mediaSrc.setCountryNameEn(mediaSrcRs.getString("countryNameEn"));
                    mediaSrc.setDistrictNameZh(mediaSrcRs.getString("districtNameZh"));
                    mediaSrc.setDistrictNameEn(mediaSrcRs.getString("districtNameEn"));
                    mediaSrc.setDomainName(mediaSrcRs.getString("domainName"));
                    mediaSrc.setLanguageCode(mediaSrcRs.getString("languageCode"));
                    mediaSrc.setLanguageTname(mediaSrcRs.getString("languageTname"));
                    mediaSrc.setMediaLevel(mediaSrcRs.getString("mediaLevel"));
                    if (!fixMediaName(mediaSrc)) continue;
                    if (mediaSrc.mediaNameSrc == null || mediaSrc.mediaNameSrc.toLowerCase().equals("null")) {
                        mediaSrc.setMediaNameSrc(mediaSrc.mediaNameZh);
                    }
                    if (fixMediaSrcCountry(mediaSrc)) {
                        Map<String, String> valueMap = new HashMap<>();

                        valueMap.put("mediaNameSrc", mediaSrc.mediaNameSrc);
                        valueMap.put("mediaNameZh", mediaSrc.mediaNameZh);
                        valueMap.put("mediaNameEn", mediaSrc.mediaNameEn);
                        valueMap.put("mediaLevel", mediaSrc.mediaLevel); // 级别
                        valueMap.put("countryNameZh", mediaSrc.countryNameZh);
                        valueMap.put("countryNameEn", mediaSrc.countryNameEn);
                        valueMap.put("districtNameZh", mediaSrc.districtNameZh);
                        valueMap.put("districtNameEn", mediaSrc.districtNameEn);
                        mediaSrcMap.put(mediaSrc.domainName, valueMap);
                    }
                    ;
                }

            } catch (Exception e) {
                System.err.println(e.getMessage());
            } finally {
                if (mediaSrcRs != null) {
                    mediaSrcRs.close();
                }
                if (preparedStatement2 != null) {
                    preparedStatement2.close();
                }
                if (mysqlCon != null) {
                    mysqlCon.close();
                }
            }
        }
    }

    public static boolean fixMediaName(MediaSrc mediaSrc) {
        if (mediaSrc.mediaNameZh == null || mediaSrc.mediaNameZh.toLowerCase().equals("null")) {
            if (mediaSrc.mediaNameSrc != null && !mediaSrc.mediaNameSrc.toLowerCase().equals("null")) {
                mediaSrc.setMediaNameZh(mediaSrc.mediaNameSrc);
            } else if (mediaSrc.mediaNameEn == null || mediaSrc.mediaNameEn.toLowerCase().equals("null")) {
                mediaSrc.setMediaNameZh(mediaSrc.mediaNameEn);
                mediaSrc.setMediaNameSrc(mediaSrc.mediaNameEn);
            } else {
                return false;
            }
        }
        return true;
    }

    public static boolean fixNewsMediaName(News news) {
        if (news.getMediaNameZh() == null || news.getMediaNameZh().toLowerCase().equals("null")) {
            if (news.getMediaNameSrc() != null && !news.getMediaNameSrc().toLowerCase().equals("null")) {
                news.setMediaNameZh(news.getMediaNameSrc());
            } else if (news.getMediaNameEn() == null || news.getMediaNameEn().toLowerCase().equals("null")) {
                news.setMediaNameZh(news.getMediaNameEn());
                news.setMediaNameSrc(news.getMediaNameEn());
            } else {
                return false;
            }
        }
        return true;
    }

    public static boolean fixMediaSrcCountry(MediaSrc mediaSrc) {
        if (isNull(mediaSrc.getCountryNameEn()) && isNull(mediaSrc.getCountryNameZh())) {
            return false;
        }
        if (isNull(mediaSrc.getCountryNameEn()) && !isNull(mediaSrc.getCountryNameZh())) {
            mediaSrc.setCountryNameEn(DicMap.getCountryEn(mediaSrc.getCountryNameZh()));
        }
        if (!isNull(mediaSrc.getCountryNameEn()) && isNull(mediaSrc.getCountryNameZh())) {
            mediaSrc.setCountryNameZh(DicMap.getCountryZh(mediaSrc.getCountryNameEn()));
        }
        return true;
    }
}
