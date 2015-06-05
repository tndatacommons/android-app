package org.tndata.android.compass.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.task.GetUserActionsTask;
import org.tndata.android.compass.task.GetUserBehaviorsTask;
import org.tndata.android.compass.ui.ActionCellView;
import org.tndata.android.compass.ui.BehaviorListView;
import org.tndata.android.compass.util.ImageCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GoalDetailsFragment extends Fragment implements
        GetUserActionsTask.GetUserActionsListener,
        GetUserBehaviorsTask.GetUserBehaviorsListener,
        ActionCellView.ActionViewListener {

    private Category mCategory;
    private Goal mGoal;
    private LinearLayout mBehaviorActionsContainer;
    private ProgressBar mProgressBar;
    private GoalDetailsFragmentListener mCallback;
    private Map<Behavior, ArrayList<Action>> mBehaviorActionMap = new HashMap<Behavior, ArrayList<Action>>();

    private static final String TAG = "GoalDetailsFragment";

    public interface GoalDetailsFragmentListener {
        public void learnMoreBehavior(Behavior behavior);
        public void learnMoreAction(Action action);
        public void deleteBehavior(Behavior behavior);
        public void actionChanged();
        public void fireBehaviorPicker(Behavior behavior);
    }

    public static GoalDetailsFragment newInstance(Category category, Goal goal) {
        GoalDetailsFragment fragment = new GoalDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("category", category);
        args.putSerializable("goal", goal);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCategory = getArguments() != null ? ((Category) getArguments().get(
                "category")) : new Category();
        mGoal = getArguments() != null ? ((Goal) getArguments().get(
                "goal")) : new Goal();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_goal_details, container, false);
        TextView titleTextView = (TextView) v.findViewById(R.id.goal_title_textview);
        titleTextView.setText(mGoal.getTitle());

        RelativeLayout goalContentContainer = (RelativeLayout) v
                .findViewById(R.id.goal_content_container);
//        goalContentContainer.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                mCallback.learnMoreBehavior();
//            }
//        });

        if (mGoal.getIconUrl() != null && !mGoal.getIconUrl().isEmpty()) {
            ImageView iconImageView = (ImageView) v.findViewById(R.id.goal_icon_imageview);
            ImageCache.instance(getActivity().getApplicationContext()).loadBitmap(iconImageView,
                    mGoal.getIconUrl(), false);

        }
        mProgressBar = (ProgressBar) v.findViewById(R.id.goal_progressbar);
        mBehaviorActionsContainer = (LinearLayout) v.findViewById(R.id.behavior_actions_container);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadBehaviors();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity); // This makes sure that the container activity
        // has implemented the callback interface. If not, it throws an
        // exception
        try {
            mCallback = (GoalDetailsFragmentListener) activity;
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

    private void loadBehaviors() {
        new GetUserBehaviorsTask(this).executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR,
                ((CompassApplication) getActivity().getApplication()).getToken(),
                String.valueOf(mGoal.getId())
        );
    }

    private void loadActions() {
        new GetUserActionsTask(this).executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR,
                ((CompassApplication) getActivity().getApplication()).getToken(),
                String.valueOf(mGoal.getId()));
    }

    @Override
    public void behaviorsLoaded(ArrayList<Behavior> behaviors) {
        for(Behavior behavior : behaviors) {
            mBehaviorActionMap.put(behavior, new ArrayList<Action>());
        }
        loadActions();
    }

    @Override
    public void actionsLoaded(ArrayList<Action> actions) {

        for(Behavior behavior : mBehaviorActionMap.keySet()) {
            for (Action action : actions) {
                if(action.getBehavior_id() == behavior.getId()) {
                    mBehaviorActionMap.get(behavior).add(action);
                }
            }
        }
        drawBehaviorsAndActions();
    }

    private void drawBehaviorsAndActions() {

        for (Map.Entry<Behavior, ArrayList<Action>> entry : mBehaviorActionMap.entrySet()) {
            Behavior behavior = entry.getKey();
            ArrayList<Action> actions = entry.getValue();

            Log.d("Behavior", "*draw name: " + behavior.getTitle());
            BehaviorListView behaviorListView = new BehaviorListView(getActivity());
            behaviorListView.setBehavior(behavior, mCategory);
            mBehaviorActionsContainer.addView(behaviorListView);

            for (Action action : actions) {
                Log.d("Action", "*draw name:" + action.getTitle());
                ActionCellView acv = new ActionCellView(getActivity());
                acv.setAction(action, mCategory);
                acv.setListener(this);
                mBehaviorActionsContainer.addView(acv);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setImageView();
    }

    public void setImageView() {
//        for (Goal goal : ((CompassApplication) getActivity().getApplication()).getGoals()) {
//            if (goal.getBehaviors().contains(mBehavior)) {
//                ImageHelper.setupImageViewButton(getResources(), mAddImageView,
//                        ImageHelper.CHOOSE);
//                mProgressBar.setVisibility(View.GONE);
//                mAddImageView.setEnabled(true);
//                return;
//            }
//        }
//        ImageHelper.setupImageViewButton(getResources(), mAddImageView, ImageHelper.ADD);
        mProgressBar.setVisibility(View.GONE);
    }

    private void showPopup() {
//        //Creating the instance of PopupMenu
//        PopupMenu popup = new PopupMenu(getActivity(), mAddImageView);
//        //Inflating the Popup using xml file
//        popup.getMenuInflater()
//                .inflate(R.menu.menu_popup_chooser, popup.getMenu());
//
//        //registering popup with OnMenuItemClickListener
//        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            public boolean onMenuItemClick(MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.menu_popup_remove_item:
//                            mCallback.deleteBehavior(mBehavior);
//                        break;
//                    case R.id.menu_popup_edit_item:
//                            mCallback.fireBehaviorPicker(mBehavior);
//                        break;
//                }
//                return true;
//            }
//        });
//
//        popup.show(); //showing popup menu
    }

    @Override
    public void actionChanged(Action action) {
        mCallback.actionChanged();
//        ArrayList<Action> actions = ((CompassApplication) getActivity().getApplication()
//        ).getActions();
//        if (actions.contains(action)) {
//            for (Goal goal : ((CompassApplication) getActivity().getApplication()).getGoals()) {
//                if (goal.getBehaviors().contains(mBehavior)) {
//                    return;
//                }
//            }
//            mCallback.addBehavior(mBehavior);
//        }
    }

    @Override
    public void fireActionPicker() {

    }
}
