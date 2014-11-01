package com.retropoktan.alinone;

import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alinone_main);
		fragmentManager = getSupportFragmentManager();
		initTextView();
		initImageView();
		initBottomTabBar();
		index = 0;
		setTabSelection(index);
		if (getIntent().getExtras() == null) {
			checkUser();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.alinone_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.scan_qr_code_from_action_bar:
			Intent intent = new Intent(AlinoneMainActivity.this, ScanQRCodeActivity.class);
			startActivity(intent);
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
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
	
	
	private void checkUser() {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("username", BaseApplication.getInstance().getPhoneNum());
			jsonObject.put("password", BaseApplication.getInstance().getPassword());
			StringEntity stringEntity = new StringEntity(String.valueOf(jsonObject));
			HttpUtil.post(AlinoneMainActivity.this, URLConstants.LoginUrl, stringEntity, URLConstants.ContentTypeJson,new JsonHttpResponseHandler() {

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
						Log.v("status", response.toString());
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
		checkUser();
	}

	private void setTabSelection(int index) {
		clearTabSelection();
		this.index = index;
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		hideFragments(fragmentTransaction);
		switch (index) {
		case 0:
			arrangeOrderTextView.setTextColor(Color.parseColor("#87CEEB"));
			if (arrangeOrderFragment == null) {
				arrangeOrderFragment = new ArrangeOrderFragment();
				fragmentTransaction.add(R.id.content, arrangeOrderFragment);
			}
			else {
				fragmentTransaction.show(arrangeOrderFragment);
			}
			break;
		case 1:
			personCenterTextView.setTextColor(Color.parseColor("#87CEEB"));
			if (personCenterFragment == null) {
				personCenterFragment = new PersonCenterFragment();
				fragmentTransaction.add(R.id.content, personCenterFragment);
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
}
