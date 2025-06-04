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


--POPULATING PART

--GO HERE TO EMPTY THE TABLE
TRUNCATE TABLE MedicalRecords RESTART IDENTITY CASCADE;
TRUNCATE TABLE Appointments RESTART IDENTITY CASCADE;
TRUNCATE TABLE Doctors RESTART IDENTITY CASCADE;
TRUNCATE TABLE Patients RESTART IDENTITY CASCADE;

-- POPULATE PATIENTS
INSERT INTO Patients (cnp, first_name, last_name, birth_date, blood_type) VALUES
('1980501123456', 'Maria', 'Popescu', '1980-05-01', 'A+'),
('1991122334455', 'Ion', 'Ionescu', '1991-12-23', 'B-'),
('1973061543210', 'Ana', 'Dumitrescu', '1973-06-15', 'O+'),
('1988110765432', 'George', 'Georgescu', '1988-11-07', 'AB+'),
('2000022912345', 'Elena', 'Constantinescu', '2000-02-29', 'A-'),
('1984022834567', 'Mihai', 'Stoica', '1984-02-28', 'B+'),
('1995081545678', 'Andreea', 'Radu', '1995-08-15', 'O-'),
('1977121267890', 'Vlad', 'Munteanu', '1977-12-12', 'AB-'),
('1969100198765', 'Doina', 'Florescu', '1969-10-01', 'A+'),
('2001050787654', 'Alexandru', 'Marin', '2001-05-07', 'B-');

-- POPULATE DOCTORS
INSERT INTO Doctors (license_number, first_name, last_name, specialization, years_experience) VALUES
('SM123456', 'Adrian', 'Popovici', 'Cardiology', 15),
('SM654321', 'Cristina', 'Vasilescu', 'Pediatrics', 8),
('SM987654', 'Bogdan', 'Ilie', 'Neurology', 20),
('SM456789', 'Diana', 'Mihai', 'Dermatology', 5),
('SM321987', 'Radu', 'Tomescu', 'Orthopedics', 12),
('SM789123', 'Simona', 'Oprea', 'Ophthalmology', 7),
('SM159753', 'Horia', 'Dobre', 'General Surgery', 18),
('SM357951', 'Larisa', 'Gheorghe', 'Endocrinology', 9);


--POPULATE APPOINTMENTS:
--ANONYMOUS BLOCK TO GENERATE APPOINTMENTS IN THE NEXT 30 DAYS

DO $$
    DECLARE
        doc RECORD;
        pat RECORD;
        app_date TIMESTAMP;
        days_offset INT;
        hour_offset INT;
        duration_min INT;
        status TEXT;
        max_attempts INT;
        attempts INT;
        success BOOLEAN;
    BEGIN
        FOR doc IN SELECT doctor_id FROM Doctors LOOP
                FOR pat IN SELECT patient_id FROM Patients ORDER BY RANDOM() LIMIT 3 LOOP
                        FOR i IN 0..2 LOOP
                                -- Reset for each appointment
                                attempts := 0;
                                max_attempts := 10;
                                success := FALSE;

                                WHILE NOT success AND attempts < max_attempts LOOP
                                        BEGIN
                                            -- Generate appointment parameters
                                            days_offset := i*7 + (RANDOM()*5)::INT;
                                            hour_offset := 9 + (RANDOM()*8)::INT; -- 9AM-5PM
                                            duration_min := CASE WHEN RANDOM() > 0.7 THEN 45 ELSE 30 END;

                                            -- Calculate appointment date/time
                                            app_date := (CURRENT_DATE + days_offset * INTERVAL '1 day') +
                                                        (hour_offset * INTERVAL '1 hour');

                                            -- Determine status
                                            IF app_date < CURRENT_TIMESTAMP THEN
                                                status := 'COMPLETED';
                                            ELSIF RANDOM() > 0.9 THEN
                                                status := 'CANCELED';
                                            ELSE
                                                status := 'SCHEDULED';
                                            END IF;

                                            -- Try to insert (will fail if conflict exists)
                                            INSERT INTO Appointments (patient_id, doctor_id, appointment_date, duration_min, status)
                                            VALUES (pat.patient_id, doc.doctor_id, app_date, duration_min, status);

                                            success := TRUE;
                                        EXCEPTION
                                            WHEN OTHERS THEN
                                                attempts := attempts + 1;
                                            -- Uncomment for debugging:
                                            -- RAISE NOTICE 'Conflict for doctor % at %, attempt %', doc.doctor_id, app_date, attempts;
                                        END;
                                    END LOOP;

                                IF NOT success THEN
                                    RAISE NOTICE 'Failed to schedule appointment after % attempts for doctor %', max_attempts, doc.doctor_id;
                                END IF;
                            END LOOP;
                    END LOOP;
            END LOOP;
    END $$;
--MEDICAL RECORDS POPULATION FOR ALREADY DONE APPOINTMENTS
INSERT INTO MedicalRecords (patient_id, doctor_id, diagnosis, treatment, severity)
SELECT
    a.patient_id,
    a.doctor_id,
    CASE
        WHEN RANDOM() > 0.7 THEN 'Hypertension'
        WHEN RANDOM() > 0.5 THEN 'Type 2 Diabetes'
        WHEN RANDOM() > 0.3 THEN 'Upper Respiratory Infection'
        ELSE 'Routine Checkup'
        END AS diagnosis,
    CASE
        WHEN RANDOM() > 0.7 THEN 'Medication prescribed'
        WHEN RANDOM() > 0.5 THEN 'Physical therapy recommended'
        WHEN RANDOM() > 0.3 THEN 'Lab tests ordered'
        ELSE 'No treatment needed'
        END AS treatment,
    FLOOR(1 + RANDOM()*5)::INT AS severity
FROM Appointments a
WHERE a.status = 'COMPLETED';

--OTHER MEDICAL RECORDS
INSERT INTO MedicalRecords (patient_id, doctor_id, diagnosis, treatment, severity) VALUES
(1, 3, 'Migraine', 'Prescribed pain medication', 3),
(4, 5, 'Fractured wrist', 'Cast applied, follow-up in 6 weeks', 4),
(7, 2, 'Childhood vaccinations', 'Administered MMR vaccine', 1),
(2, 8, 'Hypothyroidism', 'Levothyroxine 50mcg daily', 2),
(5, 1, 'High cholesterol', 'Diet and exercise plan', 2),
(9, 4, 'Psoriasis', 'Topical corticosteroids', 3);