package com.tndata.android.compass.tests.model;

import org.junit.Test;
import org.tndata.android.compass.model.ActionContent;
import org.tndata.android.compass.model.BehaviorContent;
import org.tndata.android.compass.model.GoalContent;

import java.lang.reflect.Field;

import static junit.framework.Assert.assertEquals;

public class BehaviorTest {


    @Test
    public void behavior_setter_more_info_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        BehaviorContent behavior = new BehaviorContent();
        String value = new String("more_info");
        behavior.setMoreInfo(value);
        final Field field = behavior.getClass().getDeclaredField("more_info");
        field.setAccessible(true);
        assertEquals(value, field.get(behavior));
    }

    @Test
    public void behavior_setter_html_more_info_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        BehaviorContent behavior = new BehaviorContent();
        String value = new String("html_more_info");
        behavior.setHTMLMoreInfo(value);
        final Field field = behavior.getClass().getDeclaredField("html_more_info");
        field.setAccessible(true);
        assertEquals(value, field.get(behavior));
    }

    @Test
    public void behavior_setter_external_resource_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        BehaviorContent behavior = new BehaviorContent();
        String value = new String("external_resource");
        behavior.setExternalResource(value);
        final Field field = behavior.getClass().getDeclaredField("external_resource");
        field.setAccessible(true);
        assertEquals(value, field.get(behavior));
    }

    @Test
    public void behavior_setter_notification_text_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        BehaviorContent behavior = new BehaviorContent();
        String value = new String("notification_text");
        behavior.setNotificationText(value);
        final Field field = behavior.getClass().getDeclaredField("notification_text");
        field.setAccessible(true);
        assertEquals(value, field.get(behavior));
    }

    @Test
    public void behavior_setter_icon_url_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        BehaviorContent behavior = new BehaviorContent();
        String value = new String("icon_url");
        behavior.setIconUrl(value);
        final Field field = behavior.getClass().getDeclaredField("icon_url");
        field.setAccessible(true);
        assertEquals(value, field.get(behavior));
    }

    @Test
    public void behavior_setter_image_url_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        BehaviorContent behavior = new BehaviorContent();
        String value = new String("image_url");
        behavior.setImageUrl(value);
        final Field field = behavior.getClass().getDeclaredField("image_url");
        field.setAccessible(true);
        assertEquals(value, field.get(behavior));
    }

    @Test
    public void behavior_getter_actions_count_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        BehaviorContent behavior = new BehaviorContent();
        int value = (int) Math.random();
        final Field field = behavior.getClass().getDeclaredField("actions_count");
        field.setAccessible(true);
        field.set(behavior, value);
        final int result = behavior.getActionCount();
        assertEquals(value, result);
    }

    @Test
    public void behavior_getter_more_info_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        BehaviorContent behavior = new BehaviorContent();
        String value = new String("more_info");
        final Field field = behavior.getClass().getDeclaredField("more_info");
        field.setAccessible(true);
        field.set(behavior, value);
        final String result = behavior.getMoreInfo();
        assertEquals(value, result);
    }

    @Test
    public void behavior_getter_html_more_info_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        BehaviorContent behavior = new BehaviorContent();
        String value = new String("html_more_info");
        final Field field = behavior.getClass().getDeclaredField("html_more_info");
        field.setAccessible(true);
        field.set(behavior, value);
        final String result = behavior.getHTMLMoreInfo();
        assertEquals(value, result);
    }

    @Test
    public void behavior_getter_external_resource_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        BehaviorContent behavior = new BehaviorContent();
        String value = new String("external_resource");
        final Field field = behavior.getClass().getDeclaredField("external_resource");
        field.setAccessible(true);
        field.set(behavior, value);
        final String result = behavior.getExternalResource();
        assertEquals(value, result);
    }

    @Test
    public void behavior_getter_notification_text_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        BehaviorContent behavior = new BehaviorContent();
        String value = new String("notification_text");
        final Field field = behavior.getClass().getDeclaredField("notification_text");
        field.setAccessible(true);
        field.set(behavior, value);
        final String result = behavior.getNotificationText();
        assertEquals(value, result);
    }

    @Test
    public void behavior_getter_icon_url_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        BehaviorContent behavior = new BehaviorContent();
        String value = new String("icon_url");
        final Field field = behavior.getClass().getDeclaredField("icon_url");
        field.setAccessible(true);
        field.set(behavior, value);
        final String result = behavior.getIconUrl();
        assertEquals(value, result);
    }

    @Test
    public void behavior_getter_image_url_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        BehaviorContent behavior = new BehaviorContent();
        String value = new String("image_url");
        final Field field = behavior.getClass().getDeclaredField("image_url");
        field.setAccessible(true);
        field.set(behavior, value);
        final String result = behavior.getImageUrl();
        assertEquals(value, result);
    }

    @Test
    public void behavior_getter_lists_ReturnsTrue() {
        BehaviorContent behavior = new BehaviorContent();
        assertEquals(0, behavior.getActions().size());
        assertEquals(0, behavior.getGoalIdSet().size());
        assertEquals(0, behavior.getUserCategories().size());
    }

    @Test
    public void behavior_add_actions_operations_ReturnsTrue() {
        BehaviorContent behavior = new BehaviorContent();

        assertEquals(0, behavior.getActions().size());

        ActionContent action1 = new ActionContent();
        action1.setId(100);
        behavior.addAction(action1);

        assertEquals(1, behavior.getActions().size());

        behavior.addAction(action1);
        assertEquals(1, behavior.getActions().size());
    }


    @Test
    public void behavior_remove_actions_operations_ReturnsTrue() {
        BehaviorContent behavior = new BehaviorContent();

        assertEquals(0, behavior.getActions().size());

        ActionContent action1 = new ActionContent();
        action1.setId(100);
        behavior.addAction(action1);

        ActionContent action2 = new ActionContent();
        action2.setId(200);
        behavior.addAction(action2);

        behavior.removeAction(action1);
        assertEquals(1, behavior.getActions().size());


        behavior.removeAction(action1);
        assertEquals(1, behavior.getActions().size());


        behavior.removeAction(action2);
        assertEquals(0, behavior.getActions().size());
    }


    @Test
    public void behavior_add_goals_operations_ReturnsTrue() {
        BehaviorContent behavior = new BehaviorContent();

        assertEquals(0, behavior.getGoalIdSet().size());

        GoalContent goal1 = new GoalContent();
        goal1.setId(100);
        behavior.addGoal(goal1);

        assertEquals(1, behavior.getGoalIdSet().size());

        behavior.addGoal(goal1);
        assertEquals(1, behavior.getGoalIdSet().size());
    }


    @Test
    public void behavior_remove_goals_operations_ReturnsTrue() {
        BehaviorContent behavior = new BehaviorContent();

        assertEquals(0, behavior.getGoalIdSet().size());

        GoalContent goal1 = new GoalContent();
        goal1.setId(100);
        behavior.addGoal(goal1);

        GoalContent goal2 = new GoalContent();
        goal2.setId(200);
        behavior.addGoal(goal2);

        behavior.removeGoal(goal1);
        assertEquals(1, behavior.getGoalIdSet().size());

        behavior.removeGoal(goal1);
        assertEquals(1, behavior.getGoalIdSet().size());

        behavior.removeGoal(goal2);
        assertEquals(0, behavior.getGoalIdSet().size());
    }


}
