package es.us.lsi.dad.Dosis;

import java.util.Iterator;
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
		deleteDosis();
		addDosis();
		editDosis();
	}

	public void getAllDosis() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("getAllDosis");
		consumer.handler(message -> {
			System.out.println("BD Dosis");
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
			int id_usuario = jsonDosis.getInteger("id_usuario");
			String hora_inicio = jsonDosis.getString("hora_inicio");
			String dia_semana = jsonDosis.getString("dia_semana");
			Query<RowSet<Row>> query = mySqlClient.query("SELECT * FROM pastillero_dad.Dosis WHERE id_usuario = "
					+ id_usuario + " AND hora_inicio = '" + hora_inicio + "' AND dia_semana = '" + dia_semana + "';");
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
			int id_usuario = jsonDosis.getInteger("id_usuario");
			String hora_inicio = jsonDosis.getString("hora_inicio");
			String dia_semana = jsonDosis.getString("dia_semana");
			Query<RowSet<Row>> query = mySqlClient.query("DELETE FROM pastillero_dad.Dosis WHERE id_usuario = "
					+ id_usuario + " AND hora_inicio = '" + hora_inicio + "' AND dia_semana = '" + dia_semana + "';");
			query.execute(res -> {
				if (res.succeeded()) {
					message.reply("Borrado la dosis " + id_usuario + " " + hora_inicio + " " + dia_semana);
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
			System.out.println("INSERT INTO Dosis (hora_inicio,dia_semana,id_usuario,observacion)" + " VALUES('"
							+ dosis.getHora_inicio() + "','" + dosis.getDia_semana() + "'," + dosis.getId_usuario()
							+ ",'" + dosis.getObservacion() + "');");
			Query<RowSet<Row>> query = mySqlClient
					.query("INSERT INTO Dosis (hora_inicio,dia_semana,id_usuario,observacion)" + " VALUES('"
							+ dosis.getHora_inicio() + "','" + dosis.getDia_semana() + "'," + dosis.getId_usuario()
							+ ",'" + dosis.getObservacion() + "');");
			query.execute(res -> {
				if (res.succeeded()) {
					message.reply("Añadida la dosis " + dosis.getId_usuario() + " " + dosis.getDia_semana() + " " + dosis);
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
			int id_usuario = jsonDosis.getInteger("id_usuario");
			String hora_inicio = jsonDosis.getString("hora_inicio");
			String dia_semana = jsonDosis.getString("dia_semana");
			jsonDosis.remove("id_usuario");
			jsonDosis.remove("hora_inicio");
			jsonDosis.remove("dia_semana");
			String stringQuery = "UPDATE pastillero_dad.Dosis SET ";
			Iterator<Entry<String, Object>> iteratorJsonDosis = jsonDosis.iterator();
			while (iteratorJsonDosis.hasNext()) {
				Entry<String, Object> elemento = iteratorJsonDosis.next();
				stringQuery += elemento.getKey() + " = '" + elemento.getValue() + "'";
				if (iteratorJsonDosis.hasNext()) {
					stringQuery += ", ";
				}
			}
			stringQuery += "WHERE id_usuario = "
					+ id_usuario + " AND hora_inicio = '" + hora_inicio + "' AND dia_semana = '" + dia_semana + "';";
			System.out.println(stringQuery);
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
}
