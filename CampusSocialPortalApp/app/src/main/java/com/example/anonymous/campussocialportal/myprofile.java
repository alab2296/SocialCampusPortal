package com.example.anonymous.campussocialportal;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class myprofile extends AppCompatActivity {





// User Session Manager Class
        UserSessionManager session;

        // Button Logout


        @Override
        public void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_myprofile);

            // Session class instance
            session = new UserSessionManager(getApplicationContext());

            TextView lblName = (TextView) findViewById(R.id.lblName);
            TextView lblEmail = (TextView) findViewById(R.id.lblEmail);
            TextView lblId = (TextView) findViewById(R.id.lblId);
            TextView lblFname = (TextView) findViewById(R.id.lblFname);
            TextView lblPhone = (TextView) findViewById(R.id.lblPhone);
            TextView lblImg = (TextView) findViewById(R.id.lblImg);
            TextView lblAddress = (TextView) findViewById(R.id.lblAddress);
            TextView lblRegno = (TextView) findViewById(R.id.lblRegno);
            TextView lblRollno = (TextView) findViewById(R.id.lblRollno);

            // Button logout


         //   Toast.makeText(getApplicationContext(),
                    //"User Login Status: " + session.isUserLoggedIn(),
                   // Toast.LENGTH_LONG).show();



            // Check user login
            // If User is not logged in , This will redirect user to LoginActivity.


         // if(session.checkLogin())
         //   finish();

            // get user data from session
            HashMap<String, String> user = session.getUserDetails();

            // get name
            String name = user.get(UserSessionManager.KEY_NAME);

            // get email
            String email = user.get(UserSessionManager.KEY_EMAIL);
            String fname = user.get(UserSessionManager.KEY_FNAME);
            String rollno = user.get(UserSessionManager.KEY_ROLLNO);
            String regno = user.get(UserSessionManager.KEY_REGNO);
            String address = user.get(UserSessionManager.KEY_ADDRESS);
            String phone = user.get(UserSessionManager.KEY_PHONE);
            String img = user.get(UserSessionManager.KEY_IMG);
            String id = user.get(UserSessionManager.KEY_ID);

            // Show user data on activity
            lblName.setText(Html.fromHtml("Name:   <center><b>" + name + "</b></center>"));
            lblEmail.setText(Html.fromHtml("Email:  <center> <b>" + email + "</b></center>"));
            lblId.setText(Html.fromHtml("Id:  <center> <b>" + id + "</b></center>"));
            lblFname.setText(Html.fromHtml("FatherName:  <center> <b>" + fname + "</b></center>"));
            lblRegno.setText(Html.fromHtml("RegNo:  <center> <b>" + regno + "</b></center>"));
            lblRollno.setText(Html.fromHtml("RollNo:  <center> <b>" + rollno + "</b></center>"));
            lblImg.setText(Html.fromHtml("Img:  <center> <b>" + img + "</b></center>"));
            lblAddress.setText(Html.fromHtml("Address: <center>  <b>" + address + "</b></center>"));
            lblPhone.setText(Html.fromHtml("Phone:   <center><b>" + phone + "</b></center>"));



        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
        }

}
