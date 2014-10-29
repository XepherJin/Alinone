package com.retropoktan.alinone.adapter;

import java.util.List;
import java.util.zip.Inflater;

import com.retropoktan.alinone.PersonCenterFragment;
import com.retropoktan.alinone.R;
import com.retropoktan.alinone.R.string;
import com.retropoktan.alinone.alinoneDao.Merchant;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PersonCenterAdapter extends BaseAdapter{

	public static final String TAG = PersonCenterAdapter.class.getSimpleName();
	public List<Merchant> merchantListData;
	public Context context;
	public LayoutInflater inflater;
	
	public PersonCenterAdapter(List<Merchant> merchantListData, Context context) {
		super();
		this.merchantListData = merchantListData;
		this.context = context;
		inflater = LayoutInflater.from(context);
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
		return 0;
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
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder)convertView.getTag();
		}
		Merchant merchant = (Merchant)merchantListData.get(position);
		holder.merchantNameTextView.setText(merchant.getMerchantName());
		holder.orderNumTextView.setText("累计派送 " + merchant.getOrderNum() + " 单");
		return convertView;
	}

	private static class ViewHolder{
		TextView merchantNameTextView;
		TextView orderNumTextView;
	}
}
