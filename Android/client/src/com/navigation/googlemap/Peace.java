package com.example.hellomap;

import android.app.Activity;
import android.widget.TextView;
import android.widget.Toast;

public class Peace{

	private Activity mainActivity;

	public Peace(Activity activity) {
		mainActivity = activity;
	}

	public void setText(final TextView textView, final String string) {
		mainActivity.runOnUiThread(new Runnable(){
			@Override
			public void run() {
				textView.setText(string);
			}
		});
	}

	public void toast(final String string) {
		mainActivity.runOnUiThread(new Runnable(){
			@Override
			public void run() {
				Toast.makeText(mainActivity, string, Toast.LENGTH_SHORT).show();
			}
		});
	}
}
