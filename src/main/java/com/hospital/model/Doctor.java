package com.hospital.model;

public class Doctor {

    private int doctorId;
    private String licenceNumber;
    private String firstName;
    private String lastName;
    private String specialization;
    private int yearsExperience;

    public Doctor() {
    }

    public Doctor(String licenceNumber, String firstName, String lastName, String specialization, int yearsExperience) {
        this.licenceNumber = licenceNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
        this.yearsExperience = yearsExperience;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getLicenceNumber() {
        return licenceNumber;
    }

    public void setLicenceNumber(String licenceNumber) {
        this.licenceNumber = licenceNumber;
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

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public int getYearsExperience() {
        return yearsExperience;
    }

    public void setYearsExperience(int yearsExperience) {
        this.yearsExperience = yearsExperience;
    }
}
