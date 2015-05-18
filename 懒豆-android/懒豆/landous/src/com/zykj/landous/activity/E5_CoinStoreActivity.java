package com.zykj.landous.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.BeeFramework.activity.BaseActivity;
import com.external.maxwin.view.XListView.IXListViewListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.zykj.landous.LandousAppConst;
import com.zykj.landous.R;
import com.zykj.landous.Tools.HttpUtils;
import com.zykj.landous.adapter.E5_CoinGoodsAdapter;
import com.zykj.landous.view.MyListView;

/**
 * 积分商城
 * 
 * @author zhangxu
 * 
 */
public class E5_CoinStoreActivity extends BaseActivity implements
		IXListViewListener, OnClickListener {
	private MyListView mGoodsListview;
	private ImageView backBtn;
	ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
	E5_CoinGoodsAdapter goodsAdapter;
	ImageView iv_menu;
	private Intent it;
	private ProgressDialog loadingPDialog = null;
	private View view_data;
	TextView tv_message;
	int per_page = 10;
	boolean MAX_Length = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.e5_coin_store_activity);
		loadingPDialog = new ProgressDialog(this);
		loadingPDialog.setMessage("正在加载....");
		loadingPDialog.setCancelable(false);
		loadingPDialog.show();
		HttpUtils.getPointsGoods(res, per_page);
		mGoodsListview = (MyListView) findViewById(R.id.coin_goodslistview);
		mGoodsListview.setPullLoadEnable(true);
		mGoodsListview.setPullRefreshEnable(true);
		mGoodsListview.setXListViewListener(this, 0);
		mGoodsListview.setRefreshTime();
		goodsAdapter = new E5_CoinGoodsAdapter(this, data);
		mGoodsListview.setAdapter(goodsAdapter);
		iv_menu = (ImageView) findViewById(R.id.iv_menu);
		iv_menu.setOnClickListener(this);
		backBtn = (ImageView) findViewById(R.id.coin_store_top_view_back);
		backBtn.setOnClickListener(this);
		view_data = (View) findViewById(R.id.view_data);
		tv_message = (TextView) view_data.findViewById(R.id.tv_message);
		tv_message.setText("积分中心还没有商品上架哟");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.coin_store_top_view_back:
			super.finish();
			this.finish();
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.iv_menu:
			this.finish();
			it = new Intent(E5_CoinStoreActivity.this,
					E6_Activity_getPointsOrder.class);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			startActivity(it);
			break;
		default:
			break;
		}

	}

	@Override
	public void onRefresh(int id) {
		// TODO Auto-generated method stub
		data.clear();
		HttpUtils.getPointsGoods(res, per_page);

	}

	@Override
	public void onLoadMore(int id) {
		loadingPDialog.show();
		if (!MAX_Length) {
			per_page += 10;
			HttpUtils.getPointsLog(res, "1", per_page + "");
		} else {
			Toast.makeText(E5_CoinStoreActivity.this, "只有这么多订单哟",
					Toast.LENGTH_LONG).show();
			loadingPDialog.dismiss();
			mGoodsListview.stopLoadMore();
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
				mGoodsListview.stopLoadMore();
				mGoodsListview.stopRefresh();

				try {
					JSONArray array = response.getJSONArray("list");
					if (array.length() - data.size() < 10) {
						MAX_Length = true;
					}
					data.clear();
					for (int i = 0; i < array.length(); i++) {
						JSONObject jsonItem = array.getJSONObject(i);
						Map<String, String> map = new HashMap<String, String>();
						map.put("pgoods_name",
								jsonItem.getString("pgoods_name"));
						map.put("pgoods_price",
								jsonItem.getString("pgoods_price"));
						map.put("pgoods_points",
								jsonItem.getString("pgoods_points"));
						map.put("pgoods_image",
								LandousAppConst.PointsGoods_IMG_URL
										+ jsonItem.getString("pgoods_image"));
						map.put("pgoods_storage",
								jsonItem.getString("pgoods_storage"));
						map.put("pgoods_id", jsonItem.getString("pgoods_id"));
						data.add(map);
					}
					goodsAdapter.notifyDataSetChanged();
					view_data.setVisibility(View.GONE);
				} catch (JSONException e) {
					view_data.setVisibility(View.VISIBLE);
					e.printStackTrace();
				}
			}
			loadingPDialog.dismiss();
		};

		public void onFailure(int statusCode, org.apache.http.Header[] headers,
				Throwable throwable, org.json.JSONObject errorResponse) {
			loadingPDialog.dismiss();
			mGoodsListview.setBackgroundResource(R.drawable.img_net_error);
			super.onFailure(statusCode, headers, throwable, errorResponse);
		};
	};
}
