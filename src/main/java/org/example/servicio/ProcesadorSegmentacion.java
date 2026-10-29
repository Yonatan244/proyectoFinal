
package org.example.servicio;

import org.example.config.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clase de Servicio enfocada en el proceso ETL (Extracción, Transformación y Carga).
 * Toma los datos desnormalizados de la Tabla_general, filtra los duplicados
 * y distribuye la información de forma limpia en el modelo relacional.
 */
public class ProcesadorSegmentacion {

    /**
     * Ejecuta la segmentación y traspaso automático de datos hacia las tablas maestras.
     */
    public static void ejecutarSegmentacion() {
        System.out.println("\n[ETL]: Iniciando segmentación y limpieza de la sábana de datos...");

        // 1. Sentencia para extraer Proveedores únicos eliminando duplicados mediante 'DISTINCT'
        String sqlProveedores = """
                INSERT INTO Proveedores (id_proveedor, nombre, apellido, correo, telefono)
                SELECT DISTINCT id_proveedor, nombre, apellido, email, telefono 
                FROM Tabla_general
                WHERE id_proveedor NOT IN (SELECT id_proveedor FROM Proveedores);
                """;

        // 2. Sentencia para poblar Ficha_productos validando que no dupliquemos el id_producto
        String sqlFichaProductos = """
                INSERT INTO Ficha_productos (id_producto, id_proveedor, nombre_producto, fecha_entrada, fecha_caducidad)
                SELECT DISTINCT id_producto, id_proveedor, nombre_producto, fecha_entrada, fecha_caducidad
                FROM Tabla_general
                WHERE id_producto NOT IN (SELECT id_producto FROM Ficha_productos);
                """;

        // 3. Sentencia para poblar Catalogo_tienda con el stock y precio comercial final
        String sqlCatalogoTienda = """
                INSERT INTO Catalogo_tienda (id_producto, nombre_producto, stock, precio)
                SELECT DISTINCT id_producto, nombre_producto, stock, precio_venta
                FROM Tabla_general
                WHERE id_producto NOT IN (SELECT id_producto FROM Catalogo_tienda);
                """;

        try (Connection con = Conexion.obtenerConexion();
             Statement st = con.createStatement()) {

            // Desactivamos el auto-commit para manejar todo el traspaso como una única "Transacción Segura"
            // Si algo falla a mitad de camino, la base de datos vuelve a su estado original sin romper nada.
            con.setAutoCommit(false);

            try {
                // PASO 1: Procesar e insertar Proveedores únicos
                int filasProveedores = st.executeUpdate(sqlProveedores);
                System.out.println("[ETL]: -> " + filasProveedores + " nuevos proveedores registrados sin duplicados.");

                // PASO 2: Procesar e insertar Fichas de Productos
                int filasFichas = st.executeUpdate(sqlFichaProductos);
                System.out.println("[ETL]: -> " + filasFichas + " fichas técnicas de productos enlazadas.");

                // PASO 3: Procesar e insertar Catálogo Comercial
                int filasCatalogo = st.executeUpdate(sqlCatalogoTienda);
                System.out.println("[ETL]: -> " + filasCatalogo + " artículos volcados al catálogo comercial.");

                // Si todo el bloque se ejecutó correctamente, confirmamos los cambios en el archivo físico .db
                con.commit();
                System.out.println("[OK]: Segmentación relacional finalizada con éxito. Datos listos para la venta.\n");

            } catch (SQLException e) {
                // Si ocurre un error en cualquiera de las 3 tablas, deshacemos todos los inserts parciales
                con.rollback();
                System.err.println("[ERROR - TRANSACCIÓN]: Se canceló el proceso por un fallo de integridad. Aplicando Rollback.");
                throw e; // Volvemos a lanzar la excepción para que el catch externo la registre
            }

        } catch (SQLException e) {
            System.err.println("[ERROR - ETL]: No se pudo completar la migración de datos internos.");
            System.err.println("Detalle del motor SQL: " + e.getMessage() + "\n");
        }
    }
}
