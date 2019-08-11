package com.eazyedu.frag;

/**
 * Created by namitmohanbarman on 2/20/18.
 * Error Codes:
 * 00 : OK
 * 01: UnivBean is null
 * 02 : One or more bean fields are null
 * 03: One or more bean fields are empty
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eazyedu.R;
import com.eazyedu.beans.UniversityDetailsBean;
import com.eazyedu.search.CustomSearch;
import com.eazyedu.utilities.EazyUtils;

public class UniversityDetailsFragment extends Fragment {

    private Bundle univDetailsBundle;
    UniversityDetailsBean univDetailsBean;


    public UniversityDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Set the view for the fragment
        View univDetailsView =  inflater.inflate(R.layout.layout_univ_details_fragment, container, false);

        TextView univDetailsLabel = univDetailsView.findViewById(R.id.univDetailsLabel);
        TextView univName = univDetailsView.findViewById(R.id.univName);
        TextView univLocation = univDetailsView.findViewById(R.id.univLocation);
        TextView univWebURL = univDetailsView.findViewById(R.id.univUrl);
        TextView univPhNumber = univDetailsView.findViewById(R.id.univPhNumber);
        String errorCode = "00"; //Indicates successful retrieval

        univDetailsBundle = getArguments();
        String customUnivDetailsBean = univDetailsBundle.getString("UNIV_DETAILS_BEAN");
        errorCode = univDetailsBundle.getString("ERROR_CODE");

        if(errorCode!="10") {

            univDetailsBean = EazyUtils.getGsonParser().fromJson(customUnivDetailsBean, UniversityDetailsBean.class);
            if (univDetailsBean != null) {

                String univNameStr = univDetailsBean.getUnivName();
                String univLocStr = univDetailsBean.getUnivLocation();
                String univURLStr = univDetailsBean.getUnivURL();
                String univPhNoStr = univDetailsBean.getPhoneNumber();
                if (univNameStr == null || univLocStr == null || univURLStr == null || univPhNoStr == null) {

                    errorCode = "02";
                } else if (univNameStr.isEmpty() || univLocStr.isEmpty() || univURLStr.isEmpty() || univPhNoStr.isEmpty()) {
                    errorCode = "03";
                } else {
                    makeThemVisible(univName,univLocation,univWebURL,univPhNumber);
                    univName.setText(univNameStr);
                    univLocation.setText(univLocStr);
                    univWebURL.setText(univURLStr);
                    univPhNumber.setText(univPhNoStr);
                }
            } else {
                errorCode = "01";
            }
        }


        //Error Codes Handling
        if(errorCode!="00"){
            makeThemInvisible(univName,univLocation,univWebURL,univPhNumber);
        }

        if(errorCode.equals("01")){

            univDetailsLabel.setText(errorCode + " : No Details for the requested University were found");
        } else if(errorCode.equals("02")){
            univDetailsLabel.setText(errorCode + " : Details cannot be retrieved at this time.");
        } else if(errorCode.equals("03")){
            univDetailsLabel.setText(errorCode + " : Some details could not retrieved for the requested University.");
        } else if(errorCode.equals("10")){
            univDetailsLabel.setText(errorCode + " : Search Invalid");
        }

        return univDetailsView;
    }

    private void makeThemInvisible(TextView name, TextView location, TextView URL, TextView phNumber){
        name.setVisibility(View.INVISIBLE);
        location.setVisibility(View.INVISIBLE);
        URL.setVisibility(View.INVISIBLE);
        phNumber.setVisibility(View.INVISIBLE);
    }

    private void makeThemVisible(TextView name, TextView location, TextView URL, TextView phNumber){
        name.setVisibility(View.VISIBLE);
        location.setVisibility(View.VISIBLE);
        URL.setVisibility(View.VISIBLE);
        phNumber.setVisibility(View.VISIBLE);
    }

}