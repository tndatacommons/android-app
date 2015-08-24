package org.tndata.android.compass.fragment;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.User;
import org.tndata.android.compass.task.SignUpTask;
import org.tndata.android.compass.task.SignUpTask.SignUpTaskListener;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.Animator.AnimatorListener;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


public class SignUpFragment extends Fragment implements SignUpTaskListener, OnClickListener{
    //Listener interface.
    private SignUpFragmentListener mListener;

    //UI components
    private LinearLayout mSignupContainer;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private EditText mFirstName;
    private EditText mLastName;
    private TextView mError;
    private ProgressBar mProgress;
    private Button mSignUp;

    private String mErrorString;
    private boolean mEmailViewIsHidden;


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

        //First screen UI components
        ImageView info = (ImageView)rootView.findViewById(R.id.signup_info);
        TextView signUpWithEmail = (TextView)rootView.findViewById(R.id.signup_with_email);

        info.setOnClickListener(this);
        signUpWithEmail.setOnClickListener(this);

        //Second screen UI components
        mSignupContainer = (LinearLayout)rootView.findViewById(R.id.signup_container);
        mEmail = (EditText)rootView.findViewById(R.id.signup_email);
        mPassword = (EditText)rootView.findViewById(R.id.signup_password);
        mConfirmPassword = (EditText)rootView.findViewById(R.id.signup_confirm_password);
        mFirstName = (EditText)rootView.findViewById(R.id.signup_first_name);
        mLastName = (EditText)rootView.findViewById(R.id.signup_last_name);
        mError = (TextView)rootView.findViewById(R.id.signup_error);
        mProgress = (ProgressBar)rootView.findViewById(R.id.signup_progress);
        mSignUp = (Button)rootView.findViewById(R.id.signup_button);

        mSignUp.setOnClickListener(this);

        mEmailViewIsHidden = false;
        hideEmailView();

        mErrorString = "";

        return rootView;
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.signup_info:
                mListener.showTermsAndConditions();
                break;

            case R.id.signup_with_email:
                showEmailView();
                break;

            case R.id.signup_button:
                doSignUp();
        }
    }

    private void showEmailView(){
        if (mEmailViewIsHidden){
            int height = mSignupContainer.getMeasuredHeight();
            ObjectAnimator anim = ObjectAnimator.ofFloat(mSignupContainer,
                    "translationY", height, 0);
            anim.setDuration(500);

            anim.addListener(new AnimatorListener(){
                @Override
                public void onAnimationStart(Animator animation){
                    mSignupContainer.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation){
                    mEmailViewIsHidden = false;
                }

                @Override
                public void onAnimationRepeat(Animator animation){
                    //Unused
                }

                @Override
                public void onAnimationCancel(Animator animation){
                    //Unused
                }
            });

            mSignupContainer.setVisibility(View.VISIBLE);
            anim.start();
        }
    }

    public void hideEmailView(){
        if (!mEmailViewIsHidden){
            int height = mSignupContainer.getMeasuredHeight();
            ObjectAnimator anim = ObjectAnimator.ofFloat(mSignupContainer,
                    "translationY", 0, height);
            anim.setDuration(500);

            anim.addListener(new AnimatorListener(){
                @Override
                public void onAnimationStart(Animator animation){
                    //Unused
                }

                @Override
                public void onAnimationEnd(Animator animation){
                    mSignupContainer.setVisibility(View.GONE);
                    mEmailViewIsHidden = true;
                }

                @Override
                public void onAnimationRepeat(Animator animation){
                    //Unused
                }

                @Override
                public void onAnimationCancel(Animator animation){
                    //Unused
                }
            });

            anim.start();
        }
    }

    private void doSignUp(){
        String emailAddress = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        String confirmPassword = mConfirmPassword.getText().toString();
        String firstName = mFirstName.getText().toString();
        String lastName = mLastName.getText().toString();
        if (isValidEmail(emailAddress) && confirmPasswords(password, confirmPassword)){
            mError.setVisibility(View.INVISIBLE);
            mProgress.setVisibility(View.VISIBLE);
            mSignUp.setEnabled(false);
            mErrorString = "";
            mError.setText(mErrorString);
            User user = new User();
            user.setEmail(emailAddress);
            user.setPassword(password);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.onBoardingComplete(false);
            new SignUpTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, user);
        }
        else{
            mError.setText(mErrorString);
            mError.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.GONE);
        }
    }

    private boolean confirmPasswords(String passwordA, String passwordB){
        if (passwordA.equals(passwordB)){
            if (passwordA.length() >= 5){
                mErrorString = "";
                return true;
            }
            else{
                mErrorString = getActivity().getResources().getString(
                        R.string.signup_password_length_error);
                return false;
            }
        }
        else{
            mErrorString = getActivity().getResources().getString(
                    R.string.signup_password_mismatch_error);
            return false;
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
    public void signUpResult(User result){
        if (result != null){
            mListener.signUpSuccess(result);
        }
        else{
            mErrorString = getActivity().getResources().getString(R.string.signup_auth_error);
            mError.setText(mErrorString);
            mError.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.GONE);
            mSignUp.setEnabled(true);
        }
    }

    public boolean isEmailViewShown(){
        return !mEmailViewIsHidden;
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mListener = null;
    }


    public interface SignUpFragmentListener{
        public void signUpSuccess(User user);

        public void showTermsAndConditions();
    }
}
