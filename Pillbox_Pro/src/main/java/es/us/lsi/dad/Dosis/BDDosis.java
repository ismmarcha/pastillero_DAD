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
		deleteDosis();
		editDosis();
		addDosis();

		getAllRegistroDosis();
		addRegistroDosis();
		deleteRegistroDosis();
		editRegistroDosis();

	}
	// BODY:

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
					resultadoJson.put("error", "ERROR AL OBTENER TODAS LAS DOSIS" + " ." + String.valueOf(res.cause()));
				}
				message.reply(resultadoJson);
			});
		});
	}

	// BODY: { "nif": "12344", "hora_inicio": "19:00" , "dia_semana": 2 }

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
							+ nif + "' AND hora_inicio = '" + hora_inicio + "' AND dia_semana = '" + dia_semana + "';");
					
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

	public void getDosisPorUsuario() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("getDosisPorUsuario");
		consumer.handler(message -> {
			String nif = message.body();
			Query<RowSet<Row>> query = mySqlClient.query("SELECT * FROM pastillero_dad.Dosis WHERE nif = " + nif + ";");
			query.execute(res -> {
				JsonObject resultadoJson = new JsonObject();
				if (res.succeeded()) {
					res.result().forEach(v -> {
						DosisImpl dosis = new DosisImpl(v);
						resultadoJson.put(String.valueOf(dosis.getId_dosis()), dosis.getJson());
					});
				} else {
					resultadoJson.put("error", "ERROR AL OBTENER LAS DOSIS DEL USUARIO CON DNI: " + nif + " ."
							+ String.valueOf(res.cause()));
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
					resultadoJson.put("error", "ERROR AL OBTENER LA DOSIS DEL USUARIO CON DNI: " + nif + " DEL DÍA : "
							+ dia_semana + " ." + String.valueOf(res.cause()));
				}

				message.reply(resultadoJson);
			});
		});
	}

	public void getDosisPorUsuarioGroupByDia() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("getDosisPorUsuarioGroupByDia");
		consumer.handler(message -> {
			JsonObject jsonBody = new JsonObject(message.body());
			String nif = jsonBody.getString("nif");
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
					resultadoJson.put("error", "ERROR AL OBTENER LAS DOSIS DEL USUARIO CON DNI: " + nif + " ."
							+ String.valueOf(res.cause()));
				}
				message.reply(resultadoJson);
			});
		});
	}

	public void getSiguienteDosisPorUsuario() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("getSiguienteDosisPorUsuario");
		consumer.handler(message -> {
			String nif = message.body();
			Query<RowSet<Row>> query = mySqlClient.query("SELECT * " + "FROM Dosis " + "WHERE nif = '" + nif
					+ "' ORDER BY "
					+ "if(TIMEDIFF(addtime(DATE_ADD(CURDATE(), INTERVAL dia_semana - weekday(CURDATE()) DAY), hora_inicio), now()) < 0, "
					+ "TIMEDIFF(addtime(DATE_ADD(CURDATE(), INTERVAL (7 - weekday(CURDATE())) + dia_semana DAY), hora_inicio), now()), "
					+ "TIMEDIFF(addtime(DATE_ADD(CURDATE(), INTERVAL dia_semana - weekday(CURDATE()) DAY), hora_inicio), now())) "
					+ " LIMIT 1;");
			query.execute(res -> {
				JsonObject resultadoJson = new JsonObject();
				if (res.succeeded()) {
					res.result().forEach(v -> {
						DosisImpl dosis = new DosisImpl(v);
						resultadoJson.put(String.valueOf(dosis.getId_dosis()), dosis.getJson());
					});
				} else {
					resultadoJson.put("error", "ERROR AL OBTENER LA SIGUIENTE DOSIS DEL USUARIO CON DNI: " + nif + " ."
							+ String.valueOf(res.cause()));
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

			Query<RowSet<Row>> query1 = mySqlClient
					.query("SELECT COUNT(*) as nDosis FROM pastillero_dad.Dosis WHERE NIF = '" + nif
							+ "' AND HORA_INICIO = '" + hora_inicio + "' AND DIA_SEMANA = '" + dia_semana + "';");
			query1.execute(res -> {
				JsonObject json = new JsonObject();
				if (res.succeeded()) {
					Row row = res.result().iterator().next();
					if (row.getInteger("nDosis") <= 0) {
						json.put("error", "DOSIS NO ENCONTRADA");
						message.reply(json);
					} else {
						Query<RowSet<Row>> query2 = mySqlClient
								.query("DELETE FROM pastillero_dad.Dosis WHERE nif = '" + nif + "' AND hora_inicio = '"
										+ hora_inicio + "' AND dia_semana = '" + dia_semana + "';");

						query2.execute(res2 -> {
							JsonObject resultadoJson = new JsonObject();
							if (res2.succeeded()) {
								resultadoJson.put(nif, "BORRADA LA DOSIS DEL USUARIO CON NIF:  " + nif
										+ " HORA DE INICIO: " + hora_inicio + " Y DIA DE LA SEMANA:  " + dia_semana);
							} else {
								resultadoJson.put("error",
										"ERROR AL BORRAR LA DOSIS CON NIF: " + nif + " HORA DE INICIO: " + hora_inicio
												+ " Y DIA DE LA SEMANA:  " + dia_semana + " ."
												+ String.valueOf(res2.cause()));
							}
							message.reply(resultadoJson);
						});
					}
				} else {
					json.put("error", "USUARIO CON ID: " + nif + " NO ENCONTRADO;");
					message.reply(json);
				}

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
				JsonObject resultadoJson = new JsonObject();
				if (res.succeeded()) {

					resultadoJson.put(dosis.getnif(),
							"AÑADIDA LA DOSIS DEL USUARIO CON NIF:  " + dosis.getnif() + " HORA DE INICIO: "
									+ dosis.getHora_inicio() + " Y DIA DE LA SEMANA:  " + dosis.getDia_semana());

				} else {
					resultadoJson.put("error",
							"ERROR AL AÑADIR LA DOSIS CON NIF: " + dosis.getnif() + " HORA DE INICIO: "
									+ dosis.getHora_inicio() + " Y DIA DE LA SEMANA:  " + dosis.getDia_semana() + " ."
									+ String.valueOf(res.cause()));
				}
				message.reply(resultadoJson);
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
			Query<RowSet<Row>> query1 = mySqlClient
					.query("SELECT COUNT(*) as nDosis FROM pastillero_dad.Dosis WHERE NIF = '" + nif
							+ "' AND HORA_INICIO = '" + hora_inicio + "' AND DIA_SEMANA = '" + dia_semana + "';");
			query1.execute(res -> {
				JsonObject json = new JsonObject();
				if (res.succeeded()) {
					Row row = (Row) res.result().iterator().next();
					if (row.getInteger("nDosis") <= 0) {
						json.put("error", "Dosis no encontrada");
						message.reply(json);
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

								resultadoJson.put(nif, "EDITADA LA DOSIS DEL USUARIO CON NIF:  " + nif
										+ " HORA DE INICIO: " + hora_inicio + " Y DIA DE LA SEMANA:  " + dia_semana);
							} else {
								resultadoJson.put("error",
										"ERROR AL EDITAR LA DOSIS CON NIF: " + nif + " HORA DE INICIO: " + hora_inicio
												+ " Y DIA DE LA SEMANA:  " + dia_semana + " . "
												+ String.valueOf(res2.cause()));
							}
							message.reply(resultadoJson);
						});
					}
				} else {
					json.put("error", "ERROR AL EDITAR EL LA DOSIS DEL USUARIO CON NIF: " + nif + " ."
							+ String.valueOf(res.cause()));
					message.reply(json);
				}
			});
		});
	}

	public void getAllRegistroDosis() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("getAllRegistroDosis");
		consumer.handler(message -> {
			String datosUsuario = message.body();
			JsonObject jsonUsuario = new JsonObject(datosUsuario);
			String nif = jsonUsuario.getString("nif");

			Query<RowSet<Row>> query = mySqlClient.query(
					"SELECT Registro_Dosis.id_registro_dosis, Registro_Dosis.id_dosis, Registro_Dosis.tomada FROM pastillero_dad.Registro_Dosis "
							+ "JOIN pastillero_dad.dosis ON registro_dosis.id_dosis = dosis.id_dosis WHERE dosis.nif = "
							+ nif + ";");
			System.out.println(query.toString());

			query.execute(res -> {

				JsonObject resultadoJson = new JsonObject();
				if (res.succeeded()) {
					res.result().forEach(v -> {

						RegistroDosisImpl RegistroDosis = new RegistroDosisImpl(v);
						resultadoJson.put(String.valueOf(RegistroDosis.getId_registro_dosis()),
								RegistroDosis.getJson());

					});
				} else {
					resultadoJson.put("error", "ERROR AL OBTENER TODOS LOS REGISTROS DE LA DOSIS DEL USUARIO CON NIF: "
							+ nif + " ." + String.valueOf(res.cause()));
				}
				message.reply(resultadoJson);
			});
		});
	}

	public void addRegistroDosis() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("addRegistroDosis");
		consumer.handler(message -> {
			String datosRegistro = message.body();
			JsonObject jsonRegistro = new JsonObject(datosRegistro);
			int id_dosis = jsonRegistro.getInteger("id_dosis");
			Boolean tomada = jsonRegistro.getBoolean("tomada");

			Query<RowSet<Row>> query = mySqlClient.query(
					"INSERT INTO Registro_Dosis (id_dosis,tomada)" + " VALUES(" + id_dosis + "," + tomada + ");");
			query.execute(res -> {
				JsonObject resultadoJson = new JsonObject();
				if (res.succeeded()) {
					resultadoJson.put(String.valueOf(id_dosis),
							"AÑADIDO EL REGISTRO DE LA DOSIS CON ID:  " + String.valueOf(id_dosis) + " .");
				} else {
					resultadoJson.put("error", "ERROR AL AÑADIR EL REGISTRO DE LA DOSIS CON ID: "
							+ String.valueOf(id_dosis) + " ." + String.valueOf(res.cause()));
				}
				message.reply(resultadoJson);
			});
		});
	}

	public void deleteRegistroDosis() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("deleteRegistroDosis");
		consumer.handler(message -> {
			String datosRegistroDosis = message.body();
			JsonObject jsonDosis = new JsonObject(datosRegistroDosis);
			int id_registro_dosis = jsonDosis.getInteger("id_registro_dosis");
			Query<RowSet<Row>> query1 = mySqlClient
					.query("SELECT COUNT(*) as nRegistro FROM pastillero_dad.Registro_Dosis WHERE ID_REGISTRO_DOSIS = "
							+ id_registro_dosis + ";");
			query1.execute(res -> {
				JsonObject json = new JsonObject();
				if (res.succeeded()) {
					Row row = res.result().iterator().next();
					if (row.getInteger("nRegistro") <= 0) {
						json.put("error", "Registro no encontrado");
						message.reply(json);
					} else {
						Query<RowSet<Row>> query = mySqlClient
								.query("DELETE FROM pastillero_dad.Registro_Dosis WHERE id_registro_dosis = "
										+ id_registro_dosis + ";");
						query.execute(res2 -> {
							JsonObject resultadoJson = new JsonObject();

							if (res2.succeeded()) {

								resultadoJson.put(String.valueOf(id_registro_dosis),
										"BORRADO EL REGISTRO DE LA DOSIS CON ID:  " + String.valueOf(id_registro_dosis)
												+ " .");
							} else {
								resultadoJson.put("error", "ERROR AL BORRAR EL REGISTRO DE LA DOSIS CON ID: "
										+ String.valueOf(id_registro_dosis) + " ." + String.valueOf(res2.cause()));
							}
							message.reply(resultadoJson);
						});
					}
				} else {
					json.put("error", "REGISTRO CON ID: " + id_registro_dosis + " DE LA DOSIS NO ENCONTRADO");
					message.reply(json);
				}
			});
		});
	}

	public void editRegistroDosis() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("editRegistroDosis");
		consumer.handler(message -> {
			String datosRegistroDosis = message.body();
			JsonObject jsonDosis = new JsonObject(datosRegistroDosis);
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
						json.put("error", "Registro no encontrado");
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
										"EDITADO EL REGISTRO DE LA DOSIS CON ID:  " + String.valueOf(id_registro_dosis)
												+ " .");
							} else {
								resultadoJson.put("error", "ERROR AL EDITAR EL REGISTRO DE LA DOSIS CON ID: "
										+ String.valueOf(id_registro_dosis) + " ." + String.valueOf(res2.cause()));
							}
							message.reply(resultadoJson);
						});
					}
				} else {
					json.put("error", "ERROR AL EDITAR EL REGISTRO CON ID: " + id_registro_dosis + " . "
							+ String.valueOf(res.cause()));
					message.reply(json);
				}
			});

		});
	}

}
