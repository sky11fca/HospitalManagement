package com.hospital.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MedicalRecord {
    private int recordId;
    private Patient patient;
    private Doctor doctor;
    private String diagnosis;
    private String treatement;
    private LocalDateTime recordDate;
    private int severity;

    public MedicalRecord() {

    }

    public MedicalRecord(Patient patient, Doctor doctor, String diagnosis, String treatement, LocalDateTime recordDate, int severity) {
        this.patient = patient;
        this.doctor = doctor;
        this.diagnosis = diagnosis;
        this.treatement = treatement;
        this.recordDate = recordDate;
        this.severity = severity;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatement() {
        return treatement;
    }

    public void setTreatement(String treatement) {
        this.treatement = treatement;
    }

    public LocalDateTime getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(LocalDateTime recordDate) {
        this.recordDate = recordDate;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }
}
