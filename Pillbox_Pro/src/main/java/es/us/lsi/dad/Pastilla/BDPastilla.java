package es.us.lsi.dad.Pastilla;

import java.util.Iterator;
import java.util.Map.Entry;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Query;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

public class BDPastilla {
	Vertx vertx;
	MySQLPool mySqlClient;

	public BDPastilla(Vertx vertx, MySQLPool mySqlClient) {
		this.vertx = vertx;
		this.mySqlClient = mySqlClient;
	}

	public void iniciarConsumersBDPastilla() {
		getAllPastilla();
		getPastilla();
		deletePastilla();
		addPastilla();
		editPastilla();

		getPastillaPorDosis();
		addPastillaPorDosis();
		deletePastillaPorDosis();
		editPastillaPorDosis();

		getPastillasPorUsuario();
	}

	public void getAllPastilla() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("getAllPastilla");
		consumer.handler(message -> {

			Query<RowSet<Row>> query = mySqlClient.query("SELECT * FROM pastillero_dad.Pastilla;");
			query.execute(res -> {
				JsonObject resultadoJson = new JsonObject();
				if (res.succeeded()) {
					res.result().forEach(v -> {
						PastillaImpl pastilla = new PastillaImpl(v);
						resultadoJson.put(String.valueOf(pastilla.getId_pastilla()), pastilla.getJson());
					});
				} else {
					resultadoJson.put("error",
							"ERROR AL OBTENER TODAS LAS PASTILLAS" + " ." + String.valueOf(res.cause()));
				}
				message.reply(resultadoJson);
			});
		});
	}

	public void getPastilla() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("getPastilla");
		consumer.handler(message -> {
			String datosPastilla = message.body();
			JsonObject jsonPastilla = new JsonObject(datosPastilla);
			int Id_pastilla = jsonPastilla.getInteger("id_pastilla");
			Query<RowSet<Row>> query = mySqlClient
					.query("SELECT * FROM pastillero_dad.Pastilla WHERE id_pastilla = " + Id_pastilla + ";");
			query.execute(res -> {
				JsonObject resultadoJson = new JsonObject();
				if (res.succeeded()) {
					res.result().forEach(v -> {
						PastillaImpl pastilla = new PastillaImpl(v);
						resultadoJson.put(String.valueOf(pastilla.getId_pastilla()), pastilla.getJson());
					});
				} else {
					resultadoJson.put("error", "ERROR AL OBTENER LA PASTILLA CON ID: " + String.valueOf(Id_pastilla)
							+ " ." + String.valueOf(res.cause()));
				}
				message.reply(resultadoJson);
			});
		});
	}

	public void deletePastilla() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("deletePastilla");
		consumer.handler(message -> {
			String datosPastilla = message.body();
			JsonObject jsonPastilla = new JsonObject(datosPastilla);
			int Id_pastilla = jsonPastilla.getInteger("id_pastilla");
			
			Query<RowSet<Row>> query1 = mySqlClient
					.query("SELECT COUNT(*) as nPastilla FROM pastillero_dad.Pastilla WHERE Id_pastilla = " + Id_pastilla + ";");
			query1.execute(res -> {
				JsonObject json = new JsonObject();
				if (res.succeeded()) {
<<<<<<< Updated upstream
					Row row = res.result().iterator().next();
					if (row.getInteger("nPastilla") <= 0) {
						json.put("error", "Pastilla no encontrada");
						message.reply(json);
					} else {
						Query<RowSet<Row>> query2 = mySqlClient
								.query("DELETE FROM pastillero_dad.Pastilla WHERE id_pastilla = " + Id_pastilla + ";");
						query2.execute(res2 -> {
							JsonObject resultadoJson = new JsonObject();
							if (res2.succeeded()) {
									resultadoJson.put(String.valueOf(Id_pastilla), "BORRADA LA PASTILLA CON ID: " + String.valueOf(Id_pastilla) );
							} else {
								resultadoJson.put("error", "ERROR AL BORRAR LA PASTILLA CON ID: "+ String.valueOf(Id_pastilla) + " . "+ String.valueOf(res2.cause()));
=======
					resultadoJson.put(String.valueOf(Id_pastilla),
							"BORRADA LA PASTILLA CON ID: " + String.valueOf(Id_pastilla));
				} else {
					resultadoJson.put("error", "ERROR AL BORRAR LA PASTILLA CON ID: " + String.valueOf(Id_pastilla)
							+ " ." + String.valueOf(res.cause()));
>>>>>>> Stashed changes

							}
							message.reply(resultadoJson);
						});
					}
				}else {
					json.put("error", "PASTILLA A ELIMINAR NO ENCONTRADO CON ID: " + String.valueOf(Id_pastilla) );
					message.reply(json);
				}
			});
		});
	}

	public void addPastilla() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("addPastilla");
		consumer.handler(message -> {
			PastillaImpl pastilla = new PastillaImpl(message.body());

			Query<RowSet<Row>> query = mySqlClient.query("INSERT INTO Pastilla (nombre,descripcion,peso)" + " VALUES('"
					+ pastilla.getNombre() + "','" + pastilla.getDescripcion() + "'," + pastilla.getPeso() + ");");
			query.execute(res -> {
				JsonObject resultadoJson = new JsonObject();
				if (res.succeeded()) {
					resultadoJson.put(pastilla.getNombre(), "A�ADIDA LA PASTILLA:   " + pastilla.getNombre()
							+ ", CON PESO: " + pastilla.getPeso() + " .");
				} else {
					resultadoJson.put("error", "ERROR AL A�ADIR LA PASTILLA CON ID: "
							+ String.valueOf(pastilla.getId_pastilla()) + " ." + String.valueOf(res.cause()));
				}
				message.reply(resultadoJson);
			});
		});
	}

	public void editPastilla() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("editPastilla");
		consumer.handler(message -> {

			String datosPastilla = message.body();
			JsonObject jsonPastilla = new JsonObject(datosPastilla);

			String id_pastilla = jsonPastilla.getString("id_pastilla");
			Query<RowSet<Row>> query1 = mySqlClient
					.query("SELECT COUNT(*) as nPastilla FROM Pastilla WHERE id_pastilla = " + id_pastilla + ";");
			
			query1.execute(res -> {
				JsonObject json = new JsonObject();
				if (res.succeeded()) {
<<<<<<< Updated upstream
					Row row = (Row) res.result().iterator().next();
					if (row.getInteger("nPastilla") <= 0) {
						json.put("error", "Pastilla no encontrada");
						message.reply(json);
					} else {
						String stringQuery = "UPDATE pastillero_dad.Pastilla SET ";
						jsonPastilla.remove("id_pastilla");
						Iterator<Entry<String, Object>> iteratorJsonPastilla = jsonPastilla.iterator();
						while (iteratorJsonPastilla.hasNext()) {
							Entry<String, Object> elemento = iteratorJsonPastilla.next();
							stringQuery += elemento.getKey() + " = ";
							if (elemento.getValue() == null || elemento.getValue() instanceof Number) {
								stringQuery += elemento.getValue();
							} else {
								stringQuery += "'" + elemento.getValue() + "'";
							}
							if (iteratorJsonPastilla.hasNext()) {
								stringQuery += ", ";
							}
						}
						stringQuery += " WHERE id_pastilla = " + id_pastilla + ";";

						Query<RowSet<Row>> query = mySqlClient.query(stringQuery);
						System.out.println(stringQuery);
						query.execute(res2 -> {
							JsonObject resultadoJson = new JsonObject();
							if (res2.succeeded()) {
									resultadoJson.put(id_pastilla, "EDITADA LA PASTILLA:   " + jsonPastilla.getString("nombre")   + " ." );
							} else {
								resultadoJson.put("error", "ERROR AL EDITAR LA PASTILLA CON ID: "+ id_pastilla + " ."+ String.valueOf(res2.cause()));
							}
							message.reply(resultadoJson);
						});
					}
				}else {
					json.put("error",
							"ERROR AL EDITAR LA PASTILLA CON ID: " + id_pastilla + " ." + String.valueOf(res.cause()));
					message.reply(json);
=======
					resultadoJson.put(id_pastilla, "EDITADA LA PASTILLA:   " + jsonPastilla.getString("nombre") + " .");
				} else {
					resultadoJson.put("error",
							"ERROR AL EDITAR LA PASTILLA CON ID: " + id_pastilla + " ." + String.valueOf(res.cause()));
>>>>>>> Stashed changes
				}
			});
		});
	}

	public void getPastillaPorDosis() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("getPastillaPorDosis");
		consumer.handler(message -> {
			String datosPastilla = message.body();
			JsonObject jsonPastilla = new JsonObject(datosPastilla);
			int Id_dosis = jsonPastilla.getInteger("id_dosis");
			Query<RowSet<Row>> query = mySqlClient.query(
					"SELECT pastillero_dad.Pastilla.id_pastilla ,nombre ,descripcion ,peso FROM pastillero_dad.Pastilla LEFT JOIN pastillero_dad.Pastilla_Dosis "
							+ "ON Pastilla.id_pastilla = pastilla_dosis.id_pastilla WHERE pastilla_dosis.id_pastilla ="
							+ Id_dosis + ";");
			query.execute(res -> {
				JsonObject resultadoJson = new JsonObject();
				if (res.succeeded()) {
					res.result().forEach(v -> {
						PastillaImpl pastilla = new PastillaImpl(v);
						resultadoJson.put(String.valueOf(pastilla.getId_pastilla()), pastilla.getJson());
					});
				} else {
					resultadoJson.put("error", "ERROR AL OBTENER LAS PASTILLAS DE LA DOSIS CON ID: " + Id_dosis + " ."
							+ String.valueOf(res.cause()));
				}
				message.reply(resultadoJson);
			});
		});
	}

	public void addPastillaPorDosis() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("addPastillaPorDosis");
		consumer.handler(message -> {
			String datosPastillaDosis = message.body();
			JsonObject jsonPastillaDosis = new JsonObject(datosPastillaDosis);

			int id_pastilla = jsonPastillaDosis.getInteger("id_pastilla");
			int id_dosis = jsonPastillaDosis.getInteger("id_dosis");
			double cantidad = jsonPastillaDosis.getDouble("cantidad");

			Query<RowSet<Row>> query = mySqlClient.query("INSERT INTO Pastilla_Dosis (id_pastilla,id_dosis,cantidad)"
					+ " VALUES('" + id_pastilla + "','" + id_dosis + "'," + cantidad + ");");

			query.execute(res -> {
				JsonObject resultadoJson = new JsonObject();
				if (res.succeeded()) {
					resultadoJson.put(String.valueOf(id_pastilla), "A�ADIDA LA PASTILLA CON ID: "
							+ String.valueOf(id_pastilla) + " A LA DOSIS CON ID: " + String.valueOf(id_dosis) + " .");
				} else {
					resultadoJson.put("error", "ERROR AL A�ADIR LA PASTILLA CON ID: " + String.valueOf(id_pastilla)
							+ "A LA DOSIS CON ID: " + String.valueOf(id_dosis) + " ." + String.valueOf(res.cause()));
				}
				message.reply(resultadoJson);
			});
		});
	}

	public void deletePastillaPorDosis() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("deletePastillaPorDosis");
		consumer.handler(message -> {
			String datosPastillaPorDosis = message.body();
			JsonObject jsonPastillaPorDosis = new JsonObject(datosPastillaPorDosis);
			int Id_pastilla = jsonPastillaPorDosis.getInteger("id_pastilla");
			int Id_dosis = jsonPastillaPorDosis.getInteger("id_dosis");
<<<<<<< Updated upstream
			
			Query<RowSet<Row>> query1 = mySqlClient
					.query("SELECT COUNT(*) as nPastillaDosis FROM pastillero_dad.Pastilla_Dosis WHERE id_pastilla = " + Id_pastilla + " AND Id_Dosis = " + Id_dosis + " ;");
			query1.execute(res -> {
				JsonObject json = new JsonObject();
				if (res.succeeded()) {
					Row row = res.result().iterator().next();
					if (row.getInteger("nPastillaDosis") <= 0) {
						json.put("error", "Pastilla no encontrada");
						message.reply(json);
					} else {
						Query<RowSet<Row>> query = mySqlClient
								.query("DELETE FROM pastillero_dad.Pastilla_Dosis WHERE Id_pastilla = " + Id_pastilla + " AND Id_Dosis =" + Id_dosis + " ;");
						query.execute(res2 -> {
							JsonObject resultadoJson = new JsonObject();
							if (res2.succeeded()) {
									resultadoJson.put(String.valueOf(Id_pastilla), "BORRADA LA PASTILLA CON ID: " + String.valueOf(Id_pastilla)+ " A LA DOSIS CON ID: "+  String.valueOf(Id_dosis)  + " ." );
							} else {
								resultadoJson.put("error", "ERROR AL BORRAR LA PASTILLA CON ID: "+ String.valueOf(Id_pastilla)+ "A LA DOSIS CON ID: "+ String.valueOf(Id_dosis) + " ."+ String.valueOf(res2.cause()));
							}
							message.reply(resultadoJson);
						});
					}
=======

			Query<RowSet<Row>> query = mySqlClient
					.query("DELETE FROM pastillero_dad.Pastilla_Dosis WHERE Id_pastilla = " + Id_pastilla
							+ " AND Id_Dosis =" + Id_dosis + " ;");
			query.execute(res -> {
				JsonObject resultadoJson = new JsonObject();
				if (res.succeeded()) {
					resultadoJson.put(String.valueOf(Id_pastilla), "BORRADA LA PASTILLA CON ID: "
							+ String.valueOf(Id_pastilla) + " A LA DOSIS CON ID: " + String.valueOf(Id_dosis) + " .");
				} else {
					resultadoJson.put("error", "ERROR AL BORRAR LA PASTILLA CON ID: " + String.valueOf(Id_pastilla)
							+ "A LA DOSIS CON ID: " + String.valueOf(Id_dosis) + " ." + String.valueOf(res.cause()));
>>>>>>> Stashed changes
				}
			});
		});
	}

	public void editPastillaPorDosis() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("editPastillaPorDosis");
		consumer.handler(message -> {

			String datosPastillaporDosis = message.body();
			JsonObject jsonPastillaPorDosis = new JsonObject(datosPastillaporDosis);

			String id_pastilla = jsonPastillaPorDosis.getString("id_pastilla");
			String id_dosis = jsonPastillaPorDosis.getString("id_dosis");
			jsonPastillaPorDosis.remove("id_pastilla");
			jsonPastillaPorDosis.remove("id_dosis");
			String stringQuery1 = "SELECT COUNT(*) as nPastillaDosis FROM Pastilla_Dosis WHERE id_pastilla = "
					+ id_pastilla + " AND id_dosis = " + id_dosis;
			Query<RowSet<Row>> query1 = mySqlClient.query(stringQuery1);
			query1.execute(res1 -> {
				JsonObject json1 = new JsonObject();
				if (res1.succeeded()) {
					Row row = res1.result().iterator().next();
					if (row.getInteger("nPastillaDosis") <= 0) {
						json1.put("ERROR", "ERROR AL EDITAR LA DOSIS " + id_dosis + " DE LA PASTILLA " + id_pastilla + ". "
								+ "ERROR: PASTILLA EN ESA DOSIS NO ENCONTRADA");
						message.reply(json1);
					} else {
						String stringQuery2 = "UPDATE pastillero_dad.Pastilla_Dosis SET ";

						Iterator<Entry<String, Object>> iteratorJsonPastilla = jsonPastillaPorDosis.iterator();
						while (iteratorJsonPastilla.hasNext()) {
							Entry<String, Object> elemento = iteratorJsonPastilla.next();
							stringQuery2 += elemento.getKey() + " = ";
							if (elemento.getValue() == null || elemento.getValue() instanceof Number) {
								stringQuery2 += elemento.getValue();
							} else {
								stringQuery2 += "'" + elemento.getValue() + "'";
							}
							if (iteratorJsonPastilla.hasNext()) {
								stringQuery2 += ", ";
							}
						}
						stringQuery2 += " WHERE id_pastilla = " + id_pastilla + " AND id_dosis = " + id_dosis + " ;";

						Query<RowSet<Row>> query2 = mySqlClient.query(stringQuery2);
						query2.execute(res2 -> {
							JsonObject json2 = new JsonObject();
							if (res2.succeeded()) {
								json2.put(String.valueOf(id_pastilla),
										"EDITADA LA PASTILLA CON ID: " + String.valueOf(id_pastilla)
												+ " EN LA DOSIS CON ID: " + String.valueOf(id_dosis) + " .");

							} else {
								json2.put("error",
										"ERROR AL EDITAR LA PASTILLA CON ID: " + String.valueOf(id_pastilla)
												+ "A LA DOSIS CON ID: " + String.valueOf(id_dosis) + " ."
												+ String.valueOf(res2.cause()));
							}
							message.reply(json2);
						});
					}
				} else {
					json1.put("error", "ERROR AL EDITAR LA PASTILLA CON ID: " + String.valueOf(id_pastilla)
							+ "A LA DOSIS CON ID: " + String.valueOf(id_dosis) + " ." + String.valueOf(res1.cause()));
				}
			});
		});
	}

	public void getPastillasPorUsuario() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("getPastillasPorUsuario");
		consumer.handler(message -> {
			String datosUsuario = message.body();
			JsonObject jsonPastilla = new JsonObject(datosUsuario);
			String nif = jsonPastilla.getString("nif");

			Query<RowSet<Row>> query = mySqlClient.query(
					"SELECT Pastilla.id_pastilla , Pastilla.nombre , Pastilla.descripcion , Pastilla.peso FROM pastillero_dad.Pastilla  "
							+ "JOIN pastillero_dad.Pastilla_Dosis ON Pastilla.id_pastilla = pastilla_dosis.id_pastilla"
							+ " JOIN pastillero_dad.dosis ON pastilla_dosis.id_dosis = dosis.id_dosis WHERE  dosis.nif = '"
							+ nif + "';");

			query.execute(res -> {
				JsonObject resultadoJson = new JsonObject();
				if (res.succeeded()) {
					res.result().forEach(v -> {
						PastillaImpl pastilla = new PastillaImpl(v);
						resultadoJson.put(String.valueOf(pastilla.getId_pastilla()), pastilla.getJson());
					});
				} else {
					resultadoJson.put("error", "ERROR AL OBTENER LAS PASTILLAS DEL USUARIO CON NIF: " + nif + " ."
							+ String.valueOf(res.cause()));
				}
				message.reply(resultadoJson);
			});
		});
	}
}
