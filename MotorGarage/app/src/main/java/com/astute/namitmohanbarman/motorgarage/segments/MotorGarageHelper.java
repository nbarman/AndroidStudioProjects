package com.astute.namitmohanbarman.motorgarage.segments;

/**
 * Created by namitmohanbarman on 10/14/15.
 */


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


public class MotorGarageHelper extends FragmentPagerAdapter {

    public MotorGarageHelper(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // register car fragment activity
                return new RegisterVehicleFrag();
            case 1:
                // Buy/ Sell fragment activity
                return new BuySellCarFrag();
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }
}
