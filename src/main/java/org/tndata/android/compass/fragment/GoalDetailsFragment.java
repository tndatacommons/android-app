package org.tndata.android.compass.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
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
        ActionCellView.ActionViewListener,
        BehaviorListView.BehaviorListViewListener {

    private Category mCategory;
    private Goal mGoal;
    private LinearLayout mBehaviorActionsContainer;
    private ProgressBar mProgressBar;
    private GoalDetailsFragmentListener mCallback;
    private ImageView mChooseMore;
    private Map<Behavior, ArrayList<Action>> mBehaviorActionMap = new HashMap<Behavior,
            ArrayList<Action>>();
    private Boolean reloadData = true;

    private static final String TAG = "GoalDetailsFragment";

    public interface GoalDetailsFragmentListener {
        public void chooseBehaviors(Goal goal);

        public void deleteGoal(Goal goal);

        public void learnMoreGoal(Goal goal);

        public void learnMoreBehavior(Behavior behavior);

        public void learnMoreAction(Action action);

        public void deleteBehavior(Behavior behavior);

        public void actionChanged();

        public void fireBehaviorPicker(Behavior behavior);

        public void fireActionPicker(Action action);
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
        mChooseMore = (ImageView) v.findViewById(R.id.goal_choose_more_imageview);
        mChooseMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup();
            }
        });

        TextView titleTextView = (TextView) v.findViewById(R.id.goal_title_textview);
        titleTextView.setText(mGoal.getTitle());

        TextView goalManagementLabel = (TextView) v.findViewById(R.id.goal_management_label);
        goalManagementLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.learnMoreGoal(mGoal);
            }
        });

        RelativeLayout goalContentContainer = (RelativeLayout) v
                .findViewById(R.id.goal_content_container);

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
        if (reloadData) {
            loadBehaviors();
            reloadData = false;
        } else {
            drawBehaviorsAndActions();
        }
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
        for (Behavior behavior : behaviors) {
            mBehaviorActionMap.put(behavior, new ArrayList<Action>());
        }
        loadActions();
    }

    @Override
    public void actionsLoaded(ArrayList<Action> actions) {

        for (Behavior behavior : mBehaviorActionMap.keySet()) {
            for (Action action : actions) {
                if (action.getBehavior_id() == behavior.getId() &&
                        !mBehaviorActionMap.get(behavior).contains(action)) {

                    mBehaviorActionMap.get(behavior).add(action);
                }
            }
        }
        drawBehaviorsAndActions();
    }

    private void drawBehaviorsAndActions() {
        mBehaviorActionsContainer.removeAllViews();

        for (Map.Entry<Behavior, ArrayList<Action>> entry : mBehaviorActionMap.entrySet()) {
            Behavior behavior = entry.getKey();
            ArrayList<Action> actions = entry.getValue();

            BehaviorListView behaviorListView = new BehaviorListView(getActivity());
            behaviorListView.setListener(this);
            behaviorListView.setBehavior(behavior, mCategory);
            behaviorListView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback.learnMoreBehavior(((BehaviorListView) view).getBehavior());
                }
            });
            mBehaviorActionsContainer.addView(behaviorListView);

            for (Action action : actions) {
                ActionCellView acv = new ActionCellView(getActivity());
                acv.setAction(action, mCategory);
                acv.setListener(this);
                acv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCallback.learnMoreAction(((ActionCellView) view).getAction());
                    }
                });
                mBehaviorActionsContainer.addView(acv);
            }
        }
    }

    private void showPopup() {
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(getActivity(), mChooseMore);
        popup.getMenuInflater().inflate(R.menu.menu_goal_details, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_popup_add_behavior:
                        mCallback.chooseBehaviors(mGoal);
                        break;
                    case R.id.menu_popup_remove_goal:
                        mCallback.deleteGoal(mGoal);
                }
                return true;
            }
        });
        popup.show();
    }

    @Override
    public void deleteUserBehavior(Behavior behavior) {
        mCallback.deleteBehavior(behavior);
        mBehaviorActionMap.remove(behavior);
        drawBehaviorsAndActions();
    }

    @Override
    public void actionChanged(Action action) {
        mCallback.actionChanged();
    }

    @Override
    public void fireActionPicker(Action action) {
        mCallback.fireActionPicker(action);
    }
}
