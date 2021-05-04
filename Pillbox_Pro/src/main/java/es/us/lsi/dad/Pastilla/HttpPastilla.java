package es.us.lsi.dad.Pastilla;

import io.vertx.core.Vertx;  
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;


//1 . Pastillas que hay en una dosis
//9. Todas las pastillas de un usuario
//

public class HttpPastilla {
	Vertx vertx;
	
	public HttpPastilla(Vertx vertx) {
		this.vertx = vertx;
	}
	
	public void iniciarRouterPastilla(Router router) {
		router.route("/api/pastilla/*").handler(BodyHandler.create());
		router.get("/api/pastilla").handler(this::getAllPastilla);
		router.get("/api/pastilla/getPastilla").handler(this::getPastilla);
		router.post("/api/pastilla/addPastilla").handler(this::addPastilla);
		router.put("/api/pastilla/editPastilla").handler(this::editPastilla);
		router.delete("/api/pastilla").handler(this::deletePastilla);
		
		router.get("/api/pastilla/getPastillaPorDosis").handler(this::getPastillaPorDosis);
		router.post("/api/pastilla/addPastillaPorDosis").handler(this::addPastillaPorDosis);
		router.put("/api/pastilla/editPastillaPorDosis").handler(this::editPastillaPorDosis);
		router.delete("/api/pastilla/deletePastillaPorDosis").handler(this::deletePastillaPorDosis);
		
		router.get("/api/pastilla/getPastillasPorUsuario").handler(this::getPastillasPorUsuario);
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
		// Enviamos petición al canal abierto del verticle BD y devolvemos una respuesta
		// a la petición REST. Así igual con el resto
		String datosPastilla = routingContext.getBodyAsString();
		vertx.eventBus().request("getPastilla", datosPastilla, reply -> {
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
	

	
	public void deletePastilla(RoutingContext routingContext) {
		// Obtenemos el id del usuario contenido en la propia URL de la petición
		String datosPastilla = routingContext.getBodyAsString();
		vertx.eventBus().request("deletePastilla", datosPastilla, reply -> {
			if (reply.succeeded()) {
				routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.result().body()));
			} else {
				routingContext.response().setStatusCode(500).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.result().body()));
			}
		});
	}
	
	public void addPastilla(RoutingContext routingContext) {
		// Añadimos un usuario utilizando los datos que están dentro del body de la
		// petición. IMPORTANTE: USAR EL BODY EN POSTMAN DE TIPO RAW
		String datosPastilla = routingContext.getBodyAsString();
		vertx.eventBus().request("addPastilla", datosPastilla, reply -> {
			if (reply.succeeded()) {
				routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.result().body()));
			} else {
				routingContext.response().setStatusCode(500).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.result().body()));
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
						.end(String.valueOf(reply.result().body()));
			}
		});
	}
	
	public void getPastillaPorDosis(RoutingContext routingContext) {
		System.out.println("HOLA345");
		String datosPastilla = routingContext.getBodyAsString();
		vertx.eventBus().request("getPastillaPorDosis", datosPastilla, reply -> {
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
	
	public void addPastillaPorDosis(RoutingContext routingContext) {
		// Enviamos petición al canal abierto del verticle BD y devolvemos una respuesta
		// a la petición REST. Así igual con el resto
		String datosDosis = routingContext.getBodyAsString();
		vertx.eventBus().request("addPastillaPorDosis", datosDosis, reply -> {
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
	
	public void deletePastillaPorDosis(RoutingContext routingContext) {
		// Obtenemos el id del usuario contenido en la propia URL de la petición
		String datosPastilla = routingContext.getBodyAsString();
		vertx.eventBus().request("deletePastillaPorDosis", datosPastilla, reply -> {
			if (reply.succeeded()) {
				routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.result().body()));
			} else {
				routingContext.response().setStatusCode(500).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.result().body()));
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
						.end(String.valueOf(reply.result().body()));
			}
		});
	}
	
	
	public void getPastillasPorUsuario(RoutingContext routingContext) {
		// Enviamos petición al canal abierto del verticle BD y devolvemos una respuesta
		// a la petición REST. Así igual con el resto
		String datosUsuario = routingContext.getBodyAsString();
		vertx.eventBus().request("getPastillasPorUsuario", datosUsuario, reply -> {
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
