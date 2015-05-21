package org.tndata.android.compass.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.tndata.android.compass.R;

public class CheckProgressFragment extends Fragment {
    private ImageView mCheckProgressInfoImage;
    private ImageView mPrivacyButtonImageView;
    private CheckProgressFragmentListener mCallback;

    public interface CheckProgressFragmentListener {
        public void progressCompleted();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_check_progress, container, false);
        mPrivacyButtonImageView = (ImageView) v
                .findViewById(R.id.checkprogress_privacy_button_imageview);

        mPrivacyButtonImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage(R.string.onboarding_checkprogress_privacy_content)
                        .setTitle(R.string.onboarding_checkprogress_privacy_title)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        mCheckProgressInfoImage = (ImageView) v.findViewById(R.id.checkprogress_info_button);
        mCheckProgressInfoImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.progressCompleted();
            }
        });
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity); // This makes sure that the container activity
        // has implemented the callback interface. If not, it throws an
        // exception
        try {
            mCallback = (CheckProgressFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement CheckProgressFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

}
