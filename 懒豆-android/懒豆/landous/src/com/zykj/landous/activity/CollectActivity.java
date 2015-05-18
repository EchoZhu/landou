package com.zykj.landous.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.origamilabs.library.views.StaggeredGridView;
import com.origamilabs.library.views.StaggeredGridView.OnItemClickListener;
import com.origamilabs.library.views.StaggeredGridView.OnItemLongClickListener;
import com.zykj.landous.LandousAppConst;
import com.zykj.landous.R;
import com.zykj.landous.Tools.HttpUtils;
import com.zykj.landous.adapter.E9_CollectGoodsAdapter;

public class CollectActivity extends FragmentActivity implements
		OnClickListener {
	private ImageView back;
	private StaggeredGridView gridView;
	private ListAdapter adapter;
	private E9_CollectGoodsAdapter collectAdapter;
	private LayoutInflater listContainer;
	private ArrayList<HashMap<String, String>> datalist = new ArrayList<HashMap<String, String>>();
	private ProgressDialog loadingPDialog = null;
	private SharedPreferences shared;
	private SharedPreferences.Editor editor;
	public static View view_data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// setContentView(R.layout.e4_activity_collect);

		setContentView(R.layout.e9_goods_collect_grid);
		view_data = (View) findViewById(R.id.view_data);
		loadingPDialog = new ProgressDialog(this);
		loadingPDialog.setMessage("正在加载....");
		loadingPDialog.setCancelable(false);
		shared = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
		String userID = shared.getString("member_id", "");
		Log.i("login-user-id", userID);
		back = (ImageView) findViewById(R.id.collect_top_view_back);
		listContainer = LayoutInflater.from(this);
		back.setOnClickListener(this);
		gridView = (StaggeredGridView) findViewById(R.id.staggeredGridView);
		// initAdapter();
		// gridView.setAdapter(adapter);
		collectAdapter = new E9_CollectGoodsAdapter(this, datalist);

		gridView.setAdapter(collectAdapter);

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(StaggeredGridView parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Log.i("click", position + "");
				Intent it = new Intent(CollectActivity.this,
						B2_ProductdetailsActivity.class);
				it.putExtra("goods_id", datalist.get(position).get("goods_id"));
				startActivity(it);
				overridePendingTransition(R.anim.push_right_in,
						R.anim.push_right_out);

			}

		});
		gridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(StaggeredGridView parent, View view,
					final int position, long id) {
				// TODO Auto-generated method stub
				Log.i("long-click", position + "");
				AlertDialog.Builder builder = new Builder(CollectActivity.this);
				builder.setTitle("确认删除收藏商品？");

				builder.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();

							}

						});
				builder.setPositiveButton("确认",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								loadingPDialog.show();
								HttpUtils.delFavoriteGoods(res_del, datalist
										.get(position).get("goods_id"));
							}
						});
				builder.create().show();

				return false;
			}
		});

	}

	public void isLogin() {
		SharedPreferences shared = getSharedPreferences("loginInfo",
				Activity.MODE_PRIVATE);
		String userID = shared.getString("member_id", "");
		if (userID.equals("")) {
			Intent it = new Intent(CollectActivity.this,
					E6_SigninActivity.class);
			startActivity(it);
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isLogin();
		HttpUtils.getFavoriteGoods(res, "1", "30");
		loadingPDialog.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.collect_top_view_back:
			super.finish();
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;

		default:
			break;
		}

	}

	JsonHttpResponseHandler res = new JsonHttpResponseHandler() {
		public void onSuccess(int statusCode, org.apache.http.Header[] headers,
				org.json.JSONObject response) {
			super.onSuccess(statusCode, headers, response);
			Log.i("get_collect", response.toString());
			int result = 0;
			loadingPDialog.dismiss();
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

				datalist.clear();
				JSONArray array;
				try {
					array = response.getJSONArray("list");
					for (int i = 0; i < array.length(); i++) {
						JSONObject jsonItem = array.getJSONObject(i);
						HashMap<String, String> map = new HashMap();
						map.put("goods_id", jsonItem.getString("goods_id"));
						map.put("goods_name", jsonItem.getString("goods_name"));
						map.put("goods_price",
								jsonItem.getString("goods_price"));
						map.put("goods_marketprice",
								jsonItem.getString("goods_marketprice"));
						map.put("goods_salenum",
								jsonItem.getString("goods_salenum"));
						map.put("goods_image", LandousAppConst.HOME_IMG_URL
								+ jsonItem.getString("store_id") + "/"
								+ jsonItem.getString("goods_image"));
						datalist.add(map);
					}
					if (array.length() == 0) {
						AlertDialog.Builder builder = new Builder(
								CollectActivity.this);
						builder.setMessage("您还没有收藏商品");

						builder.setTitle("提示");

						builder.setNegativeButton("确认",
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}

								});
						builder.create().show();
					}
					view_data.setVisibility(View.GONE);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					view_data.setVisibility(View.VISIBLE);
					e.printStackTrace();
				}

				collectAdapter.notifyDataSetChanged();
			} else {
				try {
					String msg = response.getString("message");

					Log.d("perssion", msg);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		public void onFailure(int statusCode, org.apache.http.Header[] headers,
				Throwable throwable, org.json.JSONObject errorResponse) {
			super.onFailure(statusCode, headers, throwable, errorResponse);
			loadingPDialog.dismiss();
			AlertDialog.Builder builder = new Builder(CollectActivity.this);
			builder.setTitle("网络连接出现异常");

			builder.setNegativeButton("确认",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}

					});
			builder.create().show();

		};

	};
	JsonHttpResponseHandler res_del = new JsonHttpResponseHandler() {
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
				loadingPDialog.dismiss();
				AlertDialog.Builder builder = new Builder(CollectActivity.this);
				builder.setMessage("删除收藏商品成功");

				builder.setTitle("提示");

				builder.setNegativeButton("确认",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								HttpUtils.getFavoriteGoods(res, "1", "30");
								loadingPDialog.show();
							}

						});
				builder.create().show();

			}
		};

		public void onFailure(int statusCode, org.apache.http.Header[] headers,
				Throwable throwable, org.json.JSONObject errorResponse) {
			super.onFailure(statusCode, headers, throwable, errorResponse);
			loadingPDialog.dismiss();
		};

	};

}
