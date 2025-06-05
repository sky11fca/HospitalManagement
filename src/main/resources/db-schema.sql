CREATE TABLE Patients (
                          patient_id SERIAL PRIMARY KEY,
                          cnp VARCHAR(13) UNIQUE NOT NULL,
                          first_name VARCHAR(50) NOT NULL,
                          last_name VARCHAR(50) NOT NULL,
                          birth_date DATE NOT NULL,
                          blood_type VARCHAR(3) CHECK(blood_type IN ('A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-')),
                          registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Doctors (
                         doctor_id SERIAL PRIMARY KEY,
                         license_number VARCHAR(20) UNIQUE NOT NULL,
                         first_name VARCHAR(50) NOT NULL,
                         last_name VARCHAR(50) NOT NULL,
                         specialization VARCHAR(100) NOT NULL,
                         years_experience INT
);

CREATE TABLE Appointments (
                              appointment_id SERIAL PRIMARY KEY,
                              patient_id INT REFERENCES Patients(patient_id) ON DELETE CASCADE,
                              doctor_id INT REFERENCES Doctors(doctor_id) ON DELETE SET NULL,
                              appointment_date TIMESTAMP NOT NULL,
                              duration_min INT DEFAULT 30,
                              status VARCHAR(20) DEFAULT 'SCHEDULED' CHECK (status IN ('SCHEDULED', 'COMPLETED', 'CANCELED')),
                              UNIQUE (doctor_id, appointment_date)
);

CREATE TABLE MedicalRecords (
                                record_id SERIAL PRIMARY KEY,
                                patient_id INT REFERENCES Patients(patient_id) ON DELETE CASCADE,
                                doctor_id INT REFERENCES Doctors(doctor_id) ON DELETE SET NULL,
                                diagnosis TEXT NOT NULL,
                                treatment TEXT,
                                record_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                severity INT CHECK (severity BETWEEN 1 AND 5)
);


--FUNCTION: Check doctor weither is busy with a patient or not

CREATE OR REPLACE FUNCTION check_doctor_availability(
    p_doctor_id INT,
    p_date TIMESTAMP,
    p_duration INT
) RETURNS BOOLEAN AS $$
DECLARE
    v_is_available BOOLEAN;
BEGIN
    SELECT NOT EXISTS(
        SELECT 1 FROM Appointments
        WHERE doctor_id = p_doctor_id
          AND appointment_date <= (p_date + (p_duration * INTERVAL '1 minute'))
          AND (appointment_date+(duration_min * INTERVAL '1 minute')) >= p_date
    ) INTO v_is_available;

    RETURN v_is_available;

EXCEPTION
    WHEN OTHERS THEN
        RAISE EXCEPTION 'ERROR checking availability: %', SQLERRM;
end;

$$ LANGUAGE plpgsql;

--PROCEDURE: patient Statistic

CREATE OR REPLACE PROCEDURE get_patient_stats(
    p_patient_id INT,
    OUT total_appointments INT,
    OUT most_common_diagnosis VARCHAR
) AS $$
BEGIN
    SELECT COUNT(*) INTO total_appointments
    FROM Appointments
    WHERE patient_id = p_patient_id AND status = 'COMPLETED';

    SELECT diagnosis INTO most_common_diagnosis
    FROM MedicalRecords
    WHERE patient_id = p_patient_id
    GROUP BY diagnosis
    ORDER BY COUNT(*)
    LIMIT 1;
end;
$$ LANGUAGE plpgsql;


--TRIGGERS:


--AVOID APPOINTMENT OVERLAPPING:

CREATE OR REPLACE FUNCTION check_appointment_overlap()
    RETURNS TRIGGER AS $$
BEGIN
    IF NOT check_doctor_availability(NEW.doctor_id, NEW.appointment_date, NEW.duration_min) THEN
        RAISE EXCEPTION 'Doctor already has an appointment scheduled for this time';
    end if;

    RETURN NEW;
end;

$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_appointment_overlap
    BEFORE INSERT OR UPDATE ON Appointments
    FOR EACH ROW EXECUTE FUNCTION check_appointment_overlap();

--UPDATE MEDICAL RECORD WHEN THE APPOINTMENT IS COMPLETE

CREATE OR REPLACE FUNCTION update_medical_history()
    RETURNS TRIGGER AS $$
BEGIN
    IF NEW.status = 'COMPLETED' AND OLD.status != 'COMPLETED' THEN
        INSERT INTO MedicalRecords (patient_id, doctor_id, diagnosis, treatment)
        VALUES (NEW.patient_id, NEW.doctor_id, 'Checkup completed', 'General examination');
    end if;
end;

$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_medical_history
    AFTER UPDATE ON Appointments
    FOR EACH ROW EXECUTE FUNCTION update_medical_history();


