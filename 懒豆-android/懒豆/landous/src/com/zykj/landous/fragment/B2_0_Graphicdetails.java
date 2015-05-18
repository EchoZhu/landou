package com.zykj.landous.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zykj.landous.R;
import com.zykj.landous.activity.B2_ProductdetailsActivity;

/**
 * @author 作者 zhang
 * @version 创建时间：2015年1月3日 下午3:09:32 类说明 图文详情
 */
public class B2_0_Graphicdetails extends Fragment {
	private WebView mWebView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.b2_0_graphicdetails, null);
		mWebView = (WebView) view.findViewById(R.id.webView);
		String url = "http://wap.landous.com/tmpl/product_info.html?goods_id="
				+ B2_ProductdetailsActivity.goods_id;
		mWebView.loadUrl(url);
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		mWebView.setWebViewClient(new MyWebViewClient());

		return view;
	}

	private class MyWebViewClient extends WebViewClient {
		ProgressDialog prDialog = null;

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			prDialog.setCancelable(true);
			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onPageFinished(WebView view, String url) {

			super.onPageFinished(view, url);
			// html加载完成之后，添加监听图片的点击js函数
			prDialog.dismiss();

		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {

			prDialog = ProgressDialog
					.show(getActivity(), null, "网页正在加载中......");
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {

			super.onReceivedError(view, errorCode, description, failingUrl);

		}
	}
}
