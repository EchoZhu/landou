package com.zykj.landous.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.BeeFramework.activity.BaseActivity;
import com.external.maxwin.view.XListView.IXListViewListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.zykj.landous.LandousAppConst;
import com.zykj.landous.R;
import com.zykj.landous.Tools.HttpUtils;
import com.zykj.landous.adapter.E2_Adapter_orderEvaluation;
import com.zykj.landous.view.MyListView;

/**
 * @author 作者 zhang
 * @version 创建时间：2015年1月25日 下午4:05:45 类说明 订单评价
 */
public class E2_Activity_orderEvaluation extends BaseActivity implements
		OnClickListener, IXListViewListener {
	private Intent it;
	String order_id = "";
	private ImageView iv_back;
	private MyListView listview;
	private E2_Adapter_orderEvaluation adapter;
	Map<Integer, String> mMapContent;
	ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
	/**
	 * 提交评论
	 */
	private Button btn_sub;
	// 评价订单用的**
	String goods_id[];
	public static Float fStart[];
	public static String str_comment[];

	// 评价订单用的三个参数
	/**
	 * 0默认，0不匿名1匿名
	 */
	String anony = "0";

	/**
	 * 匿名或不匿名选择
	 */
	CheckBox check_anony;
	private LinearLayout ll_pingjia;

	/**
	 * 标题名字
	 */
	private TextView tv_name_title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.e2_activity_orderevaluation);
        mMapContent = new HashMap<Integer, String>();        
		init();
	}

	private void init() {
		tv_name_title = (TextView) findViewById(R.id.tv_name_title);
		ll_pingjia = (LinearLayout) findViewById(R.id.ll_pingjia);
		it = getIntent();
		order_id = it.getStringExtra("order_id");
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		adapter = new E2_Adapter_orderEvaluation(
				E2_Activity_orderEvaluation.this, data);
		ll_pingjia.setVisibility(View.VISIBLE);

		listview = (MyListView) findViewById(R.id.listview);
		listview.setPullLoadEnable(false);
		listview.setPullRefreshEnable(true);
		listview.setXListViewListener(this, 0);
		listview.setRefreshTime();
		listview.setAdapter(adapter);
		btn_sub = (Button) findViewById(R.id.btn_sub);
		btn_sub.setOnClickListener(this);
		HttpUtils.getOrderDetail(res, order_id);
		check_anony = (CheckBox) findViewById(R.id.check_anony);
		check_anony.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					anony = "1";
				} else {
					anony = "0";
				}

			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			this.finish();

			break;
		case R.id.btn_sub:
			String other = "";
			for (int i = 0; i < goods_id.length; i++) {

				other += "goods[" + goods_id[i] + "][score]=" + fStart[i]
						+ "&goods[" + goods_id[i] + "][comment]="
						+ str_comment[i] + "&";
			}

			HttpUtils.orderEvaluation(res_orderE, order_id, anony, other);
			break;
		default:
			break;
		}

	}

	@Override
	public void onRefresh(int id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoadMore(int id) {
		// TODO Auto-generated method stub

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
					goods_id = new String[array.length()];
					fStart = new Float[array.length()];
					str_comment = new String[array.length()];
					for (int i = 0; i < array.length(); i++) {
						fStart[i] = 0f;// 初始化
						str_comment[i] = "";
						JSONObject jsonItem = array.getJSONObject(i);
						Map<String, String> map = new HashMap<String, String>();
						map.put("goods_name", jsonItem.getString("goods_name"));
						map.put("goods_price",
								"￥" + jsonItem.getString("goods_price"));
						map.put("goods_image", LandousAppConst.HOME_IMG_URL
								+ store_id + jsonItem.getString("goods_image"));
						goods_id[i] = jsonItem.getString("goods_id");
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
	JsonHttpResponseHandler res_orderE = new JsonHttpResponseHandler() {
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
				AlertDialog.Builder builder = new Builder(
						E2_Activity_orderEvaluation.this);
				builder.setMessage("提交评价成功");

				builder.setTitle("提示");

				builder.setNegativeButton("确认",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								finish();
								overridePendingTransition(R.anim.push_left_in,
										R.anim.push_left_out);
							}

						});
				builder.create().show();
			}

		};

		public void onFailure(int statusCode, org.apache.http.Header[] headers,
				Throwable throwable, org.json.JSONObject errorResponse) {
			super.onFailure(statusCode, headers, throwable, errorResponse);
		};
	};
}
