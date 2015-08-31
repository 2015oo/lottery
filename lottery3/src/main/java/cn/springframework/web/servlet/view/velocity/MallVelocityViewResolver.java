/*  
 * @(#) MallVelocityViewResolver.java Create on 2015年7月18日 下午2:55:19   
 *   
 * Copyright 2015 .ooyanjing
 */


package cn.springframework.web.servlet.view.velocity;

import org.springframework.web.servlet.view.velocity.VelocityViewResolver;

/**
 * @MallVelocityViewResolver.java
 * @created at 2015年7月18日 下午2:55:19 by zhanghongliang@ooyanjing.com
 *
 * @desc
 *
 * @author  zhanghongliang@ooyanjing.com
 * @version $Revision$
 * @update: $Date$
 */
public class MallVelocityViewResolver extends VelocityViewResolver{
	public MallVelocityViewResolver() {
		setViewClass(MallVeloctiyView.class);
	}
}
