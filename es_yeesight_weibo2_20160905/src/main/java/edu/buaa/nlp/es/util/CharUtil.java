package edu.buaa.nlp.es.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CharUtil {
	/**
	 * 对中英文混合情况汉字汉字之间空格，对英文不处理，对中按字切分
	 * @param input
	 * @return
	 */
	public static String addSpaceInChinese(String input){
		if(input == null){
			return "";
		}
		Pattern pattern = Pattern.compile("(?=[\u4E00-\u9FA5])(?=[\u4E00-\u9FA5])");
		Matcher matcher = pattern.matcher(input);
		if(matcher.find()){ 
			input = matcher.replaceAll(" ");
		}
		input = input.replaceAll("\\s{2,}", " ").trim();
		return input;
	}
	
	
	/**
	 *  对中英文混合情况去除汉字之间空格，对英文不处理
	 * @param input
	 * @return
	 */
	public static String removeSpaceInChinese(String input){
		if(input == null || "".equals(input)){
			return "";
		}
		Pattern pattern = Pattern.compile(" (?=[\u4E00-\u9FA5])");
		Matcher matcher = pattern.matcher(input);
		if(matcher.find()){ 
			input = matcher.replaceAll("");
		}
		return input;
	}
	
	/**
	 * 删除输入中的非英文、非中文字符、非数字字符
	 * @param input
	 */
	public static String removeUnChar(String input)
	{
		if(input == null || "".equals(input)){
			return "";
		}
		Pattern pattern = Pattern.compile("[^a-z0-9\u4E00-\u9FA5]",Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(input);
		if(matcher.find()){ 
			input = matcher.replaceAll("");
		}
		return input;
	}
	
	/**
     * 半角转全角
     * @param input String.
     * @return 全角字符串.
     */
    public static String ToSBC(String input) {
             char c[] = input.toCharArray();
             for (int i = 0; i < c.length; i++) {
               if (c[i] == ' ') {
                 c[i] = '\u3000';
               } else if (c[i] < '\177') {
                 c[i] = (char) (c[i] + 65248);

               }
             }
             return new String(c);
    }

    /**
     * 全角转半角
     * @param input String.
     * @return 半角字符串
     */
    public static String ToDBC(String input) {
        

             char c[] = input.toCharArray();
             for (int i = 0; i < c.length; i++) {
               if (c[i] == '\u3000') {
                 c[i] = ' ';
               } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                 c[i] = (char) (c[i] - 65248);

               }
             }
        String returnString = new String(c);
        
             return returnString;
    }	

	
	
	public static void main(String[] args) {
		//String text="Apple and the FBI Break the Fourth Wall In their ongoing clash, the two notoriously secret organizations are fighting for Americans hearts and minds. There ’ s a recurring moment in political debates when a candidate, eager to speak directly to the American public, shifts his gaze from the moderators and opponents and talks straight into the camera. The move is a little jarring — especially when Chris Christie deploys his unblinking stare — but it ’ s effective: It says, “ Forget these other candidates; I ’ m talking to you, voter. ” In their very public fight over device security, Apple and the FBI seem to employing this same tactic. Instead of mining the tech industry or outside experts for support, the two organizations, both known for their secrecy, are breaking the fourth ";
		//System.out.println(removeSpaceInChinese(text));
		System.out.println(removeUnChar("法\\*功"));
		
		System.out.println(ToDBC("fffｆａｌｕｎｄａｆａ哈哈"));
		
	}
}
