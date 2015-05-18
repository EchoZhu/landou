package com.zykj.landous.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.BeeFramework.BeeFrameworkApp;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zykj.landous.R;
import com.zykj.landous.Tools.HttpUtils;
import com.zykj.landous.Tools.MathTools;
import com.zykj.landous.activity.E6_SigninActivity;
import com.zykj.landous.activity.MyDialogActivity;
import com.zykj.landous.adapter.E5_CoinGoodsAdapter.ViewHolder;

/**
 * @author 作者 zhang
 * @version 创建时间：2015年1月10日 上午12:50:19 类说明
 */
public class D1_shopping_car_goods_adapter extends BaseAdapter {
	private LayoutInflater listContainer;
	private Context context;
	List<Map<String, String>> data = new ArrayList<Map<String, String>>();
	double allPrice = 0;
	private ProgressDialog loadingPDialog = null;
	private SharedPreferences shared;
	private SharedPreferences.Editor editor;

	public D1_shopping_car_goods_adapter(Context c,
			List<Map<String, String>> data) {
		this.context = c;
		this.data = data;
		listContainer = LayoutInflater.from(c);
		loadingPDialog = new ProgressDialog(c);
		loadingPDialog.setMessage("正在加载....");
		loadingPDialog.setCancelable(false);
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
	public View getView(int position, View view, ViewGroup arg2) {
		View footerview = LayoutInflater.from(context).inflate(
				R.layout.d0_shopingcar_footerview, null);
		final TextView tv_allPriceTextView = (TextView) footerview
				.findViewById(R.id.tv_allPrice);

		ViewHolder holder = null;
		if (view == null) {
			view = listContainer.inflate(R.layout.d1_shoping_car_goodsitem,
					null);
			holder = new ViewHolder();
			holder.ll_allprice = (LinearLayout) view
					.findViewById(R.id.ll_allprice);
			holder.btn_subtract = (Button) view.findViewById(R.id.btn_subtract);
			holder.btn_add = (Button) view.findViewById(R.id.btn_add);
			holder.checkBox = (CheckBox) view.findViewById(R.id.checkBox);
			holder.tv_goods_name = (TextView) view
					.findViewById(R.id.tv_goods_name);
			holder.tv_goods_price = (TextView) view
					.findViewById(R.id.tv_goods_price);
			holder.tv_goods_num = (TextView) view
					.findViewById(R.id.tv_goods_num);
			holder.iv_goods = (ImageView) view.findViewById(R.id.iv_goods);
			holder.tv_shop_goods_num = (TextView) view
					.findViewById(R.id.tv_shop_goods_num);
			holder.tv_shop_goods_price = (TextView) view
					.findViewById(R.id.tv_shop_goods_price);
			view.setTag(holder);// 绑定ViewHolder对象
		} else {
			holder = (ViewHolder) view.getTag();// 取出ViewHolder对象
		}

		holder.btn_add.setTag(R.id.tag_first, data.get(position).get("cart_id")
				+ "");
		holder.btn_subtract.setTag(R.id.tag_first,
				data.get(position).get("cart_id") + "");
		final String cart_id = data.get(position).get("cart_id") + "";
		shared = context.getSharedPreferences("carInfo", 0);
		editor = shared.edit();
		editor.putBoolean(cart_id, false);
		editor.commit();

		holder.btn_subtract.setOnClickListener(new CarClick(position));
		holder.btn_add.setOnClickListener(new CarClick(position));

		holder.tv_goods_name.setText(data.get(position).get("goods_name")
				.toString());

		final double goods_price = Double.parseDouble(data.get(position).get(
				"goods_price"));
		holder.tv_goods_price.setText("￥"
				+ MathTools.DoubleKeepTwo(goods_price));

		final int num = Integer.valueOf(data.get(position).get("goods_num")
				.toString());
		holder.btn_subtract.setTag(R.id.tag_second, num);
		holder.btn_add.setTag(R.id.tag_second, num);
		holder.tv_goods_num.setText(data.get(position).get("goods_num")
				.toString());
		allPrice += goods_price * num;
		// goods_image
		String url = data.get(position).get("goods_image") + "";

		ImageLoader.getInstance().displayImage(url, holder.iv_goods,
				BeeFrameworkApp.options_head);

		holder.tv_shop_goods_num.setText("共" + data.size() + "件商品");

		holder.tv_shop_goods_price.setText("共计:￥"
				+ MathTools.DoubleKeepTwo(allPrice));
		holder.checkBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0,
							boolean isChecked) {
						shared = context.getSharedPreferences("carInfo", 0);
						editor = shared.edit();
						Float all_Price = null;
						if (isChecked) {
							all_Price = shared.getFloat("allPrice", 0);
							editor.putBoolean(cart_id, true);
							all_Price += Float.valueOf(goods_price * num + "");
							editor.putFloat("allPrice", all_Price);
							editor.commit();
						} else {
							all_Price = shared.getFloat("allPrice", 0);
							editor.putBoolean(cart_id, false);
							all_Price -= Float.valueOf(goods_price * num + "");
							editor.putFloat("allPrice", all_Price);
							editor.commit();
						}
						tv_allPriceTextView.setText("" + allPrice);
						Toast.makeText(context, allPrice + "",
								Toast.LENGTH_LONG).show();
					}
				});
		if ((position + 1) == data.size()) {
			holder.ll_allprice.setVisibility(View.VISIBLE);
			allPrice = 0;
		}

		return view;
	}

	/** 存放控件 */
	public final class ViewHolder {
		public LinearLayout ll_allprice;
		public Button btn_subtract;
		public Button btn_add;
		public CheckBox checkBox;
		public TextView tv_goods_name;
		public TextView tv_goods_price;
		public TextView tv_goods_num;
		public ImageView iv_goods;
		public TextView tv_shop_goods_num;
		public TextView tv_shop_goods_price;
	}

	class CarClick implements View.OnClickListener {
		int position;

		public CarClick(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			int count = 0;
			String num = v.getTag(R.id.tag_second).toString();
			String cart_id = v.getTag(R.id.tag_first).toString();
			shared = context.getSharedPreferences("carInfo",
					Activity.MODE_PRIVATE);

			switch (v.getId()) {
			case R.id.btn_subtract:

				count = Integer.parseInt(num);
				if (count <= 1) {
					Toast.makeText(context, shared.getAll() + "" + num,
							Toast.LENGTH_LONG).show();
					Log.i("landous", shared.getAll() + "");
				} else {
					count--;
					HttpUtils.updateCart(res, cart_id, count);
				}

				break;
			case R.id.btn_add:
				count = Integer.parseInt(num);
				count++;
				HttpUtils.updateCart(res, cart_id, count);
				break;
			default:
				break;
			}

		}

	}

	JsonHttpResponseHandler res = new JsonHttpResponseHandler() {

		@Override
		public void onSuccess(int statusCode, Header[] headers,
				JSONObject response) {
			super.onSuccess(statusCode, headers, response);
			int result = 0;
			String message = "";

			try {
				result = Integer.valueOf(response.getString("result"));
				message = response.getString("message");
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (result == 1 && statusCode == 200) {

			} else if (message.equals("No permission to do this!")) {
				Intent it = new Intent(context, E6_SigninActivity.class);
				((Activity) context).overridePendingTransition(
						R.anim.push_up_in, R.anim.push_up_out);
				context.startActivity(it);
			}

			Intent it = new Intent(context, MyDialogActivity.class);
			context.startActivity(it);
		}

		@Override
		public void onFailure(int statusCode, Header[] headers,
				Throwable throwable, JSONObject errorResponse) {
			// TODO Auto-generated method stub
			// loadingPDialog.dismiss();
			Toast.makeText(context, "网络连接超时", Toast.LENGTH_LONG).show();
			super.onFailure(statusCode, headers, throwable, errorResponse);
		}
	};
}
