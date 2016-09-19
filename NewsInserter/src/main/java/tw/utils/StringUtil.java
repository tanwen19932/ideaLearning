package tw.utils;

public class StringUtil {

	/**
	 * @param str
	 *
	 * @return 是否为空或者为"null"
	 */
	public static boolean isNull(String str) {
		return str == null || str.toLowerCase().trim().equals("null");
	}

	/**
	 * @param str
	 *
	 * @return 是否为空或者为"null"
	 */
    public static boolean isAllNull(String str, String str1) {
        return isNull(str)&&isNull(str1);
    }
    public static boolean isAllNull(String str, String str1 ,String str2) {
        return isNull(str)&&isNull(str1)&&isNull(str2);
    }
    public static boolean isAllNull(String str, String str1 ,String str2, String... strs) {
        boolean result = isNull(str)&&isNull(str1)&&isNull(str2);
        if (result == false)
            return false;
        for (String string : strs) {
            result = result || isNull(string);
            if (result == false)
                return false;
        }
        return true;
    }


    public static boolean isOneNull(String str, String str1){
        return isNull(str)||isNull(str1);
    }
    public static boolean isOneNull(String str, String str1 ,String str2){
        return isNull(str)||isNull(str1)||isNull(str2);
    }
	public static boolean isOneNull(String str,  String str1 ,String str2,String... strs) {
		boolean result = isNull(str)||isNull(str1)||isNull(str2);
        if (result == true)
            return true;
		for (String string : strs) {
			result = result || isNull(string);
			if (result == true)
				return true;
		}
		return result;
	}

	public static boolean isEmpty(String str) {
		return isNull(str)||str.equals("");
	}

	public static void main(String[] args) {
		System.out.println(isAllNull("", "", null));
	}
}
