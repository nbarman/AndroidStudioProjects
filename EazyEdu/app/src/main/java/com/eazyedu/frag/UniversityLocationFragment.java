package com.eazyedu.frag;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eazyedu.R;
import com.eazyedu.beans.UniversityLocationBean;

/**
 * Created by namitmohanbarman on 2/20/18.
 */

public class UniversityLocationFragment extends Fragment{
    private Bundle locDetailsBundle;
    private UniversityLocationBean uLocBean;
    public UniversityLocationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View locDetailsView  = inflater.inflate(R.layout.layout_univ_location_fragment, container, false);
        //TextView locDetailsLabel = locDetailsView.findViewById(R.id.univLocationLabel);

        //locDetailsLabel.setText("Location Details");
        locDetailsBundle = getArguments();

        TextView stateName = locDetailsView.findViewById(R.id.state_name);
        stateName.setText(locDetailsBundle.getString("LOC_STATE"));

        TextView cityName = locDetailsView.findViewById(R.id.city_name);
        stateName.setText(locDetailsBundle.getString("LOC_CITY"));

        TextView fullAddr = locDetailsView.findViewById(R.id.loc_addr);
        stateName.setText(locDetailsBundle.getString("LOC_FULL_ADDR"));

        return locDetailsView;
    }

}