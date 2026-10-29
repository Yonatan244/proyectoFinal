package org.example.modelo;

/**
 * Clase orientada a la gestión comercial en tienda.
 * Controla directamente las existencias de inventario y el precio final de cara al público.
 */
public class CatalogoTienda {

    private int id;
    private int idProducto; // Enlace directo con Ficha_productos
    private String nombreProducto;
    private int stock;
    private double precio; // Tipo de dato óptimo para valores monetarios en esta fase

    public CatalogoTienda() {
    }

    public CatalogoTienda(int id, int idProducto, String nombreProducto, int stock, double precio) {
        this.id = id;
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.stock = stock;
        this.precio = precio;
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

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
}
