/*  
 * @(#) HistoryUrlInterceptor.java Create on 2015年7月15日 上午10:31:46   
 *   
 * Copyright 2015 .ooyanjing
 */


package cn.oomall.interceptor;

import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @HistoryUrlInterceptor.java
 * @created at 2015年7月15日 上午10:31:46 by zhanghongliang@ooyanjing.com
 *
 * @author  zhanghongliang@ooyanjing.com
 * @version $Revision$
 * @update: $Date$
 */
@Component
public class HistoryUrlInterceptor extends BaseMallInterceptor{

	public static final String AJAXREQUESTFLAG = "_ajaxRequestFlag";//是否ajax 请求标示
	public static final String SESSION_HISTORYURL = "_session_historyurl";//session中 请求历史路径
	
	private static int MAXSIZE = 10;//session 中存放几个历史地址
	
	private static Logger logger = LoggerFactory.getLogger(HistoryUrlInterceptor.class);
	
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		boolean flag = isAajaxReqeust(request) ;
		request.setAttribute(AJAXREQUESTFLAG, flag);
		//不是ajax 请求的添加历史请求路径
		if((Boolean)request.getAttribute(AJAXREQUESTFLAG)==false){
			//添加返回url 地址缓存
			sessionSaveBackupUrl(request,response);
		}
		return true;
	}
	
	 /**
	  * 判断是否ajax 请求
	 * @Title: isAajaxReqeust
	 * @data:2015年5月27日下午2:57:48
	 * @author:zhanghongliang@ooyanjing.com
	 *
	 * @param request
	 * @return
	 */
	public static boolean isAajaxReqeust(HttpServletRequest request) {
		String contentType = request.getContentType();
		return StringUtils.endsWith(contentType, "json") 
				|| (request.getHeader("X-Requested-With") != null  && "XMLHttpRequest".equals( request.getHeader("X-Requested-With").toString()));
	}

	 /**
	  * 注册前 保留上一 url地址与参数
	 * @Title: responseBuyerBound
	 * @data:2015年5月6日下午1:34:00
	 * @author:zhanghongliang@ooyanjing.com
	 *
	 * @param request
	 * @param response
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	public void sessionSaveBackupUrl(HttpServletRequest request,
			HttpServletResponse response) {
		if (response != null && request != null) {
			//如果有参数_back=false 页面不做会跳记录
			if("f".equals(request.getParameter("_b"))){
				return;
			}
			/*if("1".equals(request.getParameter("backFlag"))){
				return;
			}*/
			HttpSession session = request.getSession();
			String backUpUrl = request.getRequestURI();
			if(StringUtils.endsWith(backUpUrl, "login.html")){
				return;
			}
			
			String context = request.getContextPath();
			backUpUrl = StringUtils.removeStart(backUpUrl, context);
			/*if(StringUtils.contains(backUpUrl, "error")){
				return;
			}
			if(StringUtils.contains(backUpUrl, "style")){
				return;
			}

			if (backUpUrl.indexOf("main/backup") > 0) {
				return;
			}*/

			Map<String, Object> paramsMap = request.getParameterMap();
			backUpUrl += "?_b=f";
			for (String key : paramsMap.keySet()) {
				if(!"_tmp".equals(key)){
					backUpUrl += "&" + key + "="
							+ URLEncoder.encode(request.getParameter(key));
				}
			}

			add(session, backUpUrl);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void add(HttpSession session, String backUpUrl) {
		LinkedList<String> list = (LinkedList<String>) session.getAttribute(SESSION_HISTORYURL);
		
		if(list==null){
			list = new LinkedList<String>();
		}
		
		if(list.size()>0){
			//相同的不保留
			if(list.getFirst().equals(backUpUrl)){
				return;
			}
		}
		
		if(list.size()>MAXSIZE){
			list.removeLast();
		}
		list.addFirst(backUpUrl);
		session.setAttribute(SESSION_HISTORYURL, list);
	}

	/**
	 * 获取history 页面
	 * @Title: get
	 * @data:2015年5月27日下午2:58:33
	 * @author:zhanghongliang@ooyanjing.com
	 * @param flag TODO
	 * @param key
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String get(HttpSession session, int flag){ 
		LinkedList<String> list = (LinkedList<String>) session.getAttribute(SESSION_HISTORYURL);
		String result = null;
		
		if(list!=null && list.size()>0){
			//去除第一个，第二个为要跳转的路径
			if(flag==2){
				list.removeFirst();
			}
			result = list.peekFirst();
		}
		logger.debug("获取历史页面:{}",result);
		return result;
	}
}
