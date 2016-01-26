package com.tndata.android.compass.tests.model;

import org.junit.Test;
import org.tndata.android.compass.model.ActionContent;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.UserData;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

public class UserDataTest {

    @Test
    public void userData_setter_actions_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {

        UserData userData = new UserData();

        ArrayList<ActionContent> actions = new ArrayList<>();
        ActionContent actionOne = new ActionContent();
        actionOne.setId(1);
        actions.add(actionOne);

        userData.setActions(actions);

        final Field field = userData.getClass().getDeclaredField("mActions");
        field.setAccessible(true);
        assertEquals(actions, field.get(userData));
    }

    @Test
    public void userData_getter_actions_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {

        UserData userData = new UserData();

        ArrayList<ActionContent> actions = new ArrayList<>();
        ActionContent actionOne = new ActionContent();
        actionOne.setId(1);
        actions.add(actionOne);

        final Field field = userData.getClass().getDeclaredField("mActions");
        field.setAccessible(true);
        field.set(userData, actions);

        userData.setActions(actions);
        assertEquals(actions, userData.getActions());
    }

    @Test
    public void userData_remove_action_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {

        UserData userData = new UserData();

        ArrayList<ActionContent> actions = new ArrayList<>();
        ActionContent actionOne = new ActionContent();
        actionOne.setId(1);
        ActionContent actionTwo = new ActionContent();
        actionTwo.setId(2);
        actions.add(actionTwo);

        userData.setActions(actions);

        userData.removeAction(actionOne);

        assertEquals(1, userData.getActions().size());

        assertEquals(actionTwo, userData.getActions().get(0));

        //check remove same object second time

        userData.removeAction(actionOne);

        assertEquals(1, userData.getActions().size());

        assertEquals(actionTwo, userData.getActions().get(0));

    }

    @Test
    public void userData_add_action_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {

        UserData userData = new UserData();

        ArrayList<ActionContent> actions = new ArrayList<>();
        ActionContent actionOne = new ActionContent();
        actionOne.setId(1);

        userData.addAction(actionOne);

        assertEquals(1, userData.getActions().size());

        assertEquals(actionOne, userData.getActions().get(0));

        //check add same object second time

        userData.addAction(actionOne);

        assertEquals(1, userData.getActions().size());

        assertEquals(actionOne, userData.getActions().get(0));

    }

    @Test
    public void userData_update_action_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {

        UserData userData = new UserData();

        ArrayList<ActionContent> actions = new ArrayList<>();
        ActionContent actionOne = new ActionContent();
        actionOne.setId(1);
        actionOne.setNotificationText("text");

        userData.addAction(actionOne);

        actionOne.setNotificationText("textUpdated");

        //userData.updateAction(actionOne);

        assertEquals(actionOne.getNotificationText(), userData.getActions().get(0).getNotificationText());

    }


    @Test
    public void userData_add_new_action_on_update_action_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {

        UserData userData = new UserData();

        ArrayList<ActionContent> actions = new ArrayList<>();
        ActionContent actionOne = new ActionContent();
        actionOne.setId(1);
        actionOne.setNotificationText("text");

        //userData.updateAction(actionOne);

        assertEquals(1, userData.getActions().size());

        assertEquals(actionOne.getNotificationText(), userData.getActions().get(0).getNotificationText());

    }


    @Test
    public void userData_setter_behaviors_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {

        UserData userData = new UserData();

        ArrayList<Behavior> behaviors = new ArrayList<>();
        Behavior behaviorOne = new Behavior();
        behaviorOne.setId(1);
        behaviors.add(behaviorOne);

        userData.setBehaviors(behaviors);

        final Field field = userData.getClass().getDeclaredField("mBehaviors");
        field.setAccessible(true);
        assertEquals(behaviors, field.get(userData));
    }

    @Test
    public void userData_getter_behaviors_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {

        UserData userData = new UserData();

        ArrayList<Behavior> behaviors = new ArrayList<>();
        Behavior behaviorOne = new Behavior();
        behaviorOne.setId(1);
        behaviors.add(behaviorOne);

        final Field field = userData.getClass().getDeclaredField("mBehaviors");
        field.setAccessible(true);
        field.set(userData, behaviors);

        userData.setBehaviors(behaviors);
        assertEquals(behaviors, userData.getBehaviors());
    }

    @Test
    public void userData_remove_behavior_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {

        UserData userData = new UserData();

        ArrayList<Behavior> behaviors = new ArrayList<>();
        Behavior behaviorOne = new Behavior();
        behaviorOne.setId(1);
        Behavior behaviorTwo = new Behavior();
        behaviorTwo.setId(2);
        behaviors.add(behaviorTwo);

        userData.setBehaviors(behaviors);

        userData.removeBehavior(behaviorOne);

        assertEquals(1, userData.getBehaviors().size());

        assertEquals(behaviorTwo, userData.getBehaviors().get(0));

        //check remove same object second time

        userData.removeBehavior(behaviorOne);

        assertEquals(1, userData.getBehaviors().size());

        assertEquals(behaviorTwo, userData.getBehaviors().get(0));

    }

