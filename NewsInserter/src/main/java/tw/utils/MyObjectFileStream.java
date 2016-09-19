package tw.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyObjectFileStream {
	public static void saveToFile (String filePath , Object obj){
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath));
			oos.writeObject(obj);
			oos.flush();
			oos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static Object getObjFromFile(String filePath ) throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath));
		Object obj = ois.readObject();
		return obj;
	}
	public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException {
		Map<String ,String> mediaMap, sourceMap,languageMap ,errorMap;
		
		mediaMap = new HashMap<String, String>();
		mediaMap.put("新闻", "1");
		mediaMap.put("Facebook", "10");
		mediaMap.put("论坛", "11");
		mediaMap.put("QQ", "12");
		mediaMap.put("微信", "13");
		mediaMap.put("社交", "2");
		mediaMap.put("视频", "3");
		mediaMap.put("数据库", "4");
		mediaMap.put("电商", "5");
		mediaMap.put("平媒", "6");
		mediaMap.put("问答", "7");
		mediaMap.put("博客", "8");
		mediaMap.put("Twitter", "9");
		mediaMap.put("其他", "999");
		mediaMap.put("微博", "14");
		mediaMap.put("全部", "998");

		languageMap = new HashMap<String, String>();
		languageMap.put("中文", "1");
		languageMap.put("英语", "2");
		languageMap.put("日语", "3");
		languageMap.put("韩语", "4");
		languageMap.put("土耳其语", "5");
		languageMap.put("俄语", "6");
		languageMap.put("法语", "7");
		languageMap.put("德语", "8");
		languageMap.put("西班牙语", "9");
		languageMap.put("意大利语", "10");
		languageMap.put("葡萄牙语", "11");
		languageMap.put("马来语", "12");
		languageMap.put("阿拉伯语", "13");
		languageMap.put("印地语", "14");
		languageMap.put("波兰语", "15");
		languageMap.put("繁体中文", "16");
		languageMap.put("菲律宾语", "17");
		languageMap.put("泰语", "18");
		languageMap.put("越南语", "19");
		languageMap.put("希伯来语", "20");
		languageMap.put("希腊语", "21");
		languageMap.put("荷兰语", "22");
		languageMap.put("芬兰语", "23");
		languageMap.put("挪威语", "24");
		languageMap.put("马拉雅拉姆语", "25");
		languageMap.put("乌兹别克斯坦语", "26");
		languageMap.put("波斯语", "27");
		languageMap.put("格鲁吉亚语", "28");
		languageMap.put("瑞典语", "29");
		languageMap.put("爱沙尼亚语", "30");
		languageMap.put("阿尔巴尼亚语", "31");
		languageMap.put("僧伽罗语", "32");
		languageMap.put("巴斯克语", "33");
		languageMap.put("蒙古语", "34");
		languageMap.put("罗马尼亚语", "35");
		languageMap.put("捷克语", "36");
		languageMap.put("丹麦语", "37");
		languageMap.put("亚美尼亚语", "38");
		languageMap.put("匈牙利", "39");
		languageMap.put("伊布语", "40");
		languageMap.put("其他", "999");
		languageMap.put("全部", "998");

		sourceMap = new HashMap<String, String>();
		sourceMap.put("HongMai", "1");
		sourceMap.put("CisionTxt", "2");
		sourceMap.put("CisionHttp", "3");
		sourceMap.put("CisionBlog", "4");
		sourceMap.put("Rank", "5");
		sourceMap.put("BeiHang", "6");
		sourceMap.put("CisionSocial", "7");
		sourceMap.put("Goonie", "8");
		sourceMap.put("Sina", "9");
		sourceMap.put("Hailiang", "10");
		
		errorMap = new HashMap<>();
		errorMap.put("已导入","1");
		errorMap.put("无问题","2");
		errorMap.put("缺少网站名称","3");
		errorMap.put("缺少语言字段","4");
		errorMap.put("缺少正文换行标签","5");
		errorMap.put("缺少国家字段数据","6");
		List<Map> maps = new ArrayList<>();
		maps.add(mediaMap);
		maps.add(languageMap);
		maps.add(sourceMap);
		maps.add(errorMap);
		
		MyObjectFileStream.saveToFile("E:/map.dat", maps);
		
		Object object = MyObjectFileStream.getObjFromFile("E:/map.dat");
		List<Map> maps2 = (List) object;
		
	}
}
