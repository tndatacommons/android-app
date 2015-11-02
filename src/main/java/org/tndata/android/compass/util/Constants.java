package org.tndata.android.compass.util;

import org.tndata.android.compass.BuildConfig;


public class Constants{
    public final static int LOGGED_OUT_RESULT_CODE = 2200;
    public final static int SETTINGS_REQUEST_CODE = 2201;
    public final static int CHOOSE_CATEGORIES_REQUEST_CODE = 2202;
    public final static int CHOOSE_GOALS_REQUEST_CODE = 2203;
    public final static int VIEW_BEHAVIOR_REQUEST_CODE = 2204;
    public final static int BEHAVIOR_CHANGED_RESULT_CODE = 2205;
    public final static int CHOOSE_BEHAVIORS_REQUEST_CODE = 2206;
    public final static int GOALS_CHANGED_RESULT_CODE = 2207;
    public final static int ACTIVITY_CHANGED_RESULT_CODE = 2208;

    public final static int QOL_INSTRUMENT_ID = 1;
    public final static int BIO_INSTRUMENT_ID = 4;
    public final static int INITIAL_PROFILE_INSTRUMENT_ID = 6;

    public final static String SURVEY_LIKERT = "likertquestion";
    public final static String SURVEY_MULTICHOICE = "multiplechoicequestion";
    public final static String SURVEY_BINARY = "binaryquestion";
    public final static String SURVEY_OPENENDED = "openendedquestion";
    public final static String SURVEY_OPENENDED_DATE_TYPE = "datetime";

    public final static String GOAL_UPDATED_BROADCAST_ACTION = "org.tndata.android.compass.GOAL_UPDATED_BROADCAST_ACTION";

    public final static String TERMS_AND_CONDITIONS_URL = "https://app.tndata.org/terms/";
    public static final String PRIVACY_URL = "https://app.tndata.org/privacy/";
    public static final String TNDATA_BASE_URL = "https://app.tndata.org/api/";
    public static final String TNDATA_STAGING_URL = "http://staging.tndata.org/api/";
    public static final String NGROK_TUNNEL_URL = "https://tndata.ngrok.io/api/";
    public static final String BASE_URL = NGROK_TUNNEL_URL; //BuildConfig.DEBUG ? TNDATA_STAGING_URL : TNDATA_BASE_URL;

    //Preferences
    public final static String PREFERENCES_NAME = "compass_pref";
    public final static String PREFERENCES_NEW_USER = "new_user_pref";

    // Behavior, Self-reporting.
    // NOTE: These values correspond to values exposed/expected by the API
    public final static int BEHAVIOR_OFF_COURSE = 1;
    public final static int BEHAVIOR_SEEKING = 2;
    public final static int BEHAVIOR_ON_COURSE = 3;


    public final static boolean ENABLE_SURVEYS = false;
}
