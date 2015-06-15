package com.zykj.landous.fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.ls.LSInput;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.BeeFramework.BeeFrameworkApp;
import com.BeeFramework.model.BeeCallback;
import com.BeeFramework.model.BeeQuery;
import com.BeeFramework.model.BusinessResponse;
import com.BeeFramework.view.MyProgressDialog;
import com.external.androidquery.callback.AjaxStatus;
import com.external.androidquery.util.Constants;
import com.external.maxwin.view.XListView.IXListViewListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.zykj.landous.LandousAppConst;
import com.zykj.landous.R;
import com.zykj.landous.Data.BaseData;
import com.zykj.landous.Tools.HttpUtils;
import com.zykj.landous.Tools.ShareUmeng;
import com.zykj.landous.Tools.ShareUmengMain;
import com.zykj.landous.Tools.Image.MyGallery;
import com.zykj.landous.activity.A2_SearchActivity;
import com.zykj.landous.activity.B1_GoodsListActivity;
import com.zykj.landous.activity.B2_ProductdetailsActivity;
import com.zykj.landous.adapter.A0_GoodsAdapter;
import com.zykj.landous.adapter.ImgAdapter;
import com.zykj.landous.model.HomeModel;
import com.zykj.landous.view.MyListView;
import com.zykj.landous.view.SlideShowView;

/**
 * @author 作者 zhang
 * @version 创建时间：2015年1月16日 上午10:05:36 类说明
 */
public class A0_HomeFragment extends Fragment implements OnClickListener,
		IXListViewListener, BusinessResponse {
	private View view;
	private MyGallery gallery = null;
	// 轮播图
	private SlideShowView slideShowView;
	// private ArrayList<String> imgList;
	private ArrayList<ImageView> portImg;
	/**
	 * 存储上一个选择项的Index
	 */
	HomeModel homeModel;
	private int preSelImgIndex = 0;
	private LinearLayout ll_focus_indicator_container = null;
	private MyListView listview;
	private View headView;
	List<Map<String, String>> data = new ArrayList<Map<String, String>>();
	List<Map<String, String>> goods_data = new ArrayList<Map<String, String>>();
	String goods_id[] = new String[]{"3025","3028","3029","3031","3032","3035","3036","3037"};//通过gc_id来筛选需要显示在首页的物品
	ImgAdapter adapter;
	/**
	 * 生鲜饮品1
	 */
	private ImageView iv_shengxianyinpin;
	/**
	 * 休闲零食2
	 */
	private ImageView iv_xiuxianlingshi;
	/**
	 * 烟酒礼品3
	 */
	private ImageView iv_yanjiulipin;
	/**
	 * 粮油调味4
	 */
	private ImageView iv_liangyoutiaowei;
	/**
	 * 母婴生活5
	 */
	private ImageView iv_muyingshenghuo;
	/**
	 * 个护洗化6
	 */
	private ImageView iv_gehuxihua;
	/**
	 * 生活日用7
	 */
	private ImageView iv_riyongtuza;
	/**
	 * 家具文体
	 */
	private ImageView iv_jiajuwenti;
//	/**
//	 * 休闲食品
//	 */
//	private ImageView iv_leisurefood;
//	/**
//	 * 家庭清洁
//	 */
//	private ImageView iv_homecleaners;
//	/**
//	 * 个人洗护
//	 */
//	private ImageView iv_personalcare;
//	/**
//	 * 生活用品
//	 */
//	private ImageView iv_supplies;
//	/**
//	 * 酒水饮料
//	 */
//	private ImageView iv_beverages;
//	/**
//	 * 家用电器
//	 */
//	private ImageView iv_appliance;
//	/**
//	 * 粮油调味
//	 */
//	private ImageView iv_condiment;
//	/**
//	 * 办公礼品
//	 */
//	private ImageView iv_officegifts;
	private ImageView imgs[] = new ImageView[8];
	A0_GoodsAdapter goodsAdapter;
	private ProgressDialog loadingPDialog = null;
	public static boolean isRun = false;

	/**
	 * 分享
	 */
	private LinearLayout ll_share;
	private EditText search_input;
	Intent it;
	private RelativeLayout rl_main;
	Thread mThread;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.a0_homefragment, null);
		listview = (MyListView) view.findViewById(R.id.listview);
		headView = LayoutInflater.from(getActivity()).inflate(
				R.layout.a0_homehead, null);
		listview.setPullLoadEnable(false);
		listview.setPullRefreshEnable(true);
		listview.setXListViewListener(this, 0);
		listview.setRefreshTime();

		goodsAdapter = new A0_GoodsAdapter(getActivity(), goods_data);
		listview.setAdapter(goodsAdapter);
		listview.addHeaderView(headView);
		slideShowView = (SlideShowView) headView.findViewById(R.id.slideshowView);
		LayoutParams lp = slideShowView.getLayoutParams();
		DisplayMetrics metric = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels; // 屏幕宽度（像素）
		lp.height = width/5*2;
		slideShowView.setLayoutParams(lp);
