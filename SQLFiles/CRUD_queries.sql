use pastillero_dad;

insert into Pastillero (id_pastillero , alias) values ('w1e2d4f5ffeecnss3fpol247hg7fg1244423435g',"Pastillero Tío Salvador");
insert into Pastillero (id_pastillero , alias) values ('f1e5d4fyr83djwi2o3o3e247hg7fg1211420135g',"Pastillero Abuelo Paco");


insert into Usuario (nif,id_pastillero,firstname, lastname,contraseña, email, rol) values ("45349133D","w1e2d4f5ffeecnss3fpol247hg7fg1244423435g","Ismael","Márquez Chacón","ProyectoDAD11","Ismael@DAD.es","cuidador");
insert into Usuario (nif,id_pastillero,firstname, lastname,contraseña, email, rol) values ("71241839J","f1e5d4fyr83djwi2o3o3e247hg7fg1211420135g","Manuel","Lorenzo Hidalgo","ProyectoDAD11","Manuel@DAD.es","cuidador");

insert into Usuario (nif,id_pastillero,firstname, lastname,contraseña, email, rol,id_cuidador) values ("78130288F","w1e2d4f5ffeecnss3fpol247hg7fg1244423435g","Salvador","Márquez Palacios","Salvador22","enfermo1@DAD.es","enfermo","45349133D");
insert into Usuario (nif,id_pastillero,firstname, lastname,contraseña, email, rol,id_cuidador) values ("53420191L","f1e5d4fyr83djwi2o3o3e247hg7fg1211420135g","Francisco","Tejano Rodríguez","TejanoRod22","enfermo2@DAD.es","enfermo","71241839J");


insert into Pastilla (nombre,descripcion,peso) values ("Paracetamol","Comprimidos EPG","500");
insert into Pastilla (nombre,descripcion,peso) values ("Nolotil","Comprimidos recubiertos","100");
insert into Pastilla (nombre,descripcion,peso) values ("Sintrom","Comprimidos EPG","200");
insert into Pastilla (nombre,descripcion,peso) values ("Dormidina","Comprimidos EPG","100");
insert into Pastilla (nombre,descripcion,peso) values ("Lorazepam","Comprimidos EPG","300");
insert into Pastilla (nombre,descripcion,peso) values ("Oxacepam","Comprimidos recubiertos","100");


insert into Dosis (hora_inicio,dia_semana,nif) values ("10:00",1,"78130288F");
insert into Dosis (hora_inicio,dia_semana,nif) values ("14:00",1,"78130288F");
insert into Dosis (hora_inicio,dia_semana,nif) values ("18:00",1,"78130288F");
insert into Dosis (hora_inicio,dia_semana,nif) values ("22:00",1,"78130288F");
insert into Dosis (hora_inicio,dia_semana,nif) values ("10:00",2,"78130288F");
insert into Dosis (hora_inicio,dia_semana,nif) values ("14:00",2,"78130288F");
insert into Dosis (hora_inicio,dia_semana,nif) values ("18:00",2,"78130288F");
insert into Dosis (hora_inicio,dia_semana,nif) values ("22:00",2,"78130288F");
insert into Dosis (hora_inicio,dia_semana,nif) values ("10:00",3,"78130288F");
insert into Dosis (hora_inicio,dia_semana,nif) values ("14:00",3,"78130288F");
insert into Dosis (hora_inicio,dia_semana,nif) values ("18:00",3,"78130288F");
insert into Dosis (hora_inicio,dia_semana,nif,observacion) values ("22:00",3,"78130288F","MUY IMPORTANTE, MAÑANA PRUEBA MÉDICA");
insert into Dosis (hora_inicio,dia_semana,nif) values ("10:00",4,"78130288F");
insert into Dosis (hora_inicio,dia_semana,nif) values ("14:00",4,"78130288F");
insert into Dosis (hora_inicio,dia_semana,nif) values ("18:00",4,"78130288F");
insert into Dosis (hora_inicio,dia_semana,nif) values ("22:00",4,"78130288F");
insert into Dosis (hora_inicio,dia_semana,nif) values ("10:00",5,"78130288F");
insert into Dosis (hora_inicio,dia_semana,nif) values ("14:00",5,"78130288F");
insert into Dosis (hora_inicio,dia_semana,nif) values ("18:00",5,"78130288F");
insert into Dosis (hora_inicio,dia_semana,nif) values ("22:00",5,"78130288F");
insert into Dosis (hora_inicio,dia_semana,nif) values ("10:00",6,"78130288F");
insert into Dosis (hora_inicio,dia_semana,nif) values ("14:00",6,"78130288F");
insert into Dosis (hora_inicio,dia_semana,nif) values ("18:00",6,"78130288F");
##insert into Dosis (hora_inicio,dia_semana,nif) values ("22:00",6,"78130288F");
insert into Dosis (hora_inicio,dia_semana,nif) values ("10:00",0,"78130288F");
insert into Dosis (hora_inicio,dia_semana,nif) values ("14:00",0,"78130288F");
insert into Dosis (hora_inicio,dia_semana,nif) values ("18:00",0,"78130288F");
insert into Dosis (hora_inicio,dia_semana,nif) values ("22:00",0,"78130288F");

