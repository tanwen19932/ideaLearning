package edu.buaa.nlp.es.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateUtil {

	private static DateFormat formatA = new SimpleDateFormat("yyyy-MM-dd");
	private static DateFormat formatB = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * 返回任意格式的时间字符串
	 * @param strformat
	 * @param date
	 * @return
	 */
	public static String getForDate(String strformat, Date date) {
		SimpleDateFormat df = new SimpleDateFormat(strformat);// 根据字串格式
		return df.format(date);
	}
	
	
    public static String formatDate(Date date, String format){
    	if(date==null || "".equals(date)) return "";
    	 SimpleDateFormat sdf = new SimpleDateFormat(format);
    	 return sdf.format(date);
    }
    
    public static long getTimeLong(Date date){
    	String str=formatDate(date, "yyyyMMddHHmmss");
    	if(str==null || "".equals(str)) return 0;
    	return Long.valueOf(str);
    }
	
	public static Date getDate(String strformat, String timeStr) {
		SimpleDateFormat df = new SimpleDateFormat(strformat);// 根据字串格式
		try {
			return df.parse(timeStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Date(timeStr);
		}
	}
	
	public static String unix2Time(Date date){
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sd.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String strDate = sd.format(date);
        return strDate;
	}
	
	public static String unix2Time(long timestamp){
//		timestamp*=1000;
        String date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(timestamp));
		return date;
	}
	
	public static long time2Unix(String  date){
		long epoch = 0;
		try {
			epoch = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date).getTime() ; // / 1000;
		} catch (ParseException e) {
			//e.printStackTrace();
		}
		return epoch;
	}
	
	public static long timeShort2Unix(String  date){
		long epoch = 0;
		try {
			epoch = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(date).getTime() ; // / 1000;
		} catch (ParseException e) {
			//e.printStackTrace();
		}
		return epoch;
	}
	
	/**
	 * 返回8位long型时间戳
	 * @param time
	 * @return
	 */
	public static long getLongTimestamp8(Timestamp time){
		String str=getForDate("yyyyMMdd", time);
		return Long.parseLong(str);
	}
	
	/**
	 * 返回16位long型时间戳
	 * @param time
	 * @return
	 */
	public static long getLongTimestamp16(Timestamp time){
		String str=getForDate("yyyyMMddHHmmss", time);
		return Long.parseLong(str);
	}
	/**
	 * 返回8位时间戳
	 * @param time
	 * @return
	 */
	public static Timestamp getLongTimestamp8(String time){
		return new Timestamp(getDateYYYYMMDD(str2Format8(time)).getTime());
	}
	
	/**
	 * 返回16位时间戳
	 * @param time
	 * @return
	 */
	public static Timestamp getLongTimestamp16(String time){
		Date date = null;
		try {
			date = formatB.parse(str2Format16(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Timestamp(date.getTime());
	}
	
	public static Date getDateYYYYMMDD(String timeStr) {
		Date date = null;
		try {
			date = formatA.parse(timeStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	private static String str2Format8(String time){
		StringBuilder sb=new StringBuilder(time);
		sb.insert(4, '-');
		sb.insert(7, '-');
		sb.insert(10, '-');
		return sb.toString();
	}
	
	private static String str2Format16(String time){
		StringBuilder sb=new StringBuilder(time);
		sb.insert(4, '-');
		sb.insert(7, '-');
		sb.insert(10, ' ');
		sb.insert(13, ':');
		sb.insert(16, ':');
		return sb.toString();
	}
	
	public static String getDate8(String time){
		if(time==null || "".equals(time) || time.length()<8) return null;
		return time.substring(0, 8);
	}
	
	public static String getToday8(){
		DateFormat format=new SimpleDateFormat("yyyyMMdd");
		return format.format(new Date());
	}
	
	/**
	 * 判断所给日期是否是今天
	 * @param str String类型格式为yyyyMMdd的日期串
	 * @return
	 */
	public static boolean isToday(String str){
		Timestamp today=getLongTimestamp8(getToday8());
		Timestamp date=getLongTimestamp8(str);
		return today.equals(date);
	}
	/**
	 * 判断所给日期是否在本周内
	 * @param str String类型格式为yyyyMMdd的日期串
	 * @return
	 */
	public static boolean isInWeek(String str){
		Calendar calendar=Calendar.getInstance(TimeZone.getTimeZone("China/Beijing"));
		Timestamp date=getLongTimestamp8(str);
		Timestamp today=getLongTimestamp8(getToday8());
		calendar.setTime(today);
		int dow=calendar.get(Calendar.DAY_OF_WEEK);
		int sunGap=7-dow;
		Timestamp sunday=getDayBefore(today, sunGap);
		dow=dow*-1+1;
		Timestamp monday=getDayBefore(today, dow);
		if(date.compareTo(monday)>-1 && date.compareTo(sunday)<1) return true;
		return false;
	}
	
	/**
	 * 判断所给日期是否在本月内
	 * @param str String类型格式为yyyyMMdd的日期串
	 * @return
	 */
	public static boolean isInMonth(String str){
		Calendar calendar=Calendar.getInstance(TimeZone.getTimeZone("China/Beijing"));
		Timestamp date=getLongTimestamp8(str);
		Timestamp today=getLongTimestamp8(getToday8());
		calendar.setTime(today);
		int dow=calendar.get(Calendar.DAY_OF_MONTH)+1;
		int total=calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		int gap=total-dow;
		Timestamp last=getDayBefore(today, gap);
		dow=-1*dow+1;
		Timestamp first=getDayBefore(today, dow);
		if(date.compareTo(first)>-1 && date.compareTo(last)<1) return true;
		return false;
	}
	
	/**
	 * 判断所给日期是否在今天之前的days天数内，包括今天
	 * @param str String类型格式为yyyyMMdd的日期串
	 * @param days
	 * @return
	 */
	public static boolean isBeforeDays(String str, int days){
		Timestamp today=getLongTimestamp8(getToday8());
		Timestamp before=getDayBefore(today, (days-1)*-1);
		Timestamp date=getLongTimestamp8(str);
		if(date.compareTo(before)>-1 && date.compareTo(today)<1) return true;
		return false;
	}
	
	public static Timestamp getDayBefore(Timestamp init, int n){
		long day=86400000;
		long time=init.getTime()+day*n;
		if(time<0) return null;
		return new Timestamp(time);
	}
	
	public static String nextDay(String date){
		Calendar calendar=Calendar.getInstance();
		DateFormat format = new SimpleDateFormat("yyyyMMdd");
		try {
			calendar.setTime(format.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		return getForDate("MMdd", calendar.getTime());
	}
	
	public static String prevDay(String date){
		Calendar calendar=Calendar.getInstance();
		DateFormat format = new SimpleDateFormat("yyyyMMdd");
		try {
			calendar.setTime(format.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		return getForDate("MMdd", calendar.getTime());
	}
	
	public static String nextYear(String date){
		Calendar calendar=Calendar.getInstance();
		DateFormat format = new SimpleDateFormat("yyyyMMdd");
		try {
			calendar.setTime(format.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		return getForDate("yyyy", calendar.getTime());
	}
	
	public static String prevYear(String date){
		Calendar calendar=Calendar.getInstance();
		DateFormat format = new SimpleDateFormat("yyyyMMdd");
		try {
			calendar.setTime(format.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		return getForDate("yyyy", calendar.getTime());
	}
	
	public static Date addGapTime(Date startTime,Date gapTime)
	{
		Calendar timerCalender=Calendar.getInstance();
		timerCalender.setTime(gapTime);
		GregorianCalendar start = new GregorianCalendar();
		start.setTime(startTime);
		start.add(GregorianCalendar.HOUR_OF_DAY,timerCalender.get(Calendar.HOUR_OF_DAY));
		start.add(GregorianCalendar.MINUTE,timerCalender.get(Calendar.MINUTE));
		start.add(GregorianCalendar.SECOND,timerCalender.get(Calendar.SECOND));
		start.add(GregorianCalendar.MILLISECOND,timerCalender.get(Calendar.MILLISECOND));
		return start.getTime();
	}
	
	/***
	 * 增加一秒
	 * @param startTime
	 * @return
	 */
	public static Date addOneSecond(Date startTime)
	{
		GregorianCalendar start = new GregorianCalendar();
		start.setTime(startTime);
		start.add(GregorianCalendar.SECOND,1);
		return start.getTime();
	}
	/***
	 * 减少一秒
	 * @param startTime
	 * @return
	 */
	public static Date delOneSecond(Date startTime)
	{
		GregorianCalendar start = new GregorianCalendar();
		start.setTime(startTime);
		start.add(GregorianCalendar.SECOND,-1);
		return start.getTime();
	}
	/***
	 * 增加一毫秒
	 * @param startTime
	 * @return
	 */
	public static Date addOneMillSecond(Date startTime)
	{
		GregorianCalendar start = new GregorianCalendar();
		start.setTime(startTime);
		start.add(GregorianCalendar.MILLISECOND,1);
		return start.getTime();
	}
	
	/***
	 * 减少一毫秒
	 * @param startTime
	 * @return
	 */
	public static Date delOneMillSecond(Date startTime)
	{
		GregorianCalendar start = new GregorianCalendar();
		start.setTime(startTime);
		start.add(GregorianCalendar.MILLISECOND,-1);
		return start.getTime();
	}
	
	
	public static Date delGapTime(Date endTime,Date gapTime)
	{
		Calendar timerCalender=Calendar.getInstance();
		timerCalender.setTime(gapTime);
		GregorianCalendar end = new GregorianCalendar();
		end.setTime(endTime);
		end.add(GregorianCalendar.HOUR_OF_DAY,- timerCalender.get(Calendar.HOUR_OF_DAY));
		end.add(GregorianCalendar.MINUTE,- timerCalender.get(Calendar.MINUTE));
		end.add(GregorianCalendar.SECOND,- timerCalender.get(Calendar.SECOND));
		return end.getTime();
	}
	public static void main(String[] args) {
//		System.out.println(getLongTimestamp16("20140509160412"));
//		System.out.println(getLongTimestamp16(getLongTimestamp16("20140509164312")));
//		System.out.println(getDate8("2014"));
//		System.out.println(isInWeek("20140517"));
//		System.out.println(isInMonth("20110405"));
//		System.out.println(isToday("20140517"));
//		System.out.println(isBeforeDays("20140828", 30));
		/*System.out.println(unix2Time(new Date(1448909928*1000)));
		long time=new Timestamp(System.currentTimeMillis()).getTime()/1000;
		System.out.println(time);
		System.out.println(new Date(1448909928*1000));
		System.out.println(unix2Time(1444536721));
		*/
		System.out.println(time2Unix("2016-05-17 18:26:21"));
		
		System.out.println(nextDay("20160106"));
		System.out.println(nextYear("20161231"));
		System.out.println(prevDay("20160101"));
		//4914634380491497A03673FA3D09DF6
		System.out.println(unix2Time(1463438049)); //1463650050
		System.out.println("hello\r\nworld".replaceAll("[\r\n]", " "));
		
		
/*		System.out.println(DateUtil.getDateYYYYMMDD("2010-1-1").getTime());
		
		System.out.println(time2Unix("2010-1-1 00:00:00"));
		
		//		2016-04-01 05:53:57
		System.out.println(DateUtil.getDateYYYYMMDD("2016-04-01").getTime() < DateUtil.getDateYYYYMMDD("2010-1-1").getTime());
*/
		
	}
}
