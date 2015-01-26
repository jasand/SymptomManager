package org.coursera.capstone.syman;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.coursera.capstone.syman.util.CheckInAlarmHelper;
import org.coursera.capstone.syman.util.CommonDefs;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

public class PatientSettingsActivity extends Activity {
	public static final String PATIENT_PREFERENCES = "PATIENT_PREFERENCES";
	public static final String REMINDER_FREQUENCY_SETTING = "REMINDER_FREQUENCY_SETTING";
	public static final String NIGHT_ENABLE_SETTING = "NIGHT_ENABLE_SETTING";
	public static final String NIGHT_TIME_INTERVAL_SETTING = "NIGHT_TIME_INTERVAL_SETTING";
	public static final String AUDIO_ENABLE_SETTING = "AUDIO_ENABLE_SETTING";
	private final String TAG = "DEBUG_JAN";
	private List<FrequencyItem> mFrequencyItems = new ArrayList<FrequencyItem>();
	private List<NightTimeItem> mNightTimeItems = new ArrayList<NightTimeItem>();
	private ArrayAdapter<FrequencyItem> mFrequencyAdapter;
	private ArrayAdapter<NightTimeItem> mNightTimeAdapter;
	private Spinner mFrequencySpinner;
	private Spinner mNightSettingSpinner;
	private Switch mNightSwitch;
	private Switch mAudioSwitch;
	private int mFrequencyPref;
	private int mNightSettingPref;
	private boolean mNightPref;
	private boolean mAudioPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patient_settings);
		populateSpinnerItems();
		getCurrentPrefs();
		
		// Setup frequency spinner
		mFrequencyAdapter = 
				new ArrayAdapter<FrequencyItem>(this,android.R.layout.simple_list_item_1,mFrequencyItems);
		mFrequencySpinner = (Spinner) findViewById(R.id.patient_settings_frequency_spinner);
		mFrequencySpinner.setAdapter(mFrequencyAdapter);
		for (int i=0; i<mFrequencyAdapter.getCount(); i++) {
			if (((FrequencyItem)mFrequencyAdapter.getItem(i)).value == mFrequencyPref) {
				mFrequencySpinner.setSelection(i);
			}
		}

		// Setup night time spinner
		mNightTimeAdapter = 
				new ArrayAdapter<NightTimeItem>(this,android.R.layout.simple_list_item_1,mNightTimeItems);
		mNightSettingSpinner = (Spinner) findViewById(R.id.patient_settings_night_spinner);
		mNightSettingSpinner.setAdapter(mNightTimeAdapter);
		for (int i=0; i<mNightTimeAdapter.getCount(); i++) {
			if (((NightTimeItem)mNightTimeAdapter.getItem(i)).value == mNightSettingPref) {
				mNightSettingSpinner.setSelection(i);
			}
		}

		mNightSwitch = (Switch) findViewById(R.id.patient_settings_night_switch);
		mNightSwitch.setChecked(mNightPref);

		mAudioSwitch = (Switch) findViewById(R.id.patient_settings_audio_switch);
		mAudioSwitch.setChecked(mAudioPref);
		
		// Set listeners after initialization of views
		setOnChangeListeners();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.patient_settings, menu);
		return true;
	}
	
	private void setOnChangeListeners() {
		// Set listeners after initialization of components
		mFrequencySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long id) {
				FrequencyItem item = (FrequencyItem) mFrequencyAdapter.getItem(pos);
				Log.d(TAG, "Frequency Chosen: " + item.toString());
				processPreferencesChangeEvent();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
		
		mNightSettingSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long id) {
				NightTimeItem item = (NightTimeItem) mNightTimeAdapter.getItem(pos);
				Log.d(TAG, "Night time option: " + item.toString());
				processPreferencesChangeEvent();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
		
		mNightSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {		
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Log.d(TAG, "Night time switch: " + isChecked);
				processPreferencesChangeEvent();
			}
		});
		
		mAudioSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Log.d(TAG, "Audio switch: " + isChecked);
				processPreferencesChangeEvent();
			}
		});
	}
	
	private void getCurrentPrefs() {
		SharedPreferences prefs = getSharedPreferences(PatientSettingsActivity.PATIENT_PREFERENCES, Activity.MODE_PRIVATE);
		mFrequencyPref = prefs.getInt(PatientSettingsActivity.REMINDER_FREQUENCY_SETTING, -1);
		mNightSettingPref = prefs.getInt(PatientSettingsActivity.NIGHT_TIME_INTERVAL_SETTING, -1);
		mNightPref = prefs.getBoolean(PatientSettingsActivity.NIGHT_ENABLE_SETTING, false);
		mAudioPref = prefs.getBoolean(PatientSettingsActivity.AUDIO_ENABLE_SETTING, false);
		Log.i(TAG, "Current preferences: " + mFrequencyPref + ":" + mNightSettingPref + ":"
				+ mNightPref + ":" + mAudioPref);
	}
	
	private void processPreferencesChangeEvent() {
		FrequencyItem freq = (FrequencyItem) mFrequencySpinner.getSelectedItem();
		NightTimeItem nightTime = (NightTimeItem) mNightSettingSpinner.getSelectedItem();
		
		boolean nightSelected = mNightSwitch.isChecked();
		boolean audioSelected = mAudioSwitch.isChecked();
		Log.i(TAG, "Preferences change event: " + freq.getValue() + ":" + nightTime.getValue() + ":"
				+ nightSelected + ":" + audioSelected);
		saveNewPreferences(freq.getValue(), nightTime.getValue(), nightSelected, audioSelected);
		setNewAlarmSeries(freq.getValue(), nightTime.getValue(), nightSelected, audioSelected);
		List<String> alarms = getNewAlarmSchedule(freq.getValue(), nightTime.getValue(), nightSelected, audioSelected);
		displayAlarmSchedule(alarms);
	}
	
	private void saveNewPreferences(
			int frequency,
			int nightSetting,
			boolean nightSelected,
			boolean audioSelected) {
		SharedPreferences prefs = getSharedPreferences(PATIENT_PREFERENCES, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(REMINDER_FREQUENCY_SETTING, frequency);
		editor.putInt(NIGHT_TIME_INTERVAL_SETTING, nightSetting);
		editor.putBoolean(NIGHT_ENABLE_SETTING, nightSelected);
		editor.putBoolean(AUDIO_ENABLE_SETTING, audioSelected);
		editor.apply();
	}
	
	private void setNewAlarmSeries(
			int frequency,
			int nightSetting,
			boolean nightSelected,
			boolean audioSelected) {
		CheckInAlarmHelper.setNewAlarmSeries(this, frequency, nightSetting, nightSelected);
	}
	
	private List<String> getNewAlarmSchedule(
			int frequency,
			int nightSetting,
			boolean nightSelected,
			boolean audioSelected) {
		// Semi-cheating, but it's late and not ready for math...
		String[] alarmSchedule = null;
		if (!nightSelected && frequency == 6) {
			alarmSchedule = new String[]{"00:00", "06:00", "12:00", "18:00"};
		} else if (!nightSelected && frequency == 3) {
			alarmSchedule = new String[]{"00:00", "03:00", "06:00", "09:00", "12:00", "15:00", "18:00", "21:00"};
		} else if (!nightSelected && frequency == 2) {
			alarmSchedule = new String[]{"00:00", "02:00", "04:00","06:00", "08:00", "10:00","12:00", "14:00", "16:00","18:00", "20:00","22:00"};
		} else if (nightSelected && frequency == 6 && nightSetting == 22) {
			alarmSchedule = new String[]{"04:00", "10:00", "16:00", "22:00"};
		} else if (nightSelected && frequency == 6 && nightSetting == 23) {
			alarmSchedule = new String[]{"05:00", "11:00", "17:00", "23:00"};
		} else if (nightSelected && frequency == 6 && nightSetting == 0) {
			alarmSchedule = new String[]{"06:00", "12:00", "18:00", "24:00"};
		} else if (nightSelected && frequency == 6 && nightSetting == 1) {
			alarmSchedule = new String[]{"07:00", "13:00", "19:00", "01:00"};
		} else if (nightSelected && frequency == 3 && nightSetting == 22) {
			alarmSchedule = new String[]{"04:00", "07:00", "10:00", "13:00", "16:00", "19:00", "22:00"};
		} else if (nightSelected && frequency == 3 && nightSetting == 23) {
			alarmSchedule = new String[]{"05:00", "08:00", "11:00", "14:00", "17:00", "20:00", "23:00"};
		} else if (nightSelected && frequency == 3 && nightSetting == 0) {
			alarmSchedule = new String[]{"06:00", "09:00", "12:00", "15:00", "18:00", "21:00", "24:00"};
		} else if (nightSelected && frequency == 3 && nightSetting == 1) {
			alarmSchedule = new String[]{"07:00", "10:00", "13:00", "16:00", "19:00", "22:00", "01:00"};
		} else if (nightSelected && frequency == 2 && nightSetting == 22) {
			alarmSchedule = new String[]{"04:00", "06:00", "08:00", "10:00", "12:00", "14:00", "16:00", "18:00", "20:00", "22:00"};
		} else if (nightSelected && frequency == 2 && nightSetting == 23) {
			alarmSchedule = new String[]{"05:00", "07:00", "09:00", "11:00", "13:00", "15:00", "17:00", "19:00", "21:00", "23:00"};
		} else if (nightSelected && frequency == 2 && nightSetting == 0) {
			alarmSchedule = new String[]{"06:00", "08:00", "10:00", "12:00", "14:00", "16:00", "18:00", "20:00", "22:00", "24:00"};
		} else if (nightSelected && frequency == 2 && nightSetting == 1) {
			alarmSchedule = new String[]{"07:00", "09:00", "11:00", "13:00", "15:00", "17:00", "19:00", "21:00", "23:00", "01:00"};
		}
		return Arrays.asList(alarmSchedule);
	}
	
	private void displayAlarmSchedule(List<String> alarms) {
		String line1 = "", line2 = "", line3 = "";
		for (int i=0; i<alarms.size(); i++) {
			if (i<5) {
				line1 = line1 + alarms.get(i) + "\t\t";
			} else if (i<10) {
				line2 = line2 + alarms.get(i) + "\t\t";
			} else {
				line3 = line3 + alarms.get(i) + "\t\t";
			}
		}
		TextView tv = (TextView) findViewById(R.id.patient_settings_schedule_text_1);
		tv.setText(line1);
		tv = (TextView) findViewById(R.id.patient_settings_schedule_text_2);
		tv.setText(line2);
		tv = (TextView) findViewById(R.id.patient_settings_schedule_text_3);
		tv.setText(line3);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		} else if (id == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void populateSpinnerItems() {
		mFrequencyItems.add(new FrequencyItem("Reminder every 6 hours", CommonDefs.CHECK_IN_REMINDER_6_HOURS));
		mFrequencyItems.add(new FrequencyItem("Reminder every 3 hours", CommonDefs.CHECK_IN_REMINDER_3_HOURS));
		mFrequencyItems.add(new FrequencyItem("Reminder every 2 hours", CommonDefs.CHECK_IN_REMINDER_2_HOURS));
		mNightTimeItems.add(new NightTimeItem("No reminders 22:00-04:00", CommonDefs.CHECK_IN_NIGHT_START_TIME_22));
		mNightTimeItems.add(new NightTimeItem("No reminders 23:00-05:00", CommonDefs.CHECK_IN_NIGHT_START_TIME_23));
		mNightTimeItems.add(new NightTimeItem("No reminders 00:00-06:00", CommonDefs.CHECK_IN_NIGHT_START_TIME_24));
		mNightTimeItems.add(new NightTimeItem("No reminders 01:00-07:00", CommonDefs.CHECK_IN_NIGHT_START_TIME_01));
	}
	
	private class FrequencyItem {
		private String displayText;
		private int value;
		
		public FrequencyItem(String displayText, int value) {
			this.displayText = displayText;
			this.value = value;
		}
		
		public String getDisplayText() {
			return displayText;
		}
		public void setDisplayText(String displayText) {
			this.displayText = displayText;
		}
		public int getValue() {
			return value;
		}
		public void setValue(int value) {
			this.value = value;
		}
		
		@Override
		public String toString() {
			return displayText;
		}
	}
	
	private class NightTimeItem {
		private String displayText;
		private int value;
		
		public NightTimeItem(String displayText, int value) {
			this.displayText = displayText;
			this.value = value;
		}
		
		public String getDisplayText() {
			return displayText;
		}
		public void setDisplayText(String displayText) {
			this.displayText = displayText;
		}
		public int getValue() {
			return value;
		}
		public void setValue(int value) {
			this.value = value;
		}
		
		@Override
		public String toString() {
			return displayText;
		}
	}
}
