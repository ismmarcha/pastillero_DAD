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
    id_pastillero VARCHAR(20) ,
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
    dia_semana INT,
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
    
    PRIMARY KEY (id_dosis , id_pastilla) 
);


CREATE TABLE IF NOT EXISTS Registro_Dosis (
    id_registro_dosis INT UNSIGNED AUTO_INCREMENT ,
	id_dosis INT UNSIGNED,

    tomada BOOLEAN NOT NULL,
    
    PRIMARY KEY(id_registro_dosis),
	FOREIGN KEY (id_dosis) REFERENCES Dosis(id_dosis) ON DELETE CASCADE
	
);

insert into Pastillero (id_pastillero , alias) values ('192R5T',"Pastillero papá");

insert into Usuario (nif,id_pastillero,firstname, lastname,contraseña, email, rol) values ("12344","192R5T","Ismael","Mamel","Ismaelito22","admin@admin.es","cuidador");
insert into Usuario (nif,id_pastillero,firstname, lastname,contraseña, email, rol,id_cuidador) values ("12313","192R5T","Manuel","Tejano","Tejanito22","admin@admin.es","enfermo","12344");

insert into Pastilla (nombre,descripcion,peso) values ("Paracetamol","Comprimidos EPG","100");
insert into Pastilla (nombre,descripcion,peso) values ("Frenadol","Comprimidos EPG","100");

insert into Dosis (hora_inicio,dia_semana,nif,observacion) values ("15:00",1,"12344","Recordar cita médica, o recordar que se lo tome en un cierto orden");

insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("1","1",0.5);


insert into Registro_Dosis (tomada,id_dosis) values (TRUE,"1");



select * from Pastilla_Dosis;
SELECT * FROM pastillero_dad.Pastilla;
select * from Dosis;

SELECT * FROM pastillero_dad.Usuario;


SELECT * FROM pastillero_dad.Dosis WHERE nif = 1 AND hora_inicio = '15:00' AND dia_semana = 'L';
SELECT * FROM pastillero_dad.Pastilla WHERE id_pastilla IN (select id_pastilla from pastillero_dad.Pastilla_Dosis WHERE id_dosis = 1);
SELECT Pastilla.id_pastilla ,nombre ,descripcion ,peso FROM pastillero_dad.Pastilla LEFT JOIN pastillero_dad.Pastilla_Dosis ON Pastilla.id_pastilla = pastilla_dosis.id_pastilla WHERE pastilla_dosis.id_pastilla = 1 ;

DELETE FROM pastillero_dad.Pastilla_Dosis WHERE Id_pastilla = 1 AND Id_Dosis = 1;

SELECT * FROM Registro_dosis;

SELECT * FROM pastillero_dad.Dosis WHERE nif = 1 AND hora_inicio = '15:00' AND dia_semana = 'L';

SELECT * FROM pastillero_dad.Dosis WHERE nif = '12344' AND  ((hora_inicio > '12:25' AND dia_semana ='D') OR (dia_semana ='L')) ORDER BY FIELD(dia_semana, 'L', 'M', 'X', 'J', 'V', 'S', 'D') ASC, hora_inicio;
select str_to_date(CONCAT(year(now()),"-",month(CURDATE()),"-",day(now())), "%Y %m %d");
select DATE_FORMAT(CONCAT(year(now()),"-",month(CURDATE()),"-",day(now())," ", Dosis.hora_inicio), "%Y-%m-%d %T") from Dosis;




SELECT Registro_Dosis.id_registro_dosis, Registro_Dosis.id_dosis, Registro_Dosis.tomada FROM pastillero_dad.Registro_Dosis LEFT JOIN pastillero_dad.dosis ON registro_dosis.id_dosis = dosis.id_dosis WHERE dosis.nif = "12344" ;



