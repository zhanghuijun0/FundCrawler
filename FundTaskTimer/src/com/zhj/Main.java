package com.zhj;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 此类是配合FundDataDemo项目一起使用，该项目生成的jar文件要存放在与FundDataDemo项目生成的jar的相同目录下，以便每天调用
 * 
 * @author zhj
 *
 */
public class Main {
	private String command = "nohup java -jar fund.jar &";// Command命令
	private CreateFolderHelper mFolderHelper = new CreateFolderHelper();
	private String mLogPath = "log/command.log";// 定时任务的命令执行文件
	private String format = "yyyy-MM-dd HH:mm:ss";

	private void execTimer() {
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				 System.out.println("hello:" + getToday(format));
//				execCommand(command);// 定时任务为Command命令
			}
		};
		// 设置执行时间
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		calendar.set(year, month, day, 17, 53, 00);// 定制每天的8:8:8执行
		Date firstTime = calendar.getTime();
		Timer timer = new Timer();
//		int period = 24 * 60 * 60 * 1000;
		int period =  3*60*1000;
		timer.schedule(task, firstTime, period);// 每天的date时刻执行task，每隔1天重复执行
		mFolderHelper.createFile(mLogPath);// 创建日志文件
		writer(mLogPath, "首次执行时间:" + getToday(format) + "\n", true);
	}

	private boolean execCommand(String cmd) {
		writer(mLogPath, "[" + getToday(format) + "]" + cmd + "\n", true);
		Process process = null;
		BufferedReader br = null;
		try {
			process = Runtime.getRuntime().exec(cmd);
			br = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			String line = null;
			while ((line = br.readLine()) != null) {
				writer(mLogPath, line + "\n", true);
			}
		} catch (Exception e) {
			return false;
		} finally {
			try {
				process.destroy();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	/**
	 * 今天
	 * 
	 * @param format
	 *            日期的格式
	 * @return
	 */
	private String getToday(String format) {
		Calendar calendar = Calendar.getInstance();
		return new SimpleDateFormat(format).format(calendar.getTime());
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
	private void writer(String fileName, String content, boolean append) {
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
		Main mm = new Main();
		mm.execTimer();
	}
}
