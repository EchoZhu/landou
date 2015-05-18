package com.zykj.landous.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.external.maxwin.view.XListView.IXListViewListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.zykj.landous.LandousAppConst;
import com.zykj.landous.R;
import com.zykj.landous.Tools.HttpUtils;
import com.zykj.landous.Tools.MathTools;
import com.zykj.landous.activity.D1_OrderConfirmActivity;
import com.zykj.landous.activity.E6_SigninActivity;
import com.zykj.landous.adapter.D0_New_Shoping_Car_adapter;
import com.zykj.landous.adapter.D0_New_Shoping_Car_adapter.onCheckedChanged;
import com.zykj.landous.adapter.D0_shoping_car_adapter;
import com.zykj.landous.view.MyListView;

public class D0_ShopingCartFragment extends Fragment implements
		IXListViewListener, OnClickListener, onCheckedChanged {
	public static FrameLayout shop_car_null;
	public static FrameLayout shop_car_data;
	private View view;
	private MyListView listview;
	ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
	ArrayList<Map<String, String>> cart_list_data = new ArrayList<Map<String, String>>();
	private ProgressDialog loadingPDialog = null;
	D0_New_Shoping_Car_adapter adapter;
	private SharedPreferences shared;
	SharedPreferences.Editor editor = null;
	public static TextView tv_cart_Allprice;
	public static double all_price = 0;
	private TextView tv_cart_buy;
	ArrayList<String> car_List = new ArrayList<String>();
	public static String cart_id = "";

	/**
	 * 全选
	 */
	public static CheckBox cb_cart_all;
	private RelativeLayout rl_cart_all;
	/**
	 * 编辑按钮
	 */
	public static Button btn_del;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}

	@Override
	public void onResume() {

		loadingPDialog.show();
		HttpUtils.getCartList(res, "");
		editor.clear();
		editor.commit();
		// if(cb_cart_all.isChecked()){
		// cb_cart_all.setChecked(false);
		// }

		super.onResume();
		shared = getActivity().getSharedPreferences("loginInfo",
				Activity.MODE_PRIVATE);
		String userID = shared.getString("member_id", "");

		if (!userID.equals("")) {
			loadingPDialog.show();
			HttpUtils.getCartList(res, "");
			editor.clear();
			editor.commit();
			// if (cb_cart_all.isChecked()) {
			// cb_cart_all.setChecked(false);
			// }
		}
	}

	public Boolean isLogin() {
		shared = getActivity().getSharedPreferences("loginInfo",
				Activity.MODE_PRIVATE);
		String userID = shared.getString("member_id", "");
		Log.i("login-user-id", userID);
		if (userID.equals("")) {
			Intent it = new Intent(getActivity(), E6_SigninActivity.class);
			startActivity(it);
			return false;
		}
		return true;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		loadingPDialog = new ProgressDialog(getActivity());
		loadingPDialog.setMessage("正在加载....");
		loadingPDialog.setCancelable(false);
		shared = getActivity().getSharedPreferences("carInfo", 0);
		editor = shared.edit();
		// loadingPDialog.show();
		view = inflater.inflate(R.layout.d0_shoping_cart_fragment, null);
		shop_car_null = (FrameLayout) view.findViewById(R.id.shop_car_null);
		rl_cart_all = (RelativeLayout) view.findViewById(R.id.rl_cart_all);
		shop_car_data = (FrameLayout) view.findViewById(R.id.shop_car_data);
		shop_car_null.setVisibility(View.GONE);
		shop_car_data.setVisibility(View.GONE);
		listview = (MyListView) view.findViewById(R.id.listview);
		listview.setPullLoadEnable(false);
		listview.setPullRefreshEnable(true);
		listview.setXListViewListener(this, 0);
		listview.setRefreshTime();
		adapter = new D0_New_Shoping_Car_adapter(getActivity(), cart_list_data);
		adapter.setOnCheckedChanged(this);
		listview.setAdapter(adapter);
		tv_cart_Allprice = (TextView) view.findViewById(R.id.tv_cart_Allprice);
		tv_cart_buy = (TextView) view.findViewById(R.id.tv_cart_buy);
		tv_cart_buy.setOnClickListener(this);

		car_List.clear();
		btn_del = (Button) view.findViewById(R.id.btn_del);
		btn_del.setOnClickListener(this);
		cb_cart_all = (CheckBox) view.findViewById(R.id.cb_cart_all);
		cb_cart_all.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					car_List.clear();
					double all_price_ = 0;
					for (int i = 0; i < D0_New_Shoping_Car_adapter.dataList
							.size(); i++) {
						D0_New_Shoping_Car_adapter.b_goods[i] = isChecked;
						cart_id += D0_New_Shoping_Car_adapter.dataList.get(i)
								.get("cart_id") + ",";
						car_List.add(D0_New_Shoping_Car_adapter.dataList.get(i)
								.get("cart_id").toString());
					}
					if (D0_New_Shoping_Car_adapter.dataList.size() != 0) {
						all_price_ = Double
								.valueOf(D0_New_Shoping_Car_adapter.dataList
										.get(D0_New_Shoping_Car_adapter.dataList
												.size() - 1).get(
												"purchase_price")
										+ "");
						tv_cart_Allprice.setText("总金额:￥"
								+ MathTools.DoubleKeepTwo(all_price_));
					}
					adapter.notifyDataSetChanged();
				} else {

				}

			}
		});
		rl_cart_all.setOnClickListener(this);
		if (isLogin()) {
			loadingPDialog.show();
			HttpUtils.getCartList(res, "");
			editor.clear();
			editor.commit();
			if (cb_cart_all.isChecked()) {
				cb_cart_all.setChecked(false);
			}
		}
		return view;

	}

	public List<View> getAllChildViews() {

		View view = getActivity().getWindow().getDecorView();
		return getAllChildViews(view);
	}

	private List<View> getAllChildViews(View view) {
		List<View> allchildren = new ArrayList<View>();
		if (view instanceof ViewGroup) {
			ViewGroup vp = (ViewGroup) view;
			for (int i = 0; i < vp.getChildCount(); i++) {
				View viewchild = vp.getChildAt(i);
				allchildren.add(viewchild);
				allchildren.addAll(getAllChildViews(viewchild));
			}
		}
		return allchildren;
	}

	@Override
	public void onRefresh(int id) {
		// TODO Auto-generated method stub
		loadingPDialog.show();
		HttpUtils.getCartList(res, "");
	}

	@Override
	public void onLoadMore(int id) {
		// TODO Auto-generated method stub

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
					data.clear();
					cart_list_data.clear();
					double purchase_price = 0;
					JSONArray array = response.getJSONArray("list");
					for (int i = 0; i < array.length(); i++) {
						JSONObject jsonItem = array.getJSONObject(i);
						Map<String, String> map = new HashMap();
						map.put("store_id", jsonItem.getString("store_id"));
						map.put("store_name", jsonItem.getString("store_name"));
						String cart_list = jsonItem.getString("cart_list");
						purchase_price += Double.parseDouble(jsonItem
								.getString("purchase_price"));
						map.put("purchase_price",
								jsonItem.getString("purchase_price"));
						JSONArray car_array = new JSONArray(cart_list);
						int goods_num_shop = 0;
						for (int j = 0; j < car_array.length(); j++) {
							JSONObject json = car_array.getJSONObject(j);
							Map<String, String> carmap = new HashMap();
							carmap.put("goods_id", json.getString("goods_id"));
							carmap.put("goods_name",
									json.getString("goods_name"));
							carmap.put("goods_price",
									json.getString("goods_price"));// 可能包含限时折扣
							carmap.put("goods_num", json.getString("goods_num"));
							goods_num_shop += json.getInt("goods_num");
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
							carmap.put("goods_num_shop", goods_num_shop + "");
							carmap.put("purchase_price", purchase_price + "");
							carmap.put("price_shop",
									jsonItem.getString("purchase_price"));
							cart_list_data.add(carmap);
						}
						data.add(map);
					}
					shop_car_null.setVisibility(View.GONE);
					shop_car_data.setVisibility(View.VISIBLE);
					adapter.notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					shop_car_null.setVisibility(View.VISIBLE);
					e.printStackTrace();
				}
			} else if (message.equals("No permission to do this!")) {
				// Intent it = new Intent(getActivity(),
				// E6_SigninActivity.class);
				// getActivity().overridePendingTransition(R.anim.push_up_in,
				// R.anim.push_up_out);
				// startActivity(it);
				shop_car_null.setVisibility(View.VISIBLE);
				shop_car_data.setVisibility(View.GONE);
			}

			loadingPDialog.dismiss();
		}

		@Override
		public void onFailure(int statusCode, Header[] headers,
				Throwable throwable, JSONObject errorResponse) {
			// TODO Auto-generated method stub
			loadingPDialog.dismiss();
			// Toast.makeText(getActivity(), "网络连接超时",
			// Toast.LENGTH_LONG).show();
			super.onFailure(statusCode, headers, throwable, errorResponse);
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_del:
			all_price = 0;
			tv_cart_Allprice.setText("总金额:￥0.00");
			cart_id = "";
			cb_cart_all.setChecked(false);
			int tag = (Integer) (v.getTag() == null ? 0 : v.getTag());
			if (tag == 0) {
				btn_del.setText("完成");
			} else {
				btn_del.setText("编辑");
			}
			for (int i = 0; i < D0_New_Shoping_Car_adapter.dataList.size(); i++) {
				D0_New_Shoping_Car_adapter.b_del[i] = !D0_New_Shoping_Car_adapter.b_del[i];
				D0_New_Shoping_Car_adapter.b_goods[i] = false;
			}
			adapter.notifyDataSetChanged();
			tag = (++tag) % 2;
			v.setTag(tag);
			break;
		case R.id.tv_cart_buy://点击“结算”
			car_buy();

			break;
		case R.id.rl_cart_all:
			for (int i = 0; i < D0_New_Shoping_Car_adapter.dataList.size(); i++) {
				D0_New_Shoping_Car_adapter.b_del[i] = true;
			}
			D0_ShopingCartFragment.btn_del.setText("编辑");
			D0_ShopingCartFragment.btn_del.setTag(0);
			car_List.clear();
			boolean b = cb_cart_all.isChecked();
			cart_list_data = D0_New_Shoping_Car_adapter.dataList;
			for (int i = 0; i < D0_New_Shoping_Car_adapter.dataList.size(); i++) {
				D0_New_Shoping_Car_adapter.b_goods[i] = !b;
				cart_id += D0_New_Shoping_Car_adapter.dataList.get(i).get(
						"cart_id")
						+ ",";
				car_List.add(D0_New_Shoping_Car_adapter.dataList.get(i)
						.get("cart_id").toString());
			}
			adapter.notifyDataSetChanged();
			if (!b) {
				all_price = Double.valueOf(D0_New_Shoping_Car_adapter.dataList
						.get(D0_New_Shoping_Car_adapter.dataList.size() - 1)
						.get("purchase_price")
						+ "");
			} else {
				car_List.clear();
				all_price = 0;
				cart_id = "";
			}
			tv_cart_Allprice.setText("总金额:￥"
					+ MathTools.DoubleKeepTwo(all_price));

			cb_cart_all.setChecked(!b);

			break;
		default:
			break;
		}
	}

	private void car_buy() {
		if (cart_id.length() < 1) {
			Toast.makeText(getActivity(), "您没有选择任何商品", Toast.LENGTH_LONG)
					.show();
		} else {
			Intent it = new Intent(getActivity(), D1_OrderConfirmActivity.class);
			it.putExtra("cart_id", cart_id);
			startActivity(it);

		}
	}

	@Override
	public void getChoiceData(double goods_price, boolean isChoice,
			String car_id) {
		int num = 0;
		for (int j = 0; j < D0_New_Shoping_Car_adapter.b_goods.length; j++) {
			if (D0_New_Shoping_Car_adapter.b_goods[j]) {
				num++;
			}
		}
		if (num == D0_New_Shoping_Car_adapter.dataList.size()) {
			cb_cart_all.setChecked(true);
		} else if (num < D0_New_Shoping_Car_adapter.dataList.size()) {
			cb_cart_all.setChecked(false);
		}
		if (isChoice) {
			all_price += goods_price;
			car_List.add(car_id);
		} else {
			all_price -= goods_price;
			car_List.remove(car_id);
		}
		if (car_List.size() > listview.getAdapter().getCount() - 1) {
			car_List.remove(car_List.size() - 1);
		}
		for (int i = 0; i < D0_New_Shoping_Car_adapter.dataList.size(); i++) {
			String price = D0_New_Shoping_Car_adapter.dataList.get(i)
					.get("goods_price").toString();
			String n = D0_New_Shoping_Car_adapter.dataList.get(i)
					.get("goods_num").toString();
			Log.i("landoutest", price + "price" + n + "num" + ""
					+ D0_New_Shoping_Car_adapter.b_goods[i]);
		}
		tv_cart_Allprice.setText("总金额:￥" + MathTools.DoubleKeepTwo(all_price));
		cart_id = "";
		for (int i = 0; i < car_List.size(); i++) {
			cart_id += car_List.get(i) + ",";
		}
	}

}
