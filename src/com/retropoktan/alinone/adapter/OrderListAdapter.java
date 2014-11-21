package com.retropoktan.alinone.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.retropoktan.alinone.R;
import com.retropoktan.alinone.alinoneDao.AlinoneOrder;

public class OrderListAdapter extends BaseAdapter{
	
	public static final String TAG = OrderListAdapter.class.getSimpleName();
	public List<AlinoneOrder> orderList;
	public LayoutInflater inflater;
	
	public int[] checkList;
	
	public OrderListAdapter(List<AlinoneOrder> orderList, Context context, int[] checkList) {
		super();
		this.orderList = orderList;
		this.checkList = checkList;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return orderList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return orderList.get(position);
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
			convertView = inflater.inflate(R.layout.order_listview_item, null);
			viewHolder.addressTextView = (TextView)convertView.findViewById(R.id.address_textview);
			viewHolder.phoneNumberTextView = (TextView)convertView.findViewById(R.id.phone_number_textview);
			viewHolder.checkMarkImageView = (ImageView)convertView.findViewById(R.id.check_mark_for_order);
			convertView.setTag(viewHolder);
		}
		else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		AlinoneOrder order = (AlinoneOrder)orderList.get(position);
		viewHolder.addressTextView.setText("派送地址：" + order.getOrderAddress());
		viewHolder.phoneNumberTextView.setText("联系电话：" + order.getObjectPhone());
		viewHolder.checkMarkImageView.setImageResource(R.drawable.check_mark_for_order);
		if (checkList[position] == 1) {
			viewHolder.checkMarkImageView.setVisibility(View.VISIBLE);
		}
		else {
			viewHolder.checkMarkImageView.setVisibility(View.GONE);
		}
		return convertView;
	}

	private static class ViewHolder{
		TextView addressTextView;
		TextView phoneNumberTextView;
		ImageView checkMarkImageView;
	}
}
