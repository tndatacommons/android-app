package org.tndata.android.compass.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.model.User;


/**
 * Fragment to hold notification preferences. This fragment does not need to call events on
 * external objects because notification settings are self contained.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 * @see org.tndata.android.compass.ui.QuietHoursPreference
 */
public class NotificationSettingsFragment
        extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener{

    public static final String SOUND_KEY = "settings_notifications_sound";
    public static final String VIBRATION_KEY = "settings_notifications_vibration";
    public static final String LIGHT_KEY = "settings_notifications_light";
    public static final String NUMBER_KEY = "settings_notifications_daily_number";
    public static final String QUIET_HOURS_KEY = "settings_notifications_quiet_hours";


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.notification_preferences);

        User user = ((CompassApplication)getActivity().getApplicationContext()).getUser();
        Preference notificationNumber = findPreference(NUMBER_KEY);
        notificationNumber.setSummary(user.getDailyNotifications()+"");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key){
        if (key.equals(NUMBER_KEY)){
            findPreference(NUMBER_KEY).setSummary(sharedPreferences.getInt(key, 5)+"");
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        preferences.unregisterOnSharedPreferenceChangeListener(this);
    }
}
