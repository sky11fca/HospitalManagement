package com.hospital.dao;

import com.hospital.config.DatabaseConfig;
import com.hospital.model.Doctor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {
    public void addDoctor(Doctor doctor) throws SQLException {
        String sql = "INSERT INTO doctors (license_number, first_name, last_name, specialization, years_experience) VALUES (?, ?, ?, ?, ?)";

        try(Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            stmt.setString(1, doctor.getLicenceNumber());
            stmt.setString(2, doctor.getFirstName());
            stmt.setString(3, doctor.getLastName());
            stmt.setString(4, doctor.getSpecialization());
            stmt.setInt(5, doctor.getYearsExperience());

            stmt.execute();

            try(ResultSet rs = stmt.getGeneratedKeys()){
                if(rs.next()){
                    doctor.setDoctorId(rs.getInt(1));
                }
            }
        }
    }

    public List<Doctor> getAllDoctors() throws SQLException{
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors";

        try(Connection conn = DatabaseConfig.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)){
            while(rs.next()){
                Doctor doctor = new Doctor();
                doctor.setDoctorId(rs.getInt("doctor_id"));
                doctor.setLicenceNumber(rs.getString("license_number"));
                doctor.setFirstName(rs.getString("first_name"));
                doctor.setLastName(rs.getString("last_name"));
                doctor.setSpecialization(rs.getString("specialization"));
                doctor.setYearsExperience(rs.getInt("years_experience"));
                doctors.add(doctor);
            }
        }
        return doctors;
    }

    public Doctor getDoctorByLicenceNumber(String licenceNumber) throws SQLException{
        Doctor doctor = null;
        String sql = "SELECT * FROM doctors WHERE license_number = ?";

        try(Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, licenceNumber);

            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    doctor = new Doctor();
                    doctor.setDoctorId(rs.getInt("doctor_id"));
                    doctor.setLicenceNumber(rs.getString("license_number"));
                    doctor.setFirstName(rs.getString("first_name"));
                    doctor.setLastName(rs.getString("last_name"));
                    doctor.setSpecialization(rs.getString("specialization"));
                    doctor.setYearsExperience(rs.getInt("years_experience"));
                }
            }
        }
        return doctor;
    }

    public void updateDoctor(Doctor doctor) throws SQLException{
        String sql = "UPDATE doctors SET license_number = ?, first_name = ?, last_name = ?, specialization = ?, years_experience = ? WHERE doctor_id = ?";

        try(Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, doctor.getLicenceNumber());
            stmt.setString(2, doctor.getFirstName());
            stmt.setString(3, doctor.getLastName());
            stmt.setString(4, doctor.getSpecialization());
            stmt.setInt(5, doctor.getYearsExperience());
            stmt.setInt(6, doctor.getDoctorId());

            stmt.executeUpdate();
        }
    }

    public void deleteDoctor(int doctorId) throws SQLException{
        String sql = "DELETE FROM doctors WHERE doctor_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, doctorId);
            stmt.executeUpdate();
             }
    }

}
