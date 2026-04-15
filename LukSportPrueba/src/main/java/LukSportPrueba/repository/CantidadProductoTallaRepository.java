package LukSportPrueba.repository;

import LukSportPrueba.domain.CantidadProductoTalla;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CantidadProductoTallaRepository extends JpaRepository<CantidadProductoTalla, Integer> {

    public Optional<CantidadProductoTalla> findByProducto_IdProductoAndTalla_IdTalla(Integer idProducto, Integer idTalla);
}
