package org.tndata.android.compass.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.tndata.android.compass.R;


/**
 * A generic indeterminate progress fragment with the possibility of adding a custom message.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class ProgressFragment extends Fragment{
    public static final String MESSAGE_KEY = "org.tndata.compass.ProgressFragment.message";

    private String mMessage;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        if (getArguments() != null){
            mMessage = getArguments().getString(MESSAGE_KEY, "");
        }
        else{
            mMessage = "";
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View root = inflater.inflate(R.layout.fragment_progress, container, false);

        if (!mMessage.isEmpty()){
            TextView message = (TextView)root.findViewById(R.id.progress_message);
            message.setText(mMessage);
            message.setVisibility(View.VISIBLE);
        }

        return root;
    }
}
