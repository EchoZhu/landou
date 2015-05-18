package com.zykj.landous.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zykj.landous.R;

/**
 * @author 作者 zhang
 * @version 创建时间：2014年12月31日 上午11:40:04 商品详情 Frament
 */
public class ProductdetailsFrament extends Fragment implements OnClickListener {
	private LinearLayout ll_tab1, ll_tab2, ll_tab3;
	private TextView tv_tab1, tv_tab2, tv_tab3;
	private int xlstate=0;//按销量排序的状态，0为第一次点击
	private int hpstate=0;//按好评排序的状态
	private int zxstate=0;//按最新排序的状态
	/**
	 * 宝贝详情
	 */
	private B2_0_PriductdetailsFragment priductDetails;

	/**
	 * 图文详情
	 */
	private B2_0_Graphicdetails graphicDetails;

	/**
	 * 商品评价
	 */
	private B2_0_PriductComment priductcomment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View mainView = inflater.inflate(R.layout.tool_bar_product, container,
				false);
		ll_tab1 = (LinearLayout) mainView.findViewById(R.id.ll_tab1);
		ll_tab2 = (LinearLayout) mainView.findViewById(R.id.ll_tab2);
		ll_tab3 = (LinearLayout) mainView.findViewById(R.id.ll_tab3);
		if (xlstate==0) {
			ll_tab1.setOnClickListener(this);
			xlstate=1;
		}
		ll_tab2.setOnClickListener(this);
		ll_tab3.setOnClickListener(this);
		tv_tab1 = (TextView) mainView.findViewById(R.id.tv_tab1);
		tv_tab2 = (TextView) mainView.findViewById(R.id.tv_tab2);
		tv_tab3 = (TextView) mainView.findViewById(R.id.tv_tab3);
		OnTabSelected("tab_one");
		return mainView;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_tab1:
			OnTabSelected("tab_one");
			break;
		case R.id.ll_tab2:
			OnTabSelected("tab_two");
			break;
		case R.id.ll_tab3:
			OnTabSelected("tab_three");
			break;
		default:
			break;
		}
	}

	private void OnTabSelected(String tabName) {
		if (tabName == "tab_one") {
			if (priductDetails == null) {
				priductDetails = new B2_0_PriductdetailsFragment();
			}
			FragmentTransaction localFragmentTransaction = getFragmentManager()
					.beginTransaction();
			localFragmentTransaction.replace(R.id.fragment_container,
					priductDetails, "tab_one");
			localFragmentTransaction.commit();
			this.tv_tab1.setVisibility(View.VISIBLE);
			this.tv_tab2.setVisibility(View.INVISIBLE);
			this.tv_tab3.setVisibility(View.INVISIBLE);

		} else if (tabName == "tab_two") {
			graphicDetails = new B2_0_Graphicdetails();
			FragmentTransaction localFragmentTransaction = getFragmentManager()
					.beginTransaction();
			localFragmentTransaction.replace(R.id.fragment_container,
					graphicDetails, "tab_two");
			localFragmentTransaction.commit();
			this.tv_tab2.setVisibility(View.VISIBLE);
			this.tv_tab1.setVisibility(View.INVISIBLE);
			this.tv_tab3.setVisibility(View.INVISIBLE);
		} else if (tabName == "tab_three") {
			priductcomment=new B2_0_PriductComment();
			FragmentTransaction localFragmentTransaction = getFragmentManager()
					.beginTransaction();
			localFragmentTransaction.replace(R.id.fragment_container,
					priductcomment, "tab_two");
			localFragmentTransaction.commit();
			this.tv_tab3.setVisibility(View.VISIBLE);
			this.tv_tab1.setVisibility(View.INVISIBLE);
			this.tv_tab2.setVisibility(View.INVISIBLE);
		}

	}

}