//		iv_leisurefood = (ImageView) headView.findViewById(R.id.iv_leisurefood);
//		iv_leisurefood.setOnClickListener(this);
		iv_shengxianyinpin = (ImageView) headView.findViewById(R.id.iv_shengxianyinpin);
		iv_shengxianyinpin.setOnClickListener(this);
//		iv_homecleaners = (ImageView) headView.findViewById(R.id.iv_homecleaners);
//		iv_homecleaners.setOnClickListener(this);
		iv_xiuxianlingshi = (ImageView) headView.findViewById(R.id.iv_xiuxianlingshi);
		iv_xiuxianlingshi.setOnClickListener(this);
//		iv_personalcare = (ImageView) headView.findViewById(R.id.iv_personalcare);
//		iv_personalcare.setOnClickListener(this);
		iv_yanjiulipin = (ImageView) headView.findViewById(R.id.iv_yanjiulipin);
		iv_yanjiulipin.setOnClickListener(this);
//		iv_supplies = (ImageView) headView.findViewById(R.id.iv_supplies);
//		iv_supplies.setOnClickListener(this);
		iv_liangyoutiaowei = (ImageView) headView.findViewById(R.id.iv_liangyoutiaowei);
		iv_liangyoutiaowei.setOnClickListener(this);
//		iv_beverages = (ImageView) headView.findViewById(R.id.iv_beverages);
//		iv_beverages.setOnClickListener(this);
		iv_muyingshenghuo = (ImageView) headView.findViewById(R.id.iv_muyingshenghuo);
		iv_muyingshenghuo.setOnClickListener(this);
//		iv_appliance = (ImageView) headView.findViewById(R.id.iv_appliance);
//		iv_appliance.setOnClickListener(this);
		iv_gehuxihua = (ImageView) headView.findViewById(R.id.iv_gehuxihua);
		iv_gehuxihua.setOnClickListener(this);
//		iv_condiment = (ImageView) headView.findViewById(R.id.iv_condiment);
//		iv_condiment.setOnClickListener(this);
		iv_riyongtuza = (ImageView) headView.findViewById(R.id.iv_riyongtuza);
		iv_riyongtuza.setOnClickListener(this);
//		iv_officegifts = (ImageView) headView.findViewById(R.id.iv_officegifts);
//		iv_officegifts.setOnClickListener(this);
		iv_jiajuwenti = (ImageView) headView.findViewById(R.id.iv_jiajuwenti);
		iv_jiajuwenti.setOnClickListener(this);
//		imgs = new ImageView[] { iv_leisurefood, iv_personalcare, iv_beverages,
//				iv_condiment, iv_homecleaners, iv_supplies, iv_appliance,
//				iv_officegifts };
		imgs = new ImageView[] { iv_shengxianyinpin, iv_yanjiulipin, iv_muyingshenghuo,
				iv_riyongtuza, iv_xiuxianlingshi, iv_liangyoutiaowei, iv_gehuxihua,
				iv_jiajuwenti };
		if (goods_data.size() != 0) {
			for (int i = 0; i < goods_data.size(); i++) {
				imgs[i].setTag(goods_data.get(i).get("gc_id").toString());
			}
		}
		ll_share = (LinearLayout) view.findViewById(R.id.ll_share);
		ll_share.setOnClickListener(this);
		search_input = (EditText) view.findViewById(R.id.search_input);
		search_input.setOnClickListener(this);
		rl_main = (RelativeLayout) view.findViewById(R.id.rl_main);
		homeModel = new HomeModel(getActivity());
		homeModel.addResponseListener(this);
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.i("BaseData", BaseData.min_total_price + ""
				+ BaseData.online_pay_discount);
		loadingPDialog = new ProgressDialog(getActivity());
		loadingPDialog.setMessage("正在加载....");
		loadingPDialog.setCancelable(false);
		mThread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				homeScreenList();

			}
		});
		if (goods_data.size() == 0) {
			loadingPDialog.show();
			HttpUtils.getHomeGoods(res_getHomeGoods);
		} else {
		}

		isRun = true;
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		isRun = false;
		super.onPause();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
