package com.zykj.landous.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.BeeFramework.activity.BaseActivity;
import com.external.maxwin.view.XListView.IXListViewListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.zykj.landous.R;
import com.zykj.landous.Tools.HttpUtils;
import com.zykj.landous.adapter.C2_ShopClassAdapter;

/**
 * 店铺商品分类
 * 
 * @author zhangxu
 * 
 */

public class C2_ShopClassActivity extends BaseActivity implements
		IXListViewListener, OnClickListener {
	private String store_id = "";
	private ListView listview;
	// String str[] = new String[] { "全部", "生鲜冷冻", "进口商品", "粮油", "牛奶", "洗涤",
	// "护肤",
	// "锅具", "箱包", "毛巾", "自有产品", "黄油奶酪" };
	String str[];
	private C2_ShopClassAdapter shopClassAdapter;
	private LinearLayout ll_back;
	private ProgressDialog loadingPDialog = null;
	ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.c2_activityshopclass);
		store_id = C1_ShopActivity.store_id;
		init();
		loadingPDialog = new ProgressDialog(this);
		loadingPDialog.setMessage("正在加载....");
		loadingPDialog.setCancelable(false);
		loadingPDialog.show();
	}

	private void init() {
		HttpUtils.getStoreGoodsClass(res, store_id);
		Toast.makeText(getApplicationContext(), store_id, Toast.LENGTH_LONG)
				.show();
		ll_back = (LinearLayout) findViewById(R.id.ll_back);
		ll_back.setOnClickListener(this);
		listview = (ListView) findViewById(R.id.listview);
		shopClassAdapter = new C2_ShopClassAdapter(C2_ShopClassActivity.this,
				data);
		listview.setAdapter(shopClassAdapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent it = new Intent(C2_ShopClassActivity.this,
						B1_GoodsListActivity.class);
				it.putExtra("stc_id", view.getTag().toString());
				startActivity(it);
			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_back:
			this.finish();
			break;

		default:
			break;
		}

	}

	@Override
	public void onRefresh(int id) {

	}

	@Override
	public void onLoadMore(int id) {

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
				data.clear();
				try {
					JSONArray array = response.getJSONArray("list");
					for (int i = 0; i < array.length(); i++) {
						JSONObject jsonItem = array.getJSONObject(i);
						Map<String, String> map = new HashMap();
						map.put("stc_id", jsonItem.getString("stc_id"));
						map.put("stc_name", jsonItem.getString("stc_name"));
						data.add(map);
					}
					shopClassAdapter.notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// goodsAdapter.notifyDataSetChanged();
				loadingPDialog.dismiss();

			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers,
				Throwable throwable, JSONObject errorResponse) {
			// TODO Auto-generated method stub
			Toast.makeText(getApplicationContext(), "网络连接超时", Toast.LENGTH_LONG)
					.show();
			super.onFailure(statusCode, headers, throwable, errorResponse);
			loadingPDialog.dismiss();
		}
	};
}
