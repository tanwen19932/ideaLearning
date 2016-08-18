package tw.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DateUtil {
    static final List<String> regexList = new ArrayList<>();

    // By PA Aug 24, 2015
    static {
        regexList.add("yyyy-MM-dd HH:mm:ss");
        regexList.add("MM\\.dd");
        regexList.add("yyyy-MM-dd HH:mm");
        regexList.add("yyyy-MM-dd");
        regexList.add("MMM dd, yyyy");
        regexList.add("于yyyy年MM月dd日");
        regexList.add("yyyy年MM月dd日");
        regexList.add("MMM dd, yyyy");
        regexList.add("MMM dd,HH:mm:ss");
        // Tue, 04/26/2016 - 07:23
        regexList.add("EEEE, MM/dd/yyyy - HH:mm");
        // 2016-04-14T12:52:17Z
        regexList.add("yyyy-MM-ddHH:mm:ss");
    }

    public static String tryParse(String date) {

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (int i = 0; i < regexList.size(); i++) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(regexList.get(i), Locale.ENGLISH);
                return sdf2.format(sdf.parse(date));
            } catch (Exception e) {
            }
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(tryParse("于 2015年12月11日".trim()));
    }
}
