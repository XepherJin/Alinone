package com.retropoktan.alinone;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import com.retropoktan.alinone.alinoneDao.Constants;
import com.retropoktan.alinone.alinoneDao.DaoMaster;
import com.retropoktan.alinone.alinoneDao.DaoMaster.OpenHelper;
import com.retropoktan.alinone.alinoneDao.DaoSession;

public class BaseApplication extends Application{

	private static BaseApplication mInstance;
	private static DaoMaster daoMaster;
	private static DaoSession daoSession;
	
	public static Context applicationContext;
	
	public static final String PHONE_NUM = "phoneNumber";
	public static final String USER_ID = "user_id";
	public static final String TOKEN = "user_token";
	public static final String PASSWORD = "password";
	public static final String USER_NICK_NAME = "user_nick_name";
	
	private String password = null;
	private String userID = null;
	private String phoneNum = null;
	private String token = null;
	private String userNickName = null;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		if (mInstance == null) {
			mInstance = this;
		}
		if (applicationContext == null) {
			applicationContext = this;
		}
	}
	
	public static BaseApplication getInstance() {
		return mInstance;
	}
	
	public String getUserId() {
		if (userID == null) {
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
			userID = sharedPreferences.getString(USER_ID, null);
		}
		return userID;
	}
	
	public String getPhoneNum() {
		if (phoneNum == null) {
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
			phoneNum = sharedPreferences.getString(PHONE_NUM, null);
		}
		return phoneNum;
	}
	
	public String getPassword() {
		if (this.password == null) {
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
			String password = new String(Base64.decode(sharedPreferences.getString(PASSWORD, ""), Base64.URL_SAFE));
			return password;
		}
		else {
			String password = new String(Base64.decode(this.password, Base64.URL_SAFE));
			return password;
		}
	}
	
	public String getToken() {
		if (token == null) {
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
			token = sharedPreferences.getString(TOKEN, null);
		}
		return token;
	}
	
	public String getNickName() {
		if (userNickName == null) {
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
			userNickName = sharedPreferences.getString(USER_NICK_NAME, null);
		}
		return userNickName;
	}
	
	public void setUserId(String userID) {
		if (userID != null) {
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			if (editor.putString(USER_ID, userID).commit()) {
				this.userID = userID;
			}
		}
	}
	
	public void setPhoneNum(String phoneNum) {
		if (phoneNum != null) {
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			if (editor.putString(PHONE_NUM, phoneNum).commit()) {
				this.phoneNum = phoneNum;
			}
		}
	}
	
	public void setPassword(String password) {
		if (password != null) {
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			if (editor.putString(PASSWORD, Base64.encodeToString(password.getBytes(), Base64.URL_SAFE)).commit()) {
				this.password = Base64.encodeToString(password.getBytes(), Base64.URL_SAFE);
			}
		}
	}
	
	public void setToken(String token) {
		if (token != null) {
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			if (editor.putString(TOKEN, token).commit()) {
				this.token = token;
			}
		}
	}
	
	public void setNickName(String nickName) {
		if (nickName != null) {
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			if (editor.putString(USER_NICK_NAME, nickName).commit()) {
				this.userNickName = nickName;
			}
		}
	}
		  
	    /** 
	     * 取得DaoMaster 
	     *  
	     * @param context 
	     * @return DaoMaster实例
	     */  
	    public static DaoMaster getDaoMaster(Context context) {  
	        if (daoMaster == null) {  
	            OpenHelper helper = new DaoMaster.DevOpenHelper(context, Constants.DB_NAME, null);  
	            daoMaster = new DaoMaster(helper.getWritableDatabase());  
	        }  
	        return daoMaster;  
	    } 
	    /** 
	     * 取得DaoSession 
	     *  
	     * @param context 
	     * @return DaoSession实例
	     */  
	    public static DaoSession getDaoSession(Context context) {  
	        if (daoSession == null) {  
	            if (daoMaster == null) {  
	                daoMaster = getDaoMaster(context);  
	            }  
	            daoSession = daoMaster.newSession();  
	        }  
	        return daoSession;  
	    }  
}
