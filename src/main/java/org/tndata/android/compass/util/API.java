package org.tndata.android.compass.util;

import android.support.annotation.NonNull;
import android.util.Log;

import org.tndata.android.compass.BuildConfig;
import org.tndata.android.compass.model.Survey;
import org.tndata.android.compass.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;


/**
 * Class containing all the information about API endpoints and the bodies
 * that should come with the requests.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public abstract class API{
    //Api urls and app configuration
    private static final boolean USE_NGROK_TUNNEL = true;
    private static final String TNDATA_BASE_URL = "https://app.tndata.org/api/";
    private static final String TNDATA_STAGING_URL = "http://staging.tndata.org/api/";
    private static final String NGROK_TUNNEL_URL = "https://tndata.ngrok.io/api/";
    private static final String BASE_URL =
            USE_NGROK_TUNNEL ?
                    NGROK_TUNNEL_URL
                    :
                    BuildConfig.DEBUG ?
                            TNDATA_STAGING_URL
                            :
                            TNDATA_BASE_URL;


    public static String getLogInUrl(){
        return BASE_URL + "auth/token/";
    }

    public static Map<String, String> getLogInBody(@NonNull String email, @NonNull String password){
        Map<String, String> logInBody = new HashMap<>();
        logInBody.put("email", email);
        logInBody.put("password", password);
        return logInBody;
    }

    public static String getSignUpUrl(){
        return BASE_URL + "users/";
    }

    public static Map<String, String> getSignUpBody(@NonNull String email, @NonNull String password,
                                                    @NonNull String firstName, @NonNull String lastName){

        Map<String, String> signUpBody = new HashMap<>();
        signUpBody.put("email", email);
        signUpBody.put("password", password);
        signUpBody.put("first_name", firstName);
        signUpBody.put("last_name", lastName);
        return signUpBody;
    }

    public static String getUserDataUrl(){
        return BASE_URL + "users/";
    }

    public static String getCategoriesUrl(){
        return BASE_URL + "categories/";
    }

    public static String getUserCategoriesUrl(){
        return BASE_URL + "users/categories/";
    }

    public static Map<String, String> getPostUserCategoryBody(int categoryId){
        Map<String, String> postCategoriesBody = new HashMap<>();
        postCategoriesBody.put("category", String.valueOf(categoryId));
        Log.d("API", postCategoriesBody.toString());
        return postCategoriesBody;
    }

    public static Map<String, String> getDeleteUserCategoryBody(int userCategoryId){
        Map<String, String> deleteCategoriesBody = new HashMap<>();
        deleteCategoriesBody.put("usercategory", String.valueOf(userCategoryId));
        Log.d("API", deleteCategoriesBody.toString());
        return deleteCategoriesBody;
    }

    public static String getInstrumentUrl(int instrument){
        return BASE_URL + "survey/instruments/" + instrument + "/";
    }

    public static String getPostSurveyUrl(Survey survey){
        String url = survey.getResponseUrl();
        if (BuildConfig.DEBUG && url.startsWith("https")){
            url = "http" + url.substring(5);
        }
        return url;
    }

    public static Map<String, String> getPostSurveyBody(Survey survey){
        Map<String, String> postSurveyBody = new HashMap<>();
        postSurveyBody.put("question", String.valueOf(survey.getId()));
        if (survey.getQuestionType().equalsIgnoreCase(Constants.SURVEY_OPENENDED)){
            postSurveyBody.put("response", survey.getResponse());
        }
        else{
            postSurveyBody.put("selected_option", String.valueOf(survey.getSelectedOption().getId()));
        }
        return postSurveyBody;
    }

    public static String getPutUserProfileUrl(User user){
        return BASE_URL + "userprofiles/" + user.getUserprofileId() + "/";
    }

    public static Map<String, String> getPutUserProfileBody(User user){
        Map<String, String> putUserProfileBody = new HashMap<>();
        putUserProfileBody.put("timezone", TimeZone.getDefault().getID());
        putUserProfileBody.put("needs_onboarding", String.valueOf(user.needsOnBoarding()));
        return putUserProfileBody;
    }

    public static String getSearchUrl(String query){
        query = query.replace(" ", "%20");
        return BASE_URL + "search/?q=" + query;
    }
}
