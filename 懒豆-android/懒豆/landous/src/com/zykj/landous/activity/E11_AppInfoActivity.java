package com.zykj.landous.activity;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zykj.landous.R;
import com.zykj.landous.R.layout;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class E11_AppInfoActivity extends Activity implements OnClickListener{
	private ImageView back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_e11_app_info);
		back=(ImageView)findViewById(R.id.view_top_back);
		back.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.view_top_back:
			super.finish();
			overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			break;

		default:
			break;
		}
		
	}
}
