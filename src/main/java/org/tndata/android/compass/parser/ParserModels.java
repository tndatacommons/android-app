package org.tndata.android.compass.parser;

import org.tndata.android.compass.activity.AwardsActivity;
import org.tndata.android.compass.model.TDCBehavior;
import org.tndata.android.compass.model.TDCCategory;
import org.tndata.android.compass.model.CustomAction;
import org.tndata.android.compass.model.CustomGoal;
import org.tndata.android.compass.model.DailyProgress;
import org.tndata.android.compass.model.FeedData;
import org.tndata.android.compass.model.TDCGoal;
import org.tndata.android.compass.model.Place;
import org.tndata.android.compass.model.Reward;
import org.tndata.android.compass.model.SearchResult;
import org.tndata.android.compass.model.UserAction;
import org.tndata.android.compass.model.UserCategory;
import org.tndata.android.compass.model.UserGoal;
import org.tndata.android.compass.model.UserPlace;

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

    private abstract class ListResultSet implements ResultSet{
        public int count;
        public String previous;
        public String next;
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

    public final class BehaviorContentResultSet extends ListResultSet{
        public List<TDCBehavior> results;
    }

    public final class UserActionResultSet extends ListResultSet{
        public List<UserAction> results;
    }

    public final class CustomActionResultSet implements ResultSet{
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

    private ParserModels(){

    }
}
