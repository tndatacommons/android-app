package org.tndata.android.compass.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.TDCCategory;
import org.tndata.android.compass.util.ImageLoader;

import java.util.ArrayList;
import java.util.List;


/**
 * Adapter for the category chooser.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class ChooseCategoryAdapter extends RecyclerView.Adapter{
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_CATEGORY = 2;


    private Context mContext;
    private ChooseCategoryAdapterListener mListener;

    private List<List<TDCCategory>> mCategoryLists;
    private int mGroupCount;
    private int mDisplayedCategories;


    /**
     * Constructor.
     * @param context a reference to the context.
     * @param listener the listener.
     * @param categories the sorted list of categories to choose from.
     */
    public ChooseCategoryAdapter(Context context, ChooseCategoryAdapterListener listener,
                                 List<TDCCategory> categories){
        mContext = context;
        mListener = listener;

        Log.d("ChooseCategories", categories.toString());

        //Init counts and lists
        mGroupCount = 0;
        mDisplayedCategories = categories.size();
        mCategoryLists = new ArrayList<>();
        //This is the only way to ensure the compiler doesn't complain about possible
        //  missing initialization, I am aware it adds overhead
        List<TDCCategory> currentList = new ArrayList<>();
        //Populate the Lists and calculate the group number
        for (int i = 0, currentGroup = -6; i < categories.size(); i++){
            TDCCategory category = categories.get(i);
            if (currentGroup != category.getGroup()){
                mGroupCount++;
                currentList = new ArrayList<>();
                mCategoryLists.add(currentList);
                currentGroup = category.getGroup();
            }
            currentList.add(category);
        }
    }

    @Override
    public int getItemCount(){
        return mGroupCount+mDisplayedCategories;
    }

    /**
     * Gets the display size of a particular group.
     *
     * @param group the group whose display size is to be calculated.
     * @return the display size of the group.
     */
    private int getDisplaySize(int group){
        //If the provided group is out of bounds, return an arbitrarily large number for
        //  the loop in getRelativePosition() to break the condition
        if (group >= mGroupCount){
            return 9999;
        }
        return mCategoryLists.get(group).size()+1;
    }

    /**
     * Gets a group/position tuple for a particular recycler view position.
     *
     * @param position the position of the item in the recycler view.
     * @return the requested group/position tuple.
     */
    private GroupPositionTuple getGroupPositionTuple(int position){
        int group = 0, displaySize = getDisplaySize(group);
        while (position - displaySize >= 0){
            position -= displaySize;
            group++;
            displaySize = getDisplaySize(group);
        }
        return new GroupPositionTuple(group, position);
    }

    @Override
    public int getItemViewType(int position){
        GroupPositionTuple tuple = getGroupPositionTuple(position);
        if (tuple.mPosition == 0){
            return TYPE_HEADER;
        }
        return TYPE_CATEGORY;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (viewType == TYPE_HEADER){
            View rootView = inflater.inflate(R.layout.item_section_header, parent, false);
            return new HeaderHolder(rootView);
        }
        else{
            View rootView = inflater.inflate(R.layout.card_category, parent, false);
            return new CategoryViewHolder(rootView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, int position){
        GroupPositionTuple tuple = getGroupPositionTuple(position);
        Log.d("Tuple", tuple.toString());
        //Header
        if (tuple.mPosition == 0){
            HeaderHolder holder = (HeaderHolder)rawHolder;
            holder.bind(mCategoryLists.get(tuple.mGroup).get(0).getGroupName());
        }
        //Category
        else{
            CategoryViewHolder holder = (CategoryViewHolder)rawHolder;
            holder.bind(mCategoryLists.get(tuple.mGroup).get(tuple.mPosition-1));
        }
    }


    /**
     * A class representing an group/position index tuple.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private class GroupPositionTuple{
        private final int mGroup;
        private final int mPosition;


        /**
         * Constructor.
         *
         * @param group the group.
         * @param position the position.
         */
        private GroupPositionTuple(int group, int position){
            mGroup = group;
            mPosition = position;
        }

        @Override
        public String toString(){
            return "Group: " + mGroup + ", position:" + mPosition;
        }
    }


    /**
     * View holder for a category.
     *
     * @author Ismael Alonso.
     * @version 1.0.0
     */
    protected class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TDCCategory mCategory;

        private ImageView mImage;
        private TextView mCaption;


        /**
         * Constructor.
         *
         * @param rootView the view associated with this holder.
         */
        public CategoryViewHolder(View rootView){
            super(rootView);

            mImage = (ImageView)rootView.findViewById(R.id.category_image);
            mCaption = (TextView)rootView.findViewById(R.id.choose_category_category_caption);

            rootView.setOnClickListener(this);
        }

        /**
         * Binds a category to this holder.
         *
         * @param category the category to be bound.
         */
        public void bind(TDCCategory category){
            mCategory = category;

            ImageLoader.loadBitmap(mImage, category.getIconUrl());
            mCaption.setText(category.getTitle());
        }

        @Override
        public void onClick(View v){
            mListener.onCategorySelected(mCategory);
        }
    }


    /**
     * View Holder for a group title header.
     *
     * @author Ismael Alonso
     * @version 1.1.0
     */
    protected class HeaderHolder extends RecyclerView.ViewHolder{
        private TextView mHeader;


        /**
         * Creates a holder for a particular view.
         *
         * @param rootView the view associated with this holder.
         */
        public HeaderHolder(View rootView){
            super(rootView);

            mHeader = (TextView)rootView.findViewById(R.id.header_text);
        }

        /**
         * Binds a group name to this header.
         *
         * @param groupName the name of the group to be set as the header.
         */
        private void bind(String groupName){
            mHeader.setText(groupName);
        }
    }


    /**
     * Listener interface for the category adapter.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface ChooseCategoryAdapterListener{
        /**
         * Called when the user selects a category.
         *
         * @param category the selected category.
         */
        void onCategorySelected(TDCCategory category);
    }
}
