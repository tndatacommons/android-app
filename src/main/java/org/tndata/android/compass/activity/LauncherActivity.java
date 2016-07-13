package org.tndata.android.compass.activity;

import android.app.AlertDialog;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.tndata.android.compass.BuildConfig;
import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.fragment.LauncherFragment;
import org.tndata.android.compass.fragment.LogInFragment;
import org.tndata.android.compass.fragment.SignUpFragment;
import org.tndata.android.compass.model.FeedData;
import org.tndata.android.compass.model.User;
import org.tndata.android.compass.util.FeedDataLoader;
import org.tndata.android.compass.util.VersionChecker;


public class LauncherActivity
        extends AppCompatActivity
        implements
                VersionChecker.VersionCallback,
                View.OnClickListener,
                LauncherFragment.LauncherFragmentListener,
                SignUpFragment.SignUpFragmentListener,
                LogInFragment.LogInFragmentCallback,
                FeedDataLoader.Callback{

    private static final String TAG = "LauncherActivity";

    private static final String PREFERENCES_NAME = "compass_pref";
    private static final String PREFERENCES_NEW_USER = "new_user_pref";


    private CompassApplication mApplication;

    private LauncherFragment mLauncherFragment;
    private LogInFragment mLoginFragment;
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
        //new VersionChecker(this).execute();
        onVersionRetrieved(getString(R.string.version_name));
    }

    @Override
    public void onVersionRetrieved(String versionName){
        String appVersionName = getString(R.string.version_name);
        Log.i(TAG, "App version name: " + appVersionName);
        Log.i(TAG, "Play store version name: " + versionName);
        if (!BuildConfig.DEBUG && !versionName.equals(appVersionName)){
            ViewGroup rootView = (ViewGroup)findViewById(android.R.id.content);
            LayoutInflater inflater = LayoutInflater.from(this);
            View dialogRootView = inflater.inflate(R.layout.dialog_update, rootView, false);
            dialogRootView.findViewById(R.id.update_get_it).setOnClickListener(this);

            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setView(dialogRootView)
                    .create().show();
        }
        else{
            SharedPreferences settings = getSharedPreferences(PREFERENCES_NAME, 0);
            if (settings.getBoolean(PREFERENCES_NEW_USER, true)){
                startActivity(new Intent(this, TourActivity.class));
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(PREFERENCES_NEW_USER, false);
                editor.apply();
                displayLauncherFragment(false);
            }
            else{
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
                    }
                    else{
                        Log.i(TAG, "Retrieving data.");
                        Log.d(TAG, "Token: " + user.getToken());
                        fetchData();
                    }
                }
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
        FeedDataLoader.cancel();
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
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
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
        if (mApplication.getUser().needsOnBoarding()){
            transitionToOnBoarding();
        }
        else{
            fetchData();
        }
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

    private void fetchData(){
        if (!cancelled){
            FeedDataLoader.load(this);
        }
    }

    @Override
    public void onFeedDataLoaded(@Nullable FeedData feedData){
        if (!cancelled && feedData != null){
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
