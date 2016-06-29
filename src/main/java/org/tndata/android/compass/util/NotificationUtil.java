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
import org.tndata.android.compass.activity.BadgeActivity;
import org.tndata.android.compass.activity.CheckInActivity;
import org.tndata.android.compass.activity.PackageEnrollmentActivity;
import org.tndata.android.compass.activity.SnoozeActivity;
import org.tndata.android.compass.fragment.NotificationSettingsFragment;
import org.tndata.android.compass.model.GcmMessage;
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
    public static final String USER_ACTION_TAG = "org.tndata.compass.Notification.UserAction";
    public static final String CUSTOM_ACTION_TAG = "org.tndata.compass.Notification.CustomAction";
    public static final String ENROLLMENT_TAG = "org.tndata.compass.Notification.Enrollment";
    public static final String CHECK_IN_TAG = "org.tndata.compass.Notification.CheckIn";
    public static final String BADGE_TAG = "org.tndata.compass.Notification.Badge";


    /**
     * Constructor. Should never be called. Period.
     */
    private NotificationUtil(){
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
                .setSmallIcon(R.drawable.ic_compass_notification_white_24dp)
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
     * Private getter for a generic builder for a notification with a title and a message.
     *
     * @param context the context.
     * @param message the GCM message that triggered the call.
     * @return the builder.
     */
    private static NotificationCompat.Builder getBuilder(Context context, GcmMessage message){
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
                .setSmallIcon(R.drawable.ic_compass_notification_white_24dp)
                .setLargeIcon(icon)
                .setContentTitle(message.getContentTitle())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message.getContentText()))
                .setContentText(message.getContentText())
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
     * @param actionMappingId the mapping id of the action for the user.
     */
    public static void putActionNotification(Context context, int notificationId, String title,
                                             String message, int actionId, int actionMappingId){

        Reminder reminder = new Reminder(notificationId, -1, title, message, actionId, actionMappingId);

        //Action intent; what happens when the user taps the notification
        Intent intent = new Intent(context, ActionActivity.class)
                .putExtra(ActionActivity.REMINDER_KEY, reminder);

        PendingIntent contentIntent = PendingIntent.getActivity(context,
                (int)System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Dismiss intent; what happens when the user dismisses the notification
        Intent dismissIntent = new Intent(context, ActionReportService.class)
                .putExtra(REMINDER_KEY, reminder)
                .putExtra(ActionReportService.STATE_KEY, ActionReportService.STATE_DISMISSED);

        PendingIntent dismissedPendingIntent = PendingIntent.getService(context,
                (int)System.currentTimeMillis(), dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Snooze intent; what happens when the user taps the "later" action
        Intent snoozeIntent = new Intent(context, SnoozeActivity.class)
                .putExtra(REMINDER_KEY, reminder);

        PendingIntent snoozePendingIntent = PendingIntent.getActivity(context,
                (int)System.currentTimeMillis(), snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String later = context.getString(R.string.action_notification_later);

        //Generate the notification and push it
        Notification notification = getBuilder(context, title, message)
                .addAction(R.drawable.ic_snooze, later, snoozePendingIntent)
                .setContentIntent(contentIntent)
                .setDeleteIntent(dismissedPendingIntent)
                .setAutoCancel(false)
                .build();

        if (actionMappingId != -1){
            ((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE))
                    .notify(USER_ACTION_TAG, actionMappingId, notification);
        }
        else{
            ((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE))
                    .notify(CUSTOM_ACTION_TAG, actionId, notification);
        }
    }

    /**
     * Creates an action notification.
     *
     * @param context a reference to the context.
     * @param message the GCM message that triggered the notification.
     */
    private static void putActionNotification(Context context, GcmMessage message){
        Reminder reminder = new Reminder((int)message.getId(), -1, message.getContentTitle(),
                message.getContentText(), message.getObjectId(), message.getUserMappingId());

        //Action intent; what happens when the user taps the notification
        Intent intent = new Intent(context, ActionActivity.class)
                .putExtra(ActionActivity.REMINDER_KEY, reminder)
                .putExtra(ActionActivity.GCM_MESSAGE_KEY, message);

        PendingIntent contentIntent = PendingIntent.getActivity(context,
                (int)System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Dismiss intent; what happens when the user dismisses the notification
        Intent dismissIntent = new Intent(context, ActionReportService.class)
                .putExtra(REMINDER_KEY, reminder)
                .putExtra(ActionReportService.STATE_KEY, ActionReportService.STATE_DISMISSED);

        PendingIntent dismissedPendingIntent = PendingIntent.getService(context,
                (int)System.currentTimeMillis(), dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Snooze intent; what happens when the user taps the "later" action
        Intent snoozeIntent = new Intent(context, SnoozeActivity.class)
                .putExtra(REMINDER_KEY, reminder);

        PendingIntent snoozePendingIntent = PendingIntent.getActivity(context,
                (int)System.currentTimeMillis(), snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String later = context.getString(R.string.action_notification_later);

        //Generate the notification and push it
        Notification notification = getBuilder(context, message)
                .addAction(R.drawable.ic_snooze, later, snoozePendingIntent)
                .setContentIntent(contentIntent)
                .setDeleteIntent(dismissedPendingIntent)
                .setAutoCancel(false)
                .build();

        if (message.isUserActionMessage()){
            ((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE))
                    .notify(USER_ACTION_TAG, message.getUserMappingId(), notification);
        }
        else{
            ((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE))
                    .notify(CUSTOM_ACTION_TAG, message.getObjectId(), notification);
        }
    }

    /**
     * Creates a package enrollment notification.
     *
     * @param context a reference to the context.
     * @param message the GCM message that triggered the notification.
     */
    private static void putEnrollmentNotification(Context context, GcmMessage message){
        Intent intent = new Intent(context, PackageEnrollmentActivity.class)
                .putExtra(PackageEnrollmentActivity.PACKAGE_ID_KEY, message.getObjectId());
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                (int)System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = getBuilder(context, message)
                .setContentIntent(contentIntent)
                .setAutoCancel(false)
                .build();

        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        ((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(ENROLLMENT_TAG, message.getObjectId(), notification);
    }

    /**
     * Creates a check in notification.
     *
     * @param context an instance of the context.
     * @param message the GCM message that triggered the call.
     */
    public static void putCheckInNotification(Context context, GcmMessage message){
        Intent intent = new Intent(context, CheckInActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                (int)System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = getBuilder(context, message)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build();

        ((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(CHECK_IN_TAG, 1, notification);
    }

    private static void putBadgeNotification(Context context, GcmMessage message){
        Intent intent = new Intent(context, BadgeActivity.class)
                .putExtra(BadgeActivity.BADGE_KEY, message.getBadge());
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                (int)System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = getBuilder(context, message)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build();

        ((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(BADGE_TAG, (int)System.currentTimeMillis(), notification);
    }

    /**
     * Given a GCM message, decides which kind of notification to generate.
     *
     * @param context an instance of the context.
     * @param message the GCM message that triggered the call.
     */
    public static void generateNotification(Context context, GcmMessage message){
        if (message.isUserActionMessage() || message.isCustomActionMessage()){
            putActionNotification(context, message);
        }
        else if (message.isPackageEnrollmentMessage()){
            putEnrollmentNotification(context, message);
        }
        else if (message.isCheckInMessage()){
            putCheckInNotification(context, message);
        }
        else if (message.isBadgeMessage()){
            putBadgeNotification(context, message);
        }
    }

    /**
     * Cancels a notification.
     *
     * @param context a reference to the context.
     * @param tag the tag of the notification (ie, type).
     * @param id the id of the notification.
     */
    public static void cancel(Context context, String tag, long id){
        String service = Context.NOTIFICATION_SERVICE;
        NotificationManager manager = (NotificationManager)context.getSystemService(service);
        manager.cancel(tag, (int)(id%Integer.MAX_VALUE));
    }
}
