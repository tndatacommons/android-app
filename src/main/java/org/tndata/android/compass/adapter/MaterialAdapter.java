package org.tndata.android.compass.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
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
public abstract class MaterialAdapter extends RecyclerView.Adapter{
    private static final String TAG = "LibraryAdapter";

    private static final int TYPE_BLANK = 0;
    private static final int TYPE_HEADER = TYPE_BLANK+1;
    private static final int TYPE_LISTED_CONTENT = TYPE_HEADER +1;          //Either
    private static final int TYPE_DETAIL_CONTENT = TYPE_LISTED_CONTENT+1;   //Or
    private static final int TYPE_LOAD = TYPE_DETAIL_CONTENT+1;


    private Context mContext;
    private ContentType mContentType;
    private ListViewHolder mListHolder;
    private boolean mShowLoading;
    private String mLoadingError;


    /**
     * Constructor.
     *
     * @param context a reference to the context.
     * @param contentType the type of adapter, list or detail.
     * @param showLoading whether the loading widget should be shown by default.
     */
    protected MaterialAdapter(Context context, ContentType contentType, boolean showLoading){
        mContext = context;
        mContentType = contentType;
        mListHolder = null;
        mShowLoading = showLoading;
        mLoadingError = "";
    }

    /**
     * Getter for the context.
     *
     * @return a reference to the context.
     */
    protected Context getContext(){
        return mContext;
    }

    /**
     * Method to override if showing a header card is conditional or other than default.
     *
     * @return true as a default value.
     */
    protected boolean hasHeader(){
        return true;
    }

    /**
     * Method to override to determine if the backing data set in a listing adapter is empty.
     *
     * @return true as a default value.
     */
    protected boolean isEmpty(){
        return true;
    }

    /**
     * Method to override if showing a details card is optional or other than default.
     *
     * @return true as a default value.
     */
    protected boolean hasDetails(){
        return true;
    }

