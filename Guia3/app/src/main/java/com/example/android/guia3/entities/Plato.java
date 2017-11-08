package com.example.android.guia3.entities;

public class Plato {
    // Plato Table Columns
    public static final String TABLE_NAME = "plato";
    public static final String KEY_PLATO_ID = "id";

    public static final String KEY_PLATO_NOMBRE = "nombre";
    public static final String KEY_PLATO_PRECIO = "precio";
    private long id;
    private String nombre;
    private int precio;
    private String imagen;

    public Plato(long id, String nombre,int precio, String imagen){
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.imagen = imagen;
    }
    public void setId(long id){
        this.id = id;
    }
    public long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPrecio() {
        return precio;
    }

    public String getImagen() {
        return imagen;
    }

}