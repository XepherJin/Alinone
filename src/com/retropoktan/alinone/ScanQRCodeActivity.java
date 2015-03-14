package com.retropoktan.alinone;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.retropoktan.alinone.alinoneDao.AlinoneOrder;
import com.retropoktan.alinone.alinoneDao.DBService;
import com.retropoktan.alinone.alinoneDao.Dish;
import com.retropoktan.alinone.alinoneDao.Merchant;
import com.retropoktan.alinone.hud.ProgressHUD;
import com.retropoktan.alinone.netutil.HttpUtil;
import com.retropoktan.alinone.netutil.URLConstants;
import com.retropoktan.alinone.qrcode.camera.CameraManager;
import com.retropoktan.alinone.qrcode.decode.CaptureActivityHandler;
import com.retropoktan.alinone.qrcode.decode.InactivityTimer;
import com.retropoktan.alinone.qrcode.view.ViewfinderView;


public class ScanQRCodeActivity extends Activity implements Callback {

	public static final String QR_RESULT = "RESULT";
	
	private DBService dbService;
	
	private String qrCodeID;
	private TextView qrcodeTextView;
	
	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private SurfaceView surfaceView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	// private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	CameraManager cameraManager;
	
	private ArrayList<String> qrCodeList = new ArrayList<String>();
	
	private ArrayList<Dish> dishList = new ArrayList<Dish>();
	
	private int RESULT_CANCEL_WITHOUT_BIND_ORDERS = 11;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/*
		 * this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		 * WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
		 * 
		 * RelativeLayout layout = new RelativeLayout(this);
		 * layout.setLayoutParams(new
		 * ViewGroup.LayoutParams(LayoutParams.FILL_PARENT,
		 * LayoutParams.FILL_PARENT));
		 * 
		 * this.surfaceView = new SurfaceView(this); this.surfaceView
		 * .setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT,
		 * LayoutParams.FILL_PARENT));
		 * 
		 * layout.addView(this.surfaceView);
		 * 
		 * this.viewfinderView = new ViewfinderView(this);
		 * this.viewfinderView.setBackgroundColor(0x00000000);
		 * this.viewfinderView.setLayoutParams(new
		 * ViewGroup.LayoutParams(LayoutParams.FILL_PARENT,
		 * LayoutParams.FILL_PARENT)); layout.addView(this.viewfinderView);
		 * 
		 * TextView status = new TextView(this); RelativeLayout.LayoutParams
		 * params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
		 * LayoutParams.WRAP_CONTENT);
		 * params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		 * params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		 * status.setLayoutParams(params);
		 * status.setBackgroundColor(0x00000000);
		 * status.setTextColor(0xFFFFFFFF); status.setText("请将条码置于取景框内扫描。");
		 * status.setTextSize(14.0f);
		 * 
		 * layout.addView(status); setContentView(layout);
		 */

