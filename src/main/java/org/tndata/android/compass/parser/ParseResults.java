package org.tndata.android.compass.parser;

import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;

import java.util.List;


/**
 * Created by isma on 12/14/15.
 */
public final class ParseResults{
    List<Category> mCategories;
    List<Goal> mGoals;
    List<Behavior> mBehaviors;
    List<Action> mActions;

    /**
     * Modified default constructor. Makes the class instantiable only within the package.
     */
    ParseResults(){

    }
}
