package cn.oomall.util.bmap;

public class Distance {
	 private static final double EARTH_RADIUS = 6378137;
     private static double rad(double d)
     {
        return d * Math.PI / 180.0;
     }
     
     /**
      * 根据两点间经纬度坐标（double值），计算两点间距离，单位为米
      * @param lng1
      * @param lat1
      * @param lng2
      * @param lat2
      * @return
      */
     public static double GetDistance(double lng1, double lat1, double lng2, double lat2)
     {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) + 
         Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
     }
     
     
     /**
      * @param args
      */
     public static void main(String[] args)
     {
    	 // x:39.910225848412,y:116.46728924751
    	 double x1= 39.910225848412;
    	 double y1= 116.46728924751;
    	 
    	 double x2= 39.958953166407;
    	 double y2= 116.52169489108;
    	 // 39.958953166407 116.52169489108
         double distance = GetDistance(x1,y1,x2,y2);
         System.out.println("Distance is:"+distance/1000);
     }
}
