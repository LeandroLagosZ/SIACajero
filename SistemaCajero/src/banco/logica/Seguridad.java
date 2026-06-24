package banco.logica;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

//Clase encargada de la criptografía y enmascaramiento de datos sensibles.

public class Seguridad {

    //Convierte el PIN en un Hash SHA-256.
	// Inserta PIN en texto plano
	// Hash generado de 64 caracteres.
	
    public static String generarHashSHA256(String pin) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(pin.getBytes());
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error fatal: No se encontró el algoritmo SHA-256", e);
        }
    }

    //Cifrado personalizado ASCII para el CVV (+26, +2, +7).
     
    public static String cifrarCVV(String cvv) {
        if (cvv == null || cvv.length() < 3) {
            return cvv; // Retorna igual si no tiene el formato esperado
        }
        char[] caracteres = cvv.toCharArray();
        caracteres[0] = (char) (caracteres[0] + 26);
        caracteres[1] = (char) (caracteres[1] + 2);
        caracteres[2] = (char) (caracteres[2] + 7);
        return new String(caracteres);
    }

    
     //Descifrado personalizado ASCII para el CVV (-26, -2, -7).
     //Recibe cvvCifrado El texto raro extraído de la BD.
     //Retorna CVV original en texto plano.
     
    public static String descifrarCVV(String cvvCifrado) {
        if (cvvCifrado == null || cvvCifrado.length() < 3) {
            return cvvCifrado;
        }
        char[] caracteres = cvvCifrado.toCharArray();
        caracteres[0] = (char) (caracteres[0] - 26);
        caracteres[1] = (char) (caracteres[1] - 2);
        caracteres[2] = (char) (caracteres[2] - 7);
        return new String(caracteres);
    }

    
    //Oculta los primeros 12 dígitos de la tarjeta para el Ticket.
    //param numeroTarjeta Ej: "1234567890123456"
    //return Ej: "**** **** **** 3456"
    
    public static String enmascararTarjeta(String numeroTarjeta) {
        if (numeroTarjeta == null || numeroTarjeta.length() < 4) {
            return "****";
        }
        String ultimos4 = numeroTarjeta.substring(numeroTarjeta.length() - 4);
        return "**** **** **** " + ultimos4;
    }
}	