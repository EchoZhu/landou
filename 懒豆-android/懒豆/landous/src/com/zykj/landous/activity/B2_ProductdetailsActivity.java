package com.zykj.landous.activity;

import java.util.ArrayList;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.zykj.landous.LandousAppConst;
import com.zykj.landous.MainActivity;
import com.zykj.landous.R;
import com.zykj.landous.Tools.HttpUtils;
import com.zykj.landous.Tools.ShareUmeng;
import com.zykj.landous.fragment.B2_0_PriductdetailsFragment;
import com.zykj.landous.fragment.MainTabsFrament;

/**
 * 
 * @author zhangxu
 * 
 *         商品详情
 */
public class B2_ProductdetailsActivity extends FragmentActivity implements
		OnClickListener {
	private SharedPreferences shared;
	private SharedPreferences.Editor editor;

	private Intent it;
	/**
	 * 加入购物车
	 */
	private RelativeLayout rl_addspcar;
	/**
	 * 收藏图标
	 */
	public static ImageView iv_collection;
	public static String goods_id = "";
	private ProgressDialog loadingPDialog = null;
	public static ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
	private LinearLayout ll_back;

	String msg = "";
	public static int type = 0;
	/**
	 * 收藏事件触发
	 */
	private FrameLayout fl_collection;

	/**
	 * 购物车
	 */
	private Button btn_spcar;
	/**
	 * 立即购买
	 */
	private Button btn_buynow;

	/**
	 * 分享
	 */
	private LinearLayout ll_share;
	UMSocialService mController = UMServiceFactory
			.getUMSocialService("com.umeng.share");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.b2_activity_productdetails);
		loadingPDialog = new ProgressDialog(B2_ProductdetailsActivity.this);
		loadingPDialog.setMessage("正在加载....");
		loadingPDialog.setCancelable(false);
		iv_collection = (ImageView) findViewById(R.id.iv_collection);
		it = getIntent();
		goods_id = it.getStringExtra("goods_id");
		ll_back = (LinearLayout) findViewById(R.id.ll_back);
		ll_back.setOnClickListener(this);
		ll_share = (LinearLayout) findViewById(R.id.ll_share);
		ll_share.setOnClickListener(this);
		fl_collection = (FrameLayout) findViewById(R.id.fl_collection);
		fl_collection.setOnClickListener(this);
		rl_addspcar = (RelativeLayout) findViewById(R.id.rl_addspcar);
		rl_addspcar.setOnClickListener(this);
		btn_spcar = (Button) findViewById(R.id.btn_spcar);
		btn_spcar.setOnClickListener(this);
		btn_buynow = (Button) findViewById(R.id.btn_buynow);
		btn_buynow.setOnClickListener(this);
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_back:
			this.finish();
			break;
		case R.id.rl_addspcar:
			msg = "加入购物车成功";
			loadingPDialog.show();
			isLogin();
			HttpUtils.addCart(res, goods_id, "1");
			break;
		case R.id.tv_goshop:
			it = new Intent(this, C1_ShopActivity.class);
			this.startActivity(it);
			break;
		case R.id.fl_collection:

		{ 
			isLogin();
			// type = (Integer) (v.getTag() == null ? 0 : v.getTag());
			msg = type % 2 == 0 ? "收藏成功" : "取消收藏";
			int bgid = type % 2 == 0 ? R.drawable.icon_collection_act
					: R.drawable.icon_collection;
			if (type % 2 == 0) {
				loadingPDialog.show();
				HttpUtils.addFavoriteGoods(res, goods_id);
			} else {
				loadingPDialog.show();
				HttpUtils.delFavoriteGoods(res, goods_id);
			}
			type++;
			v.setTag(type);
			iv_collection.setImageResource(bgid);

		}

			break;
		case R.id.btn_buynow:
			isLogin();
			ChoseNum();
			break;
		case R.id.ll_share:
			ShareUmeng mShare = new ShareUmeng(B2_ProductdetailsActivity.this,
					LandousAppConst.url + goods_id);
			mShare.setContent("我在懒豆商城看见这件商品不错，你也来看看吧");
			mShare.show();
			break;
		case R.id.btn_spcar:
			Intent it = new Intent(B2_ProductdetailsActivity.this,
					MainActivity.class);
			MainTabsFrament.type = "tab_four";
			it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(it);
			break;
		}

	}

	private void ChoseNum() {
		LayoutInflater factory = LayoutInflater
				.from(B2_ProductdetailsActivity.this);
		final View view = factory
				.inflate(R.layout.e5_chose_num_conigoods, null);
		final EditText et_num = (EditText) view.findViewById(R.id.et_num);
		AlertDialog.Builder builder = new Builder(
				B2_ProductdetailsActivity.this);
		// builder.setIcon(R.drawable.icon_dialog);
		builder.setTitle("请输入购买数量");
		builder.setView(view);
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				String num = et_num.getText().toString();
				if (num.length() >= 1) {
					int count = Integer.parseInt(num);
					if (count > 0) {
						if (count <= B2_0_PriductdetailsFragment.goods_storage) {
							it = new Intent(B2_ProductdetailsActivity.this,
									D1_OrderConfirmActivity.class);
							it.putExtra("type", 200);
							it.putExtra("count", count);
							it.putExtra("goods_id", goods_id);
							startActivity(it);
						} else {
							Toast.makeText(B2_ProductdetailsActivity.this,
									"商品库存不足", Toast.LENGTH_LONG).show();
						}
					} else {
						Toast.makeText(B2_ProductdetailsActivity.this,
								"购买数量不能为0", Toast.LENGTH_LONG).show();
					}
					dialog.dismiss();

				} else {
					Toast.makeText(B2_ProductdetailsActivity.this, "请输入购买数量",
							Toast.LENGTH_LONG).show();
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
		shared = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
		String userID = shared.getString("member_id", "");
		Log.i("login-user-id", userID);
		if (userID.equals("")) {
			Intent it = new Intent(B2_ProductdetailsActivity.this,
					E6_SigninActivity.class);
			startActivity(it);
			return false;
		}
		return true;
	}

	JsonHttpResponseHandler res = new JsonHttpResponseHandler() {

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
				Toast.makeText(B2_ProductdetailsActivity.this, msg,
						Toast.LENGTH_LONG).show();
				if (msg == "取消收藏") {
				}
			} else if (result == 6793) {
				try {
					String msg = response.getString("message");
					Toast.makeText(getApplicationContext(), msg,
							Toast.LENGTH_LONG).show();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
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
			Toast.makeText(B2_ProductdetailsActivity.this, "网络连接超时",
					Toast.LENGTH_LONG).show();
			super.onFailure(statusCode, headers, throwable, errorResponse);
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/** 使用SSO授权必须添加如下代码 */
		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(
				requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}
}
