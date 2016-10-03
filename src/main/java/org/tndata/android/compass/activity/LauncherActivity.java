package org.tndata.android.compass.activity;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.fragment.LauncherFragment;
import org.tndata.android.compass.fragment.LogInFragment;
import org.tndata.android.compass.fragment.ResetPasswordFragment;
import org.tndata.android.compass.fragment.SignUpFragment;
import org.tndata.android.compass.model.FeedData;
import org.tndata.android.compass.model.User;
import org.tndata.android.compass.util.FeedDataLoader;


//TODO document
public class LauncherActivity
        extends AppCompatActivity
        implements
                View.OnClickListener,
                LauncherFragment.LauncherFragmentListener,
                SignUpFragment.SignUpFragmentListener,
                LogInFragment.LogInFragmentCallback,
                FeedDataLoader.DataLoadCallback{

    private static final String TAG = "LauncherActivity";


    private CompassApplication mApplication;

    private LauncherFragment mLauncherFragment;
    private LogInFragment mLoginFragment;
    private ResetPasswordFragment mResetPasswordFragment;
    private SignUpFragment mSignUpFragment;

    //Firewall. Cancelling an HttpRequest may not be enough, as the system might be parsing
    private boolean cancelled;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        cancelled = false;

        displayLauncherFragment(true);
        mApplication = (CompassApplication)getApplication();

        load();
    }

    private void load(){
        User user = mApplication.getUser();
        if (user == null){
            //If there is no user, show the menu
            Log.e(TAG, "No user was found, displaying menu.");
            displayLauncherFragment(false);
        }
        else{
            Log.i(TAG, "User was found.");
            //If there is a user, show the loading screen
            displayLauncherFragment(true);

            if (user.needsOnBoarding()){
                Log.i(TAG, "User needs on-boarding.");
                transitionToOnBoarding();
                Log.d(TAG, "Token: " + user.getToken());
            }
            else{
                Log.i(TAG, "Retrieving data.");
                Log.d(TAG, "Token: " + user.getToken());
                fetchData();
            }
        }
    }

    @Override
    public void onClick(View v){
        String playStoreUri = "market://details?id=org.tndata.android.compass";
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(playStoreUri)));
    }

    @Override
    public void onBackPressed(){
        popBackStack();
    }

    /**
     * Pops a fragment from the back stack and cancels I/O
     */
    private void popBackStack(){
        //Cancel common requests/tasks
        FeedDataLoader.getInstance().cancel();
        cancelled = true;

        int count = getSupportFragmentManager().getBackStackEntryCount();
        String name = getSupportFragmentManager().getBackStackEntryAt(count-1).getName();
        if (name.equals("Launcher")){
            finish();
        }
        else{
            mLauncherFragment.showProgress(false);
            getSupportFragmentManager().popBackStack();
        }
    }

    /**
     * Triggers a transition to main.
     */
    private void transitionToMain(){
        startActivity(new Intent(getApplicationContext(), FeedActivity.class));
        finish();
    }

    /**
     * Triggers a transition to on boarding.
     */
    private void transitionToOnBoarding(){
        startActivity(new Intent(getApplicationContext(), OnBoardingActivity.class));
        finish();
    }

    @Override
    public void signUp(){
        cancelled = false;
        if (mSignUpFragment == null){
            mSignUpFragment = new SignUpFragment();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.base_content, mSignUpFragment)
                .addToBackStack("SignUp").commit();
    }

    @Override
    public void logIn(){
        cancelled = false;
        if (mLoginFragment == null){
            mLoginFragment = new LogInFragment();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.base_content, mLoginFragment)
                .addToBackStack("LogIn").commit();
    }

    @Override
    public void onSignUpSuccess(){
        Log.d(TAG, mApplication.getUser().toString());
        transitionToOnBoarding();
    }

    @Override
    public void onLoginSuccess(){
        if (mApplication.getUser().needsOnBoarding()){
            transitionToOnBoarding();
        }
        else{
            fetchData();
        }
    }

    @Override
    public void onForgottenPassword(){
        if (mResetPasswordFragment == null){
            mResetPasswordFragment = new ResetPasswordFragment();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.base_content, mResetPasswordFragment)
                .addToBackStack("Reset").commit();
    }

    private void fetchData(){
        if (!cancelled){
            FeedDataLoader.getInstance().load(this);
        }
    }

    @Override
    public void onFeedDataLoaded(@Nullable FeedData feedData){
        if (feedData == null){
            displayLauncherFragment(false);
        }
        else if (!cancelled){
            mApplication.setFeedData(feedData);
            transitionToMain();
        }
    }

    private void displayLauncherFragment(boolean showProgress){
        if (mLauncherFragment == null){
            mLauncherFragment = new LauncherFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.base_content, mLauncherFragment)
                    .addToBackStack("Launcher").commit();
        }
        mLauncherFragment.showProgress(showProgress);
    }
}
