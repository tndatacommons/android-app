package org.tndata.android.compass.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import org.tndata.android.compass.R;
import org.tndata.android.compass.activity.ActionActivity;
import org.tndata.android.compass.activity.CheckInActivity;
import org.tndata.android.compass.activity.DidNotDoItActivity;
import org.tndata.android.compass.activity.PackageEnrollmentActivity;
import org.tndata.android.compass.activity.SnoozeActivity;
import org.tndata.android.compass.fragment.NotificationSettingsFragment;
import org.tndata.android.compass.model.Reminder;
import org.tndata.android.compass.service.ActionReportService;
import org.tndata.android.compass.ui.QuietHoursPreference;

import java.util.Calendar;


/**
 * Utility class containing all the required methods to generate notifications.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public final class NotificationUtil{
    //Data keys generally common to notifications
    public static final String REMINDER_KEY = "org.tndata.compass.Notification.Reminder";

    //Notification tags
    public static final String NOTIFICATION_TYPE_ACTION_TAG = "org.tndata.compass.ActionNotification";
    public static final String NOTIFICATION_TYPE_ENROLLMENT_TAG = "org.tndata.compass.EnrollmentNotification";
    public static final String NOTIFICATION_TYPE_CHECK_IN_TAG = "org.tndata.compass.CheckInNotification";

    //Notification ids for notification tags with more than one type
    public static final int NOTIFICATION_TYPE_CHECK_IN_REVIEW_ID = 1;
    public static final int NOTIFICATION_TYPE_CHECK_IN_FEEDBACK_ID = 2;


    /**
     * Constructor. Should never be called. Period.
     */
    public NotificationUtil(){
        throw new RuntimeException(getClass().toString() + " is not to be instantiated");
    }

    /**
     * Private getter for a generic builder for a notification with a title and a message.
     *
     * @param context the context.
     * @param title the title of the notification.
     * @param message the message of the notification.
     * @return the builder.
     */
    private static NotificationCompat.Builder getBuilder(Context context, String title, String message){
        Uri ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        String quietKey = NotificationSettingsFragment.QUIET_HOURS_KEY;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean quiet[] = QuietHoursPreference.parsePreference(quietKey, context)[hour<12 ? 0 : 1];
        boolean sound = preferences.getBoolean(NotificationSettingsFragment.SOUND_KEY, true);
        boolean vibration = preferences.getBoolean(NotificationSettingsFragment.VIBRATION_KEY, true);
        boolean light = preferences.getBoolean(NotificationSettingsFragment.LIGHT_KEY, true);

        NotificationCompat.Builder builder =  new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_action_compass_white)
                .setLargeIcon(icon)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        if (!quiet[hour%12]){
            if (sound){
                builder.setSound(ringtone);
            }
            if (vibration){
                builder.setVibrate(new long[]{0, 300, 200, 300});
            }
            if (light){
                builder.setLights(0xFFFFFFFF, 500, 500);
            }
        }

        return builder;
    }

    /**
     * Creates an action notification.
     *
     * @param context the context.
     * @param notificationId the id of the notification as given by the API.
     * @param title the title of the notification.
     * @param message the message of the notification.
     * @param actionId the id of the action enclosed in this notification.
     * @param userMappingId the mapping id of the action for the user.
     */
    public static void generateActionNotification(Context context, int notificationId, String title,
                                                  String message, int actionId, int userMappingId){

        Reminder reminder = new Reminder(notificationId, -1, title, message, actionId, userMappingId);

        //Action intent; what happens when the user taps the notification
        Intent intent = new Intent(context, ActionActivity.class)
                .putExtra(ActionActivity.ACTION_ID_KEY, actionId)
                .putExtra(ActionActivity.REMINDER_KEY, reminder);

        PendingIntent contentIntent = PendingIntent.getActivity(context,
                (int)System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Dismiss intent; what happens when the user dismisses the notification
        Intent dismissIntent = new Intent(context, ActionReportService.class)
                .putExtra(REMINDER_KEY, reminder)
                .putExtra(ActionReportService.STATE_KEY, ActionReportService.STATE_DISMISSED);

        PendingIntent dismissedPendingIntent = PendingIntent.getService(context,
                (int)System.currentTimeMillis(), dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Did it intent; what happens when the user taps the "yes" action
        Intent didItIntent = new Intent(context, ActionReportService.class)
                .putExtra(REMINDER_KEY, reminder)
                .putExtra(ActionReportService.STATE_KEY, ActionReportService.STATE_COMPLETED);

        PendingIntent didItPendingIntent = PendingIntent.getService(context,
                (int)System.currentTimeMillis(), didItIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String didIt = context.getString(R.string.action_notification_yes);

        //Didn't do it intent; what happens when the user taps the "no" action
        Intent didNotDoItIntent = new Intent(context, DidNotDoItActivity.class)
                .putExtra(REMINDER_KEY, reminder);

        PendingIntent didNotDoItPendingIntent = PendingIntent.getActivity(context,
                (int)System.currentTimeMillis(), didNotDoItIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String didNotDoIt = context.getString(R.string.action_notification_no);

        //Snooze intent; what happens when the user taps the "later" action
        Intent snoozeIntent = new Intent(context, SnoozeActivity.class)
                .putExtra(REMINDER_KEY, reminder);

        PendingIntent snoozePendingIntent = PendingIntent.getActivity(context,
                (int)System.currentTimeMillis(), snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String later = context.getString(R.string.action_notification_later);

        //Generate the notification and push it
        Notification notification = getBuilder(context, title, message)
                .addAction(R.drawable.ic_snooze, later, snoozePendingIntent)
                .addAction(R.drawable.ic_thumb_down_white, didNotDoIt, didNotDoItPendingIntent)
                .addAction(R.drawable.ic_thumb_up_white, didIt, didItPendingIntent)
                .setContentIntent(contentIntent)
                .setDeleteIntent(dismissedPendingIntent)
                .setAutoCancel(false)
                .build();
        
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        ((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(NOTIFICATION_TYPE_ACTION_TAG, actionId, notification);
    }

    /**
     * Creates a package enrollment notification.
     *
     * @param context an instance of the context.
     * @param packageId the package id.
     * @param title the title of the notification.
     * @param message the message of the notification.
     */
    public static void generateEnrollmentNotification(Context context, int packageId, String title,
                                                      String message){

        Intent intent = new Intent(context, PackageEnrollmentActivity.class)
                .putExtra(PackageEnrollmentActivity.PACKAGE_ID_KEY, packageId);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                (int)System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = getBuilder(context, title, message)
                .setContentIntent(contentIntent)
                .setAutoCancel(false)
                .build();

        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        ((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(NOTIFICATION_TYPE_ENROLLMENT_TAG, packageId, notification);
    }

    /**
     * Creates a check in notification.
     *
     * @param context an instance of the context.
     * @param review true if this is a review notification, false if this is a
     *               feedback notification.
     * @param title the title of the notification.
     * @param message the message of the notification.
     */
    public static void generateCheckInNotification(Context context, boolean review, String title,
                                                   String message){
        Intent intent = new Intent(context, CheckInActivity.class);
        int notificationId;
        if (review){
            intent.putExtra(CheckInActivity.TYPE_KEY, CheckInActivity.TYPE_REVIEW);
            notificationId = NOTIFICATION_TYPE_CHECK_IN_REVIEW_ID;
        }
        else{
            intent.putExtra(CheckInActivity.TYPE_KEY, CheckInActivity.TYPE_FEEDBACK);
            notificationId = NOTIFICATION_TYPE_CHECK_IN_FEEDBACK_ID;
        }
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                (int)System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = getBuilder(context, title, message)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build();

        ((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(NOTIFICATION_TYPE_CHECK_IN_TAG, notificationId, notification);
    }
}
