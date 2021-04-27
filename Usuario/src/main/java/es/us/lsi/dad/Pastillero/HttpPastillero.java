package es.us.lsi.dad.Pastillero;

import io.vertx.core.Vertx; 
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class HttpPastillero {
	Vertx vertx;

	public HttpPastillero(Vertx vertx) {
		this.vertx = vertx;
	}
	
	public void iniciarRouterPastillero(Router router) {
		router.route("/api/pastillero/*").handler(BodyHandler.create());
		router.get("/api/pastillero").handler(this::getAllPastillero);
		router.get("/api/pastillero/getPastilleroId").handler(this::getPastillero);
		router.post("/api/pastillero/addPastillero").handler(this::addPastillero);
		router.put("/api/pastillero/editPastillero").handler(this::editPastillero);
		router.delete("/api/pastillero").handler(this::deletePastillero);
	}
	
	public void getAllPastillero(RoutingContext routingContext) {
		// Enviamos petición al canal abierto del verticle BD y devolvemos una respuesta
		// a la petición REST. Así igual con el resto
		vertx.eventBus().request("getAllPastillero", "getAllPastillero", reply -> {
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

	public void getPastillero(RoutingContext routingContext) {
		// Enviamos petición al canal abierto del verticle BD y devolvemos una respuesta
		// a la petición REST. Así igual con el resto
		String datosPastillero = routingContext.getBodyAsString();
		vertx.eventBus().request("getPastillero", datosPastillero, reply -> {
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
		String datosPastillero = routingContext.getBodyAsString();
		vertx.eventBus().request("deletePastillero", datosPastillero, reply -> {
			if (reply.succeeded()) {
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
		String datosPastillero = routingContext.getBodyAsString();
		
		vertx.eventBus().request("editPastillero", datosPastillero, reply -> {
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
