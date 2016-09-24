package edu.buaa.nlp.es.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import net.sf.json.JSONObject;

public class FileUtil {

	public static String getRootPath() {
		return System.getProperty("user.dir");
	}

	public static String getPath(String folder) {
		return getRootPath() + File.separatorChar + folder;
	}
	
	public static boolean createFile(String dir, String filename, StringBuffer sb){
		File directory=new File(dir);
		if(!directory.exists()) directory.mkdirs();
		File file=new File(dir+File.separator+filename);
		BufferedWriter bw=null;
		try {
			bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
			bw.write(sb.toString());
			bw.flush();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	/**
	 * 加载属性文件
	 * 
	 * @param file
	 * @return
	 */
	public static Properties getPropertyFile(String file) {
		InputStream is = null;
		Properties prop = new Properties();
		try {
			is = new FileInputStream(file);
			prop.load(is);
		} catch (FileNotFoundException e) {
			// e.printStackTrace();
			System.out.println("使用Classloader查找文件");
			return useClassLoader(file);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return prop;
	}

	/**
	 * 使用classloader加载属性文件
	 * @param filename
	 * @return
	 */
	public static Properties useClassLoader(String filename) {
		InputStream is = FileUtil.class.getClassLoader().getResourceAsStream(
				filename);
		Properties props = new Properties();
		try {
			props.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(is!=null){
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return props;
	}

	/**
	 * 读json文件
	 * @param filename json文件
	 * @param charsetName
	 * @return json对象
	 */
	public static JSONObject readJson(String filename, String charsetName){
		File jsonFile=new File(filename);
		if(!jsonFile.exists()) return null;
		BufferedReader br=null;
		StringBuffer sb=new StringBuffer();
		try {
			br=new BufferedReader(new InputStreamReader(new FileInputStream(jsonFile), charsetName));
			String line="";
			while((line=br.readLine())!=null){
				sb.append(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(br!=null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return JSONObject.fromObject(sb.toString());
	}
	
	public static void main(String[] args) {
		System.out.println(readJson("default-mapping.json", "utf-8").getJSONObject("mappings").toString());
	}
}
