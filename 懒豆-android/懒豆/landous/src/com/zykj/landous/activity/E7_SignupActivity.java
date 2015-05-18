package com.zykj.landous.activity;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.zykj.landous.R;
import com.zykj.landous.Tools.HttpUtils;

@SuppressLint("ShowToast")
public class E7_SignupActivity extends Activity implements OnClickListener,
		OnFocusChangeListener, OnEditorActionListener {
	private TextView regist_phone;
	private EditText register_name;
	private EditText register_email;
	private EditText register_password1;
	private EditText register_password2;
	private Button register_btn;
	private ImageView back;
	private String phone;
	private ProgressDialog loadingPDialog = null;
	private TextView policy;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.e7_signup_activity);
		initView();
	}

	public void initView() {
		back=(ImageView)findViewById(R.id.register_back);
		back.setOnClickListener(this);
		regist_phone = (TextView) findViewById(R.id.regist_phone);
		register_name = (EditText) findViewById(R.id.register_name);
		register_name.setOnFocusChangeListener(this);
		register_email = (EditText) findViewById(R.id.register_email);
		register_email.setOnEditorActionListener(this);
		register_password1 = (EditText) findViewById(R.id.register_password1);
		register_password2 = (EditText) findViewById(R.id.register_password2);
		register_btn = (Button) findViewById(R.id.regist_btn);
		policy=(TextView)findViewById(R.id.policy);
		policy.setOnClickListener(this);
		register_btn.setOnClickListener(this);
		phone = getIntent().getStringExtra("regist_phone");
		regist_phone.setText("注册手机 ：" + phone);
		loadingPDialog = new ProgressDialog(this);
		loadingPDialog.setMessage("正在加载....");
		loadingPDialog.setCancelable(false);

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.regist_btn:
			if (register_name.getText().toString().length() < 8) {
				Toast.makeText(this, "用户名不能小于8位", Toast.LENGTH_SHORT).show();
			} else if (!isUsername(register_name.getText().toString())) {
				Toast.makeText(this, "用户名必须为字母开头的字母与数字的集合", Toast.LENGTH_SHORT)
						.show();
			} 
			/*else if (!isEmail(register_email.getText().toString())) {
				Toast.makeText(this, "邮箱格式不正确", Toast.LENGTH_SHORT).show();
			} */
			else if (register_password1.getText().toString().length() < 8) {
				Toast.makeText(this, "密码不能少于8位", Toast.LENGTH_SHORT).show();
				;
			} else if (!register_password1.getText().toString()
					.equals(register_password2.getText().toString())) {
				Toast.makeText(this, "密码与确认密码不相等", Toast.LENGTH_SHORT).show();
				;
			} else {

				Calendar c = Calendar.getInstance();
				String time = c.get(Calendar.YEAR)+""+(c.get(Calendar.MONTH)+1)+""+(c.get(Calendar.DAY_OF_MONTH))+""+(c.get(Calendar.HOUR_OF_DAY))+""+(c.get(Calendar.MINUTE))+""+(c.get(Calendar.SECOND))+"@163.com";
				HttpUtils.regist(res, register_name.getText().toString().replaceAll(" ", ""),
						register_password1.getText().toString().replaceAll(" ", ""), time, phone.replaceAll(" ", ""));
				loadingPDialog.show();
			}
			break;
		case R.id.register_back:
			super.finish();
			overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			
			break;
		case R.id.policy:
			Intent intent=new Intent(E7_SignupActivity.this,E13_User_policy_Activity.class);
			startActivity(intent);
			this.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			break;
		default:
			break;
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		// TODO Auto-generated method stub

		return false;
	}

	JsonHttpResponseHandler res = new JsonHttpResponseHandler() {
		@Override
		public void onSuccess(int statusCode, org.apache.http.Header[] headers,
				org.json.JSONObject response) {
			super.onSuccess(statusCode, headers, response);
			Log.i("regist-success", response.toString());
			int result = 0;

			try {
				result = Integer.valueOf(response.getString("result"));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (result == 1 && statusCode == 200) {
				loadingPDialog.dismiss();
				AlertDialog.Builder builder = new Builder(
						E7_SignupActivity.this);
				builder.setMessage("注册成功");

				builder.setTitle("提示");

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
				try {
					String msg=response.getString("message");
					loadingPDialog.dismiss();
					AlertDialog.Builder builder = new Builder(
							E7_SignupActivity.this);
					builder.setMessage(msg);

					builder.setTitle("提示");

					builder.setNegativeButton("确认",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});
					builder.create().show();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		};

		@Override
		public void onFailure(int statusCode, org.apache.http.Header[] headers,
				Throwable throwable, org.json.JSONObject errorResponse) {
			super.onFailure(statusCode, headers, throwable, errorResponse);
			loadingPDialog.dismiss();
			Toast.makeText(E7_SignupActivity.this, "网络状态异常", Toast.LENGTH_SHORT)
					.show();
		};
	};

	// 判断手机格式是否正确
	public boolean isMobileNO(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);

		return m.matches();
	}

	// 判断email格式是否正确
	public boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);

		return m.matches();
	}

	public boolean isUsername(String username) {
		String str = "^[a-zA-Z]+[a-zA-Z0-9]{7,15}$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(username);

		return m.matches();
	}

	// 判断是否全是数字
	public boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

}
