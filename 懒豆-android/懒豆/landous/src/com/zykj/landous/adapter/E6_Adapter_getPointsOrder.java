package com.zykj.landous.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.BeeFramework.BeeFrameworkApp;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zykj.landous.R;
import com.zykj.landous.Tools.HttpUtils;
import com.zykj.landous.activity.E6_Activity_PointsOrder;
import com.zykj.landous.adapter.B2_0_PriductCommentAdapter.ViewHolder;
import com.zykj.landous.alipay.Fiap;

/**
 * @author 作者 zhang
 * @version 创建时间：2015年1月22日 上午12:27:09 类说明
 */
public class E6_Adapter_getPointsOrder extends BaseAdapter {
	List<Map<String, String>> data = new ArrayList<Map<String, String>>();
	private Activity context;
	private LayoutInflater listContainer;
	LinearLayout ll_gone;
	private ProgressDialog loadingPDialog = null;

	public E6_Adapter_getPointsOrder(Activity context,
			List<Map<String, String>> data) {
		this.context = context;
		this.data = data;
		listContainer = LayoutInflater.from(context);
		loadingPDialog = new ProgressDialog(context);
		loadingPDialog.setMessage("正在加载....");
		loadingPDialog.setCancelable(false);
	}

	@Override
	public int getCount() {

		return data == null ? 0 : data.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		ViewHolder holder = null;
		if (view==null) {
			view = listContainer.inflate(R.layout.e6_item_getpointsorder, null);
			holder = new ViewHolder();
			holder.tv_goods_name = (TextView) view
					.findViewById(R.id.tv_goods_name);
			holder.tv_shdh = (TextView) view.findViewById(R.id.tv_shdh);
			holder.tv_price = (TextView) view.findViewById(R.id.tv_price);
			holder.tv_num = (TextView) view.findViewById(R.id.tv_num);
			holder.tv_all_price = (TextView) view.findViewById(R.id.tv_all_price);
			holder.iv_goods_image = (ImageView) view
					.findViewById(R.id.iv_goods_image);
			holder.iv_topay = (ImageView) view.findViewById(R.id.iv_topay);
			holder.iv_cancel = (ImageView) view.findViewById(R.id.iv_cancel);
			holder.tv_type = (TextView) view.findViewById(R.id.tv_type);
			holder.rl_receipt = (RelativeLayout) view
					.findViewById(R.id.rl_receipt);// 已发货
			holder.iv_receipt = (ImageView) view.findViewById(R.id.iv_receipt);
			holder.rl_topay = (RelativeLayout) view
					.findViewById(R.id.rl_topay);
			view.setTag(holder);//绑定ViewHolder对象
		}
        else{
            holder = (ViewHolder)view.getTag();//取出ViewHolder对象                  
        }
		holder.tv_goods_name.setText(data.get(position).get("point_goodsname") + "");
		holder.tv_shdh.setText("兑换单号："+data.get(position).get("point_ordersn") + "");	
		int point_goodspoints = Integer.parseInt(data.get(position)
				.get("point_goodspoints").toString());
		holder.tv_price.setText(point_goodspoints + "积分");
		int num = Integer.parseInt(data.get(position).get("point_goodsnum"));
		holder.tv_num.setText("x" + num);
		holder.tv_all_price.setText("合计：" + num * point_goodspoints + "积分");
		ImageLoader.getInstance().displayImage(
				data.get(position).get("point_goodsimage").toString(),
				holder.iv_goods_image, BeeFrameworkApp.options_car);
		String point_orderstate = data.get(position).get("point_orderstate")
				.toString();
		
		if (point_orderstate.equals("10")) {// 10 未付款
			holder.tv_type.setVisibility(View.GONE);
			holder.iv_topay.setVisibility(View.VISIBLE);
			holder.iv_cancel.setVisibility(View.VISIBLE);
			holder.rl_receipt.setVisibility(View.GONE);
		} else if (point_orderstate.equals("20")) {// 20.待发货
			holder.tv_type.setVisibility(View.VISIBLE);
			holder.iv_topay.setVisibility(View.GONE);
			holder.iv_cancel.setVisibility(View.GONE);
			holder.rl_receipt.setVisibility(View.GONE);
		} else if (point_orderstate.equals("30")) {// 30 已发货
			holder.tv_type.setVisibility(View.VISIBLE);
			holder.tv_type.setText("已发货");
			holder.iv_topay.setVisibility(View.GONE);
			holder.iv_cancel.setVisibility(View.GONE);
			holder.rl_receipt.setVisibility(View.VISIBLE);
			holder.rl_topay.setVisibility(View.GONE);
		} else if (point_orderstate.equals("40")) {// 40 已发货
			holder.tv_type.setVisibility(View.VISIBLE);
			holder.tv_type.setText("已收货");
			holder.iv_topay.setVisibility(View.GONE);
			holder.iv_cancel.setVisibility(View.GONE);
			holder.rl_receipt.setVisibility(View.GONE);
		} else {
			holder.tv_type.setVisibility(View.VISIBLE);
			holder.tv_type.setText("已取消");
			holder.iv_topay.setVisibility(View.GONE);
			holder.iv_cancel.setVisibility(View.GONE);
			holder.rl_receipt.setVisibility(View.GONE);
		}
		
		holder.iv_receipt.setOnClickListener(new MyClick(position));
		holder.iv_cancel.setOnClickListener(new MyClick(position));
		holder.iv_topay.setOnClickListener(new MyClick(position));
		return view;
	}
	
