package cn.oomall.function.wap.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.ooeyeglass.framework.core.utils.httpUtils.HttpUtil;
import cn.ooeyeglass.framework.core.utils.stringUtils.StringUtil;
import cn.ooeyeglass.framework.plugins.redis.config.RedisManager;
import cn.ooeyeglass.framework.plugins.redis.exception.RedisAccessException;
import cn.ooeyeglass.vcm.model.user.VcmWeixinbuyer;
import cn.oomall.function.weixin.config.OOWeixinConfig;
import cn.oomall.function.weixin.config.WeixinConsts;
import cn.oomall.function.weixin.handler.OOCSMessageHandle;

import com.fastweixin.api.UserAPI;
import com.fastweixin.api.config.ApiConfig;
import com.fastweixin.api.response.GetUserInfoResponse;
import com.fastweixin.handle.MessageHandle;
import com.fastweixin.message.Article;
import com.fastweixin.message.BaseMsg;
import com.fastweixin.message.NewsMsg;
import com.fastweixin.message.req.BaseEvent;
import com.fastweixin.message.req.LocationEvent;
import com.fastweixin.message.req.QrCodeEvent;
import com.fastweixin.message.util.EmojiFilter;
import com.fastweixin.servlet.WeixinSupport;

/**
 * 哦哦眼镜微信接口
 * @author allen.liu
 *
 */
@Controller
@RequestMapping(value = "/api")
@SuppressWarnings("all")
public class WeixinAPIController extends WeixinSupport{
	
	private static Logger logger = LoggerFactory.getLogger(WeixinAPIController.class);

	@Autowired
	private ApiConfig weixinApiConfig;
	
	@Autowired
	private OOWeixinConfig weixinConfig;
	
	@Autowired
	private RedisManager redisManager;

	@Override
	protected String getAESKey() {
		return null;
	}

	@Override
	protected String getAppId() {
		return weixinApiConfig.getAppid();
	}

	@Override
	protected String getToken() {
		return weixinApiConfig.getToken();
	}
	
    /**
     * 子类重写，加入自定义的微信消息处理器，细化消息的处理
     * 转发消息到多客服
     *
     * @return 微信消息处理器列表
     */
	@Override
    protected List<MessageHandle> initMessageHandles() {
        List<MessageHandle> handles = new ArrayList<MessageHandle>();
        handles.add(new OOCSMessageHandle());
        return handles;
    }
	
    /**
     * 处理地理位置事件
     *
     * @param event 地理位置事件对象
     * @return 响应消息对象
     */
	@Override
    protected BaseMsg handleLocationEvent(LocationEvent event) {
    	//本地存储用户位置信息
    		WeixinConsts.OPENID_MAP.put(event.getFromUserName(), event);
    		logger.debug("微信上报gps 信息:{}",event.toString());
    		logger.debug("上报createTime:{}",new Date(event.getCreateTime()));
    		logger.debug("上报createTime与当前系统时间差距:{}",System.currentTimeMillis()/1000 - event.getCreateTime());
    		try {
    			dealLocationInfo(event);
    		} catch (Exception e) {
				e.printStackTrace();
			}
        return handleDefaultEvent(event);
    }
	
