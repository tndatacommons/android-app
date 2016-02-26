package org.tndata.android.compass.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.View;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.BehaviorContent;
import org.tndata.android.compass.model.CategoryContent;
import org.tndata.android.compass.util.CompassTagHandler;


/**
 * Adapter for the BehaviorActivity. Displays the header and behavior cards.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class BehaviorAdapter extends MaterialAdapter implements View.OnClickListener{
    private Context mContext;
    private BehaviorListener mListener;
    private CategoryContent mCategory;
    private BehaviorContent mBehavior;

    private @IdRes int mButtonId;


    /**
     * Constructor.
     *
     * @param context a reference to the context.
     * @param listener the listener.
     * @param category the parent category of the behavior.
     * @param behavior the behavior to be displayed.
     */
    public BehaviorAdapter(@NonNull Context context, @NonNull BehaviorListener listener,
                           @NonNull CategoryContent category, @NonNull BehaviorContent behavior){

        super(context, ContentType.DETAIL, false);

        mContext = context;
        mListener = listener;
        mCategory = category;
        mBehavior = behavior;
    }

    @Override
    protected void bindHeaderHolder(HeaderViewHolder holder){
        holder.setTitle(mContext.getString(R.string.library_behavior_title, mBehavior.getTitle()));
        if (!mBehavior.getHTMLDescription().isEmpty()){
            holder.setDescription(Html.fromHtml(mBehavior.getHTMLDescription(), null,
                    new CompassTagHandler(mContext)));
        }
        else{
            holder.setDescription(mBehavior.getDescription());
        }
        holder.setButton(mContext.getString(R.string.library_behavior_yes), this);
        mButtonId = holder.getButtonId();
    }

    @Override
    protected void bindDetailHolder(DetailViewHolder holder){
        if (!mCategory.getColor().isEmpty()){
            holder.setHeaderColor(Color.parseColor(mCategory.getColor()));
        }

        if (!mBehavior.getHTMLDescription().isEmpty()){
            holder.setDescription(Html.fromHtml(mBehavior.getHTMLDescription(), null,
                    new CompassTagHandler(mContext)));
        }
        else{
            holder.setDescription(mBehavior.getDescription());
        }

        if (!mBehavior.getHTMLMoreInfo().isEmpty()){
            holder.setMoreInfo(Html.fromHtml(mBehavior.getHTMLMoreInfo(), null,
                    new CompassTagHandler(mContext)));
        }
        else if (!mBehavior.getMoreInfo().isEmpty()){
            holder.setMoreInfo(mBehavior.getMoreInfo());
        }
    }

    @Override
    public void onClick(View v){
        if (v.getId() == mButtonId){
            mListener.acceptBehavior();
        }
    }

    /**
     * Listener interface for the adapter.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface BehaviorListener{
        /**
         * Called when the user taps the 'yes, I'm in' button.
         */
        void acceptBehavior();
    }
}
