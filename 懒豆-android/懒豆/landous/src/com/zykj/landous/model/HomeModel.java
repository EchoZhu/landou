package com.zykj.landous.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.BeeFramework.model.BaseModel;
import com.BeeFramework.model.BeeCallback;
import com.BeeFramework.view.MyProgressDialog;
import com.external.androidquery.callback.AjaxStatus;
import com.external.androidquery.util.Constants;
import com.zykj.landous.ApiInterface;
import com.zykj.landous.LandousAppConst;
import com.zykj.landous.R;
import com.zykj.landous.protocol.SHOT;

/**
 * @author 作者 zhang
 * @version 创建时间：2015年1月5日 上午10:00:19 类说明
 */
public class HomeModel extends BaseModel {
	private static HomeModel instance;
	private Context context;
	String url = ApiInterface.HomeScreenList;

	public ArrayList<Map<String, String>> shotList = new ArrayList<Map<String, String>>();
	/**
	 * 获取首页商品列表
	 */
	public ArrayList<Map<String, String>> homeGoods = new ArrayList<Map<String, String>>();

	public HomeModel(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		instance = this;
		this.context = context;
	}

	/**
	 * 首页轮播图
	 */
	public void homeScreenList() {
		BeeCallback<JSONObject> cb = new BeeCallback<JSONObject>() {
			@Override
			public void callback(String url, JSONObject jo, AjaxStatus status) {
				HomeModel.this.callback(url, jo, status);
				if (null != jo) {
					shotList.clear();
					try {
						JSONArray shotArray = jo.optJSONArray("list");
						for (int i = 0; i < shotArray.length(); i++) {
							JSONObject jsonItem = shotArray.getJSONObject(i);
							Map<String, String> map = new HashMap();
							map.put("pic_id", jsonItem.getString("pic_id"));
							map.put("pic_name", jsonItem.getString("pic_name"));
							map.put("pic_url", jsonItem.getString("pic_url"));
							map.put("pic_img", LandousAppConst.IMG_URL
									+ jsonItem.getString("pic_img"));
							shotList.add(map);
						}
						HomeModel.this.OnMessageResponse(url, jo, status);
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}

			}

		};
		cb.url("http://api.landous.com/api.php?m=user&a=getScreenList").type(JSONObject.class)
				.method(Constants.METHOD_POST);
		MyProgressDialog mPro = new MyProgressDialog(mContext, "请稍后..."
				+ shotList.size());
		aq.progress(mPro.mDialog).ajax(cb);
	}

	/**
	 * 获取首页商品列表 方法不好用，取消使用
	 */
	public void getHomeGoods() {
		BeeCallback<JSONObject> cb = new BeeCallback<JSONObject>() {
			@Override
			public void callback(String url, JSONObject jo, AjaxStatus status) {
				HomeModel.this.callback(url, jo, status);
				if (jo != null) {
					homeGoods.clear();
					try {
						JSONArray shotArray = jo.getJSONArray("list");
						for (int i = 0; i < shotArray.length(); i++) {
							JSONObject jsonItem = shotArray.getJSONObject(i);
							Map<String, String> map = new HashMap();
							map.put("gc_name", jsonItem.getString("gc_name"));
							map.put("goods", jsonItem.getString("goods"));
							homeGoods.add(map);
						}
						HomeModel.this.OnMessageResponse(url, jo, status);
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
			}

		};
		cb.url("api.php?m=user&a=getHomeGoods").type(JSONObject.class)
				.method(Constants.METHOD_GET);
		aq.ajax(cb);
	}
}
