package com.example.lordmahdi.crime_report_app.Report;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.example.lordmahdi.crime_report_app.Location.GPSTracker;
import com.example.lordmahdi.crime_report_app.R;
import com.example.lordmahdi.crime_report_app.Report.classes.Constant;
import com.example.lordmahdi.crime_report_app.helper.ParseImage_endcode;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static java.lang.System.currentTimeMillis;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragmentNewReport extends Fragment {


    private static final int CAMERA_REQUEST = 1011;
    private static final int GALLERY_REQUEST = 100;
    View rootView;
    EditText title , descript;
    ImageButton imageShow;
    transferData data;
    Uri filePath = null ;
    DatabaseReference database ;
    //DatabaseReference myRef = database.getReference("Reports");
    Bundle bundle;
    ParseImage_endcode endcodeImage ;
    String BitmapImage;
    UploadTask uploadTask;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference,imgRef;
    Button getlocation;
    String sUri=null;
    Double latitude,longitude;
    int chooseLocation=0;
    AlertDialog.Builder alert ;
    ViewDialogRecord recordDialog;
    Uri uriImageDownload;
    final int RC_TAKE_PHOTO = 1;
    ProgressDialog dialog;
    public fragmentNewReport() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_fragment_new_report, container, false);
        recordDialog = new ViewDialogRecord();
        //storageReference = FirebaseStorage.getInstance().getReference("photos/");
        endcodeImage = new ParseImage_endcode();
        data = (transferData) getActivity();
        bundle = new Bundle();
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Witing...");
        storageReference = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance().getReference("new_report");
        title = (EditText) rootView.findViewById(R.id.textfield_et_label);
        descript = (EditText) rootView.findViewById(R.id.txtFragDescription);
        imageShow = (ImageButton) rootView.findViewById(R.id.fragImgShow);
        Switch swtichGPS = (Switch) rootView.findViewById(R.id.SwitchGetLocationGPS);
        if(swtichGPS.isChecked()){
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setSingleChoiceItems(R.array.choose_location, 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(which == 0){
                        //TODO get GPS location
                        getLocation();
                    }else if(which == 1)
                    {
                        //TODO get location by google
                        
                    }
                }
            });
        }else{

        }
        bundle.putInt("step",3);

        //send new Report to server firebase*
        FloatingActionButton fab_send_data = (FloatingActionButton) rootView.findViewById(R.id.fab_send_new_report);
        fab_send_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO send new REPORT to server firebase
                send_image_to_server();
                //recordDialog.send_voice_record_toServer();
            }
        });

        FloatingActionButton fab_cancel = (FloatingActionButton) rootView.findViewById(R.id.fab_cancel_new_report);
        fab_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "this feature not Availible yet (Cancel data send)", Toast.LENGTH_SHORT).show();
            }
        });

        ImageButton imgBtn = (ImageButton) rootView.findViewById(R.id.fragImgShow);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        return rootView;
    }

    public void send_image_to_server(){
        dialog.show();
            StorageReference ref_filePath = storageReference.child("image").child("imageReport"+System.currentTimeMillis());
            ref_filePath.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    uriImageDownload = taskSnapshot.getMetadata().getDownloadUrl();
                    String sUri = uriImageDownload.toString();
                    bundle.putString("imageUri",sUri);
                    String tit = title.getText().toString();
                    String desc = descript.getText().toString();
                    bundle.putString("title_new_report",tit);
                    bundle.putString("descript",desc);
                    data.bundleData(bundle);
                    Log.e("download URI"," "+sUri);
                    dialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("Error ", "Send Image to server " + e.getMessage());
                }
            });
        }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK){
            Bitmap imageBit = null;
                //Uri uri = data.getData();
                //String lastPathSegment = uri.getLastPathSegment();
                //send_image_to_server(uri,lastPathSegment);
                //imageBit = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),uri);
                //imageShow.setImageBitmap(imageBit);
                //BitmapImage = endcodeImage.getpath(imageBit);
                //bundle.putString("imageReportCamera",BitmapImage);
                //this.data.bundleData(bundle);
        }else if(requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK) {
            try {
                filePath = data.getData();
                Log.e("filePath image"," = "+filePath);
                Bitmap imageBit = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                BitmapImage = endcodeImage.getpath(imageBit);
                imageShow.setImageBitmap(imageBit);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void openCamera(){
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String time = String.valueOf(currentTimeMillis());
        File imageFile = new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+"/file");
        if(!imageFile.exists())
        {
            imageFile.mkdirs();
        }
        filePath = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+"/file/"+time+"image.jpg"));
        //bundle.putString("UriImage", String.valueOf(uri));
        i.putExtra(MediaStore.EXTRA_OUTPUT,filePath);
        startActivityForResult(i,CAMERA_REQUEST);
        //data.bundleData(bundle);
    }

    private void openGallery(){
        Intent gIntent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        gIntent.setType("image/*");
        startActivityForResult(gIntent,GALLERY_REQUEST);
    }

    public void show_image(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference m = ref.child("new_report").child("Report1").child("ImageEncode");
        m.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                Log.e("ImageBitmap = "," "+value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getLocation() {
        if (chooseLocation == 0) {
            //TODO: get location by GPS
            GPSTracker myGps = new GPSTracker(getActivity());
            myGps.getLocation();
            if (myGps.canGetLocation) {
                latitude = myGps.getLatitude();
                longitude = myGps.getLongitude();
            } else{
                myGps.showSettingsAlert();
            }
        } else {
            //TODO: get location by map
        }
    }// end of method getLocation()

    class ViewDialogRecord {

        private final static String LOG_TAG = "Record_LOG";
        MediaRecorder mRecorder;
        public String mFileName ;
        public String mFileNameserver;
        public Dialog dialog;
        String nameRecord = "/Audio"+System.currentTimeMillis()%50+".3gp";
        public void showDialog(Activity activity){
            dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.record_report_dialog);
            mFileName = Environment.getExternalStorageDirectory().getAbsolutePath()+nameRecord;
            Log.e("mFileName uri ","= "+mFileName);
            final TextView text = (TextView) dialog.findViewById(R.id.txt_record_state);

            final CircularProgressButton dialogButton = (CircularProgressButton) dialog.findViewById(R.id.btnWithText);
            dialogButton.setIndeterminateProgressMode(true);
            dialogButton.setProgress(0);
            dialogButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        dialogButton.setProgress(50);
                        text.setText("Recording....");
                        dialogButton.setProgress(1);
                        startRecording();
                    }else if(event.getAction() == MotionEvent.ACTION_UP){
                        text.setText("Stop Recording..");
                        stopRecording();
                        dialogButton.setProgress(100);
                        dialog.dismiss();
                    }
                    return false;
                }
            });
            dialog.show();
        }

        private void startRecording() {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(mFileName);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            try {
                mRecorder.prepare();
            } catch (IOException e) {
                Log.e(LOG_TAG, "prepare() failed");
            }
            mRecorder.start();
        }
        private void stopRecording() {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }

        public void send_voice_record_toServer(){

            mFileNameserver = Environment.getExternalStorageDirectory().getAbsolutePath()+nameRecord;
            Log.e("mFilename record"," = "+mFileNameserver);
            StorageReference reference = storageReference.child("Audio").child("new_audio.3pg");
            Uri uri = Uri.fromFile(new File(mFileNameserver));
            if(uri != null) {
                reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri uri = taskSnapshot.getMetadata().getDownloadUrl();
                        String sUriVoice = uri.toString();
                        bundle.putString("voiceUri", sUriVoice);

                        Log.e("Upload Voice", "Successfull upload it");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Upload voice", "Not upload Voice record");
                    }
                });
            }else {
                Log.e("URI record","is empty");
            }
        }
    }
}


