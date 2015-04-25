package org.tndata.android.grow.fragment;

import org.tndata.android.grow.R;
import org.tndata.android.grow.model.Behavior;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class LearnMoreFragment extends Fragment {
    private Behavior mBehavior;
    private LearnMoreFragmentListener mCallback;

    public interface LearnMoreFragmentListener {
        public void addBehavior(Behavior behavior);

        public void deleteBehavior(Behavior behavior);

        public void cancel();
    }

    public static LearnMoreFragment newInstance(Behavior behavior) {
        LearnMoreFragment fragment = new LearnMoreFragment();
        Bundle args = new Bundle();
        args.putSerializable("behavior", behavior);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBehavior = getArguments() != null ? ((Behavior) getArguments().get(
                "behavior")) : new Behavior();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_learn_more, container, false);
        TextView titleTextView = (TextView) v
                .findViewById(R.id.learn_more_behavior_title_textview);
        TextView descriptionTextView = (TextView) v
                .findViewById(R.id.learn_more_description_textview);
        ImageView addImageView = (ImageView) v
                .findViewById(R.id.learn_more_add_imageview);
        addImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mCallback.addBehavior(mBehavior);
            }
        });
        Button noThanksButton = (Button) v
                .findViewById(R.id.learn_more_no_thanks_button);
        noThanksButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mCallback.cancel();
            }
        });

        titleTextView.setText(mBehavior.getTitle());
        descriptionTextView.setText(mBehavior.getDescription());
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity); // This makes sure that the container activity
        // has implemented the callback interface. If not, it throws an
        // exception
        try {
            mCallback = (LearnMoreFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement LearnMoreListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }
}
