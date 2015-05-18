package com.zykj.landous.adapter;

import java.util.ArrayList;
import java.util.Map;

import com.BeeFramework.BeeFrameworkApp;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zykj.landous.R;
import com.zykj.landous.Tools.MathTools;
import com.zykj.landous.adapter.E5_CoinGoodsAdapter.ViewHolder;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author 作者 zhang
 * @version 创建时间：2015年1月13日 下午11:43:57 类说明
 */
public class D1_OrderConfirmAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater listContainer;
	public static ArrayList<Map<String, String>> dataList;
	private ProgressDialog loadingPDialog = null;

	public D1_OrderConfirmAdapter(Context context,
			ArrayList<Map<String, String>> dataList) {
		this.context = context;
		this.dataList = dataList;
		listContainer = LayoutInflater.from(context);
		loadingPDialog = new ProgressDialog(context);
		loadingPDialog.setMessage("正在加载....");
		loadingPDialog.setCancelable(false);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return dataList == null ? 0 : dataList.size();
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

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (view == null) {
			view = listContainer.inflate(R.layout.d1_orderconfirm_item, null);
			holder = new ViewHolder();
			holder.ll_name = (LinearLayout) view.findViewById(R.id.ll_name);
			holder.tv_store_name = (TextView) view
					.findViewById(R.id.tv_store_name);
			holder.tv_goods_name = (TextView) view
					.findViewById(R.id.tv_goods_name);
			holder.tv_goods_price = (TextView) view
					.findViewById(R.id.tv_goods_price);
			holder.tv_num = (TextView) view.findViewById(R.id.tv_num);
			holder.ll_allprice = (LinearLayout) view
					.findViewById(R.id.ll_allprice);
			holder.tv_shop_goods_num = (TextView) view
					.findViewById(R.id.tv_shop_goods_num);
			holder.tv_shop_goods_price = (TextView) view
					.findViewById(R.id.tv_shop_goods_price);
			holder.iv_goods = (ImageView) view.findViewById(R.id.iv_goods);
			view.setTag(holder);// 绑定ViewHolder对象
		} else {
			holder = (ViewHolder) view.getTag();// 取出ViewHolder对象
		}

		holder.ll_name.setVisibility(View.GONE);

		if (dataList.size() == 1) {
			holder.ll_name.setVisibility(View.GONE);
			holder.ll_allprice.setVisibility(View.GONE);
		}
		if (dataList.get(position).get("position").toString() == "head") {
			holder.ll_name.setVisibility(View.GONE);
		} else if (dataList.get(position).get("position").toString() == "tail") {
			holder.ll_allprice.setVisibility(View.GONE);
		}
		holder.tv_store_name.setText(dataList.get(position).get("store_name")
				+ "");
		holder.tv_shop_goods_num.setText("共" + dataList.size() + "件商品");
		holder.tv_shop_goods_price.setText("共计:￥"
				+ dataList.get(position).get("purchase_price") + "");
		holder.tv_goods_name.setText(dataList.get(position).get("goods_name")
				+ "");
		double goods_price = Double.parseDouble(dataList.get(position).get(
				"goods_price"));
		holder.tv_goods_price.setText("￥"
				+ MathTools.DoubleKeepTwo(goods_price));
		String url = dataList.get(position).get("goods_image") + "";

		ImageLoader.getInstance().displayImage(url, holder.iv_goods,
				BeeFrameworkApp.options_head);
		holder.tv_num.setText("x" + dataList.get(position).get("goods_num"));
		return view;
	}

	/** 存放控件 */
	public final class ViewHolder {
		public LinearLayout ll_name;
		public TextView tv_store_name;
		public TextView tv_goods_name;
		public TextView tv_goods_price;
		public TextView tv_num;
		public LinearLayout ll_allprice;
		public TextView tv_shop_goods_num;
		public TextView tv_shop_goods_price;
		public ImageView iv_goods;
	}

}
