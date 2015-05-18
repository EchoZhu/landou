package com.zykj.landous.adapter;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.BeeFramework.adapter.BeeBaseAdapter;
import com.zykj.landous.R;
import com.zykj.landous.Tools.Image.CircularImage;
import com.zykj.landous.adapter.B1_GoodsAdapter.Mylistener;

/**
 * @author 作者 zhang
 * @version 创建时间：2015年1月4日 上午9:50:05 类说明
 */
public class B2_0_PriductCommentAdapter extends BeeBaseAdapter {
	private ArrayList<Map<String, String>> dataList;
	private Context c;
	private LayoutInflater listContainer;
	private ImageView iv_head;

	public B2_0_PriductCommentAdapter(Context c, ArrayList dataList) {
		super(c, dataList);
		// TODO Auto-generated constructor stub
		this.dataList = dataList;
		this.c = c;
		listContainer = LayoutInflater.from(c);
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return dataList == null ? 0 : dataList.size();
	}

	@Override
	public View getView(final int position, View cellView, ViewGroup parent) {
		ViewHolder holder = null;
        //观察convertView随ListView滚动情况            
        Log.v("MyListViewBase", "getView " + position + " " + cellView);
		if (cellView==null) {
			cellView = listContainer
					.inflate(R.layout.b2_0_priductcommentitem, null);
			holder = new ViewHolder();
			 /**得到各个控件的对象*/  
			holder.tv_geval_addtime = (TextView) cellView.findViewById(R.id.tv_geval_addtime);
			holder.tv_content = (TextView) cellView.findViewById(R.id.tv_content);
			holder.tv_frommembername = (TextView) cellView.findViewById(R.id.tv_frommembername); 
			holder.ratingBar = (RatingBar) cellView.findViewById(R.id.ratingBar);
			cellView.setTag(holder);//绑定ViewHolder对象
		}
        else{
            holder = (ViewHolder)cellView.getTag();//取出ViewHolder对象                  
        }

		holder.tv_geval_addtime.setText(dataList.get(position).get("geval_addtime"));
		holder.tv_content.setText(dataList.get(position).get("geval_content") + "");
		holder.tv_frommembername.setText(dataList.get(position).get(
				"geval_frommembername")
				+ "");
		Float geval_scores = (float) Integer.parseInt(dataList.get(position)
				.get("geval_scores"));
		// ratingBar.setNumStars(geval_scores);
		holder.ratingBar.setStepSize(0.5f);
		holder.ratingBar.setRating(geval_scores);
		return cellView;

	}
	
	/**存放控件*/
	public final class ViewHolder{
		public TextView tv_geval_addtime;
		public TextView tv_content;
		public TextView tv_frommembername;
		public RatingBar ratingBar;
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

}
