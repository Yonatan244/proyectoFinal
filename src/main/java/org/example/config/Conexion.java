package org.example.config; // REUBICACIÓN: Cambiado al paquete de configuración

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase de Configuración de Infraestructura.
 * Centraliza y provee de forma dinámica las conexiones JDBC a la base de datos H2
 * para que cualquier otra capa del software pueda consumirlas.
 */
public class Conexion {

    // Constantes de configuración de la Base de Datos (Evitan duplicar código en el futuro)
    private static final String URL = "jdbc:h2:./proyectoFinal";
    private static final String USER = "sa";
    private static final String PASS = "";

    // Constructor privado para evitar que otras clases instancien este objeto con 'new Conexion()'
    private Conexion() {
    }

    /**
     * Provee una conexión activa a la base de datos H2.
     * Quien invoque este método es responsable de cerrar la conexión (idealmente con try-with-resources).
     *
     * @return Objeto Connection listo para ejecutar sentencias SQL.
     * @throws SQLException Si ocurre un error de acceso o la base de datos está bloqueada.
     */
    public static Connection obtenerConexion() throws SQLException {
        try {
            // Retorna la conexión directa administrada por el Driver de H2
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            // Logueamos el error en consola para facilitar la depuración al desarrollador
            System.err.println("[ERROR - CONEXIÓN]: No se pudo establecer el enlace con H2 -> " + e.getMessage());
            // Volvemos a lanzar la excepción para que la capa superior decida cómo gestionarla
            throw e;
        }
    }
}
