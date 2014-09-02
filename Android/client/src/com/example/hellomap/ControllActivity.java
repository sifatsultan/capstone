package com.example.hellomap;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ControllActivity extends Activity {
	private Socket client;
	ImageView iv_controll;

	EditText editTextIP;
	Button buttonConnect;
	double base, height, base_screen, height_screen, centerX, centerY,
			iv_controll_height, iv_controll_width, iv_controll_centerX,
			iv_controll_centerY;
	String ip;
	public ControllActivity myActivity = this;
	int count = 0;

	// Debug stuff
	TextView rotationValue, rotationValue_screen, connectionStatus,
			devicesNetwork, tv_debugImageViewCenter, tv_debugScreenCenter,
			tv_debugImageViewSize, tv_debugScreenSize;
	int screenWidth, screenHeight;

	@TargetApi(19)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.control_activity);

		iv_controll = (ImageView) findViewById(R.id.iv_roationKnob);

		connectionStatus = (TextView) findViewById(R.id.tv_connection_status);
		buttonConnect = (Button) findViewById(R.id.btn_Connect);
		editTextIP = (EditText) findViewById(R.id.et_enterIp);

		// Debug stuff
		tv_debugImageViewCenter = (TextView) findViewById(R.id.tv_debugImageViewCenter);
		tv_debugImageViewSize = (TextView) findViewById(R.id.tv_debugImageViewSize);
		tv_debugScreenSize = (TextView) findViewById(R.id.tv_debugScreenSize);
		tv_debugScreenCenter = (TextView) findViewById(R.id.tv_debugScreenCenter);

		rotationValue = (TextView) findViewById(R.id.tv_roationValue);
		rotationValue_screen = (TextView) findViewById(R.id.tv_rotationValueScreen);

		buttonConnect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (client != null) {
					try {
						client.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (ip != null) {
					ip = editTextIP.getText().toString();
				}
				connectToServer();
			}
		});

	}

	@TargetApi(18)
	public boolean onTouchEvent(MotionEvent touch) {
		double touchX = touch.getX();
		double touchY = touch.getY();

		switch (touch.getAction()) {
		case MotionEvent.ACTION_MOVE:
			// first quadrant
			if (touchX > iv_controll_centerX && touchY > centerY) {
				base = touchX - iv_controll_centerX;
				height = iv_controll_centerY - touchY;

				final double thetaRadian = Math.atan(height / base);
				double thetaDegreeDouble = 360 - Math.toDegrees(thetaRadian);
				int thetaDegreeInt = (int) thetaDegreeDouble;
				iv_controll.setRotation(thetaDegreeInt);

				String valueToDisplay = String
						.valueOf((int) (((double) (360 - (-1 * (int) Math
								.toDegrees(thetaRadian))) / 360) * 100));
				rotationValue.setText("iv: "+valueToDisplay);

				if (client != null) {
					sendMessageToServer((int) (((double) (360 - (-1 * (int) Math
							.toDegrees(thetaRadian))) / 360) * 100));
				}
				
				
//				using Screen Center
				base_screen = touchX - centerX;
				height_screen = centerY - touchY;

				final double thetaRadian_screen = Math.atan(height_screen / base_screen);
				double thetaDegreeDouble_screen = 360 - Math.toDegrees(thetaRadian_screen);
				@SuppressWarnings("unused")
				int thetaDegreeInt_screen = (int) thetaDegreeDouble_screen;

				String valueToDisplay_screen = String
						.valueOf((int) (((double) (360 - (-1 * (int) Math
								.toDegrees(thetaRadian_screen))) / 360) * 100));
				rotationValue_screen.setText("sc: "+valueToDisplay_screen);

			}

			// second quadrant.
			if (touchX < iv_controll_centerX && touchY > iv_controll_centerY) {
				
//				using ImageView Center
				base = iv_controll_centerX - touchX;
				height = iv_controll_centerY - touchY;

				final double thetaRadian = Math.atan(height / base);
				double thetaDegreeDouble = 180 + Math.toDegrees(thetaRadian);
				int thetaDegreeInt = (int) thetaDegreeDouble;
				iv_controll.setRotation(thetaDegreeInt);

				String valueToDisplay = String
						.valueOf((int) (((double) (180 + ((-1) * (int) Math
								.toDegrees(thetaRadian))) / 360) * 100));
				rotationValue.setText("iv :"+valueToDisplay);

				if (client != null) {
					sendMessageToServer((int) (((double) (180 + ((-1) * (int) Math
							.toDegrees(thetaRadian))) / 360) * 100));
				}
				
//				using Screen Center
				base_screen = centerX - touchX;
				height_screen = centerY - touchY;

				final double thetaRadian_screen = Math.atan(height_screen / base_screen);
				double thetaDegreeDouble_screen = 180 + Math.toDegrees(thetaRadian_screen);
				@SuppressWarnings("unused")
				int thetaDegreeInt_screen = (int) thetaDegreeDouble_screen;

				String valueToDisplay_screen = String
						.valueOf((int) (((double) (180 + ((-1) * (int) Math
								.toDegrees(thetaRadian))) / 360) * 100));
				rotationValue_screen.setText("sc: "+valueToDisplay_screen);

			}

			// third quadrant
			if (touchX < iv_controll_centerX && touchY < iv_controll_centerY) {
				// using ImageView Center
				base = iv_controll_centerX - touchX;
				height = touchY - iv_controll_centerY;

				final double thetaRadian = Math.atan(height / base);
				double thetaDegreeDouble = 180 - Math.toDegrees(thetaRadian);
				int thetaDegreeInt = (int) thetaDegreeDouble;
				iv_controll.setRotation(thetaDegreeInt);

				String valueToDisplay = String
						.valueOf((int) (((double) (180 - (-1)
								* (int) Math.toDegrees(thetaRadian)) / 360) * 100));
				rotationValue.setText("iv: " +valueToDisplay);
				if (client != null) {
					sendMessageToServer((int) (((double) (180 - (-1)
							* (int) Math.toDegrees(thetaRadian)) / 360) * 100));
				}

				// using Screen Center
				base_screen = centerX - touchX;
				height_screen = touchY - centerY;

				final double thetaRadian_screen = Math.atan(height_screen
						/ base_screen);

				String valueToDisplay_screen = String
						.valueOf((int) (((double) (180 - (-1)
								* (int) Math.toDegrees(thetaRadian_screen)) / 360) * 100));
				rotationValue_screen.setText("sc: "+valueToDisplay_screen);
				if (client != null) {
					sendMessageToServer((int) (((double) (180 - (-1)
							* (int) Math.toDegrees(thetaRadian)) / 360) * 100));
				}

			}

			// fourth quadrant
			if (touchX > iv_controll_centerX && touchY < iv_controll_centerY) {

				// using Image View center
				base = touchX - iv_controll_centerX;
				height = touchY - iv_controll_centerY;

				final double thetaRadian = Math.atan(height / base);
				double thetaDegreeDouble = Math.toDegrees(thetaRadian);
				int thetaDegreeInt = (int) thetaDegreeDouble;
				iv_controll.setRotation(thetaDegreeInt);

				String valueToDisplay = String
						.valueOf((int) (((double) ((-1) * (int) Math
								.toDegrees(thetaRadian)) / 360) * 100));
				rotationValue.setText("iv: "+valueToDisplay);
				if (client != null) {
					sendMessageToServer((int) (((double) ((-1) * (int) Math
							.toDegrees(thetaRadian)) / 360) * 100));
				}


				// using Screen Center
				base_screen = touchX - centerX;
				height_screen = touchY - centerY;

				final double thetaRadian_screen = Math.atan(height_screen
						/ base_screen);
				double thetaDegreeDouble_screen = Math
						.toDegrees(thetaRadian_screen);
				int thetaDegreeInt_screen = (int) thetaDegreeDouble_screen;
				iv_controll.setRotation(thetaDegreeInt_screen);
				
				String valueToDisplay_screen = String
						.valueOf((int) (((double) ((-1) * (int) Math
								.toDegrees(thetaRadian_screen)) / 360) * 100));
				rotationValue_screen.setText("sc: "+valueToDisplay_screen);

			}
		}
		return true;
	}

	public void connectToServer() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						// client = new Socket(ip, 8080); //home
						// client = new Socket("192.168.0.119", 8080); //home
						client = new Socket("192.168.0.104", 8080); // home
					} catch (IOException e) {
						Log.w("Sifat", "Must Try Harder");
						myActivity.runOnUiThread(new Runnable() {
							public void run() {
								connectionStatus
										.setText("Connection kar barewe"
												+ count);
							}
						});
					}
					if (client != null) {
						myActivity.runOnUiThread(new Runnable() {
							public void run() {
								connectionStatus.setText("Finally got hold of "
										+ client.getInetAddress()
												.getHostAddress() + " after "
										+ count + " tries");
							}
						});

						break;
					}
					count++;
				}
			}
		}).start();
	}

	public void sendMessageToServer(Integer value) {
		final int theMessage = value;
		new Thread(new Runnable() {
			@Override
			public void run() {
				PrintWriter write;
				try {
					write = new PrintWriter(client.getOutputStream(), true);
					write.println(theMessage);
					write.flush();
				} catch (UnknownHostException e) {
				} catch (IOException e) {
				}
			}
		}).start();
	}

}
