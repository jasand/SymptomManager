package org.coursera.capstone.syman.util;

import org.coursera.capstone.syman.LoginActivity;
import org.coursera.capstone.syman.api.SymptomServiceApi;
import org.coursera.capstone.syman.oauth.EasyHttpClient;
import org.coursera.capstone.syman.oauth.SecuredRestBuilder;

import retrofit.RestAdapter.LogLevel;
import retrofit.client.ApacheClient;
import retrofit.converter.GsonConverter;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SymptomServiceMgr {
	private static SymptomServiceApi symptomServiceApi;
	private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	
	public static synchronized SymptomServiceApi getServiceIfAvailable(Context context) {
		if (symptomServiceApi != null) {
			return symptomServiceApi;
		} else {
			String accessToken = LoggedInFileHandler.getAccessToken(context);
			if (accessToken != null && !accessToken.equals("")) {
				return initWithToken(context, accessToken);
			} else {
				return null;
			}
			
		}
	}
	
	public static synchronized SymptomServiceApi getServiceOrShowLogin(Context context) {
		if (symptomServiceApi != null) {
			return symptomServiceApi;
		} else {
			String accessToken = LoggedInFileHandler.getAccessToken(context);
			if (accessToken != null && !accessToken.equals("")) {
				return initWithToken(context, accessToken);
			} else {
				Intent i = new Intent(context, LoginActivity.class);
				context.startActivity(i);
				return null;
			}
			
		}
	}

	public static synchronized SymptomServiceApi init(Context context, String user, String pass) {
		symptomServiceApi = new SecuredRestBuilder()
				.setLoginEndpoint(SymptomServiceApi.SYMPTOM_SERVICE_BASE_URL + SymptomServiceApi.TOKEN_PATH)
				.setUsername(user)
				.setPassword(pass)
				.setClientId(SymptomServiceApi.CLIENT_ID)
				.setClient(
						new ApacheClient(new EasyHttpClient()))
				.setEndpoint(SymptomServiceApi.SYMPTOM_SERVICE_BASE_URL)
				.setLogLevel(LogLevel.FULL)
				.setContext(context)
				.setConverter(new GsonConverter(gson))
				.build()
				.create(SymptomServiceApi.class);

		return symptomServiceApi;
	}
	
	public static boolean logout(Context context) {
		symptomServiceApi = null;
		return LoggedInFileHandler.removeAccessToken(context);
	}
	
	private static synchronized SymptomServiceApi initWithToken(Context context,
			String accessToken) {
		symptomServiceApi = new SecuredRestBuilder()
				.setLoginEndpoint(SymptomServiceApi.SYMPTOM_SERVICE_BASE_URL 
						+ SymptomServiceApi.TOKEN_PATH)
				.setClientId(SymptomServiceApi.CLIENT_ID)
				.setClient(new ApacheClient(new EasyHttpClient()))
				.setEndpoint(SymptomServiceApi.SYMPTOM_SERVICE_BASE_URL)
				.setLogLevel(LogLevel.FULL)
				.setOauthToken(accessToken)
				.setContext(context)
				.setConverter(new GsonConverter(gson))
				.build()
				.create(SymptomServiceApi.class);

		return symptomServiceApi;
	}
}
