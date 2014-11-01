package com.retropoktan.alinone.GPS;

import java.util.GregorianCalendar;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.retropoktan.alinone.BaseApplication;
import com.retropoktan.alinone.netutil.HttpUtil;
import com.retropoktan.alinone.netutil.URLConstants;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class GPSServiceListener implements LocationListener{

	private static final String TAG = GPSServiceListener.class.getSimpleName();
	private static final float minAccuracyMeters = 35;
	private float latitude;
	private float longitude;
	private Context context;
	
	
	
	public GPSServiceListener(Context context) {
		super();
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		if (location != null) {
			if (location.hasAccuracy() && location.getAccuracy() <= minAccuracyMeters) {
				try {
					latitude = (float)location.getLatitude();
					longitude = (float)location.getLongitude();
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
						}
						
					});
				} catch (Exception e) {
					// TODO: handle exception
				}
			
			}
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
		
	}

}
