package org.example.servicio;

import org.example.config.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase de Servicio encargada de la modificación de datos.
 * Permite actualizar el stock y el precio de un artículo existente
 * en la tabla Catalogo_tienda de forma segura.
 */
public class ActualizadorProducto {

    /**
     * Modifica el stock y el precio de un producto específico por su ID.
     *
     * @param idProducto Código único del artículo a modificar.
     * @param nuevoStock Cantidad actualizada de unidades disponibles.
     * @param nuevoPrecio Nuevo precio de venta al público (PVP).
     * @return true si la actualización fue exitosa; false si el producto no existía o hubo un fallo.
     */
    public static boolean actualizarDatosComerciales(int idProducto, int nuevoStock, double nuevoPrecio) {
        // Sentencia para verificar la existencia previa del producto
        String sqlVerificar = "SELECT id_producto FROM Catalogo_tienda WHERE id_producto = ?;";

        // Sentencia SQL parametrizada para actualizar los valores comerciales
        String sqlUpdate = """
                UPDATE Catalogo_tienda 
                SET stock = ?, precio = ? 
                WHERE id_producto = ?;
                """;

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement psVerificar = con.prepareStatement(sqlVerificar);
             PreparedStatement psUpdate = con.prepareStatement(sqlUpdate)) {

            // PASO 1: Comprobar si el ID realmente existe en el catálogo
            psVerificar.setInt(1, idProducto);
            try (ResultSet rs = psVerificar.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("\n[SISTEMA]: No se puede actualizar. El ID de producto " + idProducto + " no existe.");
                    return false;
                }
            }

            // PASO 2: Si existe, configuramos los nuevos parámetros para el UPDATE
            psUpdate.setInt(1, nuevoStock);
            psUpdate.setDouble(2, nuevoPrecio);
            psUpdate.setInt(3, idProducto);

            // Ejecutamos la actualización en la base de datos
            int filasModificadas = psUpdate.executeUpdate();

            if (filasModificadas > 0) {
                System.out.println("\n[OK]: Catálogo actualizado correctamente para el producto ID: " + idProducto);
                return true;
            }

            return false;

        } catch (SQLException e) {
            System.err.println("[ERROR - ACTUALIZACIÓN]: Falló la operación en el motor de base de datos.");
            System.err.println("  -> Detalle técnico: " + e.getMessage() + "\n");
            return false;
        }
    }
}
