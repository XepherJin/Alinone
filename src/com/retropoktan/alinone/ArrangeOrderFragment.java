package com.retropoktan.alinone;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.retropoktan.alinone.alinoneDao.Dish;
import com.retropoktan.alinone.alinoneDao.isMarked;
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
	
	public List<AlinoneOrder> orderList = new ArrayList<AlinoneOrder>();
	private List<Dish> dishList;
	
	public List<isMarked> checkList;
	
	private int isExpanded;
	
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
		dishList = new ArrayList<Dish>();
		checkList = new ArrayList<isMarked>();
		readOrdersInfo();
		getBindOrders();
	}
	
	public void cancelOrder(AlinoneOrder order) {
		dbService.deleteDishesFromOrder(order);
		dbService.deleteOrder(order);
		
	}
	
	public void setAddOrderButtonShow() {
		addOrderButton.setVisibility(View.VISIBLE);
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
					jsonObjectInside.put("order_id", String.valueOf(alinoneOrder.getOrderID()));
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
						dbService.deleteAllDishes();
						orderList.clear();
						adapter.notifyDataSetChanged();
						addOrderButton.setVisibility(View.VISIBLE);
						Toast.makeText(getActivity().getApplicationContext(), "已完成本次派送任务", Toast.LENGTH_SHORT).show();
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
						Toast.makeText(getActivity().getApplicationContext(), "已完成本次派送任务", Toast.LENGTH_SHORT).show();
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
					jsonObjectInside.put("order_id", String.valueOf(alinoneOrder.getOrderID()));
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
						Toast.makeText(getActivity().getApplicationContext(), "已完成本次派送任务", Toast.LENGTH_SHORT).show();
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
						dbService.deleteAllDishes();
						orderList.clear();
						adapter.notifyDataSetChanged();
						addOrderButton.setVisibility(View.VISIBLE);
						Toast.makeText(getActivity().getApplicationContext(), "已完成本次派送任务", Toast.LENGTH_SHORT).show();
					}
				});
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void commitOneOrder(AlinoneOrder order) {
		orderList.remove(order);
		dbService.deleteDishesFromOrder(order);
		dbService.deleteOrder(order);
	}
	
	@SuppressLint("UseValueOf") 
	public void readOrdersInfo() {
		orderList.clear();
		checkList.clear();
		for (AlinoneOrder order : dbService.loadAllOrders()) {
			orderList.add(order);
			isMarked isMarked = new isMarked(0);
			checkList.add(isMarked);
		}
		if (orderList.size() <= 0) {
			addOrderButton.setVisibility(View.VISIBLE);
		}
		else {
			addOrderButton.setVisibility(View.GONE);
		}
		adapter = new OrderListAdapter(orderList, getActivity(), this, isExpanded, checkList);
		currentOrderListView.setAdapter(adapter);
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
							dbService.deleteAllDishes();
							orderList.clear();
							checkList.clear();
							JSONArray orderArray = ((JSONObject)response.get("body")).getJSONArray("order_list");
							for (int i = 0; i < orderArray.length(); i++) {
								isMarked isMarked = new isMarked(0);
								checkList.add(isMarked);
								JSONObject orderObject = orderArray.getJSONObject(i);
								JSONArray dishArray = orderObject.getJSONArray("dish_list");
								Log.v("arrange", dishArray.toString());
								AlinoneOrder order = new AlinoneOrder(orderObject.get("order_id").toString(), orderObject.get("phone").toString(), orderObject.get("address").toString(), orderObject.get("merchant_id").toString(), new Date(),
										orderObject.getString("name"), orderObject.getBoolean("if_pay"), Float.valueOf(orderObject.get("price").toString()));
								dbService.saveOrder(order);
								orderList.add(order);
								for (int j = 0; j < dishArray.length(); j++) {
									JSONObject dishObject = dishArray.getJSONObject(j);
									Dish dish = new Dish(dishObject.getString("name"), dishObject.getInt("count"), Float.valueOf(dishObject.get("price").toString()), order.getOrderID());
									dishList.add(dish);
								}
								dbService.saveDishLists(dishList, order);
								dishList.clear();
							}
							Toast.makeText(getActivity().getApplicationContext(), "获取已绑定订单成功", Toast.LENGTH_SHORT).show();
							if (orderList.size() > 0) {
								addOrderButton.setVisibility(View.GONE);
							}
							else {
								addOrderButton.setVisibility(View.VISIBLE);
							}
							adapter.notifyDataSetChanged();
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
