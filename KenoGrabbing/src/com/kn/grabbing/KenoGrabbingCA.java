package com.kn.grabbing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ct.lk.domain.Draw;
import com.kn.util.CommonUnits;
import com.kn.util.GameCode;
import com.kn.util.Market;
import com.kn.util.UseIPInfo;
import com.kn.util.KenoCAUtils;

public class KenoGrabbingCA extends KenoGrabbingTask {

	private static final Logger logger = LoggerFactory.getLogger(KenoGrabbingCA.class);
	private String ipUrl = "https://www.proxydocker.com/en/proxylist/search?port=All&type=HTTP&anonymity=All&country=Canada&city=All&state=All&need=All";
	private String url;
	int error = 1;

	// public static void main(String[] args) {
	// KenoGrabbingCA task = new KenoGrabbingCA();
	// task.startGrabbing();
	// }

	public void startGrabbing() {
		System.out.println("----------Keno CA start----------");
		String resultTime = CommonUnits.getNowDateTime();
		List<UseIPInfo> useIPList = new ArrayList<UseIPInfo>();
		useIPList = checkCAIP(resultTime);
		startMain(useIPList, resultTime);
		System.out.println("----------Keno CA end----------");
	}

	public void startMain(List<UseIPInfo> useIPList, String resultTime) {

		try {
			if (!useIPList.isEmpty()) {
				UseIPInfo porxyIp = new UseIPInfo();
				porxyIp = changeIP(useIPList);
				int port = Integer.parseInt(porxyIp.getPort());
				Document xmlDoc = Jsoup.connect(url).proxy(porxyIp.getIp(),port).ignoreContentType(true).timeout(10000).get();
				HashMap<String, JSONObject> newlist = KenoCAUtils.getNumber(xmlDoc);

				
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
							if (newAward != null) {
								drawDAO.updateDrawResult(GameCode.KN.name(), Market.CA.name(), mappingNumber, newAward);
							}
						} else {

							if (mappingNumber.equals(newNumber) && dList.getResult() == null) {

								newAward = newlist.get("firstAward").get("drawNbrs").toString();
								
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
			}else {
				System.out.println("目前無ip可以使用orIP回應速度過慢");
			}
			error = 1;
		} catch (Exception e) {
			System.out.println(e.toString());
			if (error <= 3) {
				System.out.println("CA錯誤次數:" + error);
				error++;
				startMain(useIPList,resultTime);
			} else {
				logger.error("Error in drawing " + Market.CA.name() + " data. Error message: " + e.getMessage());
				drawDAO.insertErrorLog(GameCode.KN.name(), Market.CA.name(), resultTime, 1);
				error = 1;
			}
		}

	}

	public UseIPInfo changeIP(List<UseIPInfo> useIPList) {
	
		Random random = new Random();
		int ran = random.nextInt(useIPList.size());
		
		String ip = useIPList.get(ran).getIp();
		String port = useIPList.get(ran).getPort();
		UseIPInfo porxyIp = new UseIPInfo();

		System.out.println("ip:" + ip + "|port:" + port);

		porxyIp.setIp(ip);
		porxyIp.setPort(port);
		// System.getProperties().setProperty("proxySet", "true");
		// System.getProperties().setProperty("http.proxyHost", ip);
		// System.getProperties().setProperty("http.proxyPort", port);

		return porxyIp;
	}

	public List<UseIPInfo> checkCAIP(String resultTime) {
		List<UseIPInfo> ipList = new ArrayList<UseIPInfo>();
		try {
			Document doc = Jsoup.connect(ipUrl).timeout(5000).post();
			Elements allIP = doc.select(".proxylist_table > tbody > tr");

			for (int i = 1;i<=allIP.size()-1;i++) {
				String filterIp = allIP.get(i).select("td").get(0).text();
				if(!filterIp.isEmpty()){
					String tmpSpeed =  allIP.get(i).select("td").get(3).select(".proxy-ping-span").attr("style");
					String[] filterSpeed = tmpSpeed.split(":|%");
					int speed = Integer.parseInt(filterSpeed[filterSpeed.length-1]);
					if(speed >= 70){
						String[] Ip_Port = filterIp.split(":");
						UseIPInfo useIPInfo = new UseIPInfo();
						useIPInfo.setIp(Ip_Port[0]);
						useIPInfo.setPort(Ip_Port[1]);
						ipList.add(useIPInfo);
					}
				
				}

			}
			if(ipList.isEmpty()){
				drawDAO.insertErrorLog(GameCode.KN.name(), Market.CA.name(), resultTime, 4);
			}
			
		} catch (Exception e) {
			System.out.println(e.toString());
			drawDAO.insertErrorLog(GameCode.KN.name(), Market.CA.name(), resultTime, 3);
			
		}
		return ipList;
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
