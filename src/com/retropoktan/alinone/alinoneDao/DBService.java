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

	public static DBService getInstance(Context context) {
		if (instance == null) {
			instance = new DBService();
			if (appContext == null) {
				appContext = context.getApplicationContext();
			}
			instance.mDaoSession = BaseApplication.getDaoSession(context);
			instance.AlinoneOrderDao = instance.mDaoSession.getAlinoneOrderDao();
			instance.merchantDao = instance.mDaoSession.getMerchantDao();
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
}
