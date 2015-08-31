/*  
 * @(#) WapPayController.java Create on 2015年8月18日 下午5:56:12   
 *   
 * Copyright 2015 .ooyanjing
 */


package cn.oomall.function.wap.web;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cn.ooeyeglass.framework.core.utils.stringUtils.StringUtil;
import cn.ooeyeglass.vcm.model.mall.order.VcmOrder;
import cn.ooeyeglass.vcm.model.mall.order.VcmOrderItem;
import cn.oomall.function.base.BaseController;

import com.alipay.config.AlipayConfig;
import com.alipay.util.AlipayNotify;
import com.alipay.util.AlipaySubmit;
import com.alipay.util.UtilDate;
import com.fastweixin.api.PayAPI;
import com.fastweixin.api.config.ApiConfig;
import com.fastweixin.api.config.PayApiConfig;
import com.fastweixin.message.pay.JsAPIPayMsg;
import com.fastweixin.message.pay.PrepayMsg;
import com.fastweixin.util.pay.PayUtil;

/**
 * @WapPayController.java
 * @created at 2015年8月18日 下午5:56:12 by liuyang@ooyanjing.com
 *
 * @desc
 *
 * @author  liuyang@ooyanjing.com
 * @version $Revision$
 * @update: $Date$
 */
@Controller
@SuppressWarnings("all")
@RequestMapping("/")
public class WapPayController extends BaseController{

	@Autowired
	private PayApiConfig payApiConfig;
	
	@Autowired
	private ApiConfig weixinApiConfig;
	
