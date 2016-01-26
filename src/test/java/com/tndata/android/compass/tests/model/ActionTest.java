package com.tndata.android.compass.tests.model;

import org.junit.Test;
import org.tndata.android.compass.model.ActionContent;
import org.tndata.android.compass.model.Behavior;

import java.lang.reflect.Field;

import static junit.framework.Assert.assertEquals;


public class ActionTest {


    @Test
    public void action_setter_behavior_id_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        ActionContent action = new ActionContent();
        int value = (int) Math.random();
        action.setBehavior_id(value);
        final Field field = action.getClass().getDeclaredField("behavior_id");
        field.setAccessible(true);
        assertEquals(value, field.get(action));
    }

    @Test
    public void action_setter_sequence_order_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        ActionContent action = new ActionContent();
        int value = (int) Math.random();
        action.setSequenceOrder(value);
        final Field field = action.getClass().getDeclaredField("sequence_order");
        field.setAccessible(true);
        assertEquals(value, field.get(action));
    }

    @Test
    public void action_setter_more_info_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        ActionContent action = new ActionContent();
        String value = new String("more_info");
        action.setMoreInfo(value);
        final Field field = action.getClass().getDeclaredField("more_info");
        field.setAccessible(true);
        assertEquals(value, field.get(action));
    }

    @Test
    public void action_setter_html_more_info_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        ActionContent action = new ActionContent();
        String value = new String("html_more_info");
        action.setHTMLMoreInfo(value);
        final Field field = action.getClass().getDeclaredField("html_more_info");
        field.setAccessible(true);
        assertEquals(value, field.get(action));
    }

    @Test
    public void action_setter_external_resource_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        ActionContent action = new ActionContent();
        String value = new String("external_resource");
        action.setExternalResource(value);
        final Field field = action.getClass().getDeclaredField("external_resource");
        field.setAccessible(true);
        assertEquals(value, field.get(action));
    }

    @Test
    public void action_setter_notification_text_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        ActionContent action = new ActionContent();
        String value = new String("notification_text");
        action.setNotificationText(value);
        final Field field = action.getClass().getDeclaredField("notification_text");
        field.setAccessible(true);
        assertEquals(value, field.get(action));
    }

    @Test
    public void action_setter_icon_url_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        ActionContent action = new ActionContent();
        String value = new String("icon_url");
        action.setIconUrl(value);
        final Field field = action.getClass().getDeclaredField("icon_url");
        field.setAccessible(true);
        assertEquals(value, field.get(action));
    }

    @Test
    public void action_setter_image_url_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        ActionContent action = new ActionContent();
        String value = new String("image_url");
        action.setImageUrl(value);
        final Field field = action.getClass().getDeclaredField("image_url");
        field.setAccessible(true);
        assertEquals(value, field.get(action));
    }

    @Test
    public void action_getter_behavior_id_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        ActionContent action = new ActionContent();
        int value = (int) Math.random();
        final Field field = action.getClass().getDeclaredField("behavior_id");
        field.setAccessible(true);
        field.set(action, value);
        final int result = action.getBehavior_id();
        assertEquals(value, result);
    }

    @Test
    public void action_getter_sequence_order_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        ActionContent action = new ActionContent();
        int value = (int) Math.random();
        final Field field = action.getClass().getDeclaredField("sequence_order");
        field.setAccessible(true);
        field.set(action, value);
        final int result = action.getSequenceOrder();
        assertEquals(value, result);
    }

    @Test
    public void action_getter_more_info_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        ActionContent action = new ActionContent();
        String value = new String("more_info");
        final Field field = action.getClass().getDeclaredField("more_info");
        field.setAccessible(true);
        field.set(action, value);
        final String result = action.getMoreInfo();
        assertEquals(value, result);
    }

    @Test
    public void action_getter_html_more_info_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        ActionContent action = new ActionContent();
        String value = new String("html_more_info");
        final Field field = action.getClass().getDeclaredField("html_more_info");
        field.setAccessible(true);
        field.set(action, value);
        final String result = action.getHTMLMoreInfo();
        assertEquals(value, result);
    }

    @Test
    public void action_getter_external_resource_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        ActionContent action = new ActionContent();
        String value = new String("external_resource");
        final Field field = action.getClass().getDeclaredField("external_resource");
        field.setAccessible(true);
        field.set(action, value);
        final String result = action.getExternalResource();
        assertEquals(value, result);
    }

    @Test
    public void action_getter_notification_text_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        ActionContent action = new ActionContent();
        String value = new String("notification_text");
        final Field field = action.getClass().getDeclaredField("notification_text");
        field.setAccessible(true);
        field.set(action, value);
        final String result = action.getNotificationText();
        assertEquals(value, result);
    }

    @Test
    public void action_getter_icon_url_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        ActionContent action = new ActionContent();
        String value = new String("icon_url");
        final Field field = action.getClass().getDeclaredField("icon_url");
        field.setAccessible(true);
        field.set(action, value);
        final String result = action.getIconUrl();
        assertEquals(value, result);
    }

    @Test
    public void action_getter_image_url_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        ActionContent action = new ActionContent();
        String value = new String("image_url");
        final Field field = action.getClass().getDeclaredField("image_url");
        field.setAccessible(true);
        field.set(action, value);
        final String result = action.getImageUrl();
        assertEquals(value, result);
    }

    @Test
    public void action_getter_behavior_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        ActionContent action = new ActionContent();
        Behavior value = new Behavior();
        final Field field = action.getClass().getDeclaredField("behavior");
        field.setAccessible(true);
        field.set(action, value);
        final Behavior result = action.getBehaviorId();
        assertEquals(value, result);
    }

}
