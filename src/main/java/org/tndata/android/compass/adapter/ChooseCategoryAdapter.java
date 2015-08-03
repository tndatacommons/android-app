package org.tndata.android.compass.adapter;

import java.util.ArrayList;
import java.util.List;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Category;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


public class ChooseCategoryAdapter extends RecyclerView.Adapter{
    private static final int VIEW_TYPE_HEADER = 1;
    private static final int VIEW_TYPE_CATEGORY = 2;
    private static final int VIEW_TYPE_NEXT = 3;


    private final Context mContext;
    private final ChooseCategoryAdapterListener mCallback;

    private List<Category> mCategories;
    private List<Category> mSelectedCategories;

    private int mLastAnimatedPosition;
    private long mLastAnimationScheduleTime;


    public ChooseCategoryAdapter(Context context, ChooseCategoryAdapterListener callback){
        mContext = context;
        mCallback = callback;

        mCategories = null;

        mLastAnimatedPosition = -1;
    }

    /**
     * Sets the dataset of the adapter.
     *
     * @param all the list of all available categories.
     */
    public void setCategories(@NonNull List<Category> all, @NonNull List<Category> selected){
        //Let the GC take care of the previous list and fill a new one
        mCategories = new ArrayList<>();
        mCategories.addAll(all);

        mSelectedCategories = new ArrayList<>();
        mSelectedCategories.addAll(selected);

        notifyDataSetChanged();
    }

    /**
     * Returns the Category at the specified position.
     *
     * @param position the position of the category in the dataset.
     * @return the Category in the requested position.
     */
    public Category getItem(int position){
        return mCategories.get(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (viewType == VIEW_TYPE_HEADER){
            View root = inflater.inflate(R.layout.item_choose_categories_header, parent, false);
            return new HeaderViewHolder(root);
        }
        else if (viewType == VIEW_TYPE_NEXT){
            View root = inflater.inflate(R.layout.item_choose_categories_next, parent, false);
            return new NextViewHolder(root);
        }
        else if (viewType == VIEW_TYPE_CATEGORY){
            View root = inflater.inflate(R.layout.item_choose_categories_category, parent, false);
            return new CategoryViewHolder(root);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, int position){
        if (getItemViewType(position) == VIEW_TYPE_HEADER){
            HeaderViewHolder holder = (HeaderViewHolder)rawHolder;
            StaggeredGridLayoutManager.LayoutParams params;
            params = (StaggeredGridLayoutManager.LayoutParams)holder.itemView.getLayoutParams();
            params.setFullSpan(true);

            if (mCategories != null){
                holder.progressBar.setVisibility(View.GONE);
            }
        }
        else if (getItemViewType(position) == VIEW_TYPE_NEXT){
            NextViewHolder holder = (NextViewHolder)rawHolder;
            StaggeredGridLayoutManager.LayoutParams params;
            params = (StaggeredGridLayoutManager.LayoutParams)holder.itemView.getLayoutParams();
            params.setFullSpan(true);
        }
        else if (getItemViewType(position) == VIEW_TYPE_CATEGORY){
            CategoryViewHolder holder = (CategoryViewHolder)rawHolder;
            if (!mSelectedCategories.contains(getItem(position-1))){
                holder.mOverlay.setVisibility(View.VISIBLE);
            }
            else{
                holder.mOverlay.setVisibility(View.GONE);
            }
            holder.mCaption.setText(getItem(position-1).getTitle());
            setAnimation(holder.itemView, position);
        }
    }

    /**
     * Sets an in-animation to an item if it hasn't been animated already. If there are
     * scheduled animations, it sets it after the last one.
     *
     * @param view the view to animate in.
     * @param position the position of the view.
     */
    private void setAnimation(View view, int position){
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > mLastAnimatedPosition){
            if (position == 1){
                mLastAnimationScheduleTime = System.currentTimeMillis()-200;
            }

            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.category_in);
            if (mLastAnimationScheduleTime <= System.currentTimeMillis()){
                animation.setStartOffset(200);
                mLastAnimationScheduleTime = System.currentTimeMillis()+200;
            }
            else{
                int offset = (int)(mLastAnimationScheduleTime-System.currentTimeMillis())+200;
                animation.setStartOffset(offset);
                mLastAnimationScheduleTime = System.currentTimeMillis()+offset;
            }
            view.startAnimation(animation);
            mLastAnimatedPosition = position;
        }
    }

    @Override
    public int getItemCount(){
        //If the categories haven't been set, yet, display the header, otherwise, display
        //  the header, the categories, and the next button.
        return mCategories == null ? 1 : mCategories.size()+2;
    }

    @Override
    public int getItemViewType(int position){
        if (position == 0){
            return VIEW_TYPE_HEADER;
        }
        else if (position == getItemCount()-1){
            return VIEW_TYPE_NEXT;
        }
        else{
            return VIEW_TYPE_CATEGORY;
        }
    }


    /**
     * The grid's header ViewHolder.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    class HeaderViewHolder extends RecyclerView.ViewHolder{
        private ProgressBar progressBar;

        /**
         * Constructor.
         *
         * @param itemView the root view of the cell.
         */
        public HeaderViewHolder(View itemView){
            super(itemView);

            progressBar = (ProgressBar)itemView.findViewById(R.id.choose_categories_header_progress_bar);
        }
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, Animation.AnimationListener{
        private ImageView mBackground;
        private View mOverlay;
        private TextView mCaption;


        public CategoryViewHolder(View itemView){
            super(itemView);

            //UI components
            mBackground = (ImageView)itemView.findViewById(R.id.choose_categories_category_background);
            mOverlay = itemView.findViewById(R.id.choose_categories_category_overlay);
            mCaption = (TextView)itemView.findViewById(R.id.choose_categories_category_caption);

            //Listeners
            itemView.setOnClickListener(this);
            itemView.findViewById(R.id.choose_categories_category_more_info).setOnClickListener(this);
        }

        @Override
        public void onClick(final View view){
            if (view == itemView){
                AlphaAnimation animation;
                Category category = getItem(getAdapterPosition()-1);
                if (mSelectedCategories.contains(category)){
                    Log.d("CategoryAdapter", "deselecting: " + category.getTitle());
                    mSelectedCategories.remove(category);
                    animation = new AlphaAnimation(0, 1);
                    animation.setDuration(200);
                    animation.setAnimationListener(this);
                    mOverlay.startAnimation(animation);
                }
                else{
                    mSelectedCategories.add(category);
                    animation = new AlphaAnimation(1, 0);
                    animation.setDuration(200);
                    animation.setAnimationListener(this);
                    mOverlay.startAnimation(animation);
                }
            }
            else{

            }
        }

        @Override
        public void onAnimationStart(Animation animation){
            mOverlay.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animation animation){
            if (mSelectedCategories.contains(getItem(getAdapterPosition()-1))){
                mOverlay.setVisibility(View.GONE);
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation){
            //Unused
        }
    }

    class NextViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public NextViewHolder(View itemView){
            super(itemView);
        }

        @Override
        public void onClick(View v){

        }
    }

    public interface ChooseCategoryAdapterListener{
        ArrayList<Category> getCurrentlySelectedCategories();
    }

    public interface OnCategoriesSelectedListener{
        void onCategoriesSelected(List<Category> selection);
    }
}
