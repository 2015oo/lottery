/*  
 * @(#) OOBaseLogInterface.java Create on 2015年7月8日 下午3:32:58   
 *   
 * Copyright 2015 .ooyanjing
 */


package cn.oomall.interceptor;

import javax.servlet.http.HttpServletRequest;

import cn.ooeyeglass.vcm.mongo.OOMongoLogModel;

/**
 * @OOBaseLogInterface.java
 * @created at 2015年7月8日 下午3:32:58 by zhanghongliang@ooyanjing.com
 *
 * @desc
 *
 * @author  zhanghongliang@ooyanjing.com
 * @version $Revision$
 * @update: $Date$
 */
public interface MallBaseLogInterface {
	/**
	 * 扩展 log 业务类，不往logModel 添加 userId，与 channel，otherInfo 为各自业务扩展信息
	 * @Title: dealLogModel
	 * @data:2015年7月8日下午3:33:59
	 * @author:zhanghongliang@ooyanjing.com
	 *
	 * @param logModel
	 * @param request
	 * @return 返回请求接口的路径
	 */
	public String dealLogModel(OOMongoLogModel logModel,HttpServletRequest request);
}
