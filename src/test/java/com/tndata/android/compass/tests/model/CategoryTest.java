package com.tndata.android.compass.tests.model;

import org.junit.Test;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;

import java.lang.reflect.Field;

import static junit.framework.Assert.assertEquals;

public class CategoryTest {

    @Test
    public void category_setter_color_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Category category = new Category();
        String value = new String("color");
        category.setColor(value);
        final Field field = category.getClass().getDeclaredField("color");
        field.setAccessible(true);
        assertEquals(value, field.get(category));
    }

    @Test
    public void category_setter_secondary_color_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Category category = new Category();
        String value = new String("secondary_color");
        category.setSecondaryColor(value);
        final Field field = category.getClass().getDeclaredField("secondary_color");
        field.setAccessible(true);
        assertEquals(value, field.get(category));
    }

    @Test
    public void category_setter_icon_url_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Category category = new Category();
        String value = new String("icon_url");
        category.setIconUrl(value);
        final Field field = category.getClass().getDeclaredField("icon_url");
        field.setAccessible(true);
        assertEquals(value, field.get(category));
    }

    @Test
    public void category_setter_image_url_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Category category = new Category();
        String value = new String("image_url");
        category.setImageUrl(value);
        final Field field = category.getClass().getDeclaredField("image_url");
        field.setAccessible(true);
        assertEquals(value, field.get(category));
    }

    @Test
    public void category_getter_order_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Category category = new Category();
        int value = (int) Math.random();
        final Field field = category.getClass().getDeclaredField("order");
        field.setAccessible(true);
        field.set(category, value);
        final int result = category.getOrder();
        assertEquals(value, result);
    }

    @Test
    public void category_getter_goals_count_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Category category = new Category();
        int value = (int) Math.random();
        final Field field = category.getClass().getDeclaredField("goals_count");
        field.setAccessible(true);
        field.set(category, value);
        final int result = category.getGoalCount();
        assertEquals(value, result);
    }

    @Test
    public void category_getter_progress_value_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Category category = new Category();
        double value = Math.random();
        final Field field = category.getClass().getDeclaredField("progress_value");
        field.setAccessible(true);
        field.set(category, value);
        final double result = category.getProgressValue();
        assertEquals(value, result);
    }

    @Test
    public void category_getter_color_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Category category = new Category();
        String value = new String("color");
        final Field field = category.getClass().getDeclaredField("color");
        field.setAccessible(true);
        field.set(category, value);
        final String result = category.getColor();
        assertEquals(value, result);
    }

    @Test
    public void category_getter_secondary_color_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Category category = new Category();
        String value = new String("secondary_color");
        final Field field = category.getClass().getDeclaredField("secondary_color");
        field.setAccessible(true);
        field.set(category, value);
        final String result = category.getSecondaryColor();
        assertEquals(value, result);
    }

    @Test
    public void category_getter_icon_url_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Category category = new Category();
        String value = new String("icon_url");
        final Field field = category.getClass().getDeclaredField("icon_url");
        field.setAccessible(true);
        field.set(category, value);
        final String result = category.getIconUrl();
        assertEquals(value, result);
    }

    @Test
    public void category_getter_image_url_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
        Category category = new Category();
        String value = new String("image_url");
        final Field field = category.getClass().getDeclaredField("image_url");
        field.setAccessible(true);
        field.set(category, value);
        final String result = category.getImageUrl();
        assertEquals(value, result);
    }

    @Test
    public void category_getter_lists_ReturnsTrue() {
        Category category = new Category();
        assertEquals(0, category.getGoals().size());
    }

    @Test
    public void category_add_goals_operations_ReturnsTrue() {
        Category category = new Category();

        assertEquals(0, category.getGoals().size());

        Goal goal1 = new Goal();
        goal1.setId(100);
        category.addGoal(goal1);

        assertEquals(1, category.getGoals().size());

        category.addGoal(goal1);
        assertEquals(1, category.getGoals().size());
    }


    @Test
    public void category_remove_goals_operations_ReturnsTrue() {
        Category category = new Category();

        assertEquals(0, category.getGoals().size());

        Goal goal1 = new Goal();
        goal1.setId(100);
        category.addGoal(goal1);

        Goal goal2 = new Goal();
        goal2.setId(200);
        category.addGoal(goal2);

        category.removeGoal(goal1);
        assertEquals(1, category.getGoals().size());

        category.removeGoal(goal1);
        assertEquals(1, category.getGoals().size());

        category.removeGoal(goal2);
        assertEquals(0, category.getGoals().size());
    }

}
