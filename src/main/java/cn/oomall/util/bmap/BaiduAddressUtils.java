/*  
 * @(#) BaiduAddressUtils.java Create on 2015年7月17日 下午6:32:41   
 *   
 * Copyright 2015 .ooyanjing
 */


package cn.oomall.util.bmap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import cn.ooeyeglass.framework.core.utils.jsonUtils.JSONTools;
import cn.ooeyeglass.vcm.vo.LocationModel;

/**
 * @BaiduAddressUtils.java
 * @created at 2015年7月17日 下午6:32:41 by zhanghongliang@ooyanjing.com
 *
 * @desc
 *
 * @author  zhanghongliang@ooyanjing.com
 * @version $Revision$
 * @update: $Date$
 */
public class BaiduAddressUtils {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void findCityByXY(LocationModel locationModel) {
		try {
			List list = new ArrayList();
			JSONObject json = new JSONObject();
			json.put("x", locationModel.getX());
			json.put("y", locationModel.getY());
			list.add(json);

			Map res = GeocodingUtils.geoconvCity(list);
			Map result = (Map) res.get("result");
			String address = getAddress(result);
			locationModel.setOrgAddress(address);
			Map addressComponent = (Map) result.get("addressComponent");
			String cityname = addressComponent.get("city") + "";
			locationModel.setOrgCity(cityname);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getAddress(Map baiduGeoconvInfo) {
		JSONObject json = JSONObject.fromObject(baiduGeoconvInfo);
		JSONArray jsonArray = JSONTools.getJSONArray(json, "poiRegions");
		String poiName = "";
		if (jsonArray != null && jsonArray.size() > 0) {
			JSONObject jsonObj = jsonArray.getJSONObject(0);
			poiName = JSONTools.getString(jsonObj, "name");
		}

		Map addressComponent = (Map) baiduGeoconvInfo.get("addressComponent");
		String district = (String) addressComponent.get("district");
		if (StringUtils.isBlank(poiName)) {
			poiName = (String) addressComponent.get("street")
					+ (String) addressComponent.get("street_number");
		}
		return district + poiName;
	}
}
