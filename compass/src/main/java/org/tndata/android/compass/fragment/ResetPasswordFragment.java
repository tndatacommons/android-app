package org.tndata.android.compass.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.tndata.android.compass.R;
import org.tndata.android.compass.databinding.FragmentResetPasswordBinding;
import org.tndata.android.compass.util.API;

import es.sandwatch.httprequests.HttpRequest;
import es.sandwatch.httprequests.HttpRequestError;


/**
 * A fragment for the reset password screen.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class ResetPasswordFragment
        extends Fragment
        implements
                View.OnClickListener,
                HttpRequest.RequestCallback{

    private FragmentResetPasswordBinding mBinding;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_reset_password, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        mBinding.resetPasswordButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        String email = mBinding.resetPassEmail.getText().toString().trim();
        if (isValidEmail(email)){
            mBinding.resetPassError.setVisibility(View.GONE);
            mBinding.resetPasswordButton.setVisibility(View.INVISIBLE);
            mBinding.resetPasswordProgress.setVisibility(View.VISIBLE);
            HttpRequest.post(this, API.URL.postResetEmailUrl(), API.BODY.postResetEmail(email));
        }
        else{
            mBinding.resetPassError.setVisibility(View.VISIBLE);
            mBinding.resetPassError.setText(R.string.reset_password_error_email);
        }
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        mBinding.resetPasswordProgress.setVisibility(View.INVISIBLE);
        mBinding.resetPasswordFeedback.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestFailed(int requestCode, HttpRequestError error){
        mBinding.resetPasswordProgress.setVisibility(View.INVISIBLE);
        mBinding.resetPasswordButton.setVisibility(View.VISIBLE);
        mBinding.resetPassError.setVisibility(View.VISIBLE);
        mBinding.resetPassError.setText(R.string.reset_password_error_request);
    }

    /**
     * Checks if the email address provided by the user has a valid format.
     *
     * @param email the address to be checked.
     * @return true if the address provided has valid email format, false otherwise.
     */
    private boolean isValidEmail(@NonNull String email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
