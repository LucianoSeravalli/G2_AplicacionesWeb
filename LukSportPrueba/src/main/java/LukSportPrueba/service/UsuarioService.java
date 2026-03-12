package LukSportPrueba.service;

import LukSportPrueba.domain.Usuario;
import LukSportPrueba.domain.Rol;
import LukSportPrueba.repository.UsuarioRepository;
import LukSportPrueba.repository.RolRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UsuarioService {

    @Autowired
    private FireBaseStorageService fireBaseStorageService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private JavaMailSender mailSender;

    public Usuario login(String identificador, String contrasena) {
        try {
            Usuario usuario = usuarioRepository.findByNombreAndContrasenaAndActivoTrue(identificador, contrasena);

            if (usuario == null) {
                usuario = usuarioRepository.findByCorreoAndContrasenaAndActivoTrue(identificador, contrasena);
            }
            return usuario;
        } catch (Exception ex) {
            System.out.println(ex);
            return null;
        }
    }

    public void guardar(Usuario usuario) {
        Rol rolUsuario = rolRepository.findById(1).orElse(null);
        usuario.setRol(rolUsuario);
        usuario.setActivo(false);
        String token = java.util.UUID.randomUUID().toString();
        usuario.setTokenVerificacion(token);
        usuario.setFechaExpiracionToken(LocalDateTime.now().plusHours(24));
        usuarioRepository.save(usuario);
        enviarCorreoVerificacion(usuario);
    }

    public Usuario buscarPorNombre(String nombre) {
        try {
            return usuarioRepository.findByNombre(nombre);
        } catch (Exception ex) {
            System.out.println(ex);
            return null;
        }
    }

    public Usuario buscarPorCorreo(String correo) {
        try {
            return usuarioRepository.findByCorreo(correo);
        } catch (Exception ex) {
            System.out.println(ex);
            return null;
        }
    }


    public void enviarCorreoVerificacion(Usuario usuario) {
        try {
            String enlace = "http://localhost:8080/usuario/verificar?token=" + usuario.getTokenVerificacion();

            System.out.println("Intentando enviar correo a: " + usuario.getCorreo());
            System.out.println("Enlace de verificación: " + enlace);

            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setTo(usuario.getCorreo());
            mensaje.setSubject("Verifica tu cuenta - Luk Sport Center");
            mensaje.setText(
                    "Hola " + usuario.getNombre() + ",\n\n"
                    + "Haz click en el siguiente enlace para verificar tu cuenta:\n"
                    + enlace
            );

            mailSender.send(mensaje);

            System.out.println("Correo enviado correctamente.");
        } catch (Exception ex) {
            System.out.println("Error enviando correo: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public boolean verificarToken(String token) {
        try {
            Usuario usuario = usuarioRepository.findByTokenVerificacion(token);

            if (usuario == null) {
                return false;
            }

            if (usuario.getFechaExpiracionToken() == null
                    || usuario.getFechaExpiracionToken().isBefore(LocalDateTime.now())) {
                return false;
            }

            usuario.setActivo(true);
            usuario.setTokenVerificacion(null);
            usuario.setFechaExpiracionToken(null);

            usuarioRepository.save(usuario);
            return true;
        } catch (Exception ex) {
            System.out.println(ex);
            return false;
        }
    }

    public Usuario actualizarPerfil(Integer idUsuario, Usuario datos, MultipartFile imagenFile) {
        try {
            Usuario usuarioActual = usuarioRepository.findById(idUsuario).orElse(null);

            if (usuarioActual == null) {
                return null;
            }

            usuarioActual.setNombre(datos.getNombre());
            usuarioActual.setFechaNacimiento(datos.getFechaNacimiento());

            if (datos.getContrasena() != null && !datos.getContrasena().trim().isEmpty()) {
                usuarioActual.setContrasena(datos.getContrasena());
            }

            if (imagenFile != null && !imagenFile.isEmpty()) {
                String urlImagen = fireBaseStorageService.uploadImage(imagenFile, "usuarios", idUsuario);
                usuarioActual.setImagen(urlImagen);
            }

            return usuarioRepository.save(usuarioActual);

        } catch (IOException ex) {
            System.out.println("Error al subir imagen a Firebase: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        } catch (Exception ex) {
            System.out.println("Error al actualizar perfil: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    public Usuario getUsuarioPorId(Integer idUsuario) {
        try {
            return usuarioRepository.findById(idUsuario).orElse(null);
        } catch (Exception ex) {
            System.out.println("Error al buscar usuario por ID: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

}
