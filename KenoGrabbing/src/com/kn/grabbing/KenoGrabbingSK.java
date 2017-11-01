package com.kn.grabbing;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.ct.lk.domain.Draw;

import com.kn.util.GameCode;
import com.kn.util.KenoSKUtils;
import com.kn.util.Market;

public class KenoGrabbingSK extends KenoGrabbingTask {

	private static final Logger logger = LoggerFactory.getLogger(KenoGrabbingSK.class);
	private String url; // = "https://eklubkeno.etipos.sk/Archive.aspx";
	int error = 1;
	
//	 public static void main(String[] args) {
//	 KenoGrabbingSK task = new KenoGrabbingSK();
//	 task.startGrabbing();
//	 }

	
	public void startGrabbing() {

		try {
			System.out.println("----------Keno SK start----------");					 			 
			Document xmlDoc = Jsoup.connect(url).timeout(10000).post();
			String resultTime = KenoSKUtils.getNowDateTime();
			Element newlist = KenoSKUtils.getNumber(xmlDoc);
			if (newlist != null) {

				String newNumber = newlist.select("#_ctl0_ContentPlaceHolder_repResult__ctl1_lblDrawTimeValue").text();
				String newDate = KenoSKUtils.formatNowDate(newNumber);
				String startDate = KenoSKUtils.formatStartDate(newNumber);
				List<Draw> list = drawDAO.getDrawDateTime(GameCode.KN.name(), Market.SK.name(), newDate);
				List<Draw> drawlist = drawDAO.getDrawDateList(GameCode.KN.name(), Market.SK.name(), startDate, newDate);
				HashMap<String, String> awardMap = null;
				HashMap<String, String> httpRequestInfo = null;
				String newAward = null;

				if (list.isEmpty() && !drawlist.isEmpty()) {
					String lastDate = KenoSKUtils.getLastDate(drawlist);
					awardMap = supplyNumber(xmlDoc, url, newNumber, lastDate);
					list = drawlist;
				}
				if (!list.isEmpty()) {
					for (Draw dList : list) {
						String Number = dList.getNumber();
						Date mappingDate = dList.getEndTime();
						if (awardMap != null) {
							Calendar tmpDate = Calendar.getInstance();
							tmpDate.setTime(mappingDate);
							tmpDate.add(Calendar.HOUR_OF_DAY, -6);
							SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
							String date = dateFormat.format(tmpDate.getTime());
							String award = awardMap.get(date);
							if (award != null) {
								drawDAO.updateDrawResult(GameCode.KN.name(), Market.SK.name(), Number, award);
							}
						} else {
							if (mappingDate.toString().equals(newDate) && dList.getResult() == null) {
								newAward = KenoSKUtils.getAward(newlist);

								httpRequestInfo = new HashMap<String, String>();
								httpRequestInfo.put("drawId", "" + dList.getId());
								httpRequestInfo.put("gameCode", GameCode.KN.name());
								httpRequestInfo.put("market", Market.SK.name());
								httpRequestInfo.put("drawNumber", Number);
								httpRequestInfo.put("drawResultTime", resultTime);
								httpRequestInfo.put("result", newAward);

								updateData(socketHttpDestination, httpRequestInfo, logger);

							}

						}
					}
				}

			} else {
				System.out.println("SK　尚未開獎！！");
			}
			System.out.println("----------Keno SK end----------");
			error = 1;
		} catch (Exception e) {
			e.printStackTrace();
			if (error <= 3) {
				System.out.println("SK錯誤次數:" + error);
				error++;
				changeIP();
			} else {
				logger.error("Error in drawing " + Market.SK.name() + " data. Error message: " + e.getMessage());
				error = 1;
			}
		}

	}

	public HashMap<String, String> supplyNumber(Document xmlDoc, String url, String newNumber, String lastdate)
			throws Exception {

		HashMap<String, String> awardMap = new HashMap<String, String>();

		String tmpNowPage[] = newNumber.split(":");
		String tmpLastPage[] = lastdate.split(":");
		int nowPage = Integer.parseInt(tmpNowPage[0]);
		int lastPage = Integer.parseInt(tmpLastPage[0]);

		while (nowPage >= lastPage) {
			nowPage--;
			HashMap<String, String> tmpAwardList = KenoSKUtils.Crawl(xmlDoc);
			awardMap.putAll(tmpAwardList);
			xmlDoc = KenoSKUtils.postNext(xmlDoc, url, nowPage);
		}
		return awardMap;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
