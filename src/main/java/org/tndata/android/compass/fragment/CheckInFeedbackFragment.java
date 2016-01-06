package org.tndata.android.compass.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.AutoSave;
import org.tndata.android.compass.util.CompassUtil;
import org.tndata.android.compass.util.NetworkRequest;


/**
 * Fragment featuring a seek bar to let the user report their progress in a goal.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class CheckInFeedbackFragment
        extends Fragment
        implements
                AutoSave.AutoSaveInterface,
                SeekBar.OnSeekBarChangeListener{

    private static final String INDEX_KEY = "org.tndata.compass.CheckInFeedback.Index";
    private static final String GOAL_KEY = "org.tndata.compass.CheckInFeedback.Goal";


    //UI components
    private SeekBar mBar;
    private TextView mDisplay;

    //Model components
    private Goal mGoal;

    //Auto save
    private AutoSave mAutoSave;
    private long mLastUpdate;

    //Listener
    private int mIndex;
    private CheckInFeedbackListener mListener;


    /**
     * Creates an instance of the fragment and delivers the provided data.
     *
     * @param index the index on this fragment within the containing adapter.
     * @param goal the goal to be displayed by the fragment.
     * @return an instance of the fragment.
     */
    public static CheckInFeedbackFragment newInstance(int index, @NonNull Goal goal){
        //Create the argument bundle
        Bundle args = new Bundle();
        args.putInt(INDEX_KEY, index);
        args.putSerializable(GOAL_KEY, goal);

        //Create the fragment and deliver the arguments
        CheckInFeedbackFragment fragment = new CheckInFeedbackFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //Retrieve the arguments
        Bundle arguments = getArguments();
        mIndex = arguments.getInt(INDEX_KEY);
        mGoal = (Goal)arguments.getSerializable(GOAL_KEY);

        mLastUpdate = -1;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            mListener = (CheckInFeedbackListener)context;
        }
        catch (ClassCastException ccx){
            throw new ClassCastException(context.toString()
                    + " must implement " + CheckInFeedbackListener.class);
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mListener = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_check_in_feedback, container, false);
    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState){
        //Fetch the UI components
        RelativeLayout header = (RelativeLayout)rootView.findViewById(R.id.check_in_feedback_header);
        TextView goalTitle = (TextView)rootView.findViewById(R.id.check_in_feedback_goal);
        mBar = (SeekBar)rootView.findViewById(R.id.check_in_feedback_bar);
        mDisplay = (TextView)rootView.findViewById(R.id.check_in_feedback_display);

        //3 by 2 ratio and color for the header
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)header.getLayoutParams();
        params.height = CompassUtil.getScreenWidth(getActivity())*2/3;
        header.setLayoutParams(params);
        if (mGoal.getPrimaryCategory() != null){
            header.setBackgroundColor(Color.parseColor(mGoal.getPrimaryCategory().getColor()));
        }

        //Header title
        String title = mGoal.getTitle().substring(0, 1).toLowerCase() + mGoal.getTitle().substring(1);
        goalTitle.setText(getResources().getString(R.string.check_in_feedback_goal, title));

        mBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onResume(){
        super.onResume();
        mAutoSave = AutoSave.start(getActivity(), this, 1000);
    }

    @Override
    public void onPause(){
        mAutoSave.stop();
        super.onPause();
    }

    @Override
    public long getLastUpdateTime(){
        return mLastUpdate;
    }

    @Override
    public void save(){
        NetworkRequest.post(getActivity(), null, API.getPostUserGoalProgressUrl(),
                ((CompassApplication)getActivity().getApplication()).getToken(),
                API.getPostUserGoalProgressBody(mGoal, mBar.getProgress()+1));
        mLastUpdate = -1;
        Log.d("Feedback", "Saving");
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
        switch (progress){
            case 0:
                mDisplay.setText(R.string.check_in_feedback_poor);
                break;

            case 1:
                mDisplay.setText(R.string.check_in_feedback_fair);
                break;

            case 2:
                mDisplay.setText(R.string.check_in_feedback_good);
                break;

            case 3:
                mDisplay.setText(R.string.check_in_feedback_very_good);
                break;

            case 4:
                mDisplay.setText(R.string.check_in_feedback_excellent);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar){
        Log.d("CheckInFeedbackFragment", "start tracking");
        mLastUpdate = -1;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar){
        Log.d("CheckInFeedbackFragment", "stop tracking");
        mLastUpdate = System.currentTimeMillis();
        mListener.onProgressChanged(mIndex, mBar.getProgress()+1);
    }


    /**
     * Listener for events triggered by the CheckInFeedbackFragment.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface CheckInFeedbackListener{
        /**
         * Called when the user sets his progress.
         *
         * @param index the index of the fragment.
         * @param progress the progress as reported by the user.
         */
        void onProgressChanged(int index, int progress);
    }
}
