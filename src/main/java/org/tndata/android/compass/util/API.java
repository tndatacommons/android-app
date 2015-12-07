package org.tndata.android.compass.util;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by isma on 12/7/15.
 */
public abstract class API{
    public static String getLogInUrl(){
        return Constants.BASE_URL + "auth/token/";
    }

    public static Map<String, String> getLogInBody(@NonNull String email, @NonNull String password){
        Map<String, String> logInBody = new HashMap<>();
        logInBody.put("email", email);
        logInBody.put("password", password);
        return logInBody;
    }

    public static String getSignUpUrl(){
        return Constants.BASE_URL + "users/";
    }


    public static Map<String, String> getSignUpBody(@NonNull String email, @NonNull String password,
                                                    @NonNull String firstName, @NonNull String lastName){

        Map<String, String> signUpBody = new HashMap<>();
        signUpBody.put("email", email);
        signUpBody.put("password", password);
        signUpBody.put("first_name", firstName);
        signUpBody.put("last_name", lastName);
        return signUpBody;
    }
}
