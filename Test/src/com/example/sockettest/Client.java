package com.example.sockettest;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;

public class Client extends Thread{
	Peace peace;

	int port;
	String ipadrs;
	PrintWriter printWriter;
	String message;
	boolean alive = true;

	public Client(int prt, String ip, Activity activity) {
		peace = new Peace(activity);
		ipadrs = ip;
		port = prt;
		peace.toast("Press submit to complete connection process");
	}

	public void write(String msg) {
		message = msg;
	}

	public void close() {
		alive = false;
	}

	public void run() {
		try {
			Socket client = new Socket(ipadrs, port);
			printWriter = new PrintWriter(client.getOutputStream(), true);

			while (alive) {
				if (message != null)
					printWriter.println(message);
				message = null;
			}
			client.close();

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
