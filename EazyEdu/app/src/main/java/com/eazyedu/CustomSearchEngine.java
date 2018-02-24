package com.eazyedu;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.api.client.auth.oauth2.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by namitmohanbarman on 2/20/18.
 */

public class CustomSearchEngine  extends AsyncTask<String, Void, String>{

    private String searchQuery;
    private final String API_KEY = "AIzaSyCRJetjVHHNZDzA4E1u5gbyVa1mudoGgk0";
    private final String SEARCH_ENGINE_ID = "018018236259375124479:ze72lk3hwk4";

    /**
     * This method connects to the Google Custom Search API (CSE) using the SEarch Engine ID
     * and the API Key which are generated from the Google Developer Console
     * @output : JSON
     */
    @Override
    protected String  doInBackground(String... queries){
        setSearchQuery(queries[0]);
        String lineText;
        StringBuilder sResponse = new StringBuilder();

        try {
            String encodedQuery = URLEncoder.encode(getSearchQuery(), "utf-8");
            //String encodedQuery = "Cleveland%20State%20University";
            Log.d("Lognam2", "Encoded Query is "+encodedQuery);
            URL url = new URL("https://www.googleapis.com/customsearch/v1?"+
                    "key="+ API_KEY +"&cx="+SEARCH_ENGINE_ID+"&q="+ encodedQuery +
                    "&exactTerms="+encodedQuery);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Accept", "application/json");
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (urlConnection.getInputStream())));


            while((lineText=br.readLine())!=null){
                sResponse.append(lineText);
            }

            JSONObject jsonOutput = new JSONObject(sResponse.toString().trim());
            JSONArray items = jsonOutput.getJSONArray("items");

            jsonOutput = new JSONObject(items.getString(0));

            Log.d("Lognam2", jsonOutput.getString("link"));

        } catch(IOException exception){
                //Handle exception here
            Log.d("Lognam2", "FATAL! IOEXCEPTION detected : "+ exception);
        } catch (JSONException exception){
                // Handle exception here
            Log.d("Lognam2", "FATAL! JSONEXCEPTION detected "+ exception);
        }
            return null;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }


    protected void onProgressUpdate() {
        //called when the background task makes any progress
        //Toast.makeText(ContactUs.this, "Your message is being sent", Toast.LENGTH_LONG).show();
    }

    protected void onPreExecute() {
        //called before doInBackground() is started
    }
    protected void onPostExecute() {
        //called after doInBackground() has finished
    }

}
