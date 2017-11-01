package com.kn.grabbing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ct.lk.domain.Draw;
import com.kn.util.GameCode;
import com.kn.util.Market;

public class KenoGrabbingMT extends KenoGrabbingTask {

	private static final String FILENAME = "/usr/local/applications/lt-grabbing-server/Keno-Grabbing-MT.txt";
	private static final Logger logger = LoggerFactory.getLogger(KenoGrabbingMT.class);
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		KenoGrabbingMT task = new KenoGrabbingMT();
		task.startGrabbing();
	}
	
	public void startGrabbing() {
		BufferedReader br = null;
		FileReader fr = null;

		try {
			fr = new FileReader(FILENAME);
			br = new BufferedReader(fr);

			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				if (sCurrentLine.contains("-")) {
					String drawNumber = sCurrentLine.split("-")[0];
					String drawResult = sCurrentLine.split("-")[1];	
//					System.out.println(drawNumber + drawResult);
					processDrawData(drawNumber, drawResult);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (fr != null)
					fr.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}

		}

	}	
	
	private void processDrawData(String drawNumber, String drawResult) {
		List<Draw> checkResult = drawDAO.selectByDrawNumberAndMarket(Market.MT.getMarketName(), drawNumber, GameCode.KN.name());

		if (!checkResult.isEmpty()) {
			Draw draw = checkResult.get(0);
			HashMap<String, String> httpRequestInfo = new HashMap<String, String>();

			try {
				httpRequestInfo.put("drawId", "" + draw.getId());
				httpRequestInfo.put("gameCode", GameCode.KN.name());
				httpRequestInfo.put("market", Market.AU.getMarketName());
				httpRequestInfo.put("drawNumber", drawNumber);
				httpRequestInfo.put("result", drawResult);

				updateData(socketHttpDestination, httpRequestInfo, logger);

			} catch (Exception e) {
				e.printStackTrace();			
				logger.error("Error in drawing " + Market.AU.getMarketName() + " data. Error message: " + e.getMessage());			
			}
		}

	}

}
