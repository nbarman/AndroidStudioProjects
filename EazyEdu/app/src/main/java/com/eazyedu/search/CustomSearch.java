package com.eazyedu.search;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.eazyedu.R;
import com.eazyedu.beans.UniversityDetailsBean;
import com.eazyedu.beans.UniversityLocationBean;
import com.eazyedu.frag.UniversityDetailsFragment;
import com.eazyedu.frag.UniversityLocationFragment;
import com.eazyedu.utilities.EazyUtils;
import com.google.api.client.json.Json;
import com.google.api.client.json.JsonObjectParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CustomSearch extends AppCompatActivity{
    private  SectionsPagerAdapter mSectionsPagerAdapter;
    private  ViewPager mViewPager;
    private  TabLayout tabLayout;

    private UniversityDetailsBean univDetailsBean;
    private UniversityLocationBean univLocationBean;
    private final  String serialClassID = "CUSTOM_SEARCH";
    private  ProgressBar pBar;
    private  String pQeury = "";
    private  CustomSearchEngine cSearchEngine;
    private  int[] tabIcons = {
            R.drawable.ic_university_details,
            R.drawable.ic_university_location
    };

    public CustomSearch(){}

    @Override
    @JavascriptInterface
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        pBar = (ProgressBar) findViewById(R.id.sLoading);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        UniversityDetailsFragment frag = new UniversityDetailsFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentParentViewGroup, new UniversityDetailsFragment())
                .commit();


        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());

        SearchView sv = (SearchView)findViewById(R.id.searchView);
        sv.setSearchableInfo(searchableInfo);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.search_items) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
        if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if(pQeury!=null && !pQeury.equalsIgnoreCase(query)) {
                pBar.setVisibility(ProgressBar.VISIBLE);

                setpQeury(query);
                String queries[] = {query};
                try {
                    //Calls the Custom Search Engine for the search async
                    cSearchEngine = new CustomSearchEngine(CustomSearch.this);
                   String result =  cSearchEngine.execute(queries).get();
                } catch(InterruptedException | ExecutionException exception){
                    Log.e("FATAL!", exception.getMessage());

                }
            }
        }
    }

    /**
     * This takes place after the CustomSearchEngine retrieves all the information and then the Fragments are being constructed
     * @param errorCode
     */
    public void onCallBack(String errorCode){
        pBar.setVisibility(ProgressBar.INVISIBLE);
        if(errorCode.equalsIgnoreCase("00")) {
            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.viewpager);
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
            mViewPager.setAdapter(mSectionsPagerAdapter);
            tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);
            setupTabIcons();
        }
    }


    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
    }


    public String getpQeury() {
        return pQeury;
    }

    public void setpQeury(String pQeury) {
        this.pQeury = pQeury;
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        private final String UNIVERSITY_DETAILS = "University";
        private final String UNIVERSITY_LOCATION = "Place";

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int index) {
            switch (index) {
                case 0:
                    // University Details Fragment
                    UniversityDetailsFragment univDetailsFrag = new UniversityDetailsFragment();
                    Bundle univDetailsBundle = new Bundle();
                    String customUnivDetailsBean = EazyUtils.getGsonParser().toJson(getUnivDetailsBean());
                    univDetailsBundle.putString("UNIV_DETAILS_BEAN",customUnivDetailsBean);
                    univDetailsFrag.setArguments(univDetailsBundle);
                    return univDetailsFrag;

                case 1:
                    // University Location Details Fragment
                    UniversityLocationFragment univLocationFrag = new UniversityLocationFragment();
                    return univLocationFrag;

            }

            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return UNIVERSITY_DETAILS;
                case 1:
                    return UNIVERSITY_LOCATION;
            }
            return null;
        }
    }
    /**
     * End of Fragments
     */

    public UniversityDetailsBean getUnivDetailsBean() {
        return univDetailsBean;
    }

    public void setUnivDetailsBean(UniversityDetailsBean univDetailsBean) {
        this.univDetailsBean = univDetailsBean;
    }

    public UniversityLocationBean getUnivLocationBean() {
        return univLocationBean;
    }

    public void setUnivLocationBean(UniversityLocationBean univLocationBean) {
        this.univLocationBean = univLocationBean;
    }
}
