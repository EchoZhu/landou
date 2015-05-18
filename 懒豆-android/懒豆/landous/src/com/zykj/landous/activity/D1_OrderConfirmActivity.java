package com.zykj.landous.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.ImageView.ScaleType;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.BeeFramework.BeeFrameworkApp;
import com.BeeFramework.activity.BaseActivity;
import com.external.maxwin.view.XListView.IXListViewListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zykj.landous.LandousAppConst;
import com.zykj.landous.R;
import com.zykj.landous.Data.BaseData;
import com.zykj.landous.Tools.HttpUtils;
import com.zykj.landous.Tools.MathTools;
import com.zykj.landous.adapter.D1_OrderConfirmAdapter;
import com.zykj.landous.alipay.Fiap;
import com.zykj.landous.fragment.D0_ShopingCartFragment;
import com.zykj.landous.view.MyListView;
import com.zykj.landous.view.SegmentedGroup;

/**
 * @author 作者 zhang
 * @version 创建时间：2015年1月13日 下午8:59:27 类说明 订单确认 car_id必选
 */
public class D1_OrderConfirmActivity extends BaseActivity implements
		OnCheckedChangeListener, OnClickListener, IXListViewListener {
	Intent it;
	String cart_id = "";
	/**
	 * 发货方式,默认配送
	 */
	String ship_method = "delivery";
	/**
	 * 付款方式,默认在线付款
	 */
	String pay_method = "online";
	/**
	 * 收货地址id
	 */
	String address_id = "";
	/**
	 * 选择配送方式
	 */
	SegmentedGroup SG_ship_method;
	/**
	 * 选择支付方式
	 */
	SegmentedGroup SG_pay_method;
	private MyListView listview;
	private View headView;
	ArrayList<Map<String, String>> cart_list_data = new ArrayList<Map<String, String>>();
	D1_OrderConfirmAdapter adapter;
	private ProgressDialog loadingPDialog = null;
	private TextView tv_all_price;
	// 每个店铺所有物品的价格
	String purchase_price;
	/**
	 * 确认结算
	 */
	private Button btn_sub;
	/**
	 * 收货人姓名
	 */
	private TextView tv_name;
	/**
	 * 收货地址
	 */
	private TextView tv_address;
	/**
	 * 手机号
	 */
	private TextView tv_phone;
	/**
	 * 邮费
	 */
	private TextView tv_postage;
	private double d_postage = 5;
	private ImageView iv_back_btn;
	double all_price = 0;
	private LinearLayout ll_chose_address;
	public static int ADDRESS_CODE = 200;
	private RadioButton rb_dispatching;
	private RadioButton rb_pickup;
	private RadioButton rb_online;
	private RadioButton rb_offline;
	/***
	 * type 200 立即购买
	 */
	private int type = 0;
	private String goods_id = "";
	private int count = 1;
	private TextView tv_chose;
	private LinearLayout ll_price;
	private LinearLayout ll_noaddress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		it = getIntent();
		cart_id = it.getStringExtra("cart_id");
		type = it.getIntExtra("type", 0);
		goods_id = it.getStringExtra("goods_id");
		count = it.getIntExtra("count", 1);
		// cart_id = "94";
		setContentView(R.layout.d1_orderconfirm_activity);
		AsyncHttpClient request = HttpUtils.getClient();
		PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
		request.setCookieStore(myCookieStore);
		loadingPDialog = new ProgressDialog(this);
		loadingPDialog.setMessage("正在加载....");
		loadingPDialog.setCancelable(false);
		loadingPDialog.show();
		headView = LayoutInflater.from(getApplicationContext()).inflate(
				R.layout.d1_orderconfirm_listviewhead, null);
		SG_ship_method = (SegmentedGroup) headView
				.findViewById(R.id.SG_ship_method);
		SG_ship_method.setTintColor(0xFFFAA800);
		SG_ship_method.setOnCheckedChangeListener(this);
		SG_pay_method = (SegmentedGroup) headView
				.findViewById(R.id.SG_pay_method);
		SG_pay_method.setTintColor(0xFFFAA800);
		SG_pay_method.setOnCheckedChangeListener(this);
		listview = (MyListView) findViewById(R.id.listview);
		listview.addHeaderView(headView);
		listview.setPullLoadEnable(false);
		listview.setPullRefreshEnable(true);
		listview.setXListViewListener(this, 0);
		listview.setRefreshTime();
		adapter = new D1_OrderConfirmAdapter(getApplicationContext(),
				cart_list_data);
		listview.setAdapter(adapter);
		tv_all_price = (TextView) findViewById(R.id.tv_all_price);
		if (type != 200) {
			HttpUtils.getCartList(res_getCar, cart_id);
			HttpUtils.getOrderConfirm(res, cart_id);

		} else {
			HttpUtils.getGoodsDetail(goods_id, getGoodsDetail);
			HttpUtils.BuyNow(res_buynow, goods_id, count);
		}
		btn_sub = (Button) findViewById(R.id.btn_sub);
		btn_sub.setOnClickListener(this);
		tv_name = (TextView) headView.findViewById(R.id.tv_name);
		tv_address = (TextView) headView.findViewById(R.id.tv_address);
		tv_phone = (TextView) headView.findViewById(R.id.tv_phone);
		tv_postage = (TextView) findViewById(R.id.tv_postage);
		iv_back_btn = (ImageView) findViewById(R.id.iv_back_btn);
		iv_back_btn.setOnClickListener(this);
		ll_chose_address = (LinearLayout) headView
				.findViewById(R.id.ll_chose_address);
		ll_chose_address.setOnClickListener(this);
		rb_dispatching = (RadioButton) headView
				.findViewById(R.id.rb_dispatching);
		rb_pickup = (RadioButton) headView.findViewById(R.id.rb_pickup);
		rb_online = (RadioButton) headView.findViewById(R.id.rb_online);
		rb_offline = (RadioButton) headView.findViewById(R.id.rb_offline);
		tv_chose = (TextView) headView.findViewById(R.id.tv_chose);
		ll_price = (LinearLayout) findViewById(R.id.ll_price);
		ll_price.setVisibility(View.GONE);
		ll_noaddress = (LinearLayout) findViewById(R.id.ll_noaddress);
		HttpUtils.getDiscountSetting(BeeFrameworkApp.res);

	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int checkedId) {
		switch (checkedId) {
		case R.id.rb_dispatching:
			ship_method = "delivery";
			rb_dispatching.setBackgroundResource(R.drawable.ic_dispatchinged);
			rb_pickup.setBackgroundResource(R.drawable.ic_pickup);
			postage();
			return;
		case R.id.rb_pickup:
			ship_method = "self_take";
			rb_dispatching.setBackgroundResource(R.drawable.ic_dispatching);
			rb_pickup.setBackgroundResource(R.drawable.ic_pickuped);
			postage();
			return;
		case R.id.rb_online:
			pay_method = "online";
			rb_online.setBackgroundResource(R.drawable.ic_onlineed);
			rb_offline.setBackgroundResource(R.drawable.ic_offline);
			postage();
			return;
		case R.id.rb_offline:
			rb_online.setBackgroundResource(R.drawable.ic_online);
			rb_offline.setBackgroundResource(R.drawable.ic_offlineed);
			pay_method = "offline";
			postage();
			return;

		}
	}

	private void postage() {
		{
			String str_postage = "";
			if (all_price >= 29) {
				d_postage = ship_method == "delivery" ? 0 : 0;
				d_postage = pay_method == "online" ? (-2) : 0;
				str_postage = "(邮费:￥" + (d_postage >= 0 ? 0 : 0) + ")";
				d_postage = 0;

			} else {
				d_postage = ship_method == "delivery" ? 5 : 0;
				d_postage = pay_method == "online" ? (d_postage - 2)
						: d_postage;
				str_postage = "邮费:￥" + (d_postage >= 0 ? 5 : 0);
				d_postage = 5;
			}
			if (pay_method != "offline") {
				if (all_price >= BaseData.min_total_price) {
					tv_all_price.setText(MathTools.DoubleKeepTwo((all_price
							+ d_postage - BaseData.online_pay_discount))
							+ "");
				} else {
					tv_all_price.setText(MathTools
							.DoubleKeepTwo((all_price + d_postage)) + "");
				}
			} else {
				tv_all_price.setText(MathTools
						.DoubleKeepTwo((all_price + d_postage)) + "");
			}
			if (pay_method == "online") {
				str_postage += ",满" + BaseData.min_total_price + "元立减"
						+ BaseData.online_pay_discount + "元";
			}
			tv_postage.setText(str_postage);
		}

	}

	JsonHttpResponseHandler res = new JsonHttpResponseHandler() {

		@Override
		public void onSuccess(int statusCode, Header[] headers,
				JSONObject response) {
			super.onSuccess(statusCode, headers, response);
			int result = 0;
			String message = "";

			try {
				result = Integer.valueOf(response.getString("result"));
				message = response.getString("message");
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (result == 1 && statusCode == 200) {
				try {
					all_price = 0;
					cart_list_data.clear();
					JSONArray array = response.getJSONArray("list");
					// 包含用户信息
					JSONObject receiver_json = response
							.getJSONObject("receiver");
					address_id = receiver_json.getString("address_id");

					tv_name.setText("收货人:"
							+ receiver_json.getString("true_name"));
					tv_address.setText("收货地址:"
							+ receiver_json.getString("area_info")
							+ receiver_json.getString("address"));
					tv_phone.setText(receiver_json.getString("mob_phone"));

					for (int i = 0; i < array.length(); i++) {
						JSONObject jsonItem = array.getJSONObject(i);
						Map<String, String> map = new HashMap();
						map.put("store_id", jsonItem.getString("store_id"));
						map.put("store_name", jsonItem.getString("store_name"));
						String cart_list = jsonItem.getString("cart_list");
						purchase_price = jsonItem.getString("purchase_price");
						map.put("purchase_price", purchase_price);
						all_price += Double.parseDouble(purchase_price);
						JSONArray car_array = new JSONArray(cart_list);
						for (int j = 0; j < car_array.length(); j++) {
							JSONObject json = car_array.getJSONObject(j);
							Map<String, String> carmap = new HashMap();
							carmap.put("goods_id", json.getString("goods_id"));
							carmap.put("goods_name",
									json.getString("goods_name"));
							carmap.put("goods_price",
									json.getString("goods_price"));
							carmap.put("goods_num", json.getString("goods_num"));
							carmap.put(
									"goods_image",
									LandousAppConst.HOME_IMG_URL
											+ json.getString("store_id") + "/"
											+ json.getString("goods_image"));
							carmap.put("cart_id", json.getString("cart_id"));
							carmap.put("store_name",
									json.getString("store_name"));
							if (j == 0) {
								carmap.put("position", "head");
							} else if (j == car_array.length() - 1) {
								carmap.put("position", "tail");
							} else {
								carmap.put("position", "body");
							}
							carmap.put("purchase_price",
									jsonItem.getString("purchase_price"));
							cart_list_data.add(carmap);

						}
					}
					adapter.notifyDataSetChanged();
					postage();
					tv_chose.setVisibility(View.GONE);
					ll_noaddress.setVisibility(View.VISIBLE);
					ll_price.setVisibility(View.VISIBLE);
				} catch (JSONException e) {
					ll_noaddress.setVisibility(View.GONE);
					tv_chose.setVisibility(View.VISIBLE);
					ll_price.setVisibility(View.GONE);
					e.printStackTrace();
				}
			} else if (message.equals("No permission to do this!")) {
				// Intent it = new Intent(getActivity(),
				// E6_SigninActivity.class);
				// getActivity().overridePendingTransition(R.anim.push_up_in,
				// R.anim.push_up_out);
				// startActivity(it);
			}

			Log.i("landousapp", response.toString());
			loadingPDialog.dismiss();
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
	JsonHttpResponseHandler res_buynow = new JsonHttpResponseHandler() {

		@Override
		public void onSuccess(int statusCode, Header[] headers,
				JSONObject response) {
			super.onSuccess(statusCode, headers, response);
			int result = 0;
			String message = "";
			Log.i("landousapp", response.toString());
			try {
				result = Integer.valueOf(response.getString("result"));
				message = response.getString("message");
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (result == 1 && statusCode == 200) {
				try {
					all_price = 0;
					cart_list_data.clear();
					JSONArray array = response.getJSONArray("list");
					// 包含用户信息
					JSONObject receiver_json = response
							.getJSONObject("receiver");
					address_id = receiver_json.getString("address_id");
					tv_name.setText("收货人:"
							+ receiver_json.getString("true_name"));
					tv_address.setText("收货地址:"
							+ receiver_json.getString("area_info")
							+ receiver_json.getString("address"));
					tv_phone.setText(receiver_json.getString("mob_phone"));

					for (int i = 0; i < array.length(); i++) {
						JSONObject jsonItem = array.getJSONObject(i);
						Map<String, String> map = new HashMap();
						map.put("store_id", jsonItem.getString("store_id"));
						map.put("store_name", jsonItem.getString("store_name"));
						String cart_list = jsonItem.getString("cart_list");
						purchase_price = jsonItem.getString("purchase_price");
						map.put("purchase_price", purchase_price);
						d_postage = jsonItem.getDouble("store_free_price");
						Log.i("landousjson", d_postage + "");
						all_price += Double.parseDouble(purchase_price);
						JSONArray car_array = new JSONArray(cart_list);
						for (int j = 0; j < car_array.length(); j++) {
							Log.i("landousjson", "374");
							JSONObject json = car_array.getJSONObject(j);
							Map<String, String> carmap = new HashMap();
							carmap.put("goods_id", json.getString("goods_id"));
							carmap.put("goods_name",
									json.getString("goods_name"));
							carmap.put("goods_price",
									json.getString("goods_price"));
							carmap.put("goods_num", json.getString("goods_num"));
							carmap.put(
									"goods_image",
									LandousAppConst.HOME_IMG_URL
											+ json.getString("store_id") + "/"
											+ json.getString("goods_image"));
							// carmap.put("cart_id", json.getString("cart_id"));
							carmap.put("store_name",
									jsonItem.getString("store_name"));
							Log.i("landousjson", "392");
							// if (j == 0) {
							// carmap.put("position", "head");
							// } else if (j == car_array.length() - 1) {
							carmap.put("position", "tail");

							// carmap.put("position", "body");

							carmap.put("purchase_price",
									jsonItem.getString("purchase_price"));
							cart_list_data.add(carmap);

						}
					}
					adapter.notifyDataSetChanged();
					postage();
					tv_chose.setVisibility(View.GONE);
					ll_noaddress.setVisibility(View.VISIBLE);
					ll_price.setVisibility(View.VISIBLE);
				} catch (JSONException e) {
					ll_noaddress.setVisibility(View.GONE);
					tv_chose.setVisibility(View.VISIBLE);
					ll_price.setVisibility(View.GONE);
					e.printStackTrace();
				}
			} else if (message.equals("No permission to do this!")) {
				// Intent it = new Intent(getActivity(),
				// E6_SigninActivity.class);
				// getActivity().overridePendingTransition(R.anim.push_up_in,
				// R.anim.push_up_out);
				// startActivity(it);
			}

			loadingPDialog.dismiss();
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_sub:
			// if (pay_method == "online") {
			//
		
			if (address_id != "") {

				loadingPDialog.show();
				if (type == 200) {
					HttpUtils.addOrderNow(res_addOrder, ship_method,
							pay_method, address_id, goods_id, count + "");
				} else {
					HttpUtils.addOrder(res_addOrder, ship_method, pay_method,
							address_id, cart_id);
				}
			} else {
				Toast.makeText(D1_OrderConfirmActivity.this, "请先选择收货地址",
						Toast.LENGTH_LONG).show();
			}
			// Fiap fiap = new Fiap(D1_OrderConfirmActivity.this);
			// // 调用支付方法，并传入支付金额
			// Date now = new Date();
			// SimpleDateFormat dateFormat = new
			// SimpleDateFormat("yyyyMMddHHmmss");
			// String order = dateFormat.format(now);
			// fiap.setOrder_id(order);
			// fiap.android_pay(all_price);
			// Toast.makeText(
			// getApplicationContext(),
			// ship_method + "," + pay_method + "," + address_id + ","
			// + cart_id, Toast.LENGTH_SHORT).show();
			// HttpUtils.addOrder(res_addOrder, ship_method, pay_method,
			// address_id, cart_id);
			break;
		case R.id.iv_back_btn:
			this.finish();
			break;
		case R.id.ll_chose_address:
			it = new Intent(getApplicationContext(),
					E2_AddressManageActivity.class);
			startActivityForResult(it, ADDRESS_CODE);
			overridePendingTransition(R.anim.push_right_in,
					R.anim.push_right_out);
			break;
		default:
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Log.i("requestCode", requestCode + "");
		if (ADDRESS_CODE == requestCode) {
			loadingPDialog.show();
			if (type != 200) {
				HttpUtils.getCartList(res_getCar, cart_id);
				HttpUtils.getOrderConfirm(res, cart_id);

			} else {
				HttpUtils.getGoodsDetail(goods_id, getGoodsDetail);
				HttpUtils.BuyNow(res_buynow, goods_id, count);
			}

			ll_price.setVisibility(View.GONE);
			ll_noaddress.setVisibility(View.GONE);
			tv_chose.setVisibility(View.VISIBLE);
			all_price = 0;
			cart_list_data.clear();
			adapter.notifyDataSetChanged();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	JsonHttpResponseHandler res_addOrder = new JsonHttpResponseHandler() {

		@Override
		public void onSuccess(int statusCode, Header[] headers,
				JSONObject response) {
			super.onSuccess(statusCode, headers, response);

			int result = 0;
			Log.i("landousjson", response.toString());
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
					JSONArray array = response.getJSONObject("data")
							.getJSONArray("order_list");
					String pay_sn = "";
					double goods_amount = 0.00;
					double shipping_fee = 0.00;
					for (int i = 0; i < array.length(); i++) {
						JSONObject jsonItem = array.getJSONObject(i);
						goods_amount += jsonItem.getDouble("goods_amount");
						shipping_fee += jsonItem.getDouble("shipping_fee");
						Log.i("landousjson", jsonItem.getString("goods_amount"));
						pay_sn = jsonItem.getString("pay_sn");
					}
					if (pay_method.equals("online")) {
						Fiap fiap = new Fiap(D1_OrderConfirmActivity.this);
						fiap.setOrder_id(pay_sn);
						double money = goods_amount + shipping_fee;
						double actually_money = money > BaseData.min_total_price ? money
								- BaseData.online_pay_discount
								: money;
						fiap.android_pay(actually_money);
						HttpUtils.updateMobileDiscount(res_update, pay_sn,
								pay_method, BaseData.online_pay_discount + "");
					} else if (pay_method.equals("offline")) {
						new AlertDialog.Builder(D1_OrderConfirmActivity.this)
								.setTitle("提示")
								.setMessage("提交订单成功")
								.setPositiveButton("确定",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												D1_OrderConfirmActivity.this
														.finish();
											}
										}).create().show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				try {
					String message = response.getString("message");
					new AlertDialog.Builder(D1_OrderConfirmActivity.this)
							.setTitle("提示")
							.setMessage(message)
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											D1_OrderConfirmActivity.this
													.finish();
										}
									}).create().show();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			loadingPDialog.dismiss();
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

	JsonHttpResponseHandler res_update = new JsonHttpResponseHandler() {
		public void onSuccess(int statusCode, org.apache.http.Header[] headers,
				org.json.JSONObject response) {
			super.onSuccess(statusCode, headers, response);
			int result = 0;
			String msg = "";
			Log.i("landousjson", response + "");
			try {
				result = Integer.valueOf(response.getString("result"));
				msg = response.getString("message").toString();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (result == 1 && statusCode == 200) {

			} else {
				Toast.makeText(D1_OrderConfirmActivity.this, msg,
						Toast.LENGTH_LONG).show();
				;
			}

		};

		public void onFailure(int statusCode, org.apache.http.Header[] headers,
				Throwable throwable, org.json.JSONObject errorResponse) {
			super.onFailure(statusCode, headers, throwable, errorResponse);
		};
	};

	@Override
	public void onRefresh(int id) {
		// TODO Auto-generated method stub
		loadingPDialog.show();
		if (type != 200) {
			HttpUtils.getCartList(res_getCar, cart_id);
			HttpUtils.getOrderConfirm(res, cart_id);

		} else {
			HttpUtils.getGoodsDetail(goods_id, getGoodsDetail);
			HttpUtils.BuyNow(res_buynow, goods_id, count);
		}
	}

	@Override
	public void onLoadMore(int id) {
		// TODO Auto-generated method stub

	}

	/**
	 * 获取商品详情
	 */
	JsonHttpResponseHandler getGoodsDetail = new JsonHttpResponseHandler() {

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
				all_price = 0;
				cart_list_data.clear();
				try {
					JSONObject json = response.getJSONObject("data");
					Log.i("json", "json" + json);
					Map<String, String> map = new HashMap<String, String>();
					map.put("goods_id", json.getString("goods_id"));
					map.put("goods_name", json.getString("goods_name"));
					map.put("goods_price", json.getString("goods_price"));
					map.put("goods_num", count + "");
					map.put("goods_image",
							LandousAppConst.HOME_IMG_URL
									+ json.getString("store_id") + "/"
									+ json.getString("goods_image"));
					map.put("cart_id", "");
					map.put("store_name", json.getString("store_name"));

					map.put("position", "one");
					cart_list_data.add(map);
					adapter.notifyDataSetChanged();
					ll_price.setVisibility(View.VISIBLE);
					double goods_price = json.getDouble("goods_price");
					tv_all_price.setText(MathTools
							.DoubleKeepTwo((goods_price * count)) + "");
					all_price = goods_price * count;
					Log.i("json", "1111");
					postage();

				} catch (JSONException e) {

					e.printStackTrace();
				}
				loadingPDialog.dismiss();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers,
				Throwable throwable, JSONObject errorResponse) {
			// TODO Auto-generated method stub
			loadingPDialog.dismiss();

			super.onFailure(statusCode, headers, throwable, errorResponse);
		}
	};
	JsonHttpResponseHandler res_getCar = new JsonHttpResponseHandler() {

		@Override
		public void onSuccess(int statusCode, Header[] headers,
				JSONObject response) {
			super.onSuccess(statusCode, headers, response);
			int result = 0;
			String message = "";

			try {
				result = Integer.valueOf(response.getString("result"));
				message = response.getString("message");
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (result == 1 && statusCode == 200) {
				cart_list_data.clear();
				try {
					JSONArray array = response.getJSONArray("list");
					double purchase_price = 0;
					all_price = 0;
					for (int i = 0; i < array.length(); i++) {
						JSONObject jsonItem = array.getJSONObject(i);
						Map<String, String> map = new HashMap();
						map.put("store_id", jsonItem.getString("store_id"));
						map.put("store_name", jsonItem.getString("store_name"));
						String cart_list = jsonItem.getString("cart_list");
						map.put("purchase_price",
								jsonItem.getString("purchase_price"));
						purchase_price += Double.parseDouble(jsonItem
								.getString("purchase_price"));
						JSONArray car_array = new JSONArray(cart_list);
						int goods_num_shop = 0;

						for (int j = 0; j < car_array.length(); j++) {
							JSONObject json = car_array.getJSONObject(j);
							Map<String, String> carmap = new HashMap();
							carmap.put("goods_id", json.getString("goods_id"));
							carmap.put("goods_name",
									json.getString("goods_name"));
							carmap.put("goods_price",
									json.getString("goods_price"));
							carmap.put("goods_num", json.getString("goods_num"));
							goods_num_shop += json.getInt("goods_num");
							all_price += json.getDouble("goods_price")
									* json.getInt("goods_num");
							carmap.put(
									"goods_image",
									LandousAppConst.HOME_IMG_URL
											+ json.getString("store_id") + "/"
											+ json.getString("goods_image"));
							carmap.put("cart_id", json.getString("cart_id"));
							carmap.put("store_name",
									json.getString("store_name"));
							if (car_array.length() == 1) {
								carmap.put("position", "one");
							} else {
								if (j == 0) {
									carmap.put("position", "head");
								} else if (j == car_array.length() - 1) {
									carmap.put("position", "tail");
								} else {
									carmap.put("position", "body");
								}
							}
							carmap.put("price_shop",
									jsonItem.getString("purchase_price"));
							carmap.put("purchase_price", purchase_price + "");
							carmap.put("goods_num_shop", goods_num_shop + "");
							cart_list_data.add(carmap);
						}

					}
					ll_price.setVisibility(View.VISIBLE);
					postage();
					adapter.notifyDataSetChanged();
					ll_price.setVisibility(View.VISIBLE);
				} catch (JSONException e) {
					// TODO Auto-generated catch block

					e.printStackTrace();
				}
				// notifyDataSetChanged();
				loadingPDialog.dismiss();
			} else if (message.equals("No permission to do this!")) {
				// Intent it = new Intent(context, E6_SigninActivity.class);
				// ((Activity) context).overridePendingTransition(
				// R.anim.push_up_in, R.anim.push_up_out);
				// context.startActivity(it);
			}

			// Intent it = new Intent(context, MyDialogActivity.class);
			// context.startActivity(it);
		}

		@Override
		public void onFailure(int statusCode, Header[] headers,
				Throwable throwable, JSONObject errorResponse) {
			// TODO Auto-generated method stub
			loadingPDialog.dismiss();
			super.onFailure(statusCode, headers, throwable, errorResponse);
		}
	};
}