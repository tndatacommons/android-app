package org.tndata.android.compass.util;

public class Constants {
    public final static int LOGGED_OUT_RESULT_CODE = 2200;
    public final static int SETTINGS_REQUEST_CODE = 2201;
    public final static int CHOOSE_CATEGORIES_REQUEST_CODE = 2202;
    public final static int CHOOSE_GOALS_REQUEST_CODE = 2203;
    public final static int VIEW_BEHAVIOR_REQUEST_CODE = 2204;
    public final static int BEHAVIOR_CHANGED_RESULT_CODE = 2205;
    public final static int CHOOSE_BEHAVIORS_REQUEST_CODE = 2206;

    public final static int QOL_INSTRUMENT_ID = 1;
    public final static int BIO_INSTRUMENT_ID = 4;

    public final static String SURVEY_LIKERT = "likertquestion";
    public final static String SURVEY_MULTICHOICE = "multiplechoicequestion";
    public final static String SURVEY_BINARY = "binaryquestion";
    public final static String SURVEY_OPENENDED = "openendedquestion";
    public final static String SURVEY_OPENENDED_DATE_TYPE = "datetime";

    public final static String GOAL_UPDATED_BROADCAST_ACTION = "org.tndata.android.compass.GOAL_UPDATED_BROADCAST_ACTION";

    public final static String TERMS_AND_CONDITIONS_URL = "http://tndata.org";
    public final static String BASE_URL = "http://app.tndata.org/api/";

    // Behavior, Self-reporting.
    // NOTE: These values correspond to values exposed/expected by the API
    public final static int BEHAVIOR_OFF_COURSE = 1;
    public final static int BEHAVIOR_WANDERING = 2;
    public final static int BEHAVIOR_ON_COURSE = 3;


    // For GCM Notifications
    public final static String ACTION_TYPE = "action";
    public final static String BEHAVIOR_TYPE = "behavior";
    public final static String GCM_DEFAULT_ACTIVITY = "org.tndata.android.compass.activity.LoginActivity";
    public final static String GCM_ACTION_ACTIVITY = "org.tndata.android.compass.activity.LoginActivity"; // TODO: Action's more info
    public final static String GCM_BEHAVIOR_ACTIVITY = "org.tndata.android.compass.activity.BehaviorActivity"; // TODO: Action's more info

    // https://stackoverflow.com/questions/18196292/what-are-consequences-of-having-gcm-sender-id-being-exposed
    public final static String GCM_SENDER_ID = "152170900684";

    public final static boolean ENABLE_SURVEYS = false;
}
