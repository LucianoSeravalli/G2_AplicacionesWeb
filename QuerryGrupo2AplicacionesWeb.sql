-- Crear la base de datos
CREATE DATABASE IF NOT EXISTS LukSportCenter
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE LukSportCenter;

-- =========================
-- TABLA ROL
-- =========================
CREATE TABLE Rol (
    idRol INT AUTO_INCREMENT PRIMARY KEY,
    Rol VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB;

Insert into Rol (Rol) Values ("Usuario"), ("Administrador");
Insert into Rol (Rol) Values ("Vendedor");
Select * from Rol;

-- =========================
-- TABLA CATEGORIA
-- =========================
CREATE TABLE Categoria (
    idCategoria INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL,
    Actividad ENUM('activo', 'inactivo') NOT NULL DEFAULT 'activo',
    Imagen VARCHAR(1024)
) ENGINE=InnoDB;

Select * from Categoria;

-- =========================
-- TABLA USUARIO
-- =========================
CREATE TABLE Usuario (
    idUsuario INT AUTO_INCREMENT PRIMARY KEY,
    idRol INT NOT NULL,
    Correo VARCHAR(100) NOT NULL,
    Nombre VARCHAR(100) NOT NULL,
    Contrasena VARCHAR(255) NOT NULL,
    Imagen VARCHAR(500),
    FechaNacimiento DATE,
    CONSTRAINT fk_usuario_rol
        FOREIGN KEY (idRol) REFERENCES Rol(idRol)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB;

-- DELETE FROM Usuario WHERE idUsuario = 9;
ALTER TABLE Usuario
ADD COLUMN Activo BOOLEAN DEFAULT FALSE,
ADD COLUMN TokenVerificacion VARCHAR(255),
ADD COLUMN FechaExpiracionToken DATETIME;

-- UPDATE Usuario SET idRol = 2 WHERE idUsuario = 4;
Insert into Usuario (idRol, Nombre,Contrasena, Imagen, FechaNacimiento, Correo, Activo, TokenVerificacion, FechaExpiracionToken) Values
(3, "Vendedor", 123, "", "2007-04-28", "vendedor@gmail.com", true, "", null);

Insert into Usuario (idRol, Nombre,Contrasena, Imagen, FechaNacimiento, Correo, Activo, TokenVerificacion, FechaExpiracionToken) Values
 (2, "Aaron", 123, "", "2007-04-28", "aaron@gmail.com", true, "", null);


Select * From Usuario;
-- =========================
-- TABLA PRODUCTO
-- =========================
CREATE TABLE Producto (
    IdProducto INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(150) NOT NULL,
    CantidadExistencia INT NOT NULL DEFAULT 0,
    PrecioUnitario DECIMAL(10,2) NOT NULL,
    Categoria INT NOT NULL,
    Imagen VARCHAR(1024),
    CONSTRAINT fk_producto_categoria
        FOREIGN KEY (Categoria) REFERENCES Categoria(idCategoria)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB;

ALTER TABLE Producto
ADD COLUMN TieneTalla boolean;
-- ADD COLUMN Descripcion VARCHAR(150) NOT NULL;

select * from producto;

Update producto set TieneTalla = true where IdProducto = 2;
-- =========================
-- TABLA PEDIDOS
-- =========================
CREATE TABLE Pedidos (
    idPedido INT AUTO_INCREMENT PRIMARY KEY,
    idUsuario INT NOT NULL,
    Total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_pedido_usuario
        FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB;

-- =========================
-- TABLA PEDIDOSXPRODUCTO
-- =========================
CREATE TABLE PedidosXProducto (
    idPedidosXProducto INT AUTO_INCREMENT PRIMARY KEY,
    idPedidos INT NOT NULL,
    idProductos INT NOT NULL,
    Cantidad INT NOT NULL DEFAULT 1,
    CONSTRAINT fk_pxp_pedido
        FOREIGN KEY (idPedidos) REFERENCES Pedidos(idPedido)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_pxp_producto
        FOREIGN KEY (idProductos) REFERENCES Producto(IdProducto)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB;


-- =========================
-- TABLA TallasProducto
-- =========================

CREATE TABLE TallasProducto (
    IdTalla INT AUTO_INCREMENT PRIMARY KEY,
    NombreTalla VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO TallasProducto (NombreTalla) VALUES ('XS');
INSERT INTO TallasProducto (NombreTalla) VALUES ('S');
INSERT INTO TallasProducto (NombreTalla) VALUES ('M');
INSERT INTO TallasProducto (NombreTalla) VALUES ('L');
INSERT INTO TallasProducto (NombreTalla) VALUES ('XL');
INSERT INTO TallasProducto (NombreTalla) VALUES ('Este producto no tiene tallas');

-- =========================
-- TABLA CantidadProductoTallas
-- =========================

CREATE TABLE CantidadProductoTalla (
    IdProductoTalla INT AUTO_INCREMENT PRIMARY KEY,
    IdProducto INT NOT NULL,
    IdTalla INT NOT NULL,
    Existencia INT NOT NULL,
    CONSTRAINT fk_producto_talla_producto
        FOREIGN KEY (IdProducto) REFERENCES producto(IdProducto),
    CONSTRAINT fk_producto_talla_talla
        FOREIGN KEY (IdTalla) REFERENCES TallasProducto(IdTalla)
);

Select * from CantidadProductoTalla;
