package com.kn.util;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class KenoBJUtils {

	public static String splitJson(String json) {

		String[] splitTmpJson = json.split("\\[");
		String[] tmpSplitJson = splitTmpJson[1].split("\\]");
		String checkIPJson = "[" + tmpSplitJson[0] + "]";

		return checkIPJson;
	}

	public static int formatInt(String tmptime) {

		String regex = "ms";
		String stringTime = tmptime.replace(regex, "");
		int time = Integer.parseInt(stringTime);

		return time;
	}

	public static Elements getNowNumber(Document xmlDoc) {

		Elements newList = xmlDoc.select(".tb").select("tr").get(1).select("td");
		System.out.println("BJ最新彩期:" + newList.get(0).text() + " | " + newList.get(1).text());
		sortAward(newList.get(1).text());

		return newList;
	}

	public static String getNowDateTime() {

		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String dateTime = dateFormat.format(now);

		return dateTime;
	}

	public static String sortAward(String tmpaward) {

		String[] splitAward = tmpaward.split(",");
		Integer[] sortaward = new Integer[splitAward.length];
		int i = 0;
		for (String tmpAward : splitAward) {
			int tmpInt = Integer.parseInt(tmpAward);
			sortaward[i] = tmpInt;
			i++;
		}
		Arrays.sort(sortaward);
		String taward = Arrays.toString(sortaward);
		String award = taward.replace(" ", "");

		return award;
	}

	public static HashMap<String, String> Crawl(Document xmlDoc, String lastNumber) {

		Elements list = xmlDoc.select(".tb").select("tr");
		long lsNumber = Long.parseLong(lastNumber);

		HashMap<String, String> awardMap = new HashMap<String, String>();

		for (int i = 1; i <= list.size() - 1; i++) {
			Elements dateList = list.get(i).select("td");
			String number = dateList.get(0).text();
			long dataNumber = Long.parseLong(number);

			if (lsNumber <= dataNumber) {
				String newAward = KenoBJUtils.sortAward(dateList.get(1).text());
				awardMap.put(number, newAward);
			} else {
				break;
			}
		}
		return awardMap;
	}

}
