package com.example.sockettest;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.app.Activity;

public class ClientUDP extends Thread{
	Peace peace;

	int port;
	String ipadrs;
	PrintWriter printWriter;
	String message;
	boolean alive = true;

	DatagramSocket client = null;
	DatagramPacket packet;
	byte[] sendBuf = new byte[256];

	public ClientUDP(int prt, String ip, Activity activity) {
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
			DatagramSocket serverSocket = new DatagramSocket(9876);
			byte[] receiveData = new byte[1024];
			byte[] sendData = new byte[1024];
			while (true) {
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				String sentence = new String(receivePacket.getData());
				System.out.println("RECEIVED: " + sentence);
				InetAddress IPAddress = receivePacket.getAddress();
				int port = receivePacket.getPort();
				String capitalizedSentence = sentence.toUpperCase();
				sendData = capitalizedSentence.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
				serverSocket.send(sendPacket);
			}

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
}
