package com.example.lordmahdi.crime_report_app.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by king pc on 5/4/2017.
 */

public class ParseImage_endcode {


    Context context ;
    public String getpath(Bitmap bitmap){
        if(bitmap!=null){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
            byte[] imageByte = baos.toByteArray();
            String encodeImage = Base64.encodeToString(imageByte,Base64.DEFAULT);
            return encodeImage;
        }else{
            return "";
        }
    }


    public static Bitmap decodeFromFirebaseBase64(String image) throws IOException {
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }





}
