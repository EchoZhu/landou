package com.zykj.landous.view;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.zykj.landous.R;
import com.zykj.landous.Tools.HttpUtils;
import com.zykj.landous.activity.B2_ProductdetailsActivity;
import com.zykj.landous.activity.E6_SigninActivity;

/**
 * @author 作者 zhang
 * @version 创建时间：2015年1月23日 上午10:09:36 类说明
 */
public class CustomShareBoard extends PopupWindow implements OnClickListener {

	private UMSocialService mController = UMServiceFactory
			.getUMSocialService("com.umeng.share");
	private Activity mActivity;

	public CustomShareBoard(Activity activity) {
		super(activity);
		this.mActivity = activity;
		initView(activity);
	}

	@SuppressWarnings("deprecation")
	private void initView(Context context) {
		View rootView = LayoutInflater.from(context).inflate(
				R.layout.custom_board, null);
		rootView.findViewById(R.id.wechat).setOnClickListener(this);
		rootView.findViewById(R.id.wechat_circle).setOnClickListener(this);
		rootView.findViewById(R.id.qq).setOnClickListener(this);
		rootView.findViewById(R.id.sina).setOnClickListener(this);
		setContentView(rootView);
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		setFocusable(true);
		setBackgroundDrawable(new BitmapDrawable());
		setTouchable(true);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.wechat:
			performShare(SHARE_MEDIA.WEIXIN);
			break;
		case R.id.wechat_circle:
			performShare(SHARE_MEDIA.WEIXIN_CIRCLE);
			break;
		case R.id.qq:
			performShare(SHARE_MEDIA.QQ);
			break;
		case R.id.sina:
			performShare(SHARE_MEDIA.SINA);
			break;
		default:
			break;
		}
	}

	private void performShare(SHARE_MEDIA platform) {
		mController.postShare(mActivity, platform, new SnsPostListener() {

			@Override
			public void onStart() {

			}

			@Override
			public void onComplete(SHARE_MEDIA platform, int eCode,
					SocializeEntity entity) {
				String showText = platform.toString();
				if (eCode == StatusCode.ST_CODE_SUCCESSED) {
					HttpUtils.addSharePoints(res);
				} else {
					showText += "平台分享失败";
				}
				dismiss();
			}
		});
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
				Toast.makeText(mActivity, "分享成功", Toast.LENGTH_LONG).show();
			} else if (result == 0) {
//				Intent it = new Intent(mActivity, E6_SigninActivity.class);
//				mActivity.startActivity(it);
			} else {
				try {
					String msg = response.getString("message");
					Toast.makeText(mActivity, msg, Toast.LENGTH_LONG).show();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	};
}
