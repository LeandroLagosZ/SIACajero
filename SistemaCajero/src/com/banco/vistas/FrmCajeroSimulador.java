package com.banco.vistas;

import com.banco.logica.OperacionesBancarias;
import com.banco.logica.Seguridad;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FrmCajeroSimulador extends JFrame {

    private final int ID_CAJERO_ACTUAL = 1; 
    
    private int idTarjetaValidada = -1;
    private List<String[]> cuentasAsociadas = null;
    private String tarjetaEnmascarada = "";

    private JTextField txtTarjeta;
    private JPasswordField txtPin;
    private JButton btnValidar;
    private JComboBox<String> cmbCuentas;
    private JComboBox<String> cmbOperacion; // Nuevo selector
    private JTextField txtMonto;
    private JButton btnEjecutar;
    private JTextArea txtTicket;

    public FrmCajeroSimulador() {
        setTitle("Cajero Automático ATM - BCP");
        setSize(450, 630);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(Color.WHITE); // Estilo BCP

        // SECCIÓN 1: INGRESO DE TARJETA Y PIN 
        JLabel lblIngreso = new JLabel("1. Ingrese su Tarjeta y PIN:");
        lblIngreso.setBounds(20, 20, 200, 20);
        lblIngreso.setForeground(new Color(0, 42, 141));
        lblIngreso.setFont(new Font("Arial", Font.BOLD, 13));
        add(lblIngreso);

        JLabel lblTarjeta = new JLabel("Nro. Tarjeta:");
        lblTarjeta.setBounds(20, 50, 80, 25);
        add(lblTarjeta);

        txtTarjeta = new JTextField();
        txtTarjeta.setBounds(100, 50, 160, 25);
        add(txtTarjeta);

        JLabel lblPin = new JLabel("PIN (4 díg):");
        lblPin.setBounds(20, 80, 80, 25);
        add(lblPin);

        txtPin = new JPasswordField();
        txtPin.setBounds(100, 80, 80, 25);
        add(txtPin);

        btnValidar = new JButton("Validar");
        btnValidar.setBounds(190, 80, 80, 25);
        btnValidar.setBackground(new Color(0, 42, 141)); // Azul BCP
        btnValidar.setForeground(Color.WHITE);
        add(btnValidar);

        // --- SECCIÓN 2: DEPÓSITO / RETIRO ---
        JLabel lblDepo = new JLabel("2. Seleccione Operación, Cuenta y Monto:");
        lblDepo.setBounds(20, 130, 300, 20);
        lblDepo.setForeground(new Color(0, 42, 141));
        lblDepo.setFont(new Font("Arial", Font.BOLD, 13));
        add(lblDepo);

        cmbCuentas = new JComboBox<>();
        cmbCuentas.setBounds(20, 160, 250, 25);
        cmbCuentas.setEnabled(false); 
        add(cmbCuentas);

        JLabel lblOperacion = new JLabel("Operación:");
        lblOperacion.setBounds(20, 195, 70, 25);
        add(lblOperacion);

        cmbOperacion = new JComboBox<>(new String[]{"Depósito", "Retiro"});
        cmbOperacion.setBounds(100, 195, 170, 25);
        cmbOperacion.setEnabled(false);
        add(cmbOperacion);

        JLabel lblMonto = new JLabel("Monto:");
        lblMonto.setBounds(20, 230, 50, 25);
        add(lblMonto);

        txtMonto = new JTextField();
        txtMonto.setBounds(70, 230, 100, 25);
        txtMonto.setEnabled(false);
        add(txtMonto);

        btnEjecutar = new JButton("CONFIRMAR");
        btnEjecutar.setBounds(180, 230, 120, 25);
        btnEjecutar.setEnabled(false);
        btnEjecutar.setBackground(new Color(255, 114, 0)); // Naranja BCP
        btnEjecutar.setForeground(Color.WHITE);
        btnEjecutar.setFont(new Font("Arial", Font.BOLD, 12));
        add(btnEjecutar);

        // --- SECCIÓN 3: TICKET ---
        txtTicket = new JTextArea();
        txtTicket.setEditable(false);
        txtTicket.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scroll = new JScrollPane(txtTicket);
        scroll.setBounds(20, 275, 390, 240);
        add(scroll);
        
        JButton btnVolver = new JButton("Cerrar Sesión / Volver al Menú");
        btnVolver.setBounds(100, 535, 250, 30);
        btnVolver.setBackground(new Color(0, 42, 141));
        btnVolver.setForeground(Color.WHITE);
        add(btnVolver);

        // EVENTOS
        btnValidar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String numeroTarjeta = txtTarjeta.getText().trim();
                String pin = new String(txtPin.getPassword());

                if(numeroTarjeta.isEmpty() || pin.isEmpty()){
                    JOptionPane.showMessageDialog(null, "Complete Tarjeta y PIN");
                    return;
                }

                idTarjetaValidada = OperacionesBancarias.validarTarjetaYPin(numeroTarjeta, pin);

                if (idTarjetaValidada != -1) {
                    JOptionPane.showMessageDialog(null, "Autenticación Correcta");
                    tarjetaEnmascarada = Seguridad.enmascararTarjeta(numeroTarjeta);
                    
                    txtTarjeta.setEnabled(false);
                    txtPin.setEnabled(false);
                    btnValidar.setEnabled(false);
                    
                    cuentasAsociadas = OperacionesBancarias.obtenerCuentasDeTarjeta(idTarjetaValidada);
                    cmbCuentas.removeAllItems();
                    
                    for (String[] cuenta : cuentasAsociadas) {
                        String item = "Cuenta: " + cuenta[1] + " (" + cuenta[2] + ")";
                        cmbCuentas.addItem(item);
                    }
                    
                    cmbCuentas.setEnabled(true);
                    cmbOperacion.setEnabled(true);
                    txtMonto.setEnabled(true);
                    btnEjecutar.setEnabled(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Tarjeta o PIN incorrecto", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnEjecutar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double monto = Double.parseDouble(txtMonto.getText().trim());
                    if (monto <= 0) {
                        JOptionPane.showMessageDialog(null, "El monto debe ser mayor a 0");
                        return;
                    }

                    int indiceSeleccionado = cmbCuentas.getSelectedIndex();
                    String[] datosCuentaElegida = cuentasAsociadas.get(indiceSeleccionado);
                    int idCuenta = Integer.parseInt(datosCuentaElegida[0]);
                    String numCuenta = datosCuentaElegida[1];
                    String simboloMoneda = datosCuentaElegida[2];
                    
                    String tipoOperacion = cmbOperacion.getSelectedItem().toString();
                    boolean exito = false;

                    // Decidir si es Depósito o Retiro
                    if (tipoOperacion.equals("Depósito")) {
                        exito = OperacionesBancarias.realizarDeposito(idCuenta, idTarjetaValidada, ID_CAJERO_ACTUAL, monto);
                    } else {
                        exito = OperacionesBancarias.realizarRetiro(idCuenta, idTarjetaValidada, ID_CAJERO_ACTUAL, monto);
                    }

                    if (exito) {
                        imprimirTicket(numCuenta, simboloMoneda, monto, tipoOperacion);
                        txtMonto.setText("");
                        txtMonto.setEnabled(false);
                        cmbCuentas.setEnabled(false);
                        cmbOperacion.setEnabled(false);
                        btnEjecutar.setEnabled(false);
                        JOptionPane.showMessageDialog(null, "¡" + tipoOperacion + " realizado con éxito!");
                    } else {
                        JOptionPane.showMessageDialog(null, "Error: Verifique que la cuenta tenga saldo suficiente o falló la BD.", "Operación Denegada", JOptionPane.WARNING_MESSAGE);
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Ingrese un monto numérico válido.");
                }
            }
        });

        btnVolver.addActionListener(e -> {
            new FrmPrincipal().setVisible(true);
            dispose();
        });
    }

    private void imprimirTicket(String numCuenta, String moneda, double monto, String tipoOperacion) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String fechaHora = sdf.format(new Date());

        String ticket = "\n";
        ticket +=       "           BANCO BCP           \n";
        ticket +=       "\n";
        ticket += " FECHA/HORA: " + fechaHora + "\n";
        ticket += " CAJERO ID:  CATM000" + ID_CAJERO_ACTUAL + "\n";
        ticket += " UBICACIÓN:  Agencia central BCP\n"; 
        ticket += "\n";
        ticket += " TARJETA:    " + tarjetaEnmascarada + "\n";
        ticket += " TIPO OP:    " + tipoOperacion.toUpperCase() + " ATM\n";
        ticket += " CUENTA:     " + Seguridad.enmascararTarjeta(numCuenta) + "\n";
        ticket += " MONTO:      " + moneda + " " + String.format("%.2f", monto) + "\n";
        ticket += " \n";
        ticket += "       ¡Gracias por su preferencia!    \n";
        ticket += "\n";
        
        txtTicket.setText(ticket);
    }
}