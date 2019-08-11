package com.eazyedu.search;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.eazyedu.frag.UniversityDetailsFragment;
import com.eazyedu.frag.UniversityLocationFragment;
import com.eazyedu.utilities.EazyUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    private CustomSearch cSearch;
    private final String UNIVERSITY_DETAILS = "University";
    private final String UNIVERSITY_LOCATION = "Place";

    public SectionsPagerAdapter(FragmentManager fm, CustomSearch cSearch) {
        super(fm);
        this.cSearch = cSearch;
    }

    public void addFragment(Fragment fragment, String title){
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position){
        return mFragmentTitleList.get(position);
    }
    @Override
    public Fragment getItem(int index) {
       return mFragmentList.get(index);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
