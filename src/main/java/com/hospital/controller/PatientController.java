package com.hospital.controller;

import com.hospital.dao.PatientDAO;
import com.hospital.model.Patient;

public class PatientController {
    private final PatientDAO patientDAO;

    public PatientController() {
        this.patientDAO = new PatientDAO();
    }

    public void addPatient(Patient patient) {
        patientDAO.addPatient(patient);
    }

    //Controller Method based on CRUD OPERATION IN DAO
}
