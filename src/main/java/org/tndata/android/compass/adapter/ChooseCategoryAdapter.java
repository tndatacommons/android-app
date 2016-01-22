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
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.util.CompassUtil;
import org.tndata.android.compass.util.ImageHelper;

import java.util.List;


/**
 * Created by isma on 1/22/16.
 */
public class ChooseCategoryAdapter extends RecyclerView.Adapter<ChooseCategoryAdapter.CategoryViewHolder>{
    private Context mContext;
    private ChooseCategoryAdapterListener mListener;
    private List<Category> mCategories;

    private Bitmap[] mBitmaps;


    public ChooseCategoryAdapter(Context context, ChooseCategoryAdapterListener listener, List<Category> categories){
        mContext = context;
        mListener = listener;
        mCategories = categories;

        mBitmaps = new Bitmap[categories.size()];
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View rootView = inflater.inflate(R.layout.item_choose_category_category, parent, false);
        return new CategoryViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position){
        Category category = mCategories.get(position);
        Bitmap circle = mBitmaps[position];
        if (circle == null){
            int imageResId = CompassUtil.getCategoryTileResId(category.getTitle());
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), imageResId);
            circle = ImageHelper.getCircleBitmap(bitmap, CompassUtil.getPixels(mContext, 100));
            bitmap.recycle();
            mBitmaps[position] = circle;
        }

        holder.mImage.setImageBitmap(circle);
        holder.mCaption.setText(category.getTitle());
    }

    @Override
    public int getItemCount(){
        return mCategories.size();
    }

    protected class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView mImage;
        private TextView mCaption;

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

    public interface ChooseCategoryAdapterListener{
        void onCategorySelected(Category category);
    }
}
