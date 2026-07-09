package se.w3footprint.campusportal.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MaterialCardView uploadCard = findViewById(R.id.card_upload);
        MaterialCardView uploadsCard = findViewById(R.id.card_uploads);
        MaterialCardView deptCard = findViewById(R.id.card_departments);
        View logoutBtn = findViewById(R.id.btn_logout);

        uploadCard.setOnClickListener(v ->
                startActivity(new Intent(this, UploadActivity.class)));

        uploadsCard.setOnClickListener(v ->
                startActivity(new Intent(this, UploadsListActivity.class)));

        deptCard.setOnClickListener(v ->
                startActivity(new Intent(this, DepartmentManagerActivity.class)));

        logoutBtn.setOnClickListener(v -> confirmLogout());
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            goToLogin();
            return;
        }

        db.collection("Admins").document(user.getUid()).get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful() || task.getResult() == null || !task.getResult().exists()) {
                        mAuth.signOut();
                        Toast.makeText(this, "Access denied. Admin privileges required.", Toast.LENGTH_LONG).show();
                        goToLogin();
                    }
                });
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.confirm_logout)
                .setPositiveButton(R.string.yes, (d, w) -> {
                    mAuth.signOut();
                    goToLogin();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void goToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
