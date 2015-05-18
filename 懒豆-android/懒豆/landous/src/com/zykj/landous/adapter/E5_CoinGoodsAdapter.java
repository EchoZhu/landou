package com.zykj.landous.adapter;

import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.BeeFramework.BeeFrameworkApp;
import com.BeeFramework.adapter.BeeBaseAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zykj.landous.MainActivity;
import com.zykj.landous.R;
import com.zykj.landous.activity.E1_NO_PaymentAvtivity;
import com.zykj.landous.activity.E6_Activity_PointsOrder;
import com.zykj.landous.activity.E6_SigninActivity;
import com.zykj.landous.adapter.E6_Adapter_getPointsOrder.ViewHolder;

public class E5_CoinGoodsAdapter extends BeeBaseAdapter {
	public static ArrayList<Map<String, String>> dataList;
	private Activity c;
	private LayoutInflater listContainer;
	private LinearLayout ll_spcar;
	int pgoods_points = 0;
	int num = 0;
	private SharedPreferences shared;
	boolean isCanexchange = false;

	public E5_CoinGoodsAdapter(Activity c, ArrayList dataList) {
		super(c, dataList);
		this.dataList = dataList;
		this.c = c;
		listContainer = LayoutInflater.from(c);
	}

	@Override
	public View getView(int position, View cellView, ViewGroup parent) {
		ViewHolder holder = null;
		if (cellView == null) {
			cellView = listContainer.inflate(
					R.layout.e5_coin_store_good_list_item, null);
			holder = new ViewHolder();
			holder.tv_title = (TextView) cellView.findViewById(R.id.tv_title);
			holder.tv_price = (TextView) cellView.findViewById(R.id.tv_price);
			holder.tv_oldprice = (TextView) cellView
					.findViewById(R.id.tv_oldprice);
			holder.iv_goods = (ImageView) cellView.findViewById(R.id.iv_goods);
			holder.tv_num = (TextView) cellView.findViewById(R.id.tv_num);
			holder.btn_sub = (Button) cellView.findViewById(R.id.btn_sub);
			cellView.setTag(holder);// 绑定ViewHolder对象
		} else {
			holder = (ViewHolder) cellView.getTag();// 取出ViewHolder对象
		}

		pgoods_points = Integer.parseInt(dataList.get(position).get(
				"pgoods_points"));
		holder.tv_price.setText("积分:" + pgoods_points);
		holder.tv_title.setText("" + dataList.get(position).get("pgoods_name"));
		holder.tv_oldprice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

		ImageLoader.getInstance().displayImage(
				dataList.get(position).get("pgoods_image").toString(),
				holder.iv_goods, BeeFrameworkApp.options_head);

		holder.tv_num.setText("剩余数量："
				+ dataList.get(position).get("pgoods_storage"));

		holder.btn_sub.setOnClickListener(new Mylistener(position,
				pgoods_points));
		return cellView;
	}

	/** 存放控件 */
	public final class ViewHolder {
		public TextView tv_title;
		public TextView tv_price;
		public TextView tv_oldprice;
		public ImageView iv_goods;
		public TextView tv_num;
		public Button btn_sub;
	}

	@Override
	protected BeeCellHolder createCellHolder(View cellView) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected View bindData(int position, View cellView, ViewGroup parent,
			BeeCellHolder h) {
		// TODO Auto-generated method stub

		return null;
	}

	@Override
	public View createCellView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return dataList == null ? 0 : dataList.size();
	}

	class Mylistener implements View.OnClickListener {
		int position;
		int pgoods_points;

		public Mylistener(int position, int pgoods_points) {
			this.position = position;
			this.pgoods_points = pgoods_points;
		}

		@Override
		public void onClick(View arg0) {
			isLogin();
			{
				showDialog(position, pgoods_points);
			}
		}

	}

	public void showDialog(final int position, final int pgoods_points) {

		AlertDialog.Builder builder = new AlertDialog.Builder(c);
		LayoutInflater factory = LayoutInflater.from(c);
		final View view = factory
				.inflate(R.layout.e5_chose_num_conigoods, null);
		final EditText et_num = (EditText) view.findViewById(R.id.et_num);
		// builder.setIcon(R.drawable.icon_dialog);
		builder.setTitle("请输入兑换数量");
		builder.setView(view);
		et_num.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				shared = c.getSharedPreferences("loginInfo", 0);
				int member_points = Integer.parseInt(shared.getString(
						"member_points", "0"));
				num = et_num.length() == 0 ? 0 : Integer.parseInt(et_num
						.getText().toString());
				if (num * pgoods_points == 0) {
					Toast.makeText(c, "请输入兑换数量", Toast.LENGTH_LONG).show();
					isCanexchange = false;
				} else if (num * pgoods_points > member_points) {
					Toast.makeText(c, "积分不足！", Toast.LENGTH_LONG).show();
					isCanexchange = false;
					// et_num.setText((member_points/pgoods_points)+"");
				} else {
					isCanexchange = true;
				}

			}
		});
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if (isCanexchange) {
					Intent it = new Intent(c, E6_Activity_PointsOrder.class);
					it.putExtra("position", position);
					it.putExtra("count", num);
					it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					c.startActivity(it);
				}

			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		});
		builder.create().show();
	}

	public Boolean isLogin() {
		shared = c.getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
		String userID = shared.getString("member_id", "");
		Log.i("login-user-id", userID);
		if (userID.equals("")) {
			Intent it = new Intent(c, E6_SigninActivity.class);
			c.startActivity(it);
			return false;
		}
		return true;
	}

}
