package org.tndata.android.compass.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.CheckInReviewAdapter;
import org.tndata.android.compass.model.GoalContent;
import org.tndata.android.compass.model.UserAction;
import org.tndata.android.compass.util.CompassUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Fragment used to display all actions belonging to a goal in the review screen.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class CheckInReviewFragment extends Fragment{
    private static final String GOAL_KEY = "org.tndata.compass.CheckInReview.Goal";
    private static final String ACTION_NUMBER_KEY = "org.tndata.compass.CheckInReview.ActionNumber";
    private static final String USER_ACTION_KEY = "org.tndata.compass.CheckInReview.UserAction";


    //Model components
    private GoalContent mGoal;
    private List<UserAction> mActions;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //Retrieve the arguments
        Bundle arguments = getArguments();
        mGoal = (GoalContent)arguments.getSerializable(GOAL_KEY);
        mActions = new ArrayList<>();
        int actionNumber = arguments.getInt(ACTION_NUMBER_KEY);
        for (int i = 0; i < actionNumber; i++){
            mActions.add((UserAction)arguments.getSerializable(getActionKey(i)));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_check_in_review, container, false);
    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState){
        //Fetch the UI components
        RelativeLayout header = (RelativeLayout)rootView.findViewById(R.id.check_in_review_header);
        TextView goalTitle = (TextView)rootView.findViewById(R.id.check_in_review_goal);
        RecyclerView list = (RecyclerView)rootView.findViewById(R.id.check_in_review_list);

        //3 by 2 ratio and color for the header
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)header.getLayoutParams();
        params.height = CompassUtil.getScreenWidth(getActivity())*2/3;
        header.setLayoutParams(params);
        /*if (mGoal.getPrimaryCategory() != null){
            header.setBackgroundColor(Color.parseColor(mGoal.getPrimaryCategory().getColor()));
        }*/

        //Header title
        String title = mGoal.getTitle().substring(0, 1).toLowerCase()
                + mGoal.getTitle().substring(1);
        goalTitle.setText(getResources().getString(R.string.check_in_review_title, title));

        list.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        list.setAdapter(new CheckInReviewAdapter(getActivity(), mActions));
    }


    /**
     * Creates an instance of the fragment and delivers the provided data.
     *
     * @param goal the goal to be displayed by the fragment.
     * @param actions the actions associated to that goal.
     * @return an instance of the fragment.
     */
    public static CheckInReviewFragment newInstance(@NonNull GoalContent goal, @NonNull List<UserAction> actions){
        //Create the argument bundle
        Bundle args = new Bundle();
        args.putSerializable(GOAL_KEY, goal);
        args.putInt(ACTION_NUMBER_KEY, actions.size());
        for (int i = 0; i < actions.size(); i++){
            args.putSerializable(getActionKey(i), actions.get(i));
        }

        //Create the fragment and deliver the arguments
        CheckInReviewFragment fragment = new CheckInReviewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Generates a key for an action.
     *
     * @param action the action number.
     * @return the key of the action.
     */
    private static String getActionKey(int action){
        return USER_ACTION_KEY + action;
    }
}
