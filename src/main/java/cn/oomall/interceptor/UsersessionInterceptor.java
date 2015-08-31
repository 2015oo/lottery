/*  
 * @(#) MallUsersessionInterceptor.java Create on 2015年8月12日 下午3:27:38   
 *   
 * Copyright 2015 .ooyanjing
 */


package cn.oomall.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.ooeyeglass.framework.core.rest.Msg;
import cn.ooeyeglass.vcm.vo.VcmBuyerSessionUser;
import cn.oomall.config.MallConstans;
import cn.oomall.function.base.BaseController;

/**
 * @MallUsersessionInterceptor.java
 * @created at 2015年8月12日 下午3:27:38 by zhanghongliang@ooyanjing.com
 *
 * @author  zhanghongliang@ooyanjing.com
 * @version $Revision$
 * @update: $Date$
 */
@Component
public class UsersessionInterceptor extends BaseMallInterceptor{
	

	@Value("${mall.debugflag}")
	private boolean debugFlag;
	
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		//校验是否有用户session 信息，如果没有，跳转登录页面
		Object sessionUser = BaseController.getSessionUserInfo(request);
		logger.info("sessionUser:{}",sessionUser);
		
		
		if(sessionUser==null){
			boolean debug = debugFlag;
			if(debug){
				VcmBuyerSessionUser session = new VcmBuyerSessionUser();
//				session.setBuyerId("40289a814f1bc12d014f1bd9ef70002e");
				session.setBuyerId("402881134e43dbad014e43f1ebd20000");
				session.setVisible(true);
				request.getSession().setAttribute(MallConstans.SESSION_CONS.SESSION_USERKEY, session);
				return true;
			}
			logger.warn("没有session 用户信息");
			
			if((Boolean)request.getAttribute(HistoryUrlInterceptor.AJAXREQUESTFLAG)){
				//是ajax 请求
				Msg msg = new Msg(false,"无session 用户信息");
				msg.setCode("003");
				response.getWriter().write(msg.toJSONObject().toString());
				response.getWriter().flush();
				return false;
			}
			
			response.sendRedirect("/wap/login.html");
			return false;
		}
		
		return true;
	}
	
}
