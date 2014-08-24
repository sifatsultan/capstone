package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.Button;
import android.widget.TextView;

public class JSON extends Thread {

	private int port;
	private ServerSocket serverSocket = null;
	private Socket client = null;
	private BufferedReader bufferedReader = null;
	Peace peace;
	Activity activity;
	TextView read, ip;
	private String msg;

	public JSON(int port) {
		this.port = port;
	}

	public void ui(Activity activity, TextView read) {
		this.read = read;
		this.activity = activity;
		peace = new Peace(activity);
	}

	public void run() {
		try {
			// WifiManager wifiManager = (WifiManager) activity
			// .getSystemService(activity.WIFI_SERVICE);
			// WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			// int ipadrs = wifiInfo.getIpAddress();
			//
			// peace.setText(
			// ip,
			// "Waiting for JSON at "
			// + "IP:	"
			// + String.format("%d.%d.%d.%d", (ipadrs & 0xff),
			// (ipadrs >> 8 & 0xff),
			// (ipadrs >> 16 & 0xff),
			// (ipadrs >> 24 & 0xff)) + ": " + port);

			serverSocket = new ServerSocket(port);
			peace.append(read, "Waiting for JSON on port: " + port);
			client = serverSocket.accept();
			peace.append(read, "Socket Established");

			bufferedReader = new BufferedReader(new InputStreamReader(
					client.getInputStream()));
			while (true) {
				msg = bufferedReader.readLine();
				if (msg != null)
					parse();
			}

			// client.close();
			// peace.toastThShrt("Socket Closed");
		} catch (Exception e) {
			peace.toastThLng(e.toString());
		}
	}

	public void parse() {
		peace.setText(read, "");
		try {
			JSONObject jsonObject = new JSONObject(msg);
			JSONArray lngJsonArr = jsonObject.getJSONArray("lat");
			JSONArray latJsonArr = jsonObject.getJSONArray("lng");

			String data = "";
			for (int i = 0; i < latJsonArr.length(); i++) {
				data += "waypoint:" + i + " Latitude: "
						+ latJsonArr.getDouble(i) + " Longitude: "
						+ lngJsonArr.getDouble(i) + "\n";

			}
			peace.setText(
					read,
					"The JSON :" + msg + " \n" + "latitude: "
							+ latJsonArr.toString() + "\n" + "longitude: "
							+ lngJsonArr.toString() + "\n" + data);

			msg = null;

		} catch (JSONException e) {
			peace.toastThLng(e.toString());
		}

	}

}
