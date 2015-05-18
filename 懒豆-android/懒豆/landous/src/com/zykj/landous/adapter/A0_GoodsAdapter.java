package com.zykj.landous.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

import com.BeeFramework.BeeFrameworkApp;
import com.BeeFramework.Utils.JsonFormatTool;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zykj.landous.LandousAppConst;
import com.zykj.landous.R;
import com.zykj.landous.activity.B2_ProductdetailsActivity;
import com.zykj.landous.adapter.E5_CoinGoodsAdapter.ViewHolder;

/**
 * @author 作者 zhang
 * @version 创建时间：2015年1月5日 下午8:12:11 类说明
 */
public class A0_GoodsAdapter extends BaseAdapter {
	private Activity context;
	private LayoutInflater listContainer;
	List<Map<String, String>> data = new ArrayList<Map<String, String>>();
	/**
	 * 每一层的标识不同
	 */
	private int[] titlebg = new int[] { R.drawable.icon_title_a0_0,
			R.drawable.icon_title_a0_1, R.drawable.icon_title_a0_2,
			R.drawable.icon_title_a0_3,

	};
	int tv_titles[] = new int[] { R.id.tv_title_food1, R.id.tv_title_food2,
			R.id.tv_title_food3 };

	// public A0_GoodsAdapter(Activity context, HomeModel homeModel) {
	// this.context = context;
	// this.homeModel = homeModel;
	// listContainer = LayoutInflater.from(context);
	//
	// }
	LinearLayout ll_goods[];

	/**
	 * 新方法
	 * 
	 * @param context
	 * @param data
	 */
	public A0_GoodsAdapter(Activity context, List<Map<String, String>> data) {
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

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub

		ViewHolder holder = null;
		if (view == null) {
			view = listContainer.inflate(R.layout.a0_homegoods_item, null);
			holder = new ViewHolder();
			holder.ll_title = (LinearLayout) view.findViewById(R.id.ll_title);
			holder.ll_null = (LinearLayout) view.findViewById(R.id.ll_null);
			holder.ll_good1 = (LinearLayout) view.findViewById(R.id.ll_good1);
			holder.ll_good2 = (LinearLayout) view.findViewById(R.id.ll_good2);
			holder.ll_good3 = (LinearLayout) view.findViewById(R.id.ll_good3);
			holder.tv_title_foods = (TextView) view
					.findViewById(R.id.tv_title_foods);

			view.setTag(holder);// 绑定ViewHolder对象
		} else {
			holder = (ViewHolder) view.getTag();// 取出ViewHolder对象
		}
		holder.ll_title.setBackgroundResource(titlebg[position % 4]);

		if (data.size() != 0) {
			int tv_prices[] = new int[] { R.id.tv_price1, R.id.tv_price2,
					R.id.tv_price3 };
			int img_goods[] = new int[] { R.id.img_good1, R.id.img_good2,
					R.id.img_good3 };

			holder.ll_good1.setOnClickListener(new Mylistener(position));

			holder.ll_good2.setOnClickListener(new Mylistener(position));

			holder.ll_good3.setOnClickListener(new Mylistener(position));

			holder.tv_title_foods.setText(data.get(position).get("gc_name")
					+ "");
			ll_goods = new LinearLayout[] { holder.ll_good1, holder.ll_good2,
					holder.ll_good3 };
			try {
				JSONObject jo = new JSONObject(data.get(position).toString());
				JSONArray array = jo.getJSONArray("goods");
				for (int i = 0; i < array.length(); i++) {
					for (int j = 0; j < 3; j++) {
						TextView text = new TextView(context);
						text = (TextView) view.findViewById(tv_titles[j]);
						text.setText(Html.fromHtml("<div>"
								+ array.getJSONObject(j)
										.getString("goods_name") + "</div>"));
						text = (TextView) view.findViewById(tv_prices[j]);
						text.setText("促销价:￥"
								+ array.getJSONObject(j).getString(
										"goods_price"));
						ImageView img = new ImageView(context);
						String url = LandousAppConst.HOME_IMG_URL
								+ array.getJSONObject(j).getString("store_id")
								+ "/"
								+ array.getJSONObject(j).getString(
										"goods_image");
						Log.i("landous", url);
						img = (ImageView) view.findViewById(img_goods[j]);
						ImageLoader.getInstance().displayImage(url, img,
								BeeFrameworkApp.options_square);
						// imageLoader.DisplayImage(url, context, img);
						ll_goods[j].setTag(array.getJSONObject(j).getString(
								"goods_id"));
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				holder.ll_null.setVisibility(View.GONE);
			}
		}

		return view;
	}

	/** 存放控件 */
	public final class ViewHolder {
		public LinearLayout ll_title;
		public LinearLayout ll_null;
		public LinearLayout ll_good1;
		public LinearLayout ll_good2;
		public LinearLayout ll_good3;
		public TextView tv_title_foods;
	}

	class Mylistener implements View.OnClickListener {
		int position;

		public Mylistener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent it = new Intent(context, B2_ProductdetailsActivity.class);
			it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			it.putExtra("goods_id", v.getTag() + "");
			if (v.getTag() != null) {
				context.startActivity(it);
			} else {
				Toast.makeText(context, "格式错误", Toast.LENGTH_LONG).show();
			}
		}

	}

}
