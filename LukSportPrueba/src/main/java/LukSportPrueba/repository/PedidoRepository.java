package LukSportPrueba.repository;

import LukSportPrueba.domain.Pedido;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    Optional<Pedido> findByUsuario_IdUsuarioAndEstado(Integer idUsuario, String estado);

    List<Pedido> findByUsuario_IdUsuarioAndEstadoOrderByIdPedidoDesc(Integer idUsuario, String estado);

    Optional<Pedido> findByIdPedidoAndUsuario_IdUsuario(Integer idPedido, Integer idUsuario);
}
