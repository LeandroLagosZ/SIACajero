package banco.vistas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import banco.db.ConexionDB;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FrmBackOffice extends JFrame {

    private JTable tablaHistorial;
    private DefaultTableModel modeloTabla;

    public FrmBackOffice() {
        setTitle("Panel Administrativo - BackOffice");
        setSize(950, 500);
        
        setExtendedState(JFrame.MAXIMIZED_BOTH); 
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panelSuperior = new JPanel();
        panelSuperior.setBackground(new Color(0, 42, 141)); 

        JLabel lblTitulo = new JLabel("HISTORIAL GENERAL DE OPERACIONES (VISTA SQL)");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setForeground(Color.WHITE); 
        panelSuperior.add(lblTitulo);
        
        JButton btnRefrescar = new JButton("Actualizar Datos");
        btnRefrescar.setBackground(new Color(255, 114, 0)); 
        btnRefrescar.setForeground(Color.WHITE);
        btnRefrescar.setFont(new Font("Arial", Font.BOLD, 12));
        panelSuperior.add(btnRefrescar);
        
        JButton btnVolver = new JButton("Volver al Menú");
        btnVolver.setBackground(Color.WHITE);
        btnVolver.setForeground(new Color(0, 42, 141));
        btnVolver.setFont(new Font("Arial", Font.BOLD, 12));
        panelSuperior.add(btnVolver);

        add(panelSuperior, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel();
        modeloTabla.addColumn("Nro Op.");
        modeloTabla.addColumn("Fecha");
        modeloTabla.addColumn("Documento");
        modeloTabla.addColumn("Nombre Cliente");
        modeloTabla.addColumn("Operación");
        modeloTabla.addColumn("Monto");
        modeloTabla.addColumn("Cta. Destino");
        modeloTabla.addColumn("Tarjeta Usada");
        modeloTabla.addColumn("Ubicación Cajero");

        tablaHistorial = new JTable(modeloTabla);
        tablaHistorial.setRowHeight(25);
        tablaHistorial.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablaHistorial.getTableHeader().setBackground(new Color(230, 230, 230));
        
        JScrollPane scroll = new JScrollPane(tablaHistorial);
        add(scroll, BorderLayout.CENTER);

        btnRefrescar.addActionListener(e -> cargarDatos());
        btnVolver.addActionListener(e -> {
            new FrmPrincipal().setVisible(true);
            dispose();
        });

        cargarDatos();
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0); 
        
        String sql = "SELECT * FROM vw_historial_operaciones ORDER BY \"Fecha_Hora\" DESC";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Object[] fila = new Object[9];
                fila[0] = rs.getInt("Nro_Operacion");
                fila[1] = rs.getTimestamp("Fecha_Hora");
                fila[2] = rs.getString("Documento_Cliente");
                fila[3] = rs.getString("Nombre_Cliente");
                fila[4] = rs.getString("Operacion");
                fila[5] = rs.getString("Moneda") + " " + rs.getDouble("Monto");
                fila[6] = rs.getString("Cuenta_Destino");
                fila[7] = rs.getString("Tarjeta_Usada");
                fila[8] = rs.getString("Ubicacion_Cajero");
                
                modeloTabla.addRow(fila);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar el historial: " + e.getMessage());
        }
    }
}