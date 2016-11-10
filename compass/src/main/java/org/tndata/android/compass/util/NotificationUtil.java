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
import org.tndata.compass.model.GcmMessage;
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
    //Notification tags
    public static final String USER_ACTION_TAG = "org.tndata.compass.Notification.UserAction";
    public static final String CUSTOM_ACTION_TAG = "org.tndata.compass.Notification.CustomAction";
    public static final String ENROLLMENT_TAG = "org.tndata.compass.Notification.Enrollment";
    private static final String CHECK_IN_TAG = "org.tndata.compass.Notification.CheckIn";
    private static final String BADGE_TAG = "org.tndata.compass.Notification.Badge";


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
     * @param context a reference to the context.
     * @param message the GCM message that triggered the notification.
     */
    private static void putActionNotification(Context context, GcmMessage message){
        //Action intent; what happens when the user taps the notification
        Intent intent = new Intent(context, ActionActivity.class)
                .putExtra(ActionActivity.ACTION_KEY, message.getAction());

        PendingIntent contentIntent = PendingIntent.getActivity(context,
                (int)System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Dismiss intent; what happens when the user dismisses the notification
        Intent dismissIntent = new Intent(context, ActionReportService.class)
                .putExtra(ActionReportService.MESSAGE_KEY, message)
                .putExtra(ActionReportService.STATE_KEY, ActionReportService.STATE_DISMISSED);

        PendingIntent dismissedPendingIntent = PendingIntent.getService(context,
                (int)System.currentTimeMillis(), dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Snooze intent; what happens when the user taps the "later" action
        Intent snoozeIntent = new Intent(context, SnoozeActivity.class)
                .putExtra(SnoozeActivity.GCM_MESSAGE_KEY, message);

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
    private static void putCheckInNotification(Context context, GcmMessage message){
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
            //CompassUtil.log(context, "Gcm Message", "Generating action notification");
            putActionNotification(context, message);
        }
        else if (message.isPackageEnrollmentMessage()){
            putEnrollmentNotification(context, message);
        }
        else if (message.isCheckInMessage()){
            putCheckInNotification(context, message);
        }
        else if (message.isBadgeMessage()){
            //Unfortunately, the web app cannot guarantee that a badge will be delivered
            if (message.getBadge() != null){
                putBadgeNotification(context, message);
            }
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
