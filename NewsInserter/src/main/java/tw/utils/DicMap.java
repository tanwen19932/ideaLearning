package tw.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.Properties;

public class DicMap {
    static Properties language = new Properties();
    static Properties country = new Properties();
    static Properties city = new Properties();
    static Map<Integer, String> mediaTMap;

    static {
        String path = DicMap.class.getResource("/").getPath();
        // String path= "E:/properties/";
        // System.out.println(path);
        language = PropertiesUtil.getProp(path + "language.properties");
        country = PropertiesUtil.getProp(path + "country.properties");
        // city = PropertiesUtil.getProp(path + "city.properties");

        mediaTMap = new HashMap<>();
        mediaTMap.put(999, "其他");
        mediaTMap.put(1, "新闻");
        mediaTMap.put(2, "社交");
        mediaTMap.put(3, "视频");
        mediaTMap.put(4, "数据库");
        mediaTMap.put(5, "电商");
        mediaTMap.put(6, "平媒");
        mediaTMap.put(7, "问答");
        mediaTMap.put(8, "博客");
        mediaTMap.put(9, "Twitter");
        mediaTMap.put(10, "Facebook");
        mediaTMap.put(11, "论坛");
        mediaTMap.put(12, "QQ");
        mediaTMap.put(13, "微信");

    }

    public static String getCountryEn(String lanZh) {
        for (Entry<Object, Object> entry : country.entrySet()) {
            if (entry.getValue().toString().replaceAll("[\\w]", "").equals(lanZh)) {
                return entry.getKey().toString();
            }
        }
        return "Other";
    }

    public static String getCountryZh(String lanEn) {
        try {
            String lanZh = country.getProperty(lanEn).replaceAll("[\\w]", "");

            return lanZh;
        } catch (Exception e) {
            String regex = "[,|，|\\.]";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(lanEn);
            if (matcher.find()) {
                return getCountryZh(lanEn.substring(0, matcher.start()));
            }
        }
        return "其他";
    }

    public static String getCountryEnByAbbr(String abbr) {
        for (Entry<Object, Object> entry : country.entrySet()) {
            if (entry.getValue().toString().replaceAll("[^\\w]", "").equals(abbr)) {
                return entry.getKey().toString();
            }
        }
        return "Other";
    }

    public static String getCountryZhByAbbr(String abbr) {
        for (Entry<Object, Object> entry : country.entrySet()) {
            if (entry.getValue().toString().replaceAll("[^\\w]", "").equals(abbr)) {
                return entry.getValue().toString().replaceAll("[\\w]", "");
            }
        }
        return null;
    }

    public static String getLanguageEn(String lanZh) {
        try {
            String lanEn = (String) language.get(lanZh);
        } catch (Exception e) {
            String regex = "[,|，|\\.]";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(lanZh);
            if (matcher.find()) {
                return getCountryEn(lanZh.substring(0, matcher.start()));
            }
        }
        return "other";
    }

    public static String getLanguageZh(String lanEn) {
        for (Entry<Object, Object> entry : language.entrySet()) {
            if (entry.getKey().toString().matches("\\w*"))
                continue;
            if (entry.getValue().equals(lanEn)) {
                return (String) entry.getKey();
            }
        }
        return "其他";
    }

    public static String getCityEn(String cityZh) {
        try {
            return city.get(cityZh).toString();
        } catch (Exception e) {
        }
        return null;
    }

    public static String getCityZh(String cityEn) {
        for (Entry<Object, Object> entry : city.entrySet()) {
            if (entry.getKey().toString().matches("\\w*"))
                continue;
            if (entry.getValue().equals(cityEn)) {
                return (String) entry.getKey();
            }
        }
        return null;
    }

    public static int getMediaType(String mediaTName) {
        for (Entry<Integer, String> entry : mediaTMap.entrySet()) {
            if (entry.getValue().equals(mediaTName)) {
                return entry.getKey();
            }
        }
        return 999;
    }

    public static String getMediaTName(String mediaTNum) {
        return mediaTMap.get(Integer.parseInt(mediaTNum));
    }

    public static String getMediaTName(int mediaTNum) {
        return mediaTMap.get(mediaTNum);
    }

    public static void main(String[] args) {
        // System.out.println(DicMap.getLanguageZh("en"));
        // System.out.println(DicMap.getLanguageZh("en"));
        System.out.println(DicMap.getCountryZh("Iran, Islamic Republic of"));
    }
}
