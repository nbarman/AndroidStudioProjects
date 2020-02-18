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
import android.widget.ImageView;
import android.widget.TextView;

import com.eazyedu.R;
import com.eazyedu.beans.UniversityDetailsBean;
import com.eazyedu.search.CustomSearch;
import com.eazyedu.utilities.EazyUtils;

import java.util.Map;

import pl.pawelkleczkowski.customgauge.CustomGauge;

public class UniversityDetailsFragment extends Fragment {

    private Bundle univDetailsBundle;
    UniversityDetailsBean univDetailsBean;
    CustomGauge univRatingGauge;


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
        TextView univWebURL = univDetailsView.findViewById(R.id.univUrl);
        TextView univPhNumber = univDetailsView.findViewById(R.id.univPhNumber);
        TextView univRank = univDetailsView.findViewById(R.id.univRank);
        TextView univRatingTxtView = univDetailsView.findViewById(R.id.univ_rating);
        TextView univFees = univDetailsView.findViewById(R.id.univ_fees);
        TextView student_rating_label = univDetailsView.findViewById(R.id.student_rating);
        ImageView websiteIcon = univDetailsView.findViewById(R.id.img_website_icon);
        ImageView phoneIcon = univDetailsView.findViewById(R.id.img_ph_icon);
        ImageView rankIcon  = univDetailsView.findViewById(R.id.img_rank_icon);
        ImageView ratingIcon = univDetailsView.findViewById(R.id.img_rating_icon);
        ImageView feeIcon = univDetailsView.findViewById(R.id.img_fee_icon);
        univRatingGauge = univDetailsView.findViewById(R.id.univ_rating_gauge);

        String errorCode = "00"; //Indicates successful retrieval

        univDetailsBundle = getArguments();
        String customUnivDetailsBean = univDetailsBundle.getString("UNIV_DETAILS_BEAN");
        errorCode = univDetailsBundle.getString("ERROR_CODE");

        if(errorCode!="10") {

            univDetailsBean = EazyUtils.getGsonParser().fromJson(customUnivDetailsBean, UniversityDetailsBean.class);
            if (univDetailsBean != null) {

                Map<String,String> feesMap = univDetailsBean.getUnivFees();
                String outOfStateFee = "NA";
                if(feesMap!=null && !feesMap.isEmpty()){
                    outOfStateFee = feesMap.get("OutState");
                }
                String univNameStr = univDetailsBean.getUnivName();
                String univURLStr = univDetailsBean.getUnivURL();
                String univPhNoStr = univDetailsBean.getPhoneNumber();
                String univRankStr = univDetailsBean.getUnivRanking();
                String univRating = univDetailsBean.getUnivRating();
                if (univNameStr == null || univRankStr == null || univURLStr == null || univPhNoStr == null || univRankStr ==null ||univRating ==null ||
                        outOfStateFee ==null) {

                    errorCode = "02";
                } else if (univNameStr.isEmpty() || univRankStr.isEmpty() || univURLStr.isEmpty() || univPhNoStr.isEmpty() ||univRankStr.isEmpty()||
                        univRating.isEmpty() || outOfStateFee.isEmpty()) {
                    errorCode = "03";
                } else {// If no Error then display the results
                    univDetailsLabel.setVisibility(View.INVISIBLE);
                    makeThemVisible(univName,univRank,univWebURL,univPhNumber,websiteIcon,
                            phoneIcon,
                            rankIcon ,
                            ratingIcon ,
                            feeIcon,
                            univFees,
                            univRatingTxtView,
                            student_rating_label);
                    univRatingGauge.setVisibility(View.VISIBLE);
                    univName.setText(univNameStr);
                    univWebURL.setText(univURLStr);
                    univPhNumber.setText(univPhNoStr);
                    univRank.setText(univRankStr);
                    univRatingTxtView.setText(univRating);
                    univFees.setText(outOfStateFee);
                    univRatingGauge.setValue(new Float(univRating).intValue());
                }
            } else {
                errorCode = "01";
            }
        }


        //Error Codes Handling
        if(errorCode!="00"){
            makeThemInvisible(univName,univRank,univWebURL,univPhNumber,websiteIcon,
                    phoneIcon,
                    rankIcon ,
                    ratingIcon ,
                    feeIcon,
                    univFees,
                    univRatingTxtView,
                    student_rating_label);
            univRatingGauge.setVisibility(View.INVISIBLE);
        }

        if(errorCode.equals("01")){

            univDetailsLabel.setText(errorCode + " : No Details for the requested University were found");
        } else if(errorCode.equals("02")){
            univDetailsLabel.setText(errorCode + " : Details cannot be retrieved at this time.");
        } else if(errorCode.equals("03")){
            univDetailsLabel.setText(errorCode + " : Some details could not retrieved for the requested University.");
        } else if(errorCode.equals("10")){
            univDetailsLabel.setText(errorCode + " : Search Not Available");
        }

        return univDetailsView;
    }

    private void makeThemInvisible(TextView name, TextView univRank, TextView URL, TextView phNumber,ImageView websiteIcon,
                                   ImageView phoneIcon,
                                   ImageView rankIcon ,
                                   ImageView ratingIcon ,
                                   ImageView feeIcon,
                                   TextView univFees,
                                   TextView univRating,
                                   TextView student_rating_label){
        name.setVisibility(View.INVISIBLE);
        univRank.setVisibility(View.INVISIBLE);
        URL.setVisibility(View.INVISIBLE);
        phNumber.setVisibility(View.INVISIBLE);
        websiteIcon.setVisibility(View.INVISIBLE);
        phoneIcon.setVisibility(View.INVISIBLE);
        rankIcon.setVisibility(View.INVISIBLE);
        ratingIcon.setVisibility(View.INVISIBLE);
        feeIcon.setVisibility(View.INVISIBLE);
        univFees.setVisibility(View.INVISIBLE);
        univRating.setVisibility(View.INVISIBLE);
        student_rating_label.setVisibility(View.INVISIBLE);
    }

    private void makeThemVisible(TextView name, TextView univRank, TextView URL, TextView phNumber,ImageView websiteIcon,
                                 ImageView phoneIcon,
                                 ImageView rankIcon ,
                                 ImageView ratingIcon ,
                                 ImageView feeIcon,
                                 TextView univFees,
                                 TextView univRating,
                                 TextView student_rating_label){
        name.setVisibility(View.VISIBLE);
        univRank.setVisibility(View.VISIBLE);
        URL.setVisibility(View.VISIBLE);
        phNumber.setVisibility(View.VISIBLE);
        websiteIcon.setVisibility(View.VISIBLE);
        phoneIcon.setVisibility(View.VISIBLE);
        rankIcon.setVisibility(View.VISIBLE);
        ratingIcon.setVisibility(View.VISIBLE);
        feeIcon.setVisibility(View.VISIBLE);
        univFees.setVisibility(View.VISIBLE);
        univRating.setVisibility(View.VISIBLE);
        student_rating_label.setVisibility(View.VISIBLE);
    }

}