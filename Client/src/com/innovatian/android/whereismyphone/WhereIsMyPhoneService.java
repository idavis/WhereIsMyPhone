package com.innovatian.android.whereismyphone;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class WhereIsMyPhoneService extends Service {
	static final String TAG = "WhereIsMyPhone";

	public WhereIsMyPhoneService() {
		Log.d(TAG, "Where Is My Phone Service Created");
		String target = getString(R.string.sms_received);
		registerReceiver(new SmsReceiver(), new IntentFilter(target));
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}