    @Override
    public final int getItemCount(){
        //Blank space
        int count = 1;
        //Description
        if (hasHeader()){
            count++;
        }
        //Content
        if (mContentType.getType() == TYPE_LISTED_CONTENT){
            if (!isEmpty()){
                count++;
            }
        }
        else if (mContentType.getType() == TYPE_DETAIL_CONTENT){
            if (hasDetails()){
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
            if (hasHeader()){
                return TYPE_HEADER;
            }
            else{
                //But if there is no description, the next block is shown: content
                if (mContentType.getType() == TYPE_LISTED_CONTENT){
                    if (!isEmpty()){
                        return TYPE_LISTED_CONTENT;
                    }
                }
                else if (mContentType.getType() == TYPE_DETAIL_CONTENT){
                    if (hasDetails()){
                        return TYPE_DETAIL_CONTENT;
                    }
                }
            }
        }
        //Item at position 2 is generally the content, if there is a description block
        if (position == 2){
            if (hasHeader()){
                if (mContentType.getType() == TYPE_DETAIL_CONTENT){
                    if (hasDetails()){
                        return TYPE_DETAIL_CONTENT;
                    }
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
        if (viewType == TYPE_BLANK){
            return new RecyclerView.ViewHolder(new CardView(mContext)){};
        }
        else if (viewType == TYPE_HEADER){
            return getHeaderHolder(parent);
        }
        else if (viewType == TYPE_LISTED_CONTENT){
            RecyclerView.ViewHolder holder = getListHolder(parent);
            if (holder instanceof ListViewHolder){
                mListHolder = (ListViewHolder)holder;
            }
            return holder;
        }
        else if (viewType == TYPE_DETAIL_CONTENT){
            return getDetailsHolder(parent);
        }
        else /*if (viewType == TYPE_LOAD)*/{
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View rootView = inflater.inflate(R.layout.item_material_progress, parent, false);
            return new RecyclerView.ViewHolder(rootView){};
        }
    }

    /**
     * Creates the default holder for the header card. The implementee is free to override this
     * method to set his own holder with the desired layout.
     *
     * @param parent the view group parent to the view that will be contained by the adapter.
     * @return an adapter to act as a header.
     */
    protected @NonNull RecyclerView.ViewHolder getHeaderHolder(ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View rootView = inflater.inflate(R.layout.card_material_header, parent, false);
        return new HeaderViewHolder(rootView);
    }

    /**
     * Creates the default holder for the list card. The implementee is free to override this
     * method to set his own holder with the desired layout.
     *
     * @param parent the view group parent to the view that will be contained by the adapter.
     * @return an adapter to (preferably) display a list of elements.
     */
    protected @NonNull RecyclerView.ViewHolder getListHolder(ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View rootView = inflater.inflate(R.layout.card_material_list, parent, false);
        return new ListViewHolder(mContext, rootView);
    }

    /**
     * Creates the default holder for the details card. The implementee is free to override this
     * method to set his own holder with the desired layout.
     *
     * @param parent the view group parent to the view that will be contained by the adapter.
     * @return the default details view holder.
     */
    protected @NonNull RecyclerView.ViewHolder getDetailsHolder(ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View rootView = inflater.inflate(R.layout.card_material_detail, parent, false);
        return new DetailViewHolder(rootView);
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder rawHolder, int position){
        //The items in this list are unique, none of them is displayed more than
        //  once, so the view type will do to determine which method to call
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
        else if (viewType == TYPE_HEADER){
            bindHeaderHolder(rawHolder);
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
                rawHolder.itemView.findViewById(R.id.material_progress_progress).setVisibility(View.GONE);
                TextView error = (TextView)rawHolder.itemView.findViewById(R.id.material_progress_error);
                error.setVisibility(View.VISIBLE);
                error.setText(mLoadingError);
            }
        }
    }

    /**
     * Called for the implementee to bind the header holder.
     *
     * @param rawHolder the header holder.
     */
    protected void bindHeaderHolder(RecyclerView.ViewHolder rawHolder){

    }

    /**
     * Called for the implementee to bind the list holder.
     *
     * @param rawHolder a generic reference to the holder returned
     *                  by {@code LibraryAdapter.getListHolder()}
     */
    protected void bindListHolder(RecyclerView.ViewHolder rawHolder){

    }

    /**
     * Called for the implementee to bind the detail holder.
     *
     * @param holder the detail holder.
     */
    protected void bindDetailHolder(DetailViewHolder holder){

    }

    /**
     * Called when the loading widget is reached.
     */
    protected void loadMore(){

    }

    /**
     * Lets the adapter know that a header item has been added.
     */
    protected final void notifyHeaderInserted(){
        notifyItemInserted(1);
    }

    /**
     * Lets the adapter know that the backing list is not empty any more and, therefore,
     * the view holder should be created and the view inserted in the recycler view.
     */
    protected final void notifyListInserted(){
        if (mContentType != ContentType.LIST){
            Log.e(TAG, "Can't insert a list in a non listing adapter");
        }
        else{
            insertContent();
        }
    }

    protected final void prepareListChange(){
        if (mContentType != ContentType.LIST){
            Log.e(TAG, "Can't update a list in a non listing adapter");
        }
        else{
            if (mListHolder == null){
                Log.e(TAG, "Can't update a non-existing default list");
            }
            else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                    ViewGroup target = (ViewGroup)mListHolder.mList.getRootView();
                    long duration = 3*mListHolder.mList.getItemAnimator().getRemoveDuration();
                    Transition transition = new ChangeBounds();
                    transition.setDuration(duration);
                    TransitionManager.beginDelayedTransition(target, transition);
                }
            }
        }
    }

    /**
     * Lets the adapter know that the default list holder's RecyclerView needs to change
     * bounds to accommodate item insertions or deletions.
     */
    protected final void notifyListChanged(){
        if (mContentType != ContentType.LIST){
            Log.e(TAG, "Can't update a list in a non listing adapter");
        }
        else{
            if (mListHolder == null){
                Log.e(TAG, "Can't update a non-existing default list");
            }
            else{
                mListHolder.mList.requestLayout();
            }
        }
    }

    /**
     * Lets the adapter know that a details item has been inserted.
     */
    protected final void notifyDetailsInserted(){
        if (mContentType != ContentType.DETAIL){
            Log.e(TAG, "Can't insert a detail in a non detail adapter");
        }
        else{
            insertContent();
        }
    }

    /**
     * Lets the adapter know that a content item has been inserted.
     */
    private void insertContent(){
        if (hasHeader()){
            notifyItemInserted(2);
        }
        else{
            notifyItemInserted(1);
        }
    }

    /**
     * Updates the state of the loading widget.
     *
     * @param showLoading whether the loading widget should be shown or updated.
     */
    protected void updateLoading(boolean showLoading){
        if (mShowLoading){
            //If we are no longer loading, remove the switch
            if (!showLoading){
                mShowLoading = false;
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
        else{
            if (showLoading){
                mShowLoading = true;
                notifyItemInserted(getItemCount()-1);
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


    /**
     * Enumeration to establish the kinds of adapters that there are.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public enum ContentType{
        LIST(TYPE_LISTED_CONTENT),
        DETAIL(TYPE_DETAIL_CONTENT);


        private int mType;


        /**
         * Constructor.
         *
         * @param type the type of adapter in an adapter-understandable way.
         */
        ContentType(int type){
            mType = type;
        }

        /**
         * Gets the type of adapter.
         *
         * @return the type of adapter in an adapter-understandable way.
         */
        private int getType(){
            return mType;
        }
    }


    /**
     * View holder for the header card.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    protected static class HeaderViewHolder extends RecyclerView.ViewHolder{
        private TextView mHeaderTitle;
        private TextView mHeaderContent;
        private TextView mButton;


        /**
         * Constructor.
         *
         * @param rootView the view to be drawn.
         */
        public HeaderViewHolder(@NonNull View rootView){
            super(rootView);

            mHeaderTitle = (TextView)rootView.findViewById(R.id.material_header_title);
            mHeaderContent = (TextView)rootView.findViewById(R.id.material_header_content);
            mButton = (TextView)rootView.findViewById(R.id.material_header_button);
        }

        /**
         * Binds a title to the holder.
         *
         * @param title the title to be displayed in the card.
         */
        public void setTitle(CharSequence title){
            mHeaderTitle.setText(title);
        }

        /**
         * Binds a content to the holder.
         *
         * @param content the description to be displayed in the card.
         */
        public void setContent(CharSequence content){
            mHeaderContent.setText(content);
        }

        /**
         * Sets up the button of the holder. Calling this method will make the button appear,
         * which is hidden by default.
         *
         * @param caption the caption of the button.
         * @param onClickListener the click listener for the button.
         */
        public void setButton(CharSequence caption, View.OnClickListener onClickListener){
            mButton.setText(caption);
            mButton.setVisibility(View.VISIBLE);
            mButton.setOnClickListener(onClickListener);
        }

        /**
         * Gets the id of the button.
         *
         * @return the id of the button.
         */
        public @IdRes int getButtonId(){
            return mButton.getId();
        }
    }


    /**
     * Default view holder for a listing card.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    protected static class ListViewHolder extends RecyclerView.ViewHolder{
        private TextView mHeader;
        private View mSeparator;
        private RecyclerView mList;


        /**
         * Constructor.
         *
         * @param context a reference to the context.
         * @param rootView the root view of the holder.
         */
        public ListViewHolder(@NonNull Context context, @NonNull View rootView){
            super(rootView);

            mHeader = (TextView)rootView.findViewById(R.id.material_list_header);
            mSeparator = rootView.findViewById(R.id.material_list_separator);
            mList = (RecyclerView)rootView.findViewById(R.id.material_list_list);
            mList.setLayoutManager(new LinearLayoutManager(context));
        }

        /**
         * Sets the background color of the header.
         *
         * @param color the color to be set as the background of the header.
         */
        public void setHeaderColor(int color){
            mHeader.setBackgroundColor(color);
            if (color != Color.WHITE){
                mSeparator.setVisibility(View.GONE);
            }
            else{
                mSeparator.setVisibility(View.VISIBLE);
            }
        }

        /**
         * Sets the title in the header.
         *
         * @param title the new title.
         */
        public void setTitle(@NonNull String title){
            mHeader.setText(title);
        }

        /**
         * Sets the color of the title in the header.
         *
         * @param color the new color.
         */
        public void setTitleColor(int color){
            mHeader.setTextColor(color);
        }

        /**
         * Sets a layout manager to the list. It defaults to a vertical linear layout manager.
         *
         * @param layoutManager the new layout manager.
         */
        public void setLayoutManager(RecyclerView.LayoutManager layoutManager){
            mList.setLayoutManager(layoutManager);
        }

        /**
         * Sets the adapter to the list.
         *
         * @param adapter the new adapter.
         */
        public void setAdapter(RecyclerView.Adapter adapter){
            mList.setAdapter(adapter);
        }
    }


    /**
     * Default view holder for the detail card.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
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

            mHeader = (TextView)rootView.findViewById(R.id.material_detail_header);
            mDescription = (TextView)rootView.findViewById(R.id.material_detail_description);
            mSeparator = rootView.findViewById(R.id.material_detail_separator);
            mMoreInfoTitle = (TextView)rootView.findViewById(R.id.material_detail_more_info_title);
            mMoreInfo = (TextView)rootView.findViewById(R.id.material_detail_more_info);
        }

        /**
         * Binds a color to the header title.
         *
         * @param color the color to be set as the background of the header.
         */
        public void setHeaderColor(int color){
            mHeader.setBackgroundColor(color);
        }

        /**
         * Binds a title to the card.
         *
         * @param title the title to be set in the header.
         */
        public void setTitle(String title){
            mHeader.setText(title);
        }

        /**
         * Binds a description to the card.
         *
         * @param description the description to be displayed.
         */
        public void setDescription(CharSequence description){
            mDescription.setVisibility(View.VISIBLE);
            if (mMoreInfoTitle.getVisibility() == View.VISIBLE){
                mSeparator.setVisibility(View.VISIBLE);
            }
            else{
                mSeparator.setVisibility(View.GONE);
            }
            mDescription.setText(description);
        }

        /**
         * Binds a more info section to the card. By default more info is hidden, but calling
         * this method will make it show.
         *
         * @param moreInfo the more info text to be displayed.
         */
        public void setMoreInfo(CharSequence moreInfo){
            if (mDescription.getVisibility() == View.VISIBLE){
                mSeparator.setVisibility(View.VISIBLE);
            }
            else{
                mSeparator.setVisibility(View.GONE);
            }
            mMoreInfoTitle.setVisibility(View.VISIBLE);
            mMoreInfo.setVisibility(View.VISIBLE);
            mMoreInfo.setText(moreInfo);
        }
    }
}
