package org.example.servicio;

import org.example.config.Conexion;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Clase de Servicio enfocada en el proceso de exportación circular.
 * Genera un archivo plano con la estructura exacta que requiere el ImportadorCSV
 * para que el sistema pueda reimportarlo sin desajustes de columnas o fechas.
 */
public class ExportadorCSV {

    /**
     * Exporta el contenido completo de la Tabla_general con la estructura idéntica de Excel.
     *
     * @param nombreArchivoSalida Nombre del archivo a generar (Ej: "inventario.csv").
     */
    public static void exportarCatalogo(String nombreArchivoSalida) {
        String sqlConsulta = """
                SELECT id_proveedor, nombre, apellido, telefono, email,
                       id_producto, nombre_producto, stock, precio_venta, precio_compra,
                       fecha_entrada, fecha_caducidad 
                FROM Tabla_general;
                """;

        // Formateador para convertir la fecha interna (AAAA-MM-DD) al formato europeo (DD/MM/AAAA) que lee tu Excel
        DateTimeFormatter formatoExcel = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        System.out.println("\n[ETL]: Iniciando exportación compatible y circular...");

        try (Connection con = Conexion.obtenerConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sqlConsulta);
             BufferedWriter escritor = new BufferedWriter(new FileWriter(nombreArchivoSalida))) {

            // PASO 1: Escribir la cabecera exacta de columnas separadas por el Tabulador (\t)
            // Añadimos una columna vacía o un título inicial para simular el índice consecutivo del Excel original
            String encabezados = "indice\tid_proveedor\tnombre\tapellido\ttelefono\temail\t"
                    + "id_producto\tnombre_producto\tstock\tprecio_venta\tprecio_compra\t"
                    + "fecha_entrada\tfecha_caducidad";
            escritor.write(encabezados);
            escritor.newLine();

            int contadorFilas = 0;

            // PASO 2: Recorrer y formatear cada campo de la Tabla_general
            while (rs.next()) {
                contadorFilas++; // Este número será el "id" o índice inicial de la columna 0

                String idProveedor = rs.getString("id_proveedor");
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellido");
                String telefono = rs.getString("telefono");
                String email = rs.getString("email");
                int idProducto = rs.getInt("id_producto");
                String nombreProducto = rs.getString("nombre_producto");
                int stock = rs.getInt("stock");

                // Precios con comas locales para que coincidan con la configuración detectada
                String precioVenta = String.valueOf(rs.getDouble("precio_venta")).replace(".", ",");
                String precioCompra = String.valueOf(rs.getDouble("precio_compra")).replace(".", ",");

                // PARSEO DE FECHAS: Convertimos el texto nativo de la BD a objetos LocalDate para formatearlos de vuelta a Europa
                String entradaBD = rs.getString("fecha_entrada");
                String caducidadBD = rs.getString("fecha_caducidad");

                String fechaEntradaFormateada = LocalDate.parse(entradaBD).format(formatoExcel);
                String fechaCaducidadFormateada = LocalDate.parse(caducidadBD).format(formatoExcel);

                // PASO 3: Construcción de la línea final con el índice consecutivo al inicio
                String filaFichero = contadorFilas + "\t"
                        + idProveedor + "\t"
                        + nombre + "\t"
                        + apellido + "\t"
                        + telefono + "\t"
                        + email + "\t"
                        + idProducto + "\t"
                        + nombreProducto + "\t"
                        + stock + "\t"
                        + precioVenta + "\t"
                        + precioCompra + "\t"
                        + fechaEntradaFormateada + "\t"
                        + fechaCaducidadFormateada;

                escritor.write(filaFichero);
                escritor.newLine();
            }

            System.out.println("[OK]: Respaldo maestro e interoperable generado con éxito.");
            System.out.println("[SISTEMA]: Se ha creado '" + nombreArchivoSalida + "' con " + contadorFilas + " filas listas para reimportar.\n");

        } catch (IOException e) {
            System.err.println("[ERROR - ARCHIVO]: No se pudo escribir el fichero de respaldo circular.");
            System.err.println("  -> Detalle: " + e.getMessage() + "\n");
        } catch (SQLException e) {
            System.err.println("[ERROR - BASE DATOS]: Error de lectura en la Tabla_general.");
            System.err.println("  -> Detalle SQL: " + e.getMessage() + "\n");
        }
    }
}