package org.tndata.android.compass.model;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * This class encapsulates all of the user-selected content from the online REST api:
 *
 * - Categories
 * - Goals
 * - Behaviors
 * - Actions
 * - Custom Goals
 * - Custom Actions
 * - Places
 *
 * It includes methods to set & get those values, and keeps a consistently updated
 * hierarchy (i.e. Behaviors know about their parent goals & child actions, etc).
 *
 * This class also contains the data to be displayed at the main feed.
 *
 * @author Brad Montgomery
 * @author Ismael Alonso
 * @version 2.0.0
 */
public class UserData extends TDCBase{
    private static final String TAG = "UserData";

    public static final String TYPE = "user_data";


    //User selected content: TDCContent.id -> UserSelectedContent maps
    @SerializedName("user_categories")
    private Map<Long, UserCategory> mCategories;
    @SerializedName("user_goals")
    private Map<Long, UserGoal> mGoals;
    @SerializedName("user_behaviors")
    private Map<Long, UserBehavior> mBehaviors;
    @SerializedName("user_actions")
    private Map<Long, UserAction> mActions;
    private Set<Long> mRequestedGoals;

    //Custom content: Custom(*).id -> Custom(*) maps
    @SerializedName("customgoals")
    private Map<Long, CustomGoal> mCustomGoals;
    @SerializedName("customactions")
    private Map<Long, CustomAction> mCustomActions;

    //User places
    @SerializedName("places")
    private List<UserPlace> mPlaces = new ArrayList<>();

    //Data for the main feed
    @SerializedName("feed_data")
    private FeedData mFeedData;


    /**
     * Constructor.
     */
    public UserData(){
        mCategories = new HashMap<>();
        mGoals = new HashMap<>();
        mBehaviors = new HashMap<>();
        mActions = new HashMap<>();

        mCustomGoals = new HashMap<>();
        mCustomActions = new HashMap<>();
    }

    @Override
    protected String getType(){
        return TYPE;
    }

    @Override
    public long getId(){
        //Not actual content, but a container
        return -1;
    }


    /*--------------------------*
     * CATEGORY RELATED METHODS *
     *--------------------------*/

    /**
     * Returns the list of user-selected Categories.
     *
     * @return an ArrayList of Category objects
     */
    public Map<Long, UserCategory> getCategories(){
        return mCategories;
    }

    /**
     * Returns the original copy of the provided category.
     *
     * @param category the category whose original copy needs to be fetched.
     * @return the original copy of such category.
     */
    public UserCategory getCategory(CategoryContent category){
        return mCategories.get(category.getId());
    }

    public UserCategory getCategory(UserCategory userCategory){
        return getCategory(userCategory.getCategory());
    }

    public boolean contains(UserCategory userCategory){
        return mCategories.containsKey(userCategory.getCategory().getId());
    }

    /**
     * Returns all of the mGoals for a given Category. This is a wrapper for category.getGoalIdSet().
     *
     * @return a List of Goal objects.
     */
    public List<UserGoal> getCategoryGoals(CategoryContent category){
        return getCategory(category).getGoals();
    }

    /**
     * Adds a Category to the list of user-selected categories (if it's not
     * already included) and assigns any user-selected goals to the new Category.
     *
     * @param userCategory the Category object to add.
     */
    public void addCategory(UserCategory userCategory){
        //If the category ain't in the data set
        if (!contains(userCategory)){
            //Add it
            mCategories.put(userCategory.getContentId(), userCategory);

            //Link mGoals
            for (UserGoal userGoals: mGoals.values()){
                if (userGoals.getGoal().getCategoryIdSet().contains(userCategory.getContentId())){
                    userGoals.addCategory(userCategory);
                    userCategory.addGoal(userGoals);
                }
            }

        }
    }

