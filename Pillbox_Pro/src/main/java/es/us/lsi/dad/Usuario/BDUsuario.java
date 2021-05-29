package es.us.lsi.dad.Usuario;

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

public class BDUsuario {
	Vertx vertx;
	MySQLPool mySqlClient;

	Utils utils = new Utils();

	public BDUsuario(Vertx vertx, MySQLPool mySqlClient) {
		this.vertx = vertx;
		this.mySqlClient = mySqlClient;
	}

	public void iniciarConsumersBDUsuario() {
		getAllUsuarios();
		getUsuarioNIF();
		getEnfermosPorCuidador();
		deleteUsuario();
		addUsuario();
		editUsuario();

	}

	public void getAllUsuarios() {

		MessageConsumer<String> consumer = vertx.eventBus().consumer("getAllUsuarios");
		consumer.handler(message -> {
			Query<RowSet<Row>> query = mySqlClient.query("SELECT * FROM pastillero_dad.Usuario;");
			query.execute(res -> {
				JsonObject json = new JsonObject();
				if (res.succeeded()) {
					res.result().forEach(v -> {
						UsuarioImpl usuario = new UsuarioImpl(v);
						json.put(String.valueOf(usuario.getNif()), usuario.getJson());
					});
					message.reply(json);
				} else {
					json.put("error", "ERROR AL OBTENER TODOS LOS USUARIOS: " + " ." + String.valueOf(res.cause()));
					message.fail(500, String.valueOf(res.cause()));
				}
			});
		});
	}

