package com.eazyedu;

import android.os.AsyncTask;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.eazyedu.beans.UniversityDetailsBean;
import com.eazyedu.beans.UniversityLocationBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by namitmohanbarman on 2/20/18.
 */

public class CustomSearchEngine  extends AsyncTask<String, Void, String>{

    private String searchQuery;
    private final String API_KEY = "AIzaSyCRJetjVHHNZDzA4E1u5gbyVa1mudoGgk0";
    private final String SEARCH_ENGINE_ID = "018018236259375124479:ze72lk3hwk4";
    private HttpURLConnection urlConnection;
    private static final String SEARCH_PATTERN_TRIM_1 = "Billionaire\\sSecrets";
    private static final String SEARCH_PATTERN_TRIM_2 = "At\\sa\\sGlance";
    private static final String SEARCH_PATTERN_HYPERLINK = "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
            + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
            + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)";
    private static final String SEARCH_PATTERN_PH_NUMBER = "\\d{3}\\s\\d{7}";

    private BufferedReader bReader;
    private UniversityDetailsBean uDetailsBean;
    private UniversityLocationBean uLocationBean;
    private Document htmlDoc;
    private CustomSearch cSearchMain;
    private String searchData;
    private int strTrimIndex;
    private int startStrTrimIndex;
    private int endStrTrimIndex;
    private int searchOccuranceIndices[];

    private final String EMPTY_STRING = "";
    public CustomSearchEngine(CustomSearch cSearchMain){

        uDetailsBean = new UniversityDetailsBean();
        uLocationBean = new UniversityLocationBean();
        this.cSearchMain = cSearchMain;
    }

    /**
     * This method connects to the Google Custom Search API (CSE) using the Search Engine ID
     * and the API Key which are generated from the Google Developer Console
     * @output : JSON
     */
    @Override
    @JavascriptInterface
    public String  doInBackground(String... queries){

        String status = getUniversityDetailsFromSearch(uDetailsBean,queries);


        return status;
    }

    private String getUniversityDetailsFromSearch(UniversityDetailsBean uDetailsBean, String... queries){


        setSearchQuery(queries[0]);
        String link = "";
        String sResponse, lResponse, sName=getSearchQuery(), sLocation="";
        String searchData = null;

        try {
            String encodedQuery = URLEncoder.encode(getSearchQuery(), "utf-8");
            URL url = new URL("https://www.googleapis.com/customsearch/v1?"+
                    "key="+ API_KEY +"&cx="+SEARCH_ENGINE_ID+"&q="+ encodedQuery +
                    "&exactTerms="+encodedQuery);

            sResponse = getDataFromUrl(url);

            if(sResponse==null || sResponse.isEmpty()){

                return "Empty Response from Search";
            }

            JSONObject jsonOutput = new JSONObject(sResponse);
            JSONArray items = jsonOutput.getJSONArray("items");

            jsonOutput = new JSONObject(items.getString(0));

            link = jsonOutput.getString("link");
            htmlDoc = Jsoup.connect(link).get();
            Log.d("Lognam2" , "University name is:: "+jsonOutput.getString("title"));
            uDetailsBean.setUnivName(jsonOutput.getString("title"));
            searchData = htmlDoc.text(); //Use Instance String variable to manipulate

            //searchOccuranceIndices contains two values [matchIndexBegin.....matchIndexEnd]
            searchOccuranceIndices = searchPatternMatch(searchData,CustomSearchEngine.SEARCH_PATTERN_TRIM_1,CustomSearchEngine.SEARCH_PATTERN_TRIM_2, "SEARCH_PATTERN_TRIM");
            searchData = searchData.substring(searchOccuranceIndices[0],searchOccuranceIndices[1]);
            Log.d("searchData: ", searchData);
            searchOccuranceIndices = searchPatternMatch(searchData,CustomSearchEngine.SEARCH_PATTERN_HYPERLINK,null,"SEARCH_LINK");
            String hLink = searchData.substring(searchOccuranceIndices[0],searchOccuranceIndices[1]);
            uDetailsBean.setUnivURL(hLink.trim());
            searchData = searchData.replaceAll("[\\-\\+\\.\\^:,]","");
            searchData = searchData.replaceAll("\\p{P}","");
            searchOccuranceIndices = searchPatternMatch(searchData,CustomSearchEngine.SEARCH_PATTERN_PH_NUMBER,null, "SEARCH_PHONE_NO");
            String phNumber =  searchData.substring(searchOccuranceIndices[0],searchOccuranceIndices[1]);
            phNumber = phNumber.replaceAll("\\s","");

            if(phNumber.length()<=10){
                //format the phone number
                StringBuilder formattedPhNumber = new StringBuilder();
                for(int i = 0;i<phNumber.length();i++){

                    if(i%3 ==0 && i>0 && i<= 6){
                    formattedPhNumber.append('-');
                    }
                    formattedPhNumber.append(phNumber.charAt(i));

                }
                Log.d("phNumber", formattedPhNumber.toString());
                uDetailsBean.setPhoneNumber(formattedPhNumber.toString());

            } else{
                uDetailsBean.setPhoneNumber("000");
            }
            String location = searchData.substring(0,searchOccuranceIndices[0]);
            uDetailsBean.setUnivLocation(location.trim());
            //Log.d("phNumber", phNumber);

        } catch (JSONException exception){
            int d = Log.d("doInBackground: ", "FATAL! JSONEXCEPTION detected " + exception);
            return "fail";
        } catch( Exception exception){
            int d = Log.d("doInBackground: ", "FATAL! Exception detected "+ exception);
            return "fail";
        }

        return "success";

    }



    private int[] searchPatternMatch(String searchStr, String searchPatternStrt, String searchPatternEnd, String pSearchCode){

        int sOccIndices[] = {-1,-1};
        Pattern searchPattern = null;
        Matcher matchSearchPattern= null;
        if(pSearchCode!=null && pSearchCode.equalsIgnoreCase("SEARCH_PATTERN_TRIM")) {
            searchPattern = Pattern.compile(searchPatternStrt,
                    Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            matchSearchPattern= searchPattern.matcher(searchStr);
            //Check the occurance of the searchPattern
            if (matchSearchPattern.find()) {

                sOccIndices[0] = matchSearchPattern.end();
            }

            searchPattern = Pattern.compile(searchPatternEnd,
                    Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

            matchSearchPattern = searchPattern.matcher(searchStr);
            //Check the occurance of the searchPattern
            if (matchSearchPattern.find()) {

                sOccIndices[1] = matchSearchPattern.start();
            }
        } else if(pSearchCode!=null && (pSearchCode.equalsIgnoreCase("SEARCH_PHONE_NO")
                    || pSearchCode.equalsIgnoreCase("SEARCH_LINK"))){

            searchPattern = Pattern.compile(searchPatternStrt,
                    Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            matchSearchPattern= searchPattern.matcher(searchStr);
            //Check the occurance of the searchPattern
            if (matchSearchPattern.find()) {

                sOccIndices[0] = matchSearchPattern.start();
                sOccIndices[1] = matchSearchPattern.end();
            }
        }



        Log.d("Start: End", sOccIndices[0]+"::" + sOccIndices[1]);
        if(matchSearchPattern!=null) {
            matchSearchPattern.reset();
        }
        return sOccIndices;
    }

    private String getDataFromUrl(URL url){
        String lineText;
        StringBuilder sResponse = new StringBuilder();

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Accept", "application/json");
            bReader = new BufferedReader(new InputStreamReader(
                    (urlConnection.getInputStream())));


            while ((lineText = bReader.readLine()) != null) {
                sResponse.append(lineText);
            }
        }catch(IOException exception){

        }

        return sResponse.toString().trim();
    }



    public int getStrTrimIndex() {
        return strTrimIndex+1;
    }

    public void setStrTrimIndex(int strTrimIndex) {
        this.strTrimIndex = strTrimIndex;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }


    public String getSearchData() {
        return searchData;
    }

    public void setSearchData(String searchData) {
        this.searchData = searchData;
    }


    protected void onProgressUpdate() {
        //called when the background task makes any progress
    }

    protected void onPreExecute() {
        //called before doInBackground() is started
        setSearchData(EMPTY_STRING);
    }
    protected void onPostExecute(String link){
        setSearchData(EMPTY_STRING);
        try{
            if(urlConnection!=null && bReader!=null) {
                urlConnection.disconnect();
                bReader.close();
            }
        }catch(IOException exception){
            Log.d("onPostExecute : ", "FATAL! IOEXCEPTION detected : "+ exception);
        }

        if(cSearchMain!= null){
            cSearchMain.onCallBack("00"); //Response callback to main activity method
        }
    }

}