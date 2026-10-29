package org.example.servicio;

import org.example.config.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Clase de Servicio encargada de la inserción manual de datos.
 * Registra un nuevo artículo replicándolo en las tablas relacionales y en
 * la Tabla_general de tránsito para asegurar que aparezca en las exportaciones.
 */
public class RegistradorProducto {

    public static boolean registrarManualmente(int idProducto, String idProveedor, String nombre, int stock, double precio) {

        // Sentencia 1: Ficha Técnica (Padre)
        String sqlFicha = """
                INSERT INTO Ficha_productos (id_producto, id_proveedor, nombre_producto, fecha_entrada, fecha_caducidad)
                VALUES (?, ?, ?, ?, ?);
                """;

        // Sentencia 2: Catálogo Comercial (Hijo)
        String sqlCatalogo = """
                INSERT INTO Catalogo_tienda (id_producto, nombre_producto, stock, precio)
                VALUES (?, ?, ?, ?);
                """;

        // CORRECCIÓN: Sentencia 3 para insertar también en la sábana de datos (Tránsito)
        String sqlTransito = """
                INSERT INTO Tabla_general (
                    id_proveedor, nombre, apellido, telefono, email,
                    id_producto, nombre_producto, stock, precio_venta, precio_compra,
                    fecha_entrada, fecha_caducidad
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
                """;

        LocalDate fechaHoy = LocalDate.now();
        LocalDate fechaCaducidadSimulada = fechaHoy.plusYears(1);

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement psFicha = con.prepareStatement(sqlFicha);
             PreparedStatement psCatalogo = con.prepareStatement(sqlCatalogo);
             PreparedStatement psTransito = con.prepareStatement(sqlTransito)) {

            con.setAutoCommit(false);

            try {
                // 1. REGISTRO EN FICHA TÉCNICA
                psFicha.setInt(1, idProducto);
                psFicha.setString(2, idProveedor.trim().toUpperCase());
                psFicha.setString(3, nombre.trim());
                psFicha.setString(4, fechaHoy.toString());
                psFicha.setString(5, fechaCaducidadSimulada.toString());
                psFicha.executeUpdate();

                // 2. REGISTRO EN CATÁLOGO TIENDA
                psCatalogo.setInt(1, idProducto);
                psCatalogo.setString(2, nombre.trim());
                psCatalogo.setInt(3, stock);
                psCatalogo.setDouble(4, precio);
                psCatalogo.executeUpdate();

                // 3. REGISTRO EN TABLA GENERAL (Asegura la compatibilidad con el Exportador)
                psTransito.setString(1, idProveedor.trim().toUpperCase());
                psTransito.setString(2, "MANUAL");            // Nombre simulado del proveedor para altas manuales
                psTransito.setString(3, "REGISTRO");          // Apellido simulado
                psTransito.setString(4, "000000000");         // Teléfono simulado
                psTransito.setString(5, "manual@sistema.com"); // Email simulado
                psTransito.setInt(6, idProducto);
                psTransito.setString(7, nombre.trim());
                psTransito.setInt(8, stock);
                psTransito.setDouble(9, precio);               // Precio de venta
                psTransito.setDouble(10, precio * 0.6);        // Estimación simulada de precio de compra (60%)
                psTransito.setString(11, fechaHoy.toString());
                psTransito.setString(12, fechaCaducidadSimulada.toString());
                psTransito.executeUpdate();

                con.commit();
                System.out.println("\n[SISTEMA]: Producto registrado con éxito y sincronizado para futuras exportaciones.");
                return true;

            } catch (SQLException e) {
                con.rollback();
                System.err.println("\n[ERROR - TRANSACCIÓN]: Se canceló el alta manual por conflictos de integridad.");
                System.err.println("  -> Motivo: " + e.getMessage());
                return false;
            }

        } catch (SQLException e) {
            System.err.println("\n[ERROR - BASE DATOS]: Falló el guardado del producto.");
            return false;
        }
    }
}