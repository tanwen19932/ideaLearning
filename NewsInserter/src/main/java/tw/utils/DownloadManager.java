package tw.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadManager {
	static final long unitSize = 100 * 1024;// 分配给每个下载线程的字节数
	private static final  Logger LOG = LoggerFactory.getLogger(DownloadManager.class);
	/**
	 * @param args
	 *            2012-10-8 void
	 * @throws IOException
	 */
	public static void downloadBy1Thread(String remoteFileUrl,String localFileName)
			throws IOException {
		long result = 0;
		long readAlready = 0;
		try {
			HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(remoteFileUrl).openConnection();
			HttpUtil.config(httpURLConnection);
			httpURLConnection.setRequestMethod("GET");
			result = httpURLConnection.getContentLength();
			System.out.println(localFileName + " ResponseCode " + httpURLConnection.getResponseCode() + "文件大小 ：" +result);
			InputStream  is=httpURLConnection.getInputStream();
			FileOutputStream fos = new FileOutputStream(localFileName);
			byte[] buff = new byte[10*1024];
			int length;
			long time = System.currentTimeMillis();
			while ( (length=is.read(buff)) != -1){
				readAlready+= length;
				if(readAlready%(1024*100)==0){
					System.out.println(localFileName + "+++++++已经下载" +readAlready/1024 +"K" + "花费时间"+(System.currentTimeMillis()-time)/1000);
				}
				fos.write(buff , 0 , length);
			}
			System.out.println("下载完成" + "花费时间"+(System.currentTimeMillis()-time)/1000);
			fos.flush();
			fos.close();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void doDownload(String remoteFileUrl, String localFileName) throws IOException {
		long fileSize = getRemoteFileSize(remoteFileUrl);
		createFile(localFileName, fileSize);
		long threadCount = fileSize / unitSize;
		System.out.println("共启动线程" + (fileSize % unitSize == 0 ? threadCount : threadCount + 1) + "个");
		long offset = 0;
		if (fileSize <= unitSize) {// 如果远程文件尺寸小于等于unitSize
			DownloadThread downloadThread = new DownloadThread(remoteFileUrl, localFileName, offset, fileSize);
			downloadThread.start();
		} else {// 如果远程文件尺寸大于unitsize
			
			for (int i = 1; i <= threadCount; i++) {
				DownloadThread downloadThread = new DownloadThread(remoteFileUrl, localFileName, offset, unitSize);
				downloadThread.start();
				offset = offset + unitSize;
			}
			if (fileSize % unitSize != 0)// 如果不能整除，则需要再创建一个线程下载剩余字节
			{
				DownloadThread downloadThread = new DownloadThread(remoteFileUrl, localFileName, offset,
						fileSize - unitSize * threadCount);
				downloadThread.start();
			}
		}
	}

	// 获取远程文件的大小
	private static long getRemoteFileSize(String remoteFileUrl) throws IOException {

		long result = 0;
		try {
			HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(remoteFileUrl).openConnection();
			HttpUtil.config(httpURLConnection);
			httpURLConnection.setRequestMethod("GET");
			result = httpURLConnection.getContentLength();
			System.out.println(" ResponseCode " + httpURLConnection.getResponseCode() + "文件大小 ：" +result);
			InputStream  is=httpURLConnection.getInputStream();
			
			byte[] buff = new byte[100*1024];
			int length;
			while ( (length=is.read(buff)) != -1){
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// 创建指定大小的文件
	private static void createFile(String fileName, long fileSize) throws IOException {
		File newFile = new File(fileName);
		RandomAccessFile raf = new RandomAccessFile(newFile, "rw");
		raf.setLength(fileSize);
		raf.close();
	}
	
	private static void getByWget(String task) throws IOException, InterruptedException  {
		String command = "wget "  + task;
		Process pro = Runtime.getRuntime().exec(command);
		LOG.info("执行命令..." + command);
		LOG.info("正在下载..." + task);
		pro.waitFor();
		LOG.info("下载成功..." + task);
	}
	public static void main(String[] args) throws ParseException {
		String filePath = "";
        String url = "http://195.23.58.154/diogo/QuaForce/08/";
        Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_hhmm");
		calendar.setTime(dateFormat.parse("2016_08_01_0000"));
		List<String> indexs = new LinkedList<>();
		while (true) {
//			System.out.println(dateFormat.format(calendar.getTime()));
			indexs.add( dateFormat.format(calendar.getTime())+".zip" );
			calendar.add(Calendar.MINUTE,1);
			if (dateFormat.format(calendar.getTime()).startsWith("2016_09_01")){
				break;
			}
		}
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for(String index : indexs){
        	System.out.println(index);
        	 executorService.submit( new Runnable() {
				public void run() {
					File file = new File(index);
					if(file.exists()){
						System.out.println("已经存在~~~     " +index);
						return;
					}
					else{
						try {
							System.out.println("开始下载~~~~     " + url+index);
							getByWget(url+index);
//							downloadBy1Thread(
//	//		     			        "http://www.pc6.com/softview/SoftView_25196.html#download",
//							        url+index,
//							        filePath+index);
						} catch (IOException | InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			});
        }
	}
	
}
