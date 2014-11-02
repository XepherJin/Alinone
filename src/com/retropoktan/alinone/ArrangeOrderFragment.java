package com.retropoktan.alinone;

import com.retropoktan.alinone.alinoneDao.DBService;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class ArrangeOrderFragment extends Fragment{

	private Button addOrderButton;
	private ListView currentOrderListView;
	private DBService dbService;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View arrangeOrderLayout = inflater.inflate(R.layout.arrange_order, container, false);
		initButton(arrangeOrderLayout);
		initListView(arrangeOrderLayout);
		return arrangeOrderLayout;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		dbService = DBService.getInstance(getActivity().getApplicationContext());
	}

	private void initButton(View parentView) {
		addOrderButton = (Button)parentView.findViewById(R.id.add_order_button);
		addOrderButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), ScanQRCodeActivity.class);
				startActivity(intent);
			}
		});
	}
	
	private void initListView(View parentView) {
		
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		inflater.inflate(R.menu.alinone_arrange_order, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.scan_qr_code_from_arrange_order:
			Intent intent = new Intent(getActivity().getApplicationContext(), ScanQRCodeActivity.class);
			startActivity(intent);
			break;
		case R.id.commit_all_orders:
			if (addOrderButton.getVisibility() == View.GONE) {
				
			}
			else {
				Toast.makeText(getActivity().getApplicationContext(), "当前暂无订单提交", Toast.LENGTH_SHORT);
			}
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
}
