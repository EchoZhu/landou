package com.zykj.landous.Tools;

import android.app.Activity;
import android.view.Gravity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.zykj.landous.R;
import com.zykj.landous.view.CustomShareBoard;

/**
 * @author 作者 zhang
 * @version 创建时间：2015年1月23日 上午9:32:50 类说明 umeng分享
 */
public class ShareUmeng {
	private Activity activity;
	UMSocialService mController = null;
	String url;
	private String content;
	QQShareContent mQQ;
	WeiXinShareContent weixinContent;
	CircleShareContent circleMedia;
	SinaShareContent sinaContent;

	public ShareUmeng(Activity activity, String url) {
		this.url = url;
		this.activity = activity;
		mController = UMServiceFactory.getUMSocialService("com.umeng.share");
		String appID = "wxd4d00764f0763ae9";
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(activity,
				"1104090547", "ev84Wp7qogHvZj8I");
		qqSsoHandler.addToSocialSDK();

		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(activity, appID);
		wxHandler.addToSocialSDK();
		// 支持微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(activity, appID);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
		mQQ = new QQShareContent();
		sinaContent = new SinaShareContent();
		weixinContent = new WeiXinShareContent();
		weixinContent.setTargetUrl(url);
		mQQ.setShareImage(new UMImage(activity,
				"http://ww4.sinaimg.cn/square/90c6c4fcjw1enydemf46rj2028028jr7.jpg"));
		mQQ.setTargetUrl(url);
		weixinContent.setShareImage(new UMImage(activity, R.drawable.ic_app));
		mController.setShareMedia(mQQ);
		circleMedia = new CircleShareContent();

		// 设置朋友圈title
		circleMedia.setTitle("懒豆商城");
		circleMedia.setShareImage(new UMImage(activity, R.drawable.ic_app));
		sinaContent
				.setShareImage(new UMImage(activity,
						"http://ww4.sinaimg.cn/square/90c6c4fcjw1enydemf46rj2028028jr7.jpg"));
		circleMedia.setTargetUrl(url);
		sinaContent.setTargetUrl(url);
		mController.setShareMedia(circleMedia);
		mController.setShareMedia(weixinContent);
		mController.setShareMedia(sinaContent);
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
	}

	public void show() {
		sinaContent.setShareContent(content + url);
		circleMedia.setShareContent(content);
		mQQ.setShareContent(content);
		weixinContent.setShareContent(content);
		mController.setShareContent(content);
		// mController.openShare(activity, false);
		CustomShareBoard shareBoard = new CustomShareBoard(activity);
		shareBoard.showAtLocation(activity.getWindow().getDecorView(),
				Gravity.BOTTOM, 0, 0);
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
