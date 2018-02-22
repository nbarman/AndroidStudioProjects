package com.eazyedu;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by namitmohanbarman on 2/20/18.
 */

public class CustomSearchEngine extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("Lognam2", "In here");
        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d("Lognam3", query);
        }
        setContentView(R.layout.content_search_home);
    }


    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
    }
}
