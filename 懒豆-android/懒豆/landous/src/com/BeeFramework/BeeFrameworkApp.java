package com.BeeFramework;

/*
 *	 ______    ______    ______
 *	/\  __ \  /\  ___\  /\  ___\
 *	\ \  __<  \ \  __\_ \ \  __\_
 *	 \ \_____\ \ \_____\ \ \_____\
 *	  \/_____/  \/_____/  \/_____/
 *
 *
 *	Copyright (c) 2013-2014, {Bee} open source community
 *	http://www.bee-framework.com
 *
 *
 *	Permission is hereby granted, free of charge, to any person obtaining a
 *	copy of this software and associated documentation files (the "Software"),
 *	to deal in the Software without restriction, including without limitation
 *	the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *	and/or sell copies of the Software, and to permit persons to whom the
 *	Software is furnished to do so, subject to the following conditions:
 *
 *	The above copyright notice and this permission notice shall be included in
 *	all copies or substantial portions of the Software.
 *
 *	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *	FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 *	IN THE SOFTWARE.
 */

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.security.Security;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.Toast;

import com.BeeFramework.Utils.CustomExceptionHandler;
import com.BeeFramework.activity.DebugCancelDialogActivity;
import com.BeeFramework.activity.DebugTabActivity;
import com.BeeFramework.view.BeeInjector;
import com.external.activeandroid.app.Application;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.pgyersdk.crash.PgyCrashManager;
import com.zykj.landous.R;
import com.zykj.landous.Data.BaseData;
import com.zykj.landous.Tools.HttpUtils;
import com.zykj.landous.activity.D1_OrderConfirmActivity;

public class BeeFrameworkApp extends Application implements OnClickListener {
	private static BeeFrameworkApp instance;
	private ImageView bugImage;
	public Context currContext;

	private WindowManager wManager;
	private boolean flag = true;

	public Handler messageHandler;

	private BeeInjector mInjector;

	public static DisplayImageOptions options; // DisplayImageOptions是用于设置图片显示的类
	public static DisplayImageOptions options_head; // DisplayImageOptions是用于设置图片显示的类
	public static DisplayImageOptions options_circle; // DisplayImageOptions是用于设置图片显示的类
	public static DisplayImageOptions options_rectangle; // DisplayImageOptions是用于设置图片显示的类
	public static DisplayImageOptions options_square;
	public static DisplayImageOptions options_car;// 购物车
	public static DisplayImageOptions options_no_default; // DisplayImageOptions是用于设置图片显示的类

	public static BeeFrameworkApp getInstance() {
		if (instance == null) {
			instance = new BeeFrameworkApp();
		}
		return instance;
	}

	@Override
	public void onCreate() {
		HttpUtils.getDiscountSetting(res);
		instance = this;
		super.onCreate();

		String appId = "497684278eb59d28e43d1e6370a00d4d";  //蒲公英注册或上传应用获取的AppId
        PgyCrashManager.register(this,appId);
		Security.setProperty("networkaddress.cache.ttl", String.valueOf(-1));
		Security.setProperty("networkaddress.cache.negative.ttl",
				String.valueOf(0));
		String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + AppConst.LOG_DIR_PATH;
		File storePath = new File(path);
		storePath.mkdirs();
		Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(
				path, null));

		Intent intent = new Intent();
		intent.setAction("com.BeeFramework.NetworkState.Service");
		startService(intent);

