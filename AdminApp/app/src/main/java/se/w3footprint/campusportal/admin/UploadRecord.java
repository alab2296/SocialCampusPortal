package se.w3footprint.campusportal.admin;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class UploadRecord {
    public String id;
    public String department;
    public String departmentId;
    public String program;
    public String batch;
    public String recordType;
    public String fileName;
    public String url;
    public String uploadedBy;
    @ServerTimestamp
    public Date timestamp;

    public UploadRecord() {}

    public UploadRecord(String department, String departmentId, String program,
                        String batch, String recordType, String fileName,
                        String url, String uploadedBy) {
        this.department = department;
        this.departmentId = departmentId;
        this.program = program;
        this.batch = batch;
        this.recordType = recordType;
        this.fileName = fileName;
        this.url = url;
        this.uploadedBy = uploadedBy;
    }
}
