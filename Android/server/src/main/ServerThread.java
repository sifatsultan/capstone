package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.TextView;

public class ServerThread extends Thread{

	private Activity activity;
	private TextView dataView, usbView, ipadrs;

	public ServerThread(Activity act) {
		activity = act;

	}

	public void initui(TextView data, TextView usb, TextView ip) {
		dataView = data;
		usbView = usb;
		ipadrs = ip;
	}

	public void run() {
		try {
			Peace peace = new Peace(activity);

			WifiManager wifiMan = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInf = wifiMan.getConnectionInfo();
			int ipAddress = wifiInf.getIpAddress();
			String ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
					(ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));

			peace.toastThLng("IP: " + ip);
			// ipadrs.setText("IP :" + ip.toString());
			USB usb = new USB(activity, usbView);
			usb.start();

			ServerSocket server = new ServerSocket(3030);
			Socket client = server.accept();
			BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));

			peace.toastThLng("Socket and USB working");

			while (true) {
				if (reader.readLine() != null) {
					// usb.send(reader.read());
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
