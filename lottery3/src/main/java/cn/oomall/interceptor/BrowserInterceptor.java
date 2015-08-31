/*  
 * @(#) BrowerInterceptor.java Create on 2015年7月20日 下午3:08:21   
 *   
 * Copyright 2015 .ooyanjing
 */


package cn.oomall.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.bitwalker.useragentutils.UserAgent;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.oomall.util.BrowserUtil;

/**
 * @BrowerInterceptor.java
 * @created at 2015年7月20日 下午3:08:21 by zhanghongliang@ooyanjing.com
 *
 * @author  zhanghongliang@ooyanjing.com
 * @version $Revision$
 * @update: $Date$
 */
@Component
public class BrowserInterceptor extends BaseMallInterceptor{
	private Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());
	//客户端浏览器的信息 key，在request attribute中
	public static final String REQUEST_BROWSER_KEY = "_request_user_browser";
	//是否微信  true  false
	public static final String REQUEST_BROWSER_WEIXIN = "_request_user_browser_weixin_Flag";
	
	@Autowired
	private HistoryUrlInterceptor historyUrlInterceptor;
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		String userAgentString = request.getHeader("User-Agent");
		Boolean  boo = BrowserUtil.checkWeixinBrowser(request);
		logger.debug("is weixin:{}",boo);
		request.setAttribute(REQUEST_BROWSER_WEIXIN, boo);
		try {
			UserAgent userAgent = new UserAgent(userAgentString );
			request.setAttribute(REQUEST_BROWSER_KEY, userAgent);
			logger.debug("客户浏览器信息：" + userAgent.toString());
			
			//TODO 根据浏览器，跳转不同平台，现在是 pc 与wap
			String type = getBrowserType(userAgent);
			String ctx = (String) request.getAttribute(ApplicationContextInterceptor.requestCtx);
			
			if(!type.equals(ctx)){
				String aa = historyUrlInterceptor.get(request.getSession(), 1);
				aa = StringUtils.replace(aa, ctx, type);
				response.sendRedirect(aa);
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public String getBrowserType(UserAgent agent){
		String result = "pc";
		String oprtaSystem = agent.getOperatingSystem().toString().toLowerCase();
		String browserName = agent.getBrowser().getName().toLowerCase();
		if(StringUtils.contains(browserName, "mobile") || oprtaSystem.equals("android")){
			result = "wap";
		}else{
			result = "pc";
		}
		return result;
	}
}
