package com.tndata.android.compass.tests.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import org.tndata.android.compass.BuildConfig;
import org.tndata.android.compass.receiver.GcmBroadcastReceiver;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 19, manifest = "src/main/AndroidManifest.xml")
public class GcmBroadcastReceiverTest {

    private ShadowApplication application;
    private Context context;

    @Before
    public void setup() {
        this.application = ShadowApplication.getInstance();
        this.context = this.application.getApplicationContext();
    }

    @Test
    public void gcmBroadcastReceiverTest_returnsTrue() {
        List<ShadowApplication.Wrapper> registeredReceivers = application.getRegisteredReceivers();

        assertFalse(registeredReceivers.isEmpty());

        boolean receiverFound = false;
        for (ShadowApplication.Wrapper wrapper : registeredReceivers) {
            if (!receiverFound)
                receiverFound = GcmBroadcastReceiver.class.getSimpleName().equals(
                        wrapper.broadcastReceiver.getClass().getSimpleName());
        }

        assertTrue(receiverFound);
    }

    @Test
    public void gcmBroadcastReceiverTest_handling_returnsTrue() {
        Intent intent = new Intent("com.google.android.c2dm.intent.RECEIVE");

        ShadowApplication shadowApplication = application;
        assertTrue(shadowApplication.hasReceiverForIntent(intent));
    }

    @Test
    public void gcmBroadcastReceiverTest_handler_number_returnsTrue() {
        Intent intent = new Intent("com.google.android.c2dm.intent.RECEIVE");
        List<BroadcastReceiver> receiversForIntent = application.getReceiversForIntent(intent);

        assertEquals(1, receiversForIntent.size());
    }

    //@Test
    public void gcmBroadcastReceiverTest_is_invoked_returnsTrue() throws IllegalAccessException {
        Intent intent = new Intent("com.google.android.c2dm.intent.RECEIVE");
        GcmBroadcastReceiver receiver = (GcmBroadcastReceiver) application.getReceiversForIntent(intent).get(0);
        receiver.onReceive(context, new Intent());

        Intent serviceIntent = application.peekNextStartedService();

        assertEquals(
                GcmBroadcastReceiver.class.getCanonicalName(),
                serviceIntent.getComponent().getClassName());

    }


}
