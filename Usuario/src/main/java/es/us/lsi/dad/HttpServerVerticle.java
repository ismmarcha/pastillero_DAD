package es.us.lsi.dad;

import com.google.gson.Gson;

import es.us.lsi.dad.Pastillero.HttpPastillero;
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
	private Router router;
	@Override
	public void start(Promise<Void> startFuture) {
		gson = new Gson();
		// Iniciamos el verticle encargado de la base de datos
		vertx.deployVerticle(new BDVerticle());
		System.out.println("hola");
		// Creamos el objeto Router que nos permite enlazar peticiones REST a funciones
		// de nuestro servidor
		router = Router.router(vertx);
		iniciarRouterUsuario();
		iniciarRouterPastillero();
		// Creamos el servidor HTTP en el puerto 808X
		httpServer = vertx.createHttpServer();
		httpServer.requestHandler(router::handle).listen(8084, res -> {
			if (res.succeeded()) {
				startFuture.complete();
			} else {
				startFuture.fail(res.cause());
			}
		});
	}
	
	private void iniciarRouterUsuario() {
		HttpUsuario httpUsuario = new HttpUsuario(vertx);
		router.route("/api/usuarios/*").handler(BodyHandler.create());
		router.get("/api/usuarios").handler(httpUsuario::getUsuarios);
		router.get("/api/usuarios/getUsuarioNif/:usuarionif").handler(httpUsuario::getUsuarioNIF);
		router.post("/api/usuarios/addUsuario").handler(httpUsuario::addUsuario);
		router.put("/api/usuarios/editUsuario/:usuarionif").handler(httpUsuario::editUsuario);
		router.delete("/api/usuarios/:usuarionif").handler(httpUsuario::deleteUsuario);
	}
	
	private void iniciarRouterPastillero() {
		HttpPastillero httpPastillero = new HttpPastillero(vertx);
		router.route("/api/pastilleros/*").handler(BodyHandler.create());
		router.get("/api/pastilleros").handler(httpPastillero::getPastilleros);
		router.post("/api/pastilleros/addPastillero").handler(httpPastillero::addPastillero);
		router.put("/api/pastilleros/editPastillero/:pastilleroid").handler(httpPastillero::editPastillero);
		router.delete("/api/pastilleros/:pastilleroid").handler(httpPastillero::deletePastillero);
	}


	@Override
	public void stop(Promise<Void> startFuture) {
		httpServer.close();
	}

}
