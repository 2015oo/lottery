/*  
 * @(#) MallWeixinInterceptor.java Create on 2015年8月12日 下午3:25:50   
 *   
 * Copyright 2015 .ooyanjing
 */

package cn.oomall.interceptor;

import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import cn.ooeyeglass.framework.core.utils.stringUtils.StringUtil;
import cn.oomall.util.BrowserUtil;

import com.fastweixin.api.JsAPI;
import com.fastweixin.api.OauthAPI;
import com.fastweixin.api.config.ApiConfig;
import com.fastweixin.api.config.PayApiConfig;
import com.fastweixin.api.enums.OauthScope;
import com.fastweixin.api.response.GetSignatureResponse;

/**
 * @MallWeixinInterceptor.java
 * @created at 2015年8月12日 下午3:25:50 by zhanghongliang@ooyanjing.com
 *
 * @desc
 *
 * @author zhanghongliang@ooyanjing.com
 * @version $Revision$
 * @update: $Date$
 */
@Component("weixinInterceptor")
public class WeixinInterceptor extends BaseMallInterceptor {

	
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ApiConfig weixinApiConfig;

	@Autowired
	private PayApiConfig payApiConfig;

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		// 1、是微信浏览器
		if (BrowserUtil.checkWeixinBrowser(request)) {
			// 是否获取到 openid
			Object openid = request.getSession().getAttribute("openid");
			if (StringUtil.isNotNull(openid)) {
				logger.debug("session 中已经有openid={}", openid);
				return true;
			} else { // 2、无openid时处理
				Object aouthType = request.getParameter("weixinoauth");

				OauthAPI oauthAPI = new OauthAPI(weixinApiConfig);
				
				String backUrl  = HistoryUrlInterceptor.get(request.getSession(), 1);
				if ("_confirm".equals(aouthType)) { // 3、根据参数判断是否走conform or
					String oauth_uri = payApiConfig.getDomainName()
							+ "/wap/weixin/oauth/getConfirmOauth";
					oauth_uri += "?backurl=" + URLEncoder.encode(backUrl);
					logger.debug("confirm oauth_url={}", oauth_uri);
					// 需要用户授权
					String pageUrl = oauthAPI.getOauthPageUrl(oauth_uri,
							OauthScope.SNSAPI_USERINFO, "oo_snsapi_userinfo");
					response.sendRedirect(pageUrl);
				} else {
					// 通过微信API静默授权获取用户openid,跳转到OauthController 进行保存openid
					String oauth_uri = payApiConfig.getDomainName()
							+ "/wap/weixin/oauth/getOauth";
					oauth_uri += "?backurl=" + URLEncoder.encode(backUrl);
					logger.debug("oauth_url={}", oauth_uri);
					// 静默授权
					String pageUrl = oauthAPI.getOauthPageUrl(oauth_uri,
							OauthScope.SNSAPI_BASE, "oo_snsapi_base");
					response.sendRedirect(pageUrl);
				}
				return false;
			}
		} else {
			return true;
		}
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
		if (BrowserUtil.checkWeixinBrowser(request)) { // 1、是微信浏览器
			Object wxjsenable = request.getAttribute("wxjsenable");
			if("1".equals(wxjsenable)&&modelAndView !=null){// 2、根据参数判断是否需要jsApi 信息，这里添加
				JsAPI jsApI = new JsAPI(weixinApiConfig);
				String url = request.getRequestURL().toString();
				final String queryString = request.getQueryString();
				if(StringUtils.isNoneBlank(queryString)){
					url += "?" + queryString;
				}
				GetSignatureResponse wxresponse = jsApI.getSignature(url);
				logger.debug("sign info:{}",wxresponse);
				logger.debug("weixinApiConfig info:{}",weixinApiConfig.toString());
				modelAndView.addObject("sign", wxresponse);
				modelAndView.addObject("payApiConfig", payApiConfig);
				modelAndView.addObject("weixinApiConfig", weixinApiConfig);
			}
			
		}
	}

}
