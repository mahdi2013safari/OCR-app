package com.example.lordmahdi.crime_report_app.registration;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.lordmahdi.crime_report_app.Location.GPSTracker;
import com.example.lordmahdi.crime_report_app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RagisterFrag1 extends Fragment {


    OnDoneClickHandler handler;

    View rootView;
    EditText fnameText,lnameText;
    Button locationBtn;
    int chooseLocation=0;
    Double longitude,latitude;
    String ageuser,gender=null;

    public RagisterFrag1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_ragister_frag1, container, false);

        fnameText=(EditText)rootView.findViewById(R.id.fragment_registration_step1_fname);
        lnameText=(EditText)rootView.findViewById(R.id.fragment_registration_step1_lname);
        locationBtn=(Button)rootView.findViewById(R.id.fragment_registration_step1_location_choserBtn);
        RadioButton radioMale = (RadioButton) rootView.findViewById(R.id.radioMale);


        if(radioMale.isSelected()){
            gender = "Male";
        }else {
            gender = "Female";
        }

        handler = (OnDoneClickHandler) getActivity();

        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateForm(fnameText,lnameText)){
                    final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setTitle("choice Location ");
                    alert.setSingleChoiceItems(new String[]{"GPS", "Google Map"}, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            chooseLocation = which;
                        }
                    });
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getLocation();
                        }
                    });
                    alert.show();
                }
            }
        });


        // loop for age Spinner list 15 - 90
        List age = new ArrayList<Integer>();
        for (int i = 15; i <= 90; i++) {
            age.add(Integer.toString(i));
        }
        ArrayAdapter<Integer> spinnerArrayAdapter = new ArrayAdapter<Integer>(
                getActivity(), android.R.layout.simple_spinner_item, age);
        spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );

        Spinner spinner = (Spinner)rootView.findViewById(R.id.fragment_ragisteration_step1_spinner_age);
        spinner.setAdapter(spinnerArrayAdapter);
        ageuser = spinner.getSelectedItem().toString();

        return rootView;



    }

    private void getLocation() {
        // Toast.makeText(getActivity(), "getLocationFromGps", Toast.LENGTH_SHORT).show();
        if (chooseLocation == 0) {
            //TODO: get location by GPS

            GPSTracker myGps = new GPSTracker(getActivity());
            myGps.getLocation();
            if (myGps.canGetLocation) {
                latitude = myGps.getLatitude();
                longitude = myGps.getLongitude();
                Bundle dataBundle = new Bundle();
                dataBundle.putString("fname", fnameText.getText().toString());
                dataBundle.putString("lname", lnameText.getText().toString());
                dataBundle.putDouble("lat", latitude);
                dataBundle.putDouble("longt", longitude);
                dataBundle.putString("ageUser",ageuser);
                dataBundle.putString("gender",gender);
                dataBundle.putInt("step", 1);
                Toast.makeText(getActivity(), "step1", Toast.LENGTH_SHORT).show();
                handler.OnDoneClick(dataBundle);

            } else{
                myGps.showSettingsAlert();
            }
        } else {
            //TODO: get location by map
        }
    }// end of method getLocation()

    private boolean validateForm(EditText... inputs){
        for(int i=0;i<inputs.length;i++){
            if(inputs[i].getText().toString().isEmpty()){
                inputs[i].setError("field is required");
                inputs[i].requestFocus();
                return false;
            }
        }
        return true;
    }

}
