package org.tndata.android.compass.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Rect;
import android.os.Bundle;
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


public class ChooseCategoriesFragment extends Fragment implements CategoryLoaderListener{
    public static final String RESTRICTIONS_KEY = "restrictions";

    private View mMaterialHeader;
    private ChooseCategoryAdapter mAdapter;
    private boolean mApplyRestrictions;

    private ChooseCategoryAdapter.OnCategoriesSelectedListener mCallback;
    private CompassApplication application;


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

        RecyclerView mGrid = (RecyclerView)root.findViewById(R.id.choose_categories_grid);
        mGrid.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        mAdapter = new ChooseCategoryAdapter(getActivity(), mCallback, mApplyRestrictions);
        mGrid.setAdapter(mAdapter);
        mGrid.addItemDecoration(new ItemPadding());
        mGrid.setOnScrollListener(new ParallaxEffect());

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
        new CategoryLoaderTask(this).execute(application.getToken());
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mCallback = null;
    }

    @Override
    public void categoryLoaderFinished(ArrayList<Category> categories){
        if (categories == null){
            Toast.makeText(getActivity(), R.string.choose_categories_error, Toast.LENGTH_SHORT).show();
        }
        else{
            mAdapter.setCategories(categories, application.getCategories());
        }
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
            params.topMargin = (int)((mPreviousMargin+topState)*0.75);
            mMaterialHeader.setLayoutParams(params);
        }
    }
}
