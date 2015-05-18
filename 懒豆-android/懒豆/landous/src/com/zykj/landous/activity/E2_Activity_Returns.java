package com.zykj.landous.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.BeeFramework.activity.BaseActivity;
import com.external.activeandroid.util.Log;
import com.external.maxwin.view.XListView.IXListViewListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.zykj.landous.LandousAppConst;
import com.zykj.landous.R;
import com.zykj.landous.Tools.HttpUtils;
import com.zykj.landous.adapter.E2_Adapter_Returns;
import com.zykj.landous.view.MyListView;

/**
 * @author 作者 zhang
 * @version 创建时间：2015年1月26日 上午12:34:46 类说明退款列表
 */
public class E2_Activity_Returns extends BaseActivity implements
		IXListViewListener, OnClickListener {
	private Intent it;
	private ImageView iv_back;
	private MyListView listview;
	ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
	private E2_Adapter_Returns adapter;
	String order_id = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.e2_activity_returns);
		init();
	}

	private void init() {
		it = getIntent();
		order_id = it.getStringExtra("order_id");
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		adapter = new E2_Adapter_Returns(E2_Activity_Returns.this, data);
		listview = (MyListView) findViewById(R.id.listview);
		listview.setPullLoadEnable(false);
		listview.setPullRefreshEnable(true);
		listview.setXListViewListener(this, 0);
		listview.setRefreshTime();
		listview.setAdapter(adapter);
		HttpUtils.getOrderDetail(res, order_id);
	}

	@Override
	public void onRefresh(int id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoadMore(int id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			this.finish();

			break;

		default:
			break;
		}
	}

	JsonHttpResponseHandler res = new JsonHttpResponseHandler() {
		public void onSuccess(int statusCode, org.apache.http.Header[] headers,
				org.json.JSONObject response) {
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
				data.clear();
				try {
					JSONObject json = response.getJSONObject("data");
					String store_id = json.getString("store_id") + "/";
					JSONArray array = json.getJSONArray("order_goods");
					for (int i = 0; i < array.length(); i++) {
						JSONObject jsonItem = array.getJSONObject(i);
						Map<String, String> map = new HashMap<String, String>();
						map.put("goods_name", jsonItem.getString("goods_name"));
						map.put("goods_price",
								"￥" + jsonItem.getString("goods_price"));
						map.put("goods_image", LandousAppConst.HOME_IMG_URL
								+ store_id + jsonItem.getString("goods_image"));
						map.put("order_id", order_id);
						map.put("goods_num", jsonItem.getString("goods_num"));
						map.put("rec_id", jsonItem.getString("rec_id"));
						map.put("goods_num", jsonItem.getString("goods_num"));
						map.put("goods_pay_price",
								jsonItem.getString("goods_pay_price"));
						String refund_state = jsonItem
								.getString("refund_return");
						map.put("refund_state", refund_state.equals("")? "0" : "1");
						Log.i("refund_state", refund_state);
						data.add(map);
					}
					adapter.notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		};

		public void onFailure(int statusCode, org.apache.http.Header[] headers,
				Throwable throwable, org.json.JSONObject errorResponse) {
			super.onFailure(statusCode, headers, throwable, errorResponse);
		};
	};
}
