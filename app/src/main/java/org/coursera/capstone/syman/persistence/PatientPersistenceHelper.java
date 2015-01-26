package org.coursera.capstone.syman.persistence;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.coursera.capstone.syman.model.Checkin;
import org.coursera.capstone.syman.model.Patient;

import android.content.Context;
import android.util.Log;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

//-----------------------------------------------------------
// Helper class for persisting data for Patient part of app.
// Originally planned to use SQLLite, but to make sure
// to finish before deadline, a simple file was used.
// This is single point of entry for managing persistent
// patient data, so it should be easy to change 
// implementation to SQLLite.
//-----------------------------------------------------------
public class PatientPersistenceHelper {
	private final static String TAG = "DEBUG_JAN";
	private final static int READ_BLOCK_SIZE = 200;
	private static final String PATIENT_CACHE_FILE = "patientCache.json";
	private static final String CHECK_IN_CACHE_FILE = "checkInCache.json";
	private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

	public static synchronized Patient getPatient(Context context) {
		try {
			Context appContext = context.getApplicationContext();
			
			FileInputStream is = appContext.openFileInput(PATIENT_CACHE_FILE);
			
			InputStreamReader isr = new InputStreamReader(is);
			char[] inputBuffer = new char[READ_BLOCK_SIZE];
			String jsonString = "";
			int charRead;
			while ((charRead = isr.read(inputBuffer))>0) {
				//---convert the chars to a String---
				String readString = String.copyValueOf(inputBuffer, 0, charRead);
				jsonString += readString;
				inputBuffer = new char[READ_BLOCK_SIZE];
			}
			Log.d(TAG, "Retrieved Patient: " + jsonString);
			Patient patient = gson.fromJson(jsonString, Patient.class);
			return patient;
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException ioEx) {
			return null;
		}
	}
	
	public static synchronized void storeOrUpdatePatient(Context context, Patient patient) {
		Context appContext = context.getApplicationContext();
		// Delete old file if exist.
		appContext.deleteFile(PATIENT_CACHE_FILE);
		try {
			String json = gson.toJson(patient);
			FileOutputStream os = context.openFileOutput(PATIENT_CACHE_FILE, Context.MODE_PRIVATE);
			OutputStreamWriter osw = new OutputStreamWriter(os);
			osw.write(json);
			osw.flush();
			osw.close();

			Log.d(TAG, "Stored JSON: " + json);
		} catch (FileNotFoundException e) {
			// Nothing to do here. 
		} catch (IOException ioEx) {
			// Nothing to do here. 
		}
	}
	
	
	public static synchronized List<Checkin> getUnsentCheckIns(Context context) {
		try {
			Context appContext = context.getApplicationContext();
			
			FileInputStream is = appContext.openFileInput(CHECK_IN_CACHE_FILE);
			
			InputStreamReader isr = new InputStreamReader(is);
			char[] inputBuffer = new char[READ_BLOCK_SIZE];
			String jsonString = "";
			int charRead;
			while ((charRead = isr.read(inputBuffer))>0) {
				//---convert the chars to a String---
				String readString = String.copyValueOf(inputBuffer, 0, charRead);
				jsonString += readString;
				inputBuffer = new char[READ_BLOCK_SIZE];
			}
			Log.d(TAG, "Unsendt Check-Ins: " + jsonString);
			Type listType = new TypeToken<List<Checkin>>() {}.getType();
			List<Checkin> checkins = gson.fromJson(jsonString, listType);
			return checkins;
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException ioEx) {
			return null;
		}
	}
	
	
	public static synchronized void storeCheckInForLaterSubmission(Context context, Checkin checkin) {
		List<Checkin> checkins = getUnsentCheckIns(context);
		if(checkins == null) {
			checkins = new ArrayList<Checkin>();
		}
		checkins.add(checkin);
		storeOrUpdateCheckIns(context, checkins);
	}
	
	
	public static synchronized void storeOrUpdateCheckIns(Context context, List<Checkin> checkins) {
		Context appContext = context.getApplicationContext();
		// Delete old file if exist.
		appContext.deleteFile(CHECK_IN_CACHE_FILE);
		try {
			String json = gson.toJson(checkins);
			FileOutputStream os = context.openFileOutput(CHECK_IN_CACHE_FILE, Context.MODE_PRIVATE);
			OutputStreamWriter osw = new OutputStreamWriter(os);
			osw.write(json);
			osw.flush();
			osw.close();

			Log.d(TAG, "Stored JSON: " + json);
		} catch (FileNotFoundException e) {
			// Nothing to do here. 
		} catch (IOException ioEx) {
			// Nothing to do here. 
		}
	}
}
