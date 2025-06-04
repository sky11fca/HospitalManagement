package com.hospital.dao;

import com.hospital.config.DatabaseConfig;
import com.hospital.model.Patient;

import java.sql.*;

public class PatientDAO {

    public void addPatient(Patient patient) throws SQLException {
        String sql = "INSERT INTO patients (cnp, first_name, last_name, birth_date, blood_type) VALUES (?, ?, ?, ?, ?)";

        try(Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS))
        {
            stmt.setString(1, patient.getCnp());
            ///

            stmt.executeUpdate();

            try(ResultSet rs = stmt.getGeneratedKeys()){
                if(rs.next()){
                    patient.setPatientId(rs.getInt(1));
                }
            }
        }
    }

    //TODO: Think of CRUD Operations: getAllPatients, updatePatients deletePatient getPatientById
}
