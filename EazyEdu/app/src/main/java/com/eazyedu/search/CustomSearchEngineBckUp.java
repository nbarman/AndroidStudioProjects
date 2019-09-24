package com.eazyedu.search;

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
import org.jsoup.nodes.Element;
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

public class CustomSearchEngineBckUp extends AsyncTask<String, Void, String>{

    private String searchQuery;
    private final String API_KEY = "AIzaSyCRJetjVHHNZDzA4E1u5gbyVa1mudoGgk0";
    private final String SEARCH_ENGINE_ID = "018018236259375124479:ze72lk3hwk4";
    private HttpURLConnection urlConnection;
    private static final String SEARCH_RANKING = "(\\s|\\A)#(\\w+)";
    private static final String SEARCH_UNIV_FEES = "\\$\\d{0,3}(,\\d{0,5})";
    private BufferedReader bReader;
    private UniversityLocationBean uLocationBean;
    private UniversityDetailsBean uDetailsBean;
    private Document parsedSearchHtml;
    private CustomSearch cSearchMain;
    private String searchData;
    private int searchOccuranceIndices[];

    private final String EMPTY_STRING = "";
    public CustomSearchEngineBckUp(CustomSearch cSearchMain){

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

        String status = getUniversityDetailsFromSearch(queries);
        if(status.equalsIgnoreCase("success")){
            //proceed with Location Details

        } else{
            status="Cannot connect to Source";
        }

        return status;
    }

    /**
     * Gets all the University Details Information
     * @param queries
     * @return
     */
    private String getUniversityDetailsFromSearch(String... queries){
        setSearchQuery(queries[0]);
        URL url;
        JSONObject jsonOutput;
        JSONArray items;
        String link = "";
        Map<String,String> feeMap = new LinkedHashMap<String,String>();
        String searchData = null;
        try {
            url = new URL(getGoogleAPIUrl(getSearchQuery(),"SearchAPI"));
            /* Parsing JSON */
            jsonOutput = getDataFromUrl(url);
            if(jsonOutput.has("items")) {
                items = jsonOutput.getJSONArray("items");
                jsonOutput = new JSONObject(items.getString(0));
            } else{
                //failed to get the details from API calls
                return "fail";
            }


            /**
             * Pattern matching and HTML parsing snippet
             */
            link = jsonOutput.getString("link");
            parsedSearchHtml = Jsoup.connect(link).get();
            //uDetailsBean.setUnivName(jsonOutput.getString("title").split("-")[0]);
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

                    searchOccuranceIndices = searchPatternMatch(rankingElement.text().trim(), CustomSearchEngineBckUp.SEARCH_RANKING,null,"SEARCH_RANKING");
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
            /** In state fees extraction **/
            Element inStateFees = feeListItems.get(0);
            searchOccuranceIndices = searchPatternMatch(inStateFees.html().trim(), CustomSearchEngineBckUp.SEARCH_UNIV_FEES,null,"FIND_FEES");
            Log.d("in state fees", inStateFees.html().trim().substring(searchOccuranceIndices[0],searchOccuranceIndices[1]));
            feeMap.put("InState",inStateFees.html().trim().substring(searchOccuranceIndices[0],searchOccuranceIndices[1]));

            /** Out state fees extraction **/
            Element outStateFees = feeListItems.get(1);
            searchOccuranceIndices = searchPatternMatch(outStateFees.html().trim(), CustomSearchEngineBckUp.SEARCH_UNIV_FEES,null,"FIND_FEES");
            Log.d("out state fees", outStateFees.html().trim().substring(searchOccuranceIndices[0],searchOccuranceIndices[1]));
            feeMap.put("OutState",outStateFees.html().trim().substring(searchOccuranceIndices[0],searchOccuranceIndices[1]));

            if(!feeMap.isEmpty()){
                uDetailsBean.setUnivFees(feeMap);
            }
            /* Google API calls */
            jsonOutput = getResponseFrmGooglePlacesAPI(jsonOutput,items);
            if(jsonOutput.has("result")) {
                jsonOutput = jsonOutput.getJSONObject("result");
                if(jsonOutput.has("website")) {
                    uDetailsBean.setUnivURL(jsonOutput.getString("website").trim());
                }
                if(jsonOutput.has("rating")){
                    uDetailsBean.setUnivRating(jsonOutput.getString("rating").trim());
                }
                if(jsonOutput.has("name")){
                    uDetailsBean.setUnivName(jsonOutput.getString("name"));
                }
                //setting the location bean details
                if(jsonOutput.has("address_components")){
                    setUnivCityAndStateFrmAPI(jsonOutput);
                }
            }
        } catch ( IOException | JSONException exception){
            Log.d("doInBackground: ", "FATAL! Exception detected " + exception.getMessage());
            return "fail";
        }
        return "success";
    }

    private String getLocDetailsFromSearch(){

            //We make some vars for calling the remaining APIs
            String city = uLocationBean.getLocCity();
            String state = uLocationBean.getLocState();

            return "success";
    }

    /**
     * Mainly used for instantiating the location bean
     * @param jsonOutput
     * @throws JSONException
     */
    private void setUnivCityAndStateFrmAPI(JSONObject jsonOutput) throws JSONException{

        JSONArray addressComponents = jsonOutput.getJSONArray("address_components");
        for(int i=0 ; i<addressComponents.length();i++){

            JSONObject addrComponent = addressComponents.getJSONObject(i);
            JSONArray addrTypes = addrComponent.getJSONArray("types");
            if(addrTypes.getString(0).equalsIgnoreCase("locality")){
                uLocationBean.setLocCity(addrComponent.getString("long_name"));
            }

            if(addrTypes.getString(0).equalsIgnoreCase("administrative_area_level_1")){
                uLocationBean.setLocState(addrComponent.getString("long_name"));
            }
        }

        if(jsonOutput.has("vicinity")){
            uLocationBean.setLocFullAddr(jsonOutput.getString("vicinity"));
        }
    }

    /**
     * Calls the Google Places API and returns a detailed JSON
     * @param jsonOutput
     * @param items
     * @return
     * @throws UnsupportedEncodingException
     * @throws MalformedURLException
     * @throws JSONException
     */
    private JSONObject getResponseFrmGooglePlacesAPI(JSONObject jsonOutput, JSONArray items) throws UnsupportedEncodingException, MalformedURLException, JSONException{

        /**
         * Google API calls code snippet
         */
        URL url = new URL(getGoogleAPIUrl(getSearchQuery(),"PlacesAPI"));
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
        return jsonOutput;
    }

    /**
     *  URLS for External Calls to Google APIs for Details
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
            return new JSONObject(new String("fail").trim());
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
        } else{
            cSearchMain.onCallBack("10");
        }
    }

}