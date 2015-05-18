package com.zykj.landous.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.external.androidquery.callback.AjaxStatus;
import com.external.maxwin.view.XListView.IXListViewListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.zykj.landous.R;
import com.zykj.landous.Tools.HttpUtils;
import com.zykj.landous.Tools.TimeStamp;
import com.zykj.landous.activity.B2_ProductdetailsActivity;
import com.zykj.landous.activity.E1_NO_PaymentAvtivity;
import com.zykj.landous.adapter.B0_ClassRightAdapter;
import com.zykj.landous.adapter.B1_GoodsAdapter;
import com.zykj.landous.adapter.B2_0_PriductCommentAdapter;
import com.zykj.landous.view.MyListView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author 作者 zhang
 * @version 创建时间：2015年1月3日 下午4:34:31 类说明：商品评价
 */
public class B2_0_PriductComment extends Fragment implements IXListViewListener {
	private MyListView listview;
	View view;
	private String goods_id = "";
	private ProgressDialog loadingPDialog = null;
	private TextView tv_good_percent;
	private TextView tv_all;
	B2_0_PriductCommentAdapter adapter;
	ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
	int page = 1;
	int per_page = 10;
	boolean MAX_Length = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.b2_0_priductcomment, null);
		loadingPDialog = new ProgressDialog(getActivity());
		loadingPDialog.setMessage("正在加载....");
		loadingPDialog.setCancelable(false);
		loadingPDialog.show();
		init(view);
		return view;
	}

	private void init(View view) {
		tv_all = (TextView) view.findViewById(R.id.tv_all);
		tv_good_percent = (TextView) view.findViewById(R.id.tv_good_percent);
		listview = (MyListView) view.findViewById(R.id.listview);
		listview.setPullLoadEnable(true);
		listview.setPullRefreshEnable(true);
		listview.setXListViewListener(this, 0);
		listview.setRefreshTime();
		adapter = new B2_0_PriductCommentAdapter(getActivity(), data);
		listview.setAdapter(adapter);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		goods_id = B2_ProductdetailsActivity.goods_id;
		HttpUtils.getGoodsComments(res, goods_id + "&page=" + page
				+ "&per_page=" + per_page);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onRefresh(int id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoadMore(int id) {
		// TODO Auto-generated method stub
		if (!MAX_Length) {
			per_page += 10;
			HttpUtils.getGoodsComments(res, goods_id + "&page=" + 1
					+ "&per_page=" + per_page);
		} else {
			Toast.makeText(getActivity(), "该商品只有这么多评论", Toast.LENGTH_LONG).show();
			listview.stopLoadMore();
		}

	}

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
				data.clear();
				try {
					JSONObject json = response.getJSONObject("info");
					tv_good_percent.setText(json.getString("good_percent")
							+ "%");
					tv_all.setText("共有" + json.getString("all") + "人参与");
					JSONArray array = response.getJSONArray("list");
					if (array.length() - json.getInt("all") == 0) {
						MAX_Length = true;
					}
					for (int i = 0; i < array.length(); i++) {
						JSONObject jsonItem = array.getJSONObject(i);
						Map<String, String> map = new HashMap();
						map.put("geval_addtime", TimeStamp
								.getBeijingTime(jsonItem
										.getString("geval_addtime")));
						map.put("geval_content",
								jsonItem.getString("geval_content"));
						map.put("geval_goodsname",
								jsonItem.getString("geval_goodsname"));
						String geval_frommembername = jsonItem
								.getString("geval_frommembername");
						map.put("geval_scores",
								jsonItem.getString("geval_scores"));
						int geval_isanonymous = jsonItem
								.getInt("geval_isanonymous");
						geval_frommembername = geval_isanonymous == 1 ? geval_frommembername
								.substring(0, 1) + "***"
								: geval_frommembername;
						map.put("geval_frommembername", geval_frommembername);

						data.add(map);
					}
					adapter.notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					MAX_Length = true;
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
			Toast.makeText(getActivity(), "网络连接超时", Toast.LENGTH_LONG).show();
			super.onFailure(statusCode, headers, throwable, errorResponse);
		}
	};
}
