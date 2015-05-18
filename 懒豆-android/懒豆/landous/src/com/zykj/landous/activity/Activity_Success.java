package com.zykj.landous.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.BeeFramework.activity.BaseActivity;
import com.zykj.landous.MainActivity;
import com.zykj.landous.R;
import com.zykj.landous.fragment.MainTabsFrament;


/**
 * @author 作者 zhang
 * @version 创建时间：2015年1月28日 上午5:11:09 类说明 支付宝付款成功失败
 */
public class Activity_Success extends BaseActivity implements OnClickListener {
	TextView message;
	Intent it;
	private View iv_back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_success);
		it = getIntent();
		message = (TextView) findViewById(R.id.tv_message);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		String type = it.getStringExtra("type");
		if (type.equals("success")) {
			message.setText("支付成功");
		} else if (type.equals("6001")) {
			ProgressDialog p = new ProgressDialog(this);
			p.setMessage("sdfsdfsdf");
			p.show();
			p.dismiss();
			message.setText("用户取消支付");
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_back:
			Intent it = new Intent(Activity_Success.this, MainActivity.class);
			MainTabsFrament.type = "tab_one";
			it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(it);
			this.finish();

			break;

		default:
			break;
		}

	}

}
