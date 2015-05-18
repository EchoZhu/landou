package com.zykj.landous.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.BeeFramework.BeeFrameworkApp;
import com.external.androidquery.callback.AjaxStatus;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zykj.landous.R;
import com.zykj.landous.Tools.HttpUtils;
import com.zykj.landous.activity.B1_GoodsListActivity;
import com.zykj.landous.adapter.A0_GoodsAdapter;
import com.zykj.landous.view.MyListView;

/**
 * 无用！ 最新修改A0_HomeFragment
 * 
 * @author zhangxu
 * 
 */
public class A0_IndexFragment extends Fragment implements OnClickListener
		 {
	Intent it;
	List<Map<String, String>> data = new ArrayList<Map<String, String>>();
	private MyListView listview;
	// 轮播图 start
	private ImageView[] imageViews;
	private List<View> pageViews;
	private ImageView imageView;
	private AdPageAdapter adapter;
	ImageView[] img;
	private LinearLayout pagerLayout;
	private ViewPager adViewPager;
	private AtomicInteger atomicInteger = new AtomicInteger(0);
	private boolean isContinue = true;
	/*
	 * 每隔固定时间切换广告栏图片
	 */
	private final Handler viewHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			adViewPager.setCurrentItem(msg.what);
			super.handleMessage(msg);

		}

	};

	// 轮播图 end
	/**
	 * 休闲食品
	 */
	private ImageView iv_leisurefood;
	/**
	 * 家庭清洁
	 */
	private ImageView iv_homecleaners;
	/**
	 * 个人洗护
	 */
	private ImageView iv_personalcare;
	/**
	 * 生活用品
	 */
	private ImageView iv_supplies;
	/**
	 * 酒水饮料
	 */
	private ImageView iv_beverages;
	/**
	 * 家用电器
	 */
	private ImageView iv_appliance;
	/**
	 * 粮油调味
	 */
	private ImageView iv_condiment;
	/**
	 * 办公礼品
	 */
	private ImageView iv_officegifts;
	// /**
	// * 分享按钮
	// */
	// private ImageView iv_share;

	/**
	 * 分享
	 */
	private LinearLayout ll_share;
	private EditText search_input;
	private View view;
	A0_GoodsAdapter goodsAdapter;
	private ImageView imgs[] = new ImageView[8];
	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			if (msg.what == 0x123) {

				// 使用adapter显示服务器响应
				goodsAdapter.notifyDataSetChanged();

			}
		}
	};
	Thread mThread = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		HttpUtils.getHomeGoods(res_getHomeGoods);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_index, null);
		// homeModel = new HomeModel(getActivity());
		// homeModel.addResponseListener(this);
		// homeModel.homeScreenList();
		init(view);

		return view;
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub

		super.onStart();
	}

	/**
	 * 获取首页商品列表
	 */
	JsonHttpResponseHandler res_getHomeGoods = new JsonHttpResponseHandler() {

		@Override
		public void onSuccess(int statusCode, Header[] headers,
				JSONObject response) {
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

				try {
					JSONArray array = response.getJSONArray("list");
					for (int i = 0; i < array.length(); i++) {
						JSONObject jsonItem = array.getJSONObject(i);
						Map<String, String> map = new HashMap();
						map.put("gc_name", jsonItem.getString("gc_name"));
						map.put("goods",
								array.getJSONObject(i).getString("goods"));
						imgs[i].setTag(array.getJSONObject(i)
								.getString("gc_id"));
						data.add(map);
					}
					Message msg = new Message();
					msg.what = 0x123;
					handler.sendMessage(msg);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers,
				Throwable throwable, JSONObject errorResponse) {
			// TODO Auto-generated method stub
			super.onFailure(statusCode, headers, throwable, errorResponse);
		}
	};

	private void init(View view) {
		initCirclePoint(view);
		listview = (MyListView) view.findViewById(R.id.listview);
		listview.setPullLoadEnable(false);
		listview.setPullRefreshEnable(true);
		listview.setRefreshTime();
		goodsAdapter = new A0_GoodsAdapter(getActivity(), data);
		listview.setAdapter(goodsAdapter);
		setListViewHeightBasedOnChildren(listview);
		search_input = (EditText) view.findViewById(R.id.search_input);
		search_input.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
		search_input.setInputType(EditorInfo.TYPE_CLASS_TEXT);
		search_input.setOnClickListener(this);
		CloseKeyBoard();
		iv_leisurefood = (ImageView) view.findViewById(R.id.iv_leisurefood);
		iv_leisurefood.setOnClickListener(this);
		iv_homecleaners = (ImageView) view.findViewById(R.id.iv_homecleaners);
		iv_homecleaners.setOnClickListener(this);
		iv_personalcare = (ImageView) view.findViewById(R.id.iv_personalcare);
		iv_personalcare.setOnClickListener(this);
		iv_supplies = (ImageView) view.findViewById(R.id.iv_supplies);
		iv_supplies.setOnClickListener(this);
		iv_beverages = (ImageView) view.findViewById(R.id.iv_beverages);
		iv_beverages.setOnClickListener(this);
		iv_appliance = (ImageView) view.findViewById(R.id.iv_appliance);
		iv_appliance.setOnClickListener(this);
		iv_condiment = (ImageView) view.findViewById(R.id.iv_condiment);
		iv_condiment.setOnClickListener(this);
		iv_officegifts = (ImageView) view.findViewById(R.id.iv_officegifts);
		iv_officegifts.setOnClickListener(this);
		imgs = new ImageView[] { iv_leisurefood, iv_personalcare, iv_beverages,
				iv_condiment, iv_homecleaners, iv_supplies, iv_appliance,
				iv_officegifts };
		// iv_share=(ImageView)view.findViewById(R.id.iv_share);
		// iv_share.setOnClickListener(this);
		ll_share = (LinearLayout) view.findViewById(R.id.ll_share);
		ll_share.setOnClickListener(this);

		// 从布局文件中获取ViewPager父容器
		pagerLayout = (LinearLayout) view.findViewById(R.id.view_pager_content);
		// 创建ViewPager
		adViewPager = new ViewPager(getActivity());

		// 获取屏幕像素相关信息
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

		// 根据屏幕信息设置ViewPager广告容器的宽高
		adViewPager.setLayoutParams(new LayoutParams(dm.widthPixels,
				dm.heightPixels * 2 / 9));

		// 将ViewPager容器设置到布局文件父容器中
		pagerLayout.addView(adViewPager);
		initPageAdapter();
		toAddImg();
		mThread.start();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mThread.interrupt();
	}

	protected void atomicOption() {
		atomicInteger.incrementAndGet();
		if (atomicInteger.get() > imageViews.length - 1) {
			atomicInteger.getAndAdd(-5);
		}
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {

		}
	}

	/**
	 * ViewPager 页面改变监听器
	 */
	private final class AdPageChangeListener implements OnPageChangeListener {

		/**
		 * 页面滚动状态发生改变的时候触发
		 */
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		/**
		 * 页面滚动的时候触发
		 */
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		/**
		 * 页面选中的时候触发
		 */
		@Override
		public void onPageSelected(int arg0) {
			// 获取当前显示的页面是哪个页面
			atomicInteger.getAndSet(arg0);
			// 重新设置原点布局集合
			for (int i = 0; i < imageViews.length; i++) {
				imageViews[arg0]
						.setBackgroundResource(R.drawable.point_focused);
				if (arg0 != i) {
					imageViews[i]
							.setBackgroundResource(R.drawable.point_unfocused);
				}
			}
		}
	}

	private void initCirclePoint(View view) {
		ViewGroup group = (ViewGroup) view.findViewById(R.id.viewGroup);
		imageViews = new ImageView[3];

		for (int i = 0; i < 3; i++) {
			// 创建一个ImageView, 并设置宽高. 将该对象放入到数组中
			imageView = new ImageView(getActivity());
			imageView.setLayoutParams(new LayoutParams(10, 10));
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					imageView.getLayoutParams());
			lp.setMargins(5, 5, 5, 5);
			imageView.setLayoutParams(lp);
			imageViews[i] = imageView;
			// 初始值, 默认第0个选中
			if (i == 0) {
				imageViews[i].setBackgroundResource(R.drawable.point_focused);
			} else {
				imageViews[i].setBackgroundResource(R.drawable.point_unfocused);
			}
			// 将小圆点放入到布局中
			group.addView(imageViews[i]);
		}

	}

	private void initPageAdapter() {
		// TODO Auto-generated method stub
		pageViews = new ArrayList<View>();
		img = new ImageView[3];
		// 下面注释掉的内饰以前是未联网状态下用来表现UI效果的
		img[0] = new ImageView(getActivity());
		img[0].setImageResource(R.drawable.default_image);
		img[1] = new ImageView(getActivity());
		img[1].setImageResource(R.drawable.default_image);
		img[2] = new ImageView(getActivity());
		img[2].setImageResource(R.drawable.default_image);

		img[0].setScaleType(ScaleType.CENTER_INSIDE);
		img[1].setScaleType(ScaleType.CENTER_INSIDE);
		img[2].setScaleType(ScaleType.CENTER_INSIDE);
		pageViews.add(img[2]);
		pageViews.add(img[0]);
		pageViews.add(img[1]);
		// end

	}

	private final class AdPageAdapter extends PagerAdapter {
		private List<View> views = null;

		/**
		 * 初始化数据源, 即View数组
		 */
		public AdPageAdapter(List<View> views) {
			this.views = views;
		}

		/**
		 * 从ViewPager中删除集合中对应索引的View对象
		 */
		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(views.get(position));
		}

		/**
		 * 获取ViewPager的个数
		 */
		@Override
		public int getCount() {
			return views.size();
		}

		/**
		 * 从View集合中获取对应索引的元素, 并添加到ViewPager中
		 */
		@Override
		public Object instantiateItem(View container, int position) {
			((ViewPager) container).addView(views.get(position), 0);
			return views.get(position);
		}

		/**
		 * 是否将显示的ViewPager页面与instantiateItem返回的对象进行关联 这个方法是必须实现的
		 */
		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.iv_leisurefood:
			it = new Intent(getActivity(), B1_GoodsListActivity.class);
			it.putExtra("gc_id", v.getTag() + "");
			startActivity(it);
			Toast.makeText(getActivity(), "休闲食品" + v.getTag(),
					Toast.LENGTH_LONG).show();
			break;
		case R.id.iv_homecleaners:
			it = new Intent(getActivity(), B1_GoodsListActivity.class);
			it.putExtra("gc_id", v.getTag() + "");
			startActivity(it);
			Toast.makeText(getActivity(), "家庭清洁" + v.getTag(),
					Toast.LENGTH_LONG).show();
			break;
		case R.id.iv_personalcare:
			it = new Intent(getActivity(), B1_GoodsListActivity.class);
			it.putExtra("gc_id", v.getTag() + "");
			startActivity(it);
			Toast.makeText(getActivity(), "个人洗护" + v.getTag(),
					Toast.LENGTH_LONG).show();
			break;
		case R.id.iv_supplies:
			it = new Intent(getActivity(), B1_GoodsListActivity.class);
			it.putExtra("gc_id", v.getTag() + "");
			startActivity(it);
			Toast.makeText(getActivity(), "生活用品" + v.getTag(),
					Toast.LENGTH_LONG).show();
			break;
		case R.id.iv_beverages:
			it = new Intent(getActivity(), B1_GoodsListActivity.class);
			it.putExtra("gc_id", v.getTag() + "");
			startActivity(it);
			Toast.makeText(getActivity(), "酒水饮料" + v.getTag(),
					Toast.LENGTH_LONG).show();
			break;
		case R.id.iv_appliance:
			it = new Intent(getActivity(), B1_GoodsListActivity.class);
			it.putExtra("gc_id", v.getTag() + "");
			startActivity(it);
			Toast.makeText(getActivity(), "家用电器" + v.getTag(),
					Toast.LENGTH_LONG).show();
			break;
		case R.id.iv_condiment:
			it = new Intent(getActivity(), B1_GoodsListActivity.class);
			it.putExtra("gc_id", v.getTag() + "");
			startActivity(it);
			Toast.makeText(getActivity(), "粮油调味" + v.getTag(),
					Toast.LENGTH_LONG).show();
			break;
		case R.id.iv_officegifts:
			it = new Intent(getActivity(), B1_GoodsListActivity.class);
			it.putExtra("gc_id", v.getTag() + "");
			startActivity(it);
			Toast.makeText(getActivity(), "办公礼品" + v.getTag(),
					Toast.LENGTH_LONG).show();
			break;
		case R.id.ll_share:
