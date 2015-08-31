/*  
 * @(#) MyException.java Create on 2015年7月28日 下午11:05:57   
 *   
 * Copyright 2015 .ooyanjing
 */


package cn.oomall.exception;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import cn.ooeyeglass.framework.core.rest.Msg;
import cn.oomall.interceptor.HistoryUrlInterceptor;

/**
 * @MyException.java
 * @created at 2015年7月28日 下午11:05:57 by zhanghongliang@ooyanjing.com
 *
 * @author  zhanghongliang@ooyanjing.com
 * @version $Revision$
 * @update: $Date$
 */
public class MallExceptionHandler implements HandlerExceptionResolver {  
	   
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, 
        Object handler, Exception ex) {  
        Map<String, Object> model = new HashMap<String, Object>();  
        model.put("ex", ex);  
        model.put("exstr", org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace(ex));
        request.setAttribute("exception", ex);
        Boolean boo = HistoryUrlInterceptor.isAajaxReqeust(request);
        if(boo!=null && boo){
        	Msg msg = new Msg(false, "后台异常:" + ex.getMessage());
        	msg.setData(ExceptionUtils.getFullStackTrace(ex));
        	try {
				response.getWriter().write(JSONObject.fromObject(msg).toString());
				response.getWriter().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	return null;
        }
        //跳页面的到controller 中处理
        request.getSession().setAttribute("mall_exception", ex);
		return new ModelAndView("redirect:" + "/wap/error.html");
        
    }  
}
