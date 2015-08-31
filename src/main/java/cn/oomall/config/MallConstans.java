package cn.oomall.config;


public class MallConstans {
	public static class SESSION_CONS{
		/**
		 * session 中用户信息
		 */
		public static final String SESSION_USERKEY = "session_mall_user";
	
		/**
		 * 位置信息
		 */
		public static final String AGENT_LOCATION = "agent_location";
		
		/**
		 * DefaultMQProducer
		 */
		public static final String DEFAULT_MQPRODUCER = "Producer";
		

		/**
		 * PushTopic
		 */
		public static final String PUSH_TOPIC = "PushTopic";
		
		/**
		 * 针对用户打开分享后单独存储到session中推广计划id
		 */
		public static final String OWER_PUP_PLAN_CODE= "ower_pup_plan_id";
		/**
		 * 针对用户打开分享后单独存储到session中销售员id
		 */
		public static final String OWER_PUP_CC_ID= "ower_pup_cc_id";
	}
}
