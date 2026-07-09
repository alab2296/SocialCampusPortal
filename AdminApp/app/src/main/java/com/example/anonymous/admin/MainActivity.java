package com.example.anonymous.admin;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import android.support.annotation.NonNull;

public class MainActivity extends Activity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    final static int PICK_PDF_CODE = 2342;

    TextView textViewStatus;
    ProgressBar progressBar;

    StorageReference mStorageReference;
    DatabaseReference mDatabaseReference;
    private Spinner editTextFilename, spinner2;

    public void record(View v) {
        Intent i = new Intent(MainActivity.this, MainPage.class);
        startActivity(i);
    }

    public void logout(View v) {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mAuth.signOut();
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendToLogin();
            return;
        }

        firebaseFirestore.collection("Admins").document(currentUser.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                    // user is a verified admin, stay on this screen
                } else {
                    mAuth.signOut();
                    Toast.makeText(MainActivity.this,
                            "Access denied. Admin privileges required.", Toast.LENGTH_LONG).show();
                    sendToLogin();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }
}
