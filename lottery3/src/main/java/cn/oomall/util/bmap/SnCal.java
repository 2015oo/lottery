package cn.oomall.util.bmap;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SnCal {
	public static void main(String[] args) throws UnsupportedEncodingException,
			NoSuchAlgorithmException {

		// 计算sn跟参数对出现顺序有关，所以用LinkedHashMap保存<key,value>，此方法适用于get请求，如果是为发送post请求的url生成签名，请保证参数对按照key的字母顺序依次放入Map。以get请求为例：http://api.map.baidu.com/geocoder/v2/?address=百度大厦&output=json&ak=yourak，paramsMap中先放入address，再放output，然后放ak，放入顺序必须跟get请求中对应参数的出现顺序保持一致。
		LinkedHashMap<String, String> paramsMap = new LinkedHashMap<String, String>();
		paramsMap.put("output", "json");
		paramsMap.put("ak", "A95a432fa8674626ae267f9c2a65a780");
		paramsMap.put("address", URLEncoder.encode("北京市东城区雍和宫", "utf-8"));

		// 调用下面的toQueryString方法，对LinkedHashMap内所有value作utf8编码，拼接返回结果address=%E7%99%BE%E5%BA%A6%E5%A4%A7%E5%8E%A6&output=json&ak=yourak
		String paramsStr = toQueryString(paramsMap);

		// 对paramsStr前面拼接上/geocoder/v2/?，后面直接拼接yoursk得到/geocoder/v2/?address=%E7%99%BE%E5%BA%A6%E5%A4%A7%E5%8E%A6&output=json&ak=yourakyoursk
		String wholeStr = new String("/geocoder/v2/?" + paramsStr
				+ "7b288db96010d5246a828925884c78f5");

		// 对上面wholeStr再作utf8编码
		String tempStr = URLEncoder.encode(wholeStr, "UTF-8");

		// 调用下面的MD5方法得到最后的sn签名7de5a22212ffaa9e326444c75a58f9a0
		System.out.println(MD5(tempStr));
		System.out.println(calSn(paramsMap));
	}

	/**
	 * 计算sn签名
	 * 
	 * @param paramsMap
	 *            向paramsMap中添加参数（不包含sn）的顺序一定与url或者body中的顺序相同
	 * @return
	 */
	public static String calSn(LinkedHashMap<String, String> paramsMap) {
		try {
			String paramsStr = toQueryString(paramsMap);
			String wholeStr = new String(Constants.MAP_BAIDUMAP_SERVICEVER
					+ paramsStr + Constants.MAP_BAIDUMAP_SK);
			String tempStr = URLEncoder.encode(wholeStr, "UTF-8");
			return MD5(tempStr);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	// 对Map内所有value作utf8编码，拼接返回结果
	private static String toQueryString(Map<?, ?> data)
			throws UnsupportedEncodingException {
		StringBuffer queryString = new StringBuffer();
		for (Entry<?, ?> pair : data.entrySet()) {
			queryString.append(pair.getKey() + "=");
			queryString.append(URLEncoder.encode((String) pair.getValue(),
					"UTF-8")
					+ "&");
		}
		if (queryString.length() > 0) {
			queryString.deleteCharAt(queryString.length() - 1);
		}
		return queryString.toString();
	}

	// 来自stackoverflow的MD5计算方法，调用了MessageDigest库函数，并把byte数组结果转换成16进制
	private static String MD5(String md5) {
		try {
			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance("MD5");
			byte[] array = md.digest(md5.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
						.substring(1, 3));
			}
			return sb.toString();
		} catch (java.security.NoSuchAlgorithmException e) {
		}
		return null;
	}
}