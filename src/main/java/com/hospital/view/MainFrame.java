package com.hospital.view;

import com.hospital.view.panels.DoctorPanel;
import com.hospital.view.panels.PatientPanel;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame
{
    private JTabbedPane tabbedPane;

    public MainFrame()
    {
        setTitle("Hospital Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents(){
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Patients", new PatientPanel());
        tabbedPane.addTab("Doctors", new DoctorPanel());
        ///

        add(tabbedPane, BorderLayout.CENTER);
    }

}