//			Share mShare = new Share(getActivity());
//			mShare.show("landous.com");
			break;
		case R.id.search_input:
			it = new Intent(getActivity(), B1_GoodsListActivity.class);
			startActivity(it);
			break;
		default:
			break;
		}
	}

	// 关闭键盘
	public void CloseKeyBoard() {
		search_input.clearFocus();
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(search_input.getWindowToken(), 0);
	}



	private void toAddImg() {
		// TODO Auto-generated method stub
		// for (int i = 0; i < 3; i++) {
		// img[i] = new ImageView(getActivity());
		// ImageLoader.getInstance().displayImage(
		// homeModel.shotList.get(i).get("pic_img") + "", img[i],
		// BeeFrameworkApp.options);
		// img[i].setScaleType(ScaleType.FIT_XY);
		// img[i].setTag(homeModel.shotList.get(i).get("pic_url"));
		// img[i].setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Toast.makeText(getActivity(), v.getTag() + "",
		// Toast.LENGTH_LONG).show();
		//
		// }
		// });
		// if (pageViews.size() < 3) {
		// pageViews.add(img[i]);
		// }
		// }
		adapter = null;
		adapter = new AdPageAdapter(pageViews);
		adViewPager.setAdapter(adapter);
		adViewPager.setOnPageChangeListener(new AdPageChangeListener());
		mThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (isContinue) {
						viewHandler.sendEmptyMessage(atomicInteger.get());
						atomicOption();

					}
				}
			}
		});

	}

	/**
	 * 动态设置ListView的高度
	 * 
	 * @param listView
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		if (listView == null)
			return;

		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

}
