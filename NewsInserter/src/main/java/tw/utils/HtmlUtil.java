package tw.utils;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlUtil {
    private static Map<String, Object> domainMap = new TreeMap<>();

    static {
        domainMap.put("com", 0);
        domainMap.put("tel", 0);
        domainMap.put("mobi", 0);
        domainMap.put("net", 0);
        domainMap.put("org", 0);
        domainMap.put("asia", 0);
        domainMap.put("me", 0);
        domainMap.put("tv", 0);
        domainMap.put("biz", 0);
        domainMap.put("cc", 0);
        domainMap.put("name", 0);
        domainMap.put("info", 0);
        domainMap.put("gov", 0);
        domainMap.put("cn", 0);
        domainMap.put("co", 0);
        domainMap.put("edu", 0);
        domainMap.put("europa", 0);
    }

    public static String getDomain2(String url) {
        Pattern pattern = Pattern
                .compile("(?=.{3,255})[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+");
        Matcher matcher = pattern.matcher(url.replaceAll("\\u00A0*$", "").replaceAll("^\\u00A0*", "").trim());
        String url2 = null;
        if (matcher.find()) {
            url2 = matcher.group(0);
        } else
            return null;
        String domain = url2.replaceAll("^www.*?\\.", "");
        String[] domains = domain.split("\\.");
        for (int i = 1; i < domains.length; i++) {
            String temp = domains[i];
            if (check(temp)) {
                return new StringBuffer().append(domains[i - 1]).append(".").append(temp).toString();
            }
        }
        return domain;
    }

    public static String getDomain(String url) {
        Pattern pattern = Pattern
                .compile("(?=.{3,255})[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+");
        Matcher matcher = pattern.matcher(url.replaceAll("\\u00A0*$", "").replaceAll("^\\u00A0*", "").trim());
        String url2 = null;
        if (matcher.find()) {
            url2 = matcher.group(0);
        } else
            return null;
        String domain = url2.replaceAll("^www.*?\\.", "");
        String[] domains = domain.split("\\.");
        for (int i = 1; i < domains.length; i++) {
            String temp = domains[i];
            if (check(temp)) {
                return new StringBuffer().append(domains[i - 1]).append(".").append(temp).toString();
            }
        }
        return domain;
    }

    private static boolean check(String domain) {
        if (domainMap.get(domain) != null) {
            return true;
        } else
            return false;
    }

    public static void main(String[] args) {
        String url = "http://my.oschina.net/KingSirLee/blog/381726";
        System.out.println(getDomain(url));
    }
}
