package org.tndata.android.compass.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.CategoryContent;
import org.tndata.android.compass.model.CustomAction;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.Reward;
import org.tndata.android.compass.model.UserAction;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.CompassTagHandler;
import org.tndata.android.compass.util.CompassUtil;

import es.sandwatch.httprequests.HttpRequest;
import es.sandwatch.httprequests.HttpRequestError;


/**
 * Adapter for ActionActivity.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class ActionAdapter
        extends MaterialAdapter
        implements
                View.OnClickListener,
                HttpRequest.RequestCallback,
                Parser.ParserCallback{

    private ActionAdapterListener mListener;
    private Action mAction;
    private CategoryContent mCategory;
    private Goal mGoal;
    private boolean mFromNotification;

    private int mGetRewardRC;
    private Reward mReward;


    /**
     * Constructor.
     *
     * @param context a reference to the context.
     */
    public ActionAdapter(@NonNull Context context, @NonNull ActionAdapterListener listener, boolean fromNotification){
        super(context, ContentType.DETAIL, true);

        mListener = listener;
        mAction = null;
        mCategory = null;
        mFromNotification = fromNotification;
    }

    public void setAction(@NonNull Action action){
        mAction = action;
        notifyHeaderInserted();
        if (hasDetails()){
            notifyDetailsInserted();
            updateLoading(false);
        }
        else{
            fetchReward();
        }
    }

    public void setAction(@NonNull Action action, @NonNull Goal goal){
        mAction = action;
        mGoal = goal;
        notifyHeaderInserted();
        fetchReward();
    }

    public void setCategory(@NonNull CategoryContent category){
        mCategory = category;
        if (hasDetails()){
            notifyItemChanged(2);
        }
    }

    @Override
    protected boolean hasHeader(){
        return mAction != null;
    }

    @Override
    protected boolean hasDetails(){
        if (mAction != null){
            if (mAction instanceof UserAction){
                UserAction userAction = (UserAction)mAction;
                if (!userAction.getMoreInfo().isEmpty() || !userAction.getHTMLMoreInfo().isEmpty()){
                    return true;
                }
                return mReward != null;
            }
            else if (mAction instanceof CustomAction){
                return mReward != null;
            }
        }
        return false;
    }

    @Override
    protected void bindHeaderHolder(RecyclerView.ViewHolder rawHolder){
        HeaderViewHolder holder = (HeaderViewHolder)rawHolder;

        if (mAction instanceof UserAction){
            holder.setTitle(mAction.getTitle());
            holder.setTitleBold();
            UserAction userAction = (UserAction)mAction;
            holder.setContent(userAction.getDescription());
        }
        else if (mAction instanceof CustomAction){
            holder.setTitle("To " + mGoal.getTitle() + ":");
            holder.setContent(mAction.getTitle());
        }

        if (mFromNotification){
            holder.addButton(R.id.action_snooze, R.string.action_snooze, this);
        }
        else{
            holder.addButton(R.id.action_reschedule, R.string.action_reschedule, this);
        }
        if (mAction instanceof UserAction && !((UserAction)mAction).getExternalResource().isEmpty()){
            holder.addButton(R.id.action_do_it_now, R.string.action_do_it_now, this);
        }
        holder.addButton(R.id.action_did_it, R.string.action_did_it, this);
    }

    @Override
    protected void bindDetailHolder(DetailViewHolder holder){
        if (mAction instanceof UserAction){
            UserAction userAction = (UserAction)mAction;
            if (mCategory == null){
                holder.setHeaderColor(getContext().getResources().getColor(R.color.grow_primary));
            }
            else{
                holder.setHeaderColor(Color.parseColor(mCategory.getColor()));
            }
            holder.setTitle("More info");
            if (!userAction.getHTMLMoreInfo().isEmpty()){
                holder.setDescription(Html.fromHtml(userAction.getHTMLMoreInfo(), null,
                        new CompassTagHandler(getContext())));
            }
            else if (!userAction.getMoreInfo().isEmpty()){
                holder.setDescription(userAction.getMoreInfo());
            }
            else{
                setReward(holder);
            }
        }
        else if (mAction instanceof CustomAction){
            holder.setHeaderColor(getContext().getResources().getColor(R.color.grow_primary));
            setReward(holder);
        }
    }

    private void setReward(DetailViewHolder holder){
        if (mReward.isFortune()){
            holder.setTitle("Here's a fortune cookie for you");
            holder.setDescription(mReward.getMessage());
        }
        else if (mReward.isFunFact()){
            holder.setTitle("Here's a fun fact for you");
            holder.setDescription(mReward.getMessage());
        }
        else if (mReward.isJoke()){
            holder.setTitle("Here's a joke for you");
            holder.setDescription(mReward.getMessage());
        }
        else{
            holder.setTitle("Here's a nice quote for you");
            holder.setDescription(mReward.getMessage() + " (" + mReward.getAuthor() + ")");
        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.action_did_it:
                mListener.onIDidItClick();
                break;

            case R.id.action_do_it_now:
                CompassUtil.doItNow(getContext(), ((UserAction)mAction).getExternalResource());
                break;

            case R.id.action_reschedule:
                mListener.onRescheduleClick();
                break;

            case R.id.action_snooze:
                mListener.onSnoozeClick();
                break;
        }
    }

    private void fetchReward(){
        mGetRewardRC = HttpRequest.get(this, API.getRandomRewardUrl());
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mGetRewardRC){
            Parser.parse(result, ParserModels.RewardResultSet.class, this);
        }
    }

    @Override
    public void onRequestFailed(int requestCode, HttpRequestError error){

    }

    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){

    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        if (result instanceof ParserModels.RewardResultSet){
            mReward = ((ParserModels.RewardResultSet)result).results.get(0);
            notifyDetailsInserted();
            updateLoading(false);
        }
    }


    /**
     * Listener interface for the adapter.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface ActionAdapterListener{
        void onIDidItClick();
        void onRescheduleClick();
        void onSnoozeClick();
    }
}
