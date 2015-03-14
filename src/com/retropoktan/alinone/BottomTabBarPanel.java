package com.retropoktan.alinone;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

public class BottomTabBarPanel extends RelativeLayout implements OnClickListener{

	private Context mContext;
	private BottomTabBarCell cell_1 = null;
	private BottomTabBarCell cell_2 = null;
	private BottomTabBarCell cell_3 = null;
	private BottomTabBarCell cell_4 = null;
	private int DEFAULT_BACKGROUND_COLOR = Color.rgb(243, 243, 243); //set panel's background color
	private BottomTabBarPanelOnClickListener mBottomTabBarPanelOnClickListener = null;
	private List<BottomTabBarCell> bottomTabBarCellList = new ArrayList<BottomTabBarCell>();
	public BottomTabBarPanel(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}
	public BottomTabBarPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	public BottomTabBarPanel(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		clearSelection();
		int index = -1;
		switch (v.getId()) {
		case R.id.btn_message:
			index = 0;
			cell_1.setChecked(0);
			break;
		case R.id.btn_contacts:
			index = 1;
			cell_2.setChecked(1);
			break;
		case R.id.btn_news:
			index = 2;
			cell_3.setChecked(2);
			break;
		case R.id.btn_setting:
			index = 3;
			cell_4.setChecked(3);
			break;
		default:
			break;
		}
		if (mBottomTabBarPanelOnClickListener != null) {
			mBottomTabBarPanelOnClickListener.onBottomTabBarPanelClick(index);
		}
	}
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		cell_1 = (BottomTabBarCell)findViewById(R.id.btn_message);
		cell_2 = (BottomTabBarCell)findViewById(R.id.btn_contacts);
		cell_3 = (BottomTabBarCell)findViewById(R.id.btn_news);
		cell_4 = (BottomTabBarCell)findViewById(R.id.btn_setting);
		setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
		bottomTabBarCellList.add(cell_1);
		bottomTabBarCellList.add(cell_2);
		bottomTabBarCellList.add(cell_3);
		bottomTabBarCellList.add(cell_4);
	}
	
	public void initBottomPanel() {
		if (cell_1 != null) {
			cell_1.setImage(R.drawable.order_pressed);
			cell_1.setText("测试1");
		}
		if (cell_2 != null) {
			cell_2.setImage(R.drawable.order_unpressed);
			cell_2.setText("测试1");
		}
		if (cell_3 != null) {
			cell_3.setImage(R.drawable.order_unpressed);
			cell_3.setText("测试1");
		}
		if (cell_4 != null) {
			cell_4.setImage(R.drawable.order_unpressed);
			cell_4.setText("测试1");
		}
		setOnClickListeners();
	}
	
	public void clearSelection() {
		if (cell_1 != null) {
			cell_1.setImage(R.drawable.order_pressed);
			cell_1.setDefaultTextColor();
		}
		if (cell_2 != null) {
			cell_2.setImage(R.drawable.order_pressed);
			cell_2.setDefaultTextColor();
		}
		if (cell_3 != null) {
			cell_3.setImage(R.drawable.order_pressed);
			cell_3.setDefaultTextColor();
		}
		if (cell_4 != null) {
			cell_4.setImage(R.drawable.order_pressed);
			cell_4.setDefaultTextColor();
		}
	}
	
	private void setOnClickListeners() {
		int num = this.getChildCount();
		for (int i = 0; i < num; i++) {
			View view = getChildAt(i);
			if (view != null) {
				view.setOnClickListener(this);
			}
		}
	}
	
	public void setBottomTabBarPanelOnClickListener(BottomTabBarPanelOnClickListener bottomTabBarPanelOnClickListener) {
		mBottomTabBarPanelOnClickListener = bottomTabBarPanelOnClickListener;
	}
	
	public void setDefaultCellChecked() {
		if (cell_1 != null) {
			cell_1.setChecked(0);
		}
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		super.onLayout(changed, l, t, r, b);
		layoutItems(l, t, r, b);
	}
	/**
	 * reset the second and the third cell's padding
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 */
	private void layoutItems(int left, int top, int right, int bottom) {
		int n = getChildCount();
		if (n == 0) {
			return;
		}
		int paddingLeft = getPaddingLeft();
		int paddingRight = getPaddingRight();
		int width = right - left;
		int height = bottom - top;
		int allViewWidth = 0;
		for (int i = 0; i < n; i++) {
			View view = getChildAt(i);
			allViewWidth += view.getWidth();
		}
		int blankWidth = (width - allViewWidth - paddingLeft - paddingRight) / (n - 1);
		LayoutParams cell_2_layoutParams = (LayoutParams)bottomTabBarCellList.get(1).getLayoutParams();
		cell_2_layoutParams.leftMargin = blankWidth;
		bottomTabBarCellList.get(1).setLayoutParams(cell_2_layoutParams);
		LayoutParams cell_3_layoutParams = (LayoutParams)bottomTabBarCellList.get(2).getLayoutParams();
		cell_3_layoutParams.leftMargin = blankWidth;
		bottomTabBarCellList.get(2).setLayoutParams(cell_3_layoutParams);
	}
}
