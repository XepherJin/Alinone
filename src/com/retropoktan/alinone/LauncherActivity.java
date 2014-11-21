package com.retropoktan.alinone;

import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.retropoktan.alinone.hud.ProgressHUD;
import com.retropoktan.alinone.netutil.HttpUtil;
import com.retropoktan.alinone.netutil.URLConstants;

public class LauncherActivity extends Activity{
	
	private EditText userAccount;
	private EditText userPassword;
	private EditText verifyPassword;
	private EditText smsVerify;
	private EditText userNickName;
	
	private TimeCount timeCount;
	
	private TextView forgetPassword;
	
	private Button loginButton;
	private Button registerButton;
	private Button smsVerifyButton;
	
	private LinearLayout loginLayout;
	
	private LayoutInflater layoutInflater;
	private View view;
	
	private ProgressHUD progressHUD;
	
	private int isSignUp = 0; // 默认为0，点击注册后为1

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (BaseApplication.getInstance().getToken() != null && !BaseApplication.getInstance().getToken().equals("")) {
			startActivity(new Intent(LauncherActivity.this, AlinoneMainActivity.class));
			LauncherActivity.this.finish();
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.launcher_activity);
		layoutInflater = LauncherActivity.this.getLayoutInflater();
		view = layoutInflater.inflate(R.layout.register, null);
		initEditText();
		initButton();
	}
	
	private void initButton() {
		loginButton = (Button)findViewById(R.id.login_button);
		registerButton = (Button)findViewById(R.id.register_button);
		smsVerifyButton = (Button)view.findViewById(R.id.sms_verify_button);
		forgetPassword = (TextView)findViewById(R.id.forget_password_textview);
		
		forgetPassword.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LauncherActivity.this, ForgetPasswordActivity.class);
				startActivity(intent);
			}
		});
		
		final LinearLayout linearLayout = new LinearLayout(LauncherActivity.this);
		loginLayout = (LinearLayout)LauncherActivity.this.findViewById(R.id.login_chart);
		loginLayout.addView(linearLayout);
		linearLayout.addView(view);
		linearLayout.setVisibility(View.GONE);
		registerButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isSignUp == 0) {
					isSignUp = 1;
					linearLayout.setVisibility(View.VISIBLE);
					loginButton.setText("取消");
					forgetPassword.setVisibility(View.GONE);
				}
				else {
					if ((userAccount.getText().toString().trim().length() != 11) ||
							!(userPassword.getText().toString().equals(verifyPassword.getText().toString().trim())) || userPassword.getText().toString().trim().equals("") || 
							verifyPassword.getText().toString().trim().equals("")) {
						Toast.makeText(LauncherActivity.this, "请正确填写注册信息！", Toast.LENGTH_SHORT).show();
					}
					else {
						//register http request
						try {
							progressHUD = ProgressHUD.show(LauncherActivity.this, "注册中", true);
							JSONObject jsonObject = new JSONObject();
							jsonObject.put("username", userAccount.getText().toString().trim());
							jsonObject.put("password", userPassword.getText().toString().trim());
							jsonObject.put("reg_code", smsVerify.getText().toString().trim());
							jsonObject.put("nick", userNickName.getText().toString().trim());
							StringEntity stringEntity = new StringEntity(String.valueOf(jsonObject), "UTF-8");
							HttpUtil.post(LauncherActivity.this, URLConstants.RegisterUrl, stringEntity, URLConstants.ContentTypeJson, new JsonHttpResponseHandler() {

								@Override
								public void onFailure(int statusCode,
										Header[] headers, Throwable throwable,
										JSONObject errorResponse) {
									// TODO Auto-generated method stub
									progressHUD.dismiss();
									Toast.makeText(LauncherActivity.this, "连接超时", Toast.LENGTH_SHORT).show();
								}

								@Override
								public void onSuccess(int statusCode,
										Header[] headers, JSONObject response) {
									try {
										if (response.get("status").toString().equals("1")) {
											progressHUD.dismiss();
											Log.v("json", response.toString());
											JSONObject jsonObject = new JSONObject(response.get("body").toString());
											BaseApplication.getInstance().setToken(jsonObject.get("private_token").toString());
											BaseApplication.getInstance().setPassword(userPassword.getText().toString());
											BaseApplication.getInstance().setUserId(userAccount.getText().toString());
											BaseApplication.getInstance().setPhoneNum(userAccount.getText().toString().trim());
											// need to be fixed
											BaseApplication.getInstance().setNickName(userNickName.getText().toString());
											Intent intent = new Intent(LauncherActivity.this, AlinoneMainActivity.class);
											intent.putExtra("activity_name", LauncherActivity.class.getSimpleName());
											startActivity(intent);
											LauncherActivity.this.finish();
										}
										else if (response.get("status").toString().equals("6")) {
											progressHUD.dismiss();
											Toast.makeText(LauncherActivity.this, "该帐号已存在", Toast.LENGTH_SHORT).show();
										}
										else if (response.get("status").toString().equals("12")) {
											progressHUD.dismiss();
											Toast.makeText(LauncherActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
										}
									} catch (JSONException e) {
										// TODO: handle exception
										e.printStackTrace();
									}
								}
							});
						} catch (JSONException e) {
							// TODO: handle exception
							e.printStackTrace();
						} catch (UnsupportedEncodingException e) {
							// TODO: handle exception
							e.printStackTrace();
						}
					}
				}
			}
		});
		
		loginButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isSignUp == 0) {
					if (userAccount.getText().toString().trim().length() != 11) {
						Toast.makeText(LauncherActivity.this, "请正确填写手机号码", Toast.LENGTH_SHORT).show();
					}
					else {
						try {
							progressHUD = ProgressHUD.show(LauncherActivity.this, "登录中", true);
							JSONObject jsonObject = new JSONObject();
							jsonObject.put("username", userAccount.getText().toString().trim());
							jsonObject.put("password", userPassword.getText().toString().trim());
							StringEntity stringEntity = new StringEntity(String.valueOf(jsonObject));
							HttpUtil.post(LauncherActivity.this, URLConstants.LoginUrl, stringEntity, URLConstants.ContentTypeJson,new JsonHttpResponseHandler() {

								@Override
								public void onFailure(int statusCode,
										Header[] headers, Throwable throwable,
										JSONObject errorResponse) {
									// TODO Auto-generated method stub
									progressHUD.dismiss();
									Toast.makeText(LauncherActivity.this, "连接超时", Toast.LENGTH_SHORT).show();
								}

								@Override
								public void onSuccess(int statusCode,
										Header[] headers, JSONObject response) {
									try {
										Log.v("status", response.toString());
										if (response.get("status").toString().equals("1")) {
											progressHUD.dismiss();
											JSONObject jsonObject = new JSONObject(response.get("body").toString());
											BaseApplication.getInstance().setToken(jsonObject.get("private_token").toString());
											BaseApplication.getInstance().setPhoneNum(userAccount.getText().toString().trim());
											BaseApplication.getInstance().setPassword(userPassword.getText().toString().trim());
											BaseApplication.getInstance().setUserId(userAccount.getText().toString().trim());
											//need to be fixed
											BaseApplication.getInstance().setNickName(jsonObject.get("nick").toString());
											Intent intent = new Intent(LauncherActivity.this, AlinoneMainActivity.class);
											intent.putExtra("activity_name", LauncherActivity.class.getSimpleName());
											startActivity(intent);
											LauncherActivity.this.finish();
										}
										else if (response.get("status").toString().equals("7")) {
											progressHUD.dismiss();
											Toast.makeText(LauncherActivity.this, "该帐号不存在", Toast.LENGTH_SHORT).show();
										}
										else if (response.get("status").toString().equals("4")) {
											progressHUD.dismiss();
											Toast.makeText(LauncherActivity.this, "帐号或密码错误", Toast.LENGTH_SHORT).show();
										}
										else {
											progressHUD.dismiss();
											Toast.makeText(LauncherActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
										}
									} catch (JSONException e) {
										// TODO: handle exception
										e.printStackTrace();
									}
								}
							});
						} catch (JSONException e) {
							// TODO: handle exception
							e.printStackTrace();
						} catch (UnsupportedEncodingException e) {
							// TODO: handle exception
							e.printStackTrace();
						}
					}
				}
				else {
					linearLayout.setVisibility(View.GONE);
					loginButton.setText("登录");
					isSignUp = 0;
					forgetPassword.setVisibility(View.VISIBLE);
				}
			}
		});
		
		smsVerifyButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				timeCount = new TimeCount(60000, 1000);
				if (userAccount.getText().toString().length() != 11) {
					Toast.makeText(LauncherActivity.this, "手机号码错误", Toast.LENGTH_SHORT).show();
				}
				else {
					Toast.makeText(LauncherActivity.this, "正在向您发送验证短信，请稍候", Toast.LENGTH_SHORT).show();
					timeCount.start();
					try {
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("phone", userAccount.getText().toString().trim());
						StringEntity stringEntity = new StringEntity(String.valueOf(jsonObject));
						HttpUtil.post(LauncherActivity.this, URLConstants.SMSRegisterUrl, stringEntity, URLConstants.ContentTypeJson, new JsonHttpResponseHandler(){

							@Override
							public void onFailure(int statusCode, Header[] headers,
									Throwable throwable, JSONObject errorResponse) {
								// TODO Auto-generated method stub
								Toast.makeText(LauncherActivity.this, "验证短信发送超时", Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onSuccess(int statusCode, Header[] headers,
									JSONObject response) {
								// TODO Auto-generated method stub
							}
							
						});
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
		});
	}
	
	private void initEditText() {
		userAccount = (EditText)findViewById(R.id.user_account);
		userPassword = (EditText)findViewById(R.id.user_password);
		if ((BaseApplication.getInstance().getPhoneNum() != null) && !(BaseApplication.getInstance().getPhoneNum().equals(""))) {
			userAccount.setText(BaseApplication.getInstance().getPhoneNum());
			userPassword.requestFocus();
		}
		verifyPassword = (EditText)view.findViewById(R.id.user_verify_password);
		smsVerify = (EditText)view.findViewById(R.id.sms_verify);
		userNickName = (EditText)view.findViewById(R.id.user_nick_name);
	}
	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if ((BaseApplication.getInstance().getPhoneNum() != null) && !(BaseApplication.getInstance().getPhoneNum().equals(""))) {
			userAccount.setText(BaseApplication.getInstance().getPhoneNum());
			userPassword.requestFocus();
		}
	}

	class TimeCount extends CountDownTimer{

		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onFinish() {
			// TODO Auto-generated method stub
			smsVerifyButton.setClickable(true);
			smsVerifyButton.setText("重新获取");
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// TODO Auto-generated method stub
			smsVerifyButton.setClickable(false);
			smsVerifyButton.setText(millisUntilFinished / 1000 + "秒");
		}
	}
}
