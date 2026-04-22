
package LukSportPrueba.repository;


import LukSportPrueba.domain.TallaProducto;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TallaProductoRepository extends JpaRepository<TallaProducto, Integer> {
    public Optional<TallaProducto> findByNombreTalla(String nombreTalla);
}
