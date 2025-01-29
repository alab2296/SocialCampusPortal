package com.example.anonymous.campussocialportal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Selection extends AppCompatActivity {
    ImageView r1,r2,r3,r4;
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);



        r1= (ImageView) findViewById(R.id.route1);
        r2= (ImageView) findViewById(R.id.route2);
        r3= (ImageView) findViewById(R.id.route3);
        r4= (ImageView) findViewById(R.id.route4);
        getSupportActionBar().setTitle("Select Route");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        r1.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //TODO Auto-generated method stub
                //    imageView.setImageResource(R.drawable.button);
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                   // Log.d("TouchTest", "Touch down");
                    r1.setImageResource(R.drawable.bluebus);
                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    r1.setImageResource(R.drawable.orangebus);
                   // Log.d("TouchTest", "Touch up");
                }

                return false;
            }
        });
        r2.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //TODO Auto-generated method stub
                //    imageView.setImageResource(R.drawable.button);
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    // Log.d("TouchTest", "Touch down");
                    r2.setImageResource(R.drawable.bluebus);
                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    r2.setImageResource(R.drawable.orangebus);
                    // Log.d("TouchTest", "Touch up");
                }
                return false;
            }
        });
        r3.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //TODO Auto-generated method stub
                //    imageView.setImageResource(R.drawable.button);
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    // Log.d("TouchTest", "Touch down");
                    r3.setImageResource(R.drawable.bluebus);
                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    r3.setImageResource(R.drawable.orangebus);
                    // Log.d("TouchTest", "Touch up");
                }
                return false;
            }
        });
        r4.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //TODO Auto-generated method stub
                //    imageView.setImageResource(R.drawable.button);
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    // Log.d("TouchTest", "Touch down");
                    r4.setImageResource(R.drawable.bluebus);
                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    r4.setImageResource(R.drawable.orangebus);
                    // Log.d("TouchTest", "Touch up");
                }
                return false;
            }
        });

    }

    public void map(View v) {
        switch (v.getId()) {
            case R.id.route1:
                Intent r1 = new Intent(Selection.this, MapsActivity.class);
                r1.putExtra("route", "1");
                startActivity(r1);


                break;
            case R.id.route2:
                Intent r2 = new Intent(Selection.this, MapsActivity.class);
                r2.putExtra("route", "2");
                startActivity(r2);


                break;
            case R.id.route3:
                Intent r3 = new Intent(Selection.this, MapsActivity.class);
                r3.putExtra("route", "3");
                startActivity(r3);


                break;
            case R.id.route4:
                Intent r4 = new Intent(Selection.this, MapsActivity.class);
                r4.putExtra("route", "4");
                startActivity(r4);


                break;
            default:
        }

    }
}

