package com.example.hellomap;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;

public class SocketThread extends Thread{

	private Peace peace;

	private Socket client = null;
	private PrintWriter printWriter;
	private int port;
	private String ipAddress;
	private String str_clientMsg = "";
	private boolean keepTrying;

	private Activity mainActivity;

	public SocketThread(Activity activity, int prt, String ip) {
		mainActivity = activity;
		port = prt;
		peace = new Peace(mainActivity);
	}

	public void run() {
		while (keepTrying)
			try {
				client = new Socket(ipAddress, port);

				if (client != null) {
					peace.toast("Socket Established");
					printWriter = new PrintWriter(client.getOutputStream());
					printWriter.write(str_clientMsg);
					keepTrying = false;
				}
			} catch (UnknownHostException e) {
				peace.toast("Server not there");
			} catch (IOException e) {
				peace.toast("Didnt get the message right");
			}
	}

}
