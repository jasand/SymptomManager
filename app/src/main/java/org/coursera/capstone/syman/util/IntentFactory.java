package org.coursera.capstone.syman.util;

import org.coursera.capstone.syman.DoctorMainActivity;
import org.coursera.capstone.syman.PatientCheckinActivity;
import org.coursera.capstone.syman.service.AlarmPollerService;
import org.coursera.capstone.syman.service.ReminderService;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class IntentFactory {

	public static PendingIntent createAlarmPollerServicePendingIntent(Context context) {
		Intent alarmPollerIntent = new Intent(context, AlarmPollerService.class);
		PendingIntent alarmPollerPendingIntent = PendingIntent.getService(context,
				0, alarmPollerIntent, 0);
		return alarmPollerPendingIntent;
	}
	
	public static PendingIntent createDoctorHomePendingIntent(Context context) {
		Intent doctorHomeIntent = new Intent(context, DoctorMainActivity.class);
        doctorHomeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent doctorHomePendingIntent = PendingIntent.getActivity(context,
				0, doctorHomeIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
		return doctorHomePendingIntent;
	}
	
	public static PendingIntent createReminderServicePendingIntent(Context context) {
		Intent reminderIntent = new Intent(context, ReminderService.class);
		PendingIntent reminderPendingIntent = PendingIntent.getService(context,
				0, reminderIntent, 0);
		return reminderPendingIntent;
	}
	
	public static PendingIntent createCheckInPendingIntent(Context context) {
		Intent checkInIntent = new Intent(context, PatientCheckinActivity.class);
		PendingIntent checkInPendingIntent = PendingIntent.getActivity(context,
				0, checkInIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
		return checkInPendingIntent;
	}
}
