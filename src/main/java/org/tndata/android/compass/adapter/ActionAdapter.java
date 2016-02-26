package org.tndata.android.compass.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.View;

import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.UserAction;
import org.tndata.android.compass.util.CompassTagHandler;


/**
 * Created by isma on 2/25/16.
 */
public class ActionAdapter extends MaterialAdapter implements View.OnClickListener{
    private Context mContext;
    private Action mAction;

    private @IdRes int mButtonId;


    /**
     * Constructor.
     *
     * @param context a reference to the context.
     */
    public ActionAdapter(@NonNull Context context, @Nullable Action action){
        super(context, ContentType.DETAIL, action == null);

        mContext = context;
        mAction = action;
    }

    public void setAction(@NonNull Action action){
        mAction = action;
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
    protected void bindHeaderHolder(HeaderViewHolder holder){
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
            holder.setHeaderColor(Color.parseColor(userAction.getPrimaryCategory().getColor()));

            if (!userAction.getHTMLDescription().isEmpty()){
                holder.setDescription(Html.fromHtml(userAction.getHTMLDescription(), null,
                        new CompassTagHandler(mContext)));
            }
            else{
                holder.setDescription(userAction.getDescription());
            }

            if (!userAction.getHTMLMoreInfo().isEmpty()){
                holder.setMoreInfo(Html.fromHtml(userAction.getHTMLMoreInfo(), null,
                        new CompassTagHandler(mContext)));
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
