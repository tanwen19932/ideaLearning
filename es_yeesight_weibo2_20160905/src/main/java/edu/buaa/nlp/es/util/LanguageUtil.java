package edu.buaa.nlp.es.util;

public class LanguageUtil {

	public static boolean isChinese(String content){
		if(content==null || "".equals(content)) return false;
		int sum=0;
		int len=content.length();
		for(int i=0; i<=len/2; i++){
			String s=content.charAt(i)+"";
			if(s.matches("[\u4E00-\u9FA5]"))
				sum++;
		}
		return (sum/(len*0.5))>0.2?true:false;
	}
	
	public static boolean isEnglish(String content){
		if(content==null || "".equals(content)) return false;
		int sum=0;
		int len=content.length()>>1;
		for(int i=0; i<=len; i++){
			String s=content.charAt(i)+"";
			if(s.matches("[\\w]")) sum++;
		}
		return (sum/(len*0.5))>0.2?true:false;
	}
	
	private static final boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
			|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}
	
	public static void main(String[] args) {
		System.out.println(isChinese("会社員"));
		System.out.println(isChinese('中'));
		System.out.println(isEnglish("会社員"));
	}
}
