package com.zykj.landous.adapter;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
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
import com.zykj.landous.activity.C1_ShopActivity;
import com.zykj.landous.adapter.B2_0_PriductCommentAdapter.ViewHolder;

public class C0_ShopsAdapter extends BeeBaseAdapter {
	private ArrayList<Map<String, String>> dataList;
	private Context c;
	private LayoutInflater listContainer;
	private LinearLayout ll_spcar;

	public C0_ShopsAdapter(Context c, ArrayList<Map<String, String>> dataList) {
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
		return dataList.size();
	}

	@Override
	public View createCellView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getView(final int position, View cellView, ViewGroup parent) {
		ViewHolder holder = null;
		if (cellView == null) {
			cellView = listContainer.inflate(R.layout.c0_shopsitem, null);
			holder = new ViewHolder();
			 /**得到各个控件的对象*/  
			holder.tv_shopname = (TextView) cellView.findViewById(R.id.tv_shopname);
			holder.tv_major = (TextView) cellView.findViewById(R.id.tv_major);
			holder.iv_shops = (ImageView) cellView.findViewById(R.id.iv_shops);
			cellView.setTag(holder);//绑定ViewHolder对象
		}
        else{
            holder = (ViewHolder)cellView.getTag();//取出ViewHolder对象                  
        }
		cellView.setOnClickListener(new Mylistener(position));
		holder.tv_shopname.setText(dataList.get(position).get("store_name") + "");
		holder.tv_major.setText("主营项目:" + dataList.get(position).get("store_zy"));
		String url = dataList.get(position).get("store_label");
		ImageLoader.getInstance().displayImage(url, holder.iv_shops,
				BeeFrameworkApp.options);
		return cellView;

	}
	
	/**存放控件*/
	public final class ViewHolder{
		public TextView tv_shopname; 
		public TextView tv_major;
		public ImageView iv_shops;
	}

	class Mylistener implements View.OnClickListener {
		int position;

		public Mylistener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View arg0) {
			Intent it = new Intent(c.getApplicationContext(),
					C1_ShopActivity.class);
			it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			String store_id = dataList.get(position).get("store_id") + "";
			String store_zy = dataList.get(position).get("store_zy") + "";
			String store_label = dataList.get(position).get("store_label") + "";
			String store_name = dataList.get(position).get("store_name") + "";
			String str[] = new String[] { store_id, store_zy, store_label,
					store_name };
			it.putExtra("store", str);
			c.startActivity(it);
		}

	}

}
