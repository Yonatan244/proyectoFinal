package org.example.modelo;

import java.time.LocalDate;

/**
 * Objeto de Tránsito (Staging).
 * Modela una fila completa y cruda proveniente del archivo masivo CSV.
 * Contiene datos desnormalizados listos para ser procesados en el proceso ETL.
 */
public class TablaGeneral {

    private int id;
    private String idProveedor;
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    private int idProducto;
    private String nombreProducto;
    private int stock;
    private double precioVenta;
    private double precioCompra;
    private LocalDate fechaEntrada;
    private LocalDate fechaCaducidad;

    public TablaGeneral() {
    }

    public TablaGeneral(int id, String idProveedor, String nombre, String apellido, String telefono, String email,
                        int idProducto, String nombreProducto, int stock, double precioVenta, double precioCompra,
                        LocalDate fechaEntrada, LocalDate LocalDate, LocalDate fechaCaducidad) {
        this.id = id;
        this.idProveedor = idProveedor;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.stock = stock;
        this.precioVenta = precioVenta;
        this.precioCompra = precioCompra;
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

    public String getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(String idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(double precioCompra) {
        this.precioCompra = precioCompra;
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
