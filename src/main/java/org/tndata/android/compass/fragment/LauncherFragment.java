package org.tndata.android.compass.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import org.tndata.android.compass.R;


/**
 * Fragment acting as splash and initial action switch.
 *
 * @author Edited by Ismael Alonso
 * @version 1.1.0
 */
public class LauncherFragment extends Fragment implements OnClickListener{
    //Listener
    private LauncherFragmentListener mListener;

    //UI components
    private ProgressBar mProgressBar;
    private Button mSignUpButton;
    private Button mLoginButton;
    private Button mTourButton;

    //Flags
    private boolean viewsLoaded;
    private boolean shouldShowProgressOnLoad;


    /**
     * Constructor.
     */
    public LauncherFragment(){
        viewsLoaded = false;
        shouldShowProgressOnLoad = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View root = inflater.inflate(R.layout.fragment_launcher, container, false);

        //Fetch UI components
        mProgressBar = (ProgressBar)root.findViewById(R.id.launcher_load_progress);
        mSignUpButton = (Button)root.findViewById(R.id.launcher_sign_up_button);
        mLoginButton = (Button)root.findViewById(R.id.launcher_login_button);
        mTourButton = (Button)root.findViewById(R.id.launcher_tour_button);

        //Set listeners
        mSignUpButton.setOnClickListener(this);
        mLoginButton.setOnClickListener(this);
        mTourButton.setOnClickListener(this);

        //Set the color of the progress bar to the accent color
        int color = getResources().getColor(R.color.grow_accent);
        mProgressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.MULTIPLY);

        //Update the flags and show progress if necessary
        viewsLoaded = true;
        if (shouldShowProgressOnLoad){
            showProgress(true);
        }

        return root;
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.launcher_sign_up_button:
                mListener.signUp();
                break;

            case R.id.launcher_login_button:
                mListener.logIn();
                break;

            case R.id.launcher_tour_button:
                mListener.tour();
        }
    }

    /**
     * Shows the splash screen or the switch.
     *
     * @param show true to show the splash screen, false to show the switch.
     */
    public void showProgress(boolean show){
        if (viewsLoaded){
            if (show){
                mProgressBar.setVisibility(View.VISIBLE);
                mSignUpButton.setVisibility(View.GONE);
                mLoginButton.setVisibility(View.GONE);
                mTourButton.setVisibility(View.GONE);
            }
            else{
                mProgressBar.setVisibility(View.GONE);
                mSignUpButton.setVisibility(View.VISIBLE);
                mLoginButton.setVisibility(View.VISIBLE);
                mTourButton.setVisibility(View.VISIBLE);
            }
        }
        else{
            shouldShowProgressOnLoad = show;
        }
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

        /**
         * Called when the tour button is clicked.
         */
        void tour();
    }
}
