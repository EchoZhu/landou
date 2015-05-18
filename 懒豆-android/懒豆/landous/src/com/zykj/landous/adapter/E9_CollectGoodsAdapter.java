package com.zykj.landous.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

import com.BeeFramework.BeeFrameworkApp;
import com.BeeFramework.Utils.JsonFormatTool;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zykj.landous.LandousAppConst;
import com.zykj.landous.R;
import com.zykj.landous.activity.B2_ProductdetailsActivity;
import com.zykj.landous.activity.CollectActivity;
import com.zykj.landous.adapter.B2_0_PriductCommentAdapter.ViewHolder;

/**
 * @author 作者 zhang
 * @version 创建时间：2015年1月5日 下午8:12:11 类说明
 */
public class E9_CollectGoodsAdapter extends BaseAdapter {
	private Activity context;
	private LayoutInflater listContainer;
	List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();




	/**
	 * 新方法
	 * 
	 * @param context
	 * @param data
	 */
	public E9_CollectGoodsAdapter(Activity context, List<HashMap<String, String>> data) {
		this.context = context;
		this.data = data;
		listContainer = LayoutInflater.from(context);

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
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
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = listContainer.inflate(R.layout.e9_goods_collect_grid_item, null);
			holder = new ViewHolder();
			 /**得到各个控件的对象*/  
			holder.title = (TextView)convertView.findViewById(R.id.tv_ad_3_name);
			holder.good_price = (TextView)convertView.findViewById(R.id.tv_ad3_price);
			holder.sale = (TextView)convertView.findViewById(R.id.tv_ad3_num);
			holder.goods_image = (ImageView)convertView.findViewById(R.id.ad_3);
			convertView.setTag(holder);//绑定ViewHolder对象
		}
        else{
            holder = (ViewHolder)convertView.getTag();//取出ViewHolder对象                  
        }
		holder.title.setText(data.get(position).get("goods_name"));
		holder.good_price.setText(data.get(position).get("goods_price"));
		holder.sale.setText(data.get(position).get("goods_salenum")+"人购买");
//		convertView.setTag(data.get(position).get("goods_id"));
//		Log.i("adapter",data.get(position).get("goods_name"));
		
		String url=data.get(position).get("goods_image");
		Log.i("img_url", url+"");
		ImageLoader.getInstance().displayImage(url, holder.goods_image,
				BeeFrameworkApp.options_head);
		return convertView;
	}
	
	/**存放控件*/
	public final class ViewHolder{
		public TextView title;
		public TextView good_price;
		public TextView sale;
		public ImageView goods_image;
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
