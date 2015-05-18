package com.zykj.landous.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zykj.landous.R;

import android.app.Activity;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class B0_ClassLeftAdapter extends BaseAdapter {


	private Activity context;
	private int item = -1;
	List<Map<String, String>> data = new ArrayList<Map<String, String>>();
	public B0_ClassLeftAdapter(Activity context, List<Map<String, String>> data) {
		this.context = context;
		this.data=data;

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
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		TextView view = new TextView(context);
		view.setGravity(Gravity.CENTER);
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		view.setWidth(dm.widthPixels / 3);
		if (item != -1 && arg0 == item) {
			view.setBackgroundResource(R.drawable.b0_class_leftbg);
		} else {
			view.setBackgroundColor(Color.WHITE);
		}
		view.setHeight((dm.heightPixels - 120) / (getCount() + 1));
		view.setText(data.get(arg0).get("gc_name"));
		view.setTextColor(Color.BLACK);
		view.setTag(data.get(arg0).get("gc_id"));
		return view;
	}

	public int getItem() {
		return item;
	}

	public void setItem(int item) {
		this.item = item;
	}

}
