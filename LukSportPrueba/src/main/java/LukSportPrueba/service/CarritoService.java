package LukSportPrueba.service;

import LukSportPrueba.domain.Pedido;
import LukSportPrueba.domain.PedidoProducto;
import LukSportPrueba.domain.Producto;
import LukSportPrueba.domain.Usuario;
import LukSportPrueba.domain.TallaProducto;
import LukSportPrueba.domain.CantidadProductoTalla;

import LukSportPrueba.repository.CantidadProductoTallaRepository;
import LukSportPrueba.repository.TallaProductoRepository;
import LukSportPrueba.repository.PedidoRepository;
import LukSportPrueba.repository.PedidoProductoRepository;
import LukSportPrueba.repository.ProductoRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarritoService {

    @Autowired
    private CantidadProductoTallaRepository cantidadProductoTallaRepository;

    @Autowired
    private TallaProductoRepository tallaProductoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PedidoProductoRepository pedidoProductoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    public String agregarProductoAlCarrito(Integer idUsuario, Integer idProducto, Integer idTalla, Integer cantidad, String tipoEntrega) {
        try {
            if (cantidad == null || cantidad <= 0) {
                return "La cantidad debe ser mayor a 0.";
            }

            if (tipoEntrega == null || tipoEntrega.isBlank()) {
                return "Debes seleccionar el tipo de entrega.";
            }

            Producto producto = productoRepository.findById(idProducto).orElse(null);
            if (producto == null) {
                return "Producto no encontrado.";
            }

            TallaProducto talla = tallaProductoRepository.findById(idTalla).orElse(null);
            if (talla == null) {
                return "Talla no encontrada.";
            }

            if (producto.getCantidadExistencia() == null || producto.getCantidadExistencia() <= 0) {
                return "Este producto no tiene existencias disponibles.";
            }

            // Validación de producto con tallas / sin tallas
            String nombreTalla = talla.getNombreTalla();

            if (Boolean.FALSE.equals(producto.getTieneTalla())
                    && !"Este producto no tiene tallas".equalsIgnoreCase(nombreTalla)) {
                return "Este producto fue registrado sin tallas.";
            }

            if (Boolean.TRUE.equals(producto.getTieneTalla())
                    && "Este producto no tiene tallas".equalsIgnoreCase(nombreTalla)) {
                return "Debes seleccionar una talla válida para este producto.";
            }

            // Validar existencia real por talla
            CantidadProductoTalla existencia = cantidadProductoTallaRepository
                    .findByProducto_IdProductoAndTalla_IdTalla(idProducto, idTalla)
                    .orElse(null);

            if (existencia == null || existencia.getExistencia() == null || existencia.getExistencia() <= 0) {
                return "No hay existencias disponibles para la talla seleccionada.";
            }

            Pedido carrito = pedidoRepository
                    .findByUsuario_IdUsuarioAndEstado(idUsuario, "carrito")
                    .orElse(null);

            if (carrito == null) {
                carrito = new Pedido();
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(idUsuario);

                carrito.setUsuario(usuario);
                carrito.setTotal(0.0);
                carrito.setEstado("carrito");
                carrito.setTipoEntrega(tipoEntrega);
                carrito = pedidoRepository.save(carrito);
            } else {
                carrito.setTipoEntrega(tipoEntrega);
                carrito = pedidoRepository.save(carrito);
            }

            PedidoProducto item = pedidoProductoRepository
                    .buscarPorPedidoProductoYTalla(carrito.getIdPedido(), idProducto, idTalla)
                    .orElse(null);

            int cantidadFinal = cantidad;

            if (item != null) {
                cantidadFinal = item.getCantidad() + cantidad;
            }

            if (cantidadFinal > existencia.getExistencia()) {
                return "La cantidad solicitada supera las existencias disponibles para esa talla.";
            }

            if (item == null) {
                item = new PedidoProducto();
                item.setPedido(carrito);
                item.setProducto(producto);
                item.setTalla(talla);
                item.setCantidad(cantidad);
            } else {
                item.setCantidad(item.getCantidad() + cantidad);
            }

            pedidoProductoRepository.save(item);

            recalcularTotal(carrito.getIdPedido());

            return "ok";

        } catch (Exception ex) {
            System.out.println("Error al agregar producto al carrito: " + ex.getMessage());
            ex.printStackTrace();
            return "Ocurrió un error al agregar el producto al carrito.";
        }
    }

    public void recalcularTotal(Integer idPedido) {
        try {
            Pedido pedido = pedidoRepository.findById(idPedido).orElse(null);
            if (pedido == null) {
                return;
            }

            List<PedidoProducto> items = pedidoProductoRepository.findByPedido_IdPedido(idPedido);

            double total = 0.0;

            for (PedidoProducto item : items) {
                if (item.getProducto() != null && item.getProducto().getPrecioUnitario() != null) {
                    total += item.getProducto().getPrecioUnitario() * item.getCantidad();
                }
            }

            pedido.setTotal(total);
            pedidoRepository.save(pedido);

        } catch (Exception ex) {
            System.out.println("Error al recalcular total del carrito: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public Pedido obtenerCarritoActivo(Integer idUsuario) {
        try {
            return pedidoRepository
                    .findByUsuario_IdUsuarioAndEstado(idUsuario, "carrito")
                    .orElse(null);
        } catch (Exception ex) {
            System.out.println("Error al obtener carrito activo: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    public List<PedidoProducto> obtenerItemsCarrito(Integer idUsuario) {
        try {
            Pedido carrito = obtenerCarritoActivo(idUsuario);

            if (carrito == null) {
                return List.of();
            }

            return pedidoProductoRepository.findByPedido_IdPedido(carrito.getIdPedido());

        } catch (Exception ex) {
            System.out.println("Error al obtener items del carrito: " + ex.getMessage());
            ex.printStackTrace();
            return List.of();
        }
    }

    public String cancelarPedido(Integer idUsuario) {
        try {
            Pedido carrito = pedidoRepository
                    .findByUsuario_IdUsuarioAndEstado(idUsuario, "carrito")
                    .orElse(null);

            if (carrito == null) {
                return "No tienes un pedido activo en el carrito.";
            }

            carrito.setEstado("cancelado");
            pedidoRepository.save(carrito);

            return "ok";

        } catch (Exception ex) {
            System.out.println("Error al cancelar pedido: " + ex.getMessage());
            ex.printStackTrace();
            return "Ocurrió un error al cancelar el pedido.";
        }
    }

    public String pagarPedido(Integer idUsuario) {
        try {
            Pedido carrito = pedidoRepository
                    .findByUsuario_IdUsuarioAndEstado(idUsuario, "carrito")
                    .orElse(null);

            if (carrito == null) {
                return "No tienes un pedido activo en el carrito.";
            }

            List<PedidoProducto> items = pedidoProductoRepository.findByPedido_IdPedido(carrito.getIdPedido());

            if (items.isEmpty()) {
                return "No puedes pagar un carrito vacío.";
            }

            carrito.setEstado("realizado");
            pedidoRepository.save(carrito);

            return "ok";

        } catch (Exception ex) {
            System.out.println("Error al pagar pedido: " + ex.getMessage());
            ex.printStackTrace();
            return "Ocurrió un error al procesar el pago del pedido.";
        }
    }
}
