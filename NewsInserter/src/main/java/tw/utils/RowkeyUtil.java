package tw.utils;

public class RowkeyUtil {

    public static String rowkey(String key) {

        String rowkey;
        String str = String.valueOf((int) (System.currentTimeMillis() % 100));
        if (str.length() == 2)
            rowkey = str;
        else if (str.length() == 1)
            rowkey = str + "0";
        else {
            rowkey = "00";
        }
        // DateFormat formatB = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        // rowkey += formatB.format(new Date());
        rowkey += String.valueOf(System.currentTimeMillis());
        rowkey += Murmurs.hashStr(key);

        return rowkey;
    }

    public static void main(String[] args) {

        String s1 = "中国";
        String s2 = "哈哈哈,今天吃得好饱,现在好饿呀，救命呀，呜呜uuwuwuuwwuuwu";

        System.out.println(rowkey(s1));
        System.out.println(rowkey(s2));
        System.out.println(rowkey(s1).length() + ", " + rowkey(s2).length());
    }
}
