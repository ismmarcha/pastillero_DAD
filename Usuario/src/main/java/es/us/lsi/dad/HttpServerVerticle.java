package es.us.lsi.dad;

import com.google.gson.Gson;

import es.us.lsi.dad.Dosis.HttpDosis;
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
		HttpDosis httpDosis = new HttpDosis(vertx);
		HttpUsuario httpUsuario = new HttpUsuario(vertx);
		HttpPastillero httpPastillero = new HttpPastillero(vertx);

		httpDosis.iniciarRouterDosis(router);
		httpUsuario.iniciarRouterUsuario(router);
		httpPastillero.iniciarRouterPastillero(router);
		
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
	
	@Override
	public void stop(Promise<Void> startFuture) {
		httpServer.close();
	}

}
