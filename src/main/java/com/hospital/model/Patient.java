package com.hospital.model;

import java.time.LocalDate;

public class Patient {
    private int patientId;
    private String cnp;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String bloodType;
    private LocalDate registrationDate;

    public Patient() {
    }

    public Patient(String cnp, String firstName, String lastName, LocalDate birthDate, String bloodType) {
        this.cnp = cnp;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.bloodType = bloodType;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getCnp() {
        return cnp;
    }

    public void setCnp(String cnp) {
        this.cnp = cnp;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }
}