//		String goods_id[] = new String[]{"3025","3028","3029","3031","3032","3035","3036","3037"};//通过gc_id来筛选需要显示在
		case R.id.iv_shengxianyinpin://(休闲食品)   生鲜饮品 1
			it = new Intent(getActivity(), B1_GoodsListActivity.class);
//			it.putExtra("gc_id", "1");
//			it.putExtra("gc_id", "2669");
			it.putExtra("gc_id", "3025");
			startActivity(it);

			break;
		case R.id.iv_xiuxianlingshi:// (家庭清洁)   休闲零食2
			it = new Intent(getActivity(), B1_GoodsListActivity.class);
//			it.putExtra("gc_id", "308");
//			it.putExtra("gc_id", "2880");
			it.putExtra("gc_id", "3032");
			startActivity(it);

			break;
		case R.id.iv_yanjiulipin://(个人洗护) 烟酒礼品3
			it = new Intent(getActivity(), B1_GoodsListActivity.class);
//			it.putExtra("gc_id", "2");
//			it.putExtra("gc_id", "2745");
			it.putExtra("gc_id", "3035");
			startActivity(it);

			break;
		case R.id.iv_liangyoutiaowei://(生活日用)  粮油调味4
			it = new Intent(getActivity(), B1_GoodsListActivity.class);
//			it.putExtra("gc_id", "470");
//			it.putExtra("gc_id", "2940");
			it.putExtra("gc_id", "3036");
			startActivity(it);

			break;
		case R.id.iv_muyingshenghuo://(酒水饮料) 母婴生活5
			it = new Intent(getActivity(), B1_GoodsListActivity.class);
//			it.putExtra("gc_id", "3");
//			it.putExtra("gc_id", "2810");
			it.putExtra("gc_id", "3037");
			startActivity(it);

			break;
		case R.id.iv_gehuxihua://(家用电器) 个护洗化6
			it = new Intent(getActivity(), B1_GoodsListActivity.class);
//			it.putExtra("gc_id", "530");
//			it.putExtra("gc_id", "2968");
			it.putExtra("gc_id", "3028");
			startActivity(it);
			break;
		case R.id.iv_riyongtuza://(粮油调味) 日用土杂7
			it = new Intent(getActivity(), B1_GoodsListActivity.class);
//			it.putExtra("gc_id", "256");
//			it.putExtra("gc_id", "3036");
			it.putExtra("gc_id", "3029");
			startActivity(it);
			break;
		case R.id.iv_jiajuwenti://(办公礼品) 家具文体8
			it = new Intent(getActivity(), B1_GoodsListActivity.class);
