use pastillero_dad;

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

insert into Usuario (nif,id_pastillero,firstname, lastname,contraseña, email, rol,id_cuidador) 
values ("1234528E","192R5T","Manuel","Tejano","Tejanito22","admin@admin.es","enfermo","12344");

UPDATE Usuario SET firstname = "helado" WHERE nif = "12345678U";

if((select count(*) from Usuario WHERE nif = '12345678U') > 0, SELECT "hola", SELECT "adios");