	/**存放控件*/
	public final class ViewHolder{
		public TextView tv_goods_name;
		public TextView tv_shdh;
		public TextView tv_price;
		public TextView tv_num;
		public TextView tv_all_price;
		public ImageView iv_goods_image;
		public ImageView iv_topay;
		public ImageView iv_cancel;
		public TextView tv_type;
		public RelativeLayout rl_receipt;
		public ImageView iv_receipt;
		public RelativeLayout rl_topay;
	}

	class MyClick implements View.OnClickListener {
		int position;

		public MyClick(int position) {
			this.position = position;

		}

		@Override
		public void onClick(View v) {
//			loadingPDialog.show();
			switch (v.getId()) {
			case R.id.iv_cancel:
				String point_orderid = data.get(position).get("point_orderid")
						.toString();
				HttpUtils.cancelPointsOrder(res, point_orderid);
				break;
			case R.id.iv_receipt:
				point_orderid = data.get(position).get("point_orderid")
						.toString();
				HttpUtils.receivePointsOrder(res, point_orderid);
				break;
			case R.id.iv_topay:
				point_orderid = data.get(position).get("point_orderid")
						.toString();
				Fiap f = new Fiap(context);
				f.setOrder_id(data.get(position).get("point_ordersn")
						.toString());
				f.setPoint_orderid(point_orderid);
				f.android_pay(5.0);
				break;
			default:
				break;
			}

		}

	}

	JsonHttpResponseHandler res = new JsonHttpResponseHandler() {
		public void onSuccess(int statusCode, org.apache.http.Header[] headers,
				org.json.JSONObject response) {
			super.onSuccess(statusCode, headers, response);
			int result = 0;

			try {
				result = Integer.valueOf(response.getString("result"));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (result == 1 && statusCode == 200) {
				HttpUtils.getPointsOrder(res_updata, "1", "30");
			}

		};

		public void onFailure(int statusCode, org.apache.http.Header[] headers,
				Throwable throwable, org.json.JSONObject errorResponse) {
			super.onFailure(statusCode, headers, throwable, errorResponse);
			loadingPDialog.dismiss();
		};
	};

	JsonHttpResponseHandler res_updata = new JsonHttpResponseHandler() {
		public void onSuccess(int statusCode, org.apache.http.Header[] headers,
				org.json.JSONObject response) {
			super.onSuccess(statusCode, headers, response);
			int result = 0;

			try {
				result = Integer.valueOf(response.getString("result"));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (result == 1 && statusCode == 200) {
				data.clear();
				try {
					JSONArray array = response.getJSONArray("list");
					for (int i = 0; i < array.length(); i++) {
						JSONObject jsonItem = array.getJSONObject(i);
						Map<String, String> map = new HashMap<String, String>();
						map.put("point_ordersn",
								jsonItem.getString("point_ordersn"));
						JSONArray goods_array = jsonItem
								.getJSONArray("goods_list");
						JSONObject json = goods_array.getJSONObject(0);
						map.put("point_goodsname",
								json.getString("point_goodsname"));
						map.put("point_goodspoints",
								json.getString("point_goodspoints"));
						map.put("point_orderstate",
								jsonItem.getString("point_orderstate"));
						map.put("point_goodsnum",
								json.getString("point_goodsnum"));
						map.put("point_goodsimage",
								"http://112.53.78.18:8088/data/upload/shop/pointprod/"
										+ json.getString("point_goodsimage"));
						map.put("point_orderid",
								json.getString("point_orderid"));
						data.add(map);
					}
					loadingPDialog.dismiss();
					notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		};

		public void onFailure(int statusCode, org.apache.http.Header[] headers,
				Throwable throwable, org.json.JSONObject errorResponse) {
			super.onFailure(statusCode, headers, throwable, errorResponse);
			loadingPDialog.dismiss();
		};
	};
}
