package org.coursera.capstone.syman.service;

import java.util.Iterator;
import java.util.List;

import org.coursera.capstone.syman.PatientSettingsActivity;
import org.coursera.capstone.syman.R;
import org.coursera.capstone.syman.api.SymptomServiceApi;
import org.coursera.capstone.syman.model.Checkin;
import org.coursera.capstone.syman.model.Patient;
import org.coursera.capstone.syman.persistence.PatientPersistenceHelper;
import org.coursera.capstone.syman.util.CheckInAlarmHelper;
import org.coursera.capstone.syman.util.IntentFactory;
import org.coursera.capstone.syman.util.SymptomServiceMgr;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.util.Log;
import android.widget.RemoteViews;

public class ReminderService extends IntentService {
	private final String TAG = "DEBUG_JAN";
	private final static String SERVICE_NAME = "ReminderService";
	private static final int PATIENT_NOTIFICATION_ID = 2;
	
	// Notification Text Elements
	private final CharSequence tickerText = "Check-In time...";
	private final CharSequence contentTitle = "Check-In reminder.";
	private final String contentText = "Please perform Check-In.";
	
	public ReminderService() {
		super(SERVICE_NAME);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i(TAG, "Started: " + SERVICE_NAME);
		Log.d(TAG, "Thread name: " + Thread.currentThread().getName());
		
		// Try to update from network in case prescription changes
		SymptomServiceApi symptomService = SymptomServiceMgr.getServiceIfAvailable(this);
		Patient patient = null;
		try {
			patient = symptomService.getPatient();
			if (patient != null) {
				PatientPersistenceHelper.storeOrUpdatePatient(this, patient);
			}
		} catch (Exception e) {
			// Do nothing, just check in with locally cached patient...
		}
		
		SharedPreferences prefs = getSharedPreferences(PatientSettingsActivity.PATIENT_PREFERENCES, Activity.MODE_PRIVATE);
		int frequencyPref = prefs.getInt(PatientSettingsActivity.REMINDER_FREQUENCY_SETTING, -1);
		int nightSettingPref = prefs.getInt(PatientSettingsActivity.NIGHT_TIME_INTERVAL_SETTING, -1);
		boolean nightPref = prefs.getBoolean(PatientSettingsActivity.NIGHT_ENABLE_SETTING, false);
		boolean audioPref = prefs.getBoolean(PatientSettingsActivity.AUDIO_ENABLE_SETTING, false);
		
		// TODO: Set sound for notification if Audio on
		// Send notification
		long[] vibrationPattern = { 0, 200, 200, 300 };
		PendingIntent checkInPendingIntent = 
				IntentFactory.createCheckInPendingIntent(this);
		RemoteViews contentView = new RemoteViews(
				"org.coursera.capstone.syman",
				R.layout.notification_check_in);
		contentView.setTextViewText(R.id.notification_check_in_message, contentText);
		Notification.Builder notificationBuilder = new Notification.Builder(
				getApplicationContext())
			.setTicker(tickerText)
			//.setSmallIcon(android.R.drawable.stat_sys_warning)
			.setSmallIcon(R.drawable.ic_warning)
			.setAutoCancel(true)
			.setContentIntent(checkInPendingIntent)
			.setVibrate(vibrationPattern)
			.setContent(contentView);
		if (audioPref) {
			notificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
		}
		// Pass the Notification to the NotificationManager:
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(PATIENT_NOTIFICATION_ID,
				notificationBuilder.build());
		Log.i(TAG, "Check-In notification sendt.");
		
		// Just set new alarm series, this will make sure that night time settings are honored.
		CheckInAlarmHelper.setNewAlarmSeries(this, frequencyPref, nightSettingPref, nightPref);
		
		// Check if unsent checkIns and try to post to server
		List<Checkin> checkins = PatientPersistenceHelper.getUnsentCheckIns(this);
		if (checkins != null && checkins.size() > 0 && symptomService != null) {
			Log.i(TAG, "Trying to send " + checkins.size() + " unsent checkin(s)...");
			Iterator<Checkin> iter = checkins.iterator();
			while (iter.hasNext()) {
				Checkin checkin = iter.next();
				try {
					symptomService.postPatientCheckin(checkin);
					iter.remove();
				} catch (Exception e) {
					
				}
			}
		}
		PatientPersistenceHelper.storeOrUpdateCheckIns(this, checkins);
	}

}
