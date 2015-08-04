package com.tndata.android.compass.tests.model;

import org.junit.Test;
import org.tndata.android.compass.model.Trigger;

import java.lang.reflect.Field;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class TriggerTest {


    @Test
    public void trigger_setter_id_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Trigger trigger = new Trigger();
        int value = (int) Math.random();
        trigger.setId(value);
        final Field field = trigger.getClass().getDeclaredField("id");
        field.setAccessible(true);
        assertEquals(value, field.get(trigger));
    }

    @Test
    public void trigger_setter_recurrences_display_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Trigger trigger = new Trigger();
        String value = new String("recurrences_display");
        trigger.setRecurrencesDisplay(value);
        final Field field = trigger.getClass().getDeclaredField("recurrences_display");
        field.setAccessible(true);
        assertEquals(value, field.get(trigger));
    }

    @Test
    public void trigger_setter_name_slug_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Trigger trigger = new Trigger();
        String value = new String("name_slug");
        trigger.setNameSlug(value);
        final Field field = trigger.getClass().getDeclaredField("name_slug");
        field.setAccessible(true);
        assertEquals(value, field.get(trigger));
    }

    @Test
    public void trigger_setter_location_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Trigger trigger = new Trigger();
        String value = new String("location");
        trigger.setLocation(value);
        final Field field = trigger.getClass().getDeclaredField("location");
        field.setAccessible(true);
        assertEquals(value, field.get(trigger));
    }

    @Test
    public void trigger_setter_name_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Trigger trigger = new Trigger();
        String value = new String("name");
        trigger.setName(value);
        final Field field = trigger.getClass().getDeclaredField("name");
        field.setAccessible(true);
        assertEquals(value, field.get(trigger));
    }

    @Test
    public void trigger_setter_recurrences_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Trigger trigger = new Trigger();
        String value = new String("recurrences");
        trigger.setRecurrences(value);
        final Field field = trigger.getClass().getDeclaredField("recurrences");
        field.setAccessible(true);
        assertEquals(value, field.get(trigger));
    }

    @Test
    public void trigger_setter_time_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Trigger trigger = new Trigger();
        String value = new String("time");
        trigger.setTime(value);
        final Field field = trigger.getClass().getDeclaredField("time");
        field.setAccessible(true);
        assertEquals(value, field.get(trigger));
    }

    @Test
    public void trigger_setter_date_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Trigger trigger = new Trigger();
        String value = new String("trigger_date");
        trigger.setDate(value);
        final Field field = trigger.getClass().getDeclaredField("trigger_date");
        field.setAccessible(true);
        assertEquals(value, field.get(trigger));
    }

    @Test
    public void trigger_getter_id_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Trigger trigger = new Trigger();
        int value = (int) Math.random();
        final Field field = trigger.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(trigger, value);
        final int result = trigger.getId();
        assertEquals(value, result);
    }

    @Test
    public void trigger_getter_recurrences_display_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Trigger trigger = new Trigger();
        String value = new String("recurrences_display");
        final Field field = trigger.getClass().getDeclaredField("recurrences_display");
        field.setAccessible(true);
        field.set(trigger, value);
        final String result = trigger.getRecurrencesDisplay();
        assertEquals(value, result);
    }

    @Test
    public void trigger_getter_recurrences_name_slug_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Trigger trigger = new Trigger();
        String value = new String("name_slug");
        final Field field = trigger.getClass().getDeclaredField("name_slug");
        field.setAccessible(true);
        field.set(trigger, value);
        final String result = trigger.getNameSlug();
        assertEquals(value, result);
    }

    @Test
    public void trigger_getter_recurrences_location_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Trigger trigger = new Trigger();
        String value = new String("location");
        final Field field = trigger.getClass().getDeclaredField("location");
        field.setAccessible(true);
        field.set(trigger, value);
        final String result = trigger.getLocation();
        assertEquals(value, result);
    }

    @Test
    public void trigger_getter_recurrences_name_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Trigger trigger = new Trigger();
        String value = new String("name");
        final Field field = trigger.getClass().getDeclaredField("name");
        field.setAccessible(true);
        field.set(trigger, value);
        final String result = trigger.getName();
        assertEquals(value, result);
    }

    @Test
    public void trigger_getter_recurrences_recurrences_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Trigger trigger = new Trigger();
        String value = new String("recurrences");
        final Field field = trigger.getClass().getDeclaredField("recurrences");
        field.setAccessible(true);
        field.set(trigger, value);
        final String result = trigger.getRecurrences();
        assertEquals(value, result);
    }

    @Test
    public void trigger_getter_recurrences_time_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Trigger trigger = new Trigger();
        String value = new String("time");
        final Field field = trigger.getClass().getDeclaredField("time");
        field.setAccessible(true);
        field.set(trigger, value);
        final String result = trigger.getTime();
        assertEquals(value, result);
    }

    @Test
    public void trigger_getter_recurrences_trigger_date_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Trigger trigger = new Trigger();
        String value = new String("trigger_date");
        final Field field = trigger.getClass().getDeclaredField("trigger_date");
        field.setAccessible(true);
        field.set(trigger, value);
        final String result = trigger.getDate();
        assertEquals(value, result);
    }

    @Test
    public void trigger_default_trigger_ReturnsFalse() {
        Trigger trigger = new Trigger();
        assertFalse(trigger.isDefaultTrigger());
    }

    @Test
    public void trigger_isDisabled_nameEmpty_ReturnsFalse() {
        Trigger trigger = new Trigger();
        assertFalse(trigger.isDisabled());
    }


    @Test
    public void trigger_getRRULE_recurrencesEmpty_ReturnsTrue() {
        Trigger trigger = new Trigger();
        assertEquals(trigger.getRRULE(), "");
    }

    @Test
    public void trigger_getRRULE_recurrencesNotEmpty_ReturnsTrue() {
        Trigger trigger = new Trigger();
        trigger.setRecurrences("RRULE:FREQ=DAILY");
        assertEquals(trigger.getRRULE(), "FREQ=DAILY");
    }

    @Test
    public void trigger_isDisabled_nameNotEmpty_ReturnsTrue() {
        Trigger trigger = new Trigger();
        trigger.setName("name");
        assertTrue(trigger.isDisabled());
    }

    @Test
    public void trigger_isDisabled_nameNotEmpty_dateNotEmpty_ReturnsFalse() {
        Trigger trigger = new Trigger();
        trigger.setName("name");
        trigger.setDate("date");
        assertFalse(trigger.isDisabled());
    }

    @Test
    public void trigger_isDisabled_nameNotEmpty_timeNotEmpty_ReturnsFalse() {
        Trigger trigger = new Trigger();
        trigger.setName("name");
        trigger.setTime("time");
        assertFalse(trigger.isDisabled());
    }

    @Test
    public void trigger_isDisabled_nameNotEmpty_recurrencesNotEmpty_ReturnsFalse() {
        Trigger trigger = new Trigger();
        trigger.setName("name");
        trigger.setRecurrences("recurrences");
        assertFalse(trigger.isDisabled());
    }

    @Test
    public void trigger_getFormattedDate_empty_ReturnsTrue() {
        Trigger trigger = new Trigger();
        assertEquals(trigger.getFormattedDate(), "");
    }

    @Test
    public void trigger_getFormattedTime_empty_ReturnsTrue() {
        Trigger trigger = new Trigger();
        assertEquals(trigger.getFormattedTime(), "");
    }

    @Test
    public void trigger_getFormattedTime_format_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Trigger trigger = new Trigger();
        final Field field = trigger.getClass().getDeclaredField("mDefaultTrigger");
        field.setAccessible(true);
        field.set(trigger, true);

        trigger.setTime("07:00:00");
        assertEquals(trigger.getFormattedTime(), "7:00 AM");
    }

    @Test
    public void trigger_getFormattedDate_format_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Trigger trigger = new Trigger();
        final Field field = trigger.getClass().getDeclaredField("mDefaultTrigger");
        field.setAccessible(true);
        field.set(trigger, true);

        trigger.setDate("2015-06-03T15:36:14.400558Z");
        assertEquals(trigger.getFormattedDate(), "Jun 3 2015");
    }


}
