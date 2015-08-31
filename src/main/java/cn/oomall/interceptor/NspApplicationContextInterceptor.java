/*  
 * @(#) MyTestInterceptor.java Create on 2013-10-8 下午3:28:55   
 *   
 * Copyright 2013 by pztx.   
 */

package cn.oomall.interceptor;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import cn.ooeyeglass.framework.core.filter.PagerFilter;
import cn.ooeyeglass.framework.core.logs.LogEnabled;
import cn.ooeyeglass.vcm.vo.LocationModel;
import cn.oomall.config.MallConstans;
import cn.oomall.function.base.BaseController;

/**
 * 获取上下文路径
 * @author andy.li
 *
 */
@Component(value="nspApplicationContextInterceptor")
public class NspApplicationContextInterceptor extends HandlerInterceptorAdapter implements LogEnabled {

	public static final String requestCtx = "_requestCtx";
	

	@Value("${mall.debugflag}")
	private boolean debugFlag;
	
	public boolean preHandle(HttpServletRequest request,HttpServletResponse response, Object handler) throws Exception {
		initCtx(request);
		return true;
	}

	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		Map<String, Object> ctxMap = new HashMap<String, Object>();
		if (modelAndView != null) {
			String viewName = modelAndView.getViewName();
			/*获取mvc的上下文路径*/
			//上下文加 web.xml 的spring 配置路径
			String requestCtx = (String) request.getAttribute(NspApplicationContextInterceptor.requestCtx);
			String envRoot=PagerFilter.getRootPath()+ "/" +requestCtx ;  
			log.debug("viewName : "+viewName);
			ctxMap.put("ctx",PagerFilter.getRootPath());
			ctxMap.put("viewName", viewName);
			ctxMap.put("mvcRoot", requestCtx);
			ctxMap.put("envRoot",envRoot);
			
			ctxMap.put("ctx", request.getAttribute("ctx"));
			ctxMap.put("requestName", request.getAttribute("_requestName"));
			ctxMap.put("requestCtx", request.getAttribute("_requestCtx"));
			ctxMap.put("sessionID", request.getRequestedSessionId());
			modelAndView.addObject("_project", ctxMap);
			
			modelAndView.addObject("_sessionUser", JSONObject.fromObject(BaseController.getSessionUserInfo(request)).toString());
			modelAndView.addObject("_sessionUserId", BaseController.getSessionUserId(request));
			LocationModel locationModel = (LocationModel) request.getSession().getAttribute(MallConstans.SESSION_CONS.AGENT_LOCATION);
			modelAndView.addObject("_locationMode",JSONObject.fromObject(locationModel).toString());
			modelAndView.addObject("_debugFlag",debugFlag);
		}
	}

	private void initCtx(HttpServletRequest request){
		try {
			String uri =request.getRequestURI();
			String contextPath = request.getContextPath();
			String requestCtx = getRequestCtx(uri, contextPath);
			
			int index1=uri.lastIndexOf("/");
			int index2 = uri.lastIndexOf(".");
			if(index1<index2){
				String requestName = uri.substring(index1+1,index2);
				request.setAttribute("_requestName", requestName);
			}
			request.setAttribute(NspApplicationContextInterceptor.requestCtx, requestCtx);
			request.setAttribute("ctx", contextPath);
			request.setAttribute("debugFlag", debugFlag);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String getRequestCtx(String uri, String contextPath) {
		uri = uri.replaceAll(contextPath, "");
//		uri = uri.substring(0, uri.lastIndexOf("/"));
//		uri = uri.replaceAll("/", "");
		uri = StringUtils.removeStart(uri, "/");
		uri = StringUtils.substring(uri, 0, uri.indexOf("/"));
		return uri;
	}
	
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		if(ex!=null && ex instanceof ServletException){
			request.getRequestDispatcher("error.html").forward(request, response);
		}
	}
}
