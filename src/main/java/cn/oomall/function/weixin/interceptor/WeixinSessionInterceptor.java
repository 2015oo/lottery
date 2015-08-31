/*  
 * @(#) WeixinSessionInterceptor.java Create on 2015年7月15日 上午10:21:09   
 *   
 * Copyright 2015 .ooyanjing
 */


package cn.oomall.function.weixin.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * <pre>
 * 微信session 用户拦截器
 * 1、订单相关的需要用户登录
 * 2、个人中心相关的需要用户登录
 * 
 * </pre>
 * 
 * @WeixinSessionInterceptor.java
 * @created at 2015年7月15日 上午10:21:09 by zhanghongliang@ooyanjing.com
 *
 * @author  zhanghongliang@ooyanjing.com
 * @version $Revision$
 * @update: $Date$
 */
public class WeixinSessionInterceptor extends HandlerInterceptorAdapter{
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		return super.preHandle(request, response, handler);
	}
	
	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		super.postHandle(request, response, handler, modelAndView);
	}
}
