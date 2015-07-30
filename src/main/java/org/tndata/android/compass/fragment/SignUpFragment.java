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
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SignUpFragment extends Fragment implements SignUpTaskListener {
    private Button mSignUpButton;
    private TextView mSignUpEmailButton;
    private TextView mErrorTextView;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mPasswordConfirmEditText;
    private EditText mFirstNameEditText;
    private EditText mLastNameEditText;
    private RelativeLayout mEmailContainerView;
    private ImageView mPrivacyButtonImageView;
    private String mErrorString = "";
    private boolean mEmailViewIsHidden = false;
    private SignUpFragmentListener mCallback;

    public interface SignUpFragmentListener {
        public void signUpSuccess(User user);

        public void showTermsAndConditions();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_signup, container, false);
        mPrivacyButtonImageView = (ImageView) v
                .findViewById(R.id.signup_privacy_button_imageview);
        mPrivacyButtonImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mCallback.showTermsAndConditions();
            }
        });
        mEmailEditText = (EditText) v.findViewById(R.id.signup_email_edittext);
        mPasswordEditText = (EditText) v
                .findViewById(R.id.signup_password_edittext);
        mPasswordConfirmEditText = (EditText) v
                .findViewById(R.id.signup_password_confirm_edittext);
        mFirstNameEditText = (EditText) v
                .findViewById(R.id.signup_first_name_edittext);
        mLastNameEditText = (EditText) v
                .findViewById(R.id.signup_last_name_edittext);
        mErrorTextView = (TextView) v.findViewById(R.id.signup_error_textview);
        mSignUpButton = (Button) v.findViewById(R.id.signup_signup_button);
        mSignUpButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                doSignUp();
            }
        });
        mSignUpEmailButton = (TextView) v
                .findViewById(R.id.signup_email_button);
        mSignUpEmailButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showEmailView();
            }
        });
        mEmailContainerView = (RelativeLayout) v
                .findViewById(R.id.signup_email_container);
        hideEmailView();
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity); // This makes sure that the container activity
        // has implemented the callback interface. If not, it throws an
        // exception
        try {
            mCallback = (SignUpFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement SignUpFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @Override
    public void signUpResult(User result) {
        if (result != null) {
            mCallback.signUpSuccess(result);
        } else {
            mErrorString = getActivity().getResources().getString(
                    R.string.signup_auth_error);
            mErrorTextView.setText(mErrorString);
            mErrorTextView.setVisibility(View.VISIBLE);
        }
    }

    private void doSignUp() {
        String emailAddress = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        String confirmPassword = mPasswordConfirmEditText.getText().toString();
        String firstName = mFirstNameEditText.getText().toString();
        String lastName = mLastNameEditText.getText().toString();
        if (isValidEmail(emailAddress)
                && confirmPasswords(password, confirmPassword)) {
            mErrorTextView.setVisibility(View.INVISIBLE);
            mErrorString = "";
            mErrorTextView.setText(mErrorString);
            User user = new User();
            user.setEmail(emailAddress);
            user.setPassword(password);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.needsOnBoarding();
            new SignUpTask(this).executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR, user);
        } else {
            mErrorTextView.setText(mErrorString);
            mErrorTextView.setVisibility(View.VISIBLE);
        }
    }

    private boolean confirmPasswords(String passwordA, String passwordB) {
        if (passwordA.equals(passwordB)) {
            if (passwordA.length() >= 5) {
                mErrorString = "";
                return true;
            } else {
                mErrorString = getActivity().getResources().getString(
                        R.string.signup_password_length_error);
                return false;
            }
        } else {
            mErrorString = getActivity().getResources().getString(
                    R.string.signup_password_mismatch_error);
            return false;
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

    public boolean isEmailViewShown() {
        return !mEmailViewIsHidden;
    }

    private void showEmailView() {
        if (mEmailViewIsHidden) {
            int height = mEmailContainerView.getMeasuredHeight();
            ObjectAnimator anim = ObjectAnimator.ofFloat(mEmailContainerView,
                    "translationY", height, 0);
            anim.setDuration(500);

            anim.addListener(new AnimatorListener() {

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mEmailViewIsHidden = false;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    mEmailContainerView.setVisibility(View.VISIBLE);
                }
            });

            mEmailContainerView.setVisibility(View.VISIBLE);
            anim.start();
        }
    }

    public void hideEmailView() {
        if (!mEmailViewIsHidden) {
            int height = mEmailContainerView.getMeasuredHeight();
            ObjectAnimator anim = ObjectAnimator.ofFloat(mEmailContainerView,
                    "translationY", 0, height);
            anim.setDuration(500);
            anim.start();
            anim.addListener(new AnimatorListener() {

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mEmailContainerView.setVisibility(View.GONE);
                    mEmailViewIsHidden = true;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }

                @Override
                public void onAnimationStart(Animator animation) {
                }
            });
        }
    }

}
