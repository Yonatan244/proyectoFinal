package org.example.servicio;

import org.example.config.Conexion;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clase de Servicio encargada de las consultas comerciales.
 * Recupera y emite por consola los productos registrados en el catálogo de la tienda.
 */
public class ConsultorCatalogo {

    /**
     * Consulta la tabla Catalogo_tienda y muestra los productos con un formato visual limpio.
     */
    public static void mostrarCatalogo() {
        // Sentencia SQL para obtener todos los productos del catálogo comercial
        String sqlConsulta = "SELECT id_producto, nombre_producto, stock, precio FROM Catalogo_tienda;";

        System.out.println("\n=====================================================================");
        System.out.println("                      CATÁLOGO COMERCIAL DE LA TIENDA                ");
        System.out.println("=====================================================================");
        // Formato de cabecera usando printf para alinear las columnas visualmente
        // %-12s = Texto alineado a la izquierda con 12 espacios mínimos
        // %-30s = Texto alineado a la izquierda con 30 espacios mínimos
        System.out.printf("%-12s | %-30s | %-10s | %-12s\n", "ID PRODUCTO", "NOMBRE DEL PRODUCTO", "STOCK", "PVP (PRECIO)");
        System.out.println("---------------------------------------------------------------------");

        // Estructura Try-with-Resources para asegurar el cierre de la conexión y los canales de datos
        try (Connection con = Conexion.obtenerConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sqlConsulta)) {

            int contadorProductos = 0;

            // Recorremos el ResultSet fila por fila mientras existan registros en la base de datos
            while (rs.next()) {
                int idProducto = rs.getInt("id_producto");
                String nombreProducto = rs.getString("nombre_producto");
                int stock = rs.getInt("stock");
                double precio = rs.getDouble("precio");

                // Imprimimos la fila alineada exactamente igual que la cabecera
                // %.2f convierte el número double a formato de dos decimales (ej: 1.50)
                System.out.printf("%-12d | %-30s | %-10d | %.2f €\n", idProducto, nombreProducto, stock, precio);
                contadorProductos++;
            }

            // Control Junior inteligente: Si el bucle no leyó nada, es porque la tabla está vacía
            if (contadorProductos == 0) {
                System.out.println(" [AVISO]: El catálogo comercial se encuentra actualmente vacío.");
                System.out.println("           Si no has procesado datos, recuerda usar primero la Opción 1");
                System.out.println("           para cargar el archivo masivo de inventario.");
            }

            System.out.println("=====================================================================\n");

        } catch (SQLException e) {
            System.err.println("[ERROR - BASE DATOS]: No se pudo recuperar el catálogo de la tienda.");
            System.err.println("Detalle técnico: " + e.getMessage() + "\n");
        }
    }
}
