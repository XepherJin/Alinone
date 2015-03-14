package com.retropoktan.alinone;

import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.retropoktan.alinone.hud.ProgressHUD;
import com.retropoktan.alinone.netutil.HttpUtil;
import com.retropoktan.alinone.netutil.URLConstants;

public class ForgetPasswordActivity extends Activity implements OnClickListener{

	private TextView forgetPasswordTitle;
	
	private Button smsButton;
	private Button backButton;
	private Button changePasswordButton;
	private ProgressHUD progressHUD;
	
	private EditText phoneNumAndNewPassword;
	private EditText passwordAgain;
	private EditText smsFeedBack;
	
	private String phoneNum = null;
	
	private LinearLayout forgetPasswordLayout;
	private LinearLayout changePasswordLayout;
	
	private LayoutInflater layoutInflater;
	private View view;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forget_password);
		layoutInflater = ForgetPasswordActivity.this.getLayoutInflater();
		view = layoutInflater.inflate(R.layout.change_new_password, null);
		forgetPasswordLayout = (LinearLayout)findViewById(R.id.forget_password_chart);
		initTextView();
		initEditText();
		initButton();
	}
	
	private void initTextView() {
		forgetPasswordTitle = (TextView)findViewById(R.id.forget_password_title);
	}
	
	private void initButton() {
		smsButton = (Button)findViewById(R.id.find_password_button);
		backButton = (Button)view.findViewById(R.id.return_button);
		changePasswordButton = (Button)view.findViewById(R.id.change_password_button);
		smsButton.setOnClickListener(this);
		backButton.setOnClickListener(this);
		changePasswordButton.setOnClickListener(this);
		changePasswordLayout = new LinearLayout(ForgetPasswordActivity.this);
		changePasswordLayout.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		changePasswordLayout.setLayoutParams(params);
		forgetPasswordLayout.addView(changePasswordLayout);
		changePasswordLayout.addView(view);
		changePasswordLayout.setVisibility(View.GONE);
	}

	private void initEditText(){
		phoneNumAndNewPassword = (EditText)findViewById(R.id.find_password_edittext);
		passwordAgain = (EditText)view.findViewById(R.id.new_password);
		smsFeedBack = (EditText)view.findViewById(R.id.sms_verify_for_new_password);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.find_password_button:
			//http request
			try {
				if (phoneNumAndNewPassword.getText().toString().trim().length() != 11) {
					Toast.makeText(getApplicationContext(), "请正确输入手机号码", Toast.LENGTH_SHORT).show();
				}
				else {
					progressHUD = ProgressHUD.show(ForgetPasswordActivity.this, "正在发送验证码短信", true);
					JSONObject jsonObject = new JSONObject();
					phoneNum = phoneNumAndNewPassword.getText().toString().trim();
					jsonObject.put("phone", phoneNum);
					StringEntity stringEntity = new StringEntity(String.valueOf(jsonObject));
					HttpUtil.post(ForgetPasswordActivity.this, URLConstants.SMSForgetUrl, stringEntity, URLConstants.ContentTypeJson, new JsonHttpResponseHandler() {

						@Override
						public void onFailure(int statusCode, Header[] headers,
								Throwable throwable, JSONObject errorResponse) {
							// TODO Auto-generated method stub
							progressHUD.dismiss();
							Toast.makeText(ForgetPasswordActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
							}

						@Override
						public void onSuccess(int statusCode, Header[] headers,
								JSONObject response) {
							// TODO Auto-generated method stub
							try {
								if (response.get("status").toString().equals("1")) {
									Log.v("smsback", response.toString());
									progressHUD.dismiss();
									phoneNumAndNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
									changePasswordLayout.setVisibility(View.VISIBLE);
									smsButton.setVisibility(view.GONE);
									phoneNumAndNewPassword.setText("");
									phoneNumAndNewPassword.setHint("新密码");
									forgetPasswordTitle.setText("验证短信已发送，请输入验证码和新密码");
								}
								else {
									progressHUD.dismiss();
									Toast.makeText(ForgetPasswordActivity.this, "该号码不存在", Toast.LENGTH_SHORT).show();
								}
							} catch (JSONException e) {
								// TODO: handle exception
							}
						}
					});
				}
			} catch (JSONException e) {
				// TODO: handle exception
			} catch (UnsupportedEncodingException e) {
				// TODO: handle exception
			}
			break;
		case R.id.change_password_button:
			if (!(phoneNumAndNewPassword.getText().toString().trim().equals(passwordAgain.getText().toString().trim())) ||
					phoneNumAndNewPassword.getText().toString().trim().equals("")) {
				Toast.makeText(ForgetPasswordActivity.this, "新密码格式不正确", Toast.LENGTH_SHORT).show();
			}
			else {
				// http request
				try {
					progressHUD = ProgressHUD.show(ForgetPasswordActivity.this, "更改密码中", true);
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("phone", phoneNum);
					jsonObject.put("verify_code", smsFeedBack.getText().toString().trim());
					jsonObject.put("new_password", passwordAgain.getText().toString().trim());
					StringEntity stringEntity = new StringEntity(String.valueOf(jsonObject));
					HttpUtil.post(ForgetPasswordActivity.this, URLConstants.SMSNewPasswordUrl, stringEntity, URLConstants.ContentTypeJson, new JsonHttpResponseHandler() {

						@Override
						public void onFailure(int statusCode, Header[] headers,
								Throwable throwable, JSONObject errorResponse) {
							// TODO Auto-generated method stub
							progressHUD.dismiss();
							Toast.makeText(ForgetPasswordActivity.this, "更改失败", Toast.LENGTH_SHORT).show();
							}

						@Override
						public void onSuccess(int statusCode, Header[] headers,
								JSONObject response) {
							// TODO Auto-generated method stub
							try {
								if (response.get("status").toString().equals("1")) {
									progressHUD.dismiss();
									BaseApplication.getInstance().setPhoneNum(phoneNum);
									ForgetPasswordActivity.this.finish();
								}
								else if (response.get("status").toString().equals("12")) {
									progressHUD.dismiss();
									Toast.makeText(ForgetPasswordActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
								}
							} catch (JSONException e) {
								// TODO: handle exception
							}
						}
						
					});
				} catch (JSONException e) {
					// TODO: handle exception
				} catch (UnsupportedEncodingException e) {
					// TODO: handle exception
				}
			}
			break;
		case R.id.return_button:
			ForgetPasswordActivity.this.finish();
			break;
		default:
			break;
		}
	}
	
}
