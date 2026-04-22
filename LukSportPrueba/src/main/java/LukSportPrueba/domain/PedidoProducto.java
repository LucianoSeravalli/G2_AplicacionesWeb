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
    @JoinColumn(name = "idPedidos", nullable = false)
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "idProductos", nullable = false)
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "idTalla")
    private TallaProducto talla;

    @Column(name = "Cantidad")
    private Integer cantidad;
}