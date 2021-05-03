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

	fecha_dosis TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    tomada BOOLEAN DEFAULT 0 NOT NULL,
    
    PRIMARY KEY(id_registro_dosis),
	FOREIGN KEY (id_dosis) REFERENCES Dosis(id_dosis) ON DELETE CASCADE
	
);

insert into Pastillero (id_pastillero , alias) values ('192R5T',"Pastillero papá");

insert into Usuario (nif,id_pastillero,firstname, lastname,contraseña, email, rol) values ("12344","192R5T","Ismael","Mamel","Ismaelito22","admin@admin.es","cuidador");
insert into Usuario (nif,id_pastillero,firstname, lastname,contraseña, email, rol,id_cuidador) 
values ("12311","192R5T","Manuel","Tejano","Tejanito22","admin@admin.es","cuidador","12344");

select * from Usuario;

insert into Pastilla (nombre,descripcion,peso) values ("Paracetamol","Comprimidos EPG","100");
insert into Pastilla (nombre,descripcion,peso) values ("Frenadol","Comprimidos EPG","100");

insert into Dosis (hora_inicio,dia_semana,nif,observacion) values ("13:00",2,"12344","Recordar cita médica, o recordar que se lo tome en un cierto orden");

insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("1","1",0.5);

#insert into Registro_Dosis (fecha_caducidad,tomada,id_dosis) values (date(12/12/2021),TRUE,"1");

insert into Registro_Dosis (tomada,id_dosis) values (TRUE,"1");

select * from Pastilla_Dosis;
SELECT * FROM pastillero_dad.Pastilla;
select * from Dosis;

SELECT * FROM pastillero_dad.Usuario;

##HAY QUE PROBAR ESTA BARBARIDAD BIEN QUE MADRE MÍA 
SELECT *
FROM Dosis 
WHERE nif = '12344'
ORDER BY
if(TIMEDIFF(addtime(DATE_ADD(CURDATE(), INTERVAL dia_semana - weekday(CURDATE()) DAY), hora_inicio), now()) < 0,  
TIMEDIFF(addtime(DATE_ADD(CURDATE(), INTERVAL (7 - weekday(CURDATE())) + dia_semana DAY), hora_inicio), now()), 
TIMEDIFF(addtime(DATE_ADD(CURDATE(), INTERVAL dia_semana - weekday(CURDATE()) DAY), hora_inicio), now()));

#TRIGGER PARA EVITAR QUE HAYA MÁS DE 28 DOSIS POR USUARIO
DROP FUNCTION IF EXISTS numeroDosisUsuarioSuperado;

DELIMITER //
CREATE FUNCTION numeroDosisUsuarioSuperado (nifIn VARCHAR(9))
	RETURNS bool
    READS SQL DATA
	DETERMINISTIC
BEGIN
	DECLARE max bool;
    DECLARE numeroDosis INT;
    
	SELECT COUNT(*)
 	INTO numeroDosis
    FROM Dosis
    WHERE nif = nifIn;
    
    if(numeroDosis >= 28)
    THEN
		SET max = true;
    ELSE 
		SET max = false;
    END IF;
    
    RETURN max;
END //
DELIMITER ;

SELECT numeroDosisUsuarioSuperado('12344');

DROP TRIGGER IF EXISTS numeroMaximoDosis;

DELIMITER //
CREATE TRIGGER numeroMaximoDosis
BEFORE INSERT ON Dosis
FOR EACH ROW
BEGIN
  IF numeroDosisUsuarioSuperado(NEW.nif) != 0
    THEN
      signal sqlstate '45000' set message_text = 'Se ha superado el máximo de 28 de dosis para este usuario';
  END IF;
END//
DELIMITER ;

SELECT COUNT(*)
 	FROM Dosis
    WHERE nif = '12344';
    
##TRIGGER PARA EVITAR QUE UN ENFERMO TENGO ENFERMOS A TU CUIDADO
DROP FUNCTION IF EXISTS permiteCuidador;

DELIMITER //
CREATE FUNCTION permiteCuidador (nifCuidadorIn VARCHAR(9), rolIn VARCHAR(25))
	RETURNS bool
    READS SQL DATA
	DETERMINISTIC
BEGIN
    DECLARE cuidador bool;
    DECLARE rolCuidador VARCHAR(25);
    
    SELECT rol
    INTO rolCuidador
    FROM Usuario
    WHERE nif = nifCuidadorIn;
    
	#signal sqlstate '45000' set message_text = rolCuidador;
    
    if(rolCuidador = 'cuidador' AND rolIn = 'enfermo')
    THEN
		SET cuidador = true;
    ELSE 
		SET cuidador = false;
    END IF;
    
    RETURN cuidador;
END //
DELIMITER ;

SELECT * from usuario;

SELECT permiteCuidador('12344', 'cuidador');

DROP TRIGGER IF EXISTS triggerPermiteCuidador;

DELIMITER //
CREATE TRIGGER triggerPermiteCuidador
BEFORE INSERT ON Usuario
FOR EACH ROW
BEGIN
  IF permiteCuidador(NEW.id_cuidador, NEW.rol) = 0
    THEN
      signal sqlstate '45000' set message_text = 'El nif del cuidador no pertenece a un cuidador válido o el rol de este usuario es distinto a "enfermo"';
  END IF;
END//
DELIMITER ;

##TRIGGER PARA EVITAR NIF INCORRECTO
DROP FUNCTION IF EXISTS nifValido;

DELIMITER //
CREATE FUNCTION nifValido (nif VARCHAR(9))
	RETURNS bool
    READS SQL DATA
	DETERMINISTIC
BEGIN
    DECLARE valido bool;
	SET valido = true;
    if(nif not regexp '([a-z]|[A-Z]|[0-9])[0-9]{7}([a-z]|[A-Z]|[0-9])')
    THEN
		SET valido = false;
    END IF;
        
    RETURN valido;
END //
DELIMITER ;

DROP TRIGGER IF EXISTS triggerNifValido;

DELIMITER //
CREATE TRIGGER triggerNifValido
BEFORE INSERT ON Usuario
FOR EACH ROW
BEGIN
  IF nifValido(NEW.nif) = 0
    THEN
      signal sqlstate '45000' set message_text = "Formato de NIF inválido";
  END IF;
END//
DELIMITER ;

insert into Usuario (nif,id_pastillero,firstname, lastname,contraseña, email, rol,id_cuidador) 
values ("12345678A","192R5T","Manuel","Tejano","Tejanito22","admin@admin.es","enfermo","12344");