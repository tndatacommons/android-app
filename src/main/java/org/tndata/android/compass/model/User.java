package org.tndata.android.compass.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


/**
 * Model class for a User.
 *
 * @author Edited by Ismael Alonso
 * @version 1.1.0
 */
public class User extends TDCBase implements Serializable{
    private static final long serialVersionUID = 4582633283983173348L;

    public static final String TYPE = "user";


    @SerializedName("userprofile_id")
    private int mUserProfileId;
    @SerializedName("email")
    private String mEmail;
    //Password ain't ever sent from the API
    private String mPassword;
    @SerializedName("first_name")
    private String mFirstName;
    @SerializedName("last_name")
    private String mLastName;
    @SerializedName("full_name")
    private String mFullName;
    @SerializedName("token")
    private String mToken;
    @SerializedName("date_joined")
    private String mDateJoined;
    @SerializedName("needs_onboarding")
    private boolean mNeedsOnboarding;


    /**
     * Constructor.
     *
     * @param email the email.
     * @param password the password.
     */
    public User(@NonNull String email, @NonNull String password){
        mEmail = email;
        mPassword = password;
    }


    @Override
    protected String getType(){
        return TYPE;
    }

    public void setUserprofileId(int userProfileId){
        mUserProfileId = userProfileId;
    }

    public int getUserprofileId(){
        return mUserProfileId;
    }

    public void setEmail(String email){
        mEmail = email;
    }

    public String getEmail(){
        return mEmail;
    }

    public void setPassword(String password){
        mPassword = password;
    }

    public String getPassword(){
        return mPassword;
    }

    public void setFirstName(String firstName){
        mFirstName = firstName;
    }

    public String getFirstName(){
        return mFirstName;
    }

    public void setLastName(String lastName){
        mLastName = lastName;
    }

    public String getLastName(){
        return mLastName;
    }

    public void setFullName(String fullName){
        mFullName = fullName;
    }

    public String getFullName(){
        return mFullName;
    }

    public void setToken(String token){
        mToken = token;
    }

    public String getToken(){
        return mToken;
    }

    public void setDateJoined(String dateJoined){
        mDateJoined = dateJoined;
    }

    public String getDateJoined(){
        return mDateJoined;
    }

    public void setOnBoardingComplete(){
        mNeedsOnboarding = false;
    }

    public boolean needsOnBoarding(){
        return mNeedsOnboarding;
    }

    @Override
    public String toString(){
        return mFullName + " (uid: " + getId() + ", pid: " + mUserProfileId + "), " + mEmail + ", "
                + (mNeedsOnboarding ? "needs onboarding" : " doesn't need onboarding");
    }
}
