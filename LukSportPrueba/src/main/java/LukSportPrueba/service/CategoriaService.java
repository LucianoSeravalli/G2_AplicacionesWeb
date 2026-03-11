package LukSportPrueba.service;

import LukSportPrueba.domain.Categoria;
import LukSportPrueba.repository.CategoriaRepository;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private FireBaseStorageService fireBaseStorageService;

    public List<Categoria> listarCategorias() {
        try {
            return categoriaRepository.findAll();
        } catch (Exception ex) {
            System.out.println("Error al listar categorías: " + ex.getMessage());
            ex.printStackTrace();
            return List.of();
        }
    }

    public List<Categoria> listarCategoriasActivas() {
        try {
            return categoriaRepository.findByActividad("activo");
        } catch (Exception ex) {
            System.out.println("Error al listar categorías activas: " + ex.getMessage());
            ex.printStackTrace();
            return List.of();
        }
    }

    public Categoria getCategoriaPorId(Integer idCategoria) {
        try {
            return categoriaRepository.findById(idCategoria).orElse(null);
        } catch (Exception ex) {
            System.out.println("Error al buscar categoría por ID: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    public Categoria guardarCategoria(Categoria categoria, MultipartFile imagenFile) {
        try {
            Categoria categoriaGuardada = categoriaRepository.save(categoria);

            if (imagenFile != null && !imagenFile.isEmpty()) {
                String urlImagen = fireBaseStorageService.uploadImage(
                        imagenFile, "categorias", categoriaGuardada.getIdCategoria()
                );
                categoriaGuardada.setImagen(urlImagen);
                categoriaGuardada = categoriaRepository.save(categoriaGuardada);
            }

            return categoriaGuardada;

        } catch (IOException ex) {
            System.out.println("Error al subir imagen de categoría a Firebase: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        } catch (Exception ex) {
            System.out.println("Error al guardar categoría: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    public Categoria actualizarCategoria(Integer idCategoria, Categoria datos, MultipartFile imagenFile) {
        try {
            Categoria categoriaActual = categoriaRepository.findById(idCategoria).orElse(null);

            if (categoriaActual == null) {
                return null;
            }

            categoriaActual.setNombre(datos.getNombre());
            categoriaActual.setActividad(datos.getActividad());

            if (imagenFile != null && !imagenFile.isEmpty()) {
                String urlImagen = fireBaseStorageService.uploadImage(
                        imagenFile, "categorias", idCategoria
                );
                categoriaActual.setImagen(urlImagen);
            }

            return categoriaRepository.save(categoriaActual);

        } catch (IOException ex) {
            System.out.println("Error al subir imagen actualizada de categoría: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        } catch (Exception ex) {
            System.out.println("Error al actualizar categoría: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    public boolean eliminarCategoria(Integer idCategoria) {
        try {
            Categoria categoria = categoriaRepository.findById(idCategoria).orElse(null);

            if (categoria == null) {
                return false;
            }

            if (categoria.getProductos() != null && !categoria.getProductos().isEmpty()) {
                return false;
            }

            categoriaRepository.delete(categoria);
            return true;

        } catch (Exception ex) {
            System.out.println("Error al eliminar categoría: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }
}
