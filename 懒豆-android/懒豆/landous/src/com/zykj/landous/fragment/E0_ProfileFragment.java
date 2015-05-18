package com.zykj.landous.fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.BeeFramework.BeeFrameworkApp;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zykj.landous.LandousAppConst;
import com.zykj.landous.R;
import com.zykj.landous.Tools.HttpUtils;
import com.zykj.landous.Tools.Image.CircularImage;
import com.zykj.landous.activity.CollectActivity;
import com.zykj.landous.activity.E1_Activity_getPointsLog;
import com.zykj.landous.activity.E1_EceiptActivity;
import com.zykj.landous.activity.E1_NO_PaymentAvtivity;
import com.zykj.landous.activity.E1_PaymentAvtivity;
import com.zykj.landous.activity.E1_SettingActivity;
import com.zykj.landous.activity.E1_ShipActivity;
import com.zykj.landous.activity.E2_AddressManageActivity;
import com.zykj.landous.activity.E5_CoinStoreActivity;
import com.zykj.landous.activity.E6_SigninActivity;
import com.zykj.landous.view.BadgeView;

/**
 * 会员中心
 * 
 * @author zhangxu
 * 
 */
public class E0_ProfileFragment extends Fragment implements OnClickListener {

	private View view;
	private View headView;
	private ListView listView;

	private ImageView setting;
	private CircularImage avatar_head_image;
	private ImageView camera;

	private TextView name;
	private TextView coin_num;
	private TextView pre_deposit;
	/**
	 * 待付款
	 */
	private FrameLayout nopayment;
	/**
	 * 待发货
	 */
	private FrameLayout payment;
	private TextView payment_num;
	/**
	 * 已收货
	 */
	private FrameLayout ship;
	private TextView ship_num;
	private FrameLayout receipt;
	private TextView receipt_num;

	private TextView history_num;
	private TextView collect_num;

	private LinearLayout star_thing;
	private LinearLayout coin_store;
	private LinearLayout address_manage;

	private LinearLayout memberLevelLayout;
	private TextView memberLevelName;
	private ImageView memberLevelIcon;

	private LinearLayout help;
	private FrameLayout avatar_login_area;

	private SharedPreferences shared;
	private SharedPreferences.Editor editor;

	private ImageView image;
	private String uid;
	public static boolean isRefresh = false;

	protected Context mContext;

	private final static int REQUEST_SIGN_IN = 1000;
	// 其他信息
	private ImageView security_img;
	private ImageView vip_icon;
	private TextView welcome_word;
	private TextView security;
	private TextView login_or_regist;
	private LinearLayout number_info;
	private ImageView profile_check;
	private ProgressDialog loadingPDialog = null;
	/**
	 * 积分
	 */
	private LinearLayout profile_head_address_manage;
	/**
	 * 上传头像的字段
	 */
	private String timeString;
	private String filename;
	private String cutnameString;
	/**
	 * 待付款
	 */
	private BadgeView badge_new;
	/**
	 * 待发货
	 */
	private BadgeView badge_pay;

