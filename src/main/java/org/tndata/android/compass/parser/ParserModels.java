package org.tndata.android.compass.parser;

import org.tndata.android.compass.model.ActionContent;
import org.tndata.android.compass.model.BehaviorContent;
import org.tndata.android.compass.model.CategoryContent;
import org.tndata.android.compass.model.CustomAction;
import org.tndata.android.compass.model.FeedData;
import org.tndata.android.compass.model.GoalContent;
import org.tndata.android.compass.model.Place;
import org.tndata.android.compass.model.Reward;
import org.tndata.android.compass.model.SearchResult;
import org.tndata.android.compass.model.UserAction;
import org.tndata.android.compass.model.UserData;
import org.tndata.android.compass.model.UserGoal;
import org.tndata.android.compass.model.UserProfile;

import java.util.List;


/**
 * This class contains the specifications of the API responses that do not match the model
 * to let Gson take care of everything.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public final class ParserModels{
    public interface ResultSet{}

    private class ListResultSet implements ResultSet{
        public int count;
        public String previous;
        public String next;
    }

    public final class UserDataResultSet implements ResultSet{
        public List<UserData> results;
    }

    public final class FeedDataResultSet implements ResultSet{
        public List<FeedData> results;
    }

    public final class PlaceResultSet implements ResultSet{
        public List<Place> results;
    }

    public final class CategoryContentResultSet implements ResultSet{
        public List<CategoryContent> results;
    }

    public final class GoalContentResultSet extends ListResultSet{
        public List<GoalContent> results;
    }

    public final class BehaviorContentResultSet extends ListResultSet{
        public List<BehaviorContent> results;
    }

    public final class ActionContentResultSet implements ResultSet{
        public List<ActionContent> results;
    }

    public final class UserActionResultSet implements ResultSet{
        public List<UserAction> results;
    }

    public final class CustomActionResultSet implements ResultSet{
        public List<CustomAction> results;
    }

    public final class UserGoalResultSet implements ResultSet{
        public List<UserGoal> results;
    }

    public final class RewardResultSet implements ResultSet{
        public List<Reward> results;
    }

    public final class SearchResultSet implements ResultSet{
        public List<SearchResult> results;
    }

    public final class UserProfileResultSet implements ResultSet{
        public List<UserProfile> results;
    }

    private ParserModels(){

    }
}
