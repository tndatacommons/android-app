package org.tndata.android.compass.fragment;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.User;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.NetworkRequest;
import org.tndata.android.compass.util.Parser;

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
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;


public class LogInFragment extends Fragment implements NetworkRequest.RequestCallback, OnClickListener{
    //Listener interface.
    private LogInFragmentListener mListener;

    //UI components
    private EditText mEmail;
    private EditText mPassword;
    private TextView mError;
    private ProgressBar mProgress;
    private Button mLogIn;

    //Attributes
    private String mErrorString = "";
    private int mLogInRequestCode;


    @Override
    public void onAttach(Activity context){
        super.onAttach(context);
        //This makes sure that the host activity has implemented the callback interface.
        //  If not, it throws an exception
        try{
            mListener = (LogInFragmentListener)context;
        }
        catch (ClassCastException ccx){
            throw new ClassCastException(context.toString()
                    + " must implement LoginFragmentListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        mEmail = (EditText)rootView.findViewById(R.id.login_email);
        mPassword = (EditText)rootView.findViewById(R.id.login_password);
        mError = (TextView)rootView.findViewById(R.id.login_error);
        mProgress = (ProgressBar)rootView.findViewById(R.id.login_progress);
        mLogIn = (Button)rootView.findViewById(R.id.login_button);

        mLogIn.setOnClickListener(this);

        return rootView;
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
        String emailAddress = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        if (isValidEmail(emailAddress) && isValidPassword(password)){
            setFormEnabled(false);

            Map<String, String> body = new HashMap<>();
            body.put("email", emailAddress);
            body.put("password", password);
            mLogInRequestCode = NetworkRequest.post(getActivity(), this,
                    Constants.BASE_URL + "auth/token/", "", body);
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
            mErrorString = "Please enter a password";
            return false;
        }
        mErrorString = "";
        return true;
    }

    /**
     * Sets the state of the form.
     *
     * @param enabled true if the form should be enabled, false otherwise.
     */
    private void setFormEnabled(boolean enabled){
        mEmail.setEnabled(enabled);
        mPassword.setEnabled(enabled);
        mLogIn.setEnabled(enabled);
        if (enabled){
            mProgress.setVisibility(View.GONE);
            mError.setVisibility(View.VISIBLE);
            mError.setText(mErrorString);
        }
        else{
            mProgress.setVisibility(View.VISIBLE);
            mError.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        NetworkRequest.cancel(mLogInRequestCode);
        mListener = null;
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        User user = new Parser().parseUser(result);
        if (user.getError().isEmpty()){
            user.setPassword(mPassword.getText().toString().trim());
            mListener.loginSuccess(user);
        }
        else{
            mErrorString = user.getError();
            setFormEnabled(true);
        }
    }

    @Override
    public void onRequestFailed(int requestCode){
        mErrorString = getActivity().getResources().getString(R.string.login_auth_error);
        setFormEnabled(true);
    }


    public interface LogInFragmentListener{
        void loginSuccess(User user);
    }
}
