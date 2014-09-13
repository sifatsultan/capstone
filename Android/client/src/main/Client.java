package main;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.util.Log;

public class Client extends Thread {
	Peace peace;
	int port;
	String ipAddress;
	String clientMsg;

	private Activity mainActivity;
	PrintWriter printWriter;
	Socket client;

	public Client(Activity activity, int prt, String ip) {
		mainActivity = activity;
		ipAddress = ip;
		port = prt;
		peace = new Peace(mainActivity);
	}

	public void write(String message) {
		clientMsg = message;
	}

	public void run() {
		try {
			client = new Socket(ipAddress, port);
			printWriter = new PrintWriter(client.getOutputStream(), true);
			peace.toast("Socket established");

			while (true) {
				if (clientMsg != null) {
					printWriter.println(clientMsg);
					peace.toast("Data Sent");
					clientMsg = null;
				}
			}
		} catch (UnknownHostException e) {
			Log.e("ClientThread", e.toString());
		} catch (IOException e) {
			Log.e("ClientThread", e.toString());
		}

	}

}
