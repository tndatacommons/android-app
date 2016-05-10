package org.tndata.android.compass.activity;

import android.app.AlertDialog;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;
import org.tndata.android.compass.BuildConfig;
import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.database.CompassDbHelper;
import org.tndata.android.compass.fragment.LauncherFragment;
import org.tndata.android.compass.fragment.LogInFragment;
import org.tndata.android.compass.fragment.SignUpFragment;
import org.tndata.android.compass.model.FeedData;
import org.tndata.android.compass.model.User;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.FeedDataLoader;
import org.tndata.android.compass.util.VersionChecker;

import java.util.ArrayList;
import java.util.List;

import es.sandwatch.httprequests.HttpRequest;
import es.sandwatch.httprequests.HttpRequestError;


public class LoginActivity
        extends AppCompatActivity
        implements
                VersionChecker.VersionCallback,
                View.OnClickListener,
                LauncherFragment.LauncherFragmentListener,
                SignUpFragment.SignUpFragmentListener,
                LogInFragment.LogInFragmentCallback,
                HttpRequest.RequestCallback,
                Parser.ParserCallback,
                FeedDataLoader.Callback{

    private static final String TAG = "LogInActivity";

    //Fragment ids
    private static final int DEFAULT = 0;
    private static final int SIGN_UP = DEFAULT+1;
    private static final int LOGIN = SIGN_UP+1;

    private static final String PREFERENCES_NAME = "compass_pref";
    private static final String PREFERENCES_NEW_USER = "new_user_pref";


    private CompassApplication mApplication;
    private List<Fragment> mFragmentStack;

    private LauncherFragment mLauncherFragment;
    private LogInFragment mLoginFragment;
    private SignUpFragment mSignUpFragment;

    //Request codes
    private int mLogInRC;
    private int mGetCategoriesRC;
    private int mGetPlacesRC;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        mApplication = (CompassApplication)getApplication();
        mFragmentStack = new ArrayList<>();
        swapFragments(DEFAULT, true);
        new VersionChecker(this).execute();
    }

    @Override
    public void onVersionRetrieved(String versionName){
        Log.d(TAG, "Version: " + versionName);
        if (!BuildConfig.DEBUG && !versionName.equals(getString(R.string.version_name))){
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
                displayLauncherActivity(false);
            }
            else{
                User user = mApplication.getUserLoginInfo();
                if (!user.getEmail().isEmpty() && !user.getPassword().isEmpty()){
                    logUserIn(user.getEmail(), user.getPassword());
                }
                else{
                    displayLauncherActivity(false);
                }
            }
        }
    }

    @Override
    public void onClick(View v){
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=org.tndata.android.compass")));
    }

    @Override
    public void onBackPressed(){
        handleBackStack();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                handleBackStack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void swapFragments(int index, boolean addToStack){
        Fragment fragment = null;
        switch (index){
            case DEFAULT:
                if (mLauncherFragment == null){
                    mLauncherFragment = new LauncherFragment();
                }
                fragment = mLauncherFragment;
                break;

            case LOGIN:
                if (mLoginFragment == null){
                    mLoginFragment = new LogInFragment();
                }
                fragment = mLoginFragment;
                break;

            case SIGN_UP:
                if (mSignUpFragment == null){
                    mSignUpFragment = new SignUpFragment();
                }
                fragment = mSignUpFragment;
                break;

        }
        if (fragment != null){
            if (addToStack){
                mFragmentStack.add(fragment);
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.base_content, fragment).commit();
        }
    }

    private void handleBackStack(){
        if (!mFragmentStack.isEmpty()){
            mFragmentStack.remove(mFragmentStack.size() - 1);
        }

        if (mFragmentStack.isEmpty()){
            HttpRequest.cancel(mLogInRC);
            FeedDataLoader.cancel();
            finish();
        }
        else{
            Fragment fragment = mFragmentStack.get(mFragmentStack.size() - 1);

            if (fragment instanceof LauncherFragment){
                ((LauncherFragment)fragment).showProgress(false);
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.base_content, fragment).commit();
        }
    }

    private void transitionToMain(){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    private void transitionToOnBoarding(){
        startActivity(new Intent(getApplicationContext(), OnBoardingActivity.class));
        finish();
    }

    /**
     * Fires up the log in task with the provided parameters.
     *
     * @param email the email address.
     * @param password the password.
     */
    private void logUserIn(String email, String password){
        Log.d(TAG, "Logging user in");
        displayLauncherActivity(true);
        mLogInRC = HttpRequest.post(this, API.getLogInUrl(), API.getLogInBody(email, password));
    }

    @Override
    public void signUp() {
        swapFragments(SIGN_UP, true);
    }

    @Override
    public void logIn() {
        swapFragments(LOGIN, true);
    }

    @Override
    public void onSignUpSuccess(@NonNull User user){
        setUser(user);
    }

    @Override
    public void onLoginSuccess(@NonNull User user){
        setUser(user);
    }

    private void setUser(User user){
        mApplication.setUser(user, true);
        mGetCategoriesRC = HttpRequest.get(this, API.getCategoriesUrl());
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mLogInRC){
            if (result.contains("\"non_field_errors\"")){
                swapFragments(LOGIN, true);
            }
            else{
                try{
                    Log.d(TAG, new JSONObject(result).toString(2));
                }
                catch (Exception x){
                    x.printStackTrace();
                }
                Parser.parse(result, User.class, this);
            }
        }
        else if (requestCode == mGetCategoriesRC){
            Parser.parse(result, ParserModels.CategoryContentResultSet.class, this);
        }
        else if (requestCode == mGetPlacesRC){
            Parser.parse(result, ParserModels.UserPlacesResultSet.class, this);
        }
    }

    @Override
    public void onRequestFailed(int requestCode, HttpRequestError error){
        if (requestCode == mLogInRC){
            Log.d(TAG, "Login request failed");
            mFragmentStack.clear();
            swapFragments(LOGIN, true);
        }
        else if (requestCode == mGetCategoriesRC){
            Log.d(TAG, "Get categories failed");
        }
    }

    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){
        if (result instanceof User){
            mApplication.setUser((User)result, false);
        }
        else if (result instanceof ParserModels.UserPlacesResultSet){
            CompassDbHelper helper = new CompassDbHelper(this);
            helper.emptyPlacesTable();
            helper.savePlaces(((ParserModels.UserPlacesResultSet)result).results);
            helper.close();
        }
    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        if (result instanceof User){
            mGetCategoriesRC = HttpRequest.get(this, API.getCategoriesUrl());
            mGetPlacesRC = HttpRequest.get(this, API.getUserPlacesUrl());
        }
        else if (result instanceof ParserModels.CategoryContentResultSet){
            mApplication.setPublicCategories(((ParserModels.CategoryContentResultSet)result).results);
            if (mApplication.getUser().needsOnBoarding()){
                transitionToOnBoarding();
            }
            else{
                Log.d(TAG, "Fetching user data");
                FeedDataLoader.load(this);
            }
        }
    }

    @Override
    public void onFeedDataLoaded(@Nullable FeedData feedData){
        if (feedData != null){
            mApplication.setFeedData(feedData);
            transitionToMain();
        }
    }

    private void displayLauncherActivity(boolean show){
        for (Fragment fragment:mFragmentStack){
            if (fragment instanceof LauncherFragment){
                ((LauncherFragment)fragment).showProgress(show);
            }
        }
    }
}
