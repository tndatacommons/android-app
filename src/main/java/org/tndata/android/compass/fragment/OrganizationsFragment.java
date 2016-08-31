package org.tndata.android.compass.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.OrganizationsAdapter;
import org.tndata.android.compass.databinding.FragmentOrganizationsBinding;
import org.tndata.android.compass.model.Organization;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.tour.CoachMark;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.CardItemDecoration;
import org.tndata.android.compass.tour.Tour;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import es.sandwatch.httprequests.HttpRequest;
import es.sandwatch.httprequests.HttpRequestError;
import tourguide.tourguide.ChainTourGuide;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Sequence;
import tourguide.tourguide.ToolTip;


/**
 * Organization selection fragment.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class OrganizationsFragment
        extends Fragment
        implements
                HttpRequest.RequestCallback,
                Parser.ParserCallback,
                OrganizationsAdapter.OrganizationsAdapterListener,
                View.OnClickListener{

    private FragmentOrganizationsBinding mBinding;

    private List<Organization> mOrganizations;
    private int mGetOrganizationsRC;
    private int mPostOrganizationRC;

    public ChainTourGuide mTourGuideHandler;

    private OrganizationsListener mListener;


    /**
     * Method to create a new instance of this fragment.
     *
     * @return an instance of the fragment.
     */
    public static OrganizationsFragment newInstance(){
        return new OrganizationsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mGetOrganizationsRC = HttpRequest.get(this, API.URL.getOrganizations());
        mOrganizations = null;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            mListener = (OrganizationsListener)context;
        }
        catch (ClassCastException ccx){
            throw new ClassCastException(context.toString()
                    + " must implement LoginFragmentListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_organizations, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        if (mOrganizations != null){
            bindOrganizations();
        }
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mGetOrganizationsRC){
            Parser.parse(result, ParserModels.OrganizationsResultSet.class, this);
        }
        else if (requestCode == mPostOrganizationRC){
            mListener.onOrganizationSelected(true);
        }
    }

    @Override
    public void onRequestFailed(int requestCode, HttpRequestError error){

    }

    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){

    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        if (result instanceof ParserModels.OrganizationsResultSet){
            mOrganizations = ((ParserModels.OrganizationsResultSet)result).results;
            if (mOrganizations.size() == 0){
                mListener.onOrganizationSelected(false);
            }
            else if (mBinding != null){
                bindOrganizations();
            }
        }
    }

    @Override
    public void onParseFailed(int requestCode){

    }

    /**
     * Creates the adapter with the proper information and changes the visibility of the
     * layouts components.
     */
    private void bindOrganizations(){
        Context context = getContext();
        mBinding.organizationsList.setLayoutManager(new LinearLayoutManager(context));
        OrganizationsAdapter adapter = new OrganizationsAdapter(context, mOrganizations, this);
        mBinding.organizationsList.setAdapter(adapter);
        mBinding.organizationsList.addItemDecoration(new CardItemDecoration());
        mBinding.organizationsHeader.setVisibility(View.VISIBLE);
        mBinding.organizationsList.setVisibility(View.VISIBLE);
        mBinding.organizationsSkip.setVisibility(View.VISIBLE);
        mBinding.organizationsSkip.setOnClickListener(this);
        mBinding.organizationsProgress.setVisibility(View.GONE);

        fireTour();
    }

    private void fireTour(){
        Queue<CoachMark> marks = new LinkedList<>();
        marks.add(new CoachMark().setOverlayColor(getResources().getColor(R.color.tour_overlay))
                .setCutawayType(CoachMark.CutawayType.NONE)
                .setTooltip(Tour.Tooltip.ORG_GENERAL));
        marks.add(new CoachMark().setOverlayColor(getResources().getColor(R.color.tour_overlay))
                .setCutawayType(CoachMark.CutawayType.CIRCLE)
                .setTooltip(Tour.Tooltip.ORG_SKIP)
                .setCutawayRadius(100)
                .setTarget(mBinding.organizationsSkip));
        Tour.display(getActivity(), marks);

        /*List<ChainTourGuide> guides = new ArrayList<>();
        for (Tour.Tooltip tooltip:Tour.getTooltipsFor(Tour.Section.ORGANIZATION)){
            ChainTourGuide guide = ChainTourGuide.init(getActivity())
                    .setToolTip(new ToolTip()
                            .setTitle(tooltip.getTitle())
                            .setDescription(tooltip.getDescription())
                            .setGravity(Gravity.CENTER|Gravity.TOP)
                            .setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View view){
                                    mTourGuideHandler.next();
                                }
                            })
                    );

            if (tooltip == Tour.Tooltip.ORG_GENERAL){
                guide.setOverlay(new Overlay()
                        .setBackgroundColor(getContext().getResources().getColor(R.color.tour_overlay))
                        .setStyle(Overlay.Style.Circle)
                        .setHoleRadius(55)
                        .disableClick(true)
                        .disableClickThroughHole(true)
                )
                .playLater(mBinding.getRoot());
            }
            else if (tooltip == Tour.Tooltip.ORG_SKIP){
                guide.setOverlay(new Overlay()
                        .setBackgroundColor(getContext().getResources().getColor(R.color.tour_overlay))
                        .setStyle(Overlay.Style.Circle)
                        .disableClick(true)
                        .disableClickThroughHole(true)
                )
                .playLater(mBinding.organizationsSkip);
            }
            guides.add(guide);
        }
        ChainTourGuide guideArray[] = new ChainTourGuide[guides.size()];
        guides.toArray(guideArray);

        Sequence sequence = new Sequence.SequenceBuilder()
                .add(guideArray)
                .setDefaultPointer(null)
                .setContinueMethod(Sequence.ContinueMethod.Overlay)
                .build();

        mTourGuideHandler = ChainTourGuide.init(getActivity()).playInSequence(sequence);
        //mTourGuideHandler.getToolTip().setTranslationY(CompassUtil.getScreenHeight(getContext())/2);*/
    }

    @Override
    public void onOrganizationSelected(@NonNull Organization organization){
        mPostOrganizationRC = HttpRequest.post(this, API.URL.postOrganization(),
                API.BODY.postOrganization(organization));
        mBinding.organizationsHeader.setVisibility(View.GONE);
        mBinding.organizationsList.setVisibility(View.GONE);
        mBinding.organizationsSkip.setVisibility(View.GONE);
        mBinding.organizationsProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view){
        mListener.onOrganizationSelected(false);
    }


    /**
     * Listener for the OrganizationsFragment.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface OrganizationsListener{
        void onOrganizationSelected(boolean organizationAdded);
    }
}
