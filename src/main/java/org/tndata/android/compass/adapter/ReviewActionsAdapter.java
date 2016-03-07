package org.tndata.android.compass.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.UserAction;
import org.tndata.android.compass.model.UserBehavior;
import org.tndata.android.compass.model.UserGoal;

import java.util.ArrayList;
import java.util.List;


/**
 * Adapter for the review actions screen.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class ReviewActionsAdapter extends MaterialAdapter{
    private ReviewActionsListener mListener;
    private UserGoal mUserGoal;
    private UserBehavior mUserBehavior;

    private ReviewActionAdapter mAdapter;
    private List<Action> mActions;


    /**
     * Constructor for goal.
     *
     * @param context a reference to the context.
     * @param listener the listener.
     * @param userGoal the user goal whose actions are to be displayed.
     */
    public ReviewActionsAdapter(Context context, ReviewActionsListener listener, UserGoal userGoal){
        super(context, ContentType.LIST, true);
        mListener = listener;
        mUserGoal = userGoal;
        mUserBehavior = null;
        mActions = new ArrayList<>();
    }

    /**
     * Constructor for behavior.
     *
     * @param context a reference to the context.
     * @param listener the listener.
     * @param userBehavior the behavior whose actions are to be displayed.
     */
    public ReviewActionsAdapter(Context context, ReviewActionsListener listener,
                                UserBehavior userBehavior){

        super(context, ContentType.LIST, true);
        mListener = listener;
        mUserGoal = null;
        mUserBehavior = userBehavior;
        mActions = new ArrayList<>();
    }

    @Override
    protected boolean hasHeader(){
        return false;
    }

    @Override
    protected boolean isEmpty(){
        return mActions.isEmpty();
    }

    @Override
    protected void bindListHolder(RecyclerView.ViewHolder rawHolder){
        ListViewHolder holder = (ListViewHolder)rawHolder;
        if (mUserBehavior != null){
            holder.setTitle(getContext().getString(R.string.library_review_action_header,
                    mUserBehavior.getTitle()));
        }
        else{
            holder.setTitle(getContext().getString(R.string.library_review_action_header,
                    mUserGoal.getTitle()));
        }
        holder.setTitleColor(getContext().getResources().getColor(R.color.black));
        if (mAdapter == null){
            mAdapter = new ReviewActionAdapter();
        }
        holder.setAdapter(mAdapter);
    }

    @Override
    protected void loadMore(){
        mListener.loadMore();
    }

    /**
     * Adds a list of actions to the action adapter.
     *
     * @param actions the actions to be added.
     * @param showLoading whether this adapter should still display the loading screen.
     */
    public void addActions(@NonNull List<UserAction> actions, boolean showLoading){
        //If there are no actions, insert the content card
        if (mActions.isEmpty()){
            notifyListInserted();
        }
        //Update the load widget
        updateLoading(showLoading);

        //Add all the actions in the behavior list
        mActions.addAll(actions);
    }


    /**
     * Adapter for the list of actions. Plain and simple.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private class ReviewActionAdapter extends RecyclerView.Adapter<ActionViewHolder>{
        @Override
        public ActionViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View rootView = inflater.inflate(R.layout.item_review_action, parent, false);
            return new ActionViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(ActionViewHolder holder, int position){
            holder.bind(mActions.get(position));
        }

        @Override
        public int getItemCount(){
            return mActions.size();
        }
    }


    /**
     * View holder for action items.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private class ActionViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        private View mSeparator;
        private ImageView mEnabled;
        private TextView mTitle;


        /**
         * Constructor.
         *
         * @param rootView the root view of this holder.
         */
        public ActionViewHolder(@NonNull View rootView){
            super(rootView);

            mSeparator = rootView.findViewById(R.id.review_action_separator);
            mEnabled = (ImageView)rootView.findViewById(R.id.review_action_enabled);
            mTitle = (TextView)rootView.findViewById(R.id.review_action_title);

            rootView.setOnClickListener(this);
        }

        /**
         * Binds an action to the holder.
         *
         * @param userAction the user action to be bound.
         */
        public void bind(@NonNull Action userAction){
            //If this is the first item, do not show the separator
            if (getAdapterPosition() == 0){
                mSeparator.setVisibility(View.GONE);
            }
            else{
                mSeparator.setVisibility(View.VISIBLE);
            }

            //Set the icon and title of the view
            if (userAction.isTriggerEnabled()){
                mEnabled.setImageResource(R.drawable.ic_enabled_black_36dp);
            }
            else{
                mEnabled.setImageResource(R.drawable.ic_disabled_black_36dp);
            }
            mTitle.setText(userAction.getTitle());
        }

        @Override
        public void onClick(View v){
            mListener.onActionSelected(mActions.get(getAdapterPosition()));
        }
    }


    /**
     * Listener interface for the review actions process.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface ReviewActionsListener{
        /**
         * Called when an action is tapped.
         *
         * @param action the action tapped.
         */
        void onActionSelected(Action action);

        /**
         * Called when the bottom of the list is reached if the progress widget is
         * still active.
         */
        void loadMore();
    }
}
