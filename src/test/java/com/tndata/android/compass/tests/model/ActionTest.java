package com.tndata.android.compass.tests.model;

import org.junit.Test;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Trigger;

import java.lang.reflect.Field;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;


public class ActionTest {


    @Test
    public void action_setter_behavior_id_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Action action = new Action();
        int value = (int) Math.random();
        action.setBehavior_id(value);
        final Field field = action.getClass().getDeclaredField("behavior_id");
        field.setAccessible(true);
        assertEquals(value, field.get(action));
    }

    @Test
    public void action_setter_sequence_order_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Action action = new Action();
        int value = (int) Math.random();
        action.setSequenceOrder(value);
        final Field field = action.getClass().getDeclaredField("sequence_order");
        field.setAccessible(true);
        assertEquals(value, field.get(action));
    }

    @Test
    public void action_setter_more_info_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Action action = new Action();
        String value = new String("more_info");
        action.setMoreInfo(value);
        final Field field = action.getClass().getDeclaredField("more_info");
        field.setAccessible(true);
        assertEquals(value, field.get(action));
    }

    @Test
    public void action_setter_html_more_info_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Action action = new Action();
        String value = new String("html_more_info");
        action.setHTMLMoreInfo(value);
        final Field field = action.getClass().getDeclaredField("html_more_info");
        field.setAccessible(true);
        assertEquals(value, field.get(action));
    }

    @Test
    public void action_setter_external_resource_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Action action = new Action();
        String value = new String("external_resource");
        action.setExternalResource(value);
        final Field field = action.getClass().getDeclaredField("external_resource");
        field.setAccessible(true);
        assertEquals(value, field.get(action));
    }

    @Test
    public void action_setter_notification_text_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Action action = new Action();
        String value = new String("notification_text");
        action.setNotificationText(value);
        final Field field = action.getClass().getDeclaredField("notification_text");
        field.setAccessible(true);
        assertEquals(value, field.get(action));
    }

    @Test
    public void action_setter_icon_url_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Action action = new Action();
        String value = new String("icon_url");
        action.setIconUrl(value);
        final Field field = action.getClass().getDeclaredField("icon_url");
        field.setAccessible(true);
        assertEquals(value, field.get(action));
    }

    @Test
    public void action_setter_image_url_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Action action = new Action();
        String value = new String("image_url");
        action.setImageUrl(value);
        final Field field = action.getClass().getDeclaredField("image_url");
        field.setAccessible(true);
        assertEquals(value, field.get(action));
    }

    @Test
    public void action_getter_behavior_id_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Action action = new Action();
        int value = (int) Math.random();
        final Field field = action.getClass().getDeclaredField("behavior_id");
        field.setAccessible(true);
        field.set(action, value);
        final int result = action.getBehavior_id();
        assertEquals(value, result);
    }

    @Test
    public void action_getter_sequence_order_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Action action = new Action();
        int value = (int) Math.random();
        final Field field = action.getClass().getDeclaredField("sequence_order");
        field.setAccessible(true);
        field.set(action, value);
        final int result = action.getSequenceOrder();
        assertEquals(value, result);
    }

    @Test
    public void action_getter_more_info_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Action action = new Action();
        String value = new String("more_info");
        final Field field = action.getClass().getDeclaredField("more_info");
        field.setAccessible(true);
        field.set(action, value);
        final String result = action.getMoreInfo();
        assertEquals(value, result);
    }

    @Test
    public void action_getter_html_more_info_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Action action = new Action();
        String value = new String("html_more_info");
        final Field field = action.getClass().getDeclaredField("html_more_info");
        field.setAccessible(true);
        field.set(action, value);
        final String result = action.getHTMLMoreInfo();
        assertEquals(value, result);
    }

    @Test
    public void action_getter_external_resource_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Action action = new Action();
        String value = new String("external_resource");
        final Field field = action.getClass().getDeclaredField("external_resource");
        field.setAccessible(true);
        field.set(action, value);
        final String result = action.getExternalResource();
        assertEquals(value, result);
    }

    @Test
    public void action_getter_notification_text_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Action action = new Action();
        String value = new String("notification_text");
        final Field field = action.getClass().getDeclaredField("notification_text");
        field.setAccessible(true);
        field.set(action, value);
        final String result = action.getNotificationText();
        assertEquals(value, result);
    }

    @Test
    public void action_getter_icon_url_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Action action = new Action();
        String value = new String("icon_url");
        final Field field = action.getClass().getDeclaredField("icon_url");
        field.setAccessible(true);
        field.set(action, value);
        final String result = action.getIconUrl();
        assertEquals(value, result);
    }

    @Test
    public void action_getter_image_url_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Action action = new Action();
        String value = new String("image_url");
        final Field field = action.getClass().getDeclaredField("image_url");
        field.setAccessible(true);
        field.set(action, value);
        final String result = action.getImageUrl();
        assertEquals(value, result);
    }

    @Test
    public void action_getter_behavior_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Action action = new Action();
        Behavior value = new Behavior();
        final Field field = action.getClass().getDeclaredField("behavior");
        field.setAccessible(true);
        field.set(action, value);
        final Behavior result = action.getBehavior();
        assertEquals(value, result);
    }

    @Test
    public void action_getter_default_trigger_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Action action = new Action();
        Trigger value = new Trigger();
        final Field field = action.getClass().getDeclaredField("default_trigger");
        field.setAccessible(true);
        field.set(action, value);
        final Trigger result = action.getDefaultTrigger();
        assertFalse(result.isDefaultTrigger());
    }

    @Test
    public void action_getter_custom_trigger_ReturnsFalse() throws NoSuchFieldException, IllegalAccessException {
        Action action = new Action();
        Trigger value = new Trigger();
        final Field field = action.getClass().getDeclaredField("custom_trigger");
        field.setAccessible(true);
        field.set(action, value);
        final Trigger result = action.getCustomTrigger();
        assertFalse(result.isDefaultTrigger());
    }

    @Test
    public void action_getter_getTrigger()  {
        Action action = new Action();

        assertFalse(action.getTrigger().isDefaultTrigger());

        Trigger value = new Trigger();
        value.setId(100);
        action.setDefaultTrigger(value);
        assertTrue(action.getTrigger().isDefaultTrigger());
        assertEquals(100, action.getTrigger().getId());

        Trigger value2 = new Trigger();
        value2.setId(200);
        action.setCustomTrigger(value2);
        assertFalse(action.getTrigger().isDefaultTrigger());
        assertEquals(200, action.getTrigger().getId());

    }

}
