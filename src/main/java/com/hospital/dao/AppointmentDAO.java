package com.hospital.dao;

import com.hospital.config.DatabaseConfig;
import com.hospital.model.Appointment;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class AppointmentDAO {
    public void addAppointment(Appointment appointment) throws SQLException {
        String sql = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, duration_min, status) VALUES (?, ?, ?, ?, ?)";

        try(Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            stmt.setInt(1, appointment.getPatient().getPatientId());
            stmt.setInt(2, appointment.getDoctor().getDoctorId());
            stmt.setTimestamp(3, Timestamp.valueOf(appointment.getAppointmentDate()));
            stmt.setInt(4, appointment.getDurationMin());
            stmt.setString(5, appointment.getStatus());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    appointment.setAppointmentId(rs.getInt(1));
                }
            }
        }
    }

    public List<Appointment> getAllAppointments() throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, p.*, d.* FROM Appointments a " +
                "JOIN Patients p ON a.patient_id = p.patient_id " +
                "JOIN Doctors d ON a.doctor_id = d.doctor_id";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Create Patient object
                Patient patient = new Patient();
                patient.setPatientId(rs.getInt("patient_id"));
                patient.setCnp(rs.getString("cnp"));
                patient.setFirstName(rs.getString("first_name"));
                patient.setLastName(rs.getString("last_name"));
                patient.setBirthDate(rs.getDate("birth_date").toLocalDate());
                patient.setBloodType(rs.getString("blood_type"));

                // Create Doctor object
                Doctor doctor = new Doctor();
                doctor.setDoctorId(rs.getInt("doctor_id"));
                doctor.setLicenceNumber(rs.getString("license_number"));
                doctor.setFirstName(rs.getString("first_name"));
                doctor.setLastName(rs.getString("last_name"));
                doctor.setSpecialization(rs.getString("specialization"));
                doctor.setYearsExperience(rs.getInt("years_experience"));

                // Create Appointment object
                Appointment appointment = new Appointment();
                appointment.setAppointmentId(rs.getInt("appointment_id"));
                appointment.setPatient(patient);
                appointment.setDoctor(doctor);
                appointment.setAppointmentDate(rs.getTimestamp("appointment_date").toLocalDateTime());
                appointment.setDurationMin(rs.getInt("duration_min"));
                appointment.setStatus(rs.getString("status"));

                appointments.add(appointment);
            }
        }
        return appointments;
    }

    //OTHER CRUD OPERATIONS:

    public boolean isDoctorAvailable(int doctorId, LocalDateTime dateTime, int durationMinutes) throws SQLException {
        String sql = "SELECT NOT EXISTS (" +
                "SELECT 1 FROM Appointments " +
                "WHERE doctor_id = ? " +
                "AND status != 'CANCELED' " +
                "AND appointment_date <= (? + (? * INTERVAL '1 minute')) " +
                "AND (appointment_date + (duration_min * INTERVAL '1 minute')) >= ?" +
                ") AS is_available";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, doctorId);
            stmt.setTimestamp(2, Timestamp.valueOf(dateTime));
            stmt.setInt(3, durationMinutes);
            stmt.setTimestamp(4, Timestamp.valueOf(dateTime));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("is_available");
                }
            }
        }
        return false;
    }

    public void updateAppointmentStatus(int appointmentId, String newStatus) throws SQLException {
        String sql = "UPDATE Appointments SET status = ? WHERE appointment_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, appointmentId);

            stmt.executeUpdate();
        }
    }

    public void cancelAppointment(int appointmentId) throws SQLException {
        updateAppointmentStatus(appointmentId, "CANCELED");
    }

    public List<Appointment> getAppointmentsByPatient(int patientId) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, p.*, d.* FROM Appointments a " +
                "JOIN Patients p ON a.patient_id = p.patient_id " +
                "JOIN Doctors d ON a.doctor_id = d.doctor_id " +
                "WHERE a.patient_id = ?";

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
                    patient.setBirthDate(rs.getDate("birth_date").toLocalDate());
                    patient.setBloodType(rs.getString("blood_type"));

                    // Create Doctor object
                    Doctor doctor = new Doctor();
                    doctor.setDoctorId(rs.getInt("doctor_id"));
                    doctor.setLicenceNumber(rs.getString("license_number"));
                    doctor.setFirstName(rs.getString("first_name"));
                    doctor.setLastName(rs.getString("last_name"));
                    doctor.setSpecialization(rs.getString("specialization"));
                    doctor.setYearsExperience(rs.getInt("years_experience"));

                    // Create Appointment object
                    Appointment appointment = new Appointment();
                    appointment.setAppointmentId(rs.getInt("appointment_id"));
                    appointment.setPatient(patient);
                    appointment.setDoctor(doctor);
                    appointment.setAppointmentDate(rs.getTimestamp("appointment_date").toLocalDateTime());
                    appointment.setDurationMin(rs.getInt("duration_min"));
                    appointment.setStatus(rs.getString("status"));

                    appointments.add(appointment);
                }
            }
        }
        return appointments;
    }
}
