package org.coursera.capstone.syman.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.util.Log;

public class LoggedInFileHandler {
	private final static String TAG = "DEBUG_JAN";
	private final static int READ_BLOCK_SIZE = 200;
	private final static String tokenFileName = "accessToken.json";
	
	public static synchronized String getAccessToken(Context context) {
		try {
			Context appContext = context.getApplicationContext();
			
			FileInputStream is = appContext.openFileInput(tokenFileName);
			
			InputStreamReader isr = new InputStreamReader(is);
			char[] inputBuffer = new char[READ_BLOCK_SIZE];
			String accessToken = "";
			int charRead;
			while ((charRead = isr.read(inputBuffer))>0) {
				//---convert the chars to a String---
				String readString = String.copyValueOf(inputBuffer, 0, charRead);
				accessToken += readString;
				inputBuffer = new char[READ_BLOCK_SIZE];
			}
			Log.i(TAG, "Retrieved Token: " + accessToken);
			return accessToken;
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException ioEx) {
			return null;
		}
	}
	
	public static synchronized void saveAccessToken(Context context, String accessToken) {
		Context appContext = context.getApplicationContext();
		// Delete old file if exist.
		appContext.deleteFile(tokenFileName);
		try {
			FileOutputStream os = context.openFileOutput(tokenFileName, Context.MODE_PRIVATE);
			OutputStreamWriter osw = new OutputStreamWriter(os);
			osw.write(accessToken);
			osw.flush();
			osw.close();

			Log.i(TAG, "Storing JSON: " + accessToken);
		} catch (FileNotFoundException e) {
			// Nothing to do here. 
			// User has to login again nest time a new SymptomService has to be created. 
		} catch (IOException ioEx) {
			// Nothing to do here. 
			// User has to login again nest time a new SymptomService has to be created.
		}
	}
	
	public static synchronized boolean removeAccessToken(Context context) {
		Context appContext = context.getApplicationContext();
		return appContext.deleteFile(tokenFileName);
	}

}
