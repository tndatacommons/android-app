package org.tndata.android.grow.fragment;

import org.tndata.android.grow.R;
import org.tndata.android.grow.model.User;
import org.tndata.android.grow.task.LoginTask;
import org.tndata.android.grow.task.LoginTask.LoginTaskListener;

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
import android.widget.TextView;

public class LoginFragment extends Fragment implements LoginTaskListener {
    private Button mLoginButton;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private TextView mErrorTextView;
    private String mErrorString = "";
    private LoginFragmentListener mCallback;

    public interface LoginFragmentListener {
        public void loginSuccess(User user);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_login, container, false);
        mEmailEditText = (EditText) v.findViewById(R.id.login_email_edittext);
        mPasswordEditText = (EditText) v
                .findViewById(R.id.login_password_edittext);
        mErrorTextView = (TextView) v.findViewById(R.id.login_error_textview);
        mLoginButton = (Button) v.findViewById(R.id.login_login_button);
        mLoginButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                doLogin();
            }
        });
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity); // This makes sure that the container activity
        // has implemented the callback interface. If not, it throws an
        // exception
        try {
            mCallback = (LoginFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement LoginFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    private void doLogin() {
        String emailAddress = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        if (isValidEmail(emailAddress) && !password.isEmpty()) {
            mErrorTextView.setVisibility(View.INVISIBLE);
            mErrorString = "";
            mErrorTextView.setText(mErrorString);
            User user = new User();
            user.setEmail(emailAddress);
            user.setPassword(password);
            new LoginTask(this).executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR, user);
        } else {
            mErrorTextView.setText(mErrorString);
            mErrorTextView.setVisibility(View.VISIBLE);
        }
    }

    private boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(target).matches()) {
            mErrorString = getActivity().getResources().getString(
                    R.string.login_email_error);
            return false;
        } else {
            mErrorString = "";
            return true;
        }
    }

    @Override
    public void loginResult(User result) {
        if (result != null) {
            if (result.getError().isEmpty()) {
                mCallback.loginSuccess(result);
            } else {
                mErrorString = result.getError();
                mErrorTextView.setText(mErrorString);
                mErrorTextView.setVisibility(View.VISIBLE);
            }
        } else {
            mErrorString = getActivity().getResources().getString(
                    R.string.login_auth_error);
            mErrorTextView.setText(mErrorString);
            mErrorTextView.setVisibility(View.VISIBLE);
        }

    }
}
