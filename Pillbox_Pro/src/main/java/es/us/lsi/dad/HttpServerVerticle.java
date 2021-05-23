package es.us.lsi.dad;

import es.us.lsi.dad.Pastilla.HttpPastilla; 
import es.us.lsi.dad.Dosis.HttpDosis;
import es.us.lsi.dad.Pastillero.HttpPastillero;
import es.us.lsi.dad.Usuario.HttpUsuario;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

public class HttpServerVerticle extends AbstractVerticle {

	private HttpServer httpServer = null;
	private Router router;


	
	@Override
	public void start(Promise<Void> startFuture) {
		// Iniciamos el verticle encargado de la base de datos
		vertx.deployVerticle(new BDVerticle());
		System.out.println("Inicio de Vert.x");
		// Creamos el objeto Router que nos permite enlazar peticiones REST a funciones
		// de nuestro servidor
		router = Router.router(vertx);
		HttpDosis httpDosis = new HttpDosis(vertx);
		HttpUsuario httpUsuario = new HttpUsuario(vertx);
		HttpPastillero httpPastillero = new HttpPastillero(vertx);
		HttpPastilla httpPastilla = new HttpPastilla(vertx);

		httpDosis.iniciarRouterDosis(router);
		httpUsuario.iniciarRouterUsuario(router);
		httpPastillero.iniciarRouterPastillero(router);
		httpPastilla.iniciarRouterPastilla(router);

		// Creamos el servidor HTTP en el puerto 808X
		httpServer = vertx.createHttpServer();

		httpServer.requestHandler(router::handle).listen(8084, res -> {

			if (res.succeeded()) {
				System.out.println("Servidor HTTP iniciado en el puerto " + res.result().actualPort());
				startFuture.complete();
			} else {
				System.out.println("Fallo al iniciar el servidor HTTP " + res.cause());
				startFuture.fail(res.cause());
			}
		});
	}

	@Override
	public void stop(Promise<Void> startFuture) {
		httpServer.close();
	}

}
