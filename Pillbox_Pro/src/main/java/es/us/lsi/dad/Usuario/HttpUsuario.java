package es.us.lsi.dad.Usuario;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class HttpUsuario {
	Vertx vertx;

	public HttpUsuario(Vertx vertx) {
		this.vertx = vertx;
	}

	public void iniciarRouterUsuario(Router router) {
		router.route("/api/usuarios/*").handler(BodyHandler.create());
		router.get("/api/usuarios").handler(this::getAllUsuarios);
		router.get("/api/usuarios/getUsuarioNif").handler(this::getUsuarioNIF);
		router.get("/api/usuarios/getEnfermosPorCuidador").handler(this::getEnfermosPorCuidador);

		router.post("/api/usuarios/addUsuario").handler(this::addUsuario);
		router.put("/api/usuarios/editUsuario").handler(this::editUsuario);
		router.delete("/api/usuarios").handler(this::deleteUsuario);
	}

	public void getAllUsuarios(RoutingContext routingContext) {
		// Enviamos petici?n al canal abierto del verticle BD y devolvemos una respuesta
		// a la petici?n REST. As? igual con el resto
		vertx.eventBus().request("getAllUsuarios", "getAllUsuarios", reply -> {
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

	public void getUsuarioNIF(RoutingContext routingContext) {
		String usuarionif = routingContext.getBodyAsString();
		if (usuarionif == null) {
			usuarionif = "{}";
		}
		vertx.eventBus().request("getUsuarioNIF", usuarionif, reply -> {
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

	public void getEnfermosPorCuidador(RoutingContext routingContext) {
		String stringId_Cuidador = routingContext.getBodyAsString();
		vertx.eventBus().request("getEnfermosPorCuidador", stringId_Cuidador, reply -> {
			if (reply.succeeded()) {
				System.out.println(reply.result().body());
				routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.result().body()));
			} else {
				System.out.println("HAY FALLO");
				routingContext.response().setStatusCode(500).putHeader("content-type", "application/json")
						.end(String.valueOf(reply.cause().getMessage()));
			}
		});
	}

	public void deleteUsuario(RoutingContext routingContext) {
		// Obtenemos el id del usuario contenido en la propia URL de la petici?n
		String usuarionif = routingContext.getBodyAsString();
		System.out.println(usuarionif);
		vertx.eventBus().request("deleteUsuario", usuarionif, reply -> {
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

	public void addUsuario(RoutingContext routingContext) {
		// A?adimos un usuario utilizando los datos que est?n dentro del body de la
		// petici?n. IMPORTANTE: USAR EL BODY EN POSTMAN DE TIPO RAW
		vertx.eventBus().request("addUsuario", routingContext.getBodyAsString(), reply -> {
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

	public void editUsuario(RoutingContext routingContext) {
		// Creamos un objeto JSON de los datos a modificar del usuario
		vertx.eventBus().request("editUsuario", routingContext.getBodyAsString(), reply -> {
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