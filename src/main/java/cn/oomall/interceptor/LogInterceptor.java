/*  
 * @(#) OOLogFilter.java Create on 2015年7月8日 下午2:09:26   
 *   
 * Copyright 2015 .ooyanjing
 */

package cn.oomall.interceptor;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import cn.ooeyeglass.framework.core.utils.stringUtils.StringUtil;
import cn.ooeyeglass.vcm.mongo.OOMongoLogModel;
import cn.oomall.util.rocketmq.RocketMqProducer;

/**
 * @OOLogFilter.java
 * @created at 2015年7月8日 下午2:09:26 by zhanghongliang@ooyanjing.com
 *
 * @author zhanghongliang@ooyanjing.com
 * @version $Revision$
 * @update: $Date$
 */
@Component
public class LogInterceptor  extends BaseMallInterceptor {
	private Logger logger = LoggerFactory.getLogger(getClass());

	private static final String PRESTART_KEY = "mall_pre_start"; 
	
	@Autowired(required=false)
	private MallBaseLogInterface logBiz;
	
	@Autowired(required=false)
	private RocketMqProducer rocketMqProducer;
	
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		request.setAttribute(PRESTART_KEY, System.currentTimeMillis());
		saveLog(request);
		return true;
	}
	
	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		logger.debug("logger ----------------------------------------------------------");
		logger.debug("请求路径:{}",request.getRequestURI());
		logger.debug("参数:{}",request.getAttributeNames());
		logger.debug("方法:{}",handler);
		logger.debug("响应:{}",modelAndView.toString());
		logger.debug("时长:{} s",(System.currentTimeMillis()-(Long)request.getAttribute(PRESTART_KEY))/1000);
		logger.debug("");
		logger.debug("");
	}
	
	//保存日志
	private void saveLog(HttpServletRequest request) {
		if(isLog(request) && rocketMqProducer!=null){
			OOMongoLogModel model = getLogModel(request);
			try {
				rocketMqProducer.rocketMqSend(JSONObject.fromObject(model).toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			logger.debug("logModel:{}",model);
		}
	}
	
	
	private OOMongoLogModel getLogModel(HttpServletRequest request) {
		OOMongoLogModel model = new OOMongoLogModel();
		String uri = request.getRequestURI();
		
		String contextPath = request.getContextPath();
		uri = StringUtils.removeStart(uri, contextPath);

		model.setCreateTime(System.currentTimeMillis()/1000);;
		model.setUserAgent(request.getHeader("User-Agent"));
		model.setUri(uri);
		model.setSessionId(request.getRequestedSessionId());

		StringBuffer paramsBuffer = new StringBuffer();
		@SuppressWarnings("unchecked")
		Enumeration<String> requestPe = request.getParameterNames();
		while(requestPe.hasMoreElements()){
			String key = requestPe.nextElement();
			String value = request.getParameter(key);
			paramsBuffer.append("&" + key + "=" + value);
		}
		
		model.setParams(StringUtils.removeStart(paramsBuffer.toString(), "&"));
		
		String referer = request.getHeader("Referer");
		logger.debug("referer:{}",referer);
		String host = request.getLocalAddr() + ":" + request.getLocalPort();
		referer = StringUtils.removeStart(referer, "http://" + host);
		model.setReferer(referer);
		
		String[] refParams = StringUtils.split(referer, "?");
		if(refParams!=null && refParams.length>1){
			model.setRefParams(refParams[1]);
		}
		
		model.setIp(getIp(request));
		
		if(logBiz!=null){
			logBiz.dealLogModel(model, request);
		}
		
		logger.debug("log-model:{}",model.toString());
		return model;
	}

	// 判断是否要日志
	// 这里定义 get 与post 而且为form 格式的写，json 的不写日志
	private boolean isLog(HttpServletRequest request) {
		String method = request.getMethod().toLowerCase();
		if ("get".equals(method) || "post".equals(method)) {
		}else{
			return false;
		}
		
		String uri = request.getRequestURI();
		//结尾是html 或者json 或者不带点的
		if(StringUtils.endsWithIgnoreCase(uri,".html") || StringUtils.endsWithIgnoreCase(uri,".json") || StringUtils.contains(uri, ".")==false){
			
		}else{
			return false;
		}
		
		
		/*System.out.println(request.getRequestURI());
		System.out.println(request.getRequestedSessionId());

		Enumeration e = request.getHeaderNames();

		while (e.hasMoreElements()) {
			String a = (String) e.nextElement();
			System.out.println(a + "===" + request.getHeader(a));
		}*/
		return true;
	}

	public String getIp(HttpServletRequest request) {
		if (request == null)
			return "";
		String ip = request.getHeader("X-Requested-For");
		if (StringUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Forwarded-For");
		}
		if (StringUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (StringUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (StringUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (StringUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (StringUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if("0:0:0:0:0:0:0:1".equals(ip)){
			ip = "127.0.0.1";
		}
		if(StringUtils.contains(ip, ",")){
			ip = StringUtils.split(ip,",")[0];
		}
		return ip;
	}
}
