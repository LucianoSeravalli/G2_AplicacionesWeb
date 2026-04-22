package LukSportPrueba.service;

import LukSportPrueba.domain.Pedido;
import LukSportPrueba.domain.PedidoProducto;
import LukSportPrueba.domain.Usuario;
import LukSportPrueba.domain.Rol;
import LukSportPrueba.repository.UsuarioRepository;
import LukSportPrueba.repository.RolRepository;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.UUID;
import org.springframework.mail.javamail.MimeMessageHelper;
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

    public void enviarCorreoCompra(Usuario usuario, Pedido pedido, List<PedidoProducto> items) {
        try {
            System.out.println("Intentando enviar correo a: " + usuario.getCorreo());

            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setTo(usuario.getCorreo());
            helper.setSubject("Detalle de Compra - Luk Sport Center");

            // ===============================
            // CREAR FILAS DINÁMICAS
            // ===============================
            StringBuilder filas = new StringBuilder();

            for (PedidoProducto item : items) {

                String talla = "Sin talla";

                if (item.getTalla() != null) {
                    talla = item.getTalla().getNombreTalla();
                }

                double precio = item.getProducto().getPrecioUnitario();
                int cantidad = item.getCantidad();
                double subtotal = precio * cantidad;

                filas.append("""
                <tr>
                    <td style="padding:12px; border-bottom:1px solid #eee;">%s</td>
                    <td style="padding:12px; border-bottom:1px solid #eee;">%s</td>
                    <td style="padding:12px; border-bottom:1px solid #eee;">%d</td>
                    <td style="padding:12px; border-bottom:1px solid #eee;">₡%,.2f</td>
                    <td style="padding:12px; border-bottom:1px solid #eee;">₡%,.2f</td>
                </tr>
                """.formatted(
                        item.getProducto().getNombre(),
                        talla,
                        cantidad,
                        precio,
                        subtotal
                ));
            }

            // ===============================
            // HTML FINAL
            // ===============================
            String html = """
            <html>
            <body style="margin:0; padding:0; font-family:Arial, Helvetica, sans-serif; background:#f4f4f4;">

                <div style="max-width:800px; margin:30px auto; background:#ffffff; border:1px solid #ddd; border-radius:12px; overflow:hidden;">

                    <div style="background:#8b0000; color:white; text-align:center; padding:18px;">
                        <h2 style="margin:0;">Factura de Compra</h2>
                    </div>

                    <div style="padding:25px; color:#333;">

                        <p style="font-size:16px;">
                            Hola <strong>%s</strong>,
                        </p>

                        <p>
                            Gracias por comprar en <strong>Luk Sport Center</strong>.
                            Este es el detalle real de tu compra:
                        </p>

                        <p>
                            <strong>Número de pedido:</strong> #%d
                        </p>

                        <div style="border:1px solid #ccc; border-radius:10px; overflow:hidden; margin-top:20px;">

                            <table style="width:100%%; border-collapse:collapse;">

                                <tr style="background:#f8f8f8;">
                                    <th style="padding:12px; text-align:left;">Producto</th>
                                    <th style="padding:12px; text-align:left;">Talla</th>
                                    <th style="padding:12px; text-align:left;">Cantidad</th>
                                    <th style="padding:12px; text-align:left;">Precio</th>
                                    <th style="padding:12px; text-align:left;">Subtotal</th>
                                </tr>

                                %s

                            </table>

                        </div>

                        <div style="margin-top:25px; padding:18px; background:#fafafa; border-left:5px solid #8b0000; border-radius:8px;">

                            <p style="margin:0 0 8px 0;">
                                <strong>Total pagado:</strong> ₡%,.2f
                            </p>

                            <p style="margin:0 0 8px 0;">
                                <strong>Tipo de entrega:</strong> %s
                            </p>

                            <p style="margin:0;">
                                <strong>Estado:</strong> %s
                            </p>

                        </div>

                        <p style="margin-top:25px;">
                            Gracias por confiar en nosotros.
                        </p>

                        <p>
                            <strong>Luk Sport Center</strong>
                        </p>

                    </div>

                </div>

            </body>
            </html>
            """.formatted(
                    usuario.getNombre(),
                    pedido.getIdPedido(),
                    filas.toString(),
                    pedido.getTotal(),
                    pedido.getTipoEntrega(),
                    pedido.getEstado()
            );

            helper.setText(html, true);

            mailSender.send(mensaje);

            System.out.println("Correo enviado correctamente.");

        } catch (Exception ex) {
            System.out.println("Error enviando correo: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
