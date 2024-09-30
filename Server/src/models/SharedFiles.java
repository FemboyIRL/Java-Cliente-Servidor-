package models;

import java.util.Date;

public class SharedFiles {

    // Atributos
    private int id;
    private int usuarioID;
    private String nombre;
    private int descargas;
    private String password; // Atributo para la contraseña
    private Date fechaExpiracion; // Atributo para la fecha de expiración

    // Constructor vacío
    public SharedFiles() {
    }

    // Constructor con parámetros
    public SharedFiles(int id, int usuarioID, String nombre, int descargas, String password, Date fechaExpiracion) {
        this.id = id;
        this.usuarioID = usuarioID;
        this.nombre = nombre;
        this.descargas = descargas;
        this.password = password; // Inicialización de la contraseña
        this.fechaExpiracion = fechaExpiracion; // Inicialización de la fecha de expiración
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUsuarioID() {
        return usuarioID;
    }

    public void setUsuarioID(int usuarioID) {
        this.usuarioID = usuarioID;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getDescargas() {
        return descargas;
    }

    public void setDescargas(int descargas) {
        this.descargas = descargas;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(Date fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    // Método para verificar si el archivo ha expirado
    public boolean haExpirado() {
        Date currentDate = new Date();
        return fechaExpiracion != null && currentDate.after(fechaExpiracion);
    }

    // Método para incrementar el número de descargas
    public void incrementarDescargas() {
        this.descargas++;
    }

    // Método para mostrar la información del archivo compartido
    @Override
    public String toString() {
        return "SharedFiles{" +
                "id=" + id +
                ", usuarioID=" + usuarioID +
                ", nombre='" + nombre + '\'' +
                ", descargas=" + descargas +
                ", password='" + password + '\'' + // Incluir la contraseña en la representación del objeto
                ", fechaExpiracion=" + fechaExpiracion +
                '}';
    }
}
