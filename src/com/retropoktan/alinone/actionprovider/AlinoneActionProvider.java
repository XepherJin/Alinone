package com.retropoktan.alinone.actionprovider;

import android.content.Context;
import android.support.v4.view.ActionProvider;
import android.view.SubMenu;
import android.view.View;

public class AlinoneActionProvider extends ActionProvider{
	
	private Context context;

	public AlinoneActionProvider(Context context) {
		super(context);
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	@Override
	public View onCreateActionView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasSubMenu() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void onPrepareSubMenu(SubMenu subMenu) {
		// TODO Auto-generated method stub
		subMenu.clear();
	}

	
}
