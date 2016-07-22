package org.tndata.android.compass.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.OrganizationsAdapter;
import org.tndata.android.compass.databinding.FragmentOrganizationsBinding;
import org.tndata.android.compass.model.Organization;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.util.API;

import java.util.List;

import es.sandwatch.httprequests.HttpRequest;
import es.sandwatch.httprequests.HttpRequestError;


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
                OrganizationsAdapter.OrganizationsAdapterListener{

    private FragmentOrganizationsBinding mBinding;

    private List<Organization> mOrganizations;
    private int mGetOrganizationsRC;

    private OrganizationsFragmentListener mListener;


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
            mListener = (OrganizationsFragmentListener)context;
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
            bindCategories();
        }
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mGetOrganizationsRC){
            Parser.parse(result, ParserModels.OrganizationsResultSet.class, this);
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
            if (mBinding == null){
                bindCategories();
            }
        }
    }

    private void bindCategories(){
        mBinding.organizationsList.setLayoutManager(new LinearLayoutManager(getContext()));
        OrganizationsAdapter adapter = new OrganizationsAdapter(getContext(), mOrganizations, this);
        mBinding.organizationsList.setAdapter(adapter);
        mBinding.organizationsList.setVisibility(View.VISIBLE);
        mBinding.organizationsProgress.setVisibility(View.GONE);
    }

    @Override
    public void onOrganizationSelected(@NonNull Organization organization){
        mListener.onOrganizationSelected(organization);
    }


    public interface OrganizationsFragmentListener{
        void onOrganizationSelected(@NonNull Organization organization);
    }
}
