package org.tndata.android.compass.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.tndata.android.compass.R;
import org.tndata.android.compass.util.CompassUtil;


/**
 * The fragment to be displayed in the check in when the user has no goals.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class CheckInReviewEmptyFragment extends Fragment{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_check_in_review_empty, container, false);
    }

    @Override
    public void onViewCreated(View rootView, @Nullable Bundle savedInstanceState){
        RelativeLayout header = (RelativeLayout)rootView.findViewById(R.id.check_in_review_header);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)header.getLayoutParams();
        params.height = CompassUtil.getScreenWidth(getActivity())*2/3;
        header.setLayoutParams(params);
    }
}
