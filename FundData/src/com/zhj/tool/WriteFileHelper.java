package com.zhj.tool;

import java.io.FileWriter;
import java.io.IOException;

/**
 * 文件帮助类，可以帮助使用者快速的记录数据，日志。
 * 
 * @author zhj 2015-10-17
 *
 */
public class WriteFileHelper {
	TimeHelper mTimeHelper = new TimeHelper();
	String fileName = mTimeHelper.getToday("yyyyMMddHHmmss");

	/**
	 * 写入日志
	 * 
	 * @param content
	 *            日志内容
	 */
	public void writeLog(String content) {
		System.out.println(content);
		writer("log/log" + fileName + ".log",
				"[" + mTimeHelper.getCurrentDateTime() + "] " + content + "\n",
				true);
	}

	/**
	 * 写入数据到文件
	 * 
	 * @param fileName
	 *            文件名
	 * @param content
	 *            内容
	 * @param append
	 *            是否追加
	 */
	public void writer(String fileName, String content, boolean append) {
		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(fileName, append);
			fileWriter.write(content);
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}
	}

	public static void main(String[] args) {
		WriteFileHelper mWriteFile = new WriteFileHelper();
		mWriteFile.writeLog("hello");
	}
}
