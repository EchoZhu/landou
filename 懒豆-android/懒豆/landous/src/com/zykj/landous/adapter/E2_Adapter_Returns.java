package com.zykj.landous.adapter;

import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.BeeFramework.BeeFrameworkApp;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zykj.landous.R;
import com.zykj.landous.activity.E3_Activity_Returns;
import com.zykj.landous.adapter.E5_CoinGoodsAdapter.ViewHolder;

/**
 * @author 作者 zhang
 * @version 创建时间：2015年1月26日 上午12:09:44 类说明 退货
 */
public class E2_Adapter_Returns extends BaseAdapter {
	private Activity context;
	ArrayList<Map<String, String>> data;
	private LayoutInflater listContainer;

	public E2_Adapter_Returns(Activity context,
			ArrayList<Map<String, String>> data) {
		this.context = context;
		this.data = data;
		listContainer = LayoutInflater.from(context);

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

	@Override
	public View getView(final int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (view == null) {
			view = listContainer.inflate(R.layout.e2_item_returns, null);
			holder = new ViewHolder();
			holder.tv_goods_name = (TextView) view
					.findViewById(R.id.tv_goods_name);
			holder.tv_price = (TextView) view.findViewById(R.id.tv_price);
			holder.iv_goods_image = (ImageView) view
					.findViewById(R.id.iv_goods_image);
			holder.iv_returns = (ImageView) view.findViewById(R.id.iv_returns);
			holder.tv_isreturn = (TextView) view.findViewById(R.id.tv_isreturn);
			holder.tv_num = (TextView) view.findViewById(R.id.tv_num);

			view.setTag(holder);// 绑定ViewHolder对象
		} else {
			holder = (ViewHolder) view.getTag();// 取出ViewHolder对象
		}

		holder.tv_goods_name.setText(data.get(position).get("goods_name")
				.toString());

		holder.tv_price.setText(data.get(position).get("goods_price")
				.toString());

		String url = data.get(position).get("goods_image").toString();
		ImageLoader.getInstance().displayImage(url, holder.iv_goods_image,
				BeeFrameworkApp.options_car);
//		Log.e("123123", data+"");
		if (data.get(position).get("seller_state").equals("")) {
			
			if (data.get(position).get("refund_state").equals("1")) {
				holder.iv_returns.setVisibility(View.GONE);//退货申请
				holder.tv_isreturn.setVisibility(View.VISIBLE);//退款中
			} else {
				holder.iv_returns.setVisibility(View.VISIBLE);
				holder.tv_isreturn.setVisibility(View.GONE);
			}
		}else {
			
			if (data.get(position).get("seller_state").equals("1")) {//卖家审核中
				holder.iv_returns.setVisibility(View.GONE);//退货申请
				holder.tv_isreturn.setText("审核中");
				
			}else if (data.get(position).get("seller_state").equals("2")) //卖家不同意退款
			{
				holder.iv_returns.setVisibility(View.GONE);//退货申请
				holder.tv_isreturn.setText("退款成功");
			}
			else if (data.get(position).get("seller_state").equals("3")) //卖家不同意退款
			{
				holder.iv_returns.setVisibility(View.GONE);//退货申请
				holder.tv_isreturn.setText("退款失败");
			}else {
				holder.iv_returns.setVisibility(View.VISIBLE);
				holder.tv_isreturn.setVisibility(View.GONE);
			}
			
		}
		
		
			

		holder.tv_num.setText("x"
				+ data.get(position).get("goods_num").toString());

		holder.iv_returns.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String order_id = data.get(position).get("order_id").toString();
				String rec_id = data.get(position).get("rec_id").toString();
				String goods_num = data.get(position).get("goods_num")
						.toString();
				String goods_pay_price = data.get(position)
						.get("goods_pay_price").toString();
				String goods_price = data.get(position).get("goods_price")
						.toString();
				Intent it = new Intent(context, E3_Activity_Returns.class);
				String goods_name = data.get(position).get("goods_name")
						.toString();
				it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				it.putExtra("order_id", order_id);
				it.putExtra("rec_id", rec_id);
				it.putExtra("goods_num", goods_num);
				it.putExtra("goods_pay_price", goods_pay_price);
				it.putExtra("goods_price", goods_price);
				it.putExtra("goods_name", goods_name);
				context.startActivity(it);

				Log.i("order_id", order_id);
			}
		});
		return view;
	}

	/** 存放控件 */
	public final class ViewHolder {
		public TextView tv_goods_name;
		public TextView tv_price;
		public ImageView iv_goods_image;
		public ImageView iv_returns;
		public TextView tv_isreturn;
		public TextView tv_num;
	}

}
