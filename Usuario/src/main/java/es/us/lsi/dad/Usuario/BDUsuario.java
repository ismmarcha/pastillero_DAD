package es.us.lsi.dad.Usuario;

import java.util.Iterator;
import java.util.Map.Entry;

import es.us.lsi.dad.Pastilla.PastillaImpl;
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
						UsuarioImpl pastilla = new UsuarioImpl(v);
						json.put(String.valueOf(pastilla.getNif()), pastilla.getJson());
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
			Query<RowSet<Row>> query = mySqlClient.query("SELECT * FROM pastillero_dad.Usuario WHERE nif = '"+ usuarionif + "';");
			query.execute(res -> {
				JsonObject json = new JsonObject();
				
				if (res.succeeded()) {
					res.result().forEach(v -> {
						json.put(String.valueOf(v.getValue("nif")), v.toJson());
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
			String datosUsuario = message.body();
			JsonObject jsonUsuario = new JsonObject(datosUsuario);
			String nif = jsonUsuario.getString("nif");
			Query<RowSet<Row>> query = mySqlClient
					.query("DELETE FROM pastillero_dad.Usuario WHERE NIF = '" + nif + "';");
			query.execute(res -> {
				if (res.succeeded()) {
					message.reply("Borrado del usuario " + nif);
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
			String datosUsuario = message.body();
			JsonObject jsonUsuario= new JsonObject(datosUsuario) ; 
			String stringQuery = "INSERT INTO pastillero_dad.Usuario(nif,id_pastillero,firstname, lastname,contraseña, email, rol,id_cuidador) VALUES (";
			
			
			Iterator<Entry<String, Object>> iteratorJsonUsuario = jsonUsuario.iterator();
			while (iteratorJsonUsuario.hasNext()) {
				Entry<String, Object> elemento = iteratorJsonUsuario.next();
				stringQuery += "'" + elemento.getValue() + "'";
				if (iteratorJsonUsuario.hasNext()) {
					stringQuery += ", ";
				}
			}stringQuery += ");";
			
			System.out.println(stringQuery);
			
			Query<RowSet<Row>> query = mySqlClient
					.query(stringQuery);
			query.execute(res -> {
				if (res.succeeded()) {
					message.reply("Añadido el usuario " + jsonUsuario.getString("firstname"));
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
			String datosUsuario= message.body();
			JsonObject jsonUsuario = new JsonObject(datosUsuario);
			
			String nif = jsonUsuario.getString("nif");
			String stringQuery = "UPDATE pastillero_dad.Usuario SET ";
			
			System.out.println(jsonUsuario);
			
			Iterator<Entry<String, Object>> iteratorJsonUsuario = jsonUsuario.iterator();
			while (iteratorJsonUsuario.hasNext()) {
				Entry<String, Object> elemento = iteratorJsonUsuario.next();
				stringQuery += elemento.getKey() + " = '" + elemento.getValue() + "'";
				if (iteratorJsonUsuario.hasNext()) {
					stringQuery += ", ";
				}
			}
			stringQuery += "WHERE nif = '"
					+ nif+ "';";
			
			System.out.println(stringQuery);
			Query<RowSet<Row>> query = mySqlClient.query(stringQuery);
			
			query.execute(res -> {
				if (res.succeeded()) {
					message.reply("Editado el usuario " );
				} else {
					message.reply("ERROR AL EDITAR EL USUARIO " + res.cause());
				}
				;
			});
		});
	}
}
