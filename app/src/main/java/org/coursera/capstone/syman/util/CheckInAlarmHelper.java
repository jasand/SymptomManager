package org.coursera.capstone.syman.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.util.Log;

public class CheckInAlarmHelper {
	private static String TAG = "DEBUG_JAN";

	public static void setNewAlarmSeries(
			Context context,
			int frequency,
			int nightSetting,
			boolean nightSelected) {
		// Cancel any previous alarm
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pendingIntent = IntentFactory.createReminderServicePendingIntent(context);
		alarmManager.cancel(pendingIntent);
		
		// Set new alarm (Could be the same if onCreate after restart of app)
		long millisecInterval = frequency * CommonDefs.MILLISECS_IN_HOUR;
		int[] alarmSchedule = getAlarmSchedule(frequency, nightSetting, nightSelected);
		long millisecToNextAlarm = calculateMillisecToNextAlarm(frequency, alarmSchedule);
		Log.i(TAG, "Scheduling next alarm in " + millisecToNextAlarm/1000 + " seconds.");
//		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
//				System.currentTimeMillis() + millisecToNextAlarm,
//				millisecInterval , pendingIntent);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis() + 120000l,
				120000l , pendingIntent);
	}
	
	
	public static int[] getAlarmSchedule(
			int frequency,
			int nightSetting,
			boolean nightSelected) {
		int[] alarmSchedule = null;
		if (!nightSelected && frequency == 6) {
			alarmSchedule = new int[]{6, 12, 18, 00};
		} else if (!nightSelected && frequency == 3) {
			alarmSchedule = new int[]{3, 6, 9, 12, 15, 18, 21, 00};
		} else if (!nightSelected && frequency == 2) {
			alarmSchedule = new int[]{2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 00};
		} else if (nightSelected && frequency == 6 && nightSetting == 22) {
			alarmSchedule = new int[]{4, 10, 16, 22};
		} else if (nightSelected && frequency == 6 && nightSetting == 23) {
			alarmSchedule = new int[]{5, 11, 17, 23};
		} else if (nightSelected && frequency == 6 && nightSetting == 0) {
			alarmSchedule = new int[]{6, 12, 18, 00};
		} else if (nightSelected && frequency == 6 && nightSetting == 1) {
			alarmSchedule = new int[]{7, 13, 19, 01};
		} else if (nightSelected && frequency == 3 && nightSetting == 22) {
			alarmSchedule = new int[]{4, 7, 10, 13, 16, 19, 22};
		} else if (nightSelected && frequency == 3 && nightSetting == 23) {
			alarmSchedule = new int[]{5, 8, 11, 14, 17, 20, 23};
		} else if (nightSelected && frequency == 3 && nightSetting == 0) {
			alarmSchedule = new int[]{6, 9, 12, 15, 18, 21, 00};
		} else if (nightSelected && frequency == 3 && nightSetting == 1) {
			alarmSchedule = new int[]{7, 10, 13, 16, 19, 22, 01};
		} else if (nightSelected && frequency == 2 && nightSetting == 22) {
			alarmSchedule = new int[]{4, 6, 8, 10, 12, 14, 16, 18, 20, 22};
		} else if (nightSelected && frequency == 2 && nightSetting == 23) {
			alarmSchedule = new int[]{5, 7, 9, 11, 13, 15, 17, 19, 21, 23};
		} else if (nightSelected && frequency == 2 && nightSetting == 0) {
			alarmSchedule = new int[]{6, 8, 10, 12, 14, 16, 18, 20, 22, 00};
		} else if (nightSelected && frequency == 2 && nightSetting == 1) {
			alarmSchedule = new int[]{7, 9, 11, 13, 15, 17, 19, 21, 23, 01};
		}
		return alarmSchedule;
	}
	
	public static long calculateMillisecToNextAlarm(int frequency, int[] alarmSchedule) {
		Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(System.currentTimeMillis());
		int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
		for (int i=0 ; i<alarmSchedule.length ; i++) {
			if (i == alarmSchedule.length-1) {
				if (alarmSchedule[i] == 1) {
					cal.roll(Calendar.DATE, 1);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.HOUR_OF_DAY, alarmSchedule[i]);
					break;
				} else if (alarmSchedule[i] == 0) {
					if (cal.get(Calendar.HOUR_OF_DAY) != 23 && cal.get(Calendar.MINUTE) != 59) {
						cal.set(Calendar.SECOND, 59);
						cal.set(Calendar.MINUTE, 59);
						cal.set(Calendar.HOUR_OF_DAY, 23);
						break;
					} else {
						cal.roll(Calendar.DATE, 1);
						cal.set(Calendar.SECOND, 0);
						cal.set(Calendar.MINUTE, 0);
						cal.set(Calendar.HOUR_OF_DAY, alarmSchedule[0]);
					}
				} else {
					if (hourOfDay < alarmSchedule[i]) {
						cal.set(Calendar.SECOND, 0);
						cal.set(Calendar.MINUTE, 0);
						cal.set(Calendar.HOUR_OF_DAY, alarmSchedule[i]);
						break;
					} else {
						cal.roll(Calendar.DATE, 1);
						cal.set(Calendar.SECOND, 0);
						cal.set(Calendar.MINUTE, 0);
						cal.set(Calendar.HOUR_OF_DAY, alarmSchedule[0]);
						break;
					}
				}
			} else if (hourOfDay < alarmSchedule[i]) {
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.HOUR_OF_DAY, alarmSchedule[i]);
				break;
			}
		}
		
		Log.i(TAG, "Returning next alarm time as: " + new Date(cal.getTimeInMillis()));
		return cal.getTimeInMillis() - System.currentTimeMillis();
	}
}
