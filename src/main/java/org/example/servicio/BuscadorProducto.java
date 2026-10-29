
package org.example.servicio;

import org.example.config.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase de Servicio enfocada en las consultas de filtrado.
 * Permite localizar artículos específicos dentro de la base de datos
 * por ID exacto o por texto coincidente en el nombre.
 */
public class BuscadorProducto {

    /**
     * Muestra las filas del catálogo impresas de forma elegante.
     * Método auxiliar interno para no repetir código de renderizado.
     */
    private static void pintarCabeceraBusqueda() {
        System.out.println("\n---------------------------------------------------------------------");
        System.out.printf("%-12s | %-30s | %-10s | %-12s\n", "ID PRODUCTO", "NOMBRE DEL PRODUCTO", "STOCK", "PVP (PRECIO)");
        System.out.println("---------------------------------------------------------------------");
    }

    /**
     * Busca un producto por su ID numérico exacto.
     *
     * @param idProducto Código identificador a buscar.
     */
    public static void buscarPorId(int idProducto) {
        String sqlBusqueda = "SELECT id_producto, nombre_producto, stock, precio FROM Catalogo_tienda WHERE id_producto = ?;";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sqlBusqueda)) {

            ps.setInt(1, idProducto);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pintarCabeceraBusqueda();
                    System.out.printf("%-12d | %-30s | %-10d | %.2f €\n",
                            rs.getInt("id_producto"),
                            rs.getString("nombre_producto"),
                            rs.getInt("stock"),
                            rs.getDouble("precio"));
                    System.out.println("---------------------------------------------------------------------\n");
                } else {
                    System.out.println("\n[SISTEMA]: No se encontró ningún producto comercial con el ID: " + idProducto + "\n");
                }
            }

        } catch (SQLException e) {
            System.err.println("[ERROR - BÚSQUEDA]: Fallo al ejecutar la consulta por ID.");
            System.err.println("Detalle técnico: " + e.getMessage() + "\n");
        }
    }

    /**
     * Busca productos cuyo nombre contenga el texto introducido por el usuario.
     *
     * @param textoBusqueda Palabra o fragmento del nombre a rastrear.
     */
    public static void buscarPorNombre(String textoBusqueda) {
        // Usamos el operador LIKE junto con UPPER para que la búsqueda sea insensible a mayúsculas/minúsculas
        String sqlBusqueda = "SELECT id_producto, nombre_producto, stock, precio FROM Catalogo_tienda WHERE UPPER(nombre_producto) LIKE ?;";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sqlBusqueda)) {

            // El comodín % permite encontrar el texto en cualquier parte de la cadena (ej: %leche%)
            ps.setString(1, "%" + textoBusqueda.trim().toUpperCase() + "%");

            try (ResultSet rs = ps.executeQuery()) {
                int coincidencias = 0;

                while (rs.next()) {
                    if (coincidencias == 0) {
                        pintarCabeceraBusqueda();
                    }
                    System.out.printf("%-12d | %-30s | %-10d | %.2f €\n",
                            rs.getInt("id_producto"),
                            rs.getString("nombre_producto"),
                            rs.getInt("stock"),
                            rs.getDouble("precio"));
                    coincidencias++;
                }

                if (coincidencias > 0) {
                    System.out.println("---------------------------------------------------------------------");
                    System.out.println("[SISTEMA]: Se han localizado " + coincidencias + " productos coincidentes.\n");
                } else {
                    System.out.println("\n[SISTEMA]: No se encontraron productos que coincidan con: \"" + textoBusqueda + "\"\n");
                }
            }

        } catch (SQLException e) {
            System.err.println("[ERROR - BÚSQUEDA]: Fallo al ejecutar la consulta por término de texto.");
            System.err.println("Detalle técnico: " + e.getMessage() + "\n");
        }
    }
}