    /**
     * TODO This method may be irrelevant?
     *
     * Remove a single Category from the user's collection. This also removes the
     * reference to that category from all Goals.
     *
     * @param category the category to remove
     */
    public void removeCategory(UserCategory category){
        /*UserCategory removedCategory = mCategories.remove(category.getId());

        if (removedCategory != null){
            List<UserGoal> goalsToRemove = new ArrayList<>();
            //Remove this category from any child mGoals
            for (UserGoal goal : removedCategory.getGoalIdSet()){
                goal.removeCategory(removedCategory);
                //Record all the Goals w/o parent Categories
                if (goal.getCategoryIdSet().isEmpty()){
                    goalsToRemove.add(goal);
                }
            }
            //Remove all the Goals w/o parent Categories
            for (UserGoal goal : goalsToRemove){
                removeGoal(goal.getGoal());
            }
        }*/
    }


    /*----------------------*
     * GOAL RELATED METHODS *
     *----------------------*/

    /**
     * Returns the list of user-selected Goals.
     *
     * @return a List of Goal objects.
     */
    public Map<Long, UserGoal> getGoals(){
        return mGoals;
    }

    /**
     * Returns the original copy of the provided goal.
     *
     * @param goal the goal whose original copy needs to be fetched.
     * @return the original copy of such goal.
     */
    public UserGoal getGoal(GoalContent goal){
        return mGoals.get(goal.getId());
    }

    /**
     * Method used to check whether the user goal is <b>currently</b> in the user's collection.
     *
     * @param userGoal the user goal to be checked.
     * @return true if the goal is in the user's collection, false otherwise.
     */
    public boolean contains(UserGoal userGoal){
        return mGoals.containsKey(userGoal.getGoal().getId());
    }

    /**
     * Method used to check wiether a particular goal is in the user's collection or in the
     * process of being added.
     *
     * @param goal the goal to be checked.
     * @return true if it is, false otherwise.
     */
    public boolean contains(GoalContent goal){
        return mGoals.containsKey(goal.getId()) || mRequestedGoals.contains(goal.getId());
    }

    /**
     * Adds a goal id to the list of goals to be added to the user's collection.
     *
     * @param id the id of the goal.
     */
    public void addGoal(long id){
        mRequestedGoals.add(id);
    }

    /**
     * Add a goal to the list of user selected Goals.
     *
     * Adding a Goal also assigns it to any existing user-selected categories,
     * as well as assigning any existing Behaviors to the Goal.
     *
     * @param userGoal the goal to be added to the user list.
     */
    private void addGoal(UserGoal userGoal){
        //If the goal ain't in the data set
        if (!contains(userGoal)){
            mRequestedGoals.remove(userGoal.getContentId());

            //Initialize the object and add it
            userGoal.init();
            mGoals.put(userGoal.getContentId(), userGoal);

            //Link the goal with the relevant categories
            for (Long categoryId:userGoal.getGoal().getCategoryIdSet()){
                if (mCategories.containsKey(categoryId)){
                    userGoal.addCategory(mCategories.get(categoryId));
                    mCategories.get(categoryId).addGoal(userGoal);
                }
            }

            //Link the goal with the relevant behaviors
            for (UserBehavior userBehavior: mBehaviors.values()){
                if (userBehavior.getBehavior().getGoalIdSet().contains(userGoal.getContentId())){
                    userGoal.addBehavior(userBehavior);
                    userBehavior.addGoal(userGoal);
                }
            }
        }
    }

    /**
     * Removes a goal from the user's collection.
     *
     * Removing a goal also removes its reference from the parent Categories
     * as well as the child Behaviors.
     *
     * @param goal the goal to be removed from the user list.
     */
    private void removeGoal(UserGoal goal){
        UserGoal removedGoal = mGoals.remove(goal.getContentId());

        if (removedGoal != null){
            //Remove the goal from its parent mCategories
            for (UserCategory category:removedGoal.getCategories()){
                category.removeGoal(removedGoal);
            }

            //Remove the goal from its child Behaviors
            for (UserBehavior behavior:removedGoal.getBehaviors()){
                behavior.removeGoal(removedGoal);
                //Record all the Behaviors w/o parent Goals
                if (behavior.getGoals().isEmpty()){
                    removeBehavior(behavior);
                }
            }
        }
    }


