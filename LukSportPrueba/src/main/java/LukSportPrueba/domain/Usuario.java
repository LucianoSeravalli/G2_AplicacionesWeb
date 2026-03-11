/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LukSportPrueba.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idUsuario")
    private Integer idUsuario;

    @ManyToOne
    @JoinColumn(name = "idRol")
    private Rol rol;

    @Column(name = "Nombre")
    private String nombre;

    @Column(name = "Contrasena")
    private String contrasena;
    
    @Column(name = "Correo")
    private String correo;

    @Column(name = "Imagen")
    private String imagen;

    @Column(name = "FechaNacimiento")
    private LocalDate fechaNacimiento;
    
    @Column(name = "Activo")
    private Boolean activo;

    @Column(name = "TokenVerificacion")
    private String tokenVerificacion;

    @Column(name = "FechaExpiracionToken")
    private LocalDateTime fechaExpiracionToken;

    
}