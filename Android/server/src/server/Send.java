package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Activity;

public class Send extends Thread{

	PrintWriter printWriter;
	boolean alive = true;

	public Send(int port) {

		
		try {
			ServerSocket server = new ServerSocket(port);
			Socket client = server.accept();
			printWriter = new PrintWriter(client.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while (alive) {

		}
	}

}
