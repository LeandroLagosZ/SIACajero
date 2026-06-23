package com.banco.logica;

import com.banco.db.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OperacionesBancarias {

    public static int validarTarjetaYPin(String numeroTarjeta, String pinIngresado) {
        String sql = "SELECT id_tarjeta, pin_hash FROM tarjeta WHERE numero_tarjeta = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, numeroTarjeta);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int idTarjeta = rs.getInt("id_tarjeta");
                String hashBaseDatos = rs.getString("pin_hash");
                String hashIngresado = Seguridad.generarHashSHA256(pinIngresado);
                if (hashBaseDatos.equals(hashIngresado)) {
                    return idTarjeta;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al validar tarjeta: " + e.getMessage());
        }
        return -1; 
    }

    public static List<String[]> obtenerCuentasDeTarjeta(int idTarjeta) {
        List<String[]> cuentas = new ArrayList<>();
        String sql = "SELECT c.id_cuenta, c.numero_cuenta, m.simbolo, c.saldo " +
                     "FROM cuenta_tarjeta ct " +
                     "INNER JOIN cuenta_bancaria c ON ct.id_cuenta = c.id_cuenta " +
                     "INNER JOIN mae_moneda m ON c.id_moneda = m.id_moneda " +
                     "WHERE ct.id_tarjeta = ?";
                     
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idTarjeta);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String[] datosCuenta = new String[4];
                datosCuenta[0] = String.valueOf(rs.getInt("id_cuenta"));
                datosCuenta[1] = rs.getString("numero_cuenta");
                datosCuenta[2] = rs.getString("simbolo");
                datosCuenta[3] = String.valueOf(rs.getDouble("saldo"));
                cuentas.add(datosCuenta);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener cuentas: " + e.getMessage());
        }
        return cuentas;
    }

    public static boolean realizarDeposito(int idCuenta, int idTarjeta, int idCajero, double monto) {
        String updateSaldo = "UPDATE cuenta_bancaria SET saldo = saldo + ? WHERE id_cuenta = ?";
        String insertTransaccion = "INSERT INTO transaccion_atm (id_cuenta, id_tarjeta, id_cajero, id_tipo_operacion, monto) VALUES (?, ?, ?, 1, ?)"; 
        
        Connection conn = ConexionDB.conectar();
        if (conn == null) return false;

        try {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmtUpdate = conn.prepareStatement(updateSaldo)) {
                pstmtUpdate.setDouble(1, monto);
                pstmtUpdate.setInt(2, idCuenta);
                pstmtUpdate.executeUpdate();
            }
            try (PreparedStatement pstmtInsert = conn.prepareStatement(insertTransaccion)) {
                pstmtInsert.setInt(1, idCuenta);
                pstmtInsert.setInt(2, idTarjeta);
                pstmtInsert.setInt(3, idCajero);
                pstmtInsert.setDouble(4, monto);
                pstmtInsert.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) {}
            return false;
        } finally {
            try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
        }
    }

    // NUEVO MÉTODO: Retiro de Dinero con validación de saldo
    public static boolean realizarRetiro(int idCuenta, int idTarjeta, int idCajero, double monto) {
        String checkSaldo = "SELECT saldo FROM cuenta_bancaria WHERE id_cuenta = ?";
        String updateSaldo = "UPDATE cuenta_bancaria SET saldo = saldo - ? WHERE id_cuenta = ?";
        String insertTransaccion = "INSERT INTO transaccion_atm (id_cuenta, id_tarjeta, id_cajero, id_tipo_operacion, monto) VALUES (?, ?, ?, 2, ?)"; // 2 es Retiro ATM

        Connection conn = ConexionDB.conectar();
        if (conn == null) return false;

        try {
            conn.setAutoCommit(false);
            
            // 1. Validar si hay dinero suficiente
            double saldoActual = 0;
            try (PreparedStatement pstmtCheck = conn.prepareStatement(checkSaldo)) {
                pstmtCheck.setInt(1, idCuenta);
                ResultSet rs = pstmtCheck.executeQuery();
                if (rs.next()) {
                    saldoActual = rs.getDouble("saldo");
                }
            }
            
            if (saldoActual < monto) {
                conn.rollback();
                return false; // Saldo insuficiente
            }

            // 2. Restar el saldo
            try (PreparedStatement pstmtUpdate = conn.prepareStatement(updateSaldo)) {
                pstmtUpdate.setDouble(1, monto);
                pstmtUpdate.setInt(2, idCuenta);
                pstmtUpdate.executeUpdate();
            }

            // 3. Registrar Transacción
            try (PreparedStatement pstmtInsert = conn.prepareStatement(insertTransaccion)) {
                pstmtInsert.setInt(1, idCuenta);
                pstmtInsert.setInt(2, idTarjeta);
                pstmtInsert.setInt(3, idCajero);
                pstmtInsert.setDouble(4, monto);
                pstmtInsert.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) {}
            return false;
        } finally {
            try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
        }
    }
}