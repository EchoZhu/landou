package com.zykj.landous.activity;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.zykj.landous.LandousAppConst;
import com.zykj.landous.R;
import com.zykj.landous.Tools.HttpUtils;

public class E1_SettingActivity extends Activity implements OnClickListener,
		Callback {
	private ImageView back;
	private Button setting_exitLogin;
	private LinearLayout ll_company_info;
	private LinearLayout ll_company_address;
	private LinearLayout ll_company_contact;
	private LinearLayout ll_app_judgement;
	private LinearLayout ll_app_info;
	private LinearLayout ll_app_update;
	private LinearLayout ll_reset_password;

	private SharedPreferences shared;
	private SharedPreferences.Editor editor;

	private static String APPKEY = "502ddf1221e6";

	// 填写从短信SDK应用后台注册得到的APPSECRET
	private static String APPSECRET = "67f53cb9f6701daa5385550be8bfc15f";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.e1_setting_activity);
		initView();
		initSmsSDK();
	}

	public void initView() {
		back = (ImageView) findViewById(R.id.top_view_back);
		back.setOnClickListener(this);
		setting_exitLogin = (Button) findViewById(R.id.setting_exitLogin);
		setting_exitLogin.setOnClickListener(this);
		ll_company_info = (LinearLayout) findViewById(R.id.setting_company_info);
		ll_company_address = (LinearLayout) findViewById(R.id.setting_company_address);
		ll_company_contact = (LinearLayout) findViewById(R.id.setting_company_contact);
		ll_app_judgement = (LinearLayout) findViewById(R.id.setting_app_judgement);
		ll_app_info = (LinearLayout) findViewById(R.id.setting_app_info);
		ll_app_update = (LinearLayout) findViewById(R.id.setting_app_update);
		ll_reset_password = (LinearLayout) findViewById(R.id.setting_reset_password);
		ll_company_info.setOnClickListener(this);
		ll_company_address.setOnClickListener(this);
		ll_company_contact.setOnClickListener(this);
		ll_app_judgement.setOnClickListener(this);
		ll_app_info.setOnClickListener(this);
		ll_app_update.setOnClickListener(this);
		ll_reset_password.setOnClickListener(this);

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if (!isLogin()) {
			setting_exitLogin.setVisibility(View.GONE);
		} else {
			setting_exitLogin.setVisibility(View.VISIBLE);
		}
	}

	public Boolean isLogin() {
		shared = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
		String userID = shared.getString("member_id", "");
		Log.i("login-user-id", userID);
		if (userID.equals("")) {

			return false;
		}
		return true;
	}

	public void initSmsSDK() {
		// 初始化短信SDK
		SMSSDK.initSDK(this, APPKEY, APPSECRET);
		final Handler handler = new Handler(this);
		EventHandler eventHandler = new EventHandler() {
			public void afterEvent(int event, int result, Object data) {
				Message msg = new Message();
				msg.arg1 = event;
				msg.arg2 = result;
				msg.obj = data;
				handler.sendMessage(msg);
			}
		};
		// 注册回调监听接口
		SMSSDK.registerEventHandler(eventHandler);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_view_back:
			super.finish();
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.setting_exitLogin:
			logout();
			AlertDialog.Builder builder = new Builder(this);
			builder.setTitle("注销成功");

			builder.setNegativeButton("确认",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							finish();
							overridePendingTransition(R.anim.push_left_in,
									R.anim.push_left_out);
						}

					});
			builder.create().show();

			break;
		case R.id.setting_company_info:
			Intent intent = new Intent(E1_SettingActivity.this,
					E10_CompanyInfoActivity.class);
			startActivity(intent);
			this.overridePendingTransition(R.anim.push_right_in,
					R.anim.push_right_out);
			break;
		case R.id.setting_company_address:
			Intent intent2 = new Intent(E1_SettingActivity.this,
					E12_CompanyMapActivity.class);
			startActivity(intent2);
			this.overridePendingTransition(R.anim.push_right_in,
					R.anim.push_right_out);
			break;
		case R.id.setting_company_contact:
			AlertDialog.Builder builder1 = new Builder(this);
			builder1.setTitle("呼叫客服：400-882-7090");

			builder1.setNegativeButton("确认",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							// 传入服务， parse（）解析号码
							Intent intent = new Intent(Intent.ACTION_CALL, Uri
									.parse("tel:" + "400-882-7090"));
							// 通知activtity处理传入的call服务
							startActivity(intent);
						}

					}).setPositiveButton("取消",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {

						}

					});
			builder1.create().show();
			break;
		case R.id.setting_app_judgement:
			break;
		case R.id.setting_app_info:
			Intent intent1 = new Intent(E1_SettingActivity.this,
					E11_AppInfoActivity.class);
			startActivity(intent1);
			this.overridePendingTransition(R.anim.push_right_in,
					R.anim.push_right_out);
			break;
		case R.id.setting_app_update:
			HttpUtils.getAppVersion(res_app_version);

			break;
		case R.id.setting_reset_password:
			RegisterPage reset_password = new RegisterPage();
			reset_password.setFlag("reset");
			reset_password.setRegisterCallback(new EventHandler() {
				public void afterEvent(int event, int result, Object data) {
					// 解析注册结果
					if (result == SMSSDK.RESULT_COMPLETE) {
						@SuppressWarnings("unchecked")
						HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
						String country = (String) phoneMap.get("country");
						String phone = (String) phoneMap.get("phone");
						// 提交用户信息
						// registerUser(country, phone);

						Intent intent = new Intent(E1_SettingActivity.this,
								E8_ResetPwdActivity.class);
						intent.putExtra("find_pwd_phone", phone);
						startActivity(intent);
						overridePendingTransition(R.anim.push_right_in,
								R.anim.push_right_out);
					}
				}
			});
			reset_password.show(this);
			break;

		default:
			break;
		}

	}

	public void update() {
		AlertDialog.Builder builder = new Builder(E1_SettingActivity.this);
		builder.setTitle("已是最新版本");

		builder.setNegativeButton("确认", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {

			}

		});
		builder.create().show();

	}

	public void logout() {
		AsyncHttpClient httpClient = HttpUtils.getClient();
		PersistentCookieStore cookieStore = new PersistentCookieStore(this);
		cookieStore.clear();
		httpClient.setCookieStore(null);
		Log.i("logout", "logout success");

		shared = getSharedPreferences("loginInfo", 0);
		editor = shared.edit();
		editor.putString("member_id", "");
		editor.putString("member_name", "");
		editor.putString("member_email", "");
		editor.putString("member_phone", "");
		editor.putString("member_avatar", "");
		editor.putString("available_predeposit", null);
		editor.putString("member_points", null);

		editor.commit();

	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

	JsonHttpResponseHandler res_app_version = new JsonHttpResponseHandler() {

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

				try {
					JSONObject version = response.getJSONObject("data");
					String version_code = version.getString("version_code");
					String version_name = version.getString("version_name");
					final String download = version.getString("download_link");
					int versioncode = Integer.valueOf(version_code);
					if (versioncode > LandousAppConst.app_version_code) {
						// 更新用户新版本
						
						AlertDialog.Builder builder = new Builder(
								getApplicationContext());
						builder.setMessage("是否下载更新最新版本？");

						builder.setTitle("提示");

						builder.setNegativeButton("确认",
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										Uri uri = Uri.parse(download);
										Intent it = new Intent(Intent.ACTION_VIEW, uri);
										startActivity(it);
									}

								});
						builder.setPositiveButton("取消",
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();

									}

								});
						builder.create().show();

					} else {
						// 当前版本已经是最新
						update();
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// goodsAdapter.notifyDataSetChanged();

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
