package org.tndata.android.compass.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import com.google.android.gms.maps.model.LatLng;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.*;


/**
 * This class contains all general purpose helper or utility methods used
 * throughout the application but small or rogue enough not to be worthy of
 * having a class of their own.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public final class CompassUtil{
    /**
     * Constructor. Declared private to prevent external instantiation. Furthermore, throws
     * an exception to prevent internal instantiation.
     */
    private CompassUtil() throws IllegalAccessException{
        throw new IllegalAccessException(getClass().toString() + " is not to be instantiated");
    }

    /**
     * Getter for the screen width in pixels.
     *
     * @param context a reference to the context.
     * @return the screen width in pixels.
     */
    public static int getScreenWidth(Context context){
        WindowManager manager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    /**
     * Converts density pixels into pixels.
     *
     * @param context the application context.
     * @param densityPixels the amount of DPs to convert.
     * @return the amount in pixels.
     */
    public static int getPixels(Context context, int densityPixels){
        return (int)Math.ceil(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, densityPixels,
                context.getResources().getDisplayMetrics()));
    }

    /**
     * Calculates the arch distance in meters between two points in Earth's surface.
     *
     * @param point1 point A.
     * @param point2 point B.
     * @return the arch distance between point A and point B.
     */
    public static double getDistance(LatLng point1, LatLng point2){
        //Coordinates
        double lat1 = point1.latitude;
        double lng1 = point1.longitude;
        double lat2 = point2.latitude;
        double lng2 = point2.longitude;

        //Distance
        lat1 *= Math.PI/180;
        lng1 *= Math.PI/180;
        lat2 *= Math.PI/180;
        lng2 *= Math.PI/180;
        double a = Math.sin((lat2-lat1)/2)*Math.sin((lat2-lat1)/2)
                +Math.cos(lat1)*Math.cos(lat2)
                *Math.sin((lng2-lng1)/2)*Math.sin((lng2-lng1)/2);
        double c = 2*Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return c*6371*1000;
    }

    /**
     * Returns the abbreviation of a month in English.
     *
     * @param month the month to be abbreviated. 1=January, 12=December.
     * @return the month abbreviation.
     */
    public static String getMonthString(int month){
        if (month == 1){
            return "JAN";
        }
        if (month == 2){
            return "FEB";
        }
        if (month == 3){
            return "MAR";
        }
        if (month == 4){
            return "APR";
        }
        if (month == 5){
            return "MAY";
        }
        if (month == 6){
            return "JUN";
        }
        if (month == 7){
            return "JUL";
        }
        if (month == 8){
            return "AUG";
        }
        if (month == 9){
            return "SEP";
        }
        if (month == 10){
            return "OCT";
        }
        if (month == 11){
            return "NOV";
        }
        if (month == 12){
            return "DEC";
        }
        return "";
    }

    /**
     * Checks whether the provided string has one of the following formats, X being a number:
     * <p/>
     * (XXX) XXX-XXX
     * XXX-XXX-XXXX
     *
     * @param resource the resource to be checked.
     * @return true if the resource is a phone number, false otherwise.
     */
    public static boolean isPhoneNumber(String resource){
        return resource.matches("[(][0-9]{3}[)] [0-9]{3}[-][0-9]{4}") ||
                resource.matches("[0-9]{3}[-][0-9]{3}[-][0-9]{4}");
    }

    public static void doItNow(@NonNull Context context, @NonNull String resource){
        //If a link
        if (resource.startsWith("http")){
            //If an app
            if (resource.startsWith("http://play.google.com/store/apps/") ||
                    resource.startsWith("https://play.google.com/store/apps/")){
                String id = resource.substring(resource.indexOf('/', 32));
                //Try, if the user does not have the store installed, launch as web link
                try{
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://" + id)));
                }
                catch (ActivityNotFoundException anfx){
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(resource)));
                }
            }
            //Otherwise opened with the browser
            else{
                Uri uri = Uri.parse(resource);
                context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        }
        //If a phone number
        else if (CompassUtil.isPhoneNumber(resource)){
            //First of all, the number needs to be extracted from the resource
            String number = "";
            for (int i = 0; i < resource.length(); i++){
                char digit = resource.charAt(i);
                if (digit >= '0' && digit <= '9'){
                    number += digit;
                }
            }
            context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number)));
        }
    }

    @DrawableRes
    public static int getCategoryTileResId(String title){
        if (title.equalsIgnoreCase("Happiness & fun")){
            return R.drawable.tile_fun;
        }
        else if (title.equalsIgnoreCase("Family & parenting")){
            return R.drawable.tile_parenting;
        }
        else if (title.equalsIgnoreCase("Work & prosperity")){
            return R.drawable.tile_prosperity;
        }
        else if (title.equalsIgnoreCase("Home & safety")){
            return R.drawable.tile_home;
        }
        else if (title.equalsIgnoreCase("Education & skills")){
            return R.drawable.tile_skills;
        }
        else if (title.equalsIgnoreCase("Health & wellness")){
            return R.drawable.tile_health;
        }
        else if (title.equalsIgnoreCase("Community & friendship")){
            return R.drawable.tile_community;
        }
        else if (title.equalsIgnoreCase("Romance & relationships")){
            return R.drawable.tile_romance;
        }
        else{
            return 0;
        }
    }

    public static Class getTypeOf(String src){
        //TODO: contains to equals whenever possible
        //IMPORTANT NOTE: the order of the statements matters.
        //Default to Long (for IDs)
        if (src == null){
            return Long.class;
        }
        else if (src.equals(DailyProgress.API_TYPE)){
            return DailyProgress.class;
        }
        else if (src.equals(FeedData.API_TYPE)){
            return FeedData.class;
        }
        else if (src.equals(UpcomingAction.API_TYPE)){
            return UpcomingAction.class;
        }
        //Search
        else if (src.contains("search")){
            return SearchResult.class;
        }
        //UserContent
        else if (src.contains("usercategory")){
            return UserCategory.class;
        }
        else if (src.contains("usergoal")){
            return UserGoal.class;
        }
        else if (src.contains("userbehavior")){
            return UserBehavior.class;
        }
        else if (src.contains("useraction")){
            return UserAction.class;
        }
        //CustomContent
        else if (src.contains("customgoal")){
            return CustomGoal.class;
        }
        else if (src.contains("customaction")){
            return CustomAction.class;
        }
        //TDCContent
        else if (src.contains("packageenrollment")){
            return TDCPackage.class;
        }
        else if (src.contains("category")){
            return CategoryContent.class;
        }
        else if (src.contains("goal")){
            return GoalContent.class;
        }
        else if (src.contains("behavior")){
            return BehaviorContent.class;
        }
        else if (src.contains("action")){
            return ActionContent.class;
        }
        //Places
        else if (src.contains("userplace")){
            return UserPlace.class;
        }
        else if (src.contains("place")){
            return Place.class;
        }
        //UserData
        else if (src.contains("userprofile")){
            return UserProfile.class;
        }
        else if (src.contains("surveyresult")){
            return UserProfile.SurveyResponse.class;
        }
        else if (src.contains("instrument")){
            return Instrument.class;
        }
        else if (src.contains("binaryquestion")){
            return Survey.class;
        }
        else if (src.contains("multiplechoicequestion")){
            return Survey.class;
        }
        else if (src.contains("openendedquestion")){
            return Survey.class;
        }
        else if (src.contains("likertquestion")){
            return Survey.class;
        }
        else if (src.contains("option")){
            return SurveyOption.class;
        }
        //Miscellaneous
        else if (src.contains("funcontent")){
            return Reward.class;
        }
        //Second default to TDCBase; the API should NOT deliver anything that's not TDCBase
        else{
            return Long.class;
        }
    }

    /**
     * Tells whether a particular permission has been granted.
     *
     * @param context a reference to the context.
     * @param permission the permission to check.
     * @return true if the permission has been granted, false otherwise.
     */
    public static boolean hasPermission(@NonNull Context context, String permission){
        int permissionGranted = PackageManager.PERMISSION_GRANTED;
        return ContextCompat.checkSelfPermission(context, permission) == permissionGranted;
    }
}
