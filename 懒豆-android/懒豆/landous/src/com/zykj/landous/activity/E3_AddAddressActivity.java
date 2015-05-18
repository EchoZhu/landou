package com.zykj.landous.activity;

import java.lang.reflect.Array;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.zykj.landous.R;
import com.zykj.landous.R.layout;
import com.zykj.landous.Tools.HttpUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class E3_AddAddressActivity extends Activity implements OnClickListener {
	private EditText add_address_name;
	private EditText add_address_telNum;
	private EditText area_name;
	private LinearLayout add_address_area;
	private EditText add_address_detail;
	private ImageView back;
	private FrameLayout add_address_btn;
	private String area_id = "";
	private TextView add_address_address;
	private int[] area_ids = { 2607, 2611, 2612, 2606, 2608, 2609, 2610, 2613,
			2614, 2615, 2616, 2617 };
	private String[] area_names = { "兰山区", "河东区", "罗庄区", "临沭县", "平邑县", "沂南县",
			"沂水县", "苍山县", "莒南县", "蒙阴县", "费县", "郯城县" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.e3_add_address_activity);
		initView();

	}

	public void initView() {
		add_address_address = (TextView) findViewById(R.id.add_address_address);
		add_address_name = (EditText) findViewById(R.id.add_address_name);
		add_address_telNum = (EditText) findViewById(R.id.add_address_telNum);
		area_name = (EditText) findViewById(R.id.area_name);
		add_address_area = (LinearLayout) findViewById(R.id.add_address_area);
		add_address_detail = (EditText) findViewById(R.id.add_address_detail);
		back = (ImageView) findViewById(R.id.top_view_back);
		add_address_btn = (FrameLayout) findViewById(R.id.add_address_btn);
		add_address_btn.setOnClickListener(this);
		add_address_area.setOnClickListener(this);
		add_address_detail.setOnClickListener(this);
		add_address_name.setOnClickListener(this);
		add_address_telNum.setOnClickListener(this);
		area_name.setOnClickListener(this);
		back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.top_view_back:
			super.finish();
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.add_address_btn:

			String phone = add_address_telNum.getText().toString();
			String name = add_address_name.getText().toString();
			String detail = add_address_detail.getText().toString();
			if (name == null || name.replace(" ", "").equals("")) {
				Toast.makeText(this, "收货人姓名不能为空", Toast.LENGTH_SHORT).show();
			} else if (phone == null || phone.replace(" ", "").equals("")) {
				// add_address_telNum.setError("手机号不能为空");
				Toast.makeText(this, "手机号码不能为空", Toast.LENGTH_SHORT).show();
			} else if (!isMobile(phone)) {
				// add_address_telNum.setError("手机号内容格式填写不完整");
				Toast.makeText(this, "手机号码不完整", Toast.LENGTH_SHORT).show();
				
			} else if (area_id.equals("")) {
				Toast.makeText(this, "请选择配送收货地区", Toast.LENGTH_SHORT).show();
				// add_address_name.setError("收货人姓名不能为空");

			} else if (detail == null || detail.replace(" ", "").equals("")) {
				// add_address_detail.setError("收货地址详情不能为空");
				Toast.makeText(this, "收货地址详情不能为空", Toast.LENGTH_SHORT).show();
				

			} else {
				HttpUtils.addAddress(res, name, area_id, detail, phone);
			}
			Log.i("add-test", "add-test" + name + area_id + detail + phone);
			break;
		case R.id.add_address_area:
			// TODO判断选的第几个,对area——id赋值
			new AlertDialog.Builder(this)
					.setTitle("请选择地区")
					.setIcon(android.R.drawable.ic_dialog_info)
					.setSingleChoiceItems(area_names, 0,
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									add_address_address
											.setText(area_names[which]);
									area_id = area_ids[which] + "";
								}
							}).setNegativeButton("确认", null).show();
			break;
		default:
			break;
		}

	}

	/**
	 * 手机号验证
	 * 
	 * @param str
	 * @return 验证通过返回true
	 */
	public static boolean isMobile(String str) {
		Pattern p = null;
		Matcher m = null;
		boolean b = false;
		p = Pattern.compile("^[1][3,4,5,8][0-9]{9}$"); // 验证手机号
		m = p.matcher(str);
		b = m.matches();
		return b;
	}

	JsonHttpResponseHandler res = new JsonHttpResponseHandler() {
		public void onSuccess(int statusCode, org.apache.http.Header[] headers,
				org.json.JSONObject response) {
			super.onSuccess(statusCode, headers, response);
			Log.i("add-test", response.toString());
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
				finish();
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
			}
		};

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
