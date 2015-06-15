package com.zykj.landous.wxapi;

import net.sourceforge.simcpux.Constants;

import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.zykj.landous.R;
import com.zykj.landous.Tools.HttpUtils;
import com.zykj.landous.activity.Activity_Success;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{
	
	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
	
    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);
        
    	api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);

        api.handleIntent(getIntent(), this);
        
       // Toast.makeText(this, "付款成功", Toast.LENGTH_SHORT).show();
        
  
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		
		Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);

		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			builder.setTitle(R.string.app_tip);
//			//builder.setMessage(getString(R.string.pay_result_callback_msg, resp.errStr +";code=" + String.valueOf(resp.errCode)));
//			builder.setMessage(getString(R.string.pay_result_OK));
//			builder.show();

//			Intent it = new Intent(WXPayEntryActivity.this, MainActivity.class);
//			MainTabsFrament.type = "tab_one";
//			it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			startActivity(it);
			if(resp.errCode==0)//支付成功，展示支付成功页面
			{
				HttpUtils.payOrder(res, Constants.ORDER_NUMBER);
			}else if(resp.errCode==-2)//用户取消支付
			{
				Intent it1 = new Intent();
				it1.setClass(this, Activity_Success.class);
				it1.putExtra("type", "6001");
				startActivity(it1);
			}
			this.finish();
//		      intent.setAction("cn.abel.action.broadcast");
//		      //要发送的内容
//		      intent.putExtra("errCode", resp.errCode);
//		      //发送 一个无序广播
//		      sendBroadcast(intent);
//		      finish();
		}
		
		
	}
	JsonHttpResponseHandler res = new JsonHttpResponseHandler() {
		public void onSuccess(int statusCode, org.apache.http.Header[] headers,
				org.json.JSONObject response) {
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

				AlertDialog.Builder builder = new Builder(WXPayEntryActivity.this);
				builder.setMessage("支付成功，请耐心等待发货");

				builder.setTitle("提示");

				builder.setNegativeButton("确认",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								Intent it = new Intent(WXPayEntryActivity.this,
										Activity_Success.class);
								it.putExtra("type", "success");
								WXPayEntryActivity.this.startActivity(it);
								WXPayEntryActivity.this.finish();
								WXPayEntryActivity.this.overridePendingTransition(
										R.anim.push_left_in,
										R.anim.push_left_out);
							}

						});
				builder.create().show();
			}

		};

		public void onFailure(int statusCode, org.apache.http.Header[] headers,
				Throwable throwable, org.json.JSONObject errorResponse) {
			super.onFailure(statusCode, headers, throwable, errorResponse);
		};
	};
}