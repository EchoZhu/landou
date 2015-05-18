package com.zykj.landous.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.BeeFramework.adapter.BeeBaseAdapter;
import com.zykj.landous.LandousAppConst;
import com.zykj.landous.R;
import com.zykj.landous.fragment.A0_IndexFragment;

/**
 * @author 作者 zhang
 * @version 创建时间：2015年1月4日 下午11:06:26 类说明
 */
public class D0_shoping_car_adapter extends BeeBaseAdapter {
	private Context c;
	private LayoutInflater listContainer;
	private ArrayList<Map<String, String>> dataList;
	private ListView listview;
	private ArrayList<Map<String, String>> goodsData = new ArrayList<Map<String, String>>();
	private D1_shopping_car_goods_adapter adapter;

	public D0_shoping_car_adapter(Context c,
			ArrayList<Map<String, String>> dataList) {
		super(c, dataList);
		this.c = c;
		listContainer = LayoutInflater.from(c);
		this.dataList = dataList;

	}

	// @Override
	// public int getViewTypeCount() {
	// // 三种布局
	// return 3;
	// }

	@Override
	public int getCount() {
		return dataList.size();

	}

	public int getItemViewRealType(int position) {
		return 0;

	}

	@Override
	protected BeeCellHolder createCellHolder(View cellView) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected View bindData(int position, View cellView, ViewGroup parent,
			BeeCellHolder h) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View createCellView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getView(final int position, View cellView, ViewGroup parent) {
		if (getItemViewRealType(position) == 1) {
			TextView view = new TextView(c);
			view.setText(position + "");
			return view;
		} else if (getItemViewRealType(position) == 0) {
			if (cellView==null) {
				cellView = listContainer.inflate(R.layout.d0_shoping_car_shopname,null);
			}
			
			TextView tv_shopname = (TextView) cellView
					.findViewById(R.id.tv_shopname);
			tv_shopname.setText(dataList.get(position).get("store_name"));
			try {
				JSONArray array = new JSONArray(dataList.get(position)
						.get("cart_list").toString());

				listview = (ListView) cellView.findViewById(R.id.listview);
				goodsData.clear();
				for (int i = 0; i < array.length(); i++) {
					JSONObject jsonItem = array.getJSONObject(i);
					Map<String, String> map = new HashMap();
					map.put("goods_id", jsonItem.getString("goods_id"));
					map.put("goods_name", jsonItem.getString("goods_name"));
					map.put("goods_price", jsonItem.getString("goods_price"));
					map.put("goods_num", jsonItem.getString("goods_num"));
					map.put("goods_image",
							LandousAppConst.HOME_IMG_URL
									+ jsonItem.getString("store_id") + "/"
									+ jsonItem.getString("goods_image"));
					map.put("cart_id", jsonItem.getString("cart_id"));
					goodsData.add(map);
				}
				adapter = new D1_shopping_car_goods_adapter(c, goodsData);
				listview.setAdapter(adapter);
				A0_IndexFragment.setListViewHeightBasedOnChildren(listview);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			TextView view = new TextView(c);
			view.setText(position + "第三种模式");
			return view;
		}
		return cellView;

	}

}
