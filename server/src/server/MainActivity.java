package server;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.server.R;

public class MainActivity extends Activity {

	// ip, read
	private Activity activity = this;
	private Peace peace;

	private TextView read;
	private StackTraceElement[] trace;

	private TextView ip, read1, read2;
	private Button refresh;
	private final Messenger mMessenger = new Messenger(
			new IncomingMessageHandler());

	private TextView screenName, ip_value, global_ip_value, connection_value,
			mode_value, aileron_value, elevation_value, thrust_value,
			rudder_value, reply_value, json_value, current, next, bearing;
	private Button start_udp, start_cameraStream;
	private ServiceConnection mConnection;

	private boolean mIsBound;
	private Messenger mServiceMessenger;
	private String LOGTAG = "Server";
	private String serviceNotification;
	public String serviceLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		try {

			ip_value = (TextView) findViewById(R.id.ip_value);
			global_ip_value = (TextView) findViewById(R.id.global_ip_value);
			connection_value = (TextView) findViewById(R.id.connection_value);
			rudder_value = (TextView) findViewById(R.id.rudder_value);
			mode_value = (TextView) findViewById(R.id.mode_value);
			aileron_value = (TextView) findViewById(R.id.aileron_value);
			elevation_value = (TextView) findViewById(R.id.elevation_value);
			thrust_value = (TextView) findViewById(R.id.thrust_value);
			reply_value = (TextView) findViewById(R.id.reply_value);
			json_value = (TextView) findViewById(R.id.json_value);
			current = (TextView) findViewById(R.id.current);
			next = (TextView) findViewById(R.id.next);
			bearing = (TextView) findViewById(R.id.bearing);

			start_udp = (Button) findViewById(R.id.start_udp);
			start_cameraStream = (Button) findViewById(R.id.start_cameraStream);

			JSON json = new JSON(4040);
			json.ui(activity, json_value);
			json.start();

			ServerUDP serverUdp = new ServerUDP(3030);
			serverUdp.uiQuad(activity, aileron_value, elevation_value,
					thrust_value, rudder_value, connection_value, mode_value,
					ip_value, global_ip_value, reply_value);
			serverUdp.start();

			mConnection = new ServiceConnection() {

				// TODO: The service is either not starting or not binding to
				// the activity4
				@Override
				public void onServiceConnected(ComponentName name,
						IBinder service) {
					mServiceMessenger = new Messenger(service);
					Toast.makeText(activity, "Client is connected",
							Toast.LENGTH_SHORT).show();
					try {
						Message msg = Message.obtain(null,
								LocationService.MSG_REGISTER_CLIENT);
						msg.replyTo = mMessenger;
						mServiceMessenger.send(msg);
					} catch (RemoteException e) {
					}

				}

				@Override
				public void onServiceDisconnected(ComponentName name) {
					mServiceMessenger = null;
					current.setText("Disconnected");

				}
			};
			;

			startService(new Intent(MainActivity.this, LocationService.class));
			Toast.makeText(activity, "Requested to start Service",
					Toast.LENGTH_SHORT).show();
			doBindService();
			automaticBind();
			// TODO: Bearing from Waypoints, Stream

		} catch (Exception e) {
			for (int i = 0; i < 10; i++) {
				Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG)
						.show();
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			doUnbindService();
		} catch (Throwable t) {
			Log.e(LOGTAG, "Failed to unbind from the service", t);
		}
	}

	/**
	 * Handle incoming messages from MyService
	 */
	private class IncomingMessageHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// Log.d(LOGTAG,"IncomingHandler:handleMessage");
			switch (msg.what) {
			case LocationService.MSG_LOCATION:
				serviceLocation = msg.getData().getString("Location");
				current.setText(serviceLocation);
			case LocationService.MSG_NOTIFICATION:
				serviceNotification = msg.getData().getString("Notification");
				current.setText(serviceNotification);
			default:
				super.handleMessage(msg);
			}
		}
	}

	/**
	 * Bind this Activity to MyService
	 */
	private void doBindService() {
		bindService(new Intent(this, LocationService.class), mConnection,
				Context.BIND_AUTO_CREATE);
		mIsBound = true;
		Toast.makeText(activity, "MainActivity is binding", Toast.LENGTH_SHORT)
				.show();
		current.setText("Binding.");
	}

	/**
	 * Un-bind this Activity to MyService
	 */
	private void doUnbindService() {
		if (mIsBound) {
			// If we have received the service, and hence registered with it,
			// then now is the time to unregister.
			if (mServiceMessenger != null) {
				try {
					Message msg = Message.obtain(null,
							LocationService.MSG_UNREGISTER_CLIENT);
					msg.replyTo = mMessenger;
					mServiceMessenger.send(msg);
				} catch (RemoteException e) {
					// There is nothing special we need to do if the service has
					// crashed.
				}
			}
			// Detach our existing connection.
			unbindService(mConnection);
			mIsBound = false;
			current.setText("Unbinding.");
		}
	}

	private void automaticBind() {
		if (LocationService.isRunning()) {
			doBindService();
		}
	}

}
