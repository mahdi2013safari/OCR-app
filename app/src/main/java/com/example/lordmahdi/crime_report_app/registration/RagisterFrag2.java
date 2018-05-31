package com.example.lordmahdi.crime_report_app.registration;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.lordmahdi.crime_report_app.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class RagisterFrag2 extends Fragment {

    OnDoneClickHandler handler;

    View rootView;
    EditText phoneText, passwordText,emailText;
    Button doneBtn;
    ImageView profileView;
    Uri fileUrl = null;
    private static int GALLERY_REQUEST_CODE = 100;
    private static int CAMERA_REQUEST_CODE = 200;
    Uri uri = null ;
    String picturePath;
    Uri imageUri;
    Bitmap photo;
    String ba1;
    Bundle dataBundle;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    public RagisterFrag2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_ragister_frag2, container, false);
        phoneText = (EditText) rootView.findViewById(R.id.fragment_registration_step2_phone);
        passwordText = (EditText) rootView.findViewById(R.id.fragment_registration_step2_password);
        emailText = (EditText) rootView.findViewById(R.id.fragment_registration_step2_Email);
        doneBtn = (Button) rootView.findViewById(R.id.fragment_registration_step2_btn_Done);
        profileView = (ImageView) rootView.findViewById(R.id.fragment_registration_step2_profile);
        storageReference = FirebaseStorage.getInstance().getReference();
        profileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        dataBundle = new Bundle();
        dataBundle.putInt("step", 2);
        handler = (OnDoneClickHandler) getActivity();

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateForm(phoneText,passwordText)){
                    if(passLenght(passwordText)){
                        send_data_toServerFirebase(imageUri);
                    }
                }
            }
        });

        profileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder imgChoserDialog = new AlertDialog.Builder(getActivity());
                imgChoserDialog.setTitle("Choice Image");
                imgChoserDialog.setCancelable(false);
                imgChoserDialog.setSingleChoiceItems(R.array.image_choser, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0 )
                        {
                            openGallery();
                            dialog.dismiss();
                        }else if(which == 0 ){
                            openCamera();
                            dialog.dismiss();
                        }
                    }
                });
                imgChoserDialog.show();
            }
        });
        return rootView;
    }

    private void send_data_toServerFirebase(final Uri Image){
            StorageReference reference = storageReference.child("ImageProfileUser").child("ImageProfile"+System.currentTimeMillis());
            reference.putFile(Image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri uriImageDownload = taskSnapshot.getMetadata().getDownloadUrl();
                    String string_imgUri = uriImageDownload.toString();
                    dataBundle.putString("phone", phoneText.getText().toString());
                    dataBundle.putString("password", passwordText.getText().toString());
                    dataBundle.putString("email",emailText.getText().toString());
                    dataBundle.putString("imageUri",string_imgUri);
                    handler.OnDoneClick(dataBundle);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("send data FS"," there is ERROR in server side FireBase database = "+e.getMessage());
                }
            });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            //if(uri != null){
            //uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+"/file/image.jpg"));
            imageUri = data.getData();
            photo = (Bitmap) data.getExtras().get("data");
            // Cursor to get image uri to display
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(imageUri,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            profileView.setImageBitmap(photo);
        } else if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            try {
                imageUri = data.getData();
                Bitmap imageBit = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                profileView.setImageBitmap(imageBit);
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    private boolean passLenght(EditText password) {
        String passText = password.getText().toString();
        if ( passText.length() <= 6) {
            passwordText.setError(getString(R.string.err_weak_pass));
            return false;
        }
        return true;
    }

    private void openGallery(){
        Intent gIntent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        gIntent.setType("image/*");
        startActivityForResult(gIntent,GALLERY_REQUEST_CODE);
    }

    private void openCamera(){

        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String time = String.valueOf(System.currentTimeMillis());
        File imageFile = new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+"/file");
        if(!imageFile.exists())
        {
            imageFile.mkdirs();
        }
        uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+"/file/"+time+"image.jpg"));
        i.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        startActivityForResult(i,CAMERA_REQUEST_CODE);
    }

}
