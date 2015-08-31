/*  
 * @(#) WapBuyerController.java Create on 2015年7月27日 下午4:03:45   
 *   
 * Copyright 2015 .ooyanjing
 */

package cn.oomall.function.wap.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.ooeyeglass.framework.core.utils.jsonUtils.JSONTools;
import cn.ooeyeglass.vcm.model.user.VcmBuyer;
import cn.ooeyeglass.vcm.model.user.VcmWeixinbuyer;
import cn.ooeyeglass.vcm.vo.VcmBuyerSessionUser;
import cn.oomall.config.MallConstans;
import cn.oomall.function.base.BaseController;
import cn.oomall.interceptor.ApplicationContextInterceptor;
import cn.oomall.interceptor.HistoryUrlInterceptor;

import com.fastweixin.util.MapUtils;

/**
 * @WapBuyerController.java
 * @created at 2015年7月27日 下午4:03:45 by zhanghongliang@ooyanjing.com
 *
 * @desc
 *
 * @author zhanghongliang@ooyanjing.com
 * @version $Revision$
 * @update: $Date$
 */
@Controller
@Scope("prototype")
public class WapUserController extends BaseController {
	/**
	 * wap 登录
	 * @Title: doLogin
	 * @data:2015年7月27日下午4:06:46
	 * @author:zhanghongliang@ooyanjing.com
	 *
	 * @param phone		手机号
	 * @param vcode		 密码或验证码	
	 * @param loginFlag  1 密码登录  2 短信验证登录
	 * @return
	 */
	@RequestMapping("login.json")
	@ResponseBody
	public Object doLogin(String username, String password,
			@RequestParam(required = false, defaultValue = "1") String loginFlag,
			HttpServletRequest request) throws Exception{
		Map<String, Object> result = new HashMap<String, Object>();
		String backUrl  = HistoryUrlInterceptor.get(request.getSession(), 1);
		result.put("code", "001");
		result.put("backUrl", backUrl);
		JSONObject json = new JSONObject();
		json.put("username", username);
		json.put("password", password);
		json.put("type",loginFlag);
		String openid = (String)request.getSession().getAttribute("openid");
		if(StringUtils.isNotBlank(openid)){ // 存在openid  
			VcmBuyerSessionUser oldSeesionUser = (VcmBuyerSessionUser)request.getSession().getAttribute(MallConstans.SESSION_CONS.SESSION_USERKEY);
			if(!oldSeesionUser.isVisible()){ // 并且buyer用户 未启用
				json.put("openid", openid);
				String url = "buyer/weixinlogin";
				JSONObject response =	postJSON(url , json, request);
				logger.debug(response.toString());

				JSONObject responseData = JSONTools.getJSONObject(response, "data");
				if("001".equals(JSONTools.getString(responseData, "code"))){
					//登录成功
					JSONObject userData = JSONTools.getJSONObject(responseData, "buyer");
					JSONObject wxData = JSONTools.getJSONObject(responseData, "weixinbuyer");
					@SuppressWarnings("unchecked")
					VcmBuyer buyer = MapUtils.toObject(VcmBuyer.class,userData,true);
					VcmWeixinbuyer wx = MapUtils.toObject(VcmWeixinbuyer.class,wxData,true);
					VcmBuyerSessionUser s = new VcmBuyerSessionUser();
					s.setBuyerId(buyer.getId());
					s.setVisible(buyer.getVisible());
					s.setBuyer(buyer);
					s.setOpenUser(wx);
					result.put("buyerId", buyer.getId());
					request.getSession().setAttribute(MallConstans.SESSION_CONS.SESSION_USERKEY, s);
				}else{
					result.put("code", "002");
					result.put("msg", JSONTools.getString(responseData, "msg"));
				}
			}

		}else{
			String url = "buyer/waplogin";
			JSONObject response =	postJSON(url , json, request);
			logger.debug(response.toString());

			JSONObject responseData = JSONTools.getJSONObject(response, "data");
			if("001".equals(JSONTools.getString(responseData, "code"))){
				//登录成功
				JSONObject userData = JSONTools.getJSONObject(responseData, "buyer");
				@SuppressWarnings("unchecked")
				VcmBuyer buyer = MapUtils.toObject(VcmBuyer.class,userData,true);
				
				VcmBuyerSessionUser s = new VcmBuyerSessionUser();
				result.put("buyerId", buyer.getId());
				s.setBuyerId(buyer.getId());
				s.setVisible(buyer.getVisible());
				s.setBuyer(buyer);
				request.getSession().setAttribute(MallConstans.SESSION_CONS.SESSION_USERKEY, s);
			}else{
				result.put("code", "002");
				result.put("msg", JSONTools.getString(responseData, "msg"));
			}
		}
		
		/*Msg resultMsg = new Msg();
		resultMsg.setMsg((String)result.get("msg"));
		resultMsg.setCode((String)result.get("code"));
		resultMsg.setSuccess("001".equals(resultMsg.getCode()));*/
		return result ;
	
	}
	
	/**
	 * 登录页面
	 * @Title: toLogin
	 * @data:2015年7月28日上午9:55:44
	 * @author:zhanghongliang@ooyanjing.com
	 *
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("login.html")
	public ModelAndView toLogin(Model model,HttpServletRequest request){
		if(getSessionUserInfo(request)!=null){
			//已经登录成功
			return toHtml(null, null, "redirect:index.html", model);
		}
		
		
		String ctx = (String)request.getAttribute(ApplicationContextInterceptor.requestCtx);
		String uri = ctx + "/login2";
		//短信验证的模板
		JSONObject currentData = new JSONObject();
		currentData.put("smsContent", "使用短信验证登录，验证码[%s]，有效时间10分钟");
		currentData.put("smsVoildTime", 10*60);
		return toHtml(currentData, null, uri, model);
	}

	/**
	 * 登录成功跳转
	 * @Title: loginSuccess
	 * @data:2015年8月27日上午9:51:57
	 * @author:zhanghongliang@ooyanjing.com
	 *
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("loginSuccess.html")
	public String loginSuccess(Model model,HttpServletRequest request){
		if(getSessionUserInfo(request)!=null){
			//登录成功
			String  uri = HistoryUrlInterceptor.get(request.getSession(), 2);
			uri = StringUtils.defaultString(uri, "index.html");
			logger.debug("登录成功，跳转地址为：{}",uri);
			return "redirect:" + uri;
		}
		return "login.html";
	}
	
	/**
	 * 注册页面
	 * @Title: toReg
	 * @data:2015年7月28日上午9:55:37
	 * @author:zhanghongliang@ooyanjing.com
	 *
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("register.html")
	public ModelAndView toReg(Model model,HttpServletRequest request){
		String ctx = (String)request.getAttribute(ApplicationContextInterceptor.requestCtx);
		String uri = ctx + "/register";
		return toHtml(null, null, uri, model);
	}
	
}
