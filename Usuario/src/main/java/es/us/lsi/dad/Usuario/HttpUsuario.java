package es.us.lsi.dad.Usuario;

import io.vertx.core.Vertx; 
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;


//7. Obtener los pacientes de un cuidador. 

public class HttpUsuario {
	Vertx vertx;
	
	public HttpUsuario(Vertx vertx) {
		this.vertx = vertx;
	}

	
	public void iniciarRouterUsuario(Router router) {
		router.route("/api/usuarios/*").handler(BodyHandler.create());
		router.get("/api/usuarios").handler(this::getAllUsuarios);
		router.get("/api/usuarios/getUsuarioNif").handler(this::getUsuarioNIF);
		router.post("/api/usuarios/addUsuario").handler(this::addUsuario);
		router.put("/api/usuarios/editUsuario").handler(this::editUsuario);
		router.delete("/api/usuarios").handler(this::deleteUsuario);
		router.get("/api/usuarios/getEnfermosPorCuidador").handler(this::getEnfermosPorCuidador);
	}
	
	public void getAllUsuarios(RoutingContext routingContext) {
		// Enviamos petición al canal abierto del verticle BD y devolvemos una respuesta
		// a la petición REST. Así igual con el resto
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
		JsonObject jsonUsuario = new JsonObject(routingContext.getBodyAsString());
		String usuarionif = jsonUsuario.getString("nif");
		vertx.eventBus().request("getUsuarioNIF", usuarionif, reply -> {
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

	public void deleteUsuario(RoutingContext routingContext) {
		// Obtenemos el id del usuario contenido en la propia URL de la petición
		JsonObject jsonUsuario = new JsonObject(routingContext.getBodyAsString());
		String usuarionif = jsonUsuario.getString("nif");
		System.out.println(usuarionif);
		vertx.eventBus().request("deleteUsuario", usuarionif, reply -> {
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

	public void addUsuario(RoutingContext routingContext) { 
		// Añadimos un usuario utilizando los datos que están dentro del body de la
		// petición. IMPORTANTE: USAR EL BODY EN POSTMAN DE TIPO RAW
		vertx.eventBus().request("addUsuario", routingContext.getBodyAsString(), reply -> {
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

	public void editUsuario(RoutingContext routingContext) {	
		String nif = routingContext.getBodyAsString();
		// Creamos un objeto JSON de los datos a modificar del usuario
		JsonObject json = routingContext.getBodyAsJson();
		// Añadimos a dicho JSON el userid para poder saber que usuario queremos
		// modificar.
		json.put("nif", nif);
		vertx.eventBus().request("editUsuario", json.toString(), reply -> {
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
	
	public void getEnfermosPorCuidador(RoutingContext routingContext) {
		JsonObject jsonId_cuidador = new JsonObject(routingContext.getBodyAsString());
		String Id_cuidador = jsonId_cuidador.getString("Id_cuidador");
		vertx.eventBus().request("getEnfermosPorCuidador", Id_cuidador, reply -> {
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