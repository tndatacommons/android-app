package com.tndata.android.compass.tests.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowNotification;
import org.robolectric.shadows.ShadowNotificationManager;
import org.tndata.android.compass.BuildConfig;
import org.tndata.android.compass.service.GcmIntentService;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = "src/main/AndroidManifest.xml")
public class GcmIntentServiceTest {

    private ShadowApplication application;
    private Context context;

    class GcmIntentServiceMock extends GcmIntentService {
        @Override
        public void onHandleIntent(Intent intent) {
            super.onHandleIntent(intent);
        }
    }

    @Before
    public void setup() {
        this.application = ShadowApplication.getInstance();
        this.context = this.application.getApplicationContext();
    }

    @Test
    public void gcmIntentService_intentWithoutExtras_returnsTrue() {
        Intent gcmIntent = new Intent(this.context, GcmIntentServiceMock.class);
        NotificationManager notificationManager = (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);

        GcmIntentServiceMock service = new GcmIntentServiceMock();
        service.onCreate();
        service.onHandleIntent(gcmIntent);

        assertEquals(0, Shadows.shadowOf(notificationManager).size());
    }

    @Test
    public void gcmIntentService_intentWithExtras_returnsTrue() {
        Intent gcmIntent = new Intent(this.context, GcmIntentServiceMock.class);
        gcmIntent.setAction("com.google.android.c2dm.intent.RECEIVE");
        Bundle bundle = new Bundle();
        bundle.putString("message", "{\"message\":\"message_\",\"title\":\"title_\",\"object_type\":\"object_type\",\"object_id\":\"1\"}");
        gcmIntent.putExtras(bundle);

        NotificationManager notificationManager = (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);

        GcmIntentServiceMock service = new GcmIntentServiceMock();
        service.onCreate();
        service.onHandleIntent(gcmIntent);


        ShadowNotificationManager manager = Shadows.shadowOf(notificationManager);
        assertEquals(1, manager.size());

        Notification notification = manager.getNotification(GcmIntentService.NOTIFICATION_TYPE_BEHAVIOR, GcmIntentService.NOTIFICATION_TYPE_BEHAVIOR_ID);
        assertNotNull(notification);

        ShadowNotification shadowNotification = Shadows.shadowOf(notification);
        assertNotNull(shadowNotification);
    }

}
