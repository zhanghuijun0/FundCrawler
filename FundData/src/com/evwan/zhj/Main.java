package com.evwan.zhj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.zhj.tool.TimeHelper;
import com.zhj.tool.WriteFileHelper;

/**
 * http://fund.chinastock.com.cn/jjpj/fundAction.do?methodCall=
 * queryLfFundOfDayListYh&pubdate=2015-10-08&sName=&down=
 * 
 * 金融数据抓取Demo
 * 
 * @author zhj
 *
 */

public class Main {
	String fileName = "log";
	String baseUrl = "http://fund.chinastock.com.cn/jjpj/fundAction.do";
	String methodCall = "queryLfFundOfDayListYh";
	DataBaseUnit mBaseUnit = new DataBaseUnit();
	int startyear;
	int startmonth;
	int startday;
	int endyear;
	int endmonth;
	int endday;
	TimeHelper mTimeHelper = new TimeHelper();
	WriteFileHelper mWriteFileHelper = new WriteFileHelper();

	public static void main(String[] args) {
		Main demo = new Main();
		demo.initParam("set.conf");// 获取配置文件的配置信息
		int[] startCount = demo.mBaseUnit
				.callProceduceToShowCount("CALL show_counts(?,?,?,?);");
		demo.crawl();
		int[] endCount = demo.mBaseUnit
				.callProceduceToShowCount("CALL show_counts(?,?,?,?);");
		demo.showAddCount(startCount, endCount);
		demo.mWriteFileHelper
				.writeLog("****************************************");
	}

	private void crawl() {
		fileName = mTimeHelper.getToday("yyyyMMddHHmmss");
		String begin = mTimeHelper.getCurrentDateTime();
		mWriteFileHelper.writeLog("----------------------------------------");
		mWriteFileHelper.writeLog("|\t\tstart：" + begin);
		Document doc = getDocument(mTimeHelper.getYesterday());
		if (doc != null) {
			ArrayList<String> array = getSelectValue(doc.body());// 通过最新日期（昨天）获得基金管理人列表
			if (startday == 0 || startmonth == 0 || startyear == 0) {
				mWriteFileHelper.writeLog("|\t\t【昨日爬行】：["
						+ mTimeHelper.getYesterday() + "]");
				mWriteFileHelper
						.writeLog("----------------------------------------");
				getSomeDayData(mTimeHelper.getYesterday(), array);
			} else if (endday == 0 || endmonth == 0 || endyear == 0) {
				mWriteFileHelper.writeLog("|\t\t【单日爬行】：[" + startyear + "-"
						+ startmonth + "-" + startday + "]");
				mWriteFileHelper
						.writeLog("----------------------------------------");
				getSomeDayData(startyear + "-" + startmonth + "-" + startday,
						array);
			} else {
				mWriteFileHelper.writeLog("|\t\t【多日爬行】：[" + startyear + "-"
						+ startmonth + "-" + startday + "]————" + "[" + endyear
						+ "-" + endmonth + "-" + endday + "]");
				mWriteFileHelper
						.writeLog("----------------------------------------");
				getAllDay(array);
			}
		} else {
			mWriteFileHelper.writeLog("网络连接失败，请检查网络连接！");
		}
		String end = mTimeHelper.getCurrentDateTime();
		mWriteFileHelper.writeLog("****************************************");
		mWriteFileHelper.writeLog("*\t\tend：" + end);
		mWriteFileHelper.writeLog("*\t\t本次爬取一共花费了："
				+ mTimeHelper.getBetweenTime(begin, end));
	}

