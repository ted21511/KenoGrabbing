package com.kn.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.output.ThresholdingOutputStream;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ct.lk.domain.Draw;
import com.sun.org.apache.regexp.internal.recompile;

public class KenoSKUtils {
	
	private static int saveTime = 0;
	
	public static int getSaveTime() {
		Calendar tmpDate = Calendar.getInstance();	
		int month = tmpDate.get(Calendar.MONTH) + 1 ;	
		if(month<4 || month>10){
			saveTime = 1;
		}else{
			saveTime = 0;
		}	
		return saveTime;
	}
	
	public static Element getNumber(Document xmlDoc) {
		Element newlist = null;
		try {
			newlist = xmlDoc.select(".pageArchive").select(".table").get(0);
		} catch (Exception e) {
			return newlist;
		}
		System.out.println(
				"SK最新彩期: " + newlist.select("#_ctl0_ContentPlaceHolder_repResult__ctl1_lblDrawTimeValue").text() + " | "
						+ newlist.select(".gameListItem").text());
		return newlist;
	}

	public static String getNowDateTime() {

		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateTime = dateFormat.format(now);

		return dateTime;
	}

	public static String formatNowDate(String newNumber) {

		String a[] = newNumber.split(":");
		int hour = Integer.parseInt(a[0]);
		int mi = Integer.parseInt(a[1]);
		
		Calendar nowdate = Calendar.getInstance();
		if (hour + 6 + saveTime>= 24){
			nowdate.add(Calendar.DAY_OF_MONTH, -1);
		}
		nowdate.set(Calendar.HOUR_OF_DAY, hour + 6 + saveTime);
		nowdate.set(Calendar.MINUTE, mi);
		nowdate.set(Calendar.SECOND, 0);

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0");
		String dateTime = dateFormat.format(nowdate.getTime());

		return dateTime;
	}

	public static String formatStartDate(String newNumber) {
		
		String a[] = newNumber.split(":");
		int hour = Integer.parseInt(a[0]);
		
		Calendar startdate = Calendar.getInstance();
		if (hour >= 18 - saveTime){
			startdate.add(Calendar.DAY_OF_MONTH, -1);
		}
		startdate.set(Calendar.HOUR_OF_DAY, 11 + saveTime);
		startdate.set(Calendar.MINUTE, 0);
		startdate.set(Calendar.SECOND, 0);

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0");
		String dateTime = dateFormat.format(startdate.getTime());

		return dateTime;
	}

	public static String getAward(Element newlist) {

		String newAward = "[";
		for (int i = 0; i <= newlist.select(".gameListItem").size() - 1; i++) {

			String award = newlist.select(".gameListItem").get(i).text();
			if (i == newlist.select(".gameListItem").size() - 1) {
				newAward = newAward + award;
			} else {
				newAward = newAward + award + ",";
			}
		}
		newAward = newAward + "]";

		return newAward;
	}

	public static String getLastDate(List<Draw> drawlist) {

		Date date = drawlist.get(drawlist.size() - 1).getEndTime();
		Calendar tmpLastDate = Calendar.getInstance();
		tmpLastDate.setTime(date);
		tmpLastDate.add(Calendar.HOUR_OF_DAY, -6 - saveTime);

		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
		String lastdate = dateFormat.format(tmpLastDate.getTime());

		return lastdate;
	}

	public static Document postNext(Document xmlDoc, String url, int page) throws Exception {

		String viewstate = xmlDoc.select("#__VIEWSTATE").val();
		String eventvalidation = xmlDoc.select("#__EVENTVALIDATION").val();

		Calendar date = Calendar.getInstance();
		date.add(Calendar.HOUR_OF_DAY, -6 - saveTime);

		String ddlDay = "" + date.get(Calendar.DAY_OF_MONTH);
		int tmpDdlMonth = date.get(Calendar.MONTH) + 1;
		String ddlMonth = "" + tmpDdlMonth;
		String ddlYear = "" + date.get(Calendar.YEAR);

		SKPostDatainfo postDataInfo = new SKPostDatainfo();
		postDataInfo.set__VIEWSTATE(viewstate);
		postDataInfo.set__EVENTVALIDATION(eventvalidation);
		postDataInfo.setddlDay(ddlDay);
		postDataInfo.setddlMonth(ddlMonth);
		postDataInfo.setddlYear(ddlYear);

		Connection con = Jsoup.connect(url).userAgent(
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");
		con.data("__VIEWSTATE", postDataInfo.get__VIEWSTATE());
		con.data("__EVENTVALIDATION", postDataInfo.get__EVENTVALIDATION());
		con.data("_ctl0:ContentPlaceHolder:ddlDay", postDataInfo.getddlDay());
		con.data("_ctl0:ContentPlaceHolder:ddlMonth", postDataInfo.getddlMonth());
		con.data("_ctl0:ContentPlaceHolder:ddlYear", postDataInfo.getddlYear());
		con.data("_ctl0:ContentPlaceHolder:ddlTimeSpan", "" + page);
		con.data("_ctl0:ContentPlaceHolder:btnSubmit", "Zobraziť");
		Document doc = con.post();

		return doc;
	}

	public static HashMap<String, String> Crawl(Document xmlDoc) {

		HashMap<String, String> awardMap = new HashMap<String, String>();
		Elements list = xmlDoc.select(".pageArchive").select(".table");

		for (int i = 0; i <= list.size() - 1; i++) {

			Element data = list.get(i);
			int row = i + 1;
			String dateTime = data.select("#_ctl0_ContentPlaceHolder_repResult__ctl" + row + "_lblDrawTimeValue")
					.text();
			String newAward = getAward(data);

			awardMap.put(dateTime, newAward);
		}

		return awardMap;
	}

	// public static String getTimeRange() {
	//
	// Calendar nowdate = Calendar.getInstance();
	// nowdate.add(Calendar.HOUR_OF_DAY, -6);
	// System.out.println(nowdate.get(Calendar.HOUR_OF_DAY));
	// int SKHours = nowdate.get(Calendar.HOUR_OF_DAY);
	//
	// return null;
	// }
}
