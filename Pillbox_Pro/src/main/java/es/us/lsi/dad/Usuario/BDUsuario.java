package es.us.lsi.dad.Usuario;

import java.util.Iterator;

import java.util.Map.Entry;

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
				} else {
					json.put("error", "ERROR AL OBTENER TODOS LOS USUARIOS: " + " ." + String.valueOf(res.cause()));
				}
				message.reply(json);
			});
		});
	}

	public void getUsuarioNIF() {

		MessageConsumer<String> consumer = vertx.eventBus().consumer("getUsuarioNIF");
		consumer.handler(message -> {
			String usuarionif = message.body();
			Query<RowSet<Row>> query = mySqlClient
					.query("SELECT * FROM pastillero_dad.Usuario WHERE nif = '" + usuarionif + "';");
			query.execute(res -> {
				JsonObject json = new JsonObject();

				if (res.succeeded()) {
					res.result().forEach(v -> {
						json.put(String.valueOf(v.getValue("nif")), v.toJson());
					});
				} else {
					json.put("error",
							"ERROR AL OBTENER EL USUARIO CON NIF: " + usuarionif + " ." + String.valueOf(res.cause()));

				}
				message.reply(json);
			});
		});
	}

	public void getEnfermosPorCuidador() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("getEnfermosPorCuidador");
		consumer.handler(message -> {
			String Id_cuidador = message.body();
			Query<RowSet<Row>> query = mySqlClient
					.query("SELECT * FROM pastillero_dad.Usuario WHERE id_cuidador = " + Id_cuidador + ";");
			query.execute(res -> {
				JsonObject json = new JsonObject();
				if (res.succeeded()) {
					res.result().forEach(v -> {
						UsuarioImpl usuario = new UsuarioImpl(v);
						json.put(String.valueOf(usuario.getNif()), usuario.getJson());
					});
				} else {
					json.put("error", "ERROR AL OBTENER LOS PACIENTES DEL CUIDADOR CON ID: " + Id_cuidador + " ."
							+ String.valueOf(res.cause()));
				}
				message.reply(json);
			});
		});
	}

	public void deleteUsuario() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("deleteUsuario");
		consumer.handler(message -> {
			String nif = message.body();
			Query<RowSet<Row>> query1 = mySqlClient
					.query("SELECT COUNT(*) as nUsuarios FROM pastillero_dad.Usuario WHERE NIF = '" + nif + "';");
			query1.execute(res -> {
				JsonObject json = new JsonObject();
				if (res.succeeded()) {
					Row row = res.result().iterator().next();
					if (row.getInteger("nUsuarios") <= 0) {
						json.put("error", "Usuario no encontrado");
						message.reply(json);
					} else {
						Query<RowSet<Row>> query2 = mySqlClient
								.query("DELETE FROM pastillero_dad.Usuario WHERE NIF = '" + nif + "';");
						query2.execute(res2 -> {
							JsonObject json2 = new JsonObject();
							if (res2.succeeded()) {
								json2.put(nif, "USUARIO BORRADO CON EL NIF " + nif);
							} else {
								json2.put("error", "ERROR AL BORRAR EL USUARIO CON NIF: " + nif + " ."
										+ String.valueOf(res.cause()));
							}
							message.reply(json2);
						});
					}
				} else {
					json.put("error", "USUARIO A ELIMINAR NO ENCONTRADO CON NIF: " + nif);
					message.reply(json);
				}
			});
		});
	}

	public void addUsuario() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("addUsuario");
		consumer.handler(message -> {
			String datosUsuario = message.body();
			JsonObject jsonUsuario = new JsonObject(datosUsuario);
			String stringQuery = "INSERT INTO pastillero_dad.Usuario(nif,id_pastillero,firstname, lastname,contraseña, email, rol,id_cuidador) VALUES (";
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
					json.put(jsonUsuario.getString("nif"), "AÑADIDO EL USUARIO  " + jsonUsuario.getString("firstname")
							+ " con DNI: " + jsonUsuario.getString("nif"));
				} else {
					json.put("error", "ERROR AL AÑADIR EL USUARIO CON NIF: " + jsonUsuario.getString("nif") + " ."
							+ String.valueOf(res.cause()));
				}
				message.reply(json);
			});
		});
	}

	public void editUsuario() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("editUsuario");
		consumer.handler(message -> {
			String datosUsuario = message.body();
			JsonObject jsonUsuario = new JsonObject(datosUsuario);
			String nif = jsonUsuario.getString("nif");
			Query<RowSet<Row>> query1 = mySqlClient
					.query("SELECT COUNT(*) as nUsuarios FROM Usuario WHERE nif = '" + nif + "';");
			query1.execute(res -> {
				JsonObject json = new JsonObject();
				if (res.succeeded()) {
					Row row = (Row) res.result().iterator().next();
					if (row.getInteger("nUsuarios") <= 0) {
						json.put("error", "Usuario no encontrado");
						message.reply(json);
					} else {
						jsonUsuario.remove(nif);
						String stringQuery = "UPDATE pastillero_dad.Usuario SET ";
						Iterator<Entry<String, Object>> iteratorJsonUsuario = jsonUsuario.iterator();
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
								json2.put(nif, "EDITADO EL USUARIO EL USUARIO  " + jsonUsuario.getString("firstname")
										+ " con DNI: " + nif);
								message.reply(json2);
							} else {
								json2.put("error", "ERROR AL EDITAR EL USUARIO CON NIF: " + nif + " ."
										+ String.valueOf(res.cause()));
								message.reply(json2);
							}
						});
					}
				} else {
					json.put("error",
							"ERROR AL EDITAR EL USUARIO CON NIF: " + nif + " ." + String.valueOf(res.cause()));
					message.reply(json);
				}
			});
		});
	}

}
