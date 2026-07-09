package se.w3footprint.campusportal.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UploadsListActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private final List<UploadRecord> uploads = new ArrayList<>();
    private UploadsAdapter adapter;
    private View emptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploads_list);

        db = FirebaseFirestore.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.view_uploads);
        }

        emptyState = findViewById(R.id.empty_state);
        RecyclerView recycler = findViewById(R.id.recycler_uploads);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UploadsAdapter(uploads);
        recycler.setAdapter(adapter);

        loadUploads();
    }

    private void loadUploads() {
        db.collection("Uploads")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null) return;
                    uploads.clear();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        UploadRecord record = doc.toObject(UploadRecord.class);
                        record.id = doc.getId();
                        uploads.add(record);
                    }
                    adapter.notifyDataSetChanged();
                    emptyState.setVisibility(uploads.isEmpty() ? View.VISIBLE : View.GONE);
                });
    }

    private void deleteUpload(UploadRecord record) {
        new MaterialAlertDialogBuilder(this)
                .setMessage(R.string.confirm_delete)
                .setPositiveButton(R.string.delete, (d, w) ->
                        db.collection("Uploads").document(record.id).delete()
                                .addOnSuccessListener(v ->
                                        Toast.makeText(this, "Record deleted.", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(v ->
                                        Toast.makeText(this, "Failed to delete.", Toast.LENGTH_SHORT).show()))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    class UploadsAdapter extends RecyclerView.Adapter<UploadsAdapter.ViewHolder> {
        private final List<UploadRecord> list;
        private final SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        UploadsAdapter(List<UploadRecord> list) { this.list = list; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_upload, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            UploadRecord record = list.get(position);
            holder.recordType.setText(record.recordType);
            holder.department.setText(record.department
                    + (record.program != null && !record.program.isEmpty() ? " · " + record.program : "")
                    + (record.batch != null && !record.batch.isEmpty() ? " · " + record.batch : ""));
            holder.fileName.setText(record.fileName != null ? record.fileName : "");
            holder.date.setText(record.timestamp != null ? sdf.format(record.timestamp) : "");

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(UploadsListActivity.this, PdfViewActivity.class);
                intent.putExtra("url", record.url);
                intent.putExtra("title", record.recordType + " — " + record.department);
                startActivity(intent);
            });

            holder.deleteBtn.setOnClickListener(v -> deleteUpload(record));
        }

        @Override
        public int getItemCount() { return list.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView recordType, department, fileName, date;
            View deleteBtn;
            ViewHolder(View v) {
                super(v);
                recordType = v.findViewById(R.id.record_type);
                department = v.findViewById(R.id.department);
                fileName = v.findViewById(R.id.file_name);
                date = v.findViewById(R.id.upload_date);
                deleteBtn = v.findViewById(R.id.btn_delete);
            }
        }
    }
}
