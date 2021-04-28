package es.us.lsi.dad.Pastilla;

import java.util.Iterator;
import java.util.Map.Entry;

import es.us.lsi.dad.Dosis.DosisImpl;
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
					resultadoJson.put("error", String.valueOf(res.cause()));
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
			int Id_pastilla = jsonPastilla.getInteger("Id_pastilla");
			Query<RowSet<Row>> query = mySqlClient
					.query("SELECT * FROM pastillero_dad.Pastilla WHERE Id_pastilla = " + Id_pastilla + ";");
			query.execute(res -> {
				JsonObject resultadoJson = new JsonObject();
				if (res.succeeded()) {
					res.result().forEach(v -> {
						PastillaImpl pastilla = new PastillaImpl(v);
						resultadoJson.put(String.valueOf(pastilla.getId_pastilla()), pastilla.getJson());
					});
				} else {
					resultadoJson.put("error", String.valueOf(res.cause()));
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
			int Id_pastilla = jsonPastilla.getInteger("Id_pastilla");
			Query<RowSet<Row>> query = mySqlClient
					.query("DELETE FROM pastillero_dad.Pastilla WHERE Id_pastilla = " + Id_pastilla + ";");
			query.execute(res -> {
				if (res.succeeded()) {
					message.reply("Borrado la Pastilla " + Id_pastilla + " .");
				} else {
					message.reply("ERROR AL BORRAR LA PASTILLA " + res.cause());
				}
				;
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
				if (res.succeeded()) {
					message.reply("Añadida la pastilla " + pastilla.getNombre() + " " + pastilla.getPeso() + " .");
				} else {
					message.reply("ERROR AL AÑADIR LA PASTILLA " + res.cause());
				}
				;
			});
		});
	}

	public void editPastilla() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("editPastilla");
		consumer.handler(message -> {

			String datosPastilla = message.body();
			JsonObject jsonPastilla = new JsonObject(datosPastilla);

			String id_pastilla = jsonPastilla.getString("id_pastilla");
			jsonPastilla.remove(id_pastilla);
			String stringQuery = "UPDATE pastillero_dad.Pastilla SET ";

			Iterator<Entry<String, Object>> iteratorJsonPastilla = jsonPastilla.iterator();
			while (iteratorJsonPastilla.hasNext()) {
				Entry<String, Object> elemento = iteratorJsonPastilla.next();
				if (elemento.getValue() == null || elemento.getValue() instanceof Number) {
					stringQuery += elemento.getValue();
				} else {
					stringQuery += "'" + elemento.getValue() + "'";
				}
				if (iteratorJsonPastilla.hasNext()) {
					stringQuery += ", ";
				}
			}
			stringQuery += "WHERE id_pastilla = '" + id_pastilla + "';";

			System.out.println(stringQuery);
			Query<RowSet<Row>> query = mySqlClient.query(stringQuery);
			query.execute(res -> {
				if (res.succeeded()) {
					message.reply("Editada la Pastilla");
				} else {
					message.reply("ERROR AL EDITAR LA PASTILLA " + res.cause());
				}
				;
			});
		});
	}
	
	public void getPastillaPorDosis() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("getPastillaPorDosis");
		consumer.handler(message -> {
			String datosPastilla = message.body();
			JsonObject jsonPastilla = new JsonObject(datosPastilla);
			int Id_dosis = jsonPastilla.getInteger("id_dosis");
			System.out.println("HOLA");
			Query<RowSet<Row>> query = mySqlClient
					.query("SELECT pastillero_dad.Pastilla.id_pastilla ,nombre ,descripcion ,peso FROM pastillero_dad.Pastilla LEFT JOIN pastillero_dad.Pastilla_Dosis "
							+ "ON Pastilla.id_pastilla = pastilla_dosis.id_pastilla WHERE pastilla_dosis.id_pastilla ="+ Id_dosis + ";");
			query.execute(res -> {
				JsonObject resultadoJson = new JsonObject();
				if (res.succeeded()) {
					res.result().forEach(v -> {
						PastillaImpl pastilla = new PastillaImpl(v);
						resultadoJson.put(String.valueOf(pastilla.getId_pastilla()), pastilla.getJson());
					});
				} else {
					resultadoJson.put("error", String.valueOf(res.cause()));
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

			int id_pastilla= jsonPastillaDosis.getInteger("id_pastilla");
			int id_dosis = jsonPastillaDosis.getInteger("id_dosis");
			double cantidad = jsonPastillaDosis.getDouble("cantidad");
			
			Query<RowSet<Row>> query = mySqlClient.query("INSERT INTO Pastilla_Dosis (id_pastilla,id_dosis,cantidad)" + " VALUES('"
					+ id_pastilla + "','" + id_dosis + "'," + cantidad + ");" );
		
			query.execute(res -> {
				if (res.succeeded()) {
					message.reply("Añadida la pastilla " + id_pastilla + " a la dosis " + id_dosis + " .");
				} else {
					message.reply("ERROR AL AÑADIR LA PASTILLA " + res.cause());
				}
				;
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
			
			Query<RowSet<Row>> query = mySqlClient
					.query("DELETE FROM pastillero_dad.Pastilla_Dosis WHERE Id_pastilla = " + Id_pastilla + " AND Id_Dosis =" + Id_dosis + " ;");
			query.execute(res -> {
				if (res.succeeded()) {
					message.reply("Borrado la Pastilla " + Id_pastilla + " de la dosis " + Id_dosis + " .");
				} else {
					message.reply("ERROR AL BORRAR LA PASTILLA " + res.cause());
				}
				;
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
			stringQuery += " WHERE id_pastilla = " + id_pastilla + " AND id_dosis = "+ id_dosis +" ;";

			System.out.println(stringQuery);
			Query<RowSet<Row>> query = mySqlClient.query(stringQuery);
			query.execute(res -> {
				if (res.succeeded()) {
					message.reply("Editada la Pastilla" + id_pastilla +" en la Dosis "+ id_dosis);
				} else {
					message.reply("ERROR AL EDITAR LA PASTILLA " + res.cause());
				}
				;
			});
		});
	}
	
	
	
	public void getPastillasPorUsuario() {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("getPastillasPorUsuario");
		consumer.handler(message -> {
			String datosUsuario = message.body();
			JsonObject jsonPastilla = new JsonObject(datosUsuario);
			String nif = jsonPastilla.getString("nif");
			Query<RowSet<Row>> query = mySqlClient
					.query("QUERY DEL INFIERNO POR ESCRIBIR);
			query.execute(res -> {
				JsonObject resultadoJson = new JsonObject();
				if (res.succeeded()) {
					res.result().forEach(v -> {
						PastillaImpl pastilla = new PastillaImpl(v);
						resultadoJson.put(String.valueOf(pastilla.getId_pastilla()), pastilla.getJson());
					});
				} else {
					resultadoJson.put("error", String.valueOf(res.cause()));
				}
				message.reply(resultadoJson);
			});
		});
	}
}
