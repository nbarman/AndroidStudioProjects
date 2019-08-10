package com.eazyedu.utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author nambarma
 * This is utilities class which contains features to eaze our coding.
 * Adding them as needed
 */

public class EazyUtils {

    private static Gson gsonClassObject;



    /**
     * Util for passing the custom class object as JSON
     * @return
     */
    public static Gson getGsonParser() {
        if(null == gsonClassObject) {
            GsonBuilder builder = new GsonBuilder();
            gsonClassObject = builder.create();
        }
        return gsonClassObject;
    }

}
