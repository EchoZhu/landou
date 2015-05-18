package com.zykj.landous.adapter;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONException;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.zykj.landous.R;
import com.zykj.landous.Tools.HttpUtils;
import com.zykj.landous.activity.B2_ProductdetailsActivity;
import com.zykj.landous.activity.E2_AddressManageActivity;
import com.zykj.landous.activity.E4_ModifyAddressActivity;
import com.zykj.landous.activity.E6_Activity_PointsOrder;
import com.zykj.landous.adapter.E5_CoinGoodsAdapter.ViewHolder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class E2_AddressManagerAdapter extends BaseAdapter {
	private ArrayList<Map<String, String>> dataList;
	private Activity c;
	private LayoutInflater listContainer;
	Intent it;

	public E2_AddressManagerAdapter(Activity c, ArrayList dataList) {
		super();
		this.dataList = dataList;
		this.c = c;
		listContainer = LayoutInflater.from(c);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.dataList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public View getView(final int position, View cellView, ViewGroup parent) {
		ViewHolder holder = null;
		if (cellView == null) {
			cellView = listContainer.inflate(R.layout.e2_address_list_item,
					null);
			holder = new ViewHolder();
			holder.ll_addr = (LinearLayout) cellView.findViewById(R.id.ll_addr);
			holder.edit = (Button) cellView.findViewById(R.id.edit_address);
			holder.del = (Button) cellView.findViewById(R.id.del_address);
			holder.name = (TextView) cellView.findViewById(R.id.addr_name);
			holder.phone = (TextView) cellView.findViewById(R.id.addr_phone);
			holder.region = (TextView) cellView.findViewById(R.id.addr_region);
			holder.ll_chose_address = (LinearLayout) cellView
					.findViewById(R.id.ll_chose_address);
			cellView.setTag(holder);// 绑定ViewHolder对象
		} else {
			holder = (ViewHolder) cellView.getTag();// 取出ViewHolder对象
		}

		holder.edit.setOnClickListener(new Mylistener_edit(position));
		holder.del.setOnClickListener(new Mylistener_del(position));

		holder.name.setText(dataList.get(position).get("name"));
		holder.phone.setText(dataList.get(position).get("phone"));
		holder.region.setText(dataList.get(position).get("region"));

		if (dataList.get(position).get("is_default").toString().equals("1")) {
			holder.ll_addr.setBackgroundColor(0xfffffff0);

		}
		holder.ll_chose_address.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String address_id = dataList.get(position).get("address_id");
				dialog(address_id, position);
			}
		});
		return cellView;
	}

	/** 存放控件 */
	public final class ViewHolder {
		public LinearLayout ll_addr;
		public Button edit;
		public Button del;
		public TextView name;
		public TextView phone;
		public TextView region;
		public LinearLayout ll_chose_address;
	}

	class Mylistener_edit implements View.OnClickListener {
		int position;

		public Mylistener_edit(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			String address_id = dataList.get(position).get("address_id");
			Log.i("addr_edit", position + "-----" + address_id);
			Intent it = new Intent(c, E4_ModifyAddressActivity.class);
			it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			it.putExtra("address_id", dataList.get(position).get("address_id"));
			it.putExtra("address", dataList.get(position).get("address"));
			it.putExtra("area_id", dataList.get(position).get("area_id"));
			it.putExtra("true_name", dataList.get(position).get("name"));
			it.putExtra("mob_phone", dataList.get(position).get("phone"));
			c.startActivity(it);
		}

	}

	class Mylistener_del implements View.OnClickListener {
		int position;

		public Mylistener_del(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			final String address_id = dataList.get(position).get("address_id");
			Log.i("addr_del", position + "--------" + address_id);
			AlertDialog.Builder builder = new Builder(c);
			builder.setMessage("确认删除此收货地址");

			builder.setTitle("提示");

			builder.setPositiveButton("确认", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Log.i("before-del", "1");
					dialog.dismiss();
					HttpUtils.delAddress(res, address_id);
				}
			});

			builder.setNegativeButton("取消", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			builder.create().show();

		}

		JsonHttpResponseHandler res = new JsonHttpResponseHandler() {
			public void onSuccess(int statusCode,
					org.apache.http.Header[] headers,
					org.json.JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				Log.i("addr_del", response.toString());
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
					AlertDialog.Builder builder = new Builder(c);
					builder.setMessage("删除成功");

					builder.setTitle("提示");

					builder.setPositiveButton("确认", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dataList.remove(position);
							if (dataList.size() == 0) {
								E2_AddressManageActivity.data_null
										.setVisibility(View.VISIBLE);
							}
							notifyDataSetChanged();
							dialog.dismiss();
						}
					});
					builder.create().show();

				}

			};

			public void onFailure(int statusCode,
					org.apache.http.Header[] headers, Throwable throwable,
					org.json.JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
				Log.i("addr_del", errorResponse.toString());
			};
		};
	}

	protected void dialog(final String address_id, final int position) {
		AlertDialog.Builder builder = new Builder(c);
		builder.setMessage("选择这个地址为收货地址吗？");
		builder.setTitle("地址选择");
		builder.setPositiveButton("确认", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				it = new Intent();
				it.putExtra("address_id",
						dataList.get(position).get("address_id"));
				it.putExtra("address", dataList.get(position).get("region"));
				it.putExtra("area_id", dataList.get(position).get("area_id"));
				it.putExtra("true_name", dataList.get(position).get("name"));
				it.putExtra("mob_phone", dataList.get(position).get("phone"));
				it.putExtra("address_id", address_id);
				c.setResult(200, it);
				HttpUtils.setDefaultAddress(res_setDefaultAddress, address_id);

			}
		});
		builder.setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	JsonHttpResponseHandler res_setDefaultAddress = new JsonHttpResponseHandler() {
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
				AlertDialog.Builder builder = new Builder(c);
				builder.setMessage("设置成功");
				builder.setTitle("提示");

				builder.setPositiveButton("确认", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						c.finish();
						dialog.dismiss();
					}
				});
				try {
					builder.create().show();
				} catch (Error e) {

				}

			}

		};

		public void onFailure(int statusCode, org.apache.http.Header[] headers,
				Throwable throwable, org.json.JSONObject errorResponse) {
			super.onFailure(statusCode, headers, throwable, errorResponse);
			Log.i("addr_del", errorResponse.toString());
		}
	};

}
