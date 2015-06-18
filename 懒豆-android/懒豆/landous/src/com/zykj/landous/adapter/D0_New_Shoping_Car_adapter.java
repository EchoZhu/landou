package com.zykj.landous.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.BeeFramework.BeeFrameworkApp;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.MemoryCacheUtil;
import com.zykj.landous.LandousAppConst;
import com.zykj.landous.R;
import com.zykj.landous.Tools.HttpUtils;
import com.zykj.landous.Tools.MathTools;
import com.zykj.landous.activity.B2_ProductdetailsActivity;
import com.zykj.landous.adapter.E5_CoinGoodsAdapter.ViewHolder;
import com.zykj.landous.fragment.D0_ShopingCartFragment;

/**
 * @author 作者 zhang
 * @version 创建时间：2015年1月13日 上午10:56:26 类说明 购物车的adapter
 */
public class D0_New_Shoping_Car_adapter extends BaseAdapter {
	private Context c;
	private LayoutInflater listContainer;
	public static ArrayList<Map<String, String>> dataList;
	int num = 1;
	private ProgressDialog loadingPDialog = null;
	private onCheckedChanged listener;
	double all_price = 0;
	public static boolean b_goods[];
	public static boolean b_del[];

	public D0_New_Shoping_Car_adapter(Context c,
			ArrayList<Map<String, String>> dataList) {
		super();
		this.c = c;
		listContainer = LayoutInflater.from(c);
		this.dataList = dataList;
		loadingPDialog = new ProgressDialog(c);
		loadingPDialog.setMessage("正在加载....");
		loadingPDialog.setCancelable(false);
		b_goods = new boolean[50];
		b_del = new boolean[50];
		for (int i = 0; i < b_goods.length; i++) {
			b_goods[i] = false;
			b_del[i] = true;
		}

	}

