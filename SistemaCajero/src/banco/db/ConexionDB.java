package banco.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


//Clase que gestiona la conexión a la base de datos PostgreSQL.

public class ConexionDB {
    
    //Cliente configura según su dispositivo
    private static final String URL = "jdbc:postgresql://localhost:5432/db_Banco";
    private static final String USUARIO = "postgres";
    private static final String PASSWORD = "123"; //Colocar contraseña de PostgreSQL, en mi caso (Leandro) es 123
    
  
    //Establece y retorna la conexión a la base de datos.
    //retorna Objeto Connection o null si falla.
     
    public static Connection conectar() {
        Connection conexion = null;
        try {
            // Cargar el driver (Opcional en versiones nuevas de JDBC, pero es buena práctica)
            Class.forName("org.postgresql.Driver");
            conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            System.out.println("Conexión a PostgreSQL establecida con éxito.");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se encontró el Driver de PostgreSQL. ¿Agregaste el .jar al proyecto?");
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
        }
        return conexion;
    }
}