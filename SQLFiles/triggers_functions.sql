use pastillero_dad;

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
    
    if((rolCuidador = 'cuidador' AND rolIn = 'enfermo') OR (rolIn = 'cuidador'))
    THEN
		SET cuidador = true;
    ELSE 
		SET cuidador = false;
    END IF;
    
    RETURN cuidador;
END //
DELIMITER ;

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


##TRIGGER PARA DÍA DE LA SEMANA
DROP FUNCTION IF EXISTS dia_semanaValido;

DELIMITER //
CREATE FUNCTION dia_semanaValido (dia_semana INT)
	RETURNS bool
    READS SQL DATA
	DETERMINISTIC
BEGIN
    DECLARE valido bool;
	SET valido = false;
    if(dia_semana >= 0 AND dia_semana <= 6)
    THEN
		SET valido = true;
    END IF;
        
    RETURN valido;
END //
DELIMITER ;

DROP TRIGGER IF EXISTS triggerdia_semanaValido;

DELIMITER //
CREATE TRIGGER triggerdia_semanaValido
BEFORE INSERT ON Dosis
FOR EACH ROW
BEGIN
  IF dia_semanaValido(NEW.dia_semana) = 0
    THEN
      signal sqlstate '45000' set message_text = "Formato de día de la semana inválido";
  END IF;
END//
DELIMITER ;

