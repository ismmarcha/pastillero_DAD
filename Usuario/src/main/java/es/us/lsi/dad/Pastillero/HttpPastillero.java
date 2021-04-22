package es.us.lsi.dad.Pastillero;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class HttpPastillero {
Vertx vertx;
	
	public HttpPastillero(Vertx vertx) {
		this.vertx = vertx;
	}

	public void getPastilleros(RoutingContext routingContext) {
		// Enviamos petición al canal abierto del verticle BD y devolvemos una respuesta
		// a la petición REST. Así igual con el resto
		vertx.eventBus().request("getPastilleros", "getPastillero", reply -> {
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

	public void deletePastillero(RoutingContext routingContext) {
		// Obtenemos el id del usuario contenido en la propia URL de la petición
		String pastilleroId = routingContext.request().getParam("pastilleroid");
		vertx.eventBus().request("deletePastillero", pastilleroId, reply -> {
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

	public void addPastillero(RoutingContext routingContext) {
		// Añadimos un usuario utilizando los datos que están dentro del body de la
		// petición. IMPORTANTE: USAR EL BODY EN POSTMAN DE TIPO RAW
		vertx.eventBus().request("addPastillero", routingContext.getBodyAsString(), reply -> {
			if (reply.succeeded()) {
				routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.result().body()));
			} else {
				routingContext.response().setStatusCode(500).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.result().body()));
			}
		});
	}

	public void editPastillero(RoutingContext routingContext) {
		String pastilleroId = routingContext.request().getParam("pastilleroid");
		// Creamos un objeto JSON de los datos a modificar del usuario
		JsonObject json = routingContext.getBodyAsJson();
		// Añadimos a dicho JSON el userid para poder saber que usuario queremos
		// modificar.
		json.put("pastilleroId", pastilleroId);
		vertx.eventBus().request("editPastillero", json.toString(), reply -> {
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
