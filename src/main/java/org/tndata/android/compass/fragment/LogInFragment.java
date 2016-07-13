package org.tndata.android.compass.fragment;

import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.databinding.FragmentLoginBinding;
import org.tndata.android.compass.model.TDCCategory;
import org.tndata.android.compass.model.User;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.util.API;

import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import java.util.List;

import es.sandwatch.httprequests.HttpRequest;
import es.sandwatch.httprequests.HttpRequestError;


/**
 * Fragment that displays a log in screen.
 *
 * @author Edited and documented by Ismael Alonso
 * @version 1.1.0
 */
public class LogInFragment
        extends Fragment
        implements
                HttpRequest.RequestCallback,
                Parser.ParserCallback,
                OnClickListener{

    private CompassApplication mApplication;

    //Listener interface.
    private LogInFragmentCallback mCallback;

    //Binding
    private FragmentLoginBinding mBinding;

    //Attributes
    private String mErrorString = "";
    private int mLogInRC;
    //TODO the category loading code is duplicated in login and signup, I can't really
    //TODO think of anything else to do to prevent that short of creating a utility class
    //TODO that loads certain kinds of data, and I am not sure it is worth the time just yet
    private int mGetCategoriesRC;


    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        //Get the application class
        mApplication = (CompassApplication)context.getApplicationContext();

        //This makes sure that the host activity has implemented the callback interface.
        //  If not, it throws an exception
        try{
            mCallback = (LogInFragmentCallback)context;
        }
        catch (ClassCastException ccx){
            throw new ClassCastException(context.toString()
                    + " must implement LoginFragmentListener");
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        HttpRequest.cancel(mLogInRC);
        HttpRequest.cancel(mGetCategoriesRC);
        mCallback = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        mBinding.loginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.login_button:
                doLogin();
        }
    }

    /**
     * Checks the fields and starts the log in process if everything checks.
     */
    private void doLogin(){
        String email = mBinding.loginEmail.getText().toString().trim();
        String password = mBinding.loginPassword.getText().toString().trim();
        if (isValidEmail(email) && isValidPassword(password)){
            setFormEnabled(false);

            mLogInRC = HttpRequest.post(this, API.getLogInUrl(), API.getLogInBody(email, password));
        }
        else{
            setFormEnabled(true);
        }
    }

    /**
     * Checks if the email address provided by the user has a valid format.
     *
     * @param email the address to be checked.
     * @return true if the address provided has valid email format, false otherwise.
     */
    private boolean isValidEmail(@NonNull String email){
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            mErrorString = getActivity().getResources().getString(R.string.login_email_error);
            return false;
        }
        mErrorString = "";
        return true;
    }

    /**
     * Checks if the user introduced a password.
     *
     * @param password the password to be checked.
     * @return true if the user introduced a password, false otherwise.
     */
    private boolean isValidPassword(@NonNull String password){
        if (password.isEmpty()){
            mErrorString = getActivity().getResources().getString(R.string.login_pass_error);
            return false;
        }
        mErrorString = "";
        return true;
    }

    /**
     * Sets the state of the form and sets the error message if necessary.
     *
     * @param enabled true if the form should be enabled, false otherwise.
     */
    private void setFormEnabled(boolean enabled){
        mBinding.loginEmail.setEnabled(enabled);
        mBinding.loginPassword.setEnabled(enabled);
        mBinding.loginButton.setEnabled(enabled);
        if (enabled){
            mBinding.loginProgress.setVisibility(View.GONE);
            mBinding.loginError.setVisibility(View.VISIBLE);
            mBinding.loginError.setText(mErrorString);
        }
        else{
            mBinding.loginProgress.setVisibility(View.VISIBLE);
            mBinding.loginError.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mLogInRC){
            Parser.parse(result, User.class, this);
        }
        else if (requestCode == mGetCategoriesRC){
            Parser.parse(result, ParserModels.CategoryContentResultSet.class, this);
        }
    }

    @Override
    public void onRequestFailed(int requestCode, HttpRequestError error){
        if (error.isServerError()){
            if (error.getMessage().contains("non_field_errors")){
                try{
                    mErrorString = new JSONObject(error.getMessage())
                            .getJSONArray("non_field_errors").getString(0);
                }
                catch (JSONException jsonx){
                    jsonx.printStackTrace();
                }
            }
        }
        else{
            mErrorString = getActivity().getResources().getString(R.string.login_error);
        }
        setFormEnabled(true);
    }

    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){
        if (result instanceof User){
            User user = (User)result;
            user.setPassword(mBinding.loginPassword.getText().toString().trim());
            mApplication.setUser(user);
        }
        else if (result instanceof ParserModels.CategoryContentResultSet){
            List<TDCCategory> categories = ((ParserModels.CategoryContentResultSet)result).results;
            mApplication.setPublicCategories(categories);
        }
    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        if (result instanceof User){
            mGetCategoriesRC = HttpRequest.get(this, API.getCategoriesUrl());
        }
        else if (result instanceof ParserModels.CategoryContentResultSet){
            mCallback.onLoginSuccess();
        }
    }


    /**
     * Callback interface for the LogInFragment.
     *
     * @author Edited and documented by Ismael Alonso
     * @version 1.0.0
     */
    public interface LogInFragmentCallback{
        /**
         * Called when the user logs in successfully.
         */
        void onLoginSuccess();
    }
}
