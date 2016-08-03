package org.tndata.android.compass.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.CustomAction;
import org.tndata.android.compass.model.Reward;
import org.tndata.android.compass.model.TDCAction;
import org.tndata.android.compass.model.TDCCategory;
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
    private TDCCategory mCategory;
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

    public void setCategory(@NonNull TDCCategory category){
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
            /*String gt = mAction.getGoalTitle().toLowerCase();
            String bt = ((UserAction)mAction).getAction().getBehaviorTitle().toLowerCase();
            holder.setSubtitle(getContext().getString(R.string.action_header_subtitle, bt, gt));*/
            holder.setSubtitle(((UserAction)mAction).getAction().getBehaviorTitle());
            UserAction userAction = (UserAction)mAction;
            holder.setContent(userAction.getDescription());
            holder.setSubtitleListener(this);
        }
        else if (mAction instanceof CustomAction){
            holder.setTitle("To " + mAction.getGoalTitle() + ":");
            holder.setContent(mAction.getTitle());
        }

        if (mFromNotification){
            holder.addButton(R.id.action_snooze, R.string.action_snooze, this);
        }
        else{
            holder.addButton(R.id.action_reschedule, R.string.action_reschedule, this);
        }

        if (mAction instanceof UserAction && !((UserAction)mAction).getExternalResource().isEmpty()){
            if(((UserAction)mAction).getExternalResourceType().equals("datetime")){
                holder.addButton(R.id.action_add_to_calendar, R.string.action_add_to_calendar, this);
            }
            else {
                holder.addButton(R.id.action_do_it_now, R.string.action_do_it_now, this);
            }
        }
        holder.addButton(R.id.action_did_it, R.string.action_did_it, this);
    }

    @Override
    protected void bindDetailHolder(DetailViewHolder holder){
        if (mAction instanceof UserAction){
            UserAction userAction = (UserAction)mAction;
            if (mCategory == null){
                holder.setHeaderColor(getContext().getResources().getColor(R.color.primary));
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
            holder.setHeaderColor(getContext().getResources().getColor(R.color.primary));
            setReward(holder);
        }
    }

    private void setReward(DetailViewHolder holder){
        if (mReward.isFortune()){
            holder.setTitle(R.string.reward_fortune_header);
        }
        else if (mReward.isFunFact()){
            holder.setTitle(R.string.reward_fact_header);
        }
        else if (mReward.isJoke()){
            holder.setTitle(R.string.reward_joke_header);
        }
        else if (mReward.isQuote()){
            holder.setTitle(R.string.reward_quote_header);
        }
        holder.setDescription(mReward.format());
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.action_did_it:
                mListener.onIDidItClick();
                break;

            case R.id.action_do_it_now:
                TDCAction action = ((UserAction)mAction).getAction();
                if(action.hasLinkResource()) {
                    CompassUtil.doItNow(getContext(), action.getExternalResource());
                }
                else if(action.hasPhoneNumberResource()){
                    CompassUtil.callPhoneNumber(getContext(), action.getExternalResource());
                }
                break;

            case R.id.action_reschedule:
                mListener.onRescheduleClick();
                break;

            case R.id.action_snooze:
                mListener.onSnoozeClick();
                break;

            case R.id.material_header_subtitle:
                mListener.onBehaviorInfoClick();
                break;

            case R.id.action_add_to_calendar:
                mListener.sendToCalendar();
                break;
        }
    }

    private void fetchReward(){
        mGetRewardRC = HttpRequest.get(this, API.URL.getRandomReward());
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
        void onBehaviorInfoClick();
        void sendToCalendar();
    }
}
