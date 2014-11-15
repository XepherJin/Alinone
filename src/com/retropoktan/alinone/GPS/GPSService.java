package com.retropoktan.alinone.GPS;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class GPSService extends Service{

	private static final long minTime = 30 * 1000; // 30s
	String tag = this.toString();
	private LocationManager locationManager;
	private LocationListener locationListener;
	private final IBinder mBinder = new GPSServiceBinder();
	
	public void startService() {
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		locationListener = new GPSServiceListener(GPSService.this);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, 0, locationListener);
	}
	
	public void endService() {
		if (locationManager != null && locationListener != null) {
			locationManager.removeUpdates(locationListener);
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		startService();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		endService();
	}

	public class GPSServiceBinder extends Binder{
		GPSService getService() {
			return GPSService.this;
		}
	}
}
