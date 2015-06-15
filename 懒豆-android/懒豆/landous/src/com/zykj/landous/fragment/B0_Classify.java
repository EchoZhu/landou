package com.zykj.landous.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.zykj.landous.MainActivity;
import com.zykj.landous.R;
import com.zykj.landous.Tools.HttpUtils;
import com.zykj.landous.activity.B1_GoodsListActivity;
import com.zykj.landous.adapter.B0_ClassLeftAdapter;
import com.zykj.landous.adapter.B0_ClassMiddle_Adapter;
import com.zykj.landous.adapter.B0_ClassRightAdapter;
import com.zykj.landous.classify.LeftViewAdapter;
import com.zykj.landous.classify.MiddleAdapter;

public class B0_Classify extends Fragment implements OnClickListener {

	List<Map<String, String>> data = new ArrayList<Map<String, String>>();
	List<Map<String, String>> dataMiddle = new ArrayList<Map<String, String>>();
	List<Map<String, String>> dataRight = new ArrayList<Map<String, String>>();
	private ListView lv_class_left = null;
	private ListView lv_class_middle = null;
	private ListView lv_class_right = null;
	private int one = 0, two = 0;
	// 以上数据用不到
	B0_ClassLeftAdapter class_ada_one;
	B0_ClassMiddle_Adapter class_ada_two;
	B0_ClassRightAdapter class_ada_three;
	/**
	 * 用来记录点击的listview；一共两种点击状态，点击最左边弹出中间，点击中间弹出第最右边
	 */
	int ON_LISTVIEW = 0;
	private ProgressDialog loadingPDialog = null;
	private LinearLayout ll_middle;
	private LinearLayout ll_left;
	private LinearLayout ll_right;
	String parent_id_middle = "";
	String parent_id_right = "";
    String gc_id[] =new String[]{"3025","3028","3029","3030","3031","3032","3033","3034","3035","3036","3037"};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.b0_classify, null);
		loadingPDialog = new ProgressDialog(getActivity());
		loadingPDialog.setMessage("正在加载....");
		loadingPDialog.setCancelable(false);
		if (data.size() == 0) {
			loadingPDialog.show();
			HttpUtils.getGoodsClass(res_getGoodsClass, "0");
		}
		ll_middle = (LinearLayout) view.findViewById(R.id.ll_middle);
		ll_left = (LinearLayout) view.findViewById(R.id.ll_left);
		ll_right = (LinearLayout) view.findViewById(R.id.ll_right);
		dataMiddle = new ArrayList<Map<String, String>>();
		dataRight = new ArrayList<Map<String, String>>();
		class_ada_one = new B0_ClassLeftAdapter(getActivity(), data);
		class_ada_two = new B0_ClassMiddle_Adapter(getActivity(), dataMiddle);
		class_ada_three = new B0_ClassRightAdapter(getActivity(), dataRight);
		lv_class_left = (ListView) view.findViewById(R.id.lv_class_left);
		lv_class_middle = (ListView) view.findViewById(R.id.lv_class_middle);
		lv_class_left.setVisibility(View.VISIBLE);
		lv_class_middle.setVisibility(View.GONE);
		lv_class_right = (ListView) view.findViewById(R.id.lv_class_right);
		lv_class_right.setVisibility(View.GONE);
		lv_class_left.setAdapter(class_ada_one);
		lv_class_middle.setAdapter(class_ada_two);
		lv_class_right.setAdapter(class_ada_three);
		lv_class_left.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int i,
					long arg3) {
				ON_LISTVIEW = 1;
				loadingPDialog.show();
				parent_id_middle = view.getTag().toString();
				HttpUtils.getGoodsClass(res_getGoodsClassMiddle,
						parent_id_middle);
				one = i;
				class_ada_one.setItem(one);
				class_ada_one.notifyDataSetChanged();
				// lv_class_middle.setAdapter(class_ada_two);
				lv_class_middle.setVisibility(View.VISIBLE);
				ll_middle.setVisibility(View.VISIBLE);
				ll_right.setVisibility(View.GONE);
				ll_left.setLayoutParams(new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.MATCH_PARENT, 2));
				ll_middle.setLayoutParams(new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.MATCH_PARENT, 1));
			}
		});
		lv_class_middle.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
				loadingPDialog.show();
				two = arg2;
				class_ada_two.setItem(two);
				if (two != 0) {
					class_ada_two.notifyDataSetChanged();
					parent_id_right = view.getTag().toString();
					HttpUtils.getGoodsClass(res_getGoodsClassRight,
							parent_id_right);
					lv_class_right.setVisibility(View.VISIBLE);
					ll_right.setVisibility(View.VISIBLE);
					ll_left.setLayoutParams(new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.MATCH_PARENT, 1));
					ll_middle.setLayoutParams(new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.MATCH_PARENT, 1));
					ll_right.setLayoutParams(new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.MATCH_PARENT, 1));
				} else {
					loadingPDialog.dismiss();
					Intent it = new Intent(getActivity(),
							B1_GoodsListActivity.class);
					it.putExtra("gc_id", view.getTag() + "");
					startActivity(it);
					getActivity().overridePendingTransition(
							R.anim.push_right_in, R.anim.push_right_out);
				}
			}
		});
		lv_class_right.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
				Intent it = new Intent(getActivity(),
						B1_GoodsListActivity.class);
				it.putExtra("gc_id", view.getTag() + "");
				startActivity(it);
				getActivity().overridePendingTransition(R.anim.push_right_in,
						R.anim.push_right_out);
			}
		});

		return view;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	/**
	 * 获取商品分类左边。。。。用一个方法写三个会出错！下午6点找到原因：做3个data可以解决
	 */
	JsonHttpResponseHandler res_getGoodsClass = new JsonHttpResponseHandler() {

		@Override
		public void onSuccess(int statusCode, Header[] headers,
				JSONObject response) {
			super.onSuccess(statusCode, headers, response);
            Log.e("res_getGoodsClass_response", response+"");
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
					for (int i = 0; i < array.length(); i++) 
//						for (int i = 0; i < 8; i++) 
					{
						JSONObject jsonItem = array.getJSONObject(i);
						Map<String, String> map = new HashMap();
						//*******
						for (int k = 0; k < gc_id.length; k++) {
							
							if (jsonItem.getString("gc_id").equals(gc_id[k])) //如果某一条的商品信息中的gc_id等于goos_id中的某一个数
							{
								map.put("gc_name", jsonItem.getString("gc_name"));
								map.put("gc_id", jsonItem.getString("gc_id"));
								data.add(map);
							}
						}
						//*******
					}
					Log.i("landousjson", data.toString());
					// if (ON_LISTVIEW == 0) {
					// class_ada_one = null;
					// class_ada_one = new B0_ClassLeftAdapter(getActivity(),
					// data);
					// lv_class_left.setAdapter(class_ada_one);
					// }
					class_ada_one.notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				loadingPDialog.dismiss();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers,
				Throwable throwable, JSONObject errorResponse) {
			// TODO Auto-generated method stub
			Toast.makeText(getActivity(), "网络连接超时", Toast.LENGTH_LONG).show();
			loadingPDialog.dismiss();
			super.onFailure(statusCode, headers, throwable, errorResponse);
		}

	};
	JsonHttpResponseHandler res_getGoodsClassMiddle = new JsonHttpResponseHandler() {

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
					dataMiddle.clear();
					JSONArray array = response.getJSONArray("list");
					Map<String, String> map = map = new HashMap();
					map.put("gc_name", "全部");
					map.put("gc_id", parent_id_middle);
					dataMiddle.add(map);
					for (int i = 0; i < array.length(); i++) {
						JSONObject jsonItem = array.getJSONObject(i);
						map = new HashMap();
						map.put("gc_name", jsonItem.getString("gc_name"));
						map.put("gc_id", jsonItem.getString("gc_id"));
						dataMiddle.add(map);
					}
					class_ada_two = null;
					class_ada_two = new B0_ClassMiddle_Adapter(getActivity(),
							dataMiddle);
					lv_class_middle.setAdapter(class_ada_two);
					class_ada_two.notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
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
			Toast.makeText(getActivity(), "网络连接超时", Toast.LENGTH_LONG).show();
			super.onFailure(statusCode, headers, throwable, errorResponse);
		}
	};
	JsonHttpResponseHandler res_getGoodsClassRight = new JsonHttpResponseHandler() {

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
					dataRight.clear();
					JSONArray array = response.getJSONArray("list");
					Map<String, String> map = new HashMap();
					map.put("gc_name", "全部");
					map.put("gc_id", parent_id_right);
					dataRight.add(map);
					for (int i = 0; i < array.length(); i++) {
						JSONObject jsonItem = array.getJSONObject(i);
						map = new HashMap();
						map.put("gc_name", jsonItem.getString("gc_name"));
						map.put("gc_id", jsonItem.getString("gc_id"));
						dataRight.add(map);
					}
					class_ada_three = null;
					class_ada_three = new B0_ClassRightAdapter(getActivity(),
							dataRight);
					lv_class_right.setAdapter(class_ada_three);
					class_ada_three.notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
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
			Toast.makeText(getActivity(), "网络连接超时", Toast.LENGTH_LONG).show();
			super.onFailure(statusCode, headers, throwable, errorResponse);
		}
	};

}
