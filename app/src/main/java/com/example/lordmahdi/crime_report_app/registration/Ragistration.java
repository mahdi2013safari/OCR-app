package com.example.lordmahdi.crime_report_app.registration;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lordmahdi.crime_report_app.MainActivity;
import com.example.lordmahdi.crime_report_app.R;
import com.example.lordmahdi.crime_report_app.Report.mainReport;
import com.example.lordmahdi.crime_report_app.helper.DbHelper;
import com.example.lordmahdi.crime_report_app.helper.checkInternet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class Ragistration extends AppCompatActivity implements OnDoneClickHandler {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    private FirebaseAuth mAuth;
    RagisterFrag1 FragmentStep1;
    RagisterFrag2 FragmentStep2;
    String fname, lname, phone, password,imageProfile,userAge,email,gender=null;
     String UserID=null;
    double lat, longt;
    TextView connecti;
    checkInternet checkNet;
    ProgressDialog progressDialog;
    DbHelper helper;
    String helperPhone;

    public Ragistration() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ragistration);
        connecti = (TextView) findViewById(R.id.txtConnection22);
        FragmentStep1 = new RagisterFrag1();
        FragmentStep2 = new RagisterFrag2();
        helper = new DbHelper(this);
        changeFragment(FragmentStep1);
        UserID = "user"+System.currentTimeMillis()%50;
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Witing insert data your details ..");
        checkNet = new checkInternet();
        mAuth = FirebaseAuth.getInstance();

        if(isConnected()){
            connecti.setBackgroundColor(0xFF00CC00);
            connecti.setText("You are conncted");
        }
        else{
            connecti.setText("You are NOT conncted");
        }
    }

    public void changeFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_ragistration, fragment).commit();
    }
    @Override
    public void OnDoneClick(Bundle data) {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(Calendar.getInstance().getTime());
        if (data.getInt("step") == 1) {
            fname = data.getString("fname");
            lname = data.getString("lname");
            lat = data.getDouble("lat");
            longt = data.getDouble("longt");
            userAge = data.getString("ageUser");
            gender = data.getString("gender");
            changeFragment(FragmentStep2);
        } else if(data.getInt("step")==2){
            phone = data.getString("phone");
            password = data.getString("password");
            email= data.getString("email");
            imageProfile = data.getString("imageUri");
            insert_data_to_sqliteDB(email,fname,lname,password,phone,imageProfile);
        }
    }

    private void create_user_auth(String email,String password){
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(Ragistration.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent i = new Intent(Ragistration.this,mainReport.class);
                    startActivity(i);
                    Log.d("mAuth firebase","create new user in auth fs");
                }else{
                    Log.w("mAuth firebase","CAN NOT create user ");
                }
            }
        });
    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    private  void send_new_user_data_toServerFirebase(final String fname,final String lname,final String gender,final String age,final String phone,
                                                      final String email,final String password,final String imageUri,final String lat,final String longt){
        helperPhone = helper.getPhoneLastUserDBlocal();
        Log.e("PHhone db = ",helperPhone);
        progressDialog.show();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DatabaseReference mRef =  database.getReference().child("User").child(helperPhone);
                mRef.child("fname").setValue(fname);
                mRef.child("lname").setValue(lname);
                mRef.child("gender").setValue(gender);
                mRef.child("age").setValue(age);
                mRef.child("phone").setValue(phone);
                mRef.child("email").setValue(email);
                mRef.child("password").setValue(password);
                mRef.child("image").setValue(imageUri);
                mRef.child("lat").setValue(lat);
                mRef.child("longt").setValue(longt);
                //TODO User info send to SQLite DB table UserInfo
                Toast.makeText(Ragistration.this, "Inserted new User into DB", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DB firebase User","ERROR Happen = "+databaseError.getMessage());
                progressDialog.dismiss();
            }
        });
    }


    //(_id TEXT,name TEXT,email TEXT,password TEXT)//
    public void insert_data_to_sqliteDB(String email,String name,String lname,String password,String phone,String image){
        try {
            ContentValues values = new ContentValues();
            values.put("email", email);
            values.put("name", name);
            values.put("lname", lname);
            values.put("password", password);
            values.put("phone", phone);
            values.put("image", image);
            long idRow = helper.insertTo("User", values);
            if (idRow != -1) {
                send_new_user_data_toServerFirebase(fname, lname, gender, userAge, phone, email, password, image, String.valueOf(lat), String.valueOf(longt));
                create_user_auth(email, password);
                Log.e("Insert data user sqlite", "Successfully insert into SQLite db userinfo");
            } else {
                Log.e("INSE data user sqlite", "ERROR can not inserted");
            }
        }catch (Exception ex){
            Toast.makeText(Ragistration.this, "Some error = "+ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void getKey(){
        DatabaseReference mRef =  database.getReference().child("User");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    String getKey = dataSnapshot1.getKey();
                    Log.e("the Key",getKey);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ERROR key ",databaseError.getMessage());
            }
        });
    }
}



