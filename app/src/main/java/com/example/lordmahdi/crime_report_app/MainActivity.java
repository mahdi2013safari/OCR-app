package com.example.lordmahdi.crime_report_app;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lordmahdi.crime_report_app.Report.classes.frag_home_class_items;
import com.example.lordmahdi.crime_report_app.Report.mainReport;
import com.example.lordmahdi.crime_report_app.helper.DbHelper;
import com.example.lordmahdi.crime_report_app.helper.Notific;
import com.example.lordmahdi.crime_report_app.registration.Ragistration;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    final static String TAG = "com.example.lordmahdi.crime_report_app";
    EditText email , password;
    FirebaseAuth.AuthStateListener mAuthListner;
    String txtphone = "";
    String txtpassword = "";
    ProgressDialog progressDialog;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Reports");
    DbHelper helper;
    boolean ischeckreply;
    boolean doubleBackToExitPressedOnce = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        helper = new DbHelper(this);
        progressDialog.setMessage("Sign in ...");
        email = (EditText) findViewById(R.id.activity_login_phone);
        password = (EditText) findViewById(R.id.activity_login_password);
        showNotification();
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
               if(firebaseAuth.getCurrentUser() != null){
                   Intent intent = new Intent(MainActivity.this, mainReport.class);
                   startActivity(intent);
               }
            }
        };

        Button singup = (Button) findViewById(R.id.login_btn_signUp);
        singup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Ragistration.class);
                startActivity(intent);
            }
        });

        Button signin = (Button) findViewById(R.id.login_btn_login);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtphone = email.getText().toString();
                txtpassword = password.getText().toString();
                Log.e("Email = ",txtphone);
                Log.e("Password = ",txtpassword);
                Sign_in_user(txtphone,txtpassword);
                Log.e("firebase Auth","Email and passowrd correct successfully sign in");
                //create_user_auth(txtphone,txtpassword);
            }
        });
        DatabaseReference ref = database.getReference("report").child("new_report");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                     frag_home_class_items items = snapshot.getValue(frag_home_class_items.class);
                     Log.e("title",items.getTitle());
                     Log.e("desctio",items.getDescription());
                     Log.e("chck replay"," = "+items.isCheckReply());
                     Log.e("image"," = "+items.getImage());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListner);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListner != null){
            //mAuth.removeAuthStateListener(mAuthListner);
            //mAuth.getInstance().signOut();
        }
    }

    public void Sign_in_user(String temail , String tpassword){
        if(validateForm(email,password)){
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(temail,tpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        //TODO successful singin
                        FirebaseUser user = mAuth.getCurrentUser();
                        progressDialog.dismiss();
                        Log.d("MainActivity","successfully LOGIN");
                        Intent report = new Intent(MainActivity.this,mainReport.class);
                        startActivity(report);
                    }else{
                        //TODO Authontication Faild
                        Log.e("MainActivity","Sign in Faild");
                        progressDialog.dismiss();
                    }
                }
            });
        }
    }



    private boolean validateForm(EditText... inputs) {
        for (int i = 0; i < inputs.length; i++) {
            if (inputs[i].getText().toString().isEmpty()) {
                inputs[i].setError("field is required");
                inputs[i].requestFocus();
                return false;
            }
        }

        return true;
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
                        Notific.notifyThis(items.getTitle(),"New message for Admin "+items.getReplay(),MainActivity.this);
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
