package org.example.servicio;

import org.example.config.Conexion;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Clase de Servicio enfocada en el proceso de carga masiva.
 * Lee un archivo plano .csv delimitado por punto y coma y lo vuelca
 * directamente en la Tabla_general de la base de datos H2.
 */
public class ImportadorCSVcopia1 {

    /**
     * Lee las filas de un archivo CSV y las vuelca en la Tabla_general.
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

        System.out.println("\n[ETL]: Iniciando la lectura del archivo: " + rutaArchivo);

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sqlInsercion);
             BufferedReader lector = new BufferedReader(new FileReader(rutaArchivo))) {

            String linea;
            boolean esPrimeraLinea = true;
            int filasInsertadas = 0;
            int numeroLinea = 0;

            while ((linea = lector.readLine()) != null) {
                numeroLinea++;

                // Saltamos la fila de encabezados
                if (esPrimeraLinea) {
                    esPrimeraLinea = false;
                    continue;
                }

                // Si la línea está completamente vacía, la saltamos
                if (linea.trim().isEmpty()) {
                    continue;
                }

                // Separamos los valores usando el punto y coma (;) de Excel
                String[] datos = linea.split("\t");

                // Validación Junior de seguridad: nos aseguramos de contar con las columnas necesarias
                if (datos.length < 12) {
                    System.err.println("[WARN]: Saltando línea " + numeroLinea + " por estar incompleta o vacía.");
                    continue;
                }

                try {
                    // Mapeamos y aplicamos .trim() individual a cada celda para eliminar espacios fantasmas
                    String idProveedor = datos[0].trim();
                    String nombre = datos[1].trim();
                    String apellido = datos[2].trim();
                    String telefono = datos[3].trim();
                    String email = datos[4].trim();

                    // Aquí estaba el cruce: aseguramos los índices exactos del arreglo
                    int idProducto = Integer.parseInt(datos[5].trim());
                    String nombreProducto = datos[6].trim();
                    int stock = Integer.parseInt(datos[7].trim());
                    double precioVenta = Double.parseDouble(datos[8].trim());
                    double precioCompra = Double.parseDouble(datos[9].trim());

                    String fechaEntrada = datos[10].trim();
                    String fechaCaducidad = datos[11].trim();

                    // Pasamos los valores limpios al PreparedStatement
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
                    ps.setString(11, fechaEntrada);
                    ps.setString(12, fechaCaducidad);

                    // Ejecutamos la inserción
                    ps.executeUpdate();
                    filasInsertadas++;

                } catch (NumberFormatException e) {
                    System.err.println("[ERROR - FILA]: Falló la conversión numérica en la línea " + numeroLinea);
                    System.err.println("Comprueba los valores de esa fila en tu Excel. Detalle: " + e.getMessage());
                    // Estampamos un break o continue según prefieras; usamos continue para no detener todo el archivo
                    continue;
                }
            }

            System.out.println("[OK]: Proceso de lectura completado.");
            System.out.println("[SISTEMA]: Se han volcado con éxito " + filasInsertadas + " filas en la tabla de tránsito.\n");

        } catch (IOException e) {
            System.err.println("[ERROR - ARCHIVO]: No se pudo leer el fichero. Verifique que exista en la raíz.");
            System.err.println("Detalle: " + e.getMessage() + "\n");
        } catch (SQLException e) {
            System.err.println("[ERROR - BASE DATOS]: Fallo al insertar los registros en la Tabla_general.");
            System.err.println("Detalle SQL: " + e.getMessage() + "\n");
        }
    }
}
