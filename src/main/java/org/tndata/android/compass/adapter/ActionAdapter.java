package org.tndata.android.compass.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;

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
    private Action mAction;
    private CategoryContent mCategory;

    private @IdRes int mButtonId;


    /**
     * Constructor.
     *
     * @param context a reference to the context.
     */
    public ActionAdapter(@NonNull Context context, @Nullable Action action, @Nullable CategoryContent category){
        super(context, ContentType.DETAIL, action == null);

        mAction = action;
        mCategory = category;
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
            holder.setButton("Edit", this);
            mButtonId = holder.getButtonId();
        }
    }

    @Override
    protected void bindDetailHolder(DetailViewHolder holder){
        if (mAction instanceof UserAction){
            UserAction userAction = (UserAction)mAction;
            holder.setHeaderColor(Color.parseColor(mCategory.getColor()));

            if (!userAction.getHTMLDescription().isEmpty()){
                holder.setDescription(Html.fromHtml(userAction.getHTMLDescription(), null,
                        new CompassTagHandler(getContext())));
            }
            else{
                holder.setDescription(userAction.getDescription());
            }

            if (!userAction.getHTMLMoreInfo().isEmpty()){
                holder.setMoreInfo(Html.fromHtml(userAction.getHTMLMoreInfo(), null,
                        new CompassTagHandler(getContext())));
            }
            else if (!userAction.getMoreInfo().isEmpty()){
                holder.setMoreInfo(userAction.getMoreInfo());
            }
        }
    }

    @Override
    public void onClick(View v){
        if (mButtonId == v.getId()){

        }
    }
}
