package com.zykj.landous.activity;

import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.sourceforge.simcpux.Constants;
import net.sourceforge.simcpux.MD5;
import net.sourceforge.simcpux.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.zykj.landous.R;
import com.zykj.landous.alipay.Fiap;

public class E1_Pay_select_page extends Activity {
	
	String pay_sn="";//订单编号
	double actually_money=0;//支付金额
	int actually_money_wx=0;//支付金额转化成以分为单位，供微信支付使用
	//微信支付
	String TAG = "MicroMsg.SDKSample.PayActivity";
	final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
	PayReq req;
	StringBuffer sb;
	Map<String,String> resultunifiedorder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//隐藏actionbar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pay_select_page);
		overridePendingTransition(R.anim.push_buttom_in,R.anim.push_buttom_out);
		//微信支付
		req = new PayReq();
		sb=new StringBuffer();
		msgApi.registerApp(Constants.APP_ID);
		
	    pay_sn="";//订单编号
	    actually_money=0;//支付金额
		
		Intent intent =getIntent();
		pay_sn=intent.getStringExtra("pay_sn");
		
		actually_money=intent.getDoubleExtra("actually_money",0);
		
		actually_money_wx=(int)(actually_money*100);
	}
	public void  weixinPay(View v)
	{
		Constants.ORDER_NUMBER=pay_sn;//订单编号
//		Toast.makeText(this, "weixinPay"+actually_money_wx, 0).show();
//		if (actually_money_wx!=0) {
			
			GetPrepayIdTask getPrepayId = new GetPrepayIdTask();
			getPrepayId.execute();
//		}else {
//			Toast.makeText(this, "请点击支付按钮进行支付", Toast.LENGTH_SHORT).show();
//		}
		
	}
	public void  aliPay(View v)
	{
//		Toast.makeText(this, "aliPay"+actually_money, 0).show();
		if (actually_money_wx!=0) {
			Fiap fiap = new Fiap(this);
			fiap.setOrder_id(pay_sn);
			fiap.android_pay(actually_money);
		}else {
			Toast.makeText(this, "请点击支付按钮进行支付", Toast.LENGTH_SHORT).show();
		}
		
	}
	public void  back(View v)
	{
		this.finish();
	}
	
	/****************************************************************************************************/
	/****************************************************************************************************/
	/****************************************************************************************************/
	/****************************************************************************************************/
	/****************************************************************************************************/
	//微信支付

	/**
	 * 新版微信支付
	 */
	/**
	 生成签名
	 */

	private String genPackageSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(Constants.API_KEY);
		

		String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
		Log.e("orion",packageSign);
		return packageSign;
	}
	/**
	 * 步骤3：统一下单接口返回正常的prepay_id，再按签名规范重新生成签名后，将数据传输给APP。
	 * 参与签名的字段名为appId，partnerId，prepayId，nonceStr，timeStamp，package。
	 * 注意：package的值格式为Sign=WXPay 
	 * @param params
	 * @return
	 */
	private String genAppSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(Constants.API_KEY);

        this.sb.append("sign str\n"+sb.toString()+"\n\n");
		String appSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
		Log.e("orion-genAppSign",appSign);
		return appSign;
	}
	private String toXml(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();
		sb.append("<xml>");
		for (int i = 0; i < params.size(); i++) {
			sb.append("<"+params.get(i).getName()+">");


			sb.append(params.get(i).getValue());
			sb.append("</"+params.get(i).getName()+">");
		}
		sb.append("</xml>");

		Log.e("orion",sb.toString());
		return sb.toString();
	}

	private class GetPrepayIdTask extends AsyncTask<Void, Void, Map<String,String>> {

		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(E1_Pay_select_page.this, getString(R.string.app_tip), getString(R.string.getting_prepayid));
		}


		@Override
		protected void onCancelled() {
			super.onCancelled();
		}
		/**
		 * 步骤2：商户后台收到用户支付单，调用微信支付统一下单接口。
		 */
		@Override
		protected Map<String,String>  doInBackground(Void... params) {

			String url = String.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
			String entity = genProductArgs();

//			Log.e("orion-GetPrepayIdTask-doInBackground",entity);

			byte[] buf = Util.httpPost(url, entity);

			String content = new String(buf);
//			Log.e("orion", content);
			Map<String,String> xml=decodeXml(content);

			return xml;
		}
		@Override
		protected void onPostExecute(Map<String,String> result) {
			if (dialog != null) {
				dialog.dismiss();
			}
			sb.append("prepay_id\n"+result.get("prepay_id")+"\n\n");
			resultunifiedorder=result;
			
			genPayReq();//步骤3
			
			sendPayReq();//步骤4：商户APP调起微信支付。
		}
	}



	public Map<String,String> decodeXml(String content) {

		try {
			Map<String, String> xml = new HashMap<String, String>();
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new StringReader(content));
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {

				String nodeName=parser.getName();
				switch (event) {
					case XmlPullParser.START_DOCUMENT:

						break;
					case XmlPullParser.START_TAG:

						if("xml".equals(nodeName)==false){
							//实例化student对象
							xml.put(nodeName,parser.nextText());
						}
						break;
					case XmlPullParser.END_TAG:
						break;
				}
				event = parser.next();
			}

			return xml;
		} catch (Exception e) {
			Log.e("orion",e.toString());
		}
		return null;

	}


	private String genNonceStr() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
	}
	
	private long genTimeStamp() {
		return System.currentTimeMillis() / 1000;
	}
	

