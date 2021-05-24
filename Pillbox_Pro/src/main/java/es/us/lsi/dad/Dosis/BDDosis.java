package es.us.lsi.dad.Dosis;

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

public class BDDosis {
	int h = 0;
	Vertx vertx;
	MySQLPool mySqlClient;

	Utils utils = new Utils();

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
		getDosisPorUsuarioGroupByDia();
		getSiguienteDosisByPastillero();
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
					message.reply(resultadoJson);
				} else {
					resultadoJson.put("error", "ERROR AL OBTENER TODAS LAS DOSIS" + " ." + String.valueOf(res.cause()));
					message.fail(500, String.valueOf(resultadoJson));
				}
			});
		});
	}

	// EJEMPLO BODY: { "nif": "78130288F", "hora_inicio": "10:00" , "dia_semana": 1 }
	public void getDosis() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("getDosis");
		consumer.handler(message -> {
			String datosDosis = message.body();
			if (utils.checkJson(datosDosis) == true) {
				JsonObject jsonDosis = new JsonObject(datosDosis);
				JsonObject jsonComp = new JsonObject();

				boolean comprobacion = jsonDosis.containsKey("nif") && jsonDosis.containsKey("hora_inicio")
						&& jsonDosis.containsKey("dia_semana") && jsonDosis != null;
				if (comprobacion) {

					String nif = jsonDosis.getString("nif");
					String hora_inicio = jsonDosis.getString("hora_inicio");
					String dia_semana = jsonDosis.getString("dia_semana");

					Query<RowSet<Row>> query = mySqlClient.query("SELECT * FROM pastillero_dad.Dosis WHERE nif = '"
							+ nif + "' AND hora_inicio = '" + hora_inicio + "' AND dia_semana = " + dia_semana + ";");

					query.execute(res -> {
						JsonObject resultadoJson = new JsonObject();

						if (res.succeeded()) {
							res.result().forEach(v -> {
								DosisImpl dosis = new DosisImpl(v);
								resultadoJson.put(String.valueOf(dosis.getId_dosis()), dosis.getJson());

							});
							message.reply(resultadoJson);
						} else {
							resultadoJson.put("error",
									"ERROR AL OBTENER LA DOSIS DEL USUARIO CON DNI: " + nif + ",CON HORA DE INICIO:"
											+ hora_inicio + " Y DÍA DE LA SEMANA:" + dia_semana + " ."
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

	// EJEMPLO BODY: { "nif":"78130288F" }
	public void getDosisPorUsuario() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("getDosisPorUsuario");
		consumer.handler(message -> {
			String datosUsuario = message.body();
			if (utils.checkJson(datosUsuario) == true) {
				JsonObject jsonUsuario = new JsonObject(datosUsuario);
				JsonObject jsonComp = new JsonObject();

				boolean comprobacion = jsonUsuario.containsKey("nif") && jsonUsuario != null;
				if (comprobacion) {
					String nif = jsonUsuario.getString("nif");

					Query<RowSet<Row>> query1 = mySqlClient
							.query("SELECT COUNT(*) as nUsuarios FROM pastillero_dad.Usuario WHERE nif = '" + nif + "';");
					query1.execute(res -> {
						JsonObject json = new JsonObject();
						if (res.succeeded()) {

							Row row = res.result().iterator().next();
							if (row.getInteger("nUsuarios") <= 0) {
								json.put("error", "USUARIO NO ENCONTRADO");
								message.fail(500, String.valueOf(json));
							} else {

								Query<RowSet<Row>> query = mySqlClient
										.query("SELECT * FROM pastillero_dad.Dosis WHERE nif = '" + nif + "';");
								query.execute(res2 -> {
									JsonObject resultadoJson = new JsonObject();
									if (res2.succeeded()) {
										
										res2.result().forEach(v -> {
											DosisImpl dosis = new DosisImpl(v);
											resultadoJson.put(String.valueOf(dosis.getId_dosis()), dosis.getJson());
										});
										message.reply(resultadoJson);
									} else {
										resultadoJson.put("error", "ERROR AL OBTENER LAS DOSIS DEL USUARIO CON DNI: "
												+ nif + " ." + String.valueOf(res2.cause()));
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

//EJEMPLO BODY : {"nif":"78130288F","dia_semana": 4}
	public void getDosisPorUsuarioYDia() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("getDosisPorUsuarioYDia");
		consumer.handler(message -> {

			String datosUsuario = message.body();
			System.out.println(datosUsuario);

			if (utils.checkJson(datosUsuario) == true) {
				JsonObject jsonUsuario = new JsonObject(datosUsuario);
				JsonObject jsonComp = new JsonObject();

				boolean comprobacion = jsonUsuario.containsKey("nif") && jsonUsuario.containsKey("dia_semana")
						&& jsonUsuario != null;
				if (comprobacion) {

					String nif = jsonUsuario.getString("nif");
					int dia_semana = jsonUsuario.getInteger("dia_semana");

					Query<RowSet<Row>> query1 = mySqlClient
							.query("SELECT COUNT(*) as nUsuarios FROM pastillero_dad.Usuario WHERE nif = '" + nif + "';");

					query1.execute(res -> {
						JsonObject json = new JsonObject();
						if (res.succeeded()) {

							Row row = res.result().iterator().next();
							if (row.getInteger("nUsuarios") <= 0) {
								json.put("error", "USUARIO NO ENCONTRADO");
								message.fail(500, String.valueOf(json));
							} else {

								Query<RowSet<Row>> query = mySqlClient
										.query("SELECT * FROM pastillero_dad.Dosis WHERE nif = '" + nif + "'"
												+ " AND dia_semana = '" + dia_semana + "';");
								query.execute(res2 -> {
									JsonObject resultadoJson = new JsonObject();
									if (res2.succeeded()) {
										res2.result().forEach(v -> {
											DosisImpl dosis = new DosisImpl(v);
											resultadoJson.put(String.valueOf(dosis.getId_dosis()), dosis.getJson());
										});
										message.reply(resultadoJson);
									} else {
										resultadoJson.put("error",
												"ERROR AL OBTENER LA DOSIS DEL USUARIO CON DNI: " + nif + " DEL DÍA : "
														+ dia_semana + " ." + String.valueOf(res2.cause()));
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

	// EJEMPLO BODY : {"nif":"78130288F"}
	public void getDosisPorUsuarioGroupByDia() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("getDosisPorUsuarioGroupByDia");
		consumer.handler(message -> {
			String datosUsuario = message.body();
			if (utils.checkJson(datosUsuario) == true) {
				JsonObject jsonUsuario = new JsonObject(datosUsuario);
				JsonObject jsonComp = new JsonObject();

				boolean comprobacion = jsonUsuario.containsKey("nif") && jsonUsuario != null;
				if (comprobacion) {
					String nif = jsonUsuario.getString("nif");

					Query<RowSet<Row>> query1 = mySqlClient
							.query("SELECT COUNT(*) as nUsuarios FROM pastillero_dad.Usuario WHERE nif = '" + nif + "';");

					query1.execute(res -> {
						JsonObject json = new JsonObject();
						if (res.succeeded()) {

							Row row = res.result().iterator().next();
							if (row.getInteger("nUsuarios") <= 0) {
								json.put("error", "USUARIO NO ENCONTRADO");
								message.fail(500, String.valueOf(json));
							} else {

								Query<RowSet<Row>> query = mySqlClient
										.query("SELECT * FROM pastillero_dad.Dosis WHERE nif = '" + nif + "';");
								query.execute(res2 -> {
									JsonObject resultadoJson = new JsonObject();
									if (res2.succeeded()) {
										res2.result().forEach(v -> {
											DosisImpl dosis = new DosisImpl(v);
											resultadoJson.put(String.valueOf(dosis.getId_dosis()), dosis.getJson());
										});
										message.reply(resultadoJson);
									} else {
										resultadoJson.put("error", "ERROR AL OBTENER LAS DOSIS DEL USUARIO CON DNI: "
												+ nif + " ." + String.valueOf(res2.cause()));
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

	// EJEMPLO BODY : {"nif":"78130288F"}
	public void getSiguienteDosisPorUsuario() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("getSiguienteDosisPorUsuario");
		consumer.handler(message -> {
			String datosUsuario = message.body();
			if (utils.checkJson(datosUsuario) == true) {
				JsonObject jsonUsuario = new JsonObject(datosUsuario);
				JsonObject jsonComp = new JsonObject();

				boolean comprobacion = jsonUsuario.containsKey("nif") && jsonUsuario != null;
				if (comprobacion) {

					String nif = jsonUsuario.getString("nif");
					Query<RowSet<Row>> query1 = mySqlClient
							.query("SELECT COUNT(*) as nUsuarios FROM pastillero_dad.Usuario WHERE nif = '" + nif + "';");

					query1.execute(res -> {
						JsonObject json = new JsonObject();
						if (res.succeeded()) {

							Row row = res.result().iterator().next();
							if (row.getInteger("nUsuarios") <= 0) {
								json.put("error", "USUARIO NO ENCONTRADO");
								message.fail(500, String.valueOf(json));
							} else {

								Query<RowSet<Row>> query = mySqlClient.query("SELECT * " + "FROM Dosis "
										+ "WHERE nif = '" + nif + "' ORDER BY "
										+ "if(TIMEDIFF(addtime(DATE_ADD(CURDATE(), INTERVAL dia_semana - weekday(CURDATE()) DAY), hora_inicio), now()) < 0, "
										+ "TIMEDIFF(addtime(DATE_ADD(CURDATE(), INTERVAL (7 - weekday(CURDATE())) + dia_semana DAY), hora_inicio), now()), "
										+ "TIMEDIFF(addtime(DATE_ADD(CURDATE(), INTERVAL dia_semana - weekday(CURDATE()) DAY), hora_inicio), now())) "
										+ " LIMIT 1;");
								query.execute(res2 -> {
									JsonObject resultadoJson = new JsonObject();
									if (res2.succeeded()) {
										res2.result().forEach(v -> {
											DosisImpl dosis = new DosisImpl(v);
											resultadoJson.put(String.valueOf(dosis.getId_dosis()), dosis.getJson());
										});
										message.reply(resultadoJson);
									} else {
										resultadoJson.put("error",
												"ERROR AL OBTENER LA SIGUIENTE DOSIS DEL USUARIO CON DNI: " + nif + " ."
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

	// EJEMPLO BODY: {"nif": "78130288F" , "dia_semana": 2, "hora_inicio": "10:00"}
	public void deleteDosis() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("deleteDosis");
		consumer.handler(message -> {
			String datosDosis = message.body();

			if (utils.checkJson(datosDosis) == true) {
				JsonObject jsonDosis = new JsonObject(datosDosis);
				JsonObject jsonComp = new JsonObject();

				boolean comprobacion = jsonDosis.containsKey("nif") && jsonDosis.containsKey("hora_inicio")
						&& jsonDosis.containsKey("dia_semana") && jsonDosis != null;

				if (comprobacion) {
					String nif = jsonDosis.getString("nif");
					String hora_inicio = jsonDosis.getString("hora_inicio");
					String dia_semana = jsonDosis.getString("dia_semana");

					Query<RowSet<Row>> query1 = mySqlClient
							.query("SELECT COUNT(*) as nDosis FROM pastillero_dad.Dosis WHERE NIF = '" + nif
									+ "' AND HORA_INICIO = '" + hora_inicio + "' AND DIA_SEMANA = '" + dia_semana
									+ "';");
					query1.execute(res -> {
						JsonObject json = new JsonObject();
						if (res.succeeded()) {
							Row row = res.result().iterator().next();
							if (row.getInteger("nDosis") <= 0) {
								json.put("error", "DOSIS NO ENCONTRADA");
								message.fail(500, String.valueOf(json));
							} else {
								Query<RowSet<Row>> query2 = mySqlClient.query(
										"DELETE FROM pastillero_dad.Dosis WHERE nif = '" + nif + "' AND hora_inicio = '"
												+ hora_inicio + "' AND dia_semana = '" + dia_semana + "';");

								query2.execute(res2 -> {
									JsonObject resultadoJson = new JsonObject();
									if (res2.succeeded()) {
										resultadoJson.put(nif,
												"BORRADA LA DOSIS DEL USUARIO CON NIF:  " + nif + " HORA DE INICIO: "
														+ hora_inicio + " Y DIA DE LA SEMANA:  " + dia_semana);
										message.reply(resultadoJson);
									} else {
										resultadoJson.put("error",
												"ERROR AL BORRAR LA DOSIS CON NIF: " + nif + " HORA DE INICIO: "
														+ hora_inicio + " Y DIA DE LA SEMANA:  " + dia_semana + " ."
														+ String.valueOf(res2.cause()));
										message.fail(500, String.valueOf(resultadoJson));
									}
								});
							}
						} else {
							json.put("error", "USUARIO CON ID: " + nif + " NO ENCONTRADO;");
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

	// EJEMPLO BODY: {"nif": "78130288F" , "dia_semana": 1, "hora_inicio": "12:30" ,"observacion": "SUPER IMPORTANTE"}
	public void addDosis() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("addDosis");
		consumer.handler(message -> {
			String datosDosis = message.body();
			if (utils.checkJson(datosDosis) == true) {
				JsonObject jsonDosis = new JsonObject(datosDosis);
				JsonObject jsonComp = new JsonObject();

				boolean comprobacion = jsonDosis.containsKey("nif") && jsonDosis.containsKey("hora_inicio")
						&& jsonDosis.containsKey("dia_semana") && jsonDosis != null;

				if (comprobacion) {
					Query<RowSet<Row>> query = null;
					if (jsonDosis.containsKey("observacion")) {
						query = mySqlClient.query("INSERT INTO Dosis (hora_inicio,dia_semana,nif)" + " VALUES('"
								+ jsonDosis.getString("hora_inicio") + "','" + jsonDosis.getString("dia_semana") + "','"
								+ jsonDosis.getString("nif") + "');");
					} else {
						query = mySqlClient.query("INSERT INTO Dosis (hora_inicio,dia_semana,nif,observacion)"
								+ " VALUES('" + jsonDosis.getString("hora_inicio") + "','"
								+ jsonDosis.getString("dia_semana") + "','" + jsonDosis.getString("nif") + "','"
								+ jsonDosis.getString("observacion") + "');");
					}

					query.execute(res -> {
						JsonObject resultadoJson = new JsonObject();
						if (res.succeeded()) {

							resultadoJson.put(jsonDosis.getString("nif"),
									"AÑADIDA LA DOSIS DEL USUARIO CON NIF:  " + jsonDosis.getString("nif")
											+ " HORA DE INICIO: " + jsonDosis.getString("hora_inicio")
											+ " Y DIA DE LA SEMANA:  " + jsonDosis.getString("dia_semana"));
							message.reply(resultadoJson);

						} else {
							resultadoJson.put("error",
									"ERROR AL AÑADIR LA DOSIS CON NIF: " + jsonDosis.getString("nif")
											+ " HORA DE INICIO: " + jsonDosis.getString("hora_inicio")
											+ " Y DIA DE LA SEMANA:  " + jsonDosis.getString("dia_semana") + " ."
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

	// EJEMPLO BODY : {"nif": "78130288F" , "dia_semana": 2, "hora_inicio": "22:00" , "observacion": "No se pueden olvidar."}
	public void editDosis() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("editDosis");
		consumer.handler(message -> {
			String datosDosis = message.body();
			if (utils.checkJson(datosDosis) == true) {
				JsonObject jsonDosis = new JsonObject(datosDosis);
				JsonObject jsonComp = new JsonObject();

				boolean comprobacion = jsonDosis.containsKey("nif") && jsonDosis.containsKey("hora_inicio")
						&& jsonDosis.containsKey("dia_semana") && jsonDosis.containsKey("observacion")
						&& jsonDosis != null;
				if (comprobacion) {

					String nif = jsonDosis.getString("nif");
					String hora_inicio = jsonDosis.getString("hora_inicio");
					String dia_semana = jsonDosis.getString("dia_semana");
					jsonDosis.remove("nif");
					jsonDosis.remove("hora_inicio");
					jsonDosis.remove("dia_semana");
					Query<RowSet<Row>> query1 = mySqlClient
							.query("SELECT COUNT(*) as nDosis FROM pastillero_dad.Dosis WHERE NIF = '" + nif
									+ "' AND HORA_INICIO = '" + hora_inicio + "' AND DIA_SEMANA = '" + dia_semana
									+ "';");
					query1.execute(res -> {
						JsonObject json = new JsonObject();
						if (res.succeeded()) {
							Row row = (Row) res.result().iterator().next();
							if (row.getInteger("nDosis") <= 0) {
								json.put("error", "DOSIS NO ENCONTRADA");
								message.fail(500, String.valueOf(json));
							} else {
								String stringQuery = "UPDATE pastillero_dad.Dosis SET ";
								Iterator<Entry<String, Object>> iteratorJsonDosis = jsonDosis.iterator();
								while (iteratorJsonDosis.hasNext()) {
									Entry<String, Object> elemento = iteratorJsonDosis.next();
									stringQuery += elemento.getKey() + " = ";
									if (elemento.getValue() == null || elemento.getValue() instanceof Number) {
										stringQuery += elemento.getValue();
									} else {
										stringQuery += "'" + elemento.getValue() + "'";
									}
									if (iteratorJsonDosis.hasNext()) {
										stringQuery += ", ";
									}
								}
								stringQuery += "WHERE nif = '" + nif + "' AND hora_inicio = '" + hora_inicio
										+ "' AND dia_semana = '" + dia_semana + "';";
								Query<RowSet<Row>> query = mySqlClient.query(stringQuery);
								query.execute(res2 -> {
									JsonObject resultadoJson = new JsonObject();
									if (res2.succeeded()) {

										resultadoJson.put(nif,
												"EDITADA LA DOSIS DEL USUARIO CON NIF:  " + nif + " HORA DE INICIO: "
														+ hora_inicio + " Y DIA DE LA SEMANA:  " + dia_semana);
										message.reply(resultadoJson);
									} else {
										resultadoJson.put("error",
												"ERROR AL EDITAR LA DOSIS CON NIF: " + nif + " HORA DE INICIO: "
														+ hora_inicio + " Y DIA DE LA SEMANA:  " + dia_semana + " . "
														+ String.valueOf(res2.cause()));
										message.fail(500, String.valueOf(resultadoJson));
									}

								});
							}
						} else {
							json.put("error", "ERROR AL EDITAR EL LA DOSIS DEL USUARIO CON NIF: " + nif + " ."
									+ String.valueOf(res.cause()));
							message.reply(json);
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

	// EJEMPLO BODY : {"nif": "78130288F"}
	public void getAllRegistroDosis() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("getAllRegistroDosis");
		consumer.handler(message -> {
			String datosUsuario = message.body();

			if (utils.checkJson(datosUsuario) == true) {
				JsonObject jsonUsuario = new JsonObject(datosUsuario);
				JsonObject jsonComp = new JsonObject();

				boolean comprobacion = jsonUsuario.containsKey("nif") && jsonUsuario != null;

				if (comprobacion) {

					String nif = jsonUsuario.getString("nif");

					Query<RowSet<Row>> query1 = mySqlClient
							.query("SELECT COUNT(*) as nUsuarios FROM pastillero_dad.Usuario WHERE nif = '" + nif + "';");

					query1.execute(res -> {
						JsonObject json = new JsonObject();
						if (res.succeeded()) {

							Row row = res.result().iterator().next();
							if (row.getInteger("nUsuarios") <= 0) {
								json.put("error", "USUARIO NO ENCONTRADO");
								message.fail(500, String.valueOf(json));
							} else {
								Query<RowSet<Row>> query2 = mySqlClient
										.query("SELECT COUNT(*) as nDosis FROM pastillero_dad.Registro_Dosis "
												+ "JOIN pastillero_dad.dosis ON registro_dosis.id_dosis = dosis.id_dosis WHERE dosis.nif = '"
												+ nif + "';");
								query2.execute(res2 -> {
									JsonObject json2 = new JsonObject();
									if (res2.succeeded()) {

										Row row2 = res2.result().iterator().next();
										if (row2.getInteger("nDosis") <= 0) {
											json2.put("error", "EL USUARIO NO POSEE NINGÚN REGISTRO DE DOSIS");
											message.reply(json2);

										} else {

											Query<RowSet<Row>> query = mySqlClient.query(
													"SELECT Registro_Dosis.id_registro_dosis, Registro_Dosis.id_dosis, Registro_Dosis.tomada FROM pastillero_dad.Registro_Dosis "
															+ "JOIN pastillero_dad.dosis ON registro_dosis.id_dosis = dosis.id_dosis WHERE dosis.nif = '"
															+ nif + "';");

											query.execute(res3 -> {

												JsonObject resultadoJson = new JsonObject();
												if (res3.succeeded()) {
													res3.result().forEach(v -> {
														RegistroDosisImpl RegistroDosis = new RegistroDosisImpl(v);
														resultadoJson.put(
																String.valueOf(RegistroDosis.getId_registro_dosis()),
																RegistroDosis.getJson());
													});
													message.reply(resultadoJson);
												} else {
													resultadoJson.put("error",
															"ERROR AL OBTENER TODOS LOS REGISTROS DE LA DOSIS DEL USUARIO CON NIF: "
																	+ nif + " ." + String.valueOf(res3.cause()));
													message.fail(500, String.valueOf(json));
												}
											});
										}
									} else {
										json.put("error",
												"ERROR AL OBTENER TODOS LOS REGISTROS DE LA DOSIS DEL USUARIO CON NIF: "
														+ nif + " . " + String.valueOf(res.cause()));
										message.fail(500, String.valueOf(json));
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

	// EJEMPLO BODY: {"id_dosis" : 1 , "tomada": false}
	public void addRegistroDosis() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("addRegistroDosis");
		consumer.handler(message -> {
			String datosRegistro = message.body();
			if (utils.checkJson(datosRegistro) == true) {
				JsonObject jsonRegistro = new JsonObject(datosRegistro);
				JsonObject jsonComp = new JsonObject();

				boolean comprobacion = jsonRegistro.containsKey("id_dosis") && jsonRegistro.containsKey("tomada")
						&& jsonRegistro != null;

				if (comprobacion) {
					int id_dosis = jsonRegistro.getInteger("id_dosis");
					Boolean tomada = jsonRegistro.getBoolean("tomada");

					Query<RowSet<Row>> query1 = mySqlClient.query(
							"SELECT COUNT(*) as nDosis FROM pastillero_dad.Dosis WHERE id_dosis = " + id_dosis + ";");

					query1.execute(res -> {
						JsonObject json = new JsonObject();
						if (res.succeeded()) {

							Row row = res.result().iterator().next();
							if (row.getInteger("nDosis") <= 0) {
								json.put("error", "DOSIS NO ENCONTRADA");
								message.fail(500, String.valueOf(json));
							} else {

								Query<RowSet<Row>> query = mySqlClient
										.query("INSERT INTO Registro_Dosis (id_dosis,tomada)" + " VALUES(" + id_dosis
												+ "," + tomada + ");");
								query.execute(res2 -> {
									JsonObject resultadoJson = new JsonObject();
									if (res2.succeeded()) {
										resultadoJson.put(String.valueOf(id_dosis),
												"AÑADIDO EL REGISTRO DE LA DOSIS CON ID:  " + String.valueOf(id_dosis)
														+ " .");
										message.reply(resultadoJson);
									} else {
										resultadoJson.put("error", "ERROR AL AÑADIR EL REGISTRO DE LA DOSIS CON ID: "
												+ String.valueOf(id_dosis) + " ." + String.valueOf(res2.cause()));
										message.fail(500, String.valueOf(resultadoJson));
									}

								});
							}
						} else {
							json.put("error", "ERROR AL OBTENER LA DOSIS CON ID : " + id_dosis + " . "
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

	// EJEMPLO BODY : {"id_registro_dosis": 2}
	public void deleteRegistroDosis() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("deleteRegistroDosis");
		consumer.handler(message -> {
			String datosRegistroDosis = message.body();

			if (utils.checkJson(datosRegistroDosis) == true) {
				JsonObject jsonDosis = new JsonObject(datosRegistroDosis);
				JsonObject jsonComp = new JsonObject();

				boolean comprobacion = jsonDosis.containsKey("id_registro_dosis") && jsonDosis != null;

				if (comprobacion) {

					int id_registro_dosis = jsonDosis.getInteger("id_registro_dosis");
					Query<RowSet<Row>> query1 = mySqlClient.query(
							"SELECT COUNT(*) as nRegistro FROM pastillero_dad.Registro_Dosis WHERE ID_REGISTRO_DOSIS = "
									+ id_registro_dosis + ";");
					query1.execute(res -> {
						JsonObject json = new JsonObject();
						if (res.succeeded()) {
							Row row = res.result().iterator().next();
							if (row.getInteger("nRegistro") <= 0) {
								json.put("error", "REGISTRO NO ENCONTRADO");
								message.fail(500, String.valueOf(json));
							} else {
								Query<RowSet<Row>> query = mySqlClient
										.query("DELETE FROM pastillero_dad.Registro_Dosis WHERE id_registro_dosis = "
												+ id_registro_dosis + ";");
								query.execute(res2 -> {
									JsonObject resultadoJson = new JsonObject();

									if (res2.succeeded()) {

										resultadoJson.put(String.valueOf(id_registro_dosis),
												"BORRADO EL REGISTRO DE LA DOSIS CON ID:  "
														+ String.valueOf(id_registro_dosis) + " .");
										message.reply(resultadoJson);
									} else {
										resultadoJson.put("error",
												"ERROR AL BORRAR EL REGISTRO DE LA DOSIS CON ID: "
														+ String.valueOf(id_registro_dosis) + " ."
														+ String.valueOf(res2.cause()));
										message.fail(500, String.valueOf(resultadoJson));
									}

								});
							}
						} else {
							json.put("error", "REGISTRO CON ID: " + id_registro_dosis + " DE LA DOSIS NO ENCONTRADO");
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

	// EJEMPLO BODY : {"id_registro_dosis": 1, "tomada" : true}
	public void editRegistroDosis() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("editRegistroDosis");
		consumer.handler(message -> {
			String datosRegistroDosis = message.body();
			if (utils.checkJson(datosRegistroDosis) == true) {
				JsonObject jsonDosis = new JsonObject(datosRegistroDosis);
				JsonObject jsonComp = new JsonObject();

				boolean comprobacion = jsonDosis.containsKey("id_registro_dosis") && jsonDosis.containsKey("tomada")
						&& jsonDosis != null;

				if (comprobacion) {
					int id_registro_dosis = jsonDosis.getInteger("id_registro_dosis");
					jsonDosis.remove("id_registro_dosis");
					Query<RowSet<Row>> query1 = mySqlClient
							.query("SELECT COUNT(*) as nRegistro FROM Registro_Dosis WHERE id_registro_dosis = "
									+ id_registro_dosis + ";");
					query1.execute(res -> {
						JsonObject json = new JsonObject();
						if (res.succeeded()) {
							Row row = (Row) res.result().iterator().next();
							if (row.getInteger("nRegistro") <= 0) {
								json.put("error", "REGISTRO NO ENCONTRADO");
								message.reply(json);
							} else {
								String stringQuery = "UPDATE pastillero_dad.Registro_Dosis SET ";
								Iterator<Entry<String, Object>> iteratorJsonDosis = jsonDosis.iterator();
								while (iteratorJsonDosis.hasNext()) {
									Entry<String, Object> elemento = iteratorJsonDosis.next();
									stringQuery += elemento.getKey() + " = ";
									if (elemento.getValue() == null || elemento.getValue() instanceof Number) {
										stringQuery += elemento.getValue();
									} else {
										stringQuery += "'" + elemento.getValue() + "'";
									}
									if (iteratorJsonDosis.hasNext()) {
										stringQuery += ", ";
									}
								}
								stringQuery += " WHERE id_registro_dosis = " + id_registro_dosis + ";";
								System.out.println(stringQuery);
								Query<RowSet<Row>> query = mySqlClient.query(stringQuery);
								query.execute(res2 -> {
									JsonObject resultadoJson = new JsonObject();
									if (res.succeeded()) {
										resultadoJson.put(String.valueOf(id_registro_dosis),
												"EDITADO EL REGISTRO DE LA DOSIS CON ID:  "
														+ String.valueOf(id_registro_dosis) + " .");
										message.reply(resultadoJson);
									} else {
										resultadoJson.put("error",
												"ERROR AL EDITAR EL REGISTRO DE LA DOSIS CON ID: "
														+ String.valueOf(id_registro_dosis) + " ."
														+ String.valueOf(res2.cause()));
										message.fail(500, String.valueOf(resultadoJson));
									}
								});
							}
						} else {
							json.put("error", "ERROR AL EDITAR EL REGISTRO CON ID: " + id_registro_dosis + " . "
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
	
	
	
	
	public void getSiguienteDosisByPastillero() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("getSiguienteDosisByPastillero");
		consumer.handler(message -> {
			h = 0;
			JsonObject jsonRes = new JsonObject();
			JsonObject jsonDatosDosis = new JsonObject();
			Query<RowSet<Row>> query1 = mySqlClient
					.query("SELECT COUNT(*) as nPastilleros FROM pastillero_dad.Pastillero ;");

			query1.execute(res -> {
				if (res.succeeded()) {
					Row row = res.result().iterator().next();
					if (row.getInteger("nPastilleros") <= 0) {
						System.out.println("NO HAY NINGÚN PASTILLERO REGISTRADO.");
					} else {
						Query<RowSet<Row>> query2 = mySqlClient.query("SELECT * FROM pastillero_dad.Pastillero ;");

						query2.execute(res2 -> {

							if (res2.succeeded()) {
								res2.result().forEach(v -> {

									Query<RowSet<Row>> query3 = mySqlClient.query("SELECT dia_semana, hora_inicio "
											+ "FROM Dosis " + "JOIN pastillero_dad.Usuario ON Usuario.nif = dosis.nif "
											+ "WHERE id_pastillero = '" + v.getString("id_pastillero") + "' ORDER BY "
											+ "if(TIMEDIFF(addtime(DATE_ADD(CURDATE(), INTERVAL dia_semana - weekday(CURDATE()) DAY), hora_inicio), now()) < 0, "
											+ "TIMEDIFF(addtime(DATE_ADD(CURDATE(), INTERVAL (7 - weekday(CURDATE())) + dia_semana DAY), hora_inicio), now()), "
											+ "TIMEDIFF(addtime(DATE_ADD(CURDATE(), INTERVAL dia_semana - weekday(CURDATE()) DAY), hora_inicio), now())) "
											+ " LIMIT 1;");
									
									query3.execute(res3 -> {
										if (res3.succeeded()) {
											
											res3.result().forEach(d -> {
												
												JsonObject jsonResultDosis = new JsonObject();
												jsonResultDosis.put(d.getInteger("dia_semana").toString(),
														d.getString("hora_inicio"));
												
												jsonRes.put(v.getString("id_pastillero"), jsonResultDosis);													
											});
											h = h + 1;
											if ( h == row.getInteger("nPastilleros")) {
												 message.reply(jsonRes);
												}
										} else {
											jsonDatosDosis.put("error",
													"ERROR AL OBTENER LAS SIGUIENTES DOSIS DE CADA PASTILLERO REGISTRADO.");
											message.fail(500, String.valueOf(jsonDatosDosis));
										}
									});
								});							
							} else {
								jsonRes.put("error", "ERROR AL OBTENER LOS PASTILLEROS REGISTRADOS.");
								message.fail(500, String.valueOf(jsonRes));
							}
						});
					}
				} else {
					jsonRes.put("error", "ERROR AL CONTABILIZAR LOS PASTILLEROS REGISTRADOS.");
					message.fail(500, String.valueOf(jsonRes));
				}	
			});
	
		});
	}
}