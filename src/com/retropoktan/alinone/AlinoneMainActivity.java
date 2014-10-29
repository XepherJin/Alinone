package com.retropoktan.alinone;

import org.json.JSONObject;

import com.retropoktan.alinone.netutil.HttpUtil;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AlinoneMainActivity extends ActionBarActivity {

	private FragmentManager fragmentManager;
	private ArrangeOrderFragment arrangeOrderFragment;
	private PersonCenterFragment personCenterFragment;
	
	private ImageView arrangeOrderImageView;
	private ImageView personCenterImageView;
	
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
		setTabSelection(0);
		checkUser();
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
		} catch (Exception e) {
			// TODO: handle exception
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
}
