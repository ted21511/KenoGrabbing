package com.kn.grabbing;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ct.lk.domain.Draw;
import com.kn.util.GameCode;
import com.kn.util.CommonUnits;
import com.kn.util.Market;

import org.json.JSONArray;
import org.json.JSONObject;

public class KenoGrabbingAU extends KenoGrabbingTask {
	
	private int issuePeriod = 10;
	private String url;// = "https://webapi-info-act.keno.com.au/v2/info/history?jurisdiction=ACT&starting_game_number=drawNumber&number_of_games=20&date=drawDate&page_size=20&page_number=1";
	private int error = 1;

	private static final Logger logger = LoggerFactory.getLogger(KenoGrabbingAU.class);
	
//	public static void main(String[] args) {
//		KenoGrabbingAU task = new KenoGrabbingAU();
//		task.startGrabbing();
//	}
	
	public void startGrabbing() {
		String resultTime = CommonUnits.getNowDateTime();
		try {
			System.out.println("----------Keno AU start----------");
			List<Draw> resultList = drawDAO.getDrawNumberNow(Market.AU.getMarketName(), GameCode.KN.name());
			Draw draw = resultList.get(resultList.size()-1);
			String startDrawNumber = draw.getNumber();
			String drawDate = new SimpleDateFormat("yyyy-MM-dd").format(draw.getDate());
			
	        Connection.Response response = Jsoup.connect(url.replace("drawNumber", startDrawNumber).replaceAll("drawDate", drawDate)).userAgent("Mozilla/5.0 (Windows NT 6.2; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0")
	                .timeout(10000)
	        		.method(Connection.Method.GET).ignoreContentType(true)
	                .execute();
	        
			Document doc= response.parse();
			Element body = doc.select("body").first();
			JSONObject jsonObj = new JSONObject(body.text());
			JSONArray list = (JSONArray) jsonObj.get("items");
			int size = list.length()-1;
			
			String drawNumber = "";
			String drawResult = "";			
			for (int i=size ; i>=0 ; i--) {
				JSONObject obj = list.getJSONObject(i);
				drawNumber = obj.getInt("game-number")+"";
				drawResult = obj.get("draw").toString();
//				drawDate = getDrawdate(drawNumber, resultList, drawDate);
//				System.out.println(drawNumber + " - " + drawResult);
				
//				processDrawData(drawNumber, drawResult, drawDate);
				processDrawData(drawNumber, drawResult, resultTime);
			}
			System.out.println("----------Keno AU end----------");
			error = 1;
		} catch (Exception e) {
			System.out.println(e.toString());
			if (error <= 3) {
				System.out.println("Keno AU 錯誤次數:" + error);
				error++;
				changeIP();
			} else {
				logger.error("Error in drawing " + Market.AU.getMarketName() + " data. Error message: " + e.getMessage());
				drawDAO.insertErrorLog(GameCode.KN.name(), Market.AU.name(), resultTime, 1);
				error = 1;
			}
		} 
	}
	
//	private String getDrawdate(String drawNumber, List<Draw> resultList, String drawDate) {
//		String dNumber;
//		for (Draw draw : resultList) {
//			dNumber = draw.getNumber();
//			if (drawNumber.equals(dNumber)) {
//				return new SimpleDateFormat("yyyy-MM-dd").format(draw.getDate());
//			}
//		}
//		return drawDate;
//	}

	private void processDrawData(String drawNumber, String drawResult, String resultTime) {
		List<Draw> checkResult = drawDAO.selectByDrawNumberAndMarketAU(Market.AU.getMarketName(), drawNumber, GameCode.KN.name());

		if (!checkResult.isEmpty()) {
			Draw draw = checkResult.get(0);
			HashMap<String, String> httpRequestInfo = new HashMap<String, String>();

			
				httpRequestInfo.put("drawId", "" + draw.getId());
				httpRequestInfo.put("gameCode", GameCode.KN.name());
				httpRequestInfo.put("market", Market.AU.getMarketName());
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