	// EJEMPLO BODY: { "nif": "53420191L"}
	public void getUsuarioNIF() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("getUsuarioNIF");
		consumer.handler(message -> {
			String usuarionif = message.body();

			if (utils.checkJson(usuarionif) == true) {
				JsonObject jsonUsuarionif = new JsonObject(usuarionif);
				JsonObject jsonComp = new JsonObject();

				boolean comprobacion = jsonUsuarionif.containsKey("nif") && jsonUsuarionif != null;
				if (comprobacion) {

					Query<RowSet<Row>> query = mySqlClient.query("SELECT * FROM pastillero_dad.Usuario WHERE nif = '"
							+ jsonUsuarionif.getString("nif") + "';");

					query.execute(res -> {
						JsonObject resultadoJson = new JsonObject();

						if (res.succeeded()) {
							res.result().forEach(v -> {
								UsuarioImpl usuario = new UsuarioImpl(v);
								resultadoJson.put(String.valueOf(usuario.getNif()), usuario.getJson());
							});
							message.reply(resultadoJson);
						} else {
							resultadoJson.put("error", "ERROR AL OBTENER EL USUARIO CON NIF: " + usuarionif + " ."
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

	// EJEMPLO BODY: {"id_cuidador" : "45349133D"}
	public void getEnfermosPorCuidador() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("getEnfermosPorCuidador");
		consumer.handler(message -> {
			String Id_cuidador = message.body();
			if (utils.checkJson(Id_cuidador) == true) {
				JsonObject jsonNifCuidador = new JsonObject(Id_cuidador);
				JsonObject jsonComp = new JsonObject();

				boolean comprobacion = jsonNifCuidador.containsKey("id_cuidador") && jsonNifCuidador != null;

				if (comprobacion) {

					Query<RowSet<Row>> query = mySqlClient
							.query("SELECT * FROM pastillero_dad.Usuario WHERE id_cuidador = '"
									+ jsonNifCuidador.getString("id_cuidador") + "';");

					query.execute(res -> {
						JsonObject json = new JsonObject();

						if (res.succeeded()) {
							res.result().forEach(v -> {
								UsuarioImpl usuario = new UsuarioImpl(v);
								json.put(String.valueOf(usuario.getNif()), usuario.getJson());
							});
							message.reply(json);
						} else {
							json.put("error", "ERROR AL OBTENER LOS PACIENTES DEL CUIDADOR CON ID: '" + Id_cuidador
									+ "' ." + String.valueOf(res.cause()));
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

	// EJEMPLO BODY: {"nif": "78130288F"}
	public void deleteUsuario() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("deleteUsuario");
		consumer.handler(message -> {
			String nif = message.body();
			if (utils.checkJson(nif) == true) {
				JsonObject jsonUsuario = new JsonObject(nif);
				JsonObject jsonComp = new JsonObject();

				boolean comprobacion = jsonUsuario.containsKey("nif") && jsonUsuario != null;

				if (comprobacion) {

					Query<RowSet<Row>> query1 = mySqlClient
							.query("SELECT COUNT(*) as nUsuarios FROM pastillero_dad.Usuario WHERE NIF = '"
									+ jsonUsuario.getString("nif") + "';");
					query1.execute(res -> {
						JsonObject json = new JsonObject();
						if (res.succeeded()) {
							Row row = res.result().iterator().next();
							if (row.getInteger("nUsuarios") <= 0) {
								json.put("error", "USUARIO NO ENCONTRADO");
								message.fail(500, String.valueOf(json));
							} else {
								Query<RowSet<Row>> query2 = mySqlClient
										.query("DELETE FROM pastillero_dad.Usuario WHERE NIF = '"
												+ jsonUsuario.getString("nif") + "';");
								query2.execute(res2 -> {
									JsonObject json2 = new JsonObject();
									if (res2.succeeded()) {
										json2.put(jsonUsuario.getString("nif"),
												"USUARIO BORRADO CON EL NIF " + jsonUsuario.getString("nif"));
										message.reply(json2);
									} else {
										json2.put("error", "ERROR AL BORRAR EL USUARIO CON NIF: "
												+ jsonUsuario.getString("nif") + " ." + String.valueOf(res.cause()));
										message.fail(500, String.valueOf(json2));
									}

								});
							}
						} else {
							json.put("error",
									"ERROR AL BORRAR EL USUARIO CON NIF: " + jsonUsuario.getString("nif") + " .");
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

	// EJEMPLO BODY : {"nif": "13039218R","id_pastillero": "w1e2d4f5ffeecnss3fpol247hg7fg1244423435g", "firstname": "Fernando", "lastname" : "Sanchez Campana", "contraseña": "Fernando22" , "email" : "fsanchez@gmail.com", "rol":"cuidador"}
	public void addUsuario() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("addUsuario");
		consumer.handler(message -> {
			String datosUsuario = message.body();
			if (utils.checkJson(datosUsuario) == true) {
				JsonObject jsonUsuario = new JsonObject(datosUsuario);
				JsonObject jsonComp = new JsonObject();

				boolean comprobacion = jsonUsuario.containsKey("nif") && jsonUsuario.containsKey("id_pastillero")
						&& jsonUsuario.containsKey("firstname") && jsonUsuario.containsKey("lastname")
						&& jsonUsuario.containsKey("contraseña") && jsonUsuario.containsKey("email")
						&& jsonUsuario.containsKey("rol") && jsonUsuario != null;

				if (comprobacion) {
					String stringQuery;
					if (jsonUsuario.containsKey("id_cuidador")) {
						stringQuery = "INSERT INTO pastillero_dad.Usuario(nif,id_pastillero,firstname, lastname,contraseña, email, rol,id_cuidador) VALUES (";
					} else {
						stringQuery = "INSERT INTO pastillero_dad.Usuario(nif,id_pastillero,firstname, lastname,contraseña, email, rol) VALUES (";
					}
					Iterator<Entry<String, Object>> iteratorJsonUsuario = jsonUsuario.iterator();
					while (iteratorJsonUsuario.hasNext()) {
						Entry<String, Object> elemento = iteratorJsonUsuario.next();

						if (elemento.getValue() == null || elemento.getValue() instanceof Number) {
							stringQuery += elemento.getValue();
						} else {
							stringQuery += "'" + elemento.getValue() + "'";
						}
						if (iteratorJsonUsuario.hasNext()) {
							stringQuery += ", ";
						}
					}
					stringQuery += ");";

					Query<RowSet<Row>> query = mySqlClient.query(stringQuery);
					query.execute(res -> {
						JsonObject json = new JsonObject();
						if (res.succeeded()) {
							json.put(jsonUsuario.getString("nif"), "AÑADIDO EL USUARIO  "
									+ jsonUsuario.getString("firstname") + " con DNI: " + jsonUsuario.getString("nif"));
							message.reply(json);
						} else {
							json.put("error", "ERROR AL AÑADIR EL USUARIO CON NIF: " + jsonUsuario.getString("nif")
									+ " ." + String.valueOf(res.cause()));
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

	// EJEMPLO BODY : {"nif": "53420191L", "contraseña": "Manuel240" }
	public void editUsuario() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("editUsuario");
		consumer.handler(message -> {
			String datosUsuario = message.body();
			if (utils.checkJson(datosUsuario) == true) {
				JsonObject jsonUsuario = new JsonObject(datosUsuario);
				JsonObject jsonComp = new JsonObject();

				boolean comprobacion = jsonUsuario.containsKey("nif") && jsonUsuario != null;

				if (comprobacion) {

					String nif = jsonUsuario.getString("nif");
					Query<RowSet<Row>> query1 = mySqlClient
							.query("SELECT COUNT(*) as nUsuarios FROM Usuario WHERE nif = '" + nif + "';");
					query1.execute(res -> {
						JsonObject json = new JsonObject();
						if (res.succeeded()) {
							Row row = (Row) res.result().iterator().next();
							if (row.getInteger("nUsuarios") <= 0) {
								json.put("error", "USUARIO NO ENCONTRADO");
								message.fail(500, String.valueOf(json));
							} else {
								jsonUsuario.remove("nif");
								String stringQuery = "UPDATE pastillero_dad.Usuario SET ";

								Iterator<Entry<String, Object>> iteratorJsonUsuario = jsonUsuario.iterator();
								if (iteratorJsonUsuario.hasNext()) {
									while (iteratorJsonUsuario.hasNext()) {
										Entry<String, Object> elemento = iteratorJsonUsuario.next();
										stringQuery += elemento.getKey() + " = ";
										if (elemento.getValue() == null || elemento.getValue() instanceof Number) {
											stringQuery += elemento.getValue();
										} else {
											stringQuery += "'" + elemento.getValue() + "'";
										}
										if (iteratorJsonUsuario.hasNext()) {
											stringQuery += ", ";
										}
									}
									stringQuery += "WHERE nif = '" + nif + "';";
									Query<RowSet<Row>> query2 = mySqlClient.query(stringQuery);
									query2.execute(res2 -> {
										JsonObject json2 = new JsonObject();
										if (res2.succeeded()) {
											json2.put(nif, "EDITADO EL USUARIO EL USUARIO  " + " CON DNI: " + nif);
											message.reply(json2);
										} else {
											json2.put("error", "ERROR AL EDITAR EL USUARIO CON NIF: " + nif + " ."
													+ String.valueOf(res.cause()));
											message.fail(500, String.valueOf(json2));
										}
									});
								} else {
									json.put("error", "NO HAY SUFICIENTES CAMPOS PARA EDITAR" + " .");
									message.fail(500, String.valueOf(json));
								}
							}
						} else {
							json.put("error",
									"ERROR AL EDITAR EL USUARIO CON NIF: " + nif + " ." + String.valueOf(res.cause()));
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
}
