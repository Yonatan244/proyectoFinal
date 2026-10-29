package org.example;

import org.example.servicio.InicializadorBaseDatos;
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
                        System.out.println("\n[OPERACIÓN]: Iniciando proceso de importación desde archivo plano...");
                        System.out.println("(Próximamente: Lógica del lector CSV y volcado en Tabla_general)\n");
                        break;

                    case 2:
                        System.out.println("\n[CONSULTA]: Cargando catálogo comercial desde la base de datos...");
                        System.out.println("(Próximamente: SELECT * FROM Catalogo_tienda)\n");
                        break;

                    case 3:
                        System.out.println("\n[OPERACIÓN]: Formulario para registrar un nuevo producto de forma manual...");
                        System.out.println("(Próximamente: Captura de atributos e INSERT relacional)\n");
                        break;

                    case 4:
                        System.out.println("\n[CONSULTA]: Búsqueda avanzada de producto...");
                        System.out.println("(Próximamente: Búsqueda por ID o nombre con filtros SQL)\n");
                        break;

                    case 5:
                        System.out.println("\n[OPERACIÓN]: Modificación de datos de un producto...");
                        System.out.println("(Próximamente: UPDATE de existencias o precios en la tabla)\n");
                        break;

                    case 6:
                        System.out.println("\n[OPERACIÓN]: Eliminación individual de un artículo...");
                        System.out.println("(Próximamente: DELETE con resguardo de integridad referencial)\n");
                        break;

                    case 7:
                        System.out.println("\n[OPERACIÓN]: Generando archivo de respaldo de inventario...");
                        System.out.println("(Próximamente: Escritura de datos relacionales a un nuevo .csv)\n");
                        break;

                    case 8:
                        System.out.println("\n[¡PELIGRO!]: Has solicitado borrar todo el inventario del sistema.");
                        System.out.print("¿Estás absolutamente seguro de vaciar todas las tablas? (S/N): ");
                        String confirmarVaciado = teclado.nextLine().trim().toUpperCase();

                        if (confirmarVaciado.equals("S")) {
                            System.out.println("[OPERACIÓN]: Ejecutando limpieza total...");
                            System.out.println("(Próximamente: TRUNCATE o DELETE en cascada de las tablas)\n");
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
