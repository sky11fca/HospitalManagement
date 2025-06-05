package com.hospital.controller;

import com.hospital.dao.PatientDAO;
import com.hospital.model.Patient;

import java.sql.SQLException;
import java.util.List;

public class PatientController {
    private final PatientDAO patientDAO;

    public PatientController() {
        this.patientDAO = new PatientDAO();
    }

    public void addPatient(Patient patient) throws SQLException {
        patientDAO.addPatient(patient);
    }

    public List<Patient> getAllPatients() throws SQLException {
        return patientDAO.getAllPatients();
    }

    //Controller Method based on CRUD OPERATION IN DAO
}
