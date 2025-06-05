package com.hospital.controller;

import com.hospital.dao.AppointmentDAO;
import com.hospital.model.Appointment;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class AppointmentController {

    private final AppointmentDAO appointmentDAO;

    public AppointmentController() {
        this.appointmentDAO = new AppointmentDAO();
    }

    public void scheduleAppointment(Patient patient, Doctor doctor, LocalDateTime dateTime, int duration)
            throws SQLException, IllegalArgumentException {

        if (!appointmentDAO.isDoctorAvailable(doctor.getDoctorId(), dateTime, duration)) {
            throw new IllegalArgumentException("Doctor is not available at the requested time");
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(dateTime);
        appointment.setDurationMin(duration);
        appointment.setStatus("SCHEDULED");

        appointmentDAO.addAppointment(appointment);
    }

    public List<Appointment> getAllAppointments() throws SQLException {
        return appointmentDAO.getAllAppointments();
    }

    public List<Appointment> getPatientAppointments(int patientId) throws SQLException {
        return appointmentDAO.getAppointmentsByPatient(patientId);
    }

    public void cancelAppointment(int appointmentId) throws SQLException {
        appointmentDAO.cancelAppointment(appointmentId);
    }

    public void rescheduleAppointment(int appointmentId, LocalDateTime newDateTime)
            throws SQLException, IllegalArgumentException {

        Appointment appointment = getAppointmentById(appointmentId);
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment not found");
        }

        if (!appointmentDAO.isDoctorAvailable(
                appointment.getDoctor().getDoctorId(),
                newDateTime,
                appointment.getDurationMin())) {

            throw new IllegalArgumentException("Doctor is not available at the new time");
        }

        appointment.setAppointmentDate(newDateTime);
        updateAppointment(appointment);
    }

    public Appointment getAppointmentById(int appointmentId) throws SQLException {
        // This would require adding a getAppointmentById method in AppointmentDAO
        // Implementation similar to other getById methods
        return null;
    }

    public void updateAppointment(Appointment appointment) throws SQLException {
        // This would require adding an update method in AppointmentDAO
        // Implementation would update all fields except ID
    }

    public boolean isDoctorAvailable(Doctor doctor, LocalDateTime dateTime, int duration) throws SQLException {
        return appointmentDAO.isDoctorAvailable(doctor.getDoctorId(), dateTime, duration);
    }

    public List<Appointment> getUpcomingAppointments() throws SQLException {
        // This would require adding a new method in AppointmentDAO
        // to get appointments after current time
        return null;
    }
}
