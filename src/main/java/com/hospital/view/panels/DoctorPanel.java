package com.hospital.view.panels;

import com.hospital.controller.DoctorController;
import com.hospital.model.Doctor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class DoctorPanel extends JPanel {
    private final DoctorController doctorController;
    private JTable doctorTable;
    private DefaultTableModel tableModel;

    public DoctorPanel() {
        this.doctorController = new DoctorController();
        initUI();
        loadDoctorData();
    }

    private void initUI()
    {
        setLayout(new BorderLayout());

        //table model

        String[] columnNames = {"ID", "Licence Number", "First Name", "Last Name", "Specialization", "Years Of Experience"};

        tableModel = new DefaultTableModel(columnNames, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        doctorTable = new JTable(tableModel);
        doctorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        //Add components to the panel
        add(new JScrollPane(doctorTable), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createButtonPanel(){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton addButton = new JButton("Add Doctor");
        JButton deleteButton = new JButton("Delete Doctor");
        JButton updateButton = new JButton("Update Doctor");
        
        addButton.addActionListener(e -> showAddDoctorDialog());
        deleteButton.addActionListener(e -> deleteSelectedDoctor());
        updateButton.addActionListener(e -> showEditDoctorDialog());

        panel.add(addButton);
        panel.add(deleteButton);
        panel.add(updateButton);
        return panel;
    }

    private void showEditDoctorDialog() {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a doctor to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int doctorId = (int) tableModel.getValueAt(selectedRow, 0);
        String licenceNumber = (String) tableModel.getValueAt(selectedRow, 1);

        try {
            JDialog dialog = new JDialog();
            dialog.setTitle("Edit Doctor");
            dialog.setModal(true);
            dialog.setSize(400, 300);
            dialog.setLayout(new GridLayout(6, 2));

            JTextField licenceNumberField = new JTextField(licenceNumber);
            JTextField firstNameField = new JTextField((String) tableModel.getValueAt(selectedRow, 2));
            JTextField lastNameField = new JTextField((String) tableModel.getValueAt(selectedRow, 3));
            JTextField specializationField = new JTextField((String) tableModel.getValueAt(selectedRow, 4));
            JTextField yearsOfExperienceField = new JTextField(String.valueOf(tableModel.getValueAt(selectedRow, 5)));

            dialog.add(new JLabel("Licence Number:"));
            dialog.add(licenceNumberField);
            dialog.add(new JLabel("First Name:"));
            dialog.add(firstNameField);
            dialog.add(new JLabel("Last Name:"));
            dialog.add(lastNameField);
            dialog.add(new JLabel("Specialization:"));
            dialog.add(specializationField);
            dialog.add(new JLabel("Years Of Experience:"));
            dialog.add(yearsOfExperienceField);

            JButton saveButton = new JButton("Update");
            saveButton.addActionListener(e -> {
                try {
                    Doctor doctor = new Doctor(
                            licenceNumberField.getText(),
                            firstNameField.getText(),
                            lastNameField.getText(),
                            specializationField.getText(),
                            Integer.parseInt(yearsOfExperienceField.getText())
                    );
                    doctor.setDoctorId(doctorId);

                    doctorController.updateDoctor(doctor);
                    loadDoctorData();
                    dialog.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            dialog.add(new JLabel());
            dialog.add(saveButton);

            dialog.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedDoctor() {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a doctor to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int doctorId = (int) tableModel.getValueAt(selectedRow, 0);
        String doctorName = tableModel.getValueAt(selectedRow, 2) + " " + tableModel.getValueAt(selectedRow, 3);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete doctor: " + doctorName + "?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                doctorController.deleteDoctor(doctorId);
                loadDoctorData();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAddDoctorDialog() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Add New Doctor");
        dialog.setModal(true);
        dialog.setSize(400, 300);
        dialog.setLayout(new GridLayout(6, 2));

        JTextField licenceNumberField = new JTextField();
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField specializationField = new JTextField();
        JTextField yearsOfExperienceField = new JTextField();

        dialog.add(new JLabel("Licence Number:"));
        dialog.add(licenceNumberField);
        dialog.add(new JLabel("First Name:"));
        dialog.add(firstNameField);
        dialog.add(new JLabel("Last Name:"));
        dialog.add(lastNameField);
        dialog.add(new JLabel("Specialization:"));
        dialog.add(specializationField);
        dialog.add(new JLabel("Years Of Experience:"));
        dialog.add(yearsOfExperienceField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try{
                Doctor doctor = new Doctor(
                        licenceNumberField.getText(),
                        firstNameField.getText(),
                        lastNameField.getText(),
                        specializationField.getText(),
                        Integer.parseInt(yearsOfExperienceField.getText())
                );

                doctorController.addDoctor(doctor);
                loadDoctorData();
                dialog.dispose();
            }catch(Exception ex){
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(new JLabel());
        dialog.add(saveButton);

        dialog.setVisible(true);
    }

    private void loadDoctorData() {
        try{
            List<Doctor> doctors = doctorController.getAllDoctors();
            tableModel.setRowCount(0);

            for(Doctor doctor : doctors){
                tableModel.addRow(new Object[]{
                        doctor.getDoctorId(),
                        doctor.getLicenceNumber(),
                        doctor.getFirstName(),
                        doctor.getLastName(),
                        doctor.getSpecialization(),
                        doctor.getYearsExperience()
                });
            }
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
