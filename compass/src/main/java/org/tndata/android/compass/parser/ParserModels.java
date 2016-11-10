package org.tndata.android.compass.parser;

import org.tndata.android.compass.activity.AwardsActivity;
import org.tndata.compass.model.CustomAction;
import org.tndata.compass.model.CustomGoal;
import org.tndata.compass.model.DailyProgress;
import org.tndata.compass.model.FeedData;
import org.tndata.compass.model.Organization;
import org.tndata.compass.model.Place;
import org.tndata.compass.model.Reward;
import org.tndata.compass.model.SearchResult;
import org.tndata.compass.model.TDCCategory;
import org.tndata.compass.model.TDCGoal;
import org.tndata.compass.model.User;
import org.tndata.compass.model.UserAction;
import org.tndata.compass.model.UserCategory;
import org.tndata.compass.model.UserGoal;
import org.tndata.compass.model.UserPlace;
import org.tndata.compass.model.ResultSet;

import java.util.List;


/**
 * This class contains the specifications of the API responses that do not match the model
 * to let Gson take care of everything.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public final class ParserModels{
    private abstract class ListResultSet implements ResultSet{
        public int count;
        public String previous;
        public String next;
    }

    public final class UserResultSet implements ResultSet{
        public List<User> results;
    }

    public final class FeedDataResultSet implements ResultSet{
        public List<FeedData> results;
    }

    public final class CategoryContentResultSet implements ResultSet{
        public List<TDCCategory> results;
    }

    public final class CustomGoalsResultSet extends ListResultSet{
        public List<CustomGoal> results;
    }

    public final class UserGoalsResultSet extends ListResultSet{
        public List<UserGoal> results;
    }

    public final class DailyProgressResultSet implements ResultSet{
        public List<DailyProgress> results;
    }

    public final class GoalContentResultSet extends ListResultSet{
        public List<TDCGoal> results;
    }

    public final class UserActionResultSet extends ListResultSet{
        public List<UserAction> results;
    }

    public final class CustomActionResultSet extends ListResultSet{
        public List<CustomAction> results;
    }

    public final class UserCategoryResultSet implements ResultSet{
        public List<UserCategory> results;
    }

    public final class RewardResultSet implements ResultSet{
        public List<Reward> results;
    }

    public final class SearchResultSet implements ResultSet{
        public List<SearchResult> results;
    }

    public final class UserPlacesResultSet implements ResultSet{
        public List<UserPlace> results;
    }

    public final class PlacesResultSet implements ResultSet{
        public List<Place> results;
    }

    public final class AwardsResultSet implements ResultSet{
        public List<AwardsActivity.Award> results;
    }

    public final class OrganizationsResultSet implements ResultSet{
        public List<Organization> results;
    }

    private ParserModels(){

    }
}
