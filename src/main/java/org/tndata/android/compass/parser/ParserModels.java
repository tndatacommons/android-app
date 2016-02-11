package org.tndata.android.compass.parser;

import org.tndata.android.compass.model.ActionContent;
import org.tndata.android.compass.model.BehaviorContent;
import org.tndata.android.compass.model.CategoryContent;
import org.tndata.android.compass.model.FeedData;
import org.tndata.android.compass.model.GoalContent;
import org.tndata.android.compass.model.Place;
import org.tndata.android.compass.model.UserData;

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

    public final class GoalContentResultSet implements ResultSet{
        public List<GoalContent> results;
    }

    public final class BehaviorContentResultSet implements ResultSet{
        public List<BehaviorContent> results;
    }

    public final class ActionContentResultSet implements ResultSet{
        public List<ActionContent> results;
    }

    private ParserModels(){

    }
}
