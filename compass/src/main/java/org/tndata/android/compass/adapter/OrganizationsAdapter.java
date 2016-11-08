package org.tndata.android.compass.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.tndata.android.compass.R;
import org.tndata.android.compass.databinding.CardOrganizationBinding;
import org.tndata.android.compass.model.Organization;

import java.util.List;


/**
 * Adapter to display a list of organizations.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class OrganizationsAdapter extends RecyclerView.Adapter<OrganizationsAdapter.OrganizationHolder>{
    private Context mContext;
    private List<Organization> mOrganizations;
    private OrganizationsAdapterListener mListener;


    /**
     * Constructor.
     *
     * @param context a reference to the context.
     * @param organizations the list of organizations to choose from.
     * @param listener the organization selection listener.
     */
    public OrganizationsAdapter(@NonNull Context context, @NonNull List<Organization> organizations,
                                @NonNull OrganizationsAdapterListener listener){

        mContext = context;
        mOrganizations = organizations;
        mListener = listener;
    }

    @Override
    public int getItemCount(){
        return mOrganizations.size();
    }

    @Override
    public OrganizationHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return new OrganizationHolder(
                DataBindingUtil.<CardOrganizationBinding>inflate(
                        inflater, R.layout.card_organization, parent, false
                )
        );
    }

    @Override
    public void onBindViewHolder(OrganizationHolder holder, int position){
        holder.bind(mOrganizations.get(position));
    }


    /**
     * Holder for an organization item.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    class OrganizationHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private CardOrganizationBinding mBinding;


        /**
         * Constructor.
         *
         * @param binding the binding class containing references to the views.
         */
        private OrganizationHolder(CardOrganizationBinding binding){
            super(binding.getRoot());
            mBinding = binding;
            itemView.setOnClickListener(this);
        }

        /**
         * Binds an organization to the holder.
         *
         * @param organization the organization to be bound.
         */
        void bind(Organization organization){
            mBinding.setOrganization(organization);
        }

        @Override
        public void onClick(View view){
            mListener.onOrganizationSelected(mBinding.getOrganization());
        }
    }


    /**
     * Listener interface for the organization selection process.
     *
     * @author Ismael Alonso
     * @version 1.0.1
     */
    public interface OrganizationsAdapterListener{
        /**
         * Called when the user selects an organization.
         *
         * @param organization the organization that was selected.
         */
        void onOrganizationSelected(@NonNull Organization organization);
    }
}
