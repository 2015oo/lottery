/*  
 * @(#) WeixinController.java Create on 2015年7月13日 上午11:33:02   
 *   
 * Copyright 2015 .ooyanjing
 */

package cn.oomall.function.wap.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cn.oomall.function.base.CommonController;

/**
 * @WeixinController.java
 * @created at 2015年7月13日 上午11:33:02 by zhanghongliang@ooyanjing.com
 *
 * @author zhanghongliang@ooyanjing.com
 * @version $Revision$
 * @update: $Date$
 */
@Controller
@SuppressWarnings("all")
@RequestMapping("/")
@Scope("prototype")
public class WapController extends CommonController{
	@RequestMapping("index.html")
	public ModelAndView toIndex(HttpServletRequest request, HttpServletResponse response,Model model) throws Exception{
		if("index".equals(request.getParameter("_from"))){
			//是从公众号菜单跳转过来的。
			return doHtml("index", request, model, "", true);
		}
		String offlineShopId = (String) request.getSession().getAttribute("_offlineShopId");
		if(StringUtils.isBlank(offlineShopId)){
			return doHtml("index", request, model, "", true);
		}
		
		//TODO 判断有线下门店 id，是云子商城，重定向到云子商城首页
		String uri = "";
		response.sendRedirect(uri);
		return null;
	}
}
