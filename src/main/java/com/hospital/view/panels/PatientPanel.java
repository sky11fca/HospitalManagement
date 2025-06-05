package com.hospital.view.panels;

import com.hospital.controller.PatientController;
import com.hospital.model.Patient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PatientPanel extends JPanel
{
    private final PatientController patientController;
    private JTable patientTable;
    private DefaultTableModel tableModel;

    public PatientPanel() {
        this.patientController = new PatientController();
        initUI();
        loadPatientData();
    }

    private void initUI()
    {
        setLayout(new BorderLayout());

        //table model

        String[] columnNames = {"ID", "CNP", "First Name", "Last Name", "Birth Date", "Blood Type"};

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        patientTable = new JTable(tableModel);
        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


        //Add components to the panel
        add(new JScrollPane(patientTable), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createButtonPanel(){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton addButton = new JButton("Add Patient");
        JButton deleteButton = new JButton("Delete Patient");
        JButton updateButton = new JButton("Update Patient");

        addButton.addActionListener(e -> showAddPatientDialog());
        deleteButton.addActionListener(e -> deleteSelectedPatient());
        updateButton.addActionListener(e -> showEditPatientDialog());

        panel.add(addButton);
        panel.add(deleteButton);
        panel.add(updateButton);
        return panel;
    }

    private void showEditPatientDialog() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int patientId = (int) tableModel.getValueAt(selectedRow, 0);
        String cnp = (String) tableModel.getValueAt(selectedRow, 1);

        try {
            Patient patient = patientController.getPatientByCnp(cnp);
            if (patient == null) {
                JOptionPane.showMessageDialog(this, "Patient not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JDialog dialog = new JDialog();
            dialog.setTitle("Edit Patient");
            dialog.setModal(true);
            dialog.setSize(400, 300);
            dialog.setLayout(new GridLayout(6, 2));

            JTextField cnpField = new JTextField(patient.getCnp());
            JTextField firstNameField = new JTextField(patient.getFirstName());
            JTextField lastNameField = new JTextField(patient.getLastName());
            JTextField birthDateField = new JTextField(patient.getBirthDate().toString());
            JComboBox<String> bloodTypeCombo = new JComboBox<>(
                    new String[]{"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"});
            bloodTypeCombo.setSelectedItem(patient.getBloodType());

            dialog.add(new JLabel("CNP:"));
            dialog.add(cnpField);
            dialog.add(new JLabel("First Name:"));
            dialog.add(firstNameField);
            dialog.add(new JLabel("Last Name:"));
            dialog.add(lastNameField);
            dialog.add(new JLabel("Birth Date (YYYY-MM-DD):"));
            dialog.add(birthDateField);
            dialog.add(new JLabel("Blood Type:"));
            dialog.add(bloodTypeCombo);

            JButton updateButton = new JButton("Update");
            updateButton.addActionListener(e -> {
                try {
                    Patient updatedPatient = new Patient(
                            cnpField.getText(),
                            firstNameField.getText(),
                            lastNameField.getText(),
                            LocalDate.parse(birthDateField.getText()),
                            (String) bloodTypeCombo.getSelectedItem()
                    );
                    updatedPatient.setPatientId(patientId);

                    patientController.updatePatient(updatedPatient);
                    loadPatientData();
                    dialog.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            dialog.add(new JLabel());
            dialog.add(updateButton);

            dialog.setVisible(true);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedPatient() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int patientId = (int) tableModel.getValueAt(selectedRow, 0);
        String patientName = tableModel.getValueAt(selectedRow, 2) + " " + tableModel.getValueAt(selectedRow, 3);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete patient: " + patientName + "?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                patientController.deletePatient(patientId);
                loadPatientData();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void showAddPatientDialog() {
            JDialog dialog = new JDialog();
            dialog.setTitle("Add New Patient");
            dialog.setModal(true);
            dialog.setSize(400, 300);
            dialog.setLayout(new GridLayout(6, 2));

            // Form fields
            JTextField cnpField = new JTextField();
            JTextField firstNameField = new JTextField();
            JTextField lastNameField = new JTextField();
            JTextField birthDateField = new JTextField();
            JComboBox<String> bloodTypeCombo = new JComboBox<>(
                    new String[]{"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"});

            dialog.add(new JLabel("CNP:"));
            dialog.add(cnpField);
            dialog.add(new JLabel("First Name:"));
            dialog.add(firstNameField);
            dialog.add(new JLabel("Last Name:"));
            dialog.add(lastNameField);
            dialog.add(new JLabel("Birth Date (YYYY-MM-DD):"));
            dialog.add(birthDateField);
            dialog.add(new JLabel("Blood Type:"));
            dialog.add(bloodTypeCombo);

            JButton saveButton = new JButton("Save");
            saveButton.addActionListener(e -> {
                try {
                    Patient patient = new Patient(
                            cnpField.getText(),
                            firstNameField.getText(),
                            lastNameField.getText(),
                            LocalDate.parse(birthDateField.getText()),
                            (String) bloodTypeCombo.getSelectedItem()
                    );

                    patientController.addPatient(patient);
                    loadPatientData();
                    dialog.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            dialog.add(new JLabel());
            dialog.add(saveButton);

            dialog.setVisible(true);
    }

    private void loadPatientData() {
        try{
            List<Patient> patients = patientController.getAllPatients();
            tableModel.setRowCount(0);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            for(Patient patient : patients){
                tableModel.addRow(new Object[]{
                        patient.getPatientId(),
                        patient.getCnp(),
                        patient.getFirstName(),
                        patient.getLastName(),
                        patient.getBirthDate().format(formatter),
                        patient.getBloodType()
                });
            }
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
