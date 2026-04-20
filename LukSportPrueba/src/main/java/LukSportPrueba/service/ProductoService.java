package LukSportPrueba.service;

import LukSportPrueba.repository.CategoriaRepository;
import LukSportPrueba.repository.CantidadProductoTallaRepository;
import LukSportPrueba.repository.TallaProductoRepository;
import LukSportPrueba.repository.ProductoRepository;

import LukSportPrueba.domain.CantidadProductoTalla;
import LukSportPrueba.domain.Producto;
import LukSportPrueba.domain.Categoria;
import LukSportPrueba.domain.TallaProducto;

import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductoService {

    @Autowired
    private FireBaseStorageService fireBaseStorageService;

    @Autowired
    private CantidadProductoTallaRepository cantidadProductoTallaRepository;

    @Autowired
    private TallaProductoRepository tallaProductoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

//    private boolean hayTallasDeEseProducto(int idProducto, int idTalla) {
//        boolean existeTalla = false;
//        cantidadProductoTalla = cantidadProductoTallaRepository.findByProducto_IdProductoAndTalla_IdTalla(idProducto, idTalla);
//        if (cantidadProductoTalla.getExistencia() > 0) {
//            existeTalla = true;
//        }
//        return existeTalla;
//    }
    public List<Producto> listarProductos() {
        try {
            return productoRepository.findAll();
        } catch (Exception ex) {
            System.out.println("Error al listar productos: " + ex.getMessage());
            ex.printStackTrace();
            return List.of();
        }
    }

    public Producto guardarProducto(Producto producto, MultipartFile imagenFile, Integer idCategoria) {
        try {
            // 🔹 Buscar la categoría
            Categoria categoria = categoriaRepository.findById(idCategoria).orElse(null);

            if (categoria == null) {
                System.out.println("Categoría no encontrada");
                return null;
            }
            // 🔹 Asignar categoría al producto
            producto.setCategoria(categoria);
            // 🔹 Guardar producto primero (para obtener ID)
            Producto productoGuardado = productoRepository.save(producto);

            if (imagenFile != null && !imagenFile.isEmpty()) {
                String urlImagen = fireBaseStorageService.uploadImage(
                        imagenFile, "productos", productoGuardado.getIdProducto()
                );
                productoGuardado.setImagen(urlImagen);
                productoGuardado = productoRepository.save(productoGuardado);
            }
            return productoGuardado;

        } catch (IOException ex) {
            System.out.println("Error al subir imagen de producto a Firebase: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        } catch (Exception ex) {
            System.out.println("Error al guardar producto: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    public Producto editarProducto(Integer idProducto, String nombre, String descripcion, MultipartFile imagenFile) {
        try {

            // 🔹 Buscar producto existente
            Producto productoExistente = productoRepository.findById(idProducto).orElse(null);

            if (productoExistente == null) {
                System.out.println("Producto no encontrado");
                return null;
            }

            // 🔹 Actualizar SOLO campos permitidos
            productoExistente.setNombre(nombre);
            productoExistente.setDescripcion(descripcion);

            // 🔹 Si viene imagen nueva → subirla
            if (imagenFile != null && !imagenFile.isEmpty()) {
                String urlImagen = fireBaseStorageService.uploadImage(
                        imagenFile, "productos", productoExistente.getIdProducto()
                );
                productoExistente.setImagen(urlImagen);
            }

            // 🔹 Guardar cambios
            return productoRepository.save(productoExistente);

        } catch (IOException ex) {
            System.out.println("Error al subir imagen de producto: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        } catch (Exception ex) {
            System.out.println("Error al editar producto: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    public void eliminarProducto(Integer idProducto) {
        try {
            Producto producto = productoRepository.findById(idProducto).orElse(null);

            if (producto == null) {
                System.out.println("Producto no encontrado");
                return;
            }

            productoRepository.delete(producto);

        } catch (Exception ex) {
            System.out.println("Error al eliminar producto: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public CantidadProductoTalla agregarExistenciaProductoTalla(Integer idProducto, Integer idTalla, Integer cantidadAgregar) {
        try {
            Producto producto = productoRepository.findById(idProducto).orElse(null);
            TallaProducto talla = tallaProductoRepository.findById(idTalla).orElse(null);

            if (producto == null || talla == null) {
                return null;
            }

            CantidadProductoTalla existencia = cantidadProductoTallaRepository
                    .findByProducto_IdProductoAndTalla_IdTalla(idProducto, idTalla)
                    .orElse(null);

            if (existencia == null) {
                existencia = new CantidadProductoTalla();
                existencia.setProducto(producto);
                existencia.setTalla(talla);
                existencia.setExistencia(cantidadAgregar);
            } else {
                existencia.setExistencia(existencia.getExistencia() + cantidadAgregar);
            }

            cantidadProductoTallaRepository.save(existencia);

            int total = cantidadProductoTallaRepository.findAll().stream()
                    .filter(e -> e.getProducto().getIdProducto().equals(idProducto))
                    .mapToInt(CantidadProductoTalla::getExistencia)
                    .sum();

            producto.setCantidadExistencia(total);
            productoRepository.save(producto);

            return existencia;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Producto obtenerProductoPorId(Integer idProducto) {
        try {
            return productoRepository.findById(idProducto).orElse(null);
        } catch (Exception ex) {
            System.out.println("Error al obtener producto por id: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    public CantidadProductoTalla quitarExistenciaProductoTalla(Integer idProducto, Integer idTalla, Integer cantidadQuitar) {
        try {
            if (cantidadQuitar == null || cantidadQuitar <= 0) {
                System.out.println("La cantidad a quitar debe ser mayor a 0");
                return null;
            }

            Producto producto = productoRepository.findById(idProducto).orElse(null);
            if (producto == null) {
                System.out.println("Producto no encontrado");
                return null;
            }

            CantidadProductoTalla existencia = cantidadProductoTallaRepository
                    .findByProducto_IdProductoAndTalla_IdTalla(idProducto, idTalla)
                    .orElse(null);

            if (existencia == null) {
                System.out.println("No existe inventario para esa talla");
                return null;
            }

            if (existencia.getExistencia() < cantidadQuitar) {
                System.out.println("No hay suficiente inventario para descontar esa cantidad");
                return null;
            }

            existencia.setExistencia(existencia.getExistencia() - cantidadQuitar);
            cantidadProductoTallaRepository.save(existencia);

            int total = cantidadProductoTallaRepository.findAll().stream()
                    .filter(e -> e.getProducto().getIdProducto().equals(idProducto))
                    .mapToInt(CantidadProductoTalla::getExistencia)
                    .sum();

            producto.setCantidadExistencia(total);
            productoRepository.save(producto);

            return existencia;

        } catch (Exception ex) {
            System.out.println("Error al quitar existencia por talla: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

}
