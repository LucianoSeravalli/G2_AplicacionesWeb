package LukSportPrueba.repository;

import LukSportPrueba.domain.CantidadProductoTalla;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CantidadProductoTallaRepository extends JpaRepository<CantidadProductoTalla, Integer> {

    public Optional<CantidadProductoTalla> findByProducto_IdProductoAndTalla_IdTalla(Integer idProducto, Integer idTalla);
    public List<CantidadProductoTalla> findByProducto_IdProductoAndExistenciaGreaterThan(Integer idProducto, Integer existencia);
    public List<CantidadProductoTalla> findByProducto_IdProducto(Integer idProducto);

}
