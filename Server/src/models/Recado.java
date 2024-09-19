/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;
import org.json.simple.JSONObject;

public class Recado {
    private int id;
    private String usuarioDe;
    private String usuarioPara;
    private String contenidoRecado;

    // Constructor de la clase Recado
    public Recado(int id, String usuarioDe, String usuarioPara, String contenidoRecado) {
        this.id = id;
        this.usuarioDe = usuarioDe;
        this.usuarioPara = usuarioPara;
        this.contenidoRecado = contenidoRecado;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsuarioDe() {
        return usuarioDe;
    }

    public void setUsuarioDe(String usuarioDe) {
        this.usuarioDe = usuarioDe;
    }

    public String getUsuarioPara() {
        return usuarioPara;
    }

    public void setUsuarioPara(String usuarioPara) {
        this.usuarioPara = usuarioPara;
    }

    public String getContenidoRecado() {
        return contenidoRecado;
    }

    public void setContenidoRecado(String contenidoRecado) {
        this.contenidoRecado = contenidoRecado;
    }
    
    // Método para convertir un JSONObject en un Recado
    public static Recado fromJson(JSONObject json) {
        int id = ((Long) json.get("id")).intValue();
        String usuarioDe = (String) json.get("usuarioDe"); 
        String usuarioPara = (String) json.get("usuarioPara");
        String contenido = (String) json.get("contenido");

        return new Recado(id, usuarioDe, usuarioPara, contenido);
    }
   
    // Método toString para representar el recado como un String
    @Override
    public String toString() {
        return "Recado{" +
                "id=" + id +
                ", usuarioDe=" + usuarioDe +
                ", usuarioPara=" + usuarioPara +
                ", contenidoRecado='" + contenidoRecado + '\'' +
                '}';
    }
}
