package com.retropoktan.alinone.netutil;

public class URLConstants {

	public static final String BasicUrl = "http://121.40.203.33";
	public static final String ApiUrl = BasicUrl + "/api/v1";
	public static final String LoginUrl = ApiUrl + "/sender/login";
	public static final String RegisterUrl = ApiUrl + "/sender/register";
	public static final String SMSRegisterUrl = ApiUrl + "";
	public static final String SMSForgetUrl = ApiUrl + "/sender/forget_password";
	public static final String SMSNewPasswordUrl = ApiUrl + "/sender/new_password";
	public static final String BindMerchantUrl = ApiUrl + "/sender/bind_merchant";
	public static final String UnBindMerchantUrl = ApiUrl + "/sender/unbind_merchant";
	public static final String BindOrdersUrl = ApiUrl + "/sender/bind_orders";
	public static final String FinishOrdersUrl = ApiUrl + "/sender/finish_orders";
	public static final String GPSUploadUrl = ApiUrl + "/sender/gps_renew";
	public static final String PersonInfoUrl = ApiUrl + "/sender/info";
	public static final String ChangePasswordUrl = ApiUrl + "/sender/change_password";
	
	public static final String ContentTypeJson = "application/json";
}