insert into Dosis (hora_inicio,dia_semana,nif) values ("10:00",1,"53420191L");
insert into Dosis (hora_inicio,dia_semana,nif) values ("14:00",1,"53420191L");
insert into Dosis (hora_inicio,dia_semana,nif) values ("18:00",1,"53420191L");
insert into Dosis (hora_inicio,dia_semana,nif) values ("22:00",1,"53420191L");
insert into Dosis (hora_inicio,dia_semana,nif) values ("10:00",2,"53420191L");
insert into Dosis (hora_inicio,dia_semana,nif) values ("14:00",2,"53420191L");
insert into Dosis (hora_inicio,dia_semana,nif) values ("18:00",2,"53420191L");
insert into Dosis (hora_inicio,dia_semana,nif) values ("22:00",2,"53420191L");
insert into Dosis (hora_inicio,dia_semana,nif) values ("10:00",3,"53420191L");
insert into Dosis (hora_inicio,dia_semana,nif) values ("14:00",3,"53420191L");
insert into Dosis (hora_inicio,dia_semana,nif) values ("18:00",3,"53420191L");
insert into Dosis (hora_inicio,dia_semana,nif) values ("22:00",3,"53420191L");
insert into Dosis (hora_inicio,dia_semana,nif) values ("10:00",4,"53420191L");
insert into Dosis (hora_inicio,dia_semana,nif) values ("14:00",4,"53420191L");
insert into Dosis (hora_inicio,dia_semana,nif) values ("18:00",4,"53420191L");
insert into Dosis (hora_inicio,dia_semana,nif) values ("22:00",4,"53420191L");
insert into Dosis (hora_inicio,dia_semana,nif) values ("10:00",5,"53420191L");
insert into Dosis (hora_inicio,dia_semana,nif) values ("14:00",5,"53420191L");
insert into Dosis (hora_inicio,dia_semana,nif) values ("18:00",5,"53420191L");
insert into Dosis (hora_inicio,dia_semana,nif) values ("22:00",5,"53420191L");
insert into Dosis (hora_inicio,dia_semana,nif) values ("10:00",6,"53420191L");
insert into Dosis (hora_inicio,dia_semana,nif) values ("14:00",6,"53420191L");
insert into Dosis (hora_inicio,dia_semana,nif) values ("19:00",6,"53420191L");
insert into Dosis (hora_inicio,dia_semana,nif) values ("22:00",6,"53420191L");
insert into Dosis (hora_inicio,dia_semana,nif,observacion) values ("10:00",0,"53420191L","MUY IMPORTANTE");
insert into Dosis (hora_inicio,dia_semana,nif) values ("14:00",0,"53420191L");
insert into Dosis (hora_inicio,dia_semana,nif) values ("18:00",0,"53420191L");
insert into Dosis (hora_inicio,dia_semana,nif) values ("22:00",0,"53420191L");


insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("1","1",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("3","1",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("1","2",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("5","3",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("4","4",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("1","5",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("3","5",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("1","6",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("5","7",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("4","8",1);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("1","9",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("3","9",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("1","10",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("5","11",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("4","12",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("1","13",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("3","13",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("1","14",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("5","15",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("4","16",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("1","17",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("3","17",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("1","18",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("5","19",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("4","20",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("1","21",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("3","21",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("1","22",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("5","23",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("4","24",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("1","25",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("3","25",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("1","26",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("5","27",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("4","28",0.5);


insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("1","29",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("3","29",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("1","30",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("5","31",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("4","32",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("1","33",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("3","33",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("1","34",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("5","35",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("4","36",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("1","37",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("3","38",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("1","39",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("5","40",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("4","41",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("1","41",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("3","42",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("1","43",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("5","44",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("4","45",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("1","45",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("3","46",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("1","47",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("5","48",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("4","49",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("1","49",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("3","50",1);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("1","51",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("5","52",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("4","53",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("1","53",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("3","54",0.5);
insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("1","55",0.5);
##insert into Pastilla_Dosis (id_pastilla,id_dosis,cantidad) values ("5","56",0.5);


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
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"15");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"16");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"17");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"18");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"19");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"20");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"21");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"22");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"23");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"24");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"25");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"26");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"27");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"28");

insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"29");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"30");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"31");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"32");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"33");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"34");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"35");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"36");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"37");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"38");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"39");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"40");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"41");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"42");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"43");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"44");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"45");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"46");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"47");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"48");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"49");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"50");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"51");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"52");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"53");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"54");
insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"55");
##insert into Registro_Dosis (tomada,id_dosis) values (FALSE,"56");

SELECT COUNT(*) as nPastilleros FROM pastillero_dad.Pastillero ;
select * from Dosis;
SELECT COUNT(*) as nUsuarios FROM pastillero_dad.Usuario WHERE nif = "78130288F";


SELECT dia_semana, hora_inicio FROM Dosis JOIN pastillero_dad.Usuario ON Usuario.nif = dosis.nif WHERE id_pastillero = 'w1e2d4f5ffeecnss3fpol247hg7fg1244423435g' ORDER BY if(TIMEDIFF(addtime(DATE_ADD(CURDATE(), INTERVAL dia_semana - weekday(CURDATE()) DAY), hora_inicio), now()) < 0, TIMEDIFF(addtime(DATE_ADD(CURDATE(), INTERVAL (7 - weekday(CURDATE())) + dia_semana DAY), hora_inicio), now()), TIMEDIFF(addtime(DATE_ADD(CURDATE(), INTERVAL dia_semana - weekday(CURDATE()) DAY), hora_inicio), now()))  LIMIT 1;



SELECT id_pastillero , dia_semana, hora_inicio FROM Dosis JOIN pastillero_dad.Usuario ON Usuario.nif = dosis.nif  ORDER BY if(TIMEDIFF(addtime(DATE_ADD(CURDATE(), 
INTERVAL dia_semana - weekday(CURDATE()) DAY), hora_inicio), now()) < 0, TIMEDIFF(addtime(DATE_ADD(CURDATE(),
 INTERVAL (7 - weekday(CURDATE())) + dia_semana DAY), hora_inicio), now()), TIMEDIFF(addtime(DATE_ADD(CURDATE(),
 INTERVAL dia_semana - weekday(CURDATE()) DAY), hora_inicio), now()))  LIMIT 1;