    /*--------------------------*
     * BEHAVIOR RELATED METHODS *
     *--------------------------*/

    /**
     * Return the user-selected Behaviors.
     *
     * @return an ArrayList of Behavior objects.
     */
    public Map<Long, UserBehavior> getBehaviors(){
        return mBehaviors;
    }

    /**
     * Returns the original copy of the provided behavior.
     *
     * @param behavior the behavior whose original copy needs to be fetched.
     * @return the original copy of such behavior.
     */
    public UserBehavior getBehavior(BehaviorContent behavior){
        return mBehaviors.get(behavior.getId());
    }

    public UserBehavior getBehavior(UserBehavior userBehavior){
        return getBehavior(userBehavior.getBehavior());
    }

    public boolean contains(UserBehavior userBehavior){
        return mBehaviors.containsKey(userBehavior.getBehavior().getId());
    }

    /**
     * Add a single Behavior to the user's collection.
     *
     * @param userBehavior the behavior to be added to the user's list.
     */
    public void addBehavior(UserBehavior userBehavior){
        if (!contains(userBehavior)){
            userBehavior.init();
            mBehaviors.put(userBehavior.getContentId(), userBehavior);

            for (Long goalId:userBehavior.getBehavior().getGoalIdSet()){
                if (mGoals.containsKey(goalId)){
                    userBehavior.addGoal(mGoals.get(goalId));
                    mGoals.get(goalId).addBehavior(userBehavior);
                }
            }

            for (UserAction userAction: mActions.values()){
                if (userAction.getAction().getBehaviorId() == userBehavior.getContentId()){
                    userAction.setBehavior(userBehavior);
                    userBehavior.addAction(userAction);
                }
            }
        }
    }

    /**
     * Remove a single Behavior from a user's collection. Doing this will also remove any
     * references to the Behavior from the parent Goal, and it will also remove the
     * child Actions.
     *
     * @param behavior the Behavior instance to remove.
     */
    public void removeBehavior(UserBehavior behavior){
        UserBehavior removedBehavior = mBehaviors.remove(behavior.getContentId());

        if (removedBehavior != null){
            //Remove the behavior from any parent Goals
            for (UserGoal goal:removedBehavior.getGoals()){
                goal.removeBehavior(removedBehavior);
            }

            //Remove the child Actions
            for (UserAction action:removedBehavior.getActions()){
                mActions.remove(action.getAction().getId());
            }
        }
    }


    /*------------------------*
     * ACTION RELATED METHODS *
     *------------------------*/

    /**
     * Retrieve a list of all user-selected Actions.
     *
     * @return an ArrayList of Action objects.
     */
    public Map<Long, UserAction> getActions(){
        return mActions;
    }

    /**
     * Returns the original copy of the provided action.
     *
     * @param action the action whose original copy needs to be fetched.
     * @return the original copy of such action.
     */
    public UserAction getAction(ActionContent action){
        return mActions.get(action.getId());
    }

    public boolean contains(UserAction userAction){
        return mActions.containsKey(userAction.getAction().getId());
    }

    /**
     *  Add an individual action the user's collection. Adding an action will also
     *  update the user's selected Behaviors, including a reference to the new
     *  action within any relevant parent Behavior.
     *
     * @param userAction the action to be added to the list.
     */
    private void addAction(UserAction userAction){
        if (!contains(userAction)){
            mActions.put(userAction.getContentId(), userAction);

            UserBehavior behavior = mBehaviors.get(userAction.getAction().getBehaviorId());
            if (behavior != null){
                behavior.addAction(userAction);
                userAction.setBehavior(behavior);
            }
        }
    }

