package com.zykj.landous.activity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.zykj.landous.R;
import com.zykj.landous.Tools.HttpUtils;

public class E8_ResetPwdActivity extends Activity implements OnClickListener{
	private TextView reset_pwd_phone;
	private EditText reset_password1;
	private EditText reset_password2;
	private Button reset_btn;
	private ImageView back;
	private String phone;
	private TextView policy;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.e8_reset_pwd_activity);
		initView();
	}
	public void initView(){
		
		reset_pwd_phone=(TextView)findViewById(R.id.reset_pwd_phone);
		reset_password1=(EditText)findViewById(R.id.reset_password1);
		reset_password2=(EditText)findViewById(R.id.reset_password2);
		phone = getIntent().getStringExtra("find_pwd_phone");
		reset_pwd_phone.setText("手机号："+phone);
		reset_btn=(Button)findViewById(R.id.reset_btn);
		back=(ImageView)findViewById(R.id.register_back);
		back.setOnClickListener(this);
		reset_btn.setOnClickListener(this);
		
		policy=(TextView)findViewById(R.id.policy);
		policy.setOnClickListener(this);
		
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.reset_btn:
			
			if (reset_password1.getText().toString().trim().length()<8) {
				Toast.makeText(this, "密码须大于8位", Toast.LENGTH_SHORT).show();
			}else if (!reset_password1.getText().toString().equals(reset_password2.getText().toString())) {
				Toast.makeText(this, "输入的两次密码不相等", Toast.LENGTH_SHORT).show();
			}else{
				HttpUtils.resetPassword(res, phone.replaceAll(" ", ""), reset_password2.getText().toString().replaceAll(" ", ""));
			}
			break;
		case R.id.register_back:
			super.finish();
			overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			break;
		case R.id.policy:
			Intent intent=new Intent(E8_ResetPwdActivity.this,E13_User_policy_Activity.class);
			startActivity(intent);
			this.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			break;

		default:
			break;
		}
		
	}
	JsonHttpResponseHandler res = new JsonHttpResponseHandler() {

		@Override
		public void onSuccess(int statusCode, Header[] headers,
				JSONObject response) {
			super.onSuccess(statusCode, headers, response);

			int result = 0;

			try {
				result = Integer.valueOf(response.getString("result"));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (result == 1 && statusCode == 200) {
				
				AlertDialog.Builder builder = new Builder(
						E8_ResetPwdActivity.this);
						builder.setTitle("修改成功");

						builder.setNegativeButton("确认",
						new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog,
						int which) {
						dialog.dismiss();
						finish();
						overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
						}

						});
						builder.create().show();


			}else{
				Log.i("reset_pwd", response.toString());
				try {
					String message=response.getString("message");
					AlertDialog.Builder builder = new Builder(
							E8_ResetPwdActivity.this);
							builder.setTitle(message);

							builder.setNegativeButton("确认",
							new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
							int which) {
							dialog.dismiss();
							finish();
							overridePendingTransition(R.anim.push_left_in,
							R.anim.push_left_out);
							}

							});
							builder.create().show();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers,
				Throwable throwable, JSONObject errorResponse) {
			// TODO Auto-generated method stub
			Toast.makeText(getApplicationContext(), "网络连接超时", Toast.LENGTH_LONG)
					.show();
			super.onFailure(statusCode, headers, throwable, errorResponse);
			
		}
	};
}
