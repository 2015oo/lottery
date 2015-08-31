/*  
 * @(#) WapOrderController.java Create on 2015年8月19日 下午1:59:13   
 *   
 * Copyright 2015 .ooyanjing
 */


package cn.oomall.function.wap.web;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import cn.ooeyeglass.framework.core.utils.MathUtils.MathUtil;
import cn.ooeyeglass.framework.core.utils.jsonUtils.JSONTools;
import cn.ooeyeglass.framework.core.utils.stringUtils.StringUtil;
import cn.ooeyeglass.vcm.model.mall.order.VcmOrder;
import cn.ooeyeglass.vcm.model.mall.order.VcmOrderDeliverInfo;
import cn.ooeyeglass.vcm.model.mall.order.VcmOrderItem;
import cn.oomall.config.MallConstans;
import cn.oomall.function.base.BaseController;
import cn.oomall.util.BrowserUtil;

import com.alipay.util.AlipayNotify;

/**
 * @WapOrderController.java
 * @created at 2015年8月19日 下午1:59:13 by zhanghongliang@ooyanjing.com
 *
 * @desc
 *
 * @author  zhanghongliang@ooyanjing.com
 * @version $Revision$
 * @update: $Date$
 */ 
@Controller
@Scope("prototype")
@RequestMapping("/")
public class WapOrderController extends BaseController{
	/**
	 * 订单保存
	 * @Title: ordersave
	 * @data:2015年8月20日上午10:32:31
	 * @author:liuyang@ooyanjing.com
	 *
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("ordersave.json")
	public String ordersave(Model model,RedirectAttributesModelMap redirectAttributes,HttpServletRequest request) throws Exception{
/*		String tokenkey = JSONTools.getString(getJsonObject(), "tokenkey");
		String sessiontokenkey = (String)request.getSession().getAttribute("ootokenkey");
		if(StringUtil.isNotNull(tokenkey)&&StringUtil.isNotNull(tokenkey)&&tokenkey.equals(sessiontokenkey)){
			request.getSession().removeAttribute("ootokenkey");
		}else{
			return "redirect:../main/index";
		}*/
		
		// 用户id从session中取得
		String buyerId = getSessionUserId(request);
		
		// 有无发票  0 无  1 有
		String isBill = JSONTools.getString(getJsonObject(), "isBill");
		String billType = JSONTools.getString(getJsonObject(), "billType");
		String billTittle = JSONTools.getString(getJsonObject(), "billTittle");
		// 物流方式 1自提2物流
		String buyway = JSONTools.getString(getJsonObject(),"buyway");
		String buyerMessage = JSONTools.getString(getJsonObject(), "buyerMessage");
		//商品的总价钱
		String productTotalPrice = JSONTools.getString(getJsonObject(),"productTotalPrice");
		// 订单总价格
		String totalPrice = JSONTools.getString(getJsonObject(),"totalPrice");
		// 支付价格
		String payablefee = JSONTools.getString(getJsonObject(),"payablefee");
		//收货信息
		String addressid = JSONTools.getString(getJsonObject(),"addressid");
		String phone = JSONTools.getString(getJsonObject(),"pickupphone"); // 提货人手机号
		String pickname = JSONTools.getString(getJsonObject(),"pickupkname"); //提货人姓名
		
		String addname = JSONTools.getString(getJsonObject(),"addname"); //收货人姓名
		String addphone = JSONTools.getString(getJsonObject(),"addphone"); //收货人电话
		String adddetail = JSONTools.getString(getJsonObject(),"adddetail"); //收货人详细地址
		// 商品数量
		String number = JSONTools.getString(getJsonObject(),"number");
		// 商铺id
		String shopid = JSONTools.getString(getJsonObject(),"shopId");
		
		String productId = JSONTools.getString(getJsonObject(),"productId");
		
		String detalId = JSONTools.getString(getJsonObject(),"detailid");
		
		String offlineShopId = JSONTools.getString(getJsonObject(),"offlineshopid");
		//（优惠劵）匹配码
		String ruleCode = JSONTools.getString(getJsonObject(),"ruleCode");
		
		Map map = new HashMap();
		//订单
		VcmOrder vcmOrder = new VcmOrder();
		vcmOrder.setShopid(shopid);
		vcmOrder.setBuyerid(buyerId);
		vcmOrder.setIsBill(isBill);
		vcmOrder.setBuyerMessage(buyerMessage);
		if("1".equals(isBill)){
			vcmOrder.setBillTittle(billTittle);
			vcmOrder.setBillType(billType);
		}
		
		//商品总价钱
		Double ProductTotalPriced = MathUtil.doubleValue(MathUtil.getBigDecimal(productTotalPrice));
		vcmOrder.setProductTotalPrice(ProductTotalPriced);
		
