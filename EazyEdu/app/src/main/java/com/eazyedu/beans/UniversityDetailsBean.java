package com.eazyedu.beans;

import java.util.Map;

/**
 * Created by namitmohanbarman on 2/22/18.
 */

public class UniversityDetailsBean {

    private String univName;
    private String univRanking;
    private Map univFees;
    private String univLocation;

    public String getUnivLocation() {
        return univLocation;
    }

    public void setUnivLocation(String univLocation) {
        this.univLocation = univLocation;
    }

    public String getUnivName() {
        return univName;
    }

    public void setUnivName(String univName) {
        this.univName = univName;
    }

    public String getUnivRanking() {
        return univRanking;
    }

    public void setUnivRanking(String univRanking) {
        this.univRanking = univRanking;
    }

    public Map getUnivFees() {
        return univFees;
    }

    public void setUnivFees(Map univFees) {
        this.univFees = univFees;
    }


}
