package com.retropoktan.alinone.GPS;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.retropoktan.alinone.BaseApplication;
import com.retropoktan.alinone.netutil.HttpUtil;
import com.retropoktan.alinone.netutil.URLConstants;

public class GPSServiceListener implements LocationListener{

	private static final String TAG = GPSServiceListener.class.getSimpleName();
	private float latitude;
	private float longitude;
	private Context context;
	
	public int GPSCurrentStatus;
	
	public GPSServiceListener(Context context) {
		super();
		this.context = context;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		Log.d("gps", latitude + "" + longitude + "");
		if (location != null) {
			latitude = (float)location.getLatitude();
			longitude = (float)location.getLongitude();
				try {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("lng", longitude);
					jsonObject.put("lat", latitude);
					jsonObject.put("private_token", BaseApplication.getInstance().getToken());
					StringEntity stringEntity = new StringEntity(String.valueOf(jsonObject));
					HttpUtil.post(context, URLConstants.GPSUploadUrl, stringEntity, URLConstants.ContentTypeJson, new JsonHttpResponseHandler() {

						@Override
						public void onFailure(int statusCode, Header[] headers,
								Throwable throwable, JSONObject errorResponse) {
							// TODO Auto-generated method stub
							super.onFailure(statusCode, headers, throwable, errorResponse);
						}

						@Override
						public void onSuccess(int statusCode, Header[] headers,
								JSONObject response) {
							// TODO Auto-generated method stub
							try {
								Log.v("status", response.toString());
								if (response.get("status").toString().equals("3")) {
									JSONArray orderArray = ((JSONObject)response.get("body")).getJSONArray("orders_id");
									for (int i = 0; i < orderArray.length(); i++) {
										
									}
								}
							} catch (JSONException e) {
								// TODO: handle exception
								e.printStackTrace();
							}
						}
						
					});
				} catch (Exception e) {
					// TODO: handle exception
			}
		}
		else {
			Toast.makeText(context, "无法获取GPS地理位置", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		GPSCurrentStatus = status;
	}

}
