package com.retropoktan.alinone.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.retropoktan.alinone.R;
import com.retropoktan.alinone.alinoneDao.TodayOrder;

public class TodayOrderAdapter extends BaseAdapter{

	public static final String TAG = TodayOrderAdapter.class.getSimpleName();
	private List<TodayOrder> list;
	private LayoutInflater inflater;
	
	public TodayOrderAdapter(List<TodayOrder> list, Context mContext) {
		super();
		this.list = list;
		this.inflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.today_order_listview_item, null);
			viewHolder.phoneTextView = (TextView)convertView.findViewById(R.id.today_order_phone);
			viewHolder.priceTextView = (TextView)convertView.findViewById(R.id.today_order_price);
			viewHolder.timeTextView = (TextView)convertView.findViewById(R.id.today_order_time);
			viewHolder.platformTextView = (TextView)convertView.findViewById(R.id.today_order_platform);
			convertView.setTag(viewHolder);
		}
		else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		TodayOrder todayOrder = list.get(position);
		viewHolder.phoneTextView.setText("电话:" + todayOrder.getPhone());
		if (todayOrder.getIsPaid()) {
			viewHolder.priceTextView.setText("价格:" + todayOrder.getPrice() + "(已付)");
		}
		else {
			viewHolder.priceTextView.setText("价格:" + todayOrder.getPrice() + "(到付)");
		}
		viewHolder.timeTextView.setText("配送时间:" + todayOrder.getTime());
		viewHolder.platformTextView.setText("平台:" + todayOrder.getPlatFormName());
		return convertView;
	}

	private static class ViewHolder{
		TextView phoneTextView, priceTextView, timeTextView, platformTextView;
	}
}
