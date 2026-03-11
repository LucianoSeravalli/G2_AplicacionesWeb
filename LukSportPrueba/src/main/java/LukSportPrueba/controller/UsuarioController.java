package LukSportPrueba.controller;

import LukSportPrueba.domain.Usuario;
import LukSportPrueba.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/usuario/acceso")
    public String accesoUsuario(HttpSession session) {
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");
        if (usuarioSesion == null) {
            return "redirect:/login";
        }
        return "redirect:/perfil";
    }

    @GetMapping("/login")
    public String login() {
        return "usuario/login";
    }

    @PostMapping("/login")
    public String iniciarSesion(@RequestParam("identificador") String identificador,
            @RequestParam("contrasena") String contrasena,
            HttpSession session,
            Model model) {
        Usuario usuario = usuarioService.login(identificador, contrasena);
        if (usuario == null) {
            model.addAttribute("toastError", "Usuario o correo no encontrado");
            return "usuario/login";
        }
        session.setAttribute("usuarioSesion", usuario);
        return "redirect:/perfil";
    }

    @GetMapping("/perfil")
    public String perfil(HttpSession session, Model model) {
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");
        if (usuarioSesion == null) {
            return "redirect:/login";
        }
        Usuario usuarioCompleto = usuarioService.getUsuarioPorId(usuarioSesion.getIdUsuario());
        if (usuarioCompleto == null) {
            session.invalidate();
            return "redirect:/login";
        }
        session.setAttribute("usuarioSesion", usuarioCompleto);
        model.addAttribute("usuario", usuarioCompleto);
        return "usuario/perfil";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/usuario/registro")
    public String registro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuario/registro";
    }

    @PostMapping("/usuario/guardar")
    public String guardarUsuario(Usuario usuario, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuarioExistente = usuarioService.buscarPorNombre(usuario.getNombre());
        if (usuarioExistente != null) {
            model.addAttribute("toastError", "El nombre de usuario ya existe");
            model.addAttribute("usuario", usuario);
            return "usuario/registro";
        }
        Usuario correoExistente = usuarioService.buscarPorCorreo(usuario.getCorreo());
        if (correoExistente != null) {
            model.addAttribute("toastError", "El correo ya está registrado");
            model.addAttribute("usuario", usuario);
            return "usuario/registro";
        }
        usuarioService.guardar(usuario);
        redirectAttributes.addFlashAttribute("popupSuccess", true);
        redirectAttributes.addFlashAttribute("correoEnviado", usuario.getCorreo());
        return "redirect:/login";
    }

    @GetMapping("/usuario/verificar")
    public String verificarCuenta(@RequestParam("token") String token, Model model) {
        boolean verificado = usuarioService.verificarToken(token);
        if (verificado) {
            model.addAttribute("toastSuccess", "Correo verificado correctamente. Ya puedes iniciar sesión.");
        } else {
            model.addAttribute("toastError", "El enlace no es válido o ha expirado.");
        }
        return "usuario/login";
    }

    @PostMapping("/usuario/actualizarPerfil")
    public String actualizarPerfil(Usuario usuario,
            @RequestParam("imagenFile") MultipartFile imagenFile,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioSesion");

        if (usuarioSesion == null) {
            return "redirect:/login";
        }

        Usuario usuarioActualizado = usuarioService.actualizarPerfil(usuarioSesion.getIdUsuario(), usuario, imagenFile);

        if (usuarioActualizado == null) {
            redirectAttributes.addFlashAttribute("toastError", "No se pudo actualizar el perfil");
            return "redirect:/perfil";
        }

        session.setAttribute("usuarioSesion", usuarioActualizado);
        redirectAttributes.addFlashAttribute("toastSuccess", "Perfil actualizado correctamente");

        return "redirect:/perfil";
    }

}