	/**
	 * 待收货
	 */
	private BadgeView badge_send;
	/**
	 * 已收货
	 */
	private BadgeView badge_success;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		shared = getActivity().getSharedPreferences("loginInfo",
				Activity.MODE_PRIVATE);
		editor = shared.edit();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.e0_profile, null);

		mContext = getActivity();

		shared = getActivity().getSharedPreferences("loginInfo", 0);
		editor = shared.edit();

		headView = LayoutInflater.from(getActivity()).inflate(
				R.layout.e0_profile_fragment, null);
		avatar_head_image = (CircularImage) headView
				.findViewById(R.id.avatar_head_image);
		avatar_head_image.setOnClickListener(this);

		avatar_login_area = (FrameLayout) headView
				.findViewById(R.id.profile_avatar_login_area);
		avatar_login_area.setOnClickListener(this);
		profile_check = (ImageView) view.findViewById(R.id.profile_check);
		profile_check.setOnClickListener(this);
		loadingPDialog = new ProgressDialog(getActivity());
		loadingPDialog.setMessage("正在签到....");
		loadingPDialog.setCancelable(false);

		image = (ImageView) view.findViewById(R.id.profile_setting);
		image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(getActivity(),
						E1_SettingActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.push_right_in,
						R.anim.push_right_out);
			}
		});
		listView = (ListView) view.findViewById(R.id.profile_list);
		listView.addHeaderView(headView);

		listView.setAdapter(null);

		memberLevelLayout = (LinearLayout) headView
				.findViewById(R.id.member_level_layout);
		memberLevelName = (TextView) headView.findViewById(R.id.member_level);

		setting = (ImageView) headView.findViewById(R.id.profile_head_setting);

		// camera = (ImageView) headView.findViewById(R.id.profile_head_camera);

		nopayment = (FrameLayout) headView
				.findViewById(R.id.profile_head_nopayment);

		nopayment.setOnClickListener(this);
		payment = (FrameLayout) headView
				.findViewById(R.id.profile_head_payment);
		payment.setOnClickListener(this);
		payment_num = (TextView) headView
				.findViewById(R.id.profile_head_payment_num);

		ship = (FrameLayout) headView.findViewById(R.id.profile_head_ship);
		ship_num = (TextView) headView.findViewById(R.id.profile_head_ship_num);

		receipt = (FrameLayout) headView
				.findViewById(R.id.profile_head_receipt);
		receipt_num = (TextView) headView
				.findViewById(R.id.profile_head_receipt_num);

		history_num = (TextView) headView
				.findViewById(R.id.profile_head_history_num);

		collect_num = (TextView) headView
				.findViewById(R.id.profile_head_collect_num);
		address_manage = (LinearLayout) headView
				.findViewById(R.id.profile_address_page);
		coin_store = (LinearLayout) headView
				.findViewById(R.id.profile_coin_store);
		star_thing = (LinearLayout) headView
				.findViewById(R.id.profile_star_thing);

		address_manage.setOnClickListener(this);
		coin_store.setOnClickListener(this);
		star_thing.setOnClickListener(this);

		setting.setOnClickListener(this);
		// camera.setOnClickListener(this);
		nopayment.setOnClickListener(this);
		ship.setOnClickListener(this);
		receipt.setOnClickListener(this);

		address_manage.setOnClickListener(this);
		name = (TextView) headView.findViewById(R.id.profile_head_name);
		coin_num = (TextView) headView.findViewById(R.id.coin_num);
		pre_deposit = (TextView) headView.findViewById(R.id.pre_deposit);
		name.setOnClickListener(this);
		security = (TextView) headView.findViewById(R.id.profile_security);
		security_img = (ImageView) headView
				.findViewById(R.id.profile_security_img);
		welcome_word = (TextView) headView
				.findViewById(R.id.profile_welcome_words);
		vip_icon = (ImageView) headView.findViewById(R.id.profile_vip_icon);
		login_or_regist = (TextView) headView
				.findViewById(R.id.login_or_regist);
		number_info = (LinearLayout) headView
				.findViewById(R.id.profile_number_info);
		profile_head_address_manage = (LinearLayout) headView
				.findViewById(R.id.profile_head_address_manage);
		profile_head_address_manage.setOnClickListener(this);
		badge_new = new BadgeView(getActivity(), nopayment);
		badge_new.setTextColor(Color.WHITE);
		badge_new.setBadgeBackgroundColor(Color.parseColor("#ff9912"));
		badge_pay = new BadgeView(getActivity(), payment);
		badge_pay.setTextColor(Color.WHITE);
		badge_pay.setBadgeBackgroundColor(Color.parseColor("#ff9912"));
		badge_send = new BadgeView(getActivity(), ship);
		badge_send.setTextColor(Color.WHITE);
		badge_send.setBadgeBackgroundColor(Color.parseColor("#ff9912"));
		badge_success = new BadgeView(getActivity(), receipt);
		badge_success.setTextColor(Color.WHITE);
		badge_success.setBadgeBackgroundColor(Color.parseColor("#ff9912"));
		return view;

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		String check = shared.getString("check_status", "0");
		if (check.equals("0")) {
			profile_check.setImageResource(R.drawable.profile_check);
		} else {
			profile_check.setImageResource(R.drawable.profile_checked);
		}
		if (isLogin()) {
			HttpUtils.refreshUserInfo(res_refresh);
			name.setVisibility(View.VISIBLE);
			security.setVisibility(View.VISIBLE);
			security_img.setVisibility(View.VISIBLE);
			welcome_word.setVisibility(View.VISIBLE);
			vip_icon.setVisibility(View.VISIBLE);
			login_or_regist.setVisibility(View.GONE);
			number_info.setVisibility(View.VISIBLE);

			String avatar_head = shared.getString("member_avatar", "");
			String url = LandousAppConst.avatar_url_head + avatar_head;
			ImageLoader.getInstance().displayImage(url, avatar_head_image
					);
			name.setText(shared.getString("member_name", ""));
			coin_num.setText("积分：" + shared.getString("member_points", "0"));
			pre_deposit.setText("预存款：￥"
					+ shared.getString("available_predeposit", "0.00"));
		} else {
			String avatar_head = shared.getString("member_avatar", "");
			String url = LandousAppConst.avatar_url_head + avatar_head;
			ImageLoader.getInstance().displayImage(url, avatar_head_image,
					BeeFrameworkApp.options_circle);
			name.setVisibility(View.GONE);
			security.setVisibility(View.GONE);
			security_img.setVisibility(View.GONE);
			welcome_word.setVisibility(View.GONE);
			vip_icon.setVisibility(View.GONE);
			login_or_regist.setVisibility(View.VISIBLE);
			number_info.setVisibility(View.GONE);
			coin_num.setText("积分：" + shared.getString("member_points", "0"));
			pre_deposit.setText("预存款：￥"
					+ shared.getString("available_predeposit", "0.00"));
		}
		if (badge_new.isShown()) {
			badge_new.toggle();
		}
		if(badge_pay.isShown())
		badge_pay.toggle();
		if(badge_send.isShown())
		badge_send.toggle();
		if(badge_success.isShown())
		badge_success.toggle();
	}

	public Boolean isLogin() {
		shared = getActivity().getSharedPreferences("loginInfo",
				Activity.MODE_PRIVATE);
		String userID = shared.getString("member_id", "");
		Log.i("login-user-id", userID);
		if (userID.equals("")) {

			return false;
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.profile_address_page:
			intent = new Intent(getActivity(), E2_AddressManageActivity.class);
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.push_right_in,
					R.anim.push_right_out);

			break;
		case R.id.profile_star_thing:
			intent = new Intent(getActivity(), CollectActivity.class);
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.push_right_in,
					R.anim.push_right_out);
			break;
		case R.id.profile_coin_store:
			intent = new Intent(getActivity(), E5_CoinStoreActivity.class);
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.push_right_in,
					R.anim.push_right_out);
			break;
		case R.id.profile_avatar_login_area:
			if (!isLogin()) {
				intent = new Intent(getActivity(), E6_SigninActivity.class);
				startActivityForResult(intent, REQUEST_SIGN_IN);
				getActivity().overridePendingTransition(R.anim.push_right_in,
						R.anim.push_right_out);
			}
			break;
		case R.id.profile_head_nopayment:
			intent = new Intent(getActivity(), E1_NO_PaymentAvtivity.class);
			intent.putExtra("order_state", "10");
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.push_right_in,
					R.anim.push_right_out);
			break;
		case R.id.profile_head_payment:
			intent = new Intent(getActivity(), E1_PaymentAvtivity.class);
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.push_right_in,
					R.anim.push_right_out);
			break;
		case R.id.profile_head_ship:
			intent = new Intent(getActivity(), E1_ShipActivity.class);
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.push_right_in,
					R.anim.push_right_out);
			break;
		case R.id.profile_head_receipt:
			intent = new Intent(getActivity(), E1_EceiptActivity.class);
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.push_right_in,
					R.anim.push_right_out);
			break;
		case R.id.profile_head_address_manage:
			intent = new Intent(getActivity(), E1_Activity_getPointsLog.class);
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.push_right_in,
					R.anim.push_right_out);
			break;
		case R.id.profile_check:
			if (isLogin()) {
				HttpUtils.addCheckPoints(res);
				loadingPDialog.show();
			} else {
				intent = new Intent(getActivity(), E6_SigninActivity.class);
				startActivityForResult(intent, REQUEST_SIGN_IN);
				getActivity().overridePendingTransition(R.anim.push_right_in,
						R.anim.push_right_out);
			}
			break;
		case R.id.avatar_head_image:
			if (isLogin()) {
				if (android.os.Environment.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED)) {
					ShowPickDialog();
				}

			} else {

			}
			break;
		default:
			break;
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		// 如果是直接从相册获取
		case 1:
			try {
				startPhotoZoom(data.getData());
			} catch (Exception e) {
				// TODO: handle exception
				Toast.makeText(getActivity(), "您没有选择任何照片", Toast.LENGTH_LONG)
						.show();
			}

			break;
		// 如果是调用相机拍照时
		case 2:
			// File temp = new File(Environment.getExternalStorageDirectory()
			// + "/xiaoma.jpg");
			// 给图片设置名字和路径
			File temp = new File(Environment.getExternalStorageDirectory()
					.getPath() + "/DCIM/Camera/" + timeString + ".jpg");
			startPhotoZoom(Uri.fromFile(temp));
			break;
		// 取得裁剪后的图片
		case 3:
			/**
			 * 非空判断大家一定要验证，如果不验证的话， 在剪裁之后如果发现不满意，要重新裁剪，丢弃
			 * 当前功能时，会报NullException，小马只 在这个地方加下，大家可以根据不同情况在合适的 地方做判断处理类似情况
			 * 
			 */
			if (data != null) {
				setPicToView(data);
			}
			break;
		default:
			break;

		}
		super.onActivityResult(requestCode, resultCode, data);
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

				loadingPDialog.dismiss();

				AlertDialog.Builder builder = new Builder(getActivity());
				builder.setTitle("签到成功");

				builder.setNegativeButton("确认",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								profile_check
										.setImageResource(R.drawable.profile_checked);
								HttpUtils.refreshUserInfo(res_refresh);
							}

						});
				builder.create().show();

			} else {
				loadingPDialog.dismiss();
				String message = "";
				try {
					message = response.getString("message");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				AlertDialog.Builder builder = new Builder(getActivity());
				builder.setTitle(message);

				builder.setNegativeButton("确认",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {

							}

						});
				builder.create().show();
				Log.i("check_fail", response.toString());

			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers,
				Throwable throwable, JSONObject errorResponse) {
			// TODO Auto-generated method stub
			Toast.makeText(getActivity(), "网络连接超时", Toast.LENGTH_LONG).show();
			super.onFailure(statusCode, headers, throwable, errorResponse);
			loadingPDialog.dismiss();
		}
	};

	JsonHttpResponseHandler res_refresh = new JsonHttpResponseHandler() {

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

					JSONObject userObject = response.getJSONObject("data");

					String userID = userObject.getString("member_id");
					editor.putString("member_id", userID);
					editor.putString("check_status",
							userObject.getString("check_status"));
					Log.i("user_id", userID);
					editor.putString("member_points",
							userObject.getString("member_points") + "");
					editor.putString("available_predeposit",
							userObject.getString("available_predeposit") + "");
					editor.putString("member_name",
							userObject.getString("member_name") + "");
					editor.putString("member_email",
							userObject.getString("member_email") + "");
					editor.putString("member_phone",
							userObject.getString("member_phone") + "");
					editor.putString("member_avatar",
							userObject.getString("member_avatar") + "");
					editor.putString("order_state_new",
							userObject.getString("order_state_new"));

					badge_new.setText(userObject.getString("order_state_new"));
					if (!userObject.getString("order_state_new").equals("0"))
						badge_new.show();

					badge_pay.setText(userObject.getString("order_state_pay"));
					if (!userObject.getString("order_state_pay").equals("0"))
						badge_pay.show();

					badge_send
							.setText(userObject.getString("order_state_send"));
					if (!userObject.getString("order_state_send").equals("0"))
						badge_send.show();

					badge_success.setText(userObject
							.getString("order_state_success"));
					if (!userObject.getString("order_state_success").equals("0"))
						badge_success.show();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				editor.commit();
				String check = shared.getString("check_status", "0");
				if (check.equals("0")) {
					profile_check.setImageResource(R.drawable.profile_check);
				} else {
					profile_check.setImageResource(R.drawable.profile_checked);
				}

			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers,
				Throwable throwable, JSONObject errorResponse) {
			// TODO Auto-generated method stub
			super.onFailure(statusCode, headers, throwable, errorResponse);
			String avatar_head = shared.getString("member_avatar", "");
			String url = LandousAppConst.avatar_url_head + avatar_head;
			ImageLoader.getInstance().displayImage(url, avatar_head_image,
					BeeFrameworkApp.options_circle);
			name.setVisibility(View.GONE);
			security.setVisibility(View.GONE);
			security_img.setVisibility(View.GONE);
			welcome_word.setVisibility(View.GONE);
			vip_icon.setVisibility(View.GONE);
			login_or_regist.setVisibility(View.VISIBLE);
			number_info.setVisibility(View.GONE);
			coin_num.setText("积分：" + shared.getString("member_points", "0"));
			pre_deposit.setText("预存款：￥"
					+ shared.getString("available_predeposit", "0.00"));

		}
	};

	/**
	 * 上传用户头像
	 */
	/**
	 * 选择提示对话框
	 */
	private void ShowPickDialog() {
		new AlertDialog.Builder(getActivity())
				.setTitle("设置头像...")
				.setNegativeButton("相册", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						/**
						 * 刚开始，我自己也不知道ACTION_PICK是干嘛的，后来直接看Intent源码，
						 * 可以发现里面很多东西，Intent是个很强大的东西，大家一定仔细阅读下
						 */
						Intent intent = new Intent(Intent.ACTION_PICK, null);
						/**
						 * 下面这句话，与其它方式写是一样的效果，如果：
						 * intent.setData(MediaStore.Images
						 * .Media.EXTERNAL_CONTENT_URI);
						 * intent.setType(""image/*");设置数据类型
						 * 如果朋友们要限制上传到服务器的图片类型时可以直接写如
						 * ："image/jpeg 、 image/png等的类型"
						 * 这个地方小马有个疑问，希望高手解答下：就是这个数据URI与类型为什么要分两种形式来写呀？有什么区别？
						 */
						intent.setDataAndType(
								MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
								"image/*");
						startActivityForResult(intent, 1);

					}
				})
				.setPositiveButton("拍照", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
						/**
						 * 下面这句还是老样子，调用快速拍照功能，至于为什么叫快速拍照，大家可以参考如下官方
						 * 文档，you_sdk_path/docs/guide/topics/media/camera.html
						 * 我刚看的时候因为太长就认真看，其实是错的，这个里面有用的太多了，所以大家不要认为
						 * 官方文档太长了就不看了，其实是错的，这个地方小马也错了，必须改正
						 */
						Date date = new Date(System.currentTimeMillis());
						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"'IMG'_yyyyMMddHHmmss");
						timeString = dateFormat.format(date);
						createSDCardDir();
						Intent intent = new Intent(
								MediaStore.ACTION_IMAGE_CAPTURE);
						intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
								.fromFile(new File(Environment
										.getExternalStorageDirectory()
										+ "/DCIM/Camera", timeString + ".jpg")));
						startActivityForResult(intent, 2);
						// 下面这句指定调用相机拍照后的照片存储的路径
						/*
						 * intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
						 * .fromFile(new File(Environment
						 * .getExternalStorageDirectory()+"/DCIM",
						 * "testpic.jpg"))); startActivityForResult(intent, 2);
						 */
					}
				}).show();
	}

	public void createSDCardDir() {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			// 创建一个文件夹对象，赋值为外部存储器的目录
			File sdcardDir = Environment.getExternalStorageDirectory();
			// 得到一个路径，内容是sdcard的文件夹路径和名字
			String path = sdcardDir.getPath() + "/DCIM/Camera";
			File path1 = new File(path);
			if (!path1.exists()) {
				// 若不存在，创建目录，可以在应用启动的时候创建
				path1.mkdirs();

			}
		}
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		/*
		 * 至于下面这个Intent的ACTION是怎么知道的，大家可以看下自己路径下的如下网页
		 * yourself_sdk_path/docs/reference/android/content/Intent.html
		 * 直接在里面Ctrl+F搜：CROP ，之前小马没仔细看过，其实安卓系统早已经有自带图片裁剪功能, 是直接调本地库的，小马不懂C C++
		 * 这个不做详细了解去了，有轮子就用轮子，不再研究轮子是怎么 制做的了...吼吼
		 */
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 3);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void setPicToView(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			Drawable drawable = new BitmapDrawable(photo);
			/**
			 * 下面注释的方法是将裁剪之后的图片以Base64Coder的字符方式上 传到服务器，QQ头像上传采用的方法跟这个类似
			 */
			savaBitmap(photo);
			// avatar_head_image.setBackgroundDrawable(drawable);
		}
	}

	// 将剪切后的图片保存到本地图片上！
	public void savaBitmap(Bitmap bitmap) {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyyMMddHHmmss");
		cutnameString = dateFormat.format(date);
		filename = Environment.getExternalStorageDirectory().getPath() + "/"
				+ cutnameString + ".jpg";
		File f = new File(filename);
		FileOutputStream fOut = null;
		try {
			f.createNewFile();
			fOut = new FileOutputStream(f);
		} catch (Exception e) {
			e.printStackTrace();
		}
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);// 把Bitmap对象解析成流
		try {
			fOut.flush();
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		HttpUtils.update(res_update, filename);
	}

	JsonHttpResponseHandler res_update = new JsonHttpResponseHandler() {

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
				Log.i("update", response.toString());
				JSONObject userObject;
				try {
					userObject = response.getJSONObject("data");
					shared = getActivity().getSharedPreferences("loginInfo",
							Activity.MODE_PRIVATE);
					editor = shared.edit();
					String userID = userObject.getString("member_id");
					editor.putString("member_id", userID);
					editor.putString("check_status",
							userObject.getString("check_status"));
					Log.i("user_id", userID);
					editor.putString("member_points",
							userObject.getString("member_points") + "");
					editor.putString("available_predeposit",
							userObject.getString("available_predeposit") + "");
					editor.putString("member_name",
							userObject.getString("member_name") + "");
					editor.putString("member_email",
							userObject.getString("member_email") + "");
					editor.putString("member_phone",
							userObject.getString("member_phone") + "");
					editor.putString("member_avatar",
							userObject.getString("member_avatar") + "");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				editor.commit();
				String avatar_head = shared.getString("member_avatar", "");
				String url = LandousAppConst.avatar_url_head + avatar_head;
				ImageLoader.getInstance().displayImage(url, avatar_head_image,
						BeeFrameworkApp.options_circle);

			}

		}

		@Override
		public void onFailure(int statusCode, Header[] headers,
				Throwable throwable, JSONObject errorResponse) {
			// TODO Auto-generated method stub
			Toast.makeText(getActivity(), "网络连接超时", Toast.LENGTH_LONG).show();
			super.onFailure(statusCode, headers, throwable, errorResponse);
			loadingPDialog.dismiss();
		}
	};
}
