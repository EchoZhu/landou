package com.zykj.landous.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;

/**
 * @author 作者 zhang
 * @version 创建时间：2015年1月10日 下午2:37:43 类说明
 */
public class MyDialogActivity extends Activity {
	private ProgressDialog loadingPDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		try {
			Thread.sleep(100);
			setResult(D1_OrderConfirmActivity.ADDRESS_CODE);
			MyDialogActivity.this.finish();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
