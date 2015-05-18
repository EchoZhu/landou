package com.zykj.landous.activity;

import com.zykj.landous.R;
import com.zykj.landous.R.anim;
import com.zykj.landous.R.id;
import com.zykj.landous.R.layout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

@SuppressLint("SetJavaScriptEnabled")
public class E13_User_policy_Activity extends Activity implements
		OnClickListener {
	private WebView policy_webview;
	private ImageView back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.e13_activity_user_policy);
		back = (ImageView) findViewById(R.id.top_view_back);
		back.setOnClickListener(this);
		policy_webview=(WebView)findViewById(R.id.policy_webview);
		WebSettings wSet = policy_webview.getSettings();
		wSet.setJavaScriptEnabled(true);
		policy_webview.loadUrl("file:///android_asset/user_policy.html");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.top_view_back:
			super.finish();
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

			break;

		default:
			break;
		}
	}
}
