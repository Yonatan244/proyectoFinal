package org.example.servicio;

import org.example.config.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase de Servicio enfocada en la baja de datos.
 * Elimina un producto específico del sistema de forma segura,
 * respetando el orden de restricciones de integridad referencial.
 */
public class EliminadorProducto {

    /**
     * Elimina un producto de la base de datos borrando primero su presencia comercial
     * y luego su ficha técnica dentro de una misma transacción.
     *
     * @param idProducto Código único del artículo a eliminar.
     * @return true si la eliminación fue exitosa; false si el artículo no existía o hubo un fallo.
     */
    public static boolean eliminarIndividual(int idProducto) {
        // Sentencia para verificar la existencia del producto antes de borrar
        String sqlVerificar = "SELECT id_producto FROM Catalogo_tienda WHERE id_producto = ?;";

        // Sentencia 1: Borrar del catálogo de la tienda (registro hijo)
        String sqlBorrarCatalogo = "DELETE FROM Catalogo_tienda WHERE id_producto = ?;";

        // Sentencia 2: Borrar de la ficha técnica (registro padre)
        String sqlBorrarFicha = "DELETE FROM Ficha_productos WHERE id_producto = ?;";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement psVerificar = con.prepareStatement(sqlVerificar);
             PreparedStatement psBorrarCatalogo = con.prepareStatement(sqlBorrarCatalogo);
             PreparedStatement psBorrarFicha = con.prepareStatement(sqlBorrarFicha)) {

            // PASO 1: Verificar si el producto realmente existe
            psVerificar.setInt(1, idProducto);
            try (ResultSet rs = psVerificar.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("\n[SISTEMA]: No se puede eliminar. El producto con ID " + idProducto + " no existe.");
                    return false;
                }
            }

            // PASO 2: Iniciar Transacción Segura controlada a mano
            con.setAutoCommit(false);

            try {
                // Borramos primero la parte comercial (Hijo)
                psBorrarCatalogo.setInt(1, idProducto);
                psBorrarCatalogo.executeUpdate();

                // Borramos después la ficha técnica (Padre)
                psBorrarFicha.setInt(1, idProducto);
                psBorrarFicha.executeUpdate();

                // Si ambos DELETE se completan de forma correcta, consolidamos los cambios en el archivo .db
                con.commit();
                System.out.println("\n[OK]: Producto con ID " + idProducto + " eliminado correctamente de todo el sistema.");
                return true;

            } catch (SQLException e) {
                // Si algo falla, revertimos el borrado parcial para evitar inconsistencias de datos
                con.rollback();
                System.err.println("\n[ERROR - TRANSACCIÓN]: Se canceló el borrado por un problema técnico. Aplicando Rollback.");
                System.err.println("  -> Detalle: " + e.getMessage());
                return false;
            }

        } catch (SQLException e) {
            System.err.println("\n[ERROR - BASE DATOS]: Fallo en los canales de comunicación de persistencia.");
            System.err.println("  -> Detalle técnico: " + e.getMessage());
            return false;
        }
    }
}
