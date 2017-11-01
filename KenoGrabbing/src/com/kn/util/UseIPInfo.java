package com.kn.util;

public class UseIPInfo {

	private String ip;
	private String port;
	private int time;
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	
	public String toString() {
		
		String str = "{ip:"+ip+"},{port:"+port+"},{time:"+time+"}";	
		System.out.println(str);
		
		return str;
	}
}
