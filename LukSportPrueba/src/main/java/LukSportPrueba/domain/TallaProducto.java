
package LukSportPrueba.domain;


import jakarta.persistence.*;
import java.util.List;
import lombok.Data;

@Data
@Entity
@Table(name = "TallasProducto")
public class TallaProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdTalla")
    private Integer idTalla;

    @Column(name = "NombreTalla", nullable = false, unique = true, length = 50)
    private String nombreTalla;

    @OneToMany(mappedBy = "talla", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CantidadProductoTalla> productos;
}
