package com.zhj.tool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 本类提供了一些现成的时间类，通过该类的一些方法，即可得到常用的时间。
 * 
 * @author zhj 2015-10-17
 *
 */
public class TimeHelper {
	private String mTimeFormat = "HH:mm:ss";
	private String mDateFormat = "yyyy-MM-dd";
	private String mDateTimeFormat = "yyyy-MM-dd HH:mm:ss";

	public String getToday() {
		return getToday(mDateFormat);
	}

	public String getYesterday() {
		return getYesterday(mDateFormat);
	}

	public String getTomorrow() {
		return getTomorrow(mDateFormat);
	}

	public String getCurrentDateTime() {
		return getToday(mDateTimeFormat);
	}

	public String getCurrentTime() {
		return getToday(mTimeFormat);
	}

	/**
	 * 今天
	 * 
	 * @param format
	 *            日期的格式
	 * @return
	 */
	public String getToday(String format) {
		Calendar calendar = Calendar.getInstance();
		return new SimpleDateFormat(format).format(calendar.getTime());
	}

	/**
	 * 昨天
	 * 
	 * @param format
	 *            日期的格式
	 * @return
	 */
	public String getYesterday(String format) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);
		return new SimpleDateFormat(format).format(calendar.getTime());
	}

	/**
	 * 明天
	 * 
	 * @param format
	 *            日期的格式
	 * @return
	 */
	public String getTomorrow(String format) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		return new SimpleDateFormat(format).format(calendar.getTime());
	}

	/**
	 * 求两个时间的时间差，时间格式：yyyy-MM-dd HH:mm:ss
	 * 
	 * @param begin
	 *            起始时间
	 * @param end
	 *            结束时间
	 * @return
	 */
	public String getBetweenTime(String begin, String end) {
		String result = "";
		SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			java.util.Date begin1 = dfs.parse(begin);
			java.util.Date end1 = dfs.parse(end);
			long between = (end1.getTime() - begin1.getTime()) / 1000;
			long day = between / (24 * 3600);
			long hour = between % (24 * 3600) / 3600;
			long minute = between % 3600 / 60;
			long second = between % 60;
			result = day + "天" + hour + "小时" + minute + "分" + second + "秒";
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Date方法不建议使用
	 * 
	 * @param format
	 *            日期的格式
	 * @return 当前时间
	 */
	private String getCurrentTime(String format) {
		return new SimpleDateFormat(format).format(new Date(System
				.currentTimeMillis()));
	}

	public static void main(String[] args) {
		TimeHelper mTimeHelper = new TimeHelper();
		System.out.println(mTimeHelper.getYesterday());
		System.out.println(mTimeHelper.getToday());
		System.out.println(mTimeHelper.getTomorrow());
		System.out.println(mTimeHelper.getCurrentDateTime());
		System.out.println(mTimeHelper.getCurrentTime());
	}

}
