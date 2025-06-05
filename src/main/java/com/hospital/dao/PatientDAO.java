package com.hospital.dao;

import com.hospital.config.DatabaseConfig;
import com.hospital.model.Patient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    public void addPatient(Patient patient) throws SQLException {
        String sql = "INSERT INTO patients (cnp, first_name, last_name, birth_date, blood_type) VALUES (?, ?, ?, ?, ?)";

        try(Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS))
        {
            stmt.setString(1, patient.getCnp());
            stmt.setString(2, patient.getFirstName());
            stmt.setString(3, patient.getLastName());
            stmt.setDate(4, Date.valueOf(patient.getBirthDate()));
            stmt.setString(5, patient.getBloodType());

            stmt.executeUpdate();

            try(ResultSet rs = stmt.getGeneratedKeys()){
                if(rs.next()){
                    patient.setPatientId(rs.getInt(1));
                }
            }
        }
    }

    public List<Patient> getAllPatients() throws SQLException{
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients";

        try(Connection conn = DatabaseConfig.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)){
            while(rs.next()){
                Patient patient = new Patient();
                patient.setPatientId(rs.getInt("patient_id"));
                patient.setCnp(rs.getString("cnp"));
                patient.setFirstName(rs.getString("first_name"));
                patient.setLastName(rs.getString("last_name"));
                patient.setBirthDate(rs.getDate("birth_date").toLocalDate());
                patient.setBloodType(rs.getString("blood_type"));
                patient.setRegistrationDate(rs.getTimestamp("registration_date").toLocalDateTime().toLocalDate());
                patients.add(patient);
            }
        }
        return patients;
    }
    
    public Patient getPatientByCnp(String cnp) throws SQLException{
        Patient patient = null;
        String sql = "SELECT * FROM patients WHERE cnp = ?";
        
        try(Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, cnp);
            
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    patient = new Patient();
                    patient.setPatientId(rs.getInt("patient_id"));
                    patient.setCnp(rs.getString("cnp"));
                    patient.setFirstName(rs.getString("first_name"));
                    patient.setLastName(rs.getString("last_name"));
                    patient.setBirthDate(rs.getDate("birth_date").toLocalDate());
                    patient.setBloodType(rs.getString("blood_type"));
                    patient.setRegistrationDate(rs.getTimestamp("registration_date").toLocalDateTime().toLocalDate());
                }
            }
        }
        return patient;
    }
    
    
    public void updatePatient(Patient patient) throws SQLException{
        String sql = "UPDATE patients SET cnp = ?, first_name = ?, last_name = ?, birth_date = ?, blood_type = ? WHERE patient_id = ?";
        
        try(Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, patient.getCnp());
            stmt.setString(2, patient.getFirstName());
            stmt.setString(3, patient.getLastName());
            stmt.setDate(4, Date.valueOf(patient.getBirthDate()));
            stmt.setString(5, patient.getBloodType());
            stmt.setInt(6, patient.getPatientId());
            
            stmt.executeUpdate();
        }
    }
    
    public void deletePatient(int patientId) throws SQLException{
        String sql = "DELETE FROM patients WHERE patient_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, patientId);
            stmt.executeUpdate();
        }
    }
}
