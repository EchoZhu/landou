package com.zykj.landous.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.zykj.landous.R;
import com.zykj.landous.Tools.HttpUtils;
import com.zykj.landous.adapter.E2_AddressManagerAdapter;

public class E2_AddressManageActivity extends Activity implements
		OnClickListener {
	private ImageView addBtn;
	private ImageView backBtn;
	private ArrayList<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
	// private SimpleAdapter simpleAdapter;
	private E2_AddressManagerAdapter addrAdapter;
	private ProgressDialog loadingPDialog = null;
	private SharedPreferences shared;
	private SharedPreferences.Editor editor;
	public static String come = "";
	private Intent it;
	public static View data_null;
	ListView list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.e2_address_manage);
		addBtn = (ImageView) findViewById(R.id.addr_top_view_add);
		addBtn.setOnClickListener(this);
		backBtn = (ImageView) findViewById(R.id.addr_manager_top_view_back);
		backBtn.setOnClickListener(this);
		it = getIntent();
		data_null = (View) findViewById(R.id.data_null);
		data_null.setVisibility(View.GONE);
		come = it.getStringExtra("come");
		isLogin();
		addrAdapter = new E2_AddressManagerAdapter(this, listItems);
		list = (ListView) findViewById(R.id.address_list);
		list.setAdapter(addrAdapter);
		list.setOnItemClickListener(new OnItemClickListener() {
			// 点击显示所选的位置
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// Toast.makeText(getApplicationContext(), position+"",
				// Toast.LENGTH_LONG).show();
			}
		});

	}

	public void isLogin() {
		shared = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
		String userID = shared.getString("member_id", "");

		if (userID.equals("")) {
			Intent it = new Intent(E2_AddressManageActivity.this,
					E6_SigninActivity.class);
			startActivity(it);
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		loadingPDialog = new ProgressDialog(this);
		loadingPDialog.setMessage("正在加载....");
		loadingPDialog.setCancelable(false);
		loadingPDialog.show();

	}

	@Override
	protected void onResume() {
		super.onResume();
		HttpUtils.getAddress(res);
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.addr_top_view_add:
			// TODO 跳转添加地址页面
			Intent intent = new Intent(E2_AddressManageActivity.this,
					E3_AddAddressActivity.class);
			startActivity(intent);
			this.overridePendingTransition(R.anim.push_right_in,
					R.anim.push_right_out);
			break;
		case R.id.addr_manager_top_view_back:
			super.finish();
			setResult(D1_OrderConfirmActivity.ADDRESS_CODE);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		default:
			break;
		}

	}

	JsonHttpResponseHandler res = new JsonHttpResponseHandler() {
		@Override
		public void onSuccess(int statusCode, org.apache.http.Header[] headers,
				org.json.JSONObject response) {
			super.onSuccess(statusCode, headers, response);
			Log.i("address-manager", response.toString());
			loadingPDialog.dismiss();
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
				listItems.clear();
				try {
					JSONArray array = response.getJSONArray("list");
					for (int i = 0; i < array.length(); i++) {
						JSONObject jsonItem = array.getJSONObject(i);
						Map<String, Object> listItem = new HashMap<String, Object>();
						listItem.put("name", jsonItem.getString("true_name"));
						listItem.put("phone", jsonItem.getString("mob_phone"));
						listItem.put("region", jsonItem.getString("area_info")
								+ jsonItem.getString("address"));
						listItem.put("area_id", jsonItem.getString("area_id"));
						listItem.put("address", jsonItem.getString("address"));
						listItem.put("address_id",
								jsonItem.getString("address_id"));
						listItem.put("is_default", jsonItem.get("is_default"));
						listItems.add(listItem);
					}
					data_null.setVisibility(View.GONE);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					data_null.setVisibility(View.VISIBLE);
				}
				addrAdapter.notifyDataSetChanged();

			} else {
				try {
					String msg = response.getString("message");
					Log.d("perssion", msg);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		@Override
		public void onFailure(int statusCode, org.apache.http.Header[] headers,
				Throwable throwable, JSONObject errorResponse) {
			super.onFailure(statusCode, headers, throwable, errorResponse);
			loadingPDialog.dismiss();
			list.setBackgroundResource(R.drawable.img_net_error);
			Toast.makeText(getApplicationContext(), "网络连接超时", Toast.LENGTH_LONG)
					.show();
		};
	};

}
