package LukSportPrueba.repository;

import LukSportPrueba.domain.PedidoProducto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PedidoProductoRepository extends JpaRepository<PedidoProducto, Integer> {

    Optional<PedidoProducto> findByPedido_IdPedidoAndProducto_IdProducto(Integer idPedido, Integer idProducto);

    List<PedidoProducto> findByPedido_IdPedido(Integer idPedido);

    @Query("SELECT pp FROM PedidoProducto pp WHERE pp.pedido.idPedido = :idPedido AND pp.producto.idProducto = :idProducto AND pp.talla.idTalla = :idTalla")
    Optional<PedidoProducto> buscarPorPedidoProductoYTalla(@Param("idPedido") Integer idPedido,
            @Param("idProducto") Integer idProducto,
            @Param("idTalla") Integer idTalla);

    Optional<PedidoProducto> findByPedido_IdPedidoAndProducto_IdProductoAndTalla_IdTalla(
            Integer idPedido, Integer idProducto, Integer idTalla
    );

    Optional<PedidoProducto> findByIdPedidosXProductoAndPedido_Usuario_IdUsuario(Integer idPedidosXProducto, Integer idUsuario);

    List<PedidoProducto> findByPedido_Usuario_IdUsuarioAndPedido_Estado(Integer idUsuario, String estado);
}
