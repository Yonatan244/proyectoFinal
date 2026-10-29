
package org.example.servicio;

import org.example.config.Conexion;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clase de Servicio enfocada en el mantenimiento crítico de datos.
 * Vacía por completo las existencias del inventario comercial, logístico y de tránsito,
 * garantizando la integridad del esquema mediante transacciones controladas.
 */
public class VaciadorInventario {

    /**
     * Elimina todos los registros de los productos del catálogo, fichas técnicas y tránsito
     * en una sola transacción atómica y segura.
     *
     * @return true si el vaciado fue exitoso; false si ocurrió algún error técnico.
     */
    public static boolean vaciarTablasDeProductos() {
        System.out.println("\n[MANTENIMIENTO]: Iniciando el proceso de vaciado completo de existencias...");

        // Definimos las sentencias de borrado en el orden jerárquico inverso exigido por H2
        String sqlBorrarCatalogo = "DELETE FROM Catalogo_tienda;";
        String sqlBorrarFichas = "DELETE FROM Ficha_productos;";
        String sqlBorrarTransito = "DELETE FROM Tabla_general;";

        try (Connection con = Conexion.obtenerConexion(); // Cambiado a obtainConexion() según tu infraestructura actual
             Statement st = con.createStatement()) {

            // Desactivamos el auto-commit para asegurar que se borre TODO o NADA
            con.setAutoCommit(false);

            try {
                // 1. Borramos el catálogo comercial (Hijo menor)
                int filasCatalogo = st.executeUpdate(sqlBorrarCatalogo);
                System.out.println("[MANTENIMIENTO]: -> " + filasCatalogo + " registros eliminados de Catalogo_tienda.");

                // 2. Borramos las fichas técnicas (Padre de catálogo / Hijo de Proveedores)
                int filasFichas = st.executeUpdate(sqlBorrarFichas);
                System.out.println("[MANTENIMIENTO]: -> " + filasFichas + " fichas técnicas eliminadas de Ficha_productos.");

                // 3. Borramos la sábana de datos temporal (Tabla de tránsito)
                int filasTransito = st.executeUpdate(sqlBorrarTransito);
                System.out.println("[MANTENIMIENTO]: -> " + filasTransito + " registros crudos limpiados de Tabla_general.");

                // Si todos los DELETE se ejecutan con éxito, consolidamos el vaciado definitivo
                con.commit();
                System.out.println("[OK]: Mantenimiento finalizado. Todo el inventario de productos ha sido eliminado de la base de datos.\n");
                return true;

            } catch (SQLException e) {
                // Si algo falla, restauramos todos los registros borrados parcialmente para evitar desastres
                con.rollback();
                System.err.println("[ERROR - TRANSACCIÓN]: No se pudo realizar el vaciado masivo de forma segura. Aplicando Rollback.");
                throw e;
            }

        } catch (SQLException e) {
            System.err.println("[ERROR - BASE DATOS]: Falló la comunicación con el motor H2 durante la purga de datos.");
            System.err.println("  -> Detalle técnico: " + e.getMessage() + "\n");
            return false;
        }
    }
}