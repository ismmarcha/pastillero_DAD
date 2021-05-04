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
		getAllPastillero();
		getPastillero();
		getUsuariosPorPastillero();
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
					resultadoJson.put("error", "ERROR AL MOSTRAR TODOS LOS PASTILLEROS"+" ."+ String.valueOf(res.cause()));
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
					resultadoJson.put("error", "ERROR AL MOSTRAR EL PASTILLERO CON ID: "+ Id_pastillero +" ."+ String.valueOf(res.cause()));
				}
				message.reply(resultadoJson);
			});
		});
	}
	
	public void getUsuariosPorPastillero() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("getUsuariosPorPastillero");
		consumer.handler(message -> {
			String datosPastillero = message.body();
			JsonObject jsonPastillero = new JsonObject(datosPastillero);
			String Id_pastillero = jsonPastillero.getString("id_pastillero");
			System.out.println(Id_pastillero);
			Query<RowSet<Row>> query = mySqlClient
					.query("SELECT * FROM pastillero_dad.Usuario WHERE id_pastillero = '" + Id_pastillero + "';");
			query.execute(res -> {
				JsonObject resultadoJson = new JsonObject();
				if (res.succeeded()) {
					res.result().forEach(v -> {
						UsuarioImpl usuario = new UsuarioImpl(v);
						resultadoJson.put(String.valueOf(usuario.getNif()), usuario.getJson());
					});
				} else {
					resultadoJson.put("error", "ERROR AL OBTENER LOS USUARIOS DEL PASTILLERO CON ID: "+ Id_pastillero +" ."+ String.valueOf(res.cause()));
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
				JsonObject json = new JsonObject();
				if (res.succeeded()) {
<<<<<<< Updated upstream
					res.result().forEach(v -> {
						json.put(Id_pastillero, "BORRADO EL PASTILLERO CON ID:  " + Id_pastillero);
					});
=======
					Row row = res.result().iterator().next();
					if (row.getInteger("nPastilleros") <= 0) {
						json.put("error", "ERROR AL ELIMINAR EL PASTILLERO CON ID: " + id_pastillero
								+ " ERROR: PASTILLERO NO ENCONTRADO");
						message.reply(json);
					} else {
						Query<RowSet<Row>> query2 = mySqlClient
								.query("DELETE FROM pastillero_dad.Pastillero WHERE id_pastillero = '" + id_pastillero + "';");
						query2.execute(res2 -> {
							JsonObject json2 = new JsonObject();
							if (res2.succeeded()) {
								json2.put(id_pastillero, "PASTILLERO BORRADO CON EL ID " + id_pastillero);
							} else {
								json2.put("error", "ERROR AL BORRAR EL USUARIO CON NIF: " + id_pastillero + " ."
										+ String.valueOf(res2.cause()));
							}
							message.reply(json2);
						});
					}
>>>>>>> Stashed changes
				} else {
					json.put("error", "ERROR AL BORRAR EL PASTILLERO CON ID: "+ Id_pastillero+" ."+ String.valueOf(res.cause()) );
				}
				message.reply(json);
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
				JsonObject json = new JsonObject();
				if (res.succeeded()) {
					res.result().forEach(v -> {
						json.put(pastillero.getId_pastillero(), "AÑADIDO EL PASTILLERO CON ID:  " + pastillero.getId_pastillero());
				});
				} else {
					json.put("error", "ERROR AL AÑADIR EL PASTILLERO CON ID: "+ pastillero.getId_pastillero());
				}
				message.reply(json);
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
				stringQuery += elemento.getKey() + " = ";
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
				JsonObject json = new JsonObject();
				if (res.succeeded()) {
					res.result().forEach(v -> {
						json.put(id_pastillero, "EDITADO EL PASTILLERO CON ID:  " + id_pastillero);
				});
				} else {
					json.put("error", "ERROR AL EDITAR EL PASTILLERO CON ID: "+ id_pastillero);
				}
				message.reply(json);

			});
		});
	}
}
