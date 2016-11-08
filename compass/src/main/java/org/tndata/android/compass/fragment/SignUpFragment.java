package org.tndata.android.compass.fragment;

import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.databinding.FragmentSignupBinding;
import org.tndata.android.compass.model.TDCCategory;
import org.tndata.android.compass.model.User;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.Tour;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import java.util.List;

import es.sandwatch.httprequests.HttpRequest;
import es.sandwatch.httprequests.HttpRequestError;


/**
 * Fragment that handles the sign up process.
 *
 * @author Edited by Ismael Alonso
 * @version 1.1.0
 */
public class SignUpFragment
        extends Fragment
        implements
                OnClickListener,
                HttpRequest.RequestCallback,
                Parser.ParserCallback{

    private static final String TAG = "SignUpFragment";


    private CompassApplication mApplication;

    //Listener interface
    private SignUpFragmentListener mListener;

    //Binding
    private FragmentSignupBinding mBinding;

    //Attributes
    private String mErrorString;
    private int mSignUpRC;
    private int mGetCategoriesRC;


    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        //Get the application
        mApplication = (CompassApplication)context.getApplicationContext();

        //This makes sure that the host activity has implemented the callback interface.
        //  If not, it throws an exception
        try{
            mListener = (SignUpFragmentListener)context;
        }
        catch (ClassCastException ccx){
            throw new ClassCastException(context.toString()
                    + " must implement SignUpFragmentListener");
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        HttpRequest.cancel(mSignUpRC);
        HttpRequest.cancel(mGetCategoriesRC);
        mListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState){
        MovementMethod movementMethod = LinkMovementMethod.getInstance();
        mBinding.signupTerms.setMovementMethod(movementMethod);

        //Set the listeners
        mBinding.signupButton.setOnClickListener(this);

        mErrorString = "";
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.signup_button:
                doSignUp();
        }
    }

    /**
     * Starts the sign up process, makes sure that the values are correct, disables the form,
     * and sends a request to the API. If the values are not correct, then displays an error.
     */
    private void doSignUp(){
        //Retrieve the values
        String emailAddress = mBinding.signupEmail.getText().toString().trim();
        String password = mBinding.signupPassword.getText().toString().trim();
        String confirmPassword = mBinding.signupConfirmPassword.getText().toString().trim();
        String firstName = mBinding.signupFirstName.getText().toString().trim();
        String lastName = mBinding.signupLastName.getText().toString().trim();

        //If the values check, proceed to sign up
        if (checkFields(emailAddress, password, confirmPassword, firstName, lastName)){
            //Change visibility and disable components
            mErrorString = "";
            setFormEnabled(false);

            JSONObject body = API.BODY.signUp(emailAddress, password, firstName, lastName);
            mSignUpRC = HttpRequest.post(this, API.URL.signUp(), body);
        }
        else{
            setFormEnabled(true);
        }
    }

    /**
     * Checks the input fields, tells whether they are valid, and sets the error string
     * accordingly.
     *
     * @param email the email address.
     * @param pass the password.
     * @param pass2 the password confirmation.
     * @param first the first name.
     * @param last the last name.
     * @return true if everything checks, false otherwise.
     */
    private boolean checkFields(String email, String pass, String pass2, String first, String last){
        if (!isValidEmail(email)){
            return false;
        }
        if (!confirmPasswords(pass, pass2)){
            return false;
        }
        if (first.equals("")){
            mErrorString = getResources().getString(R.string.login_first_name_error);
            return false;
        }
        if (last.equals("")){
            mErrorString = getResources().getString(R.string.login_last_name_error);
            return false;
        }
        return true;
    }

    /**
     * Checks if the email address provided by the user has a valid format.
     *
     * @param email the address to be checked.
     * @return true if the address provided has valid email format, false otherwise.
     */
    private boolean isValidEmail(@NonNull String email){
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            mErrorString = getResources().getString(R.string.login_email_error);
            return false;
        }
        mErrorString = "";
        return true;
    }

    /**
     * Checks whether the passwords are the correct length and match each other.
     *
     * @param passwordA password in the password field.
     * @param passwordB password in the confirm password field.
     * @return true if the passwords are the correct length and match each other, false otherwise.
     */
    private boolean confirmPasswords(@NonNull String passwordA, @NonNull String passwordB){
        if (passwordA.length() >= 5){
            if (passwordA.equals(passwordB)){
                mErrorString = "";
                return true;
            }
            else{
                mErrorString = getResources().getString(R.string.signup_password_mismatch_error);
            }
        }
        else{
            mErrorString = getResources().getString(R.string.signup_password_length_error);
        }
        return false;
    }

    /**
     * Enables or disables the form.
     *
     * @param enabled true if the form should enable, false if the form should disable.
     */
    private void setFormEnabled(boolean enabled){
        mBinding.signupEmail.setEnabled(enabled);
        mBinding.signupPassword.setEnabled(enabled);
        mBinding.signupConfirmPassword.setEnabled(enabled);
        mBinding.signupFirstName.setEnabled(enabled);
        mBinding.signupLastName.setEnabled(enabled);
        mBinding.signupButton.setEnabled(enabled);
        if (enabled){
            mBinding.signupError.setText(mErrorString);
            mBinding.signupError.setVisibility(View.VISIBLE);
            mBinding.signupProgress.setVisibility(View.GONE);
        }
        else{
            mBinding.signupError.setVisibility(View.INVISIBLE);
            mBinding.signupProgress.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mSignUpRC){
            Parser.parse(result, User.class, this);
        }
        else if (requestCode == mGetCategoriesRC){
            Parser.parse(result, ParserModels.CategoryContentResultSet.class, this);
        }
    }

    @Override
    public void onRequestFailed(int requestCode, HttpRequestError error){
        if (error.isServerError()){
            if (error.getMessage().contains("email")){
                try{
                    mErrorString = new JSONObject(error.getMessage()).
                            getJSONArray("email").getString(0);
                }
                catch (JSONException jx){
                    jx.printStackTrace();
                }
            }
        }
        else{
            mErrorString = getActivity().getResources().getString(R.string.signup_error);
        }
        Log.e(TAG, error.toString());
        setFormEnabled(true);
    }

    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){
        if (result instanceof User){
            User user = (User)result;
            user.setPassword(mBinding.signupPassword.getText().toString().trim());
            mApplication.setUser(user);
        }
        else if (result instanceof ParserModels.CategoryContentResultSet){
            List<TDCCategory> categories = ((ParserModels.CategoryContentResultSet)result).results;
            mApplication.setAvailableCategories(categories);
        }
    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        if (result instanceof User){
            mGetCategoriesRC = HttpRequest.get(this, API.URL.getCategories());
        }
        else if (result instanceof ParserModels.CategoryContentResultSet){
            Tour.reset();
            mListener.onSignUpSuccess();
        }
    }

    @Override
    public void onParseFailed(int requestCode){

    }


    /**
     * Listener interface for events triggered from this fragment.
     *
     * @author Edited by Ismael Alonso
     * @version 1.1.0
     */
    public interface SignUpFragmentListener{
        /**
         * Called when the sign up process completes successfully.
         */
        void onSignUpSuccess();
    }
}