	private void dealLocationInfo(LocationEvent event)
			throws RedisAccessException {
		if(event==null || StringUtils.isBlank(event.getFromUserName())){
			logger.error("locationEvent is null");
			return;
		}
		//1、判断坐标信息是否存在，而且是否一致与之前的
		/**判断距离与之前距离 n 范围内 不做变更*/
		//3km 移动范围不做更新
		double countDistinct = 1000;
		/*final byte[] redisLocationKey = Constants.REDIS_LOCATION_KEY.getBytes();
		String fromUser = event.getFromUserName();
		final byte[] fromUserBytes = fromUser.getBytes();
		
		byte[] b = redisManager.hget(redisLocationKey , fromUserBytes);
		LocationModel locationModel = null;
		if(b!=null &&b.length>0){
			locationModel= (LocationModel ) SerializeUtil.deserializeObject(b);
			logger.debug("redis key:{}",fromUser);
			logger.debug("redis 中有location info 信息:{}",locationModel.toString());
			
			*//**比较坐标信息*//*
			double distance = Distance.GetDistance(event.getLatitude(),event.getLongitude(),locationModel.getLatitude(),locationModel.getLongitude());
			logger.debug("移动距离为:{}",distance);
			if(distance<=countDistinct){
				return;
			}
		}
		if(locationModel==null){
			locationModel = new LocationModel();
		}
		
		locationModel.setLatitude(event.getLatitude());
		locationModel.setLongitude(event.getLongitude());
		locationModel.setCreateTime(event.getCreateTime());
		
		//坐标转换百度坐标，并转后地理位置，一块放到redis 中
		JSONObject jsonmap = GeocodingUtils.geoconv(String.valueOf(event.getLongitude()),
				String.valueOf(event.getLatitude()));
		logger.debug("获取转换ip 结果:{}", jsonmap);
		if (jsonmap != null && jsonmap.size() > 0) {
			locationModel.setX(JSONTools.getDouble(jsonmap, "y"));
			locationModel.setY(JSONTools.getDouble(jsonmap, "x"));
			findCityByXY(locationModel);
			
			redisManager.hdel(redisLocationKey,fromUserBytes);
			locationModel.setMapflag(1);
			
			locationModel.setOldLocationModel(new LocationModel());
			logger.debug("放location 信息到redis 中,\r\nopenId={}\r\nevent={}",
					event.getFromUserName(), locationModel.toString());
			redisManager.hset(redisLocationKey, fromUserBytes, SerializeUtil.serializeObject(locationModel));
		}*/
	}
	
	@SuppressWarnings( { "unchecked", "rawtypes" })
	/*public void findCityByXY(LocationModel locationModel) {
		try {
			List list = new ArrayList();
			JSONObject json = new JSONObject();
			json.put("x", locationModel.getX());
			json.put("y", locationModel.getY());
			list.add(json);

			Map res = GeocodingUtils.geoconvCity(list);
			Map result = (Map) res.get("result");
			Map addressComponent = (Map) result.get("addressComponent");
			locationModel.setAddressComponent(addressComponent);
			String cityname = addressComponent.get("city") + "";
			locationModel.setBaiduGeoconvInfo(result);
			locationModel.setCityname(cityname);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	
    /**
     * 处理扫描二维码事件
     *
     * @param event 扫描二维码事件对象
     * @return 响应消息对象
     */
	@Override
    protected BaseMsg handleQrCodeEvent(QrCodeEvent event) {
		logger.info(event.toString());
        return handleDefaultEvent(event);
    }
	
