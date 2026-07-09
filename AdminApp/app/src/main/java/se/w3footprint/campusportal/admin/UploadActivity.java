package se.w3footprint.campusportal.admin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity {

    private AutoCompleteTextView deptDropdown;
    private AutoCompleteTextView programDropdown;
    private AutoCompleteTextView recordTypeDropdown;
    private TextInputEditText batchField;
    private MaterialButton chooseFileBtn;
    private MaterialButton uploadBtn;
    private LinearProgressIndicator progress;

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth mAuth;

    private Uri selectedFileUri;
    private String selectedFileName;
    private final List<Department> departments = new ArrayList<>();
    private Department selectedDept;

    private final List<String> recordTypes = Arrays.asList(
            "Attendance", "Sessional Marks", "Time Table", "Result", "Other"
    );

    private final ActivityResultLauncher<Intent> filePicker =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedFileUri = result.getData().getData();
                    selectedFileName = getFileName(selectedFileUri);
                    chooseFileBtn.setText(selectedFileName != null ? selectedFileName : getString(R.string.choose_file));
                    uploadBtn.setEnabled(true);
                }
            });

    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) openFilePicker();
                else Toast.makeText(this, "Storage permission required to pick files.", Toast.LENGTH_SHORT).show();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.upload_record);
        }

        deptDropdown = findViewById(R.id.dropdown_department);
        programDropdown = findViewById(R.id.dropdown_program);
        recordTypeDropdown = findViewById(R.id.dropdown_record_type);
        batchField = findViewById(R.id.field_batch);
        chooseFileBtn = findViewById(R.id.btn_choose_file);
        uploadBtn = findViewById(R.id.btn_upload);
        progress = findViewById(R.id.progress);

        recordTypeDropdown.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, recordTypes));

        uploadBtn.setEnabled(false);

        chooseFileBtn.setOnClickListener(v -> checkPermissionAndPick());
        uploadBtn.setOnClickListener(v -> uploadRecord());

        loadDepartments();
    }

    private void loadDepartments() {
        db.collection("Departments").orderBy("name")
                .get().addOnSuccessListener(snapshots -> {
                    departments.clear();
                    List<String> names = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        Department dept = doc.toObject(Department.class);
                        dept.id = doc.getId();
                        departments.add(dept);
                        names.add(dept.name);
                    }
                    deptDropdown.setAdapter(new ArrayAdapter<>(this,
                            android.R.layout.simple_dropdown_item_1line, names));

                    deptDropdown.setOnItemClickListener((parent, view, position, id) -> {
                        selectedDept = departments.get(position);
                        programDropdown.setText("");
                        if (selectedDept.programs != null && !selectedDept.programs.isEmpty()) {
                            programDropdown.setAdapter(new ArrayAdapter<>(this,
                                    android.R.layout.simple_dropdown_item_1line, selectedDept.programs));
                        } else {
                            programDropdown.setAdapter(null);
                        }
                    });
                });
    }

    private void checkPermissionAndPick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            openFilePicker();
        } else {
            String perm = Manifest.permission.READ_EXTERNAL_STORAGE;
            if (ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED) {
                openFilePicker();
            } else {
                permissionLauncher.launch(perm);
            }
        }
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        filePicker.launch(Intent.createChooser(intent, "Select PDF"));
    }

    private void uploadRecord() {
        String deptName = deptDropdown.getText().toString().trim();
        String program = programDropdown.getText().toString().trim();
        String batch = batchField.getText() != null ? batchField.getText().toString().trim() : "";
        String recordType = recordTypeDropdown.getText().toString().trim();

        if (TextUtils.isEmpty(deptName)) {
            deptDropdown.setError("Select a department");
            return;
        }
        if (TextUtils.isEmpty(recordType)) {
            recordTypeDropdown.setError("Select a record type");
            return;
        }
        if (selectedFileUri == null) {
            Toast.makeText(this, "Please choose a PDF file first.", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        String deptId = selectedDept != null ? selectedDept.id : "";
        String fileId = UUID.randomUUID().toString();
        StorageReference ref = storage.getReference()
                .child("records")
                .child(deptId)
                .child(fileId + ".pdf");

        ref.putFile(selectedFileUri)
                .addOnProgressListener(snapshot -> {
                    int pct = (int) ((100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount());
                    progress.setProgress(pct);
                })
                .addOnSuccessListener(snapshot ->
                        ref.getDownloadUrl().addOnSuccessListener(uri -> {
                            String uploadedBy = mAuth.getCurrentUser() != null
                                    ? mAuth.getCurrentUser().getEmail() : "";

                            UploadRecord record = new UploadRecord(
                                    deptName, deptId, program, batch,
                                    recordType, selectedFileName, uri.toString(), uploadedBy
                            );

                            db.collection("Uploads").add(record)
                                    .addOnSuccessListener(docRef -> {
                                        setLoading(false);
                                        Toast.makeText(this, getString(R.string.upload_success), Toast.LENGTH_SHORT).show();
                                        resetForm();
                                    })
                                    .addOnFailureListener(e -> {
                                        setLoading(false);
                                        Toast.makeText(this, "Upload failed. Try again.", Toast.LENGTH_SHORT).show();
                                    });
                        }))
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(this, "Upload failed. Try again.", Toast.LENGTH_SHORT).show();
                });
    }

    private void setLoading(boolean loading) {
        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        uploadBtn.setEnabled(!loading);
        chooseFileBtn.setEnabled(!loading);
    }

    private void resetForm() {
        deptDropdown.setText("");
        programDropdown.setText("");
        batchField.setText("");
        recordTypeDropdown.setText("");
        chooseFileBtn.setText(R.string.choose_file);
        selectedFileUri = null;
        selectedFileName = null;
        uploadBtn.setEnabled(false);
        progress.setProgress(0);
    }

    private String getFileName(Uri uri) {
        if (uri == null) return null;
        String path = uri.getLastPathSegment();
        return path != null ? path.replaceAll(".*[/:]", "") : "file.pdf";
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
