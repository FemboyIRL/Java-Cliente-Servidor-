package models;


import java.util.Date;
import java.util.List;

public class SharedFiles {

    // Atributos
    private int id;
    private int usuarioID;
    private String nombre;
    private int descargas;
    private String password;
    private Date fechaExpiracion;
    private List<Comentarios> comentarios; // Atributo para los comentarios

    // Constructor vacío
    public SharedFiles() {
    }

    // Constructor con parámetros
    public SharedFiles(int id, int usuarioID, String nombre, int descargas, String password, Date fechaExpiracion, List<Comentarios> comentarios) {
        this.id = id;
        this.usuarioID = usuarioID;
        this.nombre = nombre;
        this.descargas = descargas;
        this.password = password;
        this.fechaExpiracion = fechaExpiracion;
        this.comentarios = comentarios; // Inicialización de los comentarios
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

    public List<Comentarios> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<Comentarios> comentarios) {
        this.comentarios = comentarios;
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
                ", password='" + password + '\'' +
                ", fechaExpiracion=" + fechaExpiracion +
                ", comentarios=" + comentarios + // Incluir los comentarios en la representación del objeto
                '}';
    }
}