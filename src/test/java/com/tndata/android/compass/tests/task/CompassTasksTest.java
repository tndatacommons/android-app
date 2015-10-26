package com.tndata.android.compass.tests.task;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.tndata.android.compass.BuildConfig;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.Survey;
import org.tndata.android.compass.model.User;
import org.tndata.android.compass.model.UserData;
import org.tndata.android.compass.task.ActionLoaderTask;
import org.tndata.android.compass.task.BehaviorLoaderTask;
import org.tndata.android.compass.task.CategoryLoaderTask;
import org.tndata.android.compass.task.GetUserActionsTask;
import org.tndata.android.compass.task.GetUserBehaviorsTask;
import org.tndata.android.compass.task.GetUserCategoriesTask;
import org.tndata.android.compass.task.GetUserDataTask;
import org.tndata.android.compass.task.GetUserGoalsTask;
import org.tndata.android.compass.task.GetUserProfileTask;
import org.tndata.android.compass.task.LogInTask;
import org.tndata.android.compass.task.SignUpTask;
import org.tndata.android.compass.task.UpdateProfileTask;

import java.util.ArrayList;
import java.util.UUID;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 19)
public class CompassTasksTest {

    private User userRestult = null;
    private static String userEmail = null;

    @Before
    public void setup() {
        userEmail = UUID.randomUUID().toString() + "@tndata.org";
    }

    private void singUpUserTask() {
        SignUpTask.SignUpTaskCallback signUpTaskListener = new SignUpTask.SignUpTaskCallback() {
            @Override
            public void signUpResult(User result) {
                userRestult = result;
            }
        };
        SignUpTask signUpTask = new SignUpTask(signUpTaskListener);

        User user = new User();
        user.setEmail(userEmail);
        user.setPassword("password");
        user.setFirstName("first_name");
        user.setLastName("last_name");
        user.setOnBoardingComplete(false);

        signUpTask.execute(user);

        Robolectric.flushBackgroundThreadScheduler();

        assertNotNull(userRestult);
    }

    @Test
    public void signUpTaskTest_returnsTrue() throws InterruptedException {

        singUpUserTask();

    }

    @Test
    public void loginTaskTest_returnsTrue() throws InterruptedException {

        SignUpTask.SignUpTaskCallback signUpTaskListener = new SignUpTask.SignUpTaskCallback() {
            @Override
            public void signUpResult(User result) {
                userRestult = result;
            }
        };
        SignUpTask signUpTask = new SignUpTask(signUpTaskListener);

        User user = new User();
        user.setEmail(userEmail);
        user.setPassword("password");
        user.setFirstName("first_name");
        user.setLastName("last_name");
        user.setOnBoardingComplete(false);

        signUpTask.execute(user);

        Robolectric.flushBackgroundThreadScheduler();

        assertNotNull(userRestult);


        LogInTask.LogInTaskCallback loginTaskListener = new LogInTask.LogInTaskCallback() {
            @Override
            public void loginResult(User result) {
                userRestult = result;
            }
        };
        LogInTask loginTask = new LogInTask(loginTaskListener);

        user.setEmail(userEmail);
        user.setPassword("password");
        user.setOnBoardingComplete(false);

        loginTask.execute(user);

        Robolectric.flushBackgroundThreadScheduler();

        assertNotNull(userRestult);

    }

    boolean resultTask = false;

    @Test
    public void updateProfileTaskTest_returnsTrue() throws InterruptedException {

        singUpUserTask();


        UpdateProfileTask.OnProfileUpdateCallback loginTaskListener = new UpdateProfileTask.OnProfileUpdateCallback() {
            @Override
            public void onProfileUpdated(boolean result) {
                resultTask = result;
            }
        };

        UpdateProfileTask updateProfileTask = new UpdateProfileTask(loginTaskListener);

        userRestult.setLastName("new_last_name");

        updateProfileTask.execute(userRestult);

        Robolectric.flushBackgroundThreadScheduler();

        assertTrue(resultTask);

    }


    private ArrayList<Survey> surveryList = null;

    @Test
    public void getUserProfileTaskTest_returnsTrue() throws InterruptedException {

        singUpUserTask();


        GetUserProfileTask.UserProfileTaskInterface loginTaskListener = new GetUserProfileTask.UserProfileTaskInterface() {
            @Override
            public void userProfileFound(ArrayList<Survey> result) {
                surveryList = result;
            }
        };

        GetUserProfileTask updateProfileTask = new GetUserProfileTask(loginTaskListener);

        updateProfileTask.execute(userRestult.getToken());

        Robolectric.flushBackgroundThreadScheduler();

        assertNotNull(surveryList);

    }

    private ArrayList<Goal> goalsList = null;

    @Test
    public void getUserGoalsTaskTest_returnsTrue() throws InterruptedException {

        singUpUserTask();


        GetUserGoalsTask.GetUserGoalsListener getUserGoalsListener = new GetUserGoalsTask.GetUserGoalsListener() {
            @Override
            public void goalsLoaded(ArrayList<Goal> goals) {
                goalsList = goals;
            }
        };

        GetUserGoalsTask getUserGoalsTask = new GetUserGoalsTask(getUserGoalsListener);

        getUserGoalsTask.execute(userRestult.getToken());

        Robolectric.flushBackgroundThreadScheduler();

        assertNotNull(goalsList);

    }

