package com.retropoktan.alinone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

public class ArrangeOrderFragment extends Fragment{

	private Button addOrderButton;
	private ListView currentOrderListView;
	
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
}
