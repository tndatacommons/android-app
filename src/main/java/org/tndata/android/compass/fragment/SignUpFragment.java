package org.tndata.android.compass.fragment;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.User;
import org.tndata.android.compass.task.SignUpTask;
import org.tndata.android.compass.task.SignUpTask.SignUpTaskCallback;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


/**
 * Fragment that handles the sign up process.
 *
 * @author Edited by Ismael Alonso
 * @version 1.0.0
 */
public class SignUpFragment extends Fragment implements SignUpTaskCallback, OnClickListener{
    //Listener interface
    private SignUpFragmentListener mListener;

    //UI components
    private EditText mEmail;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private EditText mFirstName;
    private EditText mLastName;
    private TextView mError;
    private ProgressBar mProgress;
    private Button mSignUp;
    private ImageView mInfo;

    //Error string
    private String mErrorString;


    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        //This makes sure that the host activity has implemented the callback interface.
        //  If not, it throws an exception
        try{
            mListener = (SignUpFragmentListener)activity;
        }
        catch (ClassCastException ccx){
            throw new ClassCastException(activity.toString()
                    + " must implement SignUpFragmentListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_signup, container, false);

        //Fetch UI components
        mEmail = (EditText)rootView.findViewById(R.id.signup_email);
        mPassword = (EditText)rootView.findViewById(R.id.signup_password);
        mConfirmPassword = (EditText)rootView.findViewById(R.id.signup_confirm_password);
        mFirstName = (EditText)rootView.findViewById(R.id.signup_first_name);
        mLastName = (EditText)rootView.findViewById(R.id.signup_last_name);
        mError = (TextView)rootView.findViewById(R.id.signup_error);
        mProgress = (ProgressBar)rootView.findViewById(R.id.signup_progress);
        mSignUp = (Button)rootView.findViewById(R.id.signup_button);
        mInfo = (ImageView)rootView.findViewById(R.id.signup_info);

        //Set the listeners
        mSignUp.setOnClickListener(this);
        mInfo.setOnClickListener(this);

        mErrorString = "";

        return rootView;
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.signup_info:
                mListener.showTermsAndConditions();
                break;

            case R.id.signup_button:
                doSignUp();
        }
    }

    /**
     * Starts the signup process, makes sure that the values are correct, disables the form,
     * and sends a request to the API. If the values are not correct, then displays an error.
     */
    private void doSignUp(){
        //Retrieve the values
        String emailAddress = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        String confirmPassword = mConfirmPassword.getText().toString().trim();
        String firstName = mFirstName.getText().toString().trim();
        String lastName = mLastName.getText().toString().trim();

        //If the values check, proceed to signup
        if (checkFields(emailAddress, password, confirmPassword, firstName, lastName)){
            //Change visibility and disable components
            mError.setVisibility(View.INVISIBLE);
            mProgress.setVisibility(View.VISIBLE);
            setFormEnabled(false);

            //Reset the error string
            mErrorString = "";
            mError.setText(mErrorString);

            //Sign up
            new SignUpTask(this, emailAddress, password, firstName,lastName).execute();
        }
        else{
            mError.setText(mErrorString);
            mError.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.GONE);
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
    private boolean isValidEmail(String email){
        if (email == null){
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            mErrorString = getResources().getString(R.string.login_email_error);
            return false;
        }
        else{
            mErrorString = "";
            return true;
        }
    }

    /**
     * Checks whether the passwords are the correct length and match each other.
     *
     * @param passwordA password in the password field.
     * @param passwordB password in the confirm password field.
     * @return true if the passwords are the correct length and match each other, false otherwise.
     */
    private boolean confirmPasswords(String passwordA, String passwordB){
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
        mEmail.setEnabled(enabled);
        mPassword.setEnabled(enabled);
        mConfirmPassword.setEnabled(enabled);
        mFirstName.setEnabled(enabled);
        mLastName.setEnabled(enabled);
        mSignUp.setEnabled(enabled);
        mInfo.setEnabled(enabled);
    }

    @Override
    public void signUpResult(User result){
        //If a user was delivered, notify the listener
        if (result != null){
            mListener.signUpSuccess(result);
        }
        //Otherwise, reset the screen and display the error
        else{
            setFormEnabled(true);
            mErrorString = getActivity().getResources().getString(R.string.signup_auth_error);
            mError.setText(mErrorString);
            mError.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mListener = null;
    }


    /**
     * Listener interface for events triggered from this fragment.
     *
     * @author Edited by Ismael Alonso
     * @version 1.0.0
     */
    public interface SignUpFragmentListener{
        /**
         * Delivers the user to the listener once the signup process completes successfully.
         *
         * @param user the newly created user.
         */
        void signUpSuccess(@NonNull User user);

        /**
         * Called when the info button is clicked.
         */
        void showTermsAndConditions();
    }
}
