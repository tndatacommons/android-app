package com.tndata.android.compass.tests.model;

import org.junit.Test;
import org.tndata.android.compass.model.User;

import java.lang.reflect.Field;

import static junit.framework.Assert.assertEquals;

public class UserTest {

    @Test
    public void user_setter_id_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        User user = new User();
        int value = (int) Math.random();
        user.setId(value);
        final Field field = user.getClass().getDeclaredField("id");
        field.setAccessible(true);
        assertEquals(value, field.get(user));
    }

    @Test
    public void user_setter_userprofile_id_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        User user = new User();
        int value = (int) Math.random();
        user.setProfileId(value);
        final Field field = user.getClass().getDeclaredField("userprofile_id");
        field.setAccessible(true);
        assertEquals(value, field.get(user));
    }

    @Test
    public void user_setter_first_name_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        User user = new User();
        String value = new String("first_name");
        user.setFirstName(value);
        final Field field = user.getClass().getDeclaredField("first_name");
        field.setAccessible(true);
        assertEquals(value, field.get(user));
    }

    @Test
    public void user_setter_last_name_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        User user = new User();
        String value = new String("last_name");
        user.setLastName(value);
        final Field field = user.getClass().getDeclaredField("last_name");
        field.setAccessible(true);
        assertEquals(value, field.get(user));
    }

    @Test
    public void user_setter_full_name_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        User user = new User();
        String value = new String("full_name");
        user.setFullName(value);
        final Field field = user.getClass().getDeclaredField("full_name");
        field.setAccessible(true);
        assertEquals(value, field.get(user));
    }

    @Test
    public void user_setter_email_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        User user = new User();
        String value = new String("email");
        user.setEmail(value);
        final Field field = user.getClass().getDeclaredField("email");
        field.setAccessible(true);
        assertEquals(value, field.get(user));
    }

    @Test
    public void user_setter_date_joined_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        User user = new User();
        String value = new String("date_joined");
        user.setDateJoined(value);
        final Field field = user.getClass().getDeclaredField("date_joined");
        field.setAccessible(true);
        assertEquals(value, field.get(user));
    }

    @Test
    public void user_setter_token_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        User user = new User();
        String value = new String("token");
        user.setToken(value);
        final Field field = user.getClass().getDeclaredField("token");
        field.setAccessible(true);
        assertEquals(value, field.get(user));
    }

    @Test
    public void user_setter_password_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        User user = new User();
        String value = new String("password");
        user.setPassword(value);
        final Field field = user.getClass().getDeclaredField("password");
        field.setAccessible(true);
        assertEquals(value, field.get(user));
    }

    @Test
    public void user_setter_error_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        User user = new User();
        String value = new String("error");
        user.setError(value);
        final Field field = user.getClass().getDeclaredField("error");
        field.setAccessible(true);
        assertEquals(value, field.get(user));
    }

    @Test
    public void user_getter_id_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        User user = new User();
        int value = (int) Math.random();
        final Field field = user.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(user, value);
        final int result = user.getId();
        assertEquals(value, result);
    }

    @Test
    public void user_getter_userprofile_id_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        User user = new User();
        int value = (int) Math.random();
        final Field field = user.getClass().getDeclaredField("userprofile_id");
        field.setAccessible(true);
        field.set(user, value);
        final int result = user.getProfileId();
        assertEquals(value, result);
    }

    @Test
    public void user_getter_first_name_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        User user = new User();
        String value = new String("first_name");
        final Field field = user.getClass().getDeclaredField("first_name");
        field.setAccessible(true);
        field.set(user, value);
        final String result = user.getFirstName();
        assertEquals(value, result);
    }

    @Test
    public void user_getter_last_name_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        User user = new User();
        String value = new String("last_name");
        final Field field = user.getClass().getDeclaredField("last_name");
        field.setAccessible(true);
        field.set(user, value);
        final String result = user.getLastName();
        assertEquals(value, result);
    }

    @Test
    public void user_getter_full_name_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        User user = new User();
        String value = new String("full_name");
        final Field field = user.getClass().getDeclaredField("full_name");
        field.setAccessible(true);
        field.set(user, value);
        final String result = user.getFullName();
        assertEquals(value, result);
    }

    @Test
    public void user_getter_email_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        User user = new User();
        String value = new String("email");
        final Field field = user.getClass().getDeclaredField("email");
        field.setAccessible(true);
        field.set(user, value);
        final String result = user.getEmail();
        assertEquals(value, result);
    }

    @Test
    public void user_getter_date_joined_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        User user = new User();
        String value = new String("date_joined");
        final Field field = user.getClass().getDeclaredField("date_joined");
        field.setAccessible(true);
        field.set(user, value);
        final String result = user.getDateJoined();
        assertEquals(value, result);
    }

    @Test
    public void user_getter_token_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        User user = new User();
        String value = new String("token");
        final Field field = user.getClass().getDeclaredField("token");
        field.setAccessible(true);
        field.set(user, value);
        final String result = user.getToken();
        assertEquals(value, result);
    }

    @Test
    public void user_getter_password_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        User user = new User();
        String value = new String("password");
        final Field field = user.getClass().getDeclaredField("password");
        field.setAccessible(true);
        field.set(user, value);
        final String result = user.getPassword();
        assertEquals(value, result);
    }

    @Test
    public void user_getter_error_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        User user = new User();
        String value = new String("error");
        final Field field = user.getClass().getDeclaredField("error");
        field.setAccessible(true);
        field.set(user, value);
        final String result = user.getError();
        assertEquals(value, result);
    }


}
