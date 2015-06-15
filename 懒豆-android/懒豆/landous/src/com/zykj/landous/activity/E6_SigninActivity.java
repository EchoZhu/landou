package com.zykj.landous.activity;

import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.zykj.landous.MainActivity;
import com.zykj.landous.R;
import com.zykj.landous.Tools.HttpUtils;

public class E6_SigninActivity extends Activity implements OnClickListener,
		Callback {
	// 返回按钮
	private ImageView back;
	// 登录用户名
	private EditText login_name;
	// 登录密码
	private EditText login_pwd;
	// 登录按钮
	private Button login_btn;
	// 找回密码按钮
	private Button find_pwd;
	// 注册按钮
	private Button regist_button;
	private ProgressDialog loadingPDialog = null;


	private SharedPreferences shared;
	private SharedPreferences.Editor editor;

	private static String APPKEY = "502ddf1221e6";

	// 填写从短信SDK应用后台注册得到的APPSECRET
	private static String APPSECRET = "67f53cb9f6701daa5385550be8bfc15f";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.e6_signin_activity);
		AsyncHttpClient request = HttpUtils.getClient();
		PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
		request.setCookieStore(myCookieStore);
		initView();
		initSmsSDK();
	}

	public void initView() {
		back = (ImageView) findViewById(R.id.login_back_btn);
		back.setOnClickListener(this);
		login_name = (EditText) findViewById(R.id.login_name);
		login_pwd = (EditText) findViewById(R.id.login_password);
		login_btn = (Button) findViewById(R.id.login_btn);
		login_btn.setOnClickListener(this);
		find_pwd = (Button) findViewById(R.id.find_pwd);
		find_pwd.setOnClickListener(this);
		regist_button = (Button) findViewById(R.id.regist_button);
		regist_button.setOnClickListener(this);
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
	/**
     * 监听Back键按下事件,方法1:
     * 注意:
     * super.onBackPressed()会自动调用finish()方法,关闭
     * 当前Activity.
     * 若要屏蔽Back键盘,注释该行代码即可
     */ 
    @Override 
    public void onBackPressed() { 
    	Intent it=new Intent(E6_SigninActivity.this,MainActivity.class);
		startActivity(it);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        super.onBackPressed(); 
        System.out.println("按下了back键   onBackPressed()");        
    } 
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_back_btn:
			
			// 跳转主页
			Intent it=new Intent(E6_SigninActivity.this,MainActivity.class);
			startActivity(it);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.login_btn:
			String loginName = login_name.getText().toString().replaceAll(" ", "");
			String loginPwd = login_pwd.getText().toString().replaceAll(" ", "");
			
			if (loginName.length()<3||loginPwd.length()<3) {
				Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();;
			} else {
				loadingPDialog = new ProgressDialog(this);
				loadingPDialog.setMessage("正在登录....");
				loadingPDialog.setCancelable(false);
				loadingPDialog.show();
				HttpUtils.login(res_loginin, loginName.trim(), loginPwd.trim());
			}
			
			break;
		case R.id.regist_button:
			// 打开注册页面
			RegisterPage registerPage = new RegisterPage();
			registerPage.setFlag("regist");
			registerPage.setRegisterCallback(new EventHandler() {
				public void afterEvent(int event, int result, Object data) {
					// 解析注册结果
					if (result == SMSSDK.RESULT_COMPLETE) {
						@SuppressWarnings("unchecked")
						HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
						String country = (String) phoneMap.get("country");
						String phone = (String) phoneMap.get("phone");
						// 提交用户信息
						// registerUser(country, phone);

						Intent intent = new Intent(E6_SigninActivity.this,
								E7_SignupActivity.class);
						intent.putExtra("regist_phone", phone);
						startActivity(intent);
						overridePendingTransition(R.anim.push_right_in,
								R.anim.push_right_out);
					}
				}
			});
			registerPage.show(this);
			break;
		case R.id.find_pwd:
			// 打开注册页面
			RegisterPage find_pwd = new RegisterPage();
			find_pwd.setFlag("reset");
			find_pwd.setRegisterCallback(new EventHandler() {
				public void afterEvent(int event, int result, Object data) {
					// 解析注册结果
					if (result == SMSSDK.RESULT_COMPLETE) {
						@SuppressWarnings("unchecked")
						HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
						String country = (String) phoneMap.get("country");
						String phone = (String) phoneMap.get("phone");
						// 提交用户信息
						// registerUser(country, phone);

						Intent intent = new Intent(E6_SigninActivity.this,
								E8_ResetPwdActivity.class);
						intent.putExtra("find_pwd_phone", phone);
						startActivity(intent);
						overridePendingTransition(R.anim.push_right_in,
								R.anim.push_right_out);
					}
				}
			});
			find_pwd.show(this);
			break;
		default:
			break;
		}

	}

	/**
	 * 登录
	 */
	JsonHttpResponseHandler res_loginin = new JsonHttpResponseHandler() {
		public void onSuccess(int statusCode, Header[] headers,
				JSONObject response) {
			super.onSuccess(statusCode, headers, response);
			int result = 0;

			try {
				result = Integer.valueOf(response.getString("result"));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (result == 1 && statusCode == 200) {
				HttpUtils.refreshUserInfo(res_refresh);
				

				Log.i("login", "login_success");
				Intent intent = new Intent();
				intent.putExtra("login", true);
				setResult(Activity.RESULT_OK, intent);
				loadingPDialog.dismiss();
				AlertDialog.Builder builder = new Builder(
						E6_SigninActivity.this);
						builder.setTitle("登录成功");

						

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
				String message="";
				try {
					message=response.getString("message");
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				loadingPDialog.dismiss();
				AlertDialog.Builder builder = new Builder(
						E6_SigninActivity.this);
						builder.setTitle("用户未注册或密码错误");

						

						builder.setNegativeButton("确认",
						new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog,
						int which) {
						
						}

						});
						builder.create().show();

				
			}
		};

		public void onFailure(int statusCode, org.apache.http.Header[] headers,
				String responseString, Throwable throwable) {
			super.onFailure(statusCode, headers, responseString, throwable);
			loadingPDialog.dismiss();
			Toast.makeText(E6_SigninActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show();;
		};
	};
	JsonHttpResponseHandler res_refresh = new JsonHttpResponseHandler() {

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
				
				
				JSONObject userObject = response.getJSONObject("data");
				shared = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
				editor = shared.edit();
				String userID=userObject.getString("member_id");
				editor.putString("member_id",
						userID);
				editor.putString("check_status", userObject.getString("check_status"));
				Log.i("user_id", userID);
				editor.putString("member_points", userObject.getString("member_points")+"");
				editor.putString("available_predeposit", userObject.getString("available_predeposit")+"");
				editor.putString("member_name",
						userObject.getString("member_name")+"");
				editor.putString("member_email",
						userObject.getString("member_email")+"");
				editor.putString("member_phone",
						userObject.getString("member_phone")+"");
				editor.putString("member_avatar",
						userObject.getString("member_avatar")+"");
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			editor.commit();
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
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

}
