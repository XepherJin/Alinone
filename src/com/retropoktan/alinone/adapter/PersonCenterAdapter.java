package com.retropoktan.alinone.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.retropoktan.alinone.R;
import com.retropoktan.alinone.alinoneDao.Merchant;
import com.retropoktan.alinone.qrcode.view.AlinoneListView;

public class PersonCenterAdapter extends BaseAdapter{

	public static final String TAG = PersonCenterAdapter.class.getSimpleName();
	public List<Merchant> merchantListData;
	public LayoutInflater inflater;
	private Context mContext;
	
	public List<Merchant> getMerchantListData() {
		return merchantListData;
	}

	public void setMerchantListData(List<Merchant> merchantListData) {
		this.merchantListData = merchantListData;
	}

	public PersonCenterAdapter(List<Merchant> merchantListData, Context context) {
		super();
		this.merchantListData = merchantListData;
		inflater = LayoutInflater.from(context);
		mContext = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return merchantListData.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return merchantListData.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.person_center_listview_item, null);
			holder.merchantNameTextView = (TextView)convertView.findViewById(R.id.merchant_name_text_view);
			holder.orderNumTextView = (TextView)convertView.findViewById(R.id.order_num_textview);
			holder.listView = (AlinoneListView)convertView.findViewById(R.id.today_order_listview);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder)convertView.getTag();
		}
		if (merchantListData.size() > 0) {
			Merchant merchant = (Merchant)merchantListData.get(position);
			if (!merchant.getTodayDishes().isEmpty()) {
				holder.listView.setAdapter(new TodayOrderAdapter(merchant.getTodayDishes(), mContext));
				holder.orderNumTextView.setVisibility(View.GONE);
				holder.merchantNameTextView.setText(merchant.getMerchantName() + "(今日已派送" + merchant.getTodayDishes().size() + "单,尚余" + merchant.getPreOrderNum() + "单)");
			}
			else {
				holder.merchantNameTextView.setText(merchant.getMerchantName() + "(今日已派送0单,尚余" + merchant.getPreOrderNum() + "单)");
				holder.orderNumTextView.setVisibility(View.VISIBLE);
			}
		}
		else {
			holder.merchantNameTextView.setText("暂无绑定商家！");
		}
		return convertView;
	}

	private static class ViewHolder{
		TextView merchantNameTextView;
		TextView orderNumTextView;
		AlinoneListView listView;
	}
	
}
