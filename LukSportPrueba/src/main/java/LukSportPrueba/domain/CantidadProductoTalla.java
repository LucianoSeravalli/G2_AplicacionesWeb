
package LukSportPrueba.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "ProductoTalla")
public class CantidadProductoTalla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdProductoTalla")
    private Integer idProductoTalla;

    @ManyToOne
    @JoinColumn(name = "IdProducto", nullable = false)
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "IdTalla", nullable = false)
    private TallaProducto talla;

    @Column(name = "Existencia", nullable = false)
    private Integer existencia;
}