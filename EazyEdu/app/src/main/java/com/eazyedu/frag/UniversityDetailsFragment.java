package com.eazyedu.frag;

/**
 * Created by namitmohanbarman on 2/20/18.
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

        TextView univName = univDetailsView.findViewById(R.id.univName);
        TextView univLocation = univDetailsView.findViewById(R.id.univLocation);
        TextView univWebURL = univDetailsView.findViewById(R.id.univUrl);
        TextView univPhNumber = univDetailsView.findViewById(R.id.univPhNumber);

        univDetailsBundle = getArguments();
        String customUnivDetailsBean = univDetailsBundle.getString("UNIV_DETAILS_BEAN");
        univDetailsBean = EazyUtils.getGsonParser().fromJson(customUnivDetailsBean,UniversityDetailsBean.class);

        Log.d("Univ name :: ", univDetailsBean.getUnivName());
        univName.setText(univDetailsBean.getUnivName());
        univLocation.setText(univDetailsBean.getUnivLocation());
        univWebURL.setText(univDetailsBean.getUnivURL());
        univPhNumber.setText(univDetailsBean.getPhoneNumber());

        return univDetailsView;
    }

}