    private UserData userData = null;

    @Test
    public void getUserDataTaskTest_returnsTrue() throws InterruptedException {

        singUpUserTask();


        GetUserDataTask.GetUserDataCallback getUserDataListener = new GetUserDataTask.GetUserDataCallback() {
            @Override
            public void userDataLoaded(UserData data) {
                userData = data;
            }
        };

        GetUserDataTask getUserDataTask = new GetUserDataTask(getUserDataListener);
        getUserDataTask.execute(userRestult.getToken());

        Robolectric.flushBackgroundThreadScheduler();

        assertNotNull(userData);

    }

    private ArrayList<Category> categoriesList = null;

    @Test
    public void getUserCategoriesTaskTest_returnsTrue() throws InterruptedException {

        singUpUserTask();


        GetUserCategoriesTask.GetUserCategoriesListener getUserCategoriesListener = new GetUserCategoriesTask.GetUserCategoriesListener() {
            @Override
            public void categoriesLoaded(ArrayList<Category> categories) {
                categoriesList = categories;
            }
        };

        GetUserCategoriesTask getUserCategoriesTask = new GetUserCategoriesTask(getUserCategoriesListener);
        getUserCategoriesTask.execute(userRestult.getToken());

        Robolectric.flushBackgroundThreadScheduler();

        assertNotNull(categoriesList);

    }

    @Test
    public void getCategoryLoaderTaskTest_returnsTrue() throws InterruptedException {

        categoriesList = null;

        singUpUserTask();


        CategoryLoaderTask.CategoryLoaderListener categoryLoaderListener = new CategoryLoaderTask.CategoryLoaderListener() {
            @Override
            public void categoryLoaderFinished(ArrayList<Category> categories) {
                categoriesList = categories;
            }
        };

        CategoryLoaderTask categoryLoaderTask = new CategoryLoaderTask(categoryLoaderListener);
        categoryLoaderTask.execute(userRestult.getToken());

        Robolectric.flushBackgroundThreadScheduler();

        assertNotNull(categoriesList);

    }

    private ArrayList<Behavior> behaviorsList = null;

    @Test
    public void getUserBehaviorsTaskTest_returnsTrue() throws InterruptedException {

        singUpUserTask();


        GetUserBehaviorsTask.GetUserBehaviorsListener getUserBehaviorsListener = new GetUserBehaviorsTask.GetUserBehaviorsListener() {
            @Override
            public void behaviorsLoaded(ArrayList<Behavior> behaviors) {
                behaviorsList = behaviors;
            }
        };

        GetUserBehaviorsTask getUserBehaviorsTask = new GetUserBehaviorsTask(getUserBehaviorsListener);
        getUserBehaviorsTask.execute(userRestult.getToken());

        Robolectric.flushBackgroundThreadScheduler();

        assertNotNull(behaviorsList);

    }

    @Test
    public void getBehaviorLoaderTaskTest_returnsTrue() throws InterruptedException {

        behaviorsList = null;

        singUpUserTask();


        BehaviorLoaderTask.BehaviorLoaderListener behaviorLoaderListener = new BehaviorLoaderTask.BehaviorLoaderListener() {
            @Override
            public void behaviorsLoaded(ArrayList<Behavior> behaviors) {
                behaviorsList = behaviors;
            }
        };

        BehaviorLoaderTask behaviorLoaderTask = new BehaviorLoaderTask(behaviorLoaderListener);
        behaviorLoaderTask.execute(userRestult.getToken());

        Robolectric.flushBackgroundThreadScheduler();

        assertNotNull(behaviorsList);

    }


    private ArrayList<Action> actionsList = null;

    @Test
    public void getUserActionsTaskTest_returnsTrue() throws InterruptedException {

        singUpUserTask();


        GetUserActionsTask.GetUserActionsCallback getUserActionsListener = new GetUserActionsTask.GetUserActionsCallback() {
            @Override
            public void actionsLoaded(ArrayList<Action> actions) {
                actionsList = actions;
            }
        };

        GetUserActionsTask getUserActionsTask = new GetUserActionsTask(getUserActionsListener);
        getUserActionsTask.execute(userRestult.getToken());

        Robolectric.flushBackgroundThreadScheduler();

        assertNotNull(actionsList);

    }

    @Test
    public void getActionLoaderTaskTest_returnsTrue() throws InterruptedException {

        actionsList = null;

        singUpUserTask();


        ActionLoaderTask.ActionLoaderListener actionLoaderListener = new ActionLoaderTask.ActionLoaderListener() {
            @Override
            public void actionsLoaded(ArrayList<Action> actions) {
                actionsList = actions;
            }
        };

        ActionLoaderTask actionLoaderTask = new ActionLoaderTask(actionLoaderListener);
        actionLoaderTask.execute(userRestult.getToken());

        Robolectric.flushBackgroundThreadScheduler();

        assertNotNull(actionsList);

    }

}
