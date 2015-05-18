package com.zykj.landous.adapter;

import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.BeeFramework.BeeFrameworkApp;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zykj.landous.R;
import com.zykj.landous.activity.E2_Activity_orderEvaluation;
import com.zykj.landous.adapter.E5_CoinGoodsAdapter.ViewHolder;

/**
 * @author 作者 zhang
 * @version 创建时间：2015年1月25日 下午4:45:28 类说明 订单评价的
 */
public class E2_Adapter_orderEvaluation extends BaseAdapter {
	private Activity context;
	ArrayList<Map<String, String>> data;
	private LayoutInflater listContainer;

	public E2_Adapter_orderEvaluation(Activity context,
			ArrayList<Map<String, String>> data) {
		this.context = context;
		this.data = data;
		listContainer = LayoutInflater.from(context);

	}

	@Override
	public int getCount() {

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
			view = listContainer
					.inflate(R.layout.e2_item_orderevaluation, null);
			holder = new ViewHolder();
			holder.tv_goodsname = (TextView) view
					.findViewById(R.id.tv_goodsname);
			holder.tv_price = (TextView) view.findViewById(R.id.tv_price);
			holder.iv_goods_image = (ImageView) view
					.findViewById(R.id.iv_goods_image);
			holder.ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
			holder.et_comment = (EditText) view.findViewById(R.id.et_comment);

			view.setTag(holder);// 绑定ViewHolder对象
		} else {
			holder = (ViewHolder) view.getTag();// 取出ViewHolder对象
		}

		holder.tv_goodsname.setText(data.get(position).get("goods_name")
				.toString());

		holder.tv_price.setText(data.get(position).get("goods_price")
				.toString());

		String url = data.get(position).get("goods_image").toString();
		ImageLoader.getInstance().displayImage(url, holder.iv_goods_image,
				BeeFrameworkApp.options_car);

		holder.ratingBar
				.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

					@Override
					public void onRatingChanged(RatingBar arg0, float f,
							boolean arg2) {
						E2_Activity_orderEvaluation.fStart[position] = f;
					}
				});

		holder.et_comment.addTextChangedListener(new TextWatcher() {

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
			public void afterTextChanged(Editable str) {
				// TODO Auto-generated method stub
				E2_Activity_orderEvaluation.str_comment[position] = str
						.toString();
			}
		});
		return view;
	}

	/** 存放控件 */
	public final class ViewHolder {
		public TextView tv_goodsname;
		public TextView tv_price;
		public ImageView iv_goods_image;
		public RatingBar ratingBar;
		public EditText et_comment;
	}

}
