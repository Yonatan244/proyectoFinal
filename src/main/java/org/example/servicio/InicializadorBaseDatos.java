

package org.example.servicio;

import org.example.config.Conexion;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clase encargada de preparar la base de datos al arrancar la aplicación.
 * Crea las tablas necesarias si no existen, respetando el orden de relaciones.
 */
public class InicializadorBaseDatos {

    /**
     * Método principal que ejecuta la creación de todo el esquema de la base de datos.
     */
    public static void crearTablasSiNoExisten() {
        System.out.println("=== Verificando estructura de la base de datos ===");

        // Estructura Try-with-Resources: Solicita la conexión a nuestra clase de configuración
        // y asegura su cierre automático al terminar el bloque, evitando fugas de memoria.
        try (Connection con = Conexion.obtenerConexion(); // Modificado según el nombre de tu método en Conexion
             Statement st = con.createStatement()) {

            // 1. TABLA DE TRÁNSITO (STAGING)
            // Guarda los datos crudos del CSV temporalmente sin restricciones de enlace.
            String sqlTablaGeneral = """
                    CREATE TABLE IF NOT EXISTS Tabla_general (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        id_proveedor VARCHAR(50) NOT NULL,
                        nombre VARCHAR(50) NOT NULL,
                        apellido VARCHAR(50) NOT NULL,
                        telefono VARCHAR(20) NOT NULL,
                        email VARCHAR(128),
                        id_producto INT NOT NULL,
                        nombre_producto VARCHAR(100) NOT NULL,
                        stock INT NOT NULL,
                        precio_venta DOUBLE NOT NULL,
                        precio_compra DOUBLE NOT NULL,
                        fecha_entrada DATE NOT NULL,
                        fecha_caducidad DATE NOT NULL
                    );
                    """;
            st.executeUpdate(sqlTablaGeneral);
            System.out.println("[OK] Tabla de tránsito (Tabla_general) verificada.");

            // 2. TABLA MAESTRA DE PROVEEDORES
            // Se crea primero porque es la entidad independiente de la que cuelgan los productos.
            String sqlProveedores = """
                    CREATE TABLE IF NOT EXISTS Proveedores (
                        id INT AUTO_INCREMENT,
                        id_proveedor VARCHAR(50) PRIMARY KEY,
                        nombre VARCHAR(50) NOT NULL,
                        apellido VARCHAR(50) NOT NULL,
                        correo VARCHAR(128),
                        telefono VARCHAR(20) NOT NULL
                    );
                    """;
            st.executeUpdate(sqlProveedores);
            System.out.println("[OK] Tabla Maestra (Proveedores) verificada.");

            // 3. TABLA LÓGICA DE FICHA DE PRODUCTOS
            // Se le añadió la coma corregida antes de la clave foránea.
            // Enlaza cada producto con su proveedor mediante id_proveedor.
            String sqlFichaProductos = """
                    CREATE TABLE IF NOT EXISTS Ficha_productos (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        id_producto INT NOT NULL UNIQUE,
                        id_proveedor VARCHAR(50) NOT NULL,
                        nombre_producto VARCHAR(100) NOT NULL,
                        fecha_entrada DATE NOT NULL,
                        fecha_caducidad DATE NOT NULL,
                        FOREIGN KEY (id_proveedor) REFERENCES Proveedores(id_proveedor)
                    );
                    """;
            st.executeUpdate(sqlFichaProductos);
            System.out.println("[OK] Tabla Logística (Ficha_productos) verificada.");

            // 4. TABLA COMERCIAL DE CATÁLOGO DE TIENDA
            // Controla stock y precio de cara al cliente final.
            // Enlaza directamente con Ficha_productos mediante id_producto.
            String sqlCatalogoTienda = """
                    CREATE TABLE IF NOT EXISTS Catalogo_tienda (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        id_producto INT NOT NULL UNIQUE,
                        nombre_producto VARCHAR(100) NOT NULL,
                        stock INT NOT NULL,
                        precio DOUBLE NOT NULL,
                        FOREIGN KEY (id_producto) REFERENCES Ficha_productos(id_producto)
                    );
                    """;
            st.executeUpdate(sqlCatalogoTienda);
            System.out.println("[OK] Tabla Comercial (Catalogo_tienda) verificada.");

            System.out.println("=== Base de datos lista para operar ===\n");

        } catch (SQLException e) {
            System.err.println("[ERROR] Hubo un problema al inicializar las tablas de la base de datos.");
            System.err.println("Detalle técnico: " + e.getMessage());
        }
    }
}
