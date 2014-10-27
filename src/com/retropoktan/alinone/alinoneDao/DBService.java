package com.retropoktan.alinone.alinoneDao;

import java.util.List;

import com.retropoktan.alinone.BaseApplication;

import android.content.Context;

public class DBService {

	private static  DBService instance;
	private static Context appContext;
	private DaoSession mDaoSession;
	private OrderDao orderDao;
	private MerchantDao merchantDao;

	public static DBService getInstance(Context context) {
		if (instance == null) {
			instance = new DBService();
			if (appContext == null) {
				appContext = context.getApplicationContext();
			}
			instance.mDaoSession = BaseApplication.getDaoSession(context);
			instance.orderDao = instance.mDaoSession.getOrderDao();
			instance.merchantDao = instance.mDaoSession.getMerchantDao();
		}
		return instance;
	}
	
	public Order loadOrder(long id) {
		return orderDao.load(id);
	}
	
	public List<Order> loadAllOrders() {
		return orderDao.loadAll();
	}
	
	public List<Order> queryOrder(String where, String... params) {
		return orderDao.queryRaw(where, params);
	}
	
	public long saveOrder(Order order) {
		return orderDao.insertOrReplace(order);
	}
	
	public void saveOrderLists(final List<Order> list) {
		if (list == null || list.isEmpty()) {
			return;
		}
		orderDao.getSession().runInTx(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (int i = 0; i < list.size(); i++) {
					Order order = list.get(i);
					orderDao.insertOrReplace(order);
				}
			}
		});
	}
	
	public void deleteAllOrders() {
		orderDao.deleteAll();
	}
	
	public void deleteOrder(long id) {
		orderDao.deleteByKey(id);
	}
	
	public void deleteOrder(Order order) {
		orderDao.delete(order);
	}
}
