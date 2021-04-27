package es.us.lsi.dad.Dosis;

import io.vertx.core.Vertx; 
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;


//2. Dosis por usuario 
//3. Siguiente dosis por usuario
//4. Dosis por día y por usuario
//5. Agregar pastillas a una dosis (pastilla dosis)
//6. Registro Dosis ( cuando dosis tomada)  PENSAR (AÑADIR ID USUARIO A  REGISTRO_DOSIS)


public class HttpDosis {
	Vertx vertx;

	public HttpDosis(Vertx vertx) {
		this.vertx = vertx;
	}
	
	public void iniciarRouterDosis(Router router) {
		router.route("/api/dosis/*").handler(BodyHandler.create());
		router.get("/api/dosis").handler(this::getAllDosis);
		router.get("/api/dosis/getDosis").handler(this::getDosis);
		router.post("/api/dosis/addDosis").handler(this::addDosis);
		router.put("/api/dosis/editDosis").handler(this::editDosis);
		router.delete("/api/dosis").handler(this::deleteDosis);
	}

	public void getAllDosis(RoutingContext routingContext) {
		// Enviamos petición al canal abierto del verticle BD y devolvemos una respuesta
		// a la petición REST. Así igual con el resto
		vertx.eventBus().request("getAllDosis", "getAllDosis", reply -> {
			if (reply.succeeded()) {
				routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.result().body()));
			} else {
				routingContext.response().setStatusCode(500).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.result().body()));
			}
		});
	}

	public void getDosis(RoutingContext routingContext) {
		// Enviamos petición al canal abierto del verticle BD y devolvemos una respuesta
		// a la petición REST. Así igual con el resto
		String datosDosis = routingContext.getBodyAsString();
		vertx.eventBus().request("getDosis", datosDosis, reply -> {
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

	public void deleteDosis(RoutingContext routingContext) {
		// Obtenemos el id del usuario contenido en la propia URL de la petición
		String datosDosis = routingContext.getBodyAsString();
		vertx.eventBus().request("deleteDosis", datosDosis, reply -> {
			if (reply.succeeded()) {
				routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.result().body()));
			} else {
				routingContext.response().setStatusCode(500).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.result().body()));
			}
		});
	}

	public void addDosis(RoutingContext routingContext) {
		// Añadimos un usuario utilizando los datos que están dentro del body de la
		// petición. IMPORTANTE: USAR EL BODY EN POSTMAN DE TIPO RAW
		String datosDosis = routingContext.getBodyAsString();
		vertx.eventBus().request("addDosis", datosDosis, reply -> {
			if (reply.succeeded()) {
				routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.result().body()));
			} else {
				routingContext.response().setStatusCode(500).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.result().body()));
			}
		});
	}

	public void editDosis(RoutingContext routingContext) {
		String datosDosis = routingContext.getBodyAsString();
		vertx.eventBus().request("editDosis", datosDosis, reply -> {
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
