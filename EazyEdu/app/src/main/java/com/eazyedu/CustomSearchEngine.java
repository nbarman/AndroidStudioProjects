package com.eazyedu;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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
    private HttpURLConnection urlConnection;
    private BufferedReader bReader;
    private CustomSearch cSearchMain;
    private WebView webView_Search;

    public CustomSearchEngine(CustomSearch cSearchMain, WebView webView_Search){

        this.cSearchMain = cSearchMain;
        this.webView_Search = webView_Search;
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
            //String encodedQuery = "Cleveland%20State%20University";
            Log.d("Lognam2", "Encoded Query is "+encodedQuery);
            URL url = new URL("https://www.googleapis.com/customsearch/v1?"+
                    "key="+ API_KEY +"&cx="+SEARCH_ENGINE_ID+"&q="+ encodedQuery +
                    "&exactTerms="+encodedQuery);

            sResponse = getDataFromUrl(url);

            JSONObject jsonOutput = new JSONObject(sResponse);
            JSONArray items = jsonOutput.getJSONArray("items");

            jsonOutput = new JSONObject(items.getString(0));

            link = jsonOutput.getString("link");

            Log.d("Lognam2", link);

            Document htmlDoc = Jsoup.connect(link).get();
            String description =
                    htmlDoc.select("meta[name=description]").get(0)
                            .attr("content");
            sLocation = htmlDoc.select("meta[data-school-name="+sName.trim()+"]").get(0)
                    .attr("data-school-location");
            Log.d("Lognam2", sLocation);

            webView_Search.getSettings().setJavaScriptEnabled(true);
            webView_Search.addJavascriptInterface(new LoadListener(), "HtmlViewer");

            WebViewClient wvClient = new WebViewClient(){
                @Override
                public void onPageFinished(WebView view, String url) {
                    Log.d("Lognam2", "Here now");
                    view.loadUrl("javascript:window.HtmlViewer.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
            }};
            webView_Search.setWebViewClient(wvClient);

            webView_Search.loadUrl(link);



        } catch(IOException exception){
            Log.d("doInBackground: "+CustomSearchEngine.class, "FATAL! IOEXCEPTION detected : "+ exception);
        } catch (JSONException exception){
            Log.d("doInBackground: "+CustomSearchEngine.class, "FATAL! JSONEXCEPTION detected "+ exception);
        } catch( Exception exception){
            Log.d("doInBackground: "+CustomSearchEngine.class, "FATAL! Exception detected "+ exception);
        }
            return link;
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

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }


    protected void onProgressUpdate() {
        //called when the background task makes any progress
    }

    protected void onPreExecute() {
        //called before doInBackground() is started
    }
    protected void onPostExecute(String link){
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

/**
 *
 */
class LoadListener{

    public LoadListener(){
        Log.d("Lognam2", "Inside Load Listener constructor");
    }

    @JavascriptInterface
    public void processHTML(String html)
    {
        Log.d("Lognam2", "END Engine");
    }
}
