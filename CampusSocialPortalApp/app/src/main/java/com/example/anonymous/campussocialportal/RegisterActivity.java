package com.example.anonymous.campussocialportal;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private EditText reg_email_field;
    private EditText reg_pass_field;
    private EditText reg_confirm_pass_field;
    private Button reg_btn;
    private Button reg_login_btn;
    private ProgressBar reg_progress;

    private FirebaseAuth mAuth;

    private static final int MIN_PASSWORD_LENGTH = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();

        reg_email_field = findViewById(R.id.reg_email);
        reg_pass_field = findViewById(R.id.reg_pass);
        reg_confirm_pass_field = findViewById(R.id.reg_confirm_pass);
        reg_btn = findViewById(R.id.reg_btn);
        reg_login_btn = findViewById(R.id.reg_login_btn);
        reg_progress = findViewById(R.id.reg_progress);

        TextView tx = (TextView) findViewById(R.id.textview1);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/third.ttf");
        tx.setTypeface(custom_font);

        reg_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = reg_email_field.getText().toString().trim();
                String pass = reg_pass_field.getText().toString();
                String confirm_pass = reg_confirm_pass_field.getText().toString();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(confirm_pass)) {
                    Toast.makeText(RegisterActivity.this, "Please fill in all fields.", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!pass.equals(confirm_pass)) {
                    Toast.makeText(RegisterActivity.this, "Passwords do not match.", Toast.LENGTH_LONG).show();
                    return;
                }

                if (pass.length() < MIN_PASSWORD_LENGTH) {
                    Toast.makeText(RegisterActivity.this,
                            "Password must be at least " + MIN_PASSWORD_LENGTH + " characters.", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!isPasswordComplex(pass)) {
                    Toast.makeText(RegisterActivity.this,
                            "Password must contain letters and numbers.", Toast.LENGTH_LONG).show();
                    return;
                }

                reg_progress.setVisibility(View.VISIBLE);

                mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent setupIntent = new Intent(RegisterActivity.this, SetupActivity.class);
                            startActivity(setupIntent);
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this,
                                    "Registration failed. Please try again.", Toast.LENGTH_LONG).show();
                        }
                        reg_progress.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
    }

    private boolean isPasswordComplex(String password) {
        boolean hasLetter = false;
        boolean hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            if (Character.isDigit(c)) hasDigit = true;
        }
        return hasLetter && hasDigit;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            sendToMain();
        }
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainPage.class);
        startActivity(mainIntent);
        finish();
    }
}
