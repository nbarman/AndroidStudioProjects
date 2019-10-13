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

/**
 * Created by namitmohanbarman on 2/20/18.
 * This architecture is updated to function as a SaaS. Most of the information is now retrieved using Web Services
 */

public class CustomSearchEngine  extends AsyncTask<String, Void, String>{

    private String searchQuery;
    private final String GOOGLE_API_KEY = "AIzaSyCRJetjVHHNZDzA4E1u5gbyVatmpdoGgk0";
    private final String US_GOVT_EDU_DB_API_KEY = "Kbf9QcXEPW5r7NX2VgwEwFwdeSuddLBf6NnnzqtL";
    private final String SEARCH_ENGINE_ID = "018018236259375124479:ze72lk3hwk4";
    private HttpURLConnection urlConnection;
    private static final String SEARCH_RANKING = "(\\s|\\A)#(\\w+)";
    private static final String SEARCH_UNIV_FEES = "\\$\\d{0,3}(,\\d{0,5})";
    private BufferedReader bReader;
    private UniversityLocationBean uLocationBean;
    private UniversityDetailsBean uDetailsBean;
    private String mainUivPostalCode;
    private Document wikiRankDocument;
    private CustomSearch cSearchMain;
    private String searchData;

    private final String EMPTY_STRING = "";
    public CustomSearchEngine(CustomSearch cSearchMain){

        uDetailsBean = new UniversityDetailsBean();
        uLocationBean = new UniversityLocationBean();
        this.cSearchMain = cSearchMain;
    }

    /**
     * This method connects to the Google Custom Search API (CSE) using the Search Engine ID
     * and the API Key which are generated from the Google Developer Console
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
        JSONObject jCmpOutput;
        JSONArray jArrayItems;
        String link = "";
        Map<String,String> feeMap = new LinkedHashMap<String,String>();
        String searchData = null;
        try {
            url = new URL(getExternalAPIUrl(getSearchQuery(),"SearchAPI"));
            /* Parsing JSON */
            jsonOutput = getDataFromUrl(url);
            if(jsonOutput.has("items")) {
                jArrayItems = jsonOutput.getJSONArray("items");
                jsonOutput = new JSONObject(jArrayItems.getString(0));
                jCmpOutput = new JSONObject(jArrayItems.getString(1));

                /**
                 * Retrieving metatags for Data Set 1
                 */
                JSONObject pMapObj = jsonOutput.getJSONObject("pagemap");
                JSONArray metaTagsArry = pMapObj.getJSONArray("metatags");

                JSONObject metaTags = metaTagsArry.getJSONObject(0);

                String schName = metaTags.getString("data-school-name");
                String schLocStr = metaTags.getString("data-school-location");


                /**
                 * Retrieving metatags for Data Set 2
                 *
                 */

                pMapObj = jCmpOutput.getJSONObject("pagemap");
                metaTagsArry = pMapObj.getJSONArray("metatags");
                metaTags = metaTagsArry.getJSONObject(0);

                String cmpSchName = metaTags.getString("data-school-name");

                if(schName!=null && cmpSchName!=null && schLocStr!=null){

                    /**
                     * Check for Ambiguous university Entry
                     */
                    if(schName.equalsIgnoreCase(cmpSchName)) {
                        schName.replaceAll("[-,]", " "); //Removing unwanted characters with whitespace
                        uDetailsBean.setUnivName(schName.trim());
                        uDetailsBean.setUnivLocation(schLocStr);
                    } else{

                        return "Exception!! Ambiguous entry";
                    }
                } else{
                    return "fail";
                }
            } else{
                //failed to get the details from API calls
                return "fail";
            }

            /* Google API calls */
            jsonOutput = getResponseFrmGooglePlacesAPI(jsonOutput,jArrayItems);
            if(jsonOutput.has("result")) {
                jsonOutput = jsonOutput.getJSONObject("result");
                if(jsonOutput.has("website")) {
                    uDetailsBean.setUnivURL(jsonOutput.getString("website").trim());
                }
                if(jsonOutput.has("rating")){
                    uDetailsBean.setUnivRating(jsonOutput.getString("rating").trim());
                }
                if(jsonOutput.has("formatted_phone_number")){
                    uDetailsBean.setPhoneNumber(jsonOutput.getString("formatted_phone_number").trim());
                }
                //setting the location bean details
                if(jsonOutput.has("address_components")){
                    setUnivCityAndStateFrmAPI(jsonOutput);
                }
            }

            /**
             * US Department of Education API calls
             */