		setContentView(R.layout.qr_code_capture);
		surfaceView = (SurfaceView) findViewById(R.id.surfaceview);
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinderview);

		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		dbService = DBService.getInstance(getApplicationContext());
		qrcodeTextView = (TextView)findViewById(R.id.scan_qr_code_textview);

		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		// CameraManager.init(getApplication());
		cameraManager = new CameraManager(getApplication());

		viewfinderView.setCameraManager(cameraManager);

		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		cameraManager.closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			// CameraManager.get().openDriver(surfaceHolder);
			cameraManager.openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	public CameraManager getCameraManager() {
		return cameraManager;
	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	public void handleDecode(Result obj, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		showResult(obj, barcode);
		//if(handler!=null)     //实现连续扫描
		//	   handler.restartPreviewAndDecode();
	}

	private void showResult(final Result rawResult, Bitmap barcode) {
		
		qrCodeID = rawResult.getText();
		
		if (qrCodeID.length() == 18) {
			getQRCodeMerchant(qrCodeID);
		}
		else if (qrCodeID.length() > 30) {
			String orderID = qrCodeID.substring(qrCodeID.length() - 22, qrCodeID.length());
			String time = orderID.substring(0, 8);
			if (isNumeric(orderID)) {
				getQRCodeInfo(orderID);
			}
			else {
				Toast.makeText(getApplicationContext(), "非法二维码", Toast.LENGTH_SHORT).show();
			}
		}
		else {
			Toast.makeText(getApplicationContext(), "非法二维码", Toast.LENGTH_SHORT).show();
		}
		/*
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		Drawable drawable = new BitmapDrawable(barcode);
		builder.setIcon(drawable);
		
		qrCodeID = rawResult.getText();

		builder.setTitle("类型:" + rawResult.getBarcodeFormat() + "\n 结果：" + qrCodeID);
		builder.setPositiveButton("确定", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Intent intent = new Intent();
				intent.putExtra("result", rawResult.getText());
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		builder.setNegativeButton("重新扫描", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				restartPreviewAfterDelay(0L);
			}
		});
		builder.setCancelable(false);
		builder.show();

		// Intent intent = new Intent();
		// intent.putExtra(QR_RESULT, rawResult.getText());
		// setResult(RESULT_OK, intent);
		// finish();
		 * 
		 */
	}

	public void restartPreviewAfterDelay(long delayMS) {
		if (handler != null) {
			handler.sendEmptyMessageDelayed(MessageIDs.restart_preview, delayMS);
		}
	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			try {
				AssetFileDescriptor fileDescriptor = getAssets().openFd("qrbeep.ogg");
				this.mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(),
						fileDescriptor.getLength());
				this.mediaPlayer.setVolume(0.1F, 0.1F);
				this.mediaPlayer.prepare();
			} catch (IOException e) {
				this.mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			setResult(RESULT_CANCELED);
			finish();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_FOCUS || keyCode == KeyEvent.KEYCODE_CAMERA) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void getQRCodeInfo(String string) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		String date = simpleDateFormat.format(new Date());
		//if (!time.equals(date)) {
		//	new AlertDialog.Builder(ScanQRCodeActivity.this)
		//	.setTitle("识别错误")
		//	.setMessage("二维码已过期！")
		//	.setPositiveButton("重新扫描", null)
		//	.show();
		//}
		//else {
			if (!qrCodeList.contains(string)) {
				qrCodeList.add(string);
				Toast.makeText(getApplicationContext(), "已成功扫描订单", Toast.LENGTH_SHORT).show();
				qrcodeTextView.setText("已成功扫描" + qrCodeList.size() + "份订单");
				restartPreviewAfterDelay(2500L);
				Log.v("list!!!!!!!!!", qrCodeList.toString());
			}
			else {
				new AlertDialog.Builder(ScanQRCodeActivity.this)
				.setTitle("识别成功")
				.setMessage("您已扫描过该订单，请勿重复扫描！")
				.setPositiveButton("扫描下一个", null)
				.show();
				restartPreviewAfterDelay(2500L);
			}
		//}
	}
	public boolean isNumeric(String str){ 
	    Pattern pattern = Pattern.compile("[0-9]*"); 
	    return pattern.matcher(str).matches();
	 }
	
	private void getQRCodeMerchant(String string) {
		String merchantID = string.substring(0, 8);
		qrCodeList.clear();
		qrCodeList.add(merchantID);
		new AlertDialog.Builder(ScanQRCodeActivity.this)
		.setTitle("识别商家成功")
		.setMessage("已成功扫描商家！")
		.setPositiveButton("确定", null)
		.show();
		Log.v("list!!!!!!!!!", qrCodeList.toString());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.alinone_qrcode, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.save_qrcode_info:
			if (qrCodeList.size() <= 0) {
				Toast.makeText(getApplicationContext(), "目前尚未扫描订单", Toast.LENGTH_SHORT).show();
			}
			else {
				if (qrCodeList.get(0).toString().length() > 20) {
					try {
						final ProgressHUD progressHUD = ProgressHUD.show(ScanQRCodeActivity.this, "绑定订单中", true);
						JSONObject jsonObject = new JSONObject();
						JSONArray jsonArray = new JSONArray();
						for (String validOrderID : qrCodeList) {
							JSONObject jsonObjectInside = new JSONObject();
							jsonObjectInside.put("order_id", validOrderID);
							jsonArray.put(jsonObjectInside);
						}
						jsonObject.put("orders_id", jsonArray);
						jsonObject.put("private_token", BaseApplication.getInstance().getToken());
						StringEntity stringEntity = new StringEntity(String.valueOf(jsonObject));
						HttpUtil.post(getApplicationContext(), URLConstants.BindOrdersUrl, stringEntity, URLConstants.ContentTypeJson, new JsonHttpResponseHandler(){
							
							@Override
							public void onFailure(int statusCode, Header[] headers,
									Throwable throwable, JSONObject errorResponse) {
								// TODO Auto-generated method stub
								progressHUD.dismiss();
								Toast.makeText(getApplicationContext(), "绑定失败", Toast.LENGTH_SHORT).show();
							}


							@Override
							public void onSuccess(int statusCode, Header[] headers,
									JSONObject response) {
								// TODO Auto-generated method stub
								progressHUD.dismiss();
								try {
									if (response.get("status").toString().equals("1")) {
										JSONArray orderArray = ((JSONObject)response.get("body")).getJSONArray("bind_list");
										for (int i = 0; i < orderArray.length(); i++) {
											JSONObject orderObject = orderArray.getJSONObject(i);
											JSONArray dishArray = orderObject.getJSONArray("dish_list");
											for (int j = 0; j < dishArray.length(); j++) {
												JSONObject dishObject = dishArray.getJSONObject(j);
												Dish dish = new Dish(dishObject.getString("name"), dishObject.getInt("count"), Float.valueOf(dishObject.get("price").toString()), qrCodeList.get(i).toString());
												dishList.add(dish);
											}
											AlinoneOrder order = new AlinoneOrder(orderObject.get("order_id").toString(), orderObject.get("phone").toString(), orderObject.get("address").toString(), orderObject.get("merchant_id").toString(), new Date(),
													orderObject.getString("name"), orderObject.getBoolean("if_pay"), Float.valueOf(orderObject.get("price").toString()));
											dbService.saveOrder(order);
											Log.d("teset", dishList.toString());
											dbService.saveDishLists(dishList, order);
										}
										Toast.makeText(getApplicationContext(), "绑定成功", Toast.LENGTH_SHORT).show();
										Intent intent = new Intent(ScanQRCodeActivity.this, ArrangeOrderFragment.class);
										setResult(RESULT_OK, intent);
										ScanQRCodeActivity.this.finish();
									}
									else if (hasSurface) {
										Toast.makeText(getApplicationContext(), "绑定出错", Toast.LENGTH_SHORT).show();
										JSONArray orderArray = ((JSONObject)response.get("body")).getJSONArray("bind_list");
										Log.d("dbservice", orderArray.toString());
										for (int i = 0; i < orderArray.length(); i++) {
											JSONObject orderObject = orderArray.getJSONObject(i);
											JSONArray dishArray = orderObject.getJSONArray("dish_list");
											for (int j = 0; j < dishArray.length(); j++) {
												JSONObject dishObject = dishArray.getJSONObject(j);
												Dish dish = new Dish(dishObject.getString("name"), dishObject.getInt("count"), Float.valueOf(dishObject.get("price").toString()), qrCodeList.get(i).toString());
												dishList.add(dish);
											}
											AlinoneOrder order = new AlinoneOrder(orderObject.get("order_id").toString(), orderObject.get("phone").toString(), orderObject.get("address").toString(), orderObject.get("merchant_id").toString(), new Date(),
													orderObject.getString("name"), orderObject.getBoolean("if_pay"), Float.valueOf(orderObject.get("price").toString()));
											dbService.saveOrder(order);
											dbService.saveDishLists(dishList, order);
										}
										dishList.clear();
										Intent intent = new Intent(ScanQRCodeActivity.this, ArrangeOrderFragment.class);
										setResult(RESULT_CANCEL_WITHOUT_BIND_ORDERS, intent);
										ScanQRCodeActivity.this.finish();
									}
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
					try {
						String timeStamp = ((long)System.currentTimeMillis() / 1000) + "";
						final ProgressHUD progressHUD = ProgressHUD.show(ScanQRCodeActivity.this, "绑定商家中", true);
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("merchant_id", qrCodeList.get(0));
						jsonObject.put("private_token", BaseApplication.getInstance().getToken());
						jsonObject.put("time_stamp", timeStamp);
						Log.d("time_stamp", timeStamp.toString());
						StringEntity stringEntity = new StringEntity(String.valueOf(jsonObject));
						HttpUtil.post(getApplicationContext(), URLConstants.BindMerchantUrl, stringEntity, URLConstants.ContentTypeJson, new JsonHttpResponseHandler(){
							
							@Override
							public void onFailure(int statusCode, Header[] headers,
									Throwable throwable, JSONObject errorResponse) {
								// TODO Auto-generated method stub
								progressHUD.dismiss();
								Toast.makeText(getApplicationContext(), "绑定失败", Toast.LENGTH_SHORT).show();
							}


							@Override
							public void onSuccess(int statusCode, Header[] headers,
									JSONObject response) {
								// TODO Auto-generated method stub
								progressHUD.dismiss();
								try {
									if (response.get("status").toString().equals("1")) {
										JSONObject merchantObject = (JSONObject)response.get("body");
										Log.d("one merchant", merchantObject.toString());
										Toast.makeText(getApplicationContext(), "绑定成功", Toast.LENGTH_SHORT).show();
										dbService.saveMerchant(new Merchant(merchantObject.get("merchant_id").toString(), merchantObject.get("merchant_name").toString(), 0));
										Intent intent = new Intent(ScanQRCodeActivity.this, PersonCenterFragment.class);
										setResult(RESULT_OK, intent);
										ScanQRCodeActivity.this.finish();
									}
									else {
										Toast.makeText(getApplicationContext(), "绑定出错", Toast.LENGTH_SHORT).show();
										Intent intent = new Intent(ScanQRCodeActivity.this, PersonCenterFragment.class);
										setResult(RESULT_CANCELED, intent);
										ScanQRCodeActivity.this.finish();
									}
								} catch (Exception e) {
									// TODO: handle exception
								}
							}
						});
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	

}