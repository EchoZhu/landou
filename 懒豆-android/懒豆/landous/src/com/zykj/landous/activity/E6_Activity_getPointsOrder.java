package com.zykj.landous.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.BeeFramework.activity.BaseActivity;
import com.external.maxwin.view.XListView.IXListViewListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.zykj.landous.LandousAppConst;
import com.zykj.landous.R;
import com.zykj.landous.Tools.HttpUtils;
import com.zykj.landous.adapter.E6_Adapter_getPointsOrder;
import com.zykj.landous.view.MyListView;

/**
 * @author 作者 zhang
 * @version 创建时间：2015年1月22日 上午12:19:02 类说明 查询积分订单
 */
public class E6_Activity_getPointsOrder extends BaseActivity implements
		IXListViewListener, OnClickListener {
	private MyListView listview;
	private E6_Adapter_getPointsOrder adapter;
	List<Map<String, String>> data = new ArrayList<Map<String, String>>();
	private ProgressDialog loadingPDialog = null;
	private ImageView iv_back;
	int per_page = 10;
	boolean MAX_Length = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.e6_activity_getpointsorder);
		listview = (MyListView) findViewById(R.id.listview);
		adapter = new E6_Adapter_getPointsOrder(
				E6_Activity_getPointsOrder.this, data);
		listview.setAdapter(adapter);
		listview.setPullLoadEnable(true);
		listview.setPullRefreshEnable(true);
		listview.setXListViewListener(this, 0);
		listview.setRefreshTime();
		loadingPDialog = new ProgressDialog(this);
		loadingPDialog.setMessage("正在加载....");
		loadingPDialog.setCancelable(false);
		loadingPDialog.show();
		HttpUtils.getPointsOrder(res, "1", "" + per_page);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
	}

	@Override
	public void onRefresh(int id) {
		loadingPDialog.show();
		HttpUtils.getPointsOrder(res, "1", "" + per_page);
	}

	@Override
	public void onLoadMore(int id) {
		listview.setRefreshTime();
		if (!MAX_Length) {
			per_page += 10;
			HttpUtils.getPointsOrder(res, "1", "" + per_page);
		} else {
			Toast.makeText(E6_Activity_getPointsOrder.this, "您只有这么多订单",
					Toast.LENGTH_LONG).show();
			listview.stopLoadMore();
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
				listview.stopLoadMore();
				listview.stopRefresh();
				try {
					JSONArray array = response.getJSONArray("list");
					if (array.length() - data.size() < 10) {
						MAX_Length = true;
					}
					data.clear();
					for (int i = 0; i < array.length(); i++) {
						JSONObject jsonItem = array.getJSONObject(i);
						Map<String, String> map = new HashMap<String, String>();
						map.put("point_ordersn",
								jsonItem.getString("point_ordersn"));
						JSONArray goods_array = jsonItem
								.getJSONArray("goods_list");
						JSONObject json = goods_array.getJSONObject(0);
						map.put("point_goodsname",
								json.getString("point_goodsname"));
						map.put("point_goodspoints",
								json.getString("point_goodspoints"));
						map.put("point_orderstate",
								jsonItem.getString("point_orderstate"));
						map.put("point_goodsnum",
								json.getString("point_goodsnum"));
						map.put("point_goodsimage",
								"http://112.53.78.18:8088/data/upload/shop/pointprod/"
										+ json.getString("point_goodsimage"));
						map.put("point_orderid",
								json.getString("point_orderid"));
						data.add(map);
					}
					loadingPDialog.dismiss();
					adapter.notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			loadingPDialog.dismiss();
		};

		public void onFailure(int statusCode, org.apache.http.Header[] headers,
				Throwable throwable, org.json.JSONObject errorResponse) {
			super.onFailure(statusCode, headers, throwable, errorResponse);
			loadingPDialog.dismiss();
		};
	};

	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		BeeFrameworkApp.getInstance().showBug(this);
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
			Intent intent = new Intent(E6_Activity_getPointsOrder.this,
					E5_CoinStoreActivity.class);
			startActivity(intent);
		}
		return true;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_back:
			this.finish();
			Intent intent = new Intent(E6_Activity_getPointsOrder.this,
					E5_CoinStoreActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}

	}
}
