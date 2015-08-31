/*  
 * @(#) BaseInterceptor.java Create on 2015年8月12日 下午2:34:20   
 *   
 * Copyright 2015 .ooyanjing
 */


package cn.oomall.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * @BaseInterceptor.java
 * @created at 2015年8月12日 下午2:34:20 by zhanghongliang@ooyanjing.com
 *
 * @author  zhanghongliang@ooyanjing.com
 * @version $Revision$
 * @update: $Date$
 */
public abstract class BaseMallInterceptor extends HandlerInterceptorAdapter{
	protected Logger logger = LoggerFactory.getLogger(getClass());
}
