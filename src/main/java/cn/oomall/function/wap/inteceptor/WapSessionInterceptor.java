/*  
 * @(#) WapSessionInterceptor.java Create on 2015年7月28日 上午9:21:39   
 *   
 * Copyright 2015 .ooyanjing
 */


package cn.oomall.function.wap.inteceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import cn.ooeyeglass.vcm.vo.VcmBuyerSessionUser;
import cn.oomall.function.base.BaseController;

/**
 * @WapSessionInterceptor.java
 * @created at 2015年7月28日 上午9:21:39 by zhanghongliang@ooyanjing.com
 *
 * @author  zhanghongliang@ooyanjing.com
 * @version $Revision$
 * @update: $Date$
 */
@Component
public class WapSessionInterceptor  extends HandlerInterceptorAdapter {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
//		String ctx = (String) request.getAttribute(NspApplicationContextInterceptor.requestCtx);
		String uri = request.getRequestURI();
		VcmBuyerSessionUser buyerSession = BaseController.getSessionUserInfo(request);
		if(buyerSession==null){
			logger.debug("请求路径:{}",uri);
			logger.debug("用户未登录，需要登录");
			request.getRequestDispatcher("login.html").forward(request, response);
			return false;
		}
		
		return true;
	}
}
