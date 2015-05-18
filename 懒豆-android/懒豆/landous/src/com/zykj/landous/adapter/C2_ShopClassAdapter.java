package com.zykj.landous.adapter;

import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zykj.landous.R;

public class C2_ShopClassAdapter extends BaseAdapter {

	private Activity context;
	private int item = -1;
	ArrayList<Map<String, String>> data;

	public C2_ShopClassAdapter(Activity context,
			ArrayList<Map<String, String>> data) {
		this.context = context;
		this.data = data;
	}

	@Override
	public int getCount() {
		if (data == null)
			return 0;
		else
			return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int i, View arg1, ViewGroup arg2) {
		TextView view = new TextView(context);
		view.setGravity(Gravity.CENTER | Gravity.LEFT);
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		view.setText(data.get(i).get("stc_name") + "");
		view.setBackgroundResource(R.drawable.shopsbg);
		/* 以下为新增部分 */
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT, 90);
		view.setLayoutParams(lp);
		/* 以上为新增部分 */
		view.setTag(data.get(i).get("stc_id") + "");
		view.setPadding(20, 0, 0, 0);
		return view;
	}

	public int getItem() {
		return item;
	}

	public void setItem(int item) {
		this.item = item;
	}

}
