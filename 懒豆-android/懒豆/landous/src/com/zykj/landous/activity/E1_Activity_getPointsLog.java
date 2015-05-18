package com.zykj.landous.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.BeeFramework.activity.BaseActivity;
import com.external.maxwin.view.XListView.IXListViewListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.zykj.landous.R;
import com.zykj.landous.Tools.HttpUtils;
import com.zykj.landous.adapter.E1_Adapter_getPointsLog;
import com.zykj.landous.view.MyListView;

/**
 * @author 作者 zhang
 * @version 创建时间：2015年1月22日 上午11:42:32 类说明 查询积分变更
 */
public class E1_Activity_getPointsLog extends BaseActivity implements
		OnClickListener, IXListViewListener {
	private MyListView listview;
	private TextView top_text;
	private ImageView back;
	List<Map<String, String>> data = new ArrayList<Map<String, String>>();
	E1_Adapter_getPointsLog adapter;
	private ProgressDialog loadingPDialog = null;
	int page = 1;
	int per_page = 10;
	boolean MAX_Length = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.e1_activity_getpointslog);
		loadingPDialog = new ProgressDialog(this);
		loadingPDialog.setMessage("正在加载....");
		loadingPDialog.setCancelable(false);
		loadingPDialog.show();
		listview = (MyListView) findViewById(R.id.listview);
		top_text = (TextView) findViewById(R.id.top_view_text);
		top_text.setText("我的积分");
		back = (ImageView) findViewById(R.id.top_view_back_point);
		back.setOnClickListener(this);
		adapter = new E1_Adapter_getPointsLog(E1_Activity_getPointsLog.this,
				data);
		listview.setAdapter(adapter);
		listview.setPullLoadEnable(true);
		listview.setPullRefreshEnable(true);
		listview.setXListViewListener(this, 0);
		listview.setRefreshTime();
		HttpUtils.getPointsLog(res, "1", per_page + "");
	}

	@Override
	public void onRefresh(int id) {
		loadingPDialog.show();
		HttpUtils.getPointsLog(res, "1", per_page + "");
	}

	@Override
	public void onLoadMore(int id) {
		loadingPDialog.show();
		if (!MAX_Length) {
			per_page += 10;
			HttpUtils.getPointsLog(res, "1", per_page + "");
		} else {
			Toast.makeText(E1_Activity_getPointsLog.this, "只有这么多订单哟",
					Toast.LENGTH_LONG).show();
			loadingPDialog.dismiss();
			listview.stopLoadMore();
		}
	}

	/**
	 * 获取首页商品列表
	 */
	JsonHttpResponseHandler res = new JsonHttpResponseHandler() {

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
				listview.stopLoadMore();
				listview.stopRefresh();
				try {
					JSONArray array = response.getJSONArray("list");
					if (array.length() - data.size() < 10) {
						MAX_Length = true;
					}
					data.clear();
					for (int i = 0; i < array.length(); i++) {
						JSONObject jsonItem = array.getJSONObject(i);
						Map<String, String> map = new HashMap();
						map.put("pl_desc", jsonItem.getString("pl_desc"));
						map.put("pl_addtime", jsonItem.getString("pl_addtime"));
						map.put("pl_points", jsonItem.getString("pl_points"));
						data.add(map);
					}
					adapter.notifyDataSetChanged();
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
			super.onFailure(statusCode, headers, throwable, errorResponse);
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_view_back_point:
			super.finish();
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

			break;

		default:
			break;
		}

	}
}
