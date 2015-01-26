package org.coursera.capstone.syman.util;

public interface CommonDefs {
	// Intent extras
	public final String INTENT_USERID_EXTRA = "INTENT_USERID_EXTRA";
	public final String INTENT_PATIENT_SERIALIZED_EXTRA = "INTENT_PATIENT_SERIALIZED_EXTRA";
	public final String INTENT_PATIENT_COMPACT_SERIALIZED_EXTRA = "INTENT_PATIENT_COMPACT_SERIALIZED_EXTRA";
	public final String INTENT_PATIENT_FULL_NAME_EXTRA = "INTENT_PATIENT_FULL_NAME_EXTRA";
	public final String INTENT_DOCTOR_SERIALIZED_EXTRA = "INTENT_DOCTOR_SERIALIZED_EXTRA";
	public final String INTENT_PRESCRIPTIONS_SERIALIZED_EXTRA = "INTENT_PRESCRIPTIONS_SERIALIZED_EXTRA";
	//
	// Check-In constants
	public final String CHECK_IN_PROBLEM_EATING_NO = "PROBLEM_EATING_NO";
	public final String CHECK_IN_PROBLEM_EATING_SOME = "PROBLEM_EATING_SOME";
	public final String CHECK_IN_PROBLEM_EATING_YES = "PROBLEM_EATING_YES";
	public final String CHECK_IN_PAIN_CONTROLLED = "PAIN_CONTROLLED";
	public final String CHECK_IN_PAIN_MODERATE = "PAIN_MODERATE";
	public final String CHECK_IN_PAIN_SEVERE = "PAIN_SEVERE";
	//
	// Check-In settings constants
	public final int CHECK_IN_REMINDER_6_HOURS = 6;
	public final int CHECK_IN_REMINDER_3_HOURS = 3;
	public final int CHECK_IN_REMINDER_2_HOURS = 2;
	public final int CHECK_IN_NIGHT_LEN_HOURS = 6;
	public final int CHECK_IN_NIGHT_START_TIME_22 = 22;
	public final int CHECK_IN_NIGHT_START_TIME_23 = 23;
	public final int CHECK_IN_NIGHT_START_TIME_24 = 0;
	public final int CHECK_IN_NIGHT_START_TIME_01 = 1;
	public final long MILLISECS_IN_HOUR = 3600000l;
	//
	// Intent Request codes
	public final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 666;
}