////订单编号
//	private String genOutTradNo() {
////		Random random = new Random();
////		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
//		return pay_sn;
//	}
//	//订单金额
//	private int genPrice() {
//		return (int)(actually_money*100);
//	}

  //获取订单详情
	private String genProductArgs() {
		StringBuffer xml = new StringBuffer();
		try {
			String	nonceStr = genNonceStr();
			xml.append("</xml>");
            List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
			packageParams.add(new BasicNameValuePair("appid", Constants.APP_ID));
			packageParams.add(new BasicNameValuePair("body",pay_sn));
			packageParams.add(new BasicNameValuePair("mch_id", Constants.MCH_ID));
			packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
			packageParams.add(new BasicNameValuePair("notify_url", "https://open.weixin.qq.com/"));
			packageParams.add(new BasicNameValuePair("out_trade_no",pay_sn));
			packageParams.add(new BasicNameValuePair("spbill_create_ip","127.0.0.1"));
			packageParams.add(new BasicNameValuePair("total_fee",actually_money_wx+""));
			packageParams.add(new BasicNameValuePair("trade_type", "APP"));
			String sign = genPackageSign(packageParams);
			packageParams.add(new BasicNameValuePair("sign", sign));

		   String xmlstring =toXml(packageParams);

			return xmlstring;

		} catch (Exception e) {
			Log.e(TAG, "genProductArgs fail, ex = " + e.getMessage());
			return null;
		}
		

	}
	private void genPayReq() {

		req.appId = Constants.APP_ID;
		req.partnerId = Constants.MCH_ID;
		req.prepayId = resultunifiedorder.get("prepay_id");
		req.packageValue = "Sign=WXPay";
		req.nonceStr = genNonceStr();
		req.timeStamp = String.valueOf(genTimeStamp());


		List<NameValuePair> signParams = new LinkedList<NameValuePair>();
		signParams.add(new BasicNameValuePair("appid", req.appId));
		signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
		signParams.add(new BasicNameValuePair("package", req.packageValue));
		signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
		signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
		signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

		/**
		 * 步骤3：统一下单接口返回正常的prepay_id，再按签名规范重新生成签名后，将数据传输给APP。
		 * 参与签名的字段名为appId，partnerId，prepayId，nonceStr，timeStamp，package。
		 * 注意：package的值格式为Sign=WXPay 
		 */
		req.sign = genAppSign(signParams);

		sb.append("sign\n"+req.sign+"\n\n");

		Log.e("orion-genPayReq", signParams.toString());

	}
	private void sendPayReq() {
		msgApi.registerApp(Constants.APP_ID);
		msgApi.sendReq(req);
	}
	
	
}
