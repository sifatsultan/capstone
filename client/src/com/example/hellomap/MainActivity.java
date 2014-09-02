package com.example.hellomap;

import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends FragmentActivity {
	private GoogleMap googleMap;
	private Button upload, btn_remove_marker, btn_my_location, connect;
	private LatLng adu_latlng;
	private EditText ip;

	private String jsonStr;
	private int count = 0;
	private JSONArray latJsonArr, lngJsonArr;
	private String ipadrs = null;

	private Activity mainAcitivity = this;

	int portInt = 0;
	Client client;

	// EditText(ip)

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.map_activity);
		setUpMapIfNeeded();

		upload = (Button) findViewById(R.id.btn_toast);
		btn_remove_marker = (Button) findViewById(R.id.btn_remove_marker);
		btn_my_location = (Button) findViewById(R.id.btn_my_location);
		ip = (EditText) findViewById(R.id.ipadrs);
		ip.setHint("Write the IP:port");
		connect = (Button) findViewById(R.id.connect);
		connect.setText("Connect");

		latJsonArr = new JSONArray();
		lngJsonArr = new JSONArray();

		String aduAddress = "Abu Dhabi University";
		List<Address> aduAddressLatLngList = null;
		try {
			aduAddressLatLngList = new Geocoder(this).getFromLocationName(
					aduAddress, 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		adu_latlng = new LatLng(aduAddressLatLngList.get(0).getLatitude(),
				aduAddressLatLngList.get(0).getLongitude());

		connect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ipadrs = ip.getText().toString();
				String[] parts = ipadrs.split(":");
				String ip = parts[0]; // 004
				String portStr = parts[1]; // 034556
				portInt = Integer.valueOf(portStr);

				if (ipadrs != null && portInt != 0) {
					client = new Client(mainAcitivity, portInt, ip);
					client.start();
				} else {
					Toast.makeText(mainAcitivity,
							"Write the IP address first below",
							Toast.LENGTH_LONG).show();
				}

			}
		});
		upload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				JSONObject jsonObj = new JSONObject();

				try {
					jsonObj.put("lat", latJsonArr);
					jsonObj.put("lng", lngJsonArr);
				} catch (JSONException e) {
					Log.e("MainActivity/Client", e.toString());
				}

				jsonStr = jsonObj.toString();
				client.write(jsonStr);
				Toast.makeText(mainAcitivity, jsonStr, Toast.LENGTH_LONG)
						.show();

			}
		});

		btn_remove_marker.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// resetting everything for the polyline and upload issue
				googleMap.clear();
				latJsonArr = new JSONArray();
				lngJsonArr = new JSONArray();
				count = 0;

			}
		});

		btn_my_location.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CameraPosition cameraPosition = new CameraPosition.Builder()
						.target(adu_latlng).zoom(17).build();
				googleMap.animateCamera(
						CameraUpdateFactory.newCameraPosition(cameraPosition),
						400, null);
			}
		});

		googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
			@Override
			public void onMapClick(LatLng point) {

				Marker marker = null;
				marker = googleMap.addMarker(new MarkerOptions()
						.position(new LatLng(point.latitude, point.longitude))
						.draggable(true)
						.snippet(
								"[" + point.latitude + "," + point.longitude
										+ "]"));

				int wayPoint = count + 1;
				marker.setTitle("Way-Point " + wayPoint);

				double lat = point.latitude;
				double lng = point.longitude;

				try {
					latJsonArr.put(lat);
					lngJsonArr.put(lng);
				} catch (JSONException e) {
					Log.e("MainActivity/Client", e.toString());
				}

				// adding a poly line from the last marker to the current marker
				if (latJsonArr.length() >= 2) {

					try {
						LatLng previous = new LatLng(latJsonArr
								.getDouble(count - 1), lngJsonArr
								.getDouble(count - 1));

						// (count-1) is to get the previous marker

						LatLng current = new LatLng(
								latJsonArr.getDouble(count), lngJsonArr
										.getDouble(count));

						Polyline polyline = googleMap
								.addPolyline(new PolylineOptions().add(
										previous, current));
					} catch (JSONException e) {
						Toast.makeText(mainAcitivity,
								"Exception :" + e.toString(), Toast.LENGTH_LONG)
								.show();
					}

				}
				count++;
			}
		});

		googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

			@Override
			public void onMarkerDragStart(Marker arg0) {
			}

			@Override
			public void onMarkerDragEnd(Marker arg0) {
			}

			@Override
			public void onMarkerDrag(Marker marker) {

				LatLng point = marker.getPosition();
				marker.setSnippet("[" + point.latitude + "," + point.longitude
						+ "]");
			}
		});

	}

	private void setUpMapIfNeeded() {
		if (googleMap != null) {
			return;
		}
		googleMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.googlemap)).getMap();
		if (googleMap == null) {
			return;
		}
	}

}
