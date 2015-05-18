package com.zykj.landous.adapter;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.BeeFramework.BeeFrameworkApp;
import com.BeeFramework.adapter.BeeBaseAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zykj.landous.R;
import com.zykj.landous.activity.B2_ProductdetailsActivity;

/**
 * 
 * @author zhangxu 商铺介绍底下的热销产品
 */
public class C1_ShopAdapter extends BeeBaseAdapter {

	private ArrayList<Map<String, String>> dataList;
	private Context c;
	private LayoutInflater listContainer;
	private LinearLayout ll_spcar;

	public C1_ShopAdapter(Context c, ArrayList<Map<String, String>> dataList) {
		super(c, dataList);
		// TODO Auto-generated constructor stub
		this.dataList = dataList;
		this.c = c;
		listContainer = LayoutInflater.from(c);
	}

	@Override
	protected BeeCellHolder createCellHolder(View cellView) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getViewTypeCount() {

		return 1000;
	}

	@Override
	protected View bindData(int position, View cellView, ViewGroup parent,
			BeeCellHolder h) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getItemViewType(int position) {
		return position;
	}

	public int getCount() {
		// TODO Auto-generated method stub

		return dataList == null ? 0 : dataList.size();
	}

	@Override
	public View createCellView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getView(final int position, View cellView, ViewGroup parent) {
		if (cellView == null) {
			cellView = listContainer.inflate(R.layout.c1_shopitem, null);
		}
		TextView tv_oldprice = (TextView) cellView
				.findViewById(R.id.tv_oldprice);
		tv_oldprice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
		tv_oldprice.setText("￥"
				+ dataList.get(position).get("goods_marketprice"));
		TextView tv_price = (TextView) cellView.findViewById(R.id.tv_price);
		tv_price.setText("￥" + dataList.get(position).get("goods_price"));
		TextView tv_title = (TextView) cellView.findViewById(R.id.tv_title);
		tv_title.setText(dataList.get(position).get("goods_name"));
		ImageView iv_goods = (ImageView) cellView.findViewById(R.id.iv_goods);
		String url = dataList.get(position).get("goods_image") + "";
		ImageLoader.getInstance().displayImage(url, iv_goods,
				BeeFrameworkApp.options_head);
		Log.i("landousurl", url);
		cellView.setOnClickListener(new Mylistener(position));
		TextView tv_num = (TextView) cellView.findViewById(R.id.tv_num);
		tv_num.setText(dataList.get(position).get("goods_salenum") + "人已付款");
		return cellView;

	}

	class Mylistener implements View.OnClickListener {
		int position;

		public Mylistener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View arg0) {

			Intent it = new Intent(mContext, B2_ProductdetailsActivity.class);
			it.putExtra("goods_id", dataList.get(position).get("goods_id") + "");
			it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(it);

		}

	}

}
