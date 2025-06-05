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

    public Patient getPatientByCnp(String cnp) throws SQLException {
        return patientDAO.getPatientByCnp(cnp);
    }

    public void updatePatient(Patient patient) throws SQLException {
        patientDAO.updatePatient(patient);
    }

    public void deletePatient(int patientId) throws SQLException {
        patientDAO.deletePatient(patientId);
    }
}
