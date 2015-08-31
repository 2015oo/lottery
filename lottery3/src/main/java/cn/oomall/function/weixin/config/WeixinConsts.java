package cn.oomall.function.weixin.config;

import java.util.Hashtable;
import java.util.Map;

public class WeixinConsts {
	public static final String WEIXIN_PATH="/weixin";
	//外网访问接口
	public static final String WEIXIN_INTEFACEPATH="/weixininteface";
	public static Map<String ,Object> OPENID_MAP = new Hashtable<String,Object>() ;
	
	public static class SESSION_CONS{
		/**
		 * 位置信息
		 */
		public static final String AGENT_LOCATION = "agent_location";
		/**
		 * 销售员信息
		 */
		public static final String SALEMAN_INFO = "saleman_info";
		/**
		 * session 中 微信消费者信息
		 */
		public static final String WEIXINBUYER_INFO = "weixinbuyer";

		/**
		 * 推广计划id
		 */
		public static final String PUP_PLAN_CODE= "pup_plan_id";
		/**
		 * 销售员id
		 */
		public static final String PUP_CC_ID= "pup_cc_id";
		
		
		/**
		 * 分享时session信息
		 */
		public static final String SHARE_INFO = "share_info";
		
		
		/**
		 * 针对用户打开分享后单独存储到session中推广计划id
		 */
		public static final String OWER_PUP_PLAN_CODE= "ower_pup_plan_id";
		/**
		 * 针对用户打开分享后单独存储到session中销售员id
		 */
		public static final String OWER_PUP_CC_ID= "ower_pup_cc_id";
		
		/**上一页面的地址*/
		public static final String BACK_UP_URL = "back_up_url";
	}
}
