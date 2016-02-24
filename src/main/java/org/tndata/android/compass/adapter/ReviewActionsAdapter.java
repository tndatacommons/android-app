package org.tndata.android.compass.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.UserBehavior;
import org.tndata.android.compass.model.UserGoal;
import org.tndata.android.compass.ui.ContentContainer;
import org.tndata.android.compass.util.CompassUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by isma on 2/24/16.
 */
public class ReviewActionsAdapter extends RecyclerView.Adapter{
    private static final int TYPE_BLANK = 0;
    private static final int TYPE_CONTENT = TYPE_BLANK+1;
    private static final int TYPE_LOAD = TYPE_CONTENT+1;
    private static final int ITEM_COUNT = TYPE_LOAD+1;


    private Context mContext;
    private ReviewActionsListener mListener;
    private UserGoal mUserGoal;
    private UserBehavior mUserBehavior;

    private ActionsViewHolder mActionsHolder;
    private List<Action> mActions;

    private boolean mShowLoading;
    private String mLoadError;


    public ReviewActionsAdapter(Context context, ReviewActionsListener listener, UserGoal userGoal){
        init(context, listener);
        mUserGoal = userGoal;
        mUserBehavior = null;
    }

    public ReviewActionsAdapter(Context context, ReviewActionsListener listener,
                                UserBehavior userBehavior){

        init(context, listener);
        mUserGoal = null;
        mUserBehavior = userBehavior;
    }

    private void init(Context context, ReviewActionsListener listener){
        mContext = context;
        mListener = listener;

        mActions = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position){
        if (position == 0){
            return TYPE_BLANK;
        }
        else if (position == 1){
            if (mActions.isEmpty()){
                return TYPE_LOAD;
            }
            return TYPE_CONTENT;
        }
        return TYPE_LOAD;
    }

    @Override
    public int getItemCount(){
        int count = ITEM_COUNT;
        if (mActions.isEmpty()){
            count--;
        }
        if (!mShowLoading){
            count--;
        }
        return count;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(mContext);

        if (viewType == TYPE_BLANK){
            return new RecyclerView.ViewHolder(new CardView(mContext)){};
        }
        else if (viewType == TYPE_CONTENT){
            if (mActionsHolder == null){
                View rootView = inflater.inflate(R.layout.card_library_content, parent, false);
                mActionsHolder = new ActionsViewHolder(this, rootView);
                mActionsHolder.addActions(mActions);
                mActionsHolder.mActionContainer.setAnimationsEnabled(true);
            }
            return mActionsHolder;
        }
        else{
            View rootView = inflater.inflate(R.layout.item_library_progress, parent, false);
            return new RecyclerView.ViewHolder(rootView){};
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, int position){
        //Blank space
        if (position == 0){
            int width = CompassUtil.getScreenWidth(mContext);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    (int)((width*2/3)*0.8)
            );
            rawHolder.itemView.setLayoutParams(params);
            rawHolder.itemView.setVisibility(View.INVISIBLE);
        }
        //Actions or load switch
        else if (position == 1){
            if (mActions.isEmpty()){
                if (mShowLoading){
                    if (mLoadError.isEmpty()){
                        mListener.loadMore();
                    }
                    else{
                        rawHolder.itemView.findViewById(R.id.library_progress_progress).setVisibility(View.GONE);
                        TextView error = (TextView)rawHolder.itemView.findViewById(R.id.library_progress_error);
                        error.setVisibility(View.VISIBLE);
                        error.setText(mLoadError);
                    }
                }
            }
            else{
                if (mUserGoal != null){
                    ((ActionsViewHolder)rawHolder).bind(mUserGoal);
                }
                else if (mUserBehavior != null){
                    ((ActionsViewHolder)rawHolder).bind(mUserBehavior);
                }
            }
        }
    }

    public void addActions(@NonNull List<Action> actions, boolean showLoading){
        //If there are no actions, insert the content card
        if (mActions.isEmpty()){
            notifyItemInserted(TYPE_CONTENT);
        }

        //Set the new loading state
        mShowLoading = showLoading;
        //If we should no longer load, remove the load switch
        if (!mShowLoading){
            notifyItemRemoved(TYPE_LOAD);
        }
        //Otherwise, schedule an item refresh for the load switch half a second from now
        //  to avoid the load callback getting called twice
        else{
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run(){
                    notifyItemChanged(TYPE_LOAD);
                }
            }, 500);
        }

        //Add all the actions in the behavior list
        mActions.addAll(actions);

        //If the holder has been created already
        if (mActionsHolder != null){
            //Add the actions
            mActionsHolder.addActions(actions);
        }
    }

    /**
     * Displays an error in place of the load switch.
     *
     * @param error the error to be displayed.
     */
    public void displayError(String error){
        mLoadError = error;
        if (mActions.isEmpty()){
            notifyItemChanged(TYPE_LOAD-1);
        }
        else if (mShowLoading){
            notifyItemChanged(TYPE_LOAD);
        }
    }


    private static class ActionsViewHolder
            extends RecyclerView.ViewHolder
            implements ContentContainer.ContentContainerListener<Action>{

        private ReviewActionsAdapter mAdapter;

        private TextView mTitle;
        private ContentContainer<Action> mActionContainer;


        @SuppressWarnings("unchecked")
        public ActionsViewHolder(ReviewActionsAdapter adapter, View rootView){
            super(rootView);
            mAdapter = adapter;

            //Fetch UI components
            mTitle = (TextView)rootView.findViewById(R.id.card_library_content_header);
            mActionContainer = (ContentContainer<Action>)rootView
                    .findViewById(R.id.card_library_content_container);

            mTitle.setTextColor(mAdapter.mContext.getResources().getColor(R.color.secondary_text_color));
            mActionContainer.setListener(this);
        }

        public void bind(UserGoal userGoal){
            mTitle.setText(mAdapter.mContext.getString(R.string.library_review_action_header,
                    userGoal.getTitle()));
        }

        public void bind(UserBehavior userBehavior){
            mTitle.setText(mAdapter.mContext.getString(R.string.library_review_action_header,
                    userBehavior.getTitle()));
        }

        public void addActions(List<Action> actions){
            for (Action action:actions){
                mActionContainer.addContent(action);
            }
        }

        @Override
        public void onContentClick(@NonNull Action content){
            mAdapter.mListener.onActionSelected(content);
        }
    }


    public interface ReviewActionsListener{
        void onActionSelected(Action action);
        void loadMore();
    }
}
