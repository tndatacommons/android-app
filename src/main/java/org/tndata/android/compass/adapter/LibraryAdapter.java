package org.tndata.android.compass.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.util.CompassUtil;


/**
 * Generic adapter for LibraryActivity. This class is an abstraction of the common
 * functionality of all the adapters used by activities extending LibraryActivity.
 *
 * Includes a material transparent view atop, the possibility of adding a description
 * card, either a list of items, or a detail card, and the possibility of adding a
 * loading widget that calls a method when it is reached.
 *
 * @author Ismael Alonso.
 * @version 1.0.0
 */
public abstract class LibraryAdapter extends RecyclerView.Adapter{
    private static final String TAG = "LibraryAdapter";

    private static final int TYPE_BLANK = 0;
    private static final int TYPE_DESCRIPTION = TYPE_BLANK+1;
    private static final int TYPE_LISTED_CONTENT = TYPE_DESCRIPTION+1;      //Either
    private static final int TYPE_DETAIL_CONTENT = TYPE_LISTED_CONTENT+1;   //Or
    private static final int TYPE_LOAD = TYPE_DETAIL_CONTENT+1;


    private Context mContext;
    private ContentType mContentType;

    private boolean mShowLoading;
    private String mLoadingError;


    protected LibraryAdapter(Context context, ContentType contentType){
        mContext = context;
        mContentType = contentType;

        mShowLoading = true;
        mLoadingError = "";
    }

