package org.tndata.android.compass.activity;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.fragment.LauncherFragment;
import org.tndata.android.compass.fragment.LauncherFragment.LauncherFragmentListener;
import org.tndata.android.compass.fragment.LogInFragment;
import org.tndata.android.compass.fragment.LogInFragment.LogInFragmentCallback;
import org.tndata.android.compass.fragment.SignUpFragment;
import org.tndata.android.compass.fragment.SignUpFragment.SignUpFragmentListener;
import org.tndata.android.compass.fragment.TourFragment;
import org.tndata.android.compass.fragment.TourFragment.TourFragmentCallback;
import org.tndata.android.compass.fragment.WebFragment;
import org.tndata.android.compass.model.User;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.NetworkRequest;
import org.tndata.android.compass.util.Parser;

import java.util.ArrayList;
import java.util.List;


public class LoginActivity
        extends AppCompatActivity
        implements
                LauncherFragmentListener,
                SignUpFragmentListener,
                LogInFragmentCallback,
                TourFragmentCallback,
                NetworkRequest.RequestCallback{


    //Fragment ids
    private static final int DEFAULT = 0;
    private static final int SIGN_UP = DEFAULT+1;
    private static final int LOGIN = SIGN_UP+1;
    private static final int TERMS = LOGIN+1;
    private static final int TOUR = TERMS+1;


    private Toolbar mToolbar;

    private WebFragment mWebFragment = null;
    private LauncherFragment mLauncherFragment = null;
    private LogInFragment mLoginFragment = null;
    private SignUpFragment mSignUpFragment = null;
    private TourFragment mTourFragment = null;

    private List<Fragment> mFragmentStack = new ArrayList<>();

    private CompassApplication mApplication;

    //Request codes
    private int mLogInRequestCode;
    private int mGetDataRequestCode;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_toolbar);

        mApplication = (CompassApplication)getApplication();

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().hide();
        }

        SharedPreferences settings = getSharedPreferences(Constants.PREFERENCES_NAME, 0);
        swapFragments(DEFAULT, true);
        if (settings.getBoolean(Constants.PREFERENCES_NEW_USER, true)){
            swapFragments(TOUR, true);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(Constants.PREFERENCES_NEW_USER, false);
            editor.apply();
        }

        SharedPreferences loginInfo = PreferenceManager.getDefaultSharedPreferences(this);
        String email = loginInfo.getString("email", "");
        String password = loginInfo.getString("password", "");
        if (!email.isEmpty() && !password.isEmpty()){
            logUserIn(email, password);
        }
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
                getSupportActionBar().hide();
                break;
            case LOGIN:
                if (mLoginFragment == null){
                    mLoginFragment = new LogInFragment();
                }
                fragment = mLoginFragment;
                getSupportActionBar().hide();
                break;
            case SIGN_UP:
                if (mSignUpFragment == null){
                    mSignUpFragment = new SignUpFragment();
                }
                fragment = mSignUpFragment;
                getSupportActionBar().hide();
                break;
            case TERMS:
                if (mWebFragment == null){
                    mWebFragment = new WebFragment();

                }
                fragment = mWebFragment;
                getSupportActionBar().show();
                mToolbar.setTitle(R.string.terms_title);
                mWebFragment.setUrl(Constants.TERMS_AND_CONDITIONS_URL);
                break;
            case TOUR:
                if (mTourFragment == null){
                    mTourFragment = new TourFragment();
                }
                fragment = mTourFragment;
                getSupportActionBar().hide();
                break;
            default:
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
            NetworkRequest.cancel(mLogInRequestCode);
            NetworkRequest.cancel(mGetDataRequestCode);
            finish();
        }
        else{
            Fragment fragment = mFragmentStack.get(mFragmentStack.size() - 1);

            int index = DEFAULT;
            if (fragment instanceof LauncherFragment){
                NetworkRequest.cancel(mGetDataRequestCode);
                ((LauncherFragment)fragment).showProgress(false);
            }
            else if (fragment instanceof LogInFragment){
                index = LOGIN;
            }
            else if (fragment instanceof SignUpFragment){
                index = SIGN_UP;
            }
            else if (fragment instanceof WebFragment){
                index = TERMS;
            }
            else if (fragment instanceof TourFragment){
                index = TOUR;
            }

            swapFragments(index, false);
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
        Log.d("LogIn", "Logging user in");
        for (Fragment fragment:mFragmentStack){
            if (fragment instanceof LauncherFragment){
                ((LauncherFragment)fragment).showProgress(true);
            }
        }

        mLogInRequestCode = NetworkRequest.post(this, this, API.getLogInUrl(), "",
                API.getLogInBody(email, password));
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
    public void tour() {
        swapFragments(TOUR, true);
    }

    @Override
    public void onSignUpSuccess(@NonNull User user){
        saveUserInfo(user);
    }

    @Override
    public void showTermsAndConditions(){
        swapFragments(TERMS, true);
    }

    @Override
    public void onLoginSuccess(@NonNull User user){
        saveUserInfo(user);
    }

    private void saveUserInfo(User user){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("auth_token", user.getToken());
        editor.putString("first_name", user.getFirstName());
        editor.putString("last_name", user.getLastName());
        editor.putString("email", user.getEmail());
        editor.putString("password", user.getPassword());
        editor.putInt("id", user.getId());
        editor.commit();

        mApplication.setToken(user.getToken());
        mApplication.setUser(user);
        if (user.needsOnBoarding()){
            transitionToOnBoarding();
        }
        else{
            mGetDataRequestCode = NetworkRequest.get(this, this, API.getUserDataUrl(),
                    mApplication.getToken(), 60*1000);
        }
    }

    @Override
    public void onTourComplete(){
        handleBackStack();
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mLogInRequestCode){
            User user = new Parser().parseUser(result);
            Log.d("LogIn", user.getError());
            if (user.getError().isEmpty()){
                saveUserInfo(user);
            }
            else{
                swapFragments(LOGIN, true);
            }
        }
        else if (requestCode == mGetDataRequestCode){
            mApplication.setUserData(new Parser().parseUserData(this, result));
            transitionToMain();
        }
    }

    @Override
    public void onRequestFailed(int requestCode){
        if (requestCode == mLogInRequestCode){
            Log.d("LogIn", "Login request failed");
            swapFragments(LOGIN, true);
        }
        else if (requestCode == mGetDataRequestCode){
            Log.d("LogIn", "Get data failed");
        }
    }
}
