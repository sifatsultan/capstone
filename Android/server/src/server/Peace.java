package server;

import android.app.Activity;
import android.widget.TextView;
import android.widget.Toast;

public class Peace {

	private Activity mainActivity;
	private TextView txtView;

	public Peace(Activity activity) {
		mainActivity = activity;
	}

	public void append(final TextView textView, final String string) {
		mainActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {

				textView.append(string);
			}
		});
	}

	public void setText(final TextView textView, final String string) {
		mainActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {

				textView.setText(string);
			}
		});
	}

	public void toastThLng(final String string) {

		mainActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(mainActivity, string, Toast.LENGTH_LONG).show();
			}
		});
	}

	public void toastThShrt(final String string) {
		mainActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(mainActivity, string, Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void stackTrace(Thread thr, Exception e) {
		Thread th = thr;
		for (int i = 0; i < 20; i++) {
			Toast.makeText(
					mainActivity,

					e.toString() + "\n"

					+ "at	" + th.getStackTrace()[2].getClassName() + "."
							+ th.getStackTrace()[2].getMethodName() + " Line: "
							+ th.getStackTrace()[2].getLineNumber() + "\n"

							+ "at	" + th.getStackTrace()[3].getClassName()
							+ "." + th.getStackTrace()[3].getMethodName()
							+ " Line: " + th.getStackTrace()[3].getLineNumber()
							+ "\n"

							+ "at	" + th.getStackTrace()[4].getClassName()
							+ "." + th.getStackTrace()[4].getMethodName()
							+ " Line: " + th.getStackTrace()[4].getLineNumber()
							+ "\n"

							+ "at	" + th.getStackTrace()[5].getClassName()
							+ "." + th.getStackTrace()[5].getMethodName()
							+ " Line: " + th.getStackTrace()[5].getLineNumber()
							+ "\n",

					Toast.LENGTH_LONG).show();
		}
	}

	public void toastLng(String strng) {
		Toast.makeText(mainActivity, strng, Toast.LENGTH_LONG).show();
	}

	public void toastLngLng(String strng) {
		for (int i = 0; i < 10; i++) {
			Toast.makeText(mainActivity, strng, Toast.LENGTH_LONG).show();
		}
	}

	public void toastShrt(String strng) {
		Toast.makeText(mainActivity, strng, Toast.LENGTH_SHORT).show();
	}
}
