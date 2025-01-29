package com.example.anonymous.campussocialportal;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class Comments {

    private String message, user_id,username,userimage;

    private Date timestamp;


    public Comments(){


    }

    public Comments(String message, String user_id, Date timestamp,String username,String userimage) {
        this.message = message;
        this.user_id = user_id;

        this.timestamp = timestamp;

        this.username = username;
        this.userimage=userimage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String getUserimage() {
        return userimage;
    }


    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }






    public String getUser_id() {
        return user_id;
    }


    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }


    public String getUsername() {
        return username;
    }
    public void setUsername(String username){
        this.username=username;
    }



    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }




}
