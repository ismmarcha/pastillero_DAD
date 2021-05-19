package es.us.lsi.dad.Pastilla;

import java.util.Iterator;
import java.util.Map.Entry;

import es.us.lsi.dad.Utils.Utils;
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

	Utils utils = new Utils();

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

	// EJEMPLO BODY: {"id_pastilla": 2}
	public void getPastilla() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("getPastilla");
		consumer.handler(message -> {
			String datosPastilla = message.body();

			if (utils.checkJson(datosPastilla) == true) {
				JsonObject jsonPastilla = new JsonObject(datosPastilla);
				JsonObject jsonComp = new JsonObject();

				boolean comprobacion = jsonPastilla.containsKey("id_pastilla") && jsonPastilla != null;
				if (comprobacion) {

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
							message.reply(resultadoJson);
						} else {
							resultadoJson.put("error", "ERROR AL OBTENER LA PASTILLA CON ID: "
									+ String.valueOf(Id_pastilla) + " ." + String.valueOf(res.cause()));
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

	// EJEMPLO BODY: {"id_pastilla": 2}
	public void deletePastilla() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("deletePastilla");
		consumer.handler(message -> {
			String datosPastilla = message.body();
			if (utils.checkJson(datosPastilla) == true) {
				JsonObject jsonPastilla = new JsonObject(datosPastilla);
				JsonObject jsonComp = new JsonObject();

				boolean comprobacion = jsonPastilla.containsKey("id_pastilla") && jsonPastilla != null;

				if (comprobacion) {
					int Id_pastilla = jsonPastilla.getInteger("id_pastilla");

					Query<RowSet<Row>> query1 = mySqlClient
							.query("SELECT COUNT(*) as nPastilla FROM pastillero_dad.Pastilla WHERE Id_pastilla = "
									+ Id_pastilla + ";");
					query1.execute(res -> {
						JsonObject json = new JsonObject();
						if (res.succeeded()) {
							Row row = res.result().iterator().next();
							if (row.getInteger("nPastilla") <= 0) {
								json.put("error", "PASTILLA NO ENCONTRADA");
								message.fail(500, String.valueOf(json));
							} else {
								Query<RowSet<Row>> query2 = mySqlClient.query(
										"DELETE FROM pastillero_dad.Pastilla WHERE id_pastilla = " + Id_pastilla + ";");
								query2.execute(res2 -> {
									JsonObject resultadoJson = new JsonObject();
									if (res2.succeeded()) {
										resultadoJson.put(String.valueOf(Id_pastilla),
												"BORRADA LA PASTILLA CON ID: " + String.valueOf(Id_pastilla));
										message.reply(resultadoJson);
									} else {
										resultadoJson.put("error", "ERROR AL BORRAR LA PASTILLA CON ID: "
												+ String.valueOf(Id_pastilla) + " . " + String.valueOf(res2.cause()));
										message.fail(500, String.valueOf(resultadoJson));

									}

								});
							}
						} else {
							json.put("error",
									"PASTILLA A ELIMINAR NO ENCONTRADO CON ID: " + String.valueOf(Id_pastilla));
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

	// EJEMPLO BODY : {"nombre": "Sintrom","peso":200}
	public void addPastilla() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("addPastilla");
		consumer.handler(message -> {
			String datosPastilla = message.body();
			if (utils.checkJson(datosPastilla) == true) {
				JsonObject jsonPastilla = new JsonObject(datosPastilla);
				JsonObject jsonComp = new JsonObject();

				boolean comprobacion = jsonPastilla.containsKey("nombre") && jsonPastilla != null;

				if (comprobacion) {

					String stringQuery = "INSERT INTO pastillero_dad.Pastilla(";
					Iterator<Entry<String, Object>> iteratorJsonPastilla = jsonPastilla.iterator();
					while (iteratorJsonPastilla.hasNext()) {
						Entry<String, Object> elemento = iteratorJsonPastilla.next();

						if (elemento.getValue() == null || elemento.getValue() instanceof Number) {
							stringQuery += elemento.getKey();
						} else {
							stringQuery += "" + elemento.getKey() + "";
						}
						if (iteratorJsonPastilla.hasNext()) {
							stringQuery += ", ";
						}
					}
					stringQuery += ") VALUES (";

					Iterator<Entry<String, Object>> iteratorJsonPastillaDatos = jsonPastilla.iterator();
					while (iteratorJsonPastillaDatos.hasNext()) {
						Entry<String, Object> elemento = iteratorJsonPastillaDatos.next();

						if (elemento.getValue() == null || elemento.getValue() instanceof Number) {
							stringQuery += elemento.getValue();
						} else {
							stringQuery += "'" + elemento.getValue() + "'";
						}
						if (iteratorJsonPastillaDatos.hasNext()) {
							stringQuery += ", ";
						}
					}
					stringQuery += ");";
					System.out.println(stringQuery);
					Query<RowSet<Row>> query = mySqlClient.query(stringQuery);
					query.execute(res -> {
						JsonObject resultadoJson = new JsonObject();
						if (res.succeeded()) {
							resultadoJson.put(jsonPastilla.getString("nombre"),
									"AÑADIDA LA PASTILLA:   " + jsonPastilla.getString("nombre") + " .");
							message.reply(resultadoJson);
						} else {
							resultadoJson.put("error", "ERROR AL AÑADIR LA PASTILLA CON ID: "
									+ jsonPastilla.getString("nombre") + " ." + String.valueOf(res.cause()));
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

	// EJEMPLO BODY : {"id_pastilla":1 ,"peso":600 }
	public void editPastilla() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("editPastilla");
		consumer.handler(message -> {
			String datosPastilla = message.body();

			if (utils.checkJson(datosPastilla) == true) {
				JsonObject jsonPastilla = new JsonObject(datosPastilla);
				JsonObject jsonComp = new JsonObject();

				boolean comprobacion = jsonPastilla.containsKey("id_pastilla") && jsonPastilla != null;

				if (comprobacion) {

					String id_pastilla = jsonPastilla.getString("id_pastilla");
					Query<RowSet<Row>> query1 = mySqlClient.query(
							"SELECT COUNT(*) as nPastilla FROM Pastilla WHERE id_pastilla = " + id_pastilla + ";");

					query1.execute(res -> {
						JsonObject json = new JsonObject();
						if (res.succeeded()) {
							Row row = (Row) res.result().iterator().next();
							if (row.getInteger("nPastilla") <= 0) {
								json.put("error", "PASTILLA NO ENCONTRADA");
								message.fail(500, String.valueOf(json));
							} else {
								String stringQuery = "UPDATE pastillero_dad.Pastilla SET ";
								jsonPastilla.remove("id_pastilla");
								Iterator<Entry<String, Object>> iteratorJsonPastilla = jsonPastilla.iterator();
								if (iteratorJsonPastilla.hasNext()) {

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
											resultadoJson.put(id_pastilla,
													"EDITADA LA PASTILLA CON ID:   " + id_pastilla + " .");
											message.reply(resultadoJson);
										} else {
											resultadoJson.put("error", "ERROR AL EDITAR LA PASTILLA CON ID: "
													+ id_pastilla + " ." + String.valueOf(res2.cause()));
											message.fail(500, String.valueOf(resultadoJson));
										}
									});
								} else {
									json.put("error", "NO HAY SUFICIENTES CAMPOS PARA EDITAR" + " .");
									message.fail(500, String.valueOf(json));
								}
							}
						} else {
							json.put("error", "ERROR AL EDITAR LA PASTILLA CON ID: " + id_pastilla + " ."
									+ String.valueOf(res.cause()));
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

	// EJEMPLO BODY : {"id_dosis": 1 }
	public void getPastillaPorDosis() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("getPastillaPorDosis");
		consumer.handler(message -> {
			String datosPastilla = message.body();
			if (utils.checkJson(datosPastilla) == true) {
				JsonObject jsonPastilla = new JsonObject(datosPastilla);
				JsonObject jsonComp = new JsonObject();

				boolean comprobacion = jsonPastilla.containsKey("id_dosis") && jsonPastilla != null;
				if (comprobacion) {
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
							message.reply(resultadoJson);
						} else {
							resultadoJson.put("error", "ERROR AL OBTENER LAS PASTILLAS DE LA DOSIS CON ID: " + Id_dosis
									+ " ." + String.valueOf(res.cause()));
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

	// EJEMPLO BODY : {"id_dosis": 3, "id_pastilla":1,"cantidad":2 }
	public void addPastillaPorDosis() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("addPastillaPorDosis");
		consumer.handler(message -> {
			String datosPastillaDosis = message.body();
			if (utils.checkJson(datosPastillaDosis) == true) {
				JsonObject jsonPastilla = new JsonObject(datosPastillaDosis);
				JsonObject jsonComp = new JsonObject();

				boolean comprobacion = jsonPastilla.containsKey("id_pastilla") && jsonPastilla.containsKey("id_dosis")
						&& jsonPastilla.containsKey("cantidad") && jsonPastilla != null;

				if (comprobacion) {

					JsonObject jsonPastillaDosis = new JsonObject(datosPastillaDosis);

					int id_pastilla = jsonPastillaDosis.getInteger("id_pastilla");
					int id_dosis = jsonPastillaDosis.getInteger("id_dosis");
					double cantidad = jsonPastillaDosis.getDouble("cantidad");

					Query<RowSet<Row>> query = mySqlClient
							.query("INSERT INTO Pastilla_Dosis (id_pastilla,id_dosis,cantidad)" + " VALUES('"
									+ id_pastilla + "','" + id_dosis + "'," + cantidad + ");");

					query.execute(res -> {
						JsonObject resultadoJson = new JsonObject();
						if (res.succeeded()) {
							resultadoJson.put(String.valueOf(id_pastilla),
									"AÑADIDA LA PASTILLA CON ID: " + String.valueOf(id_pastilla)
											+ " A LA DOSIS CON ID: " + String.valueOf(id_dosis) + " .");
							message.reply(resultadoJson);
						} else {
							resultadoJson.put("error",
									"ERROR AL AÑADIR LA PASTILLA CON ID: " + String.valueOf(id_pastilla)
											+ "A LA DOSIS CON ID: " + String.valueOf(id_dosis) + " ."
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

	// EJEMPLO BODY : {"id_dosis": 1, "id_pastilla":2 }
	public void deletePastillaPorDosis() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("deletePastillaPorDosis");
		consumer.handler(message -> {
			String datosPastillaPorDosis = message.body();
			if (utils.checkJson(datosPastillaPorDosis) == true) {
				JsonObject jsonPastillaPorDosis = new JsonObject(datosPastillaPorDosis);
				JsonObject jsonComp = new JsonObject();

				boolean comprobacion = jsonPastillaPorDosis.containsKey("id_pastilla")
						&& jsonPastillaPorDosis.containsKey("id_dosis") && jsonPastillaPorDosis != null;

				if (comprobacion) {

					int Id_pastilla = jsonPastillaPorDosis.getInteger("id_pastilla");
					int Id_dosis = jsonPastillaPorDosis.getInteger("id_dosis");
					Query<RowSet<Row>> query1 = mySqlClient.query(
							"SELECT COUNT(*) as nPastillaDosis FROM pastillero_dad.Pastilla_Dosis WHERE id_pastilla = "
									+ Id_pastilla + " AND Id_Dosis = " + Id_dosis + " ;");
					query1.execute(res -> {
						JsonObject json = new JsonObject();
						if (res.succeeded()) {
							Row row = res.result().iterator().next();
							if (row.getInteger("nPastillaDosis") <= 0) {
								json.put("error", "PASTILLA NO ENCONTRADA");
								message.fail(500, String.valueOf(json));
							} else {
								Query<RowSet<Row>> query = mySqlClient
										.query("DELETE FROM pastillero_dad.Pastilla_Dosis WHERE Id_pastilla = "
												+ Id_pastilla + " AND Id_Dosis =" + Id_dosis + " ;");
								query.execute(res2 -> {
									JsonObject resultadoJson = new JsonObject();
									if (res2.succeeded()) {
										resultadoJson.put(String.valueOf(Id_pastilla),
												"BORRADA LA PASTILLA CON ID: " + String.valueOf(Id_pastilla)
														+ " A LA DOSIS CON ID: " + String.valueOf(Id_dosis) + " .");
										message.reply(resultadoJson);
									} else {
										resultadoJson.put("error",
												"ERROR AL BORRAR LA PASTILLA CON ID: " + String.valueOf(Id_pastilla)
														+ "A LA DOSIS CON ID: " + String.valueOf(Id_dosis) + " ."
														+ String.valueOf(res2.cause()));
										message.fail(500, String.valueOf(resultadoJson));
									}
								});
							}
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

	// EJEMPLO BODY : {"id_dosis": 1, "id_pastilla":1, "cantidad":100}
	public void editPastillaPorDosis() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("editPastillaPorDosis");
		consumer.handler(message -> {

			String datosPastillaporDosis = message.body();
			if (utils.checkJson(datosPastillaporDosis) == true) {
				JsonObject jsonPastillaPorDosis = new JsonObject(datosPastillaporDosis);
				JsonObject jsonComp = new JsonObject();

				boolean comprobacion = jsonPastillaPorDosis.containsKey("id_pastilla")
						&& jsonPastillaPorDosis.containsKey("id_dosis") && jsonPastillaPorDosis.containsKey("cantidad")
						&& jsonPastillaPorDosis != null;

				if (comprobacion) {

					String id_pastilla = jsonPastillaPorDosis.getString("id_pastilla");
					String id_dosis = jsonPastillaPorDosis.getString("id_dosis");
					jsonPastillaPorDosis.remove(id_pastilla);
					jsonPastillaPorDosis.remove(id_dosis);
					String stringQuery = "UPDATE pastillero_dad.Pastilla_Dosis SET ";

					Iterator<Entry<String, Object>> iteratorJsonPastilla = jsonPastillaPorDosis.iterator();
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
					stringQuery += " WHERE id_pastilla = " + id_pastilla + " AND id_dosis = " + id_dosis + " ;";

					Query<RowSet<Row>> query = mySqlClient.query(stringQuery);
					query.execute(res -> {
						JsonObject resultadoJson = new JsonObject();
						if (res.succeeded()) {
							resultadoJson.put(String.valueOf(id_pastilla),
									"EDITADA LA PASTILLA CON ID: " + String.valueOf(id_pastilla)
											+ " EN LA DOSIS CON ID: " + String.valueOf(id_dosis) + " .");
							message.reply(resultadoJson);

						} else {
							resultadoJson.put("error",
									"ERROR AL EDITAR LA PASTILLA CON ID: " + String.valueOf(id_pastilla)
											+ "A LA DOSIS CON ID: " + String.valueOf(id_dosis) + " ."
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

	//EJEMPLO BODY: {"nif" : "78130288F"}
	public void getPastillasPorUsuario() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("getPastillasPorUsuario");
		consumer.handler(message -> {

			String datosUsuario = message.body();
			if (utils.checkJson(datosUsuario) == true) {
				JsonObject jsonPastilla = new JsonObject(datosUsuario);
				JsonObject jsonComp = new JsonObject();

				boolean comprobacion = jsonPastilla.containsKey("nif") && jsonPastilla != null;
				if (comprobacion) {

					String nif = jsonPastilla.getString("nif");

					Query<RowSet<Row>> query1 = mySqlClient
							.query("SELECT COUNT(*) as nPastillas FROM pastillero_dad.Pastilla  "
									+ "JOIN pastillero_dad.Pastilla_Dosis ON Pastilla.id_pastilla = pastilla_dosis.id_pastilla"
									+ " JOIN pastillero_dad.dosis ON pastilla_dosis.id_dosis = dosis.id_dosis WHERE  dosis.nif = '"
									+ nif + "';");

					query1.execute(res -> {
						JsonObject json = new JsonObject();
						if (res.succeeded()) {

							Row row = res.result().iterator().next();
							if (row.getInteger("nPastillas") <= 0) {
								json.put("error", "USUARIO NO ENCONTRADO");
								message.fail(500, String.valueOf(json));
							} else {

								Query<RowSet<Row>> query = mySqlClient.query(
										"SELECT Pastilla.id_pastilla , Pastilla.nombre , Pastilla.descripcion , Pastilla.peso FROM pastillero_dad.Pastilla  "
												+ "JOIN pastillero_dad.Pastilla_Dosis ON Pastilla.id_pastilla = pastilla_dosis.id_pastilla"
												+ " JOIN pastillero_dad.dosis ON pastilla_dosis.id_dosis = dosis.id_dosis WHERE  dosis.nif = '"
												+ nif + "';");

								query.execute(res2 -> {
									JsonObject resultadoJson = new JsonObject();
									if (res2.succeeded()) {
										res2.result().forEach(v -> {
											PastillaImpl pastilla = new PastillaImpl(v);
											resultadoJson.put(String.valueOf(pastilla.getId_pastilla()),
													pastilla.getJson());
											message.reply(resultadoJson);
										});
									} else {
										resultadoJson.put("error",
												"ERROR AL OBTENER LAS PASTILLAS DEL USUARIO CON NIF: " + nif + " ."
														+ String.valueOf(res2.cause()));
										message.fail(500, String.valueOf(resultadoJson));
									}
								});
							}
						} else {
							json.put("error", "ERROR AL OBTENER LAS PASTILLAS DEL USUARIO CON NIF: " + nif + " . "
									+ String.valueOf(res.cause()));
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
}
