package com.retropoktan.alinone;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.retropoktan.alinone.adapter.OrderListAdapter;
import com.retropoktan.alinone.alinoneDao.AlinoneOrder;
import com.retropoktan.alinone.alinoneDao.DBService;
import com.retropoktan.alinone.netutil.HttpUtil;
import com.retropoktan.alinone.netutil.URLConstants;

public class ArrangeOrderFragment extends Fragment{

	private Button addOrderButton;
	private ListView currentOrderListView;
	private OrderListAdapter adapter;
	private DBService dbService;
	
	private TextView addressTextView;
	private TextView phoneNumberTextView;
	
	private ImageView checkMarkImageView;
	
	private List<AlinoneOrder> orderList = new ArrayList<AlinoneOrder>();
	
	private int[] checkList;
	
	private ArrayList<String> qrCodeList = new ArrayList<String>();
	
	private int REQUEST_CODE = 1;
	private int RESULT_CANCEL_WITHOUT_BIND_ORDERS = 11;
	
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
		if (savedInstanceState != null) {
			if (savedInstanceState.getBoolean("isHidden")) {
				getFragmentManager().beginTransaction().hide(this).commit();
			}
		}
	}
	
	

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putBoolean("isHidden", true);
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
		getBindOrders();
		readOrdersInfo();
	}
	
	class OrderStatusChangeOnItemLongClickListener implements OnItemLongClickListener{

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			// TODO Auto-generated method stub
			final AlinoneOrder order = ((AlinoneOrder)orderList.get(arg2));
			new AlertDialog.Builder(getActivity())
			.setTitle("操作订单")
			.setItems(new String[]{"配送完成", "撤销订单"}, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					switch (which) {
					case 0:
						commitOneOrder(order);
						adapter.notifyDataSetChanged();
						if (orderList.size() <= 0) {
							Toast.makeText(getActivity().getApplicationContext(), "已完成本次派送任务", Toast.LENGTH_SHORT).show();
							commmitAllOrders();
							addOrderButton.setVisibility(View.VISIBLE);
						}
						break;
					case 1:
						orderList.remove(order);
						adapter.notifyDataSetChanged();
						if (orderList.size() <= 0) {
							Toast.makeText(getActivity().getApplicationContext(), "已完成本次派送任务", Toast.LENGTH_SHORT).show();
							commmitAllOrders();
							addOrderButton.setVisibility(View.VISIBLE);
						}
						break;
					default:
						break;
					}
				}
			}).setNegativeButton("取消", null)
			.show();
			return true;
		}
		
	}
	
	class OrdersOnItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, final View arg1, final int arg2,
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
						checkMarkImageView = (ImageView)arg1.findViewById(R.id.check_mark_for_order);
						checkMarkImageView.setVisibility(View.VISIBLE);
						checkList[arg2] = 1;
						adapter.notifyDataSetChanged();
						break;
					case 1:
						sendSMS(phoneString, "同学~你的外卖到了~请前来领取~~");
						checkMarkImageView = (ImageView)arg1.findViewById(R.id.check_mark_for_order);
						checkMarkImageView.setVisibility(View.VISIBLE);
						checkList[arg2] = 1;
						adapter.notifyDataSetChanged();
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
				new AlertDialog.Builder(getActivity())
				.setTitle("提交订单")
				.setMessage("确认全部订单配送完成并提交？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						commmitAllOrders(orderList);
					}
				})
				.setNegativeButton("取消", null)
				.show();
			}
			else {
				Toast.makeText(getActivity().getApplicationContext(), "当前暂无订单提交", Toast.LENGTH_SHORT).show();
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
		}
		else if (resultCode == RESULT_CANCEL_WITHOUT_BIND_ORDERS) {
			readOrdersInfo();
			Toast.makeText(getActivity().getApplicationContext(), "未能绑定全部订单！", Toast.LENGTH_SHORT).show();
		}

	}
	
	public void controlOrdersDelete(List<AlinoneOrder> arrayList) {
		for (AlinoneOrder alinoneOrder : arrayList) {
			dbService.deleteOrder(alinoneOrder);
		}
	}
	
	public void controlOrdersAdd(List<AlinoneOrder> arrayList) {
		for (AlinoneOrder alinoneOrder : arrayList) {
			dbService.saveOrder(alinoneOrder);
		}
	}
	
	public void commmitAllOrders() {
		try {
			JSONObject jsonObject = new JSONObject();
			JSONArray jsonArray = new JSONArray();
			if (dbService.loadAllOrders().size() > 0) {
				for (AlinoneOrder alinoneOrder : dbService.loadAllOrders()) {
					JSONObject jsonObjectInside = new JSONObject();
					jsonObjectInside.put("order_id", alinoneOrder.getOrderID());
					jsonArray.put(jsonObjectInside);
				}
				jsonObject.put("orders_id", jsonArray);
				jsonObject.put("private_token", BaseApplication.getInstance().getToken());
				StringEntity stringEntity = new StringEntity(String.valueOf(jsonObject));
				HttpUtil.post(getActivity().getApplicationContext(), URLConstants.FinishOrdersUrl, stringEntity, URLConstants.ContentTypeJson, new JsonHttpResponseHandler(){

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						// TODO Auto-generated method stub
						Toast.makeText(getActivity().getApplicationContext(), "提交超时", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						// TODO Auto-generated method stub
						dbService.deleteAllOrders();
						orderList.clear();
						adapter.notifyDataSetChanged();
						addOrderButton.setVisibility(View.VISIBLE);
					}
					
				});
			}
			else {
				jsonObject.put("orders_id", jsonArray);
				jsonObject.put("private_token", BaseApplication.getInstance().getToken());
				StringEntity stringEntity = new StringEntity(String.valueOf(jsonObject));
				HttpUtil.post(getActivity().getApplicationContext(), URLConstants.FinishOrdersUrl, stringEntity, URLConstants.ContentTypeJson, new JsonHttpResponseHandler(){

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						// TODO Auto-generated method stub
						Toast.makeText(getActivity().getApplicationContext(), "提交超时", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						// TODO Auto-generated method stub
						dbService.deleteAllOrders();
						orderList.clear();
						adapter.notifyDataSetChanged();
						addOrderButton.setVisibility(View.VISIBLE);
					}
					
				});
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void commmitAllOrders(List<AlinoneOrder> arrayList) {
		controlOrdersDelete(arrayList);
		try {
			JSONObject jsonObject = new JSONObject();
			JSONArray jsonArray = new JSONArray();
			if (dbService.loadAllOrders().size() > 0) {
				for (AlinoneOrder alinoneOrder : dbService.loadAllOrders()) {
					JSONObject jsonObjectInside = new JSONObject();
					jsonObjectInside.put("order_id", alinoneOrder.getOrderID());
					jsonArray.put(jsonObjectInside);
				}
				jsonObject.put("orders_id", jsonArray);
				jsonObject.put("private_token", BaseApplication.getInstance().getToken());
				StringEntity stringEntity = new StringEntity(String.valueOf(jsonObject));
				HttpUtil.post(getActivity().getApplicationContext(), URLConstants.FinishOrdersUrl, stringEntity, URLConstants.ContentTypeJson, new JsonHttpResponseHandler(){

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						// TODO Auto-generated method stub
						Toast.makeText(getActivity().getApplicationContext(), "提交超时", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						// TODO Auto-generated method stub
						dbService.deleteAllOrders();
						orderList.clear();
						adapter.notifyDataSetChanged();
						addOrderButton.setVisibility(View.VISIBLE);
					}
					
				});
			}
			else {
				jsonObject.put("orders_id", jsonArray);
				jsonObject.put("private_token", BaseApplication.getInstance().getToken());
				StringEntity stringEntity = new StringEntity(String.valueOf(jsonObject));
				HttpUtil.post(getActivity().getApplicationContext(), URLConstants.FinishOrdersUrl, stringEntity, URLConstants.ContentTypeJson, new JsonHttpResponseHandler(){

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						// TODO Auto-generated method stub
						Toast.makeText(getActivity().getApplicationContext(), "提交超时", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						// TODO Auto-generated method stub
						Log.v("qqqqqqqqqqqqqqqqqq", response.toString());
						dbService.deleteAllOrders();
						orderList.clear();
						adapter.notifyDataSetChanged();
						addOrderButton.setVisibility(View.VISIBLE);
					}
				});
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void commitOneOrder(AlinoneOrder order) {
		orderList.remove(order);
		dbService.deleteOrder(order);
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
		checkList = new int[orderList.size()];
		adapter = new OrderListAdapter(orderList, getActivity(), checkList);
		currentOrderListView.setAdapter(adapter);
		currentOrderListView.setOnItemLongClickListener(new OrderStatusChangeOnItemLongClickListener());
		currentOrderListView.setOnItemClickListener(new OrdersOnItemClickListener());
	}
	
	public void getBindOrders() {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("private_token", BaseApplication.getInstance().getToken());
			StringEntity stringEntity = new StringEntity(String.valueOf(jsonObject));
			HttpUtil.post(getActivity().getApplicationContext(), URLConstants.GetBindOrdersUrl, stringEntity, URLConstants.ContentTypeJson, new JsonHttpResponseHandler(){

				@Override
				public void onFailure(int statusCode, Header[] headers,
						Throwable throwable, JSONObject errorResponse) {
					// TODO Auto-generated method stub
					Toast.makeText(getActivity().getApplicationContext(), "获取订单超时", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onSuccess(int statusCode, Header[] headers,
						JSONObject response) {
					// TODO Auto-generated method stub
					try {
						if (response.get("status").toString().equals("1")) {
							dbService.deleteAllOrders();
							JSONArray orderArray = ((JSONObject)response.get("body")).getJSONArray("order_list");
							for (int i = 0; i < orderArray.length(); i++) {
								JSONObject orderObject = orderArray.getJSONObject(i);
								Log.d("asdasd", orderObject.toString());
								AlinoneOrder order = new AlinoneOrder(orderObject.get("order_id").toString(), orderObject.get("phone").toString(), orderObject.get("address").toString().trim(), orderObject.get("merchant_id").toString(), new Date());
								dbService.saveOrder(order);
							}
							Toast.makeText(getActivity().getApplicationContext(), "获取已绑定订单成功", Toast.LENGTH_SHORT).show();
							readOrdersInfo();
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
				
			});
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
