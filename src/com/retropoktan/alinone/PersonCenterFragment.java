package com.retropoktan.alinone;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.retropoktan.alinone.adapter.PersonCenterAdapter;
import com.retropoktan.alinone.alinoneDao.DBService;
import com.retropoktan.alinone.alinoneDao.Merchant;
import com.retropoktan.alinone.alinoneDao.MerchantDao;
import com.retropoktan.alinone.alinoneDao.MerchantDao.Properties;
import com.retropoktan.alinone.alinoneDao.TodayOrder;
import com.retropoktan.alinone.netutil.HttpUtil;
import com.retropoktan.alinone.netutil.URLConstants;
public class PersonCenterFragment extends Fragment{

	private ListView merchantListView;
	private PersonCenterAdapter adapter;
	
	private DBService dbService;
	
	private TextView userNickName;
	private TextView merchantName;
	private TextView orderNum;
	private Button bindMerchantButton;
	
	private Context context;
	
	public static final int REFRESH_PREORDERS = 1;
	
	private List<Merchant> merchantList;
	private List<TodayOrder> todayOrderList;
	private int REQUEST_CODE = 1;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 1:
				try {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("private_token", BaseApplication.getInstance().getToken());
					StringEntity stringEntity = new StringEntity(String.valueOf(jsonObject));
					HttpUtil.post(context, URLConstants.GetPreOrdersUrl, stringEntity, URLConstants.ContentTypeJson, new JsonHttpResponseHandler(){

						@Override
						public void onFailure(int statusCode, Header[] headers,
								String responseString, Throwable throwable) {
							// TODO Auto-generated method stub
							super.onFailure(statusCode, headers, responseString, throwable);
						}

						@Override
						public void onSuccess(int statusCode, Header[] headers,
								JSONObject response) {
							// TODO Auto-generated method stub
							try {
								if (response.get("status").toString().equals("1")) {
									JSONArray jsonArray = ((JSONObject)response.get("body")).getJSONArray("merchants");
									for (int i = 0; i < jsonArray.length(); i++) {
										JSONObject preObject = jsonArray.getJSONObject(i);
										List<Merchant> list = dbService.getMerchantDao().queryBuilder().where(Properties.MerchantID.eq(preObject.getString("merchant_id"))).list();
										Log.d("preorder", list.toString());
										if (!list.isEmpty()) {
											Merchant merchant = list.get(0);
											merchant.setPreOrderNum(preObject.getInt("count"));
											dbService.saveMerchant(merchant);
										}
									}
									merchantListView.setAdapter(adapter);
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
					});
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
		}
		
	};
	
	private void startGetPreOrders() {
		stopGetPreOrders();
		mHandler.postDelayed(getPreOrdersTask, 10000);
	}
	
	private void stopGetPreOrders() {
		mHandler.removeCallbacks(getPreOrdersTask);
	}
	
	private Runnable getPreOrdersTask = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			mHandler.sendEmptyMessage(REFRESH_PREORDERS);
			startGetPreOrders();
		}
	};

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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		View personCenterLayout = inflater.inflate(R.layout.person_center, container, false);
		context = getActivity().getApplicationContext();
		userNickName = (TextView)personCenterLayout.findViewById(R.id.user_nick_name_from_person_center);
		initButton(personCenterLayout);
		initListView(personCenterLayout);
		return personCenterLayout;
	}
	
	public interface OnRefreshTodayOrder{
		public void refresh();
	}
	
	private void initButton(View parentView) {
		bindMerchantButton = (Button)parentView.findViewById(R.id.bind_merchant_button);
		bindMerchantButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity().getApplicationContext(), ScanQRCodeActivity.class);
				startActivityForResult(intent, REQUEST_CODE);
			}
		});
	}
	
	private void initListView(View parentView) {
		userNickName.setText("欢迎您！" + BaseApplication.getInstance().getNickName() + "！");
		merchantListView = (ListView)parentView.findViewById(R.id.person_center_listview);
		merchantName = (TextView)parentView.findViewById(R.id.merchant_name_text_view);
		orderNum = (TextView)parentView.findViewById(R.id.order_num_textview);
		merchantList = new ArrayList<Merchant>();
		todayOrderList = new ArrayList<TodayOrder>();
		readMerchantInfo();
		getMerchantInfo();
	}

	class MerchantListOnItemCLickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			final Merchant merchant = merchantList.get(arg2);
			new AlertDialog.Builder(getActivity())
			.setTitle("取消绑定")
			.setMessage("取消您与 " + merchant.getMerchantName() + " 的绑定吗？")
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					try {
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("merchant_id", merchant.getMerchantID());
						jsonObject.put("private_token", BaseApplication.getInstance().getToken());
						StringEntity stringEntity = new StringEntity(String.valueOf(jsonObject));
						HttpUtil.post(getActivity().getApplicationContext(), URLConstants.UnBindMerchantUrl, stringEntity, URLConstants.ContentTypeJson, new JsonHttpResponseHandler() {

							@Override
							public void onFailure(int statusCode, Header[] headers,
									Throwable throwable, JSONObject errorResponse) {
								// TODO Auto-generated method stub
								Toast.makeText(getActivity().getApplicationContext(), "网络超时", Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onSuccess(int statusCode, Header[] headers,
									JSONObject response) {
								// TODO Auto-generated method stub
								try {
									if (response.get("status").toString().equals("1")) {
										merchantList.remove(merchant);
										adapter.notifyDataSetChanged();
										dbService.deleteMerchant(merchant);
									}
									else {
										Toast.makeText(getActivity().getApplicationContext(), "解除绑定失败", Toast.LENGTH_SHORT).show();
									}
								} catch (JSONException e) {
									// TODO: handle exception
								}
							}
						});
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			})
			.setNegativeButton("取消", null)
			.show();
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
			if (resultCode == Activity.RESULT_OK) {
				merchantList.clear();
				readMerchantInfo();
			}
	}
	
	

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putBoolean("isHidden", true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		inflater.inflate(R.menu.alinone_person_center, menu);
	}
	
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.refresh_from_action_bar:
			getMerchantInfo();
			break;
		case R.id.scan_qr_code_from_person_center:
			Intent intent = new Intent(getActivity().getApplicationContext(), ScanQRCodeActivity.class);
			startActivityForResult(intent, REQUEST_CODE);
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void getMerchantInfo() {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("private_token", BaseApplication.getInstance().getToken());
			StringEntity stringEntity = new StringEntity(String.valueOf(jsonObject));
			HttpUtil.post(getActivity().getApplicationContext(), URLConstants.TodayPersonInfo, stringEntity, URLConstants.ContentTypeJson, new JsonHttpResponseHandler() {

				@Override
				public void onFailure(int statusCode, Header[] headers,
						Throwable throwable, JSONObject errorResponse) {
					// TODO Auto-generated method stub
					Toast.makeText(context, "连接超时", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onSuccess(int statusCode, Header[] headers,
						JSONObject response) {
					// TODO Auto-generated method stub
					try {
						if (response.get("status").toString().equals("1")) {
							JSONArray jsonArray = ((JSONObject)response.get("body")).getJSONArray("merchants");
							dbService.deleteAllMerchants();
							dbService.deleteAllTodayOrders();
							merchantList.clear();
							todayOrderList.clear();
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject jsonObject = jsonArray.getJSONObject(i);
								JSONArray sendedJsonArray = jsonObject.getJSONArray("sended");
								Log.v("teset", sendedJsonArray.toString());
								todayOrderList.clear();
								Merchant merchant = new Merchant(jsonObject.get("merchant_id").toString(), jsonObject.get("merchant_name").toString(), sendedJsonArray.length(), 0);
								merchantList.add(merchant);
								dbService.saveMerchant(merchant);
								getTodayOrder(sendedJsonArray, todayOrderList, merchant);
							}
							if (merchantList.size() > 0) {
								bindMerchantButton.setVisibility(View.GONE);
							}
							else {
								bindMerchantButton.setVisibility(View.VISIBLE);
							}
							adapter.setMerchantListData(merchantList);
							merchantListView.setAdapter(adapter);
							try {
								JSONObject jsonObject = new JSONObject();
								jsonObject.put("private_token", BaseApplication.getInstance().getToken());
								StringEntity stringEntity = new StringEntity(String.valueOf(jsonObject));
								HttpUtil.post(context, URLConstants.GetPreOrdersUrl, stringEntity, URLConstants.ContentTypeJson, new JsonHttpResponseHandler(){

									@Override
									public void onFailure(int statusCode, Header[] headers,
											String responseString, Throwable throwable) {
										// TODO Auto-generated method stub
										super.onFailure(statusCode, headers, responseString, throwable);
									}

									@Override
									public void onSuccess(int statusCode, Header[] headers,
											JSONObject response) {
										// TODO Auto-generated method stub
										try {
											if (response.get("status").toString().equals("1")) {
												JSONArray jsonArray = ((JSONObject)response.get("body")).getJSONArray("merchants");
												for (int i = 0; i < jsonArray.length(); i++) {
													JSONObject preObject = jsonArray.getJSONObject(i);
													List<Merchant> list = dbService.getMerchantDao().queryBuilder().where(Properties.MerchantID.eq(preObject.getString("merchant_id"))).list();
													Log.d("preorder", list.toString());
													if (!list.isEmpty()) {
														Merchant merchant = list.get(0);
														merchant.setPreOrderNum(preObject.getInt("count"));
														dbService.saveMerchant(merchant);
													}
												}
												merchantListView.setAdapter(adapter);
											}
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
									
								});
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							startGetPreOrders();
						}
						else {
							Toast.makeText(context, "获取信息失败", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						// TODO: handle exception
					}
				}
			});
		} catch (JSONException e) {
			// TODO: handle exception
		} catch (UnsupportedEncodingException e) {
			// TODO: handle exception
		}
	}
	
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		startGetPreOrders();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		stopGetPreOrders();
	}

	private void readMerchantInfo() {
		for (Merchant merchant : dbService.loadAllMerchants()) {
			merchantList.add(merchant);
		}
		if (merchantList.size() <= 0) {
			bindMerchantButton.setVisibility(View.VISIBLE);
		}
		else {
			bindMerchantButton.setVisibility(View.GONE);
		}
		adapter = new PersonCenterAdapter(merchantList, getActivity());
		merchantListView.setAdapter(adapter);
		//merchantListView.setOnItemClickListener(new MerchantListOnItemCLickListener());
	}
	
	private void getTodayOrder(JSONArray jsonArray, List<TodayOrder> list, Merchant merchant) {
		try {
			list.clear();
			if (jsonArray.length() > 0) {
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					Log.v("list", list.toString());
					TodayOrder todayOrder = new TodayOrder(jsonObject.getString("alin_id"), jsonObject.getInt("platform"), jsonObject.getBoolean("online_pay"), 
							jsonObject.getString("phone"), Float.valueOf(jsonObject.get("price").toString()), jsonObject.getString("send_time"), 0, merchant.getMerchantID());
					list.add(todayOrder);
					Log.v("oderasdasdasd", list.toString());
				}
				dbService.saveTodayDishes(list, merchant);
			}
		}
		catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
