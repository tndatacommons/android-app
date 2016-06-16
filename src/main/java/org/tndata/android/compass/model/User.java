package org.tndata.android.compass.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.google.gson.annotations.SerializedName;

import org.tndata.android.compass.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Model class for a User.
 *
 * @author Edited by Ismael Alonso
 * @version 1.1.0
 */
public class User extends TDCBase{
    public static final String TYPE = "user";

    //Indices/IDs
    public static final int ZIP_CODE = 0;
    public static final int BIRTHDAY = ZIP_CODE+1;
    public static final int SEX = BIRTHDAY+1;
    public static final int EMPLOYED = SEX+1;
    public static final int PARENT = EMPLOYED+1;
    public static final int RELATIONSHIP = PARENT+1;
    public static final int DEGREE = RELATIONSHIP+1;
    public static final int ITEM_COUNT = DEGREE+1;

    private static final @StringRes int AM = R.string.profile_statement_am;
    private static final @StringRes int AM_NOT = R.string.profile_statement_am_not;
    private static final @StringRes int HAVE = R.string.profile_statement_have;
    private static final @StringRes int HAVE_NOT = R.string.profile_statement_have_not;


    //User attributes
    @SerializedName("userprofile_id")
    private int mProfileId;
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
    @SerializedName("maximum_daily_notifications")
    private int mDailyNotifications;


    //Profile attributes
    @SerializedName("zipcode")
    private String mZipCode;
    @SerializedName("birthday")
    private String mBirthday;
    @SerializedName("sex")
    private String mSex;
    @SerializedName("employed")
    private boolean mEmployed;
    @SerializedName("is_parent")
    private boolean mParent;
    @SerializedName("in_relationship")
    private boolean mRelationship;
    @SerializedName("has_degree")
    private boolean mDegree;


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


    /*---------*
     * SETTERS *
     *---------*/

    public void setProfileId(int userProfileId){
        mProfileId = userProfileId;
    }

    public void setEmail(String email){
        mEmail = email;
    }

    public void setPassword(String password){
        mPassword = password;
    }

    public void setFirstName(String firstName){
        mFirstName = firstName;
        setFullName(getFirstName() + " " + getLastName());
    }

    public void setLastName(String lastName){
        mLastName = lastName;
        setFullName(getFirstName() + " " + getLastName());
    }

    public void setFullName(String fullName){
        mFullName = fullName;
    }

    public void setToken(String token){
        mToken = token;
    }

    public void setDateJoined(String dateJoined){
        mDateJoined = dateJoined;
    }

    public void setOnBoardingComplete(){
        mNeedsOnboarding = false;
    }

    public void setDailyNotifications(int dailyNotifications){
        mDailyNotifications = dailyNotifications;
    }


    /*---------*
     * GETTERS *
     *---------*/

    public int getProfileId(){
        return mProfileId;
    }

    public String getEmail(){
        return mEmail;
    }

    public String getPassword(){
        return mPassword;
    }

    public String getFirstName(){
        return mFirstName;
    }

    public String getLastName(){
        return mLastName;
    }

    public String getFullName(){
        return mFullName;
    }

    public String getToken(){
        return mToken;
    }

    public String getDateJoined(){
        return mDateJoined;
    }

    public boolean needsOnBoarding(){
        return mNeedsOnboarding;
    }

    public int getDailyNotifications(){
        return mDailyNotifications;
    }

    /**
     * ZIP code getter.
     *
     * @return the zip code.
     */
    public String getZipCode(){
        return mZipCode != null ? mZipCode : "";
    }

    /**
     * Birthday getter.
     *
     * @return the birthday.
     */
    public String getBirthday(){
        return mBirthday != null ? mBirthday : "";
    }

    public String getFormattedBirthday(){
        if (getBirthday().isEmpty()){
            return "";
        }
        try{
            DateFormat parser = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date birthday = parser.parse(getBirthday());
            DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            return formatter.format(birthday);
        }
        catch (ParseException px){
            px.printStackTrace();
            return "";
        }
    }

    /**
     * Sex getter.
     *
     * @return the sex.
     */
    public String getSex(){
        return mSex != null ? mSex : "";
    }

    /**
     * Tells whether the sex of the user is a male. If unknown male is assumed.
     *
     * @return true if male or unknown, flase otherwise.
     */
    public boolean isMale(){
        return mSex == null || mSex.equalsIgnoreCase("male");
    }

    /**
     * Employed getter.
     *
     * @return whether the user is employed.
     */
    public boolean isEmployed(){
        return mEmployed;
    }

    /**
     * Parent getter.
     *
     * @return whether the user is a parent.
     */
    public boolean isParent(){
        return mParent;
    }

    /**
     * Relationship getter.
     *
     * @return whether the user is in a relationship.
     */
    public boolean inRelationship(){
        return mRelationship;
    }

    /**
     * Degree getter.
     *
     * @return whether the user has a degree.
     */
    public boolean hasDegree(){
        return mDegree;
    }

