package com.example.sockettest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.app.Activity;

public class ServerUDP extends Thread{
	Peace peace;

	int port;
	String ipadrs;
	PrintWriter printWriter;
	String message;
	boolean alive = true;

	DatagramSocket client = null;
	DatagramPacket packet;
	byte[] sendBuf = new byte[256];

	public ServerUDP(int prt, String ip, Activity activity) {
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
			BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
			DatagramSocket clientSocket = new DatagramSocket();
			InetAddress IPAddress = InetAddress.getByName("localhost");
			byte[] sendData = new byte[1024];
			byte[] receiveData = new byte[1024];
			String sentence = inFromUser.readLine();
			sendData = sentence.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
			clientSocket.send(sendPacket);
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			clientSocket.receive(receivePacket);
			String modifiedSentence = new String(receivePacket.getData());
			System.out.println("FROM SERVER:" + modifiedSentence);
			clientSocket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