    /**
     * 处理添加关注事件
     *
     * @param event 添加关注事件对象
     * @return 响应消息对象
     */
    protected BaseMsg handleSubscribe(BaseEvent event) {
    	UserAPI userAPI = new UserAPI(weixinApiConfig);
    	GetUserInfoResponse userInfo  = userAPI.getUserInfo(event.getFromUserName());
    	logger.info("userinfo:"+userInfo.toJsonString());
    	try {
    		// 用户是否订阅该公众号标识，值为0时
    		if(userInfo != null  && userInfo.getSubscribe() != 0){
    			VcmWeixinbuyer weixinBuyer= new VcmWeixinbuyer();
    			weixinBuyer.setOpenid(userInfo.getOpenid());
    			// 过滤Emoji符号
    			weixinBuyer.setNickname(EmojiFilter.filterEmoji(userInfo.getNickname()));
    			weixinBuyer.setGender(String.valueOf(userInfo.getSex()));
    			weixinBuyer.setCity(userInfo.getCity());
    			weixinBuyer.setCountry(userInfo.getCountry());
    			weixinBuyer.setProvince(userInfo.getProvince());
    			weixinBuyer.setLanguage(userInfo.getLanguage());
    			weixinBuyer.setImgurl(userInfo.getHeadimgurl());
    			
    			// 用户关注渠道
    			final String eventKey = event.getEventKey();
    			logger.debug("channelId={}",eventKey);
				if(StringUtil.isNotNull(eventKey)){
    				weixinBuyer.setChannelId(eventKey);
    			}
    			// 关注时间 后台设置
    			// weixinBuyer.setSubscribe_time(DateUtil.toDateTimeString(new Date(),"yyyy-MM-dd HH:mm:ss"));
    			
    			JSONObject json = JSONObject.fromObject(weixinBuyer);
    			try {
    				HttpUtil  httpUtil = HttpUtil.getInstall();
    				// 微信用户关注接口
    				httpUtil.postJsonRequest(weixinConfig.getInterfaceURI()+"weixinbuyer/subscribe",json.toString());
    			} catch (Exception e) {
    				logger.error(e.getMessage());
    			}
    			
    		}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
    	NewsMsg news = new NewsMsg();
    	Article article1 = new Article();
    	article1.setTitle("㊙20部卖座国产电影海报暗藏惊天玄机");
    	article1.setDescription("哦哦眼镜");
    	article1.setPicUrl("https://mmbiz.qlogo.cn/mmbiz/PaoBJe3KibZmcDKMSj2G5whTRWYsr0MqYiau2Qp30qEHJ9uVwdJQU05ePEmiajgArALyrOsJu3YwsnD7R1fFGAEvg/0?wx_fmt=jpeg");
    	article1.setUrl("http://mp.weixin.qq.com/s?__biz=MjM5Njg0OTE1Nw==&mid=208027364&idx=1&sn=520365ff155bb5ce006341023ec50434#rd");
    	news.add(article1);
    	news.add("卖眼镜的真会玩 六月蓉城眼镜节圆满落幕", "https://mmbiz.qlogo.cn/mmbiz/PaoBJe3KibZmcDKMSj2G5whTRWYsr0MqYDPiaoPk63f2fw4SITMgyMqLbhVKT40LBQ5AFC56xV6d1NlD22Ng3GZg/0?wx_fmt=jpeg", "http://mp.weixin.qq.com/s?__biz=MjM5Njg0OTE1Nw==&mid=208027364&idx=2&sn=86642acd8a999a9562cd6bfc5c9b314a#rd");
/*    	Article article1 = new Article();
    	article1.setTitle("Party On！哦哦眼镜端午成都嗨三天");
    	article1.setDescription("六月蓉城眼镜节");
    	article1.setPicUrl("https://mmbiz.qlogo.cn/mmbiz/PaoBJe3KibZmmjXD2hFNSKvoQ5ReDa3xUkeODAzzUE9N7DAy2XbwbsgnyKe8duvibj4Qkn9IM37HTcN5wzicvvaHw/0?wx_fmt=jpeg");
    	article1.setUrl("http://mp.weixin.qq.com/s?__biz=MjM5Njg0OTE1Nw==&mid=207826173&idx=1&sn=bc5fd422be100313646f73b588f57710#rd");
    	news.add(article1);
    	news.add("眼镜节 | 四店义诊眼科专家介绍", "https://mmbiz.qlogo.cn/mmbiz/PaoBJe3KibZmmjXD2hFNSKvoQ5ReDa3xU5dVysq3t0PYCdX6iahlicv4DxM8WSc2bx0iaHL0MVDZS2GvteR7y2y2Iw/0?wx_fmt=jpeg", "http://mp.weixin.qq.com/s?__biz=MjM5Njg0OTE1Nw==&mid=207826173&idx=2&sn=6411e180853c2df2c85c0d9beaeb8bcb#rd");
    	news.add("眼镜节 | 微信摇一摇的正确姿势", "https://mmbiz.qlogo.cn/mmbiz/PaoBJe3KibZmmjXD2hFNSKvoQ5ReDa3xU6YeDj441gLU1Elon6nroiaUa8uvRBKaMbgG8fQeDzmkwFycPOm3Mq4A/0?wx_fmt=jpeg", "http://mp.weixin.qq.com/s?__biz=MjM5Njg0OTE1Nw==&mid=207826173&idx=3&sn=20e762c11854de6cc1be9f225eab5760#rd");
    	news.add("眼镜节 | 大光明眼镜旗舰店促销活动","https://mmbiz.qlogo.cn/mmbiz/PaoBJe3KibZmmjXD2hFNSKvoQ5ReDa3xUSicjcqV0599yy3AtXk2ib0QGb4pzibOHhFWmiahWNLIxC2Rk12Ser8zBMg/0?wx_fmt=jpeg","http://mp.weixin.qq.com/s?__biz=MjM5Njg0OTE1Nw==&mid=207826173&idx=4&sn=9ba0ea42c76ca374546a01843a16e706#rd");
    	news.add("眼镜节 | 精华眼镜春熙路店促销活动", "https://mmbiz.qlogo.cn/mmbiz/PaoBJe3KibZmmjXD2hFNSKvoQ5ReDa3xUNuDf6vloEbU4ghoFZNkxqVm4cia5icdpR6XPJEfgENiaFuovSLavNzfng/0?wx_fmt=jpeg", "http://mp.weixin.qq.com/s?__biz=MjM5Njg0OTE1Nw==&mid=207826173&idx=5&sn=cf5dc20a67f7dce010f633b2a0e2bf4f#rd");
    	news.add("眼镜节 | 诺贝尔眼镜川大北门旗舰店活动","https://mmbiz.qlogo.cn/mmbiz/PaoBJe3KibZmmjXD2hFNSKvoQ5ReDa3xU57vHbR6DNqPDzjuh1Vl4WaMBMO42YicX895QrMFFpJxFlricaYTPm6IQ/0?wx_fmt=jpeg","http://mp.weixin.qq.com/s?__biz=MjM5Njg0OTE1Nw==&mid=207826173&idx=6&sn=62b79c62b5a1ae8eccd55287a0b716d1#rd");
    	news.add("眼镜节 | 浙文眼镜交大沃尔玛店促销活动", "https://mmbiz.qlogo.cn/mmbiz/PaoBJe3KibZmmjXD2hFNSKvoQ5ReDa3xUAWNUWa7iawGDGIN8JYgddc35kibDsnRibsUHDHkH7ibKT808NsibqpjPic1Q/0?wx_fmt=jpeg", "http://mp.weixin.qq.com/s?__biz=MjM5Njg0OTE1Nw==&mid=207826173&idx=7&sn=0bfd902445c118cb172dd6f33f60afe0#rd");
    	news.add("父亲节 | 我的盖世英雄，如今双眼朦胧", "https://mmbiz.qlogo.cn/mmbiz/PaoBJe3KibZmmjXD2hFNSKvoQ5ReDa3xULO9WLoWw2QlVdjkWdrK5S5okIibibXldSuDApS2HibFXXt0F1lj7mIWAg/0?wx_fmt=jpeg", "http://mp.weixin.qq.com/s?__biz=MjM5Njg0OTE1Nw==&mid=207826173&idx=8&sn=50a54320dee42fe02243d3c7b8e2af27#rd");*/
    	return  news;
//		return new TextMsg(
//				"毕竟蓉城六月中，风光不与四时同；\n专家义诊零收费，特价促销礼不停。\n 6月20-22日六月蓉城眼镜节火爆开启\n 大光明眼镜旗舰店（春熙路）\n 精华眼镜春熙路店\n 诺贝尔眼镜川大北门旗舰店\n 浙文眼镜交大沃尔玛店\n 四店同庆\n 打折、特价、微信摇一摇、玩购端午不停歇。\n 更多礼品，更多优惠，尽在哦哦眼镜。");
    	
//        return new TextMsg("发春季，怎能不发春？\n猛戳下方捡便宜，各种优惠扑面而来！\n即点即用，百城万店，镜在掌中！\n哦哦让您随心购镜！");
    }
    /**
     * 处理取消关注事件
     *
     * @param event 取消关注事件对象
     * @return 响应消息对象
     */
	protected BaseMsg handleUnsubscribe(BaseEvent event) {
		JSONObject json = new JSONObject();
		if (StringUtil.isNotNull(event.getFromUserName())) {
			json.put("openid", event.getFromUserName());
			try {
				HttpUtil httpUtil = HttpUtil.getInstall();
				// 微信用户取消关注接口
				httpUtil.postJsonRequest(weixinConfig.getInterfaceURI()
						+ "weixinbuyer/unsubscribe", json.toString());
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}

		return null;
	}
    
	@RequestMapping(value = "",method=RequestMethod.GET)
	@ResponseBody
	protected final String get(HttpServletRequest request) {
        if (isLegal(request)) {
            //绑定微信服务器成功
            return request.getParameter("echostr");
        } else {
            //绑定微信服务器失败
            return "";
        }
	}
	@RequestMapping(value = "",method=RequestMethod.POST)
	@ResponseBody
	 protected final String post(HttpServletRequest request)throws ServletException, IOException {
        if (!isLegal(request)) {
            String signature = request.getParameter("signature");
            String timestamp = request.getParameter("timestamp");
            String nonce = request.getParameter("nonce");
        	logger.debug("signature:{} timestamp:{} nonce:{}", new Object[]{signature,timestamp,nonce});
            return "";
        }
        return processRequest(request);
    }
}
