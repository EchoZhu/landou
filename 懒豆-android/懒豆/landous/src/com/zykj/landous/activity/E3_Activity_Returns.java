package com.zykj.landous.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.BeeFramework.activity.BaseActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.zykj.landous.LandousAppConst;
import com.zykj.landous.R;
import com.zykj.landous.Tools.HttpUtils;

/**
 * @author 作者 zhang
 * @version 创建时间：2015年1月26日 上午1:54:32 类说明 退款详情
 */
public class E3_Activity_Returns extends BaseActivity implements
		OnClickListener {
	private Intent it;
	String order_id = "";
	String rec_id = "";
	String goods_num = "";
	String goods_pay_price = "";
	String goods_price = "";
	String goods_name = "";
	/**
	 * 订单金额
	 */
	private TextView tv_goods_pay_price;
	/**
	 * 可退金额
	 */
	private TextView tv_pay_price;
	/**
	 * 商品名称
	 */
	private TextView tv_goods_name;
	/**
	 * 商品单价
	 */
	private TextView tv_goods_price;
	/**
	 * 商品数量
	 */
	private TextView tv_goods_num;
	/**
	 * 确定按钮
	 */
	private Button btn_sub;

	/**
	 * checkbox check_1 check_2退款类型，必选。1为退款（不退货）,2为退款退货
	 */
	private CheckBox check_1, check_2;
	private LinearLayout ll_check_1, ll_check_2;
	/**
	 * 退款类型
	 */
	String refund_type = "2";
	/**
	 * 退货金额
	 */
	private EditText et_pay_price;

	/**
	 * 退货数量
	 */
	private EditText et_goos_nnum;
	/**
	 * 退款原因
	 */
	private EditText et_extend_msg;
	private ProgressDialog loadingPDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.e3_activity_returns);
		init();
		loadingPDialog = new ProgressDialog(E3_Activity_Returns.this);
		loadingPDialog.setMessage("正在加载....");
		loadingPDialog.setCancelable(false);
	}

	private void init() {
		it = getIntent();
		order_id = it.getStringExtra("order_id");
		rec_id = it.getStringExtra("rec_id");
		goods_num = it.getStringExtra("goods_num");
		goods_pay_price = it.getStringExtra("goods_pay_price");
		goods_price = it.getStringExtra("goods_price");
		goods_name = it.getStringExtra("goods_name");
		tv_goods_pay_price = (TextView) findViewById(R.id.tv_goods_pay_price);
		tv_goods_pay_price.setText(goods_pay_price);
		tv_pay_price = (TextView) findViewById(R.id.tv_pay_price);
		tv_pay_price.setText(goods_pay_price);
		tv_goods_name = (TextView) findViewById(R.id.tv_goods_name);
		tv_goods_name.setText(goods_name);
		tv_goods_price = (TextView) findViewById(R.id.tv_goods_price);
		tv_goods_price.setText(goods_price);
		tv_goods_num = (TextView) findViewById(R.id.tv_goods_num);
		tv_goods_num.setText(goods_num);
		btn_sub = (Button) findViewById(R.id.btn_sub);
		btn_sub.setOnClickListener(this);
		check_1 = (CheckBox) findViewById(R.id.check_1);
		check_2 = (CheckBox) findViewById(R.id.check_2);
		ll_check_1 = (LinearLayout) findViewById(R.id.ll_check_1);
		ll_check_2 = (LinearLayout) findViewById(R.id.ll_check_2);
		ll_check_1.setOnClickListener(this);
		ll_check_2.setOnClickListener(this);
		et_pay_price = (EditText) findViewById(R.id.et_pay_price);
		et_pay_price.setText(goods_pay_price);
		et_goos_nnum = (EditText) findViewById(R.id.et_goos_nnum);
		et_goos_nnum.setText(goods_num);
		et_extend_msg = (EditText) findViewById(R.id.et_extend_msg);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_sub:
			String extend_msg = et_extend_msg.getText().toString();
			String refund_amount = et_pay_price.getText().toString();
			if (Double.valueOf(refund_amount) > Double.valueOf(goods_pay_price)) {
				Toast.makeText(E3_Activity_Returns.this, "金额输入错误",
						Toast.LENGTH_LONG).show();
			} else {
				loadingPDialog.show();
				HttpUtils.refund(res, order_id, rec_id, refund_type,refund_amount, goods_num, extend_msg);
			}
			break;
		case R.id.ll_check_1:
			check_1.setChecked(true);
			check_2.setChecked(false);
			refund_type = "1";
			break;
		case R.id.ll_check_2:
			check_2.setChecked(true);
			check_1.setChecked(false);
			refund_type = "2";
			break;
		case R.id.iv_back:
			this.finish();
			break;
		default:
			break;
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
			loadingPDialog.dismiss();
			if (result == 1 && statusCode == 200) {
				AlertDialog.Builder builder = new Builder(
						E3_Activity_Returns.this);
//				builder.setMessage("退款提交成功");
				builder.setMessage("您的退款申请已提交，懒豆将在1个工作日内进行审核");

				builder.setTitle("提示");

				builder.setNegativeButton("确认",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
//								finish();
								Intent intent1=new Intent(E3_Activity_Returns.this,E1_EceiptActivity.class);
								startActivity(intent1);
								overridePendingTransition(R.anim.push_left_in,
										R.anim.push_left_out);
							}

						});
				builder.create().show();
			}

		};

		public void onFailure(int statusCode, org.apache.http.Header[] headers,
				Throwable throwable, org.json.JSONObject errorResponse) {
			loadingPDialog.dismiss();
			super.onFailure(statusCode, headers, throwable, errorResponse);
		};
	};
}
