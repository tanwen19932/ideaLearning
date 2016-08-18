package tw.utils;

public class StringUtil {

    /**
     * @param str
     * @return 是否为空或者为"null"
     */
    public static boolean isNull(String str) {
        return str == null || str.toLowerCase().trim().equals("null");
    }

    /**
     * @param str
     * @return 是否为空或者为"null"
     */
    public static boolean isAllNull(String str, String... strs) {
        boolean result = isNull(str);
        for (String string : strs) {
            result = result && isNull(string);
            if (result == false)
                return false;
        }
        return result;
    }

    public static boolean isOneNull(String str, String... strs) {
        boolean result = isNull(str);
        for (String string : strs) {
            result = result || isNull(string);
            if (result == true)
                return true;
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(isAllNull("", "", null));
    }
}
