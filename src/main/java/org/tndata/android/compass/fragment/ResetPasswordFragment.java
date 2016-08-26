package org.tndata.android.compass.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.tndata.android.compass.R;


/**
 * A fragment for the reset password screen.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class ResetPasswordFragment extends Fragment{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){

        return inflater.inflate(R.layout.fragment_reset_password, container, false);
    }
}
