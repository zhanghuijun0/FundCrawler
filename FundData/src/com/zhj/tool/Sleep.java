package com.zhj.tool;

/**
 * 休眠(帮助类)
 * 
 * @author zhj
 *
 */
public class Sleep {
	public static void main(String[] args) {
		System.out.println("===========");
		Sleep mSleep = new Sleep();
		mSleep.suspend(0, 0, 5);
		System.out.println("===========");
	}

	public void suspend(int hour, int minute, int seconds) {
		try {
			Thread.sleep((hour * 3600 + minute * 60 + seconds) * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