		initImageLoader(this);

		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.searcher_no_result_empty_icon) // 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.searcher_no_result_empty_icon) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.default_image) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(false) // 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true)// 设置下载的图片是否缓存在SD卡中
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
				// .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		options_car = new DisplayImageOptions.Builder()
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
				.showStubImage(R.drawable.default_image) // 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.default_image) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.default_image) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(false) // 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
				// .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		options_head = new DisplayImageOptions.Builder()
				.imageScaleType(ImageScaleType.EXACTLY)
				.showStubImage(R.drawable.default_image) // 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.default_image) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.default_image) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(false) // 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
				// .displayer(new RoundedBitmapDisplayer(10)) // 设置成圆角图片
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		options_circle = new DisplayImageOptions.Builder()
				.imageScaleType(ImageScaleType.EXACTLY)
				.showStubImage(R.drawable.profile_head_placeholder_new) // 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.profile_head_placeholder_new) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.profile_head_placeholder_new) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(false) // 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
				.displayer(new RoundedBitmapDisplayer(80)) // 设置成圆角图片
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		options_rectangle = new DisplayImageOptions.Builder()
				.imageScaleType(ImageScaleType.EXACTLY)
				.showStubImage(R.drawable.default_image) // 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.default_image) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.default_image) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(false) // 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
				.resetViewBeforeLoading(false)
				// .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		options_square = new DisplayImageOptions.Builder()
				.imageScaleType(ImageScaleType.EXACTLY)
				.showStubImage(R.drawable.searcher_no_result_empty_icon) // 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.searcher_no_result_empty_icon) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.searcher_no_result_empty_icon) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(false) // 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
				// .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		options_no_default = new DisplayImageOptions.Builder()
				.imageScaleType(ImageScaleType.EXACTLY)
				.showStubImage(R.drawable.icon_default_800_400) //
				.showImageForEmptyUri(R.drawable.icon_default_800_400) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.icon_default_800_400) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(false) // 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
				// .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
				.bitmapConfig(Bitmap.Config.RGB_565).build();

	}

	public void showBug(final Context context) {
		BeeFrameworkApp.getInstance().currContext = context;
		wManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
		wmParams.type = LayoutParams.TYPE_PHONE;
		wmParams.format = PixelFormat.RGBA_8888;
		wmParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
				| LayoutParams.FLAG_NOT_FOCUSABLE;
		wmParams.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
		wmParams.x = 0;
		wmParams.y = 0;

		wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

		if (bugImage != null) { // 判断bugImage是否存在，如果存在则移除，必须加在 new
								// ImageView(context) 前面
			wManager.removeView(bugImage);
		}

		bugImage = new ImageView(context);
		bugImage.setImageResource(R.drawable.bug);
		wManager.addView(bugImage, wmParams);
		bugImage.setOnClickListener(this);

		bugImage.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				DebugCancelDialogActivity.parentHandler = messageHandler;
				Intent intent = new Intent(
						BeeFrameworkApp.getInstance().currContext,
						DebugCancelDialogActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				flag = false;
				return false;
			}
		});

		messageHandler = new Handler() {

			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					wManager.removeView(bugImage);
					bugImage = null; // 必须要把bugImage清空，否则再次进入debug模式会与102行冲突
				}
			}
		};
	}

	public void onClick(View v) {
		if (flag != false) {
			Intent intent = new Intent(
					BeeFrameworkApp.getInstance().currContext,
					com.BeeFramework.activity.DebugTabActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
		flag = true;
	}

	public BeeInjector getInjector() {
		if (mInjector == null) {
			mInjector = BeeInjector.getInstance();
		}
		return mInjector;
	}

	public static void initImageLoader(Context context) {
		File cacheDir = StorageUtils.getCacheDirectory(context);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.memoryCacheExtraOptions(480, 800)
				.threadPoolSize(5)
				.denyCacheImageMultipleSizesInMemory()

				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				// .writeDebugLogs() // Remove for release app
				.memoryCache(new WeakMemoryCache())
				.memoryCacheSize(8 * 1024 * 1024)
				.discCache(new UnlimitedDiscCache(cacheDir))
				// default
				.discCacheSize(50 * 1024 * 1024).discCacheFileCount(100)
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
		Log.i("image_loader_init", "init");
	}

	public static JsonHttpResponseHandler res = new JsonHttpResponseHandler() {

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
						BaseData.min_total_price = jsonItem
								.getDouble("min_total_price");
						BaseData.online_pay_discount = jsonItem
								.getDouble("online_pay_discount");
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	};

	void pingIpAddr() {
		String mPingIpAddrResult = "";
		try {
			String ipAddress = "192.168.199.1";
			Process p = Runtime.getRuntime().exec(
					"ping -c 1 -w 100 " + ipAddress);
			int status = p.waitFor();
			if (status == 0) {
			} else {
				mPingIpAddrResult = "Fail: IP addr not reachable";
			}
		} catch (IOException e) {
			mPingIpAddrResult = "Fail: IOException";
		} catch (InterruptedException e) {
			mPingIpAddrResult = "Fail: InterruptedException";
		}
		Log.i("ping", mPingIpAddrResult);
	}

	public static JsonHttpResponseHandler res_update = new JsonHttpResponseHandler() {
		public void onSuccess(int statusCode, org.apache.http.Header[] headers,
				org.json.JSONObject response) {
			super.onSuccess(statusCode, headers, response);
			int result = 0;
			String msg = "";
			Log.i("landousjson", response + "");
			try {
				result = Integer.valueOf(response.getString("result"));
				msg = response.getString("message").toString();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (result == 1 && statusCode == 200) {

			} else {

			}

		};

		public void onFailure(int statusCode, org.apache.http.Header[] headers,
				Throwable throwable, org.json.JSONObject errorResponse) {
			super.onFailure(statusCode, headers, throwable, errorResponse);
		};
	};
}
