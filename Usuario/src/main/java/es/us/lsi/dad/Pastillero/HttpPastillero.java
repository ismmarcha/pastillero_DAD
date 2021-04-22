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
		// Enviamos petici�n al canal abierto del verticle BD y devolvemos una respuesta
		// a la petici�n REST. As� igual con el resto
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
		// Obtenemos el id del usuario contenido en la propia URL de la petici�n
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
		// A�adimos un usuario utilizando los datos que est�n dentro del body de la
		// petici�n. IMPORTANTE: USAR EL BODY EN POSTMAN DE TIPO RAW
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
		// A�adimos a dicho JSON el userid para poder saber que usuario queremos
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
