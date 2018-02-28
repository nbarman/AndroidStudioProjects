package com.eazyedu;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
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
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CustomSearch extends AppCompatActivity {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private ProgressBar pBar;
    private String pQeury = "";
    CustomSearchEngine cSearchEngine;
    private int[] tabIcons = {
            R.drawable.ic_university_details,
            R.drawable.ic_university_location
    };



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
                    cSearchEngine.execute(queries).get();
                } catch(InterruptedException | ExecutionException exception){
                    //Handle exception here

                }
            }
        }
    }

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
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

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
                    return new UniversityDetailsFragment();
                case 1:
                    // University Location Details Fragment
                    return new UniversityLocationFragment();

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
     * University Details Fragment #1
     * Fragment containing details of the University searched for
     * Details include Location, Tution fees, Ranking
     */
    public static class UniversityDetailsFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public static UniversityDetailsFragment newInstance(int sectionNumber) {
            UniversityDetailsFragment fragment = new UniversityDetailsFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public UniversityDetailsFragment() {
        }
        /*
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
                                View rootView = inflater.inflate(R.layout.content_search_home, container, false);
                                TextView textView = (TextView) rootView.findViewById(R.id.search_items);
                                textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
                                return rootView;
                            }
           */
        }



    /**
     * University Location Details Fragment #2
     * Fragment containing details of the location the University is situated in
     * Details include "Places to see", Airport details
     */
    public static class UniversityLocationFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public static UniversityLocationFragment newInstance(int sectionNumber) {
            UniversityLocationFragment fragment = new UniversityLocationFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public UniversityLocationFragment() {
        }

        /*
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                                     Bundle savedInstanceState) {
                                View rootView = inflater.inflate(R.layout.content_search_home, container, false);
                                TextView textView = (TextView) rootView.findViewById(R.id.search_items);
                                textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
                                return rootView;
                            }
                            */
    }

    /**
     * End of Fragments
     */
}
