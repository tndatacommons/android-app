package org.tndata.android.compass.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.app.Fragment;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import org.tndata.android.compass.R;
import org.tndata.android.compass.databinding.FragmentLauncherBinding;


/**
 * Fragment acting as splash and initial action switch.
 *
 * @author Edited by Ismael Alonso
 * @version 1.1.0
 */
public class LauncherFragment extends Fragment implements OnClickListener{
    //Listener
    private LauncherFragmentListener mListener;

    //Binding
    private FragmentLauncherBinding mBinding;

    //Flags
    private boolean mIsLoaded;
    private boolean mShowProgressOnLoad;


    /**
     * Constructor.
     */
    public LauncherFragment(){
        mIsLoaded = false;
        mShowProgressOnLoad = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_launcher, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View root, Bundle savedInstanceState){
        //Set the color of the progress bar to the accent color
        int color = ContextCompat.getColor(getActivity(), R.color.accent);
        mBinding.launcherProgress.getIndeterminateDrawable()
                .setColorFilter(color, PorterDuff.Mode.MULTIPLY);

        //Set listeners
        mBinding.launcherSignUp.setOnClickListener(this);
        mBinding.launcherLogin.setOnClickListener(this);

        //Update the flags and show progress if necessary
        mIsLoaded = true;
        showProgress(mShowProgressOnLoad);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.launcher_sign_up:
                mListener.signUp();
                break;

            case R.id.launcher_login:
                mListener.logIn();
                break;
        }
    }

    /**
     * Shows the splash screen or the switch.
     *
     * @param show true to show the splash screen, false to show the switch.
     */
    public void showProgress(boolean show){
        if (mIsLoaded){
            if (show){
                mBinding.launcherSplash.setVisibility(View.VISIBLE);
                mBinding.launcherMenu.setVisibility(View.GONE);
            }
            else{
                mBinding.launcherSplash.setVisibility(View.GONE);
                mBinding.launcherMenu.setVisibility(View.VISIBLE);
            }
        }
        mShowProgressOnLoad = show;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        //This makes sure that the container activity has implemented the callback
        //  interface. If not, it throws an exception
        try{
            mListener = (LauncherFragmentListener)context;
        }
        catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + " must implement LauncherFragmentListener");
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mListener = null;
    }


    /**
     * Listener interface for the switch.
     *
     * @version 1.0.0
     */
    public interface LauncherFragmentListener{
        /**
         * Called when the sign up button is clicked.
         */
        void signUp();

        /**
         * Called when the log in button is clicked.
         */
        void logIn();
    }
}
