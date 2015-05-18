package com.zykj.landous.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.external.androidquery.callback.AjaxStatus;
import com.external.maxwin.view.XListView.IXListViewListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.zykj.landous.LandousAppConst;
import com.zykj.landous.R;
import com.zykj.landous.Tools.HttpUtils;
import com.zykj.landous.adapter.C0_ShopsAdapter;
import com.zykj.landous.view.MyListView;

/**
 * 商铺
 * 
 * @author zhangxu
 * 
 */
public class C0_ShopsFragment extends Fragment implements 
		IXListViewListener, OnClickListener {
	private LinearLayout ll_tabs;
	private C0_ShopsAdapter shopsApapter;
	private MyListView mShopsListview;
	private ImageView iv_share;
	private ProgressDialog loadingPDialog = null;
	ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
	private EditText search_input;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		loadingPDialog = new ProgressDialog(getActivity());
		loadingPDialog.setMessage("正在加载....");
		loadingPDialog.setCancelable(false);
		loadingPDialog.show();
		View view = inflater.inflate(R.layout.c0_shops_fragment, null);
		ll_tabs = (LinearLayout) view.findViewById(R.id.ll_tabs);
		ll_tabs.setVisibility(View.GONE);
		iv_share = (ImageView) view.findViewById(R.id.iv_share);
		iv_share.setVisibility(View.INVISIBLE);
		shopsApapter = new C0_ShopsAdapter(getActivity()
				.getApplicationContext(), data);
		mShopsListview = (MyListView) view.findViewById(R.id.listview);

		mShopsListview.setPullLoadEnable(false);
		mShopsListview.setPullRefreshEnable(true);
		mShopsListview.setXListViewListener(this, 0);
		mShopsListview.setRefreshTime();
		mShopsListview.setAdapter(shopsApapter);
		HttpUtils.getStoreList(res_getStoreList, null);
		search_input=(EditText)view.findViewById(R.id.search_input);
		search_input.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView text, int arg1, KeyEvent arg2) {
				// TODO Auto-generated method stub
				loadingPDialog.show();
				HttpUtils.getStoreList(res_getStoreList, text.getText().toString());
				return false;
			}
		});
		return view;
	}

	@Override
	public void onRefresh(int id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoadMore(int id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

	

	JsonHttpResponseHandler res_getStoreList = new JsonHttpResponseHandler() {

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
					JSONArray array = response.getJSONArray("list");
					for (int i = 0; i < array.length(); i++) {
						JSONObject jsonItem = array.getJSONObject(i);
						Map<String, String> map = new HashMap();
						map.put("store_name", jsonItem.getString("store_name"));
						map.put("store_zy", jsonItem.getString("store_zy"));
						map.put("store_label", LandousAppConst.SHOP_IMG_URL
								+ jsonItem.getString("store_label"));
						map.put("store_id", jsonItem.getString("store_id"));
						data.add(map);
					}
					shopsApapter.notifyDataSetChanged();

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				loadingPDialog.dismiss();
			}
			Log.i("landous", response+"");
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
