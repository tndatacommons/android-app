package org.tndata.android.grow.fragment;

import org.tndata.android.grow.GrowApplication;
import org.tndata.android.grow.R;

import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {
    private OnSettingsClickListener mCallback;

    public interface OnSettingsClickListener {
        public void logOut();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnSettingsClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnSettingsClickListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        Preference logOut = findPreference("pref_key_logout");
        logOut.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                mCallback.logOut();
                return false;
            }
        });

        String displayName = "";
        try {
            displayName = ((GrowApplication) getActivity().getApplication())
                    .getUser().getFullName();
        } catch (Exception e) {
            logOut.setSummary("");
        }
        logOut.setSummary(getActivity().getResources().getString(
                R.string.settings_logout_summary, displayName));

    }
}
