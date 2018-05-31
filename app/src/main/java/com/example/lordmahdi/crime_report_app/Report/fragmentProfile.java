package com.example.lordmahdi.crime_report_app.Report;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.AlteredCharSequence;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lordmahdi.crime_report_app.R;
import com.example.lordmahdi.crime_report_app.Report.classes.UserInfo;
import com.example.lordmahdi.crime_report_app.helper.DbHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rey.material.app.Dialog;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragmentProfile extends Fragment {

    transferData data;
    View rootView;
    Bundle bundle;
    DbHelper helper ;
    String idUser = null;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("User");
    String sphone,sname,slastname;
    EditText name,lastname,phone;
    TextView txtFullname;
    ProgressDialog progressDialog;
    UserInfo userInfo;
    public fragmentProfile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_fragment_profile, container, false);
        bundle = new Bundle();
        data = (transferData) getActivity();
        userInfo = new UserInfo();
        helper = new DbHelper(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Witing for update..");
        name = (EditText) rootView.findViewById(R.id.txt_profile_fname);
        lastname = (EditText) rootView.findViewById(R.id.txt_profile_lname);

        txtFullname = (TextView) rootView.findViewById(R.id.txt_profile_fullname);
        ImageView imageProfile = (ImageView) rootView.findViewById(R.id.img_profile_user);
        Button btnResetPassword = (Button) rootView.findViewById(R.id.btn_profile_resetPassword);
        bundle.putInt("step",10);
        //=================================================
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.show();
                dialog.setTitle("Change Password and Email");
                dialog.setMessage("Dear User You Can not change your Email, This is our policy. \n but You can reset your Password By your email address"+
                "( Click OK to send you email to varification reset your new password)");
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO get email form DB helper SQLite
                        String emaildb = helper.getEmailLast();
                        bundle.putString("emailreset",emaildb);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }
        });
        //===============================================================
        try {
            Cursor cursor = helper.getLast();
            String fname = cursor.getString(cursor.getColumnIndex("name"));
            String lname = cursor.getString(cursor.getColumnIndex("lname"));
            txtFullname.setText(fname + ", " + lname);
            String image = cursor.getString(cursor.getColumnIndex("image"));
            Picasso.with(getActivity()).load(Uri.parse(image)).resize(50,50).centerCrop().into(imageProfile);
            imageProfile.setImageURI(Uri.parse(image));
        }catch (Exception ex){
            Log.e("Error Profile User = ",ex.getMessage());
        }
        //===================================================================
        rootView.findViewById(R.id.btn_finish_edit_profileUser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sname = name.getText().toString();
                slastname = lastname.getText().toString();
                update_profile_date_dbHelper();
            }
        });
        return rootView;
    }

    /*_id TEXT PRIMARY KEY,fname TEXT, lname TEXT, phone TEXT, password TEXT,profile TEXT" +
                ",longt TEXT,lat TEXT,age TEXT,gender TEXT*/

    public void updateFirebase(final String fname,final String lname,final String phone){
        progressDialog.show();
        final String phonee = helper.getPhoneLastUserDBlocal();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DatabaseReference mRef =  database.getReference().child("User").child(phonee);
                if(fname != null) {
                    mRef.child("fname").setValue(fname);
                }if(lname != null){
                    mRef.child("lname").setValue(lname);
                }else{
                    Toast.makeText(getActivity(), "Empty fiald!!", Toast.LENGTH_SHORT).show();
                }
                Log.e("Update firebase","Successfully update it");
                Toast.makeText(getActivity(), "Successfully update it", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Update firebasee ","Some Erroe happen"+databaseError.getMessage());
            }
        });
    }

    private void update_profile_date_dbHelper(){
        ContentValues value = new ContentValues();
        value.put("name",sname);
        value.put("lname",slastname);
        int i = helper.updateTable("User",value, String.valueOf(helper.getPhoneLastUserDBlocal()));
        if(i == 0){
            Log.e("DB helper UserProfile","NOT update it something wrong with DB helper");
        }else{
            updateFirebase(sname,slastname,sphone);
            Log.e("DB helper UserProfile","A user Successfull update it");
        }
    }






}
