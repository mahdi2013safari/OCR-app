package com.example.lordmahdi.crime_report_app.Report;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.example.lordmahdi.crime_report_app.Location.GPSTracker;
import com.example.lordmahdi.crime_report_app.MainActivity;
import com.example.lordmahdi.crime_report_app.R;
import com.example.lordmahdi.crime_report_app.Report.classes.frag_home_class_items;
import com.example.lordmahdi.crime_report_app.helper.DbHelper;
import com.example.lordmahdi.crime_report_app.helper.Notific;
import com.example.lordmahdi.crime_report_app.helper.checkInternet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class mainReport extends AppCompatActivity implements transferData{

    private static final int CAMERA_REQUEST = 1011;
    FirebaseAuth mAuth;
    fragmentNewReport newReport ;
    fragmentNotification fragNotification;
    fragmentHome fragHome;
    ImageButton imgBtn;
    fragmentProfile fragProfile;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Reports");
    ProgressDialog progressDialog;
    checkInternet checkNet;
    String title ,desc ,profName,profImageUri,profLname = null;
    Double lat , longt;
    DbHelper helper;
    final String rondom = "Report"+System.currentTimeMillis();
    String phonehelper;

    private Handler mHandler ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        mHandler = new Handler();
        checkNet = new checkInternet();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Waiting ..");
//        fragment class definiction
        newReport = new fragmentNewReport();
        fragNotification = new fragmentNotification();
        fragHome = new fragmentHome();
        fragProfile = new fragmentProfile();
//        end fragment dification
        helper = new DbHelper(this);
        changeFragment(fragHome);
        helper.getLastUserInfo();
        showNotification();
        //proDialog = new ProgressDialog(this);
        Cursor cursor =  helper.getLast();
        profName = cursor.getString(cursor.getColumnIndex("name"));
        profLname = cursor.getString(cursor.getColumnIndex("lname"));
        profImageUri = cursor.getString(cursor.getColumnIndex("image"));
        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.nav_bottom);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectFragment(item);
                return true;
            }
        });


    }// end of method onCreate

    private void selectFragment(MenuItem item) {
        // init corresponding fragment
        switch (item.getItemId()) {
            case R.id.menu_home:
                changeFragment(fragHome);
                break;
            case R.id.menu_notify:
                changeFragment(fragNotification);
                break;
            case R.id.menu_newreport:
                changeFragment(newReport);
                break;
            case R.id.action_menu_profile:
                changeFragment(fragProfile);
                break;

        }
    }

    public void changeFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main_report, fragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_menu_signout){
            btnSignOut();
            helper.DropTable("UserInfo");
        }else if(id == R.id.action_menu_profile){
            changeFragment(fragProfile);
        }
        return super.onOptionsItemSelected(item);
    }

    public void btnSignOut(){
        //FirebaseAuth.AuthStateListener mAuthListner;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            FirebaseAuth.getInstance().signOut();
        }else if(user == null){
            Intent i = new Intent(mainReport.this,MainActivity.class);
            startActivity(i);
        }
    }

    @Override
    public void bundleData(Bundle bundle) {
        int step = bundle.getInt("step");
        if(step == 3) {
            title = bundle.getString("title_new_report");
            desc = bundle.getString("descript");
            lat = bundle.getDouble("lat");
            longt = bundle.getDouble("longt");
            String imageUri = bundle.getString("imageUri");
            String voiceUri = bundle.getString("voiceUri");
            if(voiceUri == null){
                voiceUri = "NO VOICE";
            }
            // voice get with image or only voice
            // get location land lantiude
            if (title != null || desc != null) {
                send_new_report_public(title, desc,imageUri,String.valueOf(lat),String.valueOf(longt),profName,profLname,profImageUri);
                send_new_report_user(title, desc,imageUri,String.valueOf(lat),String.valueOf(longt),profName,profLname,profImageUri);
            } else {
                Toast.makeText(this, "titel is null of desc", Toast.LENGTH_SHORT).show();
            }

            //send_image_to_server();
        }else if(step == 10){//this is profile fragment update
            final String EmailReset = bundle.getString("emailreset");
            mAuth.sendPasswordResetEmail(EmailReset).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(mainReport.this, "Please Check your Email "+EmailReset+";", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void send_new_report_public(final String title , final String desc, final String imageUri,final String lat,
                                final String longt,final String profname,final String proflname,final String profimage) {

        phonehelper = helper.getPhoneLastUserDBlocal();
        final Boolean checkrelay = false;
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                DatabaseReference mRef = database.getReference().child("new_report_public").child(rondom);
                mRef.child("title").setValue(title);
                mRef.child("description").setValue(desc);
                mRef.child("image").setValue(String.valueOf(imageUri));
                mRef.child("userim").setValue(String.valueOf(profimage));
                mRef.child("name").setValue(String.valueOf(profname));
                mRef.child("checkReply").setValue(checkrelay);
                mRef.child("last").setValue(String.valueOf(proflname));
                mRef.child("currentdate").setValue(getCurrentDateTime());
                mRef.child("phone").setValue("0790424144");
                mRef.child("lat").setValue(lat);
                mRef.child("longt").setValue(longt);
                // video and voice if null image
                Toast.makeText(mainReport.this, "Successfully send to server ", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(mainReport.this, "Error db firebase" + databaseError, Toast.LENGTH_SHORT).show();
                // progressDialog.setMessage("ERROR with DB firebase");
                progressDialog.dismiss();
            }
        });
    }

    public void send_new_report_user(final String title , final String desc, final String imageUri,final String lat,
                                final String longt,final String profname,final String proflname,final String profimage){

        phonehelper = helper.getPhoneLastUserDBlocal();
        final Boolean checkrelay = false;
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                DatabaseReference mRef =  database.getReference().child("new_report_user").child(helper.getPhoneLastUserDBlocal()).child(rondom);
                mRef.child("title").setValue(title);
                mRef.child("description").setValue(desc);
                mRef.child("image").setValue(String.valueOf(imageUri));
                mRef.child("userim").setValue(String.valueOf(profimage));
                mRef.child("name").setValue(String.valueOf(profname));
                mRef.child("checkReply").setValue(checkrelay);
                mRef.child("last").setValue(String.valueOf(proflname));
                mRef.child("currentdate").setValue(getCurrentDateTime());
                mRef.child("phone").setValue(helper.getPhoneLastUserDBlocal());
                mRef.child("lat").setValue(lat);
                mRef.child("longt").setValue(longt);
                // video and voice if null image
                Toast.makeText(mainReport.this, "Successfully send to server ", Toast.LENGTH_SHORT).show();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(mainReport.this, "Error db firebase"+databaseError, Toast.LENGTH_SHORT).show();
                // progressDialog.setMessage("ERROR with DB firebase");
                progressDialog.dismiss();
            }
        });
    }

    public String getCurrentDateTime(){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(Calendar.getInstance().getTime());
        return date;
    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*Intent intent = new Intent(mainReport.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);*/
    }

    public void showNotification(){
        DatabaseReference ref = database.getReference().child("new_report_user").child(helper.getPhoneLastUserDBlocal());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot d:dataSnapshot.getChildren()){
                    frag_home_class_items items = d.getValue(frag_home_class_items.class);
                    boolean check = items.isCheckReply();
                    if(check){
                        Notific.notifyThis(items.getTitle(),"New message for Admin "+items.getReplay(),mainReport.this);
                        items.setCheckReply(false);
                    }else{
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
