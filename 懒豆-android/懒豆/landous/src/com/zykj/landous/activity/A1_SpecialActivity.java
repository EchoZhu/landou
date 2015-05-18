package com.zykj.landous.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.BeeFramework.BeeFrameworkApp;
import com.external.maxwin.view.XListView.IXListViewListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zykj.landous.LandousAppConst;
import com.zykj.landous.R;
import com.zykj.landous.R.layout;
import com.zykj.landous.Tools.HttpUtils;
import com.zykj.landous.adapter.B1_GoodsAdapter;
import com.zykj.landous.view.MyListView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

public class A1_SpecialActivity extends Activity implements IXListViewListener,
		OnClickListener {
	private MyListView mGoodsListview;
	ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
	private ProgressDialog loadingPDialog = null;
	private ImageView banner;
	private B1_GoodsAdapter goodsAdapter;
	private TextView title;
	private ImageView back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a1_special_activity);
		String special_id = getIntent().getStringExtra("special_id");
		HttpUtils.getSpecial(res_getGoodsList, special_id);
		loadingPDialog = new ProgressDialog(this);
		loadingPDialog.setMessage("正在加载....");
		loadingPDialog.setCancelable(false);
		loadingPDialog.show();
		banner = new ImageView(A1_SpecialActivity.this);
		title = (TextView) findViewById(R.id.top_view_text);
		back = (ImageView) findViewById(R.id.top_view_back);
		back.setOnClickListener(this);
		mGoodsListview = (MyListView) findViewById(R.id.special_goods_listview);
		mGoodsListview.setPullLoadEnable(false);
		banner.setScaleType(ScaleType.FIT_XY);
		mGoodsListview.addHeaderView(banner);
		mGoodsListview.setPullRefreshEnable(true);
		mGoodsListview.setXListViewListener(this, 0);
		mGoodsListview.setRefreshTime();
		goodsAdapter = new B1_GoodsAdapter(this, data);
		mGoodsListview.setAdapter(goodsAdapter);
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
					JSONObject sData = response.getJSONObject("data");
					String img_url = LandousAppConst.special_img_head
							+ sData.get("special_image");
					ImageLoader.getInstance().displayImage(img_url, banner,
							BeeFrameworkApp.options_no_default);

					android.view.ViewGroup.LayoutParams params = banner
							.getLayoutParams();
					params.height = banner.getWidth() / 5 * 2;
					params.width = banner.getWidth();
					banner.setLayoutParams(params);
					title.setText(sData.getString("special_title"));
					JSONArray array = sData
							.getJSONArray("special_product_list");
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

				} catch (JSONException e) {
					// TODO Auto-generated catch block
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
			Toast.makeText(getApplicationContext(), "网络连接超时", Toast.LENGTH_LONG)
					.show();
			super.onFailure(statusCode, headers, throwable, errorResponse);
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.finish();
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

	}
}
