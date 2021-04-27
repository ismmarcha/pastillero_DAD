use pastillero_dad;

DROP TABLE IF EXISTS Registro_Dosis;
DROP TABLE IF EXISTS Pastilla_Dosis;
DROP TABLE IF EXISTS Dosis;
DROP TABLE IF EXISTS Pastilla;
DROP TABLE IF EXISTS Usuario;
DROP TABLE IF EXISTS Pastillero;


CREATE TABLE IF NOT EXISTS Pastillero (
    id_pastillero VARCHAR(20),
    alias VARCHAR(30),
    
    PRIMARY KEY (id_pastillero)
);


CREATE TABLE IF NOT EXISTS Usuario (
    nif VARCHAR(9),
    id_pastillero VARCHAR(20) NOT NULL,
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
    
     PRIMARY KEY ( id_pastilla)
);

CREATE TABLE IF NOT EXISTS Dosis (
    id_dosis INT UNSIGNED AUTO_INCREMENT,
	nif VARCHAR(9),
    hora_inicio VARCHAR(5) NOT NULL,
    dia_semana VARCHAR(9) CHECK(dia_semana IN ('L', 'M', 'X', 'J', 'V', 'S', 'D')),
    observacion VARCHAR(200),
    
     PRIMARY KEY ( id_dosis),
     FOREIGN KEY (nif) REFERENCES Usuario(nif) ON DELETE CASCADE,
     UNIQUE (nif, hora_inicio, dia_semana)
    
);


CREATE TABLE IF NOT EXISTS Pastilla_Dosis ( 
	id_pastilla INT UNSIGNED,
    id_dosis INT UNSIGNED,
	cantidad DOUBLE,
    
	FOREIGN KEY (id_pastilla) REFERENCES  Pastilla(id_pastilla) ON DELETE CASCADE,
    FOREIGN KEY (id_dosis) REFERENCES Dosis(id_dosis) ON DELETE CASCADE,
    
    PRIMARY KEY ( id_dosis , id_pastilla ) 
);


CREATE TABLE IF NOT EXISTS Registro_Dosis (
    id_registro_dosis INT UNSIGNED AUTO_INCREMENT ,
	id_dosis INT UNSIGNED,
    fecha_caducidad DATETIME,
    tomada BOOLEAN NOT NULL,
    
	PRIMARY KEY ( id_registro_dosis),
	FOREIGN KEY (id_dosis) REFERENCES Dosis(id_dosis) ON DELETE CASCADE
	
);

insert into Pastillero (id_pastillero , alias) values ('192R5T',"Pastillero papá");

insert into Usuario (nif,id_pastillero,firstname, lastname,contraseña, email, rol) values ("12344","192R5T","Ismael","Mamel","Ismaelito22","admin@admin.es","cuidador");
insert into Usuario (nif,id_pastillero,firstname, lastname,contraseña, email, rol,id_cuidador) values ("12313","192R5T","Manuel","Tejano","Tejanito22","admin@admin.es","enfermo","12344");

insert into Pastilla (nombre,descripcion,peso) values ("Paracetamol","Comprimidos EPG","100");

insert into Dosis (hora_inicio,dia_semana,nif,observacion) values ("15:00","L","12344","Recordar cita médica, o recordar que se lo tome en un cierto orden");

insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("1","1",0.5);

insert into Registro_Dosis (fecha_caducidad,tomada,id_dosis) values (date(12/12/2021),TRUE,"1");


select * from Pastilla;
SELECT * FROM pastillero_dad.Pastilla;
select * from Pastillero;

SELECT * FROM pastillero_dad.Usuario;

SELECT * FROM pastillero_dad.Dosis WHERE nif = 1 AND hora_inicio = '15:00' AND dia_semana = 'L';





