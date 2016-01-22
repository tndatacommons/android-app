package org.tndata.android.compass.parser;

import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.FeedData;
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

    public final class CategoriesResultSet implements ResultSet{
        public List<Category> results;
    }
}
