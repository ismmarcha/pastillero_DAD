package es.us.lsi.dad;

import com.google.gson.Gson;

import es.us.lsi.dad.Usuario.HttpUsuario;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class HttpServerVerticle extends AbstractVerticle {

	private HttpServer httpServer = null;
	private Gson gson;

	@Override
	public void start(Promise<Void> startFuture) {
		gson = new Gson();
		HttpUsuario httpUsuario = new HttpUsuario(vertx);
		// Iniciamos el verticle encargado de la base de datos
		vertx.deployVerticle(new BDVerticle());
		System.out.println("hola");
		// Creamos el objeto Router que nos permite enlazar peticiones REST a funciones
		// de nuestro servidor
		Router router = Router.router(vertx);
		router.route("/api/users/*").handler(BodyHandler.create());
		router.get("/api/users").handler(httpUsuario::getUsers);
		router.post("/api/users/addUser").handler(httpUsuario::addUser);
		router.put("/api/users/editUser/:userid").handler(httpUsuario::editUser);
		router.delete("/api/users/:userid").handler(httpUsuario::deleteUser);

		// Creamos el servidor HTTP en el puerto 808X
		httpServer = vertx.createHttpServer();
		httpServer.requestHandler(router::handle).listen(8080, res -> {
			if (res.succeeded()) {
				startFuture.complete();
			} else {
				startFuture.fail(res.cause());
			}
		});
	}

	/*private void getUsers(RoutingContext routingContext) {
		// Enviamos petición al canal abierto del verticle BD y devolvemos una respuesta
		// a la petición REST. Así igual con el resto
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

	private void deleteUser(RoutingContext routingContext) {
		// Obtenemos el id del usuario contenido en la propia URL de la petición
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

	private void addUser(RoutingContext routingContext) {
		// Añadimos un usuario utilizando los datos que están dentro del body de la
		// petición. IMPORTANTE: USAR EL BODY EN POSTMAN DE TIPO RAW
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

	private void editUser(RoutingContext routingContext) {
		String userid = routingContext.request().getParam("userid");
		// Creamos un objeto JSON de los datos a modificar del usuario
		JsonObject json = routingContext.getBodyAsJson();
		// Añadimos a dicho JSON el userid para poder saber que usuario queremos
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
	}*/

	@Override
	public void stop(Promise<Void> startFuture) {
		httpServer.close();
	}

}
