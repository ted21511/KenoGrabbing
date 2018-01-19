package com.kn.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUnits {

	public static String getNowDateTime() {

		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateTime = dateFormat.format(now);

		return dateTime;
	}
	
}
