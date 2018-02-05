package com.kn.grabbing;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ct.lk.domain.Draw;
import com.google.common.collect.Lists;
import com.kn.util.Market;
import com.kn.util.CommonUnits;
import com.kn.util.GameCode;
import com.kn.util.KenoWCAUtils;

public class KenoGrabbingWCA extends KenoGrabbingTask {
	
	private int issuePeriod = 10;
	private int dataSize;
	private String url;// = "http://www.wclc.com/winning-numbers/keno.htm?selDate=";
	private int error = 1;

	private static final Logger logger = LoggerFactory.getLogger(KenoGrabbingWCA.class);
	
//	public static void main(String[] args) {
//		KenoGrabbingWCA task = new KenoGrabbingWCA();
//		task.startGrabbing();
//
//	}
	
	public void startGrabbing() {
		String resultTime = CommonUnits.getNowDateTime();
		try {
			System.out.println("----------Keno WCA start----------");
			Draw draw = drawDAO.selectMAXDrawDate(Market.WCA.getMarketName(), GameCode.KN.name()).get(0);
			Date drawDate = draw.getDate();
			int day = drawDate.getDate();
			int month = drawDate.getMonth() + 1;
			int year = drawDate.getYear() + 1900;

			Document doc = Jsoup.parse(new URL(url + month + "/" + day + "/" + year), 10000);
			Element table = KenoWCAUtils.getTargetTable(doc);
			if (table != null) {
				List<Element> trs = table.select("tr");
				trs = Lists.reverse(trs);
				dataSize = trs.size() < issuePeriod ? trs.size() : issuePeriod;
	
				String drawNumber = "";
				String drawResult = "";
				for (int i = 0; i < dataSize; i++) {
					Element tr = trs.get(i);
					List<Element> tds = tr.select("td");
					drawNumber = tds.get(0).text();
					drawResult = "[" + tds.get(1).text() + "," + tds.get(2).text() + "," + tds.get(3).text() + ","
							+ tds.get(4).text() + "," + tds.get(5).text() + "," + tds.get(6).text() + ","
							+ tds.get(7).text() + "," + tds.get(8).text() + "," + tds.get(9).text() + ","
							+ tds.get(10).text() + "," + tds.get(11).text() + "," + tds.get(12).text() + ","
							+ tds.get(13).text() + "," + tds.get(14).text() + "," + tds.get(15).text() + ","
							+ tds.get(16).text() + "," + tds.get(17).text() + "," + tds.get(18).text() + ","
							+ tds.get(19).text() + "," + tds.get(20).text() + "]";
	
					// System.out.println(drawNumber + " - " + drawResult);
					processDrawData(drawNumber, drawResult, resultTime);
				}
			}
			System.out.println("----------Keno WCA end----------");
			error = 1;
		} catch (Exception e) {
			e.printStackTrace();
			if (error <= 3) {
				System.out.println("Keno WCA 錯誤次數:" + error);
				error++;
				changeIP();
			} else {
				logger.error(
						"Error in drawing " + Market.WCA.getMarketName() + " data. Error message: " + e.getMessage());
				drawDAO.insertErrorLog(GameCode.KN.name(), Market.WCA.name(), resultTime, 1);
				error = 1;
			}
		}
	}

	private void processDrawData(String drawNumber, String drawResult, String resultTime) {
		List<Draw> checkResult = drawDAO.selectByDrawNumberAndMarket(Market.WCA.getMarketName(), drawNumber, GameCode.KN.name());

		if (!checkResult.isEmpty()) {
			Draw draw = checkResult.get(0);
			HashMap<String, String> httpRequestInfo = new HashMap<String, String>();

				httpRequestInfo.put("drawId", "" + draw.getId());
				httpRequestInfo.put("gameCode", GameCode.KN.name());
				httpRequestInfo.put("market", Market.WCA.getMarketName());
				httpRequestInfo.put("drawNumber", drawNumber);
				httpRequestInfo.put("drawResultTime", resultTime);
				httpRequestInfo.put("result", drawResult);
				
				if (draw.getResult() == null || draw.getResult().length() == 0) {
					updateData(socketHttpDestination, httpRequestInfo, logger);
				}
		
		}

	}

	public void setIssuePeriod(int issuePeriod) {
		this.issuePeriod = issuePeriod;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