	/**
	 * 读取配置文件
	 * 
	 * @param paramFile
	 */
	private void initParam(String paramFile) {
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(paramFile));
			startyear = Integer.valueOf(props.getProperty("startyear"));
			startmonth = Integer.valueOf(props.getProperty("startmonth"));
			startday = Integer.valueOf(props.getProperty("startday"));
			endyear = Integer.valueOf(props.getProperty("endyear"));
			endmonth = Integer.valueOf(props.getProperty("endmonth"));
			endday = Integer.valueOf(props.getProperty("endday"));
		} catch (FileNotFoundException e) {
			mWriteFileHelper.writeLog(paramFile + "文件不存在！");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			mWriteFileHelper.writeLog("请仔细检查你的set.conf文件，格式是否正确！");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Document getDocument(String date, String name) {
		String url = baseUrl + "?methodCall=" + methodCall + "&pubdate=" + date
				+ "&sName=" + name;
		Connect mConnect = new Connect();
		Document doc = mConnect.getDocument(url);
		return doc;
	}

	private Document getDocument(String date) {
		return getDocument(date, "");
	}

	/**
	 * 解析所有的基金管理人
	 * 
	 * @param body
	 * @return 基金管理人列表
	 */
	private ArrayList<String> getSelectValue(Element body) {
		ArrayList<String> array = new ArrayList<String>();
		Elements elementSelect = body.select("select[id=sName] option");
		for (Element e : elementSelect) {
			array.add(e.attr("value"));
		}
		array.remove(0);// 删除第一个空元素
		return array;
	}

	/**
	 * 获取表格的数据
	 * 
	 * @param body
	 * 
	 * @param institutionId
	 *            机构id
	 */
	private void getFundData(Elements elementsTable, int institutionId,
			String date) {
		int typeId = -1;
		int fundId = -1;
		if (!elementsTable.hasAttr("class")) {// 当前日期或机构没有数据，直接返回
			return;
		}
		for (Element e : elementsTable) {
			if (e.hasAttr("class")) {
				if (e.attr("class").equals("level")) {
					Element eType = e.select("td").first();
					if (eType.hasAttr("style")) {// 分类
						String typeStr = eType.text().replaceAll("\\d+", "")
								.replace(".", "").trim();
						String typeArr[] = typeStr.split("--");
						typeId = mBaseUnit.execCall("CALL check_type('"
								+ typeArr[0] + "','" + typeArr[1] + "','"
								+ typeArr[2] + "',?);");
					}
				} else {
					Elements eData = e.select("td");
					double price = 0;
					if (!eData.get(6).text().equals("--")) {
						price = Double.parseDouble(eData.get(6).text());
					}
					if (institutionId == -1) {
						institutionId = mBaseUnit
								.execCall("CALL check_institution('"
										+ eData.get(3).text() + "','基金公司',?);");
					}
					fundId = mBaseUnit.execCall("CALL check_fund('"
							+ eData.get(2).text() + "','" + eData.get(1).text()
							+ "','" + typeId + "','" + institutionId + "','"
							+ eData.get(4).text() + "',?);");
					mBaseUnit.execCall("CALL check_price('" + fundId + "','"
							+ date + "','" + price + "',?);");
				}
			}
		}
	}

	/**
	 * 对指定日期的数据进行处理
	 * 
	 * @param date
	 *            日期
	 * @param name
	 *            基金管理人（机构）
	 */
	private void getSomeDayData(String date, ArrayList<String> array) {
		Elements elements = hasData(date);
		if (elements == null) {
			System.out.println("[" + mTimeHelper.getCurrentDateTime() + "]"
					+ date + "的数据为空！");
			return;
		}
		int count = elements.size();
		String log = "数据日期：" + date + "[" + count + "],";
		mWriteFileHelper.writeLog(log);
		if (count == 1) {
			return;
		} else if (count > 1000) {// 是以1274为标准的
			for (String institution : array) {
				int i_id = mBaseUnit.execCall("CALL check_institution('"
						+ institution + "','基金公司',?);");
				Document doc = getDocument(date, institution);
				Elements elementsTable = doc.body().select(
						"table[id=changeTable] tbody tr");
				getFundData(elementsTable, i_id, date);
			}
		} else if (count > 1 && count <= 1000) {
			getFundData(elements, -1, date);// 页面数据比较少的时候
		}
	}

	/**
	 * 得到某天所有的，以便判断其数据量的多少
	 * 
	 * @param date
	 * @return
	 */
	private Elements hasData(String date) {
		Document doc = getDocument(date);
		Elements elementsTable = doc.body().select(
				"table[id=changeTable] tbody tr");
		return elementsTable;
	}

	/**
	 * 遍历所有的日期
	 * 
	 * @param name
	 */
	private void getAllDay(ArrayList<String> array) {
		// 请注意月份是从0-11
		Calendar start = Calendar.getInstance();
		start.set(startyear, startmonth - 1, startday);// 从2001年8月1号开始
		Calendar end = Calendar.getInstance();
		end.set(endyear, endmonth - 1, endday);// 结束日期暂定为2015年10月16号
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		while (start.compareTo(end) <= 0) {
			String date = format.format(start.getTime());
			getSomeDayData(date, array);
			start.set(Calendar.DATE, start.get(Calendar.DATE) + 1);// 循环，每次天数加1
		}
	}

	private void showAddCount(int[] startCount, int[] endCount) {
		mWriteFileHelper.writeLog("*\t\t爬取了" + (endCount[1] - startCount[1])
				+ "条数据!");
		mWriteFileHelper.writeLog("*\t\t基金数目：" + startCount[0] + "——>"
				+ endCount[0]);
		mWriteFileHelper.writeLog("*\t\t基金数据数目：" + startCount[1] + "——>"
				+ endCount[1]);
		mWriteFileHelper.writeLog("*\t\t分类数目：" + startCount[2] + "——>"
				+ endCount[2]);
		mWriteFileHelper.writeLog("*\t\t基金数目：" + startCount[3] + "——>"
				+ endCount[3]);
	}

}
