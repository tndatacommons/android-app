package org.tndata.android.compass.util;

import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.BuildConfig;
import org.tndata.android.compass.model.Place;
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

    public static JSONObject getLogInBody(@NonNull String email, @NonNull String password){
        JSONObject logInBody = new JSONObject();
        try{
            logInBody.put("email", email);
            logInBody.put("password", password);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return logInBody;
    }

    public static String getSignUpUrl(){
        return BASE_URL + "users/";
    }

    public static JSONObject getSignUpBody(@NonNull String email, @NonNull String password,
                                                    @NonNull String firstName, @NonNull String lastName){

        JSONObject signUpBody = new JSONObject();
        try{
            signUpBody.put("email", email);
            signUpBody.put("password", password);
            signUpBody.put("first_name", firstName);
            signUpBody.put("last_name", lastName);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
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

    public static JSONObject getPostUserCategoryBody(int categoryId){
        JSONObject postCategoriesBody = new JSONObject();
        try{
            postCategoriesBody.put("category", categoryId);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return postCategoriesBody;
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

    public static JSONObject getPostSurveyBody(Survey survey){
        JSONObject postSurveyBody = new JSONObject();
        try{
            postSurveyBody.put("question", survey.getId());
            if (survey.getQuestionType().equalsIgnoreCase(Constants.SURVEY_OPENENDED)){
                postSurveyBody.put("response", survey.getResponse());
            }
            else{
                postSurveyBody.put("selected_option", survey.getSelectedOption().getId());
            }
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return postSurveyBody;
    }

    public static String getUserProfileUrl(){
        return BASE_URL + "userprofiles/";
    }

    public static String getPutUserProfileUrl(User user){
        return BASE_URL + "userprofiles/" + user.getUserprofileId() + "/";
    }

    public static JSONObject getPutUserProfileBody(User user){
        JSONObject putUserProfileBody = new JSONObject();
        try{
            putUserProfileBody.put("timezone", TimeZone.getDefault().getID());
            putUserProfileBody.put("needs_onboarding", user.needsOnBoarding());
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return putUserProfileBody;
    }

    public static String getSearchUrl(String query){
        query = query.replace(" ", "%20");
        return BASE_URL + "search/?q=" + query;
    }

    public static String getUserActionUrl(int actionId){
        return BASE_URL + "users/actions/?action=" + actionId;
    }

    public static String getPutTriggerUrl(int actionMappingId){
        return BASE_URL + "users/actions/" + actionMappingId + "/";
    }

    public static JSONObject getPutTriggerBody(String time, String rrule, String date){
        JSONObject putTriggerBody = new JSONObject();
        try{
            putTriggerBody.put("custom_trigger_time", time);
            putTriggerBody.put("custom_trigger_rrule", rrule);
            putTriggerBody.put("custom_trigger_date", date);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return putTriggerBody;
    }

    public static String getTodaysActionsUrl(){
        return BASE_URL + "users/actions/?today=1";
    }

    public static String getRandomRewardUrl(){
        return BASE_URL + "rewards/?random=1";
    }

    public static String getUserGoalProgressUrl(){
        return BASE_URL + "users/goals/progress/average/";
    }

    public static String getPostUserGoalProgressUrl(){
        return BASE_URL + "users/goals/progress/";
    }

    public static JSONObject getPostUserGoalProgressBody(int goalId, int progress){
        JSONObject postUserGoalProgressBody = new JSONObject();
        try{
            postUserGoalProgressBody.put("goal", goalId);
            postUserGoalProgressBody.put("daily_checkin", progress);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return postUserGoalProgressBody;
    }

    public static String getGoalUrl(int goalId){
        return BASE_URL + "goals/" + goalId + "/";
    }

    public static String getBehaviorsUrl(int goalId){
        return BASE_URL + "behaviors/?goal=" + goalId;
    }

    public static String getPostGoalUrl(){
        return BASE_URL + "users/goals/";
    }

    public static JSONObject getPostGoalBody(int goalId){
        JSONObject postGoalBody = new JSONObject();
        try{
            postGoalBody.put("goal", goalId);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return postGoalBody;
    }

    public static String getPostBehaviorUrl(){
        return BASE_URL + "users/behaviors/";
    }

    public static JSONObject getPostBehaviorBody(int behaviorId){
        JSONObject postBehaviorBody = new JSONObject();
        try{
            postBehaviorBody.put("behavior", behaviorId);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return postBehaviorBody;
    }

    public static String getGoalsUrl(int categoryId){
        return BASE_URL + "goals/?category=" + categoryId;
    }

    public static String getDeleteGoalUrl(int goalMappingId){
        return BASE_URL + "users/goals/" + goalMappingId + "/";
    }

    public static String getPostActionReportUrl(int mappingId){
        return BASE_URL + "users/actions/" + mappingId + "/complete/";
    }

    public static JSONObject getPostActionReportBody(String state){
        JSONObject postActionReportBody = new JSONObject();
        try{
            postActionReportBody.put("state", state);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return postActionReportBody;
    }

    public static JSONObject getPostActionReportBody(String state, String length){
        JSONObject postActionReportBody = new JSONObject();
        try{
            postActionReportBody.put("state", state);
            postActionReportBody.put("length", length);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return postActionReportBody;
    }

    public static String getPackageUrl(int packageId){
        return BASE_URL + "users/packages/" + packageId + "/";
    }

    public static String getPutConsentAcknowledgementUrl(int packageId){
        return BASE_URL + "users/packages/" + packageId + "/";
    }

    public static JSONObject getPutConsentAcknowledgementBody(){
        JSONObject putConsentAcknowledgementBody = new JSONObject();
        try{
            putConsentAcknowledgementBody.put("accepted", true);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return putConsentAcknowledgementBody;
    }

    public static String getPrimaryPlacesUrl(){
        return BASE_URL + "places/";
    }

    public static String getPostPutPlaceUrl(Place place){
        String url =  BASE_URL + "users/places/";
        if (place.getId() != -1){
            url += place.getId() + "/";
        }
        return url;
    }

    public static JSONObject getPostPutPlaceBody(Place place){
        JSONObject postPutPlaceBody = new JSONObject();
        try{
            postPutPlaceBody.put("place", place.getName());
            postPutPlaceBody.put("latitude", place.getLatitude());
            postPutPlaceBody.put("longitude", place.getLongitude());
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return postPutPlaceBody;
    }

    public static String getSurveyUrl(String survey){
        return BASE_URL + "survey/" + survey;
    }

    public static String getPutSnoozeUrl(int notificationId){
        return BASE_URL + "notifications/" + notificationId + "/";
    }

    public static JSONObject getPutSnoozeBody(String date, String time){
        JSONObject putSnoozeBody = new JSONObject();
        try{
            putSnoozeBody.put("date", date);
            putSnoozeBody.put("time", time);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return putSnoozeBody;
    }

    public static String getPostDeviceRegistrationUrl(){
        return BASE_URL + "notifications/devices/";
    }

    public static JSONObject getPostDeviceRegistrationBody(String registrationId, String deviceId){
        JSONObject postDeviceRegistrationBody = new JSONObject();
        try{
            postDeviceRegistrationBody.put("registration_id", registrationId);
            postDeviceRegistrationBody.put("device_name", Build.MANUFACTURER + " " + Build.PRODUCT);
            if (deviceId != null){
                postDeviceRegistrationBody.put("device_id", deviceId);
            }
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return postDeviceRegistrationBody;
    }

    public static String getDeleteBehaviorURL(int mappingId){
        return BASE_URL + "users/behaviors/" + mappingId + "/";
    }

    public static String getActionsUrl(int behaviorId){
        return BASE_URL + "actions/" + "?behavior=" + behaviorId;
    }

    public static String getPostActionUrl(){
        return BASE_URL + "users/actions/";
    }

    public static JSONObject getPostActionBody(int goalId, int actionId){
        JSONObject postActionBody = new JSONObject();
        try{
            postActionBody.put("action", actionId);
            postActionBody.put("primary_goal", goalId);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return postActionBody;
    }

    public static String getDeleteActionUrl(int mappingId){
        return BASE_URL + "users/actions/" + mappingId + "/";
    }

    public static String getLogOutUrl(){
        return BASE_URL + "auth/logout/";
    }

    public static JSONObject getLogOutBody(String registrationId){
        JSONObject logOutBody = new JSONObject();
        try{
            logOutBody.put("registration_id", registrationId);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return logOutBody;
    }

    public static String getDeleteCategoryUrl(int mappingId){
        return BASE_URL + "users/categories/" + mappingId + "/";
    }
}