    protected void updateLoading(boolean showLoading){
        mShowLoading = showLoading;
        //If we are no longer loading, remove the switch
        if (!mShowLoading){
            notifyItemRemoved(getItemCount());
        }
        //Otherwise, schedule an item refresh for the load switch half a second from now
        //  to avoid the load callback getting called twice
        else{
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run(){
                    notifyItemChanged(getItemCount()-1);
                }
            }, 500);
        }
    }

    protected boolean hasDescription(){
        return true;
    }

    protected boolean isEmpty(){
        return true;
    }

    @Override
    public final int getItemCount(){
        //Blank space
        int count = 1;
        //Description
        if (hasDescription()){
            count++;
        }
        //Content
        if (mContentType.getType() == TYPE_DETAIL_CONTENT){
            count++;
        }
        else if (mContentType.getType() == TYPE_LISTED_CONTENT){
            if (!isEmpty()){
                count++;
            }
        }
        //Loading section
        if (mShowLoading){
            count++;
        }
        return count;
    }

    @Override
    public final int getItemViewType(int position){
        //Item at position 0 is always a transparent view
        if (position == 0){
            return TYPE_BLANK;
        }
        //Item at position 1 is generally a description
        if (position == 1){
            if (hasDescription()){
                return TYPE_DESCRIPTION;
            }
            else{
                //But if there is no description, the next block is shown: content
                if (mContentType.getType() == TYPE_DETAIL_CONTENT){
                    return TYPE_DETAIL_CONTENT;
                }
                else{
                    if (!isEmpty()){
                        return TYPE_LISTED_CONTENT;
                    }
                }
            }
        }
        //Item at position 2 is generally the content, if there is a description block
        if (position == 2){
            if (hasDescription()){
                if (mContentType.getType() == TYPE_DETAIL_CONTENT){
                    return TYPE_DETAIL_CONTENT;
                }
                else{
                    if (!isEmpty()){
                        return TYPE_LISTED_CONTENT;
                    }
                }
            }
        }
        //Everything else defaults to load
        return TYPE_LOAD;
    }

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(mContext);

        if (viewType == TYPE_BLANK){
            return new RecyclerView.ViewHolder(new CardView(mContext)){};
        }
        else if (viewType == TYPE_DESCRIPTION){
            View rootView = inflater.inflate(R.layout.card_library_description, parent, false);
            return new DescriptionViewHolder(rootView);
        }
        else if (viewType == TYPE_LISTED_CONTENT){
            RecyclerView.ViewHolder holder = getListHolder(parent);
            if (holder == null){
                throw new IllegalStateException("Adapter needs to override getListHolder()");
            }
            return holder;
        }
        else if (viewType == TYPE_DETAIL_CONTENT){
            View rootView = inflater.inflate(R.layout.card_library_detail, parent, false);
            return new DetailViewHolder(rootView);
        }
        else /*if (viewType == TYPE_LOAD)*/{
            View rootView = inflater.inflate(R.layout.item_library_progress, parent, false);
            return new RecyclerView.ViewHolder(rootView){};
        }
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder rawHolder, int position){
        //The items in this list are unique, none of them is displayed more
        //  than once, so the view type will do.
        int viewType = getItemViewType(position);
        if (viewType == TYPE_BLANK){
            int width = CompassUtil.getScreenWidth(mContext);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    (int)((width*2/3)*0.8)
            );
            rawHolder.itemView.setLayoutParams(params);
            rawHolder.itemView.setVisibility(View.INVISIBLE);
        }
        else if (viewType == TYPE_DESCRIPTION){
            bindDescriptionHolder((DescriptionViewHolder)rawHolder);
        }
        else if (viewType == TYPE_LISTED_CONTENT){
            bindListHolder(rawHolder);
        }
        else if (viewType == TYPE_DETAIL_CONTENT){
            bindDetailHolder((DetailViewHolder)rawHolder);
        }
        else if (viewType == TYPE_LOAD){
            if (mLoadingError.isEmpty()){
                loadMore();
            }
            else{
                rawHolder.itemView.findViewById(R.id.library_progress_progress).setVisibility(View.GONE);
                TextView error = (TextView)rawHolder.itemView.findViewById(R.id.library_progress_error);
                error.setVisibility(View.VISIBLE);
                error.setText(mLoadingError);
            }
        }
    }

    protected RecyclerView.ViewHolder getListHolder(ViewGroup parent){
        return null;
    }

    protected void bindDescriptionHolder(DescriptionViewHolder holder){

    }

    protected void bindListHolder(RecyclerView.ViewHolder rawHolder){

    }

    protected void bindDetailHolder(DetailViewHolder holder){

    }

    protected void loadMore(){

    }

    protected final void notifyListInserted(){
        if (mContentType != ContentType.LIST){
            Log.e(TAG, "Can't insert list in a non listing adapter");
        }
        else{

            if (hasDescription()){
                notifyItemInserted(2);
            }
            else{
                notifyItemInserted(1);
            }
        }
    }

    /**
     * Displays an error in place of the load switch.
     *
     * @param error the error to be displayed.
     */
    public final void displayError(String error){
        mLoadingError = error;
        notifyItemChanged(getItemCount()-1);
    }


    public enum ContentType{
        LIST(TYPE_LISTED_CONTENT),
        DETAIL(TYPE_DETAIL_CONTENT);


        private int mType;

        ContentType(int type){
            mType = type;
        }

        private int getType(){
            return mType;
        }
    }


    /**
     * View holder for a description card.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    protected static class DescriptionViewHolder extends RecyclerView.ViewHolder{
        private TextView mDescriptionTitle;
        private TextView mDescriptionContent;
        private TextView mButton;


        /**
         * Constructor.
         *
         * @param rootView the view to be drawn.
         */
        public DescriptionViewHolder(@NonNull View rootView){
            super(rootView);

            mDescriptionTitle = (TextView)rootView.findViewById(R.id.library_description_title);
            mDescriptionContent = (TextView)rootView.findViewById(R.id.library_description_content);
            mButton = (TextView)rootView.findViewById(R.id.library_description_yes);
        }

        public void setTitle(CharSequence title){
            mDescriptionTitle.setText(title);
        }

        public void setDescription(CharSequence description){
            mDescriptionContent.setText(description);
        }

        public void setButton(CharSequence text, View.OnClickListener onClickListener){
            mButton.setText(text);
            mButton.setVisibility(View.VISIBLE);
            mButton.setOnClickListener(onClickListener);
        }

        @IdRes
        public int getButtonId(){
            return mButton.getId();
        }
    }


    protected static class DetailViewHolder extends RecyclerView.ViewHolder{
        private TextView mHeader;
        private TextView mDescription;
        private View mSeparator;
        private TextView mMoreInfoTitle;
        private TextView mMoreInfo;


        /**
         * Constructor.
         *
         * @param rootView the root view of the holder.
         */
        public DetailViewHolder(@NonNull View rootView){
            super(rootView);

            mHeader = (TextView)rootView.findViewById(R.id.library_detail_header);
            mDescription = (TextView)rootView.findViewById(R.id.library_detail_description);
            mSeparator = rootView.findViewById(R.id.library_detail_separator);
            mMoreInfoTitle = (TextView)rootView.findViewById(R.id.library_detail_more_into_title);
            mMoreInfo = (TextView)rootView.findViewById(R.id.library_detail_more_info);
        }

        public void setHeaderColor(int color){
            mHeader.setBackgroundColor(color);
        }

        public void setDescription(CharSequence description){
            mDescription.setText(description);
        }

        public void setMoreInfo(CharSequence moreInfo){
            mSeparator.setVisibility(View.VISIBLE);
            mMoreInfoTitle.setVisibility(View.VISIBLE);
            mMoreInfo.setVisibility(View.VISIBLE);
            mMoreInfo.setText(moreInfo);
        }
    }
}
