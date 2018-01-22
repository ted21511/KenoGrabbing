package com.kn.util;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class KenoWCAUtils {
	
	public static Element getTargetTable(Document doc) {
		try {
			Element table = doc.getElementsByAttributeValue("class", "kenoTable").first();
			
			return table;
		} catch (Exception e) {
			return null;
		}
		
	}
}
