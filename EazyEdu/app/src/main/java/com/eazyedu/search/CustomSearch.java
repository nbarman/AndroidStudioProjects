package com.eazyedu.search;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
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

    @SuppressLint("JavascriptInterface")
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
                   if(result.equalsIgnoreCase("fail")){
                       onCallBack("10"); // the background task has failed for some reason. We invalidate the search
                   }
                } catch(InterruptedException | ExecutionException exception){
                    Log.e("FATAL!", exception.getMessage());

                }
            } else {
                onCallBack("10");
            }
        }
    }

    /**
     * This takes place after the CustomSearchEngine retrieves all the information and then the Fragments are being constructed
     * @param errorCode
     */
    public void onCallBack(String errorCode){
        pBar.setVisibility(ProgressBar.INVISIBLE);

        if(errorCode.equals("10")){ //Search query is null
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),this);
            mViewPager = (ViewPager) findViewById(R.id.viewpager);

            setupErrorViewPager(mViewPager);
            tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);
            setupTabIcons();

        }
        if(errorCode.equalsIgnoreCase("00")) {
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),this);
            mViewPager = (ViewPager) findViewById(R.id.viewpager);
            setupViewPager(mViewPager);
            //mViewPager.setAdapter(mSectionsPagerAdapter);
            tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);
            setupTabIcons();
        }
    }

    /**
     * Sets up an error pager to show on the fragments
     * @param viewPager
     */
    private void setupErrorViewPager(ViewPager viewPager){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager(),this);

        /**
         * Error for Univ Details
         */
        UniversityDetailsFragment univDetailsFrag = new UniversityDetailsFragment();
        Bundle univDetailsBundle = new Bundle();
        univDetailsBundle.putString("ERROR_CODE","10");
        univDetailsFrag.setArguments(univDetailsBundle);
        adapter.addFragment(univDetailsFrag, "University");

        /**
         * Error for Univ Details
         */
        UniversityLocationFragment univLocationFrag = new UniversityLocationFragment();
        adapter.addFragment(univLocationFrag, "Location");
        viewPager.setAdapter(adapter);

    }

    /**
     * Sets up the fragments along with associated params
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager(),this);
        /**
         * University Details Fragment
         */
                UniversityDetailsFragment univDetailsFrag = new UniversityDetailsFragment();
                Bundle univDetailsBundle = new Bundle();
                String customUnivDetailsBean = EazyUtils.getGsonParser().toJson(getUnivDetailsBean());
                univDetailsBundle.putString("UNIV_DETAILS_BEAN",customUnivDetailsBean);
                univDetailsBundle.putString("ERROR_CODE","00");
                univDetailsFrag.setArguments(univDetailsBundle);
        adapter.addFragment(univDetailsFrag, "University");

        /**
         * University Location Fragment
         */
        UniversityLocationFragment univLocationFrag = new UniversityLocationFragment();
        adapter.addFragment(univLocationFrag, "Location");
        viewPager.setAdapter(adapter);
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
