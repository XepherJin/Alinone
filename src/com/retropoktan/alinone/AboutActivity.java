package com.retropoktan.alinone;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.retropoktan.alinone.hud.ProgressHUD;
import com.retropoktan.alinone.netutil.HttpUtil;
import com.retropoktan.alinone.netutil.URLConstants;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AboutActivity extends Activity{

	private TextView checkForUpdate;
	private TextView versionTextView;
	private TextView changePasswordTextView;
	private EditText oldPasswordEditText;
	private EditText newPasswordEditText;
	private EditText verifyPasswordChangeEditText;
	private Button changePasswordButton;
	private LinearLayout changePasswordChart;
	private ProgressHUD progressHUD;
	
	private int isToChangePassword = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_activity);
		initView();
	}

	private void initView() {
		checkForUpdate = (TextView)findViewById(R.id.check_for_update);
		versionTextView = (TextView)findViewById(R.id.alinone_logo_version_textview);
		changePasswordTextView = (TextView)findViewById(R.id.change_new_password_from_about_textview);
		oldPasswordEditText = (EditText)findViewById(R.id.old_password_from_about);
		newPasswordEditText = (EditText)findViewById(R.id.password_to_change_from_about);
		verifyPasswordChangeEditText = (EditText)findViewById(R.id.verify_password_to_change_from_about);
		changePasswordButton = (Button)findViewById(R.id.change_new_password_from_about_button);
		changePasswordChart = (LinearLayout)findViewById(R.id.change_password_chart);
		checkForUpdate.setOnClickListener(new OnClickListener() { 
			// check for update
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				checkForUpdate();
			}
		});
		changePasswordTextView.setOnClickListener(new ChangePasswordIsShownListener());
		changePasswordButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isAbleToChangePassword(oldPasswordEditText) && isAbleToChangePassword(newPasswordEditText) && isAbleToChangePassword(verifyPasswordChangeEditText) && (newPasswordEditText.getText().toString().trim().equals(verifyPasswordChangeEditText.getText().toString().trim()))) {
					progressHUD = ProgressHUD.show(AboutActivity.this, "更改中", true);
					try {
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("private_token", BaseApplication.getInstance().getToken());
						jsonObject.put("password", oldPasswordEditText.getText().toString().trim());
						jsonObject.put("new_password", newPasswordEditText.getText().toString().trim());
						StringEntity stringEntity = new StringEntity(String.valueOf(jsonObject));
						HttpUtil.post(AboutActivity.this, URLConstants.ChangePasswordUrl, stringEntity, URLConstants.ContentTypeJson, new JsonHttpResponseHandler(){

							@Override
							public void onFailure(int statusCode,
									Header[] headers, Throwable throwable,
									JSONObject errorResponse) {
								// TODO Auto-generated method stub
								progressHUD.dismiss();
								Toast.makeText(AboutActivity.this, "更改失败", Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onSuccess(int statusCode,
									Header[] headers, JSONObject response) {
								// TODO Auto-generated method stub
								progressHUD.dismiss();
								try {
									if (response.get("status").toString().equals("1")) {
										BaseApplication.getInstance().setPassword(newPasswordEditText.getText().toString().trim());
										Toast.makeText(AboutActivity.this, "更改成功", Toast.LENGTH_SHORT).show();
									}
									else {
										Toast.makeText(AboutActivity.this, "更改失败", Toast.LENGTH_SHORT).show();
									}
									isToChangePassword = 0;
									changePasswordChart.setVisibility(View.GONE);
								} catch (Exception e) {
									// TODO: handle exception
								}
							}
						});
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
				else {
					Toast.makeText(AboutActivity.this, "请正确填写密码格式", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	private boolean isAbleToChangePassword(EditText editText) {
		if (editText.getText().toString().trim().equals("")) {
			return false;
		}
		else {
			return true;
		}
	}
	
	class ChangePasswordIsShownListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (isToChangePassword) {
			case 0:
				isToChangePassword = 1;
				changePasswordChart.setVisibility(View.VISIBLE);
				break;
			case 1:
				isToChangePassword = 0;
				changePasswordChart.setVisibility(View.GONE);
			default:
				break;
			}
		}
		
	}
	
	private void checkForUpdate() {
		Toast.makeText(AboutActivity.this, "当前已是最新版本", Toast.LENGTH_SHORT).show();
	}
	
}