    /**
     * Remove a single Action from a user's collection. Doing this will also remove
     * any reference to that Action from it's parent Behaviors.
     *
     * @param action the Action object to remove.
     */
    private void removeAction(UserAction action){
        UserAction removedAction = mActions.remove(action.getContentId());
        if (removedAction != null && removedAction.getBehavior() != null){
            removedAction.getBehavior().removeAction(removedAction);
        }
    }


    /*-----------------------------*
     * CUSTOM GOAL RELATED METHODS *
     *-----------------------------*/

    public Map<Long, CustomGoal> getCustomGoals(){
        return mCustomGoals;
    }

    private void addGoal(CustomGoal customGoal){
        if (!mCustomGoals.containsKey(customGoal.getContentId())){
            mCustomGoals.put(customGoal.getContentId(), customGoal);
        }
    }

    private void removeGoal(CustomGoal customGoal){
        mCustomGoals.remove(customGoal.getContentId());

        for (CustomAction customAction:customGoal.getActions()){
            if (mCustomActions.containsKey(customAction.getContentId())){
                removeAction(customAction);
            }
        }
    }


    /*-------------------------------*
     * CUSTOM ACTION RELATED METHODS *
     *-------------------------------*/

    public Map<Long, CustomAction> getCustomActions(){
        return mCustomActions;
    }

    private void addAction(CustomAction customAction){
        mCustomActions.put(customAction.getContentId(), customAction);
        customAction.setGoal(mCustomGoals.get(customAction.getCustomGoalId()));
        customAction.getGoal().addAction(customAction);
    }

    private void removeAction(CustomAction customAction){
        mCustomActions.remove(customAction.getContentId());
        mCustomGoals.get(customAction.getCustomGoalId()).removeAction(customAction);
    }


    /*----------------------*
     * GOAL GENERIC METHODS *
     *----------------------*/

    public Goal getGoal(Goal goal){
        if (goal instanceof UserGoal){
            return mGoals.get(goal.getContentId());
        }
        else{
            return mCustomGoals.get(goal.getContentId());
        }
    }

    public void addGoal(Goal goal){
        if (goal instanceof UserGoal){
            addGoal((UserGoal)goal);
        }
        else if (goal instanceof CustomGoal){
            addGoal((CustomGoal)goal);
        }
    }

    public void removeGoal(Goal goal){
        if (goal instanceof UserGoal){
            removeGoal((UserGoal)goal);
        }
        else if (goal instanceof CustomGoal){
            removeGoal((CustomGoal)goal);
        }
    }


    /*------------------------*
     * ACTION GENERIC METHODS *
     *------------------------*/

    public Action getAction(Action action){
        if (action instanceof UserAction){
            return mActions.get(action.getContentId());
        }
        else{
            return mCustomActions.get(action.getContentId());
        }
    }

    public void addAction(Action action){
        if (action instanceof UserAction){
            addAction((UserAction)action);
        }
        else if (action instanceof CustomAction){
            addAction((CustomAction)action);
        }
    }

    public void removeAction(Action action){
        if (action instanceof UserAction){
            removeAction((UserAction)action);
        }
        else if (action instanceof CustomAction){
            removeAction((CustomAction)action);
        }
    }


    /*-------------------------*
     * SYNCHRONIZATION METHODS *
     *-------------------------*/

    /**
     * Sync up all parent/child objects. This method should only be used at startup,
     * from that point on, the lists are supposed to be kept up to date.
     */
    public void sync(){
        mRequestedGoals = new HashSet<>();
        linkGoalsAndCategories();
        linkBehaviorsAndGoals();
        linkActions();
        linkCustomContent();
        mFeedData.sync(this);
    }

