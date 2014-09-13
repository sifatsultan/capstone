package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import main.Coordinates;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.widget.TextView;

public class RecieveWayPoint extends Thread {

	TextView txtView;
	// Server server;
	Activity activity;
	Peace peace;
	Coordinates coordinates;
	ArrayList<Coordinates> arrayList_coordinates;
	private int port;
	private ServerSocket serverSocket;
	private Socket client;
	private InputStreamReader inputStreamReader;
	private BufferedReader bufferedReader;
	private String str_clientMsg;
	private boolean messageYetToParse = true;
	private String wayPoints = "The WayPoints are: \n";
	private boolean arrayListReady = false;

	public RecieveWayPoint(Activity act, int prt, TextView textView) {
		port = prt;
		txtView = textView;
		peace = new Peace(act);
		arrayList_coordinates = new ArrayList<Coordinates>();
	}

	public void run() {
		try {
			serverSocket = new ServerSocket(port);
			client = serverSocket.accept();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			inputStreamReader = new InputStreamReader(client.getInputStream());
			bufferedReader = new BufferedReader(inputStreamReader);
			str_clientMsg = bufferedReader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

		while (messageYetToParse) {
			if (str_clientMsg != null) {
				parse();
				messageYetToParse = false;
			}
		}
		str_clientMsg = null;
	}

	public void parse() {
		try {
			JSONObject jsonObject = new JSONObject(str_clientMsg);
			JSONArray jsonArray_lng = jsonObject.getJSONArray("lat");
			JSONArray jsonArray_lat = jsonObject.getJSONArray("lng");

			for (int i = 0; i < jsonArray_lat.length(); i++) {
				coordinates = new Coordinates(jsonArray_lat.getDouble(i),
						jsonArray_lng.getDouble(i), i);
				arrayList_coordinates.add(coordinates);
			}

			for (int j = 0; j < arrayList_coordinates.size(); j++) {
				wayPoints += "WayPoint " + arrayList_coordinates.get(j).getId()
						+ " Latitude: "
						+ arrayList_coordinates.get(j).getLatitude()
						+ " Longitude: "
						+ arrayList_coordinates.get(j).getLongitude() + "\n";
			}

			arrayListReady = true;

			peace.setText(txtView, wayPoints);
			peace.toastThLng("The JSON :" + str_clientMsg + "\n" + "latitude: "
					+ jsonArray_lng.toString() + "\n" + "longitude: "
					+ jsonArray_lat.toString() + "\n" + "jsonArray_lng size:"
					+ jsonArray_lng.length() + "\n" + "jsonArray_lat size:"
					+ jsonArray_lat.length() + "\n" + "ArrayList Size :"
					+ arrayList_coordinates.size() + "\n" + "Way Points are : "
					+ wayPoints);

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public boolean isArrayListReady() {
		return arrayListReady;
	}

	public ArrayList<Coordinates> getCoordinates() {
		return arrayList_coordinates;
	}
}
