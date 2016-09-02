package org.tndata.android.compass.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.ChooseCategoryAdapter;
import org.tndata.android.compass.model.TDCCategory;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.tour.Tour;
import org.tndata.android.compass.util.API;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import es.sandwatch.httprequests.HttpRequest;
import es.sandwatch.httprequests.HttpRequestError;


/**
 * Fragment used to display a list of categories during OnBoarding.
 *
 * @author Ismael Alonso
 * @version 1.1.0
 */
public class OnBoardingCategoryFragment
        extends Fragment
        implements
                HttpRequest.RequestCallback,
                Parser.ParserCallback,
                ChooseCategoryAdapter.ChooseCategoryAdapterListener,
                View.OnClickListener{

    private static final String LOAD_KEY = "org.tndata.compass.CategoryFragment.Load";


    private int mGetCategoriesRC;

    private CategoryListener mListener;
    private View mContent;
    private View mProgress;
    private RecyclerView mList;
    private Button mNext;

    private boolean mLoad;


    public static OnBoardingCategoryFragment newInstance(boolean loadCategories){
        Bundle args = new Bundle();
        args.putBoolean(LOAD_KEY, loadCategories);

        OnBoardingCategoryFragment frag = new OnBoardingCategoryFragment();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mLoad = getArguments().getBoolean(LOAD_KEY, false);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            mListener = (CategoryListener)context;
        }
        catch (ClassCastException ccx){
            throw new IllegalStateException("why");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View root, @Nullable Bundle savedInstanceState){
        CompassApplication app = (CompassApplication)getActivity().getApplication();

        TextView explanation = (TextView)root.findViewById(R.id.list_explanation);
        explanation.setText(R.string.list_explanation);
        root.findViewById(R.id.list_explanation_container).setVisibility(View.VISIBLE);

        mContent = root.findViewById(R.id.list_content);
        mProgress = root.findViewById(R.id.list_progress);

        mList = (RecyclerView)root.findViewById(R.id.list_recycler_view);
        mList.setLayoutManager(new LinearLayoutManager(getContext()));

        if (mLoad){
            mGetCategoriesRC = HttpRequest.get(this, API.URL.getCategories());
            mContent.setVisibility(View.GONE);
            mProgress.setVisibility(View.VISIBLE);
        }
        else{
            mList.setAdapter(new ChooseCategoryAdapter(getContext(), this, app.getCategoryList(true)));
            mContent.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.GONE);
        }


        mNext = (Button)root.findViewById(R.id.list_button);
        mNext.setVisibility(View.VISIBLE);
        mNext.setOnClickListener(this);
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mGetCategoriesRC){
            Parser.parse(result, ParserModels.CategoryContentResultSet.class, this);
        }
    }

    @Override
    public void onRequestFailed(int requestCode, HttpRequestError error){

    }

    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){
        if (result instanceof ParserModels.CategoryContentResultSet){
            List<TDCCategory> categories = ((ParserModels.CategoryContentResultSet)result).results;
            ((CompassApplication)getActivity().getApplication()).setAvailableCategories(categories);
        }
    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        if (result instanceof ParserModels.CategoryContentResultSet){
            CompassApplication app = (CompassApplication)getActivity().getApplication();
            List<TDCCategory> filtered = app.getCategoryList(true);
            mList.setAdapter(new ChooseCategoryAdapter(getContext(), this, filtered));
            mContent.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.GONE);

            fireTour();
        }
    }

    @Override
    public void onParseFailed(int requestCode){

    }

    private void fireTour(){
        Queue<Tour.Tooltip> tooltips = new LinkedList<>();
        for (Tour.Tooltip tooltip:Tour.getTooltipsFor(Tour.Section.CATEGORY)){
            if (tooltip == Tour.Tooltip.CAT_SKIP){
                tooltip.setTarget(mNext);
            }
            tooltips.add(tooltip);
        }
        Tour.display(getActivity(), tooltips);
    }

    /**
     * When this method gets called, the text of the skip button becomes "finish."
     */
    public void notifyContentSelected(){
        if (mNext != null){
            mNext.setText(R.string.onboarding_category_finish);
        }
    }

    @Override
    public void onCategorySelected(TDCCategory category){
        mListener.onCategorySelected(category);
    }

    @Override
    public void onClick(View v){
        v.setOnClickListener(null);
        mListener.onNext();
    }


    /**
     * Category selection listener interface.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface CategoryListener{
        /**
         * Called when a category is selected.
         *
         * @param category the selected category.
         */
        void onCategorySelected(TDCCategory category);

        /**
         * Called when the next button (whether skip or finish) is tapped.
         */
        void onNext();
    }
}
