package com.zykj.landous.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zykj.landous.R;

/**
 * @author 作者 zhang
 * @version 创建时间：2015年1月22日 下午12:03:43 类说明
 */
public class E1_Adapter_getPointsLog extends BaseAdapter {
	List<Map<String, String>> data = new ArrayList<Map<String, String>>();
	private Activity c;
	private LayoutInflater listContainer;

	public E1_Adapter_getPointsLog(Activity c, List<Map<String, String>> data) {
		this.c = c;
		this.data = data;
		listContainer = LayoutInflater.from(c);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data == null ? 0 : data.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View cellView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (cellView == null) {
			cellView = listContainer.inflate(R.layout.e1_point_log_item, null);
			holder = new ViewHolder();
			holder.add_time = (TextView) cellView.findViewById(R.id.add_time);
			holder.point_change = (TextView) cellView
					.findViewById(R.id.point_change);
			holder.desc = (TextView) cellView.findViewById(R.id.point_desc);
			cellView.setTag(holder);// 绑定ViewHolder对象
		} else {
			holder = (ViewHolder) cellView.getTag();// 取出ViewHolder对象
		}

		String t = data.get(position).get("pl_addtime");
		long timel = Long.parseLong(t);
		Date d = new Date(timel * 1000);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String a_t = sdf.format(d);
		holder.add_time.setText(a_t);
		int i = Integer.valueOf(data.get(position).get("pl_points"));
		if (i > 0) {
			holder.point_change.setText("+"
					+ data.get(position).get("pl_points"));
		} else {
			holder.point_change.setText(data.get(position).get("pl_points"));
		}
		holder.desc.setText(data.get(position).get("pl_desc"));
		return cellView;
	}

	/** 存放控件 */
	public final class ViewHolder {
		public TextView add_time;
		public TextView point_change;
		public TextView desc;
	}

}
