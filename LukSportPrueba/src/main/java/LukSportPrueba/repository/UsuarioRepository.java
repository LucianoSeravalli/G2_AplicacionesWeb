/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package LukSportPrueba.repository;

import LukSportPrueba.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    public Usuario findByTokenVerificacion(String tokenVerificacion);
    public Usuario findByNombreAndContrasenaAndActivoTrue(String nombre, String contrasena);
    public Usuario findByCorreoAndContrasenaAndActivoTrue(String correo, String contrasena);
    public Usuario findByNombre(String nombre);
    public Usuario findByCorreo(String correo);
}