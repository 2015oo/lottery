/*  
 * @(#) MallVeloctiyView.java Create on 2015年7月18日 下午2:48:08   
 *   
 * Copyright 2015 .ooyanjing
 */


package cn.springframework.web.servlet.view.velocity;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.context.Context;
import org.springframework.web.servlet.view.velocity.VelocityView;

/**
 * @MallVeloctiyView.java
 * @created at 2015年7月18日 下午2:48:08 by zhanghongliang@ooyanjing.com
 *
 * @author  zhanghongliang@ooyanjing.com
 * @version $Revision$
 * @update: $Date$
 */
public class MallVeloctiyView extends VelocityView{
	protected void doRender(Context context, HttpServletResponse response,String requestCtx)
			throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Rendering Velocity template [" + getUrl() + "] in VelocityView '" + getBeanName() + "'");
		}
		mergeTemplate(getTemplate("/common/oomall_"+requestCtx+"Head.html"), context, response);
		mergeTemplate(getTemplate(), context, response);
		mergeTemplate(getTemplate("/common/oomall_"+requestCtx+"Foot.html"), context, response);
	}
	
	@Override
	protected void renderMergedTemplateModel(Map<String, Object> model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		exposeHelpers(model, request);

		Context velocityContext = createVelocityContext(model, request, response);
		exposeHelpers(velocityContext, request, response);
		exposeToolAttributes(velocityContext, request);
		
		//自定义实现，添加header 与foot 通用模板

		String requestCtx = (String) request.getAttribute("_requestCtx");
		doRender(velocityContext, response,requestCtx );
	}
}
