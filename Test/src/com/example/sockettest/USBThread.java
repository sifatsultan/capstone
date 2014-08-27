package com.example.sockettest;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbManager;
import android.widget.TextView;

import driver.UsbSerialDriver;
import driver.UsbSerialProber;

public class USBThread extends Thread{

	private Peace peace;

	private Activity mainActivity;
	private Context context;

	private UsbManager usbManager;
	private UsbSerialDriver usbSerialDriver;

	private TextView tv_usb;
	private String clientMessage = "";

	public USBThread(Activity activity, TextView textView) {
		mainActivity = activity;
		peace = new Peace(mainActivity);
		context = mainActivity.getApplicationContext();
		tv_usb = textView;
		usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
	}

	public USBThread(Activity activity) {
		mainActivity = activity;
	}

	public void run() {
		peace = new Peace(mainActivity);
		usbManager = (UsbManager) mainActivity.getSystemService(Context.USB_SERVICE);
		while (usbSerialDriver == null) {
			try {
				usbSerialDriver = UsbSerialProber.findFirstDevice(usbManager);
			} catch (Exception e) {

			}

			if (usbSerialDriver != null) {
				peace.toast("USB Connected");
				// indicateUI();
				try {
					usbSerialDriver.open();
					usbSerialDriver.setParameters(115200, 8, UsbSerialDriver.STOPBITS_1, UsbSerialDriver.PARITY_NONE);
				} catch (IOException e) {
					e.printStackTrace();
				}

				break;
			}
		}

	}

	public void send(char[] buffer) {
		try {
			String bufferString = buffer.toString();
			byte[] bufferByteArray = bufferString.getBytes();
			if (usbSerialDriver != null) {
				usbSerialDriver.write(bufferByteArray, bufferString.length());
			}
			// usbSerialDriver.write(buffer.getBytes(), clientMessage.length());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send(String message) {
		clientMessage = message;
		try {

			if (usbSerialDriver != null) {
				usbSerialDriver.write(clientMessage.getBytes(), clientMessage.length());
				peace.setText(tv_usb, clientMessage);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
