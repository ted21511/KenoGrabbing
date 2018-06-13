package com.kn.grabbing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ct.lk.domain.Draw;
import com.kn.util.CommonUnits;
import com.kn.util.GameCode;
import com.kn.util.KenoBJUtils;
import com.kn.util.Market;
import com.kn.util.UseIPInfo;

public class KenoGrabbingBJ extends KenoGrabbingTask {

	private static final Logger logger = LoggerFactory.getLogger(KenoGrabbingBJ.class);
	private String url; // = "http://www.bwlc.gov.cn/bulletin/prevkeno.html";
	private int page = 1;
	private String ipUrl = "https://www.proxydocker.com/en/proxylist/search?port=All&type=HTTP&anonymity=All&country=China&city=All&state=All&need=All";
	private String subCheckipUrl = "http://cn-proxy.com/";
	private static boolean flag = true;
	int error = 1;

	// public static void main(String[] args) {
	// KenoGrabbingBJ task = new KenoGrabbingBJ();
	// task.startGrabbing();
	// }

	public void startGrabbing() {

		System.out.println("----------Keno BJ start----------");
		String resultTime = CommonUnits.getNowDateTime();
		List<UseIPInfo> useIPList = new ArrayList<UseIPInfo>();
		useIPList = checkCNIP(resultTime);
		if (useIPList.isEmpty() || useIPList.size()< 3) {
			useIPList = subCheckCNIP(useIPList,resultTime);
		}
	   startMain(useIPList,resultTime);
		System.out.println("----------Keno BJ end----------");

	}

	public void startMain(List<UseIPInfo> useIPList,String resultTime) {
		try {			
			if (!useIPList.isEmpty()) {
				UseIPInfo porxyIp = new UseIPInfo();
				porxyIp = changeIP(useIPList);
				int port = Integer.parseInt(porxyIp.getPort());
				String pageUrl = url + page;
				Document xmlDoc = Jsoup.connect(pageUrl).proxy(porxyIp.getIp(),port).timeout(5000).post();
				Elements newlist = KenoBJUtils.getNowNumber(xmlDoc);

				String newNumber = newlist.get(0).text();
				List<Draw> getStartNB = drawDAO.getStartNumber(GameCode.KN.name(), Market.BJ.name(),newNumber);
				String startNumber = getStartNB.get(0).getNumber();
				List<Draw> list = null;
				List<Draw> drawlist = null;
				list = drawDAO.getDrawNum(GameCode.KN.name(), Market.BJ.name(), newNumber);
				drawlist = drawDAO.getDrawNumList(GameCode.KN.name(), Market.BJ.name(), startNumber, newNumber);
				HashMap<String, String> awardMap = null;
				HashMap<String, String> httpRequestInfo = null;
				String newAward = null;

				if (list.isEmpty() && !drawlist.isEmpty()) {
					String lastNumber = drawlist.get(drawlist.size() - 1).getNumber();
					awardMap = supplyNumber(xmlDoc, url, page, lastNumber);
					list = drawlist;
				}

				if (!list.isEmpty()) {
					for (Draw dList : list) {
						String mappingNumber = dList.getNumber();
						if (awardMap != null) {
							newAward = awardMap.get(mappingNumber);
							if (newAward != null) {
								drawDAO.updateDrawResult(GameCode.KN.name(), Market.BJ.name(), mappingNumber, newAward);
								flag = true;
							}
						} else {
							if (mappingNumber.equals(newNumber) && dList.getResult() == null) {
								newAward = KenoBJUtils.sortAward(newlist.get(1).text());

								httpRequestInfo = new HashMap<String, String>();
								httpRequestInfo.put("drawId", "" + dList.getId());
								httpRequestInfo.put("gameCode", GameCode.KN.name());
								httpRequestInfo.put("market", Market.BJ.name());
								httpRequestInfo.put("drawNumber", mappingNumber);
								httpRequestInfo.put("drawResultTime", resultTime);
								httpRequestInfo.put("result", newAward);

								updateData(socketHttpDestination, httpRequestInfo, logger);						
							}
						}
					}
				}

			} else {
				System.out.println("目前無ip可以使用orIP回應速度過慢");
			}
			error = 1;
		} catch (Exception e) {
			System.out.println(e.toString());
			if (error <= 3) {
				System.out.println("BJ錯誤次數:" + error);
				error++;
				startMain(useIPList,resultTime);
			} else {
				logger.error("Error in drawing " + Market.BJ.name() + " data. Error message: " + e.getMessage());
				drawDAO.insertErrorLog(GameCode.KN.name(), Market.BJ.name(), resultTime, 1);
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
		
		return porxyIp;
	}

	public List<UseIPInfo> checkCNIP(String resultTime) {

		List<UseIPInfo> ipList = new ArrayList<UseIPInfo>();
		

		try {
			
			Document doc = Jsoup.connect(ipUrl).ignoreContentType(true).timeout(5000).get();
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
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return ipList;
	}

	public List<UseIPInfo> subCheckCNIP(List<UseIPInfo> useIPList,String resultTime) {
		
		List<UseIPInfo> ipList = useIPList;
		try {
			Document doc = Jsoup.connect(subCheckipUrl).ignoreContentType(true).timeout(5000).get();
			Elements allIP = doc.select(".sortable").select("tbody").select("tr");
			for (Element checkIP :  allIP) {
				String checkPort = checkIP.select("td").get(1).text();
					String[] speed = checkIP.select(".bar").attr("style").split("\\s|;|%");
					int resSpeed = Integer.parseInt(speed[1]);					
					if (resSpeed >= 75) {										
						UseIPInfo useIPInfo = new UseIPInfo();
						useIPInfo.setIp(checkIP.select("tr").select("td").get(0).text());
						useIPInfo.setPort(checkPort);
						ipList.add(useIPInfo);
						if(ipList.size() > 4){
							return ipList;
						}
					}
			}

			if(ipList.isEmpty()){
				drawDAO.insertErrorLog(GameCode.KN.name(), Market.BJ.name(), resultTime, 4);
			}
		} catch (Exception e) {
			System.out.println(e.toString());
			drawDAO.insertErrorLog(GameCode.KN.name(), Market.BJ.name(), resultTime, 3);
		}
		return ipList;
	}

	public HashMap<String, String> supplyNumber(Document xmlDoc, String url, int page, String lastNumber)
			throws Exception {

		HashMap<String, String> awardMap = new HashMap<String, String>();

		while (flag) {
			HashMap<String, String> tmpAwardList = KenoBJUtils.Crawl(xmlDoc, lastNumber);
			awardMap.putAll(tmpAwardList);

			if (awardMap.get(lastNumber) != null || tmpAwardList.size() == 0) {
				flag = false;
			} else {
				page++;
				String pUrl = url + page;
				xmlDoc = Jsoup.connect(pUrl).timeout(5000).post();
			}
		}
		return awardMap;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
