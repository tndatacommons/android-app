package org.tndata.android.compass.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import org.tndata.android.compass.R;


/**
 * Fragment to hold notification preferences. This fragment does not need to call events on
 * external objects because notification settings are self contained.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 * @see org.tndata.android.compass.ui.QuietHoursPreference
 */
public class NotificationSettingsFragment extends PreferenceFragment{
    public static final String SOUND_KEY = "settings_notifications_sound";
    public static final String VIBRATION_KEY = "settings_notifications_vibration";
    public static final String LIGHT_KEY = "settings_notifications_light";
    public static final String QUIET_HOURS_KEY = "settings_notifications_quiet_hours";

    
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.notification_preferences);
    }
}
