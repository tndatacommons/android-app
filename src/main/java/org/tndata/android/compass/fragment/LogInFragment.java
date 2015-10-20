package org.tndata.android.compass.fragment;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.User;
import org.tndata.android.compass.task.LogInTask;
import org.tndata.android.compass.task.LogInTask.LogInTaskCallback;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;


public class LogInFragment extends Fragment implements LogInTaskCallback, OnClickListener{
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


    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        //This makes sure that the host activity has implemented the callback interface.
        //  If not, it throws an exception
        try{
            mListener = (LogInFragmentListener)activity;
        }
        catch (ClassCastException ccx){
            throw new ClassCastException(activity.toString()
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
        String emailAddress = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        if (isValidEmail(emailAddress) && !password.isEmpty()){
            mError.setVisibility(View.INVISIBLE);
            mErrorString = "";
            mError.setText(mErrorString);
            mProgress.setVisibility(View.VISIBLE);
            mLogIn.setEnabled(false);

            User user = new User();
            user.setEmail(emailAddress);
            user.setPassword(password);
            new LogInTask(this, emailAddress, password).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else{
            mError.setText(mErrorString);
            mError.setVisibility(View.VISIBLE);
        }
    }

    private boolean isValidEmail(CharSequence target){
        if (target == null){
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(target).matches()){
            mErrorString = getActivity().getResources().getString(R.string.login_email_error);
            return false;
        }
        else{
            mErrorString = "";
            return true;
        }
    }

    @Override
    public void logInResult(User result){
        if (result != null){
            if (result.getError().isEmpty()){
                mListener.loginSuccess(result);
            }
            else{
                mErrorString = result.getError();
                mError.setText(mErrorString);
                mError.setVisibility(View.VISIBLE);
                mProgress.setVisibility(View.GONE);
                mLogIn.setEnabled(true);
            }
        }
        else{
            mErrorString = getActivity().getResources().getString(R.string.login_auth_error);
            mError.setText(mErrorString);
            mError.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.GONE);
            mLogIn.setEnabled(true);
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mListener = null;
    }


    public interface LogInFragmentListener{
        void loginSuccess(User user);
    }
}
