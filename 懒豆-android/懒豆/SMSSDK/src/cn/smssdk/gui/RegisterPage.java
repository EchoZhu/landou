/*
 * 官网地站:http://www.mob.com
 * 技术支持QQ: 4006852216
 * 官方微信:ShareSDK   （如果发布新版本的话，我们将会第一时间通过微信将版本更新内容推送给您。如果使用过程中有任何问题，也可以通过微信与我们取得联系，我们将会在24小时内给予回复）
 *
 * Copyright (c) 2014年 mob.com. All rights reserved.
 */
package cn.smssdk.gui;

import static cn.smssdk.framework.utils.R.getBitmapRes;
import static cn.smssdk.framework.utils.R.getIdRes;
import static cn.smssdk.framework.utils.R.getLayoutRes;
import static cn.smssdk.framework.utils.R.getStringRes;
import static cn.smssdk.framework.utils.R.getStyleRes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.framework.FakeActivity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

/** 短信注册页面 */
public class RegisterPage extends FakeActivity implements OnClickListener,
		TextWatcher {

	// 默认使用中国区号
	private static final String DEFAULT_COUNTRY_ID = "42";

	private EventHandler callback;

	// 国家
	private TextView tvCountry;
	// 手机号码
	private EditText etPhoneNum;
	// 国家编号
	private TextView tvCountryNum;
	// clear 号码
	private ImageView ivClear;
	// 下一步按钮
	private Button btnNext;

	private String currentId;
	private String currentCode;
	private EventHandler handler;
	// 国家号码规则
	private HashMap<String, String> countryRules;
	private Dialog pd;

	private String reset_or_regist = "";
	public static final String base_url_test = "http://112.53.78.18:8088/appif/api.php?m=user&a=";
	public static final String base_url="http://api.landous.com/api.php?m=user&a=";
	private String code_ = "";
	private String phone_ = "";
	private TextView policy;

	public void setFlag(String is) {
		this.reset_or_regist = is;
	}

	public void setRegisterCallback(EventHandler callback) {
		this.callback = callback;
	}

	public void show(Context context) {
		super.show(context, null);
	}

	public void onCreate() {
		int resId = getLayoutRes(activity, "smssdk_regist_page");
		if (resId > 0) {
			activity.setContentView(resId);
			currentId = DEFAULT_COUNTRY_ID;

			resId = getIdRes(activity, "ll_back");
			View llBack = activity.findViewById(resId);
			resId = getIdRes(activity, "tv_title");
			TextView tv = (TextView) activity.findViewById(resId);
			resId = getStringRes(activity, "smssdk_regist");
			if (resId > 0) {
				tv.setText(resId);
			}
			resId = getIdRes(activity, "rl_country");
			View viewCountry = activity.findViewById(resId);
			resId = getIdRes(activity, "btn_next");
			btnNext = (Button) activity.findViewById(resId);

			resId = getIdRes(activity, "tv_country");
			tvCountry = (TextView) activity.findViewById(resId);

			String[] country = getCurrentCountry();
			// String[] country = SMSSDK.getCountry(currentId);
			if (country != null) {
				currentCode = country[1];
				tvCountry.setText(country[0]);
			}
			resId = getIdRes(activity, "tv_country_num");
			tvCountryNum = (TextView) activity.findViewById(resId);
			tvCountryNum.setText("+" + currentCode);

			resId = getIdRes(activity, "et_write_phone");
			etPhoneNum = (EditText) activity.findViewById(resId);
			etPhoneNum.setText("");
			etPhoneNum.addTextChangedListener(this);
			etPhoneNum.requestFocus();
			if (etPhoneNum.getText().length() > 0) {
				btnNext.setEnabled(true);
				resId = getIdRes(activity, "iv_clear");
				ivClear = (ImageView) activity.findViewById(resId);
				ivClear.setVisibility(View.VISIBLE);
				resId = getBitmapRes(activity, "smssdk_btn_enable");
				if (resId > 0) {
					btnNext.setBackgroundResource(resId);
				}
			}

			resId = getIdRes(activity, "iv_clear");
			ivClear = (ImageView) activity.findViewById(resId);
			resId = getIdRes(activity, "policy");
			policy = (TextView) activity.findViewById(resId);
			policy.setOnClickListener(this);
			llBack.setOnClickListener(this);
			btnNext.setOnClickListener(this);
			ivClear.setOnClickListener(this);
			viewCountry.setOnClickListener(this);

			handler = new EventHandler() {
				@SuppressWarnings("unchecked")
				public void afterEvent(final int event, final int result,
						final Object data) {
					runOnUIThread(new Runnable() {
						public void run() {
							if (pd != null && pd.isShowing()) {
								pd.dismiss();
							}
							if (result == SMSSDK.RESULT_COMPLETE) {
								if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
									// 请求支持国家列表
									onCountryListGot((ArrayList<HashMap<String, Object>>) data);
								} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
									// 请求验证码后，跳转到验证码填写页面
									afterVerificationCodeRequested();
								}
							} else {
								// 根据服务器返回的网络错误，给toast提示
								try {
									((Throwable) data).printStackTrace();
									Throwable throwable = (Throwable) data;

									JSONObject object = new JSONObject(
											throwable.getMessage());
									String des = object.optString("detail");
									if (!TextUtils.isEmpty(des)) {
										Toast.makeText(activity, des,
												Toast.LENGTH_SHORT).show();
										return;
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
								// 如果木有找到资源，默认提示
								int resId = getStringRes(activity,
										"smssdk_network_error");
								if (resId > 0) {
									Toast.makeText(activity, resId,
											Toast.LENGTH_SHORT).show();
								}
							}
						}
					});
				}
			};
		}

	}

	private String[] getCurrentCountry() {
		String mcc = getMCC();
		String[] country = null;
		if (!TextUtils.isEmpty(mcc)) {
			country = SMSSDK.getCountryByMCC(mcc);
		}

		if (country == null) {
			Log.w("SMSSDK", "no country found by MCC: " + mcc);
			country = SMSSDK.getCountry(DEFAULT_COUNTRY_ID);
		}
		return country;
	}

	private String getMCC() {
		TelephonyManager tm = (TelephonyManager) activity
				.getSystemService(Context.TELEPHONY_SERVICE);
		// 返回当前手机注册的网络运营商所在国家的MCC+MNC. 如果没注册到网络就为空.
		String networkOperator = tm.getNetworkOperator();

		// 返回SIM卡运营商所在国家的MCC+MNC. 5位或6位. 如果没有SIM卡返回空
		String simOperator = tm.getSimOperator();

		String mcc = null;
		if (!TextUtils.isEmpty(networkOperator)
				&& networkOperator.length() >= 5) {
			mcc = networkOperator.substring(0, 3);
		}

		if (TextUtils.isEmpty(mcc)) {
			if (!TextUtils.isEmpty(simOperator) && simOperator.length() >= 5) {
				mcc = simOperator.substring(0, 3);
			}
		}

		return mcc;
	}

	public void onResume() {
		SMSSDK.registerEventHandler(handler);
	}

	public void onPause() {
		SMSSDK.unregisterEventHandler(handler);
	}

	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (s.length() > 0) {
			btnNext.setEnabled(true);
			ivClear.setVisibility(View.VISIBLE);
			int resId = getBitmapRes(activity, "orange_button_bg");
			if (resId > 0) {
				btnNext.setBackgroundResource(resId);
			}
		} else {
			btnNext.setEnabled(false);
			ivClear.setVisibility(View.GONE);
			int resId = getBitmapRes(activity, "smssdk_btn_disenable");
			if (resId > 0) {
				btnNext.setBackgroundResource(resId);
			}
		}
	}

	public void afterTextChanged(Editable s) {

	}

	public void onClick(View v) {
		int id = v.getId();
		int id_ll_back = getIdRes(activity, "ll_back");
		int id_rl_country = getIdRes(activity, "rl_country");
		int id_btn_next = getIdRes(activity, "btn_next");
		int id_iv_clear = getIdRes(activity, "iv_clear");
		int policy_id = getIdRes(activity, "policy");
		if (id == id_ll_back) {
			finish();
		} else if (id == id_rl_country) {
			// 国家列表
			CountryPage countryPage = new CountryPage();
			countryPage.setCountryId(currentId);
			countryPage.setCountryRuls(countryRules);
			countryPage.showForResult(activity, null, this);
		} else if (id == id_btn_next) {
			// 请求发送短信验证码
			if (countryRules == null || countryRules.size() <= 0) {
				if (pd != null && pd.isShowing()) {
					pd.dismiss();
				}
				pd = CommonDialog.ProgressDialog(activity);
				if (pd != null) {
					pd.show();
				}

				SMSSDK.getSupportedCountries();
			} else {
				String phone = etPhoneNum.getText().toString().trim()
						.replaceAll("\\s*", "");
				String code = tvCountryNum.getText().toString().trim();
				checkPhoneNum(phone, code);
			}
		} else if (id == id_iv_clear) {
			// 清除电话号码输入框
			etPhoneNum.getText().clear();
		} else if (id == policy_id) {
			AlertDialog.Builder builder = new Builder(activity);
			WebView policy_webview = new WebView(activity);
			WebSettings wSet = policy_webview.getSettings();
			wSet.setJavaScriptEnabled(true);
			policy_webview.loadUrl("file:///android_asset/user_policy.html");
			builder.setView(policy_webview);
			builder.setNegativeButton("确认",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

						}

					});
			builder.create().show();
		}
	}

	@SuppressWarnings("unchecked")
	public void onResult(HashMap<String, Object> data) {
		if (data != null) {
			int page = (Integer) data.get("page");
			if (page == 1) {
				// 国家列表返回
				currentId = (String) data.get("id");
				countryRules = (HashMap<String, String>) data.get("rules");
				String[] country = SMSSDK.getCountry(currentId);
				if (country != null) {
					currentCode = country[1];
					tvCountryNum.setText("+" + currentCode);
					tvCountry.setText(country[0]);
				}
			} else if (page == 2) {
				// 验证码校验返回
				Object res = data.get("res");
				HashMap<String, Object> phoneMap = (HashMap<String, Object>) data
						.get("phone");
				if (res != null && phoneMap != null) {
					int resId = getStringRes(activity,
							"smssdk_your_ccount_is_verified");
					if (resId > 0) {
						// Toast.makeText(activity, resId,
						// Toast.LENGTH_SHORT).show();
					}

					if (callback != null) {
						callback.afterEvent(
								SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE,
								SMSSDK.RESULT_COMPLETE, phoneMap);
					}
					finish();
				}
			}
		}
	}

	private void onCountryListGot(ArrayList<HashMap<String, Object>> countries) {
		// 解析国家列表
		for (HashMap<String, Object> country : countries) {
			String code = (String) country.get("zone");
			String rule = (String) country.get("rule");
			if (TextUtils.isEmpty(code) || TextUtils.isEmpty(rule)) {
				continue;
			}

			if (countryRules == null) {
				countryRules = new HashMap<String, String>();
			}
			countryRules.put(code, rule);
		}
		// 回归检查手机号码
		String phone = etPhoneNum.getText().toString().trim()
				.replaceAll("\\s*", "");
		String code = tvCountryNum.getText().toString().trim();
		checkPhoneNum(phone, code);
	}

	// 分割电话号码
	private String splitPhoneNum(String phone) {
		StringBuilder builder = new StringBuilder(phone);
		builder.reverse();
		for (int i = 4, len = builder.length(); i < len; i += 5) {
			builder.insert(i, ' ');
		}
		builder.reverse();
		return builder.toString();
	}

	// 检查电话号码
	private void checkPhoneNum(String phone, String code) {
		if (code.startsWith("+")) {
			code = code.substring(1);
		}

		if (TextUtils.isEmpty(phone)) {
			int resId = getStringRes(activity, "smssdk_write_mobile_phone");
			if (resId > 0) {
				Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT).show();
			}
			return;
		}

		String rule = countryRules.get(code);
		Pattern p = Pattern.compile(rule);
		Matcher m = p.matcher(phone);
		int resId = 0;
		if (!m.matches()) {
			resId = getStringRes(activity, "smssdk_write_right_mobile_phone");
			if (resId > 0) {
				Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT).show();
			}
			return;
		}
		isPhoneExist(res, phone);
		phone_ = phone;
		code_ = code;

	}

	// 是否请求发送验证码，对话框
	public void showDialog(final String phone, final String code) {

		int resId = getStyleRes(activity, "CommonDialog");
		if (resId > 0) {
			final String phoneNum = "+" + code + " " + splitPhoneNum(phone);
			final Dialog dialog = new Dialog(getContext(), resId);
			resId = getLayoutRes(activity, "smssdk_send_msg_dialog");
			if (resId > 0) {
				dialog.setContentView(resId);
				resId = getIdRes(activity, "tv_phone");
				((TextView) dialog.findViewById(resId)).setText(phoneNum);
				resId = getIdRes(activity, "tv_dialog_hint");
				TextView tv = (TextView) dialog.findViewById(resId);
				resId = getStringRes(activity, "smssdk_make_sure_mobile_detail");
				if (resId > 0) {
					String text = getContext().getString(resId);
					tv.setText(Html.fromHtml(text));
				}
				resId = getIdRes(activity, "btn_dialog_ok");
				if (resId > 0) {
					((Button) dialog.findViewById(resId))
							.setOnClickListener(new OnClickListener() {
								public void onClick(View v) {
									// 跳转到验证码页面
									dialog.dismiss();

									if (pd != null && pd.isShowing()) {
										pd.dismiss();
									}
									pd = CommonDialog.ProgressDialog(activity);
									if (pd != null) {
										pd.show();
									}
									Log.e("verification phone ==>>", phone);
									SMSSDK.getVerificationCode(code,
											phone.trim());
								}
							});
				}
				resId = getIdRes(activity, "btn_dialog_cancel");
				if (resId > 0) {
					((Button) dialog.findViewById(resId))
							.setOnClickListener(new OnClickListener() {
								public void onClick(View v) {
									dialog.dismiss();
								}
							});
				}
				dialog.setCanceledOnTouchOutside(true);
				dialog.show();
			}
		}
	}

	/** 请求验证码后，跳转到验证码填写页面 */
	private void afterVerificationCodeRequested() {
		String phone = etPhoneNum.getText().toString().trim()
				.replaceAll("\\s*", "");
		String code = tvCountryNum.getText().toString().trim();
		if (code.startsWith("+")) {
			code = code.substring(1);
		}
		String formatedPhone = "+" + code + " " + splitPhoneNum(phone);
		// 验证码页面
		IdentifyNumPage page = new IdentifyNumPage();
		page.setPhone(phone, code, formatedPhone);
		page.showForResult(activity, null, this);
	}

	private static AsyncHttpClient client = new AsyncHttpClient(); // 实例话对象

	static {
		client.setTimeout(5); // 设置链接超时，如果不设置，默认为15s
	}

	public static void isPhoneExist(AsyncHttpResponseHandler res, String phone) {
		String url = null;
		url = base_url + "searchPhone&phone=" + phone;
		Log.i("isExist-phone", url);
		client.post(url, res);
	}

	JsonHttpResponseHandler res = new JsonHttpResponseHandler() {

		@Override
		public void onSuccess(int statusCode, Header[] headers,
				JSONObject response) {
			super.onSuccess(statusCode, headers, response);
			Log.i("phone-is-response", response.toString() + "+"
					+ reset_or_regist);
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
					int count = response.getInt("find");
					if (reset_or_regist.equals("reset")) {
						// 重置密码
						if (count == 0) {
							// 重置密码手机号不存在
							AlertDialog.Builder builder = new Builder(activity);
							builder.setTitle("手机号码未注册");
							
							builder.setNegativeButton("确认",
									new DialogInterface.OnClickListener() {

										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();

										}

									});
							builder.create().show();

						} else {
							// 重置密码手机号存在
							// 弹出对话框，发送验证码
							showDialog(phone_, code_);
						}
					} else {
						// 用户注册
						if (count == 0) {
							// 注册用户手机号不存在
							// 弹出对话框，发送验证码
							showDialog(phone_, code_);
						} else {
							// 注册用户手机号存在
							AlertDialog.Builder builder = new Builder(activity);
							builder.setTitle("手机号码已注册");

							builder.setNegativeButton("确认",
									new DialogInterface.OnClickListener() {

										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();

										}

									});
							builder.create().show();
						}
					}

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

			super.onFailure(statusCode, headers, throwable, errorResponse);

		}
	};
}
