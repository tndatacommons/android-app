package org.tndata.android.compass.util;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.BuildConfig;
import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.model.*;

import java.util.TimeZone;


/**
 * Class containing all the information about API endpoints and the bodies
 * that should come with the requests.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public final class API{
    //Api urls and app configuration
    public static final boolean STAGING = !BuildConfig.DEBUG;
    private static final boolean USE_NGROK_TUNNEL = false;
    private static final String TNDATA_BASE_URL = "https://app.tndata.org/api/";
    private static final String TNDATA_STAGING_URL = "https://staging.tndata.org/api/";
    private static final String NGROK_TUNNEL_URL = "https://tndata.ngrok.io/api/";

    @SuppressWarnings("ConstantConditions")
    private static final String BASE_URL =
            USE_NGROK_TUNNEL ?
                    NGROK_TUNNEL_URL
                    :
                    STAGING ?
                            TNDATA_STAGING_URL
                            :
                            TNDATA_BASE_URL;


    private API(){

    }


    /**
     * Class containing all the getters for api endpoints.
     *
     * @author Ismael Alonso
     */
    public static final class URL{
        private URL(){

        }


        /*----------------*
         * AUTHENTICATION *
         *----------------*/

        public static String signUp(){
            return BASE_URL + "users/";
        }

        public static String logIn(){
            return BASE_URL + "auth/token/";
        }

        public static String logOut(){
            return BASE_URL + "auth/logout/";
        }

        public static String postDeviceRegistration(){
            return BASE_URL + "notifications/devices/";
        }


        /*------------------------------*
         * APPLICATION DATA AND LIBRARY *
         *------------------------------*/

        public static String getFeedData(){
            return BASE_URL + "users/feed/";
        }

        public static String search(@NonNull String query){
            query = query.replace(" ", "%20");
            return BASE_URL + "search/?q=" + query;
        }

        //Categories
        public static String getCategories(){
            return BASE_URL + "categories/?page_size=999999";
        }

        public static String getCategory(long categoryId){
            return BASE_URL + "categories/" + categoryId + "/";
        }

        public static String getUserCategories(){
            return BASE_URL + "users/categories/?page_size=999999";
        }

        public static String getUserCategory(long categoryId){
            return BASE_URL + "users/categories/?category=" + categoryId;
        }

        //User goals
        public static String getGoals(@NonNull TDCCategory category){
            return BASE_URL + "goals/?category=" + category.getId();
        }

        public static String getUserGoals(){
            return BASE_URL + "users/goals/?page_size=3";
        }

        public static String getUserGoal(long userGoalId){
            return BASE_URL + "users/goals/" + userGoalId + "/";
        }

        public static String getTodaysGoals(){
            return BASE_URL + "users/goals/?today=1";
        }

        public static String postGoal(@NonNull TDCGoal goal){
            return BASE_URL + "goals/" + goal.getId() + "/enroll/";
        }

        //Custom goals
        public static String getCustomGoals(){
            return BASE_URL + "users/customgoals/?page_size=3";
        }

        public static String getCustomGoal(long customGoalId){
            return BASE_URL + "users/customgoals/" + customGoalId + "/";
        }

        public static String postCustomGoal(){
            return BASE_URL + "users/customgoals/";
        }

        public static String putCustomGoal(@NonNull CustomGoal customGoal){
            return BASE_URL + "users/customgoals/" + customGoal.getId() + "/";
        }

        //Generic for goals
        public static String deleteGoal(@NonNull Goal goal){
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

        //User actions
        public static String getUserActions(@NonNull TDCCategory category){
            return BASE_URL + "users/actions/?category=" + category.getId();
        }

        public static String getUserActionsByGoal(long goalId){
            return BASE_URL + "users/actions/?goal=" + goalId;
        }

        public static String getTodaysUserActions(){
            return BASE_URL + "users/actions/?today=1&exclude_completed=1&page_size=1";
        }

        public static String getUserAction(long actionMappingId){
            return BASE_URL + "users/actions/" + actionMappingId + "/";
        }

        //Custom actions
        public static String getCustomActions(@NonNull Goal goal){
            if (goal instanceof UserGoal){
                return BASE_URL + "users/customactions/?goal=" + goal.getContentId();
            }
            else if (goal instanceof CustomGoal){
                return BASE_URL + "users/customactions/?customgoal=" + goal.getId();
            }
            else{
                return "";
            }
        }

        public static String getTodaysCustomActions(){
            return BASE_URL + "users/customactions/?today=1&exclude_completed=1&page_size=1";
        }

        public static String getCustomAction(long customActionId){
            return BASE_URL + "users/customactions/" + customActionId + "/";
        }

        public static String postCustomAction(){
            return BASE_URL + "users/customactions/";
        }

        public static String putCustomAction(@NonNull CustomAction customAction){
            return BASE_URL + "users/customactions/" + customAction.getId() + "/";
        }

        //Generic for actions
        public static String deleteAction(@NonNull Action action){
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
        public static String putTrigger(@NonNull Action action){
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


        /*------------*
         * PLACES API *
         *------------*/

        public static String getPrimaryPlaces(){
            return BASE_URL + "places/";
        }

        public static String getUserPlaces(){
            return BASE_URL + "users/places/";
        }

        public static String postPutPlace(@NonNull UserPlace userPlace){
            String url =  BASE_URL + "users/places/";
            if (userPlace.getId() != -1){
                url += userPlace.getId() + "/";
            }
            return url;
        }


        /*-------------------------------------*
         * PROGRESS AND NOTIFICATION REPORTING *
         *-------------------------------------*/

        public static String getUserProgress(){
            return BASE_URL + "users/progress/";
        }

        public static String postUserProgress(){
            return BASE_URL + "users/progress/checkin/";
        }

        public static String putSnooze(long notificationId){
            return BASE_URL + "notifications/" + notificationId + "/";
        }

        public static String postActionReport(@NonNull Action action){
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

        public static String postActionReport(@NonNull GcmMessage message){
            if (message.isUserActionMessage()){
                return BASE_URL + "users/actions/" + message.getUserAction().getId() + "/complete/";
            }
            else if (message.isCustomActionMessage()){
                return BASE_URL + "users/customactions/" + message.getCustomAction().getId() + "/complete/";
            }
            else{
                return "";
            }
        }


        /*------------*
         * SURVEY API *
         *------------*/

        public static String getInstrument(int instrument){
            return BASE_URL + "survey/instruments/" + instrument + "/";
        }

        public static String getSurvey(@NonNull String survey){
            return BASE_URL + "survey/" + survey;
        }

        public static String postSurvey(@NonNull Survey survey){
            String url = survey.getResponseUrl();
            if (BuildConfig.DEBUG && url.startsWith("https")){
                url = "http" + url.substring(5);
            }
            return url;
        }


        /*--------------------------*
         * USER ACCOUNT AND PROFILE *
         *--------------------------*/

        public static String getUserAccount(){
            return BASE_URL + "users/accounts/";
        }

        public static String putUserProfile(@NonNull User user){
            return BASE_URL + "users/profile/" + user.getProfileId() + "/";
        }


        /*----------------------*
         * PACKAGES AND CONSENT *
         *----------------------*/

        public static String getPackage(int packageId){
            return BASE_URL + "users/packages/" + packageId + "/";
        }

        public static String putConsentAcknowledgement(@NonNull TDCPackage myPackage){
            return BASE_URL + "users/packages/" + myPackage.getId() + "/";
        }


        /*---------------*
         * ORGANIZATIONS *
         *---------------*/

        public static String getOrganizations(){
            return BASE_URL + "organizations/";
        }

        public static String postOrganization(){
            return BASE_URL + "organizations/members/";
        }

        public static String postRemoveOrganizationMember(@NonNull long organizationId) {
            return BASE_URL + "organizations/" + organizationId + "/remove-member/";
        }


        /*---------------*
         * MISCELLANEOUS *
         *---------------*/

        public static String getBadges(){
            return BASE_URL + "awards/";
        }

        public static String getRandomReward(){
            return BASE_URL + "rewards/?random=1";
        }

        public static String postResetEmailUrl(){
            return BASE_URL + "reset-password/";
        }

        public static String postLog(){
            return "https://staging.tndata.org/cronlog/add/";
        }
    }


    /**
     * Class containing getters for all the bodies taken by endpoints.
     *
     * @author Ismael Alonso
     */
    public static final class BODY{
        private BODY(){

        }


        /*----------------*
         * AUTHENTICATION *
         *----------------*/

        public static JSONObject signUp(@NonNull String email, @NonNull String password,
                                               @NonNull String firstName, @NonNull String lastName){

            JSONObject signUpBody = new JSONObject();
            try{
                signUpBody.put("email", email)
                        .put("password", password)
                        .put("first_name", firstName)
                        .put("last_name", lastName);
            }
            catch (JSONException jx){
                jx.printStackTrace();
            }
            return signUpBody;
        }

        public static JSONObject logIn(@NonNull String email, @NonNull String password){
            JSONObject logInBody = new JSONObject();
            try{
                logInBody.put("email", email);
                logInBody.put("password", password);
            }
            catch (JSONException jx){
                jx.printStackTrace();
            }
            return logInBody;
        }

        public static JSONObject logOut(@NonNull String registrationId){
            JSONObject logOutBody = new JSONObject();
            try{
                logOutBody.put("registration_id", registrationId);
            }
            catch (JSONException jx){
                jx.printStackTrace();
            }
            return logOutBody;
        }

        public static JSONObject postDeviceRegistration(@NonNull String registrationId){
            JSONObject postDeviceRegistrationBody = new JSONObject();
            try{
                postDeviceRegistrationBody.put("registration_id", registrationId)
                        .put("device_type", "android")
                        .put("device_name", Build.MANUFACTURER + " " + Build.PRODUCT);
            }
            catch (JSONException jx){
                jx.printStackTrace();
            }
            return postDeviceRegistrationBody;
        }


        /*------------------------------*
         * APPLICATION DATA AND LIBRARY *
         *------------------------------*/

        public static JSONObject postGoal(@NonNull TDCCategory category){
            JSONObject body = new JSONObject();
            try{
                body.put("category", category.getId());
            }
            catch (JSONException jx){
                jx.printStackTrace();
            }
            return body;
        }

        public static JSONObject postPutCustomGoal(@NonNull CustomGoal customGoal){
            JSONObject postPutCustomGoalBody = new JSONObject();
            try{
                postPutCustomGoalBody.put("title", customGoal.getTitle());
            }
            catch (JSONException jx){
                jx.printStackTrace();
            }
            return postPutCustomGoalBody;
        }

        public static JSONObject postPutCustomAction(@NonNull String title,
                                                     @NonNull Goal goal){

            JSONObject postPutCustomActionBody = new JSONObject();
            try{
                postPutCustomActionBody.put("title", title)
                        .put("notification_text", title);
                if (goal instanceof UserGoal){
                    postPutCustomActionBody.put("goal", goal.getContentId());
                }
                else if (goal instanceof CustomGoal){
                    postPutCustomActionBody.put("customgoal", goal.getId());
                }
            }
            catch (JSONException jx){
                jx.printStackTrace();
            }
            return postPutCustomActionBody;
        }

        public static JSONObject putTrigger(@NonNull Trigger trigger){
            JSONObject putTriggerBody = new JSONObject();
            try{
                putTriggerBody.put("custom_trigger_time", trigger.getRawTime())
                        .put("custom_trigger_date", trigger.getRawDate())
                        .put("custom_trigger_rrule", trigger.getRecurrences())
                        .put("custom_trigger_disabled", !trigger.isEnabled());
            }
            catch (JSONException jx){
                jx.printStackTrace();
            }
            return putTriggerBody;
        }


        /*------------*
         * PLACES API *
         *------------*/

        public static JSONObject postPutPlace(@NonNull UserPlace userPlace){
            JSONObject postPutPlaceBody = new JSONObject();
            try{
                postPutPlaceBody.put("place", userPlace.getName())
                        .put("latitude", userPlace.getLatitude())
                        .put("longitude", userPlace.getLongitude());
            }
            catch (JSONException jx){
                jx.printStackTrace();
            }
            return postPutPlaceBody;
        }


        /*-------------------------------------*
         * PROGRESS AND NOTIFICATION REPORTING *
         *-------------------------------------*/

        public static JSONObject postUserProgress(@NonNull UserGoal userGoal, int progress){
            JSONObject postUserGoalProgressBody = new JSONObject();
            try{
                postUserGoalProgressBody.put("goal", userGoal.getContentId())
                        .put("daily_checkin", progress);
            }
            catch (JSONException jx){
                jx.printStackTrace();
            }
            return postUserGoalProgressBody;
        }

        public static JSONObject putSnooze(@NonNull String date, @NonNull String time){
            JSONObject putSnoozeBody = new JSONObject();
            try{
                putSnoozeBody.put("date", date)
                        .put("time", time);
            }
            catch (JSONException jx){
                jx.printStackTrace();
            }
            return putSnoozeBody;
        }

        public static JSONObject postActionReport(@NonNull String state, @Nullable String length){
            JSONObject postActionReportBody = new JSONObject();
            try{
                postActionReportBody.put("state", state);
                if (length != null){
                    postActionReportBody.put("length", length);
                }
            }
            catch (JSONException jx){
                jx.printStackTrace();
            }
            return postActionReportBody;
        }


        /*------------*
         * SURVEY API *
         *------------*/

        public static JSONObject postSurvey(@NonNull Survey survey){
            JSONObject postSurveyBody = new JSONObject();
            try{
                postSurveyBody.put("question", survey.getId());
                if (survey.getQuestionType() == Survey.QuestionType.OPEN_ENDED){
                    postSurveyBody.put("response", survey.getResponse());
                }
                else{
                    postSurveyBody.put("selected_option", survey.getSelectedOption().getId());
                }
            }
            catch (JSONException jx){
                jx.printStackTrace();
            }
            return postSurveyBody;
        }


        /*--------------------------*
         * USER ACCOUNT AND PROFILE *
         *--------------------------*/

        public static JSONObject putUserProfile(@NonNull User user){
            JSONObject body = new JSONObject();
            try{
                body.put("timezone", TimeZone.getDefault().getID())
                        .put("needs_onboarding", user.needsOnBoarding())
                        .put("maximum_daily_notifications", user.getDailyNotifications())
                        .put("zipcode", user.getZipCode())
                        .put("birthday", user.getBirthday())
                        .put("sex", user.getSex().toLowerCase())
                        .put("employed", user.isEmployed())
                        .put("is_parent", user.isParent())
                        .put("in_relationship", user.inRelationship())
                        .put("has_degree", user.hasDegree());
            }
            catch (JSONException jx){
                jx.printStackTrace();
            }
            return body;
        }


        /*----------------------*
         * PACKAGES AND CONSENT *
         *----------------------*/

        public static JSONObject putConsentAcknowledgement(){
            JSONObject putConsentAcknowledgementBody = new JSONObject();
            try{
                putConsentAcknowledgementBody.put("accepted", true);
            }
            catch (JSONException jx){
                jx.printStackTrace();
            }
            return putConsentAcknowledgementBody;
        }


        /*---------------*
         * ORGANIZATIONS *
         *---------------*/

        public static JSONObject postOrganization(Organization organization){
            JSONObject body = new JSONObject();
            try{
                body.put("organization", organization.getId());
            }
            catch (JSONException jx){
                jx.printStackTrace();
            }
            return body;
        }


        /*---------------*
         * MISCELLANEOUS *
         *---------------*/

        public static JSONObject postResetEmail(@NonNull String address){
            JSONObject body = new JSONObject();
            try{
                body.put("email", address);
            }
            catch (JSONException jx){
                jx.printStackTrace();
            }
            return body;
        }

        public static JSONObject postRemoveOrganizationMember(long organizationId){
            JSONObject body = new JSONObject();
            try{
                body.put("organization", organizationId);
            }
            catch (JSONException jx){
                jx.printStackTrace();
            }
            return body;
        }

        public static JSONObject postLog(Context context, String title, String message){
            JSONObject body = new JSONObject();
            try{
                CompassApplication app = (CompassApplication)context.getApplicationContext();
                body.put("command", title);
                body.put("message", message);
                body.put("host", app.getUser().getEmail());
                body.put("key", context.getString(org.tndata.android.compass.R.string.logging_key));
            }
            catch (JSONException jx){
                jx.printStackTrace();
            }
            return body;
        }
    }
}
