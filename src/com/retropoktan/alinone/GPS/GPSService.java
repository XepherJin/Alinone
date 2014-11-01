package com.retropoktan.alinone.GPS;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;

public class GPSService extends Service{

	private static final long minTime = 5000; // 5s
	private static final long minDistance = 10;
	String tag = this.toString();
	private LocationManager locationManager;
	private LocationListener locationListener;
	private final IBinder mBinder = new GPSServiceBinder();
	
	public void startService() {
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	public class GPSServiceBinder extends Binder{
		GPSService getService() {
			return GPSService.this;
		}
	}
}
