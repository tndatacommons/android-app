package org.tndata.android.compass.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.TDCCategory;
import org.tndata.android.compass.util.CompassUtil;
import org.tndata.android.compass.util.ImageHelper;

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
    private List<TDCCategory> mFeatured;
    private List<TDCCategory> mCategories;

    private Bitmap[] mBitmaps;


    /**
     * Constructor.
     * @param context a reference to the context.
     * @param listener the listener.
     * @param categories the list of categories to choose from.
     */
    public ChooseCategoryAdapter(Context context, ChooseCategoryAdapterListener listener,
                                 List<TDCCategory> categories){
        mContext = context;
        mListener = listener;
        mFeatured = new ArrayList<>();
        mCategories = new ArrayList<>();

        mBitmaps = new Bitmap[categories.size()];
        for (int i = 0; i < categories.size(); i++){
            TDCCategory category = categories.get(i);
            int imageResId = CompassUtil.getCategoryTileResId(category.getTitle());
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), imageResId);
            mBitmaps[i] = ImageHelper.getCircleBitmap(bitmap, CompassUtil.getPixels(mContext, 100));
            bitmap.recycle();
            if (category.isFeatured()){
                mFeatured.add(category);
            }
            else{
                mCategories.add(category);
            }
        }

        Log.d("ChooseCategoryAdapter", "Total: " + categories.size() + ", Featured: " + mFeatured.size());
    }

    @Override
    public int getItemCount(){
        int featuredCount = mFeatured.isEmpty() ? 0 : mFeatured.size()+1;
        int categoryCount = mCategories.isEmpty() ? 0 : mCategories.size()+1;
        return featuredCount + categoryCount;
    }

    @Override
    public int getItemViewType(int position){
        if (position == 0){
            return TYPE_HEADER;
        }
        if (mFeatured.isEmpty()){
            return TYPE_CATEGORY;
        }
        if (position == mFeatured.size()+1){
            return TYPE_HEADER;
        }
        return TYPE_CATEGORY;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (viewType == TYPE_HEADER){
            View rootView = inflater.inflate(R.layout.item_header, parent, false);
            return new HeaderHolder(rootView);
        }
        else{
            View rootView = inflater.inflate(R.layout.card_category, parent, false);
            return new CategoryViewHolder(rootView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, int position){
        if (getItemViewType(position) == TYPE_HEADER){
            HeaderHolder holder = (HeaderHolder)rawHolder;
            if (position == 0){
                if (mFeatured.isEmpty()){
                    holder.bind("Categories");
                }
                else{
                    holder.bind("Featured content");
                }
            }
            else{
                holder.bind("Categories");
            }
        }
        else{
            CategoryViewHolder holder = (CategoryViewHolder)rawHolder;
            position--;
            if (position < mFeatured.size()){
                holder.mCaption.setText(mFeatured.get(position).getTitle());
            }
            else{
                if (!mFeatured.isEmpty()){
                    position--;
                }
                int categoryPosition = position-mFeatured.size();
                holder.mCaption.setText(mCategories.get(categoryPosition).getTitle());
            }
            holder.mImage.setImageBitmap(mBitmaps[position]);
        }
    }


    /**
     * View holder for a category.
     *
     * @author Ismael Alonso.
     * @version 1.0.0
     */
    protected class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView mImage;
        private TextView mCaption;


        /**
         * Constructor.
         *
         * @param rootView the root view of this holder.
         */
        public CategoryViewHolder(View rootView){
            super(rootView);

            mImage = (ImageView)rootView.findViewById(R.id.category_image);
            mCaption = (TextView)rootView.findViewById(R.id.choose_category_category_caption);

            rootView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v){
            //Compensate for the first header
            int position = getAdapterPosition()-1;
            if (position < mFeatured.size()){
                mListener.onCategorySelected(mFeatured.get(position));
            }
            else{
                if (mFeatured.isEmpty()){
                    mListener.onCategorySelected(mCategories.get(position));
                }
                else{
                    position -= mFeatured.size();
                    mListener.onCategorySelected(mCategories.get(position-1));
                }
            }
        }
    }


    /**
     * View Holder for a group title header.
     *
     * @author Ismael Alonso
     * @version 1.1.0
     */
    protected class HeaderHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mHeader;


        /**
         * Creates a holder for a particular view.
         *
         * @param rootView the view associated with this holder.
         */
        public HeaderHolder(View rootView){
            super(rootView);

            mHeader = (TextView)rootView.findViewById(R.id.header_text);
            rootView.setOnClickListener(this);
        }

        /**
         * Binds a group name to this header.
         *
         * @param groupName the name of the group to be set as the header.
         */
        private void bind(String groupName){
            mHeader.setText(groupName);
        }

        @Override
        public void onClick(View v){

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
