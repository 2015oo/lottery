/*  
 * @(#) WeixinLogBiz.java Create on 2015年7月8日 下午5:04:53   
 *   
 * Copyright 2015 .ooyanjing
 */


package cn.oomall.function.weixin.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.ooeyeglass.vcm.mongo.OOMongoLogModel;
import cn.oomall.interceptor.MallBaseLogInterface;

/**
 * @WeixinLogBiz.java
 * @created at 2015年7月8日 下午5:04:53 by zhanghongliang@ooyanjing.com
 *
 * @desc
 *
 * @author  zhanghongliang@ooyanjing.com
 * @version $Revision$
 * @update: $Date$
 */
@Component
public class WeixinLogBiz implements MallBaseLogInterface{
	@Value("${ooweixin.interfaceURI}")
	private String logServerURI;
	@Override
	public String dealLogModel(OOMongoLogModel logModel,
			HttpServletRequest request) {
		logModel.setChannel("weixin");
		String openid = (String) request.getSession().getAttribute("openid");
		openid = StringUtils.defaultIfEmpty(openid, "");
		logModel.setUserId(openid);
		return logServerURI;
	}
}
