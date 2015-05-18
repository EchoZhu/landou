package com.zykj.landous.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.BeeFramework.BeeFrameworkApp;
import com.BeeFramework.activity.BaseActivity;
import com.external.maxwin.view.XListView.IXListViewListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zykj.landous.LandousAppConst;
import com.zykj.landous.R;
import com.zykj.landous.Tools.HttpUtils;
import com.zykj.landous.adapter.C1_ShopAdapter;
import com.zykj.landous.view.MyListView;

public class C1_ShopActivity extends BaseActivity implements
		IXListViewListener, OnClickListener {

	private LinearLayout ll_back;
	private LinearLayout ll_menu;
	private LinearLayout shop;
	/**
	 * 收藏按钮
	 */
	private Button btn_collect;
	private MyListView listview;
	private C1_ShopAdapter shopAdapter;
	private Intent it;
	public static String store_id = "";
	private String store_zy = "";
	private String store_label = "";
	private String store_name = "";
	/**
	 * store_id, store_zy, store_label
	 */
	public static String str[];
	private TextView tv_shopname;
	private TextView tv_major;
	private ImageView iv_shops;
	private ProgressDialog loadingPDialog = null;
	ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.c1_activity_shop);
		init();
		loadingPDialog = new ProgressDialog(this);
		loadingPDialog.setMessage("正在加载....");
		loadingPDialog.setCancelable(false);
		loadingPDialog.show();
	}

	@SuppressLint("NewApi")
	private void init() {
		it = getIntent();
		str = it.getStringArrayExtra("store");
		if (str != null) {
			store_id = str[0];
			store_zy = str[1];
			store_label = str[2];
			store_name = str[3];
		}
		tv_shopname = (TextView) findViewById(R.id.tv_shopname);
		tv_shopname.setText(store_name);
		tv_major = (TextView) findViewById(R.id.tv_major);
		tv_major.setText("主营项目：" + store_zy);
		iv_shops = (ImageView) findViewById(R.id.iv_shops);
		ImageLoader.getInstance().displayImage(store_label, iv_shops,
				BeeFrameworkApp.options);
		ll_back = (LinearLayout) findViewById(R.id.ll_back);
		ll_back.setOnClickListener(this);
		ll_menu = (LinearLayout) findViewById(R.id.ll_menu);
		ll_menu.setOnClickListener(this);
		shop = (LinearLayout) findViewById(R.id.shop);
		shop.setBackground(null);
		btn_collect = (Button) findViewById(R.id.btn_collect);
		btn_collect.setOnClickListener(this);
		listview = (MyListView) findViewById(R.id.listview);
		shopAdapter = new C1_ShopAdapter(getApplicationContext(), data);
		listview.setPullLoadEnable(false);
		listview.setPullRefreshEnable(true);
		listview.setXListViewListener(this, 0);
		listview.setRefreshTime();
		listview.setAdapter(shopAdapter);
//		HttpUtils.getGoodsList(res_getGoodsList, null, store_id
//				+ "&orderby=goods_salenum%20desc", null);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_back:
			this.finish();
			break;
		case R.id.ll_menu:
			it = new Intent(getApplicationContext(), C2_ShopClassActivity.class);
			startActivity(it);
			break;
		case R.id.btn_collect:
			collect(v.getTag());
			break;
		default:
			break;
		}
	}

	private void collect(Object obj) {
		int type = (Integer) (obj == null ? 0 : obj);
		if (type % 2 == 0) {
			btn_collect.setText(Html.fromHtml("<font color=WHITE>收藏</font>"));// 有其他方法告诉我zx
			btn_collect.setBackgroundResource(R.drawable.icon_collect);
		} else {
			btn_collect.setText("已收藏");
			btn_collect.setBackgroundResource(R.drawable.icon_collected);
		}
		type++;
		btn_collect.setTag(type);
	}

	@Override
	public void onRefresh(int id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoadMore(int id) {
		// TODO Auto-generated method stub

	}

	JsonHttpResponseHandler res_getGoodsList = new JsonHttpResponseHandler() {

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
					data.clear();
					JSONArray array = response.getJSONArray("list");
					for (int i = 0; i < (array.length() > 5 ? 5 : array
							.length()); i++) {
						JSONObject jsonItem = array.getJSONObject(i);
						Map<String, String> map = new HashMap();
						map.put("goods_name", jsonItem.getString("goods_name"));
						map.put("store_name", jsonItem.getString("store_name"));
						map.put("goods_marketprice",
								jsonItem.getString("goods_marketprice"));
						map.put("goods_price",
								jsonItem.getString("goods_price"));
						map.put("goods_id", jsonItem.getString("goods_id"));
						map.put("goods_salenum",
								jsonItem.getString("goods_salenum"));
						map.put("goods_image", LandousAppConst.HOME_IMG_URL
								+ jsonItem.getString("store_id") + "/"
								+ jsonItem.getString("goods_image"));
						data.add(map);
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				shopAdapter.notifyDataSetChanged();
				loadingPDialog.dismiss();

			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers,
				Throwable throwable, JSONObject errorResponse) {
			// TODO Auto-generated method stub
			loadingPDialog.dismiss();
			Toast.makeText(getApplicationContext(), "网络连接超时", Toast.LENGTH_LONG)
					.show();
			super.onFailure(statusCode, headers, throwable, errorResponse);
		}
	};

}