	@RequestMapping(value = "order/weixinpay.html")
	public ModelAndView weixinpay(final ExtendedModelMap model, HttpServletRequest request) throws Exception {
		String ctx = getRequestCtx(request);
		
		VcmOrder vcmorder = (VcmOrder) model.get("order");
		VcmOrderItem orderItem = (VcmOrderItem) model.get("orderItem");
//		model.addAttribute("order", vcmorder);
//		model.addAttribute("orderItem", orderItem);
		JSONObject currentData = new JSONObject();
		currentData.put("payablefee", vcmorder.getPayablefee());
		currentData.put("productname", orderItem.getProductName());
		if (vcmorder != null) {
				PrepayMsg prepayMsg = new PrepayMsg(weixinApiConfig,
						payApiConfig);
				if (orderItem != null
						&& StringUtil.isNotNull(orderItem.getProductName())) {
					prepayMsg.setBody(orderItem.getProductName()); // 商品描述。
				} else {
					prepayMsg.setBody("哦哦眼镜");
				}
				prepayMsg.setOut_trade_no(vcmorder.getCode());// 商户系统内部的订单号
				// 订单总金额 * 100 (微信支付金额单位为 分)
				prepayMsg.setTotal_fee(new Double(
						vcmorder.getPayablefee() * 100).longValue());
				String ip = PayUtil.getIp(request);
				if(StringUtils.isBlank(ip) || "unknown".equals(ip)){
					ip = "10.170.188.213";
				}
				logger.debug("ip={}",ip);
				prepayMsg.setSpbill_create_ip(ip); // ip
				prepayMsg.setOpenid((String) request.getSession().getAttribute(
						"openid"));
				if (orderItem != null
						&& StringUtil.isNotNull(orderItem.getProductName())) {
					prepayMsg.setAttach(orderItem.getProductName()); // 商品描述。
				} else {
					prepayMsg.setAttach("哦哦眼镜");
				}
				prepayMsg.setTrade_type("JSAPI");
				prepayMsg.setNotify_url(payApiConfig.getNotifyUrl()+"/weixin");
				
				PayAPI payAPI = new PayAPI(weixinApiConfig.getAppid(), payApiConfig);
				Map<String, String> map = payAPI.getAPIPrepayId(prepayMsg);
				logger.debug(" payAPI.getJSAPIPrepayId map={}", map.toString());
				JsAPIPayMsg payMsg = new JsAPIPayMsg(weixinApiConfig,
						payApiConfig);
				payMsg.setPackageValue(map.get("prepay_id"));
				String userAgent = request.getHeader("user-agent");
				char agent = userAgent.charAt(userAgent
						.indexOf("MicroMessenger") + 15);
				payMsg.setAgent(new String(new char[] { agent }));// 微信版本号，用于前面提到的判断用户手机微信的版本是否是5.0以上版本。
				// 生成支付sign
				payMsg.createPaySign("UTF-8", payMsg.getSortedMap());
				model.addAttribute("pay", payMsg);
				currentData.put("pay", payMsg);
				return this.toHtml(currentData, getJsonObject(), ctx+"/order/order_payment", model);
		} else {
			return this.toHtml(currentData, getJsonObject(), ctx+"/order/order_payFailure", model);
		}
	}
	
	
	@RequestMapping(value = "zhifubaopay.html")
	public void zhifubaopay(final ModelMap model, HttpServletRequest request,HttpServletResponse response) throws Exception {
		VcmOrder vcmorder = (VcmOrder) model.get("order");
		VcmOrderItem orderItem = (VcmOrderItem) model.get("orderItem");
		//支付类型
		String payment_type = "1";
		//必填，不能修改
		//服务器异步通知页面路径
		String notify_url = payApiConfig.getNotifyUrl()+"/wapzhifubao";
		//需http://格式的完整路径，不能加?id=123这类自定义参数

		//页面跳转同步通知页面路径
		String return_url = "http://waptest.ooyanjing.com/wap/order/wapzhifubaoreturn.html";
		//需http://格式的完整路径，不能加?id=123这类自定义参数，不能写成http://localhost/

		//商户订单号
		String out_trade_no = vcmorder.getCode();
		//商户网站订单系统中唯一订单号，必填
		String productName = orderItem.getProductName();
		String subject ="哦哦眼镜";
		String body ="哦哦眼镜";
		if(StringUtil.isNotNull(productName)){
			//订单名称
			subject = productName;
			//订单描述
			body = productName;
		}
		//必填

		//付款金额
		String total_fee = vcmorder.getPayablefee().toString();
		// 支付宝方式支付存储商品名支付价格到seesion中
		HttpSession session= request.getSession();
		session.setAttribute("payablefee", vcmorder.getPayablefee());
		session.setAttribute("productname", productName);
		//必填
		
		//商品展示地址
		String show_url = "http://www.ooyanjing.com/shop/index.php?act=goods&op=index&goods_id=238";
		//必填，需以http://开头的完整路径，例如：http://www.商户网址.com/myorder.html

		//选填

		//超时时间
		String it_b_pay = "";
		//选填

		//钱包token
		String extern_token = "";
		//选填
		
		//////////////////////////////////////////////////////////////////////////////////
		
		//把请求参数打包成数组
		Map<String, String> sParaTemp = new HashMap<String, String>();
		sParaTemp.put("service", "alipay.wap.create.direct.pay.by.user");
        sParaTemp.put("partner", AlipayConfig.partner);
        sParaTemp.put("seller_id", AlipayConfig.seller_id);
        sParaTemp.put("_input_charset", AlipayConfig.input_charset);
		sParaTemp.put("payment_type", payment_type);
		sParaTemp.put("notify_url", notify_url);
		sParaTemp.put("return_url", return_url);
		sParaTemp.put("out_trade_no", out_trade_no);
		sParaTemp.put("subject", subject);
		sParaTemp.put("total_fee", total_fee);
		sParaTemp.put("show_url", show_url);
		sParaTemp.put("body", body);
		sParaTemp.put("it_b_pay", it_b_pay);
		sParaTemp.put("extern_token", extern_token);
		String sHtmlText = AlipaySubmit.buildRequest(sParaTemp,"get","确认");
		//建立请求
		OutputStream  out = response.getOutputStream();
		out.write(sHtmlText.getBytes());
		out.close();
		
	}
	
}
