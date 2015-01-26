package org.coursera.capstone.syman.service;

import java.util.List;

import org.coursera.capstone.syman.R;
import org.coursera.capstone.syman.api.SymptomServiceApi;
import org.coursera.capstone.syman.model.Patient;
import org.coursera.capstone.syman.util.IntentFactory;
import org.coursera.capstone.syman.util.SymptomServiceMgr;

import retrofit.RetrofitError;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.util.Log;
import android.widget.RemoteViews;

public class AlarmPollerService extends IntentService {
	private final String TAG = "DEBUG_JAN";
	private final static String SERVICE_NAME = "AlarmPollerService";
	private static final int DOCTOR_NOTIFICATION_ID = 1;
	
	// Notification Text Elements
	private final CharSequence tickerText = "Check up on patients!";
	private final CharSequence contentTitle = "Patient alert.";
	private final String contentText = "Patients in need of follow-up: ";

	public AlarmPollerService() {
		super(SERVICE_NAME);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i(TAG, "Started: " + SERVICE_NAME);
		Log.d(TAG, "Thread name: " + Thread.currentThread().getName());
		SymptomServiceApi symptomService = SymptomServiceMgr.getServiceIfAvailable(this);
		if (symptomService != null) {
			try {
				List<Patient> patientsWithAlarm = symptomService.getPatientsWithAlarms();
				if (patientsWithAlarm != null && patientsWithAlarm.size() > 0) {
					// Send notification
					long[] vibrationPattern = { 0, 200, 200, 300 };
					PendingIntent doctorHomePendingIntent = 
							IntentFactory.createDoctorHomePendingIntent(this);
					RemoteViews contentView = new RemoteViews(
							"org.coursera.capstone.syman",
							R.layout.notification_doctor);
					contentView.setTextViewText(R.id.notification_doctor_message, 
							contentText + patientsWithAlarm.size());
					Notification.Builder notificationBuilder = new Notification.Builder(
							getApplicationContext())
						.setTicker(tickerText)
						//.setSmallIcon(android.R.drawable.stat_sys_warning)
						.setSmallIcon(R.drawable.ic_warning)
						.setAutoCancel(true)
						.setContentIntent(doctorHomePendingIntent)
						.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
						.setVibrate(vibrationPattern)
						.setContent(contentView);

					// Pass the Notification to the NotificationManager:
					NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
					notificationManager.notify(DOCTOR_NOTIFICATION_ID,
							notificationBuilder.build());
					Log.i(TAG, "Notification sendt to doctor.");
				}
			} catch (RetrofitError rErr) {
				Log.e(TAG, "RetrofitError: " + rErr.toString());
			} catch (Exception e) {
				Log.e(TAG, "Exception: " + e.toString());
			}
		} else {
			Log.e(TAG, "Could not get SymptomServiceApi handle.");
		}
	}

}
