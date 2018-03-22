package com.kn.grabbing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ct.lk.domain.Draw;
import com.kn.util.CommonUnits;
import com.kn.util.GameCode;
import com.kn.util.Market;

public class KenoGrabbingMT extends KenoGrabbingTask {

	private static final String FILENAME ="/usr/local/applications/kn-grabbing-server/Keno-Grabbing-MT.txt";
	//private static final String FILENAME = "C:/Users/ted/Desktop/Keno-Grabbing-MT.txt";
	private static final Logger logger = LoggerFactory.getLogger(KenoGrabbingMT.class);

	// public static void main(String[] args) {
	// // TODO Auto-generated method stub
	// KenoGrabbingMT task = new KenoGrabbingMT();
	// task.startGrabbing();
	// }

	public void startGrabbing() {
		BufferedReader br = null;
		FileReader fr = null;
		String resultTime = CommonUnits.getNowDateTime();
		try {
			System.out.println("----------Keno MT start----------");
			fr = new FileReader(FILENAME);
			br = new BufferedReader(fr);

			String sCurrentLine;
			String checkResultTime = null;
			String checkLastUpdateTime = null;
			String msgCode = null;
			HashMap<String, String> result = new HashMap<String, String>();
			int nHr = 0;
			int lHr = 0;

			while ((sCurrentLine = br.readLine()) != null) {

				if (sCurrentLine.contains("Last updated time:")) {
					String lastUpdateTime = sCurrentLine.split(": ")[1];
					String[] tmpcheckResultTime = resultTime.split(":");
					String[] tmpcheckLastUpdateTime = lastUpdateTime.split(":");
					checkResultTime = tmpcheckResultTime[0];
					checkLastUpdateTime = tmpcheckLastUpdateTime[0];
					nHr = Integer.parseInt(tmpcheckResultTime[1]);
					lHr = Integer.parseInt(tmpcheckLastUpdateTime[1]);
				}

				if (sCurrentLine.contains("Message_Code:")) {
					msgCode = sCurrentLine.split(": ")[1];
				}

				if (sCurrentLine.contains("@")) {
					result.put(sCurrentLine.split("@")[0], sCurrentLine.split("@")[1]);
				}

			}

			if (checkResultTime != null || checkLastUpdateTime != null || msgCode != null || result.size() != 0) {
				if (checkResultTime.equals(checkLastUpdateTime) && nHr - lHr < 20) {
					if (msgCode.equals("0")) {
						for (Object key : result.keySet()) {
							processDrawData(key.toString(), result.get(key), resultTime);
						}
					} else {
						int tmpMsgCode = Integer.parseInt(msgCode);
						drawDAO.insertErrorLog(GameCode.KN.name(), Market.MT.name(), resultTime, tmpMsgCode);
					}
				} else {
					drawDAO.insertErrorLog(GameCode.KN.name(), Market.MT.name(), resultTime, 2);
				}
			} else {
				drawDAO.insertErrorLog(GameCode.KN.name(), Market.MT.name(), resultTime, 2);
			}

			System.out.println("----------Keno MT end----------");
		} catch (Exception e) {
			System.out.println(e.toString());
			drawDAO.insertErrorLog(GameCode.KN.name(), Market.MT.name(), resultTime, 2);
		} finally {
			try {
				if (br != null)
					br.close();
				if (fr != null)
					fr.close();
			} catch (Exception ex) {
				ex.printStackTrace();
				drawDAO.insertErrorLog(GameCode.KN.name(), Market.MT.name(), resultTime, 2);
			}
		}
	}

	private void processDrawData(String drawNumber, String drawResult, String resultTime) {
		List<Draw> checkResult = drawDAO.selectByDrawNumberAndMarket(Market.MT.getMarketName(), drawNumber,
				GameCode.KN.name());

		if (!checkResult.isEmpty()) {
			Draw draw = checkResult.get(0);
			HashMap<String, String> httpRequestInfo = new HashMap<String, String>();

			httpRequestInfo.put("drawId", "" + draw.getId());
			httpRequestInfo.put("gameCode", GameCode.KN.name());
			httpRequestInfo.put("market", Market.MT.getMarketName());
			httpRequestInfo.put("drawNumber", drawNumber);
			httpRequestInfo.put("drawResultTime", resultTime);
			httpRequestInfo.put("result", drawResult);

			if (draw.getResult() == null || draw.getResult().length() == 0) {
				updateData(socketHttpDestination, httpRequestInfo, logger);
			}

		}

	}

}
