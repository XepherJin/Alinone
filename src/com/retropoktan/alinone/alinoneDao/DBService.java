package com.retropoktan.alinone.alinoneDao;

import java.util.List;

import android.content.Context;

import com.retropoktan.alinone.BaseApplication;

public class DBService {

	private static DBService instance;
	private static Context appContext;
	private DaoSession mDaoSession;
	private AlinoneOrderDao AlinoneOrderDao;
	private MerchantDao merchantDao;
	private DishDao dishDao;
	private TodayOrderDao todayOrderDao;

	public static DBService getInstance(Context context) {
		if (instance == null) {
			instance = new DBService();
			if (appContext == null) {
				appContext = context.getApplicationContext();
			}
			instance.mDaoSession = BaseApplication.getDaoSession(context);
			instance.AlinoneOrderDao = instance.mDaoSession.getAlinoneOrderDao();
			instance.merchantDao = instance.mDaoSession.getMerchantDao();
			instance.dishDao = instance.mDaoSession.getDishDao();
			instance.todayOrderDao = instance.mDaoSession.getTodayOrderDao();
		}
		return instance;
	}
	
	public List<AlinoneOrder> loadAllOrders() {
		return AlinoneOrderDao.loadAll();
	}
	
	public List<AlinoneOrder> queryOrder(String where, String... params) {
		return AlinoneOrderDao.queryRaw(where, params);
	}
	
	public long saveOrder(AlinoneOrder order) {
		return AlinoneOrderDao.insertOrReplace(order);
	}
	
	public void saveOrderLists(final List<AlinoneOrder> list) {
		if (list == null || list.isEmpty()) {
			return;
		}
		AlinoneOrderDao.getSession().runInTx(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (int i = 0; i < list.size(); i++) {
					AlinoneOrder order = list.get(i);
					AlinoneOrderDao.insertOrReplace(order);
				}
			}
		});
	}
	
	public void deleteAllOrders() {
		AlinoneOrderDao.deleteAll();
	}

	public void deleteOrder(AlinoneOrder order) {
		AlinoneOrderDao.delete(order);
	}
	
	public List<Merchant> loadAllMerchants() {
		return merchantDao.loadAll();
	}
	
	public List<Merchant> queryMerchant(String where, String... params) {
		return merchantDao.queryRaw(where, params);
	}
	
	public long saveMerchant(Merchant merchant) {
		return merchantDao.insertOrReplace(merchant);
	}
	
	public void saveMerchantLists(final List<Merchant> list) {
		if (list == null || list.isEmpty()) {
			return;
		}
		merchantDao.getSession().runInTx(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (int i = 0; i < list.size(); i++) {
					Merchant merchant = list.get(i);
					merchantDao.insertOrReplace(merchant);
				}
			}
		});
	}
	
	public void deleteAllMerchants() {
		merchantDao.deleteAll();
	}
	
	public void deleteMerchant(Merchant merchant) {
		merchantDao.delete(merchant);
	}
	
	public long saveDish(Dish dish, AlinoneOrder order) {
		dish.setOrderId(order.getOrderID());
		return dishDao.insert(dish);
	}
	
	public void saveDishLists(final List<Dish> list, final AlinoneOrder order) {
		if (list == null | list.isEmpty() || order == null) {
			return;
		}
		dishDao.getSession().runInTx(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (int i = 0; i < list.size(); i++) {
					Dish dish = list.get(i);
					dish.setAlinoneOrder(order);
					dishDao.insertOrReplace(dish);
				}
			}
		});
	}
	
	public void deleteDishFromOrder(Dish dish) {
		mDaoSession.delete(dish);
	}
	
	public void deleteDishes(final List<Dish> list) {
		if (list == null ||list.isEmpty()) {
			return;
		}
		dishDao.getSession().runInTx(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (Dish dish : list) {
					dish.delete();
				}
			}
		});
	}
	
	public void deleteAllDishes() {
		dishDao.deleteAll();
	}
	
	public void saveTodayDishes(final List<TodayOrder> list, final Merchant merchant) {
		if (list == null || list.isEmpty() || merchant == null) {
			return;
		}
		todayOrderDao.getSession().runInTx(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (int i = 0; i < list.size(); i++) {
					TodayOrder todayOrder = list.get(i);
					todayOrder.setMerchantId(merchant.getMerchantID());
					todayOrderDao.insertOrReplace(todayOrder);
				}
				
			}
		});
	}
	
	public void deleteAllTodayOrders() {
		todayOrderDao.deleteAll();
	}
	
	public void deleteTodayOrdersFromMerchant(Merchant merchant) {
		merchant.resetTodayDishes();
	}
}
