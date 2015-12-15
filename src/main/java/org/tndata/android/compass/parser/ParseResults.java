package org.tndata.android.compass.parser;

import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;

import java.util.List;
import java.util.Map;


/**
 * Created by isma on 12/14/15.
 */
public final class ParseResults{
    Map<Integer, Category> mCategories;
    Map<Integer, Goal> mGoals;
    Map<Integer, Behavior> mBehaviors;
    Map<Integer, Action> mActions;

    /**
     * Modified default constructor. Makes the class instantiable only within the package.
     */
    ParseResults(){

    }
}
