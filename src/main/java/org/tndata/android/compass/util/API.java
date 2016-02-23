package org.tndata.android.compass.util;

import android.os.Build;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.BuildConfig;
import org.tndata.android.compass.model.*;

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
    public static final boolean STAGING = !BuildConfig.DEBUG;
    private static final boolean USE_NGROK_TUNNEL = false;
    private static final String TNDATA_BASE_URL = "https://app.tndata.org/api/";
    private static final String TNDATA_STAGING_URL = "http://staging.tndata.org/api/";
    private static final String NGROK_TUNNEL_URL = "https://tndata.ngrok.io/api/";
    //This yields a warning, but flipping the boolean above is the easiest
    //  way of switching base urls
    private static final String BASE_URL =
            USE_NGROK_TUNNEL ?
                    NGROK_TUNNEL_URL
                    :
                    STAGING ?
                            TNDATA_STAGING_URL
                            :
                            TNDATA_BASE_URL;


    /*----------------*
     * AUTHENTICATION *
     *----------------*/

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

    public static String getLogOutUrl(){
        return BASE_URL + "auth/logout/";
    }

    public static JSONObject getLogOutBody(@NonNull String registrationId){
        JSONObject logOutBody = new JSONObject();
        try{
            logOutBody.put("registration_id", registrationId);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return logOutBody;
    }

    public static String getSignUpUrl(){
        return BASE_URL + "users/";
    }

    public static JSONObject getSignUpBody(@NonNull String email, @NonNull String password,
                                           @NonNull String firstName, @NonNull String lastName){

        JSONObject signUpBody = new JSONObject();
        try{
            signUpBody.put("email", email)
                    .put("password", password)
                    .put("first_name", firstName)
                    .put("last_name", lastName);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return signUpBody;
    }

    public static String getPostDeviceRegistrationUrl(){
        return BASE_URL + "notifications/devices/";
    }

    public static JSONObject getPostDeviceRegistrationBody(@NonNull String registrationId,
                                                           @NonNull String deviceId){
        JSONObject postDeviceRegistrationBody = new JSONObject();
        try{
            postDeviceRegistrationBody.put("registration_id", registrationId)
                    .put("device_name", Build.MANUFACTURER + " " + Build.PRODUCT)
                    .put("device_id", deviceId);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return postDeviceRegistrationBody;
    }


    /*------------------------------*
     * APPLICATION DATA AND LIBRARY *
     *------------------------------*/

    //User data
    public static String getUserDataUrl(){
        return BASE_URL + "users/";
    }


    //Search
    public static String getSearchUrl(@NonNull String query){
        query = query.replace(" ", "%20");
        return BASE_URL + "search/?q=" + query;
    }


    //Categories
    public static String getCategoriesUrl(){
        return BASE_URL + "categories/";
    }

    public static String getCategoryUrl(long categoryId){
        return BASE_URL + "categories/" + categoryId + "/";
    }

    public static String getDeleteCategoryUrl(@NonNull UserCategory userCategory){
        return BASE_URL + "users/categories/" + userCategory.getId() + "/";
    }

    public static String getUserCategoriesUrl(){
        return BASE_URL + "users/categories/";
    }

    public static JSONObject getPostCategoryBody(@NonNull CategoryContent category){
        JSONObject postCategoriesBody = new JSONObject();
        try{
            postCategoriesBody.put("category", category.getId());
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return postCategoriesBody;
    }


    //Goals
    public static String getGoalsUrl(@NonNull CategoryContent category){
        return BASE_URL + "goals/?category=" + category.getId();
    }

    public static String getGoalUrl(long goalId){
        return BASE_URL + "goals/" + goalId + "/";
    }

    //Custom goals
    public static String getPostCustomGoalUrl(){
        return BASE_URL + "users/customgoals/";
    }

    public static String getPutCustomGoalUrl(@NonNull CustomGoal customGoal){
        return BASE_URL + "users/customgoals/" + customGoal.getId() + "/";
    }

    public static JSONObject getPostPutCustomGoalBody(@NonNull CustomGoal customGoal){
        JSONObject postPutCustomGoalBody = new JSONObject();
        try{
            postPutCustomGoalBody.put("title", customGoal.getTitle());
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return postPutCustomGoalBody;
    }

    //Goal generic getters
    public static String getDeleteGoalUrl(@NonNull Goal goal){
        if (goal instanceof UserGoal){
            return BASE_URL + "users/goals/" + goal.getId() + "/";
        }
        else if (goal instanceof CustomGoal){
            return BASE_URL + "users/customgoals/" + goal.getId() + "/";
        }
        else{
            return "";
        }
    }


    //Behaviors
    public static String getBehaviorsUrl(@NonNull GoalContent goal){
        return BASE_URL + "behaviors/?goal=" + goal.getId();
    }

    public static String getBehaviorUrl(long behaviorId){
        return BASE_URL + "behaviors/" + behaviorId + "/";
    }

    public static String getPostBehaviorUrl(){
        return BASE_URL + "users/behaviors/";
    }

    public static JSONObject getPostBehaviorBody(@NonNull BehaviorContent behavior, @NonNull GoalContent goal,
                                                 @NonNull CategoryContent category){
        JSONObject postBehaviorBody = new JSONObject();
        try{
            postBehaviorBody.put("behavior", behavior.getId())
                    .put("goal", goal.getId())
                    .put("category", category.getId());
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return postBehaviorBody;
    }

    public static String getDeleteBehaviorUrl(@NonNull UserBehavior userBehavior){
        return BASE_URL + "users/behaviors/" + userBehavior.getId() + "/";
    }


    //Actions
    public static String getActionsUrl(@NonNull BehaviorContent behavior){
        return BASE_URL + "actions/?behavior=" + behavior.getId();
    }

    public static String getTodaysActionsUrl(){
        return BASE_URL + "users/actions/?today=1";
    }

    /**
     * Gets the URL to fetch a particular user action. Hitting this endpoint will result
     * in the retrieval of an unwrapped UserAction JSON object.
     *
     * @param actionMappingId the mapping id of the user action.
     * @return the url of the endpoint to retrieve the requested user action.
     */
    public static String getActionUrl(int actionMappingId){
        return BASE_URL + "users/actions/" + actionMappingId + "/";
    }

    public static String getPostActionUrl(){
        return BASE_URL + "users/actions/";
    }

    public static JSONObject getPostActionBody(@NonNull ActionContent action, @NonNull BehaviorContent behavior,
                                               @NonNull GoalContent goal, @NonNull CategoryContent category){
        JSONObject postActionBody = new JSONObject();
        try{
            postActionBody.put("action", action.getId())
                    .put("behavior", behavior.getId())
                    .put("goal", goal.getId())
                    .put("category", category.getId());
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return postActionBody;
    }

    //Custom actions
    public static String getCustomActionUrl(int customActionId){
        return BASE_URL + "users/customactions/" + customActionId + "/";
    }

    public static String getPostCustomActionUrl(){
        return BASE_URL + "users/customactions/";
    }

    public static String getPutCustomActionUrl(@NonNull CustomAction customAction){
        return BASE_URL + "users/customactions/" + customAction.getId() + "/";
    }

    public static JSONObject getPostPutCustomActionBody(@NonNull CustomAction customAction,
                                                        @NonNull CustomGoal customGoal){

        JSONObject postPutCustomActionBody = new JSONObject();
        try{
            postPutCustomActionBody.put("title", customAction.getTitle())
                    .put("notification_text", customAction.getNotificationText())
                    .put("customgoal", customGoal.getId());
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return postPutCustomActionBody;
    }

    //Generic action getters
    public static String getDeleteActionUrl(@NonNull Action action){
        if (action instanceof UserAction){
            return BASE_URL + "users/actions/" + action.getId() + "/";
        }
        else if (action instanceof CustomAction){
            return BASE_URL + "users/customactions/" + action.getId() + "/";
        }
        else{
            return "";
        }
    }


    //Triggers
    public static String getPutTriggerUrl(@NonNull Action action){
        if (action instanceof UserAction){
            return BASE_URL + "users/actions/" + action.getId() + "/";
        }
        else if (action instanceof CustomAction){
            return BASE_URL + "users/customactions/" + action.getId() + "/";
        }
        else{
            return "";
        }
    }

    public static JSONObject getPutTriggerBody(@NonNull String time, @NonNull String rrule,
                                               @NonNull String date){
        JSONObject putTriggerBody = new JSONObject();
        try{
            putTriggerBody.put("custom_trigger_time", time)
                    .put("custom_trigger_rrule", rrule)
                    .put("custom_trigger_date", date);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return putTriggerBody;
    }


    /*--------------------*
     * PROGRESS REPORTING *
     *--------------------*/

    public static String getUserGoalProgressUrl(){
        return BASE_URL + "users/goals/progress/average/";
    }

    public static String getPostUserGoalProgressUrl(){
        return BASE_URL + "users/goals/progress/";
    }

    //TODO
    public static JSONObject getPostUserGoalProgressBody(@NonNull GoalContent goal, int progress){
        JSONObject postUserGoalProgressBody = new JSONObject();
        try{
            postUserGoalProgressBody.put("goal", goal.getId())
                    .put("daily_checkin", progress);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return postUserGoalProgressBody;
    }

    public static String getRandomRewardUrl(){
        return BASE_URL + "rewards/?random=1";
    }


    /*--------------------*
     * NOTIFICATION LAYER *
     *--------------------*/

    public static String getPutSnoozeUrl(int notificationId){
        return BASE_URL + "notifications/" + notificationId + "/";
    }

    public static JSONObject getPutSnoozeBody(@NonNull String date, @NonNull String time){
        JSONObject putSnoozeBody = new JSONObject();
        try{
            putSnoozeBody.put("date", date)
                    .put("time", time);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return putSnoozeBody;
    }

    public static String getPostActionReportUrl(@NonNull Action action){
        if (action instanceof UserAction){
            return BASE_URL + "users/actions/" + action.getId() + "/complete/";
        }
        else if (action instanceof CustomAction){
            return BASE_URL + "users/customactions/" + action.getId() + "/complete/";
        }
        else{
            return "";
        }
    }

    public static String getPostActionReportUrl(@NonNull Reminder reminder){
        if (reminder.isUserAction()){
            return BASE_URL + "users/actions/" + reminder.getUserMappingId() + "/complete/";
        }
        else if (reminder.isCustomAction()){
            return BASE_URL + "users/customactions/" + reminder.getObjectId() + "/complete/";
        }
        else{
            return "";
        }
    }

    public static JSONObject getPostActionReportBody(@NonNull String state){
        JSONObject postActionReportBody = new JSONObject();
        try{
            postActionReportBody.put("state", state);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return postActionReportBody;
    }

    public static JSONObject getPostActionReportBody(@NonNull String state, @NonNull String length){
        JSONObject postActionReportBody = new JSONObject();
        try{
            postActionReportBody.put("state", state)
                    .put("length", length);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return postActionReportBody;
    }


    /*------------*
     * PLACES API *
     *------------*/

    public static String getPrimaryPlacesUrl(){
        return BASE_URL + "places/";
    }

    public static String getPostPutPlaceUrl(@NonNull UserPlace userPlace){
        String url =  BASE_URL + "users/places/";
        if (userPlace.getId() != -1){
            url += userPlace.getId() + "/";
        }
        return url;
    }

    public static JSONObject getPostPutPlaceBody(@NonNull UserPlace userPlace){
        JSONObject postPutPlaceBody = new JSONObject();
        try{
            postPutPlaceBody.put("place", userPlace.getName())
                    .put("latitude", userPlace.getLatitude())
                    .put("longitude", userPlace.getLongitude());
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return postPutPlaceBody;
    }


    /*------------*
     * SURVEY API *
     *------------*/

    public static String getInstrumentUrl(int instrument){
        return BASE_URL + "survey/instruments/" + instrument + "/";
    }

    public static String getSurveyUrl(@NonNull String survey){
        return BASE_URL + "survey/" + survey;
    }

    public static String getPostSurveyUrl(@NonNull Survey survey){
        String url = survey.getResponseUrl();
        if (BuildConfig.DEBUG && url.startsWith("https")){
            url = "http" + url.substring(5);
        }
        return url;
    }

    public static JSONObject getPostSurveyBody(@NonNull Survey survey){
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


    /*--------------*
     * USER PROFILE *
     *--------------*/

    public static String getUserProfileUrl(){
        return BASE_URL + "userprofiles/";
    }

    public static String getPutUserProfileUrl(@NonNull User user){
        return BASE_URL + "userprofiles/" + user.getUserprofileId() + "/";
    }

    public static JSONObject getPutUserProfileBody(@NonNull User user){
        JSONObject putUserProfileBody = new JSONObject();
        try{
            putUserProfileBody.put("timezone", TimeZone.getDefault().getID())
                    .put("needs_onboarding", user.needsOnBoarding());
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return putUserProfileBody;
    }


    /*----------------------*
     * PACKAGES AND CONSENT *
     *----------------------*/

    public static String getPackageUrl(int packageId){
        return BASE_URL + "users/packages/" + packageId + "/";
    }

    public static String getPutConsentAcknowledgementUrl(@NonNull TDCPackage myPackage){
        return BASE_URL + "users/packages/" + myPackage.getId() + "/";
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
}
