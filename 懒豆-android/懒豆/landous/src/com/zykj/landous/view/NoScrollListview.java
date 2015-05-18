package com.zykj.landous.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * @author 作者 zhang
 * @version 创建时间：2015年1月10日 下午4:08:07 类说明 禁止滚动的listview
 */
public class NoScrollListview extends ListView {

	public NoScrollListview(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * 设置不滚动
	 */
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);

	}

}
