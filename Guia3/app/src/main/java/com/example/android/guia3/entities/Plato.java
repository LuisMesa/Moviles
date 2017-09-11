package com.example.android.guia3.entities;

public class Plato {
    private int id;
    private String nombre;
    private int precio;
    private String imagen;

    public Plato(int id, String nombre,int precio, String imagen){
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.imagen = imagen;
    }
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public int getPrecio() { return precio; }
    public String getImagen() { return imagen; }
}
