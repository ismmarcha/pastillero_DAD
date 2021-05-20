package es.us.lsi.dad.Pastilla;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class HttpPastilla {
	Vertx vertx;

	public HttpPastilla(Vertx vertx) {
		this.vertx = vertx;
	}

	public void iniciarRouterPastilla(Router router) {
		router.route("/api/pastillas/*").handler(BodyHandler.create());
		router.get("/api/pastillas").handler(this::getAllPastilla);
		router.get("/api/pastillas/getPastilla").handler(this::getPastilla);
		router.post("/api/pastillas/addPastilla").handler(this::addPastilla);
		router.put("/api/pastillas/editPastilla").handler(this::editPastilla);
		router.delete("/api/pastillas").handler(this::deletePastilla);

		router.get("/api/pastillas/getPastillaPorDosis").handler(this::getPastillaPorDosis);
		router.post("/api/pastillas/addPastillaPorDosis").handler(this::addPastillaPorDosis);
		router.put("/api/pastillas/editPastillaPorDosis").handler(this::editPastillaPorDosis);
		router.delete("/api/pastillas/deletePastillaPorDosis").handler(this::deletePastillaPorDosis);

		router.get("/api/pastillas/getPastillasPorUsuario").handler(this::getPastillasPorUsuario);
	}

	public void getAllPastilla(RoutingContext routingContext) {

		vertx.eventBus().request("getAllPastilla", "getAllPastilla", reply -> {
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

	public void getPastilla(RoutingContext routingContext) {
		// Enviamos petici�n al canal abierto del verticle BD y devolvemos una respuesta
		// a la petici�n REST. As� igual con el resto
		String datosPastilla = routingContext.getBodyAsString();
		vertx.eventBus().request("getPastilla", datosPastilla, reply -> {
			if (reply.succeeded()) {
				System.out.println(reply.result().body());
				routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.result().body()));
			} else {
				routingContext.response().setStatusCode(500).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.cause().getMessage()));
			}
		});
	}

	public void deletePastilla(RoutingContext routingContext) {
		// Obtenemos el id del usuario contenido en la propia URL de la petici�n
		String datosPastilla = routingContext.getBodyAsString();
		vertx.eventBus().request("deletePastilla", datosPastilla, reply -> {
			if (reply.succeeded()) {
				routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.result().body()));
			} else {
				routingContext.response().setStatusCode(500).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.cause().getMessage()));
			}
		});
	}

	public void addPastilla(RoutingContext routingContext) {
		// A�adimos un usuario utilizando los datos que est�n dentro del body de la
		// petici�n. IMPORTANTE: USAR EL BODY EN POSTMAN DE TIPO RAW
		String datosPastilla = routingContext.getBodyAsString();
		vertx.eventBus().request("addPastilla", datosPastilla, reply -> {
			if (reply.succeeded()) {
				routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.result().body()));
			} else {
				routingContext.response().setStatusCode(500).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.cause().getMessage()));
			}
		});
	}

	public void editPastilla(RoutingContext routingContext) {
		String datosPastilla = routingContext.getBodyAsString();
		vertx.eventBus().request("editPastilla", datosPastilla, reply -> {
			if (reply.succeeded()) {
				System.out.println(reply.result().body());
				routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.result().body()));
			} else {
				routingContext.response().setStatusCode(500).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.cause().getMessage()));
			}
		});
	}

	public void getPastillaPorDosis(RoutingContext routingContext) {
		String datosPastilla = routingContext.getBodyAsString();
		vertx.eventBus().request("getPastillaPorDosis", datosPastilla, reply -> {
			if (reply.succeeded()) {
				System.out.println(reply.result().body());
				routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.result().body()));
			} else {
				routingContext.response().setStatusCode(500).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.cause().getMessage()));
			}
		});
	}

	public void addPastillaPorDosis(RoutingContext routingContext) {
		// Enviamos petici�n al canal abierto del verticle BD y devolvemos una respuesta
		// a la petici�n REST. As� igual con el resto
		String datosDosis = routingContext.getBodyAsString();
		vertx.eventBus().request("addPastillaPorDosis", datosDosis, reply -> {
			if (reply.succeeded()) {
				System.out.println(reply.result().body());
				routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.result().body()));
			} else {
				routingContext.response().setStatusCode(500).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.cause().getMessage()));
			}
		});
	}

	public void deletePastillaPorDosis(RoutingContext routingContext) {
		// Obtenemos el id del usuario contenido en la propia URL de la petici�n
		String datosPastilla = routingContext.getBodyAsString();
		vertx.eventBus().request("deletePastillaPorDosis", datosPastilla, reply -> {
			if (reply.succeeded()) {
				routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.result().body()));
			} else {
				routingContext.response().setStatusCode(500).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.cause().getMessage()));
			}
		});
	}

	public void editPastillaPorDosis(RoutingContext routingContext) {
		String datosPastilla = routingContext.getBodyAsString();
		vertx.eventBus().request("editPastillaPorDosis", datosPastilla, reply -> {
			if (reply.succeeded()) {
				System.out.println(reply.result().body());
				routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.result().body()));
			} else {
				routingContext.response().setStatusCode(500).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.cause().getMessage()));
			}
		});
	}

	public void getPastillasPorUsuario(RoutingContext routingContext) {
		// Enviamos petici�n al canal abierto del verticle BD y devolvemos una respuesta
		// a la petici�n REST. As� igual con el resto
		String datosUsuario = routingContext.getBodyAsString();
		vertx.eventBus().request("getPastillasPorUsuario", datosUsuario, reply -> {
			if (reply.succeeded()) {
				System.out.println(reply.result().body());
				routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.result().body()));
			} else {
				routingContext.response().setStatusCode(500).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.cause().getMessage()));
			}
		});

	}
}
