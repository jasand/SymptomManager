package org.coursera.capstone.syman.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class UserPhotoFileHandler {
	private static final String IMG_CAPTURE_TMP_FILE = "imageCaptureTmp.jpg";

	public static Uri findPhotoForUserId(String userId) {
		if (userId == null) {
			return null;
		}
		File photoDir = getPhotoStorageDir();
		for (File file : photoDir.listFiles()) {
			if(file.getName().startsWith(userId)) {
				return Uri.fromFile(file);
			}
		}
		return null;
	}
	
	public static Uri savePhotoForUserId(
			String userId, 
			String mimeType, 
			InputStream inputStream) {
		
		File photoDir = getPhotoStorageDir();
		// Delete file if one with userid exist.
		for (File file : photoDir.listFiles()) {
			if(file.getName().startsWith(userId)) {
				file.delete();
			}
		}
		String fileSuffix = fileEndingFromMimeType(mimeType);
		String fileName = userId + "." + fileSuffix;
		File photo = new File(photoDir.getPath() + File.separator + fileName);

		try {
			if(photo.createNewFile()) {
				OutputStream outputStream = new FileOutputStream(photo);
				copyInputStreamToOutputStream(outputStream, inputStream);
				return Uri.fromFile(photo);
			}
		} catch (IOException ioEx) {
			Log.d("DEBUG_JAN", "IOException: " + ioEx);
		}
		return null;
	}
	
	public static Uri getTempFileForCameraCapture() {
		File photoDir = getPhotoStorageDir();
		// Delete file if one with userid exist.
		for (File file : photoDir.listFiles()) {
			if(file.getName().equals(IMG_CAPTURE_TMP_FILE)) {
				boolean bool = file.delete();
				if (bool) {
					Log.d("DEBUG_JAN", "DELETED: " + IMG_CAPTURE_TMP_FILE);
				} else {
					Log.d("DEBUG_JAN", "Could not DELETE: " + IMG_CAPTURE_TMP_FILE);
				}
			}
		}
		File photoTmp = new File(photoDir.getPath() + File.separator + IMG_CAPTURE_TMP_FILE);
		return Uri.fromFile(photoTmp);
	}
	
	private static File getPhotoStorageDir() {
		String state = Environment.getExternalStorageState();
		Log.d("DEBUG_JAN", "ExternalStorageState: " + state);
		File extStorage = Environment.getExternalStorageDirectory();
		File photoStorageDir = new File(extStorage,"userphoto");
		if (!photoStorageDir.exists()) {
			if (!photoStorageDir.mkdir()) {
				Log.e("DEBUG_JAN", "failed to create directory");
				throw new RuntimeException("Could not create storage dir for photos.");
			}
		}
		return photoStorageDir;
	}
	
	private static String fileEndingFromMimeType(String mimeType) {
		if (mimeType.equalsIgnoreCase("image/png")) {
			return "png";
		} else if (mimeType.equalsIgnoreCase("image/jpeg")) {
			return "jpg";
		} else if (mimeType.equalsIgnoreCase("image/gif")) {
			return "gif";
		}
		// Default
		return "bmp";
	}
	
	private static void copyInputStreamToOutputStream(OutputStream os, InputStream is) throws IOException {
	    byte[] buffer = new byte[256];
	    int bytesRead = 0;
	    while ((bytesRead = is.read(buffer)) != -1) {
	        os.write(buffer, 0, bytesRead);
	    }
	    is.close();
	    os.flush();
	    os.close();
	}
}
