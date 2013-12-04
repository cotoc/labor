package com.mayu.android.labor;

import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class LaborAppPreferences extends PreferenceActivity {

	public static final String KEY_EXPECTED_DATE = "expected_date";
	public static final String KEY_LOGIN_ID = "login_id";
	public static final String KEY_USER_ID = "user_id";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_CONTACT_HOSPITAL ="contact_hospital";
	public static final String KEY_CONTACT_TAXI ="contact_taxi";
	public static final String KEY_CALL_FAMILY ="call_family";
	public static final String KEY_MAIL_FAMILY ="mail_family";
	
	
	private DatePickerPreference mExpectedDate;
	private AccountPreference mAccount;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.layout.apppreferences);
		
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String freq = preferences.getString(KEY_EXPECTED_DATE, "0");
		mExpectedDate = (DatePickerPreference) getPreferenceScreen().findPreference(KEY_EXPECTED_DATE);
		if(Long.parseLong(freq) > 0){
			mExpectedDate.setSummary(mExpectedDate.getValue());
		}
		mAccount = (AccountPreference) getPreferenceScreen().findPreference(KEY_USER_ID);
		mAccount.setSummary(mAccount.getValue());
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Intent intent = new Intent();
		setResult(RESULT_OK, intent);
		finish();
		
	}
	
	public static long getExpectedDate(Context context) {
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String freq = preferences.getString(KEY_EXPECTED_DATE, "0");
		
		return Long.parseLong(freq);
	}

	
	public static String getLoginId(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String userIdStr = preferences.getString(KEY_LOGIN_ID, "default");
		return userIdStr;
	}
	
	public static String getPassword(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String userIdStr = preferences.getString(KEY_PASSWORD, "default");
		return userIdStr;
	}
	
	public static String getContactTaxi(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String userIdStr = preferences.getString(KEY_CONTACT_TAXI, "default");
		return userIdStr;
	}
	
	public static String getContactHospital(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String userIdStr = preferences.getString(KEY_CONTACT_HOSPITAL, "default");
		return userIdStr;
	}
	
	public static String getCallFamily(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String userIdStr = preferences.getString(KEY_CALL_FAMILY, "default");
		return userIdStr;
	}
	
	public static String getMailFamily(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String userIdStr = preferences.getString(KEY_MAIL_FAMILY, "default");
		return userIdStr;
	}
	
}
