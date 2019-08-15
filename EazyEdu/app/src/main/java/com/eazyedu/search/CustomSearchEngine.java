package com.eazyedu.search;

import android.os.AsyncTask;
import android.util.Log;
import android.webkit.JavascriptInterface;
import org.jsoup.nodes.Element;

import com.eazyedu.beans.UniversityDetailsBean;
import com.eazyedu.beans.UniversityLocationBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by namitmohanbarman on 2/20/18.
 */

public class CustomSearchEngine  extends AsyncTask<String, Void, String>{

    private String searchQuery;
    private final String API_KEY = "AIzaSyCRJetjVHHNZDzA4E1u5gbyVa1mudoGgk1";
    private final String SEARCH_ENGINE_ID = "018018236259375124479:ze72lk3hwk4";
    private HttpURLConnection urlConnection;
    private static final String SEARCH_RANKING = "(\\s|\\A)#(\\w+)";
    private BufferedReader bReader;
    private UniversityLocationBean uLocationBean;
    private UniversityDetailsBean uDetailsBean;
    private Document parsedSearchHtml;
    private CustomSearch cSearchMain;
    private String searchData;
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

    /**
     * Gets all the University Details Information
     * @param uDetailsBean
     * @param queries
     * @return
     */
    private String getUniversityDetailsFromSearch(UniversityDetailsBean uDetailsBean, String... queries){


        setSearchQuery(queries[0]);
        URL url;
        JSONObject jsonOutput;
        JSONArray items;
        String link = "";
        Map<String,String> feesMap = new LinkedHashMap<String,String>();
        String searchData = null;

        try {

            url = new URL(getGoogleAPIUrl(getSearchQuery(),"SearchAPI"));
            /* Parsing JSON */
            jsonOutput = getDataFromUrl(url);
            items = jsonOutput.getJSONArray("items");
            jsonOutput = new JSONObject(items.getString(0));

            /**
             * Pattern matching and HTML parsing snippet
             */
            link = jsonOutput.getString("link");
            parsedSearchHtml = Jsoup.connect(link).get();
            uDetailsBean.setUnivName(jsonOutput.getString("title").split("-")[0]);
            Elements allSearchNodes = parsedSearchHtml.getElementsByClass("hero-content-main");
            for(Element searchNode : allSearchNodes){
                Elements addressPhElements = searchNode.select("p");
                for(Element addressPhElement : addressPhElements){
                    String[] addressAndPhoneNumber = addressPhElement.text().trim().split("\\|");
                    if(addressAndPhoneNumber!=null && addressAndPhoneNumber.length>1){
                        uDetailsBean.setUnivLocation(addressAndPhoneNumber[0]);
                        uDetailsBean.setPhoneNumber(addressAndPhoneNumber[1]);
                    } else{
                        uDetailsBean.setUnivLocation(addressAndPhoneNumber[0]);
                        uDetailsBean.setPhoneNumber("Not Available");
                    }
                }

                Elements rankingElements = searchNode.getElementsByClass("hero-ranking-data-rank");
                for(Element rankingElement : rankingElements){

                    searchOccuranceIndices = searchPatternMatch(rankingElement.text().trim(),SEARCH_RANKING,null,"SEARCH_RANKING");
                    Log.d("Ranking", rankingElement.text().trim().substring(searchOccuranceIndices[0],searchOccuranceIndices[1]));
                    String ranking = rankingElement.text().trim().substring(searchOccuranceIndices[0],searchOccuranceIndices[1]);
                    if(!ranking.isEmpty()){
                        uDetailsBean.setUnivRanking(ranking);
                    }
                }
            }
            /**
             * University fees extraction
             */
            Elements feeStructureNodes = parsedSearchHtml.getElementsByClass("hero-stats-widget-stats");
            /**
             * We need only the first section
             */
            Element feeStructureSection = feeStructureNodes.first();
            Elements feeList = feeStructureSection.select("ul");
            Elements feeListItems = feeList.select("li");
            Element inStateFees = feeListItems.get(0);
            searchOccuranceIndices = searchPatternMatch(inStateFees.html().trim(),"\\$([^\\$]*)\\$",null,"FIND_FEES");
            Log.d("in state fees", inStateFees.html().trim().substring(searchOccuranceIndices[0],searchOccuranceIndices[1]));

            Element outStateFees = feeListItems.get(1);
            for(int i=0; i<feeListItems.size();i++){

                Element feeListItem = feeListItems.get(i);
                Log.d("Fee Item", feeListItem.text());
            }

            Log.d("section", feeStructureNodes.text());


            /**
             * Google API calls code snippet
             */
            url = new URL(getGoogleAPIUrl(getSearchQuery(),"PlacesAPI"));
            /* Parsing JSON */
            jsonOutput = getDataFromUrl(url);
            items = jsonOutput.getJSONArray("candidates");
            jsonOutput = items.getJSONObject(0);
            String placeID = jsonOutput.getString("place_id").trim();

            url = new URL(getGoogleAPIUrl(placeID,"PlacesDetailsAPI"));
            boolean isAPIError = false;
            String apiError= null;
            do {
                jsonOutput = getDataFromUrl(url);
                if(jsonOutput.has("error_message")) {
                    apiError = jsonOutput.getString("error_message");
                }
                if(apiError!=null && apiError.contains("You have exceeded your daily request quota for this API")){
                    isAPIError = true;
                } else{
                    isAPIError = false;
                }
                if(isAPIError) {// Google API Error. Retry after 3 secs
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }while(isAPIError);

            if(jsonOutput.has("result")) {
                jsonOutput = jsonOutput.getJSONObject("result");
                if(jsonOutput.has("website")) {
                    uDetailsBean.setUnivURL(jsonOutput.getString("website").trim());
                }
            }


        } catch (JSONException exception){
            int d = Log.d("doInBackground: ", "FATAL! JSONEXCEPTION detected " + exception);
            return "fail";
        } catch( Exception exception){
            int d = Log.d("doInBackground: ", "FATAL! Exception detected "+ exception);
            return "fail";
        }

        return "success";

    }

    /**
     *  External Calls to Google APIs for Details
     * @param query
     * @param apiName
     * @return
     * @throws UnsupportedEncodingException
     */
    private String getGoogleAPIUrl(String query, String apiName)throws UnsupportedEncodingException {

        String url = "";
        if(apiName.equals("SearchAPI")) {
            String encodedQuery = URLEncoder.encode(query, "utf-8");
            url=new String("https://www.googleapis.com/customsearch/v1?" +
                    "key=" + API_KEY + "&cx=" + SEARCH_ENGINE_ID + "&q=" + encodedQuery +
                    "&exactTerms=" + encodedQuery);
        }
        if(apiName.equals("PlacesAPI")){
            String encodedQuery = URLEncoder.encode(query, "utf-8");
            url = new String("https://maps.googleapis.com/maps/api/place/findplacefromtext/json?"+
                                     "input="+encodedQuery+"&inputtype=textquery&key="+API_KEY);
        }
        if(apiName.equals("PlacesDetailsAPI")){

            url = new String("https://maps.googleapis.com/maps/api/place/details/json?"+
                             "placeid="+query+"&key="+API_KEY);
        }
        return url;
    }

    /**
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

    /**
     *  Returns the response of the HTTP call in JSON format
     * @param url
     * @return
     * @throws JSONException
     */
    private JSONObject getDataFromUrl(URL url) throws JSONException{
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
            Log.d("getDataFromUrl : ", "FATAL! IOEXCEPTION detected : "+ exception.getMessage());
            cSearchMain.onCallBack("10");
        }
        if(sResponse==null || sResponse.length()==0){

            sResponse.append("Empty Response from Search");
        }
        JSONObject jsonOutput = new JSONObject(sResponse.toString().trim());
        return jsonOutput;
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



    public UniversityDetailsBean getuDetailsBean() {
        return uDetailsBean;
    }

    public void setuDetailsBean(UniversityDetailsBean uDetailsBean) {
        this.uDetailsBean = uDetailsBean;
    }

    public UniversityLocationBean getuLocationBean() {
        return uLocationBean;
    }
    public void setuLocationBean(UniversityLocationBean uLocationBean) {
        this.uLocationBean = uLocationBean;
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
            cSearchMain.setUnivDetailsBean(getuDetailsBean());
            cSearchMain.onCallBack("00"); //Response callback to main activity method
        }
    }

}