package org.tndata.android.compass.fragment;

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
import android.widget.Toast;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.task.GoalProgressReportTask;
import org.tndata.android.compass.util.AutoSave;
import org.tndata.android.compass.util.CompassUtil;


/**
 * Created by isma on 10/27/15.
 */
public class CheckInFeedbackFragment
        extends Fragment
        implements
                AutoSave.AutoSaveInterface,
                SeekBar.OnSeekBarChangeListener{

    private static final String GOAL_KEY = "org.tndata.compass.CheckInFeedback.Goal";


    //UI components
    private SeekBar mBar;

    //Model components
    private Goal mGoal;

    //Auto save
    private AutoSave mAutoSave;
    private long mLastUpdate;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //Retrieve the arguments
        Bundle arguments = getArguments();
        mGoal = (Goal)arguments.getSerializable(GOAL_KEY);

        mLastUpdate = -1;
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

    /**
     * Creates an instance of the fragment and delivers the provided data.
     *
     * @param goal the goal to be displayed by the fragment.
     * @return an instance of the fragment.
     */
    public static CheckInFeedbackFragment newInstance(@NonNull Goal goal){
        //Create the argument bundle
        Bundle args = new Bundle();
        args.putSerializable(GOAL_KEY, goal);

        //Create the fragment and deliver the arguments
        CheckInFeedbackFragment fragment = new CheckInFeedbackFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public long getLastUpdateTime(){
        return mLastUpdate;
    }

    @Override
    public void save(){
        new GoalProgressReportTask(((CompassApplication)getActivity().getApplication()).getToken())
                .execute(new GoalProgressReportTask.GoalProgress(mGoal.getId(), mBar.getProgress()));
        mLastUpdate = -1;
        //Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
        //Unused
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
    }
}