		if(StringUtil.isNotNull(ruleCode)){
			vcmOrder.setCouponStatus("used");
			vcmOrder.setChannel("weixin");
			vcmOrder.setCouponRuleCode(ruleCode);
		}else{
			vcmOrder.setCouponStatus("unused");
		}
		
		// 订单总价
		vcmOrder.setTotalPrice(MathUtil.doubleValue(MathUtil.getBigDecimal(totalPrice)));
		// 应付款(实际需要支付的费用)
		vcmOrder.setPayablefee(MathUtil.doubleValue(MathUtil.getBigDecimal(payablefee)));
		
		//订单明细
		VcmOrderItem orderItem = new VcmOrderItem();
		
		//购买商品
		orderItem.setBuyerid(buyerId);
		orderItem.setProductId(productId);
		orderItem.setAmount(Integer.valueOf(number));
		orderItem.setProductType("1");
		orderItem.setDetailId(detalId);
		// 设置订单渠道
		orderChannal(vcmOrder,orderItem, request);
		
		//订单的配送信息
		VcmOrderDeliverInfo orderDeliverInfo = new VcmOrderDeliverInfo();
		orderDeliverInfo.setOfflineId(offlineShopId);
		if("1".equals(buyway)){
			//自取
			vcmOrder.setDeliverType("1");
			orderDeliverInfo.setMobile(phone);
			orderDeliverInfo.setRecipients(pickname);
		}else{
			//物流配送
			vcmOrder.setDeliverType("2");
			if (StringUtil.isNotNull(addressid)) {
				map.put("addressid", addressid);
			} else {
				orderDeliverInfo.setAddress(adddetail);
				orderDeliverInfo.setRecipients(addname);
				orderDeliverInfo.setMobile(addphone);
			}
		}
		
		map.put("Order", vcmOrder);
		map.put("OrderItem", orderItem);
		map.put("OrderDeliver", orderDeliverInfo);
		JSONObject jsonObject =  new JSONObject();
		jsonObject.putAll(map);
		String url = "order/orderSave";
		JSONObject response =	postJSON(url , jsonObject, request);
		logger.debug(response.toString());