            jsonOutput = getResponseFromUSGovtEduDataBankAPI(uDetailsBean.getUnivName());
            if(jsonOutput!=null && jsonOutput.length()!=0){

                jArrayItems = jsonOutput.getJSONArray("results");

                for(int i =0; i< jArrayItems.length(); i++){

                    JSONObject currentObj = jArrayItems.getJSONObject(i);

                    String currentUnivPCode = currentObj.getString("school.zip").trim();

                    if(currentUnivPCode.contains("-")){
                        String[] parsedPCode = currentUnivPCode.split("-");
                        currentUnivPCode = parsedPCode[0].trim();
                    }

                    if(currentUnivPCode.equalsIgnoreCase(mainUivPostalCode)){
                        //This is the correct data set from the resultSet
                        Map<String,String> univFees = new LinkedHashMap<String,String>();
                        univFees.put("OutState",currentObj.getString("latest.cost.tuition.out_of_state"));
                        univFees.put("InState",currentObj.getString("latest.cost.tuition.in_state"));
                        uDetailsBean.setUnivFees(univFees);
                        break;
                    }
                }
            } else{

                Log.d("ERROR!! US DATA", " Getting null response");
            }

            /**
             * WikiMedia API calls using JSoup
             */
            String univNameWithUnderscores = uDetailsBean.getUnivName().replaceAll(" ","_");
            String wikiRankingURL = getExternalAPIUrl(univNameWithUnderscores,"WikiRanking");
            wikiRankDocument = Jsoup.connect(wikiRankingURL).get();
            Elements allWikiElements = wikiRankDocument.getElementsByClass("infobox");
            for(Element wikiElement : allWikiElements){
                if(wikiElement.className().trim().equals("infobox")){
                    Log.d("wiki class ", wikiElement.className().trim());
                    Elements tableRows = wikiElement.select("tr");
                    for(int i=0 ; i <= 2; i++){
                        if(i==2){
                            Element tableRow = tableRows.get(i);
                            Elements tableCols = tableRow.select("td");

                            Element tableCol = tableCols.get(0);
                            if(tableCol!=null && tableCol.hasText()){
                                Log.d("Ranking retrieved : ", tableCol.text());
                                uDetailsBean.setUnivRanking(tableCol.text().trim() + "(Courtsey : Forbes)");
                            }
                        }
                    }
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

            if(addrTypes.getString(0).equalsIgnoreCase("postal_code")){
                mainUivPostalCode = addrComponent.getString("long_name");
            }
        }

        if(jsonOutput.has("vicinity")){
            uLocationBean.setLocFullAddr(jsonOutput.getString("vicinity"));
        }
    }

    /**
     *  This method calls a different API which is registered with the US Department of Education to retrieve important Univ Details
     * @param univNameWithLoc
     * @return
     * @throws UnsupportedEncodingException
     * @throws MalformedURLException
     * @throws JSONException
     */

    private JSONObject getResponseFromUSGovtEduDataBankAPI(String univNameWithLoc)throws UnsupportedEncodingException, MalformedURLException, JSONException{

        URL url = new URL(getExternalAPIUrl(univNameWithLoc,"USDataBankAPI"));
        JSONObject jsonOutput = getDataFromUrl(url);
        return jsonOutput;
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
        URL url = new URL(getExternalAPIUrl(getSearchQuery(),"PlacesAPI"));
        /* Parsing JSON */
        jsonOutput = getDataFromUrl(url);
        items = jsonOutput.getJSONArray("candidates");
        jsonOutput = items.getJSONObject(0);
        String placeID = jsonOutput.getString("place_id").trim();

        url = new URL(getExternalAPIUrl(placeID,"PlacesDetailsAPI"));
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
     *  URLS for External Calls to US Education Databank and Google APIs for Details
     * @param query
     * @param apiName
     * @return
     * @throws UnsupportedEncodingException
     */
    private String getExternalAPIUrl(String query, String apiName)throws UnsupportedEncodingException {

        String url = "";
        if(apiName.equals("SearchAPI")) {
            String encodedQuery = URLEncoder.encode(query, "utf-8");
            url=new String("https://www.googleapis.com/customsearch/v1?" +
                    "key=" + GOOGLE_API_KEY + "&cx=" + SEARCH_ENGINE_ID + "&q=" + encodedQuery +
                    "&exactTerms=" + encodedQuery);
        }
        if(apiName.equals("PlacesAPI")){
            String encodedQuery = URLEncoder.encode(query, "utf-8");
            url = new String("https://maps.googleapis.com/maps/api/place/findplacefromtext/json?"+
                                     "input="+encodedQuery+"&inputtype=textquery&key="+ GOOGLE_API_KEY);
        }
        if(apiName.equals("PlacesDetailsAPI")){

            url = new String("https://maps.googleapis.com/maps/api/place/details/json?"+
                             "placeid="+query+"&key="+ GOOGLE_API_KEY);
        }
        if(apiName.equals("USDataBankAPI")){

            String encodedQuery = URLEncoder.encode(query, "utf-8");
            url = new String("https://api.data.gov/ed/collegescorecard/v1/schools?"+
                    "school.name="+encodedQuery+"&_fields=school.name," +
                    "school.zip," +
                    "latest.cost.tuition.out_of_state," +
                    "latest.cost.tuition.in_state," +
                    "school.school_url" +
                    "&api_key="
                    + US_GOVT_EDU_DB_API_KEY
                    );
        }

        if(apiName.equals("WikiRanking")){
            url = new String("https://en.wikipedia.org/api/rest_v1/page/mobile-html/"
                    +query
                    +"?redirect=false");
        }
        return url;
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