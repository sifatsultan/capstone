package com.example.orientationsensor;

import android.app.Activity;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Surface;
import android.widget.TextView;

public class MainActivity extends Activity implements Orentation.Listener {

	private Orentation mOrientation;
	private TextView rollView, pitchView, yawView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		mOrientation = new Orentation(
				(SensorManager) getSystemService(Activity.SENSOR_SERVICE),
				getWindow().getWindowManager());

		rollView = (TextView) findViewById(R.id.roll);
		pitchView = (TextView) findViewById(R.id.pitch);
		yawView = (TextView) findViewById(R.id.yaw);

	}

	@Override
	protected void onResume() {
		super.onResume();
		mOrientation.startListening(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mOrientation.stopListening();
	}

	@Override
	public void onOrientationChanged(float azimuth, float pitch, float roll) {
		pitchView.setText("Pitch: " + Float.toString(pitch));
		rollView.setText("Roll: " + Float.toString(roll));
		yawView.setText("Yaw: " + Float.toString(azimuth));

	}

}
