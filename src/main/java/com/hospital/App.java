package com.hospital;

import com.hospital.view.MainFrame;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->{
            try{
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        });
    }
}
