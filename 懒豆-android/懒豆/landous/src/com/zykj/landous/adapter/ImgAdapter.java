package com.zykj.landous.adapter;

import java.util.List;
import java.util.Map;

import com.BeeFramework.BeeFrameworkApp;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zykj.landous.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class ImgAdapter extends BaseAdapter {

	private Context _context;
	List<Map<String, String>> data;

	public ImgAdapter(Context context, List<Map<String, String>> data) {
		_context = context;
		this.data = data;
	}

	public int getCount() {
		return data == null ? 0 : data.size();
	}

	public Object getItem(int position) {

		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			ImageView imageView = new ImageView(_context);
			imageView.setAdjustViewBounds(true);
			imageView.setScaleType(ScaleType.CENTER_INSIDE);
			imageView.setLayoutParams(new Gallery.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			convertView = imageView;
			viewHolder.imageView = (ImageView) convertView;
			viewHolder.imageView.setScaleType(ScaleType.FIT_XY);
			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// ImageLoader.getInstance().displayImage(
		// data.get(position).get("pic_img")+"", viewHolder.imageView,
		// BeeFrameworkApp.options); 图片测试哪种比例最协调，暂时注销

		viewHolder.imageView.setImageResource(R.drawable.img_home_defbg);
		return convertView;
	}

	private static class ViewHolder {
		ImageView imageView;
	}
}
