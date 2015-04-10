package org.tndata.android.grow.fragment;

import java.util.ArrayList;

import org.tndata.android.grow.GrowApplication;
import org.tndata.android.grow.R;
import org.tndata.android.grow.adapter.ChooseCategoryAdapter;
import org.tndata.android.grow.model.Category;
import org.tndata.android.grow.task.CategoryLoaderTask;
import org.tndata.android.grow.task.CategoryLoaderTask.CategoryLoaderListener;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ChooseCategoriesFragment extends Fragment implements
        CategoryLoaderListener, ChooseCategoryAdapter.ChooseCategoryAdapterListener {
    public final static int MIN_CATEGORIES_REQUIRED = 1;
    private ChooseCategoryAdapter mAdapter;
    private ArrayList<Category> mItems;
    private GridView mGridView;
    private ProgressBar mProgressBar;
    private TextView mErrorTextView;
    private Button mNextButton;
    private ArrayList<Category> mSelectedItems = new ArrayList<Category>();
    private ChooseCategoriesFragmentListener mCallback;

    public interface ChooseCategoriesFragmentListener {
        public void categoriesSelected(ArrayList<Category> categories);
    }

    private OnItemClickListener mClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view,
                                final int position, long id) {
            Animation animation = AnimationUtils.loadAnimation(getActivity()
                    .getApplicationContext(), R.anim.anim_tile_click);
            final ImageView check = (ImageView) view
                    .findViewById(R.id.list_item_category_grid_category_imageview);

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_choose_categories, container, false);
        mGridView = (GridView) v
                .findViewById(R.id.choose_categories_gridview);
        mProgressBar = (ProgressBar) v
                .findViewById(R.id.choose_categories_load_progress);
        mErrorTextView = (TextView) v
                .findViewById(R.id.choose_categories_error_textview);
        mNextButton = (Button) v
                .findViewById(R.id.choose_categories_done_button);
        mNextButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mSelectedItems.size() >= MIN_CATEGORIES_REQUIRED) {
                    mCallback.categoriesSelected(mSelectedItems);
                }
            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mItems = new ArrayList<Category>();
        if (((GrowApplication) getActivity().getApplication()).getCategories() != null) {
            mSelectedItems.addAll(((GrowApplication) getActivity().getApplication()).getCategories());
        }
        if (mSelectedItems.size() >= MIN_CATEGORIES_REQUIRED) {
            mNextButton.setVisibility(View.VISIBLE);
        }
        mAdapter = new ChooseCategoryAdapter(getActivity(),
                R.id.list_item_category_grid_category_textview, mItems, this);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(mClickListener);

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
                    + " must implement OnBoardingCategoryListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    private void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
        mGridView.setVisibility(View.GONE);
        mErrorTextView.setVisibility(View.GONE);
    }

    private void showCategories() {
        mProgressBar.setVisibility(View.GONE);
        mGridView.setVisibility(View.VISIBLE);
        mErrorTextView.setVisibility(View.GONE);
    }

    private void showError() {
        mProgressBar.setVisibility(View.GONE);
        mGridView.setVisibility(View.GONE);
        mErrorTextView.setVisibility(View.VISIBLE);
    }

    private void loadCategories() {
        showProgress();
        new CategoryLoaderTask(getActivity().getApplicationContext(), this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                        ((GrowApplication) getActivity().getApplication())
                                .getToken());
    }

    @Override
    public void categoryLoaderFinished(ArrayList<Category> categories) {
        if (categories != null) {
            mItems.addAll(categories);
            mAdapter.notifyDataSetChanged();
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
}
