use pastillero_dad;

DROP TABLE Registro_Dosis;
DROP TABLE Pastilla_Dosis;
DROP TABLE Franja_Horaria;
DROP TABLE Pastilla;
DROP TABLE Usuario;
DROP TABLE Pastillero;


CREATE TABLE Pastillero (
    id_pastillero VARCHAR(20),
    alias VARCHAR(30),
    
    PRIMARY KEY (id_pastillero)
);


CREATE TABLE Usuario (
    id_usuario INT UNSIGNED AUTO_INCREMENT,
    id_pastillero VARCHAR(20) NOT NULL,
    firstname VARCHAR(30) NOT NULL,
    lastname VARCHAR(30) NOT NULL,
    contraseña VARCHAR(64) NOT NULL,
    email VARCHAR(50) NOT NULL,
    rol VARCHAR(20) CHECK(rol IN ('enfermo', 'administrador','cuidador')) NOT NULL,
    id_cuidador INT UNSIGNED,
    reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    PRIMARY KEY ( id_usuario),
    FOREIGN KEY (id_pastillero) REFERENCES Pastillero(id_pastillero) ,
    FOREIGN KEY (id_cuidador) REFERENCES Usuario(id_usuario) 
    
);

CREATE TABLE Pastilla (
    id_pastilla INT UNSIGNED AUTO_INCREMENT,
    nombre VARCHAR(40) NOT NULL,
    descripcion VARCHAR(300) NOT NULL,
    peso DOUBLE,
    
     PRIMARY KEY ( id_pastilla)
);

CREATE TABLE Franja_Horaria (
    id_franja INT UNSIGNED AUTO_INCREMENT,
    hora_inicio VARCHAR(5) NOT NULL,
    dia_semana VARCHAR(9) CHECK(dia_semana IN ('L', 'M', 'X', 'J', 'V', 'S', 'D')),
    id_usuario INT UNSIGNED,
    observacion VARCHAR(200),
    
     PRIMARY KEY ( id_franja),
     FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario) ON DELETE CASCADE,
     UNIQUE (id_usuario, hora_inicio, dia_semana)
    
);


CREATE TABLE Pastilla_Dosis ( 
	id_pastilla INT UNSIGNED,
    id_franja INT UNSIGNED,
	cantidad DOUBLE,
    
	FOREIGN KEY (id_pastilla) REFERENCES  Pastilla(id_pastilla) ON DELETE CASCADE,
    FOREIGN KEY (id_franja) REFERENCES Franja_Horaria(id_franja) ON DELETE CASCADE,
    
    PRIMARY KEY ( id_franja , id_pastilla ) 
);


CREATE TABLE Registro_Dosis (
    id_registro_dosis INT UNSIGNED AUTO_INCREMENT ,
    fecha_caducidad DATETIME,
    tomada BOOLEAN NOT NULL,
    id_franja INT UNSIGNED,
    
	PRIMARY KEY ( id_registro_dosis),
	FOREIGN KEY (id_franja) REFERENCES Franja_Horaria(id_franja) ON DELETE CASCADE
	
);

insert into Pastillero (id_pastillero , alias) values ('192R5T',"Pastillero papá");

insert into Usuario (id_pastillero,firstname, lastname,contraseña, email, rol) values ("192R5T","Ismael","Mamel","Ismaelito22","admin@admin.es","cuidador");
insert into Usuario (id_pastillero,firstname, lastname,contraseña, email, rol,id_cuidador) values ("192R5T","Manuel","Tejano","Tejanito22","admin@admin.es","enfermo","1");

insert into Pastilla (nombre,descripcion,peso) values ("Paracetamol","Comprimidos EPG","100");

insert into Franja_Horaria (hora_inicio,dia_semana,id_usuario,observacion) values ("15:00","Lunes","1","Recordar cita médica, o recordar que se lo tome en un cierto orden");

insert into Pastilla_Dosis (id_pastilla,id_franja,cantidad) values ("1","1",0.5);

insert into Registro_Dosis (fecha_caducidad,tomada,id_franja) values (date(12/12/2021),TRUE,"1");


select * from Pastilla;

SELECT * FROM pastillero_dad.Usuario;



