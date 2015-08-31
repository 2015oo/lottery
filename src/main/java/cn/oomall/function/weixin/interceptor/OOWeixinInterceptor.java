package cn.oomall.function.weixin.interceptor;

import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import cn.ooeyeglass.framework.core.utils.stringUtils.StringUtil;
import cn.oomall.function.weixin.config.WeixinConsts;

import com.fastweixin.api.OauthAPI;
import com.fastweixin.api.config.ApiConfig;
import com.fastweixin.api.config.PayApiConfig;
import com.fastweixin.api.enums.OauthScope;

@Component(value="ooWeixinInterceptor")
public class OOWeixinInterceptor extends HandlerInterceptorAdapter {

	private Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

	/**
	 * 登陆提示页
	 */
	private static final String LOGIN_URI= "/controller/weixin/login/index";
	
	/**
	 * 微信授权
	 */
	private static final String AUTH_URI= "/weixin/oauth/getOauth";
	
	/**
	 * 支付回调
	 */
	private static final String NOTIFY_URI= "/weixin/pay/notify";
	/**
	 *  二维码生成接口
	 */
	private static final String QRCODE_URI= "/weixin/qrcode/createcode";
	
	/**
	 * 产品详情过滤掉
	 */
	private static final String PRODUCTINTRO_URI= "/weixin/product/productIntrodction";
	
	@Autowired
	private ApiConfig weixinApiConfig;
	
	@Autowired
	private PayApiConfig payApiConfig;
	

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		// 开发模式
		Object openid = request.getSession().getAttribute("openid");
		if(payApiConfig.isPayDebug()){
			logger.debug("weixin debug model");
			if(StringUtil.isEmpty((String)openid)){
				 request.getSession().setAttribute("openid", "oVnget16WvJKHVwtGd_5iFVjom_E");
			}
			return true;
		}
		logger.debug("weixin not debug model");

		String agent = request.getHeader("user-agent");
		String uri = request.getRequestURI();
		
		// 不需要过滤的请求
		if(uri.contains(LOGIN_URI) || uri.contains("login.html")||uri.contains(NOTIFY_URI) || uri.contains(QRCODE_URI)||uri.contains(PRODUCTINTRO_URI)){
			logger.debug("不过滤的页面，直接走过");
			return true;
		}
		
		// 判断是否是微信浏览器
		if(agent.contains("MicroMessenger")){
			// 是否获取到openid
			if(StringUtil.isNotNull(openid)){
				logger.debug("session 中已经有openid={}",openid);
				return true;
			}else{
				if(uri.contains(AUTH_URI)){
					return true;
				}else{
					OauthAPI oauthAPI = new OauthAPI(weixinApiConfig);
					String oauth_uri=payApiConfig.getDomainName()+"/controller"+WeixinConsts.WEIXIN_PATH+"/oauth/getOauth";
					// 通过微信API静默授权获取用户openid,跳转到OauthController 进行保存openid
					String backUrl = request.getRequestURL().toString() + "?a=1";
					Map<String, Object> paramsMap = request.getParameterMap();

					StringBuffer paramsBuffer = new StringBuffer();
					for (String key : paramsMap.keySet()) {
						paramsBuffer.append("&" + key + "="	+ request.getParameter(key));
					}	
					backUrl+= paramsBuffer.toString();
					
					oauth_uri +="?backurl="+URLEncoder.encode(backUrl);
					logger.debug("oauth_url={}",oauth_uri);
					
					String pageUrl = oauthAPI.getOauthPageUrl(oauth_uri, OauthScope.SNSAPI_BASE, "oo_snsapi_base");
					response.sendRedirect(pageUrl);
				}
			}
		}else{
			response.sendRedirect(request.getContextPath() + LOGIN_URI);
			return false;
		}
		return false;
	}
}