    /**
     * Generates the inner lists of parents and children for goals and categories, respectively.
     */
    private void linkGoalsAndCategories(){
        //Add each goal to the correct category and vice versa
        for (UserGoal userGoal:mGoals.values()){
            for (Long categoryId:userGoal.getGoal().getCategoryIdSet()){
                if (mCategories.containsKey(categoryId)){
                    UserCategory userCategory = mCategories.get(categoryId);
                    userCategory.addGoal(userGoal);
                    userGoal.addCategory(userCategory);
                }
            }
            userGoal.setPrimaryCategory(mCategories.get(userGoal.getPrimaryCategoryId()));
        }
    }

    /**
     * Generates the inner lists of parents and children for behaviors and goals, respectively.
     */
    private void linkBehaviorsAndGoals(){
        //Look at all the selected mGoals
        for (UserBehavior userBehavior:mBehaviors.values()){
            for (Long goalId:userBehavior.getBehavior().getGoalIdSet()){
                if (mGoals.containsKey(goalId)){
                    UserGoal userGoal = mGoals.get(goalId);
                    userGoal.addBehavior(userBehavior);
                    userBehavior.addGoal(userGoal);
                }
            }
        }
    }

    /**
     * Generates the inner list of children for mBehaviors and sets the parents for actions.
     */
    public void linkActions(){
        for (UserAction userAction:mActions.values()){
            UserBehavior userBehavior = mBehaviors.get(userAction.getAction().getBehaviorId());
            userBehavior.addAction(userAction);
            userAction.setBehavior(userBehavior);
            userAction.setPrimaryGoal(mGoals.get(userAction.getPrimaryGoalId()));
            userAction.setPrimaryCategory(mCategories.get(userAction.getPrimaryCategoryId()));
        }
    }

    /**
     * Fills the CustomAction list of CustomGoals and sets the parent CustomGoals
     * for CustomActions.
     */
    public void linkCustomContent(){
        for (CustomAction customAction:mCustomActions.values()){
            customAction.setGoal(mCustomGoals.get(customAction.getCustomGoalId()));
            customAction.getGoal().addAction(customAction);
        }
    }


    /*-----------------------*
     * PLACE RELATED METHODS *
     *-----------------------*/

    /**
     * Sets the list of user set places.
     *
     * @param places the list of mPlaces set by the user.
     */
    public void setPlaces(List<UserPlace> places){
        this.mPlaces = places;
    }

    /**
     * Gets the list of user set places.
     *
     * @return the list of mPlaces set by te user.
     */
    public List<UserPlace> getPlaces(){
        return mPlaces;
    }

    /**
     * Adds a place to the list of places set bu the user.
     *
     * @param place the place to be added.
     */
    public void addPlace(UserPlace place){
        mPlaces.add(place);
    }


    /*---------------------------*
     * FEED DATA RELATED METHODS *
     *---------------------------*/

    /**
     * Sets the feed data retrieved from the API.
     *
     * @param feedData the object containing the information to be displayed in the feed.
     */
    public void setFeedData(FeedData feedData){
        mFeedData = feedData;
    }

    /**
     * Gets the feed data.
     *
     * @return the object containing the information to be displayed in the feed.
     */
    public FeedData getFeedData(){
        return mFeedData;
    }


    /*------------------------------------*
     * LOG RELATED METHODS. FOR DEBUGGING *
     *------------------------------------*/

