package com.zykj.landous.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.zykj.landous.Data.BaseData;
import com.zykj.landous.Tools.HttpUtils;
import com.zykj.landous.adapter.E1_PaymentAdapter;
import com.zykj.landous.view.MyListView;

/**
 * @author 作者 zhang
 * @version 创建时间：2015年1月15日 下午10:08:36 类说明已付款.待发货
 */

public class E1_PaymentAvtivity extends BaseActivity implements
		IXListViewListener, OnClickListener {
	private TextView tv_title;
	private MyListView listview;
	private View iv_back;
	private E1_PaymentAdapter adapter;
	private String order_state = "";
	private Intent it;
	private ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
	private SharedPreferences shared;
	private SharedPreferences.Editor editor;
	private ProgressDialog loadingPDialog = null;
	int page = 1;
	int per_page = 10;
	boolean MAX_Length = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.e1_payment_activity);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("待发货");
		init();

	}

	private void init() {
		loadingPDialog = new ProgressDialog(this);
		loadingPDialog.setMessage("正在加载....");
		loadingPDialog.setCancelable(false);
		it = getIntent();
		order_state = it.getStringExtra("order_state");

		iv_back = (View) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		listview = (MyListView) findViewById(R.id.listview);
		listview.setPullLoadEnable(true);
		listview.setPullRefreshEnable(true);
		listview.setXListViewListener(this, 0);
		listview.setRefreshTime();
		adapter = new E1_PaymentAdapter(E1_PaymentAvtivity.this, data);
		listview.setAdapter(adapter);

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isLogin()) {
			HttpUtils.getOrderList(res, "20&page=" + page + "&per_page="
					+ per_page);
			loadingPDialog.show();
		}
	}

	public Boolean isLogin() {
		shared = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
		String userID = shared.getString("member_id", "");
		Log.i("login-user-id", userID);
		if (userID.equals("")) {
			Intent it = new Intent(E1_PaymentAvtivity.this,
					E6_SigninActivity.class);
			startActivity(it);
			return false;
		}
		return true;
	}

	@Override
	public void onRefresh(int id) {
		listview.setRefreshTime();
		HttpUtils
				.getOrderList(res, "20&page=" + page + "&per_page=" + per_page);

	}

	@Override
	public void onLoadMore(int id) {
		listview.setRefreshTime();
		if (!MAX_Length) {
			per_page += 10;
			HttpUtils.getOrderList(res, "20&page=" + page + "&per_page="
					+ per_page);
		} else {
			Toast.makeText(E1_PaymentAvtivity.this, "您只有这么多订单",
					Toast.LENGTH_LONG).show();
			listview.stopLoadMore();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (200 == requestCode) {
			HttpUtils.getOrderList(res, "20");
			loadingPDialog.show();
		}
		super.onActivityResult(requestCode, resultCode, data);
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
				listview.stopLoadMore();
				listview.stopRefresh();
				try {
					JSONArray array = response.getJSONArray("list");
					if (array.length() - data.size() < 10) {
						MAX_Length = true;
					}
					data.clear();
					for (int i = 0; i < array.length(); i++) {
						JSONArray array_order_list = array.getJSONObject(i)
								.getJSONArray("order_list");
						double goods_amount = 0;
						double discount = BaseData.online_pay_discount;
						try {
							discount = array.getJSONObject(i).getDouble(
									"discount");
						} catch (JSONException e) {
							discount = 0;
						}
						String sameorder = "";
						double shipping_fee = 0;
						for (int j = 0; j < array_order_list.length(); j++) {
							JSONObject json = array_order_list.getJSONObject(j);
							Map<String, String> map = new HashMap<String, String>();
							map.put("order_sn", json.getString("order_sn"));
							map.put("store_name", json.getString("store_name"));
							goods_amount += json.getDouble("goods_amount");
							shipping_fee += json.getDouble("shipping_fee");
							JSONArray array_order_goods = json
									.getJSONArray("order_goods");
							{
								int all_num = 0;
								for (int k = 0; k < array_order_goods.length(); k++) {
									JSONObject obj = array_order_goods
											.getJSONObject(k);
									Map<String, String> goods_map = new HashMap<String, String>();
									goods_map.put("lock_state",
											json.getString("lock_state"));
									goods_map.put("goods_name",
											obj.getString("goods_name"));
									goods_map.put("order_id",
											obj.getString("order_id"));
									goods_map.put("goods_price",
											obj.getString("goods_price"));
									all_num += obj.getInt("goods_num");
									goods_map.put("goods_num",
											obj.getString("goods_num"));
									goods_map.put("goods_id",
											obj.getString("goods_id"));
									goods_map.put("ship_method",
											json.getString("ship_method"));
									goods_map
											.put("store_name",
													"订单号:"
															+ json.getString("order_sn"));// /店铺改为订单号
									goods_map.put("payment_code",
											json.getString("payment_code"));
									goods_map.put("goods_amount", goods_amount
											+ "");
									goods_map.put("shipping_fee", shipping_fee
											+ "");
									goods_map.put("all_num", all_num + "");
									goods_map.put("discount", discount + "");
									goods_map
											.put("goods_image",
													LandousAppConst.HOME_IMG_URL
															+ json.getString("store_id")
															+ "/"
															+ obj.getString("goods_image"));
									if (array_order_goods.length() == 1) {
										goods_map.put("position", "one");
									} else {
										if (k == 0) {
											goods_map.put("position", "head");
										} else if (k == array_order_goods
												.length() - 1) {
											goods_map.put("position", "tail");
										} else {
											goods_map.put("position", "body");
										}
									}
									data.add(goods_map);
								}

							}

						}

					}
					Log.i("landousdata", data.size() + "weiweiwieiweiwie"
							+ data);
					adapter.notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					AlertDialog.Builder builder = new Builder(
							E1_PaymentAvtivity.this);
					builder.setTitle("没有订单");

					builder.setNegativeButton("确认",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}

							});
					builder.create().show();
				}

			}

		};

		public void onFailure(int statusCode, org.apache.http.Header[] headers,
				Throwable throwable, org.json.JSONObject errorResponse) {
			super.onFailure(statusCode, headers, throwable, errorResponse);
			loadingPDialog.dismiss();
		};
	};

}
