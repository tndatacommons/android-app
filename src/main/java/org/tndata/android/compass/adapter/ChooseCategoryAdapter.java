package org.tndata.android.compass.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.CategoryContent;
import org.tndata.android.compass.util.CompassUtil;
import org.tndata.android.compass.util.ImageHelper;

import java.util.List;


/**
 * Adapter for the category chooser.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class ChooseCategoryAdapter extends RecyclerView.Adapter<ChooseCategoryAdapter.CategoryViewHolder>{
    private Context mContext;
    private ChooseCategoryAdapterListener mListener;
    private List<CategoryContent> mCategories;

    private Bitmap[] mBitmaps;


    /**
     * Constructor.
     * @param context a reference to the context.
     * @param listener the listener.
     * @param categories the list of categories to choose from.
     */
    public ChooseCategoryAdapter(Context context, ChooseCategoryAdapterListener listener, List<CategoryContent> categories){
        mContext = context;
        mListener = listener;
        mCategories = categories;

        mBitmaps = new Bitmap[categories.size()];
        for (int i = 0; i < mCategories.size(); i++){
            CategoryContent category = mCategories.get(i);
            int imageResId = CompassUtil.getCategoryTileResId(category.getTitle());
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), imageResId);
            mBitmaps[i] = ImageHelper.getCircleBitmap(bitmap, CompassUtil.getPixels(mContext, 100));
            bitmap.recycle();
        }
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View rootView = inflater.inflate(R.layout.item_choose_category_category, parent, false);
        return new CategoryViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position){
        holder.mImage.setImageBitmap(mBitmaps[position]);
        holder.mCaption.setText(mCategories.get(position).getTitle());
    }

    @Override
    public int getItemCount(){
        return mCategories.size();
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

            mImage = (ImageView)rootView.findViewById(R.id.choose_category_category_image);
            mCaption = (TextView)rootView.findViewById(R.id.choose_category_category_caption);

            rootView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v){
            mListener.onCategorySelected(mCategories.get(getAdapterPosition()));
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
        void onCategorySelected(CategoryContent category);
    }
}
