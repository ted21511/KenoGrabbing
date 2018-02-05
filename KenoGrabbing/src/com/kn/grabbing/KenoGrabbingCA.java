package com.kn.grabbing;

import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ct.lk.domain.Draw;
import com.kn.util.CommonUnits;
import com.kn.util.GameCode;
import com.kn.util.Market;
import com.kn.util.KenoCAUtils;


public class KenoGrabbingCA extends KenoGrabbingTask{
	
	private static final Logger logger = LoggerFactory.getLogger(KenoGrabbingCA.class);
	private String url;
	int error = 1;
	
	
//	public static void main(String[] args) {
//	KenoGrabbingCA task = new KenoGrabbingCA();
//	task.startGrabbing();
//    }

	public void startGrabbing() {
		String resultTime = CommonUnits.getNowDateTime();
		try {
			System.out.println("----------Keno CA start----------");
			Document xmlDoc = Jsoup.connect(url).ignoreContentType(true).timeout(10000).get();
			HashMap<String,JSONObject> newlist = KenoCAUtils.getNumber(xmlDoc);
			String newNumber = newlist.get("firstAward").get("drawNbr").toString();
			String startNumber = newlist.get("lastAward").get("drawNbr").toString();
			List<Draw> list = null;
			List<Draw> drawlist = null;
			list = drawDAO.getDrawNum(GameCode.KN.name(), Market.CA.name(), newNumber);
			drawlist = drawDAO.getDrawNumList(GameCode.KN.name(), Market.CA.name(), startNumber, newNumber);
			HashMap<String, String> httpRequestInfo = null;
			HashMap<String, String> awardMap = null;
			String newAward = null;
			
			if (list.isEmpty() && !drawlist.isEmpty()) {
				String lastNumber = drawlist.get(drawlist.size() - 1).getNumber();
				awardMap = supplyNumber(xmlDoc, lastNumber);
				list = drawlist;
			}
		
			if (!list.isEmpty()) {
				for (Draw dList : list) {
					String mappingNumber = dList.getNumber();
					if (awardMap != null) {
						newAward = awardMap.get(mappingNumber);
		                 if (newAward != null){			 
		                	 drawDAO.updateDrawResult(GameCode.KN.name(), Market.CA.name(), mappingNumber, newAward);
		                 }							
					}else{
					
						if (mappingNumber.equals(newNumber) && dList.getResult() == null) {

							newAward = newlist.get("firstAward").get("drawNbrs").toString();;
							httpRequestInfo = new HashMap<String, String>();
							httpRequestInfo.put("drawId", "" + dList.getId());
							httpRequestInfo.put("gameCode", GameCode.KN.name());
							httpRequestInfo.put("market", Market.CA.name());
							httpRequestInfo.put("drawNumber", mappingNumber);
							httpRequestInfo.put("drawResultTime", resultTime);
							httpRequestInfo.put("result", newAward);

							updateData(socketHttpDestination, httpRequestInfo, logger);							
						}				
					}
				}
			}
			
			System.out.println("----------Keno CA end----------");
			error = 1;
		} catch (Exception e) {
			e.printStackTrace();
			if (error <= 3) {
				System.out.println("CA錯誤次數:" + error);
				error++;
				changeIP();
			} else {
				logger.error("Error in drawing " + Market.CA.name() + " data. Error message: " + e.getMessage());
				drawDAO.insertErrorLog(GameCode.KN.name(), Market.CA.name(), resultTime, 1);
				error = 1;
			}
		}
	}
	
	public HashMap<String, String> supplyNumber(Document xmlDoc, String lastNumber) throws Exception {
		
		HashMap<String, String> awardMap = new HashMap<String, String>();
		awardMap = KenoCAUtils.Crawl(xmlDoc, lastNumber);
		
		return awardMap;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

}
