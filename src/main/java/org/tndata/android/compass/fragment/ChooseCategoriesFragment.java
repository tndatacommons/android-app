package org.tndata.android.compass.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.ChooseCategoryAdapter;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.task.CategoryLoaderTask;
import org.tndata.android.compass.task.CategoryLoaderTask.CategoryLoaderListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A fragment containing a grid set to choose categories.
 *
 * @author Edited by Ismael Alonso
 * @version 2.0.0
 */
public class ChooseCategoriesFragment
        extends Fragment
        implements
                CategoryLoaderListener,
                ChooseCategoryAdapter.OnCategoriesSelectedListener{

    public static final String RESTRICTIONS_KEY = "restrictions";

    //10 seconds worth in milliseconds
    private static final int FETCH_TIMEOUT = 10*1000;

    private View mMaterialHeader;
    private ChooseCategoryAdapter mAdapter;
    private boolean mApplyRestrictions;

    private AlertDialog mDialog;

    private ChooseCategoryAdapter.OnCategoriesSelectedListener mCallback;
    private CompassApplication application;

    private boolean mFetchingCategories;


    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        //This makes sure that the container activity has implemented the callback
        //  interface. If not, it throws an exception

        try{
            mCallback = (ChooseCategoryAdapter.OnCategoriesSelectedListener)activity;
        }
        catch (ClassCastException e){
            throw new ClassCastException(activity.toString()
                    + " must implement ChooseCategoriesFragmentListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mApplyRestrictions = getArguments() == null || getArguments().getBoolean(RESTRICTIONS_KEY, true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View root = inflater.inflate(R.layout.fragment_choose_categories, container, false);

        mMaterialHeader = root.findViewById(R.id.choose_categories_material_header);

        RecyclerView grid = (RecyclerView)root.findViewById(R.id.choose_categories_grid);
        grid.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        mAdapter = new ChooseCategoryAdapter(getActivity(), this, mApplyRestrictions);
        grid.setAdapter(mAdapter);
        grid.addItemDecoration(new ItemPadding());
        grid.setOnScrollListener(new ParallaxEffect());

        mFetchingCategories = false;

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        application = (CompassApplication) getActivity().getApplication();

        loadCategories();
    }

    /**
     * Starts the category load process.
     */
    private void loadCategories(){
        mFetchingCategories = true;
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                if (mFetchingCategories){
                    notifyError(R.string.choose_categories_error);
                    mFetchingCategories = false;
                }
            }
        }, FETCH_TIMEOUT);

        new CategoryLoaderTask(this).execute(application.getToken());
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mCallback = null;
        if (mDialog != null){
            mDialog.dismiss();
        }
    }

    @Override
    public void categoryLoaderFinished(ArrayList<Category> categories){
        if (mFetchingCategories){
            if (categories == null){
                notifyError(R.string.choose_categories_error);
            }
            else{
                mAdapter.setCategories(categories, application.getCategories());
            }
            mFetchingCategories = false;
        }
    }

    /**
     * Toasts the string with the provided id and hides the progress bar in the header.
     *
     * @param resId the resource id of the error string.
     */
    private void notifyError(@StringRes int resId){
        mAdapter.hideProgressBar();
        Toast.makeText(getActivity(), resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCategoriesSelected(List<Category> selection){
        mDialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.choose_categories_syncing_title)
                .setCancelable(false)
                .setView(getActivity().getLayoutInflater().inflate(R.layout.dialog_syncing, null))
                .create();
        mDialog.show();
        mCallback.onCategoriesSelected(selection);
    }


    /**
     * Decoration class to establish the grid's items margin.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    final class ItemPadding extends RecyclerView.ItemDecoration{
        private int margin;


        /**
         * Constructor.
         */
        public ItemPadding(){
            margin = (int)Math.ceil(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    8, getActivity().getResources().getDisplayMetrics()));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state){

            int position = parent.getChildLayoutPosition(view);

            //If the header, only bottom margin
            if (position == 0){
                outRect.bottom = margin / 2;
            }
            //If to the left, full left margin
            else if (position%2 == 1){
                outRect.top = margin / 2;
                outRect.left = margin;
                outRect.bottom = margin / 2;
                outRect.right = margin / 2;
            }
            //If to the right, full right margin
            else if (position%2 == 0){
                outRect.top = margin / 2;
                outRect.left = margin / 2;
                outRect.bottom = margin / 2;
                outRect.right = margin;
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
            params.topMargin = (int)((mPreviousMargin+topState)*0.5);
            mMaterialHeader.setLayoutParams(params);
        }
    }
}