//			it.putExtra("gc_id", "593");
//			it.putExtra("gc_id", "3005");
			it.putExtra("gc_id", "3031");
			startActivity(it);
			break;
		case R.id.ll_share:
			// Share mShare = new Share(getActivity());
			// mShare.show("landous.com");
			ShareUmengMain mShare = new ShareUmengMain(getActivity());
			mShare.setContent("我在懒豆商城看见件商品不错，你也来看看吧");
			mShare.show();
			break;
		case R.id.search_input:
			it = new Intent(getActivity(), A2_SearchActivity.class);
			startActivity(it);
			break;
		}

	}

	private void InitFocusIndicatorContainer() {
		portImg = new ArrayList<ImageView>();
		for (int i = 0; i < 3; i++) {
			ImageView localImageView = new ImageView(getActivity());
			localImageView.setId(i);
			ImageView.ScaleType localScaleType = ImageView.ScaleType.FIT_XY;
			localImageView.setScaleType(localScaleType);
			LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(
					24, 24);
			localImageView.setLayoutParams(localLayoutParams);
			localImageView.setPadding(5, 5, 5, 5);
			localImageView.setImageResource(R.drawable.point_unfocused);
			portImg.add(localImageView);
			this.ll_focus_indicator_container.addView(localImageView);
		}
	}

	JsonHttpResponseHandler res = new JsonHttpResponseHandler() {
		public void onSuccess(int statusCode, org.apache.http.Header[] headers,
				org.json.JSONObject response) {
			super.onSuccess(statusCode, headers, response);
			Log.i("add-test", response.toString());
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
					data.clear();
					JSONArray array = response.getJSONArray("list");
					for (int i = 0; i < array.length(); i++) {
						JSONObject jsonItem = array.getJSONObject(i);
						Map<String, String> map = new HashMap<String, String>();
						map.put("pic_img",
								"http://img.landous.com/"
										+ jsonItem.getString("pic_img"));
						// imgList.add("http://img.landous.com/"
						// + jsonItem.getString("pic_img"));
						data.add(map);
					}
					Log.i("imgList", data + "!!!!");
					adapter.notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			loadingPDialog.dismiss();
		};

		@Override
		public void onFailure(int statusCode, Header[] headers,
				Throwable throwable, JSONObject errorResponse) {
			// TODO Auto-generated method stub
			listview.setBackgroundResource(R.drawable.img_net_error);
			loadingPDialog.dismiss();
			Toast.makeText(getActivity(), "网络连接超时", Toast.LENGTH_LONG).show();
			super.onFailure(statusCode, headers, throwable, errorResponse);

		}
	};
	/**
	 * 获取首页商品列表
	 */
	JsonHttpResponseHandler res_getHomeGoods = new JsonHttpResponseHandler() {

		@Override
		public void onSuccess(int statusCode, Header[] headers,
				JSONObject response) {
			super.onSuccess(statusCode, headers, response);
//			Log.e("res_getHomeGoods_response=", response+"");
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
				goods_data.clear();
				try {
					JSONArray array = response.getJSONArray("list");
//					Log.e("arrayarrayarrayarray", array+"");
					for (int i = 0; i < array.length(); i++) 
					{
						Map<String, String> map = new HashMap();
						JSONObject jsonItem = array.getJSONObject(i);
						for (int j = 0; j < goods_id.length; j++) {
							Log.e("jsonItem1_"+j, jsonItem.getString("gc_id")+"="+goods_id[j]);
							if (jsonItem.getString("gc_id").equals(goods_id[j])) //如果某一条的商品信息中的gc_id等于goos_id中的某一个数
							{
								map.put("gc_name", jsonItem.getString("gc_name"));
								map.put("goods",jsonItem.getString("goods"));
								imgs[j].setTag(jsonItem.getString("gc_id"));
								map.put("gc_id", jsonItem.getString("gc_id"));
								Log.e("jsonItem_"+j, jsonItem+"");
								goods_data.add(map);
							}
						}
					}
					Log.e("goods_data=", goods_data+"");
					
					headView.setVisibility(View.VISIBLE);
					goodsAdapter.notifyDataSetChanged();
					listview.setBackgroundColor(Color.WHITE);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					listview.setBackgroundResource(R.drawable.img_data_null);
					e.printStackTrace();
				}
			}
			loadingPDialog.dismiss();
		}

		@Override
		public void onFailure(int statusCode, Header[] headers,
				Throwable throwable, JSONObject errorResponse) {
			// TODO Auto-generated method stub
			loadingPDialog.dismiss();
			Log.i("home-good-null——state", errorResponse + "");
			headView.setVisibility(View.GONE);
			listview.setBackgroundResource(R.drawable.img_net_error);
			super.onFailure(statusCode, headers, throwable, errorResponse);
		}
	};

	@Override
	public void onRefresh(int id) {

		mThread.run();

		loadingPDialog.show();
		data.clear();
		goods_data.clear();
		// HttpUtils.getScreenList(res);
		HttpUtils.getHomeGoods(res_getHomeGoods);
	}

	@Override
	public void onLoadMore(int id) {
		// TODO Auto-generated method stub

	}

	void homeScreenList() {
		com.BeeFramework.model.BeeQuery aq = new BeeQuery(getActivity());
		BeeCallback<JSONObject> cb = new BeeCallback<JSONObject>() {
			@Override
			public void callback(String url, JSONObject jo, AjaxStatus status) {
				Log.i("Burl", jo.toString());
			}

		};
		cb.url("http://api.landous.com/api.php?m=user&a=getHomeGoods")
				.type(JSONObject.class).method(Constants.METHOD_POST);
		aq.ajax(cb);
	}

	void pingIpAddr() {
		String result = "";
		String str = "";
		try {
			Process p = Runtime.getRuntime().exec(
					"/system/bin/ping -c " + 1 + " " + "api.landous.com");
			int status = p.waitFor();
			String lost = new String();
			String delay = new String();
			BufferedReader buf = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			// 读出所有信息并显示
			while ((str = buf.readLine()) != null) {
				str = str + "\r\n";
				Log.i("ping", result + str);
			}
			if (status == 0) {
				result = "success";
			} else {
				result = "failed";
			}
			Log.i("ping-to-接口连接情况", result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void OnMessageResponse(String url, JSONObject jo, AjaxStatus status)
			throws JSONException {
		Log.i("Burl", url);

	}

}
