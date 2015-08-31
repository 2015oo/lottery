package cn.oomall.util.bmap;

import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.ooeyeglass.framework.core.utils.httpUtils.HttpUtil;
import cn.ooeyeglass.framework.core.utils.jsonUtils.JSONTools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class GeocodingUtils {

	private static Logger logger = LoggerFactory
			.getLogger(GeocodingUtils.class);

	
	/**
	 * 地址换取 x,y
	 * 
	 * @param user_addr
	 * @return
	 * @throws Exception
	 */
	public static double[] obtainLngLat(String user_addr) throws Exception {
		double[] ret = new double[2];
		try {
			String encodedUserAddress = URLEncoder.encode(user_addr, "UTF-8");
			LinkedHashMap<String, String> paramsMap = new LinkedHashMap<String, String>();
			paramsMap.put("output", "json");
			String url = "output=json";
			paramsMap.put("ak", Constants.MAP_BAIDUMAP_AK);
			url += "&ak=" + Constants.MAP_BAIDUMAP_AK;
			paramsMap.put("address", user_addr);
			url += "&address=" + encodedUserAddress;
			String sn = SnCal.calSn(paramsMap);
			paramsMap.put("sn", sn);
			url += "&sn=" + sn;
			url = Constants.MAP_BAIDUMAP_URL + url;
			HttpUtil httpUtil = HttpUtil.getInstall();
			String response = httpUtil.sendGetRequest(url, null);
			logger.debug("user_addr:{},baidumap response:{}", user_addr,
					response);
			JSONObject jsonObject = JSON.parseObject(response);
			int status = jsonObject.getIntValue("status");
			if (status != 0)
				return null;
			JSONObject location = jsonObject.getJSONObject("result")
					.getJSONObject("location");
			double lng = location.getDoubleValue("lng");
			double lat = location.getDoubleValue("lat");
			logger.debug("lng:{},lat:{}", lng, lat);
			ret[0] = lng;
			ret[1] = lat;
		} catch (Exception e) {
			logger.error("GeocodingUtils>obtainLngLat", e);
			throw e;
		}
		return ret;
	}

//	public static void main(String[] args) {
//		try {
//
//			// 根据地址请求x,y
//			// GeocodingUtils.obtainLngLat("北京市东城区 雍和宫");
//
//			// 地址转换
//			Map map1 = new HashMap();
//			map1.put("x", "39.906403");
//			map1.put("y", "116.458358");
//			// Map map2 = new HashMap();
//			// map2.put("x", "114.21892734521");
//			// map2.put("y", "29.575429778924");
//			// list.add(map2);
//			//
////			JSONObject jsonObject = geoconv(map1);
////			System.out.println(jsonObject);
//
//			Map jsonObject = (Map)geoconv("39.906403","116.458358");
//			System.out.println(jsonObject);
//			// x,y 转换成地址
//			// Map map1 = new HashMap();
//			// map1.put("x", "39.983424");
//			// map1.put("y", "116.322987");
//			//
//			// List list = new ArrayList();
//			// list.add(map1);
//			//
//			// Map map = geoconvCity(list);
//			// Map result = (Map) map.get("result");
//			// Map addressComponent = (Map) result.get("addressComponent");
//			// String cityname = addressComponent.get("city") + "";
//			// System.out.println(cityname);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	/*
	 * 百度地图坐标转换API是一套以HTTP形式提供的坐标转换接口，用于将常用的非百度坐标
	 * 目前支持GPS设备获取的坐标、google地图坐标、soso地图坐标、amap地图坐标、mapbar地图坐标
	 * 转换成百度地图中使用的坐标，并可将转化后的坐标在百度地图JavaScript API、车联网API、静态图API、web服务API等产品中使用
	 */
	public static JSONObject geoconv(Map<String, Object> map) {
		StringBuffer sb = new StringBuffer();
		if (map != null && map.size() > 0) {
				sb.append(map.get("x"));
				sb.append(",");
				sb.append(map.get("y"));
			String url = Constants.MAP_BAIDUMAP_GEOCONV_URL + "coords=" + sb
					+ "&from=3&to=5&ak=" + Constants.MAP_BAIDUMAP_GEOCONV_AK
					+ "&output=json";
			HttpUtil httpUtil = HttpUtil.getInstall();
			String json = httpUtil.sendGetRequest(url, null);
			JSONObject jsonObject = JSON.parseObject(json);
			return jsonObject;
		} else {
			return null;
		}

	}

	 /**
	  * 坐标转换 这里只转换1：GPS设备获取的角度坐标; 到5：百度地图采用的经纬度坐标
	 * @Title: geoconv
	 * @data:2015年5月13日上午9:36:45
	 * @author:zhanghongliang@ooyanjing.com
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public static net.sf.json.JSONObject geoconv(String x, String y) {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append(x);
			sb.append(",");
			sb.append(y);
			String url = Constants.MAP_BAIDUMAP_GEOCONV_URL + "coords=" + sb
					+ "&from=1&to=5&ak=" + Constants.MAP_BAIDUMAP_GEOCONV_AK
					+ "&output=json";
			logger.debug("转换坐标请求:{}",url);
			HttpUtil httpUtil = HttpUtil.getInstall();
			String json = httpUtil.sendGetRequest(url, null);
			logger.debug("转换坐标结果:{}",json);
			if(json != null ){
				net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(json);
//				JSONObject jsonObject = JSON.parseObject(json);
				if("0".equals(jsonObject.get("status").toString())){
					net.sf.json.JSONArray array = JSONTools.getJSONArray(jsonObject, "result");
//					JSONArray array = JSON.parseArray(jsonObject.get("result").toString());
					if(array != null && array.size() >0){
						return (net.sf.json.JSONObject) array.get(0);
					}else{
						return null ;
					}
				}else{
					return null ;
				}
			}else{
				return null ;
			}
		} catch (Exception e) {
			e.getStackTrace();
			return null;
		}

	}
	
	/*
	 * 逆地理编码，即逆地址解析，由百度经纬度信息得到结构化地址信息。
	 * 例如：“lat:31.325152,lng:120.558957”逆地址解析的结果是“ 江苏省苏州市虎丘区塔园路318号”
	 */
	public static JSONObject geoconvCity(List<Map<String, Object>> list) {
		StringBuffer sb = new StringBuffer();
		if (null != list && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Map map = (Map) list.get(i);
				sb.append(map.get("x"));
				sb.append(",");
				sb.append(map.get("y"));

			}
			System.out.println(sb);
			String url = Constants.MAP_BAIDUMAP_URL + "location=" + sb + "&ak="
					+ Constants.MAP_BAIDUMAP_GEOCONV_AK + "&output=json&pois=0";
			HttpUtil httpUtil = HttpUtil.getInstall();
			String json = httpUtil.sendGetRequest(url, null);
			System.out.println(url);
			JSONObject jsonObject = JSON.parseObject(json);
			return jsonObject;
		} else {
			return null;
		}
	}

}
