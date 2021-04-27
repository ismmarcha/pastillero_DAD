package es.us.lsi.dad.Pastillero;

import java.util.Iterator;
import java.util.Map.Entry;

import es.us.lsi.dad.Pastilla.PastillaImpl;
import es.us.lsi.dad.Usuario.UsuarioImpl;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Query;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

public class BDPastillero {
	Vertx vertx;
	MySQLPool mySqlClient;

	public BDPastillero(Vertx vertx, MySQLPool mySqlClient) {
		this.vertx = vertx;
		this.mySqlClient = mySqlClient;
	}

	public void iniciarConsumersBDPastillero() {
		getAllPastillero();
		getPastillero();
		deletePastillero();
		addPastillero();
		editPastillero();
	}

	public void getAllPastillero() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("getAllPastillero");
		consumer.handler(message -> {
			Query<RowSet<Row>> query = mySqlClient.query("SELECT * FROM pastillero_dad.Pastillero;");
			query.execute(res -> {
				JsonObject resultadoJson = new JsonObject();
				if (res.succeeded()) {
					res.result().forEach(v -> {
						PastilleroImpl pastillero = new PastilleroImpl(v);
						resultadoJson.put(String.valueOf(pastillero.getId_pastillero()), pastillero.getJson());
					});
				} else {
					resultadoJson.put("error", String.valueOf(res.cause()));
				}
				message.reply(resultadoJson);
			});
		});
	}

	public void getPastillero() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("getPastillero");
		consumer.handler(message -> {
			String datosPastillero = message.body();
			JsonObject jsonPastillero = new JsonObject(datosPastillero);
			String Id_pastillero = jsonPastillero.getString("id_pastillero");
			Query<RowSet<Row>> query = mySqlClient
					.query("SELECT * FROM pastillero_dad.Pastillero WHERE id_pastillero = '" + Id_pastillero + "';");
			query.execute(res -> {
				JsonObject resultadoJson = new JsonObject();
				if (res.succeeded()) {
					res.result().forEach(v -> {
						PastilleroImpl pastillero = new PastilleroImpl(v);
						resultadoJson.put(String.valueOf(pastillero.getId_pastillero()), pastillero.getJson());
					});
				} else {
					resultadoJson.put("error", String.valueOf(res.cause()));
				}
				message.reply(resultadoJson);
			});
		});
	}

	public void deletePastillero() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("deletePastillero");
		consumer.handler(message -> {
			String datosPastillero = message.body();
			JsonObject jsonPastillero = new JsonObject(datosPastillero);
			String Id_pastillero = jsonPastillero.getString("id_pastillero");

			Query<RowSet<Row>> query = mySqlClient
					.query("DELETE FROM pastillero_dad.Pastillero WHERE id_pastillero = '" + Id_pastillero + "';");
			query.execute(res -> {
				if (res.succeeded()) {
					message.reply("Borrado el pastillero " + Id_pastillero);
				} else {
					message.reply("ERROR AL BORRAR EL PASTILLERO " + res.cause());
				}
				;
			});
		});
	}

	public void addPastillero() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("addPastillero");
		consumer.handler(message -> {
			PastilleroImpl pastillero = new PastilleroImpl(message.body());

			Query<RowSet<Row>> query = mySqlClient.query("INSERT INTO Pastillero(id_pastillero, alias) VALUES ('"
					+ pastillero.getId_pastillero() + "','" + pastillero.getAlias() + "');");
			query.execute(res -> {
				if (res.succeeded()) {
					message.reply("Añadido el pastillero " + pastillero.getId_pastillero());
				} else {
					message.reply("ERROR AL AÑADIR EL PASTILLERO " + res.cause());
				}
				;
			});
		});
	}

	public void editPastillero() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("editPastillero");
		consumer.handler(message -> {
			String datosPastillero = message.body();
			JsonObject jsonPastillero = new JsonObject(datosPastillero);

			String id_pastillero = jsonPastillero.getString("id_pastillero");
			jsonPastillero.remove(id_pastillero);
			String stringQuery = "UPDATE pastillero_dad.Pastillero SET ";

			Iterator<Entry<String, Object>> iteratorJsonPastillero = jsonPastillero.iterator();
			while (iteratorJsonPastillero.hasNext()) {
				Entry<String, Object> elemento = iteratorJsonPastillero.next();
				if (elemento.getValue() == null || elemento.getValue() instanceof Number) {
					stringQuery += elemento.getValue();
				} else {
					stringQuery += "'" + elemento.getValue() + "'";
				}
				if (iteratorJsonPastillero.hasNext()) {
					stringQuery += ", ";
				}
			}
			stringQuery += " WHERE id_pastillero = '" + id_pastillero + "';";

			Query<RowSet<Row>> query = mySqlClient.query(stringQuery);
			System.out.println(query);
			query.execute(res -> {
				if (res.succeeded()) {
					message.reply("Editado el pastillero");
				} else {
					message.reply("ERROR AL EDITAR EL USUARIO " + res.cause());
				}
				;
			});
		});
	}
}
