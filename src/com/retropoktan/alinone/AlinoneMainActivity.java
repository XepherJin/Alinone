package com.retropoktan.alinone;

import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.retropoktan.alinone.GPS.GPSService;
import com.retropoktan.alinone.netutil.HttpUtil;
import com.retropoktan.alinone.netutil.URLConstants;

public class AlinoneMainActivity extends ActionBarActivity{

	private FragmentManager fragmentManager;
	private ArrangeOrderFragment arrangeOrderFragment;
	private PersonCenterFragment personCenterFragment;
	
	private ImageView arrangeOrderImageView;
	private ImageView personCenterImageView;
	
	private long mExitTime;
	
	private TextView arrangeOrderTextView;
	private TextView personCenterTextView;
	
	private int index;
	
	private LinearLayout arrangeOrderLinearLayout;
	private LinearLayout personCenterLinearLayout;
	
	private Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		openGPSSettings();
		intent = new Intent(AlinoneMainActivity.this, GPSService.class);
		startService(intent);
		setContentView(R.layout.activity_alinone_main);
		fragmentManager = getSupportFragmentManager();
		initTextView();
		initImageView();
		initBottomTabBar();
		index = 0;
		if (savedInstanceState == null) {
			setTabSelection(index);
		}
		else {
			arrangeOrderFragment = (ArrangeOrderFragment)fragmentManager.findFragmentByTag("order");
			personCenterFragment = (PersonCenterFragment)fragmentManager.findFragmentByTag("person");
			if (savedInstanceState.getBoolean("order")) {
				setTabSelection(0);
			}
			else if (savedInstanceState.getBoolean("person")) {
				setTabSelection(1);
			}
		}
		if (getIntent().getExtras() == null) {
			checkUser();
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		if (arrangeOrderFragment != null) {
			outState.putBoolean("order", !arrangeOrderFragment.isHidden());
		}
		if (personCenterFragment != null) {
			outState.putBoolean("person", !personCenterFragment.isHidden());
		}
	}

	private void initBottomTabBar() {
		arrangeOrderLinearLayout = (LinearLayout)findViewById(R.id.arrange_order);
		personCenterLinearLayout = (LinearLayout)findViewById(R.id.person_center);
		arrangeOrderLinearLayout.setOnClickListener(new BottomBarOnClickListener());
		personCenterLinearLayout.setOnClickListener(new BottomBarOnClickListener());
	}
	
	private void initTextView() {
		arrangeOrderTextView = (TextView)findViewById(R.id.arrange_order_textview);
		personCenterTextView = (TextView)findViewById(R.id.person_center_textview);
	}
	
	private void initImageView() {
		arrangeOrderImageView = (ImageView)findViewById(R.id.arrange_order_imageview);
		personCenterImageView = (ImageView)findViewById(R.id.person_center_imageview);
		
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
	}
	
	private void openGPSSettings() {
        LocationManager alm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            return;
        }
        Toast.makeText(this, "请开启GPS！", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent,0); //此为设置完成后返回到获取界面

    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.alinone_main_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.change_password_from_actionbar:
			Intent changePasswordIntent = new Intent(AlinoneMainActivity.this, AboutActivity.class);
			startActivity(changePasswordIntent);
			break;
		case R.id.exit_from_actionbar:
			BaseApplication.getInstance().setPhoneNum("");
			BaseApplication.getInstance().setPassword("");
			BaseApplication.getInstance().setUserId("");
			BaseApplication.getInstance().setToken("");
			Intent intent = new Intent(AlinoneMainActivity.this, LauncherActivity.class);
			startActivity(intent);
			AlinoneMainActivity.this.finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void checkUser() {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("username", BaseApplication.getInstance().getPhoneNum());
			jsonObject.put("password", BaseApplication.getInstance().getPassword());
			StringEntity stringEntity = new StringEntity(String.valueOf(jsonObject));
			HttpUtil.post(AlinoneMainActivity.this, URLConstants.LoginUrl, stringEntity, URLConstants.ContentTypeJson, new JsonHttpResponseHandler() {

				@Override
				public void onFailure(int statusCode,
						Header[] headers, Throwable throwable,
						JSONObject errorResponse) {
					// TODO Auto-generated method stub
					Toast.makeText(AlinoneMainActivity.this, "获取信息出错", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onSuccess(int statusCode,
						Header[] headers, JSONObject response) {
					try {
						Log.d("status", response.toString());
						if (response.get("status").toString().equals("1")) {
							JSONObject jsonObject = new JSONObject(response.get("body").toString());
							BaseApplication.getInstance().setToken(jsonObject.get("private_token").toString());
						}
						else {
							Intent intent = new Intent(AlinoneMainActivity.this, LauncherActivity.class);
							BaseApplication.getInstance().setToken("");
							startActivity(intent);
							AlinoneMainActivity.this.finish();
						}
					} catch (JSONException e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
			});
		} catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	private int getCurrentTabSelection() {
		return index;
	}
	
	class BottomBarOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.arrange_order:
				setTabSelection(0);
				break;
			case R.id.person_center:
				setTabSelection(1);
				break;
			default:
				break;
			}
		}
		
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		openGPSSettings();
		checkUser();
	}

	private void setTabSelection(int index) {
		clearTabSelection();
		this.index = index;
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		hideFragments(fragmentTransaction);
		switch (index) {
		case 0:
			arrangeOrderTextView.setTextColor(getResources().getColor(R.color.theme_color));
			arrangeOrderImageView.setImageDrawable(getResources().getDrawable(R.drawable.order_pressed));
			if (arrangeOrderFragment == null) {
				arrangeOrderFragment = new ArrangeOrderFragment();
				fragmentTransaction.add(R.id.content, arrangeOrderFragment, "order");
			}
			else {
				fragmentTransaction.show(arrangeOrderFragment);
			}
			break;
		case 1:
			personCenterTextView.setTextColor(getResources().getColor(R.color.theme_color));
			personCenterImageView.setImageDrawable(getResources().getDrawable(R.drawable.user_pressed));
			if (personCenterFragment == null) {
				personCenterFragment = new PersonCenterFragment();
				fragmentTransaction.add(R.id.content, personCenterFragment, "person");
			}
			else {
				fragmentTransaction.show(personCenterFragment);
			}
		default:
			break;
		}
		fragmentTransaction.commit();
	}
	
	private void clearTabSelection() {
		arrangeOrderTextView.setTextColor(Color.parseColor("#000000"));
		personCenterTextView.setTextColor(Color.parseColor("#000000"));
		arrangeOrderImageView.setImageDrawable(getResources().getDrawable(R.drawable.order_unpressed));
		personCenterImageView.setImageDrawable(getResources().getDrawable(R.drawable.user_unpressed));
	}
	
	private void hideFragments(FragmentTransaction fragmentTransaction) {
		if (arrangeOrderFragment != null) {
			fragmentTransaction.hide(arrangeOrderFragment);
		}
		if (personCenterFragment != null) {
			fragmentTransaction.hide(personCenterFragment);
		}
	}
	
	//点击两次back退出应用
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(AlinoneMainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            	mExitTime = System.currentTimeMillis();
            }
			else {
				AlinoneMainActivity.this.finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		stopService(intent);
	}
	
}
