/*  
 * @(#) BaseController.java Create on 2015年4月22日 下午5:11:44   
 *   
 * Copyright 2015 .ooyanjing
 */

package cn.oomall.function.base;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import cn.ooeyeglass.framework.core.handler.MultiViewResource;
import cn.ooeyeglass.framework.core.utils.httpUtils.HttpUtil;
import cn.ooeyeglass.framework.core.utils.jsonUtils.JSONTools;
import cn.ooeyeglass.vcm.vo.VcmBuyerSessionUser;
import cn.oomall.config.MallConstans;
import cn.oomall.interceptor.ApplicationContextInterceptor;
import cn.oomall.interceptor.BrowserInterceptor;

/**
 * <pre>
 * reqeust 参数_d  请求页面时是否直接请求接口，默认0|false 不请求，1|true 请求
 * 			   _t  |1| or |1|2|   1 请求接口时，带session 中VcmBuyerSessionUser 信息
 * 								  2 请求接口时，带session 中LocationModel 坐标信息
 * 			  _b   是否保存历史记录，为回退是用，默认是html请求是t，会自动保存，f时，不保存到历史记录中	
 * </pre>
 * 
 * @BaseController.java
 * @created at 2015年4月22日 下午5:11:44 by zhanghongliang@ooyanjing.com
 * 
 * @author zhanghongliang@ooyanjing.com
 * @version $Revision$
 * @update: $Date$
 */
public abstract class BaseController extends MultiViewResource {
	public Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${mall.serverPath}")
	private String serverPath;
	
	@Value("${mall.imageServerURL}")
	private String imageServerURL;

	/**
	 * 统一请求其他server
	 * 
	 * @Title: postJSON
	 * @data:2015年4月22日下午5:51:03
	 * @author:zhanghongliang@ooyanjing.com
	 * 
	 * @param url
	 * @param json
	 * @return 返回结果统一为json格式，取json 中data 字段也为jsonObject
	 */
	protected JSONObject postJSON(String url, JSONObject json,HttpServletRequest request)throws Exception {
		String ctx = getRequestCtx(request);
		String _serverPath = String.format(serverPath, ctx);
		url = _serverPath + url;
		
		String data ="";
		try {
			//如果有用户id ，每次都会带着
			json.put("_userId", getSessionUserId(request));
			
			// HttpUtil httpUtil = HttpUtil.getInstall();
			logger.debug("send url={}", url);
			logger.debug("send url json={}", json.toString());
			data = HttpUtil.postJsonRequest(url, json.toString());
			logger.debug("send url result ={}", data);
			data = StringUtils.defaultString(data, "{}");
			JSONObject res = JSONObject.fromObject(data);
			return res;
		} catch (Exception e) {
			throw new Exception("链接接口异常");
		}
	}

	public Object returnResponseBody(Object obj, HttpServletRequest request) {
		String jsonpCalback = request.getParameter("jsonpCalback");
		// Msg msg = new Msg(true, "");
		// msg.setData(obj);
		if (StringUtils.isBlank(jsonpCalback)) {
			return obj;
		} else {
			return jsonpCalback + "(" + JSONObject.fromObject(obj).toString()
					+ ")";
		}
	} 
 
	public static VcmBuyerSessionUser getSessionUserInfo(HttpServletRequest request) {
		return (VcmBuyerSessionUser) request.getSession().getAttribute(
				MallConstans.SESSION_CONS.SESSION_USERKEY);
	}
	
	public static String  getSessionUserId(HttpServletRequest request){
		VcmBuyerSessionUser obj = getSessionUserInfo(request);
		if(obj!=null){
			return obj.getBuyerId();
		}
		return "";
	}
	
	/**
	 * 是否绑定用户
	 * @Title: isVisibleUser
	 * @data:2015年8月26日上午11:02:30
	 * @author:zhanghongliang@ooyanjing.com
	 *
	 * @param request
	 * @return
	 */
	public static boolean isVisibleUser(HttpServletRequest request){
		VcmBuyerSessionUser buyer = getSessionUserInfo(request);
		boolean result = false;
		if(buyer!=null && StringUtils.isNoneBlank(buyer.getBuyerId())){
			result = buyer.isVisible();
		}
		return result;
	}

	/**
	 * 获取接口路径
	 * @Title: getInterfaceUri
	 * @data:2015年8月12日下午5:22:11
	 * @author:zhanghongliang@ooyanjing.com
	 *
	 * @param htmlName
	 * @return
	 */
	protected String getInterfaceUri(String htmlName) {
		if(StringUtils.contains(htmlName, "_")){
			return StringUtils.replace(htmlName, "_", "/");
		}else{
			return htmlName+"/" + htmlName+"Data";
		}
	}

	public ModelAndView toHtml(JSONObject currentData,JSONObject requestData,String url,Model model){
		if(currentData==null){
			currentData = new JSONObject();
		}
		if(requestData==null){
			requestData = new JSONObject();
		}
		model.addAttribute("currentData", currentData.toString());// 响应当前页面主要数据(列表除外)
		model.addAttribute("requestData", requestData.toString());// 请求时的参数信息，用户数据回填
		
		model.addAttribute("imageServerURL", imageServerURL);
		return this.toView(url, requestData);
	}
	
	protected String getRequestCtx(HttpServletRequest request) {
		String ctx = (String) request.getAttribute(ApplicationContextInterceptor.requestCtx);
		if(StringUtils.isBlank(ctx)){
			ctx ="wap";
			request.setAttribute(ApplicationContextInterceptor.requestCtx, ctx);
		}
		return ctx;
	}
	
	/**
	 * 是否 微信 true 是， false  否
	 * @Title: isWeixin
	 * @data:2015年8月26日上午10:59:38
	 * @author:zhanghongliang@ooyanjing.com
	 *
	 * @param request
	 * @return
	 */
	protected boolean isWeixin(HttpServletRequest request){
		Boolean boo = (Boolean) request.getAttribute(BrowserInterceptor.REQUEST_BROWSER_WEIXIN);
		return BooleanUtils.toBoolean(boo);
	}
	
	protected void checkPOSTData(JSONObject res) throws Exception{
		if(res!=null){
			if(JSONTools.getBoolean(res, "success")==false){
				throw new RuntimeException("接口异常" +"," + res.toString());
			}
		}
	}
}
