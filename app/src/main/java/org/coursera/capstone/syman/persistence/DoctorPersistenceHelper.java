package org.coursera.capstone.syman.persistence;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.List;

import org.coursera.capstone.syman.model.Checkin;
import org.coursera.capstone.syman.model.Doctor;
import org.coursera.capstone.syman.model.PatientCompact;

import android.content.Context;
import android.util.Log;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

//-----------------------------------------------------------
// Helper class for persisting data for Doctor part of app.
// Originally planned to use SQLLite, but to make sure
// to finish before deadline, a simple file was used.
// This is single point of entry for managing persistent
// doctor data, so it should be easy to change 
// implementation to SQLLite.
//-----------------------------------------------------------
public class DoctorPersistenceHelper {
	private final static String TAG = "DEBUG_JAN";
	private final static int READ_BLOCK_SIZE = 200;
	private static final String DOCTOR_CACHE_FILE = "doctorCache.json";
	private static final String DOCTOR_PATIENTS_CACHE_FILE = "doctorPatientsCache.json";
	private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

	public static synchronized Doctor getDoctor(Context context) {
		try {
			Context appContext = context.getApplicationContext();
			
			FileInputStream is = appContext.openFileInput(DOCTOR_CACHE_FILE);
			
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
			Doctor doctor = gson.fromJson(jsonString, Doctor.class);
			return doctor;
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException ioEx) {
			return null;
		}
	}
	
	public static synchronized void storeOrUpdateDoctor(Context context, Doctor doctor) {
		Context appContext = context.getApplicationContext();
		// Delete old file if exist.
		appContext.deleteFile(DOCTOR_CACHE_FILE);
		try {
			String json = gson.toJson(doctor);
			FileOutputStream os = context.openFileOutput(DOCTOR_CACHE_FILE, Context.MODE_PRIVATE);
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
	
	public static synchronized void storeOrUpdateDoctorsPatientsCompact(Context context, List<PatientCompact> patients) {
		Context appContext = context.getApplicationContext();
		// Delete old file if exist.
		appContext.deleteFile(DOCTOR_PATIENTS_CACHE_FILE);
		try {
			String json = gson.toJson(patients);
			FileOutputStream os = context.openFileOutput(DOCTOR_PATIENTS_CACHE_FILE, Context.MODE_PRIVATE);
			OutputStreamWriter osw = new OutputStreamWriter(os);
			osw.write(json);
			osw.flush();
			osw.close();
		} catch (FileNotFoundException e) {
			// Nothing to do here. 
		} catch (IOException ioEx) {
			// Nothing to do here. 
		}
	}
	
	public static synchronized List<PatientCompact> getUnsentDoctorsPatientsCompact(Context context) {
		try {
			Context appContext = context.getApplicationContext();
			
			FileInputStream is = appContext.openFileInput(DOCTOR_PATIENTS_CACHE_FILE);
			
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
			Type listType = new TypeToken<List<PatientCompact>>() {}.getType();
			List<PatientCompact> patients = gson.fromJson(jsonString, listType);
			return patients;
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException ioEx) {
			return null;
		}
	}
}
