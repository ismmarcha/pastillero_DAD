use pastillero_dad;

DROP TABLE IF EXISTS Registro_Dosis;
DROP TABLE IF EXISTS Pastilla_Dosis;
DROP TABLE IF EXISTS Dosis;
DROP TABLE IF EXISTS Pastilla;
DROP TABLE IF EXISTS Usuario;
DROP TABLE IF EXISTS Pastillero;


CREATE TABLE IF NOT EXISTS Pastillero (
    id_pastillero VARCHAR(100),
    alias VARCHAR(100),
    
    PRIMARY KEY (id_pastillero)
);


CREATE TABLE IF NOT EXISTS Usuario (
    nif VARCHAR(9),
    id_pastillero VARCHAR(100) ,
    firstname VARCHAR(30) NOT NULL,
    lastname VARCHAR(30) NOT NULL,
    contraseña VARCHAR(64) NOT NULL,
    email VARCHAR(50) NOT NULL,
    rol VARCHAR(20) CHECK(rol IN ('enfermo', 'administrador','cuidador')) NOT NULL,
    id_cuidador VARCHAR(9) NULL,
    reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    PRIMARY KEY ( nif),
    FOREIGN KEY (id_pastillero) REFERENCES Pastillero(id_pastillero) ,
    FOREIGN KEY (id_cuidador) REFERENCES Usuario(nif) 
    
);

CREATE TABLE IF NOT EXISTS Pastilla (
    id_pastilla INT UNSIGNED AUTO_INCREMENT,
    nombre VARCHAR(40) NOT NULL,
    descripcion VARCHAR(300) ,
    peso DOUBLE,
    
     PRIMARY KEY ( id_pastilla),
     UNIQUE (nombre,peso)
);

CREATE TABLE IF NOT EXISTS Dosis (
    id_dosis INT UNSIGNED AUTO_INCREMENT,
	nif VARCHAR(9),
    hora_inicio VARCHAR(5) NOT NULL,
    dia_semana INT,
    observacion VARCHAR(200),
    
     PRIMARY KEY ( id_dosis),
     FOREIGN KEY (nif) REFERENCES Usuario(nif) ON DELETE CASCADE,
     UNIQUE (nif, hora_inicio, dia_semana)
    
);


CREATE TABLE IF NOT EXISTS Pastilla_Dosis ( 
	id_pastilla INT UNSIGNED,
    id_dosis INT UNSIGNED,
	cantidad DOUBLE NOT NULL,
    
	FOREIGN KEY (id_pastilla) REFERENCES  Pastilla(id_pastilla) ON DELETE CASCADE,
    FOREIGN KEY (id_dosis) REFERENCES Dosis(id_dosis) ON DELETE CASCADE,
    
    PRIMARY KEY (id_dosis , id_pastilla) 
);


CREATE TABLE IF NOT EXISTS Registro_Dosis (
    id_registro_dosis INT UNSIGNED AUTO_INCREMENT ,
	id_dosis INT UNSIGNED,
	fecha_dosis TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    tomada BOOLEAN DEFAULT 0 NOT NULL,
    
    PRIMARY KEY(id_registro_dosis),
	FOREIGN KEY (id_dosis) REFERENCES Dosis(id_dosis) ON DELETE CASCADE
	
);