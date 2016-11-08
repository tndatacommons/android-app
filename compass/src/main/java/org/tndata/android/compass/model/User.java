package org.tndata.android.compass.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import org.tndata.android.compass.R;
import org.tndata.compass.model.Survey;
import org.tndata.compass.model.SurveyOption;
import org.tndata.compass.model.TDCBase;

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
public class User extends TDCBase {
    public static final String TYPE = "user";
    private static final String PREFERENCES_NAME = "CompassUserPreferences";

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
    private long mProfileId;
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
     * Explicit default constructor.
     */
    public User(){

    }

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

    public void setPassword(String password){
        mPassword = password;
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

    public long getProfileId(){
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

    public boolean isFemale(){
        return mSex != null && mSex.equalsIgnoreCase("female");
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

    /**
     * Writes this user to shared preferences.
     *
     * @param context a reference to the context.
     */
    public void writeToSharedPreferences(@NonNull Context context){
        SharedPreferences user = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = user.edit();
        editor.putLong("user.id", getId());
        editor.putLong("user.profileId", mProfileId);
        editor.putString("user.email", mEmail);
        editor.putString("user.password", mPassword);
        editor.putString("user.firstName", mFirstName);
        editor.putString("user.lastName", mLastName);
        editor.putString("user.token", mToken);
        editor.putString("user.dateJoined", mDateJoined);
        editor.putBoolean("user.needsOnBoarding", mNeedsOnboarding);
        editor.putInt("user.dailyNotifications", mDailyNotifications);
        editor.putString("user.zipCode", mZipCode);
        editor.putString("user.birthday", mBirthday);
        editor.putString("user.sex", mSex);
        editor.putBoolean("user.employed", mEmployed);
        editor.putBoolean("user.parent", mParent);
        editor.putBoolean("user.relationship", mRelationship);
        editor.putBoolean("user.degree", mDegree);
        editor.apply();
    }

    /**
     * Reads a user from its shared preferences file.
     *
     * @param context a reference to the context.
     * @return the user if it exists, null if it doesn't.
     */
    public static User getFromPreferences(@NonNull Context context){
        //Open the shared preferences file for the user and check if they exist
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        long id = prefs.getLong("user.id", -1);
        Log.d("User", "Id: " + id);
        if (id == -1){
            //If not, return null
            return null;
        }

        User user = new User();
        user.setId(id);
        user.mProfileId = prefs.getLong("user.profileId", -1);
        user.mEmail = prefs.getString("user.email", "");
        user.mPassword = prefs.getString("user.password", "");
        user.mFirstName = prefs.getString("user.firstName", "");
        user.mLastName = prefs.getString("user.lastName", "");
        user.mFullName = user.mFirstName + " " + user.mLastName;
        user.mToken = prefs.getString("user.token", "");
        user.mDateJoined = prefs.getString("user.dateJoined", "");
        user.mNeedsOnboarding = prefs.getBoolean("user.needsOnBoarding", true);
        user.mDailyNotifications = prefs.getInt("user.dailyNotifications", 5);
        user.mZipCode = prefs.getString("user.zipCode", "");
        user.mBirthday = prefs.getString("user.birthday", "");
        user.mSex = prefs.getString("user.sex", "");
        user.mEmployed = prefs.getBoolean("user.employed", false);
        user.mParent = prefs.getBoolean("user.parent", false);
        user.mRelationship = prefs.getBoolean("user.relationship", false);
        user.mDegree = prefs.getBoolean("user.degree", false);
        return user;
    }

    /**
     * Deletes a user from shared preference.
     *
     * @param context a reference to the context.
     */
    public static void deleteFromPreferences(@NonNull Context context){
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).edit().clear().commit();
    }
}
