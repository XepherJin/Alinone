package com.retropoktan.alinone;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.retropoktan.alinone.alinoneDao.Constants;
import com.retropoktan.alinone.alinoneDao.DaoMaster.DevOpenHelper;
import com.retropoktan.alinone.alinoneDao.Merchant;
import com.retropoktan.alinone.netutil.HttpUtil;
import com.retropoktan.alinone.netutil.URLConstants;

public class PersonCenterFragment extends Fragment{

	private ListView merchantListView;
	private PersonCenterAdapter adapter;
	
	private TextView merchantName;
	private TextView orderNum;
	private Button bindMerchantButton;
	
	private Context context;
	
	private List<Merchant> merchantList;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		View personCenterLayout = inflater.inflate(R.layout.person_center, container, false);
		context = getActivity().getApplicationContext();
		DevOpenHelper openHelper = new DevOpenHelper(context, Constants.DB_NAME, null);
		initButton(personCenterLayout);
		initListView(personCenterLayout);
		return personCenterLayout;
	}
	
	private void initButton(View parentView) {
		bindMerchantButton = (Button)parentView.findViewById(R.id.bind_merchant_button);
		bindMerchantButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity().getApplicationContext(), ScanQRCodeActivity.class);
				startActivity(intent);
			}
		});
	}
	
	private void initListView(View parentView) {
		merchantListView = (ListView)parentView.findViewById(R.id.person_center_listview);
		merchantName = (TextView)parentView.findViewById(R.id.merchant_name_text_view);
		orderNum = (TextView)parentView.findViewById(R.id.order_num_textview);
		merchantList = new ArrayList<Merchant>();
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("private_token", BaseApplication.getInstance().getToken());
			StringEntity stringEntity = new StringEntity(String.valueOf(jsonObject));
			HttpUtil.post(getActivity().getApplicationContext(), URLConstants.PersonInfoUrl, stringEntity, URLConstants.ContentTypeJson, new JsonHttpResponseHandler() {

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
							Log.v("jsonarray", jsonArray.toString());
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject jsonObject = jsonArray.getJSONObject(i);
								Merchant merchant = new Merchant(null, jsonObject.get("merchant_id").toString(), jsonObject.get("merchant_name").toString(), Integer.parseInt(jsonObject.get("sended").toString()));
								merchantList.add(merchant);
							}
							adapter = new PersonCenterAdapter(merchantList, getActivity());
							merchantListView.setAdapter(adapter);
							if (merchantList.size() <= 0) {
								bindMerchantButton.setVisibility(View.VISIBLE);
							}
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

	class MerchantListOnItemCLickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			
		}
		
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	
	
}
