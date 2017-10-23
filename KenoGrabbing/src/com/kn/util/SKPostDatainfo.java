package com.kn.util;

public class SKPostDatainfo {
	private String __VIEWSTATE;
	private String __EVENTVALIDATION;
	private String ddlDay;
	private String ddlMonth;
	private String ddlYear;
	
	public String get__VIEWSTATE() {
		return __VIEWSTATE;
	}
	public void set__VIEWSTATE(String __VIEWSTATE) {
		this.__VIEWSTATE = __VIEWSTATE;
	}
	
	public String get__EVENTVALIDATION() {
		return __EVENTVALIDATION;
	}
	public void set__EVENTVALIDATION(String __EVENTVALIDATION) {
		this.__EVENTVALIDATION = __EVENTVALIDATION;
	}
	
	public String getddlDay() {
		return ddlDay;
	}
	public void setddlDay(String ddlDay) {
		this.ddlDay = ddlDay;
	}
	
	public String getddlMonth() {
		return ddlMonth;
	}
	public void setddlMonth(String ddlMonth) {
		this.ddlMonth = ddlMonth;
	}
	
	public String getddlYear() {
		return ddlYear;
	}
	public void setddlYear(String ddlYear) {
		this.ddlYear = ddlYear;
	}
	
	public String toString() {
		
		String str =   "{__VIEWSTATE:"+__VIEWSTATE+"},"
				     + "{__EVENTVALIDATION:"+__EVENTVALIDATION+"},"
				     + "{ddlDay:"+ddlDay+"},"
				     + "{ddlMonth:"+ddlMonth+"},"
				     + "{ddlYear:"+ddlYear+"}";				    
		System.out.println(str);
		
		return str;
	}

}
