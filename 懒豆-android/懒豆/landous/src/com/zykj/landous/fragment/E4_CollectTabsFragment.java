package com.zykj.landous.fragment;

import com.zykj.landous.R;
import com.zykj.landous.R.layout;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class E4_CollectTabsFragment extends Fragment implements OnClickListener {
	private LinearLayout ll_tab1, ll_tab2;
	private TextView tv_tab1, tv_tab2;

	// 收藏商品列表
	private E4_0_CollectGoodsFragment collectgoods;
	// 收藏店铺列表
	private E4_0_CollectShopsFragment collectShops;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mainView = inflater.inflate(R.layout.e4_collect_tabs_fragment,
				container, false);
		ll_tab1 = (LinearLayout) mainView.findViewById(R.id.ll_tab1);
		ll_tab2 = (LinearLayout) mainView.findViewById(R.id.ll_tab2);
		ll_tab1.setOnClickListener(this);
		ll_tab2.setOnClickListener(this);
		tv_tab1 = (TextView) mainView.findViewById(R.id.tv_tab1);
		tv_tab2 = (TextView) mainView.findViewById(R.id.tv_tab2);
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
		default:
			break;
		}
	}

	private void OnTabSelected(String tabName) {
		if (tabName == "tab_one") {
			if (collectgoods == null) {
				collectgoods = new E4_0_CollectGoodsFragment();
			}
			FragmentTransaction localFragmentTransaction = getFragmentManager()
					.beginTransaction();
			localFragmentTransaction.replace(R.id.fragment_container,
					collectgoods, "tab_one");
			localFragmentTransaction.commit();
			this.tv_tab1.setVisibility(View.VISIBLE);
			this.tv_tab2.setVisibility(View.INVISIBLE);

		} else if (tabName == "tab_two") {
			collectShops = new E4_0_CollectShopsFragment();
			FragmentTransaction localFragmentTransaction = getFragmentManager()
					.beginTransaction();
			localFragmentTransaction.replace(R.id.fragment_container,
					collectShops, "tab_two");
			localFragmentTransaction.commit();
			this.tv_tab2.setVisibility(View.VISIBLE);
			this.tv_tab1.setVisibility(View.INVISIBLE);
		}
	}

}
