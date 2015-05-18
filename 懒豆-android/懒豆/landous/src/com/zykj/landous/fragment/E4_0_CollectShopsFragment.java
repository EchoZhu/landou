package com.zykj.landous.fragment;

import com.external.maxwin.view.XListView.IXListViewListener;
import com.zykj.landous.R;
import com.zykj.landous.adapter.C0_ShopsAdapter;
import com.zykj.landous.view.MyListView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class E4_0_CollectShopsFragment extends Fragment implements IXListViewListener,OnClickListener{
	private C0_ShopsAdapter shopsApapter;
	private MyListView mShopsListview;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.e4_collect_shop_fragment, null);
		shopsApapter = new C0_ShopsAdapter(getActivity()
				.getApplicationContext(), null);
		mShopsListview = (MyListView) view.findViewById(R.id.listview_collect_shops);

		mShopsListview.setPullLoadEnable(false);
		mShopsListview.setPullRefreshEnable(true);
		mShopsListview.setXListViewListener(this, 0);
		mShopsListview.setRefreshTime();
		mShopsListview.setAdapter(shopsApapter);
		return view;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRefresh(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoadMore(int id) {
		// TODO Auto-generated method stub
		
	}
}
