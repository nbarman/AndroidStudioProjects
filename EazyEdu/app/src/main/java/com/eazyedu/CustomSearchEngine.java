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
    private static final String SEARCH_PATTERN_TRIM_1 = "Research\\s*Universities";
    private static final String SEARCH_PATTERN_TRIM_2 = "Free\\s*Issues\\s*of\\s*Forbes";
    private BufferedReader bReader;
    private UniversityDetailsBean uDetailsBean;
    private UniversityLocationBean uLocationBean;
    private Document htmlDoc;
    private CustomSearch cSearchMain;
    private String searchData;
    private int strTrimIndex;
    private int startStrTrimIndex;
    private int endStrTrimIndex;
    private int searchTrimIndices[];

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
        setSearchQuery(queries[0]);
        String link = "";
        String sResponse, lResponse, sName=getSearchQuery(), sLocation="";

        try {
            String encodedQuery = URLEncoder.encode(getSearchQuery(), "utf-8");
            URL url = new URL("https://www.googleapis.com/customsearch/v1?"+
                    "key="+ API_KEY +"&cx="+SEARCH_ENGINE_ID+"&q="+ encodedQuery +
                    "&exactTerms="+encodedQuery);

            sResponse = getDataFromUrl(url);

            JSONObject jsonOutput = new JSONObject(sResponse);
            JSONArray items = jsonOutput.getJSONArray("items");

            jsonOutput = new JSONObject(items.getString(0));

            link = jsonOutput.getString("link");
            Log.d("Lognam2" , link);

            htmlDoc = Jsoup.connect(link).get();
            Log.d("Lognam2" , "University name is:: "+jsonOutput.getString("title"));
            setSearchData(htmlDoc.text()); //Use Instance String variable to manipulate




            //searchTrimIndices contains two values [matchIndexBegin.....matchIndexEnd]
            searchTrimIndices = searchPatternMatch(getSearchData(),CustomSearchEngine.SEARCH_PATTERN_TRIM_1, "PSHTRIM01");
            setSearchData(getSearchData().substring(0,searchTrimIndices[1]).trim());

            searchTrimIndices = searchPatternMatch(getSearchData(),CustomSearchEngine.SEARCH_PATTERN_TRIM_2,"PSHTRIM02");
            setSearchData(getSearchData().substring(searchTrimIndices[1],getSearchData().length()).trim());

            //Log.d("Lognam2","Data is :: "+getSearchData());

            searchTrimIndices = searchPatternMatch(getSearchData(),"[^0-9]*","PSH004");

            Log.d("Lognam2"," University Place is : "+getSearchData().substring(searchTrimIndices[0],searchTrimIndices[1]-1));

            Log.d("Lognam2", "Phone number is : "+getSearchData().substring(searchTrimIndices[1]-1,searchTrimIndices[1]+12));





            String sData[] = searchData.split("\\s+");




        } catch (JSONException exception){
            Log.d("doInBackground: "+CustomSearchEngine.class, "FATAL! JSONEXCEPTION detected "+ exception);
        } catch( Exception exception){
            Log.d("doInBackground: "+CustomSearchEngine.class, "FATAL! Exception detected "+ exception);
        }
        return link;
    }

    public int[] searchPatternMatch(String searchStr, String searchPatternStr, String pSearchCode){

        int trimIndices[] = {-1,-1};
        Pattern searchPattern = Pattern.compile(searchPatternStr);
        Matcher matchSearchPattern = searchPattern.matcher(searchStr);
        //Check the occurance of the searchPattern
        if(matchSearchPattern.find()){

            trimIndices[0] = matchSearchPattern.start();
            trimIndices[1] = matchSearchPattern.end();
            //Log.d("Lognam2" , "Indices are "+ trimIndices[0]+"::"+trimIndices[1]);
        } else if( pSearchCode.equals("PSHTRIM01") || pSearchCode.equals("PSHTRIM02")){
            // The place holders are not available. We go the long way
            trimIndices[0] = 0;
            trimIndices[1] = searchStr.length();
        }
        //Log.d("Lognam2", "Indices are "+ trimIndices[0]+"::"+trimIndices[1]+"::"+pSearchCode);
        matchSearchPattern.reset();
        return trimIndices;
    }

    public String getDataFromUrl(URL url){
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
            Log.d("onPostExecute : "+CustomSearchEngine.class, "FATAL! IOEXCEPTION detected : "+ exception);
        }

        if(cSearchMain!= null){
            cSearchMain.onCallBack("00"); //Response callback to main activity method
        }
    }

}