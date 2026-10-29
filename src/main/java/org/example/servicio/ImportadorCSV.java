package org.example.servicio;

import org.example.config.Conexion;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Clase de Servicio para la carga masiva automatizada.
 * Autodetecta el separador del archivo plano y procesa de forma adaptativa
 * la estructura de columnas de Excel, convirtiendo formatos locales de fecha y precio.
 */
public class ImportadorCSV {

    /**
     * Lee un archivo plano con detección automática de columnas y lo vuelca en la base de datos.
     *
     * @param rutaArchivo Nombre o ubicación física del archivo (ej: "inventario.csv").
     */
    public static void importarArchivo(String rutaArchivo) {
        String sqlInsercion = """
                INSERT INTO Tabla_general (
                    id_proveedor, nombre, apellido, telefono, email,
                    id_producto, nombre_producto, stock, precio_venta, precio_compra,
                    fecha_entrada, fecha_caducidad
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
                """;

        // Formateador para convertir las fechas de tu Excel (DD/MM/AAAA) al formato de base de datos (AAAA-MM-DD)
        DateTimeFormatter formatoExcel = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        System.out.println("\n[ETL]: Analizando estructura del archivo: " + rutaArchivo);

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sqlInsercion);
             BufferedReader lector = new BufferedReader(new FileReader(rutaArchivo))) {

            String linea;
            boolean esPrimeraLinea = true;
            int filasInsertadas = 0;
            int numeroLinea = 0;
            String separadorDetectado = null;

            while ((linea = lector.readLine()) != null) {
                numeroLinea++;

                if (linea.trim().isEmpty()) {
                    continue;
                }

                // Detección automática del delimitador
                if (separadorDetectado == null) {
                    if (linea.contains("\t")) {
                        separadorDetectado = "\t";
                    } else if (linea.contains(";")) {
                        separadorDetectado = ";";
                    } else {
                        separadorDetectado = ",";
                    }
                }

                if (esPrimeraLinea) {
                    esPrimeraLinea = false;
                    continue;
                }

                String[] datos = linea.split(separadorDetectado);

                // Como tu Excel tiene una columna extra al principio, ahora validamos que tenga mínimo 13 campos
                if (datos.length < 13) {
                    System.err.println("[WARN]: Saltando línea " + numeroLinea + " por columnas insuficientes.");
                    continue;
                }

                try {
                    // CORRECCIÓN DE ÍNDICES: Desplazamos todos los índices +1 para ignorar el número inicial (datos[0])
                    String idProveedor = datos[1].trim();
                    String nombre = datos[2].trim();
                    String apellido = datos[3].trim();
                    String telefono = datos[4].trim();
                    String email = datos[5].trim();

                    int idProducto = Integer.parseInt(datos[6].trim());
                    String nombreProducto = datos[7].trim();
                    int stock = Integer.parseInt(datos[8].trim());

                    // Reemplazamos las comas por puntos para que Java acepte los decimales sin rechistar
                    double precioVenta = Double.parseDouble(datos[9].trim().replace(",", "."));
                    double precioCompra = Double.parseDouble(datos[10].trim().replace(",", "."));

                    // CONVERSIÓN DE FECHAS: Pasamos de "DD/MM/AAAA" a un objeto LocalDate puro
                    LocalDate entradaLocalDate = LocalDate.parse(datos[11].trim(), formatoExcel);
                    LocalDate caducidadLocalDate = LocalDate.parse(datos[12].trim(), formatoExcel);

                    // Enviamos los datos ordenados al PreparedStatement
                    ps.setString(1, idProveedor);
                    ps.setString(2, nombre);
                    ps.setString(3, apellido);
                    ps.setString(4, telefono);
                    ps.setString(5, email);
                    ps.setInt(6, idProducto);
                    ps.setString(7, nombreProducto);
                    ps.setInt(8, stock);
                    ps.setDouble(9, precioVenta);
                    ps.setDouble(10, precioCompra);

                    // Convertimos el LocalDate a formato de texto estándar AAAA-MM-DD para H2
                    ps.setString(11, entradaLocalDate.toString());
                    ps.setString(12, caducidadLocalDate.toString());

                    ps.executeUpdate();
                    filasInsertadas++;

                } catch (Exception e) {
                    System.err.println("[ERROR - FILA]: Ocurrió un problema en la línea " + numeroLinea);
                    System.err.println("  -> Motivo: " + e.getMessage());
                    continue;
                }
            }

            System.out.println("[OK]: Proceso de lectura e importación finalizado.");
            System.out.println("[SISTEMA]: Se han volcado con éxito " + filasInsertadas + " filas en la tabla de tránsito.\n");

        } catch (IOException | SQLException e) {
            System.err.println("[ERROR]: Fallo crítico en el sistema de persistencia o lectura.");
            System.err.println("Detalle: " + e.getMessage() + "\n");
        }
    }
}
