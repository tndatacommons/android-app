package org.tndata.android.compass.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Category;

import java.util.List;


/**
 * Created by isma on 1/22/16.
 */
public class ChooseCategoryAdapter extends RecyclerView.Adapter<ChooseCategoryAdapter.CategoryViewHolder>{
    private Context mContext;
    private List<Category> mCategories;


    public ChooseCategoryAdapter(Context context, List<Category> categories){
        mContext = context;
        mCategories = categories;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View rootView = inflater.inflate(R.layout.item_choose_category_category, parent, false);
        return new CategoryViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position){
        holder.mCaption.setText(mCategories.get(position).getTitle());
    }

    @Override
    public int getItemCount(){
        return mCategories.size();
    }

    protected class CategoryViewHolder extends RecyclerView.ViewHolder{
        private TextView mCaption;

        public CategoryViewHolder(View itemView){
            super(itemView);

            mCaption = (TextView)itemView.findViewById(R.id.choose_category_category_caption);
        }
    }
}
