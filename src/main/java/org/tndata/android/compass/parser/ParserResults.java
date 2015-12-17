package org.tndata.android.compass.parser;

import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.UserData;

import java.util.Map;


/**
 * Contains the results of a background parsing operation. The idea behind this
 * class is that whichever object triggered the parser will know what to pull
 * from the data set encapsulated in here.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public final class ParserResults{
    UserData mUserData;


    /**
     * Modified default constructor. Makes the class instantiable only within the package.
     */
    ParserResults(){

    }

    public UserData getUserData(){
        return mUserData;
    }
}
