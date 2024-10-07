package models;

import java.util.List;

public class Comentarios {
    // Atributos
    private int usuarioID;
    private int archivoID;
    private String comentario;

    // Constructor vacío
    public Comentarios() {
    }

    // Constructor con parámetros
    public Comentarios(int usuarioID, int archivoID, String comentario) {
        this.usuarioID = usuarioID;
        this.archivoID = archivoID;
        this.comentario = comentario;
    }

    // Getters y Setters
    public int getUsuarioID() {
        return usuarioID;
    }

    public void setUsuarioID(int usuarioID) {
        this.usuarioID = usuarioID;
    }

    public int getArchivoID() {
        return archivoID;
    }

    public void setArchivoID(int archivoID) {
        this.archivoID = archivoID;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    // Método para mostrar la información del comentario
    @Override
    public String toString() {
        return "Comentarios{" +
                "usuarioID=" + usuarioID +
                ", archivoID=" + archivoID +
                '}';
    }

    public static void agregarComentario(SharedFiles archivo, Comentarios nuevoComentario) {
    List<Comentarios> listaComentarios = archivo.getComentarios();

    listaComentarios.add(nuevoComentario);

    archivo.setComentarios(listaComentarios);

    FileManager.updateSharedFileInFile(archivo);

    System.out.println("Comentario agregado correctamente al archivo: " + archivo.getNombre());
}

}