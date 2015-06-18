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

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.BeeFramework.BeeFrameworkApp;
import com.external.activeandroid.util.Log;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zykj.landous.LandousAppConst;
import com.zykj.landous.R;
import com.zykj.landous.Data.BaseData;
import com.zykj.landous.Tools.HttpUtils;
import com.zykj.landous.activity.B2_ProductdetailsActivity;
import com.zykj.landous.activity.C1_ShopActivity;
import com.zykj.landous.adapter.D0_New_Shoping_Car_adapter.onCheckedChanged;

/**
 * @author 作者 zhang
 * @version 创建时间：2014年12月31日 下午3:41:00 宝贝详情 Fragment
 */
public class B2_0_PriductdetailsFragment extends Fragment implements
		OnClickListener {
	private String goods_id = "";
	/**
	 * 进入店铺
	 */
	private TextView tv_goshop;
	private Intent it;
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
	JSONArray imgArr;
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
	private ProgressDialog loadingPDialog = null;
	public static ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
	/**
	 * 商品名称
	 */
	private TextView tv_productname;
	/**
	 * 店铺名称
	 */
	private TextView tv_shopname;
	/**
	 * 原价
	 */
	private TextView tv_oldprice;
	/**
	 * 现价
	 */
	private TextView tv_price;
	/**
	 * 浏览次数？
	 */
	private TextView tv_goods_click;
	/**
	 * 销量
	 */
	private TextView tv_salenum;

	/**
	 * 商铺跳转
	 */
	private RelativeLayout rl_shopid;
	/**
	 * 推荐商品名字的id
	 */
	private int tv_ad_names[] = new int[] { R.id.tv_ad_1_name,
			R.id.tv_ad_2_name, R.id.tv_ad_3_name, R.id.tv_ad_4_name };
	private View view;
	/**
	 * 推荐商品的价格 id
	 */
	private int tv_ad_prices[] = new int[] { R.id.tv_ad1_price,
			R.id.tv_ad2_price, R.id.tv_ad3_price, R.id.tv_ad4_price, };

	/**
	 * 推荐商品售出的数量 id
	 */
	private int tv_ad_nums[] = new int[] { R.id.tv_ad1_num, R.id.tv_ad2_num,
			R.id.tv_ad3_num, R.id.tv_ad4_num, };
	/**
	 * 图片的id
	 */
	private int adIds[] = new int[] { R.id.ad_1, R.id.ad_2, R.id.ad_3,
			R.id.ad_4 };
	isFavorite listener;
	private ScrollView scrollView;
	/**
	 * 库存
	 */
	private TextView tv_goods_storage;
	public static int goods_storage = 0;
	private static int num = 0;
	/**
	 * 满减金额
	 */
	 TextView  min_total_price;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.b2_0_productedtails, null);
		init(view);
		return view;
	}

	@Override
	public void onStart() {

		loadingPDialog = new ProgressDialog(getActivity());
		loadingPDialog.setMessage("正在加载....");
		loadingPDialog.setCancelable(false);
		loadingPDialog.show();
		goods_id = B2_ProductdetailsActivity.goods_id;
		HttpUtils.getGoodsDetail(goods_id, getGoodsDetail);
		super.onStart();
	}

	private void init(View view) {
		scrollView = (ScrollView) view.findViewById(R.id.scrollView);
		rl_shopid = (RelativeLayout) view.findViewById(R.id.rl_shopid);
		rl_shopid.setOnClickListener(this);
		tv_salenum = (TextView) view.findViewById(R.id.tv_salenum);
		tv_goods_click = (TextView) view.findViewById(R.id.tv_goods_click);
		tv_price = (TextView) view.findViewById(R.id.tv_price);
		tv_oldprice = (TextView) view.findViewById(R.id.tv_oldprice);
		tv_oldprice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);// 下划线
		tv_shopname = (TextView) view.findViewById(R.id.tv_shopname);
		tv_productname = (TextView) view.findViewById(R.id.tv_productname);
		tv_goshop = (TextView) view.findViewById(R.id.tv_goshop);
		tv_goshop.setOnClickListener(this);
		tv_goods_storage = (TextView) view.findViewById(R.id.tv_goods_storage);
		min_total_price=(TextView) view.findViewById(R.id.min_total_price);
		// 从布局文件中获取ViewPager父容器
		pagerLayout = (LinearLayout) view.findViewById(R.id.view_pager_content);
		// 创建ViewPager
		adViewPager = new ViewPager(getActivity());

		// 获取屏幕像素相关信息
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

		// 根据屏幕信息设置ViewPager广告容器的宽高
		adViewPager.setLayoutParams(new LayoutParams(dm.widthPixels,
				dm.widthPixels));

		// 将ViewPager容器设置到布局文件父容器中
		pagerLayout.addView(adViewPager);

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
				imageViews[arg0].setBackgroundResource(R.drawable.light_gray_8);
				if (arg0 != i) {
					imageViews[i].setBackgroundResource(R.drawable.dark_gray_8);
				}
			}
		}
	}

	private void initCirclePoint(View view) {
		ViewGroup group = (ViewGroup) view.findViewById(R.id.viewGroup);
		group.removeAllViews();
		imageViews = new ImageView[pageViews.size()];
		for (int i = 0; i < pageViews.size(); i++) {
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
		img = new ImageView[imgArr.length()];
		for (int i = 0; i < imgArr.length(); i++) {
			img[i] = new ImageView(getActivity());
			img[i].setImageResource(R.drawable.searcher_no_result_empty_icon);
			img[i].setScaleType(ScaleType.CENTER_INSIDE);
			pageViews.add(img[i]);
		}

		adapter = new AdPageAdapter(pageViews);

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
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_addspcar:
			Toast.makeText(getActivity(), "加入购物车成功", Toast.LENGTH_LONG).show();
			break;
		case R.id.tv_goshop:
			it = new Intent(getActivity(), C1_ShopActivity.class);
			getActivity().startActivity(it);
			break;
		// case R.id.rl_collection:
		//
		// {
		// int type = (Integer) (v.getTag() == null ? 0 : v.getTag());
		// String msg = type % 2 == 0 ? "收藏成功" : "取消收藏";
		// int bgid = type % 2 == 0 ? R.drawable.icon_collection_act
		// : R.drawable.icon_collection;
		// Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
		// type++;
		// v.setTag(type);
		// v.setBackgroundResource(bgid);
		// }
		// break;

		case R.id.rl_shopid:
			Toast.makeText(getActivity(), v.getTag() + "", Toast.LENGTH_LONG)
					.show();
			break;
		default:
			break;
		}

	}

	/**
	 * 获取商品详情
	 */
	JsonHttpResponseHandler getGoodsDetail = new JsonHttpResponseHandler() {

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
				scrollView.fullScroll(ScrollView.FOCUS_UP);
				try {
					JSONObject json = (JSONObject) response.get("data");
//					Toast.makeText(getActivity(), json+"", Toast.LENGTH_LONG).show();
//					Log.e("tagtag", json+"");
					goods_storage = json.getInt("goods_storage");
					tv_goods_storage.setText("库存:" + goods_storage);
					tv_productname.setText(Html.fromHtml("<div>"
							+ json.getString("goods_name") + "" + "</div>"));
					tv_shopname.setText(json.getString("store_name"));
//					tv_oldprice.setText("￥"
//							+ json.getString("goods_marketprice"));
					tv_oldprice.setText("");
					tv_price.setText("￥" + json.getString("goods_price"));
					tv_goods_click.setText(json.getString("goods_click")
							+ "人浏览");
					tv_salenum.setText("已售：" + json.getString("goods_salenum"));
					min_total_price.setText("满"+BaseData.min_total_price+"元包邮");
//					min_total_price.setText("满"+json.getString("store_free_price")+"元包邮");
					rl_shopid.setTag(json.getString("store_id"));
					imgArr = json.getJSONArray("images");
					initPageAdapter();
					initCirclePoint(view);
					adViewPager.setAdapter(adapter);
					adViewPager
							.setOnPageChangeListener(new AdPageChangeListener());
					if (num < 1) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								while (true) {
									if (isContinue) {
										viewHandler
												.sendEmptyMessage(atomicInteger
														.get());
										atomicOption();

									}
								}
							}
						}).start();
						num = num + 1;
					}
					for (int i = 0; i < imgArr.length(); i++) {
						String imgurl0 = LandousAppConst.HOME_IMG_URL
								+ json.getString("store_id")
								+ "/"
								+ imgArr.getJSONObject(i).getString(
										"goods_image");

						ImageLoader.getInstance().displayImage(imgurl0, img[i],
								BeeFrameworkApp.options_square);
						 img[i].setScaleType(ScaleType.FIT_CENTER);
					}

					int favorite = json.getInt("favorite");
					if (favorite == 1) {
						B2_ProductdetailsActivity.iv_collection
								.setImageResource(R.drawable.icon_collection_act);
						B2_ProductdetailsActivity.type = 1;
					} else if (favorite == 0) {
						B2_ProductdetailsActivity.iv_collection
								.setImageResource(R.drawable.icon_collection);
						B2_ProductdetailsActivity.type = 0;
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					JSONArray array = response.getJSONArray("recommends");
					for (int i = 0; i < 4; i++) {
						JSONObject jsonItem = array.getJSONObject(i);
						TextView text = new TextView(getActivity());
						text = (TextView) view.findViewById(tv_ad_names[i]);
						text.setText(jsonItem.getString("goods_name"));
						text = (TextView) view.findViewById(tv_ad_prices[i]);
						text.setText("￥" + jsonItem.getString("goods_price"));
						text = (TextView) view.findViewById(tv_ad_nums[i]);
						text.setText(jsonItem.getString("goods_salenum")
								+ "人购买");
						ImageView img = new ImageView(getActivity());
						img = (ImageView) view.findViewById(adIds[i]);
						img.setTag(jsonItem.get("goods_id"));
						img.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								loadingPDialog.show();
								// it=new Intent(getActivity(),
								// B2_ProductdetailsActivity.class);
								// it.putExtra("goods_id",
								// v.getTag().toString());
								// startActivity(it);
								B2_ProductdetailsActivity.goods_id = v.getTag()
										+ "";
								HttpUtils.getGoodsDetail(v.getTag() + "",
										getGoodsDetail);

							}
						});
						String url = LandousAppConst.HOME_IMG_URL
								+ jsonItem.getString("store_id") + "/"
								+ jsonItem.getString("goods_image");
						img.setScaleType(ScaleType.CENTER_INSIDE);
						ImageLoader.getInstance().displayImage(url, img,
								BeeFrameworkApp.options_square);
						// goods_image

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				loadingPDialog.dismiss();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers,
				Throwable throwable, JSONObject errorResponse) {
			// TODO Auto-generated method stub
			loadingPDialog.dismiss();
			Toast.makeText(getActivity(), "网络连接超时", Toast.LENGTH_LONG).show();
			super.onFailure(statusCode, headers, throwable, errorResponse);
		}
	};

	public interface isFavorite {

		public void OnBack();

	}

	public void set(isFavorite listener) {
		this.listener = listener;
	}
}