    /**
     * Log the value of individual private data members.
     */
    public void logData() {
        Log.d(TAG, "Categories.");
        for (UserCategory item:mCategories.values()){
            Log.d(TAG, "- " + item.toString());
            Log.d(TAG, "--> contains " + item.getGoals().size() + " goals");
        }
        Log.d(TAG, "Goals.");
        for (UserGoal item:mGoals.values()){
            Log.d(TAG, "- " + item.toString());
            Log.d(TAG, "--> contains " + item.getCategories().size() + " categories");
            Log.d(TAG, "--> contains " + item.getBehaviors().size() + " behaviors");
        }
        Log.d(TAG, "Behaviors.");
        for (UserBehavior item:mBehaviors.values()){
            Log.d(TAG, "- " + item.toString());
            Log.d(TAG, "--> contains " + item.getGoals().size() + " goals");
            Log.d(TAG, "--> contains " + item.getActions().size() + " actions");
        }
        Log.d(TAG, "Actions.");
        for (UserAction item:mActions.values()){
            Log.d(TAG, item.getAction().getBehaviorId()+"");
            Log.d(TAG, mBehaviors.get(item.getAction().getBehaviorId()).getBehavior().toString());
            Log.d(TAG, "- " + item.toString());
            Log.d(TAG, "--> contains: " + item.getBehavior().toString());
        }
    }

    /**
     * Given a goal, Log information for it's parent Categories.
     *
     * @param userGoal the goal whose information needs to be logged.
     */
    public void logParentCategories(UserGoal userGoal){
        String output = "NONE";
        if (!userGoal.getCategories().isEmpty()){
            output = "";
            for (UserCategory userCategory:userGoal.getCategories()){
                output += "(" + userCategory.getContentId() + ") " + userCategory.getTitle() + ", ";
            }
        }
        Log.d(TAG, "- (parents) -> " + output);
    }

    /**
     * Given a behavior, log information for it's parent Goals.
     *
     * @param userBehavior the behavior whose information needs to be logged.
     */
    public void logParentGoals(UserBehavior userBehavior){
        String output = "NONE";
        if (!userBehavior.getGoals().isEmpty()){
            output = "";
            for (UserGoal userGoal:userBehavior.getGoals()){
                output += "(" + userGoal.getContentId() + ") " + userGoal.getTitle() + ", ";
            }
        }
        Log.d(TAG, "-- (parents) ->" + output);
    }

    /**
     * Given an Action, log information for it's parent Behavior
     *
     * @param userAction the action whose information needs to be logged.
     */
    public void logParentBehavior(UserAction userAction){
        String output = "NONE";
        UserBehavior userBehavior = userAction.getBehavior();
        if (userBehavior != null){
            output = "(" + userBehavior.getContentId() + ") " + userBehavior.getTitle();
        }
        Log.d(TAG, "--- (parent)-> " + output);
    }

    /**
     * Log the hierarchy of all User-selected data.
     */
    public void logSelectedData(){
        logSelectedData("User Data");
    }

    /**
     * Log the hierarchy of all User-selected data, with a custom title.
     *
     * @param title a custom title to display above the output.
     *
     */
    public void logSelectedData(String title){
        logSelectedData(title, true);
    }

    /**
     * Log the hierarchy of all User-selected data, with a custom title, and an option to
     * include each level's parent data.
     *
     * @param title a custom title to display above the output.
     * @param includeParents if true, will Log each item's parent objects.
     */
    public void logSelectedData(String title, boolean includeParents){
        // Log user-selected mCategories, mGoals, mBehaviors, mActions
        Log.d(TAG, "------------- " + title + " --------------- ");
        for (UserCategory userCategory:getCategories().values()){
            Log.d(TAG, "CATEGORY: " + userCategory.getTitle());
            for (UserGoal userGoal:userCategory.getGoals()){
                Log.d(TAG, "- GOAL: " + userGoal.getTitle());
                if (includeParents){
                    logParentCategories(userGoal);
                }
                for (UserBehavior userBehavior:userGoal.getBehaviors()){
                    Log.d(TAG, "-- BEHAVIOR: " + userBehavior.getTitle());
                    if (includeParents){
                        logParentGoals(userBehavior);
                    }
                    for (UserAction userAction:userBehavior.getActions()){
                        Log.d(TAG, "--- ACTION: " + userAction.getTitle());
                        if (includeParents){
                            logParentBehavior(userAction);
                        }
                    }
                }
            }
        }
        Log.d(TAG, "------------------------------------------- ");
    }
}
