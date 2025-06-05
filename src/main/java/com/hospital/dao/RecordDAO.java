package com.hospital.dao;

import com.hospital.model.*;
import com.hospital.config.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecordDAO {
    public void addMedicalRecord(MedicalRecord record) throws SQLException {
        String sql = "INSERT INTO MedicalRecords (patient_id, doctor_id, diagnosis, treatment, severity, record_date) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, record.getPatient().getPatientId());
            stmt.setInt(2, record.getDoctor().getDoctorId());
            stmt.setString(3, record.getDiagnosis());
            stmt.setString(4, record.getTreatement());
            stmt.setInt(5, record.getSeverity());
            stmt.setTimestamp(6, Timestamp.valueOf(record.getRecordDate()));

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    record.setRecordId(rs.getInt(1));
                }
            }
        }
    }

    public List<MedicalRecord> getRecordsByPatient(int patientId) throws SQLException {
        List<MedicalRecord> records = new ArrayList<>();
        String sql = "SELECT r.*, p.*, d.* FROM MedicalRecords r " +
                "JOIN Patients p ON r.patient_id = p.patient_id " +
                "JOIN Doctors d ON r.doctor_id = d.doctor_id " +
                "WHERE r.patient_id = ? " +
                "ORDER BY r.record_date DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, patientId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Create Patient object
                    Patient patient = new Patient();
                    patient.setPatientId(rs.getInt("patient_id"));
                    patient.setCnp(rs.getString("cnp"));
                    patient.setFirstName(rs.getString("first_name"));
                    patient.setLastName(rs.getString("last_name"));
                    // Set other patient fields...

                    // Create Doctor object
                    Doctor doctor = new Doctor();
                    doctor.setDoctorId(rs.getInt("doctor_id"));
                    doctor.setLicenceNumber(rs.getString("license_number"));
                    doctor.setFirstName(rs.getString("first_name"));
                    doctor.setLastName(rs.getString("last_name"));
                    // Set other doctor fields...

                    // Create MedicalRecord object
                    MedicalRecord record = new MedicalRecord();
                    record.setRecordId(rs.getInt("record_id"));
                    record.setPatient(patient);
                    record.setDoctor(doctor);
                    record.setDiagnosis(rs.getString("diagnosis"));
                    record.setTreatement(rs.getString("treatment"));
                    record.setSeverity(rs.getInt("severity"));
                    record.setRecordDate(rs.getTimestamp("record_date").toLocalDateTime());

                    records.add(record);
                }
            }
        }
        return records;
    }

    public List<MedicalRecord> getRecordsByDoctor(int doctorId) throws SQLException {
        List<MedicalRecord> records = new ArrayList<>();
        String sql = "SELECT r.*, p.*, d.* FROM MedicalRecords r " +
                "JOIN Patients p ON r.patient_id = p.patient_id " +
                "JOIN Doctors d ON r.doctor_id = d.doctor_id " +
                "WHERE r.doctor_id = ? " +
                "ORDER BY r.record_date DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, doctorId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Create Patient object
                    Patient patient = new Patient();
                    patient.setPatientId(rs.getInt("patient_id"));
                    patient.setCnp(rs.getString("cnp"));
                    patient.setFirstName(rs.getString("first_name"));
                    patient.setLastName(rs.getString("last_name"));
                    // Set other patient fields...

                    // Create Doctor object
                    Doctor doctor = new Doctor();
                    doctor.setDoctorId(rs.getInt("doctor_id"));
                    doctor.setLicenceNumber(rs.getString("license_number"));
                    doctor.setFirstName(rs.getString("first_name"));
                    doctor.setLastName(rs.getString("last_name"));
                    // Set other doctor fields...

                    // Create MedicalRecord object
                    MedicalRecord record = new MedicalRecord();
                    record.setRecordId(rs.getInt("record_id"));
                    record.setPatient(patient);
                    record.setDoctor(doctor);
                    record.setDiagnosis(rs.getString("diagnosis"));
                    record.setTreatement(rs.getString("treatment"));
                    record.setSeverity(rs.getInt("severity"));
                    record.setRecordDate(rs.getTimestamp("record_date").toLocalDateTime());

                    records.add(record);
                }
            }
        }
        return records;
    }

    public void updateMedicalRecord(MedicalRecord record) throws SQLException {
        String sql = "UPDATE MedicalRecords SET diagnosis = ?, treatment = ?, severity = ? " +
                "WHERE record_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, record.getDiagnosis());
            stmt.setString(2, record.getTreatement());
            stmt.setInt(3, record.getSeverity());
            stmt.setInt(4, record.getRecordId());

            stmt.executeUpdate();
        }
    }

    public MedicalRecord getRecordById(int recordId) throws SQLException {
        String sql = "SELECT r.*, p.*, d.* FROM MedicalRecords r " +
                "JOIN Patients p ON r.patient_id = p.patient_id " +
                "JOIN Doctors d ON r.doctor_id = d.doctor_id " +
                "WHERE r.record_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, recordId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Patient patient = new Patient();
                    patient.setPatientId(rs.getInt("patient_id"));
                    patient.setCnp(rs.getString("cnp"));
                    patient.setFirstName(rs.getString("first_name"));
                    patient.setLastName(rs.getString("last_name"));
                    // Set other patient fields...

                    // Create Doctor object
                    Doctor doctor = new Doctor();
                    doctor.setDoctorId(rs.getInt("doctor_id"));
                    doctor.setLicenceNumber(rs.getString("license_number"));
                    doctor.setFirstName(rs.getString("first_name"));
                    doctor.setLastName(rs.getString("last_name"));
                    // Set other doctor fields...

                    // Create MedicalRecord object
                    MedicalRecord record = new MedicalRecord();
                    record.setRecordId(rs.getInt("record_id"));
                    record.setPatient(patient);
                    record.setDoctor(doctor);
                    record.setDiagnosis(rs.getString("diagnosis"));
                    record.setTreatement(rs.getString("treatment"));
                    record.setSeverity(rs.getInt("severity"));
                    record.setRecordDate(rs.getTimestamp("record_date").toLocalDateTime());

                    return record;
                }
            }
        }
        return null;
    }

    public List<MedicalRecord> getRecordsByDiagnosis(String diagnosisKeyword) throws SQLException {
        List<MedicalRecord> records = new ArrayList<>();
        String sql = "SELECT r.*, p.*, d.* FROM MedicalRecords r " +
                "JOIN Patients p ON r.patient_id = p.patient_id " +
                "JOIN Doctors d ON r.doctor_id = d.doctor_id " +
                "WHERE LOWER(r.diagnosis) LIKE LOWER(?) " +
                "ORDER BY r.record_date DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + diagnosisKeyword + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Create Patient object
                    Patient patient = new Patient();
                    patient.setPatientId(rs.getInt("patient_id"));
                    patient.setCnp(rs.getString("cnp"));
                    patient.setFirstName(rs.getString("first_name"));
                    patient.setLastName(rs.getString("last_name"));
                    // Set other patient fields...

                    // Create Doctor object
                    Doctor doctor = new Doctor();
                    doctor.setDoctorId(rs.getInt("doctor_id"));
                    doctor.setLicenceNumber(rs.getString("license_number"));
                    doctor.setFirstName(rs.getString("first_name"));
                    doctor.setLastName(rs.getString("last_name"));
                    // Set other doctor fields...

                    // Create MedicalRecord object
                    MedicalRecord record = new MedicalRecord();
                    record.setRecordId(rs.getInt("record_id"));
                    record.setPatient(patient);
                    record.setDoctor(doctor);
                    record.setDiagnosis(rs.getString("diagnosis"));
                    record.setTreatement(rs.getString("treatment"));
                    record.setSeverity(rs.getInt("severity"));
                    record.setRecordDate(rs.getTimestamp("record_date").toLocalDateTime());

                    records.add(record);
                }
            }
        }
        return records;
    }
}