		JSONObject responseData = JSONTools.getJSONObject(response, "data");
		if("001".equals(JSONTools.getString(response, "code"))){
			//订单保存成功
			@SuppressWarnings("unchecked")
//			VcmOrder saveorder = MapUtils.toObject(VcmOrder.class,JSONTools.getJSONObject(responseData, "saveorder"),false);
			VcmOrder saveorder = (VcmOrder) JSONObject.toBean((JSONObject) responseData.get("saveorder"), VcmOrder.class);
			@SuppressWarnings("unchecked")
//			VcmOrderItem saveOrderItem = MapUtils.toObject(VcmOrderItem.class,JSONTools.getJSONObject(responseData, "saveorderitem"),false);
			VcmOrderItem saveOrderItem = JSONTools.JSONToBean(responseData.getJSONObject("saveorderitem"), VcmOrderItem.class);
//			VcmOrderItem saveOrderItem = (VcmOrderItem) JSONObject.toBean((JSONObject) responseData.get("saveorderitem"), VcmOrderItem.class);
			// 请求转发中  存放支付信息
			 redirectAttributes.addFlashAttribute("order", saveorder);
			 redirectAttributes.addFlashAttribute("orderItem", saveOrderItem);
		}
		if(BrowserUtil.checkWeixinBrowser(request)){//判断是否是微信浏览器
			return "redirect:order/weixinpay.html?showwxpaytitle=1";
		}else{
			return "redirect:zhifubaopay.html";
		}
	}
	
	/**
	 * 未支付订单去支付
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "orderpay.html")
	public String orderPay(final ModelMap model,RedirectAttributesModelMap redirectAttributes,HttpServletRequest request)throws Exception{
		String url = "order/orderpay";
		JSONObject response =postJSON(url , getJsonObject(), request);
		logger.debug(response.toString());
		JSONObject responseData = JSONTools.getJSONObject(response, "data");
		if("001".equals(JSONTools.getString(response, "code"))){
			//订单
			@SuppressWarnings("unchecked")
			VcmOrder saveorder = (VcmOrder) JSONObject.toBean((JSONObject) responseData.get("saveorder"), VcmOrder.class);
			@SuppressWarnings("unchecked")
			VcmOrderItem saveOrderItem = JSONTools.JSONToBean(responseData.getJSONObject("saveorderitem"), VcmOrderItem.class);
			// 请求转发中  存放支付信息
			 redirectAttributes.addFlashAttribute("order", saveorder);
			 redirectAttributes.addFlashAttribute("orderItem", saveOrderItem);
		}
		if(BrowserUtil.checkWeixinBrowser(request)){//判断是否是微信浏览器
			return "redirect:order/weixinpay.html?showwxpaytitle=1";
		}else{
			return "redirect:zhifubaopay.html";
		}
	}
	
	/**
	 * 支付宝支付成功通知页面
	 * @Title: wapzhifubaoreturn
	 * @data:2015年8月25日下午4:30:00
	 * @author:liuyang@ooyanjing.com
	 *
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "order/wapzhifubaoreturn.html")
	public ModelAndView wapzhifubaoreturn(final Model model, HttpServletRequest request) throws Exception {
		logger.debug("wap zhifubao return ");
		HttpSession session= request.getSession();
		JSONObject currentData = new JSONObject();
		currentData.put("payablefee", session.getAttribute("payablefee"));
		currentData.put("productname", session.getAttribute("productname"));
		session.removeAttribute("payablefee");
		session.removeAttribute("productname");
		//获取支付宝GET过来反馈信息
		Map<String,String> params = new HashMap<String,String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			params.put(name, valueStr);
		}
		
		//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
		//商户订单号

		String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");

		//支付宝交易号

		String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

		//交易状态
		String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");

		//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
		
		//计算得出通知验证结果
		boolean verify_result = AlipayNotify.verify(params);
		
		String ctx = getRequestCtx(request);
		if(verify_result){//验证成功
			//////////////////////////////////////////////////////////////////////////////////////////
			//请在这里加上商户的业务逻辑程序代码

			//——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
			if(trade_status.equals("TRADE_FINISHED") || trade_status.equals("TRADE_SUCCESS")){
				//判断该笔订单是否在商户网站中已经做过处理
					//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
					//如果有做过处理，不执行商户的业务程序
			}
			logger.debug("wap zhifubao return  true");
			//该页面可做页面美工编辑
			//out.println("验证成功<br />");
			return this.toHtml(currentData, null,ctx +  "/order/order_paySuccess", model);
			//——请根据您的业务逻辑来编写程序（以上代码仅作参考）——

			//////////////////////////////////////////////////////////////////////////////////////////
		}else{
			//该页面可做页面美工编辑
			//out.println("验证失败");
			logger.debug("wap zhifubao return  false");
			return this.toHtml(currentData, null, ctx + "/order/order_payFailure", model);
		}
	}
	
	/**
	 * 设置 订单渠道
	 * @param order
	 * @param request
	 */
	private void orderChannal(VcmOrder order,VcmOrderItem orderItem, HttpServletRequest request){
		/**
		 * 销售员id
		 */
		String pubccid  = (String)request.getSession().getAttribute(MallConstans.SESSION_CONS.OWER_PUP_CC_ID);
		if(StringUtil.isNotNull(pubccid)){
			String pupplancode  = (String)request.getSession().getAttribute(MallConstans.SESSION_CONS.OWER_PUP_PLAN_CODE);
			order.setChannel("weixin_saleman");
			order.setChannelValue(pubccid);
			orderItem.setMarketingPlanCode(pupplancode);
		}else{
			String agent = request.getHeader("user-agent");
			// 判断是否是微信浏览器
			if (agent.contains("MicroMessenger")) {
				order.setChannel("weixin");
			} else {
				order.setChannel("wap");
			}
			
		}
	}
	
	@RequestMapping("order/order_confirm.html")
	public ModelAndView ordreConfirm(HttpServletRequest request,HttpServletResponse response,Model model) throws Exception{
		String ctx = getRequestCtx(request);
		if(!isVisibleUser(request) && isWeixin(request)){ //无效用户 并且是微信浏览器
			// 跳转登陆页面
			request.getRequestDispatcher("/"+ctx+"/login.html").forward(request, response);
			return null;
		}
		JSONObject json = getJsonObject();
		
		if (null == json) {
			json = new JSONObject();
		}
		JSONObject datas = null;
		String interfaceURL = "order/confirm";
		Object data = returnResponseBody(postJSON(interfaceURL , json,request), request);
		JSONObject res = JSONObject.fromObject(data);
		checkPOSTData(res);
		datas = JSONTools.getJSONObject(res, "data");
		String url = ctx +"/order/order_confirm";
		return toHtml(datas, json, url , model);
	}

	/**
	 * 订单评价页面
	 * @Title: orderCmooentary
	 * @data:2015年8月27日下午5:15:49
	 * @author:zhanghongliang@ooyanjing.com
	 *
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("order/order_cmooentary.html")
	public ModelAndView orderCmooentary(HttpServletRequest request,HttpServletResponse response,Model model
			,@RequestParam(required=true) String ordercode) throws Exception{
		String ctx = getRequestCtx(request);
		JSONObject json = getJsonObject();
		
		if (null == json) {
			json = new JSONObject();
		}
		
		JSONObject datas = null;
		String interfaceURL = "order/detail";
		Object data = returnResponseBody(postJSON(interfaceURL , json,request), request);
		JSONObject res = JSONObject.fromObject(data);
		checkPOSTData(res);
		datas = JSONTools.getJSONObject(res, "data");
		
		String url = ctx +"/order/order_cmooentary.html";
		return toHtml(datas, json, url , model);
	}
}
