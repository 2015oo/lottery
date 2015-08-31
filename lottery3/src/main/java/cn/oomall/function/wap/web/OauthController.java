package cn.oomall.function.wap.web;

import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.ooeyeglass.framework.core.utils.jsonUtils.JSONTools;
import cn.ooeyeglass.framework.core.utils.stringUtils.StringUtil;
import cn.ooeyeglass.vcm.model.user.VcmBuyer;
import cn.ooeyeglass.vcm.model.user.VcmWeixinbuyer;
import cn.ooeyeglass.vcm.vo.VcmBuyerSessionUser;
import cn.oomall.config.MallConstans;
import cn.oomall.function.base.BaseController;
import cn.oomall.function.weixin.config.WeixinConsts;

import com.fastweixin.api.OauthAPI;
import com.fastweixin.api.config.ApiConfig;
import com.fastweixin.api.config.PayApiConfig;
import com.fastweixin.api.enums.OauthScope;
import com.fastweixin.api.response.GetUserInfoResponse;
import com.fastweixin.api.response.OauthGetTokenResponse;
import com.fastweixin.util.MapUtils;

/**
 * 微信授权
 * @author allen.liu
 *
 */
@Controller
@Scope("prototype")
@RequestMapping(value = WeixinConsts.WEIXIN_PATH+"/oauth")
@SuppressWarnings("all")
public class OauthController extends BaseController{
	@Autowired
	private ApiConfig weixinApiConfig;
	
	@Autowired
	private PayApiConfig payApiConfig;

	/**
	 * 只获取用户openid 
	 * @Title: getOauth
	 * @data:2015年8月6日上午10:45:00
	 * @author:zhanghongliang@ooyanjing.com
	 *
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "getOauth")
	public String getOauth(final ModelMap model,HttpServletRequest request,HttpServletResponse response){
		String code = request.getParameter("code");
		String state = request.getParameter("state");
		String backurl = request.getParameter("backurl");
		OauthAPI oauthAPI = new OauthAPI(weixinApiConfig);
		//静默授权后 取得用户openid 存储到session中
		OauthGetTokenResponse wxresponse = oauthAPI.getToken(code);
		String openid = wxresponse.getOpenid();
		
		JSONObject resjson =  new JSONObject();
		resjson.put("openid", openid);
		String url= "wxbuyer/wexinUserLoginByOpenId";
		// 请求订单确认信息
		try {
			JSONObject responsejson =	postJSON(url , resjson, request);
			logger.debug(responsejson.toString());
			if("001".equals(JSONTools.getString(responsejson, "code"))){
				JSONObject responsejsonData = JSONTools.getJSONObject(responsejson, "data");
				VcmBuyerSessionUser sessionuser = (VcmBuyerSessionUser) JSONObject.toBean((JSONObject) responsejsonData.get("sessionuser"), VcmBuyerSessionUser.class);
				request.getSession().setAttribute("openid", openid);
				request.getSession().setAttribute(MallConstans.SESSION_CONS.SESSION_USERKEY, sessionuser);
			}else{
				backurl+="&weixinoauth=_confirm";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return toWeixinView(backurl); 

	}

	/**
	 * 需要用户确认授权，可以拉取用户信息
	 * @Title: getConfirmOauth
	 * @data:2015年8月6日上午10:44:45
	 * @author:zhanghongliang@ooyanjing.com
	 *
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "getConfirmOauth")
	public String getConfirmOauth(final ModelMap model,HttpServletRequest request,HttpServletResponse response){
		String code = request.getParameter("code");
		String state = request.getParameter("state");
		String backurl = request.getParameter("backurl");
		
		if(request.getSession().getAttribute("weixinuser")!=null){
			return toWeixinView(backurl);
		}
		
		OauthAPI oauthAPI = new OauthAPI(weixinApiConfig);
		//静默授权后 取得用户openid 存储到session中
		OauthGetTokenResponse wxresponse = oauthAPI.getToken(code);
		// 需要用户确认授权
		GetUserInfoResponse user =	oauthAPI.getUserInfo(wxresponse.getAccessToken(), wxresponse.getOpenid());
		logger.debug("weixinuserinfo={}",user.getNickname()+user.getCountry());
		
		JSONObject resjson =  new JSONObject();
		resjson.put("weixinUser", user);
		String url= "wxbuyer/wexinUserLoginByUserInfo";
		// 请求订单确认信息
		try {
//			JSONObject data_json = postJSON(url, resjson,request);
//			if(data_json!= null && StringUtil.isNotNull(data_json)){
			JSONObject responsejson =	postJSON(url , resjson, request);
			logger.debug(responsejson.toString());
			if("001".equals(JSONTools.getString(responsejson, "code"))){
				JSONObject responsejsonData = JSONTools.getJSONObject(responsejson, "data");
				VcmBuyerSessionUser sessionuser = (VcmBuyerSessionUser) JSONObject.toBean((JSONObject) responsejsonData.get("sessionuser"), VcmBuyerSessionUser.class);
				request.getSession().setAttribute("openid", wxresponse.getOpenid());
				request.getSession().setAttribute(MallConstans.SESSION_CONS.SESSION_USERKEY, sessionuser);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return toWeixinView(backurl);
	}

	private String toWeixinView(String backurl) {
		logger.debug("backurl={}",backurl);
		if(StringUtils.isNotEmpty(backurl)){
			return "redirect:" + backurl;
		}else{
			return "redirect:" + payApiConfig.getDomainName();
		}
	}
}
