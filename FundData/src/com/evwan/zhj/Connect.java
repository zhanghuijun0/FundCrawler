package com.evwan.zhj;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.zhj.tool.Sleep;

/**
 * 
 * @author zhj
 *
 */
public class Connect {
	private Sleep mSleep = new Sleep();

	public static void main(String[] args) {
		Connect mConnect = new Connect();
		System.out.println(mConnect.getDocument("https://www.google.com.hk/"));
	}

	public Document getDocument(String url) {
		int count = 0;
		Document doc = null;
		if (url == null || url.length() <= 0) {
			System.err.println("你输入的URL有误！");
			return null;
		}
		while (count < 100) {
			try {
				doc = Jsoup.connect(url).timeout(5000).ignoreContentType(true)
						.get();
				count = 0;
				break;
			} catch (IllegalArgumentException e) {
				System.err.println("你输入的URL有误！");
				break;
			} catch (UnknownHostException e) {
				System.out.println("找不到主机……");
				e.printStackTrace();
			} catch (SocketTimeoutException e) {
				System.out.println("第" + count + "次连接超时\n" + url + e);
				suspend(count % 8);// 暂时休眠
			} catch (Exception e) {
				System.out.println("第" + count + "次异常!\n" + url + e);
				suspend(count % 8);// 暂时休眠
			}
			count++;
		}
		return doc;
	}

	/**
	 * 休眠函数
	 * 
	 * @param count
	 */
	private void suspend(int count) {
		switch (count) {
		case 0:
			mSleep.suspend(0, 0, 2);
			break;
		case 1:
			mSleep.suspend(0, 0, 5);
			break;
		case 2:
			mSleep.suspend(0, 0, 15);
			break;
		case 3:
			mSleep.suspend(0, 0, 30);
			break;
		case 4:
			mSleep.suspend(0, 1, 0);
			break;
		case 5:
			mSleep.suspend(0, 5, 0);
			break;
		case 6:
			mSleep.suspend(0, 10, 0);
			break;
		case 7:
			mSleep.suspend(0, 15, 0);
			break;
		default:
			mSleep.suspend(0, 0, 10);
		}
	}
}
