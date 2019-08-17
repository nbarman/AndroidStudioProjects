package com.eazyedu.beans;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by namitmohanbarman on 2/22/18.
 */

public class UniversityDetailsBean implements Serializable{

    private static final long serialVersionUID = 1L;
    private String univName;
    private String univRanking;
    private Map univFees;
    private String univLocation;
    private String phoneNumber;
    private String univURL;



    private String univRating;

    public UniversityDetailsBean(){};

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUnivURL() {
        return univURL;
    }

    public void setUnivURL(String univURL) {
        this.univURL = univURL;
    }

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

    public String getUnivRating() {
        return univRating;
    }

    public void setUnivRating(String univRating) {
        this.univRating = univRating;
    }


}
