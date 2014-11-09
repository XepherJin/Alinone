package com.retropoktan.alinone;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.retropoktan.alinone.adapter.OrderListAdapter;
import com.retropoktan.alinone.alinoneDao.AlinoneOrder;
import com.retropoktan.alinone.alinoneDao.DBService;

public class ArrangeOrderFragment extends Fragment{

	private Button addOrderButton;
	private ListView currentOrderListView;
	private OrderListAdapter adapter;
	private DBService dbService;
	
	private TextView addressTextView;
	private TextView phoneNumberTextView;
	
	private List<AlinoneOrder> orderList = new ArrayList<AlinoneOrder>();
	
	private ArrayList<String> qrCodeList = new ArrayList<String>();
	
	private int REQUEST_CODE = 1;
	
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
				Intent intent = new Intent(getActivity().getApplicationContext(), ScanQRCodeActivity.class);
				startActivityForResult(intent, REQUEST_CODE);
			}
		});
	}
	
	private void initListView(View parentView) {
		currentOrderListView = (ListView)parentView.findViewById(R.id.current_order_listview);
		addressTextView = (TextView)parentView.findViewById(R.id.address_textview);
		phoneNumberTextView = (TextView)parentView.findViewById(R.id.phone_number_textview);
		orderList = new ArrayList<AlinoneOrder>();
		readOrdersInfo();
	}
	
	class OrdersOnItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			final String phoneString = ((AlinoneOrder)orderList.get(arg2)).getObjectPhone().toString();
			new AlertDialog.Builder(getActivity())
			.setTitle("选择联系方式")
			.setItems(new String[]{"打电话", "发短信"}, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					switch (which) {
					case 0:
						Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneString));
						startActivity(intent);
						break;
					case 1:
						sendSMS("10086", "同学~你的外卖到了~请前来领取~~");
						break;
					default:
						break;
					}
				}
			}).setNegativeButton("取消", null)
			.show();
		}
		
	}
	
	public void sendSMS(String phoneNumber, String content) {
		Uri smsUri = Uri.parse("smsto:" + phoneNumber);
		Intent intent = new Intent(Intent.ACTION_SENDTO, smsUri);
		intent.putExtra("sms_body", content);
		startActivity(intent);
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
			startActivityForResult(intent, REQUEST_CODE);
			break;
		case R.id.commit_all_orders:
			if (addOrderButton.getVisibility() == View.GONE) {
				dbService.deleteAllOrders();
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
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == Activity.RESULT_OK) {
			readOrdersInfo();
			Toast.makeText(getActivity(), "OK", Toast.LENGTH_SHORT).show();
		}
		else if (resultCode == Activity.RESULT_CANCELED) {
			readOrdersInfo();
			Toast.makeText(getActivity().getApplicationContext(), "未能绑定全部订单！", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void commmitAllOrders(ArrayList<String> arrayList) {
		
	}
	
	public void readOrdersInfo() {
		orderList.clear();
		for (AlinoneOrder order : dbService.loadAllOrders()) {
			orderList.add(order);
		}
		if (orderList.size() <= 0) {
			addOrderButton.setVisibility(View.VISIBLE);
		}
		else {
			addOrderButton.setVisibility(View.GONE);
		}
		adapter = new OrderListAdapter(orderList, getActivity());
		currentOrderListView.setAdapter(adapter);
		currentOrderListView.setOnItemClickListener(new OrdersOnItemClickListener());
	}
}
