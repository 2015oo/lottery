/*  
 * @(#) CommonController.java Create on 2015年8月19日 下午2:09:24   
 *   
 * Copyright 2015 .ooyanjing
 */


package cn.oomall.function.base;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cn.ooeyeglass.framework.core.rest.Msg;
import cn.ooeyeglass.framework.core.utils.jsonUtils.JSONTools;
import cn.ooeyeglass.vcm.vo.LocationModel;
import cn.oomall.config.MallConstans;
import cn.oomall.interceptor.ApplicationContextInterceptor;
import cn.oomall.interceptor.HistoryUrlInterceptor;
import cn.oomall.util.bmap.BaiduAddressUtils;
import cn.oomall.util.bmap.GeocodingUtils;

/**
 * @CommonController.java
 * @created at 2015年8月19日 下午2:09:24 by zhanghongliang@ooyanjing.com
 *
 * @desc
 *
 * @author  zhanghongliang@ooyanjing.com
 * @version $Revision$
 * @update: $Date$
 */
public class CommonController extends BaseController{
	/**
	 * post json interface 接口
	 * 
	 * @Title: doPostJSONInterfaceURI
	 * @param request
	 * @param _t
	 * @param interfaceURL
	 * @return
	 */
	@RequestMapping("doPostJSONInterfaceURI.json")
	@ResponseBody
	public Object doPostJSONInterfaceURI(HttpServletRequest request,
			@RequestParam(required = false, defaultValue = "") String _t,
			String interfaceURL) throws Exception{
		JSONObject json = getJsonObject();
		if (json == null) {
			json = new JSONObject();
		}
		if (StringUtils.contains(_t, "|1|")) {
			// 需要session信息
			json.put("sessionUserInfo", getSessionUserInfo(request));
//			json.put("userId", getSessionUserId(request));
		}
		
		if (StringUtils.contains(_t, "|2|")) {
			// 需要地理位置信息
			Object obj = request.getSession().getAttribute(MallConstans.SESSION_CONS.AGENT_LOCATION);
			if(obj!=null){
				json.put("location", obj);
			}
		}
		return returnResponseBody(postJSON(interfaceURL, json,request), request);
	}

	@RequestMapping("{htmlName}.html")
	public ModelAndView doHtml(@PathVariable String htmlName,
			HttpServletRequest request, Model model,
			@RequestParam(required = false, defaultValue = "|1|") String _t,
			@RequestParam(required=false,defaultValue="false")  boolean _d) throws Exception{
		JSONObject json = getJsonObject();
		String ctx = getRequestCtx(request);
		String itUrl = getInterfaceUri(htmlName);
		
		if (null == json) {
			json = new JSONObject();
		}
		JSONObject datas = null;
		if(_d){
			Object data = doPostJSONInterfaceURI(request, _t, itUrl);
			JSONObject res = JSONObject.fromObject(data);
			checkPOSTData(res);
			datas = JSONTools.getJSONObject(res, "data");
			
		}
		String url = ctx +"/" + htmlName;
		return toHtml(datas, json, url , model);
	}

	@RequestMapping(value="{prx}/{htmlName}.html")
	public ModelAndView doHtml(@PathVariable String prx,@PathVariable String htmlName,
			HttpServletRequest request,Model model,
			@RequestParam(required = false, defaultValue = "|1|") String _t,
			@RequestParam(required=false,defaultValue="false")  boolean _d) throws Exception{
		JSONObject json = getJsonObject();
		String ctx = getRequestCtx(request);
		String itUrl = getInterfaceUri(htmlName);
		
		if (null == json) {
			json = new JSONObject();
		}
		JSONObject datas = null;
		if(_d){
			Object data = doPostJSONInterfaceURI(request, _t, itUrl);
			JSONObject res = JSONObject.fromObject(data);
			checkPOSTData(res);
			datas = JSONTools.getJSONObject(res, "data");
		}
		String url = ctx +"/" + prx +"/" + htmlName ;
		return toHtml(datas, json, url , model);
	}

	/**
	 * 放session 中用户地理坐标信息
	 * @Title: putLocaltionToSession
	 * @data:2015年7月17日下午6:37:57
	 * @author:zhanghongliang@ooyanjing.com
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping("putLocaltionToSession.json")
	@ResponseBody
	public Object putLocaltionToSession(HttpServletRequest request) {
		final JSONObject jsonObject = getJsonObject();
		String longitude = JSONTools.getString(jsonObject, "longitude");
		String latitude = JSONTools.getString(jsonObject, "latitude");
		JSONObject jsonmap = GeocodingUtils.geoconv(longitude, latitude);
		logger.debug("获取转换坐标 结果:{}", jsonmap);
		LocationModel locationModel = new LocationModel();
		Msg result = new Msg(false, "");
		if (jsonmap != null && jsonmap.size() > 0) {
			locationModel.setX(JSONTools.getDouble(jsonmap, "y"));
			locationModel.setY(JSONTools.getDouble(jsonmap, "x"));
			BaiduAddressUtils.findCityByXY(locationModel);
			
			Set<String> activityCity = new HashSet<String>();
			activityCity.add("成都市");
			activityCity.add("北京市");
			
			if(!activityCity.contains(locationModel.getOrgCity())){
				locationModel.setActivity(false);
			}
			locationModel.setMapflag("1");
			request.getSession().setAttribute(
					MallConstans.SESSION_CONS.AGENT_LOCATION, locationModel);
			result.setSuccess(true);
		}
		return result;
	}
	
	@RequestMapping("goBack.html")
	public String goBack(HttpServletRequest request){
		String uri = HistoryUrlInterceptor.get(request.getSession(), 0);
		String requestCtx = (String) request.getAttribute(ApplicationContextInterceptor.requestCtx);
		if(StringUtils.isBlank(uri)){
			uri = requestCtx + "/index.html";
		}
//		uri = uri.replaceFirst("/", "");
		return "forward:" + uri;
	}
	
	@RequestMapping("error.html")
	public ModelAndView error(Model model,HttpServletRequest request,RedirectAttributes attributes){
		String ctx = (String) request.getAttribute(ApplicationContextInterceptor.requestCtx);
		String uri = "/error";
		boolean debugFlag = (Boolean)request.getAttribute("debugFlag");
		if(debugFlag){
			uri = "/error2";
		}
//		request.getParameterMap();
//		request.getAttribute(HistoryUrlInterceptor.AJAXREQUESTFLAG);
//		request.getAttribute("exception");
		
		Exception ex = (Exception) request.getSession().getAttribute("mall_exception");
		if(ex!=null){
			model.addAttribute("ex", ex);
			model.addAttribute("exstr", ExceptionUtils.getStackTrace(ex));
			request.getSession().removeAttribute("mall_exception");
		}
		
		return toHtml(null, null, ctx + uri, model);
	}
}
