package com.kn.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class HtmlUnit {

public static Document start(String url) {
		
		Document doc = null;
		try {
		WebClient wc = new WebClient(BrowserVersion.CHROME);
		deploy(wc);
		HtmlPage page = wc.getPage(url);
		doc = Jsoup.parseBodyFragment(page.asXml());
		wc.closeAllWindows();
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
		}
		return doc;		
	}
	
    public static Document changeIPStart(String url,String proxyHost,int proxyPort) {
		
		Document doc = null;
		try {
		WebClient wc = new WebClient(BrowserVersion.CHROME,proxyHost,proxyPort);
		deploy(wc);
		HtmlPage page = wc.getPage(url);
		doc = Jsoup.parseBodyFragment(page.asXml());
		wc.closeAllWindows();
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
		}
		return doc;		
	}
    
    public static void deploy(WebClient wc) {
    	wc.getOptions().setUseInsecureSSL(true);
		wc.getOptions().setJavaScriptEnabled(true); // 启用JS解释器，默认为true
		wc.getOptions().setCssEnabled(false); // 禁用css支持
		wc.getOptions().setThrowExceptionOnScriptError(false); // js运行错误时，是否抛出异常
		wc.getOptions().setTimeout(100000); // 设置连接超时时间 ，这里是10S。如果为0，则无限期等待
		wc.getOptions().setDoNotTrackEnabled(false);
    }
	
}
