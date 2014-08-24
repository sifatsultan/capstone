package server;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.usb.UsbManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import driver.UsbSerialDriver;
import driver.UsbSerialProber;

public class Server extends Thread {

	int port;
	TextView ip, read;
	Activity mainActivity;
	Peace peace;
	ServerSocket server;
	Socket client;
	DataInputStream reader;
	USBThread usbThread;
	InputStreamReader inputStreamReader;
	BufferedReader bufferedReader;
	String str_clientMsg;
	char[] char_clientMsg;

	private UsbManager usbManager;
	private UsbSerialDriver usbSerialDriver;

	public static final int BUFFER_SIZE = 2048;
	private Socket socket = null;
	private PrintWriter out = null;
	private BufferedReader in = null;

	String message;
	int charsRead;
	// char[] buffer;
	char[] buffer;
	String bufferString;

	String ipString;
	private String msg;
	protected char[] bufferCharArray;

	public Server(int port) {
		this.port = port;
		buffer = new char[7];
	}

	public void ui(Activity activity, TextView ip, TextView read) {
		this.read = read;
		this.ip = ip;
		this.ip.setTextSize(40);
		this.ip.setTextColor(Color.BLACK);
		mainActivity = activity;

		usbManager = (UsbManager) mainActivity
				.getSystemService(Context.USB_SERVICE);

	}

	public void run() {
		try {
			WifiManager wifiManager = (WifiManager) mainActivity
					.getSystemService(mainActivity.WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			int ipadrs = wifiInfo.getIpAddress();
			ipString = String.format("%d.%d.%d.%d", (ipadrs & 0xff),
					(ipadrs >> 8 & 0xff), (ipadrs >> 16 & 0xff),
					(ipadrs >> 24 & 0xff));
			mainActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					ip.setText("IP: " + ipString);
				}
			});

			while (true) {
				while (usbSerialDriver == null) {
					usbSerialDriver = UsbSerialProber
							.findFirstDevice(usbManager);
					if (usbSerialDriver != null) {
						try {
							usbSerialDriver.open();
							usbSerialDriver.setParameters(1200, 8,
									UsbSerialDriver.STOPBITS_1,
									UsbSerialDriver.PARITY_NONE);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				server = new ServerSocket(port);
				client = server.accept();
				while (true) {
					inputStreamReader = new InputStreamReader(
							client.getInputStream());
					bufferedReader = new BufferedReader(inputStreamReader);
					bufferedReader.read(buffer);
					str_clientMsg = String.valueOf(buffer);
					mainActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							read.setText("Message: " + str_clientMsg);
						}
					});

					if (usbSerialDriver != null) {
						usbSerialDriver.write(toBytes(buffer), 7);
					}
				}

			}

		} catch (Exception e) {
			final String error = e.toString();
			mainActivity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(mainActivity, error, Toast.LENGTH_LONG)
							.show();
				}
			});
		}
	}

	private byte[] toBytes(char[] chars) {
		CharBuffer charBuffer = CharBuffer.wrap(chars);
		ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
		byte[] bytes = Arrays.copyOfRange(byteBuffer.array(),
				byteBuffer.position(), byteBuffer.limit());
		Arrays.fill(charBuffer.array(), '\u0000'); // clear sensitive data
		Arrays.fill(byteBuffer.array(), (byte) 0); // clear sensitive data
		return bytes;
	}
}
