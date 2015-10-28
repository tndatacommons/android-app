package org.tndata.android.compass.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import org.tndata.android.compass.R;


/**
 * Created by isma on 10/28/15.
 */
public class NotificationSettingsFragment extends PreferenceFragment{
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.notification_preferences);
    }
}
