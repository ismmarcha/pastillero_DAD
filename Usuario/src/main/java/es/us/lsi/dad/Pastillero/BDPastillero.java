package es.us.lsi.dad.Pastillero;

import java.util.Iterator;
import java.util.Map.Entry;

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
		getPastilleros();
		getPastillero();
		deletePastillero();
		addPastillero();
		editPastillero();
	}

	public void getPastilleros() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("getPastilleros");
		consumer.handler(message -> {
			Query<RowSet<Row>> query = mySqlClient.query("SELECT * FROM pastillero_dad.Pastillero;");
			query.execute(res -> {
				JsonObject resultadoJson = new JsonObject();
				if (res.succeeded()) {
					res.result().forEach(v -> {
						JsonObject pastilleroJson = new JsonObject();
						pastilleroJson.put("id_pastillero", String.valueOf(v.getValue("id_pastillero")));
						pastilleroJson.put("alias", String.valueOf(v.getValue("alias")));
						resultadoJson.put(String.valueOf(v.getValue("id_pastillero")), pastilleroJson);
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
			String pastilleroId = message.body();
			Query<RowSet<Row>> query = mySqlClient.query("SELECT * FROM pastillero_dad. WHERE id_pastillero = '"+pastilleroId+"';");
			query.execute(res -> {
				JsonObject resultadoJson = new JsonObject();
				if (res.succeeded()) {
					res.result().forEach(v -> {
						JsonObject pastilleroJson = new JsonObject();
						pastilleroJson.put("id_pastillero", String.valueOf(v.getValue("id_pastillero")));
						pastilleroJson.put("alias", String.valueOf(v.getValue("alias")));
						resultadoJson.put(String.valueOf(v.getValue("id_pastillero")), pastilleroJson);
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
			String pastilleroId = message.body();
			Query<RowSet<Row>> query = mySqlClient
					.query("DELETE FROM pastillero_dad.Pastillero WHERE id_pastillero = '" + pastilleroId + "';");
			query.execute(res -> {
				if (res.succeeded()) {
					message.reply("Borrado el pastillero " + pastilleroId);
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
			JsonObject jsonNewPastillero = new JsonObject(message.body());
			String pastilleroId = String.valueOf(jsonNewPastillero.getString("id_pastillero"));
			String pastilleroAlias = String.valueOf(jsonNewPastillero.getString("alias"));
			Query<RowSet<Row>> query = mySqlClient
					.query("INSERT INTO pastillero_dad.Pastillero(id_pastillero, alias) VALUES ('" + pastilleroId
							+ "','" + pastilleroAlias + "');");
			query.execute(res -> {
				if (res.succeeded()) {
					message.reply("Añadido el pastillero " + pastilleroId);
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
			JsonObject jsonEditPastillero = new JsonObject(message.body());
			String pastilleroId = String.valueOf(jsonEditPastillero.getString("pastilleroId"));
			jsonEditPastillero.remove("pastilleroId");
			String stringQuery = "UPDATE pastillero_dad.Pastillero SET ";
			Iterator<Entry<String, Object>> iteratorJsonPastillero = jsonEditPastillero.iterator();
			while (iteratorJsonPastillero.hasNext()) {
				Entry<String, Object> elemento = iteratorJsonPastillero.next();
				stringQuery += elemento.getKey() + " = '" + elemento.getValue() + "'";
				if (iteratorJsonPastillero.hasNext()) {
					stringQuery += ", ";
				}
			}
			stringQuery += " WHERE id_pastillero = '" + pastilleroId + "';";
			System.out.println(stringQuery);
			Query<RowSet<Row>> query = mySqlClient.query(stringQuery);
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
