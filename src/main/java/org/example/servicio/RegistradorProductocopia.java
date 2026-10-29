
package org.example.servicio;

import org.example.config.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Clase de Servicio encargada de la inserción manual de datos.
 * Registra un nuevo artículo en el sistema asegurando la integridad
 * relacional entre la Ficha Técnica y el Catálogo Comercial.
 */
public class RegistradorProductocopia {

    /**
     * Registra un producto de forma manual en la base de datos relacional.
     *
     * @param idProducto Código único identificador del artículo.
     * @param idProveedor Código del proveedor existente al que se asocia.
     * @param nombre Nombre comercial del producto.
     * @param stock Cantidad de unidades disponibles.
     * @param precio Precio de venta al público (PVP).
     * @return true si el registro fue exitoso en ambas tablas; false si falló.
     */
    public static boolean registrarManualmente(int idProducto, String idProveedor, String nombre, int stock, double precio) {

        // Sentencia 1: Inserción en la Ficha Técnica (Requerida por la restricción FK)
        String sqlFicha = """
                INSERT INTO Ficha_productos (id_producto, id_proveedor, nombre_producto, fecha_entrada, fecha_caducidad)
                VALUES (?, ?, ?, ?, ?);
                """;

        // Sentencia 2: Inserción en el Catálogo Comercial de cara al público
        String sqlCatalogo = """
                INSERT INTO Catalogo_tienda (id_producto, nombre_producto, stock, precio)
                VALUES (?, ?, ?, ?);
                """;

        // Establecemos por defecto las fechas para el alta manual: entrada hoy y caducidad en un año
        LocalDate fechaHoy = LocalDate.now();
        LocalDate fechaCaducidadSimulada = fechaHoy.plusYears(1);

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement psFicha = con.prepareStatement(sqlFicha);
             PreparedStatement psCatalogo = con.prepareStatement(sqlCatalogo)) {

            // Iniciamos una Transacción Segura controlada a mano
            con.setAutoCommit(false);

            try {
                // PARAMETRIZAMOS LA FICHA TÉCNICA
                psFicha.setInt(1, idProducto);
                psFicha.setString(2, idProveedor.trim().toUpperCase());
                psFicha.setString(3, nombre.trim());
                psFicha.setString(4, fechaHoy.toString());         // Formato AAAA-MM-DD
                psFicha.setString(5, fechaCaducidadSimulada.toString());

                psFicha.executeUpdate();

                // PARAMETRIZAMOS EL CATÁLOGO COMERCIAL
                psCatalogo.setInt(1, idProducto);
                psCatalogo.setString(2, nombre.trim());
                psCatalogo.setInt(3, stock);
                psCatalogo.setDouble(4, precio);

                psCatalogo.executeUpdate();

                // Si ambos INSERT se ejecutan sin errores, confirmamos de golpe en el archivo físico
                con.commit();
                System.out.println("\n[SISTEMA]: Producto registrado con éxito en el modelo relacional.");
                return true;

            } catch (SQLException e) {
                // Si alguna de las dos tablas falla (por ejemplo, si el ID de producto ya existe),
                // cancelamos todo el proceso para no dejar datos huérfanos o incompletos.
                con.rollback();
                System.err.println("\n[ERROR - TRANSACCIÓN]: Se canceló el alta manual por conflictos de integridad.");
                System.err.println("  -> Motivo: " + e.getMessage());
                return false;
            }

        } catch (SQLException e) {
            System.err.println("\n[ERROR - BASE DATOS]: Error en los canales de comunicación de persistencia.");
            System.err.println("  -> Detalle: " + e.getMessage());
            return false;
        }
    }
}
