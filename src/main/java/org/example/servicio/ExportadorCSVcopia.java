
package org.example.servicio;

import org.example.config.Conexion;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clase de Servicio enfocada en el proceso de salida de datos.
 * Extrae la información actual de Catalogo_tienda y genera un archivo plano
 * compatible con Microsoft Excel estructurado por tabulaciones.
 */
public class ExportadorCSVcopia {

    /**
     * Exporta los datos de la tabla Catalogo_tienda a un archivo plano de texto.
     *
     * @param nombreArchivoSalida Nombre del archivo a generar (Ej: "respaldo_inventario.csv").
     */
    public static void exportarCatalogo(String nombreArchivoSalida) {
        String sqlConsulta = "SELECT id_producto, nombre_producto, stock, precio FROM Catalogo_tienda;";

        System.out.println("\n[ETL]: Iniciando la preparación del archivo de exportación...");

        // Estructura Try-with-Resources doble para cerrar de forma automática tanto la conexión SQL
        // como el Buffer de escritura del archivo físico, previniendo bloqueos de ficheros.
        try (Connection con = Conexion.obtenerConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sqlConsulta);
             BufferedWriter escritor = new BufferedWriter(new FileWriter(nombreArchivoSalida))) {

            // PASO 1: Escribir la línea de encabezados/títulos separada por Tabuladores (\t)
            escritor.write("id_producto\tnombre_producto\tstock\tprecio_venta");
            escritor.newLine(); // Salto de línea para pasar a los registros de datos

            int filasExportadas = 0;

            // PASO 2: Recorrer el catálogo de la base de datos y volcarlo al archivo plano
            while (rs.next()) {
                int idProducto = rs.getInt("id_producto");
                String nombreProducto = rs.getString("nombre_producto");
                int stock = rs.getInt("stock");
                double precio = rs.getDouble("precio");

                // Formateamos la línea sustituyendo los puntos por comas en el precio double
                // para que tu Excel en español lo interprete como un número monetario real
                String precioFormateado = String.valueOf(precio).replace(".", ",");

                // Construimos la fila concatenando con el carácter del tabulador
                String filaFichero = idProducto + "\t" + nombreProducto + "\t" + stock + "\t" + precioFormateado;

                escritor.write(filaFichero);
                escritor.newLine(); // Pasamos a la siguiente línea del archivo de texto
                filasExportadas++;
            }

            System.out.println("[OK]: Archivo '" + nombreArchivoSalida + "' generado con éxito en la raíz del proyecto.");
            System.out.println("[SISTEMA]: Se han exportado un total de " + filasExportadas + " artículos comerciales.\n");

        } catch (IOException e) {
            System.err.println("[ERROR - ARCHIVO]: No se pudo escribir el fichero de salida.");
            System.err.println("  -> Detalle técnico: " + e.getMessage() + "\n");
        } catch (SQLException e) {
            System.err.println("[ERROR - BASE DATOS]: Falló la extracción de datos desde el Catálogo.");
            System.err.println("  -> Detalle SQL: " + e.getMessage() + "\n");
        }
    }
}
