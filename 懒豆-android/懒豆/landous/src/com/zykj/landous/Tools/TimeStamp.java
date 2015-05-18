package com.zykj.landous.Tools;

/**
 * @author 作者 zhang
 * @version 创建时间：2015年1月14日 下午9:13:52 类说明 时间转换
 */
public class TimeStamp {
	/**
	 * unix时间转北京时间
	 * 
	 * @param geval_addtime
	 * @return
	 */
	public static String getBeijingTime(String geval_addtime) {
		Long timestamp = Long.parseLong(geval_addtime) * 1000;
		String date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(new java.util.Date(timestamp));
		return date;

	}
}
