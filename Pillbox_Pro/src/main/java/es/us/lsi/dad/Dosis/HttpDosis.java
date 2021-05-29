package es.us.lsi.dad.Dosis;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class HttpDosis {
	Vertx vertx;

	public HttpDosis(Vertx vertx) {
		this.vertx = vertx;
	}

	public void iniciarRouterDosis(Router router) {
		router.route("/api/dosis/*").handler(BodyHandler.create());
		router.get("/api/dosis").handler(this::getAllDosis);
		router.get("/api/dosis/getDosis").handler(this::getDosis);
		router.get("/api/dosis/getDosisPorUsuario").handler(this::getDosisPorUsuario);
		router.get("/api/dosis/getDosisPorUsuarioYDia").handler(this::getDosisPorUsuarioYDia);
		router.get("/api/dosis/getDosisPorUsuarioGroupByDia").handler(this::getDosisPorUsuarioGroupByDia);
		router.get("/api/dosis/getSiguienteDosisPorUsuario").handler(this::getSiguienteDosisPorUsuario);
		router.get("/api/dosis/getDosisPorPastillero").handler(this::getDosisPorPastillero);
		router.post("/api/dosis/addDosis").handler(this::addDosis);
		router.put("/api/dosis/editDosis").handler(this::editDosis);
		router.delete("/api/dosis").handler(this::deleteDosis);

		router.get("/api/dosis/getAllRegistroDosis").handler(this::getAllRegistroDosis);
		router.post("/api/dosis/addRegistroDosis").handler(this::addRegistroDosis);
		router.delete("/api/dosis/deleteRegistroDosis").handler(this::deleteRegistroDosis);
		router.put("/api/dosis/editRegistroDosis").handler(this::editRegistroDosis);
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
		if (datosDosis == null) {
			datosDosis = "{}";
		}

		vertx.eventBus().request("getDosis", datosDosis, reply -> {
			if (reply.succeeded()) {
				routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.result().body()));
			} else {
				routingContext.response().setStatusCode(500).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.cause().getMessage()));
			}
		});

	}

	public void getDosisPorUsuario(RoutingContext routingContext) {
		// Enviamos petición al canal abierto del verticle BD y devolvemos una respuesta
		// a la petición REST. Así igual con el resto
		vertx.eventBus().request("getDosisPorUsuario", routingContext.getBodyAsString(), reply -> {
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

	public void getDosisPorUsuarioYDia(RoutingContext routingContext) {
		// Enviamos petición al canal abierto del verticle BD y devolvemos una respuesta
		// a la petición REST. Así igual con el resto
		vertx.eventBus().request("getDosisPorUsuarioYDia", routingContext.getBodyAsString(), reply -> {
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

	public void getDosisPorUsuarioGroupByDia(RoutingContext routingContext) {
		// Enviamos petición al canal abierto del verticle BD y devolvemos una respuesta
		// a la petición REST. Así igual con el resto
		vertx.eventBus().request("getDosisPorUsuarioGroupByDia", routingContext.getBodyAsString().toString(), reply -> {
			if (reply.succeeded()) {
				System.out.println(reply.result().body());
				JsonObject jsonReply = new JsonObject(String.valueOf(reply.result().body()));
				JsonObject jsonRes = new JsonObject();
				jsonReply.forEach(elem -> {
					JsonArray elemArray;
					JsonObject elemValue = new JsonObject(String.valueOf(elem.getValue()));
					String elemDiaSemana = String.valueOf(elemValue.getInteger("dia_semana"));
					if (jsonRes.containsKey(elemDiaSemana)) {
						elemArray = (JsonArray) jsonRes.getValue(elemDiaSemana);
					} else {
						elemArray = new JsonArray();
					}
					elemArray.add(elemValue);
					jsonRes.put(String.valueOf(elemValue.getInteger("dia_semana")), elemArray);
				});
				routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(String.valueOf(jsonRes));
			} else {
				routingContext.response().setStatusCode(500).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.cause().getMessage()));
			}
		});
	}

	public void getSiguienteDosisPorUsuario(RoutingContext routingContext) {
		// Enviamos petición al canal abierto del verticle BD y devolvemos una respuesta
		// a la petición REST. Así igual con el resto
		vertx.eventBus().request("getSiguienteDosisPorUsuario", routingContext.getBodyAsString(), reply -> {
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

	public void getDosisPorPastillero(RoutingContext routingContext) {
		vertx.eventBus().request("getDosisPorPastillero", routingContext.getBodyAsString(), reply -> {
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

	
	public void deleteDosis(RoutingContext routingContext) {
		// Obtenemos el id del usuario contenido en la propia URL de la petición
		String datosDosis = routingContext.getBodyAsString();
		vertx.eventBus().request("deleteDosis", datosDosis, reply -> {
			if (reply.succeeded()) {
				routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.result().body()));
			} else {
				routingContext.response().setStatusCode(500).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.cause().getMessage()));
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
						.end(String.valueOf(reply.cause().getMessage()));
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
						.end(String.valueOf(reply.cause().getMessage()));
			}
		});
	}

	public void getAllRegistroDosis(RoutingContext routingContext) {

		String datosUsuario = routingContext.getBodyAsString();
		vertx.eventBus().request("getAllRegistroDosis", datosUsuario, reply -> {
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

	public void addRegistroDosis(RoutingContext routingContext) {

		String datosDosis = routingContext.getBodyAsString();

		vertx.eventBus().request("addRegistroDosis", datosDosis, reply -> {
			if (reply.succeeded()) {
				routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.result().body()));
			} else {
				routingContext.response().setStatusCode(500).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.cause().getMessage()));
			}
		});
	}

	public void deleteRegistroDosis(RoutingContext routingContext) {
		// Obtenemos el id del usuario contenido en la propia URL de la petición
		String datosDosis = routingContext.getBodyAsString();
		vertx.eventBus().request("deleteRegistroDosis", datosDosis, reply -> {
			if (reply.succeeded()) {
				routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.result().body()));
			} else {
				routingContext.response().setStatusCode(500).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.cause().getMessage()));
			}
		});
	}

	public void editRegistroDosis(RoutingContext routingContext) {
		String datosDosis = routingContext.getBodyAsString();
		vertx.eventBus().request("editRegistroDosis", datosDosis, reply -> {
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
