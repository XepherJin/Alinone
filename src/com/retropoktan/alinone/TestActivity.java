package com.retropoktan.alinone;

import android.app.Activity;
import android.os.Bundle;

public class TestActivity extends Activity implements BottomTabBarPanelOnClickListener{

	private BottomTabBarPanel bottomTabBarPanel = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
		bottomTabBarPanel = (BottomTabBarPanel)findViewById(R.id.bottom_layout);
		if (bottomTabBarPanel != null) {
			bottomTabBarPanel.initBottomPanel();
			bottomTabBarPanel.setBottomTabBarPanelOnClickListener(this);
		}
	}
	@Override
	public void onBottomTabBarPanelClick(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case 0:
			
			break;

		default:
			break;
		}
	}

	
}
