package org.example.modelo;

/**
 * Clase que representa a la entidad Proveedor en el sistema.
 * Actúa como un POJO puro (Plain Old Java Object) para transportar datos
 * sin contener lógica de negocio ni conexiones técnicas a la base de datos.
 */
public class Proveedores {

    // Atributos privados para garantizar el encapsulamiento
    private int id;
    private String idProveedor; // Clave primaria alfanumérica
    private String nombre;
    private String apellido;
    private String correo;
    private String telefono;

    // Constructor vacío obligatorio para frameworks y buenas prácticas de persistencia
    public Proveedores() {
    }

    // Constructor completo para instanciar el objeto con datos rápidamente
    public Proveedores(int id, String idProveedor, String nombre, String apellido, String correo, String telefono) {
        this.id = id;
        this.idProveedor = idProveedor;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.telefono = telefono;
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

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
