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