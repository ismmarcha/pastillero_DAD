package es.us.lsi.dad.Dosis;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Query;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

public class BDDosis {
	Vertx vertx;
	MySQLPool mySqlClient;

	public BDDosis(Vertx vertx, MySQLPool mySqlClient) {
		this.vertx = vertx;
		this.mySqlClient = mySqlClient;
	}

	public void iniciarConsumersBDDosis() {
		getAllDosis();
		getDosis();
		getDosisPorUsuario();
		getDosisPorUsuarioYDia();
		getSiguienteDosisPorUsuario();
		deleteDosis();
		editDosis();
		addDosis();
		
		getAllRegistroDosis();
		addRegistroDosis();
		deleteRegistroDosis();
		editRegistroDosis();
		
	}

	public void getAllDosis() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("getAllDosis");
		consumer.handler(message -> {
			Query<RowSet<Row>> query = mySqlClient.query("SELECT * FROM pastillero_dad.Dosis;");
			query.execute(res -> {
				JsonObject resultadoJson = new JsonObject();
				if (res.succeeded()) {
					res.result().forEach(v -> {
						DosisImpl dosis = new DosisImpl(v);
						resultadoJson.put(String.valueOf(dosis.getId_dosis()), dosis.getJson());
					});
				} else {
					resultadoJson.put("error", String.valueOf(res.cause()));
				}
				message.reply(resultadoJson);
			});
		});
	}

	public void getDosis() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("getDosis");
		consumer.handler(message -> {
			String datosDosis = message.body();
			JsonObject jsonDosis = new JsonObject(datosDosis);
			String nif = jsonDosis.getString("nif");
			String hora_inicio = jsonDosis.getString("hora_inicio");
			String dia_semana = jsonDosis.getString("dia_semana");
			Query<RowSet<Row>> query = mySqlClient.query("SELECT * FROM pastillero_dad.Dosis WHERE nif = '" + nif
					+ "' AND hora_inicio = '" + hora_inicio + "' AND dia_semana = '" + dia_semana + "';");
			query.execute(res -> {
				JsonObject resultadoJson = new JsonObject();
				if (res.succeeded()) {
					res.result().forEach(v -> {
						DosisImpl dosis = new DosisImpl(v);
						resultadoJson.put(String.valueOf(dosis.getId_dosis()), dosis.getJson());
					});
				} else {
					resultadoJson.put("error", String.valueOf(res.cause()));
				}
				message.reply(resultadoJson);
			});
		});
	}

	public void getDosisPorUsuario() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("getDosisPorUsuario");
		consumer.handler(message -> {
			String nif = message.body();
			Query<RowSet<Row>> query = mySqlClient
					.query("SELECT * FROM pastillero_dad.Dosis WHERE nif = '" + nif + "';");
			query.execute(res -> {
				JsonObject resultadoJson = new JsonObject();
				if (res.succeeded()) {
					res.result().forEach(v -> {
						DosisImpl dosis = new DosisImpl(v);
						resultadoJson.put(String.valueOf(dosis.getId_dosis()), dosis.getJson());
					});
				} else {
					resultadoJson.put("error", String.valueOf(res.cause()));
				}
				message.reply(resultadoJson);
			});
		});
	}

	public void getDosisPorUsuarioYDia() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("getDosisPorUsuarioYDia");
		consumer.handler(message -> {
			JsonObject jsonBody = new JsonObject(message.body());
			String nif = jsonBody.getString("nif");
			String dia_semana = jsonBody.getString("dia_semana");
			Query<RowSet<Row>> query = mySqlClient.query("SELECT * FROM pastillero_dad.Dosis WHERE nif = '" + nif + "'"
					+ " AND dia_semana = '" + dia_semana + "';");
			query.execute(res -> {
				JsonObject resultadoJson = new JsonObject();
				if (res.succeeded()) {
					res.result().forEach(v -> {
						DosisImpl dosis = new DosisImpl(v);
						resultadoJson.put(String.valueOf(dosis.getId_dosis()), dosis.getJson());
					});
				} else {
					resultadoJson.put("error", String.valueOf(res.cause()));
				}
				message.reply(resultadoJson);
			});
		});
	}

	public void getSiguienteDosisPorUsuario() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("getSiguienteDosisPorUsuario");
		consumer.handler(message -> {
			String nif = message.body();
			/*LocalDateTime ldt = LocalDateTime.now();
			Locale localeEs = new Locale("es", "ES");
			String dia_semana = ldt.getDayOfWeek().getDisplayName(TextStyle.NARROW, localeEs);
			String siguiente_dia_semana = ldt.plusDays(1).getDayOfWeek().getDisplayName(TextStyle.NARROW, localeEs);
			LocalTime lt = LocalTime.of(ldt.getHour(), ldt.getMinute());
			System.out.println(dia_semana);*/
			Query<RowSet<Row>> query = mySqlClient.query(
					"SELECT * FROM pastillero_dad.Dosis WHERE nif = '"+nif+"' "
					+ "AND ((now() < DATE_FORMAT(CONCAT(year(now()),'-',month(CURDATE()),'-',day(now()),' ', hora_inicio), '%Y-%m-%d %T') "
					+ " AND dia_semana = weekday(now())) OR (now() < DATE_ADD(DATE_FORMAT(CONCAT(year(now()),'-',month(CURDATE()),'-',day(now()),' ', hora_inicio), '%Y-%m-%d %T'),INTERVAL 1 DAY) AND dia_semana = weekday(DATE_ADD(now(), INTERVAL 1 DAY))));");
			System.out.println("SELECT * FROM pastillero_dad.Dosis WHERE nif = '"+nif+"' "
					+ "AND ((now() < DATE_FORMAT(CONCAT(year(now()),'-',month(CURDATE()),'-',day(now()),' ', hora_inicio), '%Y-%m-%d %T') "
					+ " AND dia_semana = weekday(now())) OR (now() < DATE_ADD(DATE_FORMAT(CONCAT(year(now()),'-',month(CURDATE()),'-',day(now()),' ', hora_inicio), '%Y-%m-%d %T'),INTERVAL 1 DAY) AND dia_semana = weekday(DATE_ADD(now(), INTERVAL 1 DAY)))");
			query.execute(res -> {
				JsonObject resultadoJson = new JsonObject();
				if (res.succeeded()) {
					res.result().forEach(v -> {
						DosisImpl dosis = new DosisImpl(v);
						resultadoJson.put(String.valueOf(dosis.getId_dosis()), dosis.getJson());
					});
				} else {
					resultadoJson.put("error", String.valueOf(res.cause()));
				}
				message.reply(resultadoJson);
			});
		});
	}

	public void deleteDosis() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("deleteDosis");
		consumer.handler(message -> {
			String datosDosis = message.body();
			JsonObject jsonDosis = new JsonObject(datosDosis);
			String nif = jsonDosis.getString("nif");
			String hora_inicio = jsonDosis.getString("hora_inicio");
			String dia_semana = jsonDosis.getString("dia_semana");
			Query<RowSet<Row>> query = mySqlClient.query("DELETE FROM pastillero_dad.Dosis WHERE nif = '" + nif
					+ "' AND hora_inicio = '" + hora_inicio + "' AND dia_semana = '" + dia_semana + "';");
			query.execute(res -> {
				if (res.succeeded()) {
					message.reply("Borrado la dosis " + nif + " " + hora_inicio + " " + dia_semana);
				} else {
					message.reply("ERROR AL BORRAR LA DOSIS " + res.cause());
				}
				;
			});
		});
	}

	public void addDosis() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("addDosis");
		consumer.handler(message -> {
			DosisImpl dosis = new DosisImpl(message.body());
			Query<RowSet<Row>> query = mySqlClient.query("INSERT INTO Dosis (hora_inicio,dia_semana,nif,observacion)"
					+ " VALUES('" + dosis.getHora_inicio() + "','" + dosis.getDia_semana() + "','" + dosis.getnif()
					+ "','" + dosis.getObservacion() + "');");
			query.execute(res -> {
				if (res.succeeded()) {
					message.reply("Añadida la dosis " + dosis.getnif() + " " + dosis.getDia_semana() + " " + dosis);
				} else {
					message.reply("ERROR AL AÑADIR EL Dosis " + res.cause());
				}
				;
			});
		});
	}
	
	
	public void editDosis() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("editDosis");
		consumer.handler(message -> {
			String datosDosis = message.body();
			JsonObject jsonDosis = new JsonObject(datosDosis);
			String nif = jsonDosis.getString("nif");
			String hora_inicio = jsonDosis.getString("hora_inicio");
			String dia_semana = jsonDosis.getString("dia_semana");
			jsonDosis.remove("nif");
			jsonDosis.remove("hora_inicio");
			jsonDosis.remove("dia_semana");
			String stringQuery = "UPDATE pastillero_dad.Dosis SET ";
			Iterator<Entry<String, Object>> iteratorJsonDosis = jsonDosis.iterator();
			while (iteratorJsonDosis.hasNext()) {
				Entry<String, Object> elemento = iteratorJsonDosis.next();
				if (elemento.getValue() == null || elemento.getValue() instanceof Number) {
					stringQuery += elemento.getValue();
				} else {
					stringQuery += "'" + elemento.getValue() + "'";
				}
				if (iteratorJsonDosis.hasNext()) {
					stringQuery += ", ";
				}
			}
			stringQuery += "WHERE nif = '" + nif + "' AND hora_inicio = '" + hora_inicio + "' AND dia_semana = '"
					+ dia_semana + "';";
			Query<RowSet<Row>> query = mySqlClient.query(stringQuery);
			query.execute(res -> {
				if (res.succeeded()) {
					message.reply("Editado el Dosis");
				} else {
					message.reply("ERROR AL EDITAR EL USUARIO " + res.cause());
				}
				;
			});
		});
	}
	
	
	public void getAllRegistroDosis() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("getAllRegistroDosis");
		consumer.handler(message -> {
			String datosUsuario = message.body();
			JsonObject jsonDosis = new JsonObject(datosUsuario);
			String nif = jsonDosis.getString("nif");
			Query<RowSet<Row>> query = mySqlClient.query("SELECT Registro_Dosis.id_registro_dosis, Registro_Dosis.id_dosis, Registro_Dosis.tomada FROM pastillero_dad.Registro_Dosis JOIN pastillero_dad.dosis ON registro_dosis.id_dosis = dosis.id_dosis WHERE dosis.nif = ' "+ nif + "' ;" );
			query.execute(res -> {
				JsonObject resultadoJson = new JsonObject();
				if (res.succeeded()) {
					res.result().forEach(v -> {
						DosisImpl dosis = new DosisImpl(v);
						resultadoJson.put(String.valueOf(dosis.getId_dosis()), dosis.getJson());
					});
				} else {
					resultadoJson.put("error", String.valueOf(res.cause()));
				}
				message.reply(resultadoJson);
			});
		});
	}
	
	public void addRegistroDosis() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("addRegistroDosis");
		consumer.handler(message -> {
			int id_dosis = Integer.valueOf(message.body());
			Query<RowSet<Row>> query = mySqlClient.query("INSERT INTO Registro_Dosis (id_dosis)"
					+ " VALUES("+id_dosis+")");
			query.execute(res -> {
				if (res.succeeded()) {
					message.reply("Añadido Registro Dosis con id_dosis: "+ id_dosis);
				} else {
					message.reply("ERROR AL AÑADIR EL REGISTRO DOSIS " + res.cause());
				}
				;
			});
		});
	}
	
	public void deleteRegistroDosis() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("addRegistroDosis");
		consumer.handler(message -> {
			int id_dosis = Integer.valueOf(message.body());
			Query<RowSet<Row>> query = mySqlClient.query("INSERT INTO Registro_Dosis (id_dosis)"
					+ " VALUES("+id_dosis+")");
			query.execute(res -> {
				if (res.succeeded()) {
					message.reply("Añadido Registro Dosis con id_dosis: "+ id_dosis);
				} else {
					message.reply("ERROR AL AÑADIR EL REGISTRO DOSIS " + res.cause());
				}
				;
			});
		});
	}
	
	public void editRegistroDosis() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("addRegistroDosis");
		consumer.handler(message -> {
			int id_dosis = Integer.valueOf(message.body());
			Query<RowSet<Row>> query = mySqlClient.query("INSERT INTO Registro_Dosis (id_dosis)"
					+ " VALUES("+id_dosis+")");
			query.execute(res -> {
				if (res.succeeded()) {
					message.reply("Añadido Registro Dosis con id_dosis: "+ id_dosis);
				} else {
					message.reply("ERROR AL AÑADIR EL REGISTRO DOSIS " + res.cause());
				}
				;
			});
		});
	}

}
