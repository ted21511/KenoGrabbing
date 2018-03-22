package com.kn.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;

public class KenoCAUtils {

	public static HashMap<String, JSONObject> getNumber(Document xmlDoc) {

		HashMap<String, JSONObject> newlist = new HashMap<String, JSONObject>();

		try {
			String json = xmlDoc.text();
			JSONArray jsonArray = new JSONArray(json);
			JSONObject firstAward = jsonArray.getJSONObject(0);
			JSONObject lastAward = jsonArray.getJSONObject(jsonArray.length() - 1);
			System.out.println(
					"CA最新彩期: " + firstAward.get("drawNbr").toString() + " | " + firstAward.get("drawNbrs").toString());

			newlist.put("firstAward", firstAward);
			newlist.put("lastAward", lastAward);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
		}

		return newlist;
	}

	public static HashMap<String, String> Crawl(Document xmlDoc, String lastNumber) {

		HashMap<String, String> awardMap = new HashMap<String, String>();
		long Number = Long.parseLong(lastNumber);

		try {
			String json = xmlDoc.text();
			JSONArray jsonArray = new JSONArray(json);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObj = jsonArray.getJSONObject(i);
				String dateList = jsonObj.get("drawNbr").toString();
				long dataNumber = Long.parseLong(dateList);
				if (Number <= dataNumber) {
					String newAward = jsonObj.get("drawNbrs").toString();
					awardMap.put(dateList, newAward);
				}
			}

		} catch (Exception e) {

		}

		return awardMap;
	}

}
