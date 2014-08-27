package com.example.sockettest;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Activity;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.TextView;

public class Server extends Thread {

	int port;
	TextView ip, read;
	Activity activity;
	Peace peace;
	ServerSocket server;
	BufferedReader reader;
	USBThread usbThread;

	public static final int BUFFER_SIZE = 2048;
	private Socket socket = null;
	private PrintWriter out = null;
	private BufferedReader in = null;

	String message;
	int charsRead;
	// char[] buffer;
	byte buffer[];

	String ipString;
	private String msg;
	protected char[] char_clientMsg;
	Socket client;

	public Server() {
		port = 3030;
	}

	public void ui(Activity activity, TextView ip, TextView read) {
		this.read = read;
		this.ip = ip;
		this.activity = activity;
	}

	public void run() {
		try {
			peace = new Peace(activity);
			usbThread = new USBThread(activity);
			// peace.setText(ip,
			// server.getInetAddress().getHostAddress().toString());
			WifiManager wifiManager = (WifiManager) activity
					.getSystemService(activity.WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			int ipadrs = wifiInfo.getIpAddress();
			ipString = String.format("%d.%d.%d.%d", (ipadrs & 0xff),
					(ipadrs >> 8 & 0xff), (ipadrs >> 16 & 0xff),
					(ipadrs >> 24 & 0xff));
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					ip.setText("IP: " + ipString);
				}
			});

			server = new ServerSocket(port);
			client = server.accept();
			reader = new BufferedReader(new InputStreamReader(
					client.getInputStream()));

			if (reader.readLine() == null)
				peace.toast("The input stream is having issues");
			else
				peace.toast("The input stream is fine and dandy");

			message = "";
			charsRead = 0;

			while (true) {

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				char[] buffer = new char[7];
				while ((in.read(buffer)) != -1) {
					read.setText(buffer.toString());
					usbThread.send(new String(buffer));
				}

				// peace.append(read, msg);
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						read.append(msg);
						// usbThread.send(new String(char_clientMsg));
					}
				});
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
