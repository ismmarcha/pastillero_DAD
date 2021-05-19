package es.us.lsi.dad.Pastillero;

import java.util.Iterator;
import java.util.Map.Entry;

import es.us.lsi.dad.Usuario.UsuarioImpl;
import es.us.lsi.dad.Utils.Utils;
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

	Utils utils = new Utils();

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
					resultadoJson.put("error",
							"ERROR AL MOSTRAR TODOS LOS PASTILLEROS" + " ." + String.valueOf(res.cause()));
				}
				message.reply(resultadoJson);
			});
		});
	}

	// EJEMPLO BODY: {"id_pastillero":"f1e5d4fyr83djwi2o3o3e247hg7fg1211420135g"}
	public void getPastillero() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("getPastillero");
		consumer.handler(message -> {
			String datosPastillero = message.body();
			if (utils.checkJson(datosPastillero) == true) {
				JsonObject jsonPastillero = new JsonObject(datosPastillero);
				JsonObject jsonComp = new JsonObject();

				boolean comprobacion = jsonPastillero.containsKey("id_pastillero") && jsonPastillero != null;
				if (comprobacion) {

					String Id_pastillero = jsonPastillero.getString("id_pastillero");
					Query<RowSet<Row>> query = mySqlClient.query(
							"SELECT * FROM pastillero_dad.Pastillero WHERE id_pastillero = '" + Id_pastillero + "';");
					query.execute(res -> {
						JsonObject resultadoJson = new JsonObject();
						if (res.succeeded()) {
							res.result().forEach(v -> {
								PastilleroImpl pastillero = new PastilleroImpl(v);
								resultadoJson.put(String.valueOf(pastillero.getId_pastillero()), pastillero.getJson());
							});
							message.reply(resultadoJson);
						} else {
							resultadoJson.put("error", "ERROR AL MOSTRAR EL PASTILLERO CON ID: " + Id_pastillero + " ."
									+ String.valueOf(res.cause()));
							message.fail(500, String.valueOf(resultadoJson));
						}
					});
				} else {
					jsonComp.put("error",
							"NO SE HAN INTRODUCIDO LOS CAMPOS CORRESPONDIENTES EN EL CUERPO DE LA PETICIÓN.");
					message.fail(500, String.valueOf(jsonComp));
				}
			} else {
				JsonObject checkJson = new JsonObject();
				checkJson.put("error", "FORMATO DE JSON NO VÁLIDO.");
				message.fail(500, String.valueOf(checkJson));
			}
		});
	}

	// EJEMPLO BODY: {"id_pastillero":"f1e5d4fyr83djwi2o3o3e247hg7fg1211420135g"}
	public void getUsuariosPorPastillero() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("getUsuariosPorPastillero");
		consumer.handler(message -> {
			String datosPastillero = message.body();
			if (utils.checkJson(datosPastillero) == true) {
				JsonObject jsonPastillero = new JsonObject(datosPastillero);
				JsonObject jsonComp = new JsonObject();

				boolean comprobacion = jsonPastillero.containsKey("id_pastillero") && jsonPastillero != null;
				if (comprobacion) {

					String Id_pastillero = jsonPastillero.getString("id_pastillero");

					Query<RowSet<Row>> query = mySqlClient.query(
							"SELECT * FROM pastillero_dad.Usuario WHERE id_pastillero = '" + Id_pastillero + "';");
					query.execute(res -> {
						JsonObject resultadoJson = new JsonObject();
						if (res.succeeded()) {
							res.result().forEach(v -> {
								UsuarioImpl usuario = new UsuarioImpl(v);
								resultadoJson.put(String.valueOf(usuario.getNif()), usuario.getJson());
							});
							message.reply(resultadoJson);
						} else {
							resultadoJson.put("error", "ERROR AL OBTENER LOS USUARIOS DEL PASTILLERO CON ID: "
									+ Id_pastillero + " ." + String.valueOf(res.cause()));
							message.fail(500, String.valueOf(resultadoJson));
						}

					});
				} else {
					jsonComp.put("error",
							"NO SE HAN INTRODUCIDO LOS CAMPOS CORRESPONDIENTES EN EL CUERPO DE LA PETICIÓN.");
					message.fail(500, String.valueOf(jsonComp));
				}
			} else {
				JsonObject checkJson = new JsonObject();
				checkJson.put("error", "FORMATO DE JSON NO VÁLIDO.");
				message.fail(500, String.valueOf(checkJson));
			}
		});
	}

	// EJEMPLO BODY: {"id_pastillero":"f1e5d4fyr83djwi2o3o3e247hg7fg1211420135g" }
	public void deletePastillero() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("deletePastillero");
		consumer.handler(message -> {
			String datosPastillero = message.body();
			if (utils.checkJson(datosPastillero) == true) {
				JsonObject jsonPastillero = new JsonObject(datosPastillero);
				JsonObject jsonComp = new JsonObject();

				boolean comprobacion = jsonPastillero.containsKey("id_pastillero") && jsonPastillero != null;

				if (comprobacion) {

					String id_pastillero = jsonPastillero.getString("id_pastillero");
					Query<RowSet<Row>> query1 = mySqlClient.query(
							"SELECT COUNT(*) as nPastilleros FROM pastillero_dad.Pastillero WHERE id_pastillero = '"
									+ id_pastillero + "' ;");

					query1.execute(res1 -> {
						JsonObject json1 = new JsonObject();
						if (res1.succeeded()) {
							Row row = res1.result().iterator().next();
							if (row.getInteger("nPastilleros") > 0) {
								Query<RowSet<Row>> query2 = mySqlClient
										.query("DELETE FROM pastillero_dad.Pastillero WHERE id_pastillero = '"
												+ id_pastillero + "';");
								query2.execute(res2 -> {
									JsonObject json2 = new JsonObject();
									if (res2.succeeded()) {
										json2.put(id_pastillero, "BORRADO EL PASTILLERO CON ID:  " + id_pastillero);
										message.reply(json2);
									} else {
										json2.put("error", "ERROR AL BORRAR EL PASTILLERO CON ID: " + id_pastillero
												+ " ." + String.valueOf(res2.cause()));
										message.fail(500, String.valueOf(json2));
									}

								});
							} else {
								json1.put("error", "ERROR AL BORRAR EL PASTILLERO CON ID: " + id_pastillero + " ."
										+ " NO EXISTE UN PASTILLERO CON ESE ID");
								message.fail(500, String.valueOf(json1));
							}
						} else {
							json1.put("error", "ERROR AL BORRAR EL PASTILLERO CON ID: " + id_pastillero + " ."
									+ String.valueOf(res1.cause()));
							message.fail(500, String.valueOf(json1));
						}
					});
				} else {
					jsonComp.put("error",
							"NO SE HAN INTRODUCIDO LOS CAMPOS CORRESPONDIENTES EN EL CUERPO DE LA PETICIÓN.");
					message.fail(500, String.valueOf(jsonComp));
				}
			} else {
				JsonObject checkJson = new JsonObject();
				checkJson.put("error", "FORMATO DE JSON NO VÁLIDO.");
				message.fail(500, String.valueOf(checkJson));
			}
		});
	}

	// EJEMPLO BODY: {"id_pastillero":"35glqp12qp034216o3o3e247hg7fg1r223019203k" , "alias" :"Pastillero de la tía Carmen"}
	public void addPastillero() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("addPastillero");
		consumer.handler(message -> {

			String datosPastillero = message.body();
			if (utils.checkJson(datosPastillero) == true) {
				JsonObject jsonPastillero = new JsonObject(datosPastillero);
				JsonObject jsonComp = new JsonObject();

				boolean comprobacion = jsonPastillero.containsKey("id_pastillero") && jsonPastillero != null;

				if (comprobacion) {
					Query<RowSet<Row>> query = null;
					if (jsonPastillero.containsKey("alias")) {
						query = mySqlClient.query("INSERT INTO Pastillero(id_pastillero, alias) VALUES ('"
								+ jsonPastillero.getString("id_pastillero") + "','" + jsonPastillero.getString("alias")
								+ "');");
					} else {
						query = mySqlClient.query("INSERT INTO Pastillero(id_pastillero) VALUES ('"
								+ jsonPastillero.getString("id_pastillero") + "');");
					}

					query.execute(res -> {
						JsonObject json = new JsonObject();
						if (res.succeeded()) {
							json.put(jsonPastillero.getString("id_pastillero"),
									"AÑADIDO EL PASTILLERO CON ID:  " + jsonPastillero.getString("id_pastillero"));
							message.reply(json);
						} else {
							json.put("error", "ERROR AL AÑADIR EL PASTILLERO CON ID: "
									+ jsonPastillero.getString("id_pastillero"));
							message.fail(500, String.valueOf(json));
						}

					});
				} else {
					jsonComp.put("error",
							"NO SE HAN INTRODUCIDO LOS CAMPOS CORRESPONDIENTES EN EL CUERPO DE LA PETICIÓN.");
					message.fail(500, String.valueOf(jsonComp));
				}
			} else {
				JsonObject checkJson = new JsonObject();
				checkJson.put("error", "FORMATO DE JSON NO VÁLIDO.");
				message.fail(500, String.valueOf(checkJson));
			}
		});
	}

	// EJEMPLO BODY: {"id_pastillero":"w1e2d4f5ffeecnss3fpol247hg7fg1244423435g" , "alias" :"Pastillero de la tía María del Carmen"}
	public void editPastillero() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("editPastillero");
		consumer.handler(message -> {
			String datosPastillero = message.body();

			if (utils.checkJson(datosPastillero) == true) {
				JsonObject jsonPastillero = new JsonObject(datosPastillero);
				JsonObject jsonComp = new JsonObject();

				boolean comprobacion = jsonPastillero.containsKey("id_pastillero")
						&& jsonPastillero.containsKey("alias") && jsonPastillero != null;

				if (comprobacion) {

					String id_pastillero = jsonPastillero.getString("id_pastillero");
					jsonPastillero.remove("id_pastillero");
					Query<RowSet<Row>> query1 = mySqlClient.query(
							"SELECT COUNT(*) as nPastilleros FROM pastillero_dad.Pastillero WHERE id_pastillero = '"
									+ id_pastillero + "';");
					query1.execute(res1 -> {
						JsonObject json1 = new JsonObject();
						Row row = res1.result().iterator().next();
						if (row.getInteger("nPastilleros") > 0) {
							if (res1.succeeded()) {
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

								Query<RowSet<Row>> query2 = mySqlClient.query(stringQuery);
								query2.execute(res2 -> {
									JsonObject json2 = new JsonObject();
									if (res2.succeeded()) {
										json2.put(id_pastillero, "EDITADO EL PASTILLERO CON ID:  " + id_pastillero);
										message.reply(json2);
									} else {
										json2.put("error", "ERROR AL EDITAR EL PASTILLERO CON ID: " + id_pastillero
												+ " " + res2.cause());
										message.fail(500, String.valueOf(json2));
									}

								});
							} else {
								json1.put("error", "ERROR AL EDITAR EL PASTILLERO. ERROR: " + res1.cause());
								message.fail(500, String.valueOf(json1));
							}
						} else {
							json1.put("error", "ERROR AL EDITAR EL PASTILLERO. ERROR: NO EXISTE PASTILLERO CON ESE ID");
							message.fail(500, String.valueOf(json1));
						}
					});
				} else {
					jsonComp.put("error",
							"NO SE HAN INTRODUCIDO LOS CAMPOS CORRESPONDIENTES EN EL CUERPO DE LA PETICIÓN.");
					message.fail(500, String.valueOf(jsonComp));
				}
			} else {
				JsonObject checkJson = new JsonObject();
				checkJson.put("error", "FORMATO DE JSON NO VÁLIDO.");
				message.fail(500, String.valueOf(checkJson));
			}
		});
	}
}
