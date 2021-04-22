package es.us.lsi.dad.Usuario;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class HttpUsuario {
	Vertx vertx;
	
	public HttpUsuario(Vertx vertx) {
		this.vertx = vertx;
	}

	public void getUsers(RoutingContext routingContext) {
		// Enviamos petici�n al canal abierto del verticle BD y devolvemos una respuesta
		// a la petici�n REST. As� igual con el resto
		vertx.eventBus().request("getUsers", "getUsers", reply -> {
			if (reply.succeeded()) {
				System.out.println(reply.result().body());
				routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.result().body()));
			} else {
				routingContext.response().setStatusCode(500).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.result().body()));
			}
		});
	}

	public void deleteUser(RoutingContext routingContext) {
		// Obtenemos el id del usuario contenido en la propia URL de la petici�n
		String userid = routingContext.request().getParam("userid");
		vertx.eventBus().request("deleteUser", userid, reply -> {
			if (reply.succeeded()) {
				System.out.println(reply.result().body());
				routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.result().body()));
			} else {
				routingContext.response().setStatusCode(500).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.result().body()));
			}
		});
	}

	public void addUser(RoutingContext routingContext) {
		// A�adimos un usuario utilizando los datos que est�n dentro del body de la
		// petici�n. IMPORTANTE: USAR EL BODY EN POSTMAN DE TIPO RAW
		vertx.eventBus().request("addUser", routingContext.getBodyAsString(), reply -> {
			if (reply.succeeded()) {
				System.out.println(reply.result().body());
				routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.result().body()));
			} else {
				routingContext.response().setStatusCode(500).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.result().body()));
			}
		});
	}

	public void editUser(RoutingContext routingContext) {
		String userid = routingContext.request().getParam("userid");
		// Creamos un objeto JSON de los datos a modificar del usuario
		JsonObject json = routingContext.getBodyAsJson();
		// A�adimos a dicho JSON el userid para poder saber que usuario queremos
		// modificar.
		json.put("userid", userid);
		vertx.eventBus().request("editUser", json.toString(), reply -> {
			if (reply.succeeded()) {
				System.out.println(reply.result().body());
				routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.result().body()));
			} else {
				routingContext.response().setStatusCode(500).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.result().body()));
			}
		});
	}
}
