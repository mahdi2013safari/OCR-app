package com.example.lordmahdi.crime_report_app.Report.classes;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by Lord Mahdi on 11/1/2017.
 */

public class frag_home_class_items {

    public frag_home_class_items(int id, String title, String description, String date, String image) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.currentdate = date;
        this.image = image;
    }

    public frag_home_class_items(){

    }

    int id ;
    String title ;
    String description;
    String currentdate;
    String image;
    String phone;
    boolean checkReply;
    String replay;

    public void setReplay(String replay) {
        this.replay = replay;
    }

    public String getReplay() {

        return replay;
    }

    public void setCheckReply(boolean checkReply) {
        this.checkReply = checkReply;
    }

    public boolean isCheckReply() {
        return checkReply;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setCurrentdate(String currentdate) {
        this.currentdate = currentdate;
    }

    public String getCurrentdate() {

        return currentdate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }


}
