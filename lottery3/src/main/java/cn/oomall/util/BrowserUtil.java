/*  
 * @(#) BrowserUtil.java Create on 2015年8月22日 上午11:18:35   
 *   
 * Copyright 2015 .ooyanjing
 */


package cn.oomall.util;

import javax.servlet.http.HttpServletRequest;

/**
 * @BrowserUtil.java
 * @created at 2015年8月22日 上午11:18:35 by liuyang@ooyanjing.com
 *
 * @desc
 *
 * @author  liuyang@ooyanjing.com
 * @version $Revision$
 * @update: $Date$
 */
public class BrowserUtil {
	/**
	 * 判断是否为微信内置浏览器
	 * 
	 * @Title: checkWeixinBrowser
	 * @data:2015年8月13日上午11:38:08
	 * @author:liuyang@ooyanjing.com
	 *
	 * @param request
	 * @return
	 */
	public static boolean checkWeixinBrowser(HttpServletRequest request) {
		String agent = request.getHeader("user-agent");
		// 判断是否是微信浏览器
		if (agent.contains("MicroMessenger")) {
			return true;
		} else {
			return false;
		}
	}
}
