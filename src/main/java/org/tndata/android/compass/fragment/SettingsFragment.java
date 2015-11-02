package org.tndata.android.compass.fragment;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;

import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;


/**
 * Fragment that contains the UI for settings. Triggers settings events.
 */
public class SettingsFragment extends PreferenceFragment implements OnPreferenceClickListener{
    private static final String NOTIFICATIONS_KEY = "settings_notifications";
    private static final String LOGOUT_KEY = "settings_logout";
    private static final String SOURCES_KEY = "settings_sources";


    //The listener interface
    private OnSettingsClickListener mListener;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        Preference mLogOut = findPreference(LOGOUT_KEY);
        mLogOut.setOnPreferenceClickListener(this);
        String displayName = "";
        try{
            displayName = ((CompassApplication)getActivity().getApplication()).getUser().getFullName();
        }
        catch (Exception x){
            mLogOut.setSummary("");
        }
        mLogOut.setSummary(getActivity().getResources().getString(
                R.string.settings_logout_summary, displayName));


        findPreference(NOTIFICATIONS_KEY).setOnPreferenceClickListener(this);
        findPreference(SOURCES_KEY).setOnPreferenceClickListener(this);
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        //This makes sure that the container activity has implemented the callback
        //  interface. If not, it throws an exception.
        try{
            mListener = (OnSettingsClickListener)activity;
        }
        catch (ClassCastException ccx){
            throw new ClassCastException(activity.toString()
                    + " must implement OnSettingsClickListener");
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference){
        switch (preference.getKey()){
            case NOTIFICATIONS_KEY:
                mListener.notifications();
                return true;

            case LOGOUT_KEY:
                mListener.logOut();
                return true;

            case SOURCES_KEY:
                mListener.sources();
                return true;
        }
        return false;
    }


    /**
     * Listener interface to handle preference click events int the host activity.
     */
    public interface OnSettingsClickListener{
        /**
         * Called when the notifications preference is clicked.
         */
        void notifications();

        /**
         * Called when the log out preference is clicked.
         */
        void logOut();

        /**
         * Called when the sources preference is clicked.
         */
        void sources();
    }
}
