package com.microservicio.pedidos.dto;

public class ProductoResponseDTO {
    private Integer id;
    private String nombre;
    private Double precio;
    private String categoria;
    private Integer inStock;
    private String descripcion;
    private String img;

    public ProductoResponseDTO() {}

    public ProductoResponseDTO(Integer id, String nombre, Double precio, String categoria,
                               Integer inStock, String descripcion, String img) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
        this.inStock = inStock;
        this.descripcion = descripcion;
        this.img = img;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Integer getInStock() { return inStock; }
    public void setInStock(Integer inStock) { this.inStock = inStock; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getImg() { return img; }
    public void setImg(String img) { this.img = img; }
}