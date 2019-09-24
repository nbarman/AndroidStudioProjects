package com.eazyedu.utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


    /**
     * Deactivated currently
     * A very important method which uses Java RegEx to extract details
     * @param searchStr
     * @param searchPatternStrt
     * @param searchPatternEnd
     * @param pSearchCode
     * @return
     */

    private int[] searchPatternMatch(String searchStr, String searchPatternStrt, String searchPatternEnd, String pSearchCode){

        int sOccIndices[] = {-1,-1};
        Pattern searchPattern = null;
        Matcher matchSearchPattern= null;
        if(pSearchCode!=null && pSearchCode.equalsIgnoreCase("SEARCH_RANKING")){

            searchPattern = Pattern.compile(searchPatternStrt,
                    Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            matchSearchPattern= searchPattern.matcher(searchStr);
            if (matchSearchPattern.find()) {
                sOccIndices[0] = matchSearchPattern.start()+1; //Ignore the hash(#)
                sOccIndices[1] = matchSearchPattern.end();
            }
        }

        if(pSearchCode!=null && pSearchCode.equalsIgnoreCase("FIND_FEES")){

            searchPattern = Pattern.compile(searchPatternStrt,
                    Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            matchSearchPattern= searchPattern.matcher(searchStr);
            if (matchSearchPattern.find()) {
                sOccIndices[0] = matchSearchPattern.start();
                sOccIndices[1] = matchSearchPattern.end();
            }
        }

        if(matchSearchPattern!=null) {
            matchSearchPattern.reset();
        }
        return sOccIndices;
    }

}
