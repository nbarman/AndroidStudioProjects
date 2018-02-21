package com.eazyedu;

import android.support.design.widget.FloatingActionButton;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import android.widget.TextView;

public class CustomSearch extends AppCompatActivity {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        //setupViewPager(viewPager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }); */

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_tabbed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search_items) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        private final String UNIVERSITY_DETAILS = "University Details";
        private final String UNIVERSITY_LOCATION = "Location Details";

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
