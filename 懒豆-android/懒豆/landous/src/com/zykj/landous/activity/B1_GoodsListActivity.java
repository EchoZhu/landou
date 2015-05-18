package com.zykj.landous.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.external.maxwin.view.XListView.IXListViewListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.socialize.utils.Log;
import com.zykj.landous.LandousAppConst;
import com.zykj.landous.R;
import com.zykj.landous.Tools.HttpUtils;
import com.zykj.landous.adapter.B1_GoodsAdapter;
import com.zykj.landous.view.MyListView;

/**
 * 商品列表
 * 
 * @author zhangxu
 * 
 */
public class B1_GoodsListActivity extends Activity implements
		IXListViewListener, OnClickListener {
	ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
	private LinearLayout ll_tab1, ll_tab2, ll_tab3, ll_tab4;
	private LinearLayout[] tabs = null;
	private MyListView mGoodsListview;
	private int tabs_type = 0;
	private Intent it;
	String type = "";
	private String gc_id = "";
	private String getListType = "";
	String search_text = "";
	String orderby = "";
	View view_data;
	View view_net;
	private int xlstate=0;//按销量排序的状态，0为第一次点击，1为多次点击
	private int hpstate=0;//按好评排序的状态
	private int zxstate=0;//按最新排序的状态
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 1) {
				tabs[msg.what]
						.setBackgroundResource(R.drawable.icon_sort_top_below);
			} else {
				tabs[msg.what]
						.setBackgroundResource(R.drawable.icon_sort_below);
			}

		}
	};
	B1_GoodsAdapter goodsAdapter;
	private ProgressDialog loadingPDialog = null;
	private EditText search_input;
	private String stc_id = "";
	private ImageView nav_back_button;
	int page = 1;
	int per_page = 20;
	boolean MAX_Length = false;
	/**
	 * data.size();
	 */
	int D_SIZE = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.b1_listofgoods);
		init();
	}

	private void init() {
		view_data = (View) findViewById(R.id.view_data);
		view_net = (View) findViewById(R.id.view_net);
		view_net.setVisibility(View.GONE);
		view_data.setVisibility(View.GONE);
		nav_back_button = (ImageView) findViewById(R.id.nav_back_button);
		nav_back_button.setOnClickListener(this);
		loadingPDialog = new ProgressDialog(this);
		loadingPDialog.setMessage("正在加载....");
		loadingPDialog.setCancelable(false);
		search_input = (EditText) findViewById(R.id.search_input);
		it = getIntent();
		type = it.getStringExtra("type") == null ? "" : it
				.getStringExtra("type");
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (type.equals("search_input")) {
			search_input.setSelected(true);
			imm.showSoftInput(search_input, InputMethodManager.SHOW_FORCED);

		} else {

		}
		gc_id = it.getStringExtra("gc_id") == null ? "" : it
				.getStringExtra("gc_id");
		Log.i("gc_id", gc_id);
		stc_id = it.getStringExtra("stc_id") == null ? "" : it
				.getStringExtra("stc_id");
		ll_tab1 = (LinearLayout) findViewById(R.id.ll_tab1);
		ll_tab1.setOnClickListener(this);
		ll_tab2 = (LinearLayout) findViewById(R.id.ll_tab2);
		ll_tab2.setOnClickListener(this);
		ll_tab3 = (LinearLayout) findViewById(R.id.ll_tab3);
		ll_tab3.setOnClickListener(this);
		ll_tab4 = (LinearLayout) findViewById(R.id.ll_tab4);
		ll_tab4.setOnClickListener(this);
		tabs = new LinearLayout[] { ll_tab1, ll_tab2, ll_tab3, ll_tab4 };
		mGoodsListview = (MyListView) findViewById(R.id.goodslistview);
		mGoodsListview.setPullLoadEnable(true);
		mGoodsListview.setPullRefreshEnable(true);
		mGoodsListview.setXListViewListener(this, 0);
		mGoodsListview.setRefreshTime();
		goodsAdapter = new B1_GoodsAdapter(this, data);
		mGoodsListview.setAdapter(goodsAdapter);
		if (gc_id.length() != 0 || stc_id.length() != 0) {
			loadingPDialog.show();
			HttpUtils.getGoodsList(res_getGoodsList, "&gc_id=" + gc_id
					+ "&stc_id=" + stc_id + "&search_text=" + search_text
					+ "&orderby=" + orderby + "&page=" + page + "&per_page="
					+ per_page);
		}
		if (gc_id != null) {
			getListType = gc_id;
		}

		search_input.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				search_input.setCursorVisible(true);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
				search_input.setCursorVisible(true);
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}
		});
		search_input.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView text, int arg1, KeyEvent arg2) {
				// TODO Auto-generated method stub
				data.clear();
				goodsAdapter.notifyDataSetChanged();
				loadingPDialog.show();
				search_text = search_input.getText().toString();
				gc_id = "";
				stc_id = "";
				orderby = "";
				HttpUtils.getGoodsList(res_getGoodsList, "&gc_id=" + gc_id
						+ "&stc_id=" + stc_id + "&search_text=" + search_text
						+ "&orderby=" + orderby + "&page=" + page
						+ "&per_page=" + per_page);
				return true;
			}
		});
		search_input.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub

				if (hasFocus) {
					search_input.setCursorVisible(true);
				} else {
					search_input.setCursorVisible(false);
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_tab1:
			if (xlstate==0) {
			data.clear();
			goodsAdapter.notifyDataSetChanged();
			loadingPDialog.show();
			page = 1;
			per_page = 20;
			MAX_Length = false;
			D_SIZE = 0;
			// 求速，最简单方法，销量
			orderby = "goods_salenum desc";
				xlstate=1;
			HttpUtils.getGoodsList(res_getGoodsList, "&gc_id=" + gc_id
					+ "&stc_id=" + stc_id + "&search_text=" + search_text
					+ "&orderby=" + orderby + "&page=" + page + "&per_page="
					+ per_page);
			setBg(0);
			hpstate=0;
			zxstate=0;
		}else{
			xlstate=1;
			hpstate=0;
			zxstate=0;
			setBg(0);
//			goodsAdapter.notifyDataSetChanged();
			}
			break;
		case R.id.ll_tab2:
			data.clear();
			goodsAdapter.notifyDataSetChanged();
			// 0是价格从高到底1是从底到高
			loadingPDialog.show();
			int mTag = (Integer) (v.getTag() == null ? 0 : v.getTag());
			page = 1;
			per_page = 20;
			MAX_Length = false;
			D_SIZE = 0;
			if (mTag % 2 == 0) {
				orderby = "goods_price%20desc";
				HttpUtils.getGoodsList(res_getGoodsList, "&gc_id=" + gc_id
						+ "&stc_id=" + stc_id + "&search_text=" + search_text
						+ "&orderby=" + orderby + "&page=" + page
						+ "&per_page=" + per_page);

			} else {
				orderby = "goods_price%20asc";
				HttpUtils.getGoodsList(res_getGoodsList, "&gc_id=" + gc_id
						+ "&stc_id=" + stc_id + "&search_text=" + search_text
						+ "&orderby=" + orderby + "&page=" + page
						+ "&per_page=" + per_page);

			}

			mTag++;
			v.setTag(mTag);
			setBg(1);
			xlstate=0;
			hpstate=0;
			zxstate=0;
			break;
		case R.id.ll_tab3:
			if (hpstate==0) {
				hpstate=1;
			data.clear();
			goodsAdapter.notifyDataSetChanged();
			loadingPDialog.show();
			// 好评
			page = 1;
			per_page = 20;
			MAX_Length = false;
			D_SIZE = 0;
			orderby = "evaluation_good_star desc";
			HttpUtils.getGoodsList(res_getGoodsList, "&gc_id=" + gc_id
					+ "&stc_id=" + stc_id + "&search_text=" + search_text
					+ "&orderby=" + orderby + "&page=" + page + "&per_page="
					+ per_page);
			setBg(2);
			xlstate=0;
			zxstate=0;
			}else{
				xlstate=0;
				hpstate=1;
				zxstate=0;
				setBg(2);
//				goodsAdapter.notifyDataSetChanged();
				}
			break;
		case R.id.ll_tab4:
			if (zxstate==0) {
				zxstate=1;
			data.clear();
			goodsAdapter.notifyDataSetChanged();
			loadingPDialog.show();
			// 最新
			page = 1;
			per_page = 20;
			MAX_Length = false;
			D_SIZE = 0;
			orderby = "goods_edittime desc";
			HttpUtils.getGoodsList(res_getGoodsList, "&gc_id=" + gc_id
					+ "&stc_id=" + stc_id + "&search_text=" + search_text
					+ "&orderby=" + orderby + "&page=" + page + "&per_page="
					+ per_page);
			setBg(3);
			xlstate=0;
			hpstate=0;
			}else{
				xlstate=0;
				hpstate=0;
				zxstate=1;
				setBg(3);
//				goodsAdapter.notifyDataSetChanged();
				}
			break;
		case R.id.nav_back_button:
			this.finish();
			break;

		}

	}

	private void setBg(int p) {
		int bgid = 0;
		if (p == 1) {
			bgid = tabs_type % 2 == 0 ? R.drawable.icon_sort_act_below
					: R.drawable.icon_sort_act_top;
		} else {
			bgid = tabs_type % 1 == 0 ? R.drawable.icon_sort_act_below
					: R.drawable.icon_sort_act_top;
		}
		tabs_type++;
		tabs[p].setBackgroundResource(bgid);
		for (int i = 0; i <= 3; i++) {
			if (i != p) {
				Message message = new Message();
				message.what = i;
				mHandler.sendMessage(message);
			}
		}
	}

	@Override
	public void onRefresh(int id) {
		// TODO Auto-generated method stub
		loadingPDialog.show();
		HttpUtils.getGoodsList(res_getGoodsList, "&gc_id=" + gc_id + "&stc_id="
				+ stc_id + "&search_text=" + search_text + "&orderby="
				+ orderby + "&page=" + page + "&per_page=" + per_page);

	}

	@Override
	public void onLoadMore(int id) {
		mGoodsListview.setRefreshTime();
		loadingPDialog.show();
		if (!MAX_Length) {
			per_page += 20;
			HttpUtils.getGoodsList(res_getGoodsList, "&gc_id=" + gc_id
					+ "&stc_id=" + stc_id + "&search_text=" + search_text
					+ "&orderby=" + orderby + "&page=" + page + "&per_page="
					+ per_page);
		} else {
			Toast.makeText(B1_GoodsListActivity.this, "只有这么多商品哟",
					Toast.LENGTH_LONG).show();
			loadingPDialog.dismiss();
			mGoodsListview.stopLoadMore();
		}
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
				mGoodsListview.stopLoadMore();
				mGoodsListview.stopRefresh();
				D_SIZE = data.size();
				data.clear();
				try {
					JSONArray array = response.getJSONArray("list");
					Log.i("datasize", array.length() - D_SIZE + "hello");
					if (array.length() - D_SIZE < 20) {
						Log.i("datasize", D_SIZE + "");
						MAX_Length = true;
					}

					for (int i = 0; i < array.length(); i++) {
						JSONObject jsonItem = array.getJSONObject(i);
						Map<String, String> map = new HashMap();
						String store_id = jsonItem.getString("store_id") + "/";
						map.put("goods_name", jsonItem.getString("goods_name"));
						map.put("store_name", jsonItem.getString("store_name"));
						map.put("goods_marketprice",
								jsonItem.getString("goods_marketprice"));
						map.put("goods_price",
								jsonItem.getString("goods_price"));
						map.put("goods_id", jsonItem.getString("goods_id"));
						map.put("goods_image", LandousAppConst.HOME_IMG_URL
								+ store_id + jsonItem.getString("goods_image"));
						data.add(map);
					}
					view_net.setVisibility(View.GONE);
					view_data.setVisibility(View.GONE);
				} catch (JSONException e) {
					view_net.setVisibility(View.GONE);
					view_data.setVisibility(View.VISIBLE);
					e.printStackTrace();
				}
				goodsAdapter.notifyDataSetChanged();
				loadingPDialog.dismiss();

			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers,
				Throwable throwable, JSONObject errorResponse) {
			// TODO Auto-generated method stub
			loadingPDialog.dismiss();
			// mGoodsListview.setBackgroundResource(R.drawable.img_net_error);
			view_net.setVisibility(View.VISIBLE);
			view_data.setVisibility(View.GONE);
			Toast.makeText(getApplicationContext(), "网络连接超时", Toast.LENGTH_LONG)
					.show();
			super.onFailure(statusCode, headers, throwable, errorResponse);
		}
	};

}
