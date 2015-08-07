package com.tndata.android.compass.tests;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.tndata.android.compass.BuildConfig;
import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.model.User;
import org.tndata.android.compass.model.UserData;

import java.lang.reflect.Field;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = "src/main/AndroidManifest.xml")
public class CompassApplicationTest {

    private static CompassApplication application;

    @Before
    public void setup() {
        application = (CompassApplication) RuntimeEnvironment.application;
    }

    @Test
    public void application_notNull_returnsTrue() {
        assertNotNull(application);
    }

    @Test
    public void userData_notNull_returnsTrue() {
        assertNotNull(application.getUserData());
    }

    @Test
    public void user_Null_returnsTrue() {
        assertNull(application.getUser());
    }

    @Test
    public void token_Null_returnsTrue() {
        assertNull(application.getToken());
    }

    @Test
    public void setUser_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        User user = new User();
        int value = (int) Math.random();
        user.setId(value);
        application.setUser(user);
        final Field field = application.getClass().getDeclaredField("mUser");
        field.setAccessible(true);
        assertEquals(user, field.get(application));
    }

    @Test
    public void getUser_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        User user = new User();
        int value = (int) Math.random();
        user.setId(value);
        final Field field = application.getClass().getDeclaredField("mUser");
        field.setAccessible(true);
        field.set(application, user);
        final User result = application.getUser();
        assertEquals(user, result);
    }

    @Test
    public void setUserData_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        UserData userData = new UserData();
        application.setUserData(userData);
        final Field field = application.getClass().getDeclaredField("mUserData");
        field.setAccessible(true);
        assertEquals(userData, field.get(application));
    }

    @Test
    public void getUserData_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        UserData userData = new UserData();
        final Field field = application.getClass().getDeclaredField("mUserData");
        field.setAccessible(true);
        field.set(application, userData);
        final UserData result = application.getUserData();
        assertEquals(userData, result);
    }

    @Test
    public void setToken_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        String token = new String(UUID.randomUUID().toString());
        application.setToken(token);
        final Field field = application.getClass().getDeclaredField("mToken");
        field.setAccessible(true);
        assertEquals(token, field.get(application));
    }

    @Test
    public void getToken_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        String token = new String(UUID.randomUUID().toString());
        final Field field = application.getClass().getDeclaredField("mToken");
        field.setAccessible(true);
        field.set(application, token);
        final String result = application.getToken();
        assertEquals(token, result);
    }


}