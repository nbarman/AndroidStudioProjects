package com.eazyedu.beans;

import java.util.List;
import java.util.Map;

/**
 * Created by namitmohanbarman on 2/22/18.
 */

public class UniversityLocationBean {

   private String locCity;

   private String locState;

    private String locFullAddr;

    private List<Map<String, String>> areaLodging;

    private List<Map<String, String>> areaCuisine;

    private String locWeather;

    public String getLocWeather() {
        return locWeather;
    }

    public void setLocWeather(String locWeather) {
        this.locWeather = locWeather;
    }

    public String getLocCity() {
        return locCity;
    }

    public void setLocCity(String locCity) {
        this.locCity = locCity;
    }

    public String getLocState() {
        return locState;
    }

    public void setLocState(String locState) {
        this.locState = locState;
    }

    public String getLocFullAddr() {
        return locFullAddr;
    }

    public void setLocFullAddr(String locFullAddr) {
        this.locFullAddr = locFullAddr;
    }

    public List<Map<String, String>> getAreaLodging() {
        return areaLodging;
    }

    public void setAreaLodging(List<Map<String, String>> areaLodging) {
        this.areaLodging = areaLodging;
    }

    public List<Map<String, String>> getAreaCuisine() {
        return areaCuisine;
    }

    public void setAreaCuisine(List<Map<String, String>> areaCuisine) {
        this.areaCuisine = areaCuisine;
    }

}
