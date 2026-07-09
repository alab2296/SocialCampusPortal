package com.campusportal.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DepartmentManagerActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private List<Department> departments = new ArrayList<>();
    private DeptAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_manager);

        db = FirebaseFirestore.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.manage_departments);
        }

        RecyclerView recycler = findViewById(R.id.recycler_departments);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DeptAdapter(departments);
        recycler.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab_add_dept);
        fab.setOnClickListener(v -> showAddDepartmentDialog());

        loadDepartments();
    }

    private void loadDepartments() {
        db.collection("Departments").orderBy("name")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null) return;
                    departments.clear();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        Department dept = doc.toObject(Department.class);
                        dept.id = doc.getId();
                        departments.add(dept);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void showAddDepartmentDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_department, null);
        TextInputEditText nameField = view.findViewById(R.id.field_name);
        TextInputEditText codeField = view.findViewById(R.id.field_code);

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.add_department)
                .setView(view)
                .setPositiveButton(R.string.add, (d, w) -> {
                    String name = nameField.getText() != null ? nameField.getText().toString().trim() : "";
                    String code = codeField.getText() != null ? codeField.getText().toString().trim().toUpperCase() : "";
                    if (TextUtils.isEmpty(name) || TextUtils.isEmpty(code)) {
                        Toast.makeText(this, "Name and code are required.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    addDepartment(name, code);
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void addDepartment(String name, String code) {
        Department dept = new Department(name, code);
        db.collection("Departments").add(dept)
                .addOnSuccessListener(ref ->
                        Toast.makeText(this, "Department added.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to add department.", Toast.LENGTH_SHORT).show());
    }

    private void deleteDepartment(Department dept) {
        new MaterialAlertDialogBuilder(this)
                .setMessage("Delete department \"" + dept.name + "\"?")
                .setPositiveButton(R.string.delete, (d, w) ->
                        db.collection("Departments").document(dept.id).delete()
                                .addOnSuccessListener(v ->
                                        Toast.makeText(this, "Department deleted.", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Failed to delete.", Toast.LENGTH_SHORT).show()))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    class DeptAdapter extends RecyclerView.Adapter<DeptAdapter.ViewHolder> {
        private final List<Department> list;

        DeptAdapter(List<Department> list) { this.list = list; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_department, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Department dept = list.get(position);
            holder.name.setText(dept.name);
            holder.code.setText(dept.code);
            holder.deleteBtn.setOnClickListener(v -> deleteDepartment(dept));
        }

        @Override
        public int getItemCount() { return list.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView name, code;
            View deleteBtn;
            ViewHolder(View v) {
                super(v);
                name = v.findViewById(R.id.dept_name);
                code = v.findViewById(R.id.dept_code);
                deleteBtn = v.findViewById(R.id.btn_delete);
            }
        }
    }
}
