package org.example.modelo;

import java.time.LocalDate;

/**
 * Clase que representa la Ficha Técnica de un producto.
 * Mapea los datos lógicos internos y su relación con el proveedor.
 */
public class TablaProductos {

    private int id;
    private int idProducto;
    private String idProveedor; // Enlace clave foránea hacia Proveedores
    private String nombreProducto;
    private LocalDate fechaEntrada; // Uso de LocalDate para fechas limpias sin horas
    private LocalDate fechaCaducidad;

    public TablaProductos() {
    }

    public TablaProductos(int id, int idProducto, String idProveedor, String nombreProducto, LocalDate fechaEntrada, LocalDate fechaCaducidad) {
        this.id = id;
        this.idProducto = idProducto;
        this.idProveedor = idProveedor;
        this.nombreProducto = nombreProducto;
        this.fechaEntrada = fechaEntrada;
        this.fechaCaducidad = fechaCaducidad;
    }

    // --- Métodos Getters y Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(String idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public LocalDate getFechaEntrada() {
        return fechaEntrada;
    }

    public void setFechaEntrada(LocalDate fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }

    public LocalDate getFechaCaducidad() {
        return fechaCaducidad;
    }

    public void setFechaCaducidad(LocalDate fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }
}
