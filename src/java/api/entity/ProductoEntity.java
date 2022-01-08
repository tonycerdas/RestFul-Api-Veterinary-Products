/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package api.entity;

public class ProductoEntity {

    public ProductoEntity(Integer idproducto, String nombre, String descripcion, String url, Double precio, Integer cantinventario) {
        this.idproducto = idproducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.url = url;
        this.precio = precio;
        this.cantinventario = cantinventario;
    }

    public Integer getIdproducto() {
        return idproducto;
    }

    public void setIdproducto(Integer idproducto) {
        this.idproducto = idproducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Integer getCantinventario() {
        return cantinventario;
    }

    public void setCantinventario(Integer cantinventario) {
        this.cantinventario = cantinventario;
    }
    Integer idproducto; 
    String nombre; 
    String descripcion;
    String url;
    Double precio;
    Integer cantinventario;
}
