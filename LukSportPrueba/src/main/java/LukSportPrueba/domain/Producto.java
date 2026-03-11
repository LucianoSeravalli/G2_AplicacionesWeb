/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LukSportPrueba.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "producto")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdProducto")
    private Integer idProducto;

    @Column(name = "Nombre")
    private String nombre;

    @Column(name = "CantidadExistencia")
    private Integer cantidadExistencia;

    @Column(name = "PrecioUnitario")
    private Double precioUnitario;

    @ManyToOne
    @JoinColumn(name = "Categoria")
    private Categoria categoria;

    @Column(name = "Imagen")
    private String imagen;
}