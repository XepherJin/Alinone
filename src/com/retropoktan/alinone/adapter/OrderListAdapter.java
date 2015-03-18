package com.retropoktan.alinone.adapter;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.retropoktan.alinone.ArrangeOrderFragment;
import com.retropoktan.alinone.R;
import com.retropoktan.alinone.alinoneDao.AlinoneOrder;
import com.retropoktan.alinone.alinoneDao.Dish;
import com.retropoktan.alinone.alinoneDao.isMarked;

public class OrderListAdapter extends BaseAdapter{
	
	public static final String TAG = OrderListAdapter.class.getSimpleName();
	public List<AlinoneOrder> orderList;
	public LayoutInflater inflater;
	public int mLastPosition;
	
	public List<isMarked> checkList;
	
	public ArrangeOrderFragment arrangeOrderFragment;
	
	private Context mContext;
	
	private StringBuffer detailDish;
	
	public OrderListAdapter(List<AlinoneOrder> orderList, Context context, ArrangeOrderFragment arrangeOrderFragment, int expanded, List<isMarked> checkList) {
		super();
		this.orderList = orderList;
		inflater = LayoutInflater.from(context);
		mLastPosition = expanded;
		mContext = context;
		this.arrangeOrderFragment = arrangeOrderFragment;
		this.checkList = checkList;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.order_listview_item, null);
			viewHolder.addressTextView = (TextView)convertView.findViewById(R.id.address_textview);
			viewHolder.phoneNumberTextView = (TextView)convertView.findViewById(R.id.phone_number_textview);
			viewHolder.checkMarkImageView = (ImageView)convertView.findViewById(R.id.check_mark_for_order);
			viewHolder.whetherPaidTextView = (TextView)convertView.findViewById(R.id.whether_paid_mark);
			viewHolder.dishTextView = (TextView)convertView.findViewById(R.id.dish_textview);
			convertView.setTag(viewHolder);
		}
		else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		final AlinoneOrder order = (AlinoneOrder)orderList.get(position);
		viewHolder.addressTextView.setText("地址：" + order.getOrderAddress());
		viewHolder.phoneNumberTextView.setText("电话：" + order.getObjectPhone());
		viewHolder.checkMarkImageView.setImageResource(R.drawable.check_mark_for_order);
		if (checkList.get(position).getValue() == 0) {
			viewHolder.checkMarkImageView.setVisibility(View.INVISIBLE);
		}
		else {
			viewHolder.checkMarkImageView.setVisibility(View.VISIBLE);
		}
		List<Dish> list = order.getDishes();
		if (list.isEmpty()) {
			viewHolder.dishTextView.setText("暂无菜品信息");
		}
		else {
			detailDish = new StringBuffer("");
			for (int i = 0; i < list.size(); i++) {
				detailDish.append(list.get(i).getDishName() + "    " + "*" + list.get(i).getDishNum() + "    " + list.get(i).getDishCost() + "元");
				detailDish.append("\n");
			}
			detailDish.delete(detailDish.length() - 1, detailDish.length());
			viewHolder.dishTextView.setText(detailDish);
			detailDish.delete(0, detailDish.length() - 1);
		}
		if (!order.getPaid()) {
			viewHolder.whetherPaidTextView.setText(Html.fromHtml("<font color=\'#ff0000\'>(未付)" + String.valueOf(order.getPrice()) + "元</font>"));
		}
		else {
			viewHolder.whetherPaidTextView.setText(Html.fromHtml("<font color=\'#66cd00\'>(已付)" + String.valueOf(order.getPrice()) + "元</font>"));
		}
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final String phoneString = order.getObjectPhone();
				new AlertDialog.Builder(mContext)
				.setTitle("选择联系方式")
				.setItems(new String[]{"打电话", "发短信"}, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						viewHolder.checkMarkImageView.setVisibility(View.VISIBLE);
						checkList.get(position).setValue(1);
						arrangeOrderFragment.checkList.get(position).setValue(1);
						switch (which) {
						case 0:
							Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneString));
							mContext.startActivity(intent);
							break;
						case 1:
							sendSMS(phoneString, "同学~你的外卖到了~请前来领取~~", mContext);
							break;
						default:
							break;
						}
					}
				}).setNegativeButton("取消", null)
				.show();
			}
		});
		convertView.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(mContext)
				.setTitle("操作订单")
				.setItems(new String[]{"配送完成", "撤销订单"}, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						switch (which) {
						case 0:
							arrangeOrderFragment.commitOneOrder(order);
							orderList.remove(order);
							arrangeOrderFragment.checkList.remove(position);
							notifyDataSetChanged();
							if (orderList.size() <= 0) {
								arrangeOrderFragment.commmitAllOrders();
								arrangeOrderFragment.setAddOrderButtonShow();
							}
							break;
						case 1:
							arrangeOrderFragment.orderList.remove(order);
							arrangeOrderFragment.checkList.remove(position);
							orderList.remove(order);
							notifyDataSetChanged();
							if (orderList.size() <= 0) {
								arrangeOrderFragment.commmitAllOrders();
								arrangeOrderFragment.setAddOrderButtonShow();
							}
							break;
						default:
							break;
						}
					}
				}).setNegativeButton("取消", null)
				.show();
				return true;
			}
		});
		return convertView;
	}

	private static class ViewHolder{
		TextView addressTextView;
		TextView phoneNumberTextView;
		TextView whetherPaidTextView;
		TextView dishTextView;
		ImageView checkMarkImageView;
	}
	
	private void sendSMS(String phoneNumber, String content, Context context) {
		Uri smsUri = Uri.parse("smsto:" + phoneNumber);
		Intent intent = new Intent(Intent.ACTION_SENDTO, smsUri);
		intent.putExtra("sms_body", content);
		context.startActivity(intent);
	}
}
