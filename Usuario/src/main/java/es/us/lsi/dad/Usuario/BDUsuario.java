package es.us.lsi.dad.Usuario;

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
		getUsuarios();
		getUsuarioNIF();
		deleteUsuario();
		addUsuario();
		editUsuario();
	}
	
	public void getUsuarios() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("getUsuarios");
		consumer.handler(message -> {
			Query<RowSet<Row>> query = mySqlClient.query("SELECT * FROM pastillero_dad.Usuario;");
			query.execute(res -> {
				JsonObject json = new JsonObject();
				if (res.succeeded()) {
					res.result().forEach(v -> {
						UsuarioImpl usuario = new UsuarioImpl();
						usuario.setName((String) v.getValue("firstname"));
						usuario.setSurname((String) v.getValue("lastname"));
						String nif = "" + v.getValue("nif");
						System.out.println(json);
						json.put(String.valueOf(v.getValue("nif")), v.toJson());
					});
				} else {
					json.put("error", String.valueOf(res.cause()));
				}
				message.reply(json);
			});
		});
	}
	
	public void getUsuarioNIF() {
		
		MessageConsumer<String> consumer = vertx.eventBus().consumer("getUsuarioNIF");
		consumer.handler(message -> {
			String usuarionif = message.body();
			Query<RowSet<Row>> query = mySqlClient.query("SELECT * FROM pastillero_dad.Usuario WHERE NIF = '"+ usuarionif + "';");
			
			query.execute(res -> {
				JsonObject json = new JsonObject();
				
				if (res.succeeded()) {
					res.result().forEach(v -> {
						json.put(String.valueOf(v.getValue("NIF")), v.toJson());
					});
				} else {
					json.put("No existe usuario con ese NIF", String.valueOf(res.cause()));
					
				}
				message.reply(json);
			});
		});
	}
	
	public void deleteUsuario() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("deleteUsuario");
		consumer.handler(message -> {
			String usuarionif = message.body();
			Query<RowSet<Row>> query = mySqlClient
					.query("DELETE FROM pastillero_dad.Usuario WHERE NIF = '" + usuarionif + "';");
			query.execute(res -> {
				if (res.succeeded()) {
					message.reply("Borrado del usuario " + usuarionif);
				} else {
					message.reply("ERROR AL BORRAR EL USUARIO " + res.cause());
				}
				;
			});
		});
	}

	public void addUsuario() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("addUsuario");
		consumer.handler(message -> {
			JsonObject jsonNewUsuario = new JsonObject(message.body());
			UsuarioImpl newUsuario = new UsuarioImpl();
			newUsuario.setName(jsonNewUsuario.getString("firstname"));
			newUsuario.setSurname(jsonNewUsuario.getString("lastname"));
			Query<RowSet<Row>> query = mySqlClient
					.query("INSERT INTO pastillero_dad.Usuario(firstname, lastname) VALUES ('" + newUsuario.getName()
							+ "','" + newUsuario.getSurname() + "');");
			query.execute(res -> {
				if (res.succeeded()) {
					message.reply("Añadido el usuario " + newUsuario.getName());
				} else {
					message.reply("ERROR AL AÑADIR EL USUARIO " + res.cause());
				}
				;
			});
		});
	}

	public void editUsuario() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("editUsuario");
		consumer.handler(message -> {
			System.out.println("editusuario " + message.body());
			JsonObject jsonEditUsuario = new JsonObject(message.body());
			String usuarionif = jsonEditUsuario.getString("usuarionif");
			UsuarioImpl editUsuario = new UsuarioImpl();
			editUsuario.setName(jsonEditUsuario.getString("firstname"));
			editUsuario.setSurname(jsonEditUsuario.getString("lastname"));
			Query<RowSet<Row>> query = mySqlClient.query("UPDATE pastillero_dad.Usuario SET firstname = '"
					+ editUsuario.getName() + "', lastname = '" + editUsuario.getSurname() + "' WHERE NIF = " + usuarionif);
			query.execute(res -> {
				if (res.succeeded()) {
					message.reply("Editado el usuario " + editUsuario.getName());
				} else {
					message.reply("ERROR AL EDITAR EL USUARIO " + res.cause());
				}
				;
			});
		});
	}
}
