package org.tndata.android.compass.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.CategoryContent;
import org.tndata.android.compass.model.UserAction;
import org.tndata.android.compass.util.CompassTagHandler;


/**
 * Adapter for ActionActivity.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class ActionAdapter extends MaterialAdapter implements View.OnClickListener{
    private ActionAdapterListener mListener;
    private Action mAction;
    private CategoryContent mCategory;
    private boolean mFromNotification;


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

    public void setAction(@NonNull Action action, @Nullable CategoryContent category){
        mAction = action;
        mCategory = category;
        notifyHeaderInserted();
        if (mAction instanceof UserAction){
            notifyDetailsInserted();
        }
        updateLoading(false);
    }

    public void setCategory(@NonNull CategoryContent category){
        mCategory = category;
        notifyItemChanged(2);
    }

    @Override
    protected boolean hasHeader(){
        return mAction != null;
    }

    @Override
    protected boolean hasDetails(){
        return mAction != null && mAction instanceof UserAction;
    }

    @Override
    protected void bindHeaderHolder(RecyclerView.ViewHolder rawHolder){
        HeaderViewHolder holder = (HeaderViewHolder)rawHolder;
        holder.setTitle(mAction.getTitle());

        if (mAction instanceof UserAction){
            UserAction userAction = (UserAction)mAction;
            holder.setContent(userAction.getDescription());
        }

        if (mFromNotification){
            holder.addButton(R.id.action_snooze, R.string.action_snooze, this);
        }
        else{
            holder.addButton(R.id.action_reschedule, R.string.action_reschedule, this);
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
        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.action_did_it:
                mListener.onIDidItClick();
                break;

            case R.id.action_reschedule:
                mListener.onRescheduleClick();
                break;

            case R.id.action_snooze:
                mListener.onSnoozeClick();
                break;
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
