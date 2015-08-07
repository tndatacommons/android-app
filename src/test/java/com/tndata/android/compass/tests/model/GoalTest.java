package com.tndata.android.compass.tests.model;

import org.junit.Test;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;

import java.lang.reflect.Field;

import static junit.framework.Assert.assertEquals;

public class GoalTest {

    @Test
    public void goal_setter_subtitle_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Goal goal = new Goal();
        String value = new String("subtitle");
        goal.setSubtitle(value);
        final Field field = goal.getClass().getDeclaredField("subtitle");
        field.setAccessible(true);
        assertEquals(value, field.get(goal));
    }

    @Test
    public void goal_setter_outcome_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Goal goal = new Goal();
        String value = new String("outcome");
        goal.setOutcome(value);
        final Field field = goal.getClass().getDeclaredField("outcome");
        field.setAccessible(true);
        assertEquals(value, field.get(goal));
    }

    @Test
    public void goal_setter_icon_url_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Goal goal = new Goal();
        String value = new String("icon_url");
        goal.setIconUrl(value);
        final Field field = goal.getClass().getDeclaredField("icon_url");
        field.setAccessible(true);
        assertEquals(value, field.get(goal));
    }

    @Test
    public void goal_setter_progress_value_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Goal goal = new Goal();
        double value = Math.random();
        goal.setProgressValue(value);
        final Field field = goal.getClass().getDeclaredField("progress_value");
        field.setAccessible(true);
        assertEquals(value, field.get(goal));
    }

    @Test
    public void goal_getter_progress_value_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Goal goal = new Goal();
        double value = Math.random();
        final Field field = goal.getClass().getDeclaredField("progress_value");
        field.setAccessible(true);
        field.set(goal, value);
        final double result = goal.getProgressValue();
        assertEquals(value, result);
    }

    @Test
    public void goal_getter_behaviors_count_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Goal goal = new Goal();
        int value = (int) Math.random();
        final Field field = goal.getClass().getDeclaredField("behaviors_count");
        field.setAccessible(true);
        field.set(goal, value);
        final int result = goal.getBehaviorCount();
        assertEquals(value, result);
    }

    @Test
    public void goal_getter_subtitle_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Goal goal = new Goal();
        String value = new String("more_info");
        final Field field = goal.getClass().getDeclaredField("subtitle");
        field.setAccessible(true);
        field.set(goal, value);
        final String result = goal.getSubtitle();
        assertEquals(value, result);
    }

    @Test
    public void goal_getter_outcome_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Goal goal = new Goal();
        String value = new String("outcome");
        final Field field = goal.getClass().getDeclaredField("outcome");
        field.setAccessible(true);
        field.set(goal, value);
        final String result = goal.getOutcome();
        assertEquals(value, result);
    }

    @Test
    public void goal_getter_icon_url_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Goal goal = new Goal();
        String value = new String("icon_url");
        final Field field = goal.getClass().getDeclaredField("icon_url");
        field.setAccessible(true);
        field.set(goal, value);
        final String result = goal.getIconUrl();
        assertEquals(value, result);
    }

    @Test
    public void goal_getter_lists_ReturnsTrue() {
        Goal goal = new Goal();
        assertEquals(0, goal.getBehaviors().size());
        assertEquals(0, goal.getCategories().size());
    }

    @Test
    public void goal_add_behaviors_operations_ReturnsTrue() {
        Goal goal = new Goal();

        assertEquals(0, goal.getBehaviors().size());

        Behavior behavior1 = new Behavior();
        behavior1.setId(100);
        goal.addBehavior(behavior1);

        assertEquals(1, goal.getBehaviors().size());

        goal.addBehavior(behavior1);
        assertEquals(1, goal.getBehaviors().size());
    }


    @Test
    public void goal_remove_behaviors_operations_ReturnsTrue() {
        Goal goal = new Goal();

        assertEquals(0, goal.getBehaviors().size());

        Behavior behavior1 = new Behavior();
        behavior1.setId(100);
        goal.addBehavior(behavior1);

        Behavior behavior2 = new Behavior();
        behavior2.setId(200);
        goal.addBehavior(behavior2);

        goal.removeBehavior(behavior1);
        assertEquals(1, goal.getBehaviors().size());


        goal.removeBehavior(behavior1);
        assertEquals(1, goal.getBehaviors().size());


        goal.removeBehavior(behavior2);
        assertEquals(0, goal.getBehaviors().size());
    }


    @Test
    public void goal_add_categories_operations_ReturnsTrue() {
        Goal goal = new Goal();

        assertEquals(0, goal.getCategories().size());

        Category category1 = new Category();
        category1.setId(100);
        goal.addCategory(category1);

        assertEquals(1, goal.getCategories().size());

        goal.addCategory(category1);
        assertEquals(1, goal.getCategories().size());
    }


    @Test
    public void goal_remove_categories_operations_ReturnsTrue() {
        Goal goal = new Goal();

        assertEquals(0, goal.getCategories().size());

        Category category1 = new Category();
        category1.setId(100);
        goal.addCategory(category1);

        Category category2 = new Category();
        category2.setId(200);
        goal.addCategory(category2);

        goal.removeCategory(category1);
        assertEquals(1, goal.getCategories().size());

        goal.removeCategory(category1);
        assertEquals(1, goal.getCategories().size());

        goal.removeCategory(category2);
        assertEquals(0, goal.getCategories().size());
    }

}