	@Override
	public int getCount() {
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
	public View getView(final int position, View view, ViewGroup viewGroup) {
		ViewHolder holder = null;
		if (view == null) {
			view = listContainer.inflate(R.layout.d1_shoping_car_goodsitem,
					null);
			holder = new ViewHolder();
			holder.btn_choice = (Button) view.findViewById(R.id.btn_choice);
			holder.iv_goods = (ImageView) view.findViewById(R.id.iv_goods);
			holder.btn_subtract = (Button) view.findViewById(R.id.btn_subtract);
			holder.btn_add = (Button) view.findViewById(R.id.btn_add);
			holder.tv_int_num = (TextView) view.findViewById(R.id.tv_int_num);
			holder.tv_goods_num = (TextView) view
					.findViewById(R.id.tv_goods_num);
			holder.tv_goods_name = (TextView) view
					.findViewById(R.id.tv_goods_name);
			holder.tv_goods_price = (TextView) view
					.findViewById(R.id.tv_goods_price);
			holder.tv_shopname = (TextView) view.findViewById(R.id.tv_shopname);
			holder.tv_price_shop = (TextView) view
					.findViewById(R.id.tv_price_shop);
			holder.cb_choice = (CheckBox) view.findViewById(R.id.cb_choice);
			holder.ll_tail = (LinearLayout) view.findViewById(R.id.ll_tail);
			holder.rl_head = (RelativeLayout) view.findViewById(R.id.rl_head);
			holder.tv_num_shop = (TextView) view.findViewById(R.id.tv_num_shop);
			holder.btn_del = (Button) view.findViewById(R.id.btn_del);
			holder.ll_num = (LinearLayout) view.findViewById(R.id.ll_num);

			view.setTag(holder);// 绑定ViewHolder对象
		} else {
			holder = (ViewHolder) view.getTag();// 取出ViewHolder对象
		}
		int goods_num = 0;
		double goods_price = 0;

		goods_num = Integer.parseInt(dataList.get(position).get("goods_num")
				.toString());

		holder.tv_goods_price.setText("￥"
				+ dataList.get(position).get("goods_price"));
		holder.tv_goods_name.setText(""
				+ dataList.get(position).get("goods_name"));
		holder.tv_goods_num.setText("" + goods_num);
		holder.tv_int_num.setText("x" + goods_num);

		// tv_price_shop.setText("" + dataList.get(position).get("price_shop"));
		Double price_shop = Double.valueOf(dataList.get(position)
				.get("price_shop").toString());
		holder.tv_price_shop.setText(MathTools.DoubleKeepTwo(price_shop));
		holder.tv_shopname.setText(""
				+ dataList.get(position).get("store_name"));
		holder.btn_add.setOnClickListener(new CarClick(position));
		holder.btn_subtract.setOnClickListener(new CarClick(position));
		String url = dataList.get(position).get("goods_image") + "";

		if (holder.iv_goods.getTag() == null ? true : !holder.iv_goods.getTag()
				.toString().equals(url)) {
			try {
				ImageLoader.getInstance().displayImage(url, holder.iv_goods,
						BeeFrameworkApp.options_car);
			} catch (Error e) {

			}

		}
		holder.iv_goods.setTag(url);

		holder.btn_choice.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				double goods_price = 0;
				int goods_num = Integer.parseInt(dataList.get(position)
						.get("goods_num").toString());
				goods_price = Double.valueOf(dataList.get(position)
						.get("goods_price").toString());
				String cart_id = dataList.get(position).get("cart_id")
						.toString();
				if (b_goods[position]==false) {
					b_goods[position] =true;
				}else {
					b_goods[position] =false;
				}
//				b_goods[position] = !b_goods[position];
				listener.getChoiceData(goods_price * goods_num,
						b_goods[position], cart_id);
				for (int i = 0; i < dataList.size(); i++) {
					b_del[i] = true;
				}
				D0_ShopingCartFragment.btn_del.setText("编辑");
				D0_ShopingCartFragment.btn_del.setTag(0);
				notifyDataSetChanged();
			}
		});
		// cb_choice.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		//
		// @Override
		// public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		//
		// }
		// });
		holder.cb_choice.setChecked(b_goods[position]);

		String pos = dataList.get(position).get("position") + "";

		holder.tv_num_shop.setText(""
				+ dataList.get(position).get("goods_num_shop"));
		if (pos.equals("one")) {
			holder.rl_head.setVisibility(View.VISIBLE);
			holder.ll_tail.setVisibility(View.VISIBLE);
		} else {
			if (pos.equals("head")) {
				holder.rl_head.setVisibility(View.VISIBLE);
				holder.ll_tail.setVisibility(View.GONE);
			} else if (pos.equals("tail")) {
				holder.rl_head.setVisibility(View.GONE);
				holder.ll_tail.setVisibility(View.VISIBLE);
			} else if (pos.equals("body")) {
				holder.rl_head.setVisibility(View.GONE);
				holder.ll_tail.setVisibility(View.GONE);
			}
		}

		holder.btn_del.setOnClickListener(new DelClick(position));
		if (b_del[position]) {
			holder.btn_del.setVisibility(View.GONE);
			holder.ll_num.setVisibility(View.VISIBLE);
		} else {
			holder.btn_del.setVisibility(View.VISIBLE);
			holder.ll_num.setVisibility(View.GONE);
		}
		holder.iv_goods.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent it = new Intent(c, B2_ProductdetailsActivity.class);
				it.putExtra("goods_id", dataList.get(position).get("goods_id")
						.toString());
				c.startActivity(it);

			}
		});
		return view;
	}

	/** 存放控件 */
	public final class ViewHolder {
		public Button btn_choice;
		public ImageView iv_goods;
		public Button btn_subtract;
		public Button btn_add;
		public TextView tv_int_num;
		public TextView tv_goods_num;
		public TextView tv_goods_name;
		public TextView tv_goods_price;
		public TextView tv_shopname;
		public TextView tv_price_shop;
		public CheckBox cb_choice;
		public LinearLayout ll_tail;
		public RelativeLayout rl_head;
		public TextView tv_num_shop;
		public Button btn_del;
		public LinearLayout ll_num;
	}

	class DelClick implements View.OnClickListener {
		int position;

		public DelClick(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View arg0) {
			String cart_id = dataList.get(position).get("cart_id").toString();
			loadingPDialog.show();
			HttpUtils.delCart(res, cart_id);

		}

	}

	class CarClick implements View.OnClickListener {
		int position;

		public CarClick(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			for (int i = 0; i < dataList.size(); i++) {
				b_del[i] = true;
			}
			D0_ShopingCartFragment.btn_del.setText("编辑");
			D0_ShopingCartFragment.btn_del.setTag(0);
			double goods_price = 0;
			goods_price = Double.valueOf(dataList.get(position)
					.get("goods_price").toString());
			loadingPDialog.show();
			String cart_id = dataList.get(position).get("cart_id") + "";
			num = Integer.valueOf(dataList.get(position).get("goods_num")
					.toString());
			switch (v.getId()) {
			case R.id.btn_subtract:
				if (num <= 1) {
					loadingPDialog.dismiss();
					Toast.makeText(c, "最后一件商品", Toast.LENGTH_LONG).show();
				} else {
					for (int i = 0; i < b_goods.length; i++) {
						b_goods[i] = false;
					}
					if (b_goods[position]) {
						listener.getChoiceData(goods_price, false, cart_id);
						notifyDataSetChanged();
					}
					num--;
					D0_ShopingCartFragment.cb_cart_all.setChecked(false);
					D0_ShopingCartFragment.tv_cart_Allprice
							.setText("总金额:￥0.00");
					D0_ShopingCartFragment.cart_id = "";
					D0_ShopingCartFragment.all_price = 0;
					HttpUtils.updateCart(res, cart_id, num);
				}
				break;
			case R.id.btn_add:
				for (int i = 0; i < b_goods.length; i++) {
					b_goods[i] = false;
				}
				D0_ShopingCartFragment.cb_cart_all.setChecked(false);
				D0_ShopingCartFragment.tv_cart_Allprice.setText("总金额:￥0.00");
				D0_ShopingCartFragment.cart_id = "";
				D0_ShopingCartFragment.all_price = 0;
				if (b_goods[position]) {
					listener.getChoiceData(goods_price, true, cart_id);
					notifyDataSetChanged();
				}
				num++;
				HttpUtils.updateCart(res, cart_id, num);
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
			loadingPDialog.dismiss();
			if (result == 1 && statusCode == 200) {

			} else if (message.equals("No permission to do this!")) {
				// Intent it = new Intent(context, E6_SigninActivity.class);
				// ((Activity) context).overridePendingTransition(
				// R.anim.push_up_in, R.anim.push_up_out);
				// context.startActivity(it);
			} else if (result == 6793) {
				Toast.makeText(c, "商品库存不足", Toast.LENGTH_LONG).show();
			}
			HttpUtils.getCartList(res_getCar, "");
			// Intent it = new Intent(context, MyDialogActivity.class);
			// context.startActivity(it);
		}

		@Override
		public void onFailure(int statusCode, Header[] headers,
				Throwable throwable, JSONObject errorResponse) {
			// TODO Auto-generated method stub
			loadingPDialog.dismiss();
			Toast.makeText(c, "网络连接超时", Toast.LENGTH_LONG).show();
			super.onFailure(statusCode, headers, throwable, errorResponse);
		}
	};
	JsonHttpResponseHandler res_getCar = new JsonHttpResponseHandler() {

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
				dataList.clear();
				try {
					JSONArray array = response.getJSONArray("list");
					double purchase_price = 0;
					for (int i = 0; i < array.length(); i++) {
						JSONObject jsonItem = array.getJSONObject(i);
						Map<String, String> map = new HashMap();
						map.put("store_id", jsonItem.getString("store_id"));
						map.put("store_name", jsonItem.getString("store_name"));
						String cart_list = jsonItem.getString("cart_list");
						map.put("purchase_price",
								jsonItem.getString("purchase_price"));
						purchase_price += Double.parseDouble(jsonItem
								.getString("purchase_price"));
						JSONArray car_array = new JSONArray(cart_list);
						int goods_num_shop = 0;
						for (int j = 0; j < car_array.length(); j++) {
							JSONObject json = car_array.getJSONObject(j);
							Map<String, String> carmap = new HashMap();
							carmap.put("goods_id", json.getString("goods_id"));
							carmap.put("goods_name",
									json.getString("goods_name"));
							carmap.put("goods_price",
									json.getString("goods_price"));
							carmap.put("goods_num", json.getString("goods_num"));
							goods_num_shop += json.getInt("goods_num");
							carmap.put(
									"goods_image",
									LandousAppConst.HOME_IMG_URL
											+ json.getString("store_id") + "/"
											+ json.getString("goods_image"));
							carmap.put("cart_id", json.getString("cart_id"));
							carmap.put("store_name",
									json.getString("store_name"));
							if (car_array.length() == 1) {
								carmap.put("position", "one");
							} else {
								if (j == 0) {
									carmap.put("position", "head");
								} else if (j == car_array.length() - 1) {
									carmap.put("position", "tail");
								} else {
									carmap.put("position", "body");
								}
							}
							carmap.put("price_shop",
									jsonItem.getString("purchase_price"));
							carmap.put("purchase_price", purchase_price + "");
							carmap.put("goods_num_shop", goods_num_shop + "");
							dataList.add(carmap);
						}

					}
					D0_ShopingCartFragment.shop_car_data
							.setVisibility(View.VISIBLE);
					D0_ShopingCartFragment.shop_car_null
							.setVisibility(View.GONE);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					D0_ShopingCartFragment.shop_car_data
							.setVisibility(View.GONE);
					D0_ShopingCartFragment.shop_car_null
							.setVisibility(View.VISIBLE);
					e.printStackTrace();
				}
				all_price = 0;
				notifyDataSetChanged();
				loadingPDialog.dismiss();
			} else if (message.equals("No permission to do this!")) {
				// Intent it = new Intent(context, E6_SigninActivity.class);
				// ((Activity) context).overridePendingTransition(
				// R.anim.push_up_in, R.anim.push_up_out);
				// context.startActivity(it);
			}

			// Intent it = new Intent(context, MyDialogActivity.class);
			// context.startActivity(it);
		}

		@Override
		public void onFailure(int statusCode, Header[] headers,
				Throwable throwable, JSONObject errorResponse) {
			// TODO Auto-generated method stub
			loadingPDialog.dismiss();
			Toast.makeText(c, "网络连接超时", Toast.LENGTH_LONG).show();
			super.onFailure(statusCode, headers, throwable, errorResponse);
		}
	};

	public interface onCheckedChanged {

		public void getChoiceData(double price, boolean isChoice, String cart_id);

	}

	public void setOnCheckedChanged(onCheckedChanged listener) {
		this.listener = listener;
	}

}
