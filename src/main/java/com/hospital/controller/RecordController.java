package com.hospital.controller;

import com.hospital.dao.RecordDAO;
import com.hospital.model.Doctor;
import com.hospital.model.MedicalRecord;
import com.hospital.model.Patient;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class RecordController {
    private final RecordDAO recordDAO;

    public RecordController() {
        this.recordDAO = new RecordDAO();
    }

    public void createMedicalRecord(Patient patient, Doctor doctor,
                                    String diagnosis, String treatment, int severity)
            throws SQLException, IllegalArgumentException {

        if (severity < 1 || severity > 5) {
            throw new IllegalArgumentException("Severity must be between 1 and 5");
        }

        MedicalRecord record = new MedicalRecord();
        record.setPatient(patient);
        record.setDoctor(doctor);
        record.setDiagnosis(diagnosis);
        record.setTreatement(treatment);
        record.setSeverity(severity);
        record.setRecordDate(LocalDateTime.now());

        recordDAO.addMedicalRecord(record);
    }

    public List<MedicalRecord> getPatientMedicalHistory(int patientId) throws SQLException {
        return recordDAO.getRecordsByPatient(patientId);
    }

    public List<MedicalRecord> getDoctorRecords(int doctorId) throws SQLException {
        return recordDAO.getRecordsByDoctor(doctorId);
    }

    public void updateMedicalRecord(int recordId, String diagnosis,
                                    String treatment, int severity)
            throws SQLException, IllegalArgumentException {

        if (severity < 1 || severity > 5) {
            throw new IllegalArgumentException("Severity must be between 1 and 5");
        }

        MedicalRecord record = getMedicalRecordById(recordId);
        if (record == null) {
            throw new IllegalArgumentException("Medical record not found");
        }

        record.setDiagnosis(diagnosis);
        record.setTreatement(treatment);
        record.setSeverity(severity);

        recordDAO.updateMedicalRecord(record);
    }

    public MedicalRecord getMedicalRecordById(int recordId) throws SQLException {
        return recordDAO.getRecordById(recordId);
    }

    public List<MedicalRecord> searchRecordsByDiagnosis(String keyword) throws SQLException {
        return recordDAO.getRecordsByDiagnosis(keyword);
    }

    public List<MedicalRecord> getCriticalRecords() throws SQLException {
        // This would require adding a new method in RecordDAO
        // to get records with severity >= 4



        return null;
    }

    public List<MedicalRecord> getRecentRecords(int days) throws SQLException {
        // This would require adding a new method in RecordDAO
        // to get records from the last N days
        return null;
    }
}
