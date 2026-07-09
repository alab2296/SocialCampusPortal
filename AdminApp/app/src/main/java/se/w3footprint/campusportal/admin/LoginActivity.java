package se.w3footprint.campusportal.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText emailField;
    private TextInputEditText passField;
    private MaterialButton loginBtn;
    private CircularProgressIndicator progress;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailField = findViewById(R.id.email_field);
        passField = findViewById(R.id.pass_field);
        loginBtn = findViewById(R.id.login_btn);
        progress = findViewById(R.id.progress);

        loginBtn.setOnClickListener(v -> attemptLogin());
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            goToMain();
        }
    }

    private void attemptLogin() {
        String email = emailField.getText() != null ? emailField.getText().toString().trim() : "";
        String pass = passField.getText() != null ? passField.getText().toString() : "";

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Please enter your email and password.", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            setLoading(false);
            if (task.isSuccessful()) {
                goToMain();
            } else {
                Toast.makeText(this, "Login failed. Check your email and password.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        loginBtn.setEnabled(!loading);
        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private void goToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
