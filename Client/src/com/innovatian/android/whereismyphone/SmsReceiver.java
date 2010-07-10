package com.innovatian.android.whereismyphone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {
	static final String TRIGGER = "PING";
	static final String TAG = "WhereIsMyPhone";
	static final int AUDIO_LEVELS = 7;

	@Override
	public void onReceive(Context context, Intent intent) {
		String target = context.getString(R.string.sms_received);
		if (!intent.getAction().equals(target)) {
			return;
		}
		Log.d(TAG, "Received sms.");
		Bundle bundle = intent.getExtras();
		if (bundle == null) {
			return;
		}

		boolean found = isTriggerFound(bundle);
		Log.i(TAG, "Received sms with trigger.");
		if (found) {
			performPing(context);
		}
	}

	private boolean isTriggerFound(Bundle bundle) {
		Object[] pdus = (Object[]) bundle.get("pdus");
		SmsMessage[] msgs = new SmsMessage[pdus.length];
		for (int i = 0; i < msgs.length; i++) {
			SmsMessage message = SmsMessage.createFromPdu((byte[]) pdus[i]);
			String body = message.getMessageBody().toString();
			if (body.toUpperCase().startsWith(TRIGGER)) {
				return true;
			}
		}
		return false;
	}

	private void performPing(Context context) {
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		int ringerMode = audioManager.getRingerMode();
		if (ringerMode != AudioManager.RINGER_MODE_NORMAL) {
			audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		}

		raiseVolumeToMax(audioManager);
		ring(context);
	}

	private void raiseVolumeToMax(AudioManager audioManager) {
		for (int i = 0; i < AUDIO_LEVELS; i++) {
			audioManager.adjustVolume(AudioManager.ADJUST_RAISE, 0);
		}
	}

	private void ring(Context context) {
		Ringtone ringtone = getDefaultRingtone(context);
		if (ringtone == null) {
			// the default ringtone is null.
			Log.wtf(TAG, "There is no default ringtone!");
		} else {
			playRingtone(ringtone);
		}
	}

	private Ringtone getDefaultRingtone(Context context) {
		RingtoneManager ringtoneManager = new RingtoneManager(context);
		int position = ringtoneManager
				.getRingtonePosition(Settings.System.DEFAULT_RINGTONE_URI);
		Ringtone ringtone = ringtoneManager.getRingtone(position);
		return ringtone;
	}

	private void playRingtone(Ringtone ringtone) {
		// TODO: Pull this time from settings.
		int millis = 90000;
		try {
			ringtone.wait(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ringtone.stop();
	}
}
