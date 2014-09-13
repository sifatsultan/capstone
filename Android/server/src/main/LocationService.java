package main;

import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;

public class LocationService extends Service implements
		GooglePlayServicesClient.OnConnectionFailedListener,
		GooglePlayServicesClient.ConnectionCallbacks, LocationListener {

	private static boolean isRunning;
	TextView current, next, bearing;
	Activity activity;
	ConnectionCallbacks callback;
	OnConnectionFailedListener listener;
	LocationClient location;
	Peace peace;
	LocationListener loclistener;
	private LocationClient locationClient;
	private Messenger mMessenger;
	private List<Messenger> mClients;

	private String locationStr;
	// private NotificationManager mNotificationManager;
	private String notificationStr;
	private String LOGTAG = "Server";

	public static final int MSG_REGISTER_CLIENT = 1;
	public static final int MSG_UNREGISTER_CLIENT = 2;
	public static final int MSG_SET_INT_VALUE = 3;
	public static final int MSG_SET_STRING_VALUE = 4;
	public static final int MSG_NOTIFICATION = 5;
	public static final int MSG_LOCATION = 6;

	public static boolean isRunning() {
		return isRunning;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(LOGTAG, "Received start id " + startId + ": " + intent);
		return START_STICKY; // Run until explicitly stopped.
	}

	@Override
	public void onLocationChanged(android.location.Location loc) {

		locationStr = "Lat: " + loc.getLatitude() + " Lng: "
				+ loc.getLongitude();
		sendLocation();

	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		sendNotification();
	}

	@Override
	public void onConnected(Bundle arg0) {
		notificationStr = "Client is connected";
		locationStr = "Lat: " + locationClient.getLastLocation().getLatitude()
				+ " Lng: " + locationClient.getLastLocation().getLongitude();
		sendNotification();
		sendLocation();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		locationClient = new LocationClient(this, this, this);
		locationClient.connect();
		notificationStr = "LocationService is created";
		sendNotification();
		//
		// Log.i(LOGTAG, "Service Started.");
		// showNotification();
		// mTimer.scheduleAtFixedRate(new MyTask(), 0, 100L);
		isRunning = true;
	}

	/**
	 * Handle incoming messages from MainActivity
	 */
	private class IncomingMessageHandler extends Handler { // Handler of
															// incoming messages
															// from clients.
		@Override
		public void handleMessage(Message msg) {
//			Log.d(LOGTAG, "handleMessage: " + msg.what);
			switch (msg.what) {
			case MSG_REGISTER_CLIENT:
				mClients.add(msg.replyTo);
				break;
			case MSG_UNREGISTER_CLIENT:
				mClients.remove(msg.replyTo);
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}

	private void sendNotification() {
		Iterator<Messenger> messengerIterator = mClients.iterator();
		while (messengerIterator.hasNext()) {
			Messenger messenger = messengerIterator.next();
			try {
				Bundle bundle = new Bundle();
				bundle.putString("Notification", notificationStr);
				Message msg = Message.obtain(null, MSG_NOTIFICATION);
				msg.setData(bundle);
				messenger.send(msg);
			} catch (RemoteException e) {
				mClients.remove(messenger);
			}
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i("LocationService", "Service Stopped.");
		isRunning = false;

	}

	private void sendLocation() {
		Iterator<Messenger> messengerIterator = mClients.iterator();
		while (messengerIterator.hasNext()) {
			Messenger messenger = messengerIterator.next();
			try {
				Bundle bundle = new Bundle();
				bundle.putString("Location", locationStr);
				Message msg = Message.obtain(null, MSG_LOCATION);
				msg.setData(bundle);
				messenger.send(msg);
			} catch (RemoteException e) {
				mClients.remove(messenger);
			}
		}
	}

	@Override
	public void onDisconnected() {

	}

	@Override
	public IBinder onBind(Intent intent) {
		return mMessenger.getBinder();
	}

}
