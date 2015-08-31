/*  
 * @(#) WeixinController.java Create on 2015年7月13日 上午11:33:02   
 *   
 * Copyright 2015 .ooyanjing
 */

package cn.oomall.function.weixin.web;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.HttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.ooeyeglass.framework.core.utils.jsonUtils.JSONTools;
import cn.ooeyeglass.framework.core.utils.stringUtils.StringUtil;
import cn.oomall.function.base.BaseController;
import cn.oomall.function.weixin.config.OOWeixinConfig;
import cn.oomall.function.weixin.config.WeixinConsts;

/**
 * @WeixinController.java
 * @created at 2015年7月13日 上午11:33:02 by zhanghongliang@ooyanjing.com
 *
 * @author zhanghongliang@ooyanjing.com
 * @version $Revision$
 * @update: $Date$
 */
@Controller
@SuppressWarnings("all")
@RequestMapping("/weixin")
public class WeixinController extends BaseController {

	@Autowired
	private OOWeixinConfig weixinConfig;
}
