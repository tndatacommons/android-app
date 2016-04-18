package org.tndata.android.compass.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
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
import org.tndata.android.compass.adapter.ChooseInterestsAdapter;
import org.tndata.android.compass.model.CategoryContent;
import org.tndata.android.compass.model.UserCategory;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.CompassUtil;

import java.util.List;

import es.sandwatch.httprequests.HttpRequest;
import es.sandwatch.httprequests.HttpRequestError;


/**
 * A fragment containing a grid set to choose categories.
 *
 * @author Edited by Ismael Alonso
 * @version 2.0.0
 */
public class ChooseInterestsFragment
        extends Fragment
        implements
                HttpRequest.RequestCallback,
                Parser.ParserCallback,
                ChooseInterestsAdapter.OnCategoriesSelectedListener{

    public static final String ON_BOARDING_KEY = "org.tndata.compass.ChooseCategories.OnBoarding";


    private View mMaterialHeader;
    private ChooseInterestsAdapter mAdapter;
    private boolean mOnBoarding;

    private AlertDialog mDialog;

    private ChooseInterestsAdapter.OnCategoriesSelectedListener mCallback;
    private CompassApplication mApplication;

    //Request codes
    private int mGetUserCategoriesRC;


    /**
     * Creates a new instance of this fragment.
     *
     * @param onBoarding true if this was called from onboarding, false otherwise.
     * @return the new instance of the fragment.
     */
    public static ChooseInterestsFragment newInstance(boolean onBoarding){
        Bundle args = new Bundle();
        args.putBoolean(ChooseInterestsFragment.ON_BOARDING_KEY, onBoarding);
        ChooseInterestsFragment mFragment = new ChooseInterestsFragment();
        mFragment.setArguments(args);
        return mFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mOnBoarding = getArguments() != null && getArguments().getBoolean(ON_BOARDING_KEY, true);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        //This makes sure that the container activity has implemented the callback
        //  interface. If not, it throws an exception
        try{
            mCallback = (ChooseInterestsAdapter.OnCategoriesSelectedListener)context;
        }
        catch (ClassCastException ccx){
            throw new ClassCastException(context.toString()
                    + " must implement ChooseCategoriesFragmentListener");
        }
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_choose_categories, container, false);
    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState){
        mMaterialHeader = rootView.findViewById(R.id.choose_categories_material_header);
        ViewGroup.LayoutParams params = mMaterialHeader.getLayoutParams();
        params.height = CompassUtil.getScreenWidth(getActivity())*2/3;
        mMaterialHeader.setLayoutParams(params);

        RecyclerView grid = (RecyclerView)rootView.findViewById(R.id.choose_categories_grid);
        grid.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        mAdapter = new ChooseInterestsAdapter(getActivity(), this, mOnBoarding);
        grid.setAdapter(mAdapter);
        grid.addItemDecoration(new ItemPadding());
        grid.addOnScrollListener(new ParallaxEffect());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        mApplication = (CompassApplication)getActivity().getApplication();
        loadCategories();
    }

    /**
     * Starts the category load process.
     */
    private void loadCategories(){
        mGetUserCategoriesRC = HttpRequest.get(this, API.getUserCategoriesUrl());
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mGetUserCategoriesRC){
            Parser.parse(result, ParserModels.UserCategoryResultSet.class, this);
        }
    }

    @Override
    public void onRequestFailed(int requestCode, HttpRequestError error){
        if (requestCode == mGetUserCategoriesRC){
            notifyError(R.string.choose_categories_error);
        }
    }

    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){
        if (result instanceof ParserModels.UserCategoryResultSet){
            List<UserCategory> categories = ((ParserModels.UserCategoryResultSet)result).results;
            if (categories != null){
                mAdapter.setCategories(mApplication.getFilteredCategoryList(), categories);
            }
        }
    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Hides the progress bar and toasts an error.
     *
     * @param resId the resource id of the string.
     */
    private void notifyError(@StringRes int resId){
        //mAdapter.hideProgressBar();
        Toast.makeText(getActivity(), resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCategoriesSelected(List<CategoryContent> selection, List<UserCategory> original){
        mDialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.choose_categories_syncing_title)
                .setCancelable(false)
                .setView(getActivity().getLayoutInflater().inflate(R.layout.dialog_syncing, null))
                .create();
        mDialog.show();
        mCallback.onCategoriesSelected(selection, original);
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
     * //TODO kill this and use the other one.
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
