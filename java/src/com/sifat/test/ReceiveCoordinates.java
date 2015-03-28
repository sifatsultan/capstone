package com.sifat.test;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;

public class ReceiveCoordinates {
	
	public ReceiveCoordinates(){
		
		try {
			System.out.print("IP:"+ Inet4Address.getLocalHost());
			ServerSocket server = new ServerSocket(8080);
			Socket client = server.accept();
			System.out.print("Client connected");
			
			DataInputStream is = new DataInputStream(client.getInputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			
			while(true){
				String msg = reader.readLine();
				System.out.print(msg);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args){
		ReceiveCoordinates myServer = new ReceiveCoordinates();
	}

}
