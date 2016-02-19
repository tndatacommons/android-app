package org.tndata.android.compass.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.BehaviorContent;
import org.tndata.android.compass.model.CategoryContent;
import org.tndata.android.compass.util.CompassTagHandler;
import org.tndata.android.compass.util.CompassUtil;


/**
 * Adapter for the BehaviorActivity. Displays the header and behavior cards.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class BehaviorAdapter extends RecyclerView.Adapter{
    public static final int TYPE_BLANK = 0;
    public static final int TYPE_DESCRIPTION = TYPE_BLANK+1;
    public static final int TYPE_BEHAVIOR = TYPE_DESCRIPTION+1;
    public static final int ITEM_COUNT = TYPE_BEHAVIOR+1;


    private Context mContext;
    private BehaviorListener mListener;
    private CategoryContent mCategory;
    private BehaviorContent mBehavior;


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

        mContext = context;
        mListener = listener;
        mCategory = category;
        mBehavior = behavior;
    }

    @Override
    public int getItemViewType(int position){
        if (position == 0){
            return TYPE_BLANK;
        }
        else if (position == 1){
            return TYPE_DESCRIPTION;
        }
        else{
            return TYPE_BEHAVIOR;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if (viewType == TYPE_BLANK){
            return new RecyclerView.ViewHolder(new CardView(mContext)){};
        }
        else if (viewType == TYPE_DESCRIPTION){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View rootView = inflater.inflate(R.layout.card_library_description, parent, false);
            return new DescriptionViewHolder(mContext, this, rootView);
        }
        else{
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View rootView = inflater.inflate(R.layout.card_library_behavior_detail, parent, false);
            return new BehaviorViewHolder(mContext, rootView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, int position){
        if (position == 0){
            int width = CompassUtil.getScreenWidth(mContext);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    (int)((width*2/3)*0.8)
            );
            rawHolder.itemView.setLayoutParams(params);
            rawHolder.itemView.setVisibility(View.INVISIBLE);
        }
        else if (position == 1){
            ((DescriptionViewHolder)rawHolder).bind(mBehavior);
        }
        else{
            ((BehaviorViewHolder)rawHolder).bind(mCategory, mBehavior);
        }
    }

    @Override
    public int getItemCount(){
        return ITEM_COUNT;
    }


    /**
     * View holder for a description card.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    static class DescriptionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private Context mContext;
        private BehaviorAdapter mAdapter;
        private TextView mBehaviorTitle;
        private TextView mBehaviorDescription;


        /**
         * Constructor.
         *
         * @param context a reference to the context.
         * @param rootView the view to be drawn.
         */
        public DescriptionViewHolder(@NonNull Context context, @NonNull BehaviorAdapter adapter,
                                     @NonNull View rootView){

            super(rootView);

            mContext = context;
            mAdapter = adapter;
            mBehaviorTitle = (TextView)rootView.findViewById(R.id.library_description_title);
            mBehaviorDescription = (TextView)rootView.findViewById(R.id.library_description_content);

            rootView.findViewById(R.id.library_description_button_container).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.library_description_no).setOnClickListener(this);
            rootView.findViewById(R.id.library_description_yes).setOnClickListener(this);
        }

        /**
         * Binds a behavior to the holder.
         *
         * @param behavior the behavior whose description is to be drawn.
         */
        public void bind(@NonNull BehaviorContent behavior){
            mBehaviorTitle.setText(mContext.getString(R.string.behavior_title, behavior.getTitle()));
            if (!behavior.getHTMLDescription().isEmpty()){
                mBehaviorDescription.setText(Html.fromHtml(behavior.getHTMLDescription(), null,
                        new CompassTagHandler(mContext)));
            }
            else{
                mBehaviorDescription.setText(behavior.getDescription());
            }
        }

        @Override
        public void onClick(View v){
            switch (v.getId()){
                case R.id.library_description_no:
                    mAdapter.mListener.dismissBehavior();
                    break;

                case R.id.library_description_yes:
                    mAdapter.mListener.acceptBehavior();
                    break;
            }
        }
    }


    /**
     * View holder for the displayed behavior.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    static class BehaviorViewHolder extends RecyclerView.ViewHolder{
        private Context mContext;

        private TextView mHeader;
        private TextView mDescription;
        private View mSeparator;
        private TextView mMoreInfoTitle;
        private TextView mMoreInfo;


        /**
         * Constructor.
         *
         * @param context a reference to the context.
         * @param rootView the root view of the holder.
         */
        public BehaviorViewHolder(@NonNull Context context, @NonNull View rootView){
            super(rootView);

            mContext = context;

            mHeader = (TextView)rootView.findViewById(R.id.behavior_detail_header);
            mDescription = (TextView)rootView.findViewById(R.id.behavior_detail_description);
            mSeparator = rootView.findViewById(R.id.behavior_detail_separator);
            mMoreInfoTitle = (TextView)rootView.findViewById(R.id.behavior_detail_more_into_title);
            mMoreInfo = (TextView)rootView.findViewById(R.id.behavior_detail_more_info);
        }

        /**
         * Binds a behavior to this holder.
         *
         * @param category the parent category of the behavior to be bound.
         * @param behavior the behavior to be displayed in the view.
         */
        public void bind(@NonNull CategoryContent category, @NonNull BehaviorContent behavior){
            if (!category.getColor().isEmpty()){
                mHeader.setBackgroundColor(Color.parseColor(category.getColor()));
            }
            else{
                mHeader.setBackgroundResource(R.color.grow_accent);
            }
            if (!behavior.getHTMLDescription().isEmpty()){
                mDescription.setText(Html.fromHtml(behavior.getHTMLDescription(), null,
                        new CompassTagHandler(mContext)));
            }
            else{
                mDescription.setText(behavior.getDescription());
            }
            if (behavior.getMoreInfo().isEmpty() && behavior.getHTMLMoreInfo().isEmpty()){
                mSeparator.setVisibility(View.GONE);
                mMoreInfoTitle.setVisibility(View.GONE);
                mMoreInfo.setVisibility(View.GONE);
            }
            else{
                mSeparator.setVisibility(View.VISIBLE);
                mMoreInfoTitle.setVisibility(View.VISIBLE);
                mMoreInfo.setVisibility(View.VISIBLE);
                if (!behavior.getHTMLMoreInfo().isEmpty()){
                    mMoreInfo.setText(Html.fromHtml(behavior.getHTMLMoreInfo(), null,
                            new CompassTagHandler(mContext)));
                }
                else{
                    mMoreInfo.setText(behavior.getMoreInfo());
                }
            }
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
         * Called when the user taps the 'not now' button.
         */
        void dismissBehavior();

        /**
         * Called when the user taps the 'yes, I'm in' button.
         */
        void acceptBehavior();
    }
}
