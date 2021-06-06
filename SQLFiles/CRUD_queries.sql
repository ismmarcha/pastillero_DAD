use pastillero_dad;

insert into Pastillero (id_pastillero , alias) values ('a8df25211e38f106b2602c3cb5da01c66616160a',"Pastillero Tío Salvador");
insert into Pastillero (id_pastillero , alias) values ('f1e5d4fyr83djwi2o3o3e247hg7fg1211420135g',"Pastillero Abuelo Paco");


insert into Usuario (nif,id_pastillero,firstname, lastname,contraseña, email, rol) values ("45349133D","a8df25211e38f106b2602c3cb5da01c66616160a","Ismael","Márquez Chacón","ProyectoDAD11","Ismael@DAD.es","cuidador");
insert into Usuario (nif,id_pastillero,firstname, lastname,contraseña, email, rol) values ("71241839J","f1e5d4fyr83djwi2o3o3e247hg7fg1211420135g","Manuel","Lorenzo Hidalgo","ProyectoDAD11","Manuel@DAD.es","cuidador");

insert into Usuario (nif,id_pastillero,firstname, lastname,contraseña, email, rol,id_cuidador) values ("78130288F","a8df25211e38f106b2602c3cb5da01c66616160a","Salvador","Márquez Palacios","Salvador22","enfermo1@DAD.es","enfermo","45349133D");
insert into Usuario (nif,id_pastillero,firstname, lastname,contraseña, email, rol,id_cuidador) values ("53420191L","f1e5d4fyr83djwi2o3o3e247hg7fg1211420135g","Francisco","Tejano Rodríguez","TejanoRod22","enfermo2@DAD.es","enfermo","71241839J");


insert into Pastilla (nombre,descripcion,peso) values ("Paracetamol","Comprimidos EPG","500");
insert into Pastilla (nombre,descripcion,peso) values ("Nolotil","Comprimidos recubiertos","100");
insert into Pastilla (nombre,descripcion,peso) values ("Sintrom","Comprimidos EPG","200");
insert into Pastilla (nombre,descripcion,peso) values ("Dormidina","Comprimidos EPG","100");
insert into Pastilla (nombre,descripcion,peso) values ("Lorazepam","Comprimidos EPG","300");
insert into Pastilla (nombre,descripcion,peso) values ("Oxacepam","Comprimidos recubiertos","100");


insert into Dosis (hora_inicio,dia_semana,nif) values ("10:00",0,"78130288F");
insert into Dosis (hora_inicio,dia_semana,nif) values ("12:00",1,"78130288F");
insert into Dosis (hora_inicio,dia_semana,nif) values ("14:00",2,"78130288F");
insert into Dosis (hora_inicio,dia_semana,nif) values ("16:00",3,"78130288F");
insert into Dosis (hora_inicio,dia_semana,nif) values ("18:00",4,"78130288F");
insert into Dosis (hora_inicio,dia_semana,nif) values ("20:00",5,"78130288F");
insert into Dosis (hora_inicio,dia_semana,nif) values ("22:00",6,"78130288F");

insert into Dosis (hora_inicio,dia_semana,nif) values ("10:00",0,"53420191L");
insert into Dosis (hora_inicio,dia_semana,nif) values ("12:00",1,"53420191L");
insert into Dosis (hora_inicio,dia_semana,nif) values ("14:00",2,"53420191L");
insert into Dosis (hora_inicio,dia_semana,nif) values ("16:00",3,"53420191L");
insert into Dosis (hora_inicio,dia_semana,nif) values ("18:00",4,"53420191L");
insert into Dosis (hora_inicio,dia_semana,nif) values ("20:00",5,"53420191L");
insert into Dosis (hora_inicio,dia_semana,nif) values ("22:00",6,"53420191L");

insert into Dosis (hora_inicio,dia_semana,nif) values ("19:44",6,"78130288F");

insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("1","1",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("3","1",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("1","2",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("3","2",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("1","3",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("3","3",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("1","4",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("3","4",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("1","5",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("3","5",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("1","6",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("3","6",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("1","7",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("3","7",0.5);



insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("2","8",1);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("4","8",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("2","9",1);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("4","9",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("2","10",1);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("4","10",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("2","11",1);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("4","11",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("2","12",1);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("4","12",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("2","13",1);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("4","13",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("2","14",1);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("4","14",0.5);





insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"1");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"2");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"3");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"4");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"5");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"6");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"7");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"8");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"9");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"10");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"11");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"12");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"13");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"14");



SELECT COUNT(*) as nPastilleros FROM pastillero_dad.Pastillero ;
select * from Dosis;
SELECT COUNT(*) as nUsuarios FROM pastillero_dad.Usuario WHERE nif = "78130288F";


SELECT dia_semana, hora_inicio FROM Dosis JOIN pastillero_dad.Usuario ON Usuario.nif = dosis.nif WHERE id_pastillero = 'a8df25211e38f106b2602c3cb5da01c66616160a' ORDER BY if(TIMEDIFF(addtime(DATE_ADD(CURDATE(), INTERVAL dia_semana - weekday(CURDATE()) DAY), hora_inicio), now()) < 0, TIMEDIFF(addtime(DATE_ADD(CURDATE(), INTERVAL (7 - weekday(CURDATE())) + dia_semana DAY), hora_inicio), now()), TIMEDIFF(addtime(DATE_ADD(CURDATE(), INTERVAL dia_semana - weekday(CURDATE()) DAY), hora_inicio), now()))  LIMIT 1;



SELECT id_pastillero , dia_semana, hora_inicio FROM Dosis JOIN pastillero_dad.Usuario ON Usuario.nif = dosis.nif  ORDER BY if(TIMEDIFF(addtime(DATE_ADD(CURDATE(), 
INTERVAL dia_semana - weekday(CURDATE()) DAY), hora_inicio), now()) < 0, TIMEDIFF(addtime(DATE_ADD(CURDATE(),
 INTERVAL (7 - weekday(CURDATE())) + dia_semana DAY), hora_inicio), now()), TIMEDIFF(addtime(DATE_ADD(CURDATE(),
 INTERVAL dia_semana - weekday(CURDATE()) DAY), hora_inicio), now()))  LIMIT 1;

SELECT id_pastillero FROM pastillero_dad.Usuario WHERE nif = "78130288F" ;
