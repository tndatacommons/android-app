package org.tndata.android.compass.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.ChooseCategoryAdapter;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.task.CategoryLoaderTask;
import org.tndata.android.compass.task.CategoryLoaderTask.CategoryLoaderListener;

import java.util.ArrayList;


public class ChooseCategoriesFragment
        extends Fragment
        implements
                CategoryLoaderListener,
                ChooseCategoryAdapter.ChooseCategoryAdapterListener{

    public final static int MIN_CATEGORIES_REQUIRED = 4;
    private ChooseCategoryAdapter mAdapter;
    private ArrayList<Category> mItems;

    private View mMaterialHeader;
    private RecyclerView mGrid;


    private GridView mGridView;
    private ProgressBar mProgressBar;
    private TextView mErrorTextView;
    private Button mNextButton;
    private ArrayList<Category> mSelectedItems = new ArrayList<Category>();
    private ChooseCategoriesFragmentListener mCallback;
    private CompassApplication application;

    public interface ChooseCategoriesFragmentListener {
        public void categoriesSelected(ArrayList<Category> categories);
    }

    private OnItemClickListener mClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view,
                                final int position, long id) {
            Animation animation = AnimationUtils.loadAnimation(getActivity()
                    .getApplicationContext(), R.anim.anim_tile_click);
            final ImageView check = null;/*(ImageView) view
                    .findViewById(R.id.list_item_category_grid_category_imageview);*/

            animation.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Category category = mItems.get(position);
                    if (mSelectedItems.contains(category)) {
                        mSelectedItems.remove(category);
                        check.setVisibility(View.GONE);
                        if (mSelectedItems.size() < MIN_CATEGORIES_REQUIRED) {
                            mNextButton.setEnabled(false);
                        }
                    } else {
                        mSelectedItems.add(category);
                        check.setVisibility(View.VISIBLE);
                        if (mSelectedItems.size() >= MIN_CATEGORIES_REQUIRED) {
                            mNextButton.setEnabled(true);
                            mNextButton.setVisibility(View.VISIBLE);
                        } else {
                            mNextButton.setEnabled(false);
                        }
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            view.startAnimation(animation);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View root = inflater.inflate(R.layout.fragment_choose_categories, container, false);

        mMaterialHeader = root.findViewById(R.id.choose_categories_material_header);

        mGrid = (RecyclerView)root.findViewById(R.id.choose_categories_grid);
        mGrid.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        mAdapter = new ChooseCategoryAdapter(getActivity(), this);
        mGrid.setAdapter(mAdapter);
        mGrid.addItemDecoration(new ItemPadding());
        mGrid.setOnScrollListener(new ParallaxEffect());

        /*mGridView = (GridView) root
                .findViewById(R.id.choose_categories_gridview);*/
        mProgressBar = (ProgressBar) root
                .findViewById(R.id.choose_categories_load_progress);
        mErrorTextView = (TextView) root
                .findViewById(R.id.choose_categories_error_textview);
        mNextButton = (Button) root
                .findViewById(R.id.choose_categories_done_button);
        /*mNextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedItems.size() >= MIN_CATEGORIES_REQUIRED) {
                    mCallback.categoriesSelected(mSelectedItems);
                }
            }
        });
        mNextButton.setEnabled(false);*/ // Start with a disabled button
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        application = (CompassApplication) getActivity().getApplication();

        mItems = new ArrayList<Category>();
        if (!application.getCategories().isEmpty()) {
            mSelectedItems.addAll(application.getCategories());
        }
        if (mSelectedItems.size() >= MIN_CATEGORIES_REQUIRED) {
            /*mNextButton.setVisibility(View.VISIBLE);
            mNextButton.setEnabled(true);*/
        }


        /*mAdapter = new ChooseCategoryAdapter(getActivity(),
                R.id.list_item_category_grid_category_textview, mItems, this);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(mClickListener);*/

        loadCategories();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity); // This makes sure that the container activity
        // has implemented the callback interface. If not, it throws an
        // exception
        try {
            mCallback = (ChooseCategoriesFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ChooseCategoriesFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    private void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
        //mGridView.setVisibility(View.GONE);
        mErrorTextView.setVisibility(View.GONE);
    }

    private void showCategories() {
        mProgressBar.setVisibility(View.GONE);
        //mGridView.setVisibility(View.VISIBLE);
        mErrorTextView.setVisibility(View.GONE);
    }

    private void showError() {
        mProgressBar.setVisibility(View.GONE);
        //mGridView.setVisibility(View.GONE);
        mErrorTextView.setVisibility(View.VISIBLE);
    }

    private void loadCategories() {
        showProgress();
        new CategoryLoaderTask(this).executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR, application.getToken());
    }

    @Override
    public void categoryLoaderFinished(ArrayList<Category> categories) {
        if (categories != null) {
            mAdapter.setCategories(categories, application.getCategories());
        }

        if (!mItems.isEmpty()) {
            showCategories();
        } else {
            showError();
        }
    }

    @Override
    public ArrayList<Category> getCurrentlySelectedCategories() {
        return mSelectedItems;
    }


    final class ItemPadding extends RecyclerView.ItemDecoration{
        private int padding;


        public ItemPadding(){
            padding = (int)Math.ceil(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    8, getActivity().getResources().getDisplayMetrics()));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state){

            int position = parent.getChildLayoutPosition(view);

            //If the header, only bottom padding
            if (position == 0){
                outRect.bottom = padding / 2;
            }
            //If to the left, full left padding
            else if (position%2 == 1){
                outRect.top = padding / 2;
                outRect.left = padding;
                outRect.bottom = padding / 2;
                outRect.right = padding / 2;
            }
            //If to the right, full right padding
            else if (position%2 == 0){
                outRect.top = padding / 2;
                outRect.left = padding / 2;
                outRect.bottom = padding / 2;
                outRect.right = padding;
            }
        }
    }


    /**
     * Creates a parallax effect on the material header view.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    final class ParallaxEffect extends RecyclerView.OnScrollListener{
        //The combined height of the rows that are off-screen. NOTE: This number will either
        //  be 0 or negative because the origin of coordinates if the top left corner.
        private int mPreviousMargin;

        //Header and category item heights
        private int mHeaderHeight;
        private int mCategoryHeight;

        //Current item.
        private int mCurrent;


        /**
         * Constructor.
         */
        private ParallaxEffect(){
            mPreviousMargin = 0;

            mCurrent = -1;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy){
            //If this is the first call
            if (mCurrent == -1){
                //Retrieve the two item heights for later calculations
                mHeaderHeight = recyclerView.findViewHolderForLayoutPosition(0).itemView.getHeight();
                mCategoryHeight = recyclerView.findViewHolderForLayoutPosition(1).itemView.getHeight();
                mCurrent = 0;
            }

            //While there are views above the current
            while (mCurrent > 0 && recyclerView.findViewHolderForLayoutPosition(mCurrent-1) != null){
                mCurrent -= 2;
                //Update the margin
                if (mCurrent > 0){
                    mPreviousMargin += mCategoryHeight;
                }
                else{
                    mPreviousMargin += mHeaderHeight;
                }
            }

            //While there are not views below the current
            while (recyclerView.findViewHolderForLayoutPosition(mCurrent) == null){
                //Update the margin
                if (mCurrent == 0){
                    mPreviousMargin -= mHeaderHeight;
                }
                else{
                    mPreviousMargin -= mCategoryHeight;
                }
                mCurrent += 2;
            }

            //Retrieve the margin state of the current view
            int topState = recyclerView.findViewHolderForLayoutPosition(mCurrent).itemView.getTop();

            //Update the parameters of the material header
            RelativeLayout.LayoutParams params;
            params = (RelativeLayout.LayoutParams)mMaterialHeader.getLayoutParams();
            params.topMargin = (int)((mPreviousMargin+topState)*0.75);
            mMaterialHeader.setLayoutParams(params);
        }
    }
}
