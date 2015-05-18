package com.zykj.landous.Tools;

import java.text.DecimalFormat;

/**
 * @author 作者 zhang
 * @version 创建时间：2015年1月10日 上午10:13:08 类说明
 */
public class MathTools {
	/**
	 * double保留小数点后两位
	 * 
	 * @param num
	 * @return
	 */
	public static String DoubleKeepTwo(double num) {
		if (num < 0.01) {
			return "0.00";
		} else {
			DecimalFormat df = new DecimalFormat("#0.00");
			String str = df.format(num)==".00"?"0.00":df.format(num);
			
			return str;
		}

	}
}
