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
    //The listener interface
    private OnSettingsClickListener mListener;

    //A list of clickable preferences
    private Preference mNotifications;
    private Preference mLogOut;
    private Preference mSources;


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
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        mNotifications = findPreference("pref_key_notifications");
        mNotifications.setOnPreferenceClickListener(this);

        mLogOut = findPreference("pref_key_logout");
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

        mSources = findPreference("pref_key_sources");
        mSources.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference){
        if (preference == mLogOut){
            mListener.logOut();
        }
        else if (preference == mSources){
            mListener.sources();
        }
        else if (preference == mNotifications){
            mListener.notifications();
        }
        else{
            return false;
        }
        return true;
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
