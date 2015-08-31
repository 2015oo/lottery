/*  
 * @(#) WapBuyerControlller.java Create on 2015年8月23日 下午3:09:03   
 *   
 * Copyright 2015 .ooyanjing
 */


package cn.oomall.function.wap.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.oomall.function.base.BaseController;

/**
 * @WapBuyerControlller.java
 * @created at 2015年8月23日 下午3:09:03 by zhanghongliang@ooyanjing.com
 *
 * @desc
 *
 * @author  zhanghongliang@ooyanjing.com
 * @version $Revision$
 * @update: $Date$
 */
@Controller
@Scope("prototype")
@RequestMapping("buyer")
public class WapBuyerControlller extends BaseController{
	
	/**
	 * 领取优惠券
	 * @Title: getCoupon
	 * @data:2015年8月23日下午3:10:49
	 * @author:zhanghongliang@ooyanjing.com
	 *
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("getCoupon.json")
	@ResponseBody
	public Object getCoupon(HttpServletRequest request,HttpServletResponse response,Model model) throws Exception{
		String  code = request.getParameter("code");
		String userId = getSessionUserId(request);
		String url = "coupon/buyerGetCoupon";
		JSONObject json = new JSONObject();
		json.put("couponCode", code);
		json.put("buyerId",userId);
		json.put("receiveSource","receive");
		json.put("channel","weixin");
		return postJSON(url, json , request);
	}
	
	/**
	 * 保存收藏
	 * @Title: saveFavority
	 * @data:2015年8月24日上午11:29:54
	 * @author:zhanghongliang@ooyanjing.com
	 *
	 * @return
	 */
	@RequestMapping("saveFavority.json")
	public Object saveFavority(HttpServletRequest request,HttpServletResponse response,Model model
			,@RequestParam(required=true,value="offlineshopid")String offlineShopId,
			@RequestParam(required=false,value="productid")String productId,
			@RequestParam(required=true,value="type",defaultValue="0")String type) throws Exception{
		String url = "favorities/save";
		JSONObject json = new JSONObject();
		json.put("offlineshopid", offlineShopId);
		json.put("productid", productId);
		json.put("buyerid", getSessionUserId(request));
		json.put("type", type);
		json.put("channel", "wap");
		return postJSON(url, json , request);
	}
}
