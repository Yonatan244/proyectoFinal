package org.example;

import org.example.servicio.*;

import java.util.Scanner;

/**
 * Capa de Presentación.
 * Punto de entrada único del sistema con el menú interactivo ordenado del 1 al 9.
 */
public class Main {

    public static void main(String[] args) {
        // PASO 1: Asegurar que las tablas existan en H2 antes de iniciar la aplicación
        InicializadorBaseDatos.crearTablasSiNoExisten();

        System.out.println("=========================================");
        System.out.println("   === GESTOR DE INVENTARIO ===   ");
        System.out.println("=========================================");

        Scanner teclado = new Scanner(System.in);
        boolean continuarPrograma = true;

        // PASO 2: Bucle principal con la numeración consecutiva
        while (continuarPrograma) {
            System.out.println("----------- MENÚ DE OPERACIONES -----------");
            System.out.println("1. Importar Productos (.csv)");
            System.out.println("2. Ver catálogo de la tienda");
            System.out.println("3. Agregar producto");
            System.out.println("4. Buscar Producto");
            System.out.println("5. Actualizar Producto");
            System.out.println("6. Eliminar Producto");
            System.out.println("7. Exportar Productos (.csv)");
            System.out.println("8. Eliminar todos los productos");
            System.out.println("9. Salir");
            System.out.print("Seleccione una opción (1-9): ");

            // Validamos que el usuario introduzca un número entero para prevenir fallos
            if (teclado.hasNextInt()) {
                int opcionSeleccionada = teclado.nextInt();
                teclado.nextLine(); // Limpiar el salto de línea pendiente en el búfer

                switch (opcionSeleccionada) {
                    case 1:
                        System.out.println("\n[SISTEMA]: Has seleccionado 'Importar Productos (.csv)'.");
                        System.out.print("¿Desea iniciar la carga masiva desde el archivo 'inventario.csv'? (S/N): ");
                        String respuestaConfirmacion = teclado.nextLine().trim().toUpperCase();

                        if (respuestaConfirmacion.equals("S")) {
                            // 1. Lee el archivo y llena la tabla de tránsito
                            ImportadorCSV.importarArchivo("inventario.csv");

                            // 2. Procesa, elimina duplicados y distribuye en las tablas relacionales de forma inmediata
                            ProcesadorSegmentacion.ejecutarSegmentacion();
                        } else {
                            System.out.println("[SISTEMA]: Importación masiva cancelada por el usuario.\n");
                        }
                        break;

                    case 2:
                        // LLAMADA REAL: Invoca al consultor para mostrar la tabla comercial en la consola
                        ConsultorCatalogo.mostrarCatalogo();
                        break;

                    case 3:
                        System.out.println("\n=========================================");
                        System.out.println("       ALTA MANUAL DE NUEVO PRODUCTO     ");
                        System.out.println("=========================================");

                        try {
                            // Captura del ID Único del producto con validación elemental
                            System.out.print("1. Introduzca el ID numérico del producto: ");
                            while (!teclado.hasNextInt()) {
                                System.err.print("[ERROR]: El ID debe ser un número entero. Pruebe de nuevo: ");
                                teclado.next();
                            }
                            int nuevoIdProducto = teclado.nextInt();
                            teclado.nextLine(); // Limpiar el búfer

                            // Captura del ID del Proveedor
                            System.out.print("2. Introduzca el código del proveedor (Ej: PROV01, PROV02): ");
                            String proveedorAsociado = teclado.nextLine().trim();

                            // Captura del Nombre Comercial
                            System.out.print("3. Nombre comercial del producto: ");
                            String nombreNuevoProducto = teclado.nextLine().trim();

                            // Captura de las existencias iniciales (Stock)
                            System.out.print("4. Unidades de stock iniciales: ");
                            while (!teclado.hasNextInt()) {
                                System.err.print("[ERROR]: El stock debe ser un número entero. Pruebe de nuevo: ");
                                teclado.next();
                            }
                            int stockInicial = teclado.nextInt();
                            teclado.nextLine(); // Limpiar el búfer

                            // Captura del precio final (PVP)
                            System.out.print("5. Precio de venta al público (Ej: 2,50): ");
                            while (!teclado.hasNextDouble()) {
                                System.err.print("[ERROR]: El precio debe ser un número o decimal. Pruebe de nuevo: ");
                                teclado.next();
                            }
                            double precioVentaPublico = teclado.nextDouble();
                            teclado.nextLine(); // Limpiar el búfer

                            // EJECUCIÓN: Llamamos al servicio de persistencia enviando los datos capturados
                            RegistradorProducto.registrarManualmente(
                                    nuevoIdProducto,
                                    proveedorAsociado,
                                    nombreNuevoProducto,
                                    stockInicial,
                                    precioVentaPublico
                            );

                        } catch (Exception e) {
                            System.err.println("[ERROR]: Error inesperado durante la captura de datos en consola.");
                        }
                        System.out.println("=========================================\n");
                        break;

                    case 4:
                        System.out.println("\n=========================================");
                        System.out.println("          MÓDULO DE BÚSQUEDA AVANZADA    ");
                        System.out.println("=========================================");
                        System.out.println("A. Buscar por ID único del producto");
                        System.out.println("B. Buscar por palabra clave en el nombre");
                        System.out.print("Elija la modalidad de búsqueda (A/B): ");
                        String modalidad = teclado.nextLine().trim().toUpperCase();

                        if (modalidad.equals("A")) {
                            System.out.print("Introduzca el ID numérico del producto: ");
                            while (!teclado.hasNextInt()) {
                                System.err.print("[ERROR]: El ID debe ser un número entero. Pruebe de nuevo: ");
                                teclado.next();
                            }
                            int idABuscar = teclado.nextInt();
                            teclado.nextLine(); // Limpiar el búfer

                            // Invocamos la búsqueda exacta por código
                            BuscadorProducto.buscarPorId(idABuscar);

                        } else if (modalidad.equals("B")) {
                            System.out.print("Escriba el nombre o término a buscar (Ej: Leche): ");
                            String terminoABuscar = teclado.nextLine().trim();

                            // Invocamos la búsqueda por coincidencia textual
                            BuscadorProducto.buscarPorNombre(terminoABuscar);

                        } else {
                            System.err.println("\n[ERROR]: Modalidad no reconocida. Regresando al menú principal.\n");
                        }
                        break;

                    case 5:
                        System.out.println("\n=========================================");
                        System.out.println("       MODIFICACIÓN DE DATOS DE PRODUCTO ");
                        System.out.println("=========================================");

                        try {
                            // 1. Solicitar el ID del producto a modificar
                            System.out.print("Introduzca el ID del producto que desea actualizar: ");
                            while (!teclado.hasNextInt()) {
                                System.err.print("[ERROR]: El ID debe ser un número entero. Pruebe de nuevo: ");
                                teclado.next();
                            }
                            int idAEditar = teclado.nextInt();
                            teclado.nextLine(); // Limpiar el búfer

                            // 2. Solicitar el nuevo stock
                            System.out.print("Introduzca la nueva cantidad de STOCK: ");
                            while (!teclado.hasNextInt()) {
                                System.err.print("[ERROR]: El stock debe ser un número entero. Pruebe de nuevo: ");
                                teclado.next();
                            }
                            int nuevoStock = teclado.nextInt();
                            teclado.nextLine(); // Limpiar el búfer

                            // 3. Solicitar el nuevo precio
                            System.out.print("Introduzca el nuevo PRECIO de venta (Ej: 1.99): ");
                            while (!teclado.hasNextDouble()) {
                                System.err.print("[ERROR]: El precio debe ser un número o decimal. Pruebe de nuevo: ");
                                teclado.next();
                            }
                            double nuevoPrecio = teclado.nextDouble();
                            teclado.nextLine(); // Limpiar el búfer

                            // 4. Ejecución: Invocamos al servicio de actualización
                            ActualizadorProducto.actualizarDatosComerciales(idAEditar, nuevoStock, nuevoPrecio);

                        } catch (Exception e) {
                            System.err.println("[ERROR]: Error al capturar los datos de actualización en la terminal.");
                        }
                        System.out.println("=========================================\n");
                        break;

                    case 6:
                        System.out.println("\n=========================================");
                        System.out.println("          ELIMINAR PRODUCTO INDIVIDUAL   ");
                        System.out.println("=========================================");

                        try {
                            // 1. Capturar el ID del artículo que se va a borrar
                            System.out.print("Introduzca el ID del producto que desea ELIMINAR: ");
                            while (!teclado.hasNextInt()) {
                                System.err.print("[ERROR]: El ID debe ser un número entero. Pruebe de nuevo: ");
                                teclado.next();
                            }
                            int idAEliminar = teclado.nextInt();
                            teclado.nextLine(); // Limpiar el búfer de la terminal

                            // 2. Pedir confirmación explícita de seguridad al usuario
                            System.out.print("¿Está seguro de que desea borrar permanentemente el producto ID " + idAEliminar + "? (S/N): ");
                            String confirmacionBorrado = teclado.nextLine().trim().toUpperCase();

                            if (confirmacionBorrado.equals("S")) {
                                // 3. Ejecución: Invocamos al servicio de eliminación relacional
                                EliminadorProducto.eliminarIndividual(idAEliminar);
                            } else {
                                System.out.println("[SISTEMA]: Operación de eliminación cancelada por seguridad.\n");
                            }

                        } catch (Exception e) {
                            System.err.println("[ERROR]: Error al procesar la baja del producto en la consola.");
                        }
                        System.out.println("=========================================\n");
                        break;

                    case 7:
                        System.out.println("\n=========================================");
                        System.out.println("        EXPORTAR CATÁLOGO A EXCEL (.csv) ");
                        System.out.println("=========================================");
                        System.out.print("¿Desea generar un archivo de respaldo con el inventario actual? (S/N): ");
                        String confirmarExportacion = teclado.nextLine().trim().toUpperCase();

                        if (confirmarExportacion.equals("S")) {
                            // Definimos el nombre del archivo de salida
                            String archivoRespaldo = "respaldo_inventario.csv";

                            // INTEGRACIÓN REAL: Llamamos al servicio exportador de datos
                            ExportadorCSV.exportarCatalogo(archivoRespaldo);
                        } else {
                            System.out.println("[SISTEMA]: Operación de exportación cancelada por el usuario.\n");
                        }
                        break;

                    case 8:
                        System.out.println("\n=========================================");
                        System.out.println("   ¡PELIGRO! VACIADO TOTAL DE PRODUCTOS  ");
                        System.out.println("=========================================");
                        System.out.println("Esta acción borrará permanentemente:");
                        System.out.println(" - El Catálogo Comercial de la Tienda");
                        System.out.println(" - Todas las Fichas Técnicas de Productos");
                        System.out.println(" - Todo el historial de la Tabla de Tránsito");
                        System.out.print("\n¿Está ABSOLUTAMENTE seguro de continuar? (S/N): ");
                        String confirmarVaciado = teclado.nextLine().trim().toUpperCase();

                        if (confirmarVaciado.equals("S")) {
                            // INTEGRACIÓN REAL: Invocamos al purgador de tablas
                            VaciadorInventario.vaciarTablasDeProductos();
                        } else {
                            System.out.println("[SISTEMA]: Operación de borrado masivo cancelada de forma segura.\n");
                        }
                        break;

                    case 9:
                        System.out.println("\n[SISTEMA]: Cerrando el Gestor de Inventario...");
                        System.out.println("Base de datos guardada localmente en 'proyectoFinal.mv.db'. ¡Hasta pronto!");
                        continuarPrograma = false; // Finaliza el bucle y cierra la consola de forma limpia
                        break;

                    default:
                        System.err.println("\n[ERROR]: La opción introducida no está en el rango 1-9. Pruebe de nuevo.\n");
                        break;
                }
            } else {
                // Evitamos el cuelgue del programa si el usuario escribe letras en vez de números
                System.err.println("\n[ERROR]: Entrada no válida. Debe introducir un número entero del menú.\n");
                teclado.next(); // Descarta la entrada incorrecta para limpiar el búfer
            }
        }

        teclado.close(); // Cerramos el scanner de forma limpia al terminar el programa
    }
}
