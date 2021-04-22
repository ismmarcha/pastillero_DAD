package es.us.lsi.dad;

import com.google.gson.Gson;

import es.us.lsi.dad.Usuario.BDUsuario;
import es.us.lsi.dad.Usuario.UsuarioImpl;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Query;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

public class BDVerticle extends AbstractVerticle {

	MySQLPool mySqlClient;
	Gson gson;

	@Override
	public void start(Promise<Void> startFuture) {
		gson = new Gson();
		MySQLConnectOptions connectOptions = new MySQLConnectOptions().setPort(3306).setHost("localhost")
				.setDatabase("pastillero_dad").setUser("root").setPassword("123456");

		PoolOptions poolOptions = new PoolOptions().setMaxSize(5);

		mySqlClient = MySQLPool.pool(vertx, connectOptions, poolOptions);

		BDUsuario bdUsuario = new BDUsuario(vertx, mySqlClient);

		bdUsuario.iniciarConsumersBDUsuario();
		
		startFuture.complete();

	}

	/*private void getUsers() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("getUsers");
		consumer.handler(message -> {
			Query<RowSet<Row>> query = mySqlClient.query("SELECT * FROM pastillero_dad.Usuario;");
			query.execute(res -> {
				JsonObject json = new JsonObject();
				if (res.succeeded()) {
					res.result().forEach(v -> {
						UsuarioImpl usuario = new UsuarioImpl();
						usuario.setName((String) v.getValue("firstname"));
						usuario.setSurname((String) v.getValue("lastname"));
						String id = "" + v.getValue("id");
						System.out.println(json);
						json.put(String.valueOf(v.getValue("id")), v.toJson());
					});
				} else {
					json.put("error", String.valueOf(res.cause()));
				}
				message.reply(json);
			});
		});
	}

	private void deleteUser() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("deleteUser");
		consumer.handler(message -> {
			String userid = message.body();
			Query<RowSet<Row>> query = mySqlClient
					.query("DELETE FROM pastillero_dad.Usuario WHERE id = " + userid + ";");
			query.execute(res -> {
				if (res.succeeded()) {
					message.reply("Borrado del usuario " + userid);
				} else {
					message.reply("ERROR AL BORRAR EL USUARIO "+res.cause());
				}
				;
			});
		});
	}

	private void addUser() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("addUser");
		consumer.handler(message -> {
			JsonObject jsonNewUsuario = new JsonObject(message.body());
			UsuarioImpl newUser = new UsuarioImpl();
			newUser.setName(jsonNewUsuario.getString("firstname"));
			newUser.setSurname(jsonNewUsuario.getString("lastname"));
			Query<RowSet<Row>> query = mySqlClient
					.query("INSERT INTO pastillero_dad.Usuario(firstname, lastname) VALUES ('" + newUser.getName()
							+ "','" + newUser.getSurname() + "');");
			query.execute(res -> {
				if (res.succeeded()) {
					message.reply("Añadido el usuario " + newUser.getName());
				} else {
					message.reply("ERROR AL AÑADIR EL USUARIO " + res.cause());
				}
				;
			});
		});
	}

	private void editUser() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("editUser");
		consumer.handler(message -> {
			System.out.println("edituser " + message.body());
			JsonObject jsonEditUsuario = new JsonObject(message.body());
			String userid = jsonEditUsuario.getString("userid");
			UsuarioImpl editUser = new UsuarioImpl();
			editUser.setName(jsonEditUsuario.getString("firstname"));
			editUser.setSurname(jsonEditUsuario.getString("lastname"));
			Query<RowSet<Row>> query = mySqlClient.query("UPDATE pastillero_dad.Usuario SET firstname = '"
					+ editUser.getName() + "', lastname = '" + editUser.getSurname() + "' WHERE id = " + userid);
			query.execute(res -> {
				if (res.succeeded()) {
					message.reply("Editado el usuario " + editUser.getName());
				} else {
					message.reply("ERROR AL EDITAR EL USUARIO " + res.cause());
				}
				;
			});
		});
	}*/
	
	
}