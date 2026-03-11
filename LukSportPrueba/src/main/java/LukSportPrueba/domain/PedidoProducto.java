/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LukSportPrueba.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "PedidosXProducto")
public class PedidoProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPedidosXProducto")
    private Integer idPedidosXProducto;

    @ManyToOne
    @JoinColumn(name = "idPedidos")
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "idProductos")
    private Producto producto;

    @Column(name = "cantidad")
    private Integer cantidad;
}