package com.hospital.controller;

import com.hospital.dao.DoctorDAO;
import com.hospital.model.Doctor;

import java.sql.SQLException;
import java.util.List;

public class DoctorController {
    private final DoctorDAO doctorDAO;

    public DoctorController() {
        this.doctorDAO = new DoctorDAO();
    }

    public void addDoctor(Doctor doctor) throws SQLException {
        doctorDAO.addDoctor(doctor);
    }

    public List<Doctor> getAllDoctors() throws SQLException {
        return doctorDAO.getAllDoctors();
    }

    public Doctor getDoctorByLicenceNumber(String licenceNumber) throws SQLException {
        return doctorDAO.getDoctorByLicenceNumber(licenceNumber);
    }
    public void updateDoctor(Doctor doctor) throws SQLException {
        doctorDAO.updateDoctor(doctor);
    }

    public void deleteDoctor(int doctorId) throws SQLException {
        doctorDAO.deleteDoctor(doctorId);
    }
}
