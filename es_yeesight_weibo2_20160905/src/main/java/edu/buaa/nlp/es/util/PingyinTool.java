/**
 * 
 */
package edu.buaa.nlp.es.util;


import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;




/**
 *
 * Created by zengxm on 2014/12/4.
 */
public class PingyinTool {
    HanyuPinyinOutputFormat format = null;
    public static enum Type {
        UPPERCASE,              //ȫ����д
        LOWERCASE,              //ȫ��Сд
        FIRSTUPPER,              //����ĸ��д
        ONLYFIRST				//ֻ��ʾ����ĸ
    }

    public PingyinTool(){
        format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    }

    public String toPinYin(String str) throws BadHanyuPinyinOutputFormatCombination{
        return toPinYin(str, "", Type.UPPERCASE);
    }



    /**
     * ��strת����ƴ����������Ǻ��ֻ���û�ж�Ӧ��ƴ��������ת��
     * �磺 ���� ת���� MINGTIAN
     * @param str
     * @param spera
     * @return
     * @throws BadHanyuPinyinOutputFormatCombination
     */
    public String toPinYin(String str, String spera, Type type) throws BadHanyuPinyinOutputFormatCombination {
        if(str == null || str.trim().length()==0)
            return "";
        if(type == Type.UPPERCASE)
            format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        else
            format.setCaseType(HanyuPinyinCaseType.LOWERCASE);

        
        
        String py = "";
        String temp = "";
        String[] t;
        for(int i=0;i<str.length();i++){
            char c = str.charAt(i);
            if((int)c <= 128)
                py += c;
            else{
                t = PinyinHelper.toHanyuPinyinStringArray(c, format);
                if(t == null)
                    py += c;
                else{
                    temp = t[0];
                    if(type == Type.FIRSTUPPER)
                        temp = t[0].toUpperCase().charAt(0)+temp.substring(1);
                    else if(type == Type.ONLYFIRST)
                    {
                    	temp =String.valueOf(t[0].charAt(0));
                    }
                    py += temp+(i==str.length()-1?"":spera);
                    py += " ";
                }
            }
        }
        return py.trim();
    }
    
    
    
	public static void main(String args[]) {
		try
		{
			PingyinTool tool = new PingyinTool();
			/*			System.out.println(tool.toPinYin("中国"));
			System.out.println(tool.toPinYin("中国","_"));
			System.out.println(tool.toPinYin("中国","_",PingyinTool.Type.UPPERCASE));
			System.out.println(tool.toPinYin("中国","", PingyinTool.Type.ONLYFIRST));*/
			
			
			
			System.out.println(tool.toPinYin("江 賊民","", PingyinTool.Type.FIRSTUPPER));
			System.out.println(tool.toPinYin("⒍4学潮","", PingyinTool.Type.FIRSTUPPER));
			
			
			
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
		}
	}
    
}