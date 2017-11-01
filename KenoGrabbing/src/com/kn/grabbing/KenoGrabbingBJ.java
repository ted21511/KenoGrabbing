package com.kn.grabbing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ct.lk.domain.Draw;
import com.kn.util.GameCode;
import com.kn.util.KenoBJUtils;
import com.kn.util.Market;
import com.kn.util.UseIPInfo;

public class KenoGrabbingBJ extends KenoGrabbingTask {

	private static final Logger logger = LoggerFactory.getLogger(KenoGrabbingBJ.class);
	private String url; // = "http://www.bwlc.gov.cn/bulletin/prevkeno.html";
	private int page = 1;
	private String ipUrl = "http://www.xdaili.cn/ipagent//freeip/getFreeIps?page=1&rows=10";
	private String checkipUrl = "http://www.xdaili.cn/ipagent//checkIp/ipList?";
	private static boolean flag = true;
	int error = 1;

	// public static void main(String[] args) {
	// KenoGrabbingBJ task = new KenoGrabbingBJ();
	// task.startGrabbing();
	// }

	public void startGrabbing() {

		System.out.println("----------Keno BJ start----------");
		List<UseIPInfo> useIPList = checkCNIP();
		startMain(useIPList);
		System.out.println("----------Keno BJ end----------");

	}

	public void startMain(List<UseIPInfo> useIPList) {
		try {

			if (!useIPList.isEmpty()) {

				changeIP(useIPList, error);
				String pageUrl = url + page;
				Document xmlDoc = Jsoup.connect(pageUrl).timeout(10000).post();
				Elements newlist = KenoBJUtils.getNowNumber(xmlDoc);
				String resultTime = KenoBJUtils.getNowDateTime();

				String newNumber = newlist.get(0).text();
				List<Draw> getStartNB = drawDAO.getStartNumber(GameCode.KN.name(), Market.BJ.name());
				String startNumber = getStartNB.get(0).getNumber();
				List<Draw> list = drawDAO.getDrawNum(GameCode.KN.name(), Market.BJ.name(), newNumber);
				List<Draw> drawlist = drawDAO.getDrawNumList(GameCode.KN.name(), Market.BJ.name(), startNumber,
						newNumber);
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
			e.printStackTrace();
			if (error <= 3) {
				System.out.println("BJ錯誤次數:" + error);
				error++;
				startMain(useIPList);
			} else {
				logger.error("Error in drawing " + Market.BJ.name() + " data. Error message: " + e.getMessage());
				error = 1;
			}
		}
	}

	public void changeIP(List<UseIPInfo> useIPList, int error) {

		int errorCount = error - 1;

		if (errorCount > useIPList.size() - 1) {
			errorCount = useIPList.size() - 1;
		}
		String ip = useIPList.get(errorCount).getIp();
		String port = useIPList.get(errorCount).getPort();

		System.out.println("ip:" + ip + "|port:" + port);

		System.getProperties().setProperty("proxySet", "true");
		System.getProperties().setProperty("http.proxyHost", ip);
		System.getProperties().setProperty("http.proxyPort", port);

	}

	public List<UseIPInfo> checkCNIP() {

		List<UseIPInfo> ipList = new ArrayList<UseIPInfo>();

		try {
			Document doc = Jsoup.connect(ipUrl).ignoreContentType(true).timeout(5000).get();
			String json = doc.select("body").text();
			String checkIPJson = KenoBJUtils.splitJson(json);

			JSONArray jsonArray = new JSONArray(checkIPJson);

			for (int i = 0; i <= jsonArray.length() - 1; i++) {
				JSONObject tmpJson = jsonArray.getJSONObject(i);
				String ip = tmpJson.get("ip").toString();
				String port = tmpJson.get("port").toString();
				checkipUrl = checkipUrl + "ip_ports%5B%5D=" + ip + "%3A" + port + "&";
			}

			Document ckipDoc = Jsoup.connect(checkipUrl).ignoreContentType(true).get();
			String ckipJson = ckipDoc.select("body").text();
			String ipJson = KenoBJUtils.splitJson(ckipJson);

			JSONArray jsonIPArray = new JSONArray(ipJson);

			for (int j = 0; j <= jsonIPArray.length() - 1; j++) {
				JSONObject tmpIPJson = jsonIPArray.getJSONObject(j);
				String tmpTime = tmpIPJson.optString("time");

				if (!tmpTime.isEmpty()) {
					int time = KenoBJUtils.formatInt(tmpTime);

					if (time <= 3000) {
						UseIPInfo useIPInfo = new UseIPInfo();
						useIPInfo.setIp(tmpIPJson.get("ip").toString());
						useIPInfo.setPort(tmpIPJson.get("port").toString());
						useIPInfo.setTime(time);
						ipList.add(useIPInfo);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
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
				xmlDoc = Jsoup.connect(pUrl).timeout(10000).post();
			}
		}
		return awardMap;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
