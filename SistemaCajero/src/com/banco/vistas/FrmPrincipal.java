package com.banco.vistas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FrmPrincipal extends JFrame {

    public FrmPrincipal() {
        setTitle("Sistema Bancario - UCSM");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        setLayout(null);
        getContentPane().setBackground(Color.WHITE); // Estilo BCP

        JLabel lblTitulo = new JLabel("MÓDULOS DEL SISTEMA BANCARIO");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setForeground(new Color(0, 42, 141));
        lblTitulo.setBounds(50, 30, 300, 30);
        add(lblTitulo);

        JButton btnCajero = new JButton("Simulador de Cajero (ATM)");
        btnCajero.setBounds(80, 90, 240, 40);
        btnCajero.setBackground(new Color(255, 114, 0)); // Naranja BCP
        btnCajero.setForeground(Color.WHITE);
        btnCajero.setFont(new Font("Arial", Font.BOLD, 12));
        add(btnCajero);

        JButton btnBackOffice = new JButton("Panel de Administrador");
        btnBackOffice.setBounds(80, 150, 240, 40);
        btnBackOffice.setBackground(new Color(0, 42, 141)); // Azul BCP
        btnBackOffice.setForeground(Color.WHITE);
        btnBackOffice.setFont(new Font("Arial", Font.BOLD, 12));
        add(btnBackOffice);

        btnCajero.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FrmCajeroSimulador().setVisible(true);
                dispose(); 
            }
        });

        btnBackOffice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String pass = JOptionPane.showInputDialog("Ingrese contraseña de Administrador:");
                if ("admin123".equals(pass)) {
                    new FrmBackOffice().setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Contraseña incorrecta");
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FrmPrincipal().setVisible(true);
        });
    }
}