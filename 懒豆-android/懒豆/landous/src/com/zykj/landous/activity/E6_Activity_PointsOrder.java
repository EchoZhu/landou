package com.zykj.landous.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.BeeFramework.BeeFrameworkApp;
import com.BeeFramework.activity.BaseActivity;
import com.external.activeandroid.util.Log;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zykj.landous.LandousAppConst;
import com.zykj.landous.R;
import com.zykj.landous.Tools.HttpUtils;
import com.zykj.landous.adapter.E5_CoinGoodsAdapter;
import com.zykj.landous.alipay.Fiap;

/**
 * @author 作者 zhang
 * @version 创建时间：2015年1月21日 下午8:40:24 类说明 提交积分订单
 */
public class E6_Activity_PointsOrder extends BaseActivity implements
		OnClickListener {
	private Intent it;
	int position = 0;
	int count = 1;
	String address_id = "";
	/**
	 * 没有地址信息
	 */
	private LinearLayout ll_noaddress;
	/**
	 * 选择收货地址
	 */
	private LinearLayout ll_chose_address;
	/**
	 * 礼品标题
	 */
	private TextView tv_title;
	/**
	 * 数量
	 */
	private TextView tv_num;
	/**
	 * 所需积分
	 */
	private TextView tv_price;
	/**
	 * 礼品的图片
	 */
	private ImageView iv_goods;
	/**
	 * 收货人姓名
	 */
	private TextView tv_name;
	/**
	 * 电话号码
	 */
	private TextView tv_phone;
	/**
	 * 收货地址
	 */
	private TextView tv_address;
	/**
	 * 提交订单
	 */
	private Button btn_sub;
	/**
	 * 礼品id
	 */
	private String pgoods_id;

	/**
	 * 选择收货地址
	 */
	private TextView tv_chose;
	private ImageView iv_back_btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.e6_activity_pointsorder);
		init();
	}

	private void init() {
		it = getIntent();
		position = it.getIntExtra("position", 0);
		count = it.getIntExtra("count", 1);
		ll_noaddress = (LinearLayout) findViewById(R.id.ll_noaddress);
		ll_chose_address = (LinearLayout) findViewById(R.id.ll_chose_address);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(E5_CoinGoodsAdapter.dataList.get(position)
				.get("pgoods_name").toString());
		pgoods_id = E5_CoinGoodsAdapter.dataList.get(position).get("pgoods_id");
		tv_num = (TextView) findViewById(R.id.tv_num);
		tv_num.setText("x" + count);
		tv_price = (TextView) findViewById(R.id.tv_price);
		tv_price.setText("积分"
				+ E5_CoinGoodsAdapter.dataList.get(position).get(
						"pgoods_points"));
		iv_goods = (ImageView) findViewById(R.id.iv_goods);
		String url = E5_CoinGoodsAdapter.dataList.get(position)
				.get("pgoods_image").toString();
		ImageLoader.getInstance().displayImage(url, iv_goods,
				BeeFrameworkApp.options_head);
		ll_chose_address = (LinearLayout) findViewById(R.id.ll_chose_address);
		ll_chose_address.setOnClickListener(this);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_phone = (TextView) findViewById(R.id.tv_phone);
		tv_address = (TextView) findViewById(R.id.tv_address);
		btn_sub = (Button) findViewById(R.id.btn_sub);
		btn_sub.setOnClickListener(this);
		tv_chose = (TextView) findViewById(R.id.tv_chose);
		if (address_id.length() == 0) {
			ll_noaddress.setVisibility(View.GONE);
			tv_chose.setVisibility(View.VISIBLE);
		} else {
			ll_noaddress.setVisibility(View.VISIBLE);
			tv_chose.setVisibility(View.GONE);
		}
		iv_back_btn = (ImageView) findViewById(R.id.iv_back_btn);
		iv_back_btn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_chose_address:
			it = new Intent(E6_Activity_PointsOrder.this,
					E2_AddressManageActivity.class);
			it.putExtra("come", "E6_Activity_getPointsOrder");
			startActivityForResult(it, 200);
			overridePendingTransition(R.anim.push_right_in,
					R.anim.push_right_out);
			break;
		case R.id.btn_sub:
			if (pgoods_id.length() != 0 & count != 0 & address_id.length() != 0) {

				HttpUtils.addPointsOrder(res, pgoods_id, count + "",
						"delivery", address_id);
			}
			break;
		case R.id.iv_back_btn:
			this.finish();
			Intent intent = new Intent(E6_Activity_PointsOrder.this,
					E5_CoinStoreActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}

	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		BeeFrameworkApp.getInstance().showBug(this);
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
			Intent intent = new Intent(E6_Activity_PointsOrder.this,
					E5_CoinStoreActivity.class);
			startActivity(intent);
		}
		return true;
	}
	JsonHttpResponseHandler res = new JsonHttpResponseHandler() {
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
				try {
					JSONObject json = response.getJSONObject("data");
					String point_orderid = json.getString("point_orderid");
					Fiap f = new Fiap(E6_Activity_PointsOrder.this);
					f.setOrder_id(json.getString("point_ordersn"));
					f.setPoint_orderid(point_orderid);
					f.android_pay(5.0);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				Toast.makeText(E6_Activity_PointsOrder.this, msg,
						Toast.LENGTH_LONG).show();
			}

		};

		public void onFailure(int statusCode, org.apache.http.Header[] headers,
				Throwable throwable, org.json.JSONObject errorResponse) {
			super.onFailure(statusCode, headers, throwable, errorResponse);
		};
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Log.i("requestCode", requestCode + "");
		if (200 == requestCode && data != null) {
			ll_noaddress.setVisibility(View.VISIBLE);
			tv_chose.setVisibility(View.GONE);
			ll_chose_address.setBackgroundResource(R.drawable.bg_address);
			tv_name.setText("收货人：" + data.getStringExtra("true_name"));
			tv_phone.setText("" + data.getStringExtra("mob_phone"));
			tv_address.setText("" + data.getStringExtra("address"));
			address_id = data.getStringExtra("address_id");
		}
		if (data == null) {
			ll_noaddress.setVisibility(View.GONE);
			tv_chose.setVisibility(View.VISIBLE);
		} else {
			ll_noaddress.setVisibility(View.VISIBLE);
			tv_chose.setVisibility(View.GONE);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