    /**
     * Gets a valid statement about a particular profile item.
     *
     * @param context a reference to the context.
     * @param itemId the id of the item to be made a statement.
     * @return a valid statement about the requested profile item.
     */
    public String getStatement(Context context, int itemId){
        String mod;
        switch (itemId){
            case ZIP_CODE:
                if (getZipCode().isEmpty()){
                    return context.getString(R.string.profile_zip_question);
                }
                else{
                    return context.getString(R.string.profile_zip, getZipCode());
                }

            case BIRTHDAY:
                if (getBirthday().isEmpty()){
                    return context.getString(R.string.profile_birthday_question);
                }
                else{
                    return context.getString(R.string.profile_birthday, getFormattedBirthday());
                }

            case SEX:
                if (getSex().isEmpty()){
                    return context.getString(R.string.profile_gender_question);
                }
                else{
                    return context.getString(R.string.profile_gender, getSex().toLowerCase());
                }

            case EMPLOYED:
                mod = context.getString(mEmployed ? AM : AM_NOT);
                return context.getString(R.string.profile_employed, mod);

            case PARENT:
                mod = context.getString(mParent ? HAVE : HAVE_NOT);
                return context.getString(R.string.profile_offspring, mod);

            case RELATIONSHIP:
                mod = context.getString(mRelationship ? AM : AM_NOT);
                return context.getString(R.string.profile_relationship, mod);

            case DEGREE:
                mod = context.getString(mDegree ? HAVE : HAVE_NOT);
                return context.getString(R.string.profile_education, mod);

            default:
                throw new IllegalStateException();
        }
    }

    /**
     * Creates a survey question from a particular profile field.
     *
     * @param context a reference to the context.
     * @param itemId the id of the item to be converted to a survey question.
     * @return a survey question for a particular profile field.
     */
    public Survey generateSurvey(Context context, int itemId){
        Survey survey = new Survey();
        survey.setId(itemId);
        List<SurveyOption> options;
        switch (itemId){
            case ZIP_CODE:
                survey.setQuestionType(Survey.QuestionType.OPEN_ENDED);
                survey.setInputType(Survey.InputType.NUMBER);
                survey.setQuestion(context.getString(R.string.profile_zip_question));
                survey.setResponse(getZipCode());
                break;

            case BIRTHDAY:
                survey.setQuestionType(Survey.QuestionType.OPEN_ENDED);
                survey.setInputType(Survey.InputType.DATE);
                survey.setQuestion(context.getString(R.string.profile_birthday_question));
                survey.setResponse(getBirthday());
                break;

            case SEX:
                survey.setQuestionType(Survey.QuestionType.MULTIPLE_CHOICE);
                survey.setQuestion(context.getString(R.string.profile_gender_question));
                options = new ArrayList<>();
                options.add(new SurveyOption(1, "Male"));
                options.add(new SurveyOption(2, "Female"));
                survey.setOptions(options);
                if (getSex().equalsIgnoreCase("male")){
                    survey.setSelectedOption(1);
                }
                else if (getSex().equalsIgnoreCase("female")){
                    survey.setSelectedOption(2);
                }
                break;

            case EMPLOYED:
                survey.setQuestionType(Survey.QuestionType.BINARY);
                survey.setQuestion(context.getString(R.string.profile_employed_question));
                options = new ArrayList<>();
                options.add(new SurveyOption(0, "Yes"));
                options.add(new SurveyOption(1, "No"));
                survey.setOptions(options);
                if (mEmployed){
                    survey.setSelectedOption(0);
                }
                break;

            case PARENT:
                survey.setQuestionType(Survey.QuestionType.BINARY);
                survey.setQuestion(context.getString(R.string.profile_offspring_question));
                options = new ArrayList<>();
                options.add(new SurveyOption(0, "Yes"));
                options.add(new SurveyOption(1, "No"));
                survey.setOptions(options);
                if (mParent){
                    survey.setSelectedOption(0);
                }
                break;

            case RELATIONSHIP:
                survey.setQuestionType(Survey.QuestionType.BINARY);
                survey.setQuestion(context.getString(R.string.profile_relationship_question));
                options = new ArrayList<>();
                options.add(new SurveyOption(0, "Yes"));
                options.add(new SurveyOption(1, "No"));
                survey.setOptions(options);
                if (mRelationship){
                    survey.setSelectedOption(0);
                }
                break;

            case DEGREE:
                survey.setQuestionType(Survey.QuestionType.BINARY);
                survey.setQuestion(context.getString(R.string.profile_education_question));
                options = new ArrayList<>();
                options.add(new SurveyOption(0, "Yes"));
                options.add(new SurveyOption(1, "No"));
                survey.setOptions(options);
                if (mDegree){
                    survey.setSelectedOption(0);
                }
                break;

            default:
                throw new IllegalStateException("");
        }
        return survey;
    }

    /**
     * Updates a profile field given a survey question.
     *
     * @param survey the survey question used to update a profile item.
     */
    public void postSurvey(Survey survey){
        switch ((int)survey.getId()){
            case ZIP_CODE:
                mZipCode = survey.getResponse();
                break;

            case BIRTHDAY:
                mBirthday = survey.getResponse();
                break;

            case SEX:
                if (survey.getSelectedOption().getId() == 1){
                    mSex = "Male";
                }
                else if (survey.getSelectedOption().getId() == 2){
                    mSex = "Female";
                }
                else{
                    mSex = "Prefer not to answer";
                }
                break;

            case EMPLOYED:
                mEmployed = survey.getSelectedOption().getId() == 0;
                break;

            case PARENT:
                mParent = survey.getSelectedOption().getId() == 0;
                break;

            case RELATIONSHIP:
                mRelationship = survey.getSelectedOption().getId() == 0;
                break;

            case DEGREE:
                mDegree = survey.getSelectedOption().getId() == 0;
                break;

            default:
                throw new IllegalStateException("");
        }
    }

    @Override
    public String toString(){
        return mFullName + " (uid: " + getId() + ", pid: " + mProfileId + "), " + mEmail + ", "
                + (mNeedsOnboarding ? "needs on-boarding" : " doesn't need on-boarding");
    }

    @Override
    protected String getType(){
        return TYPE;
    }
}