    @Test
    public void userData_add_behavior_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {

        UserData userData = new UserData();

        ArrayList<Behavior> behaviors = new ArrayList<>();
        Behavior behaviorOne = new Behavior();
        behaviorOne.setId(1);

        userData.addBehavior(behaviorOne);

        assertEquals(1, userData.getBehaviors().size());

        assertEquals(behaviorOne, userData.getBehaviors().get(0));

        //check add same object second time

        userData.addBehavior(behaviorOne);

        assertEquals(1, userData.getBehaviors().size());

        assertEquals(behaviorOne, userData.getBehaviors().get(0));

    }

    @Test
    public void userData_setter_goals_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {

        UserData userData = new UserData();

        ArrayList<Goal> goals = new ArrayList<>();
        Goal goalOne = new Goal();
        goalOne.setId(1);
        goals.add(goalOne);

        userData.setGoals(goals);

        final Field field = userData.getClass().getDeclaredField("mGoals");
        field.setAccessible(true);
        assertEquals(goals, field.get(userData));
    }

    @Test
    public void userData_getter_goals_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {

        UserData userData = new UserData();

        ArrayList<Goal> goals = new ArrayList<>();
        Goal goalOne = new Goal();
        goalOne.setId(1);
        goals.add(goalOne);

        final Field field = userData.getClass().getDeclaredField("mGoals");
        field.setAccessible(true);
        field.set(userData, goals);

        userData.setGoals(goals);
        assertEquals(goals, userData.getGoals());
    }

    @Test
    public void userData_remove_goal_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {

        UserData userData = new UserData();

        ArrayList<Goal> goals = new ArrayList<>();
        Goal goalOne = new Goal();
        goalOne.setId(1);
        Goal goalTwo = new Goal();
        goalTwo.setId(2);
        goals.add(goalTwo);

        userData.setGoals(goals);

        userData.removeGoal(goalOne);

        assertEquals(1, userData.getGoals().size());

        assertEquals(goalTwo, userData.getGoals().get(0));

        //check remove same object second time

        userData.removeGoal(goalOne);

        assertEquals(1, userData.getGoals().size());

        assertEquals(goalTwo, userData.getGoals().get(0));

    }

    @Test
    public void userData_add_goal_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {

        UserData userData = new UserData();

        ArrayList<Goal> goals = new ArrayList<>();
        Goal goalOne = new Goal();
        goalOne.setId(1);

        userData.addGoal(goalOne);

        assertEquals(1, userData.getGoals().size());

        assertEquals(goalOne, userData.getGoals().get(0));

        //check add same object second time

        userData.addGoal(goalOne);

        assertEquals(1, userData.getGoals().size());

        assertEquals(goalOne, userData.getGoals().get(0));

    }

    @Test
    public void userData_setter_categories_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {

        UserData userData = new UserData();

        ArrayList<Category> category = new ArrayList<>();
        Category categoryOne = new Category();
        categoryOne.setId(1);
        category.add(categoryOne);

        userData.setCategories(category);

        final Field field = userData.getClass().getDeclaredField("mCategories");
        field.setAccessible(true);
        assertEquals(category, field.get(userData));
    }

    @Test
    public void userData_getter_categories_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {

        UserData userData = new UserData();

        ArrayList<Category> category = new ArrayList<>();
        Category categoryOne = new Category();
        categoryOne.setId(1);
        category.add(categoryOne);

        final Field field = userData.getClass().getDeclaredField("mCategories");
        field.setAccessible(true);
        field.set(userData, category);

        userData.setCategories(category);
        assertEquals(category, userData.getCategories());
    }

    @Test
    public void userData_remove_category_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {

        UserData userData = new UserData();

        ArrayList<Category> category = new ArrayList<>();
        Category categoryOne = new Category();
        categoryOne.setId(1);
        Category categoryTwo = new Category();
        categoryTwo.setId(2);
        category.add(categoryTwo);

        userData.setCategories(category);

        userData.removeCategory(categoryOne);

        assertEquals(1, userData.getCategories().size());

        assertEquals(categoryTwo, userData.getCategories().get(0));

        //check remove same object second time

        userData.removeCategory(categoryOne);

        assertEquals(1, userData.getCategories().size());

        assertEquals(categoryTwo, userData.getCategories().get(0));

    }

    @Test
    public void userData_add_category_ReturnsTrue() throws NoSuchFieldException, IllegalAccessException {

        UserData userData = new UserData();

        ArrayList<Category> category = new ArrayList<>();
        Category categoryOne = new Category();
        categoryOne.setId(1);

        userData.addCategory(categoryOne);

        assertEquals(1, userData.getCategories().size());

        assertEquals(categoryOne, userData.getCategories().get(0));

        //check add same object second time

        userData.addCategory(categoryOne);

        assertEquals(1, userData.getCategories().size());

        assertEquals(categoryOne, userData.getCategories().get(0));

    }

}
